package de.hamster.fsm.controller.handler;

import java.awt.event.MouseEvent;

import de.hamster.fsm.controller.FsmMenuMode;
import de.hamster.fsm.view.FsmAutomataPanel;

/**
 * Diese Klasse kapselt im Controller die Reaktion auf Klicken auf den CreateTransistionMode-Button.
 * @author Raffaela Ferrari
 */
public class CreateTransistionHandler extends KlickListenerFsmAutomataMenu{

	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 */
	public CreateTransistionHandler(FsmAutomataPanel con) {
		super(con);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// Controller weiß nun, dass er im CreateTransistionMode ist
		this.getController().setModeType(FsmMenuMode.createTransitionMode);
	}
}
