package de.hamster.simulation.view.multimedia.opengl;

/**
 * @author chris
 * @date 05.2007
 * 
 * Der OpenGLCOntroller ist ein Singleton, der wichtige Funktionen zur Steuerung
 * der OpenGL-Komponente enthält. Diese Funktionen werden aus den bestehenden
 * Komponenten heraus aufgerufen, zB um das 3D-Fenster zu erstellen. 
 */

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.view.multimedia.sound.MidiFile;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLDrawableFactory;

public class OpenGLController {

	private static OpenGLController instance = null;
	private static Workbench workbench = null;
	private static OpenGLEventListener listener = null;
	private static GLCanvas canvas = null;
	private static Animator animator;
	private boolean running;
	private boolean animatorRunning = false;
	private boolean playMusic = true;
	private MidiFile music = null;

	protected OpenGLController() {

	}

	public static OpenGLController getInstance() {
		if (instance == null) {
			instance = new OpenGLController();
		}
		return instance;
	}

	public void create3DView(J3DFrame mainFrame, Workbench wb,
			SimulationModel simModel, DebuggerModel debModel) {

		this.workbench = wb;

		// capabilities definieren
		GLProfile glProfile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(glProfile);
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);

		// damit einen GL-Canvas erstellen:
		// canvas =
		// GLDrawableFactory.getFactory(glProfile).createGLCanvas(capabilities);
		canvas = new GLCanvas(capabilities);

		// einen Hinweistext im Fenster unterbringen:
		// String state = "Lade 3D-Szene... bitte warten.";
		String state = Utils.getResource("windows.3dsimulation.wait");
		mainFrame.setTitle(state);
		JPanel p = new JPanel();
		JLabel label = new JLabel(state);
		p.add(label);
		mainFrame.getContentPane().add(BorderLayout.SOUTH, p);

		mainFrame.getContentPane().add(BorderLayout.CENTER, canvas);

		// animator erstellen:
		animator = new Animator(canvas);

		// listener erstellen und anhängen:
		canvas.addGLEventListener(listener = new OpenGLEventListener(mainFrame,
				canvas, animator, simModel, debModel));

		this.music = new MidiFile(simModel);

		this.toggleMusic();
	}

	public void setRunning(boolean r) {
		this.running = r;
		if (r)
			startAnimator();
		else
			stopAnimator();

	}

	public boolean isRunning() {
		return running;
	}

	private void startAnimator() {

		if (!this.animatorRunning) {
			this.animator.start();
			this.animatorRunning = true;
			if (this.playMusic) {
				this.music.play("data/music.mid");
			}
		}
	}

	private void stopAnimator() {

		if (this.animatorRunning) {
			this.animatorRunning = false;
			animator.stop();

			if (this.playMusic) {
				this.music.stop();
			}
		}
	}

	public void zoomIn() {
		listener.zoom(-3f);
	}

	public void zoomOut() {
		listener.zoom(3f);
	}

	public void rotateLeft() {
		listener.rotate(-4f);
	}

	public void rotateRight() {
		listener.rotate(4f);
	}

	public void lookUp() {
		listener.lookup(-4f);
	}

	public void lookDown() {
		listener.lookup(4f);
	}

	public void toggleGrid() {

		boolean b = workbench.getProperty("grid", "true").equals("true") ? true
				: false;

		listener.getScene().getHamsterWorld().toggleGrid(b);
	}

	public void toggleMusic() {

		this.playMusic = workbench.getProperty("music", "true").equals("true") ? true
				: false;

		if (!playMusic)
			this.music.stop();
		else {
			if (this.animatorRunning) {
				this.music.play("data/music.mid");
			}
		}
	}

	public void toggleSound() {

		boolean b = workbench.getProperty("sound", "true").equals("true") ? true
				: false;

		listener.getScene().getRealtimeSimulation().toggleSound(b);
	}

	public void togglePerspective() {

		listener.scene.setFirstPersonView(!listener.scene.isFirstPersonView());

	}
}
