package de.hamster.fsm.controller.handler;

import java.awt.event.MouseEvent;

import de.hamster.fsm.view.FsmPanel;

/**
 * Diese Klasse kapselt im Controller die Reaktion auf Klicken auf den TypeOfFsmMode-Button.
 * @author Raffaela Ferrari
 */
public class TypeOfFsmHandler extends KlickListenerFsmMenu{
	
	/**
	 * gibt an, ob der endliche Automat nichtdeterministisch ist
	 */
	boolean isNondeterministic;

	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 * @param b 
	 */
	public TypeOfFsmHandler(FsmPanel con, boolean b) {
		super(con);
		this.isNondeterministic = b;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.getController().setTypeOfFsm(this.isNondeterministic);
	}
}
