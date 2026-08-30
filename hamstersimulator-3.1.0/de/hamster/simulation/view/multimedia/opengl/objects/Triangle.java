package de.hamster.simulation.view.multimedia.opengl.objects;

/** 
 * @author chris
 * 
 * Verwaltet ein Dreieck. (für alte Meshversion)
 */

import de.hamster.simulation.view.multimedia.opengl.math.Vector2f;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

public class Triangle {

	private Vector3f[] vertices = new Vector3f[3];

	private Vector3f[] normals = new Vector3f[3];

	private Vector2f[] textureCoords = new Vector2f[3];

	private float[] color = new float[3];

	private int material = -1;

	public Triangle(Vector3f a, Vector3f b, Vector3f c) {
		this.vertices[0] = a;
		this.vertices[1] = b;
		this.vertices[2] = c;
		this.color[0] = 1.0f;
		this.color[1] = 1.0f;
		this.color[2] = 1.0f;

		this.textureCoords = new Vector2f[] { new Vector2f(0.0f, 0.0f),
				new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f) };

		this.normals[0] = new Vector3f();
		this.normals[1] = new Vector3f();
		this.normals[2] = new Vector3f();

		this.setFaceNormals();
	}

	public void setFaceNormals() {

		Vector3f a = new Vector3f(this.vertices[1].getX()
				- this.vertices[0].getX(), this.vertices[1].getY()
				- this.vertices[0].getY(), this.vertices[1].getZ()
				- this.vertices[0].getZ());

		Vector3f b = new Vector3f(this.vertices[2].getX()
				- this.vertices[0].getX(), this.vertices[2].getY()
				- this.vertices[0].getY(), this.vertices[2].getZ()
				- this.vertices[0].getZ());

		this.normals[0].crossProduct(a, b);
		this.normals[0].normalize();
		this.normals[1].crossProduct(a, b);
		this.normals[1].normalize();
		this.normals[2].crossProduct(a, b);
		this.normals[2].normalize();

	}

	public void setTexCoords(Vector2f v1, Vector2f v2, Vector2f v3) {
		this.textureCoords[0] = v1;
		this.textureCoords[1] = v2;
		this.textureCoords[2] = v3;

	}

	public void setMaterialID(int materialID) {
		this.material = materialID;

	}

	public Vector3f getVertex(int i) {

		return this.vertices[i];
	}

	public Vector3f getNormal(int i) {
		return this.normals[i];
	}

	public Vector2f getTexCoord(int i) {
		return this.textureCoords[i];
	}

}
