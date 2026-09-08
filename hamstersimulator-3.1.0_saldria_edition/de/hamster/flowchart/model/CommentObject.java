package de.hamster.flowchart.model;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JTextArea;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.view.FlowchartTextBox;

/**
 * Kommentar Objekt für Programmablaufpläne im Hamster-PAP-Editor
 * 
 * @author gerrit
 * 
 */
public class CommentObject extends RenderObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8849333020068242831L;
	private BufferedImage background;
	private FlowchartTextBox textbox;

	private FlowchartObject relatedObject;

	private Point tmpPoint;
	private int relId;

	/**
	 * Konstruktor vom CommentObject
	 * 
	 * @param comment
	 *            Der String der als Kommentar erscheinen soll.
	 */
	public CommentObject(String comment) {
		super();
		setString(comment);
		this.background = FlowchartUtil.getImage("flowchart/comment.png");
		this.setId(FlowchartUtil.generateId());
	}

	@Override
	public int getWidth() {
		return background.getWidth();
	}

	@Override
	public int getHeight() {
		return background.getHeight();
	}

	@Override
	public boolean isActivated(int x, int y) {
		return false;
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters("\t\t");
		writer.writeEmptyElement("comment");
		writer.writeAttribute("x", String.valueOf(this.x));
		writer.writeAttribute("y", String.valueOf(this.y));
		writer.writeAttribute("text", this.getText());
		if (relatedObject != null)
			writer.writeAttribute("relId", this.getRelatedObjectId());
		writer.writeCharacters("\n");

	}

	/**
	 * Gibt die ID vom zugeordneten Objekt
	 * 
	 * @return die ID
	 */
	private String getRelatedObjectId() {
		return String.valueOf(this.relatedObject.getId());
	}

	/**
	 * Ordne dem Kommentar ein Objekt zu.
	 * 
	 * @param o
	 *            Das zugeordnete Objekt.
	 */
	public void setRelatedObject(FlowchartObject o) {
		this.relatedObject = o;
	}

	/**
	 * Gibt das zugeordnete Objekt zurück.
	 * 
	 * @return Das FlowchartObjekt
	 */
	public FlowchartObject getRelatedObjekt() {
		return this.relatedObject;
	}

	/**
	 * Setzt einen temporären Punkt zum zeichnen der gestrichelten Linie.
	 * 
	 * @param p
	 *            der Punkt
	 */
	public void setTmpPoint(Point p) {
		this.tmpPoint = p;
	}

	/**
	 * Gtib den temporären Punkt zum zeichnen der getrichelten Linie zurück.
	 * 
	 * @return der Punkt
	 */
	public Point getTmpPoint() {
		return this.tmpPoint;
	}

	@Override
	public void draw(Graphics g) {

		// Zeichne eine gestrichelte Linie zu einem FlowchartObject
		if (relatedObject != null || tmpPoint != null) {
			Graphics2D line = (Graphics2D) g;
			float dashPhase = 0f;
			float dash[] = { 5.0f, 5.0f };
			line.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_MITER, 1.5f, dash, dashPhase));
			if (relatedObject != null) {
				line.drawLine(this.x, this.y + 30, relatedObject.x + 45,
						relatedObject.y + 30);
			} else if (tmpPoint != null) {
				line.drawLine(this.x, this.y + 30, tmpPoint.x, tmpPoint.y);
			}
			line.setStroke(new BasicStroke(3));
		}
		g.setFont(f);
		g.drawImage(this.background, this.x, this.y, null);
		BufferedImage textLabelImage = new BufferedImage(this.getWidth(),
				this.getHeight(), Transparency.BITMASK);
		JTextArea textArea = new JTextArea(this.getText());
		textArea.setLineWrap(true);
		textArea.setAutoscrolls(true);
		textArea.setBounds(this.x + 3, this.y + 3, 80, 50);
		textArea.setOpaque(false);
		textArea.paint(textLabelImage.getGraphics());
		g.drawImage(textLabelImage, this.x + 7, this.y + 5, null);
	}

	@Override
	public CommentObject clone() {
		CommentObject tmp = new CommentObject("");
		tmp.setCoordinates(this.x, this.y);
		return tmp;
	}

	/**
	 * Setzt die Textbox über die das Kommentar geändert werden kann.
	 * 
	 * @param box
	 *            Die Textbox.
	 */
	public void setTextBox(FlowchartTextBox box) {
		this.textbox = box;
	}

	/**
	 * Gibt die Textbox zurück um diese beispielswiese anzuzeigen und das
	 * Kommentar zu ändern.
	 * 
	 * @return Die Textbox.
	 */
	public FlowchartTextBox getTextBox() {
		return this.textbox;
	}

	/**
	 * Setzt die relId um später das Objekt zuzuweisen.
	 * 
	 * @param id
	 *            die Id
	 */
	public void setRelatedObjectId(int id) {
		this.relId = id;
	}

	/**
	 * Gibt die relId zurück um das relObjekt zuzuweisen.
	 * 
	 * @return die Id
	 */
	public int getRelId() {
		return this.relId;
	}

}
