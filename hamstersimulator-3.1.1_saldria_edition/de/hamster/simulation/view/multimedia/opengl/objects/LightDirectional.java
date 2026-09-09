package de.hamster.simulation.view.multimedia.opengl.objects;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class LightDirectional extends LightAbstract {
	
	public LightDirectional(int id, String name) {
		super(id, name);			
	}
	
	public void enable(GLAutoDrawable gld) {
                        
		System.out.println("enable light #" + this.glLightId);
		
		GL2 gl = gld.getGL().getGL2();
		
		gl.glLightfv( GL2.GL_LIGHT0 + this.glLightId, GL2.GL_AMBIENT,  this.ambient.getData(), 0  );
		gl.glLightfv( GL2.GL_LIGHT0 + this.glLightId, GL2.GL_DIFFUSE,  this.diffuse.getData(), 0  );

		this.pos[3] = 0f; // immer 0 bei directional
		gl.glLightfv(GL2.GL_LIGHT0 + this.glLightId, GL2.GL_POSITION, this.pos, 0 );
	    
	     //Enable the first light and the lighting mode
	     gl.glEnable(GL2.GL_LIGHT0 + this.glLightId);
	     gl.glEnable(GL2.GL_LIGHTING);
	     this.isUsed=true;			

	}





}
