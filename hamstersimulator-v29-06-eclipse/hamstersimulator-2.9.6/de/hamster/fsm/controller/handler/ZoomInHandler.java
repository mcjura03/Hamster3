package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import de.hamster.fsm.view.FsmPanel;

/**
 * Diese Klasse kapselt im Controller die Reaktion auf Klicken auf den ZoomIn-Button
 * bzw. ZoomIn-Menüpunkt.
 * @author Raffaela Ferrari
 */
public class ZoomInHandler extends KlickListenerFsmMenu implements ActionListener{

	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 */
	public ZoomInHandler(FsmPanel con) {
		super(con);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.getController().zoomIn();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getController().zoomIn();
	}
}
