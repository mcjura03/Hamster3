package de.hamster.simulation.view.multimedia.opengl.objects;

import java.util.ArrayList;

import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

/**
 * @author chris
 * 
 * Keyframe für neues Objektformat. Enthält Listen mit Vertices, Texturkoordinaten und Normals. 
 * Diese werden in den einzelnen Dreiecken (Faces) indiziert.
 *
 */
public class Keyframe {
	
	public ArrayList<Vector3f> vertices;
	
	public ArrayList<Vector3f> tvertices;
	
	public ArrayList<Vector3f> normals;

	public ArrayList<Face> faces;
	
	public int materialID = 0;
	
	public Mesh mesh;
	
	public Keyframe(Mesh m) {
		
		this.mesh = m;
		
		this.vertices = new ArrayList<Vector3f>();
		
		this.tvertices= new ArrayList<Vector3f>();
		
		this.normals= new ArrayList<Vector3f>();
		
		this.faces= new ArrayList<Face>();
	}

	public void addVertex(Vector3f vec) {
		this.vertices.add(vec);
	}

	public void addNormal(Vector3f vec) {
		this.normals.add(vec);
	}
		
	public void addFace(Face f) {
		this.faces.add(f);
	}

	public void addTVertex(Vector3f vec) {
		this.tvertices.add(vec);
		
	}

	public void setMaterialID(int i) {
		this.materialID = i;
		
	}

	public void calcNormals(int typ) {
		
		// flat
		if (typ == 0) {
						
			for (int i = 0; i < this.faces.size(); i++) {
				Vector3f normal = new Vector3f();
				normal.calcNormal3f(vertices
						.get(this.faces.get(i).a), vertices
						.get(this.faces.get(i).b), vertices
						.get(this.faces.get(i).c));

				this.faces.get(i).faceNormal = normal;
				this.faces.get(i).faceNormal.normalize();
				//this.faces.get(i).faceNormal.invert();
			}
			
		} 
		// rund:
		else {
			
			// zuerst alle face normals berechnen: 
			this.calcNormals(0);

			// alle unnötig doppelten vertices entfernen:
			this.trimVertexList();
			
			// wir löschen die smooth normals:
			this.normals = new ArrayList<Vector3f>();
			
			// wir setzen leeren normals:
			for (int n = 0; n < this.vertices.size(); n++) 
				this.normals.add(new Vector3f(0f,0f,0f));
			
			Vector3f fn;
			Face fa;
			
			// nun gehen alle faces durch:
			for (int f = 0; f < this.faces.size(); f++) {
								
				fa = this.faces.get(f);
				fn = fa.faceNormal;
								
				// wir addieren diesen face-normal auf den smoothnormal des betreffenden vertex:
				this.normals.get(fa.a).add(fn);
				
				// auch addieren wir den face-normal auf den normal des nächsten vertex
				this.normals.get(fa.b).add(fn);

				// und... genau! 
				this.normals.get(fa.c).add(fn);
				
				
				
			}
			
			// wir sind alle faces durchgegangen und haben die face-normals jeweils auf
			// die smoothnormnals der vetices aufaddiert. jetzt müssen wür die so entstandenden
			// smooth-normals nur noch normalisieren:
			
			for (int n = 0; n < this.normals.size(); n++) { 
				//this.normals.get(n).invert();
				this.normals.get(n).normalize();
			}
			
		}
		
		
	}

	
	 public void replaceVertex(int v1, int v2) {
    	
    	// v1 löschen
    	this.vertices.remove(v1);
    	
    	// falls v2 einen nach vorne rutscht...
    	if (v2 > v1) v2--;
		
    	// Referenzen updaten
		for (int j = 0; j < this.faces.size(); j++) {
			Face t = this.faces.get(j);
			for (int k = 0; k < 3; k++) {
				if (t.getVertexID(k) == v1) {
					t.setVertexID(k, v2);
				} else if (t.getVertexID(k) > v1) {
					t.setVertexID(k, t.getVertexID(k)-1);
				}
			}
		}
	}
	
	private void trimVertexList() {
	

		for (int i = 0; i < this.faces.size(); i++) {
			Face t1 = this.faces.get(i);
			for (int j = i+1; j < this.faces.size(); j++) {
				Face t2 = this.faces.get(j);

				// doppelte Vertices loeschen
				for (int k = 0; k < 3; k++) {
					Vector3f  v1 = this.vertices.get(t1.getVertexID(k));
					for (int l = 0; l < 3; l++) {
						Vector3f v2 = this.vertices.get(t2.getVertexID(l));
						if (v1 != v2 && v1.equals(v2)) {
							this.replaceVertex(t1.getVertexID(k), t2.getVertexID(l));
						}
					}
				}
			}
			
			
			
			
		}
		
	}

	public int numberOfTriangles() {
		return this.faces.size();
	}

	public Face getFace(int i) {
		return this.faces.get(i);
	}

	public int getMaterialID() {
		return materialID;
	}



}
