package de.hamster.fsm.model.transition;

import java.awt.Graphics;
import java.util.List;
import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Klasse, die die äußere Hülle des Inputs repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class InputObject extends FsmObject  {
	private boolean fullView = false;

	/**
	 * Konstruktor
	 */
	public InputObject() {
		super();
		this.name = "...";
		setTextCoordinates(name);
	}

	@Override
	public int getWidth() {
		int maxWidth = 0;
		if(this.childs.size()>0) {
			maxWidth = this.childs.get(0).getWidth();
		}
		if(!fullView && maxWidth > 100) {
			maxWidth = 115;
		}
		return maxWidth;
	}

	@Override
	public int getHeight() {
		if(this.childs.size()>0) {
			return this.childs.get(0).getHeight();
		}
		return 0;
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		return null;
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return false;
	}

	@Override
	public FsmObject isClicked(int x, int y) {
		if(!fullView && getWidth() == 115 && x > (this.xStart+115)) {
			return null;
		}
		for(FsmObject child : this.childs) {
			FsmObject tmp = child.isClicked(x, y);
			if(tmp != null) {
				return tmp;
			}
		}
		return null;
	}
	
	/**
	 * Überprüft für die Hover-Methode, ob das InputObject angeklickt wurde.
	 * @param x X-Koordinate des Klicks
	 * @param y Y-Koordinate des Klicks
	 * @return
	 */
	public boolean isClickedOnHover(int x, int y) {
		return (x>this.xStart && x<(this.xStart+getWidth()) && y>this.yStart 
				&& y<(this.yStart + getHeight()));
	}


	/**
	 * Setzt ob alle Elemente gezeigt werden sollen, wenn es mehr als 2 sind
	 * @param fullView true, wenn alle Elemente gezeigt werden sollen
	 */
	public void setFullView(boolean fullView) {
		this.fullView = fullView;
	}

	@Override
	public void render(Graphics g) {
		if(this.childs.size() > 0) {
			this.childs.get(0).setCoordinates(this.xStart, this.yStart);
		}
		
		if(!fullView && getWidth() == 115) {
			Graphics gcopy = g.create();
			gcopy.setClip( getXCoordinate(), getYCoordinate(), 100, this.childs.get(0).getHeight()+2);
			super.render(gcopy);
			g.drawString("...", this.xStart + 105, this.yStart+ getHeight()/2);
		} else {
			super.render(g);
		}
	}

	/**
	 * Fügt ein Kind-Element diesem InputObject hinzu. Da dieses InputObject immer nur ein
	 * Kind-Element besitzt, wird ggf. das bestehende Kindelement vorher entfernt.
	 * @param child Das Kind-Element, welches hinzugefügt werden soll
	 */
	public void add(FsmObject child) {
		if(this.childs.size()>0) {
			this.childs.remove(0);
		}
		child.setParent(this);
		this.childs.add(child);
	}

	/**
	 * Methode für die Transformation des Endlichen Automaten in ein imperatives Java-Program.
	 * @param buffer Buffer, indem der SourceCode geschrieben wird.
	 * @param indentation Einrückfaktor
	 */
	public void writeSourceCode(StringBuffer buffer, int indentation){
		if(this.childs.size()>0) {
			((BooleanObject)this.childs.get(0)).writeSourceCode(buffer, 0);
		}
	}

	@Override
	public FsmObject clone() {
		InputObject clonedInputObject = new InputObject();
		clonedInputObject.setChilds(this.childs);
		clonedInputObject.setCoordinates(xStart, yStart);
		clonedInputObject.setParent(this.parent);
		return clonedInputObject;
	}


	@Override
	public Object perform(FsmProgram program) throws IsNondeterministicException {
			highlight(true);
			program.updateUpdateHandler();
			Object temp = performImplementation(program);
			highlight(false);
			program.updateUpdateHandler();
			return temp;
	}

	@Override
	public Object performImplementation(FsmProgram program) throws IsNondeterministicException {
		return this.childs.get(0).perform(program);
	}
	
	/**
	 * Überprüft, ob dieser Input für die Ausführung überhaupt in Frage kommt.
	 * Dabei wird der Schritt simuliert.
	 * @param program FsmProgram, das ausgeführt werden soll.
	 * @return true, wenn dieser Input für die Ausführung in Frage kommt.
	 * @throws NoTransistionInputDefinedException
	 * @throws NoTransistionOutputDefinedException
	 */
	public Object checkPerform(FsmProgram program) {
		return ((BooleanObject)this.childs.get(0)).checkPerform(program);
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		if (this.childs.size() > 0) {
			writer.writeCharacters(NEWLINE);
			this.childs.get(0).toXML(writer);
		}
	}

	/**
	 * Methode, um aus der Xml-Repräsentation des endlichen Automaten Objecte zu generieren.
	 * @param inputElement Mit diesem Element können die entsprechenden Xml-Elemente ausgelesen werden,
	 * um das InputObject zu erstellen.
	 */
	public void loadProgramm(Element inputElement) {
		Element booleanElement = (Element) inputElement.getElementsByTagName("booleanObject")
				.item(0);
		if(booleanElement != null) {
			String name = booleanElement.getAttribute("name");
			BooleanObject booleanObject = (BooleanObject) FsmUtils.getTransistionDescriptionELementByName(name);
			booleanObject.loadProgramm(booleanElement);
			add(booleanObject);
		}
	}
}
