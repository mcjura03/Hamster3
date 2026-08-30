package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris
 * 
 * Heighmapklasse zur Darstellung zufällig generierter Landschaften.
 * 
 * Erzeugt erst eine zufällige Hightmap und wandelt diese dann in ein entsprechendes Mesh um.
 * auf Wunsch werden die Kanten der Felder mit Rahmen versehen, was beim SPielfeld für den
 * Hamster benutzt wird.
 * 
 */

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;
import de.hamster.simulation.view.multimedia.opengl.material.Color;
import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.material.MaterialController;
import de.hamster.simulation.view.multimedia.opengl.math.Vector2f;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

public class Heightmap {
	
	private Mesh m;
	private float map[];
	private int mapSizeX;
	private int mapSizeY;
	private float cellSize;
	private boolean showGrid;
	private int border;
	private Color c1;
	private Color c2;
		
	public Heightmap(MaterialController mc, int xs, int ys, float cellSize, float maxheight, int border, float dropFrame) {		
		
		m = new Mesh(null, mc);
				
		this.c1 = new Color (0.7f, 0.7f, 0.7f);
		this.c2 = new Color (0.0f, 0.0f, 0.0f);
				
		Keyframe k = new Keyframe(m);
		this.mapSizeX = xs + (border*2);
		this.mapSizeY = ys + (border*2);
		this.border = border;
		this.cellSize = cellSize;
		
		float hx = (mapSizeX * cellSize)/2f;
		float hy = (mapSizeY * cellSize)/2f;
				
		this.map = new float[mapSizeX*mapSizeY];
				
		for (int y=0; y<mapSizeY; y++) {
			
			for (int x=0; x<mapSizeX; x++) { 
				
				map[x+(y*mapSizeX)] = (float) (Math.random() * maxheight);
				
				if (dropFrame > 0f) {		
				
						if (x == 0) map[x+(y*mapSizeX)] = -dropFrame;
					else 
						if (x == mapSizeX-1) map[x+(y*mapSizeX)] = -dropFrame;				                
					else 
						if (y == 0) map[x+(y*mapSizeX)] = -dropFrame;										
					else 
						if (y == mapSizeY-1) map[x+(y*mapSizeX)] = -dropFrame;				                
				}				                			
			}
		}
	
		// nun alles etwas weichzeichnen:
		for (int x=1; x<mapSizeX-1; x++) {
			for (int y=1; y<mapSizeY-1; y++) {

				float f = 0f;
				for (int b=-1;b<2;b++) {
					for (int a=-1;a<2;a++) {
						f+= map[(x+a)+((y+b)*mapSizeX)];
					}					
				}
				map[x+(y*mapSizeX)] += f/15f;
				 
	
			}
		}
			
		Color c;
		Vector3f v;
		Face f; 
		int vindex=-1;
		int tindex=0;
		
		for (int ypos=0; ypos<(this.mapSizeY-1); ypos++) {
		
			for (int xpos=0; xpos<(this.mapSizeX-1); xpos++) {			
			       	  
				 // unten links: 
				float x = (float) xpos * this.cellSize;
				float y = this.map[(ypos*this.mapSizeX)+xpos];
				float z = ypos * this.cellSize;
				v = new Vector3f(x-hx, y, z-hy);
				k.addVertex(v);
				vindex++;
				
				 // oben links: 
				x = (float) xpos * this.cellSize;
				y = this.map[((ypos+1)*this.mapSizeX)+xpos];
				z = (ypos+1) * this.cellSize;
				v = new Vector3f(x-hx, y, z-hy);
				k.addVertex(v);
				vindex++;
				
				 // oben rechts: 
				x = (float) (xpos+1) * this.cellSize;
				y = this.map[((ypos+1)*this.mapSizeX)+(xpos+1)];
				z = (ypos+1) * this.cellSize;		     
				v = new Vector3f(x-hx, y, z-hy);
				k.addVertex(v);
				vindex++;
				
				 // unten rechts: 
				x = (float) (xpos+1) * this.cellSize;
				y = this.map[(ypos*this.mapSizeX)+(xpos+1)];
				z = ypos * this.cellSize;	
				v = new Vector3f(x-hx, y, z-hy);
				k.addVertex(v);
				vindex++;
				
					
				Vector3f tv0 = new Vector3f(0f, 0f, 0f);
				Vector3f tv1 = new Vector3f(0f, 1f, 0f);
				Vector3f tv2 = new Vector3f(1f, 1f, 0f);
				Vector3f tv3 = new Vector3f(1f, 0f, 0f);
				k.addTVertex(tv0);
				k.addTVertex(tv1);
				k.addTVertex(tv2);
				k.addTVertex(tv3);
												
				f = new Face(vindex-3, vindex-2, vindex-1);
				f.texCoordIDC = tindex+0;
				f.texCoordIDB = tindex+1;
				f.texCoordIDA = tindex+2;				
				k.addFace(f);			
				
				f = new Face(vindex-1, vindex, vindex-3);
				f.texCoordIDC = tindex+2;
				f.texCoordIDB = tindex+3;
				f.texCoordIDA = tindex+0;				
				k.addFace(f);
				tindex+=4;				
			}
		}
		k.calcNormals(1);
		m.addKeyframe(k);
	
	}
	
	public float get(float[] m, int x, int y) {
		if (x<0) x = 0;
		if (y<0) y = 0;
		if (x >= mapSizeX) x = mapSizeX-1;
		if (y >= mapSizeY) y = mapSizeY-1;

		return m[x+(y*mapSizeX)];
	}
	
	public void draw(GLAutoDrawable gld, long time) {
			
		m.draw(gld, time, null);
		
	}
	
	public void drawGrid(GLAutoDrawable gld) {
		
		GL2 gl2 = gld.getGL().getGL2();
		
		float hx = (mapSizeX * cellSize)/2f;
		float hy = (mapSizeY * cellSize)/2f;
		
		gl2.glMaterialfv( GL.GL_FRONT, GL2.GL_AMBIENT,   this.c1.getData(), 0  );
		gl2.glMaterialfv( GL.GL_FRONT, GL2.GL_DIFFUSE,   this.c2.getData(), 0  );
		
		gl2.glNormal3f(0f, 1f, 0f);
		gl2.glLineWidth(1.1f);
		gl2.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST); 
		
		gl2.glBegin(GL.GL_LINES);
		
		gl2.glNormal3f(0f, 1f, 0f);
		
		Vector3f ul = new Vector3f();
		Vector3f ol = new Vector3f();
		Vector3f ur = new Vector3f();
		Vector3f or = new Vector3f();
		int xpos;
		int ypos;
		for (xpos=border; xpos<(this.mapSizeX-1) - border +1; xpos+=1) {			
			   
			for (ypos=border; ypos<(this.mapSizeY-1) - border +1; ypos+=1) {
				
				 // unten links: 
				float x = (float) xpos * this.cellSize - hx;
				float y = this.map[ypos*this.mapSizeX+xpos];
				float z = ypos * this.cellSize - hy;
				ul.setValues(x,y,z);
						
				 // oben links: 
				x = (float) xpos * this.cellSize - hx;
				y = this.map[(ypos+1)*this.mapSizeX+xpos];
				z = (ypos+1) * this.cellSize - hy;	
				ol.setValues(x,y,z);

				// oben rechts: 
				x = (float) (xpos+1) * this.cellSize - hx;
				y = this.map[(ypos+1)*this.mapSizeX+(xpos+1)];
				z = (ypos+1) * this.cellSize - hy;		      
				or.setValues(x,y,z);					

				// unten rechts: 
				x = (float) (xpos+1) * this.cellSize - hx;
				y = this.map[ypos*this.mapSizeX+(xpos+1)];
				z = ypos * this.cellSize - hy;	
				ur.setValues(x,y,z);			
				
				if (ypos<(this.mapSizeY-1) - border) {
					gl2.glVertex3fv(ol.getData(),0);
					gl2.glVertex3fv(ul.getData(),0);
				}
				
				if (xpos<(this.mapSizeX-1) - border ) {
					gl2.glVertex3fv(ul.getData(),0);
					gl2.glVertex3fv(ur.getData(),0);
				}
				
			}			
		}
		
		
		gl2.glEnd();
			
	}
	
	
	public void setMaterial(Material m2) {
		this.m.setMaterial(m2);
		
	}
	
	public void setMaterialID(int i) {
		this.m.setMaterialID(i);
	}

	public void showGrid(boolean b) {
		this.showGrid = b;		
	}

}
 