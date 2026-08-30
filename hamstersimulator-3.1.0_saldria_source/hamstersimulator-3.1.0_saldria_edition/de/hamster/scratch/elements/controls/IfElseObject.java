package de.hamster.scratch.elements.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.DockingBox;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.elements.booleans.FalseBooleanObject;
import de.hamster.scratch.elements.booleans.TrueBooleanObject;
import de.hamster.scratch.elements.voids.FunctionResultException;
import de.hamster.workbench.Utils;

public class IfElseObject extends Renderable {
	public static final int MIN_WIDTH = 40;
	public static final int START_WIDTH = 8;
	public static final int MIN_HEIGHT = 20;
	public static final int PARAMETER_SPACING = 5;
	public static final int MIN_INTERNAL_HEIGHT = 11;
	public static final int FOOT_HEIGHT = 12;
	public static final int MIDDLE_HEIGHT = 17;
	public static final Font TEXT_FONT = new Font("Verdana", Font.BOLD, 9);
	public static Color COLOR_PRIMARY = new Color(230, 168, 34);
	public static Color COLOR_DARK = new Color(161, 117, 24);
	public static Color COLOR_LIGHT = new Color(248, 181, 37);
	public static Color COLOR_VLIGHT = new Color(255, 197, 71);
	public static Color COLOR_TEXT = Color.white;
	public static final String TEXT1 = Utils.getElseIf();
	public static final String TEXT2 = Utils.getElse();

	protected int textWidth;
	protected BoundingBox internBounding = new BoundingBox(0, 0, 0, 0);

	public IfElseObject() {
		name = "fallsSonst";
		this.bounding = new BoundingBox(0, 0, MIN_WIDTH, MIN_HEIGHT);
		this.maxChilds = 3;

		int textWidth1 = ScratchUtils.getTextWidth(TEXT1, TEXT_FONT);
		int textWidth2 = ScratchUtils.getTextWidth(TEXT2, TEXT_FONT);
		textWidth = Math.max(textWidth1, textWidth2);

		DockingBox dockBool = new DockingBox(65, 5, 10, 10, 65, 5, RType.BOOLEAN);
		dockings.put(0, dockBool);

		DockingBox dockVoid = new DockingBox(25, 20, 100, 10, 25, 20, RType.VOID);
		dockings.put(1, dockVoid);

		DockingBox dockVoidElse = new DockingBox(25, 40, 100, 10, 25, 40,
				RType.VOID);
		dockings.put(2, dockVoidElse);

		nextDock = new DockingBox(0, 40, 100, 10, 0, 40, RType.VOID);
		parentDock = new DockingBox(0, -10, MIN_WIDTH, 10, 0, 0, RType.VOID);

		updateBounds();
	}

	@Override
	public void updateBounds() {
		// Breite berechnen
		int w = START_WIDTH;
		int h = MIN_HEIGHT;
		w += textWidth;

		// alle Spacings und Parameter in die Breite mit einrechnen
		for (int i = 0; i < maxChilds - 2; i++) {
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

		// Verticale Ausrichtung der Parameter anpassen
		for (int i = 0; i < maxChilds - 2; i++) {
			dockings.get(i).setPosition(dockings.get(i).getX(),
					(h - dockings.get(i).getHeight()) / 2);
			dockings.get(i).setDockY((h - dockings.get(i).getHeight()) / 2);
		}

		w = (w < MIN_WIDTH) ? MIN_WIDTH : w;
		internBounding.setSize(w, h);

		// Gesamte BoundingBox
		h = internBounding.getHeight();
		// If-Block
		if (childs.get(1) != null)
			h += childs.get(1).getGlobalBounding().getHeight();
		else
			h += MIN_INTERNAL_HEIGHT;

		h += MIDDLE_HEIGHT;

		// Else-Block
		if (childs.get(2) != null)
			h += childs.get(2).getGlobalBounding().getHeight();
		else
			h += MIN_INTERNAL_HEIGHT;

		h += FOOT_HEIGHT;
		bounding.setSize(w, h);

		// DockingBoxen berechnen
		parentDock.setSize(w, 10);
		nextDock.setSize(w, 10);
		nextDock.setPosition(0, bounding.getHeight());
		nextDock.setDockY(bounding.getHeight());

		// If-Block
		dockings.get(1).setSize(w - 12, 10);
		dockings.get(1).setPosition(12, internBounding.getHeight());
		dockings.get(1).setDockX(12);
		dockings.get(1).setDockY(internBounding.getHeight());

		// Else-Block
		int ifHeight = internBounding.getHeight() + MIDDLE_HEIGHT;
		if (childs.get(1) != null)
			ifHeight += childs.get(1).getGlobalBounding().getHeight();
		else
			ifHeight += MIN_INTERNAL_HEIGHT;
		dockings.get(2).setSize(w - 12, 10);
		dockings.get(2).setPosition(12, ifHeight);
		dockings.get(2).setDockX(12);
		dockings.get(2).setDockY(ifHeight);

		updateParent();
	}

	@Override
	public void add(Renderable child, int dockingIndex) {
		if (dockingIndex == 1 || dockingIndex == 2)
			addIntern(child, dockingIndex);
		else
			super.add(child, dockingIndex);
	}

	/**
	 * Fügt in einem if-Statement ein Kindobjekt an
	 * 
	 * @param child
	 */
	private void addIntern(Renderable child, int dockingIndex) {
		if (childs.get(dockingIndex) != null) {
			Renderable child2 = childs.get(dockingIndex);
			child2.removeFromParent();

			addIntern(child, dockingIndex);
			Renderable tempChild = child.getDeepestVoid();
			tempChild.addAsNext(child2);
		}

		childs.put(dockingIndex, child);
		child.setParent(this);
		updateChilds();
	}

	@Override
	public String getName() {
		return "fallsSonst";
	}

	@Override
	public Renderable clone() {
		IfElseObject temp = new IfElseObject();
		return temp;
	}

	@Override
	public RType getType() {
		return RType.VOID;
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
		// g.fillRect(x + 13, y + h - 1, 10, 4);
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
		renderMiddle(g);
		renderFoot(g);

		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
		int textHeight = 9;
		g.drawString(TEXT1, x + 5, y + textHeight + (h - textHeight) / 2);

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

	private void renderLeft(Graphics g) {
		int x = bounding.getX() + 1;
		int y = bounding.getY() + internBounding.getHeight() - 3;
		int w = FOOT_HEIGHT - 1;
		int h = bounding.getHeight() - internBounding.getHeight() + 1;

		g.setColor(getPrimaryColor());
		g.fillRect(x, y, w, h);
		g.drawLine(x + w, y + 2, x + w, y + 2);
		g.drawLine(x + w, y + h - FOOT_HEIGHT + 1, x + w, y + h - FOOT_HEIGHT
				+ 1);

		g.setColor(getDrakColor());
		g.drawLine(x + w, y + 3, x + w, y + 3);
		g.drawLine(x + w - 1, y + 4, x + w - 1, y + h - FOOT_HEIGHT - 1);

		g.setColor(getLightColor());
		g.drawLine(x, y - internBounding.getHeight() + 4, x, y + h - 2);
		g.drawLine(x - 1, y - internBounding.getHeight() + 5, x - 1, y + h - 2);
	}

	private void renderMiddle(Graphics g) {
		int x = bounding.getX() + FOOT_HEIGHT - 1;
		int y = bounding.getY() + dockings.get(2).getY() - MIDDLE_HEIGHT;
		int w = internBounding.getWidth() - FOOT_HEIGHT;
		int h = MIDDLE_HEIGHT;

		g.setColor(getPrimaryColor());
		g.fillRect(x, y, w - 1, h);
		g.drawLine(x, y + h, x, y + h);
		g.drawLine(x, y - 1, x + 1, y - 1);
		g.drawLine(x, y - 2, x, y - 2);
		g.fillRect(x + w - 1, y + 1, 2, h - 3);
		g.fillRect(x + 13, y + h, 12, 2);
		g.drawLine(x + 14, y + h + 2, x + 23, y + h + 2);

		g.setColor(getDrakColor());
		g.drawLine(x + 1, y + h, x + 1, y + h);
		g.drawLine(x + 2, y + h - 1, x + 11, y + h - 1);
		g.drawLine(x + 26, y + h - 1, x + w - 2, y + h - 1);
		g.drawLine(x + w - 1, y + h - 2, x + w, y + h - 3);
		g.drawLine(x + w + 1, y + h - 4, x + w + 1, y + 2);
		g.drawLine(x + 12, y + h, x + 12, y + h + 1);
		g.drawLine(x + 25, y + h, x + 25, y + h + 1);
		g.drawLine(x + 13, y + h + 2, x + 13, y + h + 2);
		g.drawLine(x + 24, y + h + 2, x + 24, y + h + 2);
		g.drawLine(x + 14, y + h + 3, x + 23, y + h + 3);

		g.setColor(getLightColor());
		g.drawLine(x + 3, y, x + w - 1, y);

		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
		int textHeight = 7;
		g.drawString(TEXT2, x - FOOT_HEIGHT + 7, y + textHeight
				+ (h - textHeight) / 2);
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
		renderShadowMiddle(g);
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

	private void renderShadowMiddle(Graphics g) {
		int x = bounding.getX() + FOOT_HEIGHT - 1 + SHADOW_SIZE;
		int y = bounding.getY() + dockings.get(2).getY() - MIDDLE_HEIGHT
				+ SHADOW_SIZE;
		int w = internBounding.getWidth() - FOOT_HEIGHT;
		int h = MIDDLE_HEIGHT;

		g.fillRect(x + 1, y, w - 2, h);
		g.drawLine(x + w - 1, y, x + w - 1, y + h - 2);
		g.drawLine(x + w, y + 1, x + w, y + h - 3);
		g.drawLine(x + w + 1, y + 2, x + w + 1, y + h - 4);
		g.drawLine(x + 1, y + h, x + 1, y + h);
		g.fillRect(x + 12, y + h, 14, 2);
		g.drawLine(x + 13, y + h + 2, x + 24, y + h + 2);
		g.drawLine(x + 14, y + h + 3, x + 23, y + h + 3);
	}

	private void renderShadowFoot(Graphics g) {
		int x = bounding.getX() + FOOT_HEIGHT + SHADOW_SIZE;
		int y = bounding.getY() + bounding.getHeight() - FOOT_HEIGHT
				+ SHADOW_SIZE;
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
	public Object performImplementation(ScratchProgram program)
			throws FunctionResultException {
		// If Auswertung
		boolean bool = true;
		if (childs.get(0) != null)
			bool = (Boolean) childs.get(0).perform(program);

		// If ausführen
		if (bool && childs.get(1) != null)
			childs.get(1).perform(program);

		// Else ausführen
		if (!bool && childs.get(2) != null)
			childs.get(2).perform(program);

		return null;
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int layer,
			boolean comment, boolean needsReturn)
			throws FunctionResultException {
		startLine(buffer, layer, comment);
		buffer.append("if (");
		int numReturns = 0;
		boolean reachableBlock = true;
		boolean reachableEnd = true;

		if (childs.get(0) == null) {
			buffer.append("true");
			reachableEnd = false;
		} else {
			childs.get(0).writeSourceCode(buffer, 0, false, false);
			if (childs.get(0).getClass() == TrueBooleanObject.class)
				reachableEnd = false;
			if (childs.get(0).getClass() == FalseBooleanObject.class)
				reachableBlock = false;
		}

		buffer.append(") {" + NEWLINE);

		if (childs.get(1) == null) {
			startLine(buffer, layer + 1, comment);
			buffer.append(NEWLINE);
		} else {
			try {
				childs.get(1).writeSourceCode(buffer, layer + 1, comment,
						needsReturn);
			} catch (FunctionResultException e) {
				numReturns++;
			}
		}

		startLine(buffer, layer, comment);
		buffer.append("} else {" + NEWLINE);

		if (childs.get(2) == null) {
			startLine(buffer, layer + 1, comment);
			buffer.append(NEWLINE);
		} else {
			try {
				childs.get(2).writeSourceCode(buffer, layer + 1, comment,
						needsReturn);
			} catch (FunctionResultException e) {
				numReturns++;
			}
		}

		startLine(buffer, layer, comment);
		buffer.append("}" + NEWLINE);

		if (next != null)
			next.writeSourceCode(buffer, layer, comment, needsReturn);

	}

	// @Override
	// public void writeSourceCode(StringBuffer buffer, int layer, boolean
	// comment, boolean needsReturn) throws FunctionResultException {
	// startLine(buffer, layer, comment);
	// buffer.append("if (");
	// int numReturns = 0;
	// boolean reachableBlock = true;
	// boolean reachableEnd = true;
	//
	// if (childs.get(0) == null) {
	// buffer.append("true");
	// reachableEnd = false;
	// } else {
	// childs.get(0).writeSourceCode(buffer, 0, false, false);
	// if (childs.get(0).getClass() == TrueBooleanObject.class)
	// reachableEnd = false;
	// if (childs.get(0).getClass() == FalseBooleanObject.class)
	// reachableBlock = false;
	// }
	//
	// buffer.append(") {" + NEWLINE);
	//
	// if (reachableBlock) {
	// if (childs.get(1) == null) {
	// startLine(buffer, layer + 1, comment);
	// buffer.append(NEWLINE);
	// } else {
	// try {
	// childs.get(1).writeSourceCode(buffer, layer + 1, comment, needsReturn);
	// } catch (FunctionResultException e) {
	// numReturns++;
	// }
	// }
	// } else {
	// startLine(buffer, layer + 1, comment);
	// buffer.append("// Unreachable Code." + NEWLINE);
	// }
	//
	// startLine(buffer, layer, comment);
	// buffer.append("} else {" + NEWLINE);
	//
	// if (reachableEnd) {
	// if (childs.get(2) == null) {
	// startLine(buffer, layer + 1, comment);
	// buffer.append(NEWLINE);
	// } else {
	// try {
	// childs.get(2).writeSourceCode(buffer, layer + 1, comment, needsReturn);
	// } catch (FunctionResultException e) {
	// numReturns++;
	// }
	// }
	// } else {
	// startLine(buffer, layer + 1, comment);
	// buffer.append("// Unreachable Code." + NEWLINE);
	// }
	//
	// startLine(buffer, layer, comment);
	// buffer.append("}" + NEWLINE);
	//
	// if (numReturns == 2) {
	// // If-Else hat in beiden Teilen ein return
	// // => unreachable Code
	// throw new FunctionResultException(true);
	// }
	//
	// if (next != null)
	// next.writeSourceCode(buffer, layer, comment, needsReturn);
	// else if (needsReturn && layer == 1) {
	// startLine(buffer, layer, comment);
	// buffer.append("return true;" + NEWLINE);
	// }
	// }
}
