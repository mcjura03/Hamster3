package de.hamster.fsm.model.transition;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Klasse, die die äußere Hülle des Outputs repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class OutputObject extends FsmObject {
	private boolean fullView = false;

	/**
	 * Konstruktor
	 */
	public OutputObject() {
		super();
		this.name = "...";
		setTextCoordinates(name);
	}

	@Override
	public int getWidth() {
		int maxWidth = 0;
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		for( int i =0; i<this.childs.size(); i++) {
			child = iterator.next();
			if(maxWidth < child.getWidth()) {
				maxWidth = child.getWidth();
			}
			if(i == 1 && !fullView) {
				break;
			}
		}
		return maxWidth;
	}

	@Override
	public int getHeight() {
		int maxHeight = 0;
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		for( int i =0; i<this.childs.size(); i++) {
			child = iterator.next();
			maxHeight += child.getHeight();
			if(i == 1 && !fullView) {
				break;
			}
		}
		if(this.childs.size()>2) {
			maxHeight += 10 + textHeight;
		}
		return maxHeight;
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		return null;
	}

	@Override
	public FsmObject isClicked(int x, int y) {
		if(!fullView && this.childs.size() > 2) {
			int height = this.childs.get(0).getHeight() + this.getChilds().get(1).getHeight() + 4;
			if(x > this.xStart + height) {
				return null;
			}
		}
		for(FsmObject child : this.childs) {
			FsmObject tmp = child.isClicked(x, y);
			if(tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return false;
	}

	/**
	 * Überprüft für die Hover-Methode, ob das OutputObject angeklickt wurde.
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
		int offset = 0;
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		for( int i =0; i<this.childs.size(); i++) {
			child = iterator.next();
			child.setCoordinates(this.xStart, this.yStart + offset);
			offset += child.getHeight() + 2;
		}
		if(!fullView && this.childs.size()>2) {
			int height = this.childs.get(0).getHeight() + this.getChilds().get(1).getHeight() + 4;
			Graphics gcopy = g.create();
			gcopy.setClip(this.xStart, this.yStart, 100, height);
			super.render(gcopy);
			g.drawString("...", this.xStart, this.yStart + height + 6);
		} else {
			super.render(g);
		}
	}

	/**
	 * Fügt ein Kind-Element diesem RenderableElement hinzu. Setzt dabei gleich das Parent-Objekt.
	 * @param child Das Kind-Element, welches hinzugefügt werden soll.
	 */
	public void add(FsmObject child) {
		child.setParent(this);
		this.childs.add(child);
	}

	@Override
	public FsmObject clone() {
		OutputObject clonedOutputObject = new OutputObject();
		clonedOutputObject.setChilds(this.childs);
		clonedOutputObject.setCoordinates(this.xStart, this.yStart);
		clonedOutputObject.setParent(this.parent);
		return clonedOutputObject;
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
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		while (iterator.hasNext()) {
			child = iterator.next();
			child.perform(program);
		}
		return null;
	}

	/**
	 * Methode für die Transformation des Endlichen Automaten in ein imperatives Java-Program.
	 * @param buffer Buffer, indem der SourceCode geschrieben wird.
	 * @param indentation Einrückfaktor
	 */
	public void writeSourceCode(StringBuffer buffer, int indentation) {
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		while (iterator.hasNext()) {
			child = iterator.next();
			VoidObject childVoid = (VoidObject) child;
			childVoid.writeSourceCode(buffer, indentation+1);
		}
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		if (this.childs.size() > 0) {
			writer.writeCharacters(NEWLINE);
			for(FsmObject child: this.childs) {
				child.toXML(writer);
			}
		}
	}

	/**
	 * Methode, um aus der Xml-Repräsentation des endlichen Automaten Objecte zu generieren.
	 * @param outputElement Mit diesem Element können die entsprechenden Xml-Elemente ausgelesen werden,
	 * um das OutputObject zu erstellen.
	 */
	public void loadProgramm(Element outputElement) {
		NodeList outputElementList = outputElement.getElementsByTagName("voidObject");
		for (int k=0; k<outputElementList.getLength(); k++) {
			Element voidObjectElement = (Element)outputElementList.item(k);

			//Lade alle Attribute
			String name = voidObjectElement.getAttribute("name");

			VoidObject child = (VoidObject) FsmUtils.getTransistionDescriptionELementByName(name);

			//Füge es als Aktion des Outputs hinzu
			add(child);
		}
	}
}
