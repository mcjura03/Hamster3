package de.hamster.editor.view;

import de.hamster.model.HamsterFile;
import de.hamster.workbench.Utils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author Daniel
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Color BACKGROUND = new Color(67, 67, 67);
    private static final Color SELECTION_BACKGROUND = new Color(90, 90, 90);
    private static final Color TEXT_COLOR = Color.WHITE;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);

        // --- Icons setzen ---
        if (leaf) {
            HamsterFile file = ((FileTreeNode) value).getHamsterFile();

            if (file.getType() == HamsterFile.TERRITORIUM)
                setIcon(Utils.getIcon("Terrain16.gif"));
            else if (file.getType() == HamsterFile.HAMSTERCLASS)
                setIcon(Utils.getIcon("HamsterClass16.gif"));
            else if (file.getType() == HamsterFile.IMPERATIVE)
                setIcon(Utils.getIcon("IHamster16.gif"));
            else if (file.getType() == HamsterFile.OBJECT)
                setIcon(Utils.getIcon("OOHamster16.gif"));
            else if (file.getType() == HamsterFile.SCHEMEPROGRAM)
                setIcon(Utils.getIcon("SchemeHamster16.gif"));
            else if (file.getType() == HamsterFile.PROLOGPROGRAM)
                setIcon(Utils.getIcon("PrologHamster16.gif"));
            else if (file.getType() == HamsterFile.PYTHONPROGRAM)
                setIcon(Utils.getIcon("PythonHamster16.gif"));
            else if (file.getType() == HamsterFile.JAVASCRIPTPROGRAM)
                setIcon(Utils.getIcon("JavaScriptHamster16.gif"));
            else if (file.getType() == HamsterFile.RUBYPROGRAM)
                setIcon(Utils.getIcon("RubyHamster16.gif"));
            else if (file.getType() == HamsterFile.SCRATCHPROGRAM)
                setIcon(Utils.getIcon("ScratchHamster16.gif"));
            else if (file.getType() == HamsterFile.FSM)
                setIcon(Utils.getIcon("FSMHamster16.gif"));
            else if (file.getType() == HamsterFile.FLOWCHART)
                setIcon(Utils.getIcon("FlowchartHamster16.gif"));
            else if (file.getType() == HamsterFile.LEGOPROGRAM)
                setIcon(Utils.getIcon("Zahnrad16.gif"));
        }

        // --- Farben setzen (entscheidend!) ---
        setTextNonSelectionColor(TEXT_COLOR);
        setTextSelectionColor(TEXT_COLOR);

        setBackgroundNonSelectionColor(BACKGROUND);
        setBackgroundSelectionColor(SELECTION_BACKGROUND);

        setOpaque(true);

        return this;
    }
}