package de.hamster.fsm.model.transition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.model.state.StateObject;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Klasse, die eine Transition repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class TransitionObject extends FsmObject{
	private static final Color ARROW_COLOR = new Color(0,0,0);
	private static final int ARROWHEAD_WIDTH= 4;
	private static final int ARROWHEAD_HEIGHT= 8;
	private static final int OFFSET = 5;
	
	int endX = 0;
	int endY = 0;
	int bendX = -1;
	int bendY = -1;
	int closestX = 0;
	int closestY = 0;
	
	private boolean isMarked;
	private StateObject toState;
	private ArrayList<TransitionDescriptionObject> matched;
	private TransitionDescriptionObject temp;
	
	private Line2D line;
	private QuadCurve2D curve;
	private CubicCurve2D cubicCurve;
	private Path2D p2d;
	private Ellipse2D bendPoint;

	/**
	 * Konstruktor
	 * @param from Zustand, von wo die Transition ausgeht.
	 * @param to Zustand, zu dem die Transition führt.
	 */
	public TransitionObject(StateObject from, StateObject to) {
		super();
		setParent(from);
		this.toState = to;
	}

	/**
	 * Setzt den Zustand, von wo die Transition ausgeht.
	 * @param from Zustand
	 */
	public void setFromState(StateObject from) {
		setParent(from);
	}

	/**
	 * Setzt den Zustand, zu dem die Transition führt.
	 * @param from Zustand
	 */
	public void setToState(StateObject to) {
		this.toState = to;
	}

	/**
	 * Gibt den Zustand zurück, zu dem die Transition führt.
	 * @return
	 */
	public StateObject getToState() {
		return this.toState;
	}
	
	/**
	 * Fügt eine neue Beschriftung der Transition hinzu.
	 * @param description Beschriftung, die hinzugefügt werden soll.
	 */
	public void addTransitionDescriptionObject(TransitionDescriptionObject description) {
		this.childs.add(description);
	}

	/**
	 * Gibt zurück, ob diese Transition ausgeführt werden kann.
	 * @param program FsmProgram, das ausgeführt werden soll.
	 * @return true, wenn sich eine valide Beschriftung für diese Transition findet.
	 * @throws NoTransistionInputDefinedException wenn für eine Beschriftung kein Input definiert ist.
	 * @throws NoTransistionOutputDefinedException wenn für eine Beschriftung kein Output definiert ist.
	 */
	public Boolean canBePerformed(FsmProgram program) {
		matched = new ArrayList<TransitionDescriptionObject>();
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		while (iterator.hasNext()) {
			child = iterator.next();
			TransitionDescriptionObject description = (TransitionDescriptionObject) child;
			if ((Boolean)description.getInput().checkPerform(program)== true) {
				matched.add(description);
			}
		}
		if(matched.size()>0) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem deleteTransition = new JMenuItem("Transition l�schen");
		deleteTransition.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteTransition);

		return menuItemList;
	}

	@Override
	public int getWidth() {
		if(curve != null) {
			return (int)curve.getBounds().getWidth();
		} else if (line != null) {
			return (int)line.getBounds().getWidth();
		}
		return 0;
	}

	@Override
	public int getHeight() {
		if(curve != null) {
			return (int)curve.getFlatness();
		} else {
			return (int) ARROWHEAD_HEIGHT;
		}
	}

	/**
	 * Setzt den Endpunkt der Transition, je nach dem wo der to-Zustand aufhört, falls eine existiert.
	 * @param x X-Koordinate des Endpunktes.
	 * @param y Y-Koordinate des Endpunktes.
	 */
	public void setEndPoint(double x, double y) {
		if(this.toState == null) {
			this.endX = (int)x;
			this.endY = (int)y;
		} else if(this.toState.isFinal()) {
    		this.endX = (int) (this.toState.getXCoordinate()+(this.toState.getHeight()/2)*x+(x>0 ? 4 : -4));
    		this.endY = (int) (this.toState.getYCoordinate()+(this.toState.getHeight()/2)*y+(y<0 ? -4 : 4));
    	} else {
    		this.endX = (int) (this.toState.getXCoordinate()+(this.toState.getHeight()/2)*x);
    		this.endY = (int) (this.toState.getYCoordinate()+(this.toState.getHeight()/2)*y);
    	}
	}

	/**
	 * Setzt den Punkt zum Biegen.
	 * @param x X-Koordinate des Biegpunktes.
	 * @param y Y-Koordinate des Biegpunktes.
	 */
	public void setBendPoint(int x, int y) {
		this.bendX = x;
		this.bendY = y;
	}

	/**
	 * Setzt den Startpunkt der Transition, je nach dem wo der from-Zustand aufhört und ob er final ist.
	 * @param x X-Koordinate des Startpunktes.
	 * @param y Y-Koordinate des Startpunktes.
	 */
	public void setStartPoint(double x, double y){
		StateObject state = (StateObject)this.parent;
    	if(state.isFinal()) {
    		this.xStart = (int) (state.getXCoordinate()+(state.getHeight()/2)*x+(x>0 ? 4 : -4));
    		this.yStart = (int) (state.getYCoordinate()+(state.getHeight()/2)*y+(y<0 ? -4 : 4));
    	} else {
    		this.xStart = (int) (state.getXCoordinate()+(state.getHeight()/2)*x);
    		this.yStart = (int) (state.getYCoordinate()+(state.getHeight()/2)*y);
    	}
	}

	/**
	 * Gibt den Biegpunkt zurück.
	 * @return
	 */
	public Point getBendPoint() {
		Point p = new Point(this.bendX, this.bendY);
		return p;
	}

	/**
	 * Gibt den Endpunkt zurück.
	 * @return
	 */
	public Point getEndPoint() {
		Point p = new Point(endX, endY);
		return p;
	}

	/**
	 * Gibt an, ob es sich bei dieser Transition um eine Linie handelt.
	 * @return true, wenn es eine Linie ist.
	 */
	public boolean isLine() {
		return this.line != null;
	}

	/**
	 * Gibt die Kurve zurück, falls vorhanden, sonst null.
	 * @return
	 */
	public QuadCurve2D getCurve() {
		return this.curve;
	}

	/**
	 * Gibt die kubische Kurve zurück, falls vorhanden, sonst null.
	 * @return
	 */
	public CubicCurve2D getCubicCurve() {
		return this.cubicCurve;
	}

	/**
	 * gibt den Punkt auf der Kurve zurück, der dem Bend-Punkt am nächsten ist.
	 * @return
	 */
	public Point getClosestPointFromBendPoint() {
		if(this.curve != null) {
			return new Point(closestX,closestY);
		} else {
			return null;
		}
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		boolean isClickedOn = false;
		if(this.curve != null) {
			isClickedOn = this.curve.contains(x, y);
		} else if (this.line != null) {
			isClickedOn = (this.line.ptLineDist(x, y) < 2);
		}  else if (this.cubicCurve != null) {
			isClickedOn = this.cubicCurve.contains(x, y);
		}
		if (isClickedOn == false) {
			isClickedOn = Path2D.contains(p2d.getPathIterator(null, 1.5), x, y);
		}
		return isClickedOn;
	}

	/**
	 * Gibt an, ob die Pfeilspitze getroffen wurde.
	 * @param x X-Koordinate des zu überprüfenden Punktes.
	 * @param y Y-Koordinate des zu überprüfenden Punktes.
	 * @return
	 */
	public boolean isArrowHeadClicked(int x, int y) {
		return p2d.contains(x, y);
	}

	/**
	 * Markiert den BendPunkt, damit dieser beliebig gezogen werden kann.
	 * Gibt es alternativ keinen BendPunkt, so wird der Mittelpunkt der Linie genommen.
	 * @param mark Gibt an, ob die Transition markiert werden soll.
	 * @return gibt true zurück, wenn diese markiert wurde.
	 */
	public boolean markTransition(boolean mark) {
		this.isMarked = mark;
		if(!isMarked || this.cubicCurve != null) {
			return false;
		}
		return true;
	}

	/**
	 * Gibt an, ob der BendPunkt getroffen wurde.
	 * @param x X-Koordinate, die überprüft werden soll.
	 * @param y Y-Koordinate, die überprüft werden soll.
	 * @return true, wenn der BendPunkt getroffen wurde.
	 */
	public boolean isClickedOnBendPoint(int x, int y) {
		if(bendPoint != null) {
			return bendPoint.contains(x, y);
		}
		return false;
	}

	@Override
	public void render(Graphics g) {
		StateObject fromState = (StateObject) this.parent;
		Graphics2D g2d = (Graphics2D)g;
		
		Stroke stroke = new BasicStroke((float) (LINE_THICKNESS*ZOOMFACTOR));
		g2d.setStroke(stroke);

		if(highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(ARROW_COLOR);	
		}

		//Setze Antialiasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		PathIterator p;
		line = null;
		cubicCurve=null;
		curve = null;
		if(this.toState == null) {
			//Die gerade Linie
			line = new Line2D.Double(fromState.getXCoordinate(), 
					fromState.getYCoordinate(), this.endX, 
					this.endY);
			p = line.getPathIterator(null, 1.0);
		} else if(this.parent.equals(toState)) {
			cubicCurve = new CubicCurve2D.Double(fromState.getXCoordinate(), 
					fromState.getYCoordinate(), fromState.getXCoordinate()-100, 
					fromState.getYCoordinate()+70, fromState.getXCoordinate()+100, 
					fromState.getYCoordinate()+70, fromState.getXCoordinate(), 
					fromState.getYCoordinate());
			p = cubicCurve.getPathIterator(null, 1.0);
		} else if(this.bendX == -1 && this.bendY == -1) {
			//Die gerade Linie
			line = new Line2D.Double(fromState.getXCoordinate(), 
					fromState.getYCoordinate(), this.toState.getXCoordinate(), 
					this.toState.getYCoordinate());
			p = line.getPathIterator(null, 1.0);
		} else {
			//Die gebogene Linie
			curve = new QuadCurve2D.Double(fromState.getXCoordinate(), 
					fromState.getYCoordinate(), this.bendX, this.bendY, 
					this.toState.getXCoordinate(), this.toState.getYCoordinate());
			 p = curve.getPathIterator(null, 1.0);
		}

		//Berechnung die Punkte für den Pfeilvektor gegen Ende der Kurve
        double nextToLastXCoordinate = 0;
        double nextToLastYCoordinate = 0;
        double lastXCoordinate = fromState.getXCoordinate();
        double lastYCoordinate = fromState.getYCoordinate();
        double coordinates[] = new double[6];
        while (!p.isDone()) {
            p.currentSegment(coordinates);
            nextToLastXCoordinate = lastXCoordinate;
            nextToLastYCoordinate = lastYCoordinate;
            lastXCoordinate = coordinates[0];
            lastYCoordinate = coordinates[1];
            p.next();
            
            if(nextToLastXCoordinate == fromState.getXCoordinate() && 
            		nextToLastYCoordinate == fromState.getYCoordinate()) {
            	//Vektor bestimmen in Richtung (lastXCoordinate,lastYCoordinate)
                double vectorStartX = - nextToLastXCoordinate + lastXCoordinate;
                double vectorStartY = - nextToLastYCoordinate + lastYCoordinate;
                
                //Einheitsvektor aus u bestimmen, dafür benötigt man zunächst die Länge des Vektors
                // dann teilt man den X und Y durch diese Länge
                double length = Math.sqrt(vectorStartX*vectorStartX + vectorStartY*vectorStartY);
                vectorStartX /= length;
                vectorStartY /= length;

                setStartPoint(vectorStartX, vectorStartY);
            }
        }
        
        //Vektor bestimmen in Richtung (nextToLastXCoordinate,nextToLastYCoordinate)
        //weil das letzte Stück der Linie mit der Spitze überzeichnet werden soll
        double vectorX = nextToLastXCoordinate - lastXCoordinate;
        double vectorY = nextToLastYCoordinate - lastYCoordinate;
        
        //Einheitsvektor aus u bestimmen, dafür benötigt man zunächst die Länge des Vektors
        // dann teilt man den X und Y durch diese Länge
        double length = Math.sqrt(vectorX*vectorX + vectorY*vectorY);
        vectorX /= length;
        vectorY /= length;
 
        // orthogonalen Vektor bestimmen
        double orthogonalVectorX = -vectorY;
        double orthogonalVectorpY = vectorX;

        if(this.toState != null) {
	        //Endpunkt definieren
	        setEndPoint(vectorX, vectorY);
	
	        //Linie überarbeiten
	        if(this.parent.equals(toState)) {
	        	cubicCurve = new CubicCurve2D.Double(this.xStart, this.yStart, this.xStart-80, 
	        			this.yStart+70, this.xStart+100, this.yStart+70, this.endX, this.endY);
	        } else if(this.bendX == -1 && this.bendY == -1) {
				//Die gerade Linie
				line = new Line2D.Double(this.xStart, this.yStart, this.endX, this.endY);
			} else {
				curve = new QuadCurve2D.Double(this.xStart, this.yStart, this.bendX, this.bendY,
						this.endX, this.endY);
			}
        }

        // Zeichnet nun die Pfeilspitze mithilfe der beiden Vektoren
        p2d = new Path2D.Double();
        p2d.moveTo(this.endX, this.endY);
        p2d.lineTo(this.endX + ARROWHEAD_HEIGHT*vectorX + ARROWHEAD_WIDTH*orthogonalVectorX,
        		this.endY + ARROWHEAD_HEIGHT*vectorY + ARROWHEAD_WIDTH*orthogonalVectorpY);
        p2d.lineTo(this.endX + ARROWHEAD_HEIGHT*vectorX*0.8, 
        		this.endY + ARROWHEAD_HEIGHT*vectorY*0.8);
        p2d.lineTo(this.endX + ARROWHEAD_HEIGHT*vectorX - ARROWHEAD_WIDTH*orthogonalVectorX, 
        		this.endY + ARROWHEAD_HEIGHT*vectorY - ARROWHEAD_WIDTH*orthogonalVectorpY);
        p2d.closePath();

        bendPoint = null;
        if(this.parent.equals(toState)) {
			g2d.draw(cubicCurve);
        } else if(this.toState == null || (this.bendX == -1 && this.bendY == -1) ) {
        	g2d.draw(line);
        	bendPoint = new Ellipse2D.Double((xStart + endX)/2-2, (yStart + endY)/2 -2, 5, 5);
		} else {
			g2d.draw(curve);
			bendPoint = new Ellipse2D.Double(this.bendX-2, this.bendY -2, 5, 5);
			setClosestPointOnCurveToBendPoint();
		}
        g2d.fill(p2d);
        g2d.draw(p2d);
 
        if(isMarked && bendPoint != null) { // dibo wegen Nullpointer-Exception
        	g2d.draw(bendPoint);
        }
		int offset = 0;
		TransitionDescriptionObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		while (iterator.hasNext()) {
			child = (TransitionDescriptionObject) iterator.next();
			child.setOffset(offset);
			child.render(g);
			offset += child.getHeight() + OFFSET;
		}
	}

	/**
	 * Berechnet den Punkt auf der Kurve, der dem Bend-Punkt am nächsten ist
	 */
	private void setClosestPointOnCurveToBendPoint() {
		//Berechnung die Punkte für den Pfeilvektor gegen Ende der Kurve
        double closestYPoint = this.xStart;
        double closestXPoint = this.yStart;
        double lastXCoordinate = 0;
        double lastYCoordinate = 0;
        double distance = Math.sqrt ((this.bendX - this.xStart) * (this.bendX - this.xStart)
        		+ (this.bendY - this.yStart) * (this.bendY - this.yStart));
        double coordinates[] = new double[6];
        PathIterator p = curve.getPathIterator(null, 1.0);
        while (!p.isDone()) {
            p.currentSegment(coordinates);
            lastXCoordinate = coordinates[0];
            lastYCoordinate = coordinates[1];
            p.next();
            
            double distTemp = Math.sqrt ((this.bendX - lastXCoordinate) * (this.bendX - lastXCoordinate)
            		+ (this.bendY - lastYCoordinate) * (this.bendY - lastYCoordinate));
            if(distance>distTemp) {
            	distance = distTemp;
            	closestXPoint = lastXCoordinate;
            	closestYPoint = lastYCoordinate;
            }
        }
        closestX = (int) closestXPoint;
        closestY = (int) closestYPoint;
	}

	@Override
	public FsmObject clone() {
		TransitionObject clonedTransitionObject 
			= new TransitionObject((StateObject) parent, this.toState);
		clonedTransitionObject.setChilds(this.childs);
		return clonedTransitionObject;
	}
	
	@Override
	public Object performImplementation(FsmProgram program) throws IsNondeterministicException {
		switch (matched.size()) {
			case 0:
				this.temp = null;
				return this.temp;
			case 1:
				this.temp = matched.get(matched.size()-1);
				this.temp.perform(program);
				return this.toState;
			default:
				if(program.isNondeterministic()) {
					int index = (int) (Math.random() * matched.size());
					this.temp = matched.get(index);
					this.temp.perform(program);
					return this.toState;
				} else {
					throw new IsNondeterministicException(this.name);
				}
		}
	}

	/**
	 * Methode für die Transformation des Endlichen Automaten in ein imperatives Java-Program.
	 * @param buffer Buffer, indem der SourceCode geschrieben wird.
	 * @param indentation Einrückfaktor
	 * @param numbering Gibt den Zustandsübergang an.
	 * @param begin Gibt an, ob es das erste Mal ist, wenn diese Methode ausgerufen wird.
	 * @param states Gibt alle Zustände an.
	 */
	public int writeSourceCode(StringBuffer buffer, int indentation, int numbering, boolean begin,
			CopyOnWriteArrayList<StateObject> states) {
		if(begin) {
			FsmObject child;
			Iterator<FsmObject> iterator = this.childs.iterator();
			while (iterator.hasNext()) {
				child = iterator.next();
				TransitionDescriptionObject childTDO = (TransitionDescriptionObject) child;
				startLine(buffer, indentation);
				buffer.append("if (");
				childTDO.getInput().writeSourceCode(buffer, 0);
				buffer.append(") {" + NEWLINE);
				startLine(buffer, indentation+1);
				buffer.append("zustandsUebergaenge[anzahlMoeglicherUebergaenge] = " + numbering
						+ ";" + NEWLINE);
				startLine(buffer, indentation+1);
				buffer.append("anzahlMoeglicherUebergaenge++;" + NEWLINE);
				startLine(buffer, indentation);
				buffer.append("}" + NEWLINE);
				numbering++;
			}
			return numbering;
		} else {
			FsmObject child;
			Iterator<FsmObject> iterator = this.childs.iterator();
			while (iterator.hasNext()) {
				child = iterator.next();
				TransitionDescriptionObject childTDO = (TransitionDescriptionObject) child;
				startLine(buffer, indentation+1);
				buffer.append("case " + numbering + ":" + NEWLINE);
				childTDO.getOutput().writeSourceCode(buffer, indentation + 1);
				startLine(buffer, indentation + 2);
				buffer.append("aktuellerZustand = " + (states.indexOf(toState)+1) + ";"+ NEWLINE);
				startLine(buffer, indentation + 2);
				buffer.append("break;" + NEWLINE);
				numbering++;
			}
			return numbering;
		}
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 2);
		writer.writeStartElement("transition");
		writer.writeAttribute("fromState", this.parent.getName());
		writer.writeAttribute("toState", this.toState.getName());
		if(this.bendX != -1 || this.bendY != -1) {
			writer.writeAttribute("x", String.valueOf(this.bendX));
			writer.writeAttribute("y", String.valueOf(this.bendY));
		}
	
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 3);
		writer.writeStartElement("descriptions");
		FsmObject child;
		Iterator<FsmObject> iterator = this.childs.iterator();
		while (iterator.hasNext()) {
			child = iterator.next();
			child.toXML(writer);
		}
		setLinefeed(writer, 3);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 2);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
	}

	/**
	 * Methode, um aus der Xml-Repräsentation des endlichen Automaten Objecte zu generieren.
	 * @param descriptionsElement Mit diesem Element können die entsprechenden Xml-Elemente ausgelesen werden,
	 * um das TransitionObject zu erstellen.
	 */
	public void loadProgramm(Element descriptionsElement) {
		NodeList transistionDescriptionList = descriptionsElement.getElementsByTagName("description");
		for (int k=0; k<transistionDescriptionList.getLength(); k++) {
			Element descriptionElement = (Element)transistionDescriptionList.item(k);
			TransitionDescriptionObject transistionDescriptionObject
				= new TransitionDescriptionObject(this);
			transistionDescriptionObject.loadProgramm(descriptionElement);
			addTransitionDescriptionObject(transistionDescriptionObject);
		}
	}
}
