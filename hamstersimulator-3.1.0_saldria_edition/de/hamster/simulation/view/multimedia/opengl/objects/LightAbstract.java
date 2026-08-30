package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris 
 * 
 * Basisklasse für Lichter.
 */

import de.hamster.simulation.view.multimedia.opengl.material.Color;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;


public abstract class LightAbstract {
	
	protected Color   ambient, diffuse, specular;
	protected boolean isUsed			   = false;	
	protected int     glLightId            = -1;
	protected float[] pos 				   = {0f, 0f, 0f, 1f};

	public LightAbstract(int id, String name) {

		this.glLightId = id;
		this.diffuse = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		this.ambient = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    	this.specular = new Color( 1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public void enable(GLAutoDrawable gld) {  
	    
    }
	
	public void disable(GLAutoDrawable gld) {
		if (this.isUsed) {			
            GL2 gl2 = gld.getGL().getGL2();
			gl2.glDisable(GL2.GL_LIGHT0 + this.glLightId);
			this.isUsed=false;
		}
	}

	public Color getAmbient() {
		return ambient;
	}

	public void setAmbient(Color ambient) {
		this.ambient = new Color(ambient);
	}

	public void setAmbientAndDiffuse(Color c) {
		this.ambient = new Color(c);
		this.diffuse = new Color(c);
	}

	
	public Color getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Color c) {
		this.diffuse = new Color(c);
	}

	public Color getSpecular() {
		return specular;
	}

	public void setSpecular(Color c) {
		this.specular = new Color(c);
	}
	

	public float getPosX() {
		return this.pos[0];
	}

	public float getPosY() {
		return this.pos[1];
	}
	public float getPosZ() {
		return this.pos[2];
	}
	
	public void setPos(float x, float y, float z) {
		this.pos[0] = x;
		this.pos[1] = y;
		this.pos[2] = z;
	}

	}
