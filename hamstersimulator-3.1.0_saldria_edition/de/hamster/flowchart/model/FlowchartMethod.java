package de.hamster.flowchart.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.w3c.dom.Element;

import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.view.FlowchartDrawPanel;

/**
 * Die FlowchartMethoden repräsentieren die verschiedenen Unterprogramme des
 * Programmablaufplans.
 * 
 * @author gerrit
 * 
 */
public class FlowchartMethod {

	private Element methodDoc;
	public String name;
	private CopyOnWriteArrayList<FlowchartObject> elementList = new CopyOnWriteArrayList<FlowchartObject>();
	private String startId;
	private CopyOnWriteArrayList<CommentObject> commentList = new CopyOnWriteArrayList<CommentObject>();
	private FlowchartDrawPanel drawPanel;

	public FlowchartMethod(FlowchartHamsterFile f, String n) {
		this.name = n;
		this.setDrawPanel(new FlowchartDrawPanel(this, true, f));
	}

	/**
	 * Setzt das ZeichenPanel
	 * 
	 * @param panel
	 *            das Panel
	 */
	public void setDrawPanel(FlowchartDrawPanel panel) {
		this.drawPanel = panel;
	}

	/**
	 * Gibt das ZeichenPanel zurück
	 * 
	 * @return das Panel
	 */
	public FlowchartDrawPanel getDrawPanel() {
		return this.drawPanel;
	}

	/**
	 * Setzt die XML-Element-Liste
	 * 
	 * @param doc
	 *            die XML-Liste
	 */
	public void setElementNodeList(Element doc) {
		this.methodDoc = doc;
	}

	/**
	 * Gibt die XML-Element-Liste zurück.
	 * 
	 * @return die XML-Liste
	 */
	public Element getElementList() {
		return methodDoc;
	}

	/**
	 * Setzt den Methodennamen
	 * 
	 * @param s
	 *            der Name
	 */
	public void setName(String s) {
		this.name = s;
	}

	/**
	 * Gibt den Methodennamen zurück
	 * 
	 * @return den Namen
	 */
	public String getName() {
		return name;
	}

	/**
	 * Fügt ein Element zur FlowchartObjektListe hinzu
	 * 
	 * @param element
	 *            das neue Element/Objekt
	 */
	public void addElementToList(FlowchartObject element) {
		this.elementList.add(element);
	}

	/**
	 * Gibt die FlowchartObjektListe zurück
	 * 
	 * @return die Liste
	 */
	public CopyOnWriteArrayList<FlowchartObject> getElemList() {
		return elementList;
	}

	/**
	 * Setzt das Start Attribut
	 * 
	 * @param attribute
	 *            die StartId
	 */
	public void setStart(String attribute) {
		this.startId = attribute;
	}

	/**
	 * Gibt die StartId zurück
	 * 
	 * @return die Id
	 */
	public String getStart() {
		return this.startId;
	}

	/**
	 * Entfernt ein Element von der Liste
	 * @param flowchartElement
	 * 		das Element welchen entfernt werden soll
	 */
	public void removeElementFromList(FlowchartObject flowchartElement) {
		this.elementList.remove(flowchartElement);
	}

	/**
	 * Gibt die Kommentarliste der Methode zurück.
	 * 
	 * @return Die Liste mit den Kommentaren.
	 */
	public CopyOnWriteArrayList<CommentObject> getCommentList() {
		return this.commentList;
	}

	/**
	 * Füht ein Kommentar zur Liste in der Methode hinzu.
	 * 
	 * @param comment
	 *            Das neue Kommentar.
	 */
	public void addComment(CommentObject comment) {
		this.commentList.add(comment);
	}

	/**
	 * Gibt den höchten x Wert der Elemente bzw. Kommentare zurück.
	 * 
	 * @return höchster x-Wert
	 */
	public int getWidth() {
		int w = 0;
		for (FlowchartObject f : this.getElemList()) {
			if (f.x > w)
				w = f.x + 120;
		}
		for (CommentObject c : this.getCommentList()) {
			if (c.x > w)
				w = c.x + 120;
		}
		return w;
	}

	/**
	 * Gibt den höchten y Wert der Elemente bzw. Kommentare zurück.
	 * 
	 * @return höchster y-Wert
	 */
	public int getHeight() {
		int h = 0;
		for (FlowchartObject f : this.getElemList()) {
			if (f.y > h)
				h = f.y + 80;
		}
		for (CommentObject c : this.getCommentList()) {
			if (c.y > h)
				h = c.y + 80;
		}
		return h;
	}

}
