package de.hamster.fsm.controller.handler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.hamster.fsm.view.FsmAutomataPanel;

/**
 * Kapselt die MouseEvent, die auf dem FsmAutomataPanel ausgelöst werden, während es gelockt ist
 * @author Raffaela Ferrari
 *
 */
public class UnlockFsmAutomataPanelHandler implements MouseListener{

	/**
	 * FsmAutomataPanel, in dem die weiterführenden Funktionen ausgeführt werden.
	 */
	FsmAutomataPanel panel;
	
	/**
	 * Konstruktor
	 * @param panel FsmAutomataPanel, welches geunlocked werden soll.
	 */
	public UnlockFsmAutomataPanelHandler(FsmAutomataPanel panel) {
		super();
		this.panel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.panel.setInputFieldValueForObject();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
