package de.hamster.debugger.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.StepRequest;

import de.hamster.flowchart.controller.FlowchartHamster;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.fsm.controller.FsmHamster;
import de.hamster.fsm.controller.FsmHamsterFile;
import de.hamster.javascript.model.JavaScriptHamster;
import de.hamster.model.HamsterFile;
import de.hamster.model.InstructionProcessor;
import de.hamster.prolog.controller.PrologController;
import de.hamster.prolog.model.PrologHamster;
import de.hamster.python.model.PythonHamster;
import de.hamster.ruby.model.RubyHamster;
import de.hamster.scheme.model.SchemeHamster;
import de.hamster.scheme.view.SchemeKonsole;
import de.hamster.scratch.ScratchHamster;
import de.hamster.scratch.ScratchHamsterFile;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * TODO: Testen der Debuggersteuerung (vor allem ThreadResumed und
 * ConnectionClosed)
 *
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class DebuggerModel implements Runnable {
	/**
	 * This argument informs off a state-change.
	 */
	public static final String ARG_ENABLE = "enable";

	/**
	 * This argument informs off a state-change.
	 */
	public static final String ARG_STATE = "state-change";

	/**
	 * In this state the debugger is not running. It has no associated Runner.
	 */
	public static final int NOT_RUNNING = 0;

	/**
	 * In this state the debugger is executing the user-sourcecode.
	 */
	public static final int RUNNING = 2;

	/**
	 *
	 */
	public static final int PAUSED = 5;

	/**
	 * This is the current state of the Debugger.
	 */
	private int state;

	/**
	 * This is the HamsterFile to be debugged.
	 */
	private HamsterFile currentFile;

	/**
	 * The corresponding class name.
	 */
	private String className;

	private boolean enabled;

	// --- Generics statt raw types ---
	private final List<HamsterFile> lockedFiles;
	protected final List<ReferenceType> lockedRefs;

	protected int delay;

	protected boolean suspended;

	RemoteRunner runner;

	InstructionProcessor processor;
	VirtualMachine machine;
	EventRequestManager eventRequestManager;
	EventQueue eventQueue;
	StepRequest stepRequest;
	ThreadReference suspendedThread;
	Thread thread;

	// Martin
	Thread hamsterThread;

	protected List<StackFrame> stackFrames;

	private LocalProcessor localProcessor;

	// --- Ersatz für Observable (deprecated): PropertyChangeSupport ---
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public DebuggerModel(InstructionProcessor processor) {
		this.processor = processor;
		this.localProcessor = new LocalProcessor(processor, this);
		this.state = DebuggerModel.NOT_RUNNING;
		this.delay = 500;
		this.lockedFiles = new ArrayList<>();
		this.lockedRefs = new ArrayList<>();
	}

	// Listener API (statt addObserver/notifyObservers)
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public int getState() {
		return this.state;
	}

	public List<StackFrame> getStackFrames() {
		return this.stackFrames;
	}

	public boolean isSuspended() {
		return this.suspended;
	}

	public void start(HamsterFile file) {
		if (this.state != DebuggerModel.NOT_RUNNING) {
			// TODO: ueber Exceptions
			return;
		}
		this.currentFile = file;
		if (this.currentFile == null) {
			return;
		}

		if (Utils.runlocally) {
			IHamster.processor = this.localProcessor;
			/* lego */de.hamster.lego.model.LHamster.processor = this.localProcessor;
			Territorium.processor = this.localProcessor;
			Hamster.count = 0;
			this.localProcessor.start();
			this.setState(DebuggerModel.PAUSED);
			try {
				this.localProcessor.run(new HamsterClassLoader().getHamsterInstance(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.className = this.currentFile.getName();

			LaunchingConnector connector = Bootstrap.virtualMachineManager().defaultConnector();

			// --- Generics statt raw Map ---
			Map<String, Connector.Argument> args = connector.defaultArguments();

			Connector.Argument main = args.get("main");
			main.setValue(RemoteRunner.class.getName() + " " + this.className);

			Connector.Argument options = args.get("options");
			String opt = "-classpath \"" + file.getDir() + Utils.PSEP + System.getProperty("java.class.path")
					+ Utils.PSEP + Workbench.getWorkbench().getProperty("classpath", "") + "\"";
			options.setValue(opt);
			try {
				this.machine = connector.launch(args);
				this.machine.resume(); // dibo 27082015
				Thread.sleep(500); // dibo 25102015
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			this.runner = new RemoteRunner(this.processor, this.machine.process());

			this.setState(DebuggerModel.PAUSED);
			this.stepRequest = null;
			this.eventRequestManager = this.machine.eventRequestManager();
			this.eventQueue = this.machine.eventQueue();

			this.thread = new Thread(this);
			this.thread.start();
			if (!this.enabled) {
				this.runner.setDelay(this.delay);
			}
			this.runner.start();
		}
	}

	// Martin
	/**
	 * Eine weitere Start-Methode um einen Scheme bzw. Prolog-Hamster zu
	 * starten. Diese Methode braucht als weiteren Parameter die
	 * Workbench-Objektreferenz.
	 */
	public void start(HamsterFile file, final Workbench workbench) {
		if (this.state != DebuggerModel.NOT_RUNNING) {
			// TODO: ueber Exceptions
			return;
		}
		this.currentFile = file;
		if (this.currentFile == null) {
			return;
		}

		if (file.getType() == HamsterFile.SCHEMEPROGRAM) {
			// Workbench im SchemeHamster setzen
			SchemeHamster.setWorkbench(workbench);

			// Scheme-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);
			this.hamsterThread = new Thread() {
				@Override
				public void run() {
					try {

						// dibo 260110
						JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
						if (!simFrame.isVisible()) {
							simFrame.setVisible(true);
							Workbench.winSim.setState(true);
						}
						simFrame.toFront();

						Workbench.getWorkbench().getSimulation().getSimulationModel().start(); // dibo
																								// 210110
						// Hamster-datei laden und starten
						SchemeHamster.load(DebuggerModel.this.currentFile.load(), true);
					} catch (Exception e) {
						System.out.println(e);
					} finally {
						DebuggerModel.this.setState(DebuggerModel.NOT_RUNNING);
						DebuggerModel.isStop = false;
						SchemeHamster.getWorkbench().getEditor().getTabbedTextArea()
								.propertyChange(DebuggerModel.this.currentFile, false);
					}
				}
			};
			this.hamsterThread.start();
		}
		// Prolog
		else if (file.getType() == HamsterFile.PROLOGPROGRAM) {
			// Prolog-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			// Deaktiviere Tool-Buttons im Sumulationsfenster..
			Workbench.getWorkbench().getSimulation().getSimulationTools().setSimulationPanelListenerEnabled(false);

			this.hamsterThread = new Thread() {
				@Override
				public void run() {
					try {

						// dibo 260110
						JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
						if (!simFrame.isVisible()) {
							simFrame.setVisible(true);
							Workbench.winSim.setState(true);
						}
						simFrame.toFront();

						/*
						 * Merke sich den alten Zustand des Territoriums vor,
						 * damit die M�glichkeit des Zur�cksetzens genutzt
						 * werden kann.
						 */
						Workbench.getWorkbench().getSimulation().getSimulationModel().start();
						/*
						 * �berpr�fe, ob der Prolog-Prozess noch
						 * ordnungsgem�� l�uft und starte ihn gegebenfalls
						 * neu..
						 */
						PrologController.get().ensurePrologEngineIsRunning();
						/*
						 * Initialisiere den PrologHamster neu. Dabei wird die
						 * Prolog-Datenbank aufger�umt und das aktuelle
						 * Territorium neu importiert.
						 */
						PrologHamster.get().initPrologHamster();
						/*
						 * L�dt das Benutzerprogramm und startet die
						 * 'main.'-Prozedur.
						 */
						PrologHamster.get().start(DebuggerModel.this.currentFile);
					} catch (Exception e) {
						System.err.println(e);
					} finally {
						DebuggerModel.this.setState(DebuggerModel.NOT_RUNNING);
						DebuggerModel.isStop = false;
						Workbench.getWorkbench().getEditor().getTabbedTextArea()
								.propertyChange(DebuggerModel.this.currentFile, false);
						Workbench.getWorkbench().getSimulation().getSimulationTools()
								.setSimulationPanelListenerEnabled(true);
					}
				}
			};
			this.hamsterThread.start();
			// Python
		} else if (file.getType() == HamsterFile.PYTHONPROGRAM) {

			// Python-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			this.hamsterThread = new Thread() {
				@Override
				public void run() {
					try {

						JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
						if (!simFrame.isVisible()) {
							simFrame.setVisible(true);
							Workbench.winSim.setState(true);
						}
						simFrame.toFront();

						Workbench.getWorkbench().getSimulation().getSimulationModel().start(); // dibo
																								// 210110

						// Hamster-datei laden und starten
						PythonHamster.load(DebuggerModel.this.currentFile.load(), true);
					} catch (Exception e) {
						System.out.println(e);
					} finally {
						DebuggerModel.this.setState(DebuggerModel.NOT_RUNNING);
						DebuggerModel.isStop = false;
						Workbench.getWorkbench().getEditor().getTabbedTextArea()
								.propertyChange(DebuggerModel.this.currentFile, false);
					}
				}
			};
			this.hamsterThread.start();
			// JavaScript
		} else if (file.getType() == HamsterFile.JAVASCRIPTPROGRAM) {

			// JavaScript-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			this.hamsterThread = new Thread() {
				@Override
				public void run() {
					try {

						JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
						if (!simFrame.isVisible()) {
							simFrame.setVisible(true);
							Workbench.winSim.setState(true);
						}
						simFrame.toFront();

						Workbench.getWorkbench().getSimulation().getSimulationModel().start(); // dibo
																								// 210110

						// Hamster-datei laden und starten
						JavaScriptHamster.load(DebuggerModel.this.currentFile.load(), true);
					} catch (Exception e) {
						System.out.println(e);
					} finally {
						DebuggerModel.this.setState(DebuggerModel.NOT_RUNNING);
						DebuggerModel.isStop = false;
						Workbench.getWorkbench().getEditor().getTabbedTextArea()
								.propertyChange(DebuggerModel.this.currentFile, false);
					}
				}
			};
			this.hamsterThread.start();
			// Ruby
		} else if (file.getType() == HamsterFile.RUBYPROGRAM) {

			// Ruby-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			this.hamsterThread = new Thread() {
				@Override
				public void run() {
					try {

						JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
						if (!simFrame.isVisible()) {
							simFrame.setVisible(true);
							Workbench.winSim.setState(true);
						}
						simFrame.toFront();

						Workbench.getWorkbench().getSimulation().getSimulationModel().start(); // dibo
																								// 210110

						// Hamster-datei laden und starten
						RubyHamster.load(DebuggerModel.this.currentFile.load(), true);
					} catch (ThreadDeath th) {
					} catch (Exception e) {
						// System.out.println(e);
					} finally {
						DebuggerModel.this.setState(DebuggerModel.NOT_RUNNING);
						DebuggerModel.isStop = false;
						Workbench.getWorkbench().getEditor().getTabbedTextArea()
								.propertyChange(DebuggerModel.this.currentFile, false);
					}
				}
			};
			this.hamsterThread.start();
			// Scratch
		} else if (file.getType() == HamsterFile.SCRATCHPROGRAM) {

			// Scratch-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
			if (!simFrame.isVisible()) {
				simFrame.setVisible(true);
				Workbench.winSim.setState(true);
			}
			simFrame.toFront();

			Workbench.getWorkbench().getSimulation().getSimulationModel().start(); // dibo
																					// 210110

			// Hamster starten
			ScratchHamster.getScratchHamster().startProgram((ScratchHamsterFile) this.currentFile);

		} // FSM
		else if (file.getType() == HamsterFile.FSM) {

			// FSM-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
			if (!simFrame.isVisible()) {
				simFrame.setVisible(true);
				Workbench.winSim.setState(true);
			}
			simFrame.toFront();

			Workbench.getWorkbench().getSimulation().getSimulationModel().start();

			// Hamster starten
			FsmHamster.getFSMHamster().startProgram(((FsmHamsterFile) this.currentFile).getProgram());

		}
		// Flowchart
		else if (file.getType() == HamsterFile.FLOWCHART) {

			// Flowchart-HamsterProgramm starten
			this.setState(DebuggerModel.RUNNING);
			workbench.getEditor().getTabbedTextArea().propertyChange(file, true);

			JFrame simFrame = Workbench.getWorkbench().getView().getSimulationFrame();
			if (!simFrame.isVisible()) {
				simFrame.setVisible(true);
				Workbench.winSim.setState(true);
			}
			simFrame.toFront();

			Workbench.getWorkbench().getSimulation().getSimulationModel().start();

			// Hamster starten
			FlowchartHamster.getFlowchartHamster().startProgram(((FlowchartHamsterFile) this.currentFile).getProgram());

		}
		DebuggerModel.isStop = false;
	}

	public static boolean isStop = false;

	@SuppressWarnings("deprecation")
	public void stop() {
		// System.out.println("STOPP");

		/*
		 * Martin Falls ein Scheme-Programm gestoppt werden soll muss dies
		 * anders geschehen als bei Java-Hamstern.
		 */
		if (SchemeKonsole.isRunning) {
			SchemeKonsole.hamsterThread.stop();
			this.setState(DebuggerModel.NOT_RUNNING);
			// Martin + Python + Ruby + JavaScript
		} else if (this.currentFile != null && (this.currentFile.getType() == HamsterFile.SCHEMEPROGRAM
				|| this.currentFile.getType() == HamsterFile.PYTHONPROGRAM
				|| this.currentFile.getType() == HamsterFile.RUBYPROGRAM
				|| this.currentFile.getType() == HamsterFile.JAVASCRIPTPROGRAM)) {
			DebuggerModel.isStop = true;
			try {
				this.hamsterThread.stop();
			} catch (ThreadDeath td) {
			}
			this.setState(DebuggerModel.NOT_RUNNING);
			// Unlock editor text area for editing..
			Workbench.getWorkbench().getEditor().getTabbedTextArea().propertyChange(this.currentFile, false);
			// Scratch
		} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.SCRATCHPROGRAM) {
			ScratchHamster.getScratchHamster().stopProgram();
			this.setState(DebuggerModel.NOT_RUNNING);
			Workbench.getWorkbench().getEditor().getTabbedTextArea().propertyChange(this.currentFile, false);
		} // FSM
		else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FSM) {
			FsmHamster.getFSMHamster().stopProgram();
			this.setState(DebuggerModel.NOT_RUNNING);
			Workbench.getWorkbench().getEditor().getTabbedTextArea().propertyChange(this.currentFile, false);
		}
		// FSM
		else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FLOWCHART) {
			FlowchartHamster.getFlowchartHamster().stopProgram();
			this.setState(DebuggerModel.NOT_RUNNING);
			Workbench.getWorkbench().getEditor().getTabbedTextArea().propertyChange(this.currentFile, false);
		}
		// Prolog
		else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.PROLOGPROGRAM) {
			PrologController.get().stopPrologEngine();
			DebuggerModel.isStop = false;
			this.hamsterThread.stop();
			this.setState(DebuggerModel.NOT_RUNNING);
			// Unlock editor text area for editing..
			Workbench.getWorkbench().getEditor().getTabbedTextArea().propertyChange(this.currentFile, false);
			Workbench.getWorkbench().getSimulation().getSimulationTools().setSimulationPanelListenerEnabled(true);
		} else if (this.currentFile != null) {
			if (Utils.runlocally) {
				this.localProcessor.stop();
			} else {
				this.machine.exit(0);
			}
		}
	}

	public void resume() {
		if (this.state == DebuggerModel.PAUSED) {
			if (this.currentFile != null && this.currentFile.getType() == HamsterFile.SCRATCHPROGRAM) {
				ScratchHamster.getScratchHamster().resumeProgram();
			} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FSM) {
				FsmHamster.getFSMHamster().resumeProgram();
			} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FLOWCHART) {
				FlowchartHamster.getFlowchartHamster().resumeProgram();
			} else {
				if (Utils.runlocally) {
					this.localProcessor.resume();
				} else {
					this.stepInto(this.currentFile);
				}
			}
			this.setState(DebuggerModel.RUNNING);
		} else {
			System.err.println("Error: resume, when not in SUSPENDED state.");
		}
	}

	public void stepOver(HamsterFile file) {
		this.currentFile = file;
		if (this.currentFile != null && this.currentFile.getType() == HamsterFile.SCRATCHPROGRAM) {
			ScratchHamster.getScratchHamster().stepOver((ScratchHamsterFile) this.currentFile);
		} else if (this.currentFile != null && (this.currentFile.getType() == HamsterFile.FSM
				|| this.currentFile.getType() == HamsterFile.FLOWCHART)) {
			// gibts nicht
		} else {
			this.step(StepRequest.STEP_OVER);
		}
	}

	public void stepInto(HamsterFile file) {
		this.currentFile = file;
		if (this.currentFile != null && this.currentFile.getType() == HamsterFile.SCRATCHPROGRAM) {
			ScratchHamster.getScratchHamster().stepInto((ScratchHamsterFile) this.currentFile);
		} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FSM) {
			FsmHamster.getFSMHamster().stepInto(((FsmHamsterFile) this.currentFile).getProgram());
		} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FLOWCHART) {
			FlowchartHamster.getFlowchartHamster().stepInto(((FlowchartHamsterFile) this.currentFile).getProgram());
		} else {

			this.step(StepRequest.STEP_INTO);
		}
	}

	public void pause() {
		if (this.currentFile != null
				&& (this.currentFile.getType() == HamsterFile.SCRATCHPROGRAM
						|| this.currentFile.getType() == HamsterFile.FSM)
				|| this.currentFile.getType() == HamsterFile.FLOWCHART) {
			if (this.state == DebuggerModel.NOT_RUNNING) {
				// TODO: Exceptions
				return;
			}
		}
		this.setState(DebuggerModel.PAUSED);

		// Scratch
		if (this.currentFile != null && this.currentFile.getType() == HamsterFile.SCRATCHPROGRAM) {
			ScratchHamster.getScratchHamster().pauseProgram();
		} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FSM) {
			FsmHamster.getFSMHamster().pauseProgram();
		} else if (this.currentFile != null && this.currentFile.getType() == HamsterFile.FLOWCHART) {
			FlowchartHamster.getFlowchartHamster().pauseProgram();
		} else {
			if (Utils.runlocally) {
				this.localProcessor.pause();
			} else {
				if (!this.enabled) {
					this.machine.suspend();
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			for (;;) {
				if (this.removeEvent()) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		this.removeTypeLocks();
		this.suspended = false;
		this.suspendedThread = null;
		this.stackFrames = null;
		this.setState(DebuggerModel.NOT_RUNNING);
		this.machine = null;
	}

	public HamsterFile getProgram(ReferenceType ref) {
		String sourcePath = null;
		try {
			sourcePath = ref.sourcePaths(null).get(0);
		} catch (AbsentInformationException e) {
			return null;
		}
		sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf('.'));

		sourcePath += ".ham";
		String classpath = this.currentFile.getDir() + Utils.PSEP
				+ Workbench.getWorkbench().getProperty("classpath", "");
		String[] cpEntries = classpath.split(Utils.PSEP);
		for (int i = 0; i < cpEntries.length; i++) {
			File f = new File(cpEntries[i]);
			if (!f.isDirectory()) {
				continue;
			}
			File file = new File(f, sourcePath);
			if (file.exists()) {
				return HamsterFile.getHamsterFile(file.getAbsolutePath());
			}
		}
		return null;
	}

	private void removeTypeLocks() {
		for (int i = 0; i < this.lockedFiles.size(); i++) {
			HamsterFile file = this.lockedFiles.get(i);
			file.setLocked(false);
		}
		this.lockedFiles.clear();
		this.lockedRefs.clear();
	}

	private void lockType(ReferenceType ref) {
		HamsterFile file = this.getProgram(ref);
		if (file != null) {
			file.setLocked(true);
			this.lockedFiles.add(file);
		} else {
			if (ref.name().startsWith("java.") || ref.name().startsWith("javax.")
					|| ref.name().startsWith("de.hamster.") || ref.name().startsWith("sun.")) {
				return;
			}
			this.lockedRefs.add(ref);
		}
	}

	private boolean removeEvent() throws InterruptedException, IncompatibleThreadStateException {
		EventSet set = this.eventQueue.remove();
		EventIterator eventIterator = set.eventIterator();
		boolean resume = false;
		while (eventIterator.hasNext()) {
			Event event = eventIterator.nextEvent();
			if (event instanceof VMStartEvent) {
				ClassPrepareRequest r = this.eventRequestManager.createClassPrepareRequest();
				r.enable();
				resume = true;
			} else if (event instanceof VMDisconnectEvent) {
				return true;
			} else if (event instanceof VMDeathEvent) {
				resume = true;
			} else if (event instanceof ClassPrepareEvent) {
				ClassPrepareEvent e = (ClassPrepareEvent) event;
				ReferenceType ref = e.referenceType();
				this.lockType(ref);
				if (ref.name().equals(this.className)) {
					MethodEntryRequest entryRequest = this.eventRequestManager.createMethodEntryRequest();
					entryRequest.addClassFilter(this.className);
					entryRequest.enable();
				}
				resume = true;
			} else if (event instanceof MethodEntryEvent) {
				MethodEntryEvent e = (MethodEntryEvent) event;
				Method m = e.method();
				if (m.isConstructor()) {
					resume = true;
				} else {
					this.eventRequestManager.deleteEventRequest(event.request());
					this.suspendedThread = e.thread();
					this.stackFrames = e.thread().frames(0, e.thread().frameCount() - 1);
				}
			} else if (event instanceof StepEvent) {
				StepEvent e = (StepEvent) event;
				this.suspendedThread = e.thread();
				this.stackFrames = e.thread().frames(0, e.thread().frameCount() - 1);
			} else {
				resume = true;
			}
		}

		if (resume | !this.enabled) {
			set.resume();
		} else if (this.state == DebuggerModel.RUNNING) {
			this.suspended = true;
			this.fireStateChanged();

			try {
				Thread.sleep(this.delay);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			if (this.state == DebuggerModel.RUNNING) {
				this.suspended = false;
				this.fireStateChanged();
				this.stepInto(this.currentFile);
			}
		} else {
			this.setState(DebuggerModel.PAUSED);
		}
		return false;
	}

	private void step(int stepDepth) {
		if (this.suspendedThread == null) {
			return;
		}
		if (this.stepRequest != null) {
			this.eventRequestManager.deleteEventRequest(this.stepRequest);
		}
		this.stepRequest = this.eventRequestManager.createStepRequest(this.suspendedThread, StepRequest.STEP_LINE,
				stepDepth);
		for (int i = 0; i < this.lockedRefs.size(); i++) {
			this.stepRequest.addClassExclusionFilter(this.lockedRefs.get(i).name());
		}
		this.stepRequest.addClassExclusionFilter("jdk.*");
		this.stepRequest.addClassExclusionFilter("java.*");
		this.stepRequest.addClassExclusionFilter("javax.*");
		this.stepRequest.addClassExclusionFilter("sun.*");
		this.stepRequest.addClassExclusionFilter("de.hamster.*");
		this.stepRequest.addCountFilter(1);
		this.stepRequest.enable();
		ThreadReference st = this.suspendedThread;
		this.suspendedThread = null;
		this.machine.resume();
	}

	/**
	 * Setzt den Zustand und feuert ein PropertyChange-Event für ARG_STATE.
	 * UI-Code kann dann z.B. auf (evt.getPropertyName().equals(ARG_STATE))
	 * reagieren.
	 */
	public void setState(int state) {
		int oldState = this.state;
		this.state = state;

		// Event möglichst auf dem EDT feuern (Swing)
		if (java.awt.EventQueue.isDispatchThread()) {
			this.pcs.firePropertyChange(ARG_STATE, oldState, this.state);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						DebuggerModel.this.pcs.firePropertyChange(ARG_STATE, oldState, DebuggerModel.this.state);
					}
				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void fireStateChanged() {
		// Bei "suspended" ändert sich nicht zwingend state, aber die UI soll trotzdem refreshen.
		// Daher feuern wir ein ARG_STATE-Event mit null->null.
		if (java.awt.EventQueue.isDispatchThread()) {
			this.pcs.firePropertyChange(ARG_STATE, null, null);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					DebuggerModel.this.pcs.firePropertyChange(ARG_STATE, null, null);
				}
			});
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}
		boolean oldEnabled = this.enabled;
		this.enabled = enabled;

		if (this.state != DebuggerModel.NOT_RUNNING) {
			if (enabled) {
				this.runner.setDelay(0);
				this.stepInto(this.currentFile);
			} else {
				this.runner.setDelay(this.delay);
				if (this.state == DebuggerModel.RUNNING) {
					this.machine.resume();
				}
			}
		}

		// Auch hier: Event auf EDT
		if (java.awt.EventQueue.isDispatchThread()) {
			this.pcs.firePropertyChange(ARG_ENABLE, oldEnabled, this.enabled);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					DebuggerModel.this.pcs.firePropertyChange(ARG_ENABLE, oldEnabled, DebuggerModel.this.enabled);
				}
			});
		}
	}

	public void setDelay(int delay) {
		this.delay = delay;
		if (this.runner != null && !this.enabled) {
			this.runner.setDelay(delay);
		}
	}

	public int getDelay() {
		return this.delay;
	}

	public Thread getHamsterThread() {
		return this.hamsterThread;
	}
}