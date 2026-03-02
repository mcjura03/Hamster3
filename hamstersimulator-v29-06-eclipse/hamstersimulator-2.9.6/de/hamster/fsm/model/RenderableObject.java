package de.hamster.fsm.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Klasse, die sich vor allem mit der grafischen Umsetzung der Elemente in dem Graph eines
 * endlichen Automaten auseinandersetzt.
 * Es werden Methoden bereitgestellt, mit denen überprüft werden kann ob ein Object angeklickt worden ist,
 * mit denen diese Objekte gezeichnet werden, mit denen diese Objekte geladen
 * @author Raffaela Ferrari
 *
 */
public abstract class RenderableObject {
	protected static int ZOOMFACTORFONT = 0;
	protected static double ZOOMFACTOR = 1;
	protected static final String NEWLINE = "\n";
	protected static final String LINEFEED = "\t";
	protected static Font TEXT_FONT = new Font("Arial", Font.BOLD, 10+ ZOOMFACTORFONT);
	protected static final Color HIGHLIGHTCOLOR = Color.red; //new Color(194, 68, 19);
	protected static final int LINE_THICKNESS = 2;

	protected boolean highlight = false;
	protected String name = "";
	protected int textWidth;
	protected int textHeight;
	protected int xStart = 0;
	protected int yStart = 0;
	
	/**
	 * Gibt die Breite des Objekts zurück.
	 * @return
	 */
	abstract public int getWidth();

	/**
	 * Gibt die Höhe des Objekts zurück.
	 * @return
	 */
	abstract public int getHeight();

	/**
	 * Gibt an, ob ein Objekt angeklickt wurde.
	 * @param x X-Koordinate des Klicks.
	 * @param y Y-Koordinate des Klicks.
	 * @return true, wenn das Objekt angeklickt wurde.
	 */
	abstract public boolean isClickedOn(int x, int y);

	/**
	 * Gibt die JMenuItems für das KontextMenü des Objekts zurück.
	 * @param panel Panel, indem die Funktionen des MenuItems ausgeführt werden sollen.
	 * @return Liste an JMenuItems.
	 */
	abstract public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel);

	/**
	 * Gibt eine XML-Darstellung des Objektes an den XMLStreamWriter
	 * @param writer XMLStreamWriter, der das XML-Dokument erstellt.
	 * @throws XMLStreamException
	 */
	abstract public void toXML(XMLStreamWriter writer) throws XMLStreamException;

	/**
	 * Rendert sich und alle Kinder
	 * @param g
	 */
	abstract public void render(Graphics g);


	/**
	 * Setzt den Namen des RenderableObjects fest
	 * @param name Name des RenderableObjects
	 */
	public void setName(String name) {
		this.name = name;
		setTextCoordinates(name);
	}

	/**
	 * Liefert den Namen des RenderableObjects zurück 
	 * @return name Name des RenderableObjects
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt die Textweite und -höhe für den übergebenen Namen
	 * @param name Name, der dazu untersucht wird
	 */
	public void setTextCoordinates(String name){
		this.textWidth = FsmUtils.getTextWidth(this.name, TEXT_FONT);
		this.textHeight = FsmUtils.getTextHeight(TEXT_FONT);
	}

	/**
	 * Gibt die xStartKoordinate zurück
	 * @return xStartKoordinate
	 */
	public int getXCoordinate() {
		return this.xStart;
	}

	/**
	 * Gibt die yStartKoordinate zurück
	 * @return yStartKoordinate
	 */
	public int getYCoordinate() {
		return this.yStart;
	}

	/**
	 * Setzt die  x- und yStartKoordinate auf den neuen x- bzw. y-Wert
	 * @param x neuer X-Wert
	 * @param y neuer y-Wert
	 */
	public void setCoordinates(int x, int y) {
		this.xStart = x;
		this.yStart = y;
	}

	/**
	 * Setzt, ob ein RenderableElement grafisch hervorgehoben werden soll.
	 * @param shouldHighlight sagt aus, ob ein RenderableElement grafisch hervorgehoben werden soll.
	 */
	public void highlight(boolean shouldHighlight) {
		this.highlight = shouldHighlight;
	}
}
