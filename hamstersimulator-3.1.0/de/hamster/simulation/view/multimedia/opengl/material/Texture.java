package de.hamster.simulation.view.multimedia.opengl.material;

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import de.hamster.simulation.view.multimedia.opengl.OpenGLController;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.gl2.GLUgl2;

/**
 * @author Chris
 * 
 * Textur, über eine Bitmapgrafik definiert. Kann Texturen mit und ohne Alphakanal enthalten.
 * 
 */
public class Texture {

    private String filename;
    private int glTextureID = -1;
    //private int internID = -1;
    private int textureType = GL2.GL_TEXTURE_2D;
    private int height;    
    private int width;
    private int texWidth;
    private int texHeight;
    private float widthRatio;
    private float heightRatio;
    
    private int minFilter = GL2.GL_LINEAR_MIPMAP_LINEAR;
    private int magFilter = GL2.GL_LINEAR;
    private int wrapModeS = GL2.GL_CLAMP_TO_EDGE;
    private int wrapModeT = GL2.GL_CLAMP_TO_EDGE;   
    
    private boolean blend = false;
    
    public void setWrapMode(int m) {
        this.wrapModeS = m;
        this.wrapModeT = m;       	
    }

    
    
    public Texture(String filename) {
    	this.filename = filename;
    }

    public void loadTexture(GLAutoDrawable gld) {
    	
    	
        try {
            GL2 gl2 = gld.getGL().getGL2();
            
            if (this.glTextureID == -1) {
            	
	            IntBuffer temp = IntBuffer.allocate(1);
	            gl2.glGenTextures(1, temp);
	            this.glTextureID = temp.get(0);
            }
           
                
            gl2.glBindTexture(this.textureType, this.glTextureID);

            BufferedImage buffer;

            buffer = loadImage(this.filename);

            this.width = get2Fold(buffer.getWidth());
            this.height = get2Fold(buffer.getHeight());

            int srcPixelFormat = 0;
            if (buffer.getColorModel().hasAlpha()) {
                srcPixelFormat = GL2.GL_RGBA;
            } else {
                srcPixelFormat = GL2.GL_RGB;
            }

            ByteBuffer textureBuffer = convertImageData(buffer);

            int wrapMode = GL2.GL_REPEAT;

            gl2.glTexParameteri(textureType, GL2.GL_TEXTURE_WRAP_S, wrapMode);
            gl2.glTexParameteri(textureType, GL2.GL_TEXTURE_WRAP_T, wrapMode);
            gl2.glTexParameteri(textureType, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            gl2.glTexParameteri(textureType, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            
            GLUgl2 glu2 = new GLUgl2(); // GLU glu = gld.getGLU();

            glu2.gluBuild2DMipmaps(textureType, srcPixelFormat, this.width, this.height, srcPixelFormat,
                    GL2.GL_UNSIGNED_BYTE, textureBuffer);

        } catch (Exception ex) {
               ex.printStackTrace();
        }

    }
    
    public void unloadTexture(GLAutoDrawable gld) {

    	GL gl = gld.getGL();
    	
    	// TODO: unLoadTexture() implementieren, texturen werden bisher noch nicht wieder rausgeworfen 
    }
    

    private ByteBuffer convertImageData(BufferedImage bufferedImage) throws IOException {
        ByteBuffer imageBuffer = null;
        WritableRaster raster;
        BufferedImage texImage;

        int tWidth = get2Fold(bufferedImage.getWidth());
        int tHeight = get2Fold(bufferedImage.getHeight());

        this.texHeight = tHeight;
        this.texWidth = tWidth;
        
        ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8, 8, 8, 8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
                
        ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8, 8, 8, 0}, 
                false, 
                false, 
                ComponentColorModel.OPAQUE, 
                DataBuffer.TYPE_BYTE);
                
        if (bufferedImage.getColorModel().hasAlpha()) {
        	this.blend = true;
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        } else {
        	this.blend = false;
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
        }

        Graphics g = texImage.getGraphics();
        g.drawImage(bufferedImage, 0, 0, null);

        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.rewind();	// rewind to the beginning, so that the bytes are readable

        return imageBuffer;
    }

    private BufferedImage loadImage(String ref) throws IOException {
        
        File file = new File(ref);
        
        if (!file.isFile()) {
            throw new IOException("Cannot find: " + ref);
        }
        BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(new FileInputStream(file)));
        return bufferedImage;
    }

    public void useTexture(GLAutoDrawable gld) {
        GL gl = gld.getGL();
       
     
        
        gl.glBindTexture(this.textureType, this.glTextureID);
        
        gl.glTexParameteri(textureType, GL.GL_TEXTURE_WRAP_S, this.wrapModeS);
        gl.glTexParameteri(textureType, GL.GL_TEXTURE_WRAP_T, this.wrapModeT);
        gl.glTexParameteri(textureType, GL.GL_TEXTURE_MIN_FILTER, this.minFilter);
        gl.glTexParameteri(textureType, GL.GL_TEXTURE_MAG_FILTER, this.magFilter);
     
                
        
        gl.glDepthMask(true);
        if (this.blend){ 
	    	gl.glEnable(GL.GL_BLEND);		
	    	gl.glDepthMask(false);
        } else {
	    	gl.glDepthMask(true);
        	gl.glDisable(GL.GL_BLEND);		     	
        }

    }

    private int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }
    
    public boolean isInitialized() {
             
        return (this.glTextureID != -1);
    }

    /**
     * @return wrapModeS.
     */
    public int getWrapModeS() {
        return wrapModeS;
    }

    /**
     * @param wrapModeS The wrapModeS to set.
     */
    public void setWrapModeS(int wrapModeS) {
        this.wrapModeS = wrapModeS;
    }

    /**
     * @return Returns the wrapModeT.
     */
    public int getWrapModeT() {
        return wrapModeT;
    }

    /**
     * @param wrapModeT The wrapModeT to set.
     */
    public void setWrapModeT(int wrapModeT) {
        this.wrapModeT = wrapModeT;
    }



	public String getFilename() {
		return this.filename;
		
	}
    

}
 