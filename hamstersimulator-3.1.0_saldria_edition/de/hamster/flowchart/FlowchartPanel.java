package de.hamster.flowchart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.hamster.editor.view.TextArea;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.FlowchartMethod;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.FlowchartTransition;
import de.hamster.flowchart.view.FlowchartDrawPanel;
import de.hamster.flowchart.view.FlowchartGlassPane;
import de.hamster.flowchart.view.OverLayout;
import de.hamster.flowchart.view.Toolbox;

/**
 * Diese Klasse repräsentiert die Zeichenfläche für ein Flowchart-Programm in
 * der GUI des Hamster-Simulators
 * 
 * @author dibo
 * 
 */
public class FlowchartPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// für interne Zwecke
	private TextArea textArea;

	// das zugeordnete Flowchart-HamsterFile, über dass das aktuelle
	// Flowchart-Programm
	// ermittelt werden kann
	private FlowchartHamsterFile file;

	private JComponent mainPane;

	private JTabbedPane panel;

	private FlowchartGlassPane glassPane;

	// public static Color BACKGROUND_COLOR = new Color(124, 128, 131);
	public static Color BACKGROUND_COLOR = new Color(118, 145, 163);

	/**
	 * Erzeugt das Flowchart-Panel für das übergebene File (inkl. Programm)
	 * 
	 * @param file
	 */
	public FlowchartPanel(FlowchartHamsterFile file) {
		this.file = file; // wichtig: nicht ändern!

		this.setLayout(new OverLayout(this));

		this.setOpaque(true);

		this.panel = createDrawPanel();
		JPanel toolbox = createToolbox();

		mainPane = new JLayeredPane();
		mainPane.setLayout(new BorderLayout());
		mainPane.add(toolbox, BorderLayout.LINE_START);
		mainPane.add(panel, BorderLayout.CENTER);
		mainPane.setName("mainPane");
		this.setBackground(BACKGROUND_COLOR);

		glassPane = new FlowchartGlassPane(panel, toolbox, this, file);

		this.add(mainPane, new Integer(0), 0);
		this.add(glassPane, new Integer(1), 0);

		this.file.getProgram().getController().setFlowchartTabbedPane(panel);

	}

	private JPanel createToolbox() {
		JPanel toolbox = new Toolbox("Toolbox");
		toolbox.setSize(300, this.getHeight());
		return toolbox;
	}

	/**
	 * Erzeugt die TabPane für die PAP-Hamster-Programme.
	 * 
	 * @return Die JTabbedPane.
	 */
	private JTabbedPane createDrawPanel() {
		JTabbedPane tmpTabPanel = new JTabbedPane();
		// panel.setOpaque(true);
		tmpTabPanel.setPreferredSize(new Dimension(ImageObserver.WIDTH,
				ImageObserver.HEIGHT));
		for (FlowchartMethod method : this.file.getProgram().getController()
				.getMethods()) {
			FlowchartDrawPanel tmpPanel = method.getDrawPanel();
			// Wenn die Methode noch kein Panel hat, dann erstelle ein neues.
			// Tritt auf, wenn ein neues Programm erstellt wird.
			if (tmpPanel == null) {
				method.setDrawPanel(new FlowchartDrawPanel(method, true,
						this.file));
				tmpPanel = method.getDrawPanel();
				tmpPanel.setTmpTransitionList(this.file.getProgram()
						.getController().getTransitions());
			}
			tmpTabPanel.addTab(method.getName(), null,
					new JScrollPane(tmpPanel), method.getName());
		}

		return tmpTabPanel;
	}

	/**
	 * Setzt die zugeordnete TextArea (nicht verändern)
	 */
	public void setTextArea(TextArea area) {
		this.textArea = area;
		this.file.getProgram().setController(glassPane.getLocalController());
	}

	/**
	 * Liefert die zugeordnete TextArea (nicht verändern)
	 */
	public TextArea getTextArea() {
		return this.textArea;
	}

	/**
	 * Setzt fest, dass das Panel modifiziert wurde, so dass der Speicherbutton
	 * enabled wird (nicht verändern)
	 */
	public void modified() {
		if (this.textArea == null) {
			return;
		}
		this.textArea.getFile().setModified(true);
	}

	/**
	 * Setzt fest, dass das Panel nicht modifiziert wurde, so dass der
	 * Speicherbutton disabled wird (nicht verändern)
	 */
	public void unmodified() {
		if (this.textArea == null) {
			return;
		}
		this.textArea.getFile().setModified(false);
	}

	/**
	 * Wird aufgerufen, wenn ein Flowchart-Programm gestartet bzw. gestoppt
	 * wird. Während einer Programm-Ausfährung sollte das Programm nicht
	 * verändert werden kännen (locked == true)
	 */
	public void setLocked(boolean locked) {
		// TODO : Panel-Eingaben bzw. -Veränderungen sperren
	}

	/**
	 * Für das Drucken der Graphen
	 * 
	 * @return Für jeden Methode ein BufferedImage in einem Array
	 */
	public BufferedImage[] getImages() {
		BufferedImage[] methodImg = new BufferedImage[this.file.getProgram()
				.getController().getMethods().size()];
		int i = 0;
		for (FlowchartMethod m : this.file.getProgram().getController()
				.getMethods()) {

			methodImg[i] = new BufferedImage(m.getWidth(), m.getHeight(),
					Transparency.BITMASK);

			Graphics g = methodImg[i].getGraphics();

			g.setColor(Color.BLACK);
			Graphics2D line = (Graphics2D) g;
			line.setStroke(new BasicStroke(3));

			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
			g.drawString(m.getName(), 10, 20);

			for (CommentObject c : m.getCommentList()) {
				c.draw(g);
			}

			for (FlowchartObject f : m.getElemList()) {
				f.draw(g);
			}

			for (FlowchartTransition t : this.file.getProgram().getController()
					.getTransitions()) {
				for (FlowchartObject f : m.getElemList()) {
					if (f.equals(t.getSourceObject())) {
						t.draw(g);
					}
				}
			}

			i++;
		}

		return methodImg;
	}

	public JComponent getMainPane() {
		return mainPane;
	}
}
