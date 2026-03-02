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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.swing.SwingUtilities;

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
public class WorkbenchView implements PropertyChangeListener, WindowFocusListener {

	private static final Logger LOGGER = Logger.getLogger(WorkbenchView.class.getName());

	/**
	 * Dies ist eine Verknuepfung zum Controller der Werkbank.
	 */
	private final Workbench workbench;

	/**
	 * Dies ist eine Verknuepfung zur View der Werkbank.
	 */
	private WorkbenchModel model;

	/**
	 * In dieser Map werden die einzelnen Menuebars ueber einen Schluessel gespeichert.
	 */
	private final Map<String, JMenuBar> menuBars;

	/**
	 * In dieser Map werden die einzelnen Menues ueber einen Schluessel gespeichert.
	 */
	private final Map<String, JMenu> menus;

	/**
	 * In dieser Map werden die einzelnen Toolbars ueber einen Schluessel gespeichert.
	 */
	private final Map<String, JToolBar> toolBars;

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
	 * ResourceBundle für Lokalisierung.
	 */
	private final ResourceBundle resources;

	public class ShowInfoAction extends AbstractAction {
		public ShowInfoAction() {
			Utils.setData(this, "workbench.info");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			InfoFrame.getInstance().setVisible(true);
		}
	}

	public ShowInfoAction showInfoAction = new ShowInfoAction();

	public class ShowManualAction extends AbstractAction {
		public ShowManualAction() {
			Utils.setData(this, "workbench.manual");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new File("handbuch/handbuch.pdf").toURI());
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Konnte Handbuch lokal nicht öffnen, falle auf WebBrowser zurück.", exc);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new File("API/index.html").toURI());
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Konnte API lokal nicht öffnen, falle auf WebBrowser zurück.", exc);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de/eBooks/hamster1.pdf"));
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Konnte Link nicht öffnen, falle auf WebBrowser zurück.", exc);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de/eBooks/hamster2.pdf"));
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Konnte Link nicht öffnen, falle auf WebBrowser zurück.", exc);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de/eBooks/hamster3.pdf"));
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Konnte Link nicht öffnen, falle auf WebBrowser zurück.", exc);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://www.java-hamster-modell.de"));
			} catch (Exception exc) {
				LOGGER.log(Level.WARNING, "Konnte Link nicht öffnen, falle auf WebBrowser zurück.", exc);
				new WebBrowser(Utils.getResources().getString("workbench.hwww.text"),
						"http://www.java-hamster-modell.de");
			}
		}
	}

	public ShowHWWWAction showHWWWAction = new ShowHWWWAction();

	/**
	 * Diese Methode erzeugt die WorkbenchView. Sie fügt sich selbst als Listener
	 * zum Debugger und Compiler hinzu, damit UI-Komponenten ein-/ausgeblendet werden können.
	 */
	public WorkbenchView(Workbench workbench) {
		this.workbench = workbench;
		this.model = workbench.getModel();

		// Alt: addObserver(this)
		// Neu: PropertyChangeListener (nach Konstruktor-Ende registrieren, um "leaking this" zu vermeiden)
		SwingUtilities.invokeLater(() -> {
			this.model.getDebuggerModel().addPropertyChangeListener(WorkbenchView.this);
			this.model.getCompilerModel().addPropertyChangeListener(WorkbenchView.this);

			if (Utils.LEGO) {
				this.model.getLegoModel().addPropertyChangeListener(WorkbenchView.this);
			}
		});

		this.toolBars = new HashMap<>();
		this.menuBars = new HashMap<>();
		this.menus = new HashMap<>();

		this.resources = Utils.getResources();
	}

	/**
	 * @author chris
	 * Diese Methode macht das 3D-Fenster sichtbar oder versteckt es.
	 */
	public void set3DVisible(boolean value) {
		if (!Utils.DREI_D)
			return;

		opengl.setVisible(value); // chris
		OpenGLController.getInstance().setRunning(value); // chris
	}

	/**
	 * Diese Methode macht die beiden Fenster sichtbar oder versteckt sie.
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
			@Override
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
			@Override
			public void windowClosing(WindowEvent e) {
				simulation.setVisible(false);
				Workbench.winSim.setState(false);
			}
		});

		DialogTerminal.createInstance(simulation);

		JPanel main = new JPanel(new BorderLayout());
		simulation.getContentPane().add(BorderLayout.CENTER, main);

		JToolBar simulationBar = findToolBar("simulation");
		simulation.getContentPane().add(BorderLayout.NORTH, simulationBar);

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
				@Override
				public void windowClosing(WindowEvent e) {
					OpenGLController.getInstance().setRunning(false);
					Workbench.win3D.setState(false);
				}
			});
		} catch (Throwable t) {
			Workbench.disable3D();
		}
	}

	/**
	 * Diese Methode erzeugt das Editorfenster.
	 */
	public void createEditorFrame() {
		editor = new JFrame("Editor");
		editor.setSize(800, 600); // dibo
		editor.setDefaultCloseOperation(0);
		editor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (Utils.DREI_D) {
					OpenGLController.getInstance().setRunning(false); // chris
				}

				workbench.close(editor);
			}
		});
		editor.addWindowFocusListener(this);

		editor.setJMenuBar(findMenuBar("editor"));

		JToolBar editorBar = findToolBar("editor");
		editor.getContentPane().add(BorderLayout.NORTH, editorBar);

		debugPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		debugPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		debugPanel.add(new JScrollPane(workbench.getDebugger().getStackFrameTable()));
		debugPanel.add(new JScrollPane(workbench.getDebugger().getVariableViewer()));
		debugPanel.setPreferredSize(new Dimension(150, 150));

		workbench.getComiler().getErrorPanel().setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		mainPanel = new JPanel(new BorderLayout());
		workbench.getEditor().getTabbedTextArea().setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 1));
		mainPanel.add(BorderLayout.CENTER, workbench.getEditor().getTabbedTextArea());

		JScrollPane fileTree = new JScrollPane(workbench.getEditor().getFileTree());
		fileTree.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 0, 5),
				fileTree.getBorder()));
		fileTree.setPreferredSize(new Dimension(150, 150));

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTree, mainPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(150);

		editor.getContentPane().add(BorderLayout.CENTER, sp);
	}

	/**
	 * Toolbar finden/erzeugen.
	 */
	public JToolBar findToolBar(String id) {
		JToolBar toolBar = toolBars.get(id);
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
	 * Menuebar finden/erzeugen.
	 */
	public JMenuBar findMenuBar(String id) {
		JMenuBar menuBar = menuBars.get(id);
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBars.put(id, menuBar);
		}
		return menuBar;
	}

	/**
	 * Menue finden/erzeugen.
	 */
	public JMenu findMenu(String bar, String key) {
		JMenu menu = menus.get(bar + "." + key);
		if (menu == null) {
			menu = new JMenu(resources.getString("menu." + bar + "." + key + ".text"));
			findMenuBar(bar).add(menu);
			menus.put(bar + "." + key, menu);
		}
		return menu;
	}

	/**
	 * Neu: reagiert auf PropertyChange-Events statt Observable.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// UI-Änderungen immer auf dem EDT
		if (SwingUtilities.isEventDispatchThread()) {
			handleModelEvent(evt);
		} else {
			SwingUtilities.invokeLater(() -> handleModelEvent(evt));
		}
	}

	private void handleModelEvent(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// UI immer auf dem EDT
			if (SwingUtilities.isEventDispatchThread()) {
				handleModelEvent(evt);
			} else {
				SwingUtilities.invokeLater(() -> handleModelEvent(evt));
			}
		}
		
		private void handleModelEvent(PropertyChangeEvent evt) {
			String prop = evt.getPropertyName();
		
			// Compiler
			if (CompilerModel.COMPILER_ERRORS.equals(prop)) {
				if (model.getCompilerModel().getCompilerErrors().isEmpty()) {   // <- isEmpty statt size()==0
					JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.kompilierung"));
					mainPanel.remove(workbench.getComiler().getErrorPanel());
				} else {
					mainPanel.add(BorderLayout.SOUTH, workbench.getComiler().getErrorPanel());
				}
			}
			// Debugger
			else if (DebuggerModel.ARG_STATE.equals(prop)) {
				if (model.getDebuggerModel().getState() == DebuggerModel.NOT_RUNNING
						|| !model.getDebuggerModel().isEnabled()) {
					mainPanel.remove(debugPanel);
				} else {
					mainPanel.add(BorderLayout.NORTH, debugPanel);
				}
			}
			// LEGO
			else if (Utils.LEGO && LegoModel.LEGO_UPLOAD.equals(prop)) {
				if (model.getLegoModel().getState() == LegoModel.SUCCESS) {
					JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.lego.success"));
				} else if (model.getLegoModel().getState() == LegoModel.FAILURE) {
					JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.lego.failure"));
				}
			}
		
			mainPanel.revalidate();
			mainPanel.repaint();
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

	@Override
	public void windowGainedFocus(WindowEvent e) {
		workbench.getEditor().refreshFiles();
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
	}

	public JFrame getSimulation() {
		return simulation;
	}

	public void setSimulation(JFrame simulation) {
		this.simulation = simulation;
	}
}