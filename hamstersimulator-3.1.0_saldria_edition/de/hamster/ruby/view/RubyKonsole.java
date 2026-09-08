package de.hamster.ruby.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.scheme.view.JMyTextArea;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse stellt eine JFrame als Konsole für Ruby-Code dar.
 */
public class RubyKonsole extends JFrame implements ActionListener,
		MouseListener {

	// Es gibt nur eine Instanz der Konsole
	private static RubyKonsole konsole = null;

	private ScriptingContainer container;

	// die beiden EingabeFelder
	static JMyTextArea input;

	static JMyTextArea output;

	// 2 Scrollpanes, die die eingabefelder beinhalten
	static JScrollPane in;

	static JScrollPane out;

	// ein Speicher um ausgaben der display-Funktionen am ende wiedergeben zu
	// können
	static String displaySpeicher;

	static JButton submit;

	static JButton delete;

	static JButton vor;

	static JButton zurueck;

	// eine Liste um die Eingaben in einer History zu speichern
	static LinkedList<String> list;

	int index;

	JPopupMenu menu;

	/*
	 * nicht-öffentlicher Konstruktor
	 */
	private RubyKonsole() {

		super("Ruby - " + Utils.getResource("ruby.view.konsole"));
		setSize(600, 400);
		setLocationRelativeTo(Workbench.getWorkbench().getView()
				.getSimulationFrame());
		// setLocation(180, 270);
		// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(0);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// workbench.close(simulation);
				setVisible(false);
				Workbench.winRubyKon.setState(false);
			}
		});

		initKonsole();

		input = new JMyTextArea(true);
		input.erlaubeEinfuegen(true);
		output = new JMyTextArea(false);
		output.setEditable(false);
		output.setBackground(new Color(230, 230, 230));

		in = new JScrollPane(input);
		out = new JScrollPane(output);

		displaySpeicher = "";

		submit = new JButton(Utils.getResource("scheme.konsole.run"));
		submit.addActionListener(this);
		delete = new JButton(Utils.getResource("scheme.konsole.delete"));
		delete.addActionListener(this);
		vor = new JButton(Utils.getResource("scheme.konsole.forward"));
		vor.addActionListener(this);
		vor.setEnabled(false);
		zurueck = new JButton(Utils.getResource("scheme.konsole.back"));
		zurueck.addActionListener(this);
		zurueck.setEnabled(false);

		list = new LinkedList();
		// list.addLast("");
		index = 0;

		// Das Layout
		Container c = this.getContentPane();
		GridBagLayout layout = new GridBagLayout();
		c.setLayout(layout);

		// Positionierung x y w h wx wy
		addComponent(c, layout, new JLabel(Utils
				.getResource("scheme.konsole.input")), 1, 0, 1, 1, 0.0, 0.0);
		addComponent(c, layout, new JLabel(Utils
				.getResource("scheme.konsole.output")), 1, 2, 1, 1, 0.0, 0.0);

		addComponent(c, layout, in, 1, 1, 5, 1, 1.0, 1.0);
		addComponent(c, layout, out, 1, 3, 5, 1, 1.0, 1.0);

		addComponentB(c, layout, submit, 1, 5, 1, 1, 0.0, 0.0);
		addComponentB(c, layout, delete, 2, 5, 1, 1, 0.0, 0.0);
		addComponentB(c, layout, vor, 3, 5, 1, 1, 0.0, 0.0);
		addComponentB(c, layout, zurueck, 4, 5, 1, 1, 0.0, 0.0);

	}

	/**
	 * Diese Funktion löscht den Inhalt des Eingabe- und Ausgabe-Felds
	 */
	public static void clear() {
		input.setText("");
		output.setText("");
	}

	/**
	 * Diese Funtkion leert das Eingabe-Feld
	 */
	public static void clearInput() {
		input.setText("");
	}

	public static RubyKonsole getRubyKonsole() {
		if (konsole == null) {
			konsole = new RubyKonsole();
		}
		return konsole;
	}

	public static JMyTextArea getInput() {
		return input;
	}

	public static JMyTextArea getOutput() {
		return output;
	}

	/**
	 * Fügt dem Speicher die die display-Funktionen text hinzu.
	 * 
	 * @param text
	 *            Auszugebener Text
	 */
	public static void addDisplayText(String text) {
		displaySpeicher = displaySpeicher + text;
	}

	public void actionPerformed(ActionEvent arg0) {

		String tmp = ((JButton) arg0.getSource()).getText();

		if (tmp.equals(Utils.getResource("scheme.konsole.run"))) {
			if (!input.getText().trim().equals("")) {
				list.addLast(input.getText());
				index = list.size();
				doInput();
			}
		} else if (tmp.equals(Utils.getResource("scheme.konsole.delete"))) {
			clearInput();
		} else if (tmp.equals(Utils.getResource("scheme.konsole.forward"))) {
			index++;
			input.setText(list.get(index));
		} else if (tmp.equals(Utils.getResource("scheme.konsole.back"))) {
			index--;
			input.setText(list.get(index));
		}

		if (index == 0) {
			zurueck.setEnabled(false);
		} else {
			zurueck.setEnabled(true);
		}

		if (index >= list.size() - 1) {
			vor.setEnabled(false);
		} else {
			vor.setEnabled(true);
		}
	}

	/**
	 * Eine Funktion um die positionierung der einzelnen gui-elemnte zu
	 * vereinfachen
	 * 
	 * @param cont
	 * @param gbl
	 * @param c
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param weightx
	 * @param weighty
	 */
	static void addComponent(Container cont, GridBagLayout gbl, Component c,
			int x, int y, int width, int height, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	/**
	 * Eine Funktion um die positionierung der einzelnen gui-elemnte zu
	 * vereinfachen
	 * 
	 * @param cont
	 * @param gbl
	 * @param c
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param weightx
	 * @param weighty
	 */
	static void addComponentB(Container cont, GridBagLayout gbl, Component c,
			int x, int y, int width, int height, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	public static boolean isRunning = false;

	public static Thread hamsterThread = null;

	/**
	 * Führt die entsprechenden Funktion be einer Eingabe aus.
	 */
	static void doInput() {
		hamsterThread = new Thread() {
			@Override
			public void run() {
				String befehl = "";
				Object erg = null;
				try {
					befehl = RubyKonsole.getInput().getText().trim();
					// befehl = befehl.replaceAll("\n", "");

					// code ausführen
					isRunning = true;
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(DebuggerModel.RUNNING);
					Workbench.getWorkbench().getSimulationController()
							.getLogPanel().clearLog();

					if (konsole.container != null) {
						erg = konsole.container.runScriptlet(befehl);
					}

					isRunning = false;
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(
									DebuggerModel.NOT_RUNNING);

					if (erg == null) {
						erg = "nil";
					} else {
						erg = erg.toString();
					}
				} catch (Exception e) {
					// System.out.println(e.toString());
					erg = e.toString();
				} finally {
					isRunning = false;
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(
									DebuggerModel.NOT_RUNNING);
					// Ausgabe erzeugen
					// text der display funktion vor ausgabe
					// schreiben
					String ausgabe = ">" + befehl + "\n=>" + displaySpeicher
							+ erg + "\n";
					RubyKonsole.getOutput().append(ausgabe);
					RubyKonsole.getOutput().setCaretPosition(RubyKonsole.getOutput().getDocument().getLength());

					// Eingabefeld leeren
					RubyKonsole.getInput().setText("");
					// displayspeicher leeren
					displaySpeicher = "";
				}
			}
		};
		hamsterThread.start();
	}

	private void initKonsole() {
		if (container != null) {
			return;
		}
		try {
			container = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
			String command = "include Java\n";
			command += "require \"hamstersimulator.jar\"\n";
			command += "include_class \"de.hamster.ruby.model.RubyHamster\"\n";
			command += "class Hamster < RubyHamster\n";
			command += "end\n";
			command += "def vor\n";
			command += "Hamster.getStandardHamsterIntern().vor()\n";
			command += "end\n";
			command += "def linksUm\n";
			command += "Hamster.getStandardHamsterIntern().linksUm()\n";
			command += "end\n";
			command += "def nimm\n";
			command += "Hamster.getStandardHamsterIntern().nimm()\n";
			command += "end\n";
			command += "def gib\n";
			command += "Hamster.getStandardHamsterIntern().gib()\n";
			command += "end\n";
			command += "def vornFrei\n";
			command += "return Hamster.getStandardHamsterIntern().vornFrei()\n";
			command += "end\n";
			command += "def kornDa\n";
			command += "return Hamster.getStandardHamsterIntern().kornDa()\n";
			command += "end\n";
			command += "def maulLeer\n";
			command += "return Hamster.getStandardHamsterIntern().maulLeer()\n";
			command += "end\n";
			command += "def Hamster.NORD\n";
			command += "return 0\n";
			command += "end\n";
			command += "def Hamster.OST\n";
			command += "return 1\n";
			command += "end\n";
			command += "def Hamster.SUED\n";
			command += "return 2\n";
			command += "end\n";
			command += "def Hamster.WEST\n";
			command += "return 3\n";
			command += "end\n";
			container.runScriptlet(command);
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
		if (arg0.isPopupTrigger()) {
			menu.show(this, arg0.getX(), arg0.getY());
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}
}
