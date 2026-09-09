package de.hamster.simulation.view.multimedia.opengl.math;


/**
 * @author chris
 *
 * 3D-Vector mit einigen nützlichen Methoden
 */
public class Vector3f {

    protected float data[];

    public Vector3f() {
        this.data = new float[] { 0f, 0f, 0f };
    }

    public Vector3f(float x, float y, float z) {
        this();
        data[0] = x;
        data[1] = y;
        data[2] = z;
    }

    public Vector3f(Vector3f v) {
    	
    	this();
        data[0] = v.data[0];
        data[1] = v.data[1];
        data[2] = v.data[2];        
    }

    public Vector3f(float[] data) {
    	
    	this();
        this.data[0] = data[0];
        this.data[1] = data[1];
        this.data[2] = data[2];
    }

    public void setX(float value) {
        data[0] = value;
    }

    public void setY(float value) {
        data[1] = value;
    }

    public void setZ(float value) {
        data[2] = value;
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
        data = new float[]{value[0], value[1], value[2]};
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

    public float getZ() {
        return data[2];
    }

    public float getValue(int index) {
        return data[index];
    }

    public Object clone() {
        return new Vector3f(this);
    }

    public boolean equals(Object o) {
        try {
            Vector3f v = (Vector3f) o;
            for (int i = 0; i < 3; i++) {
                if (v.getValue(i) != this.getValue(i))
                    return false;
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }
     
    public String toString() {
        return "Vector3f: (" + this.getX() + ", " + this.getY() + ", "
                + this.getZ() + ")\n";
    }


    public float length()
    {
    	return (float)(Math.sqrt(this.data[0]*this.data[0]+this.data[1]*this.data[1]+this.data[2]*this.data[2]));
    }
    
    public void normalize ()
    {
    	float l = this.length();
    	if (l == 0.0f) return;
    	this.data[0] = this.data[0] / l;
    	this.data[1] = this.data[1] / l;
    	this.data[2] = this.data[2] / l;
    }
    
    
    public void add(Vector3f v) {    	
    	this.add(v.data);  	
    }
    
    public void sub(Vector3f v) {    	
    	this.sub(v.data);  	
    }

    public void add(float[] v) {
    	this.data[0] += v[0];
    	this.data[1] += v[1];
    	this.data[2] += v[2];
    }

    public void sub(float[] v) {
    	this.data[0] -= v[0];
    	this.data[1] -= v[1];
    	this.data[2] -= v[2];
    }
  
    
    // Skalarmultiplikation eines Vektors
    public void sMult(float s) {

        for (int i = 0; i < 3; i++) {
            this.data[i] *=  s;
        }
    }
    
    public Vector3f getSMult(float s) {

    	Vector3f v = new Vector3f();
        for (int i = 0; i < 3; i++) {
            v.data[i] =  this.data[i] * s;
        }
        return v;
    }
        
    
    /**
     * setzt sich selbst auf das Kreuzprodukt der beiden gegebenen Vectoren
     */
    public void crossProduct(Vector3f a, Vector3f b) {
    	
    	this.data[0] = (a.data[1] * b.data[2]) - (a.data[2] * b.data[1]);
    	this.data[1] = (a.data[2] * b.data[0]) - (a.data[0] * b.data[2]);
    	this.data[2] = (a.data[0] * b.data[1]) - (a.data[1] * b.data[0]);
    }

	public void calcNormal3f(Vector3f a, Vector3f b, Vector3f c) {
				
		 Vector3f i = new Vector3f(b); 
		 i.sub(a);
		 
		 Vector3f j = new Vector3f(c);
		 j.sub(a);
		
		 Vector3f n = new Vector3f();
		 n.crossProduct(i, j);
		 		 
		 this.data = n.getData(); 
		  
		 this.normalize();
	}

	public void invert() {

		this.data[0] *= -1f;
    	this.data[1] *= -1f;
    	this.data[2] *= -1f;
	}

  
    
}
