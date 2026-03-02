package de.hamster.debugger.view;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;

import de.hamster.debugger.model.DebuggerModel;

/**
 * TODO: Aktualisierung
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class VariableViewer extends JTree implements Observer {
	DefaultMutableTreeNode root;
	DefaultTreeModel model;
	
	DebuggerModel debuggerModel;

	public VariableViewer(DebuggerModel debuggerModel) {
		super(new DefaultMutableTreeNode());
		model = (DefaultTreeModel) getModel();
		this.debuggerModel = debuggerModel;
		debuggerModel.addObserver(this);
		root = (DefaultMutableTreeNode) model.getRoot();
		setRootVisible(false);
		setShowsRootHandles(true);
	}

	public void showVariable(StackFrame frame) {
		root.removeAllChildren();
		root.add(new VariableTreeNode("this", frame.thisObject()));
		try {
			List variables = frame.visibleVariables();
			for (int i = 0; i < variables.size(); i++) {
				LocalVariable variable = (LocalVariable) variables.get(i);
				Value value = frame.getValue(variable);
				root.add(new VariableTreeNode(variable.name(), value));
			}
		} catch (AbsentInformationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model = new DefaultTreeModel(root);
		setModel(model);
	}

	public void update(Observable o, Object arg) {
		if(debuggerModel.getState() == DebuggerModel.NOT_RUNNING) {
			model.setRoot(new DefaultMutableTreeNode());
		}
	}
}