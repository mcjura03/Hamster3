package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.hamster.scratch.ScratchUtils;

public class ElementListButton implements MouseListener, MouseMotionListener {
	public static final int PADDING = 6;
	public static final int HEIGHT = 20;
	public static final Font FONT = new Font("Verdana", Font.BOLD, 10);
	public static Color TEXT_COLOR = new Color(78, 82, 82);
	public static Color HOVERED_TEXT_COLOR = new Color(0, 0, 1);

	private static BufferedImage imgLeft;
	private static BufferedImage imgMiddle;
	private static BufferedImage imgRight;
	private static BufferedImage imgLeftHover;
	private static BufferedImage imgMiddleHover;
	private static BufferedImage imgRightHover;
	
	private String name;
	private int textWidth;
	private int textHeight;
	private int x;
	private int y;
	private boolean visible;
	private boolean hovered;
	private boolean mouseDown;
	private ArrayList<RefreshHandler> refreshHandler;
	private ArrayList<ClickHandler> clickHandler;
	
	public ElementListButton(String name) {
		this.name = name;
		this.textWidth = ScratchUtils.getTextWidth(name, FONT);
		this.textHeight = ScratchUtils.getTextHeight(name, FONT);
		this.refreshHandler = new ArrayList<RefreshHandler>();
		this.clickHandler = new ArrayList<ClickHandler>();
		
		loadImages();
	}
	
	private void loadImages() {
		if (imgLeft != null)
			return; // Bilder sind bereits geladen

		imgLeft = ScratchUtils.getImage("scratch/buttons/create_button_l.png");
		imgMiddle = ScratchUtils.getImage("scratch/buttons/create_button_m.png");
		imgRight = ScratchUtils.getImage("scratch/buttons/create_button_r.png");
		imgLeftHover = ScratchUtils.getImage("scratch/buttons/create_button_l_hover.png");
		imgMiddleHover = ScratchUtils.getImage("scratch/buttons/create_button_m_hover.png");
		imgRightHover = ScratchUtils.getImage("scratch/buttons/create_button_r_hover.png");
	}
	
	public void addRefreshHandler(RefreshHandler handler) {
		refreshHandler.add(handler);
	}
	
	public void refresh() {
		for (RefreshHandler handler : refreshHandler)
			handler.refresh();
	}
	
	public void addClickHandler(ClickHandler handler) {
		clickHandler.add(handler);
	}
	
	public void onClick() {
		for (ClickHandler handler : clickHandler)
			handler.onClick();
	}
	
	public void paint(Graphics g) {
		if (!visible)
			return;
		
		g.drawImage(getLeftImage(), x, y, null);
		for (int i = 0; i < getMiddleImage().getHeight(); i++) {
			g.setColor(new Color(getMiddleImage().getRGB(0, i)));
			g.drawLine(x + PADDING, y + i, x + PADDING + textWidth, y + i);
		}
		g.drawImage(getRightImage(), x + textWidth + PADDING, y, null);
		
		g.setColor(getTextColor());
		g.setFont(FONT);
		g.drawString(name, x + 5, y + (HEIGHT + textHeight) / 2 - 3);
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveRelative(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	public int getWidth() {
		return textWidth + 2 * PADDING;
	}
	
	public void setVisible(boolean value) {
		this.visible = value;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isHovered() {
		return hovered;
	}
	
	private BufferedImage getLeftImage() {
		if (hovered)
			return imgLeftHover;
		
		return imgLeft;
	}
	
	private BufferedImage getRightImage() {
		if (hovered)
			return imgRightHover;
		
		return imgRight;
	}
	
	private BufferedImage getMiddleImage() {
		if (hovered)
			return imgMiddleHover;
		
		return imgMiddle;
	}
	
	private Color getTextColor() {
		if (hovered)
			return HOVERED_TEXT_COLOR;
		
		return TEXT_COLOR;
	}
	
	private boolean hitTest(int x, int y) {
		if (x < this.x || x > this.x + getWidth())
			return false;
		
		if (y < this.y || y > this.y + HEIGHT)
			return false;
		
		return true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!visible)
			return;
		
		if (!hitTest(e.getX(), e.getY()))
			return;
		
		mouseDown = true;
		hovered = true;
		refresh();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!mouseDown)
			return;
		
		mouseDown = false;
		hovered = false;
		refresh();
		
		if (hitTest(e.getX(), e.getY()))
			onClick();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!mouseDown)
			return;
		
		boolean hit = hitTest(e.getX(), e.getY());
		if (hovered && !hit) {
			hovered = false;
			refresh();
			return;
		}
		
		if (!hovered && hit) {
			hovered = true;
			refresh();
			return;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
