package de.hamster.fsm.controller.handler;

import java.awt.event.MouseEvent;

import de.hamster.fsm.controller.FsmMenuMode;
import de.hamster.fsm.view.FsmAutomataPanel;

/**
 * Diese Klasse kapselt im Controller die Reaktion auf Klicken auf den DeleteMode-Button.
 * @author Raffaela Ferrari
 */
public class DeleteModeHandler extends KlickListenerFsmAutomataMenu{

	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 */
	public DeleteModeHandler(FsmAutomataPanel con) {
		super(con);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// Controller weiß nun, dass er im DeleteMode ist
		this.getController().setModeType(FsmMenuMode.deleteMode);
	}
}
