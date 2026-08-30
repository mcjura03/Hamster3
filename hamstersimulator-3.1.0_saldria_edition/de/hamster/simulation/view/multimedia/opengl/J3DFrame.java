package de.hamster.simulation.view.multimedia.opengl;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

/**
 * @author chris
 * 
 * ein einfacher, abgeleiteter Frame: Wir brauchen Zugriff auf die setVisible-Methode
 * um eigene Aktionen damit zu verknüpfen.
 *
 */
public class J3DFrame extends JFrame {

	/**
	 * @throws HeadlessException
	 */
	public J3DFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	// nur dafür haben wir diese klasse abgeleitet:
	// wir müssen den OpenGLController starten und stoppen können.
	// während es beim alten SImulator reichte, das Fenster zu verstecken:
	public void setVisible(boolean b) {
		super.setVisible(b);
		OpenGLController.getInstance().setRunning(b);
	}
	
	
	/**
	 * @param gc
	 */
	public J3DFrame(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public J3DFrame(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param gc
	 */
	public J3DFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

}
