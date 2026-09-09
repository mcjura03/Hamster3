package de.hamster.fsm.controller.handler;

import java.awt.event.MouseAdapter;

import de.hamster.fsm.view.FsmPanel;

/**
 * Diese Klasse kapselt alle Listener, die MouseAdapter verwenden und zum FsmMenu gehören.
 * @author Raffaela Ferrari
 */
public abstract class KlickListenerFsmMenu extends MouseAdapter{
	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private FsmPanel controller;
	
	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 */
	public KlickListenerFsmMenu(FsmPanel con) {
		this.controller=con;
	}

	/**
	 * Gibt den intern verwendeten Controller zurück.
	 * @return Controller für die interne Verwendung
	 */
	public FsmPanel getController() {
		return this.controller;
	}
}
