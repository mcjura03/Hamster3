package de.hamster.simulation.view.multimedia.opengl.material;

import de.hamster.simulation.view.multimedia.opengl.material.Color;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * @author chris
 * 
 * Material, definiert die Oberflächeneigenschaften von 3D-objekten.
 * Enthält Farben und Texturen und wird im MaterialCOntroller verwaltet.  
 */
public class Material {

	// ambient: ohne abstand, ohne ausrichtung
    // diffuse: mit abstand und ausrichtung zur lichtquelle
    // specular: zusetzlich zu diffuse: ausrichtung zum betrachter (einfallswinkel vs. ausfallwinkel)
	private Color ambient; 
	private Color diffuse; 
	private Color specular; 
	private Color emission; 
    
    // exponent der shininess (glanz) (0-100) 
	private float shininess = 0.1f;
    
    // TODO: Material multitexturing-fähig machen.
    private int[] textureSet = new int[] {};
    
    private MaterialController mC;
    
    public static final int NOT_IN_USE = 0;
    public static final int IN_USE = 1;
    public static final int TO_BE_LOADED = 2;
    public static final int TO_BE_DELETED = 3;
    
    private int state = this.NOT_IN_USE;
        
    private int mainTextureID = 0;
    
    public Material() {
     	this.ambient = new Color( 0.5f, 0.5f, 0.5f, 1.0f);
     	this.diffuse = new Color( 0.5f, 0.5f, 0.5f, 1.0f);
     	this.specular = new Color( 0.0f, 0.0f, 0.0f, 1.0f);
        this.emission = new Color( 0.0f, 0.0f, 0.0f, 1.0f);
    	// default: aus
    }

    public void use(GLAutoDrawable gld) {
            	
        GL2 gl2 = gld.getGL().getGL2();
        
        // die Farbe immer ueberschreiben 
        gl2.glMaterialfv( GL.GL_FRONT, GL2.GL_AMBIENT,   this.ambient.getData(), 0);
        gl2.glMaterialfv( GL.GL_FRONT, GL2.GL_DIFFUSE,   this.diffuse.getData(), 0);
        gl2.glMaterialfv( GL.GL_FRONT, GL2.GL_SPECULAR,  this.specular.getData(),0);        	
        gl2.glMaterialf ( GL.GL_FRONT, GL2.GL_SHININESS, this.shininess           );
              
        if (this.mC.getCurrentMaterial() != this) {
	    	        	
        	this.mC.setCurrentMaterial(this);
        	
        	if (this.getNumberOfTextures() > 0) this.mC.useTextureSet(this.textureSet, gld, this.mainTextureID);
        	else this.mC.disableTextures(gl2);
       	        	
        }
    }

    /**
     * wird jetzt direkt gemacht.
     * @deprecated
     */
    public void useMaterialColorW(GL2 gl2, Color baseColor) {
        gl2.glMaterialfv( GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE,   baseColor.getData(), 0);
    }

	public Color getEmission() {
		return emission;
	}

    public void setAmbientAndDiffuse(Color c) {
        this.ambient = new Color(c);
        this.diffuse = new Color(c);
    }
    
    public void setAmbient(Color c) {
        this.ambient = new Color(c);
    }
    public void setDiffuse(Color c) {
        this.diffuse = new Color(c);
    }
    
	public void setEmission(Color c) {
		this.emission = new Color(c);
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public Color getSpecular() {
		return specular;
	}

	public void setSpecular(Color c) {
		this.specular = new Color(c);
	}

    /**
     * @return Returns the textur.
     */
    public int getTextureID(int index) {
        return textureSet[index];
    }
    
    public int getNumberOfTextures() {
    	return this.textureSet.length;
    }
    public Color getDiffuse() {
        return this.diffuse;
    }

    /**
     * @param texturid
     */
    public void addTexture(int textureID) {
    	    	
        int[] newTextures = new int[this.textureSet.length + 1];
        for (int i = 0; i < this.textureSet.length; i++) {
        	newTextures[i] = this.textureSet[i];
        }
    	newTextures[newTextures.length - 1] = textureID;
        this.textureSet = newTextures;
    }
    
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

    /**
     * @return mainTexture.
     */
    public int getMainTextureID() {
        return mainTextureID;
    }

    /**
     * @param mainTexture 
     */
    public void setMainTextureID(int mainTexture) {
        this.mainTextureID = mainTexture;
    }

    public boolean isTransparent() {

        if (this.ambient.getData()[3] < 1.f || this.diffuse.getData()[3] < 1.f ) return true;        
        return false;
    }

	public MaterialController getMC() {
		return mC;
	}

	public void setMC(MaterialController mc) {
		mC = mc;
	}
    
	public String toString() {
		
		String s = "Material:\n";
		
		if (this.textureSet.length > 0) s = s + this.mC.textures.get(this.textureSet[0]).getFilename()+ ", \n";
		s = s + "Ambient: " + this.ambient.toString() + "\n";
		s = s + "Diffuse: " + this.diffuse.toString() + "\n";
		s = s + "Specular: " + this.specular.toString() + "\n";
		s = s + "Emmission: " + this.emission.toString() + "\n";		
		s = s + "Shininess: " + this.shininess+ "\n";
		
		return s;
	}
}

