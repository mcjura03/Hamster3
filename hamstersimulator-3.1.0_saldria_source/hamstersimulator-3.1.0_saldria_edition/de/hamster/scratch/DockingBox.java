package de.hamster.scratch;

import de.hamster.scratch.Renderable.RType;

/**
 * Eine DockingBox ist im Grunde eine BoundingBox und
 * agiert auch wie eine und hat dementspprechend auch
 * die gleichen Methoden (ist abgeleitet von der
 * BoundingBox). Jedoch hat die DockingBox eine weitere
 * Koordinate, die die Position des Andockpunktes
 * beinhaltet. Desweiteren hat die DockingBox einen
 * RType, der von der selben Enumeration wie die
 * Renderable RType ist, so dass nur gleiche Typen
 * angedockt werden können.
 * @author HackZ
 *
 */
public class DockingBox extends BoundingBox {
	private int dockX;
	private int dockY;
	private RType rType;
	
	/**
	 * Erstellt eine neue Docking Box mit den übergebenen
	 * Parametern.
	 * @param x
	 * x-Koordinate der BoundingBox.
	 * @param y
	 * y-Koordinate der BoundingBox.
	 * @param width
	 * Breite der BoundingBox.
	 * @param height
	 * Höhe der BoundingBox.
	 * @param dockX
	 * x-Koordinate des Andockpunktes.
	 * @param dockY
	 * y-Koordinate des Andockpunktes.
	 * @param rType
	 * Typ der Elemente, die engedockt werden können.
	 */
	public DockingBox(int x, int y, int width, int height, int dockX, int dockY, RType rType) {
		super(x, y, width, height);
		this.dockX = dockX;
		this.dockY = dockY;
		this.rType = rType;
	}

	/**
	 * Ändert die x-Koordinate des Andockpunktes auf
	 * den übergebenen Wert.
	 * @param dockX
	 */
	public void setDockX(int dockX) {
		this.dockX = dockX;
	}

	/**
	 * Liefert die x-Koordinate des Andockpunktes.
	 * @return
	 */
	public int getDockX() {
		return dockX;
	}

	/**
	 * Ändert die y-Koordinate des Andockpunktes auf
	 * den übergebenen Wert.
	 * @param dockY
	 */
	public void setDockY(int dockY) {
		this.dockY = dockY;
	}

	/**
	 * Liefert die y-Koordinate des Andockpunktes.
	 * @return
	 */
	public int getDockY() {
		return dockY;
	}
	
	/**
	 * Liefert den Typ der Elemente, die angedockt
	 * werden können.
	 * @return
	 */
	public RType getType() {
		return rType;
	}
}
