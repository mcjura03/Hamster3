package de.hamster.simulation.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JToggleButton;

import de.hamster.model.HamsterFile;
import de.hamster.prolog.controller.PrologController;
import de.hamster.prolog.model.PrologHamster.TerObject;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.model.Terrain;
import de.hamster.simulation.view.DialogTerminal;
import de.hamster.simulation.view.LogPanel;
import de.hamster.simulation.view.SimulationPanel;
import de.hamster.simulation.view.SimulationTools;
import de.hamster.simulation.view.SizeDialog;
import de.hamster.simulation.view.multimedia.opengl.OpenGLController;
import de.hamster.workbench.HamsterFileFilter;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.3 $
 */
public class SimulationController implements ActionListener {
	public static final String NEW = "new";
	public static final String OPEN = "open";
	public static final String SAVE_AS = "saveas";
	public static final String TURN = "turn hamster";
	public static final String HAMSTER_CORN = "hamster corn";
	public static final String RESET = "reset";
	public static final String ZOOM_IN_3D = "zoom in 3d";
	public static final String ZOOM_OUT_3D = "zoom out 3d";
	public static final String TURN_LEFT_3D = "turn left 3d";
	public static final String TURN_RIGHT_3D = "turn right 3d";
	public static final String LOOK_UP_3D = "look up 3d";
	public static final String LOOK_DOWN_3D = "look down 3d";
	public static final String TOGGLE_GRID_3D = "toggle grid 3d";
	public static final String TOGGLE_MUSIC_3D = "toggle music 3d";
	public static final String TOGGLE_SOUND_3D = "toggle sound 3d";
	public static final String PERSPECTIVE_3D = "perspective 3d";

	Workbench workbench;

	SimulationModel simulationModel;
	DialogTerminal dialogTerminal;

	private SimulationTools simulationTools;
	private SimulationPanel simulationPanel;

	private LogPanel logPanel;
	private SizeDialog sizeDialog;

	private JFileChooser fileChooser;

	public SimulationController(SimulationModel simulationModel,
			Workbench workbench) {
		this.workbench = workbench;
		this.simulationModel = simulationModel;
		simulationTools = new SimulationTools(simulationModel, this);
		simulationPanel = new SimulationPanel(simulationModel, simulationTools);
		simulationTools.setSimulationPanel(simulationPanel);

		logPanel = new LogPanel();
		simulationModel.addLogSink(logPanel);

		dialogTerminal = DialogTerminal.getInstance();
		simulationModel.setTerminal(dialogTerminal);

		fileChooser = Utils.getFileChooser();
		fileChooser.setFileFilter(HamsterFileFilter.TER_FILTER);

		sizeDialog = new SizeDialog(simulationPanel);
	}

	public DialogTerminal getDialogTerminal() {
		return dialogTerminal;
	}

	public void setWall(int x1, int y1, int x2, int y2) {
		for (int i = x1; i <= x2; i++) {
			if (i < 0 || i >= simulationModel.getTerrain().getWidth())
				continue;
			for (int j = y1; j <= y2; j++) {
				if (j < 0 || j >= simulationModel.getTerrain().getHeight())
					continue;
				if (!simulationModel.containsHamster(i, j)) {
					simulationModel.getTerrain().setWall(i, j, true);
					// Entferne Körner an dieser Position..
					simulationModel.getTerrain().setCornCount(i, j, 0);
				}
			}
		}
		simulationModel.setChanged();
		simulationModel.notifyObservers(SimulationModel.TERRAIN);

		// Prolog
		if (Utils.PROLOG) {
			PrologController.get().updateTerrainObject(TerObject.MAUER);
		}
	}

	public void setEmpty(int x1, int y1, int x2, int y2) {
		for (int i = x1; i <= x2; i++) {
			if (i < 0 || i >= simulationModel.getTerrain().getWidth())
				continue;
			for (int j = y1; j <= y2; j++) {
				if (j < 0 || j >= simulationModel.getTerrain().getHeight())
					continue;
				simulationModel.getTerrain().setWall(i, j, false);
				simulationModel.getTerrain().setCornCount(i, j, 0);
			}
		}
		simulationModel.setChanged();
		simulationModel.notifyObservers(SimulationModel.TERRAIN);

		// Prolog
		if (Utils.PROLOG) {
			PrologController.get().updateTerrainObject(TerObject.TERRITORIUM);
		}
	}

	public void setCorn(int x1, int y1, int x2, int y2, int count) {
		for (int i = x1; i <= x2; i++) {
			if (i < 0 || i >= simulationModel.getTerrain().getWidth())
				continue;
			for (int j = y1; j <= y2; j++) {
				if (j < 0 || j >= simulationModel.getTerrain().getHeight())
					continue;
				if (!simulationModel.getTerrain().getWall(i, j))
					simulationModel.getTerrain().setCornCount(i, j, count);
			}
		}
		simulationModel.setChanged();
		simulationModel.notifyObservers(SimulationModel.TERRAIN);

		// Prolog
		if (Utils.PROLOG) {
			PrologController.get().updateTerrainObject(TerObject.KORN);
		}
	}

	public boolean containsValidCornCell(int x1, int y1, int x2, int y2) {
		for (int i = x1; i <= x2; i++) {
			if (i < 0 || i >= simulationModel.getTerrain().getWidth())
				continue;
			for (int j = y1; j <= y2; j++) {
				if (j < 0 || j >= simulationModel.getTerrain().getHeight())
					continue;
				if (!simulationModel.getTerrain().getWall(i, j))
					return true;
			}
		}
		return false;
	}

	public void setHamsterPos(int x, int y) {
		if (!simulationModel.getTerrain().getWall(x, y)) {
			simulationModel.getTerrain().getDefaultHamster().setXY(x, y);
		}
		simulationModel.setChanged();
		simulationModel.notifyObservers(SimulationModel.TERRAIN);

		// Prolog
		if (Utils.PROLOG) {
			PrologController.get().updateTerrainObject(TerObject.HAMSTER);
		}
	}

	public void turnHamster() {
		simulationModel.turnLeft(-1);
		simulationModel.setChanged();
		simulationModel.notifyObservers(SimulationModel.TERRAIN);

		// Prolog
		if (Utils.PROLOG) {
			PrologController.get().updateTerrainObject(TerObject.HAMSTER);
		}
	}

	public SimulationPanel getSimulationPanel() {
		return simulationPanel;
	}

	public LogPanel getLogPanel() {
		return logPanel;
	}

	public Workbench getWorkbench() {
		return workbench;
	}

	// dibo 11.01.2006
	public SimulationModel getSimulationModel() {
		return simulationModel;
	}

	public SimulationTools getSimulationTools() {
		return simulationTools;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == OPEN) {
			int val = fileChooser.showOpenDialog(simulationPanel);
			if (val == JFileChooser.APPROVE_OPTION) {
				HamsterFile file = HamsterFile.getHamsterFile(fileChooser
						.getSelectedFile().getAbsolutePath());
				simulationModel.setTerrain(new Terrain(file.load()));

				// Prolog
				if (Utils.PROLOG) {
					PrologController.get().updateTerrainObject(
							TerObject.TERRITORIUM);
				}
			}
		} else if (e.getActionCommand() == SAVE_AS) {
			int val = fileChooser.showSaveDialog(simulationPanel);
			if (val == JFileChooser.APPROVE_OPTION) {
				String name = fileChooser.getSelectedFile().getAbsolutePath();
				if (!name.endsWith(".ter"))
					name += ".ter";
				HamsterFile file = HamsterFile.getHamsterFile(name);
				file.save(simulationModel.getTerrain().toString());
			}
		} else if (e.getActionCommand() == NEW) {
			Dimension size = sizeDialog.requestSize();
			if (size != null) {
				simulationModel.setTerrain(new Terrain((int) size.getWidth(),
						(int) size.getHeight()));

				// Prolog
				if (Utils.PROLOG) {
					PrologController.get().updateTerrainObject(
							TerObject.TERRITORIUM);
				}
			}
		} else if (e.getActionCommand() == TURN) {
			simulationModel.turnLeft(-1);
			// Prolog
			if (Utils.PROLOG) {
				PrologController.get().updateTerrainObject(TerObject.HAMSTER);
			}
		} else if (e.getActionCommand() == HAMSTER_CORN) {
			int count = simulationModel.getTerrain().getDefaultHamster()
					.getMouth();
			String countString = Utils.input(simulationPanel,
					"simulation.dialog.cornnumber", count + "");
			if (countString != null) {
				count = Math.max(0, Integer.parseInt(countString));
				simulationModel.getTerrain().getDefaultHamster()
						.setMouth(count);
				simulationModel.setChanged();
				simulationModel.notifyObservers();

				// Prolog
				if (Utils.PROLOG) {
					PrologController.get().updateTerrainObject(
							TerObject.HAMSTER);
				}
			}
		} else if (e.getActionCommand() == RESET) {
			simulationModel.reset();
			// Prolog
			if (Utils.PROLOG) {
				PrologController.get().updateTerrainObject(
						TerObject.TERRITORIUM);
			}
		}
		// chris
		else if (e.getActionCommand() == ZOOM_IN_3D) {
			OpenGLController.getInstance().zoomIn();
		} else if (e.getActionCommand() == ZOOM_OUT_3D) {

			OpenGLController.getInstance().zoomOut();
		} else if (e.getActionCommand() == TURN_LEFT_3D) {
			OpenGLController.getInstance().rotateLeft();
		} else if (e.getActionCommand() == TURN_RIGHT_3D) {
			OpenGLController.getInstance().rotateRight();
		} else if (e.getActionCommand() == LOOK_UP_3D) {
			OpenGLController.getInstance().lookUp();
		} else if (e.getActionCommand() == LOOK_DOWN_3D) {
			OpenGLController.getInstance().lookDown();
		} else if (e.getActionCommand() == TOGGLE_GRID_3D) {
			String s = "false";
			if (((JToggleButton) e.getSource()).isSelected())
				s = "true";
			workbench.setProperty("grid", s);
			OpenGLController.getInstance().toggleGrid();
		} else if (e.getActionCommand() == TOGGLE_MUSIC_3D) {
			String s = "false";
			if (((JToggleButton) e.getSource()).isSelected())
				s = "true";
			workbench.setProperty("music", s);
			OpenGLController.getInstance().toggleMusic();
		} else if (e.getActionCommand() == TOGGLE_SOUND_3D) {
			String s = "false";
			if (((JToggleButton) e.getSource()).isSelected())
				s = "true";
			workbench.setProperty("sound", s);
			OpenGLController.getInstance().toggleSound();
		} else if (e.getActionCommand() == PERSPECTIVE_3D) {
			OpenGLController.getInstance().togglePerspective();
		}
	}
}