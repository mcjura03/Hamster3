package de.hamster.scratch;

/**
 * Eine Representation eine BoundingBox, mit
 * diversen Hilfsmethoden, wie HitTest und
 * Vereinigung.
 * @author HackZ
 *
 */
public class BoundingBox {
	private int x;
	private int y;
	private int width;
	private int height;
	
	/**
	 * Erstellt eine neue BoundingBox mit den übergebenen
	 * Parametern.
	 * @param x
	 * x-Position der Box
	 * @param y
	 * y-Position der Box
	 * @param width
	 * Breite der Box
	 * @param height
	 * Höhe der Box
	 */
	public BoundingBox(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Testet, ob die übergebene Koordinate in dieser
	 * Bounding Box enthalten ist.
	 * @param x
	 * x-Koordinate.
	 * @param y
	 * y-Koordinate.
	 * @return
	 * true, wenn die Koordinate in der BoundingBox liegt.
	 */
	public boolean hitTest(int x, int y) {
		if (x < this.x || x > this.x + width)
			return false;
		
		if (y < this.y || y > this.y + height)
			return false;
		
		return true;
	}
	
	/**
	 * Testet, ob die übergebene BoundingBox diese
	 * BoundingBox berühr(schneidet).
	 * @param b
	 * zweite BoundingBox mit der getestet werden soll.
	 * @return
	 * true, wenn beide BoundingBoxen einen gemeinsamen
	 * Schnitt haben.
	 */
	public boolean hitTest(BoundingBox b) {
		BoundingBox unionB = union(this, b);
		return (width + b.width > unionB.width && height + b.height > unionB.height);
	}
	
	/**
	 * Liefert die Vereinigung zweier BoundingBoxen
	 * (Box, in die beide reinpassen)
	 * @param a
	 * @param b
	 */
	public static BoundingBox union(BoundingBox a, BoundingBox b) {
		int xl = a.x;
		int yo = a.y;
		int xr = b.x + b.width;
		int yu = b.y + b.height;
		if (b.x < xl) xl = b.x;
		if (b.y < yo) yo = b.y;
		if (a.x + a.width > xr) xr = a.x + a.width;
		if (a.y + a.height > yu) yu = a.y + a.height;
		
		return new BoundingBox(xl, yo, xr - xl, yu - yo);
	}
	
	/**
	 * Ändert die Position dieser BoundingBox.
	 * @param x
	 * x-Koordinate.
	 * @param y
	 * y-Koordinate.
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Liefert die x-Koordinate dieser BoundingBox.
	 * @return
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Liefert die y-Koordinate dieser BoundingBox.
	 * @return
	 */
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Ändert die Größe dieser BoundingBox auf die
	 * übergebenen Parameter.
	 * @param width
	 * Neue Breite.
	 * @param height
	 * Neue Höhe.
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Liefert die Breite dieser BoundingBox.
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Liefert die Höhe dieser BoundingBox.
	 * @return
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Ändert (verschiebt) die Koordinate dieser
	 * BoundingBox um die übergebenen Parameter
	 * @param x
	 * Delta x
	 * @param y
	 * Delta y
	 */
	public void add(int x, int y) {
		this.x += x;
		this.y += y;
	}
}
