package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris
 *  
 * Faces (hier: Dreiecke) für das neue Objektformat. Enthalten die Indizes auf die
 * im Keyframe definierten Vertices und Texturkoordinaten. Zusätzlich hat jedes Face
 * auch einen Facenormal, der die Ausrichtung der Fläche angibt.
 * 
 */

import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

public class Face {

	public Vector3f faceNormal;
		
	public int a, b, c;

	public int texCoordIDA;
	public int texCoordIDB;
	public int texCoordIDC;

	
	public Face(int a, int b, int c) {
		
		this.a = a;
		this.b = b;
		this.c = c;
	
	}
	
	public void setFaceNormal(Vector3f faceNormal) {
		this.faceNormal = faceNormal;
	}
	
	public void setTexCoordsIDs(int a, int b, int c) {
		this.texCoordIDA = a;
		this.texCoordIDB = b;
		this.texCoordIDC = c;
	}

	public int getVertexID(int k) {

		if (k == 0) return this.a;
		if (k == 1) return this.b;
		return this.c;
		
	}

	public void setVertexID(int k, int v) {

		if (k == 0) a = v;
		if (k == 1) b = v;
		if (k == 2) c = v;

		
	}

	
}

