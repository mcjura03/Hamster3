package de.hamster.simulation.view.multimedia.opengl;

/**
 * @author chris
 * 
 * Enthält die Definition der Hamsterwelt und die Methoden, sie zu zeichnen.
 * Hier werden die Objekte, Materialien und Texturen geladen und verwaltet.
 */

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.view.multimedia.opengl.material.Color;
import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.material.MaterialController;
import de.hamster.simulation.view.multimedia.opengl.material.Texture;
import de.hamster.simulation.view.multimedia.opengl.objects.Animation;
import de.hamster.simulation.view.multimedia.opengl.objects.Factory;
import de.hamster.simulation.view.multimedia.opengl.objects.Heightmap;
import de.hamster.simulation.view.multimedia.opengl.objects.Mesh;
import de.hamster.simulation.view.multimedia.opengl.objects.MeshAlt;
import de.hamster.simulation.view.multimedia.opengl.objects.Obj;
import de.hamster.simulation.view.multimedia.realtimesimulation.RealtimeSimulation;

import java.util.ArrayList;
import java.util.Random;

public class HamsterWorld {

	private MaterialController mC = null;

	private MeshAlt korn;
	private MeshAlt mauer;
	private Mesh terrain;

	private Color defaultColor = new Color(1f, 1f, 1f);
	/*
	 * private Color hamsterColors[] = {new Color (1f, 0f, 0f), new Color (0f,
	 * 1f, 0f), new Color (1f, 1f, 0f), new Color (0f, 1f, 1f), new Color (1f,
	 * 0f, 1f) };
	 */
	private Color hamsterColors[] = { new Color(java.awt.Color.BLUE),
			new Color(java.awt.Color.RED), new Color(java.awt.Color.GREEN),
			new Color(java.awt.Color.YELLOW), new Color(java.awt.Color.CYAN),
			new Color(java.awt.Color.MAGENTA),
			new Color(java.awt.Color.ORANGE), new Color(java.awt.Color.PINK),
			new Color(java.awt.Color.GRAY), new Color(java.awt.Color.WHITE) };

	// die objekte für den Default- und die Extra-Hamster:
	private Obj hamster = null;
	private ArrayList<Obj> hamsters = null;

	private Random rand;

	private Heightmap map = null;
	private Heightmap untergrund = null;
	private int width, height;

	private boolean showGrid = true;

	public HamsterWorld(MaterialController mC) {
		this.mC = mC;
		this.rand = new Random();
		this.hamsters = new ArrayList<Obj>();
		this.createObjects();

	}

	private void createObjects() {

		// ein körner-material erstellen:
		Material m1 = new Material();
		m1.setAmbientAndDiffuse(new Color(0.8f, 0.8f, 0f));
		mC.defineAndRequestMaterial(m1);

		this.korn = Factory.createCuboid(0.1f, 0.1f, 0.1f, -1);
		this.korn.setMaterial(m1);

		// ein mauer-material erstellen:
		Material m2 = new Material();
		m2.setAmbientAndDiffuse(new Color(0.9f, 0.9f, 0.9f));
		// m2.setSpecular(new Color(0.0f,0.0f, 0.0f));
		mC.defineTexture(new Texture("data/mauer.png"));
		m2.addTexture(mC.getLastTextureIndex());
		mC.defineAndRequestMaterial(m2);

		this.mauer = Factory.createCuboid(1f, 1f, 0.5f, -1);
		this.mauer.setMaterial(m2);

		// eine gras-material erstellen:
		Material m3 = new Material();
		mC.defineTexture(new Texture("data/gras.png"));
		m3.setAmbientAndDiffuse(new Color(0.9f, 0.9f, 0.9f));
		// m3.setSpecular(new Color(0.9f, 0.9f, 0.9f));
		m3.addTexture(mC.getLastTextureIndex());
		mC.defineAndRequestMaterial(m3);

		// noch eine gras-material erstellen:
		Material m4 = new Material();
		mC.defineTexture(new Texture("data/gras2.png"));
		m4.setAmbientAndDiffuse(new Color(0.9f, 0.9f, 0.9f));
		m4.addTexture(mC.getLastTextureIndex());
		mC.defineAndRequestMaterial(m4);

		this.map = new Heightmap(mC, 48, 48, 4f, 1.35f, 0, 0f);
		this.map.setMaterialID(2);

		this.hamster = new Obj(mC);
		this.hamster.loadObj("data/hamster.obj");

		this.hamster.startAnimating(System.currentTimeMillis());
	}

	public void draw(GLAutoDrawable gld, RealtimeSimulation positions, long time) {

		GL2 gl = gld.getGL().getGL2();

		SimulationModel model = positions.getSim();

		if (model.getTerrain().getWidth() != this.width
				|| model.getTerrain().getHeight() != this.height) {
			// die karte hat sich geändert, es muss gleich ne neue her:
			this.untergrund = null;
		}

		this.width = model.getTerrain().getWidth();
		this.height = model.getTerrain().getHeight();
		float ww = (float) model.getTerrain().getWidth() / 2f;
		float hh = (float) model.getTerrain().getHeight() / 2f;

		// landschaft:
		gl.glPushMatrix();
		gl.glTranslatef(ww, -2.5f, hh);
		this.map.draw(gld, time);
		gl.glPopMatrix();

		// untergrund des hamsters:
		if (this.untergrund == null) {

			this.untergrund = new Heightmap(mC, width + 1, height + 1, 1,
					0.09f, 3, 3f);
			this.untergrund.setMaterialID(3);
			this.untergrund.showGrid(true);

		}
		gl.glPushMatrix();
		gl.glTranslatef(ww + 0.5f, -0.07f, hh + 0.5f);
		this.untergrund.draw(gld, time);
		if (this.showGrid) {
			this.mC.unsetMaterial(gl);
			gl.glDepthRange(0.0f, 0.99999f);
			gl.glTranslatef(0f, 0.006f, 0f);
			this.untergrund.drawGrid(gld);
			gl.glDepthRange(0.00001f, 1f);
		}
		gl.glPopMatrix();

		// körner und mauern zeichnen:
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				this.rand.setSeed((i + 1) * (j + 1));

				int koerner = positions.getTerrain().getCornCount(i, j);
				for (int k = 0; k < koerner; k++) {
					float x = (float) (i + 0.25f)
							+ (float) (this.rand.nextFloat() % 0.6);
					float z = (float) (j + 0.25f)
							+ (float) (this.rand.nextFloat() % 0.6);

					gl.glPushMatrix();
					gl.glTranslatef(x, 0.05f, z);
					korn.draw(gld, time);
					gl.glPopMatrix();
				}

				if (positions.getTerrain().getWall(i, j)) {

					float x = (float) (i);
					float z = (float) (j);

					gl.glPushMatrix();
					gl.glTranslatef(0.5f + x, 0.2f, 0.5f + z);
					mauer.draw(gld, time);
					gl.glPopMatrix();

				}
			}
		}

		// alle hamster im entry durchlaufen. mit -1 für den default hamster
		// anfagen:
		Obj h = null;
		for (int i = -1; i < positions.getNumberOfHamsters() - 1; i++) {

			// den jeweiligen Hamster wählen. Achtung: Im Multi-Hamster-Modus
			// können zur Laufzeit neue Hamster entstehen. Wenn wir also noch
			// nicht genug
			// Hamsterobjekte erzeugt haben, müssen wir das jetzt tun.
			h = this.hamster;
			if (i >= 0) {

				if (i >= this.hamsters.size()) {
					this.hamsters.add(this.hamster.cloneWithSharedMeshes());
				}
				h = this.hamsters.get(i);

			}

			// hamster zeichnen:
			float x = positions.getInterpolatedX(i);
			float z = positions.getInterpolatedY(i);

			// farben und animationen setzen:
			if (positions.getInterpolatedMoves(i))
				h.setAnimation(1);
			else
				h.setAnimation(0);

			de.hamster.simulation.model.Hamster ham = model.getHamster(i);
			if (ham == null) continue;
			int id = ham.getId();
			int color = ham.getColor();
			Color col = null;

			if (id <= -1) {
				col = this.defaultColor;
			} else if (color <= -1) {
				if (id + 1 < this.hamsterColors.length)
					col = this.hamsterColors[id + 1];
				else
					col = this.hamsterColors[this.hamsterColors.length - 1];
			} else if (color >= this.hamsterColors.length) {
				col = this.hamsterColors[this.hamsterColors.length - 1];
			} else {
				col = this.hamsterColors[color];
			}

			// if (i >= 0 && i < this.hamsterColors.length) {
			// h.setAmbientAndDiffuse(0, this.hamsterColors[i]);
			// h.setAmbientAndDiffuse(3, this.hamsterColors[i]);
			// h.setAmbientAndDiffuse(4, this.hamsterColors[i]);
			// } else {
			// h.setAmbientAndDiffuse(0, this.defaultColor);
			// h.setAmbientAndDiffuse(3, this.defaultColor);
			// h.setAmbientAndDiffuse(4, this.defaultColor);
			// }

			h.setAmbientAndDiffuse(0, col);
			h.setAmbientAndDiffuse(3, col);
			h.setAmbientAndDiffuse(4, col);

			gl.glPushMatrix();
			gl.glTranslatef(x + 0.45f, 0.0f, z + 0.5f);
			gl.glRotatef(180f + positions.getInterpolatedDir(i) * 90f * -1, 0f,
					1f, 0f);

			gl.glScalef(0.005f, 0.005f, 0.005f);
			h.draw(gld, time);

			gl.glPopMatrix();

		}
	}

	public void toggleGrid(boolean b) {
		this.showGrid = b;

	}

}
