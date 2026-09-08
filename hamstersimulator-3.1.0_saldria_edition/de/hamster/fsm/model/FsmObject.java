package de.hamster.fsm.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.fsm.controller.FsmHamster;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.state.IsNondeterministicException;

/**
 * Ist die Oberklasse aller Elemente, die für das Erstellen eines endlichen Automaten von Nöten sind.
 * Es stellt Methoden bereit, um die Baumstruktur eines endlichen Automaten umzusetzen und zum Speichern
 * dieser.
 * @author Raffaela Ferrari
 *
 */
public abstract class FsmObject extends RenderableObject{
	protected static final Color INTEGRATE_COLOR = Color.LIGHT_GRAY;

	protected FsmHamster hamster = FsmHamster.getFSMHamster();
	protected LinkedList<FsmObject> childs = new LinkedList<FsmObject>();
	protected FsmObject parent;
	
	
	/**
	 * Gibt eine Kopie des Objektes zurück.
	 */
	abstract public FsmObject clone();
	
	/**
	 * Führt die Funktion des Objektes aus.
	 * @param program FsmProgram, dass ausgeführt wird.
	 * @return
	 * @throws IsNondeterministicException 
	 */
	abstract public Object performImplementation(FsmProgram program) throws IsNondeterministicException;

	/**
	 * Setzt den Zoomfaktor neu für alle Elemente des endlichen Automaten
	 * @param more true, wenn er erhöht werden soll
	 */
	public void setZoomFactor(boolean more) {
		if(more) {
			ZOOMFACTOR +=0.05;
			ZOOMFACTORFONT += 1;
			TEXT_FONT = new Font("Arial", Font.BOLD, 10+ ZOOMFACTORFONT);
			
		} else if(ZOOMFACTOR > 0 && ZOOMFACTORFONT>-5){
			ZOOMFACTOR -=0.05;
			ZOOMFACTORFONT -= 1;
			TEXT_FONT = new Font("Arial", Font.BOLD, 10+ ZOOMFACTORFONT);
		}
	}

	/**
	 * Updated für sich und alle Kinder die Texthöhe und -breite
	 */
	public void updateTextCoordinates() {
		setTextCoordinates(this.name);
		for(FsmObject child : this.childs) {
			child.updateTextCoordinates();
		}
	}

	/**
	 * Setzt das Vaterelement von diesem RenderableElement fest.
	 * @param parent {@link FsmObject}, das als Vater gesetzt werden soll.
	 */
	public void setParent(FsmObject parent) {
		this.parent = parent;
	}

	/**
	 * Gibt das Vaterelement von diesem RenderableElement zurück.
	 * @return
	 */
	public FsmObject getParentRenderable() {
		return this.parent;
	}

	/**
	 * Fügt ein Kind-Element diesem RenderableElement hinzu.
	 * @param child Das Kind-Element, welches hinzugefügt werden soll.
	 */
	public void add(FsmObject child) {
		this.childs.add(child);
	}

	/**
	 * Löst das Kind vom Eltern-Element ab.
	 * @param child Kind-Element, welches abgelöst werden soll.
	 */
	public void removeChildFromParent(FsmObject child) {
		this.childs.remove(child);
	}
	
	/**
	 * Setzt die Kinder-Elemente.
	 * @param childs Kinder-Elemente, die gesetzt werden sollen.
	 */
	public void setChilds(LinkedList<FsmObject> childs) {
		this.childs = new LinkedList<FsmObject>(childs);
	}
	
	/**
	 * Gibt alle Kinderelemente zurück.
	 * @return
	 */
	public LinkedList<FsmObject> getChilds() {
		return this.childs;
	}

	/**
	 * Überprüft ob das RenderableElement bzw. eines seiner Kinder
	 * angeklickt wurden.
	 * @param x x-Koordinate des Klicks
	 * @param y y-Koordinate des Klicks
	 */
	public FsmObject isClicked(int x, int y) {
		if(isClickedOn(x, y)) {
			return this;
		} else {
			for(FsmObject child : this.childs) {
				FsmObject tmp = child.isClicked(x, y);
				if(tmp != null) {
					return tmp;
				}
			}
		}
		return null;
	}

	/**
	 * Rendert sich und alle Kinder
	 * @param g
	 */
	@Override
	public void render(Graphics g) {
		for(FsmObject child : this.childs) {
			child.render(g);
		}
	}

	/**
	 * Führt das RenderableElement programtechnisch aus. Dies ist eine zusätzliche Funktion, damit
	 * auf Stopp und Pause reagiert werden kann.
	 * @param program Program, das ausgeführt werden soll.
	 * @return
	 * @throws IsNondeterministicException 
	 * @throws NoTransistionInputDefinedException
	 * @throws NoTransistionOutputDefinedException
	 */
	public Object perform(FsmProgram program) throws IsNondeterministicException {
		// Wenn Programm gestoppt ist, sofort wieder verlassen
		try {
			if (program.isStopped()) {
				return new Boolean(false);
			}
			program.isPaused();
	
			// während einer Pause wird stop gedrückt
			if (program.isStopped()) {
				return new Boolean(false);
			}
	
			highlight(true);
			program.updateUpdateHandler();
			Object temp = performImplementation(program);
			highlight(false);
			program.updateUpdateHandler();
	
			//Pause und Stopp überprüfen
			if (program.isStopped()) {
				return new Boolean(false);
			}
			return temp;
		} finally {
			highlight(false);
			program.updateUpdateHandler();
		}
	}

	/**
	 * Methode um pro Einrückung einen Linefeed einzufügen.
	 * @param buffer StringBuffer, dem die Einschübe übergeben werden.
	 * @param indentation Anzahl der Einschübe
	 */
	public static void startLine(StringBuffer buffer, int indentation) {
		for (int i = 0; i < indentation; i++)
			buffer.append(LINEFEED);
	}

	/**
	 * Methode um pro Einrückung einen Linefeed einzufügen.
	 * @param writer xmlStreamWriter, dem die Einschübe übergeben werden.
	 * @param indentation Anzahl der Einschübe
	 * @throws XMLStreamException
	 */
	public static void setLinefeed(XMLStreamWriter writer, int indentation) throws XMLStreamException {
		for (int i = 0; i < indentation; i++)
			writer.writeCharacters(LINEFEED);
	}
}
