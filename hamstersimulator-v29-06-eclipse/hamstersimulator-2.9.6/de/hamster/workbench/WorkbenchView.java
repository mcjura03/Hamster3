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
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
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

import de.hamster.compiler.model.CompilerModel;
import de.hamster.console.Console;
import de.hamster.debugger.model.DebuggerModel;
import de.hamster.lego.model.LegoModel;
import de.hamster.simulation.view.DialogTerminal;
import de.hamster.simulation.view.multimedia.opengl.J3DFrame;
import de.hamster.simulation.view.multimedia.opengl.OpenGLController;

/**
 * View-Teil der Werkbank.
 *
 * Best Practices:
 * - PropertyChangeListener statt Observable/Observer
 * - Keine Listener-Registrierung im Konstruktor (vermeidet "leaking this")
 * - Actions als static Klassen ohne implizite Outer-Referenz
 * - WebBrowser.open(...) statt "new instance ignored"
 */
public class WorkbenchView implements PropertyChangeListener, WindowFocusListener {

	private static final Logger LOGGER = Logger.getLogger(WorkbenchView.class.getName());

	private final Workbench workbench;
	private final WorkbenchModel model;

	private final Map<String, JMenuBar> menuBars;
	private final Map<String, JMenu> menus;
	private final Map<String, JToolBar> toolBars;

	private JFrame editor;
	private JFrame simulation;
	private Console console;
	private J3DFrame opengl;

	private JPanel debugPanel;
	private JPanel mainPanel;

	private final ResourceBundle resources;

	// Actions (werden erst in initializeUiActions() erzeugt)
	public AbstractAction showInfoAction;
	public AbstractAction showManualAction;
	public AbstractAction showAPIAction;
	public AbstractAction showImpAction;
	public AbstractAction showOOAction;
	public AbstractAction showParAction;
	public AbstractAction showHWWWAction;

	public WorkbenchView(Workbench workbench) {
		this.workbench = workbench;
		this.model = workbench.getModel();

		this.toolBars = new HashMap<>();
		this.menuBars = new HashMap<>();
		this.menus = new HashMap<>();

		this.resources = Utils.getResources();
	}

	/**
	 * Muss NACH dem Konstruktor aufgerufen werden (Best Practice).
	 * Erst hier werden Actions erstellt → verhindert "leaking this" in Feldinitialisierungen.
	 */
	public void initializeUiActions() {
		this.showInfoAction = new SimpleAction("workbench.info", () -> InfoFrame.getInstance().setVisible(true));

		this.showManualAction = new SimpleAction("workbench.manual", () -> openLocalFileOrWeb(
				new File("handbuch/handbuch.pdf"),
				"workbench.manual.text",
				"http://www.java-hamster-modell.de/download/v29/handbuch.pdf",
				"Konnte Handbuch lokal nicht öffnen, falle auf WebBrowser zurück."));

		this.showAPIAction = new SimpleAction("workbench.api", () -> openLocalFileOrWeb(
				new File("API/index.html"),
				"workbench.api.text",
				"http://www.java-hamster-modell.de/band2/API/index.html",
				"Konnte API lokal nicht öffnen, falle auf WebBrowser zurück."));

		this.showImpAction = new SimpleAction("workbench.imp", () -> openUriOrWeb(
				"http://www.java-hamster-modell.de/eBooks/hamster1.pdf",
				"workbench.imp.text",
				"http://books.google.com/books?id=_pQ-9QTpcZMC&hl=de",
				"Konnte Link nicht öffnen, falle auf WebBrowser zurück."));

		this.showOOAction = new SimpleAction("workbench.oo", () -> openUriOrWeb(
				"http://www.java-hamster-modell.de/eBooks/hamster2.pdf",
				"workbench.oo.text",
				"http://books.google.de/books?id=CnPbZO98SjAC",
				"Konnte Link nicht öffnen, falle auf WebBrowser zurück."));

		this.showParAction = new SimpleAction("workbench.par", () -> openUriOrWeb(
				"http://www.java-hamster-modell.de/eBooks/hamster3.pdf",
				"workbench.par.text",
				"http://books.google.de/books?id=AZlQeTp1ORkC",
				"Konnte Link nicht öffnen, falle auf WebBrowser zurück."));

		this.showHWWWAction = new SimpleAction("workbench.hwww", () -> openUriOrWeb(
				"http://www.java-hamster-modell.de",
				"workbench.hwww.text",
				"http://www.java-hamster-modell.de",
				"Konnte Link nicht öffnen, falle auf WebBrowser zurück."));
	}

	/**
	 * Muss NACH dem Konstruktor aufgerufen werden (Best Practice).
	 * Erst hier Listener registrieren → verhindert "leaking this in constructor".
	 */
	public void startListening() {
		model.getDebuggerModel().addPropertyChangeListener(this);
		model.getCompilerModel().addPropertyChangeListener(this);

		if (Utils.LEGO) {
			model.getLegoModel().addPropertyChangeListener(this);
		}
	}

	// ---------------- Actions (static, keine implizite Outer-Referenz) ----------------

	private static final class SimpleAction extends AbstractAction {
		private final Runnable onAction;

		private SimpleAction(String resourceKey, Runnable onAction) {
			this.onAction = onAction;
			Utils.setData(this, resourceKey);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			onAction.run();
		}
	}

	// ---------------- Robust "open" helpers ----------------

	private void openLocalFileOrWeb(File localFile, String fallbackTextKey, String fallbackUrl, String logMsg) {
		try {
			Desktop.getDesktop().browse(localFile.toURI());
		} catch (IOException | SecurityException | UnsupportedOperationException ex) {
			LOGGER.log(Level.WARNING, logMsg, ex);
			new WebBrowser(resources.getString(fallbackTextKey), fallbackUrl);
		}
	}

	private void openUriOrWeb(String uriString, String fallbackTextKey, String fallbackUrl, String logMsg) {
		try {
			Desktop.getDesktop().browse(new URI(uriString));
		} catch (IOException | URISyntaxException | SecurityException | UnsupportedOperationException ex) {
			LOGGER.log(Level.WARNING, logMsg, ex);
			new WebBrowser(resources.getString(fallbackTextKey), fallbackUrl);
		}
	}

	// ---------------- Visibility ----------------

	public void set3DVisible(boolean value) {
		if (!Utils.DREI_D) {
			return;
		}
		opengl.setVisible(value);
		OpenGLController.getInstance().setRunning(value);
	}

	public void setVisible(boolean value) {
		editor.setLocation(50, 50);
		editor.setVisible(value);
		simulation.setLocation(200, 200);
		simulation.setVisible(value);
	}

	public void setOnlySimVisible(boolean value) {
		editor.setLocation(50, 50);
		editor.setVisible(false);

		simulation.setLocation(200, 200);
		simulation.setVisible(value);

		if (!Utils.DREI_D) {
			return;
		}
		opengl.setVisible(value);
		OpenGLController.getInstance().setRunning(value);
	}

	// ---------------- Console ----------------

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

	// ---------------- Frames ----------------

	public void createSimulationFrame() {
		simulation = new JFrame("Simulation");
		simulation.setSize(700, 500);
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

		if (!Utils.DREI_D) {
			return;
		}

		try {
			opengl = new J3DFrame();
			opengl.setSize(640, 480);
			opengl.setLocation(100, 100);

			JToolBar simulationBar3D = findToolBar("3dsimulation");
			opengl.getContentPane().add(BorderLayout.NORTH, simulationBar3D);

			OpenGLController.getInstance().create3DView(
					opengl,
					workbench,
					workbench.getSimulation().getSimulationModel(),
					workbench.getDebugger().getDebuggerModel());

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

	public void createEditorFrame() {
		editor = new JFrame("Editor");
		editor.setSize(800, 600);
		editor.setDefaultCloseOperation(0);
		editor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (Utils.DREI_D) {
					OpenGLController.getInstance().setRunning(false);
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

	// ---------------- Menus/Toolbars ----------------

	public JToolBar findToolBar(String id) {
		JToolBar toolBar = toolBars.get(id);
		if (toolBar == null) {
			toolBar = new JToolBar(resources.getString("toolbar." + id + ".text"));
			toolBar.setMargin(new Insets(1, 1, 0, 0));
			toolBar.setFloatable(false);
			toolBar.setBackground(new Color(255, 215, 180));
			toolBars.put(id, toolBar);
		}
		return toolBar;
	}

	public JMenuBar findMenuBar(String id) {
		JMenuBar menuBar = menuBars.get(id);
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBars.put(id, menuBar);
		}
		return menuBar;
	}

	public JMenu findMenu(String bar, String key) {
		String mapKey = bar + "." + key;
		JMenu menu = menus.get(mapKey);
		if (menu == null) {
			menu = new JMenu(resources.getString("menu." + bar + "." + key + ".text"));
			findMenuBar(bar).add(menu);
			menus.put(mapKey, menu);
		}
		return menu;
	}

	// ---------------- PropertyChangeListener ----------------

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		handleModelEvent(evt);
	}

	private void handleModelEvent(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (CompilerModel.COMPILER_ERRORS.equals(prop)) {
			if (model.getCompilerModel().getCompilerErrors().isEmpty()) {
				JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.kompilierung"));
				if (mainPanel != null) {
					mainPanel.remove(workbench.getComiler().getErrorPanel());
				}
			} else {
				if (mainPanel != null) {
					mainPanel.add(BorderLayout.SOUTH, workbench.getComiler().getErrorPanel());
				}
			}
		} else if (DebuggerModel.ARG_STATE.equals(prop)) {
			if (mainPanel != null) {
				if (model.getDebuggerModel().getState() == DebuggerModel.NOT_RUNNING
						|| !model.getDebuggerModel().isEnabled()) {
					mainPanel.remove(debugPanel);
				} else {
					mainPanel.add(BorderLayout.NORTH, debugPanel);
				}
			}
		} else if (Utils.LEGO && LegoModel.LEGO_UPLOAD.equals(prop)) {
			if (model.getLegoModel().getState() == LegoModel.SUCCESS) {
				JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.lego.success"));
			} else if (model.getLegoModel().getState() == LegoModel.FAILURE) {
				JOptionPane.showMessageDialog(editor, Utils.getResource("workbench.lego.failure"));
			}
		}

		if (mainPanel != null) {
			mainPanel.revalidate();
			mainPanel.repaint();
		}
	}

	// ---------------- Getters ----------------

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

	// ---------------- WindowFocusListener ----------------

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