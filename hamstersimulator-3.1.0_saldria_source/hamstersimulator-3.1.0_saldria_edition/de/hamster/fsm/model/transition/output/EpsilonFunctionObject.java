package de.hamster.fsm.model.transition.output;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.VoidObject;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Klasse, die ein Epsilon-Operand im Output eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class EpsilonFunctionObject extends VoidObject {
	private String renderName = "\u03B5";
	private RoundRectangle2D roundRec;
	private boolean isDisabled = false;

	/**
	 * Konstruktor
	 */
	public EpsilonFunctionObject() {
		super("epsilonFunction");
		this.textWidth = FsmUtils.getTextWidth(renderName, TEXT_FONT);
		this.textHeight = FsmUtils.getTextHeight(TEXT_FONT);
	}

	@Override
	public FsmObject clone() {
		EpsilonFunctionObject clonedEpsilonFunctionObject = new EpsilonFunctionObject();
		clonedEpsilonFunctionObject.setParent(this.parent);
		clonedEpsilonFunctionObject.setCoordinates(this.xStart, this.yStart);
		return clonedEpsilonFunctionObject;
	}

	@Override
	public void updateTextCoordinates() {
		this.textWidth = FsmUtils.getTextWidth(renderName, TEXT_FONT);
		this.textHeight = FsmUtils.getTextHeight(TEXT_FONT);
	}

	@Override
	public Object performImplementation(FsmProgram program) {
		return null;
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem deleteVoid = new JMenuItem(renderName + " l�schen");
		deleteVoid.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteVoid);

		return menuItemList;
	}
	
	@Override
	public boolean isClickedOn(int x, int y) {
		return roundRec.contains(x, y);
	}

	@Override
	public boolean isClickedOnIntegrate(int x, int y) {
		return false;
	}

	@Override
	public void render(Graphics g) {
		int h = HEIGHT+textHeight;
		int w = WIDTH+textWidth;
		Graphics2D g2d = (Graphics2D)g;
		roundRec = new RoundRectangle2D.Double(getXCoordinate(),getYCoordinate(), w, h, 5, 5);
		if(isDisabled) {
			g2d.setColor(Color.LIGHT_GRAY);
		} else {
			g2d.setPaint(COLOR_2);	
		}
		g2d.fill(roundRec);
		if(highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(COLOR_1);	
		}
		g2d.draw(roundRec);
		
		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(renderName, getXCoordinate()+((int)WIDTH/2),getYCoordinate()
				+((int) (HEIGHT+1+textHeight/2)));
	}

	/**
	 * Disabled dieses Objekt für die Eingabefestlegung der Transition
	 * @param shouldDisabled true, fall gedisabled werden soll
	 */
	public void setDisabled(boolean shouldDisabled) {
		this.isDisabled   = shouldDisabled;
	}
}
