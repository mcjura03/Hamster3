package de.hamster.fsm.model.transition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Oberklasse, von der alle Void-Funktionen abgeleitet werden.
 * @author Raffaela Ferrari
 *
 */
public abstract class VoidObject extends FsmObject{
	protected static final Color TEXT_COLOR = new Color(255,255,255);
	protected static final Color COLOR_1 = new Color(0,0,0);
	protected static final Color COLOR_2 = new Color(74, 108, 212);
	protected static final int HEIGHT = 6;
	protected static final int WIDTH = 2;

	private RoundRectangle2D roundRec;
	private RoundRectangle2D integrateRec;
	private boolean canIntegrate;
	
	/**
	 * Konstruktor
	 * @param name Name der Void-Funktion.
	 */
	public VoidObject(String name) {
		this.name = name;
		setTextCoordinates(this.name);
	}

	@Override
	public int getWidth() {
		return WIDTH+textWidth;
	}

	@Override
	public int getHeight() {
		return HEIGHT+textHeight;
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem deleteVoid = new JMenuItem(this.name + " l�schen");
		deleteVoid.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteVoid);

		return menuItemList;
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return roundRec.contains(x, y);
	}

	/**
	 * Überprüft ob der Punkt im Integrierungsbereich liegt.
	 * @param x X-Koordinate des zu überprüfenden Punktes
	 * @param y Y-Koordinate des zu überprüfenden Punktes
	 * @return true, wenn der Punkt im Integrierungsbereich liegt.
	 */
	public boolean isClickedOnIntegrate(int x, int y) {
		if(integrateRec != null) {
			return integrateRec.contains(x,y);
		} else {
			return false;
		}
	}

	/**
	 * Soll die dazugehörige Variable setzen, damit klar ist ob der Integrierungsbereich
	 * mitgezeichnet werden soll.
	 * @param canIntegrate true, wenn der Integrierungsbereich mitgezeichnet werden soll.
	 */
	public void setCanIntegrate(boolean canIntegrate) {
		this.canIntegrate = canIntegrate;
	}

	@Override
	public void render(Graphics g) {
		int h = HEIGHT+textHeight;
		int w = WIDTH+textWidth;
		Graphics2D g2d = (Graphics2D)g;
		
		Stroke stroke = new BasicStroke((float) (1*ZOOMFACTOR));
		g2d.setStroke(stroke);
		
		roundRec = new RoundRectangle2D.Double(this.xStart,this.yStart, w, h, 5, 5);
		integrateRec = new RoundRectangle2D.Double(this.xStart,this.yStart + h, 47, 20, 5, 5);
		g2d.setPaint(COLOR_2);
		g2d.fill(roundRec);
		if(highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(COLOR_1);	
		}
		g2d.draw(roundRec);
		if(canIntegrate) {
			g2d.setColor(INTEGRATE_COLOR);
			g2d.fill(integrateRec);
			g2d.setColor(Color.gray);
			g2d.draw(integrateRec);
			
		}
		
		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(this.name, this.xStart+((int)WIDTH/2),this.yStart
				+((int) (HEIGHT+1+textHeight/2)));
	}

	@Override
	abstract public FsmObject clone();

	@Override
	abstract public Object performImplementation(FsmProgram program) throws IsNondeterministicException;

	/**
	 * Methode für die Transformation des Endlichen Automaten in ein imperatives Java-Program.
	 * @param buffer Buffer, indem der SourceCode geschrieben wird.
	 * @param indentation Einrückfaktor
	 */
	public void writeSourceCode(StringBuffer buffer, int indentation) {
		startLine(buffer, indentation);
		buffer.append((!this.name.equals("epsilonFunction")? this.name + "();" : "") + NEWLINE);	
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		setLinefeed(writer, 6);
		writer.writeStartElement("voidObject");
		writer.writeAttribute("name", this.name);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
	}
}
