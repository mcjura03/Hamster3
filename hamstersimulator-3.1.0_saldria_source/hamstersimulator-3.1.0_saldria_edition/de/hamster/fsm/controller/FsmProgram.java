package de.hamster.fsm.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.fsm.controller.handler.UpdateHandler;
import de.hamster.fsm.model.CommentObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.model.state.StateObject;
import de.hamster.fsm.model.transition.TransitionObject;
import de.hamster.fsm.view.NoTransitionFoundException;
import de.hamster.workbench.Workbench;

/**
 * Ein FSMProgramm stellt eine interne Repräsentation eines endlichen Automaten
 * dar
 * 
 * @author dibo
 * 
 */
public class FsmProgram extends Thread {
	public static final String NEWLINE = "\n";
	public static final String LINEFEED = "\t";

	FsmHamsterFile file;
	private boolean isPaused;
	private boolean isStopped;

	/**
	 * Automaten-Komponenten
	 */
	private boolean isNondeterministic;
	private int numberingOfStates;
	private StateObject startState;
	private StateObject currentState;
	private CopyOnWriteArrayList<StateObject> states;
	private CopyOnWriteArrayList<CommentObject> comments;
	private UpdateHandler updateHandler;

	/**
	 * Default-Konstruktor: erstellt ein leeres FSM-Programm
	 */
	public FsmProgram(FsmHamsterFile file) {
		super();
		this.file = file;
		
		this.isPaused = false;
		this.isStopped = false;

		this.isNondeterministic = false;
		this.numberingOfStates = 0;
		this.states = new CopyOnWriteArrayList<StateObject>();
		this.comments = new CopyOnWriteArrayList<CommentObject>();
	}

	/**
	 * Copy-Konstruktor: erzeugt eine wertegleiche Kopie des übergebenen
	 * FSM-Programms
	 * 
	 * @param p
	 *            ein existierendes FSM-Programm
	 * @param file
	 *            das zugeordnete HamsterFile
	 */
	public FsmProgram(FsmProgram p, FsmHamsterFile file) {
		super(p);
		this.file = file;
		
		this.isPaused = p.isPaused;
		this.isStopped = p.isStopped();

		this.isNondeterministic = p.isNondeterministic;
		this.numberingOfStates = p.numberingOfStates;
		this.states = p.getAllStates();
		this.comments = p.getAllComments();
		this.startState = p.getStartState();
		this.currentState = p.getCurrentState();
		this.updateHandler = p.updateHandler;
	}

	/**
	 * Copy-Konstruktor: erzeugt eine wertegleiche Kopie des übergebenen
	 * FSM-Programms
	 * 
	 * @param p
	 *            ein existierendes FSM-Programm
	 * @param file
	 *            das zugeordnete HamsterFile
	 */
	public FsmProgram(FsmProgram p) {
		this(p, p.file);
	}

	/**
	 * Ausführung des Programms
	 */
	@Override
	public void run() {
		FsmHamster hamster = FsmHamster.getFSMHamster();
		try {
			performImplementation();
		} catch (final Throwable exc) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, exc.toString(),
							"Endlicher Automat-Exception", JOptionPane.ERROR_MESSAGE,
							null);
				}
			});
//			exc.printStackTrace();
		} finally {
			this.updateHandler.updateObject();
			this.isPaused = false;
			this.isStopped = false;
			this.finish(hamster);
		}
	}

	/**
	 * wird aufgerufen, wenn der Nutzer den Stopp-Button gedrückt hat
	 */
	public synchronized void stopProgram() {
		this.notify();
		this.isStopped = true;
	}

	/**
	 * wird aufgerufen, wenn der Nutzer den Pause-Button gedrückt hat
	 */
	public synchronized void pauseProgram() {
		isPaused = true;
	}

	/**
	 * wird aufgerufen, wenn der Nutzer den Resume-Button gedrückt hat
	 */
	public synchronized void resumeProgram() {
		isPaused = false;
		this.notify();
	}

	/**
	 * wird aufgerufen, wenn der Nutzer den Step-Button gedrückt hat
	 */
	public synchronized void stepInto() {
		this.notify();
		this.isPaused = true;
	}

	/**
	 * muss in allen Fällen aufgerufen werden, in denen das Programm beendet
	 * wird
	 * 
	 * @param hamster
	 */
	private void finish(FsmHamster hamster) {
		hamster.setProgramFinished();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Workbench.getWorkbench().getDebuggerController()
						.getDebuggerModel().setState(DebuggerModel.NOT_RUNNING);
				Workbench.getWorkbench().getEditor().getTabbedTextArea()
						.propertyChange(FsmProgram.this.file, false);
			}
		});
	}

	/**
	 * Pausiert dn Thread, wenn isPaused true ist
	 */
	public synchronized void isPaused() {
		if (this.isPaused) {
			try {
				this.wait();
			} catch (InterruptedException exc) {

			}
		}
	}

	/**
	 * Gibt zurück, ob das Programm gestoppt wurde
	 * @return
	 */
	public synchronized boolean isStopped() {
		return this.isStopped;
	}

	/**
	 * Konvertiert das FSM-Programm in ein äquivalentes imperatives
	 * Java-Hamster-Programm und liefert hierzu den Quellcode
	 * 
	 * @return Quellcode des äquivalenten imperativen Java-Hamster-Programms
	 */
	public String getSourceCode() {
		
		ArrayList<Integer> finalStateList= new ArrayList<Integer>();
		int startStateInteger = 0;

		StringBuffer buffer = new StringBuffer();
		
		//Zustände definieren
		buffer.append("int[] zustaende = {");
		for(StateObject state : this.states) {
			buffer.append(this.states.indexOf(state) + 1);
			if(state.isFinal()) {
				finalStateList.add(this.states.indexOf(state) + 1);
			}
			if(state.isInitial() && this.startState != null && this.startState.equals(state)) {
				startStateInteger = this.states.indexOf(state) + 1;
			}
			if(!this.states.get(states.size()-1).equals(state)){
				buffer.append(",");
			}
		}
		buffer.append("};" + NEWLINE);
		
		//Endzustände definieren
		buffer.append("int[] endZustaende = {");
		for(Integer stateName : finalStateList) {
			buffer.append(stateName);
			if(!finalStateList.get(finalStateList.size()-1).equals(stateName)) {
				buffer.append(",");
			}
		}
		buffer.append("};" + NEWLINE);

		//Startzustand definieren
		buffer.append("int startZustand = " + startStateInteger + ";" + NEWLINE);
		
		//Aktueller Zustand definieren
		buffer.append("int aktuellerZustand = " + startStateInteger + ";" + NEWLINE);
		
		//Art des Automaten speichern
		buffer.append("boolean istNichtdeterministisch = " + this.isNondeterministic + ";" + 
		    NEWLINE + NEWLINE);
		
		//Variablen zur temporären Speicherung von Zuständen
		buffer.append("int[] zustandsUebergaenge;" + NEWLINE);
		buffer.append("int anzahlMoeglicherUebergaenge = 0;" + NEWLINE);
		buffer.append("int aktuellerUebergang = 0;" + NEWLINE + NEWLINE);

		buffer.append("void main() {" + NEWLINE);
		
		//Fehler ausgeben, wenn kein Startzustand definiert wurde
		buffer.append(LINEFEED + "if (startZustand == 0) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + "schreib(\"kein Startzustand definiert\");" + NEWLINE);
		buffer.append(LINEFEED + "}" + NEWLINE);
		
		//Zustandsübergänge definieren
		buffer.append(LINEFEED + "while (aktuellerUebergang != -1) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + "anzahlMoeglicherUebergaenge = 0;" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + "switch (aktuellerZustand) {" + NEWLINE);
		for(StateObject state : this.states) {
			buffer.append(LINEFEED + LINEFEED + LINEFEED + "case " + (this.states.indexOf(state) + 1) 
					+ ":" + NEWLINE);
			buffer.append(LINEFEED + LINEFEED + LINEFEED + LINEFEED + 
					"zustandsUebergaenge = new int["+ state.getNumberOfTransistions() + "];" + NEWLINE);
			state.writeSourceCode(buffer, 4, this.states);
			buffer.append(LINEFEED + LINEFEED + LINEFEED + LINEFEED + "break;" + NEWLINE);
		}
		buffer.append(LINEFEED + LINEFEED + "}" + NEWLINE + LINEFEED + "}" + NEWLINE);
		buffer.append("}" + NEWLINE + NEWLINE);
		
		buffer.append("void waehleTransitionAus() {" + NEWLINE);
		buffer.append(LINEFEED +  "if (anzahlMoeglicherUebergaenge == 0) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + 
				"boolean beimEndzustandAngekommen = false; " + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + 
				"for (int i = 0; i < endZustaende.length; i++) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED +
				"if(endZustaende[i] == aktuellerZustand) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED + LINEFEED +
				"beimEndzustandAngekommen = true;" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED + "}" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED +"}" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED +
				"if (beimEndzustandAngekommen) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED +
				"schreib(\"Erfolgreicher Durchlauf!\");" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED +
				"} else {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED +
				"schreib(\"Fehler: keinen �bergang gefunden!\");" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + "}" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + "aktuellerUebergang = -1;" + NEWLINE);
		buffer.append(LINEFEED + "} else if (anzahlMoeglicherUebergaenge > 1) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED +
				"if (!istNichtdeterministisch) {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED +
				"schreib(\"Es wurden mehrere ausf�hrbare Transitionen" +
				" gefunden, obwohl der Automat als deterministisch eingestellt ist.\");" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED + "aktuellerUebergang = -1;" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED +
				"} else {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED +
				"int index = (int) (Math.random() * anzahlMoeglicherUebergaenge);" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + LINEFEED +
				"aktuellerUebergang = zustandsUebergaenge[index];" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED + "}" + NEWLINE);
		buffer.append(LINEFEED + "} else {" + NEWLINE);
		buffer.append(LINEFEED + LINEFEED +
				"aktuellerUebergang = zustandsUebergaenge[0];" + NEWLINE);
		buffer.append(LINEFEED + "}" + NEWLINE);
		buffer.append("}" + NEWLINE);

		return buffer.toString();
	}

	/**
	 * Erstellt aus der übergebenen Datei ein FSM-Programm
	 * 
	 * @param xmlFile
	 *            eine XML-Datei mit einem abgespeicherten FSM-Programm
	 */
	public void loadProgram(File xmlFile) {
		this.isPaused = false;
		this.isStopped = false;
		
		this.isNondeterministic = false;
		this.startState = null;
		this.numberingOfStates = 0;
		this.states = new CopyOnWriteArrayList<StateObject>();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			Element startStateElement = (Element) doc.getElementsByTagName("startState").item(0);
			String startStateName = "";
			if(startStateElement != null) {
				 startStateName = startStateElement.getAttribute("name");
			}
			Element numberingElement = (Element) doc.getElementsByTagName("numberingOfStates").item(0);
			if(numberingElement !=null) {
				this.numberingOfStates = Integer.parseInt(numberingElement.getAttribute("int"));
			}
			Element typeOfFsmElement = (Element) doc.getElementsByTagName("typeOfFsm").item(0);
			if(typeOfFsmElement !=null) {
				this.isNondeterministic = Boolean.parseBoolean(typeOfFsmElement.getTextContent());
			}
			
			//Zustände parsen
			NodeList statesList = doc.getElementsByTagName("state");
			for (int i=0; i<statesList.getLength(); i++) {
				Element stateElement = (Element)statesList.item(i);
				String name = stateElement.getAttribute("name");
				boolean isFinal = Boolean.parseBoolean(stateElement.getAttribute("final"));
				boolean isInitial = Boolean.parseBoolean(stateElement.getAttribute("initial"));
				int xStart = Integer.parseInt(stateElement.getAttribute("x"));
				int yStart = Integer.parseInt(stateElement.getAttribute("y"));
				StateObject state = new StateObject(isInitial, isFinal);
				state.setName(name);
				state.setCoordinates(xStart, yStart);
				this.states.add(state);
			}
			
			//Transistionen parsen
			NodeList transistionsList = doc.getElementsByTagName("transition");
			for (int i=0; i<transistionsList.getLength(); i++) {
				Element transistionElement = (Element)transistionsList.item(i);
				String parent = transistionElement.getAttribute("fromState");
				String to = transistionElement.getAttribute("toState");
				String bendX = transistionElement.getAttribute("x");
				String bendY = transistionElement.getAttribute("y");
				
				Iterator<StateObject> statesIterator = this.states.iterator();
				StateObject fromState = null;
				StateObject toState = null;
				while(statesIterator.hasNext()) {
					StateObject temp = (StateObject)statesIterator.next();
					if(parent.equals(temp.getName())) {
						fromState = temp;
					}
					if(to.equals(temp.getName())) {
						toState = temp;
					}
				}
				TransitionObject transistion = new TransitionObject(fromState, toState);
				
				int bendIntX = -1;
				int bendIntY = -1;
				if(bendX != "") {
					bendIntX = Integer.parseInt(bendX);
				}
				if(bendY != "") {
					bendIntY = Integer.parseInt(bendY);
				}
				transistion.setBendPoint(bendIntX, bendIntY);

				//TransistionDescriptions parsen
				Element descriptionsElement 
					= (Element) transistionElement.getElementsByTagName("descriptions").item(0);
				transistion.loadProgramm(descriptionsElement);

				//zum Zustand hinzufügen
				if(fromState != null) {
					fromState.addTransition(transistion);
				}
			}
			
			//Anfangs- und momentaner Zustand zuordnen
			Iterator<StateObject> statesIterator = this.states.iterator();
			StateObject temp = null;
			if(!startStateName.equals("")) {
				while(statesIterator.hasNext()) {
					temp = (StateObject)statesIterator.next();
					if(startStateName.equals(temp.getName())) {
						this.startState = temp;
					}
				}
			}

			//Kommentare parsen
			NodeList commentList = doc.getElementsByTagName("comment");
			for (int i=0; i<commentList.getLength(); i++) {
				Element commentElement = (Element)commentList.item(i);
				int xStart = Integer.parseInt(commentElement.getAttribute("x"));
				int yStart = Integer.parseInt(commentElement.getAttribute("y"));
				String text = commentElement.getTextContent();
				CommentObject comment = new CommentObject();
				comment.setName(text);
				comment.setCoordinates(xStart,yStart);
				this.comments.add(comment);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Interne Generierung der XML-Tags für den endlichen Automaten
	 * sowie dessen Zustände und Transistionen
	 * @param writer der XMLStreamWriter, indem die XML Datei aufbereitet wird
	 * @throws XMLStreamException 
	 * @throws NoTransistionInputDefinedException 
	 * @throws NoTransistionOutputDefinedException 
	 */
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("startState");
		writer.writeAttribute("name", this.startState !=null ? this.startState.getName() : "");
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
		writer.writeCharacters(LINEFEED);
		writer.writeStartElement("numberingOfStates");
		writer.writeAttribute("int", String.valueOf(numberingOfStates));
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
		writer.writeCharacters(LINEFEED);
		writer.writeStartElement("typeOfFsm");
		writer.writeCharacters(String.valueOf(this.isNondeterministic));
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
		writer.writeCharacters(LINEFEED);
		
		//Nun werden die einzelnen Zustände und dazugehörige Transistionen eingefügt
		Iterator<StateObject> statesIterator = this.states.iterator();
		StateObject temp = null;
		while(statesIterator.hasNext()) {
			temp = statesIterator.next();
			temp.toXML(writer);
		}

		//Nun werden die einzelnen Kommentare eingefügt
		Iterator<CommentObject> commentsIterator = this.comments.iterator();
		CommentObject tempComment = null;
		while(commentsIterator.hasNext()) {
			tempComment = commentsIterator.next();
			tempComment.toXML(writer);
		}
	}

	/**
	 * Ab hier folgen Methoden für den endlichen Automaten.
	 */

	/**
	 * Fügt einen neuen Zustand hinzu und vergibt dabei einen Default-Namen.
	 * @param state Zustand, der hinzugefügt werden soll.
	 */
	public void addState(StateObject state) {
		state.setName("z" + this.numberingOfStates);
		this.numberingOfStates++;
		this.states.add(state);
		if(this.states.size() == 1) {
			state.setInitial(true);
			setStartState(state);
		}
	}

	/**
	 * Gibt alle Zustände zurück, die der Automat beinhaltet.
	 * @return
	 */
	public CopyOnWriteArrayList<StateObject> getAllStates() {
		return this.states;
	}

	/**
	 * Löscht einen Zustand, sofern er nicht der Initial-Zustand ist.
	 * @param state Zustand, der gelöscht werden soll.
	 */
	public void removeState(StateObject state) {
		if(!this.startState.equals(state) && !state.isInitial()) {
			this.states.remove(state);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Der Startzustand kann nicht"
							+ " gel�scht werden!","Endlicher Automat-Exception", 
							JOptionPane.ERROR_MESSAGE, null);
				}
			});
		}
	}

	/**
	 * Setzt die Position in der Liste neu für einen State
	 * @param state State, für den die Position neu gesetzt werden soll.
	 */
	public void updateStatePositionInStateList(StateObject state) {
		int index = this.states.lastIndexOf(state);
		if(index != (this.states.size()-1)) {
			this.states.remove(state);
			this.states.add(state);
		}
	}

	/**
	 * Gibt den Startzustand zurück.
	 * @return
	 */
	public StateObject getStartState() {
		return this.startState;
	}

	/**
	 * Setzt den Startzustand neu.
	 * @param state Zustand, der nun Startzustand sein soll.
	 */
	public void setStartState(StateObject state) {
		this.startState = state;
	}

	/**
	 * Fügt einen neuen Kommentar dem Automaten hinzu.
	 * @param newComment Kommentar, der hinzugefügt werden soll.
	 */
	public void addComment(CommentObject newComment) {
		this.comments.add(newComment);	
	}

	/**
	 * Gibt alle Kommentare des Automaten zurück.
	 * @return
	 */
	public CopyOnWriteArrayList<CommentObject> getAllComments() {
		return this.comments;
	}
	
	/**
	 * Löscht einen Kommentar.
	 * @param comment Kommentar, der gelöscht werden soll.
	 */
	public void removeComment(CommentObject comment) {
		this.comments.remove(comment);
	}

	/**
	 * Setzt die Position in der Liste neu für einen Comment
	 * @param comment Comment, für den die Position neu gesetzt werden soll.
	 */
	public void updateStatePositionInCommentList(CommentObject comment) {
		int index = this.comments.lastIndexOf(comment);
		if(index != (this.comments.size()-1)) {
			this.comments.remove(comment);
			this.comments.add(comment);
		}
	}

	/**
	 * Gibt den momentanen Zustand zurück.
	 * @return
	 */
	public StateObject getCurrentState() {
		return this.currentState;
	}

	public Object performImplementation() throws NoTransitionFoundException, IsNondeterministicException {
		this.currentState = this.startState;
		while(this.currentState != null) {
			Object object = (Object) this.currentState.perform(this);
			StateObject temp = null;
			if(object != null && object.equals(false)) {
				break;
			} else if (object != null && object instanceof StateObject) {
				temp = (StateObject) object;
			}
			if(temp != null) {
				this.currentState = temp;
			} else {
				if(this.currentState.isFinal() != true) {
					throw new NoTransitionFoundException(this.currentState.getName());
				} else {
					this.currentState = null;
				}
			}
		}
		return null;
	}

	/**
	 * Setzt den Typ des Automaten neu.
	 * @param isNondeterministic gibt an, dass der Automat bei true nichtdeterministisch ist.
	 */
	public void setTypeOfFsm(boolean isNondeterministic) {
		this.isNondeterministic = isNondeterministic;
	}

	/**
	 * Gibt zurück, ob es sich um einen nichtdeterministischen Automaten handelt.
	 * @return true, wenn der Automat nichtdeterministisch ist.
	 */
	public boolean isNondeterministic() {
		return this.isNondeterministic;
	}

	/**
	 * Setzt ein Object als UpdateHandler für dieses FsmProgramm
	 * @param object
	 */
	public void setUpdateHandler(UpdateHandler object) {
		this.updateHandler = object;
	}
	
	/**
	 * Der UpdateHandler wird davon informatiert, dass sich etwas verändert hat.
	 */
	public void updateUpdateHandler() {
		this.updateHandler.updateObject();
	}
}
