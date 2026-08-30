package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import de.hamster.scratch.ScratchUtils;

/**
 * Ein MenuItem symbolisiert einen Eintrag im ContextMenu.
 * Dieser besitzt einen Namen, der im Menu angezeigt wird
 * und einen dazugehörigen ClickHandler, dessen Methode
 * onClick() ausgeführt wird, sobald auf diesen Menueintrag
 * geklickt wurde.
 * @author HackZ
 *
 */
public class MenuItem {
	public static final Font FONT = new Font("Verdana", Font.PLAIN, 10);
	public static final int PADDING = 5;
	public static final int SPACING = 1;
	public static Color BG_COLOR = new Color(248, 248, 248);
	public static Color BG_COLOR_HOVERED = new Color(206, 206, 206);
	
	protected String name;
	protected ClickHandler clickHandler;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	private int textHeight;
	protected boolean hovered;
	protected boolean enabled = true;
	
	public MenuItem(String name, ClickHandler clickHandler) {
		this.name = name;
		this.clickHandler = clickHandler;
		this.width = ScratchUtils.getTextWidth(name, FONT) + 2 * PADDING;
		this.textHeight = ScratchUtils.getTextHeight("dj", FONT);
		this.height = textHeight + 2 * SPACING;
	}
	
	/**
	 * Zeichnet das MenuItem auf das übergebene Graphics Element
	 * @param g
	 * Graphics Element, auf dem gerendert werden soll.
	 */
	public void paint(Graphics g) {
		g.setColor(getBackgroundColor());
		g.fillRect(x, y, width, height);
		
		g.setColor(getTextColor());
		g.setFont(FONT);
		g.drawString(name, x + PADDING, y + (height + textHeight) / 2 - 4);
	}
	
	/**
	 * Ändert die Position und die Abmessungen des MenuItems.
	 * Dies geschieht beim erstellen des ContextMenus, wenn
	 * die einzelnen Menueeinträge ihre eigenen Positionen
	 * bekommen.
	 * @param x
	 * x Koordinate
	 * @param y
	 * y Koordinate
	 * @param width
	 * Breite des MenuItems
	 * @param height
	 * Höhe des MenuItems
	 */
	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Setzt die Breite des MenuItems
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Liefert die Breite des MenuItems.
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Liefert die Höhe des MenuItems.
	 * @return
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Liefert den Wert der Variablen <tt>hovered</tt>.
	 * @return
	 */
	public boolean isHovered() {
		return hovered;
	}
	
	/**
	 * Setzt die <tt>hovered</tt> Variable fest, die
	 * über die Hintergrundfarbe bestimmt.
	 * @param value
	 */
	public void setHovered(boolean value) {
		hovered = value;
	}
	
	/**
	 * Liefert den boolschen Wert zurück, ob das
	 * MenuItem aktiv ist und angeklickt werden kann.
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Legt fest, ob dieses MenuItem aktiv ist und
	 * angeklickt werden kann
	 * @param value
	 */
	public void setEnabled(boolean value) {
		enabled = value;
	}
	
	/**
	 * Liefert abhängig von <tt>hovered</tt>
	 * die Hintergrundfarbe des MenuItems.
	 * @return
	 */
	private Color getBackgroundColor() {
		if (hovered)
			return BG_COLOR_HOVERED;
		
		return BG_COLOR;
	}
	
	/**
	 * Liefert abhängig von <tt>enabled</tt>
	 * die Farbe des Textes.
	 * @return
	 */
	private Color getTextColor() {
		if (enabled)
			return Color.black;
		
		return Color.gray;
	}
	
	/**
	 * Testet, on die Koordinate (x; y) auf das
	 * MenuItem zeigt
	 * @param x
	 * x Koordinate
	 * @param y
	 * y Koordinate
	 * @return
	 * true, wenn das MenuItem getroffen wurde
	 */
	public boolean hitTest(int x, int y) {
		if (!enabled)
			return false;
		
		if (x < this.x || x >= this.x + width)
			return false;
		
		if (y < this.y || y >= this.y + height)
			return false;
		
		return true;
	}
	
	/**
	 * Wird vom ContextMenu aufgerufen, wenn auf
	 * dieses MenuItem geklickt wurde. Das veranlasst
	 * den im MenuItem enthaltenen ClickHandler die
	 * Methode onClick() auszuführen.
	 */
	public void onClick() {
		if (!enabled)
			return;
		
		if (clickHandler != null)
			clickHandler.onClick();
	}
}
