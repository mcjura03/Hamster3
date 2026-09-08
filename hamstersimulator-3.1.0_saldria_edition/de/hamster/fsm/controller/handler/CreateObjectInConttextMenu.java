package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hamster.fsm.view.FsmAutomataPanel;

/**
 * Wird ausgelöst, wenn im KontextMenu etwas erzeugt werden soll
 * @author Raffaela Ferrari
 *
 */
public class CreateObjectInConttextMenu implements ActionListener{
	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private FsmAutomataPanel controller;
	
	/**
	 * speichert die Koordinaten
	 */
	private int x;
	private int y;
	
	/**
	 * Konstruktor
	 * @param x x-Koordinate, an der das Object erstellt werden soll
	 * @param y y-Koordinate, an der das Object erstellt werden soll
	 * @param panel Controller, der die Benutzerinteraktion steuert
	 */
	public CreateObjectInConttextMenu(FsmAutomataPanel panel, int x, int y) {
		this.controller = panel;
		this.x = x;
		this.y = y;
	}

	/**
	 * Gibt den intern verwendeten Controller zurück.
	 * @return Controller für die interne Verwendung
	 */
	public FsmAutomataPanel getController() {
		return this.controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("state".equals(e.getActionCommand())) {
			this.controller.getPanel().createState(x,y);
		} else if ("transition".equals(e.getActionCommand())) {
			this.controller.setCreateTransitionField(100, 100);
		} else {
			this.controller.setLocked(true);
			this.controller.setInputField(null,x,y);
		}
	}

}
