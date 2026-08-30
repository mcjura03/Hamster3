package de.hamster.scratch.elements.controls;

import java.awt.Graphics;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.elements.ControlObject;
import de.hamster.scratch.elements.booleans.TrueBooleanObject;
import de.hamster.scratch.elements.voids.FunctionResultException;
import de.hamster.workbench.Utils;

public class DoWhileObject extends ControlObject {
	public static final int HEADER_HEIGHT = 17;
	public static final String TEXT1 = Utils.getDoWhile();
	public static final String TEXT2 = Utils.getDo();

	public DoWhileObject() {
		super("solange");
		int textWidth1 = ScratchUtils.getTextWidth(TEXT1, TEXT_FONT);
		int textWidth2 = ScratchUtils.getTextWidth(TEXT2, TEXT_FONT);
		textWidth = Math.max(textWidth1, textWidth2);
		updateBounds();
	}

	@Override
	public String getName() {
		return "tueSolange";
	}

	@Override
	public Renderable clone() {
		DoWhileObject temp = new DoWhileObject();
		return temp;
	}

	@Override
	public void updateBounds() {
		// Breite berechnen
		int w = START_WIDTH;
		int h = MIN_HEIGHT;
		w += textWidth;

		// alle Spacings und Parameter in die Breite mit einrechnen
		for (int i = 0; i < maxChilds - 1; i++) {
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

		// Gesamte BoundingBox
		int fullHeight = HEADER_HEIGHT;
		if (childs.get(1) != null)
			fullHeight += childs.get(1).getGlobalBounding().getHeight();
		else
			fullHeight += MIN_INTERNAL_HEIGHT;
		fullHeight += h;

		// Verticale Ausrichtung der Parameter anpassen
		for (int i = 0; i < maxChilds; i++) {
			dockings.get(i).setPosition(dockings.get(i).getX(),
					fullHeight - h + (h - dockings.get(i).getHeight()) / 2);
			dockings.get(i).setDockY(
					fullHeight - h + (h - dockings.get(i).getHeight()) / 2);
		}

		w = (w < MIN_WIDTH) ? MIN_WIDTH : w;
		internBounding.setSize(w, h);
		internBounding.setPosition(0, fullHeight - h);

		bounding.setSize(w, fullHeight);

		// DockingBoxen berechnen
		parentDock.setSize(w, 10);
		nextDock.setSize(w, 10);
		nextDock.setPosition(0, bounding.getHeight());
		nextDock.setDockY(bounding.getHeight());
		dockings.get(1).setSize(w - 12, 10);
		dockings.get(1).setPosition(12, HEADER_HEIGHT);
		dockings.get(1).setDockX(12);
		dockings.get(1).setDockY(HEADER_HEIGHT);

		updateParent();
	}

	@Override
	public void render(Graphics g) {
		int x = bounding.getX();
		int y = bounding.getY();
		int w = internBounding.getWidth();
		int h = HEADER_HEIGHT;
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
		renderFoot(g);

		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
		int textHeight = 9;
		g.drawString(TEXT2, x + 5, y + textHeight + (h - textHeight) / 2);

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
		int y = bounding.getY() + HEADER_HEIGHT - 3;
		int w = FOOT_HEIGHT - 1;
		int h = bounding.getHeight() - HEADER_HEIGHT + 1;

		g.setColor(getPrimaryColor());
		g.fillRect(x, y, w, h);
		g.drawLine(x + w, y + 2, x + w, y + 2);
		g.drawLine(x + w, y + h - internBounding.getHeight() + 1, x + w, y + h
				- internBounding.getHeight() + 1);

		g.setColor(getDrakColor());
		g.drawLine(x + w, y + 3, x + w, y + 3);
		g.drawLine(x + w - 1, y + 4, x + w - 1,
				y + h - internBounding.getHeight() - 1);

		g.setColor(getLightColor());
		g.drawLine(x, y - HEADER_HEIGHT + 4, x, y + h - 2);
		g.drawLine(x - 1, y - HEADER_HEIGHT + 5, x - 1, y + h - 2);
	}

	private void renderFoot(Graphics g) {
		int x = bounding.getX() + 2;
		int y = bounding.getY() + bounding.getHeight()
				- internBounding.getHeight();
		int w = internBounding.getWidth() - 3;
		int h = internBounding.getHeight() - 1;

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

		g.setColor(COLOR_TEXT);
		g.setFont(TEXT_FONT);
		int textHeight = 9;
		g.drawString(TEXT1, x + 3, y + textHeight + (h - textHeight) / 2);
	}

	@Override
	public void renderShadow(Graphics g) {
		int x = bounding.getX() + SHADOW_SIZE;
		int y = bounding.getY() + SHADOW_SIZE;
		int w = internBounding.getWidth();
		int h = HEADER_HEIGHT;

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
		int y = bounding.getY() + HEADER_HEIGHT + SHADOW_SIZE;
		int w = FOOT_HEIGHT;
		int h = bounding.getHeight() - HEADER_HEIGHT - 3;

		g.fillRect(x, y, w, h);
		g.drawLine(x + w, y, x + w, y);
	}

	private void renderShadowFoot(Graphics g) {
		int x = bounding.getX() + FOOT_HEIGHT + SHADOW_SIZE;
		int y = bounding.getY() + bounding.getHeight()
				- internBounding.getHeight() + SHADOW_SIZE;
		int w = internBounding.getWidth() - FOOT_HEIGHT;
		int h = internBounding.getHeight();

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
	public Object performImplementation(ScratchProgram program)
			throws FunctionResultException {
		while (true) {
			// Bug Fix (Bei einer leeren Schlefe, soll auf
			// Programmstopp geachtet werden
			if (program.checkStop()) {
				return false;
			}

			// Schleife ausführen
			if (childs.get(1) != null)
				childs.get(1).perform(program);

			// While Auswertung
			boolean bool = true;
			if (childs.get(0) != null)
				bool = (Boolean) childs.get(0).perform(program);
			if (!bool)
				break;
		}
		return null;
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int layer,
			boolean comment, boolean needsReturn)
			throws FunctionResultException {
		startLine(buffer, layer, comment);
		buffer.append("do {");
		boolean reachableEnd = true;

		if (childs.get(1) == null) {
			startLine(buffer, layer + 1, comment);
			buffer.append(NEWLINE);
		} else {
			buffer.append(NEWLINE);
			try {
				childs.get(1).writeSourceCode(buffer, layer + 1, comment,
						needsReturn);
			} catch (FunctionResultException e) {
			}
		}

		startLine(buffer, layer, comment);
		buffer.append("} while (");

		if (childs.get(0) == null) {
			buffer.append("true");
			reachableEnd = false;
		} else {
			childs.get(0).writeSourceCode(buffer, 0, false, needsReturn);
			if (childs.get(0).getClass() == TrueBooleanObject.class)
				reachableEnd = false;
		}

		buffer.append(");" + NEWLINE);

		// dibo 19.01.2011
		/*
		 * if (!reachableEnd) { // Unreachable Code throw new
		 * FunctionResultException(true); }
		 */

		if (next != null)
			next.writeSourceCode(buffer, layer, comment, needsReturn);
		else {
			// dibo 19.01.2011
			/*
			if (needsReturn && layer == 1) {
				startLine(buffer, layer, comment);
				buffer.append("return true;" + NEWLINE);
			}
			*/
		}
	}
}
