package de.hamster.fsm.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.RenderableObject;
import de.hamster.fsm.model.transition.BooleanObject;
import de.hamster.fsm.model.transition.VoidObject;

/**
 * JPanel für die selektierten Objekte, die per Drop in diesem Panel hinzugefügt werden
 * sofern die Aktion valide ist.
 * @author Raffaela Ferrari
 *
 */
public class SelectedObjectsJPanel extends ObjectJPanel implements ContextMenuPanel{
	private static final Color BACKGROUND_INPUT = new Color(78, 140, 27);
	private static final Color BACKGROUND_LINE_INPUT = new Color(68, 90, 51);
	private static final Color BACKGROUND_OUTPUT = new Color(45, 66, 175);
	private static final Color BACKGROUND_LINE_OUTPUT = new Color(29, 50, 159);
	
	private boolean isInput = false;
	private DragAndDropJPanel parent;
	private RoundRectangle2D dockingOnParent;
	private boolean canDocking;

	/**
	 * Konstruktor
	 * @param childs Objekte, die schon von vorherein in der Liste stehen sollen
	 * @param input Gibt an, ob es sich um Objekte handelt, die dem Input hinzugefügt werden sollen
	 * oder die Output hinzugefügt werden sollen
	 * @param parent JPanel, dass neu gezeichnet werden soll
	 */
	public SelectedObjectsJPanel(LinkedList<FsmObject> childs,
			boolean input, DragAndDropJPanel parent) {
		super(childs);
		this.isInput = input;
		this.parent = parent;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.WHITE); 
		g2d.fillRect(0,0,getWidth(),getHeight());
		
		int shiftX = (int)parent.getSelectedJScrollPane().getViewport().getViewPosition().getX();
		int shiftY = (int)parent.getSelectedJScrollPane().getViewport().getViewPosition().getY();

		dockingOnParent = new RoundRectangle2D.Double(getX() + shiftX + 10 , getY() + shiftY, 47, 20, 5, 5);
		int offsetX = 10;
		int offsetY = getY() + shiftY;
		for(FsmObject childInput : this.objects) {
			childInput.setCoordinates(offsetX, offsetY);
			offsetY += childInput.getHeight() + 5;
		}
		RoundRectangle2D dockingStation = new RoundRectangle2D.Double(getX() + shiftX + 5, getY() + shiftY,
				57, offsetY, 5, 5);
		if(isInput && this.objects.size() != 0) {
			dockingStation.setRoundRect(getX() + shiftX + 5, getY() + shiftY, this.objects.get(0)
					.getWidth() + 10, offsetY, 5, 5);
		}
		if(offsetY == 0) {
			dockingStation.setRoundRect(getX() + shiftX + 5, getY() + shiftY, 57, 25, 5, 5);
		}
		if(isInput) {
			g2d.setColor(BACKGROUND_INPUT);
			g2d.fill(dockingStation);
			g2d.setColor(BACKGROUND_LINE_INPUT);
			g2d.draw(dockingStation);
		} else {
			g2d.setColor(BACKGROUND_OUTPUT);
			g2d.fill(dockingStation);
			g2d.setColor(BACKGROUND_LINE_OUTPUT);
			g2d.draw(dockingStation);
		}
		for(FsmObject childInput : this.objects) {
			childInput.render(g2d);
		}
		if(canDocking) {
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(dockingOnParent);
			g2d.setColor(Color.GRAY);
			g2d.draw(dockingOnParent);
		}
		updateBounds();
	}

	/**
	 * Drop-Funktion, die für das Hinzufügen von Elementen verantwortlich ist.
	 * @param x X-Koordinate, an der das Element fallen gelassen wurde
	 * @param y Y-Koordinate, an der das Element fallen gelassen wurde
	 * @param element Element, dass hinzugefügt werden soll
	 */
	public void doDropMode(int x, int y, FsmObject element) {
		if(isInput) {
			if(objects.size() == 0) {
				if(isClickedOnIntegrate(x, y)) {
					this.objects.add(element);
					parent.getSelectableObjects().setDisableEpsilonObject(true);
				}
			} else {
				BooleanObject temp = ((BooleanObject)this.objects.get(0)).isClickedChildRec(x, 
						y);
				if(temp != null) {
					if(temp.isClickedOnChildRecRight(x, y)) {
						((BooleanObject)element).setPositioning(1);
					}
					temp.add(element);
					element.setParent(temp);
				}
			}
		} else {
			if(objects.size() == 0) {
				if(isClickedOnIntegrate(x, y)) {
					this.objects.add(element);
					parent.getSelectableObjects().setDisableEpsilonObject(true);
				}
			} else {
				for(FsmObject temp : this.objects) {
					if(((VoidObject)temp).isClickedOnIntegrate(x, y)) {
						int index = this.objects.indexOf(temp);
						this.objects.add(index + 1, element);
						break;
					} else if (isClickedOnIntegrate(x, y)) {
						this.objects.add(0, element);
						break;
					}
				}
			}
		}
	}

	/**
	 * Öffnet ein Kontextmenu an der spezifierten Stelle, wenn dort ein Element vorhanden ist.
	 * @param x X-Koordinate der spezifizierten Stelle.
	 * @param y Y-Koordinate der spezifizierten Stelle.
	 */
	public void openContextMenu(int x, int y) {
		for(FsmObject element : this.objects) {
			FsmObject tmp = element.isClicked(x, y);
			if(tmp != null) {
				openContextMenu(tmp, tmp.getContextMenuItems(this), x, y);
				break;
			}
		}
	}

	/**
	 * Öffnet ein KontextMenü für ein spezifisches Objekt mit bestimmten JMenuItem an einer
	 * spezifizierten Position.
	 * @param tmp Objekt, für das das KontextMenu geöffnet werden soll
	 * @param contextMenuItems JMenuItems, die im Kontextmenu angezeigt werden sollen.
	 * @param x X-Koordinate der spezifizierten Stelle.
	 * @param y Y-Koordinate der spezifizierten Stelle.
	 */
	private void openContextMenu(FsmObject tmp,
			List<JMenuItem> contextMenuItems, int x, int y) {
		if(contextMenuItems != null) {
			JPopupMenu popup = new JPopupMenu();
			for (JMenuItem menuItem : contextMenuItems) {
				popup.add(menuItem);
			}
			// Menü anzeigen
			popup.show(this, x, y);
		}
	}

	/**
	 * Löscht ein Objekt aus der Objekt-Liste.
	 */
	@Override
	public void deleteObject(RenderableObject object) {
		boolean isRemoved = this.objects.remove(object);
		if(!isRemoved) {
			((FsmObject) object).getParentRenderable()
				.removeChildFromParent((FsmObject) object);
		}
		if(this.objects.size() == 0) {
			parent.getSelectableObjects().setDisableEpsilonObject(false);
		}
		parent.repaint();
	}

	/**
	 * Überprüft ob der Punkt im Dockingbereich liegt.
	 * @param x X-Koordinate des zu überprüfenden Punktes
	 * @param y Y-Koordinate des zu überprüfenden Punktes
	 * @return true, wenn der Punkt im Dockingbereich liegt.
	 */
	public boolean isClickedOnIntegrate(int x, int y) {
		if(dockingOnParent != null) {
			return dockingOnParent.contains(x,y);
		} else {
			return false;
		}
	}

	/**
	 * Soll die dazugehörige Variable setzen, damit klar ist ob der Dockingbereich
	 * mitgezeichnet werden soll.
	 * @param canIntegrate true, wenn der Dockingbereich mitgezeichnet werden soll.
	 */
	public void setCanIntegrate(boolean canDocking) {
		this.canDocking = canDocking;
	}
}
