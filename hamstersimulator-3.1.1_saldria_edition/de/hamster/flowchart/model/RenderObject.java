package de.hamster.flowchart.model;

import java.awt.Choice;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * RenderObject von dem alle PAP-Elemente abgeleitet sind.
 * 
 * @author gerrit
 * 
 */
public abstract class RenderObject extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 175461039240592800L;
	protected boolean active = false;
	public Choice dropDown = new Choice();
	Font f = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	protected int id = 0;
	private String name;
	protected String text;
	public int x = 0;
	public int y = 0;
	protected FlowchartAnchor anchorN;
	protected FlowchartAnchor anchorE;
	protected FlowchartAnchor anchorS;
	protected FlowchartAnchor anchorW;

	abstract public void draw(Graphics g);

	abstract public int getHeight();

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getText() {
		return this.text;
	}

	abstract public int getWidth();

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	abstract public boolean isActivated(int x, int y);

	public void setActivated(boolean active) {
		this.active = active;
	}

	public void setCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
		if (this instanceof FlowchartObject)
			this.setAnchors(new Point(x, y));
	}

	public void setCoordinates(Point point) {
		this.x = point.x;
		this.y = point.y;
		if (this instanceof FlowchartObject)
			this.setAnchors(point);
	}

	public void setAnchors(Point position) {
		anchorN = new FlowchartAnchor(position.x + 45, position.y, 0);
		anchorE = new FlowchartAnchor(position.x + 90, position.y + 30, 1);
		anchorS = new FlowchartAnchor(position.x + 45, position.y + 60, 2);
		anchorW = new FlowchartAnchor(position.x, position.y + 30, 3);
	}

	public void setId(int l) {
		this.id = l;
		setName(String.valueOf(l));
	}

	public void setName(String s) {
		this.name = s;
	}

	public void setString(String s) {
		this.text = s;
	}

	abstract public void toXML(XMLStreamWriter writer)
			throws XMLStreamException;

}
