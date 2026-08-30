package de.hamster.simulation.view;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.StyleConstants;

import de.hamster.model.HamsterInstruction;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.model.Terminal;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.4 $
 */
public class DialogTerminal extends JDialog implements Terminal {
	private static DialogTerminal instance;

	protected JOptionPane optionPane;

	protected JTextField textField;

	protected JFrame parent;

	public static DialogTerminal createInstance(JFrame parent) {
		instance = new DialogTerminal(parent);
		return instance;
	}

	public static DialogTerminal getInstance() {
		return instance;
	}

	private DialogTerminal(JFrame parent) {
		super(parent);
		this.parent = parent;
		optionPane = new JOptionPane();
		optionPane.setOptionType(JOptionPane.DEFAULT_OPTION);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if (isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					close();
				}
			}
		});

		textField = new JTextField();

		setContentPane(optionPane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		optionPane.setMessage("test");
		optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	}

	JLabel getMessage(int hamsterid, String string) {

		// dibo 230309
		Color hamColor = Color.black;
		if (UIManager.getLookAndFeel() == Workbench.startLAF) {
			int color = -2;
			SimulationModel model = de.hamster.workbench.Workbench
					.getWorkbench().getSimulationController()
					.getSimulationModel();
			int hid = hamsterid;
			int i = -2;
			i = Math.min(hamsterid, Utils.COLORS.length - 2);

			color = model.getHamster(hid).getColor();
			if (hid == -1) {
				color = model.getHamster(hid).getColor() - 1;
			} else if (color <= -1) {
				color = i;
			} else if (color >= Utils.COLORS.length) {
				color = Utils.COLORS.length - 2;
			} else {
				color = color - 1;
			}
			color = color + 1;
			hamColor = Utils.COLORS[color];
		}

		string = "<html>" + string + "</html>";
		string = string.replaceAll("\n", "<br>");
		JLabel label = new JLabel(string);

		// label.setForeground(Utils.COLORS[Math.min(hamsterid + 1,
		// Utils.COLORS.length - 1)]);
		label.setForeground(hamColor);
		return label;
	}

	public synchronized void showAndWaitForClose() {
		pack();
		setVisible(true);
		this.setLocationRelativeTo(parent);
		try {
			while (isVisible())
				wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		optionPane.setValue(null);
	}

	public void write(final int hamsterid, String string) {

		if (string.contains("$_dibo_p_intern$"))
			return; // dibo 210807

		if (string.contains("$_dibo_intern$")) {
			setTitle("Thread-RuntimeException");
			string = string.substring(14, string.length());
		} else {
			setTitle(Utils.getResource("hamster.schreib") + "()");
		}

		if (UIManager.getLookAndFeel() == Workbench.startLAF) {
			optionPane.setMessage(getMessage(hamsterid, string));
			optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		} else {
			try {
				LabelGen gen = new LabelGen(this, optionPane, hamsterid, string);
				SwingUtilities.invokeAndWait(gen);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		showAndWaitForClose();

	}

	public void write(Throwable e) {
		setTitle("Exception");
		String s = e.toString();
		StringWriter sw = new StringWriter();
		StackTraceElement[] ste = e.getStackTrace();
		for (int i = 0; i < ste.length - 1; i++) {
			if (ste[i].getClassName().equals(
					"de.hamster.debugger.model.Hamster"))
				continue;
			if (ste[i].getClassName().equals(
					"de.hamster.debugger.model.IHamster"))
				continue;
			s += "\n   in   " + ste[i].getClassName() + "."
					+ ste[i].getMethodName() + " ("
					+ Utils.getResource("simulation.view.reihe") + " "
					+ ste[i].getLineNumber() + ")";
		}
		e.printStackTrace(new PrintWriter(sw));

		if (UIManager.getLookAndFeel() == Workbench.startLAF) {
			optionPane.setMessage(s);
			optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		} else {
			try {
				LabelGen3 gen = new LabelGen3(this, optionPane, s);
				SwingUtilities.invokeAndWait(gen);
			} catch (InterruptedException exc) {
				e.printStackTrace();
			} catch (InvocationTargetException exc) {
				e.printStackTrace();
			}
		}

		optionPane.setMessage(s);
		optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);

		showAndWaitForClose();
	}

	public int readInt(int hamsterid, String message) {
		setTitle(Utils.getResource("hamster.liesZahl") + "()");
		textField.setText("0");

		if (UIManager.getLookAndFeel() == Workbench.startLAF) {
			optionPane.setMessage(new Object[] {
					getMessage(hamsterid, message), textField });
			optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		} else {
			try {
				LabelGen2 gen = new LabelGen2(this, optionPane, hamsterid,
						message, textField);
				SwingUtilities.invokeAndWait(gen);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		showAndWaitForClose();
		try {
			return Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public String readString(int hamsterid, String message) {
		setTitle(Utils.getResource("hamster.liesZeichenkette") + "()");
		textField.setText("");

		if (UIManager.getLookAndFeel() == Workbench.startLAF) {
			optionPane.setMessage(new Object[] {
					getMessage(hamsterid, message), textField });
			optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		} else {
			try {
				LabelGen2 gen = new LabelGen2(this, optionPane, hamsterid,
						message, textField);
				SwingUtilities.invokeAndWait(gen);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		showAndWaitForClose();
		return textField.getText();
	}

	public synchronized void close() {
		setVisible(false);
		notify();
	}
}

class LabelGen implements Runnable {
	DialogTerminal terminal;
	JOptionPane pane;
	int id;
	String str;
	JLabel label;

	LabelGen(DialogTerminal t, JOptionPane p, int hamsterid, String string) {
		terminal = t;
		pane = p;
		id = hamsterid;
		str = string;
	}

	public void run() {
		label = terminal.getMessage(id, str);
		pane.setMessage(label);
		pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	}

	JLabel getLabel() {
		return label;
	}

}

class LabelGen2 implements Runnable {
	DialogTerminal terminal;
	JOptionPane pane;
	JTextField field;
	int id;
	String str;
	JLabel label;

	LabelGen2(DialogTerminal t, JOptionPane p, int hamsterid, String string,
			JTextField f) {
		terminal = t;
		pane = p;
		field = f;
		id = hamsterid;
		str = string;
	}

	public void run() {
		label = terminal.getMessage(id, str);
		pane.setMessage(new Object[] { label, field });

		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
	}

	JLabel getLabel() {
		return label;
	}

}

class LabelGen3 implements Runnable {
	DialogTerminal terminal;
	JOptionPane pane;
	String str;
	JLabel label;

	LabelGen3(DialogTerminal t, JOptionPane p, String string) {
		terminal = t;
		pane = p;

		str = string;
	}

	public void run() {
		pane.setMessage(str);
		pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	}

	JLabel getLabel() {
		return label;
	}

}