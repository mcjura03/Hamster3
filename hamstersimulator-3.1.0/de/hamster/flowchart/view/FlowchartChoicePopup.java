package de.hamster.flowchart.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import de.hamster.flowchart.controller.FlowchartController;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.model.CommandObject;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.DecisionObject;
import de.hamster.flowchart.model.FlowchartMethod;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.FlowchartTransition;
import de.hamster.flowchart.model.ProcedureObject;
import de.hamster.flowchart.model.StartStopObject;
import de.hamster.flowchart.model.command.GibCommandObject;
import de.hamster.flowchart.model.command.LinksUmCommandObject;
import de.hamster.flowchart.model.command.NimmCommandObject;
import de.hamster.flowchart.model.command.VorCommandObject;
import de.hamster.flowchart.model.decision.KornDaDecisionObject;
import de.hamster.flowchart.model.decision.MaulLeerDecisionObject;
import de.hamster.flowchart.model.decision.VornFreiDecisionObject;

/**
 * Popup Auswahl zum wählen der Operatoren, Entscheidungen und Prozeduren.
 * 
 * @author gerrit
 * 
 */
public class FlowchartChoicePopup extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = -6799168143647700696L;
	private Component component;
	private FlowchartHamsterFile file;
	private FlowchartObject flowchartElement;
	private Boolean isNot;
	private FlowchartMethod method;
	private Point point;
	private Boolean replace;
	private JMenuItem tmp;
	private CommentObject comment;

	/**
	 * Der Konstruktor
	 * 
	 * @param flowchartElement
	 *            Das Element zu dem eine Auswahl getroffen werden soll.
	 * @param m
	 *            Die Methode in der sich das Element befindet.
	 * @param componentPoint
	 *            Der Punkt an dem das Popup erscheinen soll.
	 * @param component
	 *            Die Komponente zum neuzeichnen und getten.
	 * @param file
	 *            Die Hamster-PAP-File um sich den Controller zu holen.
	 * @param replace
	 *            Ein Boolean in induziert, ob ein Element ausgetauscht wird,
	 *            oder neu erzeugt wird.
	 */
	public FlowchartChoicePopup(FlowchartObject flowchartElement,
			FlowchartMethod m, Point componentPoint, Component component,
			FlowchartHamsterFile file, Boolean replace) {
		super();
		this.flowchartElement = flowchartElement;
		this.method = m;
		this.point = componentPoint;
		this.component = component;
		this.file = file;
		this.isNot = false;
		this.replace = replace;

		// all variables are initiated. proceed ...
		this.createMenuEntries();
	}

	/**
	 * Der Konstruktor
	 * 
	 * @param flowchartElement
	 *            Das Element zu dem eine Auswahl getroffen werden soll.
	 * @param m
	 *            Die Methode in der sich das Element befindet.
	 * @param componentPoint
	 *            Der Punkt an dem das Popup erscheinen soll.
	 * @param component
	 *            Die Komponente zum neuzeichnen und getten.
	 * @param file
	 *            Die Hamster-PAP-File um sich den Controller zu holen.
	 * @param replace
	 *            Ein Boolean in induziert, ob ein Element ausgetauscht wird,
	 *            oder neu erzeugt wird.
	 */
	public FlowchartChoicePopup(CommentObject comment, FlowchartMethod m,
			Point componentPoint, Component component,
			FlowchartHamsterFile file, Boolean replace) {
		super();
		this.comment = comment;
		this.method = m;
		this.point = componentPoint;
		this.component = component;
		this.file = file;
		this.isNot = false;
		this.replace = replace;

		// all variables are initiated. proceed ...
		this.createMenuEntries();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("neu ...")) {

			// creates TextField where you can enter the new procedure name
			// then the procedure will me created.
			new FlowchartProcedureTextField(file, this);
		}
	}

	/**
	 * Fügt ein neues FlowchartObject ein, oder ersetzt es.
	 * 
	 * @param o
	 *            Das neue FlowchartObjekt
	 */
	public void addOrReplaceObject(FlowchartObject o) {
		o.setCoordinates(((FlowchartDrawPanel) component).getGridPoint(point.x,
				point.y));

		if (replace) {
			// kopiere die Daten des angeklickten FlowchartObjekts
			o.copyAndReplace(flowchartElement, file);
			// entferne das alte Objekt
			method.removeElementFromList(flowchartElement);
			// aktualisiere evtl. vorhandene Transitionen
			for (FlowchartTransition t : this.file.getProgram().getController()
					.getTransitions()) {
				if (t.getSourceObject().equals(flowchartElement)) {
					t.setSourceObject(o);
				}
				if (t.getDestinationObject().equals(flowchartElement)) {
					t.setDestinationObject(o, 0, true);
				}
			}
		}
		method.addElementToList(o);

		// fügt auch das Element in eine Liste im Controller hinzu
		this.file.getProgram().getController().addElement(o);

		// wichtig! - updated die methode im laufenden Programm, da diese im
		// Programm häufig veraltet ist
		// this.file.getProgram().updateMethod(method);

		component.repaint();
	}

	private void createMenuEntries() {
		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new GridLayout(0, 1));
		if (this.flowchartElement instanceof CommandObject) {
			for (final String s : FlowchartController.COMMANDS) {
				tmp = new JMenuItem(s);
				tmp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CommandObject tmpCommand = null;
						if (s.equals("vor")) {
							tmpCommand = new VorCommandObject(s);
						} else if (s.equals("linksUm")) {
							tmpCommand = new LinksUmCommandObject(s);
						} else if (s.equals("nimm")) {
							tmpCommand = new NimmCommandObject(s);
						} else if (s.equals("gib")) {
							tmpCommand = new GibCommandObject(s);
						} else {
							// does not exist
						}

						if (tmpCommand != null) {
							addOrReplaceObject(tmpCommand);
						}
					}

				});
				this.add(tmp);
			}
		} else if (this.flowchartElement instanceof DecisionObject) {
			JCheckBox check = new JCheckBox("Aussage negieren?");
			check.setActionCommand("negate");
			choicePanel.add(check);
			check.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					isNot = !isNot;
				}
			});
			for (final String s : FlowchartController.DECISIONS) {
				tmp = new JMenuItem(s);
				tmp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						DecisionObject tmpDecision = null;
						if (s.equals("vornFrei")) {
							tmpDecision = new VornFreiDecisionObject(s, isNot);
						} else if (s.equals("kornDa")) {
							tmpDecision = new KornDaDecisionObject(s, isNot);
						} else if (s.equals("maulLeer")) {
							tmpDecision = new MaulLeerDecisionObject(s, isNot);
						} else {
							// does not exist
						}

						if (tmpDecision != null) {
							addOrReplaceObject(tmpDecision);
						}
					}
				});
				this.add(tmp);
			}
			if (replace) {
				this.addSeparator();
				JMenuItem removeYes = new JMenuItem("entferne Verbindung 'Ja'");
				removeYes.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						for (FlowchartTransition t : file.getProgram()
								.getController().getTransitions()) {
							if (t.hasTrueChild
									&& t.getSourceObject().equals(
											flowchartElement)) {
								file.getProgram().getController()
										.getTransitions().remove(t);
								t.removeDestinationObject();
								file.setModified(true);
								component.repaint();
								break;
							}
						}
					}
				});
				JMenuItem removeNo = new JMenuItem("entferne Verbindung 'Nein'");
				removeNo.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						for (FlowchartTransition t : file.getProgram()
								.getController().getTransitions()) {
							if (!t.hasTrueChild
									&& t.getSourceObject().equals(
											flowchartElement)) {
								file.getProgram().getController()
										.getTransitions().remove(t);
								t.removeDestinationObject();
								file.setModified(true);
								component.repaint();
								break;
							}
						}
					}
				});
				this.add(removeYes);
				this.add(removeNo);
			}
		} else if (this.flowchartElement instanceof ProcedureObject) {
			for (final String s : this.file.getProgram().getController().PROCEDURES) {
				tmp = new JMenuItem(s);
				tmp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						ProcedureObject tmpFlowchartObject = new ProcedureObject(
//								s + "()", s); dibo
								s, s);
						addOrReplaceObject(tmpFlowchartObject);

					}
				});
				this.add(tmp);
			}
			this.addSeparator();
			JMenuItem new_p = new JMenuItem("neu ...");
			new_p.addActionListener(this);
			this.add(new_p);
		} else if (this.flowchartElement instanceof StartStopObject) {
			for (final String s : FlowchartController.STARTSTOP) {
				tmp = new JMenuItem(s);
				tmp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						StartStopObject tmpStartStopObject = new StartStopObject(
								s, method);
						if (s.equals("Start")
								&& method.getName().equals("main")) {
							file.getProgram().getController()
									.setStart(tmpStartStopObject);
						}
						addOrReplaceObject(tmpStartStopObject);
					}
				});
				this.add(tmp);

				// validate if stop or start already exist
				for (FlowchartObject o : this.method.getElemList()) {
					if (o instanceof StartStopObject && o.getText().equals(s)) {
						tmp.setEnabled(false);
					}
				}
			}
		} else if (this.comment != null) {

			tmp = new JMenuItem("entfernen");
			tmp.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					method.getCommentList().remove(comment);
					file.setModified(true);
					component.repaint();

				}
			});
			this.add(tmp);
		}

		// replace is true, when item inside drawPanel is selected. here you can
		// remove items
		if (replace) {

			if (!(this.flowchartElement instanceof DecisionObject)) {
				this.addSeparator();
				tmp = new JMenuItem("entferne Verbindung");
				tmp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						for (FlowchartTransition t : file.getProgram()
								.getController().getTransitions()) {
							if (t.getSourceObject().equals(flowchartElement)) {
								t.removeDestinationObject();
								t.setSourceObject(null);
								file.getProgram().getController()
										.getTransitions().remove(t);
								file.setModified(true);
								component.repaint();
							}
						}
					}
				});
				this.add(tmp);
			}

			this.addSeparator();
			JMenuItem remover = new JMenuItem("entfernen");
			remover.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					flowchartElement.remove(file);
					method.removeElementFromList(flowchartElement);
					component.repaint();

					if (method.getName().equals("main")
							&& flowchartElement.getText().equals("Start")) {
						file.getProgram().getController().setStart(null);
					} else if (method.getName().equals("main")
							&& flowchartElement.getText().equals("Stop")) {
						file.getProgram().getController().setStop(null);
					}
				}
			});
			this.add(remover);
		}
	}

	public Component getFlowchartComponent() {
		return this.component;
	}
}

/**
 * Textfeld zum eingeben des Namens für die neue Methode/Prozedur
 * 
 * @author gerrit
 * 
 */
class FlowchartProcedureTextField extends JFrame implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2165634643731532129L;

	JTextField textfeld = new JTextField(10);

	DefaultCaret dcaret = (DefaultCaret) textfeld.getCaret();

	private FlowchartHamsterFile file;

	private FlowchartChoicePopup popup;

	/**
	 * Textfeld um den Namen eines neuen Unterprogrammes einzugeben.
	 * 
	 * @param file
	 *            Die Flowchart Hamster Datei um sich die aktuelle
	 *            Programmstruktur zu holen
	 * @param flowchartChoicePopup
	 *            Das Popup Fenster, welches dieses Textfeld initiiert hat
	 */
	public FlowchartProcedureTextField(FlowchartHamsterFile file,
			FlowchartChoicePopup flowchartChoicePopup) {
		this.file = file;

		this.popup = flowchartChoicePopup;

		add(textfeld);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);

		// get the mouse position on screen and display textfield there
		PointerInfo a = MouseInfo.getPointerInfo();
		this.setLocation(a.getLocation());

		textfeld.addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		enter_name: switch (e.getKeyCode()) {
		case KeyEvent.VK_DOWN:
			break;
		case KeyEvent.VK_UP:
			break;

		case KeyEvent.VK_ENTER:

			String newProcedureName = textfeld.getText();

			// überprüfe ob es die Prozedur schon gibt
			for (FlowchartMethod m : this.file.getProgram().getController()
					.getMethods()) {
				if (m.getName().equals(newProcedureName)) {
					this.dispose();
					JOptionPane
							.showMessageDialog(
									null,
									"Es gibt bereits ein Unterprogramm mit diesem Namen",
									"Name schon vorhanden",
									JOptionPane.ERROR_MESSAGE);
					break enter_name;

				}
			}

			String pattern = "([a-zA-Z_$][a-zA-Z0-9_$]*)";

			// überprüft ob der Name nur Buchstaben enthält
			if (!newProcedureName.matches(pattern)) {
				this.dispose();
				JOptionPane
						.showMessageDialog(
								null,
								"Der Name darf keine Sonderzeichen (Ausnahme: '_' und '$') enthalten!\n"
										+ "Der Name darf nur mit a-z, A-Z, '_' oder '$' anfangen.",
								"Keine Sonderzeichen",
								JOptionPane.ERROR_MESSAGE);
				break enter_name;
			}

			// inititate new procedure
			FlowchartMethod tmpMethod = new FlowchartMethod(this.file,
					newProcedureName);
			tmpMethod.setDrawPanel(new FlowchartDrawPanel(tmpMethod, true,
					this.file));
			tmpMethod.getDrawPanel().setTmpTransitionList(
					this.file.getProgram().getController().getTransitions());
			this.file.getProgram().getController().PROCEDURES
					.add(newProcedureName);
			this.file.getProgram().getController().getMethods().add(tmpMethod);

			// inititate new DrawPanel for the procedure
			FlowchartDrawPanel tmpDrawPanel = new FlowchartDrawPanel(tmpMethod,
					true, this.file);
			JTabbedPane tmpTabbedPane = this.file.getProgram().getController()
					.getFlowchartTabbedPane();
			tmpTabbedPane.addTab(tmpMethod.getName(), null, new JScrollPane(
					tmpDrawPanel), tmpMethod.getName());

			// finally add the new procedure
			FlowchartObject newProcedure = new ProcedureObject(newProcedureName
//					+ "()", newProcedureName); dibo
					, newProcedureName);
			this.popup.addOrReplaceObject(newProcedure);

			// aktiviere den hinzugefügten Tab bzw. die neue Prozedur
			tmpTabbedPane
					.setSelectedIndex(tmpTabbedPane.getComponentCount() - 1);

			this.dispose();
			break;
		case KeyEvent.VK_ESCAPE:
			this.dispose();
			break;
		default: {
			textfeld.requestFocus();
		}
		}
	}

}
