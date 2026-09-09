package de.hamster.debugger.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jdi.StackFrame;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.debugger.view.DebuggerTools;
import de.hamster.debugger.view.StackFrameTable;
import de.hamster.debugger.view.VariableViewer;
import de.hamster.model.HamsterFile;
import de.hamster.workbench.Workbench;

/**
 * Dies ist der Controller-Teil der Debugger-Komponente. Erzeugt die View und
 * nimmt Events von dieser entgegen.
 * 
 * TODO: Vor dem Ausfuehren auf Compilierung testen
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.2 $
 */
public class DebuggerController implements ActionListener, ChangeListener {
	/**
	 * Aktion Debugger aktiveren.
	 */
	public static final String ACTION_ENABLE = "enable";
	/**
	 * Aktion Schritt hinein.
	 */
	public static final String ACTION_STEP = "step";
	/**
	 * Aktion Schritt ueber.
	 */
	public static final String ACTION_STEPOVER = "stepover";
	/**
	 * Aktion Stoppen.
	 */
	public static final String ACTION_STOP = "stop";
	/**
	 * Aktion Pause.
	 */
	public static final String ACTION_PAUSE = "pause";
	/**
	 * Aktion Starten.
	 */
	public static final String ACTION_START = "start";
	/**
	 * Aktion Stackframe-Auswaehlen.
	 */
	public static final String ACTION_FRAME = "frame";

	/**
	 * Der Controller der Werkbank.
	 */
	protected Workbench workbench;

	/**
	 * Das dazugehoerige Model.
	 */
	protected DebuggerModel model;

	/**
	 * View-Komponente fuer Toolbars und Menues.
	 */
	protected DebuggerTools debuggerTools;
	/**
	 * View-Komponente zur Anzeige von Stackframes.
	 */
	protected StackFrameTable stackFrameList;
	/**
	 * View-Komponente zur Anzeige von Variablen.
	 */
	protected VariableViewer variableViewer;

	/**
	 * Die aktuell editierte Datei.
	 */
	protected HamsterFile activeFile;

	public DebuggerController(DebuggerModel model, Workbench workbench) {
		this.workbench = workbench;
		this.model = model;

		debuggerTools = new DebuggerTools(model, this);
		stackFrameList = new StackFrameTable(model, this);
		variableViewer = new VariableViewer(model);
	}

	/*
	 * Verarbeitet ActionEvents
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String zz = e.getActionCommand();
		if (e.getActionCommand() == ACTION_STEP) {
			if (activeFile != null
					&& (activeFile.getType() == HamsterFile.SCRATCHPROGRAM ||
							activeFile.getType() == HamsterFile.FSM ||
							activeFile.getType() == HamsterFile.FLOWCHART
							)) {
				workbench.getEditor().getTabbedTextArea().propertyChange(
						activeFile, true);
				model.stepInto(activeFile);
			} else if (model.getState() == DebuggerModel.NOT_RUNNING) {
				// Martin + Python + Ruby + JavaScript
				// Falls ein Scheme- oder Python Programm ausgeführt wird muss
				// der
				// start-Funktion
				// die workbench mit übergeben werden.
				if ((activeFile.getType() == HamsterFile.SCHEMEPROGRAM
						|| activeFile.getType() == HamsterFile.PYTHONPROGRAM || activeFile
						.getType() == HamsterFile.RUBYPROGRAM || activeFile
						.getType() == HamsterFile.JAVASCRIPTPROGRAM)
						&& workbench.ensureSaved(activeFile)) {
					model.start(activeFile, workbench);
				} else if (workbench.ensureSaved(activeFile)
						&& workbench.ensureCompiled(activeFile))
					model.start(activeFile);
			} else {
				model.stepInto(activeFile);
			}
		} else if (e.getActionCommand() == ACTION_STEPOVER) {
			// gar nicht möglich / implementiert
			if (activeFile != null
					&& (activeFile.getType() == HamsterFile.SCRATCHPROGRAM ||
							activeFile.getType() == HamsterFile.FSM ||
							activeFile.getType() == HamsterFile.FLOWCHART
							)) {
				workbench.getEditor().getTabbedTextArea().propertyChange(
						activeFile, true);
			}
			model.stepOver(activeFile);
		} else if (e.getActionCommand() == ACTION_START) {
			if (model.getState() == DebuggerModel.PAUSED) {
				model.resume();
				// Martin + Python + Ruby + JavaScript
			} else if ((activeFile.getType() == HamsterFile.SCHEMEPROGRAM
					|| activeFile.getType() == HamsterFile.PYTHONPROGRAM || activeFile
					.getType() == HamsterFile.RUBYPROGRAM|| activeFile
					.getType() == HamsterFile.JAVASCRIPTPROGRAM)
					&& workbench.ensureSaved(activeFile)) {
				// Martin Python + JavaScript
				// Falls ein Scheme- oder Python Programm ausgeführt wird muss
				// der start-Funktion die workbench mit übergeben werden.
				model.setEnabled(false); // dibo 290710
				model.start(activeFile, workbench);
				// Scratch
			} else if (activeFile.getType() == HamsterFile.SCRATCHPROGRAM) {
				model.setEnabled(false); // dibo 290710
				model.start(activeFile, workbench);
				// FSM
			} else if (activeFile.getType() == HamsterFile.FSM) {
				model.setEnabled(false); 
				model.start(activeFile, workbench);
				// Flowchart
			} else if (activeFile.getType() == HamsterFile.FLOWCHART) {
				model.setEnabled(false); 
				model.start(activeFile, workbench);
				// Prolog
			} else if (activeFile.getType() == HamsterFile.PROLOGPROGRAM
					&& workbench.ensureSaved(activeFile)) {
				// Ein Prolog-Programm wird immer zur Laufzeit interpretiert,
				// Es braucht nicht vorher kompelliert zu werden..
				model.setEnabled(false); // dibo 290710
				model.start(activeFile, workbench);
			} else if (model.getState() == DebuggerModel.NOT_RUNNING) {
				if (workbench.ensureSaved(activeFile)
						&& workbench.ensureCompiled(activeFile)) {
					model.start(activeFile);
					model.resume();
				}
			}
		} else if (e.getActionCommand() == ACTION_ENABLE) {
			model.setEnabled(!model.isEnabled());
		} else if (e.getActionCommand() == ACTION_FRAME) {
			StackFrame frame = stackFrameList.getSelectedFrame();
			HamsterFile f = model.getProgram(frame.location().declaringType());
			workbench.markLine(f, frame.location().lineNumber() - 1);
			variableViewer.showVariable(frame);
		} else if (e.getActionCommand() == ACTION_STOP) {
			workbench.stop();
		} else if (e.getActionCommand() == ACTION_PAUSE) {
			model.pause();
		}
	}

	/**
	 * Liefert die Werkbank
	 * 
	 * @return Die Werkbank
	 */
	public Workbench getWorkbench() {
		return workbench;
	}

	/**
	 * Liefert die Stackframe-Anzeige
	 * 
	 * @return Die Stackframe-Anzeige
	 */
	public StackFrameTable getStackFrameTable() {
		return stackFrameList;
	}

	/**
	 * Liefert die Variablen-Anzeige
	 * 
	 * @return Die Variablen-Anzeige
	 */
	public VariableViewer getVariableViewer() {
		return variableViewer;
	}

	/**
	 * Setzt die aktuelle editierte Datei.
	 * 
	 * @param activeFile
	 *            Die neue Datei
	 */
	public void setActiveFile(HamsterFile activeFile) {
		this.activeFile = activeFile;
		debuggerTools.setActiveFile(activeFile);
	}

	public HamsterFile getActiveFile() {
		return this.activeFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent event) {
		JSlider delay = (JSlider) event.getSource();
		if (delay.getName().equals("delay"))
			model.setDelay(delay.getValue()); // diboxy
	}

	public DebuggerModel getDebuggerModel() {
		return this.model;
	}
}