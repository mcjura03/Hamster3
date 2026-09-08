package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hamster.fsm.model.RenderableObject;
import de.hamster.fsm.view.FsmAutomataPanel;

/**
 * Wird ausgelöst, wenn im KontextMenu etwas umbenannt werden soll.
 * @author Raffaela Ferrari
 *
 */
public class RenameObjectInContextMenu implements ActionListener{

	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private FsmAutomataPanel controller;
	
	/**
	 * speichert den Eltern-Zustand
	 */
	private RenderableObject parent;
	
	/**
	 * speichert die Koordinaten
	 */
	private int x;
	private int y;
	
	/**
	 * Konstruktor
	 * @param panel Controller, der die Benutzerinteraktion steuert
	 * @param parent RenderableObject, für den das Umbenennen stattfinden soll.
	 * @param x x-Koordinate, an der das Texteingabefeld erstellt werden soll
	 * @param y y-Koordinate, an der das Texteingabefeld erstellt werden soll
	 */
	public RenameObjectInContextMenu(RenderableObject parent, FsmAutomataPanel panel, int x, int y) {
		this.controller = panel;
		this.parent = parent;
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
		this.controller.setTempObject(parent);
		this.controller.setLocked(true);
		this.controller.setInputField(parent.getName(),x,y);
	}
}
