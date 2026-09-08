package de.hamster.debugger.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.sun.jdi.StackFrame;

import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class StackFrameTableModel extends AbstractTableModel {
	List stackFrames;

	public StackFrameTableModel() {
		stackFrames = new ArrayList(0);
	}

	public void setStackFrames(List stackFrames) {
		this.stackFrames = stackFrames;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return 4;
	}

	public int getRowCount() {
		return stackFrames.size();
	}

	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return ImageIcon.class;
		if (columnIndex == 3)
			return Integer.class;
		return super.getColumnClass(columnIndex);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (stackFrames.size() == 0)
			return null;
		StackFrame frame = (StackFrame) stackFrames.get(rowIndex);
		switch (columnIndex) {
			case 0 :
				return Utils.getIcon("Play16.gif");
			case 1 :
				return frame.location().declaringType().name();
			case 2 :
				return frame.location().method().name();
			case 3 :
				return new Integer(frame.location().lineNumber());
		}
		return null;
	}

	public StackFrame getStackFrame(int i) {
		return (StackFrame) stackFrames.get(i);
	}
}