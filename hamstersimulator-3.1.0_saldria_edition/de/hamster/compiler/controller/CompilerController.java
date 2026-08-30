package de.hamster.compiler.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.hamster.compiler.model.CompilerModel;
import de.hamster.compiler.model.JavaError;
import de.hamster.compiler.view.CompilerErrorPanel;
import de.hamster.compiler.view.CompilerTools;
import de.hamster.model.HamsterFile;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Dies ist der Controller-Teil der Compiler-Komponente.
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.3 $
 */
public class CompilerController implements ActionListener {
	/**
	 * Dieser ActionCommand wird von einer Action benutzt, die das Kompilieren
	 * startet.
	 */
	public static final String ACTION_COMPILE = "compile";

	/**
	 * Dieser ActionCommand wird benutzt, wenn ein Eintrag aus der Fehlertabelle
	 * ausgewaehlt wurde.
	 */
	public static final String ACTION_SELECT = "select";

	/**
	 * Dieses Kommando wird benutzt, wenn der CLASSPATH geaendert werden soll.
	 */
	public static final String ACTION_CLASSPATH = "classpath";

	/**
	 * Dies ist die Werkbank.
	 */
	private Workbench workbench;

	/**
	 * Das Model der Compiler-Componente.
	 */
	private CompilerModel compilerModel;

	/**
	 * Die Tools der Compiler-Komponente (View-Teil).
	 */
	private CompilerTools compilerTools;

	/**
	 * Die Fehleranzeige in Form einer Tabelle (View-Teil).
	 */
	private CompilerErrorPanel errorPanel;

	/**
	 * Dies ist die aktuelle editierte Datei. Diese wird bei einem Klick auf
	 * "Kompilieren" uebersetzt.
	 */
	private HamsterFile activeFile;

	/**
	 * Der Konstruktor des CompilerControllers. Erzeugt die View-Komponenten.
	 * 
	 * @param model
	 *            Das schon erzeugte Model
	 * @param workbench
	 *            Die Werkbank
	 */
	public CompilerController(CompilerModel model, Workbench workbench) {
		this.workbench = workbench;

		this.compilerModel = model;
		this.compilerTools = new CompilerTools(model, this);
		this.errorPanel = new CompilerErrorPanel(model, this);
	}

	public boolean ensureCompiled(HamsterFile file) {
		File classFile = new File(file.getFile().getParent(), file.getName()
				+ ".class");
		if (!classFile.exists()
				|| file.lastModified() > classFile.lastModified()) {
			int val = Utils.confirm(Workbench.getWorkbench().getView()
					.getEditorFrame(), "compiler.dialog.compile");
			if (val == Utils.CANCEL) {
				return false;
			} else if (val == Utils.YES) {
				try {
					return this.compilerModel.compile(file); // dibo 15.11.2010
				} catch (IOException e) { // dibo 25.10.2011
					String msg = Utils.getResource("compiler.msg.rights1")
							+ Utils.HOME
							+ Utils.getResource("compiler.msg.rights2");
					String title = Utils.getResource("compiler.msg.title");
					JOptionPane.showMessageDialog(this.workbench.getView()
							.getEditorFrame(), msg, title,
							JOptionPane.ERROR_MESSAGE);
				}
			} else if (val == Utils.NO) {
				return false; // dibo 15.11.2010
			}
		}
		return true;
	}

	/**
	 * Wird von den View-Komponenten ausgeloest und faehrt die entsprechende
	 * Aktion aus.
	 * 
	 * @param e
	 *            Der ActionEvent, der die Aktion beschreibt.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == CompilerController.ACTION_COMPILE) {
			if (this.workbench.ensureSaved(this.activeFile)) {
				try {
					this.compilerModel.compile(this.activeFile);
				} catch (IOException e1) { // dibo 25.10.2011
					String msg = Utils.getResource("compiler.msg.rights1")
							+ Utils.HOME
							+ Utils.getResource("compiler.msg.rights2");
					String title = Utils.getResource("compiler.msg.title");
					JOptionPane.showMessageDialog(this.workbench.getView()
							.getEditorFrame(), msg, title,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getActionCommand() == CompilerController.ACTION_SELECT) {
			JavaError error = this.errorPanel.getSelectedError();
			this.workbench.markError(error.getFile(), error.getLine() - 1,
					error.getColumn());
		} else if (e.getActionCommand() == CompilerController.ACTION_CLASSPATH) {
			String text = Utils.getResource("compiler.dialog.classpath.text",
					Utils.PSEP);
			String classpath = (String) JOptionPane.showInputDialog(
					this.workbench.getView().getEditorFrame(), text,
					Utils.getResource("compiler.dialog.classpath.title"),
					JOptionPane.QUESTION_MESSAGE, null, null,
					this.workbench.getProperty("classpath", ""));
			if (classpath != null) {
				this.workbench.setProperty("classpath", classpath);
			}
		}
	}

	/**
	 * Liefert die Fehlertabelle
	 * 
	 * @return die Fehlertabelle
	 */
	public CompilerErrorPanel getErrorPanel() {
		return this.errorPanel;
	}

	/**
	 * Liefert die Werkbank
	 * 
	 * @return die Werkbank
	 */
	public Workbench getWorkbench() {
		return this.workbench;
	}

	/**
	 * Setzt activeFile.
	 * 
	 * @param activeFile
	 *            Der neue Wert von activeFile.
	 */
	public void setActiveFile(HamsterFile activeFile) {
		this.activeFile = activeFile;

		// Martin
		if (activeFile == null
				|| this.activeFile.getType() == HamsterFile.SCHEMEPROGRAM) {
			// Falls die aktuelle Datei ein Scheme-Programm ist, brauchen
			// die Buttons zum kompilieren nicht dargestellt werden.
			this.compilerTools.setButtonsVisible(false);
			// Prolog
		} else if (this.activeFile.getType() == HamsterFile.PROLOGPROGRAM) {
			// Das gleiche wie oben gilt auch für Prolog-Programme..
			this.compilerTools.setButtonsVisible(false);
			// Python
		} else if (this.activeFile.getType() == HamsterFile.PYTHONPROGRAM) {
			// Das gleiche wie oben gilt auch für Python-Programme..
			this.compilerTools.setButtonsVisible(false);
			// Ruby
		} else if (this.activeFile.getType() == HamsterFile.RUBYPROGRAM) {
			// Das gleiche wie oben gilt auch für Ruby-Programme..
			this.compilerTools.setButtonsVisible(false);
			// Scratch
		} else if (this.activeFile.getType() == HamsterFile.SCRATCHPROGRAM) {
			// Das gleiche wie oben gilt auch für Scratch-Programme..
			this.compilerTools.setButtonsVisible(false);
			// FSM
		} else if (this.activeFile.getType() == HamsterFile.FSM) {
			// Das gleiche wie oben gilt auch für FSM-Programme..
			this.compilerTools.setButtonsVisible(false);
			// Flowchart
		} else if (this.activeFile.getType() == HamsterFile.FLOWCHART) {
			// Das gleiche wie oben gilt auch für Flowchart-Programme..
			this.compilerTools.setButtonsVisible(false);
		} else if (this.activeFile.getType() == HamsterFile.JAVASCRIPTPROGRAM) {
			// Das gleiche wie oben gilt auch für JavaScript
			this.compilerTools.setButtonsVisible(false);
		} else {
			this.compilerTools.setButtonsVisible(true);
		}
	}
}