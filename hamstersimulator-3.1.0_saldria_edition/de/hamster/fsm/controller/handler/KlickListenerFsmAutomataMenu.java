package de.hamster.fsm.controller.handler;

import java.awt.event.MouseAdapter;

import de.hamster.fsm.view.FsmAutomataPanel;


/**
 * Diese Klasse kapselt alle Listener, die MouseAdapter verwenden und zum FsmAutomataMenu gehören.
 * @author Raffaela Ferrari
 */
public abstract class KlickListenerFsmAutomataMenu extends MouseAdapter {
	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private FsmAutomataPanel controller;
	
	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 */
	public KlickListenerFsmAutomataMenu(FsmAutomataPanel con) {
		this.controller=con;
	}

	/**
	 * Gibt den intern verwendeten Controller zurück.
	 * @return Controller für die interne Verwendung
	 */
	public FsmAutomataPanel getController() {
		return this.controller;
	}
}