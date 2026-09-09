package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.hamster.scratch.ScratchUtils;

public class TabButton implements MouseListener {
	public static Color DARK_TEXT_COLOR = new Color(33, 33, 31);
	public static Color LIGHT_TEXT_COLOR = new Color(88, 89, 93);
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 11);
	public static final int PADDING = 15;
	public static final int SPACING = -6;
	public static final int HEIGHT = 23;

	private static BufferedImage imgLeft;
	private static BufferedImage imgRight;
	private static BufferedImage imgMiddle;
	private static BufferedImage imgLeftHovered;
	private static BufferedImage imgRightHovered;
	private static BufferedImage imgMiddleHovered;
	
	private ArrayList<RefreshHandler> refreshHandler;
	private ArrayList<TabChangedHandler> tabChangedHandler;
	private ArrayList<TabClickHandler> clickHandler;
	private String name;
	private int textWidth;
	private int textHeight;
	private int x;
	private int y;
	private boolean selected;
	private boolean hovered;
	private boolean visible;
	
	public TabButton(String name) {
		this.name = name;
		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight("Ag", TEXT_FONT);
		refreshHandler = new ArrayList<RefreshHandler>();
		tabChangedHandler = new ArrayList<TabChangedHandler>();
		clickHandler = new ArrayList<TabClickHandler>();
		loadImages();
	}
	
	/**
	 * Wird im Konstruktor aufgerufen und lädt LookAndFeel Bilder.
	 * Diese Methode wird nur einmalig ausgeführt, da die Bilder
	 * statisch in der Klasse sind.
	 */
	private void loadImages() {
		if (imgLeft != null)
			return; // Bilder sind bereits geladen

		imgLeft = ScratchUtils.getImage("scratch/buttons/tab_l.png");
		imgRight = ScratchUtils.getImage("scratch/buttons/tab_r.png");
		imgMiddle = ScratchUtils.getImage("scratch/buttons/tab_m.png");
		imgLeftHovered = ScratchUtils.getImage("scratch/buttons/tab_l_hover.png");
		imgRightHovered = ScratchUtils.getImage("scratch/buttons/tab_r_hover.png");
		imgMiddleHovered = ScratchUtils.getImage("scratch/buttons/tab_m_hover.png");
	}
	
	public void paint(Graphics g, int x, int y) {
		this.x = x;
		this.y = y;
		paintBackground(g, x, y);
		
		g.setColor(getTextColor());
		g.setFont(TEXT_FONT);
		g.drawString(name, x + PADDING, y + (HEIGHT + textHeight) / 2 - 3);
	}
	
	private void paintBackground(Graphics g, int x, int y) {
		g.drawImage(getLeftImage(), x, y, null);
		g.drawImage(getRightImage(), x + PADDING + textWidth, y, null);
		
		//g.drawImage(getMiddleImage(), x + PADDING, y, textWidth, HEIGHT, null);
		for (int i = 0; i < getMiddleImage().getHeight(); i++) {
			g.setColor(new Color(getMiddleImage().getRGB(0, i)));
			g.drawLine(x + PADDING, y + i, x + PADDING + textWidth, y + i);
		}
	}
	
	private BufferedImage getLeftImage() {
		if (selected || hovered)
			return imgLeftHovered;
		
		return imgLeft;
	}
	
	private BufferedImage getRightImage() {
		if (selected || hovered)
			return imgRightHovered;
		
		return imgRight;
	}
	
	private BufferedImage getMiddleImage() {
		if (selected || hovered)
			return imgMiddleHovered;
		
		return imgMiddle;
	}
	
	public void setSelected(boolean value) {
		selected = value;
	}
	
	public void setVisible(boolean value) {
		visible = value;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isSelected()  {
		return selected;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
	}
	
	public int getWidth() {
		return textWidth + 2 * PADDING + SPACING;
	}
	
	private boolean hitTest(int x, int y) {
		if (x < this.x || x > this.x + getWidth())
			return false;
		
		if (y < this.y || y > this.y + HEIGHT)
			return false;
		
		return true;
	}
	
	/**
	 * Führt für alle angemeldeten TabClickHandler die darin
	 * enthaltene Methode onClick(TabButton) aus und übergibt
	 * dabei diesen TabButton als Sender und die angeklickte
	 * Koordinate.
	 * @param x
	 * x Koordinate
	 * @param y
	 * y Koordinate
	 */
	public void onRightClick(int x, int y) {
		for (TabClickHandler handler : clickHandler)
			handler.onClick(this, x, y);
	}
	
	/**
	 * Fügt diesem TabButton einen TabClickHandler hinzu 
	 * @param handler
	 */
	public void addTabClickHandler(TabClickHandler handler) {
		clickHandler.add(handler);
	}
	
	public void addRefreshHandler(RefreshHandler handler) {
		refreshHandler.add(handler);
	}
	
	/**
	 * Fügt einen TabChangedHandler zu diesem TabPanel hinzu, dessen
	 * interne Methode tabChanged aufgerufen wird, sobald sich der
	 * aktive Tab ändert.
	 * @param handler
	 * Handler, der beim Tabwechsel aufgerufen werden soll
	 */
	public void addTabChangedHandler(TabChangedHandler handler) {
		tabChangedHandler.add(handler);
	}
	
	private void refresh() {
		for (RefreshHandler handler : refreshHandler)
			handler.refresh();
	}
	
	private void tabChanged() {
		if (selected)
			return;
		
		for (TabChangedHandler handler : tabChangedHandler)
			handler.tabChanged(this);
	}
	
	private Color getTextColor() {
		if (selected)
			return DARK_TEXT_COLOR;
		
		return LIGHT_TEXT_COLOR;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!visible)
			return;
		
		if (hitTest(e.getX(), e.getY())) {
			hovered = true;
			refresh();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!visible)
			return;
		
		if (hovered) {
			hovered = false;
			if (hitTest(e.getX(), e.getY()))
				if (e.getButton() == MouseEvent.BUTTON1)
					tabChanged();
				else if (e.getButton() == MouseEvent.BUTTON3)
					onRightClick(e.getX(), e.getY());
			
			refresh();
		}
	}
}
