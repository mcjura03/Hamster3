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

public abstract class ControlObject extends Renderable {
	public static final int MIN_WIDTH = 40;
	public static final int START_WIDTH = 8;
	public static final int MIN_HEIGHT = 20;
	public static final int PARAMETER_SPACING = 5;
	public static final int MIN_INTERNAL_HEIGHT = 11;
	public static final int FOOT_HEIGHT = 12;
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 9);
	public static Color COLOR_PRIMARY = new Color(230, 168, 34);
	public static Color COLOR_DARK = new Color(161, 117, 24);
	public static Color COLOR_LIGHT = new Color(248, 181, 37);
	public static Color COLOR_VLIGHT = new Color(255, 197, 71);
	public static Color COLOR_TEXT = Color.white;
	
	protected int textWidth;
	protected BoundingBox internBounding = new BoundingBox(0, 0, 0, 0);
	
	public ControlObject(String name) {
		this.name = name;
		this.drawName = name;
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = 2;

	    textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		
		DockingBox dockBool = new DockingBox(65, 5, 10, 10, 65, 5, RType.BOOLEAN);
		dockings.put(0, dockBool);
		
		DockingBox dockVoid = new DockingBox(25, 20, 100, 10, 25, 20, RType.VOID);
		dockings.put(1, dockVoid);
		
		nextDock = new DockingBox(0, 20, 100, 10, 0, 20, RType.VOID);
		parentDock = new DockingBox(0, -10, MIN_WIDTH, 10, 0, 0, RType.VOID);
		
		updateBounds();
	}
	
	public ControlObject(String name, String drawName) {
		this.name = name;
		this.drawName = drawName;
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = 2;

	    textWidth = ScratchUtils.getTextWidth(drawName, TEXT_FONT);
		
		DockingBox dockBool = new DockingBox(65, 5, 10, 10, 65, 5, RType.BOOLEAN);
		dockings.put(0, dockBool);
		
		DockingBox dockVoid = new DockingBox(25, 20, 100, 10, 25, 20, RType.VOID);
		dockings.put(1, dockVoid);
		
		nextDock = new DockingBox(0, 20, 100, 10, 0, 20, RType.VOID);
		parentDock = new DockingBox(0, -10, MIN_WIDTH, 10, 0, 0, RType.VOID);
		
		updateBounds();
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		this.drawName = name;
		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		updateBounds();
	}

	@Override
	public void render(Graphics g) {
		int x = bounding.getX();
		int y = bounding.getY();
		int w = internBounding.getWidth();
		int h = internBounding.getHeight();
		g.setColor(getPrimaryColor());
		g.fillRect(x + 2, y + 5, w - 3, h - 6);
		g.fillRect(x + 2, y + 2, 9, 3);
		g.drawLine(x + 11, y + 2, x + 11, y + 4);
		g.drawLine(x + 12, y + 3, x + 12, y + 4);
		//g.fillRect(x + 13, y + h - 1, 10, 4);
		g.drawLine(x + 23, y + 3, x + 23, y + 4);
		g.drawLine(x + 24, y + 2, x + 24, y + 4);
		g.fillRect(x + 25, y + 2, w - 26, 3);
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
		
		// Nach Rechts verschobener Anker
		g.drawLine(x + 23, y + 5, x + 23, y + h - 1);
		g.drawLine(x + 24, y + 5, x + 24, y + h + 1);
		g.fillRect(x + 25, y + h - 1, 10, 4);
		g.drawLine(x + 35, y + 5, x + 35, y + h + 1);
		g.drawLine(x + 36, y + 5, x + 36, y + h - 1);
		
		g.setColor(getDrakColor());
		g.drawLine(x + 13, y + h - 1, x + 22, y + h - 1);
		g.drawLine(x + 23, y + h, x + 23, y + h + 1);
		g.drawLine(x + 24, y + h + 2, x + 24, y + h + 2);
		g.drawLine(x + 25, y + h + 3, x + 34, y + h + 3);
		g.drawLine(x + 35, y + h + 2, x + 35, y + h + 2);
		g.drawLine(x + 36, y + h, x + 36, y + h + 1);
		g.drawLine(x + 37, y + h - 1, x + w - 3, y + h - 1);
		g.drawLine(x + w - 2, y + h - 2, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + h - 4, x + w, y + 3);

		g.setColor(getLightColor());
		g.drawLine(x + 1, y + 1, x + 10, y + 1);
		g.drawLine(x + 25, y + 1, x + w - 2, y + 1);
		
		g.setColor(getVLightColor());
		g.drawLine(x + 2, y, x + 10, y);
		g.drawLine(x + 25, y, x + w - 3, y);
		g.drawLine(x + 13, y + 4, x + 22, y + 4);
	    
	    renderLeft(g);
	    renderFoot(g);
		
		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
		int textHeight = 9;
	    g.drawString(drawName, x + 5, y + textHeight + (h - textHeight) / 2);
		
		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null)
				childs.get(i).render(g);
			else
				dockings.get(i).getType().render(g, dockings.get(i).getDockX() + bounding.getX(), dockings.get(i).getDockY() + bounding.getY());
		
		if (next != null)
			next.render(g);
	}
	
	private void renderLeft(Graphics g) {
		int x = bounding.getX() + 1;
		int y = bounding.getY() + internBounding.getHeight() - 3;
		int w = FOOT_HEIGHT - 1;
		int h = bounding.getHeight() - internBounding.getHeight() + 1;
		
		g.setColor(getPrimaryColor());
		g.fillRect(x, y, w, h);
		g.drawLine(x + w, y + 2, x + w, y + 2);
		g.drawLine(x + w, y + h - FOOT_HEIGHT + 1, x + w, y + h - FOOT_HEIGHT + 1);
		
		g.setColor(getDrakColor());
		g.drawLine(x + w, y + 3, x + w, y + 3);
		g.drawLine(x + w - 1, y + 4, x + w - 1, y + h - FOOT_HEIGHT - 1);

		g.setColor(getLightColor());
		g.drawLine(x, y - internBounding.getHeight() + 4, x, y + h - 2);
		g.drawLine(x - 1, y - internBounding.getHeight() + 5, x - 1, y + h - 2);
	}
	
	private void renderFoot(Graphics g) {
		int x = bounding.getX() + 2;
		int y = bounding.getY() + bounding.getHeight() - FOOT_HEIGHT;
		int w = internBounding.getWidth() - 3;
		int h = FOOT_HEIGHT - 1;

		g.setColor(getPrimaryColor());
		g.fillRect(x, y, w, h);
		g.drawLine(x + 9, y + h, x + 22, y + h);
		g.fillRect(x + 10, y + h + 1, 12, 2);
		g.drawLine(x + 11, y + h + 3, x + 20, y + h + 3);
		g.drawLine(x + w, y + h - 3, x + w, y + 1);
		
		g.setColor(getDrakColor());
		g.drawLine(x + 1, y + h, x + 8, y + h);
		g.drawLine(x + 9, y + h + 1, x + 9, y + h + 2);
		g.drawLine(x + 10, y + h + 3, x + 10, y + h + 3);
		g.drawLine(x + 11, y + h + 4, x + 20, y + h + 4);
		g.drawLine(x + 21, y + h + 3, x + 21, y + h + 3);
		g.drawLine(x + 22, y + h + 1, x + 22, y + h + 2);
		g.drawLine(x + 23, y + h, x + w - 2, y + h);
		g.drawLine(x + w - 1, y + h - 1, x + w, y + h - 2);
		g.drawLine(x + w + 1, y + h - 3, x + w + 1, y + 2);
		
		g.setColor(getVLightColor());
		g.drawLine(x + 12, y, x + w - 1, y);
	}
	
	@Override
	public void renderShadow(Graphics g) {
		int x = bounding.getX() + SHADOW_SIZE;
		int y = bounding.getY() + SHADOW_SIZE;
		int w = internBounding.getWidth();
		int h = internBounding.getHeight();
		
		g.setColor(SHADOW_COLOR);
		g.drawLine(x + 1, y + 1, x + 1, y + h - 3);
		g.drawLine(x, y + 2, x, y + h - 4);
		g.fillRect(x + 2, y, 21, h - 1);
		g.drawLine(x + 3, y + h - 1, x + 22, y + h - 1);
		g.drawLine(x + 23, y + 2, x + 23, y + h + 1);
		g.drawLine(x + 24, y + 3, x + 24, y + h + 2);
		g.fillRect(x + 25, y + 4, 10, h);
		g.drawLine(x + 35, y + 3, x + 35, y + h + 2);
		g.drawLine(x + 36, y + 2, x + 36, y + h + 1);
		g.fillRect(x + 37, y, w - 39, h);
		g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + 3, x + w, y + h - 4);
		
		renderShadowLeft(g);
		renderShadowFoot(g);
		
		for (int i = 1; i < maxChilds; i++)
			if (childs.get(i) != null)
				childs.get(i).renderShadow(g);
		
		if (next != null)
			next.renderShadow(g);
	}
	
	private void renderShadowLeft(Graphics g) {
		int x = bounding.getX() + SHADOW_SIZE;
		int y = bounding.getY() + internBounding.getHeight() + SHADOW_SIZE;
		int w = FOOT_HEIGHT;
		int h = bounding.getHeight() - internBounding.getHeight() - 3;
		
		g.fillRect(x, y, w, h);
		g.drawLine(x + w, y, x + w, y);
	}
	
	private void renderShadowFoot(Graphics g) {
		int x = bounding.getX() + FOOT_HEIGHT + SHADOW_SIZE;
		int y = bounding.getY() + bounding.getHeight() - FOOT_HEIGHT + SHADOW_SIZE;
		int w = internBounding.getWidth() - FOOT_HEIGHT;
		int h = FOOT_HEIGHT;

		g.drawLine(x - 11, y + h - 3, x - 2, y + h - 3);
		g.drawLine(x - 10, y + h - 2, x - 2, y + h - 2);
		g.drawLine(x - 9, y + h - 1, x - 2, y + h - 1);
		g.drawLine(x - 1, y, x - 1, y + h + 1);
		g.drawLine(x, y - 1, x, y + h + 2);
		g.fillRect(x + 1, y, 10, h + 4);
		g.drawLine(x + 11, y, x + 11, y + h + 2);
		g.drawLine(x + 12, y, x + 12, y + h + 1);
		g.fillRect(x + 13, y, w - 15, h);
		g.drawLine(x + w - 2, y, x + w - 2, y + h - 2);
		g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + 2, x + w, y + h - 4);
	}
	
	@Override
	public void updateBounds() {
		// Breite berechnen
		int w = START_WIDTH;
		int h = MIN_HEIGHT;
		w += textWidth;
		
		//alle Spacings und Parameter in die Breite mit einrechnen
		for (int i = 0; i < maxChilds - 1; i++) {
			w += PARAMETER_SPACING;
			dockings.get(i).setPosition(w, 0);
			dockings.get(i).setDockX(w);
			
			if (childs.get(i) != null) {
				BoundingBox temp = childs.get(i).getGlobalBounding();
				w += temp.getWidth();
				h = (h < temp.getHeight() + 7) ? temp.getHeight() + 7 : h;
				dockings.get(i).setSize(temp.getWidth(), temp.getHeight());
			}
			else {
				w += dockings.get(i).getType().getWidth();
				h = (h < dockings.get(i).getType().getHeight() + 7) ? dockings.get(i).getType().getHeight() + 7 : h;
				dockings.get(i).setSize(dockings.get(i).getType().getWidth(), dockings.get(i).getType().getHeight());
			}
		}
		w += PARAMETER_SPACING;
		
		// Verticale Ausrichtung der Dockings anpassen
		for (int i = 0; i < maxChilds; i++) {
			dockings.get(i).setPosition(dockings.get(i).getX(), (h - dockings.get(i).getHeight()) / 2);
			dockings.get(i).setDockY((h - dockings.get(i).getHeight()) / 2);
		}
		
		w = (w < MIN_WIDTH) ? MIN_WIDTH : w;
		internBounding.setSize(w, h);
		
		// Gesamte BoundingBox
		h = internBounding.getHeight();
		if (childs.get(1) != null)
			h += childs.get(1).getGlobalBounding().getHeight();
		else
			h += MIN_INTERNAL_HEIGHT;
		
		h += FOOT_HEIGHT;
		bounding.setSize(w, h);
		
		// DockingBoxen berechnen
		parentDock.setSize(w, 10);
		nextDock.setSize(w, 10);
		nextDock.setPosition(0, bounding.getHeight());
		nextDock.setDockY(bounding.getHeight());
		dockings.get(1).setSize(w - 12, 10);
		dockings.get(1).setPosition(12, internBounding.getHeight());
		dockings.get(1).setDockX(12);
		dockings.get(1).setDockY(internBounding.getHeight());
		
		updateParent();
	}
	
	@Override
	public void add(Renderable child, int dockingIndex) {
		if (dockingIndex == 1)
			addIntern(child);
		else
			super.add(child, dockingIndex);
	}
	
	/**
	 * Fügt in einem if-Statement ein Kindobjekt an
	 * @param child
	 */
	private void addIntern(Renderable child) {
		if (childs.get(1) != null) {
			Renderable child2 = childs.get(1);
			child2.removeFromParent();
			
			addIntern(child);
			Renderable tempChild = child.getDeepestVoid();
			tempChild.addAsNext(child2);
		}
			
		childs.put(1, child);
		child.setParent(this);
		updateChilds();
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
	abstract public Renderable clone();

	@Override
	abstract public Object performImplementation(ScratchProgram program) throws FunctionResultException;
}
