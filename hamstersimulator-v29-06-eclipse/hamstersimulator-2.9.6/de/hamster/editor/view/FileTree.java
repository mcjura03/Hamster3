package de.hamster.editor.view;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.hamster.editor.controller.EditorController;
import de.hamster.model.HamsterFile;
import de.hamster.prolog.controller.PrologController;
import de.hamster.prolog.model.PrologHamster.TerObject;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.model.Terrain;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.10 $
 */
public class FileTree extends JTree implements MouseListener,
		DragGestureListener, DragSourceListener, DropTargetListener {
	protected FilePopupMenu filePopupMenu;
	protected EditorController controller;

	protected DragSource dragSource;

	public FileTree(EditorController controller) {
		super(new FileTreeNode(HamsterFile.getHamsterFile(Utils.HOME)));
		this.controller = controller;
		filePopupMenu = new FilePopupMenu(controller);

		setCellRenderer(new FileTreeCellRenderer());
		addMouseListener(this);
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		new DropTarget(this, this);
		dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	public void update() {
		TreePath t = getSelectionPath();
		Enumeration expanded = getExpandedDescendants(new TreePath(getModel()
				.getRoot()));
		((FileTreeNode) getModel().getRoot()).update();
		((DefaultTreeModel) getModel()).reload();
		while (expanded != null && expanded.hasMoreElements()) {
			TreePath tp = (TreePath) expanded.nextElement();
			expandPath(tp);
		}
		setSelectionPath(t);
	}

	public String getFilename() {
		TreePath path = getLeadSelectionPath();
		FileTreeNode node = (FileTreeNode) path.getLastPathComponent();
		return node.getHamsterFile().getAbsolute();
	}

	public HamsterFile getFile(TreePath path) {
		FileTreeNode node = (FileTreeNode) path.getLastPathComponent();
		return node.getHamsterFile();
	}

	public void mouseClicked(MouseEvent e) {
		TreePath tp = getPathForLocation(e.getX(), e.getY());
		if (tp == null)
			return;
		HamsterFile file = getFile(tp);
		if (file.isTerrain()) {
			SimulationModel simModel = Workbench.getWorkbench().getSimulation()
					.getSimulationModel();

			simModel.setTerrain(new Terrain(file.load()));

			// Prolog
			if (Utils.PROLOG) {
				PrologController.get().updateTerrainObject(
						TerObject.TERRITORIUM);
			}

			if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
				JFrame simFrame = Workbench.getWorkbench().getView()
						.getSimulationFrame();
				if (!simFrame.isVisible()) {
					simFrame.setVisible(true);
					Workbench.winSim.setState(true);
				}
				simFrame.toFront();
			}
		} else {
			filePopupMenu.setFile(file);
			if (!getFile(tp).isDirectory())
				controller.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED,
						EditorController.FILE_OPEN));
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!e.isPopupTrigger())
			return;
		TreePath tp = getPathForLocation(e.getX(), e.getY());
		if (tp == null)
			return;
		filePopupMenu.setFile(getFile(tp));
		filePopupMenu.show(this, e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		if (!e.isPopupTrigger())
			return;
		TreePath tp = getPathForLocation(e.getX(), e.getY());
		if (tp == null)
			return;
		filePopupMenu.setFile(getFile(tp));
		filePopupMenu.show(this, e.getX(), e.getY());
	}

	public FilePopupMenu getFilePopupMenu() {
		return filePopupMenu;
	}

	// DragGuestureListener //

	public void dragGestureRecognized(DragGestureEvent dge) {
		TreePath tp = getPathForLocation(dge.getDragOrigin().x, dge
				.getDragOrigin().y);
		if (tp == null)
			return;
		addSelectionPath(tp);
		FileTreeNode ftn = (FileTreeNode) tp.getLastPathComponent();
		if (ftn.getHamsterFile() != HamsterFile.getHamsterFile(Utils.HOME)) {
			SimpleTransferable st = new SimpleTransferable(ftn.getHamsterFile()
					.getFile());
			dragSource.startDrag(dge, selectCursor(dge.getDragAction()), st,
					this);
		}
	}

	private Cursor selectCursor(int action) {
		switch (action) {
		case DnDConstants.ACTION_MOVE:
			return DragSource.DefaultMoveDrop;
		case DnDConstants.ACTION_COPY:
			return DragSource.DefaultCopyDrop;
		case DnDConstants.ACTION_NONE:
			return DragSource.DefaultMoveNoDrop;
		}
		return null;
	}

	// DragSourceListener //

	public void dragEnter(DragSourceDragEvent dsde) {
		dsde.getDragSourceContext().setCursor(
				selectCursor(dsde.getDropAction()));
	}

	public void dragOver(DragSourceDragEvent dsde) {
		dsde.getDragSourceContext().setCursor(
				selectCursor(dsde.getDropAction()));
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		controller.refreshFiles();
	}

	public void dragExit(DragSourceEvent dse) {
		dse.getDragSourceContext().setCursor(
				selectCursor(DnDConstants.ACTION_NONE));
	}

	// DropTargetLister //
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
		TreePath tp = getPathForLocation(dtde.getLocation().x, dtde
				.getLocation().y);
		if (tp == null) {
			dtde.rejectDrag();
			return;
		}
		FileTreeNode ftn = (FileTreeNode) tp.getLastPathComponent();
		if (ftn.isLeaf()) {
			dtde.rejectDrag();
		} else {
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		HamsterFile source = null;
		dtde.acceptDrop(dtde.getDropAction());
		try {
			Transferable tr = dtde.getTransferable();
			source = HamsterFile.getHamsterFile((File) ((List) tr
					.getTransferData(DataFlavor.javaFileListFlavor)).get(0));
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TreePath tp = getPathForLocation(dtde.getLocation().x, dtde
				.getLocation().y);
		FileTreeNode ftn = null; // dibo 22(08/07
		if (tp != null) {
			ftn = (FileTreeNode) tp.getLastPathComponent();
		}
		if (ftn != null
				&& (ftn.getHamsterFile().getAbsolute().startsWith(
						source.getAbsolute() + Utils.FSEP) || ftn
						.getHamsterFile().getAbsolute().equals(
								source.getAbsolute()))) {
			// System.out.println("geht nicht");
		} else {
			if (dtde.getDropAction() == DnDConstants.ACTION_COPY)
				controller.copyFile(source, ftn.getHamsterFile());
			if (dtde.getDropAction() == DnDConstants.ACTION_MOVE)
				controller.moveFile(source, new File(ftn.getHamsterFile()
						.getFile(), source.getFile().getName()));
		}
	}

	public void dragExit(DropTargetEvent dte) {
	}
}