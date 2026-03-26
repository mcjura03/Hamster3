package de.hamster.debugger.view;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import de.hamster.debugger.model.DebuggerModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * TODO: Aktualisierung
 * 
 * @author $Author: djasper, jrahn $
 * @version $Revision: 1.2 $
 */
public class VariableViewer extends JTree implements PropertyChangeListener {

	private final DefaultMutableTreeNode root;
	private DefaultTreeModel model;

	private final DebuggerModel debuggerModel;

	public VariableViewer(DebuggerModel debuggerModel) {
		super(new DefaultMutableTreeNode());
		this.model = (DefaultTreeModel) getModel();
		this.debuggerModel = debuggerModel;

		this.root = (DefaultMutableTreeNode) this.model.getRoot();
		setRootVisible(false);
		setShowsRootHandles(true);

		// alt: debuggerModel.addObserver(this);
		// neu (und "Leaking this" vermeiden):
		SwingUtilities.invokeLater(() -> this.debuggerModel.addPropertyChangeListener(VariableViewer.this));
	}

	public void showVariable(StackFrame frame) {
		root.removeAllChildren();
		root.add(new VariableTreeNode("this", frame.thisObject()));
		try {
			// Generics statt raw List
			List<LocalVariable> variables = frame.visibleVariables();
			for (int i = 0; i < variables.size(); i++) {
				LocalVariable variable = variables.get(i);
				Value value = frame.getValue(variable);
				root.add(new VariableTreeNode(variable.name(), value));
			}
		} catch (AbsentInformationException e) {
			e.printStackTrace();
		}
		model = new DefaultTreeModel(root);
		setModel(model);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Wir reagieren nur auf State-Änderungen
		if (!DebuggerModel.ARG_STATE.equals(evt.getPropertyName())) {
			return;
		}

		if (debuggerModel.getState() == DebuggerModel.NOT_RUNNING) {
			// auf dem EDT updaten
			if (SwingUtilities.isEventDispatchThread()) {
				clearTree();
			} else {
				SwingUtilities.invokeLater(this::clearTree);
			}
		}
	}

	private void clearTree() {
		DefaultMutableTreeNode emptyRoot = new DefaultMutableTreeNode();
		model.setRoot(emptyRoot);
		setModel(model);
	}

	// Optional: falls du später sauber aufräumen willst
	public void dispose() {
		debuggerModel.removePropertyChangeListener(this);
	}
}