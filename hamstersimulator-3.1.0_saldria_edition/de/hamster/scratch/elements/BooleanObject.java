package de.hamster.scratch.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.voids.FunctionResultException;

public abstract class BooleanObject extends Renderable {
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 9);
	public static Color COLOR_PRIMARY = new Color(98, 194, 19);
	public static Color COLOR_DARK = new Color(78, 155, 15);
	public static Color COLOR_LIGHT = new Color(106, 209, 21);
	public static Color COLOR_VLIGHT = new Color(137, 237, 54);
	public static Color COLOR_PRIMARY2 = new Color(99, 166, 44);
	public static Color COLOR_DARK2 = new Color(68, 90, 51);
	public static Color COLOR_LIGHT2 = new Color(154, 218, 101);
	public static Color COLOR_TEXT = Color.white;
	
	private boolean inner;
	
	public BooleanObject(boolean inner) {
		this.inner = inner;
		this.bounding = new BoundingBox(0, 0, 40, 17);
		maxChilds = 0;
		
//		DockingBox dockBottom = new DockingBox(0, 15, 100, 10, 0, 20);
//		dockings.put(0, dockBottom);
//		dockingTypes.put(0, RType.VOID);
		
		parentDock = null;
	}

	@Override
	public void render(Graphics g) {
		int x = bounding.getX();
		int y = bounding.getY();
		int w = bounding.getWidth();
		int h = bounding.getHeight() - 1;
		
		g.setColor(getPrimaryColor());
		int[] xPoints = new int[8];
		int[] yPoints = new int[8];
		xPoints[0] = x;					yPoints[0] = y + h / 2;
		xPoints[1] = x + h / 2;			yPoints[1] = y;
		xPoints[2] = x + w - h / 2;		yPoints[2] = y;
		xPoints[3] = x + w ;			yPoints[3] = y + h / 2;
		xPoints[4] = x + w;				yPoints[4] = y + h - h / 2;
		xPoints[5] = x + w - h / 2;		yPoints[5] = y + h;
		xPoints[6] = x + h / 2;			yPoints[6] = y + h;
		xPoints[7] = x;					yPoints[7] = y + h - h / 2;
		g.fillPolygon(xPoints, yPoints, 8);
		
		g.setColor(getDrakColor());
		g.drawLine(x, y + h - h / 2, x + h / 2, y + h);
		g.drawLine(x + h / 2, y + h, x + w - h / 2 - 1, y + h);
		g.drawLine(x + w - h / 2 - 1, y + h, x + w - 1, y + h - h / 2);
		g.drawLine(x + w - h / 2 - 1, y, x + w - 1, y + h / 2);
		
		g.setColor(getVLightColor());
		g.drawLine(x, y + h / 2, x + h / 2, y);
		g.drawLine(x + h / 2, y, x + w - h / 2 - 1, y);
		
		g.setColor(getLightColor());
		g.drawLine(x + h / 2, y + 1, x + w - h / 2, y + 1);
		
		if (inner) {
			g.drawLine(x + 1, y + h / 2, x + h / 2, y + 1);
			g.drawLine(x + w - h / 2 - 1, y, x + w - 2, y + h / 2 - 1);
			
			g.setColor(getDrakColor());
			g.drawLine(x + w - h / 2 - 2, y + h, x + w - 2, y + h - h / 2);
			g.drawLine(x + h / 2, y + h - 1, x + w - h / 2 - 1, y + h - 1);
		}
		
		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null)
				childs.get(i).render(g);
			else
				dockings.get(i).getType().render(g, dockings.get(i).getDockX() + bounding.getX(), dockings.get(i).getDockY() + bounding.getY());
		
		// Ein boolean kann kein Next haben
	}
	
	public void updateBounds() {
		
	}
	
	@Override
	public void renderShadow(Graphics g) {
		int x = bounding.getX();
		int y = bounding.getY();
		int w = bounding.getWidth();
		int h = bounding.getHeight() - 1;
		
		g.setColor(SHADOW_COLOR);
		int[] xPoints = new int[8];
		int[] yPoints = new int[8];
		xPoints[0] = x;					yPoints[0] = y + h / 2;
		xPoints[1] = x + h / 2;			yPoints[1] = y;
		xPoints[2] = x + w - h / 2;		yPoints[2] = y;
		xPoints[3] = x + w ;			yPoints[3] = y + h / 2;
		xPoints[4] = x + w;				yPoints[4] = y + h - h / 2;
		xPoints[5] = x + w - h / 2 - 1;	yPoints[5] = y + h + 1;
		xPoints[6] = x + h / 2 + 1;		yPoints[6] = y + h + 1;
		xPoints[7] = x;					yPoints[7] = y + h - h / 2;
		
		for (int i = 0; i < 8; i++) {
			xPoints[i] += SHADOW_SIZE;
			yPoints[i] += SHADOW_SIZE;
		}
		
		g.fillPolygon(xPoints, yPoints, 8);
		
		// Nicht die Childschatten rendern, weil diese alle ineinander liegen
//		for (int i = 0; i < maxChilds; i++)
//			if (childs.get(i) != null)
//				childs.get(i).renderShadow(g);
	}
	
	private Color getPrimaryColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_PRIMARY;
		
		if (inner)
			return COLOR_PRIMARY2;
		else
			return COLOR_PRIMARY;
	}
	
	private Color getDrakColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_DARK;
		
		if (inner)
			return COLOR_LIGHT2;
		else
			return COLOR_DARK;
	}
	
	private Color getLightColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_LIGHT;
		
		if (inner)
			return COLOR_DARK2;
		else
			return COLOR_LIGHT;
	}
	
	private Color getVLightColor() {
		if (ScratchProgram.isActive(this))
			return ACTIVE_VLIGHT;
		
		if (inner)
			return COLOR_DARK2;
		else
			return COLOR_VLIGHT;
	}

	@Override
	public RType getType() {
		return RType.BOOLEAN;
	}

	@Override
	abstract public Renderable clone();

	@Override
	abstract public Object performImplementation(ScratchProgram program) throws FunctionResultException;
}
