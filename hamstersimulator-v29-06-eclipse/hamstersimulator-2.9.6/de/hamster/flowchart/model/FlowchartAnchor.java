package de.hamster.flowchart.model;

import java.awt.Point;

/**
 * Ankerounkte für die FlowchartObjekte
 * 
 * @author gerrit
 * 
 */
public class FlowchartAnchor extends Point {

	/**
	 * 
	 */
	private static final long serialVersionUID = -765400457338207751L;
	private int anchorOriantation;

	/**
	 * Default Konstruktor
	 */
	public FlowchartAnchor() {
		super();
	}

	/**
	 * Konstruktor mit Koordinaten
	 * 
	 * @param x
	 *            die x-Koordinate
	 * @param y
	 *            die y-Koordinate
	 * @param orientation
	 *            die Orientierung
	 */
	public FlowchartAnchor(int x, int y, int orientation) {
		super(x, y);
		this.anchorOriantation = orientation;
	}

	/**
	 * Konstruktor mit Punkt
	 * 
	 * @param p
	 *            der Punkt
	 * @param orientation
	 *            die Orientierung
	 */
	public FlowchartAnchor(Point p, int orientation) {
		super(p);
		this.anchorOriantation = orientation;
	}

	/**
	 * Gibt die Orientierung des Ankerpunktes zurück.
	 * 
	 * @return Integer, der die Orientierung angibt.
	 */
	public int getOrientation() {
		return this.anchorOriantation;
	}

}