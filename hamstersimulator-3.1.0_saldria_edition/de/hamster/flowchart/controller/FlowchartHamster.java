package de.hamster.flowchart.controller;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.interpreter.Hamster;
import de.hamster.workbench.Workbench;

/**
 * Repräsentation des Standard-Hamsters als Flowchart-Hamster
 * 
 * @author dibo
 * 
 */
public class FlowchartHamster extends Hamster {

	// dies ist der Standard-Hamster, der durch das Territorium gesteuert wird
	protected static FlowchartHamster hamster = new FlowchartHamster();

	// über diese Methode sollte auf den Standard-Hamster zugegriffen werden
	public static FlowchartHamster getFlowchartHamster() {
		return FlowchartHamster.hamster;
	}

	/**
	 * Diese Methode wird beim Start des Hamster-Simulators einmal aufgerufen,
	 * wenn das Property flowchart gesetzt ist. Hier koennen generelle
	 * Initialisierungen vorgenommen werden
	 * 
	 * @return true, wenn die Initialisierung erfolgreich war; false, sonst
	 */
	public static boolean initFlowchart() {
		return true;
	}

	// das Flowchart-Programm, das gerade ausgeführt wird; null, wenn sich
	// aktuell
	// kein Programm in Ausführung befindet
	private FlowchartProgram program = null;

	// bitte nicht verändern!
	private FlowchartHamster() {
		super(true);
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Start-Button klickt
	 * 
	 * @param program
	 *            das auszuführende Programm
	 */
	public void startProgram(FlowchartProgram program) {
		if (this.program == null) {
			Hamster._re_init(); // interne Initialisierungen

			// notwendig, um ein Programm mehrmals starten zu können
			this.program = new FlowchartProgram(program);

			this.program.start();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Stop-Button klickt
	 */
	public void stopProgram() {
		if (this.program != null) {
			this.program.stopProgram();
			try {
				this.program.join();
			} catch (InterruptedException exc) {
			}
		}
		this.program = null;
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Pause-Button klickt
	 */
	public void pauseProgram() {
		if (this.program != null) {
			this.program.pauseProgram();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer während das Programm pausiert den
	 * Start-Button klickt
	 */
	public void resumeProgram() {
		if (this.program != null) {
			this.program.resumeProgram();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den StepInto-Button klickt
	 */
	public void stepInto(FlowchartProgram prog) {

		// eine Implementierungsvariante wird hier skizziert

		if (this.program == null) { // Programm noch nicht gestarted
			Hamster._re_init();
			this.program = new FlowchartProgram(prog);
			this.program.pauseProgram();
			Workbench.getWorkbench().getDebuggerController().getDebuggerModel()
					.setState(DebuggerModel.PAUSED);
			Workbench.getWorkbench().getEditor().getTabbedTextArea()
					.propertyChange(this.program.file, true);
			this.program.start();
		} else {
			Workbench.getWorkbench().getDebuggerController().getDebuggerModel()
					.setState(DebuggerModel.PAUSED);
			this.program.stepInto();
		}
	}

	/**
	 * wird aufgerufen, wenn immer das Programm beendet wird
	 */
	void setProgramFinished() {
		this.program = null;
	}

}
