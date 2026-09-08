package de.hamster.flowchart.model;

import java.awt.Point;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.flowchart.controller.FlowchartController;
import de.hamster.flowchart.controller.FlowchartHamster;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.controller.FlowchartProgram;

public abstract class FlowchartObject extends RenderObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3048295076686818031L;
	protected ArrayList<String> childIds = new ArrayList<String>();
	protected FlowchartHamster hamster = FlowchartHamster.getFlowchartHamster();
	protected Boolean isHighlighted = false;
	private String perform;
	public boolean terminate;
	protected FlowchartObject trueChild;
	private int trueChildId = -1;
	private String type;
	
	public void sleep() {
		hamster.sleep();
	}

	// 4 Ankerpunkte für die Flusslinien.

	/**
	 * adding the childId to set child objects afterwards
	 * 
	 * @param id
	 *            the ID - read from stored file
	 */
	public void addChildId(String id) {
		this.childIds.add(id);
	}

	public FlowchartAnchor getAnchorAtMouse(Point eventPoint) {
		if (eventPoint.distance(anchorN) < 25) {
			return anchorN;
		}
		if (eventPoint.distance(anchorE) < 25) {
			return anchorE;
		}
		if (eventPoint.distance(anchorS) < 25) {
			return anchorS;
		}
		if (eventPoint.distance(anchorW) < 25) {
			return anchorW;
		}
		return null;
	}

	public FlowchartAnchor getAnchor(int orientation) {
		if (orientation == 0) {
			return anchorN;
		} else if (orientation == 1) {
			return anchorE;
		} else if (orientation == 2) {
			return anchorS;
		} else if (orientation == 3) {
			return anchorW;
		} else {
			// das sollte niemals passieren
			// das passiert nur wenn man die XML manipuliert
			return null;
		}
	}

	@Override
	abstract public FlowchartObject clone();

	public void copyAndReplace(FlowchartObject oldFlowchartObject,
			FlowchartHamsterFile file) {

		this.setTrueChild(oldFlowchartObject.getTrueChild());
		oldFlowchartObject.setTrueChild(null);

		if (this instanceof DecisionObject) {
			((DecisionObject) this)
					.setFalseChild(((DecisionObject) oldFlowchartObject)
							.getFalseChild());
			((DecisionObject) oldFlowchartObject).setFalseChild(null);
		}

		this.setId(oldFlowchartObject.getId());
		oldFlowchartObject.setId(-1);

		// find objects where this is child and replace
		for (FlowchartObject o : file.getProgram().getController()
				.getElements()) {
			if (o.getTrueChild() != null
					&& o.getTrueChild().equals(oldFlowchartObject)) {
				o.setTrueChild(this);

			}

			// if decision object also replace flaseChild
			if (o instanceof DecisionObject
					&& ((DecisionObject) o).getFalseChild() != null
					&& ((DecisionObject) o).getFalseChild().equals(
							oldFlowchartObject)) {
				((DecisionObject) o).setFalseChild(this);
			}
		}

		this.setCoordinates(oldFlowchartObject.x, oldFlowchartObject.y);

		// old object no longer required
		oldFlowchartObject.destroy();
	}

	public void destroy() {
		try {
			this.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
	}

	/**
	 * Setzt, ob ein RenderableElement grafisch hervorgehoben werden soll.
	 * 
	 * @param shouldHighlight
	 *            sagt aus, ob ein RenderableElement grafisch hervorgehoben
	 *            werden soll.
	 */
	public void doHighlight(boolean shouldHighlight) {
		this.isHighlighted = shouldHighlight;
	}

	public synchronized Object execute(FlowchartProgram program) {
		try {

			if (program.isStopped()) {
				return null;
			}
			program.tryPause();

			if (program.isStopped()) {
				return null;
			}

			Object tmp = executeImpl(program);

			if (program.isStopped()) {
				return null;
			}

			return tmp;

		} finally {
		}

	}

	/**
	 * Führt die implementierte Funktion aus
	 * 
	 * @param program
	 *            Das Flowchart Programm
	 * @return Das Ergebnis der Ausführung vom Typ Object
	 */
	abstract public Object executeImpl(FlowchartProgram program);

	public String getPerform() {
		return this.perform;
	}

	public FlowchartObject getTrueChild() {
		return trueChild;
	}

	public int getTrueChildId() {
		return this.trueChildId;
	}

	public String getType() {
		return this.type;
	}

	/**
	 * Löscht sich selbst aus dem HamsterFile.
	 * 
	 * @param file
	 *            Das HamsterFile. Wird benötigt um an die Elemente zu gelangen.
	 */
	public void remove(FlowchartHamsterFile file) {

		FlowchartController c = file.getProgram().getController();

		for (FlowchartObject o : c.getElements()) {
			if (o.getTrueChild() != null && o.getTrueChild().equals(this)) {
				o.setTrueChild(null);
				o.setTrueChildId(-1);
			}

			if (o instanceof DecisionObject
					&& ((DecisionObject) o).getFalseChild() != null
					&& ((DecisionObject) o).getFalseChild().equals(this)) {
				((DecisionObject) o).setFalseChild(null);
				((DecisionObject) o).setFalseChildId(-1);
			}
		}

		// dieses Objekt auch aus dem Controller entfernen
		c.getElements().remove(this);

		// entferne alle Verbindungen von und zu diesem Element
		for (FlowchartTransition t : c.getTransitions()) {
			if (t.getSourceObject().equals(this)
					|| t.getDestinationObject().equals(this)) {
				c.getTransitions().remove(t);
			}
		}

		// entferne Kommentar-Linie
		for (FlowchartMethod m : c.getMethods()) {
			for (CommentObject k : m.getCommentList()) {
				if (k.getRelatedObjekt().equals(this)) {
					k.setRelatedObject(null);
				}
			}
		}
	}

	public void setPerform(String p) {
		this.perform = p;
	}

	public void setTerminate() {
		this.terminate = true;
	}

	public void setTrueChild(FlowchartObject o) {
		this.trueChild = o;
	}

	public void setTrueChildId(int id) {
		this.trueChildId = id;
	}

	public void setType(String t) {
		this.type = t;
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters("\t\t");
		writer.writeStartElement("element");
		writer.writeAttribute("id", String.valueOf(this.id));
		writer.writeAttribute("string", this.text);
		if (this.perform != null)
			writer.writeAttribute("perform", this.perform);
		if (this.type != null)
			writer.writeAttribute("type", this.type);
		writer.writeAttribute("x", String.valueOf(this.getX()));
		writer.writeAttribute("y", String.valueOf(this.getY()));
		if (this.terminate)
			writer.writeAttribute("terminate", "true");
		writer.writeCharacters("\n\t\t\t");
		if (this.getTrueChild() != null) {
			writer.writeEmptyElement("nextId");
			writer.writeAttribute("return", "true");
			writer.writeAttribute("value",
					String.valueOf(this.getTrueChild().id));
		}
		if (this instanceof DecisionObject
				&& ((DecisionObject) this).getFalseChild() != null) {
			writer.writeCharacters("\n\t\t\t");
			writer.writeEmptyElement("nextId");
			writer.writeAttribute("return", "false");

			writer.writeAttribute("value",
					String.valueOf(((DecisionObject) this).getFalseChild().id));
		}
		writer.writeCharacters("\n");
		writer.writeCharacters("\t\t");
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}

	public FlowchartObject() {
		super();
		this.setBounds(0, 0, 90, 60);

	}
}
