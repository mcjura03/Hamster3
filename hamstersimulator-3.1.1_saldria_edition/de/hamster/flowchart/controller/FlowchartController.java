package de.hamster.flowchart.controller;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTabbedPane;

import de.hamster.flowchart.model.FlowchartMethod;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.FlowchartTransition;

/**
 * Diese Klasse speichert den aktuellen Status eines Flowchart-Programms. Alle
 * Elemente, Methoden und Transitionen werden hier gespeichert.
 * 
 * @author gerrit
 * 
 */
public class FlowchartController {

	private CopyOnWriteArrayList<FlowchartObject> elements;
	private CopyOnWriteArrayList<FlowchartMethod> methods;
	private CopyOnWriteArrayList<FlowchartTransition> transitions;

	private FlowchartObject start;
	private FlowchartObject stop;
	private FlowchartObject tmpObject;

	private JTabbedPane flowchartTabbedPane;

	public static CopyOnWriteArrayList<String> COMMANDS = new CopyOnWriteArrayList<String>();
	public static CopyOnWriteArrayList<String> DECISIONS = new CopyOnWriteArrayList<String>();
	public static CopyOnWriteArrayList<String> STARTSTOP = new CopyOnWriteArrayList<String>();
	public CopyOnWriteArrayList<String> PROCEDURES = new CopyOnWriteArrayList<String>();

	static {
		COMMANDS.add("vor");
		COMMANDS.add("linksUm");
		COMMANDS.add("nimm");
		COMMANDS.add("gib");

		DECISIONS.add("vornFrei");
		DECISIONS.add("kornDa");
		DECISIONS.add("maulLeer");

		STARTSTOP.add("Start");
		STARTSTOP.add("Stop");
	}

	/**
	 * Der FlowchartController speichert die gesammte Programmstruktur. Alle
	 * Informationen des Programmablaufplans werden hier verwaltet.
	 * 
	 * @param isNew
	 *            Der Parameter der angibt, ob ein existierendes Programm
	 *            geladen wird oder ein neues Programm erstellt werden soll.
	 * @param file
	 */
	public FlowchartController(boolean isNew, FlowchartHamsterFile file) {
		super();
		elements = new CopyOnWriteArrayList<FlowchartObject>();
		methods = new CopyOnWriteArrayList<FlowchartMethod>();
		transitions = new CopyOnWriteArrayList<FlowchartTransition>();

		if (isNew) {
			FlowchartMethod flowchartMain = new FlowchartMethod(file, "main");
			flowchartMain.getDrawPanel().setTmpTransitionList(transitions);
			flowchartMain.setName("main");
			this.addMethod(flowchartMain);
			this.PROCEDURES.add("main");
		}
	}

	/**
	 * Setzt das Start-Objekt für das Flowchart Programm
	 * 
	 * @param element
	 *            Das neue Start-Objekt
	 */
	public void setStart(FlowchartObject element) {
		this.start = element;
	}

	/**
	 * Setzt das Stop-Objekt für das Flowchart Programm
	 * 
	 * @param element
	 *            Das neue Stop-Objekt
	 */
	public void setStop(FlowchartObject element) {
		this.stop = element;
	}

	/**
	 * Fügt ein neues Element der Element-Liste hinzu
	 * 
	 * @param newElement
	 *            Das neue Element
	 */
	public void addElement(FlowchartObject newElement) {
		this.elements.add(newElement);
	}

	/**
	 * Fügt eine neue Methode der Methoden-Liste hinzu
	 * 
	 * @param newMethod
	 *            Die neue Methode
	 */
	public void addMethod(FlowchartMethod newMethod) {
		this.methods.add(newMethod);
	}

	/**
	 * Fügt eine neue Transition der Transitions-Liste hinzu
	 * 
	 * @param newTransition
	 *            Die neue Transition
	 */
	public void addTransition(FlowchartTransition newTransition) {
		this.transitions.add(newTransition);
	}

	/**
	 * Gibt das Start Objekt des Programms zurück
	 * 
	 * @return Das Start-Objekt
	 */
	public FlowchartObject getStart() {
		return this.start;
	}

	/**
	 * Gibt das Stop Objekt des Programms zurück
	 * 
	 * @return Das Stop-Objekt
	 */
	public FlowchartObject getStop() {
		return this.stop;
	}

	/**
	 * Gibt die Methodes des Flowchart-Programms zurück
	 * 
	 * @return Die Methoden
	 */
	public CopyOnWriteArrayList<FlowchartMethod> getMethods() {
		return this.methods;
	}

	/**
	 * Gibt die Elemente des Flowchart-Programms zurück
	 * 
	 * @return Die Elemente
	 */
	public CopyOnWriteArrayList<FlowchartObject> getElements() {
		return this.elements;
	}

	/**
	 * Gibt die Transitionen des Flowchart-Programms zurück
	 * 
	 * @return Die Transitionen
	 */
	public CopyOnWriteArrayList<FlowchartTransition> getTransitions() {
		return this.transitions;
	}

	/**
	 * Gibt die JTabbedPane des Programms zurück Wird benötigt um neue Tabs
	 * hinzuzufügen.
	 * 
	 * @return Die JTabbedPane
	 */
	public JTabbedPane getFlowchartTabbedPane() {
		return this.flowchartTabbedPane;
	}

	/**
	 * Setzt die JTabbedPane in der die Methoden aufgelistet sind/werden
	 * 
	 * @param drawPanel
	 *            Die JTabbedPane
	 */
	public void setFlowchartTabbedPane(JTabbedPane drawPanel) {
		this.flowchartTabbedPane = drawPanel;
	}

	/**
	 * Löscht eine alte Methode von der Methoden-Liste Wird bspw. aufgerufen,
	 * wenn eine Methode aktualisiert wird.
	 * 
	 * @param oldMethod
	 *            Die alte Methode
	 */
	public void removeMethod(FlowchartMethod oldMethod) {
		this.methods.remove(oldMethod);
	}

	@Override
	public String toString() {
		return "FlowchartController [elements=" + elements + ", methods="
				+ methods + ", transitions=" + transitions + ", start=" + start
				+ ", stop=" + stop + ", tmpObject=" + tmpObject
				+ ", flowchartTabbedPane=" + flowchartTabbedPane
				+ ", PROCEDURES=" + PROCEDURES + "]";
	}

}
