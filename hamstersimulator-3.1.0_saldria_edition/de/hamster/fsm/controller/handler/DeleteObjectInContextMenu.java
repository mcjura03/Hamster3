package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hamster.fsm.model.RenderableObject;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Wird ausgelöst, wenn im KontextMenu ein Object gelöscht werden soll
 * @author Raffaela Ferrari
 *
 */
public class DeleteObjectInContextMenu implements ActionListener {
	/**
	 * Speichert eine Referenz auf den übergegebenen Controller
	 */
	private ContextMenuPanel controller;
	
	/**
	 * speichert das Element, dass gelöscht werden soll
	 */
	private RenderableObject parent;

	/**
	 * Konstruktor
	 * @param object Eltern-Zustand-Object, für das das Löschen ausgeführt werden soll
	 * @param panel Controller, der die Benutzerinteraktion steuert
	 */
	public DeleteObjectInContextMenu(RenderableObject object, ContextMenuPanel panel) {
		this.parent = object;
		this.controller = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.controller.deleteObject(parent);
	}

}
