package de.hamster.fsm.model.transition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.controller.handler.OpenTransitionDescriptionDialogInContextMenu;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.view.ContextMenuPanel;
import de.hamster.fsm.view.FsmPanel;

/**
 * Klasse, die eine Beschriftung einer Transition repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class TransitionDescriptionObject extends FsmObject{
	private static final Color COLOR = new Color(190,190,190);
	private static final Color TEXT_COLOR = new Color(0,0,0);
	private static final int PADDING = 4;
	private static final int TRANSITION_DISTANCE = 5;
	
	private Rectangle2D rectangle;
	private int offset = 0;
	
	/**
	 * Konstruktor
	 * @param parent TransitionObject, zu dem die Beschriftung gehört.
	 */
	public TransitionDescriptionObject(TransitionObject parent) {
		super();
		setParent(parent);
		setName("/");
		InputObject input = new InputObject();
		OutputObject output = new OutputObject();
		setInput(input);
		setOutput(output);
	}

	@Override
	public int getWidth() {
		return (int) this.childs.get(0).getWidth() + this.childs.get(1).getWidth() 
				+ textWidth + 4*PADDING;
	}

	@Override
	public int getHeight() {
		int inputHeight = this.childs.get(0).getHeight();
		int outputHeight = this.childs.get(1).getHeight();
		return (int) (inputHeight>outputHeight ? inputHeight : outputHeight) + 2*PADDING;
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem deleteDescription = new JMenuItem("Beschriftung l�schen");
		deleteDescription.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteDescription);
		
		JMenuItem openDescription = new JMenuItem("Beschriftung �ffnen");
		openDescription.addActionListener(new OpenTransitionDescriptionDialogInContextMenu(this, 
				((FsmPanel)panel).getAutomataPanel()));
		menuItemList.add(openDescription);

		return menuItemList;
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return rectangle.contains(x, y);
	}

	@Override
	public FsmObject isClicked(int x, int y) {
		for(FsmObject child : this.childs) {
			FsmObject tmp = child.isClicked(x, y);
			if(tmp != null) {
					return tmp;
			}
		}
		if(isClickedOn(x, y)) {
			return this;
		}
		return null;
	}

	/**
	 * Setzt den Input neu. Ist schon ein InputObject vorhanden, so wird dieses entfernt und
	 * das übergebene hinzugefügt.
	 * @param input Das neue InputObject
	 */
	public void setInput(InputObject input) {
		if(this.childs.size() > 0) {
			this.childs.remove(0);
		}
		input.setParent(this);
		this.childs.add(0,input);
	}

	/**
	 * Gibt das InputObject zurück.
	 * @return
	 */
	public InputObject getInput() {
		return (InputObject) childs.get(0);
	}

	/**
	 * Setzt den Output neu. Ist schon ein OutputObject vorhanden, so wird dieses entfernt und
	 * das übergebene hinzugefügt.
	 * @param output Das neue OutputObject
	 */
	public void setOutput(OutputObject output) {
		if(this.childs.size()==2) {
			this.childs.remove(1);
		}
		output.setParent(this);
		this.childs.add(1,output);
	}

	/**
	 * Gibt das OutputObject zurück.
	 * @return
	 */
	public OutputObject getOutput() {
		return (OutputObject) childs.get(1);
	}

	/**
	 * Setzt den Offset, der beim Zeichnen des TransitionDescriptionObjects wichtig ist.
	 * @param offset Zahl, um den das TransitionDescriptionObjects versetzt wird auf der Y-Achse.
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public void render(Graphics g) {
		int inputWidth = this.childs.get(0).getWidth();
		int inputHeight = this.childs.get(0).getHeight();
		int outputWidth = this.childs.get(1).getWidth();
		int outputHeight = this.childs.get(1).getHeight();
		int height = (inputHeight>outputHeight ? inputHeight : outputHeight) + 2*PADDING;
		
		TransitionObject parent = (TransitionObject) this.getParentRenderable();
		if(parent.getCurve() != null) {
			setCoordinatesWhenParentIsCurve(parent.getCurve(), parent.getClosestPointFromBendPoint(), 
					inputWidth, outputWidth, height);
		} else if (parent.isLine()) {
			setCoordinatesWhenParentIsLine(parent, inputWidth, outputWidth, height);
		} else {
			double xCoord = (parent.getCubicCurve().getCtrlX2()+ parent.getCubicCurve().getCtrlX1())/2;
			this.xStart = (int) (xCoord - (inputWidth + outputWidth + textWidth + 4*PADDING)/2);
			this.yStart = (int) (parent.getCubicCurve().getCtrlY2() + this.offset);
		}

		Graphics2D g2d = (Graphics2D)g;
		
		Stroke stroke = new BasicStroke((float) (LINE_THICKNESS*ZOOMFACTOR));
		g2d.setStroke(stroke);
		
		g2d.setColor(Color.black);
		this.rectangle = new Rectangle2D.Double(this.xStart, this.yStart, inputWidth 
				+ outputWidth + textWidth + 4*PADDING, height);
		
		g2d.draw(this.rectangle);
		g2d.setColor(COLOR);
		g2d.fill(this.rectangle);

		g2d.setColor(TEXT_COLOR);
		g2d.drawString(this.name, this.xStart + 2*PADDING + inputWidth, this.yStart + height/2);
		this.childs.get(0).setCoordinates(this.xStart + PADDING, this.yStart + PADDING);
		this.childs.get(1).setCoordinates(this.xStart + inputWidth 
				+ textWidth + 3*PADDING, this.yStart+PADDING);
		super.render(g);
	}

	/**
	 * Bestimmt die x und y-Koordinate, wenn die Transition nicht gebogen ist oder zu sich selbst führt.
	 * @param parent Die Transition von der die x-Koordinate abhängt
	 * @param outputWidth Weite des OutputObjects
	 * @param inputWidth Weite des InputObjects
	 * @param height Höhe des TransitionDescriptionObjects
	 */
	private void setCoordinatesWhenParentIsLine(TransitionObject parent, 
			int inputWidth, int outputWidth, int height) {
		int mXState = parent.getParentRenderable().getXCoordinate();
	    int mYState = parent.getParentRenderable().getYCoordinate();
	    
	    if(mXState > parent.getXCoordinate()) {
	    	if(mYState > parent.getYCoordinate()) {
	    		this.xStart = (int) ((parent.getXCoordinate() + parent.getEndPoint().getX())/2 
	    				+ TRANSITION_DISTANCE);
	    		this.yStart = (int) ((parent.getYCoordinate() + parent.getEndPoint().getY())/2 
	    				- height - TRANSITION_DISTANCE-this.offset);
	    	} else {
	    		this.xStart = (int) ((parent.getXCoordinate() + parent.getEndPoint().getX())/2 
	    				- inputWidth - outputWidth - textWidth - 4*PADDING - TRANSITION_DISTANCE);
	    		this.yStart = (int) ((parent.getYCoordinate() + parent.getEndPoint().getY())/2 
	    				- height - TRANSITION_DISTANCE - this.offset);
	    	}
	    } else {
	    	if(mYState > parent.getYCoordinate()) {
	    		this.xStart = (int) ((parent.getXCoordinate() + parent.getEndPoint().getX())/2 
	    				+TRANSITION_DISTANCE);
	    		this.yStart = (int) ((parent.getYCoordinate() + parent.getEndPoint().getY())/2 
	    				+ TRANSITION_DISTANCE + this.offset);
	    	} else {
	    		this.xStart = (int) ((parent.getXCoordinate() + parent.getEndPoint().getX())/2 
	    				- inputWidth - outputWidth - textWidth - 4*PADDING - TRANSITION_DISTANCE);
	    		this.yStart = (int) ((parent.getYCoordinate() + parent.getEndPoint().getY())/2 
	    				+ TRANSITION_DISTANCE + this.offset);
	    	}
	    }
	    
	}

	/**
	 * Bestimmt die x und y-Koordinate, wenn die Transition gebogen ist oder nicht zu sich selbst führt.
	 * @param curve Kurve, die die Transition repräsentiert.
	 * @param point Punkt auf der Kure, der dem Bend-Punkt am nächsten ist.
	 * @param outputWidth Weite des OutputObjects
	 * @param inputWidth Weite des InputObjects
	 * @param height Höhe des TransitionDescriptionObjects
	 */
	private void setCoordinatesWhenParentIsCurve(QuadCurve2D curve,
			Point point, int inputWidth, int outputWidth, int height) {
		if(curve.getX1()<=point.getX() && curve.getX2()<=point.getX()) {
			this.xStart = (int) (point.getX() + TRANSITION_DISTANCE);
			this.yStart = (int) (point.getY() + TRANSITION_DISTANCE + this.offset);
		} else if (curve.getX1()>point.getX() && curve.getX2()>point.getX()) {
			this.xStart = (int) (point.getX()- TRANSITION_DISTANCE- inputWidth 
					- outputWidth - textWidth - 4*PADDING);
			this.yStart = (int) (point.getY() + TRANSITION_DISTANCE + this.offset);
		} else if (curve.getY1()<=point.getY() && curve.getY2()<=point.getY()) {
			this.xStart = (int) (point.getX());
			this.yStart = (int) (point.getY()+ TRANSITION_DISTANCE + this.offset + height);
		} else if (curve.getY1()>point.getY() && curve.getY2()>point.getY()) {
			this.xStart = (int) (point.getX());
			this.yStart = (int) (point.getY()- TRANSITION_DISTANCE - height - this.offset);
		} else if (curve.getX1()>=point.getX() && curve.getX2()<=point.getX()) {
			if (curve.getY1()>point.getY() && curve.getY2()<=point.getY()) {
				this.xStart = (int) (point.getX()- TRANSITION_DISTANCE- inputWidth 
						- outputWidth - textWidth - 4*PADDING);
				this.yStart = (int) (point.getY()+ TRANSITION_DISTANCE + this.offset);
			} else {
				this.xStart = (int) (point.getX() + TRANSITION_DISTANCE);
				this.yStart = (int) (point.getY()+ TRANSITION_DISTANCE + this.offset);
			}
		} else if (curve.getX1()<=point.getX() && curve.getX2()>=point.getX()) {
			if (curve.getY1()>point.getY() && curve.getY2()<=point.getY()) {
				this.xStart = (int) (point.getX() - TRANSITION_DISTANCE- inputWidth 
						- outputWidth - textWidth - 4*PADDING);
				this.yStart = (int) (point.getY()- TRANSITION_DISTANCE - height - this.offset);
			} else {
				this.xStart = (int) (point.getX() - TRANSITION_DISTANCE- inputWidth 
						- outputWidth - textWidth - 4*PADDING);
				this.yStart = (int) (point.getY()+ TRANSITION_DISTANCE + this.offset);
			}
		}
	}

	@Override
	public FsmObject clone() {
		TransitionDescriptionObject clonedTransitionObject 
			= new TransitionDescriptionObject((TransitionObject)this.parent);
		clonedTransitionObject.setInput((InputObject)this.childs.get(0));
		clonedTransitionObject.setOutput((OutputObject)this.childs.get(1));
		return clonedTransitionObject;
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
		this.childs.get(0).perform(program);
		this.childs.get(1).perform(program);
		return null;
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 4);
		writer.writeStartElement("description");
		writer.writeCharacters(NEWLINE);
		
		setLinefeed(writer, 5);
		writer.writeStartElement("input");
		childs.get(0).toXML(writer);
		setLinefeed(writer, 5);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
		
		setLinefeed(writer, 5);
		writer.writeStartElement("output");
		childs.get(1).toXML(writer);
		setLinefeed(writer, 5);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
		
		setLinefeed(writer, 4);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
	}

	/**
	 * Methode, um aus der Xml-Repräsentation des endlichen Automaten Objecte zu generieren.
	 * @param descriptionElement Mit diesem Element können die entsprechenden Xml-Elemente ausgelesen werden,
	 * um das TransitionDescriptionObject zu erstellen.
	 */
	public void loadProgramm(Element descriptionElement) {
		//Input parsen
		Element inputElement = (Element)descriptionElement.getElementsByTagName("input").item(0);
		InputObject inputObject = new InputObject();
		inputObject.loadProgramm(inputElement);
		setInput(inputObject);
		
		//Output parsen
		Element outputElement = (Element)descriptionElement.getElementsByTagName("output").item(0);
		OutputObject outputObject = new OutputObject();
		outputObject.loadProgramm(outputElement);
		setOutput(outputObject);
	}
}
