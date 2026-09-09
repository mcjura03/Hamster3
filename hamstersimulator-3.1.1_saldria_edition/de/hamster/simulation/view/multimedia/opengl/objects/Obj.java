
package de.hamster.simulation.view.multimedia.opengl.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.jogamp.opengl.GLAutoDrawable;

import de.hamster.simulation.view.multimedia.opengl.material.Color;
import de.hamster.simulation.view.multimedia.opengl.material.MaterialController;

/**
 * @author chris
 * 
 * Das 3D-Objekt. Enthält Meshes und Animationsdaten und kann aus einer Datei
 * geladen werden.
 */
public class Obj {

	public ArrayList<Mesh> meshes = null; 
	public MaterialController mC = null;
	BufferedReader br;
	
	
	// animation
	public ArrayList<Animation> animations;
	public boolean animating = false;
	public long lastTime = 0;
	public long timeSinceLastKeyframe = 0;
	public Animation currentAnimation = null;
	
	public Obj (MaterialController mc) {
		this.mC = mc;
		this.meshes = new ArrayList<Mesh>();
		this.animations = new ArrayList<Animation>();
	}
	
	public void draw(GLAutoDrawable gld, long time) {
	
		if (this.meshes != null) {
		
			for (int i=0; i<this.meshes.size(); i++) {
				this.meshes.get(i).draw(gld, time, this);
			}
		}
	}
	
	
	private  String readLine() {
		try {
			return this.br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void loadObj(String filename) {
		
		File f = new File(filename);
		try {
			
			this.br = new BufferedReader(new FileReader(f));
			
		} catch (FileNotFoundException e) { e.printStackTrace(); }	
		
		String line = readLine();
		
		Animation a = null;
		
		while (line != null) {
			
			if (line.contains("Materials")) {
				String[] fields = line.split(" ");
				ASEFormat.loadMaterials(fields[1], this.mC);
				
			} else			
			if (line.contains("Keyframe")) {								
				String[] fields = line.split(" ");
				ASEFormat.addKeyframe(fields[2], this.mC, this);				
			} else			
				
			if (line.contains("Animation")) {
				
				// falls wir eben bereits eine animation eingelesen haben, müssen 
				// wir diese in die objekte eintragen, bevor wir die neue lesen:
				if (a != null) {

					this.addAnimation(a);
					a.setObj(this);				
					a = null;
				}
				
				String[] fields = line.split(" ");
				a = new Animation(fields[2]);
				Integer i = new Integer(fields[3]);
				a.setSuccessorID(i.intValue());
			}
			if (line.contains("Step")) {
				String[] fields = line.split(" ");
				Integer i = new Integer(fields[2]);
				Integer j = new Integer(fields[3]);
				if (a!= null) a.addKeyframeIndex(i.intValue(), j.intValue());
			}
			
			line = readLine();
		}
		
		// nun doch die letzte animation eintragen: 
		if (a != null) {
			this.addAnimation(a);
		}		
	}

	
	
	
	public void setInstantAnimation() {
		
	}

	public void startAnimating(long time) {
		this.animating = true;
		this.lastTime = time;
	}
	
	public void stopAnimate() {
		this.animating = false;
	}
	
	public void addAnimation(Animation a) {
		this.animations.add(a);
		if (this.currentAnimation == null) this.currentAnimation = a;
	}
	
	public void setAnimated(boolean b) {
		
		if (b) {
			startAnimating(System.currentTimeMillis());
		} else {
			stopAnimate();
		}
	}

	public Obj cloneWithSharedMeshes() {

		//System.out.println("cloneWithSharedMesh");
		
		// ein frisches objekt erstellen:
		Obj o = new Obj(this.mC);

		// die meshes werden nur als referenz kopiert, denn wir wollen die 
		// geometrie ja nur einmal im speicher haben:
		o.meshes = this.meshes;
		
		// die animation muss dagegen kopiert werden:
		for (int i=0; i< this.animations.size(); i++) {
			o.animations.add(new Animation(this.animations.get(i)));
		}
		
		Random rand = new Random(System.currentTimeMillis());
		
		// dies müssen wir auch noch setzen:
		o.currentAnimation = o.animations.get(0);
		long v = rand.nextLong() % 1234;
		o.startAnimating(System.currentTimeMillis() - v);
				
		return o;
	}
	
	public Animation getAnimation(int i) {
		return animations.get(i);
	}

	public boolean isAnimating() {
		return animating;
	}
	
	public void setAnimation(int i) {
		if (i < this.animations.size()) this.currentAnimation = animations.get(i);		
	}
	
	public void setNextAnimation(int i) {
		if (i < this.animations.size()) this.currentAnimation.setSwitchNextTo(animations.get(i));
	}

	public void setAmbientAndDiffuse(int mesh, Color color) {
		
		if (this.meshes != null && mesh<this.meshes.size()) {			
			this.mC.getMaterial(this.meshes.get(mesh).getKeyframes().get(0).getMaterialID()).setAmbientAndDiffuse(color);
		}
		
	}

	
}
