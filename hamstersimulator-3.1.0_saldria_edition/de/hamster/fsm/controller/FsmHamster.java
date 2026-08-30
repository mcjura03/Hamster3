package de.hamster.fsm.controller;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.interpreter.Hamster;
import de.hamster.workbench.Workbench;

/**
 * Repräsentation des Standard-Hamsters als FSM-Hamster
 * 
 * @author dibo
 * 
 */
public class FsmHamster extends Hamster {

	// dies ist der Standard-Hamster, der durch das Territorium gesteuert wird
	protected static FsmHamster hamster = new FsmHamster();

	// über diese Methode sollte auf den Standard-Hamster zugegriffen werden
	public static FsmHamster getFSMHamster() {
		return FsmHamster.hamster;
	}

	/**
	 * Diese Methode wird beim Start des Hamster-Simulators einmal aufgerufen,
	 * wenn das Property fsm gesetzt ist. Hier koennen generelle
	 * Initialisierungen vorgenommen werden
	 * 
	 * @return true, wenn die Initialisierung erfolgreich war; false, sonst
	 */
	public static boolean initFSM() {
		return true;
	}

	// das FSM-Programm, das gerade ausgeführt wird; null, wenn sich aktuell
	// kein Programm in Ausführung befindet
	private FsmProgram program = null;

	// bitte nicht verändern!
	private FsmHamster() {
		super(true);
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Start-Button klickt
	 * 
	 * @param program
	 *            das auszuführende Programm
	 */
	public void startProgram(FsmProgram program) {
		if (this.program == null) {
			Hamster._re_init(); // interne Initialisierungen

			// notwendig, um ein Programm mehrmals starten zu können
			this.program = new FsmProgram(program);

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
	public void stepInto(FsmProgram prog) {

		// eine Implementierungsvariante wird hier skizziert

		if (this.program == null) { // Programm noch nicht gestarted
			Hamster._re_init();
			this.program = new FsmProgram(prog);
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
	
	// neu: 20.06.2012

	private final static int INTERN_ID = -1; // siehe Klasse Hamster (ID des
												// Standard-Hamsters)

	/**
	 * liefert genau dann true, wenn sich in Blickrichtung vor dem aufgerufenen
	 * Hamster keine Mauer befindet (wenn sich der Hamster in Blickrichtung am
	 * Rand des Territoriums befindet, wird false geliefert); keine Ausgabe auf
	 * das LogPanel
	 * 
	 * @return true, wenn sich in Blickrichtung vor dem aufgerufenen Hamster
	 *         keine Mauer befindet; sonst false
	 */
	public boolean vornFreiQuiet() {
		return Workbench.getWorkbench().getSimulationController()
				.getSimulationModel().free(INTERN_ID);
	}

	/**
	 * liefert genau dann true, wenn der aufgerufene Hamster keine Koerner im
	 * Maul hat; ohne Ausgabe auf das LogPanel
	 * 
	 * @return true, wenn der aufgerufene Hamster keine Koerner im Maul hat;
	 *         sonst false
	 */
	public boolean maulLeerQuiet() {
		return Workbench.getWorkbench().getSimulationController()
				.getSimulationModel().mouthEmpty(INTERN_ID);
	}

	/**
	 * liefert genau dann true, wenn auf der Kachel, auf der sich der
	 * aufgerufene Hamster gerade befindet, mindestens ein Korn liegt; keine
	 * Ausgabe auf das LogPanel
	 * 
	 * @return true, wenn auf der Kachel, auf der sich der aufgerufene Hamster
	 *         gerade befindet, mindestens ein Korn liegt; sonst false
	 */
	public boolean kornDaQuiet() {
		return Workbench.getWorkbench().getSimulationController()
				.getSimulationModel().cornAvailable(INTERN_ID);
	}

}
