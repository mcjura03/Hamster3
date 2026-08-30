package de.hamster.scratch.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class ContextMenu extends JPanel implements MouseListener, MouseMotionListener, FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4958790842943614188L;
	public static final int ARC_WIDTH = 13;
	public static final int PADDING = 4;
	public static Color BACKGROUND_COLOR = new Color(248, 248, 248);
	public static Color BORDER_COLOR = new Color(96, 99, 101);
	
	private ArrayList<RefreshHandler> refreshHandler;
	private ArrayList<MenuItem> menuItems;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public ContextMenu() {
		addMouseMotionListener(this);
		addMouseListener(this);
		addFocusListener(this);
		setVisible(false);
		refreshHandler = new ArrayList<RefreshHandler>();
	}
	
	/**
	 * Fügt diesem Menu einen neuen RefreshHandler hinzu, dieser
	 * wird aufgerufen, sobald das menu neu gezeichnet werden muss
	 * @param handler
	 */
	public void addRefreshHandler(RefreshHandler handler) {
		refreshHandler.add(handler);
	}
	
	/**
	 * Veranlasst das neuzeichnen des Menus durch den angemeldeten
	 * Parent, der den RefreshHandler implementiert
	 */
	public void refresh() {
		for (RefreshHandler handler : refreshHandler)
			handler.refresh();
	}
	
	@Override
	public void paint(Graphics g) {
		if (menuItems == null)
			return;
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(BACKGROUND_COLOR);
		g2.fillRoundRect(x, y, width, height, ARC_WIDTH, ARC_WIDTH);
		g2.setColor(BORDER_COLOR);
		g2.setStroke(new BasicStroke(2.0f));
		g.drawRoundRect(x, y, width, height, ARC_WIDTH, ARC_WIDTH);
		g2.setStroke(new BasicStroke(1.0f));
		
		for (MenuItem m : menuItems)
			m.paint(g);
	}
	
	/**
	 * Erstellt ein Menu mit den übergebenen Einträgen an der
	 * Position (x - width; y)
	 * @param menuItems
	 * @param x
	 * @param y
	 */
	public void openMenu(ArrayList<MenuItem> menuItems, int x, int y) {
		this.menuItems = menuItems;
		
		int maxW = 0;
		for (MenuItem m : menuItems)
			if (m.getWidth() > maxW)
				maxW = m.getWidth();
		
		this.x = x - maxW - PADDING;
		if (this.x < 0) {
			this.x = 0;
			x = maxW + PADDING;
		}
		this.y = y - PADDING;
		if (this.y < 0) {
			this.y = 0;
			y = PADDING;
		}
		
		int currentY = y;
		for (int i = 0; i < menuItems.size(); i++) {
			menuItems.get(i).setBounds(x - maxW, currentY, maxW, menuItems.get(i).getHeight());
			currentY += menuItems.get(i).getHeight();
		}
		
		this.width = maxW + 2 * PADDING;
		this.height = currentY - y + 2 * PADDING;
		this.setVisible(true);
		this.requestFocus();
	}
	
	/**
	 * Testet, on die Koordinate (x; y) auf das offene
	 * Menu trifft
	 * @param x
	 * x Koordinate
	 * @param y
	 * y Koordinate
	 * @return
	 * true, wenn das Menu getroffen wurde
	 */
	private boolean hitTest(int x, int y) {
		if (x < this.x || x >= this.x + width)
			return false;
		
		if (y < this.y || y >= this.y + height)
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
		if (!hitTest(e.getX(), e.getY()))
			this.setVisible(false);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!isVisible())
			return;
		
		if (!hitTest(e.getX(), e.getY())) {
			this.setVisible(false);
			repaint();
			return;
		}
		
		if (e.getButton() != MouseEvent.BUTTON1)
			this.setVisible(false);
		
		for (MenuItem m : menuItems)
			if (m.hitTest(e.getX(), e.getY())) {
				m.onClick();
				break;
			}
		
		setVisible(false);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		onMouseMotion(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		onMouseMotion(e);
	}
	
	private void onMouseMotion(MouseEvent e) {
		if (!isVisible() || menuItems == null)
			return;
		
		boolean needRefresh = false;
		for (int i = 0; i < menuItems.size(); i++) {
			MenuItem temp = menuItems.get(i);
			boolean hit = temp.hitTest(e.getX(), e.getY());
			if (temp.isHovered() != hit) {
				needRefresh = true;
				temp.setHovered(hit);
			}
		}
		
		if (needRefresh)
			refresh();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		refresh();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		setVisible(false);
	}
	
	public String toS() {
		return "" + this.x + "/" + this.y + "/" + this.width + "/" + this.height;
	}
}
