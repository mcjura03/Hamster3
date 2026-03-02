package de.hamster.editor.view;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.hamster.model.HamsterFile;
import de.hamster.workbench.Utils;

/**
 * @author Daniel
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (leaf) {
			HamsterFile file = ((FileTreeNode) value).getHamsterFile();
			if (file.getType() == HamsterFile.TERRITORIUM) // dibo 260110
				setIcon(Utils.getIcon("Terrain16.gif"));
			else if (file.getType() == HamsterFile.HAMSTERCLASS)
				setIcon(Utils.getIcon("HamsterClass16.gif"));
			else if (file.getType() == HamsterFile.IMPERATIVE)
				setIcon(Utils.getIcon("IHamster16.gif"));
			else if (file.getType() == HamsterFile.OBJECT)
				setIcon(Utils.getIcon("OOHamster16.gif"));
			else if (file.getType() == HamsterFile.SCHEMEPROGRAM) // Martin
				setIcon(Utils.getIcon("SchemeHamster16.gif"));
			else if (file.getType() == HamsterFile.PROLOGPROGRAM) // Prolog
				setIcon(Utils.getIcon("PrologHamster16.gif"));
			else if (file.getType() == HamsterFile.PYTHONPROGRAM) // Python
				setIcon(Utils.getIcon("PythonHamster16.gif"));
			else if (file.getType() == HamsterFile.JAVASCRIPTPROGRAM) // JavaScript
				setIcon(Utils.getIcon("JavaScriptHamster16.gif"));
			else if (file.getType() == HamsterFile.RUBYPROGRAM) // Ruby
				setIcon(Utils.getIcon("RubyHamster16.gif"));
			else if (file.getType() == HamsterFile.SCRATCHPROGRAM) // Scratch
				setIcon(Utils.getIcon("ScratchHamster16.gif"));
			else if (file.getType() == HamsterFile.FSM) // FSM
				setIcon(Utils.getIcon("FSMHamster16.gif"));
			else if (file.getType() == HamsterFile.FLOWCHART) // Flowchart
				setIcon(Utils.getIcon("FlowchartHamster16.gif"));
			else if (file.getType() == HamsterFile.LEGOPROGRAM) // lego
				setIcon(Utils.getIcon("Zahnrad16.gif"));
		}
		this.setBackgroundNonSelectionColor(tree.getBackground()); // dibo
																	// 230309
		// this.setBackgroundSelectionColor(new Color(240, 244, 246)); // dibo
		// 230309
		return this;
	}
}
