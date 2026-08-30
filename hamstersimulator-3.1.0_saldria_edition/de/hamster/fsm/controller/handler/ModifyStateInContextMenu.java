package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hamster.fsm.model.state.StateObject;
import de.hamster.fsm.view.FsmPanel;

/**
 * Wird ausgelöst, wenn im KontextMenu ein Zustand verändert werden soll.
 * @author Raffaela Ferrari
 *
 */
public class ModifyStateInContextMenu implements ActionListener{
	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private FsmPanel controller;
	
	/**
	 * speichert den Eltern-Zustand
	 */
	private StateObject parent;
	
	/**
	 * Konstruktor
	 * @param parent Eltern-Zustand-Object, für das die Änderung ausgeführt werden soll
	 * @param panel Controller, der die Benutzerinteraktion steuert
	 */
	public ModifyStateInContextMenu(StateObject parent,FsmPanel panel) {
		this.parent = parent;
		this.controller = panel;
	}

	/**
	 * Gibt den intern verwendeten Controller zurück.
	 * @return Controller für die interne Verwendung
	 */
	public FsmPanel getController() {
		return this.controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("final".equals(e.getActionCommand())) {
			this.controller.modifyState(parent, parent.isInitial(), true, parent.getXCoordinate(),
					parent.getYCoordinate());
		} else {
			this.controller.modifyState(parent, true, false, parent.getXCoordinate(),
					parent.getYCoordinate());
		}
	}
}
