package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris
 * 
 * Keyframeklase für altes Meshformat. Enthält eine Liste an Dreiecken.
 */

import java.util.ArrayList;


public class KeyframeAlt {

	private ArrayList<Triangle> triangles;

	public KeyframeAlt () {
		this.triangles = new ArrayList<Triangle>();
	}
	
	public void addTriangle(Triangle t) {
		this.triangles.add(t);
	}

	public int numberOfTriangles() {
		return triangles.size();
	}

	public Triangle getTriangle(int i) {
		return this.triangles.get(i);
	}
}
