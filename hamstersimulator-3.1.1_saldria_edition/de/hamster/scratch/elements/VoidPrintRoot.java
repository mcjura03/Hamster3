package de.hamster.scratch.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.DockingBox;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.elements.voids.FunctionResultException;

public class VoidPrintRoot extends Renderable {
	public static final int MIN_WIDTH = 40;
	public static final int START_WIDTH = 8;
	public static final int PARAMETER_SPACING = 5;
	public static final int MIN_HEIGHT = 23;
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 12);
	public static Color COLOR_PRIMARY = new Color(54, 88, 192);
	public static Color COLOR_DARK = new Color(32, 56, 128);
	public static Color COLOR_LIGHT = new Color(60, 98, 210);
	public static Color COLOR_VLIGHT = new Color(94, 125, 217);
	public static Color COLOR_TEXT = Color.white;
	
	protected int textWidth;
	protected int textHeight;
	
	public VoidPrintRoot(String name) {
		this.name = name;
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = 0;

		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight(name, TEXT_FONT);
		
		nextDock = new DockingBox(0, 20, MIN_WIDTH, 10, 0, MIN_HEIGHT, RType.VOID);
		parentDock = new DockingBox(0, -10, MIN_WIDTH, 10, 0, 0, RType.VOID);
		
		updateBounds();
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight(name, TEXT_FONT);
		updateBounds();
	}

	@Override
	public void render(Graphics g) {
		int x = bounding.getX();
		int y = bounding.getY();
		int w = bounding.getWidth();
		int h = bounding.getHeight();
		g.setColor(getPrimaryColor());
		g.fillRect(x + 2, y + 3, w - 3, h - 4);
		g.drawLine(x + 11, y + 2, x + 11, y + h - 1);
		g.drawLine(x + 12, y + 3, x + 12, y + h + 1);
		g.fillRect(x + 13, y + h - 1, 10, 4);
		g.drawLine(x + 23, y + 3, x + 23, y + h + 1);
		g.drawLine(x + 24, y + 2, x + 24, y + h - 1);
		g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 3);
		g.drawLine(x + 2, y + 2, x + w - 2, y + 2);
		
		g.setColor(getDrakColor());
		g.drawLine(x + 3, y + h - 1, x + 10, y + h - 1);
		g.drawLine(x + 11, y + h, x + 11, y + h + 1);
		g.drawLine(x + 12, y + h + 2, x + 12, y + h + 2);
		g.drawLine(x + 13, y + h + 3, x + 22, y + h + 3);
		g.drawLine(x + 23, y + h + 2, x + 23, y + h + 2);
		g.drawLine(x + 24, y + h, x + 24, y + h + 1);
		g.drawLine(x + 25, y + h - 1, x + w - 3, y + h - 1);
		g.drawLine(x + w - 2, y + h - 2, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + h - 4, x + w, y + 5);

		g.setColor(getLightColor());
		g.drawLine(x, y + 5, x, y + h - 4);
		g.drawLine(x + 1, y + 3, x + 1, y + h - 3);
		g.drawLine(x + 5, y + 1, x + w - 3, y + 1);
		g.drawLine(x + 3, y + 2, x + 4, y + 2);
		g.drawLine(x + 2, y + 3, x + 2, y + 4);
		
		g.setColor(getVLightColor());
		g.drawLine(x + 5, y, x + w - 5, y);
		g.drawLine(x + 3, y + 1, x + 4, y + 1);
		g.drawLine(x + 2, y + 2, x + 2, y + 2);
		
		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
	    g.drawString(name, x + 5, y + textHeight + (h - textHeight) / 2 - 2);
		
		// Hat keine Childs, es kann direkt das next-Element gerendert werden
		
		if (next != null)
			next.render(g);
	}
	
	@Override
	public void addAsNext(Renderable r) {
		next = r;
		if (r == null)
			return;
		
		bounding.setPosition(r.getX(), r.getY() - bounding.getHeight());
	}
	
	@Override
	public void renderShadow(Graphics g) {
		// Das PrintRoot Objekt wird nur verwendet als
		// Root-Element in Bildern und braucht kein Schatten
	}
	
	public void updateBounds() {
		// Breite berechnen
		int w = START_WIDTH;
		int h = MIN_HEIGHT;
		w += textWidth;
		w += PARAMETER_SPACING;
		
		w = (w < MIN_WIDTH) ? MIN_WIDTH : w;
		bounding.setSize(w, h);
		
		// DockingBoxen berechnen
		parentDock.setSize(w, 10);
		if (nextDock != null) {
			nextDock.setSize(w, 10);
			nextDock.setPosition(0, bounding.getHeight());
			nextDock.setDockY(bounding.getHeight());
		}
	}
	
	protected Color getPrimaryColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_PRIMARY;
		
		return COLOR_PRIMARY;
	}
	
	protected Color getDrakColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_DARK;
		
		return COLOR_DARK;
	}
	
	protected Color getLightColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_LIGHT;
		
		return COLOR_LIGHT;
	}
	
	protected Color getVLightColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_VLIGHT;
		
		return COLOR_VLIGHT;
	}

	@Override
	public RType getType() {
		return RType.VOID;
	}

	@Override
	public Renderable clone() {
		// Kann nicht geklohnt werden
		return null;
	}

	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		// Kann nicht ausgeführt werden
		return null;
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int layer, boolean comment, boolean needsReturn) throws FunctionResultException {
		// Kann nicht als Quelltext ausgegeben werden
	}
}
