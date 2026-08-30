package de.hamster.fsm.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.controller.handler.RenameObjectInContextMenu;
import de.hamster.fsm.view.ContextMenuPanel;
import de.hamster.fsm.view.FsmPanel;

/**
 * Klasse, die einen Kommentar reprÃ¤sentiert.
 * @author Raffaela Ferrari
 *
 */
public class CommentObject extends RenderableObject{
	private static final Color COLOR = new Color(0,0,0);
	private static final Color INNER_COLOR = new Color(255,236,139);
	private static final int HEIGHT = 10;
	private static final int WIDTH = 20;
	
	private Rectangle2D oval;
	
	/**
	 * Konstruktor
	 */
	public CommentObject() {
		super();
		this.name = "Kommentar";
		setTextCoordinates(this.name);
	}

	@Override
	public int getWidth() {
		return WIDTH+this.textWidth;
	}

	@Override
	public int getHeight() {
		return HEIGHT+this.textHeight;
	}

	@Override
	public void setCoordinates(int x, int y) {
		if(x-2 >= 0 && y-2>=0) {
			this.xStart = x;
			this.yStart = y;
		}
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem deleteComment = new JMenuItem("Kommentar löschen");
		deleteComment.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteComment);
		
		JMenuItem renameComment = new JMenuItem("Kommentartext ändern");
		renameComment.addActionListener(new RenameObjectInContextMenu(this, 
				((FsmPanel)panel).getAutomataPanel(), this.getXCoordinate(), this.getYCoordinate()));
		menuItemList.add(renameComment);

		return menuItemList;
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return oval.contains(x, y);
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		Stroke stroke = new BasicStroke((float) (LINE_THICKNESS*ZOOMFACTOR));
		g2d.setStroke(stroke);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		oval = new Rectangle2D.Double(this.xStart, this.yStart, WIDTH+this.textWidth, 
				HEIGHT+this.textHeight);
		g2d.setColor(INNER_COLOR);
		g2d.fill(oval);
		g2d.setColor(COLOR);
		if(this.highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(COLOR);
		}
		g2d.draw(oval);

		if(this.highlight) {
			g2d.setColor(COLOR);
		} 
		g2d.setFont(TEXT_FONT);
		g2d.drawString(this.name, this.xStart+10, this.yStart+HEIGHT+this.textHeight/2);
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters(NEWLINE);
		writer.writeCharacters(LINEFEED);
		writer.writeStartElement("comment");
		writer.writeAttribute("x", String.valueOf(getXCoordinate()));
		writer.writeAttribute("y", String.valueOf(getYCoordinate()));
		writer.writeCharacters(NEWLINE);
		writer.writeCharacters(this.name);
		writer.writeCharacters(NEWLINE);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
	}
}
