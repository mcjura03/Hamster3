package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris
 * 
 * einfache Objekt-Factory für das alte Meshformat
 * 
 */
import de.hamster.simulation.view.multimedia.opengl.math.Vector2f;
import de.hamster.simulation.view.multimedia.opengl.math.Vector3f;

public class Factory {

public static MeshAlt createCuboid(float length, float width, float height, int materialID) {
		
		Triangle f;
		
		float ldiv = length / 2.0f;
		float wdiv = width / 2.0f;
		float hdiv = height / 2.0f;
		
		KeyframeAlt newKeyframe = new KeyframeAlt();
				
		// Vertices der Eckpunkte erzeugen
		// "vordere" Koordinaten
		// links oben
		Vector3f vlo = new Vector3f(-wdiv,hdiv,ldiv);
		// rechts oben
		Vector3f vro = new Vector3f(wdiv,hdiv,ldiv);
		// rechts unten
		Vector3f vru = new Vector3f(wdiv,-hdiv,ldiv);
		// links unten
		Vector3f vlu = new Vector3f(-wdiv,-hdiv,ldiv);
		// "hintere" Koordinaten
		// links oben
		Vector3f hlo = new Vector3f(-wdiv,hdiv,-ldiv);
		// rechts oben
		Vector3f hro = new Vector3f(wdiv,hdiv,-ldiv);
		// rechts unten
		Vector3f hru = new Vector3f(wdiv,-hdiv,-ldiv);
		// links unten
		Vector3f hlu = new Vector3f(-wdiv,-hdiv,-ldiv);

		// Triangles erzeugen
		// obere Seite
		f = new Triangle(hro, hlo, vlo);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.998f), new Vector2f(0.001f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		f = new Triangle(hro, vlo, vro);
		newKeyframe.addTriangle(f);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.001f));
		f.setMaterialID(materialID);
		// untere Seite
		f = new Triangle(vru, vlu, hlu);
		f.setTexCoords(new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.001f), new Vector2f(0.999f, 0.999f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		f = new Triangle(vru, hlu, hru);
		f.setTexCoords(new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.999f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		// vordere Seite
		f = new Triangle(vro, vlo, vlu);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.999f), new Vector2f(0.001f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		f = new Triangle(vro, vlu, vru);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		// hintere Seite
		f = new Triangle(hlo, hro, hlu);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.999f), new Vector2f(0.999f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		f = new Triangle(hro, hru, hlu);
		f.setTexCoords(new Vector2f(0.001f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		// linke Seite
		f = new Triangle(hlo, hlu, vlo);
		f.setTexCoords(new Vector2f(0.001f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.999f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		f = new Triangle(vlo, hlu, vlu);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		// rechte Seite
		f = new Triangle(vro, vru, hro);
		f.setTexCoords(new Vector2f(0.001f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.999f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);
		f = new Triangle(hro, vru, hru);
		f.setTexCoords(new Vector2f(0.999f, 0.999f), new Vector2f(0.001f, 0.001f), new Vector2f(0.999f, 0.001f));
		f.setMaterialID(materialID);
		newKeyframe.addTriangle(f);

		MeshAlt m = new MeshAlt();
		m.addKeyframe(newKeyframe);
				
		return m;
	}
	
	
	
}
