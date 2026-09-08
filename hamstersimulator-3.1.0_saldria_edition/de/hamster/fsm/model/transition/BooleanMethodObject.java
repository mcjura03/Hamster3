package de.hamster.fsm.model.transition;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

/**
 * Oberklasse, von der alle Booleschen Operanden abgeleitet werden.
 * @author Raffaela Ferrari
 *
 */
public class BooleanMethodObject extends BooleanObject{
	protected static final int HEIGHT = 6;
	protected static final int WIDTH = 2;
	private RoundRectangle2D roundRec;

	/**
	 * Konstruktor
	 * @param name Name des Boolschen Operatoren/Operanden.
	 * @param positioning Gibt an, ob das Objekt rechts oder links in einem 
	 * anderen BooleanObjekt positioniert werden soll.
	 */
	public BooleanMethodObject(String name, int positioning) {
		super(name, positioning);
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
	public boolean isClickedOn(int x, int y) {
		return roundRec.contains(x, y);
	}

	/**
	 * Gibt immer null zurück, weil boolesche Operanden keine Rechtecke haben.
	 */
	@Override
	public BooleanObject isClickedChildRec(int x, int y) {
		return null;
	}

	/**
	 * Gibt immer false zurück, weil boolesche Operanden keine Rechtecke haben.
	 */
	@Override
	public boolean isClickedOnChildRec(int x, int y) {
		return false;
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		Stroke stroke = new BasicStroke((float) (1*ZOOMFACTOR));
		g2d.setStroke(stroke);
		
		roundRec = new RoundRectangle2D.Double(this.xStart, this.yStart, 
				getWidth(), getHeight(), 5, 5);
		g2d.setPaint(COLOR_2);
		g2d.fill(roundRec);
		if(highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(COLOR_1);	
		}
		g2d.draw(roundRec);
		
		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(this.name, this.xStart +((int)WIDTH/2),this.yStart
				+((int) (HEIGHT+1+textHeight/2)));
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int indentation){
		startLine(buffer, indentation);
		buffer.append(!this.name.equals("epsilonBoolean")? this.name + "()" : "true");
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 7);
		writer.writeStartElement("booleanObject");
		writer.writeAttribute("name", this.name);
		writer.writeAttribute("positioning", String.valueOf(this.positioning));
		writer.writeEndElement();
		
		writer.writeCharacters(NEWLINE);
	}

	@Override
	public void loadProgramm(Element booleanElement) {
		//Lade alle Attribute
		this.positioning = Integer.parseInt(booleanElement.getAttribute("positioning"));
	}
}
