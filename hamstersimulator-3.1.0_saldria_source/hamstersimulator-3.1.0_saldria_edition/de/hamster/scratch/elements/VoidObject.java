package de.hamster.scratch.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.DockingBox;
import de.hamster.scratch.Method;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.elements.voids.FunctionResultException;

public class VoidObject extends Renderable {
	public static final int MIN_WIDTH = 40;
	public static final int START_WIDTH = 8;
	public static final int MIN_HEIGHT = 20;
	public static final int PARAMETER_SPACING = 5;
	public static final int MIN_INTERNAL_HEIGHT = 10;
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 9);
	public static Color COLOR_PRIMARY = new Color(74, 108, 212);
	public static Color COLOR_DARK = new Color(52, 76, 148);
	public static Color COLOR_LIGHT = new Color(80, 118, 230);
	public static Color COLOR_VLIGHT = new Color(114, 145, 237);
	public static Color COLOR_TEXT = Color.white;

	protected int textWidth;
	protected int textHeight;
	protected ArrayList<RType> parameter;

	public VoidObject(String name, ArrayList<RType> parameter) {
		this.parameter = parameter;
		this.name = name;
		this.drawName = name;
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = parameter.size();

		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight(name, TEXT_FONT);

		for (int i = 0; i < parameter.size(); i++) {
			DockingBox dockTemp = new DockingBox(0, 0, 0, 0, 0, 0,
					parameter.get(i));
			dockings.put(i, dockTemp);
		}

		nextDock = new DockingBox(0, 20, MIN_WIDTH, 10, 0, MIN_HEIGHT,
				RType.VOID);
		parentDock = new DockingBox(0, -10, MIN_WIDTH, 10, 0, 0, RType.VOID);

		updateBounds();
	}

	public VoidObject(String name, String drawName, ArrayList<RType> parameter) {
		this.parameter = parameter;
		this.name = name;
		this.drawName = drawName;
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = parameter.size();

		textWidth = ScratchUtils.getTextWidth(drawName, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight(drawName, TEXT_FONT);

		for (int i = 0; i < parameter.size(); i++) {
			DockingBox dockTemp = new DockingBox(0, 0, 0, 0, 0, 0,
					parameter.get(i));
			dockings.put(i, dockTemp);
		}

		nextDock = new DockingBox(0, 20, MIN_WIDTH, 10, 0, MIN_HEIGHT,
				RType.VOID);
		parentDock = new DockingBox(0, -10, MIN_WIDTH, 10, 0, 0, RType.VOID);

		updateBounds();
	}

	@Override
	public void setName(String name) {
		this.name = name;
		this.drawName = name;
		textWidth = ScratchUtils.getTextWidth(name, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight(name, TEXT_FONT);
		updateBounds();
	}

	public void initDrawable() {
		textWidth = ScratchUtils.getTextWidth(drawName, TEXT_FONT);
		textHeight = ScratchUtils.getTextHeight(drawName, TEXT_FONT);
		updateBounds();
	}

	@Override
	public void render(Graphics g) {
		initDrawable();
		int x = bounding.getX();
		int y = bounding.getY();
		int w = bounding.getWidth();
		int h = bounding.getHeight();
		g.setColor(getPrimaryColor());
		g.fillRect(x + 2, y + 5, w - 3, h - 6);
		g.fillRect(x + 2, y + 2, 9, 3);
		g.drawLine(x + 11, y + 2, x + 11, y + h - 1);
		g.drawLine(x + 12, y + 3, x + 12, y + h + 1);
		g.fillRect(x + 13, y + h - 1, 10, 4);
		g.drawLine(x + 23, y + 3, x + 23, y + h + 1);
		g.drawLine(x + 24, y + 2, x + 24, y + h - 1);
		g.fillRect(x + 25, y + 2, w - 26, 3);
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);

		g.setColor(getDrakColor());
		g.drawLine(x + 3, y + h - 1, x + 10, y + h - 1);
		g.drawLine(x + 11, y + h, x + 11, y + h + 1);
		g.drawLine(x + 12, y + h + 2, x + 12, y + h + 2);
		g.drawLine(x + 13, y + h + 3, x + 22, y + h + 3);
		g.drawLine(x + 23, y + h + 2, x + 23, y + h + 2);
		g.drawLine(x + 24, y + h, x + 24, y + h + 1);
		g.drawLine(x + 25, y + h - 1, x + w - 3, y + h - 1);
		g.drawLine(x + w - 2, y + h - 2, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + h - 4, x + w, y + 3);

		g.setColor(getLightColor());
		g.drawLine(x + 1, y + 1, x + 1, y + h - 3);
		g.drawLine(x + 1, y + 1, x + 10, y + 1);
		g.drawLine(x, y + 2, x, y + h - 4);
		g.drawLine(x + 25, y + 1, x + w - 2, y + 1);

		g.setColor(getVLightColor());
		g.drawLine(x + 2, y, x + 10, y);
		g.drawLine(x + 25, y, x + w - 3, y);
		g.drawLine(x + 13, y + 4, x + 22, y + 4);

		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
		g.drawString(drawName, x + 5, y + textHeight + (h - textHeight) / 2);

		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null)
				childs.get(i).render(g);
			else
				dockings.get(i)
						.getType()
						.render(g,
								dockings.get(i).getDockX() + bounding.getX(),
								dockings.get(i).getDockY() + bounding.getY());

		if (next != null)
			next.render(g);
	}

	@Override
	public void renderShadow(Graphics g) {
		int x = bounding.getX() + SHADOW_SIZE;
		int y = bounding.getY() + SHADOW_SIZE;
		int w = bounding.getWidth();
		int h = bounding.getHeight();

		g.setColor(SHADOW_COLOR);
		g.drawLine(x + 1, y + 1, x + 1, y + h - 3);
		g.drawLine(x, y + 2, x, y + h - 4);
		g.fillRect(x + 2, y, 9, h - 1);
		g.drawLine(x + 3, y + h - 1, x + 10, y + h - 1);
		g.drawLine(x + 11, y + 2, x + 11, y + h + 1);
		g.drawLine(x + 12, y + 3, x + 12, y + h + 2);
		g.fillRect(x + 13, y + 4, 10, h);
		g.drawLine(x + 23, y + 3, x + 23, y + h + 2);
		g.drawLine(x + 24, y + 2, x + 24, y + h + 1);
		g.fillRect(x + 25, y, w - 27, h);
		g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
		g.drawLine(x + w, y + 3, x + w, y + h - 4);

		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null)
				childs.get(i).renderShadow(g);

		if (next != null)
			next.renderShadow(g);
	}

	public void updateBounds() {
		// Breite berechnen
		int w = START_WIDTH;
		int h = MIN_HEIGHT;
		w += textWidth;

		// alle spacings und parameter in die breite mit einrechnen
		for (int i = 0; i < maxChilds; i++) {
			w += PARAMETER_SPACING;
			dockings.get(i).setPosition(w, 0);
			dockings.get(i).setDockX(w);

			if (childs.get(i) != null) {
				BoundingBox temp = childs.get(i).getGlobalBounding();
				w += temp.getWidth();
				h = (h < temp.getHeight() + 7) ? temp.getHeight() + 7 : h;
				dockings.get(i).setSize(temp.getWidth(), temp.getHeight());
			} else {
				w += dockings.get(i).getType().getWidth();
				h = (h < dockings.get(i).getType().getHeight() + 7) ? dockings
						.get(i).getType().getHeight() + 7 : h;
				dockings.get(i).setSize(dockings.get(i).getType().getWidth(),
						dockings.get(i).getType().getHeight());
			}
		}
		w += PARAMETER_SPACING;

		// Verticale Ausrichtung der Dockings anpassen
		for (int i = 0; i < maxChilds; i++) {
			dockings.get(i).setPosition(dockings.get(i).getX(),
					(h - dockings.get(i).getHeight()) / 2);
			dockings.get(i).setDockY((h - dockings.get(i).getHeight()) / 2);
		}

		w = (w < MIN_WIDTH) ? MIN_WIDTH : w;
		bounding.setSize(w, h);

		// DockingBoxen berechnen
		parentDock.setSize(w, 10);
		if (nextDock != null) {
			nextDock.setSize(w, 10);
			nextDock.setPosition(0, bounding.getHeight());
			nextDock.setDockY(bounding.getHeight());
		}

		updateParent();
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
		Renderable temp = new VoidObject(name, drawName, parameter);
		return temp;
	}

	@Override
	public Object performImplementation(ScratchProgram program)
			throws FunctionResultException {
		Method method = program.getProgram().getVoidMethod(name);
		program.nexMethod(name);

		try {
			Renderable first = method.getRootElement().getNext();
			if (first != null)
				first.perform(program);
		} catch (FunctionResultException res) {
			return res;
		} finally {
			program.endMmethod();
		}
		return null;
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int layer,
			boolean comment, boolean needsReturn)
			throws FunctionResultException {
		startLine(buffer, layer, comment);
		buffer.append(this.name + "();" + NEWLINE);

		if (next != null)
			next.writeSourceCode(buffer, layer, comment, needsReturn);
		else if (needsReturn && layer == 1) {
			startLine(buffer, layer, comment);
			buffer.append("return true;" + NEWLINE);
		}
	}
}
