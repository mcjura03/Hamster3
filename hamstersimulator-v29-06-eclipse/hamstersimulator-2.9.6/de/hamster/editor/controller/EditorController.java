package de.hamster.editor.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import de.hamster.editor.view.EditorTools;
import de.hamster.editor.view.FileTree;
import de.hamster.editor.view.FileTreeNode;
import de.hamster.editor.view.NewHamsterDialog;
import de.hamster.editor.view.TabbedTextArea;
import de.hamster.editor.view.TextArea;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.fsm.controller.FsmHamsterFile;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.model.HamsterFile;
import de.hamster.prolog.controller.PrologController;
import de.hamster.prolog.model.PrologHamster.TerObject;
import de.hamster.scheme.model.SchemeHamster;
import de.hamster.scheme.view.SchemeKonsole;
import de.hamster.scratch.ScratchHamsterFile;
import de.hamster.scratch.ScratchProgram;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.model.Terrain;
import de.hamster.workbench.HamsterFileFilter;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.7 $
 */
public class EditorController implements TreeSelectionListener, ActionListener,
		ChangeListener, DocumentListener {
	public static final String ACTION_NEW = "new";
	public static final String ACTION_OPEN = "open";
	public static final String ACTION_SAVE = "save";
	public static final String ACTION_SAVE_AS = "saveas";
	public static final String ACTION_SAVE_PLUS = "saveplus";
	public static final String ACTION_SAVE_AS_PLUS = "saveasplus";
	public static final String ACTION_GENERATE = "generate";
	public static final String ACTION_CLOSE = "close";
	public static final String ACTION_PRINT = "print";
	public static final String ACTION_EXIT = "exit";
	public static final String ACTION_UNDO = "undo";
	public static final String ACTION_REDO = "redo";

	public static final String FILE_DELETE = "file_delete";
	public static final String FILE_OPEN = "file_open";
	public static final String FILE_COPY = "file_copy";
	public static final String FILE_PASTE = "file_paste";
	public static final String FILE_NEWFOLDER = "file_newfolder";
	public static final String FILE_RENAME = "file_rename";
	public static final String FILE_GENERATE = "file_generate";

	protected Workbench workbench;

	protected EditorTools editorTools;
	protected FileTree fileTree;
	protected TabbedTextArea tabbedTextArea;
	protected JFileChooser fileChooser;

	protected NewHamsterDialog newHamsterDialog;

	protected HamsterFile copiedFile;

	public EditorController(Workbench workbench) {
		this.workbench = workbench;

		this.editorTools = new EditorTools(this);
		this.tabbedTextArea = new TabbedTextArea(this);
		this.tabbedTextArea.setBackground(new Color(255, 255, 220)); // dibo
																		// 230309
		this.fileTree = new FileTree(this);
		this.fileTree.setBackground(new Color(240, 252, 202)); // dibo 230309

		this.newHamsterDialog = new NewHamsterDialog(this.tabbedTextArea);
		this.fileChooser = Utils.getFileChooser();
		this.fileChooser.setFileFilter(HamsterFileFilter.HAM_FILTER);
	}

	public FileTree getFileTree() {
		return this.fileTree;
	}

	public TabbedTextArea getTabbedTextArea() {
		return this.tabbedTextArea;
	}

	public Workbench getWorkbench() {
		return this.workbench;
	}

	public void markLine(HamsterFile file, int line) {
		TextArea textArea = this.tabbedTextArea.openFile(file);
		if (textArea != null) {
			textArea.markLine(line);
		}
	}

	public void markError(HamsterFile file, int line, int column) {
		TextArea textArea = this.tabbedTextArea.getActiveTextArea();
		textArea.requestFocus();
		textArea.selectError(line, column);
	}

	public boolean ensureSaved(HamsterFile file) {
		return this.tabbedTextArea.ensureSaved(file);
	}

	public boolean ensureSaved2(HamsterFile file) { // dibo 17.11.2010
		return this.tabbedTextArea.ensureSaved2(file);
	}

	public void refreshFiles() {
		this.fileTree.update();
		this.tabbedTextArea.refreshFiles();
	}

	protected boolean save() {
		HamsterFile file = this.tabbedTextArea.getActiveFile();
		if (file.exists()) {
			this.tabbedTextArea.getActiveTextArea().save();
			this.tabbedTextArea.getActiveFile().save(
					this.tabbedTextArea.getText());
			return true;
		} else {
			//return this.saveAs("NewHamsterFile");
			String filename = file.getName();
			if (filename != null) {
				return this.saveAs(filename);
			} else {
				return this.saveAs();
			}
		}
	}

	protected boolean savePlus() {
		SimulationModel simModel = Workbench.getWorkbench().getSimulation()
				.getSimulationModel();
		Terrain ter = simModel.getTerrain();
		boolean res = this.save();
		simModel.setTerrain(ter);
		if (!res) {
			return false;
		}

		if (this.tabbedTextArea.getActiveFile().exists()) {
			HamsterFile file = this.tabbedTextArea.getActiveFile();

			String name = file.getAbsolute();
			if (name.endsWith(".ham")) {
				name = name.substring(0, name.length() - 4);
			}
			name += ".ter";
			simModel = Workbench.getWorkbench().getSimulation()
					.getSimulationModel();
			HamsterFile tFile = HamsterFile.getHamsterFile(name);
			tFile.save(simModel.getTerrain().toString());
		}

		return true;
	}

	public boolean saveAs() {
		return this.saveAs(null);
	}
	
	public boolean saveAs(String proposedFileName) {
		if (proposedFileName != null) {
			// set file even if it does not exist in order to prevent
			// that the name of the file saved last is proposed. Otherwise 
			// the risk is high to override the content of the other file!
			this.fileChooser.setSelectedFile(new File(proposedFileName));
		} 
		int action = this.fileChooser.showSaveDialog(this.tabbedTextArea);
		if (action == JFileChooser.CANCEL_OPTION) {
			return false;
		}
		File f = this.fileChooser.getSelectedFile();
		HamsterFileFilter filter = (HamsterFileFilter) this.fileChooser
				.getFileFilter();
		String newName = f.getName();
		if (newName.endsWith(filter.getExtension())) {
			newName = newName.substring(0, newName.length()
					- filter.getExtension().length());
		}
		if (!Utils.isValidName(newName)) {
			Utils.message(this.tabbedTextArea, "editor.dialog.javanames");
			return false;
		}
		if (!f.getName().endsWith(filter.getExtension())) {
			f = new File(f.getAbsolutePath() + filter.getExtension());
		}

		HamsterFile file = this.tabbedTextArea.getActiveFile();
		HamsterFile newFile = HamsterFile.createHamsterFile(
				f.getAbsolutePath(), file.getType());

		if (file.getClass() != newFile.getClass()) {
			Utils.message(this.tabbedTextArea, "editor.dialog.saveimpossible");
			return false;
		}

		newFile.setType(file.getType());

		if (file.getType() == HamsterFile.SCRATCHPROGRAM) {
			ScratchHamsterFile oldS = (ScratchHamsterFile) file;
			ScratchHamsterFile newS = (ScratchHamsterFile) newFile;
			newS.setProgram(new ScratchProgram(oldS.getProgram()));
		} else if (file.getType() == HamsterFile.FSM) {
			FsmHamsterFile oldS = (FsmHamsterFile) file;
			FsmHamsterFile newS = (FsmHamsterFile) newFile;
			newS.setProgram(new FsmProgram(oldS.getProgram(), newS));
		} else if (file.getType() == HamsterFile.FLOWCHART) {
			FlowchartHamsterFile oldS = (FlowchartHamsterFile) file;
			FlowchartHamsterFile newS = (FlowchartHamsterFile) newFile;
			newS.setProgram(new FlowchartProgram(oldS.getProgram(), newS));
		}

		newFile.save(this.tabbedTextArea.getActiveTextArea().getText());

		this.tabbedTextArea.closeFile(file);
		this.tabbedTextArea.closeFile(newFile);
		this.tabbedTextArea.openFile(newFile);
		return true;
	}

	public boolean saveAsPlus() {
		SimulationModel simModel = Workbench.getWorkbench().getSimulation()
				.getSimulationModel();
		Terrain ter = simModel.getTerrain();
		boolean res = this.saveAs();
		simModel.setTerrain(ter);
		if (!res) {
			return false;
		}

		if (this.tabbedTextArea.getActiveFile().exists()) {
			HamsterFile file = this.tabbedTextArea.getActiveFile();

			String name = file.getAbsolute();
			if (name.endsWith(".ham")) {
				name = name.substring(0, name.length() - 4);
			}
			name += ".ter";
			simModel = Workbench.getWorkbench().getSimulation()
					.getSimulationModel();
			HamsterFile tFile = HamsterFile.getHamsterFile(name);
			tFile.save(simModel.getTerrain().toString());
		}

		return true;
	}

	protected void open() {
		int action = this.fileChooser.showOpenDialog(this.tabbedTextArea);
		if (action == JFileChooser.CANCEL_OPTION) {
			return;
		}
		File f = this.fileChooser.getSelectedFile();
		open(f);
		/*
		if (f.exists()) {
			HamsterFile hamFile = HamsterFile.getHamsterFile(f
					.getAbsolutePath());
			this.tabbedTextArea.openFile(hamFile);
			if (!this.tabbedTextArea.isLocked()) {
				this.checkOpenTerrain(hamFile);
			}
		}
		*/
	}

	// added by C. Noeske
	public void open(File f) {
		if (f.exists()) {
			HamsterFile hamFile = HamsterFile.getHamsterFile(f.getAbsolutePath());
			this.tabbedTextArea.openFile(hamFile);
			if (!this.tabbedTextArea.isLocked()) {
				this.checkOpenTerrain(hamFile);
			}
		}
	}
	// -- end of addition
	
	
	
	protected void newFile() {
		char type = this.newHamsterDialog.askForType();
		if (type == (char) -1) {
			return;
		} else if (type == HamsterFile.SCHEMEKONSOLE) {
			// SchemeKonsole �ffnen
			SchemeHamster.setWorkbench(this.workbench);
			SchemeKonsole.getSchemeKonsole().setVisible(true);
		} else if (type == HamsterFile.SCRATCHPROGRAM) {
			ScratchHamsterFile newFile = new ScratchHamsterFile(type);
			this.tabbedTextArea.openFile(newFile);
			// newFile.setModified(true);
			newFile.setModified(false); // dibo 290710
		} else if (type == HamsterFile.FSM) {
			FsmHamsterFile newFile = new FsmHamsterFile(type);
			this.tabbedTextArea.openFile(newFile);
			// newFile.setModified(true);
			newFile.setModified(false); // dibo 290710
		} else if (type == HamsterFile.FLOWCHART) {
			FlowchartHamsterFile newFile = new FlowchartHamsterFile(type);
			this.tabbedTextArea.openFile(newFile);
			// newFile.setModified(true);
			newFile.setModified(false); // dibo 290710
		} else {
			HamsterFile newFile = new HamsterFile(type);
			this.tabbedTextArea.openFile(newFile);
			// newFile.setModified(true);
			newFile.setModified(false); // dibo 290710
		}
	}

	protected void deleteFile() {
		HamsterFile file = this.fileTree.getFilePopupMenu().getFile();
		if (!file.isEmpty()) {
			if (Utils.ask(this.tabbedTextArea, "editor.dialog.deletedir",
					file.getName())) {
				this.tabbedTextArea.closeFile(file);
				file.delete();
			}
		} else {
			if (Utils.ask(this.tabbedTextArea, "editor.dialog.reallydelete",
					file.getName())) {
				this.tabbedTextArea.closeFile(file);
				file.delete();
			}
		}
	}

	protected void renameFile() {
		HamsterFile oldFile = this.fileTree.getFilePopupMenu().getFile();

		// Namen einlesen
		String name = Utils.input(this.tabbedTextArea, "editor.dialog.rename");
		if (name == null || name.equals(oldFile.getName())) {
			return;
		}
		if (!oldFile.isDirectory()) {
			if (oldFile.isTerrain()) { // dibo 260110
				if (!name.endsWith(".ter")) {
					name += ".ter";
				}
			} else if (Utils.isValidName(name)) {
				name += ".ham";
			} else {
				Utils.message(this.tabbedTextArea, "editor.dialog.javanames");
				return;
			}
		}

		File newFile = new File(oldFile.getFile().getParentFile(), name);
		if (!oldFile.getFile().getAbsolutePath()
				.equals(newFile.getAbsolutePath())) { // nicht identisch
			this.moveFile(oldFile, newFile);
		}
	}

	protected boolean checkOverwrite(File newFile) {
		if (newFile.exists()) {
			if (newFile.isDirectory()) {
				Utils.message(this.tabbedTextArea, "editor.dialog.exists",
						newFile.getName());
				return false;
			} else if (!Utils.ask(this.tabbedTextArea,
					"editor.dialog.overwrite",
					HamsterFile.getHamsterFile(newFile).getName())) {
				return false;
			} else {
				HamsterFile f = HamsterFile.getHamsterFile(newFile);
				this.tabbedTextArea.closeFile(f);
				f.delete();
			}
		}
		return true;
	}

	public void copyFile(HamsterFile copiedFile, HamsterFile dir) {
		File newFile = new File(dir.getFile(), copiedFile.getFile().getName());
		// if (HamsterFile.getHamsterFile(newFile) != copiedFile) {
		if (!newFile.getAbsolutePath().equals(
				copiedFile.getFile().getAbsolutePath())) {
			if (this.tabbedTextArea.ensureSaved2(copiedFile)) {
				if (this.checkOverwrite(newFile)) {
					copiedFile.copy(dir.getFile());
				}
			}
		}
	}

	public void moveFile(HamsterFile movedFile, File newFile) {
		if (this.tabbedTextArea.ensureSaved2(movedFile)) {
			if (this.checkOverwrite(newFile)) {
				this.tabbedTextArea.closeFile(movedFile);
				movedFile.move(newFile);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == EditorController.ACTION_SAVE) {
			this.save();
		} else if (e.getActionCommand() == EditorController.ACTION_NEW) {
			this.newFile();
		} else if (e.getActionCommand() == EditorController.ACTION_SAVE_AS) {
			this.saveAs();
		} else if (e.getActionCommand() == EditorController.ACTION_SAVE_PLUS) {
			this.savePlus();
		} else if (e.getActionCommand() == EditorController.ACTION_SAVE_AS_PLUS) {
			this.saveAsPlus();
		} else if (e.getActionCommand() == EditorController.ACTION_GENERATE) {
			generateFile();
		} else if (e.getActionCommand() == EditorController.ACTION_OPEN) {
			this.open();
		} else if (e.getActionCommand() == EditorController.ACTION_CLOSE) {
			if (this.tabbedTextArea.ensureSaved2(this.tabbedTextArea
					.getActiveFile())) {
				this.tabbedTextArea.closeFile(this.tabbedTextArea
						.getActiveFile());
			}
		} else if (e.getActionCommand() == EditorController.ACTION_PRINT) {
			if (this.tabbedTextArea.getActiveTextArea().isScratch()) {
				Utils.print(this.tabbedTextArea.getActiveTextArea()
						.getScratchPanel());
			} else if (this.tabbedTextArea.getActiveTextArea().isFSM()) {
				Utils.print(this.tabbedTextArea.getActiveTextArea()
						.getFSMPanel());
			} else if (this.tabbedTextArea.getActiveTextArea().isFlowchart()) {
				Utils.print(this.tabbedTextArea.getActiveTextArea()
						.getFlowchartPanel());
			} else {
				Utils.print(this.tabbedTextArea.getActiveTextArea().getText());
			}
		} else if (e.getActionCommand() == EditorController.ACTION_UNDO) {
			TextArea textArea = this.tabbedTextArea.getActiveTextArea();
			if (textArea != null) {
				if (textArea.getUndoManager().canUndo()) {
					try {
						textArea.getUndoManager().undo();
					} catch (Throwable th) { // dibo 17.11.2010
					}
					this.editorTools.updateButtons(textArea);
				}
			}
		} else if (e.getActionCommand() == EditorController.ACTION_REDO) {
			TextArea textArea = this.tabbedTextArea.getActiveTextArea();
			if (textArea != null) {
				if (textArea.getUndoManager().canRedo()) {
					try {
						textArea.getUndoManager().redo();
					} catch (Throwable th) { // dibo 17.11.2010
					}
					this.editorTools.updateButtons(textArea);
				}
			}
		} else if (e.getActionCommand() == EditorController.FILE_OPEN) {
			this.tabbedTextArea.openFile(this.fileTree.getFilePopupMenu()
					.getFile());
			if (!this.tabbedTextArea.isLocked()) {
				this.checkOpenTerrain(this.fileTree.getFilePopupMenu()
						.getFile());
			}
		} else if (e.getActionCommand() == EditorController.FILE_DELETE) {
			this.deleteFile();
		} else if (e.getActionCommand() == EditorController.FILE_COPY) {
			this.copiedFile = this.fileTree.getFilePopupMenu().getFile();
		} else if (e.getActionCommand() == EditorController.FILE_PASTE) {
			if (this.copiedFile != null && this.copiedFile.exists()) {
				this.copyFile(this.copiedFile, this.fileTree.getFilePopupMenu()
						.getFile());
			}
		} else if (e.getActionCommand() == EditorController.FILE_NEWFOLDER) {
			String name = Utils.input(this.tabbedTextArea,
					"editor.dialog.newfolder");
			if (name == null) {
				return;
			}
			File f = new File(this.fileTree.getFilePopupMenu().getFile()
					.getFile(), name);
			f.mkdir();
		} else if (e.getActionCommand() == EditorController.FILE_RENAME) {
			this.renameFile();
		} else if (e.getActionCommand() == EditorController.FILE_GENERATE) {
			generateFile();
		}
		this.refreshFiles();
	}

	private void generateFile() {
		String code = null;
		if (this.tabbedTextArea.getActiveTextArea().isScratch()) {
			ScratchHamsterFile file = (ScratchHamsterFile) this.tabbedTextArea
					.getActiveFile();
			code = file.getProgram().getSourceCode();
		} else if (this.tabbedTextArea.getActiveTextArea().isFSM()) {
			FsmHamsterFile file = (FsmHamsterFile) this.tabbedTextArea
					.getActiveFile();
			code = file.getProgram().getSourceCode();
		} else if (this.tabbedTextArea.getActiveTextArea().isFlowchart()) {
			FlowchartHamsterFile file = (FlowchartHamsterFile) this.tabbedTextArea
					.getActiveFile();
			code = file.getProgram().getSourceCode();
		}
		HamsterFile newFile = new HamsterFile(HamsterFile.IMPERATIVE);
		this.tabbedTextArea.openFile(newFile);
		this.tabbedTextArea.getActiveTextArea().setText(code);
		this.tabbedTextArea.getActiveTextArea().setCaretPosition(0);
		newFile.setModified(true);
	}

	private void checkOpenTerrain(HamsterFile hamFile) {
		if (hamFile == null) {
			return;
		}
		if (hamFile.isProgram()) {
			String name = hamFile.getAbsolute();
			if (name.endsWith(".ham")) {
				name = name.substring(0, name.length() - 4);
			}
			name += ".ter";
			if (new File(name).exists()) {
				HamsterFile terFile = HamsterFile.getHamsterFile(name);
				SimulationModel simModel = Workbench.getWorkbench()
						.getSimulation().getSimulationModel();
				simModel.setTerrain(new Terrain(terFile.load()));

				// Prolog
				if (Utils.PROLOG) {
					PrologController.get().updateTerrainObject(
							TerObject.TERRITORIUM);
				}
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() == null) {
			// an item was deselected
			return;
		}
		FileTreeNode f = (FileTreeNode) e.getPath().getLastPathComponent();
		HamsterFile h = f.getHamsterFile();
		if (!h.isDirectory()) {
			this.tabbedTextArea.openFile(h);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.workbench.setActiveFile(this.tabbedTextArea.getActiveFile());
		this.editorTools.setActiveFile(this.tabbedTextArea.getActiveFile());
		this.editorTools.updateButtons(this.tabbedTextArea.getActiveTextArea());
		if (!this.tabbedTextArea.isLocked()) { // dibo 290710
			this.checkOpenTerrain(this.tabbedTextArea.getActiveFile());
		}
		if (this.tabbedTextArea.getActiveTextArea() != null) {
			this.tabbedTextArea.getActiveTextArea().requestFocus();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.editorTools.updateButtons(this.tabbedTextArea.getActiveTextArea());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.editorTools.updateButtons(this.tabbedTextArea.getActiveTextArea());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.editorTools.updateButtons(this.tabbedTextArea.getActiveTextArea());
	}
}