package de.hamster.simulation.view.multimedia.opengl.objects;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.material.MaterialController;
import de.hamster.simulation.view.multimedia.opengl.math.Vector2f;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

/**
 * @author chris
 * 
 * Mesh, enthält die Gitterstruktur des 3D-(Unter-)Objekts. Diese werden in den Keyframes verwaltet
 * die jeweils ein Morph-Target darstellen. Die Steuerung der Animation geschieht im Obj,
 * da ein 3D-Objekt mehrere dieser Meshes enthalten kann. 
 *
 */
public class Mesh {
	
	private ArrayList<Keyframe> keyframes;
	private MaterialController mc;
	private Obj obj;

	private boolean buffersCreated = false;	
	private int numVertices;
		
	// die vertexbuffer für opengl, diese enthalten jeweils den aktuell interpolierten keyframe: 
	private ByteBuffer currentVertices;
	private ByteBuffer currentNormals;
	private ByteBuffer currentTexCoords;
	private boolean buffersFilled;
	
	public Mesh(Obj obj, MaterialController mc) {
	
		this.mc=mc;
		this.obj = obj;
		this.keyframes  = new ArrayList<Keyframe>();
	}
	
	public void addKeyframe(Keyframe k) {
				
		this.keyframes.add(k);
		
	}
	
	
	public void draw(GLAutoDrawable gld, long time, Obj obj) {
		
		GL2 gl2 = gld.getGL().getGL2();
		
		/// buffer erstellen und anmelden:
		if (!buffersCreated) this.createBuffers();
		
		if (this.numberOfKeyframes() > 1 && obj != null) {			
			// falls die animation läuft oder noch nix interpoliert wurde:
			if (obj.animating && obj.animations.size() > 0) this.interpolate(gl2, time, obj);
		}
		
		this.mc.materials.get(keyframes.get(0).materialID).use(gld);
		
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 
		// rewind buffers to be readable
		currentVertices.rewind();
		currentNormals.rewind();
		currentTexCoords.rewind();
		
        gl2.glVertexPointer( 3, GL.GL_FLOAT, 0, (ByteBuffer) this.currentVertices);               
        gl2.glNormalPointer( GL.GL_FLOAT, 0, (ByteBuffer) this.currentNormals);               
        gl2.glTexCoordPointer( 2, GL.GL_FLOAT, 0, (ByteBuffer) this.currentTexCoords);               

		// zack
        gl2.glDrawArrays(GL.GL_TRIANGLES,0,this.numVertices);

        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);

		
		
	}
	
	private void createBuffers() {
		
		Keyframe k = this.keyframes.get(0);
		this.currentVertices = ByteBuffer.allocateDirect(k.numberOfTriangles()*3*3*4);
		this.currentVertices.order(java.nio.ByteOrder.nativeOrder());
		this.currentNormals = ByteBuffer.allocateDirect(k.numberOfTriangles()*3*3*4);
		this.currentNormals.order(java.nio.ByteOrder.nativeOrder());
		this.currentTexCoords = ByteBuffer.allocateDirect(k.numberOfTriangles()*2*3*4);
		this.currentTexCoords.order(java.nio.ByteOrder.nativeOrder());
		this.numVertices = k.numberOfTriangles()*3;
		this.buffersCreated = true;
		
		// auf jeden fall die buffer initial mit dem ersten keyframe füllen
		// bei sgtatischen objekten ist dies das einzige mal, das es geschieht,
		// bei animierten dagegen wird in jedem frame nach dem interpolieren neu gefüllt.
		fillBuffers(0);

	}
	
	private void fillBuffers(int k) {
		
		// übertrage die vertices, die normals und die texturkoords in die buffer:
		Vector3f v; 


		for (int i=0; i< this.keyframes.get(k).numberOfTriangles(); i++) {
			
			Face t = this.keyframes.get(k).getFace(i);		
			v = this.keyframes.get(k).vertices.get(t.a);
			
			this.addToBuffer(this.currentVertices,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			
			v = this.keyframes.get(k).vertices.get(t.b);
			this.addToBuffer(this.currentVertices,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v = this.keyframes.get(k).vertices.get(t.c);
			this.addToBuffer(this.currentVertices,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			
			v = this.keyframes.get(k).normals.get(t.a);
			this.addToBuffer(this.currentNormals,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			
			v = this.keyframes.get(k).normals.get(t.b);
			this.addToBuffer(this.currentNormals,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v = this.keyframes.get(k).normals.get(t.c);
			this.addToBuffer(this.currentNormals,
					v.getX(),
					v.getY(),
					v.getZ()
					);
			v = this.keyframes.get(k).tvertices.get(t.texCoordIDA);
			this.addToBuffer(this.currentTexCoords,
					v.getX(),
					v.getY()
					);
			v = this.keyframes.get(k).tvertices.get(t.texCoordIDB);
			this.addToBuffer(this.currentTexCoords,
					v.getX(),
					v.getY()
					);
			v = this.keyframes.get(k).tvertices.get(t.texCoordIDC);
			this.addToBuffer(this.currentTexCoords,
					v.getX(),
					v.getY()
					);
			
		}
		
		// mark them as filled
		this.buffersFilled = true;
		
		
		
	}
	
	
	private void interpolate(GL gl, long time, Obj obj) {
		
		if (obj.currentAnimation == null) obj.setAnimation(0);
		
		long timeToInterpolate = time - obj.lastTime;
		long timeLeftFromLastKeyframe = obj.currentAnimation.getLastDelay() - obj.timeSinceLastKeyframe;
						
		// wenn wir mehr zeit darstellen müssen, als der gerade offenen keyframe
		// dauert, dann müssen wir folglich mindestens einen überspringen:
		while (timeLeftFromLastKeyframe < timeToInterpolate) {			
			
			// die animation anweisen, zum nächsten Keyframe zu springen:
			// die gibt uns eine aktualisierte Angabe über die aktuelle Animation.
			// entweder springen wir zu einer ganz ani, oder zum nachfolger der aktuellen
			// oder wie bleibn in der gleichen, jedenfalls ist dies hier der aktuelle index:
			obj.currentAnimation = obj.currentAnimation.skipThisKeyframe();
			
			// da wir dieses keyframe quasi grade erst beginnen, ist keine zeit seitdem verstrichen:
			obj.timeSinceLastKeyframe = 0;
			
			// wir haben durch das springen zum nächsten frame gewissermaßen die restzeit des alten
			// abgearbeitet, also ziehen wir sie von der zeit, die noch abzuarbeiten ist, ab:
			timeToInterpolate -= timeLeftFromLastKeyframe;
			
			// vom neuen frame ist alles abzuarbeiten, also setzen wir den wert auf
			// die dauer des neuen lastKeyframes:
			timeLeftFromLastKeyframe = obj.currentAnimation.getLastDelay();
			
			// wenn die schleife nun von neuem beginnt, wird sie prüfen, ob wir immer 
			// noch mehr zeit zu überbrücken haben, als der nun aktuelle keyframe dauert.
			// wenn nicht, dann ist die schleife zuende und wir haben sowohl den aktuellen
			// last-keyframe, als auch eine aktuelle zeit, die in diesem keyframe zu überbrücken ist.
			
		}
		
		// so, wenn wir hier sind, ist der teil oder genau der rest eines keyframes zu überbrücken:
		
		// so. nun ist der ausgangskeyframe in lastKeyframeIndex gesichert.
		// wir holen den ausgangskeyframe:
		Keyframe last = this.keyframes.get(obj.currentAnimation.getLastKeyFrame());
		
		// und wir holen uns den folgenden Keyframe:
		Keyframe next = this.keyframes.get(obj.currentAnimation.getNextKeyFrame());
		
		
		
		// in diesem frame, den wir nicht zuende gehen weren, werden wir genau
		// timeToInterpolate millisekunden abarebiten, also merken wir uns das.
		obj.timeSinceLastKeyframe += timeToInterpolate;
		
		// schauen, wie lang der aktuelle keyframe ist:
		long duration = obj.currentAnimation.getLastDelay();
		
		// wieviel prozent der zeit interpolieren wir in diesem schritt:
		float fraction = (float) obj.timeSinceLastKeyframe / duration;

		float x, y, z;
		
		// nun die vertices interpolieren:
		this.currentVertices.clear();
		
		for (int i=0; i< this.keyframes.get(0).numberOfTriangles(); i++) {
			Face f = keyframes.get(0).getFace(i);		
			
			x = last.vertices.get(f.a).getX() + fraction*(next.vertices.get(f.a).getX()-last.vertices.get(f.a).getX());
			y = last.vertices.get(f.a).getY() + fraction*(next.vertices.get(f.a).getY()-last.vertices.get(f.a).getY());
			z = last.vertices.get(f.a).getZ() + fraction*(next.vertices.get(f.a).getZ()-last.vertices.get(f.a).getZ());

			this.addToBuffer(this.currentVertices, x, y, z);
			
			x = last.vertices.get(f.b).getX() + fraction*(next.vertices.get(f.b).getX()-last.vertices.get(f.b).getX());
			y = last.vertices.get(f.b).getY() + fraction*(next.vertices.get(f.b).getY()-last.vertices.get(f.b).getY());
			z = last.vertices.get(f.b).getZ() + fraction*(next.vertices.get(f.b).getZ()-last.vertices.get(f.b).getZ());
			this.addToBuffer(this.currentVertices, x, y, z);

			x = last.vertices.get(f.c).getX() + fraction*(next.vertices.get(f.c).getX()-last.vertices.get(f.c).getX());
			y = last.vertices.get(f.c).getY() + fraction*(next.vertices.get(f.c).getY()-last.vertices.get(f.c).getY());
			z = last.vertices.get(f.c).getZ() + fraction*(next.vertices.get(f.c).getZ()-last.vertices.get(f.c).getZ());
			this.addToBuffer(this.currentVertices, x, y, z);
			
		}

		// nun auch die normals interpolieren:
		this.currentNormals.clear();
		
		for (int i=0; i< this.keyframes.get(0).numberOfTriangles(); i++) {

			Face f =  keyframes.get(0).getFace(i);			
			
			x = last.normals.get(f.a).getX() + fraction*(next.normals.get(f.a).getX()-last.normals.get(f.a).getX());
			y = last.normals.get(f.a).getY() + fraction*(next.normals.get(f.a).getY()-last.normals.get(f.a).getY());
			z = last.normals.get(f.a).getZ() + fraction*(next.normals.get(f.a).getZ()-last.normals.get(f.a).getZ());
			this.addToBuffer(this.currentNormals, x, y, z);
			
			x = last.normals.get(f.b).getX() + fraction*(next.normals.get(f.b).getX()-last.normals.get(f.b).getX());
			y = last.normals.get(f.b).getY() + fraction*(next.normals.get(f.b).getY()-last.normals.get(f.b).getY());
			z = last.normals.get(f.b).getZ() + fraction*(next.normals.get(f.b).getZ()-last.normals.get(f.b).getZ());
			this.addToBuffer(this.currentNormals, x, y, z);

			x = last.normals.get(f.c).getX() + fraction*(next.normals.get(f.c).getX()-last.normals.get(f.c).getX());
			y = last.normals.get(f.c).getY() + fraction*(next.normals.get(f.c).getY()-last.normals.get(f.c).getY());
			z = last.normals.get(f.c).getZ() + fraction*(next.normals.get(f.c).getZ()-last.normals.get(f.c).getZ());
			this.addToBuffer(this.currentNormals, x, y, z);
		}
				
		obj.lastTime = time;
		this.buffersFilled = true;
		
	}

	public void drawSimple(GLAutoDrawable gld) {
		
		GL2 gl2 = gld.getGL().getGL2();		
		Keyframe k = keyframes.get(0);	
		
		mc.getMaterial(k.materialID).use(gld);
		
		gl2.glBegin(GL.GL_TRIANGLES);			
			
		for (int face=0; face<k.faces.size(); face++) {
			
//			gl.glNormal3fv( k.faces.get(face).faceNormal.getData() );
			
			gl2.glTexCoord3fv( k.tvertices.get(k.faces.get(face).texCoordIDA).getData(), 0 );
			gl2.glNormal3fv( k.normals.get(k.faces.get(face).a).getData(), 0 );				
			gl2.glVertex3fv( k.vertices.get(k.faces.get(face).a).getData(), 0 );

			gl2.glTexCoord3fv( k.tvertices.get(k.faces.get(face).texCoordIDB).getData(), 0 );
			gl2.glNormal3fv( k.normals.get(k.faces.get(face).b).getData(), 0 );
			gl2.glVertex3fv( k.vertices.get(k.faces.get(face).b).getData(), 0 );

			gl2.glTexCoord3fv( k.tvertices.get(k.faces.get(face).texCoordIDC).getData(), 0 );
			gl2.glNormal3fv( k.normals.get(k.faces.get(face).c).getData(), 0 );
			gl2.glVertex3fv( k.vertices.get(k.faces.get(face).c).getData(), 0 );
			
			
		}
		gl2.glEnd();

	}
	
	public void setMaterial(Material m) {
		
		int id = mc.materials.indexOf(m);
		
		for (int i=0; i< keyframes.size(); i++) {
			this.keyframes.get(i).setMaterialID(id);
		}
		
	}
	
	public void setMaterialID(int t) {
			
		for (int i=0; i< keyframes.size(); i++) {
			this.keyframes.get(i).setMaterialID(t);
		}
		
	}

	public void calcNormals(int t) {
		for (int i=0; i< keyframes.size(); i++) {
			this.keyframes.get(i).calcNormals(t);
		}
		
	}
	
	public int numberOfKeyframes() {
		return this.keyframes.size();
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

	public ArrayList<Keyframe> getKeyframes() {
		return keyframes;
	}

	public void setKeyframes(ArrayList<Keyframe> keyframes) {
		this.keyframes = keyframes;
	}





}
