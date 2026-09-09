package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris
 * 
 * Positionales Licht, das ein einer gegebenen Position sitzt und von da in alle
 * Richtungen strahlt.
 * 
 */

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class LightPositional extends LightAbstract {

	protected float   constantAttenuation  = 0.8f;
	protected float   linearAttenuation    = 0.0f;
	protected float   quadraticAttenuation = 0.0f;
	
	public LightPositional(int id, String name) {
		super(id, name);		
	}
	
	public void enable(GLAutoDrawable gld) {
		GL2 gl2 = gld.getGL().getGL2();
					
		gl2.glLightfv( GL2.GL_LIGHT0 + this.glLightId, GL2.GL_AMBIENT,  this.ambient.getData(), 0  );
		gl2.glLightfv( GL2.GL_LIGHT0 + this.glLightId, GL2.GL_DIFFUSE,  this.diffuse.getData(), 0  );

		this.pos[3] = 1f;// immer für positional...

		gl2.glLightfv( GL2.GL_LIGHT0 + this.glLightId, GL2.GL_POSITION, this.pos, 0 );

		gl2.glLightf (GL2.GL_LIGHT0 + this.glLightId, GL2.GL_CONSTANT_ATTENUATION,  this.constantAttenuation  );
		gl2.glLightf (GL2.GL_LIGHT0 + this.glLightId, GL2.GL_LINEAR_ATTENUATION,    this.linearAttenuation    );
		gl2.glLightf (GL2.GL_LIGHT0 + this.glLightId, GL2.GL_QUADRATIC_ATTENUATION, this.quadraticAttenuation );

		gl2.glEnable(GL2.GL_LIGHT0 + this.glLightId);
		gl2.glEnable(GL2.GL_LIGHTING);
		this.isUsed=true;
	

		
	}

	public float getConstantAttenuation() {
		return constantAttenuation;
	}

	public void setConstantAttenuation(float constantAttenuation) {
		this.constantAttenuation = constantAttenuation;
	}

	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	public void setLinearAttenuation(float linearAttenuation) {
		this.linearAttenuation = linearAttenuation;
	}

	public float getQuadraticAttenuation() {
		return quadraticAttenuation;
	}

	public void setQuadraticAttenuation(float quadraticAttenuation) {
		this.quadraticAttenuation = quadraticAttenuation;
	}
		

}
