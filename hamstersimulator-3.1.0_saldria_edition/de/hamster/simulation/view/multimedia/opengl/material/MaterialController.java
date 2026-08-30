package de.hamster.simulation.view.multimedia.opengl.material;

import java.util.ArrayList;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.util.texture.*;

/**
 * @author chris
 *
 * Verwaltet die Materialien. Wichtig für das Laden der Texturen, 
 * das nicht zu jedem Zeitpunkt, sondern nur innerhalb eines GL-Kontextes 
 * geschehen darf. Darum werden Materialien mit ihren Texturen erst nur
 * "angefordert" und später dann von der korrekten Stelle aus geladen
 */
public class MaterialController {

	private boolean useVeryOldGraphicMode = true;
	private Material currentMaterial = null;
	
	// Bibliothek der verwendeten Materialien
	public ArrayList<Material> materials = new ArrayList<Material>();
    public ArrayList<Texture> textures = new ArrayList<Texture>();

    private int texUnitsInUse = 0;
    public MaterialController() {
    	
    }
    
    public void defineAndRequestMaterial(Material m) { 
    	this.defineMaterial(m);
    	this.requestMaterial(m);
    }
    
    public void defineMaterial(Material m) {
    	m.setMC(this);    	
		this.materials.add(m);		
	}
	
    public void defineTexture(Texture t) {
		this.textures.add(t);		
	}

	public Material getMaterial(int i) {
		return materials.get(i);
	}

	public Texture getTexture(int i) {
		return textures.get(i);
	}
    
	public ArrayList<Texture> getTextures() {
		return textures;
	}
	
	public ArrayList<Material> getMaterials() {
		return materials;
	}

	public int getLastTextureIndex() {
		return this.textures.size()-1;
	}
	
	// wir können die texturen nicht an beliebigen zeitpunkten laden,
	// daher markieren wir zu ladende texturen und erledigen es dann
	// sobald wie ein opengl-handle haben, innerhalb eines draw-callbacks:
	public void requestMaterial(Material m) {
		
		int i = this.materials.indexOf(m);
		
		if (i==-1) return;
		
		if (this.materials.get(i).getState() == Material.NOT_IN_USE)
			this.materials.get(i).setState(Material.TO_BE_LOADED);

		if (this.materials.get(i).getState() == Material.TO_BE_DELETED)
			this.materials.get(i).setState(Material.IN_USE);
	}

	public void releaseAll() {
		
		for (int i=0; i<this.materials.size(); i++)
		{
			if (this.materials.get(i).getState() == Material.IN_USE) 
					this.materials.get(i).setState(Material.TO_BE_DELETED);
			
			if (this.materials.get(i).getState() == Material.TO_BE_LOADED) 
				this.materials.get(i).setState(Material.NOT_IN_USE);
		}
	}
	
	public void loadMaterial(int materialID, GLAutoDrawable gld) {

		for (int i = 0; i < this.materials.get(materialID).getNumberOfTextures(); i++) {
			this.textures.get(this.materials.get(materialID).getTextureID(i)).loadTexture(gld);
		}
		this.materials.get(materialID).setState(Material.IN_USE);
	}
	
	public void unloadMaterial(int materialID, GLAutoDrawable gld) {

		for (int i = 0; i < this.materials.get(materialID).getNumberOfTextures(); i++) {
			this.textures.get(this.materials.get(materialID).getTextureID(i)).unloadTexture(gld);
		}
		this.materials.get(materialID).setState(Material.NOT_IN_USE);
	}
    
    /**
     * @deprecated
     * @param num
     * @param gl
     */
    public void activateTexUnitsOld(int num, GL gl)
    {
        for (int i=0; i< num; i++) {
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            gl.glEnable(GL.GL_TEXTURE_2D);
        }

        for (int i=num; i<texUnitsInUse; i++) {
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
        this.texUnitsInUse = num;

    }

    public void useTextureSet(int[] set, GLAutoDrawable gld, int mainTex) {

        GL gl = gld.getGL();
        
        // das kümmert sich darum, und schaltet auch nur die benötigte Anzahl an Units an:
        
        if (this.useVeryOldGraphicMode) {
        	
            // falls der index gültig ist:
        	if (mainTex < set.length) {
        		gl.glEnable(GL.GL_TEXTURE_2D);
        		this.textures.get(set[mainTex]).useTexture(gld);
        	}      	

        } else {
            gl.glEnable(GL.GL_TEXTURE_2D);
            
        	for (int i = 0; i< set.length; i++) {
        		gl.glActiveTexture(GL.GL_TEXTURE0 + i);
                gl.glEnable(GL.GL_TEXTURE_2D);
                this.textures.get(set[i]).useTexture(gld);
            }

            for (int i = set.length; i < texUnitsInUse; i++) {

                gl.glDisable(GL.GL_TEXTURE_2D);
            }
        }
        this.texUnitsInUse = set.length;        
       
    }

    
    
    
    
    // durch umstellung auf vertex array so nicht mehr benötigt:
    public void setTextureCoordinates(GL2 gl2, int textureUnit, int mainTextureID, float s, float t) {
    	   	
    	if (this.useVeryOldGraphicMode) {
			if (textureUnit == mainTextureID) gl2.glTexCoord2f(s, t);
		} else {
	    	try {
	    		gl2.glMultiTexCoord2f(GL.GL_TEXTURE0 + textureUnit, s, t);
	    	} catch (Exception e) {
				if (textureUnit == mainTextureID) gl2.glTexCoord2f(s, t);
				this.useVeryOldGraphicMode = true;
	    	}
		}
    }

	public Material getCurrentMaterial() {
		return currentMaterial;
	}

	public void setCurrentMaterial(Material currentMaterial) {
		this.currentMaterial = currentMaterial;
	}

	public void setUseVeryOldGraphicMode(boolean useVeryOldGraphicMode) {
		this.useVeryOldGraphicMode = useVeryOldGraphicMode;
	}
	
	public void doTextureRefresh(GLAutoDrawable gld) {
					
		// wir wollen alle materialien durchlaufen:
		int num = this.getMaterials().size();
		
		Material m;
		for (int i=0; i<num; i++)
		{
			
			m = this.getMaterial(i);
			
			if (m.getState() == Material.TO_BE_LOADED) this.loadMaterial(i, gld); 
			else if (m.getState() == Material.TO_BE_DELETED) this.unloadMaterial(i, gld);
		}
	}

	public void disableTextures(GL gl) {

		 gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
	public void unsetMaterial(GL gl) {
		this.currentMaterial = null;
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	
}
