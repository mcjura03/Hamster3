package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hamster.fsm.model.transition.TransitionDescriptionObject;
import de.hamster.fsm.view.FsmAutomataPanel;
import de.hamster.fsm.view.TransitionDescriptionDialog;

/**
 * Wird ausgelöst, wenn im KontextMenu der Dialog zum Ändern der Ei- und Ausgabe
 * einer Transition aufgerufen werden soll.
 * @author Raffaela Ferrari
 *
 */
public class OpenTransitionDescriptionDialogInContextMenu implements ActionListener{
	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private FsmAutomataPanel controller;
	
	/**
	 * speichert den Eltern-Zustand
	 */
	private TransitionDescriptionObject parent;
	
	/**
	 * Konstruktor
	 * @param parent Eltern-Zustand-Object, für das die Änderung ausgeführt werden soll
	 * @param panel Controller, der die Benutzerinteraktion steuert
	 */
	public OpenTransitionDescriptionDialogInContextMenu(TransitionDescriptionObject parent,
			FsmAutomataPanel panel) {
		this.parent = parent;
		this.controller = panel;
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
		TransitionDescriptionDialog.createTransitionDescriptionDialog(this.controller, 
				this.parent);
	}
}
