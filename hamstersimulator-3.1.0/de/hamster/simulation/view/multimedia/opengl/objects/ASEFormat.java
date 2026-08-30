package de.hamster.simulation.view.multimedia.opengl.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import de.hamster.simulation.view.multimedia.opengl.material.Color;
import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.material.MaterialController;
import de.hamster.simulation.view.multimedia.opengl.material.Texture;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

/**
 * @author chris
 *
 * Laderoutinen für das ASE Format, wie es aus 3D-Studio MAX exportiert werden kann.
 */
public class ASEFormat {
	
	private static ASEFormat instance = null;
	
	private static File f = null; 
	private static BufferedReader br = null;
	private static ArrayList<Mesh> list = null;
	private static String line = new String();
	private static Keyframe k = null;
	private static Mesh m = null;
	private static MaterialController mc;
	private static int startMaterialID = 0;
	   
	protected ASEFormat() {
	   	   
	}	

	public static ASEFormat getInstance() {
		if(instance == null) {
			instance = new ASEFormat();
		}
		return instance;
	}
	
	
	private static String readLine() {
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void openFile(String filename) {
		f = new File(filename);
		try {
			
			br = new BufferedReader(new FileReader(f));
			
		} catch (FileNotFoundException e) { e.printStackTrace(); }	
	}
	
	/*public static ArrayList<Mesh> load(String filename, MaterialController mcontroller) {

		openFile(filename);
		
		mc = mcontroller;
				
		list = new ArrayList<Mesh>();
		
		startMaterialID = mc.materials.size();
		
		parseFile();
				
		return list;
	}
	
	*/
/*	private static void parseFile() {	
				
		line = readLine();
		
		// wir gehen die datei durch. dabei ignorieren wir unnötige teile und parsen nur
		// die interessanten:
		while (line != null) {
			
			if (line.contains("*MATERIAL_LIST {")) parseMaterials();
			if (line.contains("*GEOMOBJECT {")) {
				
				m = new Mesh(mc);
				list.add(m);
				k = new Keyframe(m);
				m.addKeyframe(k);
				
				parseGeomObject(); 
				
				//m.calcNormals(1);
			}
		
			line = readLine();
		}
	}
	*/
	   
	private static void parseMaterials() {
				
		// wir zählen die geschweiften klammern mit:
		int blockCounter = 0;
			
		// wir loopen, bis wir mit return ganz raus gehen:
		while (true) {
			// aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;
			
			if (line.contains("*MATERIAL_COUNT")) { }
			if (line.contains("*MATERIAL ")) {
				
				Material m = new Material();
				parseMaterial(m);
				mc.defineAndRequestMaterial( m );					
			}

			// es gibt durch das parsen eine neue line, also aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;			
			
			// nun noch die nächste zeile lesen:
			line = readLine();			
		}
		
		
	}
	
	private static void parseGeomObject() {
		
		// wir zählen die geschweiften klammern mit:
		int blockCounter = 0;
		
		// wir loopen, bis wir mit return ganz raus gehen:
		while (true) {

			// aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;
			
			// wir lesen ein:
			
			if (line.contains("*NODE_NAME")) { }
	
			if (line.contains("*MESH {")) parseMesh();
			if (line.contains("*MATERIAL_REF")) {
				
				line.trim();
				String[] fields = line.split("\\s+");
				fields[2].trim();				

				Integer i = new Integer(fields[2]);
				k.setMaterialID(i.intValue() + startMaterialID);
			}			

			// es gibt durch das parsen eine neue line, also aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;
			
			// sonst die nächste zeile lesen:
			line = readLine();			
		}
		
		
	}
	
	private static void parseMesh() {
		
		// wir zählen die geschweiften klammern mit:
		int blockCounter = 0;
		
		// wir loopen, bis wir mit return ganz raus gehen:
		while (true) {
			// aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;

			if (line.contains("*MESH_VERTEX_LIST {")) { parseMeshVertexList(); }
			if (line.contains("*MESH_FACE_LIST {")) { parseMeshFaceList(); }
			if (line.contains("*MESH_TVERTLIST {")) { parseMeshTextureVertexList(); }
			if (line.contains("*MESH_TFACELIST {")) { parseMeshTextureFaceList(); }
			if (line.contains("*MESH_NORMALS {")) { parseMeshNormals(); }
						
			//es es gibt durch das parsen eine neue line, also aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;
			
			// sonst die nächste zeile lesen:
			line = readLine();			
		}
		
		
	}
	
	private static void parseMeshVertexList() {
		
		Vector3f vec;
		Float x;
		Float y;
		Float z;
		
		while (true) {	
			if (line.contains("}")) return;
			
			if (line.contains("*MESH_VERTEX ")) {
				// *MESH_VERTEX    0	-52.747246	-0.523170	-55.870491
				line.trim();
				String[] fields = line.split("\\s+");
				fields[0].trim();
				fields[1].trim();
				fields[2].trim();
				fields[3].trim();
				fields[4].trim();
				fields[5].trim();
								
				x = new Float(fields[3]);
				y = new Float(fields[4]);
				z = new Float(fields[5]);
				
				// opengl und 3dsmax haben andere koordinatensysteme, daher z und y austauschen:
				vec = new Vector3f (x.floatValue(), z.floatValue(), y.floatValue() *-1 ); //xxx
				k.addVertex(vec);
			}			
			line = readLine();
		}				
	}
	
	private static void parseMeshTextureVertexList() {
		
		Vector3f vec;
		Float x;
		Float y;
		Float z;
		
		while (true) {	
			if (line.contains("}")) return;
			
			if (line.contains("*MESH_TVERT ")) {
				// *MESH_TVERT    0	-52.747246	-0.523170	-55.870491
				line.trim();
				String[] fields = line.split("\\s+");
				fields[0].trim();
				fields[1].trim();
				fields[2].trim();
				fields[3].trim();
				fields[4].trim();
				fields[5].trim();
								
				x = new Float(fields[3]);
				y = new Float(fields[4]);
				z = new Float(fields[5]);
				
				// opengl und 3dsmax haben andere koordinatensysteme, daher z und y austauschen:
				vec = new Vector3f (x.floatValue(), y.floatValue(), z.floatValue());
				k.addTVertex(vec);
			}			
			line = readLine();
		}				
	}
	
	private static void parseMeshFaceList() {
		
		Face f;
		
		while (true) {	
			if (line.contains("}")) return;
			
			if (line.contains("*MESH_FACE ")) {
				//*MESH_FACE    0:    A:  307 B:  308 C:  309 AB:    1 BC:    1 CA:    0	 *MESH_SMOOTHING 1 	*MESH_MTLID 0
				line.trim();
				String[] fields = line.split("\\s+");
				
				fields[4].trim();								
				fields[6].trim();				
				fields[8].trim();
//				fields[18].trim();
				
				Integer x = new Integer(fields[4]);
				Integer y = new Integer(fields[6]);
				Integer z = new Integer(fields[8]);
//				Integer m = new Integer(fields[18]);
				f = new Face(x.intValue(), y.intValue(), z.intValue());	
				k.addFace(f);			
			}			
			line = readLine();
		}				
	}
	
	private static void parseMeshTextureFaceList() {
		
		while (true) {	
			if (line.contains("}")) return;
			
			if (line.contains("*MESH_TFACE ")) {
				// *MESH_TFACE 1958	692	1157	1292
				line.trim();
				String[] fields = line.split("\\s+");
				
				fields[2].trim();								
				fields[3].trim();				
				fields[4].trim();
				fields[5].trim();
				
				Integer a = new Integer(fields[2]);
				Integer x = new Integer(fields[3]);
				Integer y = new Integer(fields[4]);
				Integer z = new Integer(fields[5]);

				k.faces.get(a).setTexCoordsIDs(x, y, z);
			
			}			
			line = readLine();
		}				
	}
	
	
	private static void parseMeshNormals() {
		
		k.normals = new ArrayList<Vector3f>(k.vertices);
		
		while (true) {	
			if (line.contains("}")) return;
			
			if (line.contains("*MESH_FACENORMAL ")) {
				//*MESH_FACENORMAL 0	0.532846	-0.809478	-0.246619
				line.trim();
				String[] fields = line.split("\\s+");
											
				fields[2].trim();				
				fields[3].trim();				
				fields[4].trim();
				fields[5].trim();		
				
				Float x = new Float(fields[3]);
				Float y = new Float(fields[4]);
				Float z = new Float(fields[5]);
				
				Integer face = new Integer(fields[2]);
								
				k.faces.get(face.intValue()).setFaceNormal(new Vector3f(z.floatValue()*-1f, y.floatValue(), x.floatValue()  )); // xxx
				
			} else if (line.contains("*MESH_VERTEXNORMAL ")) {
				
				//*MESH_FACENORMAL 0	0.532846	-0.809478	-0.246619
				line.trim();
				String[] fields = line.split("\\s+");
											
				fields[2].trim();				
				fields[3].trim();				
				fields[4].trim();
				fields[5].trim();		
				
				Float x = new Float(fields[3]);
				Float y = new Float(fields[4]);
				Float z = new Float(fields[5]);
				
				Integer vertex = new Integer(fields[2]);
				k.normals.set(vertex, new Vector3f(z.floatValue()*-1f, y.floatValue(), x.floatValue()));
			}
			
			line = readLine();
		}		
		
	}
	
	
	
	private static void parseMaterial(Material m) {
				
		// wir zählen die geschweiften klammern mit:
		int blockCounter = 0;
		
		// wir loopen, bis wir mit return ganz raus gehen:
		while (true) {
			// aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;

			if (line.contains("*MATERIAL_AMBIENT")) {
				line.trim();String[] fields = line.split("\\s+");		
				fields[2].trim(); fields[3].trim();fields[4].trim();						
				Float r = new Float(fields[2]);
				Float g = new Float(fields[3]);
				Float b = new Float(fields[4]);
				m.setAmbient(new Color(r,g,b));
			}
			if (line.contains("*MATERIAL_DIFFUSE ")) {
				line.trim();String[] fields = line.split("\\s+");		
				fields[2].trim(); fields[3].trim();fields[4].trim();						
				Float r = new Float(fields[2]);
				Float g = new Float(fields[3]);
				Float b = new Float(fields[4]);
				m.setDiffuse(new Color(r,g,b));
			}
			if (line.contains("*MATERIAL_SPECULAR ")) {
				line.trim();String[] fields = line.split("\\s+");		
				fields[2].trim(); fields[3].trim();fields[4].trim();						
				Float r = new Float(fields[2]);
				Float g = new Float(fields[3]);
				Float b = new Float(fields[4]);
				m.setSpecular(new Color(r,g,b));
			}
			if (line.contains("*MATERIAL_SHINE ")) { 
				line.trim();String[] fields = line.split("\\s+");		
				fields[2].trim(); 						
				Float r = new Float(fields[2]);
				m.setShininess(r);
			}
			if (line.contains("*MATERIAL_SELFILLUM ")) {
				line.trim();String[] fields = line.split("\\s+");		
				fields[2].trim(); 						
				Float r = new Float(fields[2]);
				Color c = new Color(m.getDiffuse());
				c.setValue(0, c.getValue(0)* r);
				c.setValue(1, c.getValue(1)* r);
				c.setValue(2, c.getValue(2)* r);
				m.setEmission(c);
			}
			
			if (line.contains("*MAP_DIFFUSE ")) parseTexture(m); 
						
			//es es gibt durch das parsen eine neue line, also aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;
			
			// sonst die nächste zeile lesen:
			line = readLine();			
		}
	}

	private static void parseTexture(Material m) {
		
		// wir zählen die geschweiften klammern mit:
		int blockCounter = 0;
		
		// wir loopen, bis wir mit return ganz raus gehen:
		while (true) {
			// aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;

			if (line.contains("*BITMAP ")) {
				
				line.trim();String[] fields = line.split("\\s+");		
				fields[2].trim(); 		 						
				String s = new String(fields[2].substring(1, fields[2].length()-1));			
				Texture t = new Texture(s);
				mc.defineTexture(t);
				m.addTexture(mc.getLastTextureIndex());
			}
					
			//es es gibt durch das parsen eine neue line, also aktualisieren:
			if (line.contains("{")) blockCounter ++;
			if (line.contains("}")) blockCounter --;
			if (blockCounter == 0) return;
			
			// sonst die nächste zeile lesen:
			line = readLine();			
		}
	}

	public static void addKeyframe(String filename, MaterialController mc2, Obj obj) {
		mc = mc2;
		openFile(filename);
		line = readLine();
		int submesh = 0;
		
		while (line != null) {
			
			if (line.contains("*GEOMOBJECT {")) {
				
				// testen, ob es das aktuelle submesh im array schon gibt, sonst erstellen:
				if (obj.meshes.size() <= submesh) { 
					m = new Mesh(obj, mc);
					obj.meshes.add(m);
				}
				m = obj.meshes.get(submesh);
				
				// auf jeden fall einen neuen keyframe erstellen und einfügen:
				k = new Keyframe(m);
				m.addKeyframe(k);
				
				parseGeomObject();
				submesh++;
				
			}
		
			line = readLine();
		}
		
	}

	public static void loadMaterials(String filename, MaterialController mc2) {
		openFile(filename);		
		mc = mc2;
		line = readLine();		
		startMaterialID = mc.materials.size();
		while (line != null) {			
			if (line.contains("*MATERIAL_LIST {")) parseMaterials();
			line = readLine();
		}		
	}
	

}
