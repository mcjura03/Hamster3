package de.hamster.fsm.controller.handler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.hamster.fsm.view.DragAndDropJPanel;
import de.hamster.fsm.view.SelectedObjectsJPanel;

/**
 * Kapselt die MouseEvent, die auf dem DragAndDropPanel ausgelöst werden
 * @author Raffaela Ferrari
 *
 */
public class DragAndDropHandler implements MouseListener, MouseMotionListener{
	/**
	 * speichern den MouseClick-Startpunkt zwischen
	 */
	private int startX;
	private int startY;	

	/**
	 * DragAndDropJPanel, in dem die weiterführenden Funktionen ausgeführt werden
	 */
	DragAndDropJPanel panel;

	/**
	 * Kostruktor
	 * @param panel  DragAndDropJPanel, in dem die weiterführenden Funktionen ausgeführt werden
	 */
	public DragAndDropHandler(DragAndDropJPanel panel) {
		super();
		this.panel = panel;
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		checkPopupMenu(e);
		this.startX = e.getX();
		this.startY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		checkPopupMenu(e);
		this.panel.doDropMode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(!(e.getComponent() instanceof SelectedObjectsJPanel)) {
			boolean hasClickedOnAComponent = this.panel.doDragMode(this.startX, 
					this.startY, e.getX(), e.getY());
			if(hasClickedOnAComponent) {
				this.startX = e.getX();
				this.startY = e.getY();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
//		if (e.getButton() == MouseEvent.BUTTON3) {
//			this.panel.openContextMenu(e.getX(), e.getY());
//		}
		checkPopupMenu(e);
	}
	
	private boolean checkPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.panel.openContextMenu(e.getX(), e.getY());
			return true;
		}
		return false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
