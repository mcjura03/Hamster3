/**
 * 
 */
package de.hamster.simulation.view.multimedia.realtimesimulation;

import java.util.ArrayList;

import de.hamster.simulation.model.Hamster;
import de.hamster.simulation.model.Terrain;

/**
 * @author chris
 * 
 * Der Cache für die diskreten Hamsterpositionen
 *
 */

public class MovementCacheEntry {
	
	private Terrain terrain = null;
	private ArrayList<MovementSet> hamsters = null;
	private long time = 0;
	
	public MovementCacheEntry() {
		this.time = System.currentTimeMillis();
		this.hamsters = new ArrayList<MovementSet>();
	}
	
	public void setTerrain(Terrain t) {	
		this.terrain = new Terrain(t);
	}

	
	public void addHamster(int x, int y, int dir, boolean move, int color) {			
		MovementSet p = new MovementSet(x, y, dir, move, color);
		this.hamsters.add(p);
	}

	public MovementSet getHamster(int i) {
		return this.hamsters.get(i);	
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public int numberOfHamsters() {
		return this.hamsters.size();	
	}
	
}
