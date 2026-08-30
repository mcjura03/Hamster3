package de.hamster.fsm.model.transition.input;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.model.transition.BooleanObject;

/**
 * Klasse, die einen Nicht-Operator für das Objekt im Input eines endlichen
 * Automaten repräsentiert.
 * 
 * @author Raffaela Ferrari
 * 
 */
public class NotObject extends BooleanObject {

	/**
	 * Konstruktor
	 * 
	 * @param positioning
	 *            Gibt an, ob das Objekt rechts oder links in einem anderen
	 *            BooleanObjekt positioniert werden soll.
	 */
	public NotObject(int positioning) {
		super("nicht", positioning);
	}

	@Override
	public FsmObject clone() {
		NotObject clonedNotObject = new NotObject(this.positioning);
		clonedNotObject.setChilds(this.childs);
		clonedNotObject.setParent(this.parent);
		clonedNotObject.setCoordinates(this.xStart, this.yStart);
		return clonedNotObject;
	}

	@Override
	public int getWidth() {
		int child0 = RECWIDTH;
		if (this.childs.size() > 0) {
			child0 = this.childs.get(0).getWidth();
		}
		return (int) child0 + textWidth + 3 * PADDING;
	}

	@Override
	public int getHeight() {
		int maxChildHeight = RECHEIGHT;
		if (this.childs.size() > 0) {
			maxChildHeight = this.childs.get(0).getHeight();
		}
		return (int) maxChildHeight + 2 * PADDING;
	}

	@Override
	public void render(Graphics g) {
		int child0Width = RECWIDTH;
		int child0Height = RECHEIGHT;
		if (this.childs.size() > 0) {
			child0Width = this.childs.get(0).getWidth();
			child0Height = this.childs.get(0).getHeight();
			this.childs.get(0).setCoordinates(
					this.xStart + 2 * PADDING + textWidth,
					this.yStart + PADDING);
		}
		Graphics2D g2d = (Graphics2D) g;

		Stroke stroke = new BasicStroke((float) (1 * ZOOMFACTOR));
		g2d.setStroke(stroke);

		roundRec = new RoundRectangle2D.Double(this.xStart, this.yStart,
				child0Width + textWidth + 3 * PADDING, child0Height + 2
						* PADDING, 5, 5);
		g2d.setPaint(COLOR_2);
		g2d.fill(roundRec);
		if (highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(COLOR_1);
		}
		g2d.draw(roundRec);

		roundRecRight = new RoundRectangle2D.Double(this.xStart + 2 * PADDING
				+ textWidth, this.yStart + PADDING, child0Width, child0Height,
				2, 2);
		if (this.canIntegrate) {
			g2d.setColor(INTEGRATE_COLOR);
		} else {
			g2d.setColor(BACKGROUND);
		}
		g2d.fill(roundRecRight);
		g2d.setColor(BACKGROUND_LINE);
		g2d.draw(roundRecRight);

		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(this.name, this.xStart + PADDING, this.yStart
				+ (child0Height + 2 * PADDING) / 2 + textHeight / 2 - PADDING);
		if (this.childs.size() > 0) {
			this.childs.get(0).render(g);
		}
	}

	@Override
	public Object checkPerform(FsmProgram program) {
		// Boolesche Variable auswerten
		boolean booleanVariable = true;
		if (this.childs.size() == 1) {
			booleanVariable = (Boolean) ((BooleanObject) this.childs.get(0))
					.checkPerform(program);
		}
		return (!booleanVariable);
	}

	@Override
	public Object performImplementation(FsmProgram program)
			throws IsNondeterministicException {
		// Boolesche Variable auswerten
		boolean booleanVariable = true;
		if (this.childs.size() == 1) {
			booleanVariable = (Boolean) childs.get(0).perform(program);
		}
		return (!booleanVariable);
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int indentation) {
		startLine(buffer, indentation);
		buffer.append("(");
		buffer.append("!");
		if (this.childs.size() == 1) {
			((BooleanObject) this.childs.get(0)).writeSourceCode(buffer, 0);
		} else {
			buffer.append(true);
		}
		buffer.append(")");
	}

	@Override
	public void loadProgramm(Element booleanElement) {
		// Lade alle Attribute
		this.positioning = Integer.parseInt(booleanElement
				.getAttribute("positioning"));

		Element child0 = null;
		NodeList children = booleanElement.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node currentChild = children.item(i);

			switch (currentChild.getNodeType()) {

			case Node.ELEMENT_NODE:
				child0 = (Element) children.item(i);
				break;
			}
		}

		if (child0 != null) {
			Element booleanMethodChild0 = (Element) child0
					.getElementsByTagName("booleanObject").item(0);
			if (booleanMethodChild0 != null) {
				String name = booleanMethodChild0.getAttribute("name");
				BooleanObject booleanObject = (BooleanObject) FsmUtils
						.getTransistionDescriptionELementByName(name);
				booleanObject.loadProgramm(booleanMethodChild0);
				booleanObject.setParent(this);
				add(booleanObject);
			}
		}
	}
}
