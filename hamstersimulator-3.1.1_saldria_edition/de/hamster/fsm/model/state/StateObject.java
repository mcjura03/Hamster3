package de.hamster.fsm.model.state;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.controller.handler.ModifyStateInContextMenu;
import de.hamster.fsm.controller.handler.RenameObjectInContextMenu;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.TransitionDescriptionObject;
import de.hamster.fsm.model.transition.TransitionObject;
import de.hamster.fsm.view.ContextMenuPanel;
import de.hamster.fsm.view.FsmPanel;

/**
 * Klasse, die einen Zustand eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class StateObject extends FsmObject {
	private static final Color COLOR = new Color(0,0,0);
	private static final Color INNER_COLOR = new Color(255,255,255);
	private static final int ARROWHEAD_HEIGHT = 4;
	private static final int ARROWHEAD_WIDTH = 4;

	private TransitionObject temp;
	private boolean isFinal;
	private boolean isInitial;
	
	private Ellipse2D fillOval;

	/**
	 * Kostruktor
	 * @param isInitial gibt an, ob der Zustand initial sein soll.
	 * @param isFinal gibt an, ob der Zustand final sein soll.
	 */
	public StateObject(boolean isInitial, boolean isFinal) {
		super();
		this.isFinal = isFinal;
		this.isInitial = isInitial;
	}

	/**
	 * Setzt diesen Zustand final oder nicht.
	 * @param isInitial true, wenn er final gesetzt werden soll.
	 */
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	/**
	 * Gibt an, ob der Zustand final ist.
	 * @return true, wenn der Zustand final ist.
	 */
	public boolean isFinal() {
		return this.isFinal;
	}

	/**
	 * Setzt diesen Zustand initial oder nicht.
	 * @param isInitial true, wenn er initial gesetzt werden soll.
	 */
	public void setInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	/**
	 * Gibt an, ob der Zustand initial ist.
	 * @return true, wenn der Zustand initial ist.
	 */
	public boolean isInitial() {
		return this.isInitial;
	}

	/**
	 * Fügt eine Transition zum StateObjekt hinzu.
	 * @param transition Transition, die hinzugefügt werden soll.
	 */
	public void addTransition(TransitionObject transition) {
		this.childs.add(transition);
	}

	/**
	 * Gibt die Anzahl aller Transitionen zurück. Dabei werden mehrere Beschriftungen für eine
	 * Transition nicht als eine Transition gezählt, sondern jede Beschriftung wird aufaddiert.
	 * @return Anzahl aller Transitionen.
	 */
	public int getNumberOfTransistions() {
		int number = 0;
		FsmObject child;
		Iterator<FsmObject> transitionsIterator = this.childs.iterator();
		while (transitionsIterator.hasNext()) {
			child = transitionsIterator.next();
			number = number + child.getChilds().size();
		}
		return number;
	}

	/**
	 * Gibt an, ob dieser Zustand eine Transition besitzt, die als toState den übergebenen Parameter
	 * enthält.
	 * @param isDeletedState Zustand, der als toState für die Transition eingetragen sein müsste.
	 * @return die entsprechende Transition oder null, wenn es keine passende Transition gibt.
	 */
	public TransitionObject getTransitionWithToState(StateObject isDeletedState) {
		FsmObject child;
		Iterator<FsmObject> transitionsIterator = this.childs.iterator();
		while (transitionsIterator.hasNext()) {
			child = transitionsIterator.next();
			if(((TransitionObject) child).getToState().equals(isDeletedState)) {
				return (TransitionObject) child;
			}
		}
		return null;
	}


	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();
		
		JMenuItem renameState = new JMenuItem("Zustand umbenennen");
		renameState.addActionListener(new RenameObjectInContextMenu(this, 
				((FsmPanel)panel).getAutomataPanel(), this.getXCoordinate(), this.getYCoordinate()));
		menuItemList.add(renameState);

		JMenuItem deleteState = new JMenuItem("Zustand l�schen");
		deleteState.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteState);

		JMenuItem asStartState = new JMenuItem("Als Startzustand definieren");
		asStartState.setActionCommand("initial");
		asStartState.addActionListener(new ModifyStateInContextMenu(this, (FsmPanel)panel));
		menuItemList.add(asStartState);

		JMenuItem setFinalState;
		if(!this.isFinal) {
			setFinalState = new JMenuItem("Als Endzustand definieren");
		} else {
			setFinalState = new JMenuItem("Als Nicht-Endzustand definieren");
		}
		setFinalState.setActionCommand("final");
		setFinalState.addActionListener(new ModifyStateInContextMenu(this, (FsmPanel)panel));
		menuItemList.add(setFinalState);

		return menuItemList;
	}

	@Override
	public int getWidth() {
		return 20;
	}

	@Override
	public int getHeight() {
		return 20+this.textWidth;
	}

	@Override
	public void setCoordinates(int x, int y) {
		if (this.isFinal && (x-(getWidth()+this.textWidth)/2-4*ZOOMFACTOR-2) >= 0 &&
				(y-(getWidth()+this.textWidth)/2-4*ZOOMFACTOR-2) >=0) {
			this.xStart = x;
			this.yStart = y;
		} else if (!this.isFinal &&(x-(getWidth()+this.textWidth)/2-2) >= 0 && (y-(getWidth()+this.textWidth)/2-2) >=0) {
			this.xStart = x;
			this.yStart = y;
		}
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return this.fillOval.contains(x, y);
	}

	/**
	 * Überprüft für das Hover, ob eine ihrer TransitionDescription einen entsprechenden
	 * Input oder Output hat.
	 * @param x X-Koordinate des Klicks
	 * @param y Y-Koordinate des Klicks
	 * @return
	 */
	public FsmObject isClickedOnHover(int x, int y) {
		for(FsmObject transition : this.childs) {
			for(FsmObject transDesc : transition.getChilds()) {
				TransitionDescriptionObject transitionDesc = (TransitionDescriptionObject) transDesc;
				if(transitionDesc.getInput().isClickedOnHover(x, y)) {
					return transitionDesc.getInput();
				}
				if(transitionDesc.getOutput().isClickedOnHover(x, y)) {
					return transitionDesc.getOutput();
				}
			}
		}
		return null;
	}

	@Override
	public void render(Graphics g) {
		int w = getWidth()+this.textWidth;
		int x = this.xStart;
		int y = this.yStart;
		Graphics2D g2d = (Graphics2D)g;

		Stroke stroke = new BasicStroke((float) (LINE_THICKNESS*ZOOMFACTOR));
		g2d.setStroke(stroke);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(INNER_COLOR);
		Line2D line;
		if(this.isFinal == true) {
			fillOval = new Ellipse2D.Double(x-w/2-4*ZOOMFACTOR, y-w/2-4*ZOOMFACTOR, w+8*ZOOMFACTOR, w+8*ZOOMFACTOR);
			g2d.fill(fillOval);
			if(highlight) {
				g2d.setColor(HIGHLIGHTCOLOR);
			} else {
				g2d.setColor(COLOR);	
			}
			g2d.draw(fillOval);
			if(this.isInitial == true) {
				line = new Line2D.Double(x-w/2-16*ZOOMFACTOR, y, x-w/2-4*ZOOMFACTOR, y);
				g2d.draw(line);
				Path2D p2d = new Path2D.Double();
		        p2d.moveTo(x-w/2-4*ZOOMFACTOR,  y);
		        p2d.lineTo(x-w/2-4*ZOOMFACTOR-ARROWHEAD_WIDTH, y + ARROWHEAD_HEIGHT);
		        p2d.lineTo(x-w/2-4*ZOOMFACTOR - ARROWHEAD_WIDTH, y - ARROWHEAD_HEIGHT);
		        p2d.closePath();
		        g2d.fill(p2d);
		        g2d.draw(p2d);
			}
		} else {
			fillOval = new Ellipse2D.Double(x-w/2, y-w/2, w, w);
			g2d.fill(fillOval);
			if(highlight) {
				g2d.setColor(HIGHLIGHTCOLOR);
			} else {
				g2d.setColor(COLOR);	
			}
			if(this.isInitial == true) {
				line = new Line2D.Double(x-w/2-12*ZOOMFACTOR, y, x-w/2, y);
				g2d.draw(line);
				Path2D p2d = new Path2D.Double();
		        p2d.moveTo(x-w/2,  y);
		        p2d.lineTo(x-w/2 -ARROWHEAD_WIDTH, y + ARROWHEAD_HEIGHT);
		        p2d.lineTo(x-w/2 - ARROWHEAD_WIDTH, y - ARROWHEAD_HEIGHT);
		        p2d.closePath();
		        g2d.fill(p2d);
		        g2d.draw(p2d);
			}
		}
		Ellipse2D innerCircle = new Ellipse2D.Double(x-w/2, y-w/2, w, w);
		g2d.draw(innerCircle);
		
		g2d.setFont(TEXT_FONT);
		g2d.drawString(this.name, x-w/2+((int)getWidth()/2),y);
		super.render(g);
	}

	@Override
	public FsmObject clone() {
		StateObject clonedStateObject = new StateObject(this.isInitial, this.isFinal);
		clonedStateObject.setChilds(this.childs);
		clonedStateObject.setCoordinates(this.xStart, this.yStart);
		return clonedStateObject;
	}

	@Override
	public Object performImplementation(FsmProgram program) throws IsNondeterministicException{
		int trueMatches = 0;
		ArrayList<TransitionObject> matchedObject = new ArrayList<TransitionObject>();
		FsmObject child;
		Iterator<FsmObject> transitionsIterator = this.childs.iterator();
		while (transitionsIterator.hasNext()) {
			child = transitionsIterator.next();
			TransitionObject transition = (TransitionObject) child;
			if ((Boolean) transition.canBePerformed(program) == true) {
				trueMatches ++;
				matchedObject.add(transition);
			}
		}
		switch (trueMatches) {
		case 0:
			this.temp = null;
			return this.temp;
		case 1:
			this.temp = matchedObject.get(matchedObject.size()-1);
			this.temp.perform(program);
			return this.temp.getToState();
		default:
			if(program.isNondeterministic()) {
				int index = (int) (Math.random() * matchedObject.size());
				this.temp = matchedObject.get(index);
				this.temp.perform(program);
				return this.temp.getToState();
			} else {
				throw new IsNondeterministicException(this.name);
			}
		}
	}

	/**
	 * Methode für die Transformation des Endlichen Automaten in ein imperatives Java-Program.
	 * @param buffer Buffer, indem der SourceCode geschrieben wird.
	 * @param indentation Einrückfaktor
	 * @param states Zustände-Liste, die an die Unterobjekte weitergegeben werden.
	 */
	public void writeSourceCode(StringBuffer buffer, int indentation, 
			CopyOnWriteArrayList<StateObject> states) {
		int numbering = 0;
		//Übergänge anhand der Inputs herausfinden, die benutzt werden können
		FsmObject child;
		Iterator<FsmObject> transitionsIterator = this.childs.iterator();
		while (transitionsIterator.hasNext()) {
			child = transitionsIterator.next();
			TransitionObject childTO = (TransitionObject) child;
			numbering = childTO.writeSourceCode(buffer, indentation, numbering, true, states);
		}

 		//suche den passenden Übergang
		startLine(buffer, indentation);
		buffer.append("waehleTransitionAus();" + NEWLINE);
		
		//Setze auf 0
		numbering = 0;
		
		//Führe den Output aus und gebe den nächsten Zustand zurück
		startLine(buffer, indentation);
		buffer.append("switch (aktuellerUebergang) {" + NEWLINE);
		
		// Übergänge hinschreiben
		transitionsIterator = this.childs.iterator();
		while (transitionsIterator.hasNext()) {
			child = transitionsIterator.next();
			TransitionObject childTO = (TransitionObject) child;
			numbering = childTO.writeSourceCode(buffer, indentation, numbering, false, states);
		}
		startLine(buffer, indentation);
		buffer.append("}" + NEWLINE);
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 1);
		writer.writeStartElement("state");
		writer.writeAttribute("name", name);
		writer.writeAttribute("initial", String.valueOf(this.isInitial));
		writer.writeAttribute("final", String.valueOf(this.isFinal));
		writer.writeAttribute("x", String.valueOf(getXCoordinate()));
		writer.writeAttribute("y", String.valueOf(getYCoordinate()));
		writer.writeCharacters(NEWLINE);
		FsmObject child;
		Iterator<FsmObject> transitionsIterator = this.childs.iterator();
		while (transitionsIterator.hasNext()) {
			child = transitionsIterator.next();
			child.toXML(writer);
		}
		setLinefeed(writer, 1);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
	}
	
	/**
	 * Gibt nicht das Objekt als String zurück, sondern nur den Namen des Objekts,
	 * der durch das Feld <Code>name</Code> festgelegt wird.
	 */
	@Override
	public String toString() {
		return name;
	}
}
