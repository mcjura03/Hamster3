package de.hamster.fsm.view;

import java.awt.Graphics;

import javax.swing.JComponent;

import de.hamster.fsm.model.FsmObject;

/**
 * GlassPane. um das DragAndDrop-Object zu malen.
 * @author Raffaela Ferrari
 *
 */
public class DragAndDropGlassPane extends JComponent{
	FsmObject dragAndDropObject = null;

	/**
	 * Kosntruktor
	 */
	public DragAndDropGlassPane() {
		super();
	}

	/**
	 * Setzt das FsmObject, welches gezeichnet werden soll. 
	 * @param dragAndDropObject FsmObject, welches gezeichnet werden soll. 
	 */
	public void setObject(FsmObject dragAndDropObject) {
		this.dragAndDropObject = dragAndDropObject;
	}

	@Override
	public void paintComponent(Graphics g) {
		if(this.dragAndDropObject != null) {
			dragAndDropObject.render(g);
		}
	}
}
