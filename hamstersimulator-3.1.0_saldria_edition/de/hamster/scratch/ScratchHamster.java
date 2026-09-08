package de.hamster.scratch;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.interpreter.Hamster;
import de.hamster.workbench.Workbench;

/**
 * Repräsentation des Standard-Hamsters als Scratch-Hamster
 * 
 * @author dibo
 * 
 */
public class ScratchHamster extends Hamster {

	public static ScratchHamster hamster = new ScratchHamster();

	public static ScratchHamster getScratchHamster() {
		return hamster;
	}

	/**
	 * Wird beim Start des Hamster-Simulators einmal aufgerufen, wenn das
	 * Property sratch gesetzt ist. Hier koennen generelle Initialisierungen
	 * vorgenommen werden
	 * 
	 * @return true, wenn die Initialisierung erfolgreich war; false, sonst
	 */
	public static boolean initScratch() {
		return true;
	}

	private ScratchProgram program = null;

	private ScratchHamster() {
		super(true);
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Start-Button klickt
	 * 
	 * @param file
	 *            die auszuführende Datei/Programm
	 */
	public void startProgram(ScratchHamsterFile file) {
		if (program == null) {
			Hamster._re_init();
			program = new ScratchProgram();
			program.setProgram(file.getProgram().getProgram());
			program.setRefreshHandler(file.getProgram().getRefreshHandler());
			program.setNextMethodHandler(file.getProgram().getNextMethodHandler());
			program.setFile(file);
			program.start();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Stop-Button klickt
	 */
	public void stopProgram() {
		if (program != null) {
			program.stopProgram();
			try {
				program.join();
			} catch (InterruptedException exc) {
			}
		}
		program = null;
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Pause-Button klickt
	 */
	public void pauseProgram() {
		if (program != null) {
			program.pauseProgram();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer während das Programm pausiert den
	 * Start-Button klickt
	 */
	public void resumeProgram() {
		if (program != null) {
			program.resumeProgram();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den StepOver-Button klickt
	 */
	public void stepOver(ScratchHamsterFile file) {
		// ist eh nicht frei geschaltet
		stepInto(file);
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den StepInto-Button klickt
	 */
	public void stepInto(ScratchHamsterFile file) {	
		if (program == null) { // Programm noch nicht gestarted
			Hamster._re_init();
			program = new ScratchProgram();
			program.setProgram(file.getProgram().getProgram());
			program.setRefreshHandler(file.getProgram().getRefreshHandler());
			program.setNextMethodHandler(file.getProgram().getNextMethodHandler());
			program.setFile(file);
			program.pauseProgram();
			Workbench.getWorkbench().getDebuggerController().getDebuggerModel()
					.setState(DebuggerModel.PAUSED);
			Workbench.getWorkbench().getEditor().getTabbedTextArea()
					.propertyChange(file, true);
			program.start();
		} else {
			Workbench.getWorkbench().getDebuggerController().getDebuggerModel()
					.setState(DebuggerModel.PAUSED);
			program.stepInto();
		}
	}

	void setProgramFinished() {
		program = null;
	}

}
