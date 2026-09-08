package de.hamster.flowchart.view;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import de.hamster.flowchart.FlowchartUtil;

/**
 * Ein Button/Schalter um den Transitions Modus an und aus zu schalten.
 * 
 * @author gerrit
 * 
 */
public class ArrowModeButton implements MouseListener {

	private BufferedImage highlight;
	private BufferedImage background;
	private BufferedImage image;
	private Point coordinates;
	private int width;
	private int height;

	/**
	 * Der Konstruktor holt sich den aktuellen aktive Wert vom FlowchartUtil.
	 */
	public ArrowModeButton() {
		this.image = FlowchartUtil.getImage("flowchart/arrow.png");
		this.background = FlowchartUtil
				.getImage("flowchart/arrow_background.png");
		this.highlight = FlowchartUtil
				.getImage("flowchart/arrow_highlight.png");
		this.coordinates = new Point();
		this.width = 90;
		this.height = 60;

	}

	/**
	 * Stellt den Aktive/Inaktive Modus um.
	 */
	public void toggle() {
		FlowchartUtil.TRANSITIONMODE = !FlowchartUtil.TRANSITIONMODE;
	}

	public void draw(Graphics g) {
		if (FlowchartUtil.TRANSITIONMODE) {
			g.drawImage(this.highlight, this.coordinates.x, this.coordinates.y,
					null);
		} else {
			g.drawImage(this.background, this.coordinates.x,
					this.coordinates.y, null);
		}
		g.drawImage(this.image, this.coordinates.x, this.coordinates.y, null);
	}

	/**
	 * Setzt die Koordinaten des Buttons.
	 * 
	 * @param x
	 *            Die x Koordinate
	 * @param y
	 *            Die y Koordinate
	 */
	public void setCoords(int x, int y) {
		this.coordinates.x = x;
		this.coordinates.y = y;
	}

	/**
	 * Gibt die Koordinaten vom Button zurück.
	 * 
	 * @return Point mit den Koordinaten.
	 */
	public Point getCoords() {
		return coordinates;
	}

	/**
	 * Gibt die Breite des Buttons zurück.
	 * 
	 * @return Integer mit der Breite.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Gibt die Höhe des Buttons zurück.
	 * 
	 * @return Intger mit der Höhe.
	 */
	public int getHeight() {
		return this.height;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.toggle();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
