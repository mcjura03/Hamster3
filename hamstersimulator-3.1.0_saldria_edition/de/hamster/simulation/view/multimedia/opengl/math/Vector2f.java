package de.hamster.simulation.view.multimedia.opengl.math;


/**
 * @author chris
 *
 * 2D-Vector
 */
public class Vector2f {

    protected float data[];

    public Vector2f() {
        this.data = new float[] { 0f, 0f };
    }

    public Vector2f(float x, float y) {
        this();
        data[0] = x;
        data[1] = y;
    }

    public Vector2f(Vector2f v) {
    	
    	this();
        data[0] = v.data[0];
        data[1] = v.data[1];  
    }

    public Vector2f(float[] data) {
    	
    	this();
        this.data[0] = data[0];
        this.data[1] = data[1];
    }

    public void setX(float value) {
        data[0] = value;
    }

    public void setY(float value) {
        data[1] = value;
    }

    public void setValue(int index, float value) {
        data[index] = value;
    }

    public void setValues(float x, float y, float z) {
        data[0] = x;
        data[1] = y;
        data[2] = z;
    }
    
    public void setData(float[] value) {
        data = new float[]{value[0], value[1]};
    }

    public float[] getData() {
        return data;
    }

    public float getX() {
        return data[0];
    }

    public float getY() {
        return data[1];
    }


    public float getValue(int index) {
        return data[index];
    }

    public Object clone() {
        return new Vector2f(this);
    }

    public boolean equals(Object o) {
        try {
            Vector3f v = (Vector3f) o;
            for (int i = 0; i < 2; i++) {
                if (v.getValue(i) != this.getValue(i))
                    return false;
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }
     
    public String toString() {
        return "Vector3f: (" + this.getX() + ", " + this.getY() + ")\n";
    }


    public float length()
    {
    	return (float)(Math.sqrt(this.data[0]*this.data[0]+this.data[1]*this.data[1]));
    }
    
    public void normalize ()
    {
    	float l = this.length();
    	if (l == 0.0f) return;
    	this.data[0] = this.data[0] / l;
    	this.data[1] = this.data[1] / l;
    }
    
    
    public void add(Vector2f v) {    	
    	this.add(v.data);  	
    }
    
    public void sub(Vector2f v) {    	
    	this.sub(v.data);  	
    }

    public void add(float[] v) {
    	this.data[0] += v[0];
    	this.data[1] += v[1];
    }

    public void sub(float[] v) {
    	this.data[0] -= v[0];
    	this.data[1] -= v[1];
    }

    
    
    /**
     * Skalarmultiplikation eines Vektors
     * @param a 1. Vektor
     * @param s Skalar
     * @return skalierter Vektor
     */
    public void sMult(float s) {

        for (int i = 0; i < 2; i++) {
            this.data[i] *=  s;
        }
    }
    
    public Vector2f getSMult(float s) {

    	Vector2f v = new Vector2f();
        for (int i = 0; i < 2; i++) {
            v.data[i] =  this.data[i] * s;
        }
        return v;
    }
        
    
    

}
