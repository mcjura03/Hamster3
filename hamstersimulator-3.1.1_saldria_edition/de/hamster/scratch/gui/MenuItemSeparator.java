package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Graphics;

public class MenuItemSeparator extends MenuItem {
	public static Color PRIMARY = new Color(172, 172, 172);
	public static Color SECONDARY = new Color(206, 206, 206);
	
	public MenuItemSeparator() {
		super("", null);
		height = 2;
		enabled = false;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(PRIMARY);
		g.drawLine(x, y, x + width, y);
		g.setColor(SECONDARY);
		g.drawLine(x, y + 1, x + width, y + 1);
	}
}
