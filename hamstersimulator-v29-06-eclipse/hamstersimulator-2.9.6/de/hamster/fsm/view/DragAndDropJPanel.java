package de.hamster.fsm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import de.hamster.fsm.controller.handler.DragAndDropHandler;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.BooleanObject;
import de.hamster.fsm.model.transition.VoidObject;
import de.hamster.fsm.model.transition.input.EpsilonBooleanObject;
import de.hamster.fsm.model.transition.output.EpsilonFunctionObject;

/**
 * JPanel, das das Drag and Drop zwischen zwei Panels simuliert.
 * Dabei kann aus dem einen Panel nur per Drag Elemente hinausgezogen werden, die
 * dann wiederum bei weiteren Drag und späteren Drop im anderen Panel hinzugefügt werden,
 * wenn die Bedingungen dafür erfüllt sind.
 * @author Raffaela Ferrari
 *
 */
public class DragAndDropJPanel extends JPanel{
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	private static final int HEIGHT = 190;
	private static final int WIDTH = 300;
	private static final Border BORDER = BorderFactory.createLoweredBevelBorder();
	
	private ObjectJPanel selectableObjects;
	private SelectedObjectsJPanel selectedObjects;
	private JScrollPane selectableScrollPane;
	private JScrollPane selectedScrollPane;
	private DragAndDropHandler handler;
	private BufferedImage image;
	private boolean isInput;
	private TransitionDescriptionDialog parent;
	
	//Temporäres RenderableElement
	private FsmObject copyOfRenderElement;
	private Object integrateElement;
	private int differenceX = 0;
	private int differenceY = 0;
	private boolean leftElement;

	
	/**
	 * Konstruktor
	 * @param selectable auswählbare Objekte
	 * @param selected die schon vorher selektierten Objekte
	 * @param isInput sagt, ob es sich um ein DragAndDropJPanel für den Input oder den Output handelt.
	 */
	public DragAndDropJPanel(LinkedList<FsmObject> selectable, 
			LinkedList<FsmObject> selected, boolean isInput,
			TransitionDescriptionDialog parent) {
		this.isInput = isInput;
		this.parent = parent;
		setLayout(null);
		handler = new DragAndDropHandler(this);

		if(selected.size() > 0) {
			if(isInput) {
				((EpsilonBooleanObject)selectable.get(0)).setDisabled(true);
			} else {
				((EpsilonFunctionObject)selectable.get(0)).setDisabled(true);
			}
		}
		selectableObjects = new ObjectJPanel(selectable);
		selectableScrollPane = new JScrollPane(selectableObjects);;
		selectableObjects.setBorder(BORDER);
		selectableObjects.addMouseListener(handler);
		selectableObjects.addMouseMotionListener(handler);
		selectableScrollPane.setBounds(0, 0, WIDTH, HEIGHT);
	    add(selectableScrollPane);

	    selectedObjects = new SelectedObjectsJPanel(selected,isInput, this);
	    selectedScrollPane = new JScrollPane(selectedObjects);
	    selectedObjects.setBorder(BORDER);
	    selectedObjects.addMouseListener(handler);
	    selectedObjects.addMouseMotionListener(handler);
	    selectedScrollPane.setBounds(300, 0, WIDTH, HEIGHT);
	    add(selectedScrollPane);
	    
	    setPreferredSize(new Dimension(2*WIDTH, HEIGHT));
	}
	
	/**
	 * Gibt das JPanel für alle für die Auswahl zur Verfügung stehenden Objekte zurück.
	 * @return ObjectJPanel
	 */
	public ObjectJPanel getSelectableObjects() {
		return selectableObjects;
	}

	/**
	 * Gibt das JPanel für alle ausgewählten Objekte zurück.
	 * @return ObjectJPanel
	 */
	public ObjectJPanel getSelectedObjects() {
		return selectedObjects;
	}

	/**
	 * Gibt die JScrollPane für alle ausgewählten Objekte zurück.
	 * @return JScrollPane
	 */
	public JScrollPane getSelectedJScrollPane() {
		return selectedScrollPane;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics g2;
		if ( image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight() ) {
				image = gfxConf.createCompatibleImage(getWidth(), getHeight());
		}
		g2 = image.getGraphics();
		g2.setColor(Color.WHITE); 
		g2.fillRect(0,0,getWidth(),getHeight());

		super.paintComponent(g);
		g.drawImage(image,0,0,null);
	}

	/**
	 * Drag-Methode, die ein Element von einer Position zu einer Position zieht.
	 * @param startX X-Koordinate, von dem aus gezogen wurde.
	 * @param startY Y-Koordinate, von dem aus gezogen wurde.
	 * @param x X-Koordinate, zu dem gezogen wurde.
	 * @param y Y-Koordinate, zu dem gezogen wurde.
	 * @return
	 */
	public boolean doDragMode(int startX, int startY, int x, int y) {
		if(copyOfRenderElement == null) {
			for(FsmObject element : selectableObjects.getObjects()) {
				if(element.isClickedOn(startX, startY)) {
					if(selectedObjects.getChildSize()>0 && (element instanceof EpsilonBooleanObject 
							|| element instanceof EpsilonFunctionObject)){
						return false;
					}
					differenceX = startX - element.getXCoordinate();
					differenceY = startY - element.getYCoordinate();
					copyOfRenderElement = element.clone();
					copyOfRenderElement.setCoordinates(startX + getX() + getParent().getX()-differenceX,
							startY + getY() + getParent().getY()-differenceY);
					this.parent.getDialogGlassPane().setObject(copyOfRenderElement);
					if(!this.parent.getDialogGlassPane().isVisible()) {
						this.parent.getDialogGlassPane().setVisible(true);
					}
					repaint();
					return true;
				}
			}
		} else {
			copyOfRenderElement.setCoordinates(x + getX()+ getParent().getX()-differenceX,
					y + getY() + getParent().getY()-differenceY);
			int shiftX = (int)selectedScrollPane.getViewport().getViewPosition().getX();
			int shiftY = (int)selectedScrollPane.getViewport().getViewPosition().getY();
			showIntegrateElement(x + shiftX - selectedScrollPane.getX(), y + shiftY- selectedScrollPane.getY());
			repaint();
			return true;
		}
		return false;
	}
	
	/**
	/**
	 * Drop-Funktion, die für das Hinzufügen von Elementen verantwortlich ist.
	 * @param x X-Koordinate, an der das Element fallen gelassen wurde.
	 * @param y Y-Koordinate, an der das Element fallen gelassen wurde.
	 */
	public void doDropMode(int x, int y) {
		if(integrateElement != null) {
			if(integrateElement instanceof VoidObject) {
				((VoidObject)integrateElement).setCanIntegrate(false);
			} else if(integrateElement instanceof BooleanObject) {
				((BooleanObject)integrateElement).setCanIntegrate(leftElement, false);	
			}else {
				((SelectedObjectsJPanel)integrateElement).setCanIntegrate(false);
			}
		}
		if(copyOfRenderElement != null && selectedScrollPane.contains(x - selectedScrollPane.getX(),
				y - selectedScrollPane.getY())) {
			int shiftX = (int)selectedScrollPane.getViewport().getViewPosition().getX();
			int shiftY = (int)selectedScrollPane.getViewport().getViewPosition().getY();
			selectedObjects.doDropMode(x - selectedScrollPane.getX() + shiftX ,y
					- selectedScrollPane.getY() + shiftY, copyOfRenderElement);
		}
		integrateElement = null;
		copyOfRenderElement = null;
		this.parent.getDialogGlassPane().setVisible(false);
		differenceX = 0;
		differenceY = 0;
		this.repaint();
	}

	/**
	 * Öffnet ein Kontextmenu an der spezifierten Stelle, wenn dort ein Element vorhanden ist.
	 * @param x X-Koordinate der spezifizierten Stelle.
	 * @param y Y-Koordinate der spezifizierten Stelle.
	 */
	public void openContextMenu(int x, int y) {
		selectedObjects.openContextMenu(x, y);
		this.repaint();
	}

	/**
	 * Highlightet den Bereich, in dem ein Element in die Liste einegefügt wird,
	 * wenn es sich um den Output handelt
	 * @param x X-Koordinate des momentanen Zieh-Punktes
	 * @param y Y-Koordinate des momentanen Zieh-Punktes
	 */
	private void showIntegrateElement(int x, int y) {
		boolean isTheSame = false;
		if(integrateElement != null) {
			if(integrateElement instanceof VoidObject) {
				isTheSame = ((VoidObject)integrateElement).isClickedOnIntegrate(x, y);
				if(!isTheSame) {
					((VoidObject)integrateElement).setCanIntegrate(false);
					integrateElement = null;
				}
			} else if(integrateElement instanceof BooleanObject) {
				if(((BooleanObject)integrateElement).isClickedChildRec(x, y) == integrateElement) {
					isTheSame = ((BooleanObject)integrateElement).isClickedOnChildRecRight(x, y);
					if(leftElement) {
						isTheSame = !isTheSame && ((BooleanObject)integrateElement)
							.isClickedOnChildRec(x, y);
					}
				}
				if(!isTheSame) {
					((BooleanObject)integrateElement).setCanIntegrate(leftElement,false);
					integrateElement = null;
				}
			} else {
				isTheSame = ((SelectedObjectsJPanel)integrateElement).isClickedOnIntegrate(x, y);
				if(!isTheSame) {
					((SelectedObjectsJPanel)integrateElement).setCanIntegrate(false);
					integrateElement = null;
				}
			}
		}
		if(!isTheSame) {
			if(selectedObjects.getObjects().size() == 0 && selectedObjects.isClickedOnIntegrate(x, y)) {
				integrateElement = selectedObjects;
				((SelectedObjectsJPanel)integrateElement).setCanIntegrate(true);
			} else {
				if(!isInput) {
					if(selectedObjects.isClickedOnIntegrate(x, y)) {
						integrateElement = selectedObjects;
						((SelectedObjectsJPanel)integrateElement).setCanIntegrate(true);
					}
					for(FsmObject element : selectedObjects.getObjects()) {
						if(((VoidObject)element).isClickedOnIntegrate(x, y)) {
							integrateElement = element;
							((VoidObject)integrateElement).setCanIntegrate(true);
							break;
						}
					}
				} else {
					for(FsmObject element : selectedObjects.getObjects()) {
						BooleanObject temp = ((BooleanObject)element).isClickedChildRec(x, y);
						if(temp != null) {
							integrateElement = temp;
							if(((BooleanObject)integrateElement).isClickedOnChildRecRight(x, y)) {
								leftElement = false;
							} else {
								leftElement = true;
							}
							((BooleanObject)integrateElement).setCanIntegrate(leftElement,true);
							break;
						}
					}
				}
			}
		}
		repaint();
	}
}
