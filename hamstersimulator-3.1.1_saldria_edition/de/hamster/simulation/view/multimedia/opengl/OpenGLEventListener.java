package de.hamster.simulation.view.multimedia.opengl;

/**
 * @author chris
 * @date 05.2007
 * 
 * Der OpenGLListener, der die Callbacks für die Steuerung der 3D-Ausgabe
 * implementiert. Er ist auch Keyboard- und Maus-Listener für den
 * 3D-Canvas.
 */

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.simulation.model.SimulationModel;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class OpenGLEventListener implements GLEventListener, KeyListener,
		MouseListener, MouseMotionListener, MouseWheelListener {

	GLCanvas canvas = null;

	Animator animator = null;

	J3DFrame mainFrame = null;

	Scene scene = null;

	private boolean rightMousePressed = false;

	private boolean leftMousePressed = false;

	float perspectiveDist = -12f;

	float perspectiveAngle = 22f;

	float perspectiveRotate = 0f;

	float perspectiveTranslateX = 0f;

	float perspectiveTranslateY = 0f;

	private float aspect = 4f / 3f;

	private float near = 0.1f;

	private float far = 1000f;

	private int width = 0;

	private int height = 0;

	private SimulationModel simModel;

	private DebuggerModel debModel;

	private int mouseClickX;

	private int mouseClickY;

	private int mouseX;

	private int mouseY;

	public OpenGLEventListener(J3DFrame mainFrame, GLCanvas c, Animator a,
			SimulationModel simModel, DebuggerModel debModel) {

		this.mainFrame = mainFrame;
		this.canvas = c;
		this.animator = a;
		this.simModel = simModel;
		this.debModel = debModel;
		// listener anhängen:
		this.canvas.addKeyListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMotionListener(this);
		this.canvas.addMouseWheelListener(this);

	}

	public void display(GLAutoDrawable gld) {

		if (!OpenGLController.getInstance().isRunning())
			return;

		if (simModel.blockingFlag)
			return;

		GL2 gl2 = gld.getGL().getGL2();

		gl2.glDepthMask(true);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		long time = System.currentTimeMillis();

		// wir füttern die kamera mit den korrekten werten
		this.scene.setPerspectiveCamPosition(this.perspectiveAngle,
				this.perspectiveRotate, this.perspectiveDist,
				this.perspectiveTranslateX, this.perspectiveTranslateY);

		this.scene.draw(gld, time);

		try {
			Thread.sleep(this.scene.getLoopDelay());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void displayChanged(GLDrawable gld, boolean arg1, boolean arg2) {

		if (!OpenGLController.getInstance().isRunning())
			return;

		GLU glu = new GLU(); // gld.getGLU();

		// perspektive anpassen:
		glu.gluPerspective(40.0f, this.aspect, this.near, this.far);

	}

	public void init(GLAutoDrawable gld) {

		GL2 gl2 = gld.getGL().getGL2();
		GLU glu = new GLU(); // gld.getGLU();

		this.scene = new Scene(this.simModel, this.debModel, gld);
		this.scene.setPerspectiveCamPosition(this.perspectiveAngle,
				this.perspectiveRotate, this.perspectiveDist,
				this.perspectiveTranslateX, this.perspectiveTranslateY);

		// perspektive anpassen:
		glu.gluPerspective(40.0f, this.aspect, this.near, this.far);

		gl2.glEnable(GL.GL_DEPTH_TEST);
		gl2.glFrontFace(GL.GL_CCW);
		// gl.glEnable(GL.GL_LIGHTING);
		gl2.glEnable(GL.GL_CULL_FACE);
		// gl.glEnable(GL.GL_NORMALIZE);
		gl2.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl2.glClearColor(0.4f, 0.7f, 1.0f, 0.0f);
		float fogColor[] = { 0.1f, 0.2f, 0.5f, 1f };
		gl2.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0);
		gl2.glFogf(GL2.GL_FOG_DENSITY, 0.01f);
		gl2.glFogi(GL2.GL_FOG_MODE, GL.GL_LINEAR);
		gl2.glFogf(GL2.GL_FOG_START, 50f);
		gl2.glFogf(GL2.GL_FOG_END, 100f);
		gl2.glHint(GL2.GL_FOG_HINT, GL.GL_NICEST);
		gl2.glEnable(GL2.GL_FOG);

		// den hinweistext aus dem fenster entfernen:
		if (EventQueue.isDispatchThread()) {
			// System.out.println("111");
		} else {
			// System.out.println("222");
		}

		// den hinweistext aus dem fenster entfernen:
		if (EventQueue.isDispatchThread()) {
			this.mainFrame.getContentPane().remove(1);
			this.mainFrame.getContentPane().validate();
			this.mainFrame.setTitle("3D-Simulation");
		} else {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mainFrame.getContentPane().remove(1);
					mainFrame.getContentPane().validate();
					mainFrame.setTitle("3D-Simulation");
				}
			});
		}

		OpenGLController.getInstance().toggleSound();
		OpenGLController.getInstance().toggleGrid();

	}

	public void reshape(GLDrawable gl, int x, int y, int width, int height) {

		if (!OpenGLController.getInstance().isRunning())
			return;

		this.width = width;
		this.height = height;
		this.aspect = (this.height != 0) ? this.width / (float) this.height
				: 1f;

	}

	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.perspectiveAngle += 1.0f;
			if (this.perspectiveAngle > 89.99f)
				this.perspectiveAngle = 89.99f;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.perspectiveAngle -= 1.0f;
			if (this.perspectiveAngle < 10f)
				this.perspectiveAngle = 10f;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.perspectiveRotate += 2.0f;
			if (this.perspectiveRotate > 89.99f)
				this.perspectiveRotate = 89.99f;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.perspectiveRotate -= 2.0f;
			if (this.perspectiveRotate < -89.99f)
				this.perspectiveRotate = -89.99f;

		}

	}

	public void zoom(float val) {
		this.perspectiveDist += val;
		if (this.perspectiveDist < -25f)
			this.perspectiveDist = -25f;
		if (this.perspectiveDist > 5f)
			this.perspectiveDist = 5f;

		if (this.scene != null)
			this.scene.setPerspectiveCamPosition(this.perspectiveAngle,
					this.perspectiveRotate, this.perspectiveDist,
					this.perspectiveTranslateX, this.perspectiveTranslateY);
	}

	public void lookup(float val) {

		this.perspectiveAngle += val;
		if (this.perspectiveAngle > 89.99f)
			this.perspectiveAngle = 89.99f;
		if (this.perspectiveAngle < 10.f)
			this.perspectiveAngle = 10f;

		if (this.scene != null)
			this.scene.setPerspectiveCamPosition(this.perspectiveAngle,
					this.perspectiveRotate, this.perspectiveDist,
					this.perspectiveTranslateX, this.perspectiveTranslateY);
	}

	public void rotate(float val) {
		this.perspectiveRotate += val;

		if (this.scene != null)
			this.scene.setPerspectiveCamPosition(this.perspectiveAngle,
					this.perspectiveRotate, this.perspectiveDist,
					this.perspectiveTranslateX, this.perspectiveTranslateY);
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {

			this.scene.setFirstPersonView(!this.scene.isFirstPersonView());
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {

		if (e.getButton() == 1) {
			this.leftMousePressed = true;
		}
		if (e.getButton() == 3) {
			this.rightMousePressed = true;
		}
		this.mouseClickX = e.getX();
		this.mouseClickY = e.getY();

		this.mouseX = e.getX();
		this.mouseY = e.getY();

	}

	public void mouseReleased(MouseEvent e) {

		if (e.getButton() == 1)
			this.leftMousePressed = false;
		else if (e.getButton() == 3)
			this.rightMousePressed = false;

	}

	public void mouseDragged(MouseEvent e) {

		int transformateX = e.getX() - mouseX;
		int transformateY = mouseY - e.getY();

		if (rightMousePressed && !leftMousePressed) {

			this.lookup(transformateY);
			this.rotate(transformateX);

		}

		if (leftMousePressed && !rightMousePressed) {

			// wir rechnen hier die rotation mit ein, damit man auch eine
			// gedrehte
			// karte intuitiv an den senkrechten den bildschirm- und nicht an
			// den
			// rotierten karten-achsen verschiebt:

			double x = transformateX
					* Math.cos(perspectiveRotate * Math.PI / 180);
			x -= transformateY * Math.sin(perspectiveRotate * Math.PI / 180);
			x *= 0.05;
			this.perspectiveTranslateX -= x;

			if (this.perspectiveTranslateX > 14.f)
				this.perspectiveTranslateX = 14f;
			if (this.perspectiveTranslateX < -14.f)
				this.perspectiveTranslateX = -14f;

			double y = transformateX
					* Math.sin(perspectiveRotate * Math.PI / 180);
			y += transformateY * Math.cos(perspectiveRotate * Math.PI / 180);
			y *= 0.05;
			this.perspectiveTranslateY -= y;

			if (this.perspectiveTranslateY > 10.f)
				this.perspectiveTranslateY = 10f;
			if (this.perspectiveTranslateY < -10.f)
				this.perspectiveTranslateY = -10f;

		}
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {

		this.zoom(e.getWheelRotation());

		/*
		 * this.perspectiveDist += (e.getWheelRotation() 1.0f); if
		 * (this.perspectiveDist < -50f) this.perspectiveDist = -50f; if
		 * (this.perspectiveDist > 45f) this.perspectiveDist = 45f;
		 * 
		 * this.scene.setPerspectiveCamPosition(this.perspectiveAngle,
		 * this.perspectiveRotate, this.perspectiveDist,
		 * this.perspectiveTranslateX, this.perspectiveTranslateY);
		 */
	}

	public Scene getScene() {
		return scene;
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		canvas.repaint(); // keep it simple ...
	}

}
