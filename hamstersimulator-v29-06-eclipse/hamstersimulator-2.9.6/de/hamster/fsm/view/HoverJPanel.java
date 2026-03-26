package de.hamster.fsm.view;

import java.awt.Graphics;
import javax.swing.JPanel;

import de.hamster.fsm.model.FsmObject;

/**
 * JPanel für das Hovern von Input und Output-Objekten
 * @author Raffaela Ferrari
 *
 */
public class HoverJPanel extends JPanel{
	FsmObject element;
	
	/**
	 * Konstruktor
	 * @param element Element, welches gehovert werden soll
	 */
	public HoverJPanel(FsmObject element) {
		super();
		this.element = element;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		element.setCoordinates(5, 5);
		element.render(g);
	}
}
