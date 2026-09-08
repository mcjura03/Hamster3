package de.hamster.simulation.view.multimedia.opengl;

/**
 * @author chris
 * 
 * Das Kameraobjekt verwaltet und berechnet die Lage der virtuellen 
 * Kamera in der Szene. Dazu benutzt sie die Position, sowie Vektoren
 * für die Richtungen hoch, rechts, sowie die blickrichtung. 
 * 
 */

import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

public class Camera {

	// die vektoren geben position und ausrichtung
	private Vector3f pos;
	private Vector3f view;
	private Vector3f right;
	private Vector3f up;
	
	private float rotatedX = 0f;
	private float rotatedY = 0f;
	private float rotatedZ = 0f;
	
	private GLAutoDrawable gld;
	private GL2 gl2;
	private GLU glu;
	
	private final float piDiv180 = (float)(Math.PI/180f);
	
	public Camera (GLAutoDrawable gld) {		
		this.gld = gld;
		this.gl2  = this.gld.getGL().getGL2();
		this.glu = new GLU();// old style: this.gld.getGLU(); 
		
		this.pos = new Vector3f(0f, 0f, 0f);
		this.view = new Vector3f(0f, 0f, -1f);
		this.right = new Vector3f(1f, 0f, 0f);
		this.up = new Vector3f(0f, 1f, 0f);
	}	
	
	public void applyTransformation() {
		
		gl2.glPushMatrix();
		
		// position + richtung ergibt den viewpunkt für gluLookat
		Vector3f viewPoint = new Vector3f(this.pos);
		viewPoint.add(this.view);

		this.glu.gluLookAt(	this.pos.getX(), this.pos.getY(), this.pos.getZ(), viewPoint.getX(), viewPoint.getY(), viewPoint.getZ(), this.up.getX(), this.up.getY(), this.up.getZ());

	}

	public void restoreTransformation() {
		gl2.glPopMatrix();
	}
	
	public void clearTransformation() {
		this.pos.setValues(0f, 0f, 0f);
		this.view.setValues(0f, 0f, -1f);
		this.right.setValues(1f, 0f, 0f);
		this.up.setValues(0f, 1f, 0f);		
		
		this.rotatedX = 0f;
		this.rotatedY = 0f;
		this.rotatedZ = 0f;
	}

	
	public void translate (Vector3f richtung)
	{
		this.pos.add(richtung);
	}


	public void rotateX (float winkel)
	{
		this.rotatedX += winkel;
		
		//view um right drehen:
		float f = winkel*this.piDiv180;
		this.view.sMult((float)Math.cos(f));
		this.view.add(this.up.getSMult((float)Math.sin(f)));
		this.view.normalize();
		
		//Up erneuern:
		up.crossProduct(view, right);
		up.sMult(-1f);
	
	}
	

	public void rotateY (float winkel)
	{
		rotatedY += winkel;
		
		//view um up rotieren:		
		float f = winkel*this.piDiv180;
		this.view.sMult((float)Math.cos(f));
		this.view.sub(this.right.getSMult((float)Math.sin(f)));
		this.view.normalize();
		
		//right erneuern:
		right.crossProduct(up,view);
		right.sMult(-1f);	
	}


	public void rotateZ (float winkel)
	{
		this.rotatedZ += winkel;
		
		//right um up drehen:
		float f = winkel*this.piDiv180;
		this.right.sMult((float)Math.cos(f));
		this.right.add(this.up.getSMult((float)Math.sin(f)));
		this.right.normalize();
		
		//Up erneuern:
		up.crossProduct(view, right);
		up.sMult(-1f);
	}
	
	public void moveForward(float dist)
	{
		this.pos.add(this.view.getSMult(dist));	
	}

	public void strafeRight (float dist)
	{
		this.pos.add(this.right.getSMult(dist));
	}

	public void moveUpward(float dist)
	{
		this.pos.add(this.up.getSMult(dist));
	}

	public void setPosition(float x, float y, float z) {
		this.pos.setValues(x, y, z);
		
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	
}

