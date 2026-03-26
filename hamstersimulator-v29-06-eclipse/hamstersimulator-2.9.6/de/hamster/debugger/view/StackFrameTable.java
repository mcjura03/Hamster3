package de.hamster.debugger.view;

import com.sun.jdi.StackFrame;
import de.hamster.debugger.controller.DebuggerController;
import de.hamster.debugger.model.DebuggerModel;
import de.hamster.workbench.Utils;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

/**
 * @author $Author: djasper, jrahn $
 * @version $Revision: 1.2 $
 */
public class StackFrameTable extends JTable implements PropertyChangeListener {

	private final StackFrameTableModel tableModel;

	private final DebuggerModel model;
	private final DebuggerController controller;

	public StackFrameTable(DebuggerModel model, DebuggerController controller) {
		super(new StackFrameTableModel());
		this.model = model;
		this.controller = controller;

		// alt: model.addObserver(this);
		// neu:
		javax.swing.SwingUtilities.invokeLater(() -> this.model.addPropertyChangeListener(StackFrameTable.this));

		this.tableModel = (StackFrameTableModel) getModel();

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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// alt: update(Observable o, Object arg)
		// neu: evt.getPropertyName()
		if (!DebuggerModel.ARG_STATE.equals(evt.getPropertyName())) {
			return;
		}

		if (model.getState() == DebuggerModel.NOT_RUNNING) {
			tableModel.setStackFrames(new ArrayList<StackFrame>());
			return;
		}

		if ((model.getState() == DebuggerModel.PAUSED || model.isSuspended())
				&& model.isEnabled()
				&& model.getStackFrames() != null) {
			tableModel.setStackFrames(model.getStackFrames());

			// Nur selektieren, wenn mindestens eine Zeile existiert (verhindert mögliche Exceptions)
			if (tableModel.getRowCount() > 0) {
				setRowSelectionInterval(0, 0);
			}
		} else {
			tableModel.setStackFrames(new ArrayList<StackFrame>());
		}
	}

	public StackFrame getSelectedFrame() {
		return tableModel.getStackFrame(getSelectedRow());
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		if (e.getValueIsAdjusting()) {
			return;
		}
		if (getSelectedRowCount() != 0) {
			controller.actionPerformed(new ActionEvent(
					this,
					ActionEvent.ACTION_PERFORMED,
					DebuggerController.ACTION_FRAME));
		}
	}
}