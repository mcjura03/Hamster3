package de.hamster.simulation.view;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.hamster.simulation.controller.SimulationController;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.workbench.ForwardAction;
import de.hamster.workbench.Utils;

public class SimulationTools implements MouseListener, MouseMotionListener {
	public static final int WALL = 0;

	private int startRow;
	private int startCol;
	private int endRow;
	private int endCol;
	private Rectangle lastDrag;
	protected boolean isDragging;

	private TerrainTool activeTool;
	private SimulationPanel simulationPanel;

	private SimulationModel simulationModel;
	private SimulationController simulationController;

	public class NewAction extends ForwardAction {
		public NewAction() {
			super("simulation.new", SimulationController.NEW);
		}
	}
	public class OpenAction extends ForwardAction {
		public OpenAction() {
			super("simulation.open", SimulationController.OPEN);
		}
	}
	public class SaveAsAction extends ForwardAction {
		public SaveAsAction() {
			super("simulation.save", SimulationController.SAVE_AS);
		}
	}
	public class ZoomInAction extends AbstractAction {
		public ZoomInAction() {
			super();
			Utils.setData(this, "simulation.zoomin");
		}
		public void actionPerformed(ActionEvent e) {
			simulationPanel.zoomIn();
		}
	}
	public class ZoomOutAction extends AbstractAction {
		public ZoomOutAction() {
			super();
			Utils.setData(this, "simulation.zoomout");
		}
		public void actionPerformed(ActionEvent e) {
			simulationPanel.zoomOut();
		}
	}

	public class SelectCornAction extends AbstractAction {
		public SelectCornAction() {
			Utils.setData(this, "simulation.corn");
		}
		public void actionPerformed(ActionEvent e) {
			activeTool = cornTool;
		}
	}
	public class SelectDeleteAction extends AbstractAction {
		public SelectDeleteAction() {
			Utils.setData(this, "simulation.delete");
		}
		public void actionPerformed(ActionEvent e) {
			activeTool = deleteTool;
		}
	}
	public class SelectHamsterAction extends AbstractAction {
		public SelectHamsterAction() {
			Utils.setData(this, "simulation.hamster");
		}
		public void actionPerformed(ActionEvent e) {
			activeTool = hamsterTool;
		}
	}
	public class SelectWallAction extends AbstractAction {
		public SelectWallAction() {
			Utils.setData(this, "simulation.wall");
		}
		public void actionPerformed(ActionEvent e) {
			activeTool = wallTool;
		}
	}
	public class TurnHamsterAction extends ForwardAction {
		public TurnHamsterAction() {
			super("simulation.turn", SimulationController.TURN);
		}
	}
	public class HamsterCornAction extends ForwardAction {
		public HamsterCornAction() {
			super("simulation.hamstercorn", SimulationController.HAMSTER_CORN);
		}
	}

	public class ResetAction extends ForwardAction {
		public ResetAction() {
			super("simulation.reset", SimulationController.RESET);
			this.setEnabled(false);
		}
	}
	
	// chris
	public class ZoomInAction3D extends ForwardAction {
		
		public ZoomInAction3D() {
			super("simulation.zoomIn3D", SimulationController.ZOOM_IN_3D);
		}
	}

	public class ZoomOutAction3D extends ForwardAction {
		
		public ZoomOutAction3D() {
			super("simulation.zoomOut3D", SimulationController.ZOOM_OUT_3D);
		}
	}

	public class RotateLeftAction3D extends ForwardAction {
		
		public RotateLeftAction3D() {
			super("simulation.turnLeft3D", SimulationController.TURN_LEFT_3D);
		}
	}

	public class RotateRightAction3D extends ForwardAction {
		
		public RotateRightAction3D() {
			super("simulation.turnRight3D", SimulationController.TURN_RIGHT_3D);
		}
	}

	public class LookUpAction3D extends ForwardAction {
		
		public LookUpAction3D() {
			super("simulation.lookUp3D", SimulationController.LOOK_UP_3D);
		}
	}

	public class LookDownAction3D extends ForwardAction {
		
		public LookDownAction3D() {
			super("simulation.lookDown3D", SimulationController.LOOK_DOWN_3D);
		}
	}
	
	public class ToggleGridAction3D extends ForwardAction {
		
		public ToggleGridAction3D() {
			super("simulation.toggleGrid3D", SimulationController.TOGGLE_GRID_3D);
		}
	}

	public class ToggleMusicAction3D extends ForwardAction {
		
		public ToggleMusicAction3D() {
			super("simulation.toggleMusic3D", SimulationController.TOGGLE_MUSIC_3D);
		}
	}

	public class ToggleSoundAction3D extends ForwardAction {
		
		public ToggleSoundAction3D() {
			super("simulation.toggleSound3D", SimulationController.TOGGLE_SOUND_3D);
			
		}
	}
	
	public class PerspectiveAction3D extends ForwardAction {
		
		public PerspectiveAction3D() {
			super("simulation.perspective3D", SimulationController.PERSPECTIVE_3D);
			
		}
	}
	
	private NewAction newAction = new NewAction();
	private OpenAction openAction = new OpenAction();
	private SaveAsAction saveAsAction = new SaveAsAction();
	private SelectCornAction selectCornAction = new SelectCornAction();
	private SelectHamsterAction selectHamsterAction = new SelectHamsterAction();
	private SelectWallAction selectWallAction = new SelectWallAction();
	private SelectDeleteAction selectDeleteAction = new SelectDeleteAction();
	private ZoomInAction zoomInAction = new ZoomInAction();
	private ZoomOutAction zoomOutAction = new ZoomOutAction();
	private TurnHamsterAction turnHamsterAction = new TurnHamsterAction();
	private HamsterCornAction hamsterCornAction = new HamsterCornAction();
	private ResetAction resetAction = new ResetAction();
	
	// chris:
	private ZoomInAction3D zoomInAction3D = new ZoomInAction3D();
	private ZoomOutAction3D zoomOutAction3D = new ZoomOutAction3D();
	private RotateLeftAction3D rotateLeftAction3D = new RotateLeftAction3D();
	private RotateRightAction3D rotateRightAction3D = new RotateRightAction3D();
	private LookUpAction3D lookUpAction3D = new LookUpAction3D();
	private LookDownAction3D lookDownAction3D = new LookDownAction3D();
	private ToggleGridAction3D toggleGridAction3D = new ToggleGridAction3D();
	private ToggleSoundAction3D toggleSoundAction3D = new ToggleSoundAction3D();
	private ToggleMusicAction3D toggleMusicAction3D = new ToggleMusicAction3D();
	private PerspectiveAction3D perspectiveAction3D = new PerspectiveAction3D();
		
	// dibo
	public ResetAction getResetAction() {
		return resetAction;
	}

	public class CornTool extends TerrainTool {
		public CornTool() {
			super(true);
			setImage(Utils.getImage("Corn32.png"));
		}
		public void done() {
			try {
				if (!simulationController.containsValidCornCell(Math.min(
						startCol, endCol), Math.min(startRow, endRow), Math
						.max(startCol, endCol), Math.max(startRow, endRow)))
					return;
				String countString = Utils.input(simulationPanel,
						"simulation.dialog.cornnumber", "1");
				if (countString != null) {
					int count = Math.max(Integer.parseInt(countString), 0);
					simulationController.setCorn(Math.min(startCol, endCol),
							Math.min(startRow, endRow), Math.max(startCol,
									endCol), Math.max(startRow, endRow), count);
				}
			} catch (NumberFormatException e) {
			}
		}
	}
	public class HamsterTool extends TerrainTool {
		public HamsterTool() {
			super(false);
		}
		public void done() {
			simulationController.setHamsterPos(endCol, endRow);
		}
	}
	public class WallTool extends TerrainTool {
		public WallTool() {
			super(true);
			setImage(Utils.getImage("Wall32.png"));
		}
		public void done() {
			simulationController.setWall(Math.min(startCol, endCol), Math.min(
					startRow, endRow), Math.max(startCol, endCol), Math.max(
					startRow, endRow));
		}
	}
	public class DeleteTool extends TerrainTool {
		public DeleteTool() {
			super(true);
			setImage(Utils.getImage("Delete32.gif"));
		}
		public void done() {
			simulationController.setEmpty(Math.min(startCol, endCol), Math.min(
					startRow, endRow), Math.max(startCol, endCol), Math.max(
					startRow, endRow));
		}
	}
	private CornTool cornTool = new CornTool();
	private DeleteTool deleteTool = new DeleteTool();
	private HamsterTool hamsterTool = new HamsterTool();
	private WallTool wallTool = new WallTool();

	public SimulationTools(SimulationModel model,
			SimulationController controller) {
		this.simulationModel = model;
		this.simulationController = controller;
		
		newAction.addActionListener(controller);
		openAction.addActionListener(controller);
		saveAsAction.addActionListener(controller);
		turnHamsterAction.addActionListener(controller);
		hamsterCornAction.addActionListener(controller);
		resetAction.addActionListener(controller);
		
		JToolBar toolBar = controller.getWorkbench().getView().findToolBar("simulation");
		toolBar.add(Utils.createButton(newAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(openAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(saveAsAction));

		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));

		JToggleButton corn = Utils.createToggleButton(selectCornAction);
		JToggleButton hamster = Utils.createToggleButton(selectHamsterAction);
		JToggleButton wall = Utils.createToggleButton(selectWallAction);
		JToggleButton delete = Utils.createToggleButton(selectDeleteAction);

		toolBar.add(hamster);
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(turnHamsterAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(hamsterCornAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(corn);
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(wall);
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(delete);

		ButtonGroup group = new ButtonGroup();
		group.add(corn);
		group.add(hamster);
		group.add(wall);
		group.add(delete);

		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));

		toolBar.add(Utils.createButton(zoomInAction));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(zoomOutAction));

		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));
		toolBar.add(Utils.createButton(resetAction));
		
		
		//chris
		zoomInAction3D.addActionListener(controller);
		zoomOutAction3D.addActionListener(controller);
		rotateLeftAction3D.addActionListener(controller);		
		rotateRightAction3D.addActionListener(controller);
		lookUpAction3D.addActionListener(controller);		
		lookDownAction3D.addActionListener(controller);
		toggleGridAction3D.addActionListener(controller);
		toggleMusicAction3D.addActionListener(controller);
		toggleSoundAction3D.addActionListener(controller);
		perspectiveAction3D.addActionListener(controller);
		
		toolBar = controller.getWorkbench().getView().findToolBar(
		"3dsimulation");
		
		toolBar.add(Utils.createButton(zoomInAction3D));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(zoomOutAction3D));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));		
		toolBar.add(Utils.createButton(rotateRightAction3D));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(rotateLeftAction3D));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));			
		toolBar.add(Utils.createButton(lookUpAction3D));
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		toolBar.add(Utils.createButton(lookDownAction3D));		
		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));		

		toolBar.add(Utils.createButton(perspectiveAction3D));
			
		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));
		JToggleButton b = Utils.createToggleButton(toggleGridAction3D);		
		String s = controller.getWorkbench().getProperty("grid", "true");
		if (s.equals("true")) b.setSelected(true); else b.setSelected(false); 
		toolBar.add(b);
		
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		b = Utils.createToggleButton(toggleSoundAction3D);
		s = controller.getWorkbench().getProperty("sound", "true");
		if (s.equals("true")) b.setSelected(true); else b.setSelected(false); 
		toolBar.add(b);
		
		toolBar.add(Box.createRigidArea(new Dimension(2, 2)));
		b = Utils.createToggleButton(toggleMusicAction3D);
		s = controller.getWorkbench().getProperty("music", "true");
		if (s.equals("true")) b.setSelected(true); else b.setSelected(false); 
		toolBar.add(b);
		
	}

	public void setSimulationPanel(SimulationPanel simulationPanel)
	{
		this.simulationPanel = simulationPanel;
		
		simulationPanel.addMouseListener(this);
		simulationPanel.addMouseMotionListener(this);
	}
	
	// Prolog
	/**
	 * Aktiviert / Deaktiviert die Möglichkeit zum Editieren im Territorium des 
	 * Simulationssfensters. Das Editieren soll während der Ausführung eines 
	 * Prolog-Programms unterbunden werden.
	 */
	public void setSimulationPanelListenerEnabled(boolean value)
	{
		if(value)
		{
			boolean mLRegistered = false;
			for (MouseListener ml : simulationPanel.getMouseListeners())
         {
	         if(ml.equals(this))
	         {
	         	mLRegistered = true;
	         }
         }
			boolean mmLRegistered = false;
			for (MouseMotionListener mml : simulationPanel.getMouseMotionListeners())
         {
	         if(mml.equals(this))
	         {
	         	mmLRegistered = true;
	         }
         }
			if(!mLRegistered)
				simulationPanel.addMouseListener(this);
			if(!mmLRegistered)
				simulationPanel.addMouseMotionListener(this);

			newAction.setEnabled(true);
			openAction.setEnabled(true);
			saveAsAction.setEnabled(true);			
			selectCornAction.setEnabled(true);			
			selectHamsterAction.setEnabled(true);
			selectWallAction.setEnabled(true);
			selectDeleteAction.setEnabled(true);			
			turnHamsterAction.setEnabled(true);
			hamsterCornAction.setEnabled(true);
			resetAction.setEnabled(true);
		}
		else
		{
			simulationPanel.removeMouseListener(this);
			simulationPanel.removeMouseMotionListener(this);
			
			newAction.setEnabled(false);
			openAction.setEnabled(false);
			saveAsAction.setEnabled(false);			
			selectCornAction.setEnabled(false);			
			selectHamsterAction.setEnabled(false);
			selectWallAction.setEnabled(false);
			selectDeleteAction.setEnabled(false);			
			turnHamsterAction.setEnabled(false);
			hamsterCornAction.setEnabled(false);
			resetAction.setEnabled(false);
		}
	}

	public void paint(Graphics2D g) {
		if (activeTool == null || !isDragging)
			return;
		if (activeTool.isDragTool()) {
			int minCol = Math.min(startCol, endCol);
			int maxCol = Math.max(startCol, endCol);
			int minRow = Math.min(startRow, endRow);
			int maxRow = Math.max(startRow, endRow);
			for (int i = minCol; i <= maxCol; i++) {
				if (i < 0 || i >= simulationModel.getTerrain().getWidth())
					continue;
				for (int j = minRow; j <= maxRow; j++) {
					if (j < 0 || j >= simulationModel.getTerrain().getHeight())
						continue;
					simulationPanel.drawImage(g, activeTool.getImage(), i, j);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		startCol = simulationPanel.getCol(e.getX());
		startRow = simulationPanel.getRow(e.getY());
		endCol = startCol;
		endRow = startRow;
		isDragging = true;
		simulationPanel.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if (activeTool != null)
			activeTool.done();
		isDragging = false;
		simulationPanel.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		endCol = simulationPanel.getCol(e.getX());
		endRow = simulationPanel.getRow(e.getY());
		simulationPanel.repaint();
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}