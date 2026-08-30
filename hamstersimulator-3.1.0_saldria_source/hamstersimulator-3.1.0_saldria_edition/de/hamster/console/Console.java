package de.hamster.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Realisiert eine Thread-sichere Console, auf die System.in, System.out und
 * System.err umgeleitet werden können. Genutzt werden kann die Console auf
 * folgende Art und Weise:
 * 
 * Console console = new Console();<br>
 * System.setOut(new PrintStream(console.getOut()));<br>
 * System.setErr(new PrintStream(console.getErr()));<br>
 * System.setIn(console.getIn());<br>
 * 
 * @author Dietrich Boles, Uni Oldenburg
 * @version 1.0 (12.11.2008)
 * 
 */
public class Console extends JFrame {

	ConsoleTextPane outputArea;
	JScrollPane scrollPane;
	StyledDocument doc;

	JMenuBar menuBar;
	JMenu optionMenu;
	JMenuItem clearMenuItem;
	JMenuItem saveMenuItem;
	JMenuItem closeMenuItem;

	StandardOut out;
	StandardErr err;
	StandardIn in;

	PrintStream oldOut;
	PrintStream oldErr;
	InputStream oldIn;

	OLList inputs;
	OLList outputs;

	ReentrantLock lock;
	Condition lockCondition;
	boolean outputflag;

	JCheckBoxMenuItem menuItem;

	int fontSize;

	JMenu fontsizeMenu;
	JRadioButtonMenuItem size8, size10, size12, size14, size16, size18, size24,
			size36, size48;

	public Console() {
		this("Console");
	}

	public Console(String title) {
		super(title);
		// setLocationRelativeTo(Workbench.getWorkbench().getView().getEditorFrame());
		setLocation(10, 10);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		this.menuItem = null;
		this.fontSize = 14;

		this.lock = new ReentrantLock();
		this.lockCondition = this.lock.newCondition();
		this.outputflag = false;

		this.inputs = new OLList();
		this.outputs = new OLList();

		ResourceBundle bundle = Utils.getResources();
		this.menuBar = new JMenuBar();
		this.setJMenuBar(this.menuBar);
		this.optionMenu = new JMenu(bundle.getString("console.option.text"));
		this.menuBar.add(this.optionMenu);
		this.clearMenuItem = new JMenuItem();
		this.clearMenuItem.setText(bundle.getString("console.clear.text"));
		this.clearMenuItem.setToolTipText(bundle
				.getString("console.clear.tooltip"));
		this.clearMenuItem.setMnemonic(bundle.getString(
				"console.clear.mnemonic").charAt(0));
		this.clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(bundle
				.getString("console.clear.keystroke")));
		this.clearMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Console.this.lock.lock();
				try {
					Console.this.inputs = new OLList();
					Console.this.outputs = new OLList();
					Console.this.in.reset();
					Console.this.doc.remove(0, Console.this.doc.getLength());

				} catch (BadLocationException exc) {
					Console.this.oldErr.println(exc.getMessage());
				} finally {
					Console.this.lock.unlock();
				}
			}
		});
		this.optionMenu.add(this.clearMenuItem);
		this.saveMenuItem = new JMenuItem();
		this.saveMenuItem.setText(bundle.getString("console.save.text"));
		this.saveMenuItem.setToolTipText(bundle
				.getString("console.save.tooltip"));
		this.saveMenuItem.setMnemonic(bundle.getString("console.save.mnemonic")
				.charAt(0));
		this.saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(bundle
				.getString("console.save.keystroke")));
		this.saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showSaveDialog(Console.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						PrintStream out = new PrintStream(new FileOutputStream(
								chooser.getSelectedFile()));
						out.print(Console.this.doc.getText(0, Console.this.doc
								.getLength()));
						out.close();
					} catch (FileNotFoundException e1) {
						Console.this.oldErr.println(e1.getMessage());
					} catch (BadLocationException e2) {
						Console.this.oldErr.println(e2.getMessage());
					}
				}
			}
		});
		this.optionMenu.add(this.saveMenuItem);

		initFontSizeMenu();
		this.optionMenu.add(this.fontsizeMenu);

		this.closeMenuItem = new JMenuItem();
		this.closeMenuItem.setText(bundle.getString("console.close.text"));
		this.closeMenuItem.setToolTipText(bundle
				.getString("console.close.tooltip"));
		this.closeMenuItem.setMnemonic(bundle.getString(
				"console.close.mnemonic").charAt(0));
		this.closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(bundle
				.getString("console.close.keystroke")));
		this.closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Console.this.setVisible(false);
				if (menuItem != null) {
					menuItem.setState(false);
				}
			}
		});
		this.optionMenu.add(this.closeMenuItem);

		this.outputArea = new ConsoleTextPane(this);
		this.outputArea.setMargin(new Insets(5, 5, 5, 5));
		this.outputArea.setEditable(true);

		this.doc = this.outputArea.getStyledDocument();
		this.doc.addDocumentListener(new IOListener(this));
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		Style regular = this.doc.addStyle("regular", def);
		StyleConstants.setFontFamily(regular, "Monospaced");
		StyleConstants.setFontSize(regular, fontSize);
		StyleConstants.setForeground(regular, Color.GREEN);
		this.doc.setLogicalStyle(0, regular);
		((AbstractDocument) this.doc).setDocumentFilter(new InputFilter(this));

		this.scrollPane = new JScrollPane(this.outputArea);
		this.scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane.setPreferredSize(new Dimension(800, 600));

		this.setLayout(new BorderLayout());
		this.add(this.scrollPane, BorderLayout.CENTER);

		this.oldOut = System.out;
		this.oldErr = System.err;
		this.oldIn = System.in;

		this.out = new StandardOut(this);
		this.err = new StandardErr(this);
		this.in = new StandardIn(this);

		this.pack();
	}
	
	public void init() {
		
	}

	public OutputStream getOut() {
		// this.oldOut = System.out;
		return this.out;
	}

	public OutputStream getErr() {
		// this.oldErr = System.err;
		return this.err;
	}

	public InputStream getIn() {
		// this.oldIn = System.in;
		return this.in;
	}

	public void setMenuItem(JCheckBoxMenuItem i) {
		this.menuItem = i;
	}

	public void changeFontSize(int size) {
		this.fontSize = size;
		Style style = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontSize(style, size);
		style = doc.getStyle("regular");
		StyleConstants.setFontSize(style, size);
		style = doc.getStyle("out");
		StyleConstants.setFontSize(style, size);
		style = doc.getStyle("err");
		StyleConstants.setFontSize(style, size);
		SwingUtilities.invokeLater(new ReRun(this));
	}

	public int getFontSize() {
		return this.fontSize;
	}

	public void initFontSizeMenu() {
		this.fontsizeMenu = new JMenu();
		this.fontsizeMenu.setText(Utils.getResource("console.font.text"));
		this.fontsizeMenu.setToolTipText(Utils
				.getResource("console.font.tooltip"));
		this.fontsizeMenu.setMnemonic(Utils
				.getResource("console.font.mnemonic").charAt(0));

		ButtonGroup group = new ButtonGroup();

		this.size8 = new JRadioButtonMenuItem("8");
		this.size8.addActionListener(new FontSizeListener(this, 8));
		this.fontsizeMenu.add(this.size8);
		group.add(this.size8);
		this.size10 = new JRadioButtonMenuItem("10");
		this.size10.addActionListener(new FontSizeListener(this, 10));
		this.fontsizeMenu.add(this.size10);
		group.add(this.size10);
		this.size12 = new JRadioButtonMenuItem("12");
		this.size12.addActionListener(new FontSizeListener(this, 12));
		this.fontsizeMenu.add(this.size12);
		group.add(this.size12);
		this.size14 = new JRadioButtonMenuItem("14");
		this.size14.addActionListener(new FontSizeListener(this, 14));
		this.fontsizeMenu.add(this.size14);
		group.add(this.size14);
		this.size16 = new JRadioButtonMenuItem("16");
		this.size16.addActionListener(new FontSizeListener(this, 16));
		this.fontsizeMenu.add(this.size16);
		group.add(this.size16);
		this.size18 = new JRadioButtonMenuItem("18");
		this.size18.addActionListener(new FontSizeListener(this, 18));
		this.fontsizeMenu.add(this.size18);
		group.add(this.size18);
		this.size24 = new JRadioButtonMenuItem("24");
		this.size24.addActionListener(new FontSizeListener(this, 24));
		this.fontsizeMenu.add(this.size24);
		group.add(this.size24);
		this.size36 = new JRadioButtonMenuItem("36");
		this.size36.addActionListener(new FontSizeListener(this, 36));
		this.fontsizeMenu.add(this.size36);
		group.add(this.size36);
		this.size48 = new JRadioButtonMenuItem("48");
		this.size48.addActionListener(new FontSizeListener(this, 48));
		this.fontsizeMenu.add(this.size48);
		group.add(this.size48);

		switch (this.fontSize) {
		case 8:
			this.size8.setSelected(true);
			break;
		case 10:
			this.size10.setSelected(true);
			break;
		case 12:
			this.size12.setSelected(true);
			break;
		case 14:
			this.size14.setSelected(true);
			break;
		case 16:
			this.size16.setSelected(true);
			break;
		case 18:
			this.size18.setSelected(true);
			break;
		case 24:
			this.size24.setSelected(true);
			break;
		case 36:
			this.size36.setSelected(true);
			break;
		default:
			this.size48.setSelected(true);
			break;
		}
	}

	final static boolean insertIsInOutput(int pos, OLList outputs) {
		for (OffsetLength ol : outputs) {
			if (pos >= ol.from() + 1 && pos <= ol.to()) {
				return true;
			}
		}
		return false;
	}

	final static boolean removeIsInOutput(int pos, OLList outputs) {
		for (OffsetLength ol : outputs) {
			if (pos >= ol.from() + 1 && pos <= ol.to() + 1) {
				return true;
			}
		}
		return false;
	}

	final static boolean removeIsInOld(int pos, int readPos) {
		return pos <= readPos + 1;
	}

	final static boolean touchOutput(OffsetLength ol, OLList outputs) {
		for (OffsetLength out : outputs) {
			if (out.from() <= ol.from() && ol.from() <= out.to()
					|| out.from() <= ol.to() && ol.to() <= out.to()
					|| ol.from() <= out.from() && ol.to() >= out.to()) {
				return true;
			}
		}
		return false;
	}

	final static boolean touchOld(OffsetLength ol, int readPos) {
		return ol.offset <= readPos;
	}
}

abstract class StandardOutput extends OutputStream {

	protected Console console;

	StandardOutput(Console console) {
		this.console = console;
	}

	public static void prepareConsole(final Console console) {
		if (console == null
				|| console != Workbench.getWorkbench().getView().getConsole()) {
			return;
		}
		if (EventQueue.isDispatchThread()) {
			Workbench.getWorkbench().getView().openConsole();
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {

						// if (!console.isVisible()) {
						// console.setVisible(true);
						// console.toFront();
						// }

						Workbench.getWorkbench().getView().openConsole();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}
}

class StandardOut extends StandardOutput {

	StandardOut(Console console) {
		super(console);
		Style regular = console.doc.getStyle("regular");
		Style s = console.doc.addStyle("out", regular);
		StyleConstants.setForeground(s, Color.BLACK);
	}

	public void write(int b) throws IOException {
		StandardOutput.prepareConsole(this.console);
		this.console.lock.lock();
		try {
			this.console.outputflag = true;
			OffsetLength outs = new OffsetLength(this.console.doc.getLength(),
					1);
			this.console.doc.insertString(this.console.doc.getLength(), ""
					+ (char) b, this.console.doc.getStyle("out"));
			this.console.outputs.add(outs);
			this.console.outputflag = false;
		} catch (Throwable exc) {
			this.console.oldErr.println(exc.getMessage());
		} finally {
			this.console.lock.unlock();
		}
		this.console.outputArea.setCaretPosition(this.console.doc.getLength());
	}

	public void write(byte[] b, int off, int len) throws IOException {
		char[] buffer = new char[len];
		for (int i = 0; i < len; i++) {
			buffer[i] = (char) b[off + i];
		}
		String output = new String(buffer).replace("\r\n", "\n");
		StandardOutput.prepareConsole(this.console);
		this.console.lock.lock();
		try {
			this.console.outputflag = true;
			OffsetLength outs = new OffsetLength(this.console.doc.getLength(),
					output.length());
			this.console.doc.insertString(this.console.doc.getLength(), output,
					this.console.doc.getStyle("out"));
			this.console.outputs.add(outs);
			this.console.outputflag = false;
		} catch (Throwable exc) {
			this.console.oldErr.println(exc.getMessage());
		} finally {
			this.console.lock.unlock();
		}
		this.console.outputArea.setCaretPosition(this.console.doc.getLength());
	}
}

class StandardErr extends StandardOutput {

	StandardErr(Console console) {
		super(console);
		Style regular = console.doc.getStyle("regular");
		Style s = console.doc.addStyle("err", regular);
		StyleConstants.setForeground(s, Color.RED);
	}

	public void write(int b) throws IOException {
		StandardOutput.prepareConsole(this.console);
		this.console.lock.lock();
		try {
			this.console.outputflag = true;
			OffsetLength outs = new OffsetLength(this.console.doc.getLength(),
					1, true);
			this.console.doc.insertString(this.console.doc.getLength(), ""
					+ (char) b, this.console.doc.getStyle("err"));
			this.console.outputs.add(outs);
			this.console.outputflag = false;
		} catch (Throwable exc) {
			this.console.oldErr.println(exc.getMessage());
		} finally {
			this.console.lock.unlock();
		}
		this.console.outputArea.setCaretPosition(this.console.doc.getLength());
	}

	public void write(byte[] b, int off, int len) throws IOException {
		char[] buffer = new char[len];
		for (int i = 0; i < len; i++) {
			buffer[i] = (char) b[off + i];
		}
		String output = new String(buffer).replace("\r\n", "\n");
		StandardOutput.prepareConsole(this.console);
		this.console.lock.lock();
		try {
			this.console.outputflag = true;
			OffsetLength outs = new OffsetLength(this.console.doc.getLength(),
					output.length(), true);
			this.console.doc.insertString(this.console.doc.getLength(), output,
					this.console.doc.getStyle("err"));
			this.console.outputs.add(outs);
			this.console.outputflag = false;
		} catch (Throwable exc) {
			this.console.oldErr.println(exc.getMessage());
		} finally {
			this.console.lock.unlock();
		}
		this.console.outputArea.setCaretPosition(this.console.doc.getLength());
	}
}

class StandardIn extends InputStream {

	protected Console console;
	boolean waited;
	int pos;
	int curOLIndex;
	boolean flag = false;

	StandardIn(Console console) {
		this.console = console;
		this.reset();
	}

	public void reset() {
		this.waited = false;
		OffsetLength dummy = new OffsetLength(-1, 1);
		dummy.dummy = true;
		this.console.inputs.add(dummy);
		this.pos = -1;
		this.curOLIndex = 0;
	}

	public int available() throws IOException {
		return -1; // wichtig
	}

	public int read() throws IOException {
		StandardOutput.prepareConsole(this.console);
		this.console.lock.lock();
		try {
			if (this.noReturnInInput()) {
				if (this.flag) {
					this.flag = false;
					this.waited = false;
					return -1;
				}
				if (!this.waited) {
					try {
						this.waited = true;
						this.console.lockCondition.await();
					} catch (InterruptedException exc) {
					}
				}

			}
			char ch = this.calcCurChar();
			if (ch == '\n') { // && noInput()) {
				this.flag = true;
			}
			return ch;
		} finally {
			this.console.lock.unlock();
		}
	}

	private char calcCurChar() {
		try {
			OffsetLength curOL = this.console.inputs.get(this.curOLIndex);
			if (this.pos == curOL.to()) {
				do {
					this.curOLIndex++;
					curOL = this.console.inputs.get(this.curOLIndex);
				} while (curOL.dummy);
				this.pos = curOL.offset;
			} else {
				this.pos++;
			}
			char ch = this.console.doc.getText(this.pos, 1).charAt(0);
			return ch;
		} catch (BadLocationException exc) {
			this.console.oldErr.println(exc.getMessage());
			return '\0';
		}
	}

	private boolean noReturnInInput() {
		try {
			OffsetLength curOL = this.console.inputs.get(this.curOLIndex);
			if (!curOL.dummy && this.pos < curOL.to()) {
				String str = this.console.doc.getText(this.pos + 1, curOL.to()
						- this.pos);
				if (str.contains("\n")) {
					return false;
				}
			}
			for (int i = this.curOLIndex + 1; i < this.console.inputs.size(); i++) {
				OffsetLength ol = this.console.inputs.get(i);
				if (!ol.dummy) {
					String str = this.console.doc.getText(ol.offset, ol.length);
					if (str.contains("\n")) {
						return false;
					}
				}
			}
		} catch (BadLocationException exc) {
			this.console.oldErr.println(exc.getMessage());
		}
		return true;
	}
}

class IOListener implements DocumentListener {

	private Console console;

	public IOListener(Console console) {
		this.console = console;

	}

	public void changedUpdate(DocumentEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
		StyledDocument doc = (StyledDocument) e.getDocument();
		int offset = e.getOffset();
		int length = e.getLength();

		OffsetLength ins = new OffsetLength(offset, length);

		ArrayList<OffsetLength> newList = new ArrayList<OffsetLength>();
		for (OffsetLength ol : this.console.inputs) {
			if (ol.from() >= ins.from()) { // dahinter
				ol.offset = ol.offset + ins.length;
			} else if (ol.to() < ins.from()) { // davor
				// nothing to change
			} else {
				int l = ol.length;
				ol.length = ins.from() - ol.from();
				newList.add(new OffsetLength(ins.to() + 1, l - ol.length));
			}
		}
		for (OffsetLength ol : newList) {
			this.console.inputs.add(ol);
		}

		for (OffsetLength ol : this.console.outputs) {
			if (ol.from() >= ins.from()) { // dahinter
				ol.offset = ol.offset + ins.length;
			}
		}

		if (!this.console.outputflag) {
			this.console.inputs.add(ins);
			String text = null;
			try {
				text = doc.getText(offset, length);
			} catch (BadLocationException e1) {
				this.console.oldErr.println(e1.getMessage());
			}
			this.console.lock.lock();
			try {
				if (text.contains("\n")) {
					this.console.lockCondition.signal();
				}
			} finally {
				this.console.lock.unlock();
			}
			SwingUtilities.invokeLater(new RunSetCorrectInputStyle(doc, offset,
					length));
		}
	}

	public void removeUpdate(DocumentEvent e) {
		int offset = e.getOffset();
		int length = e.getLength();
		OffsetLength rem = new OffsetLength(offset, length);

		Iterator<OffsetLength> it = this.console.inputs.iterator();
		while (it.hasNext()) {
			OffsetLength ol = it.next();
			if (ol.to() < rem.from()) { // davor
				// nothing to change
			} else if (ol.from() > rem.to()) { // dahinter
				ol.offset = ol.offset - rem.length;
			} else if (ol.from() >= rem.from() && ol.to() <= rem.to()) {
				it.remove();
			} else if (ol.from() < rem.from() && ol.to() > rem.to()) { // darueber
				ol.length = ol.length - rem.length;
			} else if (ol.from() < rem.from() && ol.to() <= rem.to()) { // davor
				// darin
				ol.length = rem.from() - ol.from();
			} else if (ol.from() >= rem.from() && ol.to() > rem.to()) { // dahinter
				// darin
				int to = ol.to();
				ol.offset = rem.from();
				ol.length = to - rem.to();
			}
		}

		for (OffsetLength ol : this.console.outputs) {
			if (ol.from() > rem.to()) { // dahinter
				ol.offset = ol.offset - rem.length;
			}
		}

	}

	String generateInputString(OLList inputs, Document doc) {
		String res = "";
		int length = 0;
		for (OffsetLength ol : inputs) {
			if (!ol.dummy) {
				try {
					String r = doc.getText(ol.offset, ol.length);
					length += r.length();
					res += r;
				} catch (BadLocationException e) {
					this.console.oldErr.println(e.getMessage());
				}
			}
		}
		return res;
	}
}

class OffsetLength {
	int offset;
	int length;
	boolean dummy = false;
	boolean isErr;

	OffsetLength(int offset, int length) {
		this(offset, length, false);
	}

	OffsetLength(int offset, int length, boolean err) {
		this.offset = offset;
		this.length = length;
		this.isErr = err;
	}

	public boolean equals(Object obj) {
		OffsetLength ol = (OffsetLength) obj;
		return this.offset == ol.offset && this.length == ol.length;
	}

	int from() {
		return this.offset;
	}

	int to() {
		return this.offset + this.length - 1;
	}

	boolean isErr() {
		return this.isErr;
	}
}

class RunSetCorrectInputStyle implements Runnable {

	StyledDocument document;
	int offset, length;

	public RunSetCorrectInputStyle(StyledDocument document, int offset,
			int length) {
		this.document = document;
		this.offset = offset;
		this.length = length;
	}

	public void run() {
		this.document.setCharacterAttributes(this.offset, this.length,
				this.document.getStyle("regular"), true);
	}
}

class InputFilter extends DocumentFilter {
	Console console;

	public InputFilter(Console console) {
		this.console = console;
	}

	public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {
		super.remove(fb, offset, length);
	}

	public void insertString(DocumentFilter.FilterBypass fb, int offset,
			String string, AttributeSet attr) throws BadLocationException {
		super.insertString(fb, offset, string, attr);
	}

	public void replace(FilterBypass fb, int offs, int length, String str,
			AttributeSet a) throws BadLocationException {
		if (Console.touchOld(new OffsetLength(offs, length),
				this.console.in.pos)
				|| Console.insertIsInOutput(offs, this.console.outputs)) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		if (length > 0
				&& Console.touchOutput(new OffsetLength(offs, length),
						this.console.outputs)) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		super.replace(fb, offs, length, str, a);

	}
}

class ConsoleTextPane extends JTextPane {

	Console console;

	public ConsoleTextPane(Console console) {
		this.console = console;
	}

	public void processKeyEvent(KeyEvent event) {
		this.console.lock.lock();
		try {
			int code = event.getKeyCode();
			if (code == 10) {
				this.console.outputArea.setCaretPosition(this.console.doc
						.getLength());
			} else if (event.getID() == KeyEvent.KEY_PRESSED
					&& code == KeyEvent.VK_BACK_SPACE) { // <-
				int selStart = this.console.outputArea.getSelectionStart();
				int selEnd = this.console.outputArea.getSelectionEnd();
				if (selStart == selEnd) { // keine Selektion
					if (Console.removeIsInOld(selStart, this.console.in.pos)
							|| Console.removeIsInOutput(selStart,
									this.console.outputs)) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
				} else if (Console.touchOld(new OffsetLength(selStart, selEnd
						- selStart + 1), this.console.in.pos)
						|| Console.touchOutput(new OffsetLength(selStart,
								selEnd - selStart), this.console.outputs)) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}
			} else if (event.getID() == KeyEvent.KEY_PRESSED
					&& code == KeyEvent.VK_DELETE) { // Entf
				int selStart = this.console.outputArea.getSelectionStart();
				int selEnd = this.console.outputArea.getSelectionEnd();
				if (selStart == selEnd) { // keine Selektion
					if (Console
							.removeIsInOld(selStart + 1, this.console.in.pos)
							|| Console.removeIsInOutput(selStart + 1,
									this.console.outputs)) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
				} else if (Console.touchOld(new OffsetLength(selStart, selEnd
						- selStart + 1), this.console.in.pos)
						|| Console.touchOutput(new OffsetLength(selStart,
								selEnd - selStart), this.console.outputs)) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}
			}
			super.processKeyEvent(event);
		} finally {
			this.console.lock.unlock();
		}
	}
}

class OLList extends ArrayList<OffsetLength> {

	public OLList() {
		super();
	}

	public boolean add(OffsetLength ol) {
		for (int i = 0; i < this.size(); i++) {
			if (ol.offset <= this.get(i).offset) {
				super.add(i, ol);
				return true;
			}
		}
		super.add(ol);
		return true;
	}
}

class FontSizeListener implements ActionListener {

	Console console;
	int size;

	public FontSizeListener(Console con, int s) {
		console = con;
		size = s;
	}

	public void actionPerformed(ActionEvent e) {
		console.changeFontSize(size);
	}

}

class ReRun implements Runnable {

	Console console;

	public ReRun(Console c) {
		this.console = c;
	}

	public void run() {
		StyledDocument doc = console.doc;
		for (OffsetLength ol : console.outputs) {
			if (!ol.isErr) {
				doc.setCharacterAttributes(ol.offset, ol.length, doc
						.getStyle("out"), true);
			} else {
				doc.setCharacterAttributes(ol.offset, ol.length, doc
						.getStyle("err"), true);
			}
		}
		for (OffsetLength ol : console.inputs) {
			if (ol.dummy) {
				continue;
			}
			doc.setCharacterAttributes(ol.offset, ol.length, doc
					.getStyle("regular"), true);
		}
	}
}
