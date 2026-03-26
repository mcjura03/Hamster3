package de.hamster.debugger.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.hamster.debugger.controller.DebuggerController;
import de.hamster.debugger.model.DebuggerModel;
import de.hamster.model.HamsterFile;
import de.hamster.workbench.ForwardAction;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper, jrahn $
 * @version $Revision: 1.2 $
 */
public class DebuggerTools implements PropertyChangeListener, ChangeListener {
	private final DebuggerModel model;

	private final StartAction startAction = new StartAction();

	public class StartAction extends ForwardAction {
		public StartAction() {
			super("debugger.start", DebuggerController.ACTION_START);
		}
	}

	private final StepAction stepAction = new StepAction();

	public class StepAction extends ForwardAction {
		public StepAction() {
			super("debugger.stepinto", DebuggerController.ACTION_STEP);
		}
	}

	private final StepOverAction stepOverAction = new StepOverAction();

	public class StepOverAction extends ForwardAction {
		public StepOverAction() {
			super("debugger.stepover", DebuggerController.ACTION_STEPOVER);
		}
	}

	private final EnableAction enableAction = new EnableAction();

	public class EnableAction extends ForwardAction {
		public EnableAction() {
			super("debugger.enable", DebuggerController.ACTION_ENABLE);
		}
	}

	private final StopAction stopAction = new StopAction();

	public class StopAction extends ForwardAction {
		public StopAction() {
			super("debugger.stop", DebuggerController.ACTION_STOP);
		}
	}

	private final PauseAction pauseAction = new PauseAction();

	public class PauseAction extends ForwardAction {
		public PauseAction() {
			super("debugger.pause", DebuggerController.ACTION_PAUSE);
		}
	}

	private JToggleButton enableButton;
	private JCheckBoxMenuItem enableItem;

	private JSlider delay, delaySim, delay3D;

	protected HamsterFile activeFile;

	public DebuggerTools(DebuggerModel model, final DebuggerController controller) {
		this.model = model;


		model.addPropertyChangeListener(this);

		JMenu menu = controller.getWorkbench().getView().findMenu("editor", "debugger");
		menu.add(new JMenuItem(startAction));
		menu.add(new JMenuItem(pauseAction));
		menu.add(new JMenuItem(stopAction));
		menu.addSeparator();
		enableItem = new JCheckBoxMenuItem(enableAction);
		menu.add(enableItem);
		menu.add(new JMenuItem(stepAction));
		menu.add(new JMenuItem(stepOverAction));

		JToolBar toolBar = controller.getWorkbench().getView().findToolBar("editor");
		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));
		toolBar.add(Utils.createButton(startAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(pauseAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(stopAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		enableButton = Utils.createToggleButton(enableAction);
		toolBar.add(enableButton);
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(stepAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(stepOverAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));

		delay = new JSlider(0, 1000, 500);
		delay.setToolTipText(Utils.getResource("debugger.delay.tooltip"));
		delay.addChangeListener(controller);
		delay.setInverted(true);
		delay.setMajorTickSpacing(100);
		delay.setPaintTicks(true);
		delay.addChangeListener(this);
		delay.setName("delay");
		delay.setBackground(toolBar.getBackground());
		toolBar.add(delay);

		JToolBar simulationBar = controller.getWorkbench().getView().findToolBar("simulation");

		simulationBar.add(Box.createRigidArea(new Dimension(11, 11)));
		JButton button = Utils.createButton(startAction);

		KeyStroke keyM = KeyStroke.getKeyStroke(Utils.getResource("debugger.start.keystroke"));
		Action actionM = new AbstractAction("start2") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent event = new ActionEvent(e.getSource(), e.getID(), DebuggerController.ACTION_START);
				controller.actionPerformed(event);
			}
		};
		button.getActionMap().put("start2", actionM);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyM, "start2");

		simulationBar.add(button);

		simulationBar.add(Box.createRigidArea(new Dimension(2, 2)));

		button = Utils.createButton(pauseAction);

		keyM = KeyStroke.getKeyStroke(Utils.getResource("debugger.pause.keystroke"));
		actionM = new AbstractAction("start2") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent event = new ActionEvent(e.getSource(), e.getID(), DebuggerController.ACTION_PAUSE);
				controller.actionPerformed(event);
			}
		};
		button.getActionMap().put("start2", actionM);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyM, "start2");
		simulationBar.add(button);

		simulationBar.add(Box.createRigidArea(new Dimension(2, 2)));

		button = Utils.createButton(stopAction);

		keyM = KeyStroke.getKeyStroke(Utils.getResource("debugger.stop.keystroke"));
		actionM = new AbstractAction("start2") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent event = new ActionEvent(e.getSource(), e.getID(), DebuggerController.ACTION_STOP);
				controller.actionPerformed(event);
			}
		};
		button.getActionMap().put("start2", actionM);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyM, "start2");
		simulationBar.add(button);

		delaySim = new JSlider(0, 1000, 500);
		delaySim.setToolTipText(Utils.getResource("debugger.delay.tooltip"));
		delaySim.addChangeListener(controller);
		delaySim.setInverted(true);
		delaySim.setMajorTickSpacing(100);
		delaySim.setPaintTicks(true);
		delaySim.setName("sim");
		delaySim.setBackground(simulationBar.getBackground());
		delaySim.addChangeListener(this);
		simulationBar.add(Box.createRigidArea(new Dimension(2, 2)));
		simulationBar.add(delaySim);

		startAction.addActionListener(controller);
		stepAction.addActionListener(controller);
		stepOverAction.addActionListener(controller);
		stopAction.addActionListener(controller);
		enableAction.addActionListener(controller);
		pauseAction.addActionListener(controller);

		// chris
		JToolBar simulationBar3D = controller.getWorkbench().getView().findToolBar("3dsimulation");
		simulationBar3D.add(Box.createRigidArea(new Dimension(11, 11)));
		simulationBar3D.add(Utils.createButton(startAction));
		simulationBar3D.add(Box.createRigidArea(new Dimension(2, 2)));
		simulationBar3D.add(Utils.createButton(pauseAction));
		simulationBar3D.add(Box.createRigidArea(new Dimension(2, 2)));
		simulationBar3D.add(Utils.createButton(stopAction));

		simulationBar3D.add(Box.createRigidArea(new Dimension(2, 2)));
		delay3D = new JSlider(0, 1000, 500);
		delay3D.setToolTipText(Utils.getResource("debugger.delay.tooltip"));
		delay3D.addChangeListener(controller);
		delay3D.setInverted(true);
		delay3D.setMajorTickSpacing(100);
		delay3D.setPaintTicks(true);
		delay3D.setName("3d");
		delay3D.addChangeListener(this);
		simulationBar3D.add(Box.createRigidArea(new Dimension(2, 2)));
		simulationBar3D.add(delay3D);

		updateButtonStates();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Observable hat "arg" geschickt; hier ist es evt.getPropertyName()
		String prop = evt.getPropertyName();

		// Viele Änderungen sollen einfach die Button-States neu berechnen
		updateButtonStates();

		// Enable-State gezielt synchronisieren
		if (DebuggerModel.ARG_ENABLE.equals(prop)) {
			enableButton.setSelected(model.isEnabled());
			enableItem.setSelected(model.isEnabled());
		}
	}

	public void updateButtonStates() {

		// martin
		// Falls die aktive Datei ein Scheme-Programm ist, muessen
		// ein paar Buttons ausgeblendet werden.
		// Martin + Python + Ruby + JavaScript
		if (activeFile != null && (activeFile.getType() == HamsterFile.SCHEMEPROGRAM
				|| activeFile.getType() == HamsterFile.PYTHONPROGRAM || activeFile.getType() == HamsterFile.RUBYPROGRAM
				|| activeFile.getType() == HamsterFile.JAVASCRIPTPROGRAM)) {
			enableAction.setEnabled(false);
			stopAction.setEnabled(model.getState() != DebuggerModel.NOT_RUNNING);
			pauseAction.setEnabled(false);
			stepAction.setEnabled(false);
			stepOverAction.setEnabled(false);
			startAction.setEnabled(activeFile != null
					&& (model.getState() != DebuggerModel.RUNNING && !(activeFile.getType() == HamsterFile.HAMSTERCLASS
							&& model.getState() == DebuggerModel.NOT_RUNNING)));

			// Reset-Button im Sim-Fenster.
			de.hamster.workbench.Workbench.getWorkbench().getSimulation().getSimulationTools().getResetAction()
					.setEnabled(de.hamster.workbench.Workbench.getWorkbench().getSimulation()
							.getSimulationModel().savedTerrain != null);

			// Prolog
		} else if (activeFile != null && activeFile.getType() == HamsterFile.PROLOGPROGRAM) {
			// Spezielle Debugger-Buttons deaktiviert..
			enableAction.setEnabled(false);
			stepAction.setEnabled(false);
			stepOverAction.setEnabled(false);

			// Stop-Button
			stopAction.setEnabled(true);

			pauseAction.setEnabled(false);

			startAction.setEnabled(activeFile != null
					&& (model.getState() != DebuggerModel.RUNNING && !(activeFile.getType() == HamsterFile.HAMSTERCLASS
							&& model.getState() == DebuggerModel.NOT_RUNNING)));

			// Reset-Button im Sim-Fenster.
			de.hamster.workbench.Workbench.getWorkbench().getSimulation().getSimulationTools().getResetAction()
					.setEnabled(de.hamster.workbench.Workbench.getWorkbench().getSimulation()
							.getSimulationModel().savedTerrain != null
							&& (model.getState() != DebuggerModel.RUNNING && model.getState() != DebuggerModel.PAUSED));
		} else {
			startAction.setEnabled(activeFile != null
					&& (model.getState() != DebuggerModel.RUNNING && !(activeFile.getType() == HamsterFile.HAMSTERCLASS
							&& model.getState() == DebuggerModel.NOT_RUNNING)));
			pauseAction.setEnabled(model.getState() == DebuggerModel.RUNNING);
			if (activeFile != null && (activeFile.getType() == HamsterFile.SCRATCHPROGRAM
					|| activeFile.getType() == HamsterFile.FSM || activeFile.getType() == HamsterFile.FLOWCHART)) {
				if (model.getState() != DebuggerModel.RUNNING) { // dibo 300710
					stepAction.setEnabled(true);
				} else {
					stepAction.setEnabled(false);
				}
				stepOverAction.setEnabled(false); // dibo 290710
				enableAction.setEnabled(false);
			} else {
				stepAction.setEnabled(model.isEnabled() && (model.getState() == DebuggerModel.PAUSED
						|| (model.getState() == DebuggerModel.NOT_RUNNING) && startAction.isEnabled()));
				stepOverAction.setEnabled(model.isEnabled() && model.getState() == DebuggerModel.PAUSED);
				enableAction.setEnabled(!Utils.runlocally);
			}
			stopAction.setEnabled(model.getState() != DebuggerModel.NOT_RUNNING);

			if (de.hamster.workbench.Workbench.workbench != null) {
				de.hamster.workbench.Workbench.getWorkbench().getSimulation().getSimulationTools().getResetAction()
						.setEnabled(de.hamster.workbench.Workbench.getWorkbench().getSimulation()
								.getSimulationModel().savedTerrain != null
								&& activeFile != null
								&& (model.getState() != DebuggerModel.RUNNING
										&& model.getState() != DebuggerModel.PAUSED
										&& !(activeFile.getType() == HamsterFile.HAMSTERCLASS
												&& model.getState() == DebuggerModel.NOT_RUNNING)));
			}
		}

	}

	public void setActiveFile(HamsterFile activeFile) {
		this.activeFile = activeFile;
		updateButtonStates();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if (slider == delay) {
			if (delaySim.getValue() != delay.getValue()) {
				delaySim.setValue(delay.getValue());
			}
			if (delay3D.getValue() != delay.getValue()) { // chris
				delay3D.setValue(delay.getValue());
			}
		} else if (slider == delaySim) {
			if (delay.getValue() != delaySim.getValue()) {
				delay.setValue(delaySim.getValue());
			}
			if (delay3D.getValue() != delaySim.getValue()) { // chris
				delay3D.setValue(delaySim.getValue());
			}
		} else if (slider == delay3D) { // chris
			if (delay.getValue() != delay3D.getValue()) {
				delay.setValue(delay3D.getValue());
			}
			if (delaySim.getValue() != delay3D.getValue()) {
				delaySim.setValue(delay3D.getValue());
			}
		}
	}
}