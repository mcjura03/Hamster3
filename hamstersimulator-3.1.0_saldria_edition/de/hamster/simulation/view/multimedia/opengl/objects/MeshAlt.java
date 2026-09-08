package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris
 * 
 * Alte Version des Meshes, bevor die Anforderungen an das durch 3D-Studio gelieferte 
 * Objektformat vorlagen.
 * 
 */

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.math.Vector2f;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;


public class MeshAlt {

	// die keyframes:	
	private ArrayList<KeyframeAlt> keyframes;
	
	// die animationen
	private ArrayList<Animation> animations;
	
	// das material:
	Material material = null;

	// die vertexbuffer für opengl, diese enthalten jeweils den aktuell interpolierten keyframe: 
	private ByteBuffer currentVertices;
	private ByteBuffer currentNormals;
	private ByteBuffer currentTexCoords;

	// die IDs
	private int vertexIndex = -1; 
	private int normalIndex = -1; 
	private int textureIndex = -1; 

	// status des meshes und der animation
	private boolean animating = false;
	private long lastTime = 0;
	private long timeSinceLastKeyframe = 0;
	private Animation currentAnimation = null;
	private Animation defaultAnimation = null;	
	private boolean buffersFilled = false;
	private boolean buffersBound = false;

	private boolean buffersCreated = false;
	private int numVertices;
	
	public MeshAlt () {
		this.keyframes = new ArrayList<KeyframeAlt>();
		this.animations = new ArrayList<Animation>();
		
		this.currentNormals = ByteBuffer.allocateDirect(0);
		this.currentVertices = ByteBuffer.allocateDirect(0);
		this.currentTexCoords = ByteBuffer.allocateDirect(0);

		this.animating = false;
		
	}
	
	public void draw(GLAutoDrawable gld, long time) {
			
		GL2 gl2 = gld.getGL().getGL2();
		
		/// buffer erstellen und anmelden:
		if (!buffersCreated) this.createBuffers();
		
		// interpolieren, oder sichergehen, daß die buffers mit statischen daten gefüllt sind,
		// je nachdem, ob wir animiert sind, oder nicht:
		if (this.numberOfKeyframes() > 1 ) {			
			// falls die animation läuft oder noch nix interpoliert wurde:
			if (this.animating) this.interpolate(gl2, time);
			this.buffersBound = false;			
		}
		
		// prima, nun sind die buffers also mit daten zum darstellen gefüllt,
		// sichergehen, daß sie auch gebunden sind:
		if (!this.buffersBound) this.bindBuffers(gl2);
		
		// und nun können wir die buffer zeichnen lassen:
		
		// material, farben etc setzen:
		// die texturkoordinaten werden ja woanders, nämlich in den buffern gesetzt.
		//gl.glEnable(GL.GL_TEXTURE_2D);
		
		
		this.material.use(gld);
			
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 /*               
        gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, this.vertexIndex );
        gl.glVertexPointer( 3, GL.GL_FLOAT, 0, (ByteBuffer) null );               
        gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, this.normalIndex );
        gl.glNormalPointer( GL.GL_FLOAT, 0, (ByteBuffer) null );               
        gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, this.textureIndex );
        gl.glTexCoordPointer( 2, GL.GL_FLOAT, 0, (ByteBuffer) null );               
*/      

		// rewind buffers to be readable
		currentVertices.rewind();
		currentNormals.rewind();
		currentTexCoords.rewind();
        
        gl2.glVertexPointer( 3, GL.GL_FLOAT, 0, (ByteBuffer) this.currentVertices);               
        gl2.glNormalPointer( GL.GL_FLOAT, 0, (ByteBuffer) this.currentNormals);               
        gl2.glTexCoordPointer( 2, GL.GL_FLOAT, 0, (ByteBuffer) this.currentTexCoords);               


		// zack
        gl2.glDrawArrays(GL.GL_TRIANGLES,0,this.numVertices);
		
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	
	}

	
		
	private void bindBuffers(GL gl) {
		
		int[] ids = {-1, -1, -1};
/*	    gl.glGenBuffersARB( 3, ids ); 
	    this.vertexIndex = ids[0];
	    this.normalIndex = ids[1];
	    this.textureIndex = ids[2];
	    
        gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, this.vertexIndex ); 
        gl.glBufferDataARB( GL.GL_ARRAY_BUFFER_ARB, this.numVertices*3*4, this.currentVertices, GL.GL_DYNAMIC_DRAW_ARB );
        
        gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, this.normalIndex ); 
        gl.glBufferDataARB( GL.GL_ARRAY_BUFFER_ARB, this.numVertices*3*4, this.currentNormals, GL.GL_DYNAMIC_DRAW_ARB );
 
        gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, this.textureIndex );
        gl.glBufferDataARB( GL.GL_ARRAY_BUFFER_ARB, this.numVertices*2*4, this.currentTexCoords, GL.GL_STATIC_DRAW_ARB );
*/
        this.buffersBound = true;	    
	}
	
	private void createBuffers() {
				
		KeyframeAlt k = this.keyframes.get(0);
		this.currentVertices = ByteBuffer.allocateDirect(k.numberOfTriangles()*3*3*4);
		this.currentVertices.order(java.nio.ByteOrder.nativeOrder());
		this.currentNormals = ByteBuffer.allocateDirect(k.numberOfTriangles()*3*3*4);
		this.currentNormals.order(java.nio.ByteOrder.nativeOrder());
		this.currentTexCoords = ByteBuffer.allocateDirect(k.numberOfTriangles()*3*2*4);
		this.currentTexCoords.order(java.nio.ByteOrder.nativeOrder());
		this.numVertices = k.numberOfTriangles()*3;
		this.buffersCreated = true;
		
		// auf jeden fall die buffer initial mit dem ersten keyframe füllen
		// bei sgtatischen objekten ist dies das einzige mal, das es geschieht,
		// bei animierten dagegen wird in jedem frame nach dem interpolieren neu gefüllt.
		fillBuffers();

	}
	
	private void fillBuffers() {
		
		// übertrage die vertices, die normals und die texturkoords in die buffer:
		Vector3f v; 
		Vector2f v2; 

		for (int i=0; i< this.keyframes.get(0).numberOfTriangles(); i++) {
			
			Triangle t = this.keyframes.get(0).getTriangle(i);
			
			v = t.getVertex(0);
			this.addToBuffer(this.currentVertices,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			
			v = t.getVertex(1);
			this.addToBuffer(this.currentVertices,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v = t.getVertex(2);
			this.addToBuffer(this.currentVertices,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			
			v = t.getNormal(0);
			this.addToBuffer(this.currentNormals,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v = t.getNormal(1);
			this.addToBuffer(this.currentNormals,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v = t.getNormal(2);
			this.addToBuffer(this.currentNormals,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v2 = t.getTexCoord(0);
			this.addToBuffer(this.currentTexCoords,
					v2.getX(),
					v2.getY()
					);
			v2 = t.getTexCoord(1);
			this.addToBuffer(this.currentTexCoords,
					v2.getX(),
					v2.getY()
					);
			v2 = t.getTexCoord(2);
			this.addToBuffer(this.currentTexCoords,
					v2.getX(),
					v2.getY()
					);
			
		}
		this.buffersFilled = true;
		
		
	}

	private void interpolate(GL gl, long time) {

//		System.out.println("Interpolate");
		
		long timeToInterpolate = time - lastTime;
		long timeLeftFromLastKeyframe = currentAnimation.getLastDelay() - timeSinceLastKeyframe;
						
		// wenn wir mehr zeit darstellen müssen, als der gerade offenen keyframe
		// dauert, dann müssen wir folglich mindestens einen überspringen:
		while (timeLeftFromLastKeyframe < timeToInterpolate) {
			
//			System.out.println(" Skipping...");
			
			// die animation anweisen, zum nächsten Keyframe zu springen:
			// die gibt uns eine aktualisierte Angabe über die aktuelle Animation.
			// entweder springen wir zu einer ganz ani, oder zum nachfolger der aktuellen
			// oder wie bleibn in der gleichen, jedenfalls ist dies hier der aktuelle index:
			this.currentAnimation = currentAnimation.skipThisKeyframe();
			
			// da wir dieses keyframe quasi grade erst beginnen, ist keine zeit seitdem verstrichen:
			this.timeSinceLastKeyframe = 0;
			
			// wir haben durch das springen zum nächsten frame gewissermaßen die restzeit des alten
			// abgearbeitet, also ziehen wir sie von der zeit, die noch abzuarbeiten ist, ab:
			timeToInterpolate -= timeLeftFromLastKeyframe;
			
			// vom neuen frame ist alles abzuarbeiten, also setzen wir den wert auf
			// die dauer des neuen lastKeyframes:
			timeLeftFromLastKeyframe = currentAnimation.getLastDelay();
			
			// wenn die schleife nun von neuem beginnt, wird sie prüfen, ob wir immer 
			// noch mehr zeit zu überbrücken haben, als der nun aktuelle keyframe dauert.
			// wenn nicht, dann ist die schleife zuende und wir haben sowohl den aktuellen
			// last-keyframe, als auch eine aktuelle zeit, die in diesem keyframe zu überbrücken ist.
			
		}
		
//		System.out.println(" getting last and next");
		
		// so, wenn wir hier sind, ist der teil oder genau der rest eines keyframes zu überbrücken:
		
		// so. nun ist der ausgangskeyframe in lastKeyframeIndex gesichert.
		// wir holen den ausgangskeyframe:
		KeyframeAlt last = this.keyframes.get(currentAnimation.getLastKeyFrame());
		
		// und wir holen uns den folgenden Keyframe:
		KeyframeAlt next = this.keyframes.get(currentAnimation.getNextKeyFrame());
		
		// in diesem frame, den wir nicht zuende gehen weren, werden wir genau
		// timeToInterpolate millisekunden abarebiten, also merken wir uns das.
		this.timeSinceLastKeyframe += timeToInterpolate;
		
		// schauen, wie lang der aktuelle keyframe ist:
		long duration = currentAnimation.getLastDelay();
		
		// wieviel prozent der zeit interpolieren wir in diesem schritt:
		float fraction = (float) timeSinceLastKeyframe / duration;

		float x, y, z;
		
		// nun die vertices interpolieren:
//		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, this.vertexIndex);
//		this.currentVertices = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY_ARB);
		this.currentVertices.clear();
		
//		System.out.println(" interpolating");
		
		for (int i=0; i< this.keyframes.get(0).numberOfTriangles(); i++) {
			Triangle source = last.getTriangle(i);
			Triangle dest   = next.getTriangle(i);
						
			x = source.getVertex(0).getX() + fraction*(dest.getVertex(0).getX()-source.getVertex(0).getX());
			y = source.getVertex(0).getY() + fraction*(dest.getVertex(0).getY()-source.getVertex(0).getY());
			z = source.getVertex(0).getZ() + fraction*(dest.getVertex(0).getZ()-source.getVertex(0).getZ());
			this.addToBuffer(this.currentVertices, x, y, z);
			
			x = source.getVertex(1).getX() + fraction*(dest.getVertex(1).getX()-source.getVertex(1).getX());
			y = source.getVertex(1).getY() + fraction*(dest.getVertex(1).getY()-source.getVertex(1).getY());
			z = source.getVertex(1).getZ() + fraction*(dest.getVertex(1).getZ()-source.getVertex(1).getZ());
			this.addToBuffer(this.currentVertices, x, y, z);

			x = source.getVertex(2).getX() + fraction*(dest.getVertex(2).getX()-source.getVertex(2).getX());
			y = source.getVertex(2).getY() + fraction*(dest.getVertex(2).getY()-source.getVertex(2).getY());
			z = source.getVertex(2).getZ() + fraction*(dest.getVertex(2).getZ()-source.getVertex(2).getZ());
			this.addToBuffer(this.currentVertices, x, y, z);
			
		}
//		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);	
		
		// nun auch die normals interpolieren:
//		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, this.normalIndex);
//		this.currentNormals = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY_ARB);
		this.currentNormals.clear();
		
		for (int i=0; i< this.keyframes.get(0).numberOfTriangles(); i++) {
			Triangle source = last.getTriangle(i);
			Triangle dest = next.getTriangle(i);
			
			x = source.getNormal(0).getX() + fraction*(dest.getNormal(0).getX()-source.getNormal(0).getX());
			y = source.getNormal(0).getY() + fraction*(dest.getNormal(0).getY()-source.getNormal(0).getY());
			z = source.getNormal(0).getZ() + fraction*(dest.getNormal(0).getZ()-source.getNormal(0).getZ());
			this.addToBuffer(this.currentNormals, x, y, z);
			
			x = source.getNormal(1).getX() + fraction*(dest.getNormal(1).getX()-source.getNormal(1).getX());
			y = source.getNormal(1).getY() + fraction*(dest.getNormal(1).getY()-source.getNormal(1).getY());
			z = source.getNormal(1).getZ() + fraction*(dest.getNormal(1).getZ()-source.getNormal(1).getZ());
			this.addToBuffer(this.currentNormals, x, y, z);

			x = source.getNormal(2).getX() + fraction*(dest.getNormal(2).getX()-source.getNormal(2).getX());
			y = source.getNormal(2).getY() + fraction*(dest.getNormal(2).getY()-source.getNormal(2).getY());
			z = source.getNormal(2).getZ() + fraction*(dest.getNormal(2).getZ()-source.getNormal(2).getZ());
			this.addToBuffer(this.currentNormals, x, y, z);

		}
//		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
				
		this.lastTime = time;
		this.buffersFilled = true;
	}
		 
	public void addKeyframe(KeyframeAlt kf) {
		this.keyframes.add(kf);
	}
	
	public int numberOfKeyframes() {
		return this.keyframes.size();
	}
	
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	private void addToBuffer(ByteBuffer b, float x, float y, float z) {
		b.putFloat(x); 
		b.putFloat(y);
		b.putFloat(z);
	}

	private void addToBuffer(ByteBuffer b, float x, float y) {
		b.putFloat(x);
		b.putFloat(y);
	}

	public KeyframeAlt getKeyframe(int i) {
		return this.keyframes.get(i);
	}

	public void startAnimation(long time) {
		this.animating = true;
		this.lastTime = time;
	}
	
	public void stopAnimation() {
		this.animating = false;
	}
	public void setDefaultAnimation(Animation a) {
		this.defaultAnimation = a;
	}
	
	public void setCurrentAnimation(Animation a) {
		this.currentAnimation = a;
	}	

	public void addAnimation(Animation a) {
		if (this.animations.size() == 0) { 
			setDefaultAnimation(a);
			setCurrentAnimation(a);
		}
		this.animations.add(a);
	}

	public void setMaterial(Material m) {
		this.material = m;
		
	}

}

