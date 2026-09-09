package de.hamster.fsm.controller.handler;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.hamster.fsm.view.FsmAutomataPanel;

/**
 * Kapselt die MouseEvent, die auf dem FsmAutomataPanel ausgelöst werden
 * 
 * @author Raffaela Ferrari
 * 
 */
public class FsmAutomataPanelHandler implements MouseListener,
		MouseMotionListener {
	/**
	 * speichern den MouseClick-Startpunkt zwischen
	 */
	private int startX;
	private int startY;

	/**
	 * speichert die Differenz beim Verschieben vom links oberen Punkt zum
	 * Klick-Punkt
	 */
	private int differenceX = 0;
	private int differenceY = 0;

	/**
	 * Temporäre Objekte
	 */
	private int x = -1;
	private int y = -1;

	/**
	 * FsmAutomataPanel, in dem die weiterführenden Funktionen ausgeführt
	 * werden
	 */
	FsmAutomataPanel panel;

	/**
	 * Kostruktor
	 * 
	 * @param panel
	 *            FsmAutomataPanel, in dem die weiterführenden Funktionen
	 *            ausgeführt werden
	 */
	public FsmAutomataPanelHandler(FsmAutomataPanel panel) {
		super();
		this.panel = panel;

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		checkPopupMenu(e);
		if (this.x == -1 && this.y == -1) {
			this.x = e.getX();
			this.y = e.getY();
		}
		// if (e.getButton() == MouseEvent.BUTTON3) {
		// this.panel.openContextMenu(e.getX(), e.getY());
		// this.x = -1;
		// this.y = -1;
		// } else
		if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
			if (this.x != -1 && this.y != -1) {
				this.panel.addObjectsToMoveObjects(this.x, this.y);
			} else {
				this.panel.addObjectsToMoveObjects(e.getX(), e.getY());
			}
			this.x = -1;
			this.y = -1;
		} else if (e.getButton() == MouseEvent.BUTTON1
				&& e.getClickCount() % 2 != 0) {
			this.panel.doClickMode(e);
		} else if (e.getButton() == MouseEvent.BUTTON1
				&& e.getClickCount() % 2 == 0) {
			this.panel.doDoubleClickMode(e);
			this.x = -1;
			this.y = -1;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		checkPopupMenu(e);
		this.startX = e.getX();
		this.startY = e.getY();
		if (!e.isControlDown()) {
			Point difference = this.panel.doPressMode(this.startX, this.startY);
			if (difference != null) {
				this.differenceX = (int) difference.getX();
				this.differenceY = (int) difference.getY();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		checkPopupMenu(e);
		if (!(e.getButton() == MouseEvent.BUTTON1 && e.isControlDown())) {
			this.panel.doDropMode(this.startX - this.differenceX, this.startY
					- this.differenceY, e.getX() - this.differenceX, e.getY()
					- this.differenceY);
			this.differenceX = 0;
			this.differenceY = 0;
		}
	}

	private boolean checkPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.panel.openContextMenu(e.getX(), e.getY());
			this.x = -1;
			this.y = -1;
			return true;
		}
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		boolean hasClickedOnAComponent = false;
		hasClickedOnAComponent = this.panel.doDragMode(this.startX
				- this.differenceX, this.startY - this.differenceY, e.getX()
				- this.differenceX, e.getY() - this.differenceY);
		if (hasClickedOnAComponent) {
			this.startX = e.getX();
			this.startY = e.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.panel.doHoverMode(e.getX(), e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
