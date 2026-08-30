package de.hamster.fsm.controller.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import de.hamster.fsm.view.FsmPanel;

/**
 * Diese Klasse kapselt im Controller die Reaktion auf Klicken auf den Layout-Button
 * bzw. auf den Layout-Menüpunkt.
 * @author Raffaela Ferrari
 */
public class LayoutHandler extends KlickListenerFsmMenu implements ActionListener{

	/**
	 * Konstruktor
	 * @param con Controller, der die Benutzerinteraktion steuert
	 */
	public LayoutHandler(FsmPanel con) {
		super(con);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.getController().layoutGraph();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getController().layoutGraph();
	}
}
