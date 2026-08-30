package de.hamster.fsm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;

import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.input.EpsilonBooleanObject;
import de.hamster.fsm.model.transition.output.EpsilonFunctionObject;

/**
 * JPanel, dass für das Hin- und Herschieben von FsmObjects zuständig ist
 * @author Raffaela Ferrari
 *
 */
public class ObjectJPanel extends JPanel{

	protected CopyOnWriteArrayList<FsmObject> objects;
	protected Dimension area;
	
	/**
	 * Konstruktor
	 * @param childs FsmObjects, die im Panel angezeigt werden sollen
	 * @param handler 
	 */
	public ObjectJPanel(LinkedList<FsmObject> childs) {
		setLayout(null);
		area = new Dimension(0,0);
		if(childs != null) {
			this.objects = new CopyOnWriteArrayList<FsmObject>();
			this.objects.addAll(childs);
		} else {
			this.objects  = new CopyOnWriteArrayList<FsmObject>(); 
		} 
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.WHITE); 
		g2d.fillRect(0,0,getWidth(),getHeight());

		int offsetX = 5;
		int offsetY = 5;
		for(FsmObject childInput : this.objects) {
			childInput.setCoordinates(offsetX, offsetY);
			childInput.render(g2d);
			offsetY += childInput.getHeight() + 5;
		}
		updateBounds();
	}
	
	/**
	 * Gibt alle in dem ObjectJPanel gespeicherten Objekte zurück.
	 * @return
	 */
	public CopyOnWriteArrayList<FsmObject> getObjects() {
		return this.objects;
	}
	
	/**
	 * Überprüft, ob ScrollBalken benötigt werden und setzt diese ggf.falls.
	 */
	public void updateBounds() {
		boolean changed = false;
		int farthestX = 0;
		int farthestY = 0;
		for(FsmObject object : this.objects) {
			if(farthestX < (object.getXCoordinate()+object.getWidth())) {
				farthestX = object.getXCoordinate()+object.getWidth();
			}
			if(farthestY < (object.getYCoordinate()+object.getHeight())) {
				farthestY = object.getYCoordinate()+object.getHeight();
			}
		}
		if (farthestX != area.width) {
            area.width = farthestX + 10; changed=true;
        }
        if (farthestY != area.height) {
            area.height = farthestY + 15; changed=true;
        }
		if (changed) {
            setPreferredSize(area);
            revalidate();
        }
	}

	/**
	 * Gibt die Anzahl der Kinder zurück
	 * @return Anzahl der Kindelemente
	 */
	public int getChildSize() {
		return this.objects.size();
	}

	/**
	 * Setzt den isDisabled-Wert des Epsilon-Objectes neu
	 * @param shouldDisabled true, falls es disabled werden soll
	 */
	public void setDisableEpsilonObject(boolean shouldDisabled) {
		if(this.objects.get(0) instanceof EpsilonBooleanObject) {
				((EpsilonBooleanObject)this.objects.get(0)).setDisabled(shouldDisabled);
		} else {
				((EpsilonFunctionObject)this.objects.get(0)).setDisabled(shouldDisabled);
		}
	}
}
