/**
 * 
 */
package de.hamster.simulation.view.multimedia.realtimesimulation;

/**
 * @author chris
 * 
 * Datensatz für Positionen
 *
 */
public class MovementSet {

	public float x;
	public float y;
	public float dir;
	public int color;
	public boolean moves = false;

	public MovementSet(MovementSet p) {

		setValues(p.x, p.y, p.dir, p.moves, p.color);
	}

	public MovementSet(int x, int y, int dir, boolean moves, int color) {

		setValues(x, y, dir, moves, color);
	}

	public void setValues(int x, int y, int dir, boolean moves, int color) {
		
		this.x = (float) x;
		this.y = (float) y;
		this.dir = (float) dir;
		this.moves = moves;
		this.color = color;
	}

	public void setValues(float x, float y, float dir, boolean moves, int color) {
		
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.moves = moves;
		this.color = color;
	}

}
