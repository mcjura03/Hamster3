package de.hamster.debugger.view;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import com.sun.jdi.StackFrame;

import de.hamster.debugger.controller.DebuggerController;
import de.hamster.debugger.model.DebuggerModel;

import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class StackFrameTable extends JTable implements Observer {
	private StackFrameTableModel tableModel;

	private DebuggerModel model;
	private DebuggerController controller;

	public StackFrameTable(DebuggerModel model, DebuggerController controller) {
		super(new StackFrameTableModel());
		this.model = model;
		this.controller = controller;
		model.addObserver(this);

		tableModel = (StackFrameTableModel) getModel();

		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		setShowGrid(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		getColumnModel().getColumn(0).setMaxWidth(1);
		getColumnModel().getColumn(0).setHeaderValue("");

		getColumnModel().getColumn(1).setHeaderValue(Utils.getResource("debugger.programm"));
		getColumnModel().getColumn(2).setHeaderValue(Utils.getResource("debugger.funktion"));

		getColumnModel().getColumn(3).setHeaderValue(Utils.getResource("debugger.zeile"));
	}

	public void update(Observable o, Object arg) {
		if (arg == DebuggerModel.ARG_STATE) {
			if (model.getState() == DebuggerModel.NOT_RUNNING) {
				tableModel.setStackFrames(new ArrayList());
			} else if ((model.getState() == DebuggerModel.PAUSED || model
					.isSuspended())
					&& model.isEnabled() && model.getStackFrames() != null) {
				tableModel.setStackFrames(model.getStackFrames());
				addRowSelectionInterval(0, 0);
			} else {
				tableModel.setStackFrames(new ArrayList());
			}
		}
	}

	public StackFrame getSelectedFrame() {
		return tableModel.getStackFrame(getSelectedRow());
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		if (e.getValueIsAdjusting())
			return;
		if (getSelectedRowCount() != 0) {
			controller.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED,
					DebuggerController.ACTION_FRAME));
		}
	}
}