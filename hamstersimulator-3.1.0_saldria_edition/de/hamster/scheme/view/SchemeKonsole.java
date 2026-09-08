package de.hamster.scheme.view;

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

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.ruby.view.RubyKonsole;
import de.hamster.scheme.model.SchemeHamster;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse stellt eine JFrame als Konsole fürs Scheme-Code dar. Es besteht
 * aus einem eingabe und einem ausgabe felt und nimmt sämtliche Scheme-Befehle
 * entgegen und bearbeitet sie. Dabei kann auch der Hamster im Simulator
 * gesteuert werden, da der Scheme-Hamster mit seinem JScheme-Interpreter
 * genutzt wird.
 * 
 * @author momo
 * 
 */
public class SchemeKonsole extends JFrame implements ActionListener,
		MouseListener {

	// Es gibt nur eine Instanz der Konsole
	private static SchemeKonsole konsole = null;

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
	private SchemeKonsole() {

		super("Scheme - " + Utils.getResource("scheme.view.konsole"));
		// SchemeHamster h = SchemeHamster.getHam();
		setSize(600, 400);
		setLocationRelativeTo(Workbench.getWorkbench().getView().getSimulationFrame());
		//setLocation(180, 270);
		// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(0);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// workbench.close(simulation);
				setVisible(false);
				Workbench.winSKon.setState(false);
			}
		});

		input = new JMyTextArea(true);
		input.erlaubeEinfuegen(true);
		output = new JMyTextArea(false);
		output.setEditable(false);
		output.setBackground(new Color(230, 230, 230));

		in = new JScrollPane();
		in.getViewport().add(input);
		out = new JScrollPane();
		out.getViewport().add(output);

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
				.getResource("scheme.konsole.output")), 1, 0, 1, 1, 0.0, 0.0);
		addComponent(c, layout, new JLabel(Utils
				.getResource("scheme.konsole.input")), 1, 2, 1, 1, 0.0, 0.0);

		addComponent(c, layout, out, 1, 1, 5, 1, 1.0, 1.0);
		addComponent(c, layout, in, 1, 3, 5, 1, 1.0, 1.0);

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

	public static SchemeKonsole getSchemeKonsole() {
		if (konsole == null) {
			konsole = new SchemeKonsole();
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

	/**
	 * Führt die entsprechenden Funktion be einer Eingabe aus.
	 */
	static void doInputMartin() {
		new Thread() {
			public void run() {
				try {
					// Falls mehrere Befehle eingegeben wurden, sollen alle
					// einzeln ausgeführt werden
					int klammerAuf = 0;
					int klammerZu = 0;
					String tmpInput = SchemeKonsole.getInput().getText();
					tmpInput.replaceAll("\n", "");

					for (int i = 0; i < tmpInput.length(); i++) {
						switch (tmpInput.charAt(i)) {
						case '(':
							klammerAuf++;
							break;
						case ')':
							klammerZu++;
							break;
						}
						// Falls ein Befehl beendet ist soll er auch sofort
						// ausgeführt werden
						if (i == tmpInput.length() - 1) {
							Object erg = null;
							String befehl = "";
							try {
								befehl = tmpInput.substring(0, i + 1);
								klammerAuf = 0;
								klammerZu = 0;
								if (tmpInput.length() > i + 1) {
									tmpInput = tmpInput.substring(i + 1,
											tmpInput.length());
									i = -1;
								}

								// code ausführen
								initKonsole();
								erg = SchemeHamster.getJS().eval(befehl);

								if (erg == null) {
									erg = "";
									// falls ergebnis true oder false is in #t
									// und #f umwandeln
								} else if (erg.getClass() == java.lang.Boolean.class) {
									if (((Boolean) erg).booleanValue()) {
										erg = "#t";
									} else {
										erg = "#f";
									}

								} else {
									// falls ergebnis territoriumsliste is nicht
									// ausgeben, andere listen wohl
									erg = erg.toString();
									// try { // dibo 20.11.2006
									// if (((Pair) erg).equals(SchemeHamster
									// .getTerritorium())) {
									// erg = "";
									// } else {
									// erg = erg.toString();
									// }
									// } catch (Exception e) {
									// erg = erg.toString();
									// }
								}
							} catch (Exception e) {
								System.out.println(e.toString());
								erg = e.toString();
							} finally {
								// Ausgabe erzeugen
								// text der display funktion vor ausgabe
								// schreiben
								String ausgabe = SchemeKonsole.getOutput()
										.getText();
								ausgabe = ausgabe + "\n>" + befehl + "\n"
										+ "\n" + displaySpeicher + erg;
								SchemeKonsole.getOutput().setText(ausgabe);
								// output runterscrollen
								int max = out.getVerticalScrollBar()
										.getMaximum();
								// out.getVerticalScrollBar().setValue(max);

								// Eingabefeld leeren
								SchemeKonsole.getInput().setText("");
								// displayspeicher leeren
								displaySpeicher = "";
							}
						}
					}

				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
		}.start();
	}

	public static boolean isRunning = false;

	public static Thread hamsterThread = null;

	/**
	 * Führt die entsprechenden Funktion be einer Eingabe aus.
	 */
	static void doInput() {
		hamsterThread = new Thread() {
			public void run() {
				String befehl = "";
				Object erg = null;
				try {
					String tmpInput = SchemeKonsole.getInput().getText().trim();
					befehl = tmpInput.replaceAll("\n", "");

					// code ausführen
					initKonsole();
					isRunning = true;
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(DebuggerModel.RUNNING);
					Workbench.getWorkbench().getSimulationController().getLogPanel().clearLog();
					erg = SchemeHamster.getJS().eval(befehl);
					isRunning = false;
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(
									DebuggerModel.NOT_RUNNING);

					if (erg == null) {
						erg = "";
						// falls ergebnis true oder false is in #t
						// und #f umwandeln
					} else if (erg.getClass() == java.lang.Boolean.class) {
						if (((Boolean) erg).booleanValue()) {
							erg = "#t";
						} else {
							erg = "#f";
						}

					} else {
						erg = erg.toString();
					}
				} catch (Exception e) {
					//System.out.println(e.toString());
					erg = e.toString();
				} finally {
					isRunning = false;
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(
									DebuggerModel.NOT_RUNNING);
					// Ausgabe erzeugen
					// text der display funktion vor ausgabe
					// schreiben
					String ausgabe = ">" + befehl + "\n" + displaySpeicher
							+ erg + "\n";
					SchemeKonsole.getOutput().append(ausgabe);
					// output runterscrollen
					// int max = out.getVerticalScrollBar().getMaximum();
					// out.getVerticalScrollBar().setValue(max);
					SchemeKonsole.getOutput().setCaretPosition(SchemeKonsole.getOutput().getDocument().getLength());

					// Eingabefeld leeren
					SchemeKonsole.getInput().setText("");
					// displayspeicher leeren
					displaySpeicher = "";
				}
			}
		};
		hamsterThread.start();
	}

	private static void initKonsole() {
		String s = "(define (read)(de.hamster.scheme.model.SchemeHamster.read))";
		SchemeHamster.getJS().load(s);
		s = "(define (display text)(de.hamster.scheme.model.SchemeHamster.display text))";
		SchemeHamster.getJS().load(s);
		s = "(define (newline)(display \"\\n\"))";
		SchemeHamster.getJS().load(s);
	}

	public void mouseClicked(MouseEvent arg0) {

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		if (arg0.isPopupTrigger()) {
			menu.show(this, arg0.getX(), arg0.getY());
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
