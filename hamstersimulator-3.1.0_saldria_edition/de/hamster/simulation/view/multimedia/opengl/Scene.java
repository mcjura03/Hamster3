/**
 * @author chris
 * @date 06.2007
 */
package de.hamster.simulation.view.multimedia.opengl;

import java.util.Observable;
import java.util.Observer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLAutoDrawable;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.simulation.model.Hamster;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.view.multimedia.opengl.material.Color;
import de.hamster.simulation.view.multimedia.opengl.material.Material;
import de.hamster.simulation.view.multimedia.opengl.material.MaterialController;
import de.hamster.simulation.view.multimedia.opengl.material.Texture;
import de.hamster.simulation.view.multimedia.opengl.objects.Animation;
import de.hamster.simulation.view.multimedia.opengl.objects.Factory;
import de.hamster.simulation.view.multimedia.opengl.objects.LightAbstract;
import de.hamster.simulation.view.multimedia.opengl.objects.LightDirectional;
import de.hamster.simulation.view.multimedia.opengl.objects.LightPositional;
import de.hamster.simulation.view.multimedia.opengl.objects.MeshAlt;
import de.hamster.simulation.view.multimedia.realtimesimulation.RealtimeSimulation;
import de.hamster.simulation.view.multimedia.sound.*;

/**
 * Diese Klasse enthält die Daten für die Darstellung der Hamsterwelt und die
 * Positionen. Sie enthält die Daten in aufbereiteter Form für die Darstellung
 * in 3D. (Interpolierte Hamsterposition, etc.)
 * 
 * Diese Klasse enthält die technischen Aspekte der Szene (Kamera etc.),
 * inhaltliche sind in der Hamsterworld.
 */

public class Scene implements Observer {

	private boolean firstPersonView = false;

	private SimulationModel simmodel = null;

	private RealtimeSimulation positions;

	private HamsterWorld hamsterWorld;

	public Camera camera = null;

	private int loopDelay = 2;

	private MaterialController materialController = null;

	LightAbstract light1;
	LightAbstract light2;

	float x = 0f;

	public Scene(SimulationModel simmodel, DebuggerModel debmodel,
			GLAutoDrawable gld) {

		this.simmodel = simmodel;
		simmodel.addObserver(this);

		// kamera erstellen:
		this.camera = new Camera(gld);

		this.positions = new RealtimeSimulation(simmodel, debmodel, this);

		this.light1 = new LightPositional(0, "Licht 0");
		this.light1.setPos(10.0f, 1.0f, 7.0f);
		this.light1.setDiffuse(new Color(0.2f, 0.2f, 0.2f));
		this.light1.setAmbient(new Color(0.8f, 0.8f, 0.8f));

		this.materialController = new MaterialController();

		this.hamsterWorld = new HamsterWorld(materialController);

		// hier haben wir einen gl-kontext, können also die texturen laden
		// lassen:
		this.materialController.doTextureRefresh(gld);

	}

	public void setPerspectiveCamPosition(float neigung, float rotation,
			float dist, float transX, float transY) {

		float b = this.simmodel.getTerrain().getWidth();
		float h = this.simmodel.getTerrain().getHeight();
		float max = h + b;

		float d = dist + ((90 - neigung) / 10) + max;

		float y = (float) Math.sin(neigung * Math.PI / 180) * (d);
		float z = (float) Math.cos(neigung * Math.PI / 180) * (d);

		this.camera.clearTransformation();

		// diese werte errechnen sich aus der kartengröße und zentrieren die
		// karte:
		this.camera.strafeRight(b / 2);
		this.camera.moveForward(h / 2 * -1);

		// falls der user verschieben will:
		this.camera.strafeRight(transX);
		this.camera.moveForward(transY);

		this.camera.rotateY(rotation);

		this.camera.moveUpward(y);
		this.camera.moveForward(-z);

		this.camera.rotateX(neigung * -1f);

	}

	private void updateFirstPersonCamera() {

		this.camera.clearTransformation();
		float x = positions.getInterpolatedX(-1);
		float z = positions.getInterpolatedY(-1);

		float r = positions.getInterpolatedDir(-1) * 90f * -1;

		this.camera.setPosition(x + 0.5f, 1.4f, z + 0.5f);

		this.camera.rotateY(r);
		this.camera.rotateX(-10f);

		this.camera.moveForward(-4.0f);

	}

	/**
	 * 
	 * Zeichnet die Szene. Erhält Systemzeit als Parameter um Animationen zu
	 * interpolieren.
	 */
	public void draw(GLAutoDrawable gld, long time) {

		GL gl = gld.getGL();

		// die positionen anhand der verstrichenen Zeit interpolieren:
		this.positions.interpolatePositions(time);

		// ggf. kamera für firstpeson-view anpassen:
		if (this.firstPersonView) {

			this.updateFirstPersonCamera();
		}

		this.camera.applyTransformation();

		this.light1.enable(gld);

		// die hamsterwelt zeichnen:
		this.hamsterWorld.draw(gld, positions, time);

		this.light1.disable(gld);

		// und die modelviewmatrix wieder in den ursprünglichen zustand bringen:
		this.camera.restoreTransformation();

	}

	public void update(Observable o, Object arg) { 

		// neuen zustand merken:
		if (positions != null) {
			positions.addStage();
		}
	}

	public boolean isFirstPersonView() {
		return firstPersonView;
	}

	public void setFirstPersonView(boolean firstPersonView) {
		this.firstPersonView = firstPersonView;
	}

	public void setLoopDelay(int i) {
		this.loopDelay = i;
	}

	public int getLoopDelay() {
		return this.loopDelay;
	}

	public HamsterWorld getHamsterWorld() {
		return hamsterWorld;
	}

	public RealtimeSimulation getRealtimeSimulation() {
		return positions;
	}

}
