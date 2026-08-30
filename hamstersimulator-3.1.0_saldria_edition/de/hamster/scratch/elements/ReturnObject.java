package de.hamster.scratch.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.voids.FunctionResultException;

public class ReturnObject extends VoidObject {
	public ReturnObject(ArrayList<RType> parameter) {
		super("return", parameter);
		next = null;
		nextDock = null;
	}

	public ReturnObject(String drawName, ArrayList<RType> parameter) {
		super("return", drawName, parameter);
		next = null;
		nextDock = null;
	}

	@Override
	public void render(Graphics g) {
		super.render(g);

		int x = bounding.getX();
		int y = bounding.getY();
		int h = bounding.getHeight();

		g.setColor(getPrimaryColor());
		g.fillRect(x + 24, y + h - 1, 10, 3);
		g.drawLine(x + 23, y + h + 2, x + 32, y + h + 2);

		g.setColor(getDrakColor());
		g.drawLine(x + 23, y + h + 3, x + 31, y + h + 3);
		g.drawLine(x + 32, y + h + 2, x + 32, y + h + 2);
		g.drawLine(x + 33, y + h + 1, x + 33, y + h);
	}

	@Override
	public void renderShadow(Graphics g) {
		super.renderShadow(g);

		int x = bounding.getX();
		int y = bounding.getY();
		int h = bounding.getHeight();

		g.setColor(SHADOW_COLOR);
		g.fillRect(x + 25 + SHADOW_SIZE, y + h + SHADOW_SIZE, 9, 2);
		g.drawLine(x + 24 + SHADOW_SIZE, y + h + 2 + SHADOW_SIZE, x + 32
				+ SHADOW_SIZE, y + h + 2 + SHADOW_SIZE);
		g.drawLine(x + 23 + SHADOW_SIZE, y + h + 3 + SHADOW_SIZE, x + 31
				+ SHADOW_SIZE, y + h + 3 + SHADOW_SIZE);
	}

	@Override
	public void addAsNext(Renderable child) {
		// Man kann kein weiteres Objekt andocken
	}

	@Override
	public Renderable clone() {
		return new ReturnObject(parameter);
	}

	@Override
	public Object performImplementation(ScratchProgram program)
			throws FunctionResultException {
		throw new FunctionResultException(true);
	}
}
