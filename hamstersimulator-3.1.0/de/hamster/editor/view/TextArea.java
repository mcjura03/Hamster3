package de.hamster.editor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.UndoManager;

import de.hamster.compiler.model.JavaToken;
import de.hamster.editor.controller.EditorController;
import de.hamster.flowchart.FlowchartPanel;
import de.hamster.fsm.view.FsmPanel;
import de.hamster.model.HamsterFile;
import de.hamster.scratch.ScratchPanel;
import de.hamster.workbench.Utils;

/**
 * Diese Klasse stellt eine einzelne TextArea dar, die sich in einem
 * Registerreiter der TabbedTextArea befindet. Sie unterstuetzt
 * Syntax-Highlighting, Undo, Redo und die Hervorhebung von einzelnen Zeilen.
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.6 $
 */
public class TextArea extends JTextPane implements UndoableEditListener,
		DocumentListener, Printable {

	/**
	 * Dieses Attribut aktiviert das Highlighting der aktuellen Zeile.
	 */
	private boolean activeLineHighlighted;

	protected long lastModified;

	protected int line;

	/**
	 * Diese Datei wird aktuell editiert.
	 */
	protected HamsterFile file;

	/**
	 * Der UndoManager dieses Dokuments.
	 */
	protected UndoManager undoManager;

	protected TabbedTextArea tabbedTextArea;

	protected int numberOfLines;

	LineNumberPanel lineNumberPanel; // dibo 230309

	private boolean isScratch;

	private ScratchPanel scratchPanel;

	private boolean isFSM;

	private FsmPanel fsmPanel;

	private boolean isFlowchart;

	private FlowchartPanel flowchartPanel;

	public TextArea(TabbedTextArea tabbedTextArea, EditorController controller,
			HamsterFile file) {
		// Prolog
		super(
				// Martin
				file.getType() == HamsterFile.SCHEMEPROGRAM ? new SchemeDocument(
						false)
						: file.getType() == HamsterFile.PROLOGPROGRAM ? new PrologDocument(
								false)
						// Python
								: file.getType() == HamsterFile.PYTHONPROGRAM ? new PythonDocument(
										false)
								// JavaScript
								: file.getType() == HamsterFile.JAVASCRIPTPROGRAM ? new JavaScriptDocument(
										false)
								// Ruby
										: file.getType() == HamsterFile.RUBYPROGRAM ? new RubyDocument(
												false)
										// Scratch
												: file.getType() == HamsterFile.SCRATCHPROGRAM ? new ScratchDocument(
														false)
												// FSM
														: file.getType() == HamsterFile.FSM ? new FSMDocument(
																false)
														// Flowchart
																: file.getType() == HamsterFile.FLOWCHART ? new FlowchartDocument(
																		false)
																		: new JavaDocument(
																				false));

		// super(new JavaDocument(false));
		this.tabbedTextArea = tabbedTextArea;
		this.file = file;
		load();
		setCaretPosition(0);
		undoManager = new UndoManager();
		JavaDocument document = (JavaDocument) getDocument();
		document.addUndoableEditListener(this);
		document.addDocumentListener(this);
		line = -1;
		lineNumberPanel = new LineNumberPanel();// dibo 230309
		this.isScratch = false;
		this.isFSM = false;
		this.isFlowchart = false;

		// dibo 31.01.2007
		// if (Utils.INDENT) {// dibo 230309
		MyKeyAdapter _keyListener = new MyKeyAdapter();
		addKeyListener(_keyListener);
		// }
	}

	public TextArea(TabbedTextArea tabbedTextArea, EditorController controller,
			HamsterFile file, ScratchPanel scratchPanel) {
		this(tabbedTextArea, controller, file);
		this.isScratch = true;
		this.scratchPanel = scratchPanel;
	}

	public boolean isScratch() {
		return this.isScratch;
	}

	public ScratchPanel getScratchPanel() {
		return this.scratchPanel;
	}

	public TextArea(TabbedTextArea tabbedTextArea, EditorController controller,
			HamsterFile file, FsmPanel fsmPanel) {
		this(tabbedTextArea, controller, file);
		this.isFSM = true;
		this.fsmPanel = fsmPanel;
	}

	public TextArea(TabbedTextArea tabbedTextArea, EditorController controller,
			HamsterFile file, FlowchartPanel flowchartPanel) {
		this(tabbedTextArea, controller, file);
		this.isFlowchart = true;
		this.flowchartPanel = flowchartPanel;
	}

	public boolean isFSM() {
		return this.isFSM;
	}

	public FsmPanel getFSMPanel() {
		return this.fsmPanel;
	}

	public boolean isFlowchart() {
		return this.isFlowchart;
	}

	public FlowchartPanel getFlowchartPanel() {
		return this.flowchartPanel;
	}

	// dibo 31.01.2007

	class MyKeyAdapter extends java.awt.event.KeyAdapter {
		public void keyPressed(KeyEvent evt) {
			Document doc = getDocument();
			int key = evt.getKeyCode();
			switch (key) {
			case KeyEvent.VK_ENTER:
				try {
					String blanks = "";
					if (Utils.INDENT) {// dibo 230309
						blanks = genBlanks(doc.getText(0, getCaretPosition()),
								getCaretPosition() - 1);
					}
					doc.insertString(getCaretPosition(), "\n" + blanks, null);
					evt.consume();
				} catch (Exception exc) {
					// System.out.println("dumm" + exc);
				}
			}
		}

		String genBlanks(String text, int pos) {
			int retIndex = -1;
			int i = pos;
			for (; i >= 0; i--) {
				if (text.charAt(i) == '\n') {
					retIndex = i;
					break;
				}
			}
			if (i != -1 && retIndex == -1) {
				return "";
			}
			String blanks = "";
			i = retIndex + 1;
			while (i <= pos) {
				if (text.charAt(i) == ' ') {
					blanks += " ";
				} else if (text.charAt(i) == '\t') {
					blanks += "\t";
				} else {
					break;
				}
				i++;
			}
			return blanks;
		}
	}

	public TextArea(String text) {
		super(new JavaDocument(true));
		setText(text);
		setBorder(BorderFactory.createEmptyBorder());
		JavaDocument document = (JavaDocument) getDocument();
		document.rehighlight(0, text.length());
		line = -1;
		lineNumberPanel = new LineNumberPanel();// dibo 230309

		// dibo 31.01.2007
		// if (Utils.INDENT) {// dibo 230309
		MyKeyAdapter _keyListener = new MyKeyAdapter();
		addKeyListener(_keyListener);
		// }
	}

	// dibo 230309
	public LineNumberPanel getLineNumberPanel() {
		return lineNumberPanel;
	}

	public void calcNumberOfLines() {
		int oldNumberOfLines = numberOfLines;
		numberOfLines = 1;
		try {
			StringBuffer text = new StringBuffer(getDocument().getText(0,
					getDocument().getLength()));
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) == '\n') {
					numberOfLines++;
				}
			}
			if (oldNumberOfLines != numberOfLines) {
				this.getLineNumberPanel().setNumberOfLines(numberOfLines);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
			numberOfLines = oldNumberOfLines;
		}
	}

	public void refreshFile() {
		if (getParent() == null)
			return;
		if (file.lastModified() == 0 && lastModified != 0)
			tabbedTextArea.closeFile(file);
		// TODO: Workaround: Speichern und direkt danach das Aenderungsdatum
		// lesen funktioniert nicht
		else if (Math.abs(lastModified - file.lastModified()) > 100) {
			System.out.println(lastModified + " != " + file.lastModified());
			if (Utils.ask(this, "editor.dialog.modifiedexternal")) {
				load();
			} else {
				lastModified = file.lastModified();
			}
		}
	}

	// dibo 230309
	public void changeFontSize(int size) {
		((JavaDocument) this.getDocument()).changeFontSize(size);
	}

	/**
	 * Diese Methode liefert den undoManager
	 * 
	 * @return undoManager
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * Diese Methode bringt das Caret in eine andere Zeile.
	 * 
	 * @param line
	 *            Die neue Zeile des Carets
	 */
	public void markLine(int line) {
		Element root = getDocument().getDefaultRootElement();
		setCaretPosition(root.getElement(line).getStartOffset());
		this.line = line;
		repaint();
	}

	/**
	 * Mit dieser Methode wird ein Fehler ausgewaehlt.
	 * 
	 * @param line
	 *            Die Zeile in der sich der Fehler befindet
	 * @param column
	 *            Die Spalte, an der der Fehler beginnt
	 */
	public void selectError(int line, int column) {
		Element root = getDocument().getDefaultRootElement();
		int pos = root.getElement(line).getStartOffset();
		String text = getText();
		for (int i = 0; i < column; i++) {
			try {
				if (text.charAt(pos + i) == '\t')
					column -= 7;
			} catch (Exception exc) {
				// dibo 210110
			}
		}
		JavaDocument document = (JavaDocument) getDocument();
		JavaToken token = document.getTokenAt(pos + column);
		if (token != null) {
			setSelectionStart(token.getStart());
			setSelectionEnd(token.getStart() + token.getText().length());
		}
	}

	// Martin
	public void undoableEditHappened(UndoableEditEvent e) {
		if (file.getType() == HamsterFile.SCHEMEPROGRAM) {
			if (!((SchemeDocument) getDocument()).isHighlighting()) {
				undoManager.addEdit(e.getEdit());
			}
		} else {
			if (!((JavaDocument) getDocument()).isHighlighting()) {
				undoManager.addEdit(e.getEdit());
			}
		}
	}

	/**
	 * Diese Methode liefert die Datei, die gerade editiert wird
	 * 
	 * @return Die aktuell editierte Datei.
	 */
	public HamsterFile getFile() {
		return file;
	}

	public void save() {
		// System.out.println(1);
		file.save(getText());
		// System.out.println(2);
		lastModified = file.lastModified();
		// System.out.println(3);
	}

	public void load() {
		Document document = getDocument();
		// document.removeDocumentListener(this);
		setText(file.load());
		// document.addDocumentListener(this);
		file.setModified(false);
		lastModified = file.lastModified();
	}

	/**
	 * Diese Methode setzt die aktuell verwendete Datei
	 * 
	 * @param file
	 *            Die neue Datei
	 */
	public void setFile(HamsterFile file) {
		this.file = file;
		lastModified = file.lastModified();
	}

	public void changedUpdate(DocumentEvent e) {
		calcNumberOfLines();// dibo 230309
	}

	public void insertUpdate(DocumentEvent e) {
		file.setModified(true);
		calcNumberOfLines();// dibo 230309
	}

	public void removeUpdate(DocumentEvent e) {
		file.setModified(true);
		calcNumberOfLines();// dibo 230309
	}

	public void paintComponent(Graphics g) {
		if (line != -1) {
			int y = 0;
			try {
				Element root = getDocument().getDefaultRootElement();
				y = (int) modelToView(root.getElement(line).getStartOffset())
						.getY();
			} catch (BadLocationException e) {
			}
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getSelectionColor());
			JavaDocument doc = (JavaDocument) getDocument();
			g.fillRect(0, y, getWidth(), doc.lineHeight); // dibo 210110 vorher
			// 17
			setOpaque(false);
			super.paintComponent(g);
			setOpaque(true);
		} else {
			super.paintComponent(g);
		}
	}

	public void removeLineHighlight() {
		line = -1;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		double length = getSize().getHeight();
		double pageHeight = (int) pageFormat.getImageableHeight();
		double pageWidth = (int) pageFormat.getImageableWidth();
		TextArea t = new TextArea(getText());
		t.setSize((int) pageWidth, 100000);
		if (pageIndex <= length / pageHeight) {
			Graphics2D g = (Graphics2D) graphics
					.create((int) pageFormat.getImageableX(),
							(int) (pageFormat.getImageableY() - pageIndex
									* pageHeight), (int) pageWidth,
							(int) length);

			t.print(g);
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

	// dibo 230309
	public void setSize(Dimension d) {
		this.getParent().setBackground(this.getBackground());

		// System.out.println("dd " + d);
		if (d.width < this.getParent().getSize().width) {
			d.width = this.getParent().getSize().width;
		}
		// kein LineWrap
		super.setSize(d);
	}

	// dibo 230309
	public boolean getScrollableTracksViewportWidth() {
		return false; // kein LineWrap
	}

	// dibo 230309
	public void setBackground(Color color) {
		super.setBackground(color);
		if (getParent() != null) {
			this.getParent().setBackground(color);
		}
	}

}