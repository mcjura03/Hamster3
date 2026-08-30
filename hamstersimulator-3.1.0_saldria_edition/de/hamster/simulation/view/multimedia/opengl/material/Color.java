package de.hamster.simulation.view.multimedia.opengl.material;

/**
 * @author chris
 * 
 * einfache Farb-Klasse
 * 
 */

public class Color {

    private float[] data;

    public Color() {
        this.data = new float[] { 0.5f, 0.5f, 0.5f, 1f };
    }

    public Color(float r, float g, float b, float a) {
        this.data = new float[] { r, g, b, a };
    }

    public Color(float r, float g, float b) {
        this.data = new float[] { r, g, b, 1f };
    }
    
    public Color(java.awt.Color c) {
    	this(c.getRed(), c.getGreen(), c.getBlue());
    }

    public Color(Color c) {
    	this();
        this.data[0] = c.getData()[0];
        this.data[1] = c.getData()[1];
        this.data[2] = c.getData()[2];
        this.data[3] = c.getData()[3];
    }

    public boolean equals(Object o) {
        try {
            Color c = (Color) o;
            for (int i = 0; i < 4; i++) {
                if (c.getData()[i] != this.getData()[i])
                    return false;
            }
            return true;

        } catch (Exception x) {

        }
        return false;
    }

    public String toString() {
        return "Color (RGB): (" + data[0] + ", " + data[1] + ", " + data[2] + ")";
    }

    public boolean isTransparent() {
        return (this.data[3] != 1);
    }

    public float[] getData() {
        return data;
    }
    
    public float getValue(int i) {
    	return this.data[i];
    }

    public void setValue(int i, float v) {
    	this.data[i] = v;
    }

	public void setData(float[] data) {
		this.data = data;
	}

}
