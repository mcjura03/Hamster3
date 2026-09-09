package de.hamster.scratch.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.DockingBox;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.voids.FunctionResultException;

public class RootBooleanObject extends Renderable {
	public static final int MIN_WIDTH = 40;
	public static final int START_WIDTH = 8;
	public static final int MIN_HEIGHT = 20;
	public static final int PARAMETER_SPACING = 5;
	public static final int MIN_INTERNAL_HEIGHT = 10;
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 9);
	public static Color COLOR_PRIMARY = new Color(78, 174, 0);
	public static Color COLOR_DARK = new Color(58, 135, 0);
	public static Color COLOR_LIGHT = new Color(86, 189, 1);
	public static Color COLOR_VLIGHT = new Color(117, 217, 34);
	public static Color COLOR_TEXT = Color.white;
	
	public RootBooleanObject() {
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = 0;
		
		nextDock = new DockingBox(0, 5, MIN_WIDTH, 5, 0, MIN_HEIGHT, RType.VOID);
		parentDock = null;
		
		updateBounds();
	}

	@Override
	public void render(Graphics g) {
		int x = bounding.getX();
		int y = bounding.getY();
		int w = bounding.getWidth();
		int h = bounding.getHeight();
		g.setColor(COLOR_PRIMARY);
		g.fillRect(x + 2, y + 5, w - 3, h - 6);
		g.fillRect(x + 2, y + 2, 9, 3);
		g.drawLine(x + 11, y + 2, x + 11, y + h - 1);
		g.drawLine(x + 12, y + 3, x + 12, y + h + 1);
		g.fillRect(x + 13, y + h - 1, 10, 4);
		g.drawLine(x + 23, y + 3, x + 23, y + h + 1);
		g.drawLine(x + 24, y + 2, x + 24, y + h - 1);
		g.fillRect(x + 25, y + 2, w - 26, 3);
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
		
		g.setColor(COLOR_DARK);
		g.drawLine(x + 3, y + h - 1, x + 10, y + h - 1);
		g.drawLine(x + 11, y + h, x + 11, y + h + 1);
		g.drawLine(x + 12, y + h + 2, x + 12, y + h + 2);
		g.drawLine(x + 13, y + h + 3, x + 22, y + h + 3);
		g.drawLine(x + 23, y + h + 2, x + 23, y + h + 2);
		g.drawLine(x + 24, y + h, x + 24, y + h + 1);
		g.drawLine(x + 25, y + h - 1, x + w - 3, y + h - 1);
		g.drawLine(x + w - 2, y + h - 2, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + h - 4, x + w, y + 3);

		g.setColor(COLOR_LIGHT);
		g.drawLine(x + 1, y + 1, x + 1, y + h - 3);
		g.drawLine(x + 1, y + 1, x + 10, y + 1);
		g.drawLine(x, y + 2, x, y + h - 4);
		g.drawLine(x + 25, y + 1, x + w - 2, y + 1);
		
		g.setColor(COLOR_VLIGHT);
		g.drawLine(x + 2, y, x + 10, y);
		g.drawLine(x + 25, y, x + w - 3, y);
		g.drawLine(x + 13, y + 4, x + 22, y + 4);
		
		if (next != null)
			next.render(g);
	}
	
	@Override
	public void renderShadow(Graphics g) {
		// Das Root Objekt kann nicht verschoben werden
	}
	
	public void updateBounds() {
		bounding.setSize(100, 10);
		
		// DockingBoxen berechnen
		if (nextDock != null) {
			nextDock.setSize(100, 10);
			nextDock.setPosition(0, bounding.getHeight());
			nextDock.setDockY(bounding.getHeight());
		}
	}
	
	public Renderable hitTest(int x, int y) {
		if (next != null)
			return next.hitTest(x, y);
		
		return null;
	}

	@Override
	public RType getType() {
		return RType.VOID;
	}

	@Override
	public Renderable clone() {
		return null;
	}

	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		return null;
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int layer, boolean comment, boolean needsReturn) throws FunctionResultException {
		if (next != null)
			next.writeSourceCode(buffer, layer, comment, needsReturn);
		else {
			startLine(buffer, layer, comment);
			buffer.append("return true;" + NEWLINE);
		}
	}
}
