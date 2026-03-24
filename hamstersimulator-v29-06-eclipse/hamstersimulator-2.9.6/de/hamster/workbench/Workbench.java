package de.hamster.workbench;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import de.hamster.compiler.controller.CompilerController;
import de.hamster.console.Console;
import de.hamster.debugger.controller.DebuggerController;
import de.hamster.debugger.model.DebuggerModel;
import de.hamster.editor.controller.EditorController;
import de.hamster.flowchart.controller.FlowchartHamster;
import de.hamster.fsm.controller.FsmHamster;
import de.hamster.javascript.model.JavaScriptHamster;
import de.hamster.lego.controller.LegoController;
import de.hamster.model.HamsterFile;
import de.hamster.prolog.controller.PrologController;
import de.hamster.prolog.view.PrologKonsole;
import de.hamster.python.model.PythonHamster;
import de.hamster.python.view.PythonKonsole;
import de.hamster.ruby.model.RubyHamster;
import de.hamster.ruby.view.RubyKonsole;
import de.hamster.scheme.view.SchemeKonsole;
import de.hamster.scratch.ScratchHamster;
import de.hamster.simulation.controller.SimulationController;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.view.DialogTerminal;
import de.hamster.simulation.view.multimedia.opengl.material.Color;

/**
 * Diese Klasse implementiert den Controller der zentralen Werkbank. Die
 * dazugehoerige main-Methode wird beim Programmstart aufgerufen und erzeugt
 * eine Instanz des Controller. Der Controller erzeugt dann Instanzen der der
 * WorkbenchModel und der WorkbenchView. Ausserdem erzeugt er die
 * Controller-Komponenten von Compiler, Editor, Debugger und Simulation.
 * 
 * Die Werkbank stellt Methoden bereit, ueber die Funktionen von einzelnen
 * Komponenten (Editor, Compiler, ...) aufgerufen werden koennen.
 * 
 * TODO: Classpath einbauen
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.4 $
 */
public class Workbench {
	public static Workbench workbench;

	/**
	 * Das Model der Werkbank.
	 */
	private WorkbenchModel model;

	/**
	 * Die View der Werkbank.
	 */
	private WorkbenchView view;

	/**
	 * Der Controller des Compilers.
	 */
	private CompilerController compiler;

	/**
	 * Der Controller des Debuggers.
	 */
	private DebuggerController debugger;

	/**
	 * Der Controller des Editors.
	 */
	private EditorController editor;

	/**
	 * Der Controller der Simulation.
	 */
	private SimulationController simulation;

	private Properties settings;

	/* lego */
	private LegoController lego;

	// dibo
	public boolean simulatdorOnly;

	// dibo
	public static LookAndFeel startLAF;

	protected Workbench(boolean simulatorOnly, SimulationModel simModel) {
		workbench = this; // Prolog
		settings = new Properties();
		try {
			settings.load(new FileInputStream(Utils.HOME + Utils.FSEP + "settings.properties"));
		} catch (IOException e) {
		}

		if (Utils.PROLOG) {
			if (!PrologController.checkProlog()) {
				Utils.PROLOG = false;
				JOptionPane.showMessageDialog(null, Utils.PROLOG_MSG, "Prolog-Initialisierungsfehler",
						JOptionPane.ERROR_MESSAGE, null);
			}
		}
		model = new WorkbenchModel(simulatorOnly, simModel);

		view = new WorkbenchView(this);

		simulation = new SimulationController(model.getSimulationModel(), this);
		editor = new EditorController(this);
		compiler = new CompilerController(model.getCompilerModel(), this);
		debugger = new DebuggerController(model.getDebuggerModel(), this);

		/* lego */
		if (Utils.LEGO) {
			lego = new LegoController(model.getLegoModel(), this);
		}

		if (Utils.runlocally) {
			view.createConsole(); // dibo 070708
		}
		view.createSimulationFrame();
		view.createEditorFrame();
		handleWindowMenu(); // dibo 151106
		handleExtrasMenu(); // dibo 230309

		view.findMenu("editor", "info").add(new JMenuItem(view.showHWWWAction));
		view.findMenu("editor", "info").add(new JMenuItem(view.showManualAction));
		view.findMenu("editor", "info").add(new JMenuItem(view.showAPIAction));
		view.findMenu("editor", "info").add(new JMenuItem(view.showImpAction));
		view.findMenu("editor", "info").add(new JMenuItem(view.showOOAction));
		view.findMenu("editor", "info").add(new JMenuItem(view.showParAction));
		view.findMenu("editor", "info").add(new JMenuItem(view.showInfoAction));

		model.getSimulationModel().setTerminal(DialogTerminal.getInstance());
		if (!simulatorOnly) {
			view.setVisible(true);
			view.set3DVisible(false); // chris
			if (Utils.PROLOG) {
				PrologKonsole.get().setVisible(true);
			}
		} else {
			view.setOnlySimVisible(true);
		}
	}

	JMenu extrasMenu;
	JCheckBoxMenuItem indentMenuItem;
	JMenuItem formatMenuItem;
	JMenu fontsizeMenu;
	JRadioButtonMenuItem size8, size10, size12, size14, size16, size18, size24, size36, size48;

	int fontSize = Utils.FONTSIZE;

	public int getFontSize() {
		return fontSize;
	}

	private void handleExtrasMenu() { // dibo 230309
		extrasMenu = view.findMenu("editor", "extras");

		this.indentMenuItem = new JCheckBoxMenuItem();
		this.indentMenuItem.setSelected(Utils.INDENT);
		this.indentMenuItem.setText(Utils.getResource("extras.indent.text"));
		this.indentMenuItem.setToolTipText(Utils.getResource("extras.indent.tooltip"));
		this.indentMenuItem.setMnemonic(Utils.getResource("extras.indent.mnemonic").charAt(0));
		this.indentMenuItem.setAccelerator(KeyStroke.getKeyStroke(Utils.getResource("extras.indent.keystroke")));
		this.indentMenuItem.addActionListener(new IndentListener(this));
		this.extrasMenu.add(this.indentMenuItem);

		this.fontsizeMenu = new JMenu();
		this.fontsizeMenu.setText(Utils.getResource("extras.font.text"));
		this.fontsizeMenu.setToolTipText(Utils.getResource("extras.font.tooltip"));
		this.fontsizeMenu.setMnemonic(Utils.getResource("extras.font.mnemonic").charAt(0));
		this.extrasMenu.add(this.fontsizeMenu);

		ButtonGroup group = new ButtonGroup();

		this.size8 = new JRadioButtonMenuItem("8");
		this.size8.addActionListener(new FontSizeListener(this, 8));
		this.fontsizeMenu.add(this.size8);
		group.add(this.size8);
		this.size10 = new JRadioButtonMenuItem("10");
		this.size10.addActionListener(new FontSizeListener(this, 10));
		this.fontsizeMenu.add(this.size10);
		group.add(this.size10);
		this.size12 = new JRadioButtonMenuItem("12");
		this.size12.addActionListener(new FontSizeListener(this, 12));
		this.fontsizeMenu.add(this.size12);
		group.add(this.size12);
		this.size14 = new JRadioButtonMenuItem("14");
		this.size14.addActionListener(new FontSizeListener(this, 14));
		this.fontsizeMenu.add(this.size14);
		group.add(this.size14);
		this.size16 = new JRadioButtonMenuItem("16");
		this.size16.addActionListener(new FontSizeListener(this, 16));
		this.fontsizeMenu.add(this.size16);
		group.add(this.size16);
		this.size18 = new JRadioButtonMenuItem("18");
		this.size18.addActionListener(new FontSizeListener(this, 18));
		this.fontsizeMenu.add(this.size18);
		group.add(this.size18);
		this.size24 = new JRadioButtonMenuItem("24");
		this.size24.addActionListener(new FontSizeListener(this, 24));
		this.fontsizeMenu.add(this.size24);
		group.add(this.size24);
		this.size36 = new JRadioButtonMenuItem("36");
		this.size36.addActionListener(new FontSizeListener(this, 36));
		this.fontsizeMenu.add(this.size36);
		group.add(this.size36);
		this.size48 = new JRadioButtonMenuItem("48");
		this.size48.addActionListener(new FontSizeListener(this, 48));
		this.fontsizeMenu.add(this.size48);
		group.add(this.size48);

		switch (this.fontSize) {
		case 8:
			this.size8.setSelected(true);
			break;
		case 10:
			this.size10.setSelected(true);
			break;
		case 12:
			this.size12.setSelected(true);
			break;
		case 14:
			this.size14.setSelected(true);
			break;
		case 16:
			this.size16.setSelected(true);
			break;
		case 18:
			this.size18.setSelected(true);
			break;
		case 24:
			this.size24.setSelected(true);
			break;
		case 36:
			this.size36.setSelected(true);
			break;
		default:
			this.size48.setSelected(true);
			break;
		}

		// this.formatMenuItem = new JMenuItem();
		// this.formatMenuItem.setText(Utils.getResource("extras.format.text"));
		// this.formatMenuItem.setToolTipText(Utils.getResource("extras.format.tooltip"));
		// this.formatMenuItem.setMnemonic(Utils.getResource(
		// "extras.format.mnemonic").charAt(0));
		// this.formatMenuItem.setAccelerator(KeyStroke
		// .getKeyStroke(Utils.getResource("extras.format.keystroke")));
		// this.formatMenuItem.addActionListener(new FormatListener(this));
		// this.extrasMenu.add(this.formatMenuItem);

	}

	public static JCheckBoxMenuItem winSim = null;

	public static JCheckBoxMenuItem console = null;

	// Scheme Martin
	public static JCheckBoxMenuItem winSKon = null;

	// Prolog
	public static JCheckBoxMenuItem winPKon = null;

	// Python
	public static JCheckBoxMenuItem winPyKon = null;

	// JavaScript
	public static JCheckBoxMenuItem winJSKon = null;

	// Ruby
	public static JCheckBoxMenuItem winRubyKon = null;

	public static JCheckBoxMenuItem win3D = null; // chris

	private void handleWindowMenu() { // dibo 151106
		JMenu fensterMenue = view.findMenu("editor", "windows");

		fensterMenue.add(winSim = new JCheckBoxMenuItem(Utils.getResource("windows.simulation"), true));
		winSim.setAccelerator(KeyStroke.getKeyStroke(Utils.getResource("windows.simulation.mnemonic").charAt(0),
				InputEvent.ALT_MASK));
		winSim.addActionListener(new WindowVisible(view.getSimulationFrame()));

		if (Utils.runlocally) {
			fensterMenue.add(console = new JCheckBoxMenuItem(Utils.getResource("windows.console"), true));
			console.setAccelerator(KeyStroke.getKeyStroke(Utils.getResource("windows.console.mnemonic").charAt(0),
					InputEvent.ALT_MASK));
			console.addActionListener(new WindowVisible(view.getConsole()));
			console.setState(false);
			view.getConsole().setMenuItem(console);
		} else {
		}

		if (Utils.DREI_D) {
			fensterMenue.add(win3D = new JCheckBoxMenuItem(Utils // chris
					.getResource("windows.3dsimulation"), true));
			win3D.setAccelerator(KeyStroke.getKeyStroke(Utils.getResource("windows.3dsimulation.mnemonic").charAt(0),
					InputEvent.ALT_MASK));
			win3D.addActionListener(new WindowVisible(view.get3DSimulationFrame()));
			win3D.setState(false);
		} else if (DDException) {
			fensterMenue.add(win3D = new JCheckBoxMenuItem(Utils // chris
					.getResource("windows.3dsimulation"), false));
			win3D.setEnabled(false);
		}

		// Scheme Martin
		if (Utils.SCHEME) {
			fensterMenue.add(winSKon = new JCheckBoxMenuItem(Utils.getResource("windows.schemekonsole"), false));
			winSKon.setAccelerator(KeyStroke.getKeyStroke(Utils.getResource("windows.schemekonsole.mnemonic").charAt(0),
					InputEvent.ALT_MASK));
			winSKon.addActionListener(new WindowVisible(SchemeKonsole.getSchemeKonsole()));
		}

		// Prolog
		if (Utils.PROLOG) {
			// Initialisiere den PrologController
			try {
				PrologController.get();

			} catch (RuntimeException exc) {
				Utils.PROLOG = false;
				return;
			}

			fensterMenue.add(winPKon = new JCheckBoxMenuItem(Utils.getResource("windows.prologkonsole"), false));
			winPKon.setAccelerator(KeyStroke.getKeyStroke(Utils.getResource("windows.prologkonsole.mnemonic").charAt(0),
					InputEvent.ALT_MASK));
			winPKon.addActionListener(new WindowVisible(PrologKonsole.get()));
		}

		// Python
		if (Utils.PYTHON) {
			fensterMenue.add(winPyKon = new JCheckBoxMenuItem(Utils.getResource("windows.pythonkonsole"), false));
			winPyKon.setAccelerator(KeyStroke
					.getKeyStroke(Utils.getResource("windows.pythonkonsole.mnemonic").charAt(0), InputEvent.ALT_MASK));
			winPyKon.addActionListener(new ConsoleVisible(PythonKonsole.getPythonKonsole()));
			PythonKonsole.getPythonKonsole().setMenuItem(winPyKon);
		}

		// JavaScript
		// if (Utils.JAVASCRIPT) {
		// fensterMenue.add(winJSKon = new JCheckBoxMenuItem(Utils
		// .getResource("windows.javascriptkonsole"), false));
		// winJSKon.setAccelerator(KeyStroke.getKeyStroke(
		// Utils.getResource("windows.javascriptkonsole.mnemonic").charAt(
		// 0), InputEvent.ALT_MASK));
		// winJSKon.addActionListener(new ConsoleVisible(JavaScriptKonsole
		// .getJavaScriptKonsole()));
		// JavaScriptKonsole.getJavaScriptKonsole().setMenuItem(winJSKon);
		// }

		// Ruby
		if (Utils.RUBY) {
			fensterMenue.add(winRubyKon = new JCheckBoxMenuItem(Utils.getResource("windows.rubykonsole"), false));
			winRubyKon.setAccelerator(KeyStroke
					.getKeyStroke(Utils.getResource("windows.rubykonsole.mnemonic").charAt(0), InputEvent.ALT_MASK));
			winRubyKon.addActionListener(new WindowVisible(RubyKonsole.getRubyKonsole()));
		}

	}

	private static boolean DDException = false;

	public static void disable3D() {
		DDException = true;
		Utils.DREI_D = false;
	}

	class WindowVisible implements ActionListener {

		JFrame frame;

		WindowVisible(JFrame f) {
			this.frame = f;
		}

		public void actionPerformed(ActionEvent e) {
			if (frame.isVisible()) {
				frame.setVisible(false);
			} else {
				frame.toFront();
				frame.setVisible(true);
				frame.toFront();
			}
		}

	}

	class ConsoleVisible implements ActionListener {

		Console frame;

		ConsoleVisible(Console f) {
			this.frame = f;
		}

		public void actionPerformed(ActionEvent e) {
			if (frame.isVisible()) {
				frame.setVisible(false);
			} else {
				frame.init();
				frame.toFront();
				frame.setVisible(true);
				frame.toFront();
			}
		}

	}

	public static Workbench getWorkbench() {
		if (workbench == null)
			workbench = new Workbench(false, null);
		return workbench;
	}

	public static Workbench getOnlyWorkbench() {
		return workbench;
	}

	// dibo 11.01.2006
	public static Workbench getSimWorkbench(SimulationModel simModel) {
		System.exit(0);
		if (workbench == null)
			workbench = new Workbench(true, simModel);
		return workbench;
	}

	// dibo 11.01.2006
	public SimulationController getSimulationController() {
		return simulation;
	}

	public DebuggerController getDebuggerController() {
		return debugger;
	}

	/**
	 * Diese Funktion beendet den Hamster-Simulator.
	 */
	public void close(JFrame f) {
		// boolean val = Utils.ask(f, "editor.dialog.quit");
		// if (!val) return;
		if (model.getDebuggerModel().getState() != DebuggerModel.NOT_RUNNING)
			model.getDebuggerModel().stop();
		boolean res = editor.ensureSaved2(HamsterFile.getHamsterFile(Utils.HOME));
		if (!res)
			return;
		try {
			settings.store(new FileOutputStream(Utils.HOME + Utils.FSEP + "settings.properties"), "");
		} catch (IOException e) {
		}
		System.exit(0);
	}

	/**
	 * Diese Funktion hebt eine bestimmte Zeile im Programmtext hervor. Dazu wird
	 * diese im Editor geoeffnet und der Cursor springt zu der entsprechenden Zeile.
	 * 
	 * @param file
	 *            Die Datei, deren Zeile hervorgehoben werden soll.
	 * @param line
	 *            Die Zeile, die hervorgehoben werden soll.
	 */
	public void markLine(HamsterFile file, int line) {
		editor.markLine(file, line);
	}

	/**
	 * Diese Funktion hebt einen Compiler-Fehler im Programmtext hervor. Dazu wird
	 * die Datei zunaechst im Editor geoeffnet und dann wird an die Stelle
	 * gesprungen, die durch line und column bestimmt ist. Dort wird dann
	 * entsprechend das aktuelle Wort selektiert.
	 * 
	 * @param file
	 *            Die Datei, in der sich der Fehler befindet.
	 * @param line
	 *            Die Zeile, in der sich der Fehler befindet.
	 * @param column
	 *            Die Spalte, in der sich der Fehler befindet.
	 */
	public void markError(HamsterFile file, int line, int column) {
		editor.markError(file, line, column);
	}

	public boolean ensureSaved(HamsterFile file) {
		return editor.ensureSaved(file);
	}

	public boolean ensureCompiled(HamsterFile file) {
		return compiler.ensureCompiled(file);
	}

	/**
	 * Ueber diese Methode benachrichtigt der Editor den Compiler und den Debugger
	 * darueber, dass nun eine andere Datei editiert wird.
	 * 
	 * @param file
	 *            Die nun editierte Datei.
	 */
	public void setActiveFile(HamsterFile file) {
		compiler.setActiveFile(file);
		debugger.setActiveFile(file);
		/* lego */
		if (Utils.LEGO) {
			lego.setActiveFile(file);
		}
	}

	/**
	 * Diese Methode wird beim Start des Hamster-Simulators aufgerufen.
	 * 
	 * @param args
	 *            Als Argument kann der Simulator ein ham-File entgegennehmen
	 */
	public static void main(String[] args) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					if (!checkVersion()) {
						return;
					}
					Locale.setDefault(Locale.GERMANY);
					// Sicherstellen, dass die noetigen Verzeichnisse
					// existieren, in
					// denen die Hamsterprogramme abgelegt werden.

					Utils.loadProperties(); // dibo
					if (Utils.language.equals("en")) {
						Locale.setDefault(Locale.ENGLISH);
					}
					Utils.ensureHome();

					handleLAF();

					// Erzeugen der Werkbank.
					Workbench wb = getWorkbench();

					if (Utils.PYTHON) {
						if (!PythonHamster.initPython()) {
							Utils.PYTHON = false;
						}
					}
					if (Utils.JAVASCRIPT) {
						if (!JavaScriptHamster.initJavaScript()) {
							Utils.JAVASCRIPT = false;
						}
					}
					if (Utils.RUBY) {
						if (!RubyHamster.initRuby()) {
							Utils.RUBY = false;
						}
					}
					if (Utils.SCRATCH) {
						if (!ScratchHamster.initScratch()) {
							Utils.SCRATCH = false;
						}
					}
					if (Utils.FSM) {
						if (!FsmHamster.initFSM()) {
							Utils.FSM = false;
						}
					}
					if (Utils.FLOWCHART) {
						if (!FlowchartHamster.initFlowchart()) {
							Utils.FLOWCHART = false;
						}
					}

					if (Utils.SCHEME) {
						boolean ok = de.hamster.scheme.model.SchemeHamster.initScheme();
						de.hamster.scheme.model.SchemeHamster.setWorkbench(wb);
						if (!ok) {
							Utils.SCHEME = false;
						}
					}
				}
			});

			// added by C. Noeske: if a .ham file is given, open it
			if (args.length > 0) {
				Workbench wb = getWorkbench();
				EditorController edctrl = wb.getEditor();
				File f = new File(args[0]);
				edctrl.open(f);
			}
			// --- end of addition

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean checkVersion() {
		String version = System.getProperty("java.specification.version");
		if (version.startsWith("1.8")) {
			return true;
		}
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		if (javac == null) {
			JOptionPane.showMessageDialog(null, "Ab Java SE 9 kann der Hamster-Simulator "
					+ "nicht mehr mit einem JRE\n" + "sondern nur noch mit einem JDK gestartet werden!\n\n"
					+ "Lade bitte von der URL http://www.oracle.com/technetwork/java/javase/downloads/index.html\n"
					+ "ein JDK herunter, installiere und nutze es!", "JDK erforderlich", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public static void handleLAF() {
		try {
			JFrame.setDefaultLookAndFeelDecorated(true); // jrahn
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // jrahn
			startLAF = UIManager.getLookAndFeel(); // jrahn
	
			UIManager.put("TabbedPane.background", new java.awt.Color(67,67,67)); // jrahn: Tab-Hintergrund dunkel
			UIManager.put("TabbedPane.foreground", new java.awt.Color(255,255,255)); // jrahn: Tab-Schrift weiß
			UIManager.put("TabbedPane.selected", new java.awt.Color(128, 169, 98)); // jrahn: ausgewaehlten Tab grün
			UIManager.put("TabbedPane.focus", new java.awt.Color(128, 169, 98)); // jrahn: Fokusfarbe grün
			UIManager.put("TabbedPane.highlight", new java.awt.Color(67, 67, 67)); // jrahn: Highlight dunkel
			UIManager.put("TabbedPane.light", new java.awt.Color(67, 67, 67)); // jrahn: helle Kante dunkel
			UIManager.put("TabbedPane.shadow", new java.awt.Color(30, 30, 30)); // jrahn: Schatten anpassen
			UIManager.put("TabbedPane.darkShadow", new java.awt.Color(10, 10, 10)); // jrahn: dunklen Schatten anpassen


		} catch (Exception e) {
			e.printStackTrace();
			JFrame.setDefaultLookAndFeelDecorated(false); // jrahn
		}
	}

	/**
	 * Diese Methode liefert den Controller des Compilers.
	 * 
	 * @return Der Controller des Compilers.
	 */
	public CompilerController getComiler() {
		return compiler;
	}

	/**
	 * Diese Methode liefert den Controller des Debuggers.
	 * 
	 * @return Der Controller des Debuggers.
	 */
	public DebuggerController getDebugger() {
		return debugger;
	}

	/**
	 * Diese Methode liefert den Controller des Editors.
	 * 
	 * @return Der Controller des Editors.
	 */
	public EditorController getEditor() {
		return editor;
	}

	/**
	 * Diese Methode liefert den Controller der Simulation.
	 * 
	 * @return Der Controller der Simulation.
	 */
	public SimulationController getSimulation() {
		return simulation;
	}

	/**
	 * Diese Methode liefert das WorkbenchModel.
	 * 
	 * @return Das WorkbenchModel.
	 */
	public WorkbenchModel getModel() {
		return model;
	}

	/**
	 * Diese Methode liefert die WorkbenchView.
	 * 
	 * @return Die WorkbenchView.
	 */
	public WorkbenchView getView() {
		return view;
	}

	public String getProperty(String key, String defaultValue) {
		return settings.getProperty(key, defaultValue);
	}

	public void setProperty(String key, String value) {
		settings.setProperty(key, value);
	}

	public void stop() {
		model.getDebuggerModel().stop();
		DialogTerminal.getInstance().close();
	}
}

class IndentListener implements ActionListener {
	Workbench w;

	IndentListener(Workbench w) {
		this.w = w;
	}

	public void actionPerformed(ActionEvent e) {
		Utils.INDENT = !Utils.INDENT;
		w.indentMenuItem.setSelected(Utils.INDENT);

	}

}

class FontSizeListener implements ActionListener {

	Workbench workbench;
	int size;

	public FontSizeListener(Workbench editor, int size) {
		this.workbench = editor;
		this.size = size;
	}

	public void actionPerformed(ActionEvent e) {
		workbench.fontSize = size;
		workbench.getEditor().getTabbedTextArea().changeFontSize(size);
		// editor.lineNumbePanel.doc.changeFontSize(fontSize);
	}

}