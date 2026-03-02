package de.hamster.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import de.hamster.compiler.model.CompilerModel;
import de.hamster.console.Console;
import de.hamster.debugger.model.DebuggerModel;
import de.hamster.lego.model.LegoModel;
import de.hamster.simulation.view.DialogTerminal;
import de.hamster.simulation.view.multimedia.opengl.J3DFrame;
import de.hamster.simulation.view.multimedia.opengl.OpenGLController;

/**
 * Diese Klasse implementiert den View-Teil der Werkbank. Instanzen erzeugen
 * zwei Fenster und fuegen die einzelnen Komponenten des Hamster-Simulators ein.
 * 
 * Die Klasse verwaltet ausserdem die Toolbars und Menuezeilen, in die die
 * einzelnen Komponenten ihre Eintraege einfuegen koennen.
 * 
 * @author Daniel Jasper
 */
public class WorkbenchView implements Observer, WindowFocusListener {
	/**
	 * Dies ist eine Verknuepfung zum Controller der Werkbank.
	 */
	private Workbench workbench;

	/**
	 * Dies ist eine Verknuepfung zur View der Werkbank.
	 */
	private WorkbenchModel model;

	/**
	 * In dieser HashMap werden die einzelnen Menuebars ueber einen Schluessel
	 * gespeichert. Dieser Schluessel entspricht dem Schluessel aus der
	 * Locale-Datei.
	 */
	private HashMap menuBars;

	/**
	 * In dieser HashMap werden die einzelnen Menues ueber einen Schluessel
	 * gespeichert. Dieser Schluessel entspricht gleichzeitig dem Schluessel aus
	 * der Locale-Datei, so dass dem Menue automatisch ein Name zugeordnet
	 * werden kann.
	 */
	private HashMap menus;

	/**
	 * In dieser HashMap werden die einzelnen Toolbars ueber einen Schluessel
	 * gespeichert.
	 */
	private HashMap toolBars;

	/**
	 * Dies ist das Fenster des Editors.
	 */
	private JFrame editor;

	/**
	 * Dies ist das Fenster der Simulation.
	 */
	private JFrame simulation;

	/*
	 * Console für Standard-Out, -Err und -In
	 */
	private Console console;

	/**
	 * Dies ist das Fenster der Simulation. // chris
	 */
	private J3DFrame opengl;

	/**
	 * In diesem Panel werden die beiden Komponenten des Debuggers
	 * (StackframeViewer und VariableViewer) angeordnet.
	 */
	private JPanel debugPanel;

	/**
	 * Diese Panel enthaelt die Debugger-Komponenten, die Textflaechen und die
	 * Compiler-Fehler-Anzeige.
	 */
	private JPanel mainPanel;

	/**
	 * Das ResourceBundle haelt die Text in den zur Verfuegung stehenden
	 * Sprachen bereit. Es wird dabei der Locale entsprechend eine Datei
	 * hamster_xx_YY.properties geladen, in der fuer die entsprechende Sprache
	 * zu jedem Schluessel ein Text zugeordnet sein muss. gibt es keine Datei
	 * mit YY, so wird hamster_xx.properties genommen, gibt es auch diese nicht,
	 * so wird hamster.properties genommen, die die Standardsprache enthaelt
	 * (zur Zeit Deutsch). Soll der Simulator fuer eine andere Sprache erweitert
	 * werden, muss nur diese Datei uebersetzt und entsprechend gespeichert
	 * werden.
	 */
	private ResourceBundle resources;

	public class ShowInfoAction extends AbstractAction {
		public ShowInfoAction() {
			Utils.setData(this, "workbench.info");
		}

		public void actionPerformed(ActionEvent e) {
			InfoFrame.getInstance().setVisible(true);
		}
	}

	public ShowInfoAction showInfoAction = new ShowInfoAction();

	public class ShowManualAction extends AbstractAction {
		public ShowManualAction() {
			Utils.setData(this, "workbench.manual");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new File("handbuch/handbuch.pdf").toURI());

			} catch (Exception exc) {
				exc.printStackTrace();
					new WebBrowser(
							Utils.getResources().getString("workbench.manual.text"),
							 "http://www.java-hamster-modell.de/download/v29/handbuch.pdf");
			}
		}
	}

	public ShowManualAction showManualAction = new ShowManualAction();

	public class ShowAPIAction extends AbstractAction {
		public ShowAPIAction() {
			Utils.setData(this, "workbench.api");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
//				desktop.browse(new URI(
//						"http://www.java-hamster-modell.de/band2/API/index.html?package-summary.html"));
				desktop.browse(new File("API/index.html").toURI());

			} catch (Exception exc) {
				exc.printStackTrace();
				new WebBrowser(Utils.getResources().getString("workbench.api.text"),
						"http://www.java-hamster-modell.de/band2/API/index.html");
			}
		}
	}

	public ShowAPIAction showAPIAction = new ShowAPIAction();

	public class ShowImpAction extends AbstractAction {
		public ShowImpAction() {
			Utils.setData(this, "workbench.imp");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de/eBooks/hamster1.pdf"));
			} catch (Exception exc) {
				new WebBrowser(Utils.getResources().getString("workbench.imp.text"),
						"http://books.google.com/books?id=_pQ-9QTpcZMC&hl=de");
			}
		}
	}

	public ShowImpAction showImpAction = new ShowImpAction();

	public class ShowOOAction extends AbstractAction {
		public ShowOOAction() {
			Utils.setData(this, "workbench.oo");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de/eBooks/hamster2.pdf"));
			} catch (Exception exc) {
				new WebBrowser(Utils.getResources().getString("workbench.oo.text"),
						"http://books.google.de/books?id=CnPbZO98SjAC");
			}
		}
	}

	public ShowOOAction showOOAction = new ShowOOAction();

	public class ShowParAction extends AbstractAction {
		public ShowParAction() {
			Utils.setData(this, "workbench.par");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de/eBooks/hamster3.pdf"));
			} catch (Exception exc) {
				new WebBrowser(Utils.getResources().getString("workbench.par.text"),
						"http://books.google.de/books?id=AZlQeTp1ORkC");
			}
		}
	}

	public ShowParAction showParAction = new ShowParAction();

	public class ShowHWWWAction extends AbstractAction {
		public ShowHWWWAction() {
			Utils.setData(this, "workbench.hwww");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de"));
			} catch (Exception exc) {
				new WebBrowser(Utils.getResources().getString("workbench.hwww.text"),
						"http://www.java-hamster-modell.de");
			}
		}
	}

	public ShowHWWWAction showHWWWAction = new ShowHWWWAction();

	/**
	 * Diese Methode erzeugt die WorkbenchView. Sie fuegt sich selbst als
	 * Observer zum Debugger und Compiler hinzu. Auf diese Weise ist die
	 * WorkbenchView in der Lage, die Debugger- und Compiler-Komponenten ein-
	 * und auszublenden, je nachdem, ob diese benoetigt werden oder nicht.
	 * 
	 * @param workbench
	 *            Der Controller der Werkbank.
	 */
	public WorkbenchView(Workbench workbench) {
		this.workbench = workbench;
		this.model = workbench.getModel();
		model.getDebuggerModel().addObserver(this);
		model.getCompilerModel().addObserver(this);

		if (Utils.LEGO) {
			/* lego */model.getLegoModel().addObserver(this);
		}

		toolBars = new HashMap();
		menuBars = new HashMap();
		menus = new HashMap();

		resources = Utils.getResources();
	}

	/**
	 * @author chris
	 * 
	 *         Diese Methode macht das 3D-Fenster sichtbar oder versteckt sie.
	 * @param value
	 */
	public void set3DVisible(boolean value) {
		if (!Utils.DREI_D)
			return;

		opengl.setVisible(value); // chris
		OpenGLController.getInstance().setRunning(value); // chris
	}

	/**
	 * Diese Methode macht die beiden Fenster sichtbar oder versteckt sie.
	 * 
	 * @param value
	 *            Sichtbar machen oder verstecken?
	 */
	public void setVisible(boolean value) {

		editor.setLocation(50, 50); // dibo
		editor.setVisible(value);
		simulation.setLocation(200, 200); // dibo
		simulation.setVisible(value);
	}

	public void setOnlySimVisible(boolean value) {
		editor.setLocation(50, 50); // dibo
		editor.setVisible(false);
		simulation.setLocation(200, 200); // dibo
		simulation.setVisible(value);
		if (!Utils.DREI_D)
			return;
		opengl.setVisible(value); // chris
		OpenGLController.getInstance().setRunning(value); // chris
	}

	/*
	 * Erzeugt die Console dibo 070708
	 */
	public void createConsole() {
		console = new Console();
		console.setDefaultCloseOperation(0);
		if (Utils.runlocally) {
			System.setOut(new PrintStream(console.getOut()));
			System.setErr(new PrintStream(console.getErr()));
			System.setIn(console.getIn());
		}
		console.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				console.setVisible(false);
				Workbench.console.setState(false);
			}
		});
	}

	public void openConsole() {
		if (!console.isVisible()) {
			console.setVisible(true);
			console.toFront();
			Workbench.console.setState(true);
		}
	}

	/**
	 * Diese Methode erzeugt das Simulationsfenster.
	 */
	public void createSimulationFrame() {
		simulation = new JFrame("Simulation");
		simulation.setSize(700, 500); // dibo
		simulation.setDefaultCloseOperation(0);
		simulation.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// workbench.close(simulation);
				simulation.setVisible(false);
				Workbench.winSim.setState(false);
			}
		});

		DialogTerminal.createInstance(simulation);

		JPanel main = new JPanel(new BorderLayout());
		simulation.getContentPane().add(BorderLayout.CENTER, main);

		JToolBar simulationBar = findToolBar("simulation");
		simulation.getContentPane().add(BorderLayout.NORTH, simulationBar);

		/*
		 * JScrollPane scrollPane = new JScrollPane(workbench.getSimulation()
		 * .getSimulationPanel()); main.add(BorderLayout.CENTER, scrollPane);
		 * 
		 * JPanel log = workbench.getSimulation().getLogPanel();
		 * log.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		 * log.setPreferredSize(new Dimension(200, 200));
		 * 
		 * main.add(BorderLayout.EAST, log);
		 */

		JScrollPane scrollPane = new JScrollPane(workbench.getSimulation().getSimulationPanel());
		JPanel log = workbench.getSimulation().getLogPanel();
		log.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		log.setPreferredSize(new Dimension(200, 200));
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, log);
		sp.setResizeWeight(1);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(500);
		main.add(BorderLayout.CENTER, sp);

		if (!Utils.DREI_D)
			return;
		// chris: 3D-Ansichtshauptfenster
		try {
			opengl = new J3DFrame();
			opengl.setSize(640, 480);
			opengl.setLocation(100, 100);

			JToolBar simulationBar3D = findToolBar("3dsimulation");
			opengl.getContentPane().add(BorderLayout.NORTH, simulationBar3D);

			OpenGLController.getInstance().create3DView(opengl, workbench,
					workbench.getSimulation().getSimulationModel(), workbench.getDebugger().getDebuggerModel());

			opengl.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					OpenGLController.getInstance().setRunning(false);
					Workbench.win3D.setState(false);
				}
			});
		} catch (Throwable t) {
			// t.printStackTrace();
			// System.err.println(t);

			Workbench.disable3D();
		}

	}

	/**
	 * Diese Methode erzeugt das Editorfenster.
	 */
	public void createEditorFrame() {
		// Erzeugen des Fensters
		editor = new JFrame("Editor");
		editor.setSize(800, 600); // dibo
		editor.setDefaultCloseOperation(0);
		editor.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				if (Utils.DREI_D) {
					OpenGLController.getInstance().setRunning(false); // chris
				}

				workbench.close(editor);
			}
		});
		editor.addWindowFocusListener(this);

		// Erzeugen der MenuBar
		editor.setJMenuBar(findMenuBar("editor"));

		// Erzeugen der ToolBar
		JToolBar editorBar = findToolBar("editor");
		editor.getContentPane().add(BorderLayout.NORTH, editorBar);

		// Erzeugen des DebuggerPanels
		debugPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		debugPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		debugPanel.add(new JScrollPane(workbench.getDebugger().getStackFrameTable()));
		debugPanel.add(new JScrollPane(workbench.getDebugger().getVariableViewer()));
		debugPanel.setPreferredSize(new Dimension(150, 150));

		// Anpassen der Compiler-Fehler
		workbench.getComiler().getErrorPanel().setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		// Erzeugen des Main-Panels
		// dibo 151106

		mainPanel = new JPanel(new BorderLayout());
		workbench.getEditor().getTabbedTextArea().setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 1));
		mainPanel.add(BorderLayout.CENTER, workbench.getEditor().getTabbedTextArea());

		// Anpassen des Dateibaums
		JScrollPane fileTree = new JScrollPane(workbench.getEditor().getFileTree());
		fileTree.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5), fileTree.getBorder()));
		fileTree.setPreferredSize(new Dimension(150, 150));

		// Hinzufuegen von Dateibaum und Main-Panel
		// in SplitPane (dibo)
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTree, mainPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(150);

		/*
		 * editor.getContentPane().add(BorderLayout.WEST, fileTree);
		 * editor.getContentPane().add(BorderLayout.CENTER, mainPanel);
		 */

		editor.getContentPane().add(BorderLayout.CENTER, sp);
	}

	/**
	 * Diese Methode findet eine Toolbar mit einem bestimmten Schluessel.
	 * Existiert diese nicht, wird sie erzeugt und mit den Daten aus dem
	 * ResourceBundle initialisiert.
	 * 
	 * @param id
	 *            Der Schluessel der Toolbar
	 * @return Die (evtl. neu erzeugte) Toolbar
	 */
	public JToolBar findToolBar(String id) {
		JToolBar toolBar = (JToolBar) toolBars.get(id);
		if (toolBar == null) {
			toolBar = new JToolBar(resources.getString("toolbar." + id + ".text"));
			toolBar.setMargin(new Insets(1, 1, 0, 0));
			toolBar.setFloatable(false);
			toolBar.setBackground(new Color(255, 215, 180)); // dibo 230309
			toolBars.put(id, toolBar);
		}
		return toolBar;
	}

	/**
	 * Diese Methode findet eine Menuebar mit einem bestimmten Schluessel.
	 * Existiert diese nicht, wird sie erzeugt und mit den Daten aus dem
	 * ResourceBundle initialisiert.
	 * 
	 * @param id
	 *            Der Schluessel der Menuebar
	 * @return Die Menuebar
	 */
	public JMenuBar findMenuBar(String id) {
		JMenuBar menuBar = (JMenuBar) menuBars.get(id);
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBars.put(id, menuBar);
		}
		return menuBar;
	}

	/**
	 * Diese Methode findet ein Menue mit einem bestimmten Schluessel. Existiert
	 * dieses nicht, wird es erzeugt und mit den Daten aus dem ResourceBundle
	 * initialisiert.
	 * 
	 * @param bar
	 *            Der Schluessel der Menuebar des Menues
	 * @param key
	 *            Der Schluessel des Menues
	 * @return Das Menue
	 */
	public JMenu findMenu(String bar, String key) {
		JMenu menu = (JMenu) menus.get(bar + "." + key);
		if (menu == null) {
			menu = new JMenu(resources.getString("menu." + bar + "." + key + ".text"));
			findMenuBar(bar).add(menu);
			menus.put(bar + "." + key, menu);
		}
		return menu;
	}

	/**
	 * Ueber diese Methode koennen DebuggerModel und CompilerModel die
	 * WorkbenchView ueber eine Aenderung informieren. Daraufhin blendet die
	 * WorkbenchView die dazugehoerigen Komponenten ein oder aus.
	 */
	public void update(Observable arg0, Object arg) {
		if (arg == CompilerModel.COMPILER_ERRORS) {
			if (model.getCompilerModel().getCompilerErrors().size() == 0) {
				JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.kompilierung"));
				// Compiler-Fehler ausblenden
				mainPanel.remove(workbench.getComiler().getErrorPanel());
			} else {
				// Compiler-Fehler einblenden
				mainPanel.add(BorderLayout.SOUTH, workbench.getComiler().getErrorPanel());
			}
		} else if (arg == DebuggerModel.ARG_STATE) {
			if (model.getDebuggerModel().getState() == DebuggerModel.NOT_RUNNING
					|| !model.getDebuggerModel().isEnabled()) {
				// Debugger-Panel einblenden
				mainPanel.remove(debugPanel);
			} else if (model.getDebuggerModel().isEnabled()) {
				// Debugger-Panel ausblenden
				mainPanel.add(BorderLayout.NORTH, debugPanel);
			}
		}
		/* lego */
		else if (Utils.LEGO && arg == LegoModel.LEGO_UPLOAD) {
			if (model.getLegoModel().getState() == LegoModel.SUCCESS) {
				JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.lego.success"));
			} else if (model.getLegoModel().getState() == LegoModel.FAILURE) {
				JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.lego.failure"));
			}
		}
		// Das Layout aktualisieren
		mainPanel.validate();
	}

	public JFrame getSimulationFrame() {
		return simulation;
	}

	public Console getConsole() {
		return console;
	}

	public JFrame getEditorFrame() {
		return editor;
	}

	public JFrame get3DSimulationFrame() {
		return opengl;
	}

	public void windowGainedFocus(WindowEvent e) {
		workbench.getEditor().refreshFiles();
	}

	public void windowLostFocus(WindowEvent e) {
	}

	public JFrame getSimulation() {
		return simulation;
	}

	public void setSimulation(JFrame simulation) {
		this.simulation = simulation;
	}

}