package de.hamster.fsm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import de.hamster.fsm.controller.FsmMenuMode;
import de.hamster.fsm.controller.handler.CreateObjectInConttextMenu;
import de.hamster.fsm.controller.handler.FsmAutomataPanelHandler;
import de.hamster.fsm.controller.handler.LayoutHandler;
import de.hamster.fsm.controller.handler.UnlockFsmAutomataPanelHandler;
import de.hamster.fsm.controller.handler.UpdateHandler;
import de.hamster.fsm.controller.handler.ZoomInHandler;
import de.hamster.fsm.controller.handler.ZoomOutHandler;
import de.hamster.fsm.model.CommentObject;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.RenderableObject;
import de.hamster.fsm.model.state.StateObject;
import de.hamster.fsm.model.transition.InputObject;
import de.hamster.fsm.model.transition.OutputObject;
import de.hamster.fsm.model.transition.TransitionDescriptionObject;
import de.hamster.fsm.model.transition.TransitionObject;
import de.hamster.fsm.model.transition.VoidObject;

/**
 * Das JPanel, in dem der Automat gezeichnet wird. Grafisch gesehen ist es das
 * Herzstück des Programms.
 * 
 * @author Raffaela Ferrari
 * 
 */
public class FsmAutomataPanel extends JPanel implements ActionListener,
		KeyListener, UpdateHandler {
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();
	private static final Border BORDER = BorderFactory.createEtchedBorder(
			Color.BLACK, Color.GRAY);

	private boolean panelLocked;
	private FsmPanel panel;
	private JPanel hoverField;
	private JTextField inputField;
	private FsmAutomataPanelHandler panelHandler;
	private UnlockFsmAutomataPanelHandler unlockHandler;
	private FsmMenuMode menuMode;
	private BufferedImage image;
	private Dimension area;

	// Temoräre Elemente
	private RenderableObject temp;
	private FsmObject tempHover;
	private TransitionObject tmpTransition;
	private TransitionObject tmpBendTransition;
	private StateObject tmpState;
	private StateObject tmpStateHighlight;
	private List<StateObject> tempStateList;
	int farthestX = 0;
	int farthestY = 0;

	/**
	 * Konstruktor
	 * 
	 * @param panel
	 *            FsmPanel, die die Verbindung zum FsmProgram darstellt.
	 */
	public FsmAutomataPanel(FsmPanel panel) {
		this.panel = panel;
		this.setLayout(null);
		this.area = new Dimension(0, 0);
		this.panelHandler = new FsmAutomataPanelHandler(this);
		this.unlockHandler = new UnlockFsmAutomataPanelHandler(this);
		this.menuMode = FsmMenuMode.editMode;
		this.addMouseListener(this.panelHandler);
		this.addMouseMotionListener(this.panelHandler);
		this.setBorder(BorderFactory.createEtchedBorder());
		this.refresh();
	}

	/**
	 * Gibt das zugehörige FsmPanel zurück.
	 * 
	 * @return FsmPanel
	 */
	public FsmPanel getPanel() {
		return this.panel;
	}

	/**
	 * Öffnet ein Kontextmenu an der spezifierten Stelle, wenn der EditMode
	 * ausgewählt wurde.
	 * 
	 * @param x
	 *            X-Koordinate der spezifizierten Stelle.
	 * @param y
	 *            Y-Koordinate der spezifizierten Stelle.
	 */
	public void openContextMenu(int x, int y) {
		if (this.menuMode == FsmMenuMode.editMode) {
			boolean objectFound = false;
			for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
				FsmObject tmp = state.isClicked(x, y);
				if (tmp != null) {
					this.openContextMenu(tmp,
							tmp.getContextMenuItems(this.panel), x, y);
					objectFound = true;
					break;
				}
			}
			if (!objectFound) {
				for (CommentObject comment : this.panel.getFsmProgram()
						.getAllComments()) {
					if (comment.isClickedOn(x, y)) {
						this.openContextMenu(comment,
								comment.getContextMenuItems(this.panel), x, y);
						objectFound = true;
						break;
					}
				}
			}
			if (!objectFound) {
				this.openContextMenu(this, this.getContextMenuItems(x, y), x, y);
			}
		}
	}

	/**
	 * Öffnet ein KontextMenü für ein spezifisches Objekt mit bestimmten
	 * JMenuItem an einer spezifizierten Position.
	 * 
	 * @param object
	 *            Objekt, für das das KontextMenu geöffnet werden soll
	 * @param menuItems
	 *            JMenuItems, die im Kontextmenu angezeigt werden sollen.
	 * @param x
	 *            X-Koordinate der spezifizierten Stelle.
	 * @param y
	 *            Y-Koordinate der spezifizierten Stelle.
	 */
	private void openContextMenu(Object object, List<JMenuItem> menuItems,
			int x, int y) {
		if (menuItems != null) {
			JPopupMenu popup = new JPopupMenu();
			for (JMenuItem menuItem : menuItems) {
				popup.add(menuItem);
			}
			// Menü anzeigen
			popup.show(this, x, y);
		}
	}

	/**
	 * Gibt die JMenuItems für das FsmAutomataPanel zurück
	 * 
	 * @param x
	 *            X-Koordinate, die für das Erstellen von Zuständen,
	 *            Kommentaren bzw. Transitionen benötigt wird.
	 * @param y
	 *            Y-Koordinate, die für das Erstellen von Zuständen,
	 *            Kommentaren bzw. Transitionen benötigt wird.
	 * @return Liste an JMenuItems
	 */
	private List<JMenuItem> getContextMenuItems(int x, int y) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem createState = new JMenuItem("Zustand hinzuf�gen");
		createState.setActionCommand("state");
		createState
				.addActionListener(new CreateObjectInConttextMenu(this, x, y));
		menuItemList.add(createState);

		JMenuItem createTransition = new JMenuItem("Transition hinzuf�gen");
		createTransition.setActionCommand("transition");
		createTransition.addActionListener(new CreateObjectInConttextMenu(this,
				x, y));
		menuItemList.add(createTransition);

		JMenuItem createComment = new JMenuItem("Kommentar hinzuf�gen");
		createComment.setActionCommand("comment");
		createComment.addActionListener(new CreateObjectInConttextMenu(this, x,
				y));
		menuItemList.add(createComment);

		JMenuItem zoomIn = new JMenuItem("Zoom In");
		zoomIn.addActionListener(new ZoomInHandler(this.panel));
		menuItemList.add(zoomIn);

		JMenuItem zoomOut = new JMenuItem("ZoomOut");
		zoomOut.addActionListener(new ZoomOutHandler(this.panel));
		menuItemList.add(zoomOut);

		JMenuItem layout = new JMenuItem("Layout");
		layout.addActionListener(new LayoutHandler(this.panel));
		menuItemList.add(layout);

		return menuItemList;
	}

	/**
	 * Öffnet ein Input-Feld an der gegebenen Position
	 * 
	 * @param text
	 *            Text, der in dem Input-Feld angezeigt werden soll.
	 * @param x
	 *            X-Koordinate, an dem das Input-Feld geöffnet werden soll.
	 * @param y
	 *            Y-Koordinate, an dem das Input-Feld geöffnet werden soll.
	 * @return
	 */
	public void setInputField(String text, int x, int y) {
		this.inputField = new JTextField(text != null ? text : "");
		this.add(this.inputField);
		this.inputField.setBounds(x, y, 100, 25);
		this.inputField.requestFocus();
		this.refresh();
		this.inputField.addActionListener(this);
		this.inputField.addKeyListener(this);
	}

	/**
	 * Öffnet den Dialog, um eine neue Transition erstellen zu können.
	 * 
	 * @param x
	 *            X-Koordinate, an dem der Dialog geöffnet werden soll.
	 * @param y
	 *            Y-Koordinate, an dem der Dialog geöffnet werden soll.
	 */
	public void setCreateTransitionField(int x, int y) {
		FsmCreateTransitionDialog.createFsmCreateTransitionDialog(this.panel,
				x, y);
	}

	/**
	 * Setzt ein JPanel für die Zeit des Hovers über einem Input oder Output,
	 * damit alle Elemente betrachtet werden können.
	 * 
	 * @param x
	 *            X-Koordinate, an der das JPanel kreiert wird.
	 * @param y
	 *            Y-Koordinate, an der das JPanel kreiert wird.
	 */
	public boolean doHoverMode(int x, int y) {
		if (this.tempHover != null && this.tempHover.isClickedOn(x, y)) {
			return true;
		} else {
			if (this.tempHover != null && !this.tempHover.isClickedOn(x, y)) {
				this.resetHoverField();
			}
			for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
				this.tempHover = state.isClickedOnHover(x, y);
				if (this.tempHover != null) {
					if (this.tempHover instanceof InputObject
							|| this.tempHover instanceof OutputObject) {
						FsmObject tempClone = this.tempHover.clone();
						if (tempClone instanceof InputObject) {
							((InputObject) tempClone).setFullView(true);
						} else {
							((OutputObject) tempClone).setFullView(true);
						}
						this.hoverField = new HoverJPanel(tempClone);
						this.add(this.hoverField);
						this.hoverField.setBorder(FsmAutomataPanel.BORDER);
						this.hoverField.setBounds(tempClone.getXCoordinate(),
								tempClone.getYCoordinate(),
								tempClone.getWidth() + 10,
								tempClone.getHeight() + 10);
						this.refresh();
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Macht das HoverField wieder unsichtbar
	 */
	public void resetHoverField() {
		if (this.hoverField != null) {
			this.tempHover = null;
			this.hoverField.setVisible(false);
			this.remove(this.hoverField);
			this.refresh();
		}
	}

	/**
	 * Setzt das Eingabefeld für Kommentare und Zustände zusrück
	 */
	public void resetInputField() {
		this.inputField.removeActionListener(this);
		this.remove(this.inputField);
		this.inputField = null;
		this.refresh();
	}

	/**
	 * Sperrt bzw. Entsperrt das FsmAutomataPanel je nach dem Wert von locked.
	 * 
	 * @param locked
	 *            Gibt an, ob das FsmAutomataPanel gesperrt werden soll.
	 */
	public void setLocked(boolean locked) {
		if (locked == this.panelLocked) {
			return;
		}
		this.panelLocked = locked;

		if (locked) {
			this.removeMouseListener(this.panelHandler);
			this.removeMouseMotionListener(this.panelHandler);
			this.addMouseListener(this.unlockHandler);
		} else {
			this.addMouseListener(this.panelHandler);
			this.addMouseMotionListener(this.panelHandler);
			this.removeMouseListener(this.unlockHandler);
		}
	}

	/**
	 * Gibt das BufferedImage von dieses FsmAutomataPanel zurück. Dieses
	 * beinhaltet alle in diesem Panel gezeichneten Elemente.
	 * 
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics g2;
		if (this.image == null || this.image.getWidth() != this.getWidth()
				|| this.image.getHeight() != this.getHeight()) {
			this.image = this.gfxConf.createCompatibleImage(this.getWidth(),
					this.getHeight());
		}
		g2 = this.image.getGraphics();

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (this.temp != null) {
			if (this.temp instanceof StateObject) {
				this.panel.getFsmProgram().updateStatePositionInStateList(
						(StateObject) this.temp);
			} else if (this.temp instanceof CommentObject) {
				this.panel.getFsmProgram().updateStatePositionInCommentList(
						(CommentObject) this.temp);
			}
		}
		if (this.tmpState != null) {
			this.panel.getFsmProgram().updateStatePositionInStateList(
					this.tmpState);
		}
		if (this.tempStateList != null) {
			for (StateObject state : this.tempStateList) {
				this.panel.getFsmProgram()
						.updateStatePositionInStateList(state);
			}
		}
		for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
			state.render(g2);
		}
		for (CommentObject comment : this.panel.getFsmProgram()
				.getAllComments()) {
			comment.render(g2);
		}
		if (this.tmpTransition != null) {
			this.tmpTransition.render(g2);
		}
		g.drawImage(this.image, 0, 0, null);
	}

	/**
	 * Setzt den vom Input-Field erhaltenen Text für ein Element als Name
	 * (Zustand) oder als Text(Kommentar).
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.setInputFieldValueForObject();
	}

	/**
	 * Löscht das Input-Field ohne einen Kommentar zu erstellen bzw. modifieren
	 * oder Zustand umzubenennen. Dafür muss das FsmAutomataPanel wieder frei
	 * gegeben werden uns bestimmte temporäre Objekte gelöscht werden.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.resetHoverField();
			this.setNameForEditableObjects(null, this.inputField.getX(),
					this.inputField.getY());
			this.inputField.removeActionListener(this);
			this.remove(this.inputField);
			this.inputField = null;
			this.refresh();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Setzt den Inhalt des Eingabefeldes ggf. für ein Objekt
	 */
	public void setInputFieldValueForObject() {
		if (this.inputField != null) {
			this.resetHoverField();
			this.setNameForEditableObjects(this.inputField.getText(),
					this.inputField.getX(), this.inputField.getY());
			this.resetInputField();
		}
	}

	/**
	 * Setzt den Menü-Modus.
	 * 
	 * @param mode
	 *            Modus in dem sich das Menü befindet.
	 */
	public void setModeType(FsmMenuMode mode) {
		this.menuMode = mode;
	}

	/**
	 * Setzt einen Klick im FsmAutomataPanel um. Dabei handelt es sich nur um
	 * Klicks, bei denen einer der Menüpunkte ungleich des EditModus vorher
	 * ausgewählt wurde.
	 * 
	 * @param e
	 *            MouseEvent, aus dem die Position de Klicks ausgelesen werden
	 *            kann.
	 */
	public void doClickMode(MouseEvent e) {
		this.resetHoverField();
		switch (this.menuMode) {
		case deleteMode:
			boolean hasDeleted = false;
			ListIterator<StateObject> iterator = this.panel
					.getFsmProgram()
					.getAllStates()
					.listIterator(
							this.panel.getFsmProgram().getAllStates().size());
			while (iterator.hasPrevious()) {
				StateObject state = iterator.previous();
				FsmObject tmp = state.isClicked(e.getX(), e.getY());
				if (tmp != null) {
					this.panel.deleteObject(tmp);
					this.setModeType(FsmMenuMode.editMode);
					this.panel.setEditModeSelected();
					hasDeleted = true;
					break;
				}
			}
			if (hasDeleted == false) {
				ListIterator<CommentObject> commentIterator = this.panel
						.getFsmProgram()
						.getAllComments()
						.listIterator(
								this.panel.getFsmProgram().getAllComments()
										.size());
				while (commentIterator.hasPrevious()) {
					CommentObject comment = commentIterator.previous();
					if (comment.isClickedOn(e.getX(), e.getY())) {
						this.panel.deleteComment(comment);
						this.setModeType(FsmMenuMode.editMode);
						this.panel.setEditModeSelected();
						break;
					}
				}
			}
			break;
		case createStateMode:
			this.panel.createState(e.getX(), e.getY());
			this.setModeType(FsmMenuMode.editMode);
			this.panel.setEditModeSelected();
			break;
		case markFinalStateMode:
			for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
				if (state.isClickedOn(e.getX(), e.getY())) {
					this.panel.modifyState(state, state.isInitial(), true,
							state.getXCoordinate(), state.getYCoordinate());
					this.setModeType(FsmMenuMode.editMode);
					this.panel.setEditModeSelected();
					break;
				}
			}
			break;
		case markStartStateMode:
			for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
				if (state.isClickedOn(e.getX(), e.getY())) {
					this.panel.modifyState(state, true, false,
							state.getXCoordinate(), state.getYCoordinate());
					this.setModeType(FsmMenuMode.editMode);
					this.panel.setEditModeSelected();
					break;
				}
			}
			break;
		case createCommentMode:
			this.setLocked(true);
			this.setInputField(null, e.getX(), e.getY());
			break;
		case editMode:
			if (this.tmpBendTransition != null) {
				this.tmpBendTransition.highlight(false);
				this.tmpBendTransition.markTransition(false);
			}
			this.refresh();
			this.tmpBendTransition = null;
		default:
			break;
		}
	}

	/**
	 * Setzt einen Doppelklick im FsmAutomataPanel um. Dabei handelt es sich nur
	 * um Doppelklicks, bei denen der Menüpunkt des Editierens vorher
	 * ausgewählt wurde.
	 * 
	 * @param e
	 *            MouseEvent, aus dem die Position de Klicks ausgelesen werden
	 *            kann.
	 */
	public void doDoubleClickMode(MouseEvent e) {
		this.resetHoverField();
		if (this.menuMode == FsmMenuMode.editMode) {
			for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
				this.temp = state.isClicked(e.getX(), e.getY());
				if (this.temp != null) {
					if (this.temp instanceof StateObject) {
//						this.temp = this.temp;
						this.setLocked(true);
						this.setInputField(this.temp.getName(), e.getX(),
								e.getY());
						break;
					} else if (this.temp instanceof VoidObject
							|| this.temp instanceof InputObject
							|| this.temp instanceof OutputObject
							|| this.temp instanceof TransitionDescriptionObject) {
						if (this.temp instanceof VoidObject) {
							this.temp = ((FsmObject) this.temp)
									.getParentRenderable()
									.getParentRenderable();
						} else if (this.temp instanceof InputObject) {
							while (!(((InputObject) this.temp)
									.getParentRenderable() instanceof TransitionDescriptionObject)) {
								this.temp = ((InputObject) this.temp)
										.getParentRenderable();
							}
							this.temp = ((FsmObject) this.temp)
									.getParentRenderable();
						} else if (this.temp instanceof OutputObject) {
							this.temp = ((OutputObject) this.temp)
									.getParentRenderable();
						} else {
//							this.temp = this.temp;
						}
						TransitionDescriptionDialog
								.createTransitionDescriptionDialog(this,
										(TransitionDescriptionObject) this.temp);
						this.temp = null;
						break;
					} else if (this.temp instanceof TransitionObject) {
						this.tmpBendTransition = (TransitionObject) this.temp;
						this.temp = null;
						boolean isMarked = this.tmpBendTransition
								.markTransition(true);
						if (isMarked) {
							this.tmpBendTransition.highlight(true);
							this.refresh();
						} else {
							this.tmpBendTransition = null;
						}

					}
					break;
				}
			}
			if (this.temp == null && this.tmpBendTransition == null) {
				for (CommentObject comment : this.panel.getFsmProgram()
						.getAllComments()) {
					if (comment.isClickedOn(e.getX(), e.getY())) {
						this.temp = comment;
						this.setLocked(true);
						this.setInputField(this.temp.getName(), e.getX(),
								e.getY());
						break;
					}
				}
			}
		}
	}

	/**
	 * Markiert einen Zustand bzw. Kommentar schon beim Anklicken im EditMode
	 * 
	 * @param startX
	 *            X-Koordinate des zu überprüfenden Punktes auf Kommentar bzw.
	 *            Zustand
	 * @param startY
	 *            Y-Koordinate des zu überprüfenden Punktes auf Kommentar bzw.
	 *            Zustand
	 */
	public Point doPressMode(int startX, int startY) {
		this.resetHoverField();
		switch (this.menuMode) {
		case editMode:
			if (this.temp == null && this.tmpTransition == null
					&& this.tempStateList == null) {
				for (StateObject state : this.panel.getFsmProgram()
						.getAllStates()) {
					if (state.isClickedOn(startX, startY)) {
						this.temp = state;
						this.temp.highlight(true);
						this.panel.modified();
						this.refresh();
						return new Point(startX - this.temp.getXCoordinate(),
								startY - this.temp.getYCoordinate());
					}
				}
				for (CommentObject comment : this.panel.getFsmProgram()
						.getAllComments()) {
					if (comment.isClickedOn(startX, startY)) {
						this.temp = comment;
						this.temp.highlight(true);
						this.panel.modified();
						this.refresh();
						return new Point(startX - this.temp.getXCoordinate(),
								startY - this.temp.getYCoordinate());
					}
				}
			}
		}
		return null;
	}

	/**
	 * Setzt einen Ziehen im FsmAutomataPanel um. Dabei handelt es sich um
	 * Ziehbewegungen, die entweder für die Erzeugung von Transitionen im
	 * entsprechenden Modus oder im Editiermodus vollzogen werden.
	 * 
	 * @param startX
	 *            x-Koordinates des Startziehpunktes
	 * @param startY
	 *            y-Koordinates des Startziehpunktes
	 * @param x
	 *            x-Koordinates des Endziehpunktes
	 * @param y
	 *            y-Koordinates des Endziehpunktes
	 * @return
	 */
	public boolean doDragMode(int startX, int startY, int x, int y) {
		this.resetHoverField();
		switch (this.menuMode) {
		case createTransitionMode:
			if (this.tmpTransition == null) {
				StateObject fromState = null;
				for (StateObject state : this.panel.getFsmProgram()
						.getAllStates()) {
					if (state.isClickedOn(startX, startY)) {
						fromState = state;
						break;
					}
				}
				if (fromState != null) {
					this.tmpTransition = new TransitionObject(fromState, null);
					this.tmpTransition.setEndPoint(x, y);
					for (StateObject state : this.panel.getFsmProgram()
							.getAllStates()) {
						if (state.isClickedOn(x, y)) {
							this.temp = state;
							this.temp.highlight(true);
							break;
						}
					}
					this.refresh();
					return true;
				}
			} else {
				this.tmpTransition.setEndPoint(x, y);
				if (this.temp != null) {
					if (!this.temp.isClickedOn(x, y)) {
						this.temp.highlight(false);
						this.temp = null;
					}
				}
				if (this.temp == null) {
					for (StateObject state : this.panel.getFsmProgram()
							.getAllStates()) {
						if (state.isClickedOn(x, y)) {
							this.temp = state;
							this.temp.highlight(true);
							break;
						}
					}
				}
				this.refresh();
				return true;
			}
			return false;
		case editMode:
			if (this.temp == null && this.tmpTransition == null
					&& this.tempStateList == null
					&& this.tmpBendTransition == null) {
				for (StateObject state : this.panel.getFsmProgram()
						.getAllStates()) {
					for (FsmObject transition : state.getChilds()) {
						if (((TransitionObject) transition).isArrowHeadClicked(
								x, y)) {
							this.tmpTransition = (TransitionObject) transition;
							this.tmpState = this.tmpTransition.getToState();
							this.tmpTransition.setToState(null);
							this.tmpTransition.setEndPoint(x, y);
							for (StateObject toState : this.panel
									.getFsmProgram().getAllStates()) {
								if (toState.isClickedOn(x, y)) {
									this.tmpStateHighlight = state;
									this.tmpStateHighlight.highlight(true);
									break;
								}
							}
							this.refresh();
							return true;
						}
					}
				}
			} else {
				if (this.temp != null && this.tempStateList == null) {
					this.temp.setCoordinates(x, y);
					this.refresh();
					return true;
				} else if (this.tmpBendTransition != null) {
					if (this.tmpBendTransition.isClickedOnBendPoint(startX,
							startY)) {
						this.tmpBendTransition.setBendPoint(x, y);
					}
					this.refresh();
					return true;
				} else if (this.tmpTransition != null) {
					this.tmpTransition.setEndPoint(x, y);
					if (this.tmpStateHighlight != null) {
						if (!this.tmpStateHighlight.isClickedOn(x, y)) {
							this.tmpStateHighlight.highlight(false);
							this.tmpStateHighlight = null;
						}
					}
					if (this.tmpStateHighlight == null) {
						for (StateObject toState : this.panel.getFsmProgram()
								.getAllStates()) {
							if (toState.isClickedOn(x, y)) {
								this.tmpStateHighlight = toState;
								this.tmpStateHighlight.highlight(true);
								break;
							}
						}
					}
					this.refresh();
					return true;
				} else if (this.tempStateList != null) {
					int xOffset = 0;
					int yOffset = 0;
					for (StateObject state : this.tempStateList) {
						if (state.isClickedOn(startX, startY)) {
							xOffset = x - startX;
							yOffset = y - startY;
							break;
						}
					}
					for (StateObject state : this.tempStateList) {
						state.setCoordinates(state.getXCoordinate() + xOffset,
								state.getYCoordinate() + yOffset);
					}
					this.refresh();
					return true;
				}

			}
			return false;
		default:
			return false;
		}
	}

	/**
	 * Setzt ein Loslassen im FsmAutomataPanel um. Dabei handelt es sich um
	 * Loslass- Bewegungen, die entweder für die Erzeugung von Transitionen im
	 * entsprechenden Modus oder im Editiermodus vollzogen werden.
	 * 
	 * @param startX
	 *            x-Koordinates des Startziehpunktes
	 * @param startY
	 *            y-Koordinates des Startziehpunktes
	 * @param x
	 *            x-Koordinates des Endziehpunktes
	 * @param y
	 *            y-Koordinates des Endziehpunktes
	 */
	public void doDropMode(int startX, int startY, int x, int y) {
		this.resetHoverField();
		switch (this.menuMode) {
		case createTransitionMode:
			if (this.tmpTransition != null) {
				if (this.tmpTransition.getParentRenderable() != null
						&& this.temp != null) {
					this.tmpTransition.setToState((StateObject) this.temp);
					TransitionObject transition = null;
					for (FsmObject tmp : this.tmpTransition
							.getParentRenderable().getChilds()) {
						transition = (TransitionObject) tmp;
						if (transition.getToState().equals(this.temp)) {
							this.tmpTransition = transition;
							break;
						}
					}
					this.temp.highlight(false);
					TransitionDescriptionObject tdo = new TransitionDescriptionObject(
							this.tmpTransition);
					this.tmpTransition.add(tdo);
					TransitionDescriptionDialog
							.createTransitionDescriptionDialog(
									this,
									(TransitionDescriptionObject) this.tmpTransition
											.getChilds().getLast());
				}
				this.refresh();
			}
			break;
		case editMode:
			if (this.temp != null && this.tempStateList == null) {
				this.temp.setCoordinates(x, y);
				this.temp.highlight(false);
				this.refresh();
				break;
			} else if (this.tmpBendTransition != null) {
				if (this.tmpBendTransition.isClickedOnBendPoint(startX, startY)) {
					this.tmpBendTransition.setBendPoint(x, y);
				}
			} else if (this.tmpTransition != null && this.tmpState != null) {
				if (this.tmpStateHighlight != null) {
					if (this.tmpStateHighlight.isClickedOn(x, y)) {
						this.tmpTransition.setToState(this.tmpStateHighlight);
						this.panel.modified();
					}
					this.tmpStateHighlight.highlight(false);
				} else {
					this.tmpTransition.setToState(this.tmpState);
				}
				this.refresh();
				break;
			} else if (this.tempStateList != null) {
				int xOffset = 0;
				int yOffset = 0;
				for (StateObject state : this.tempStateList) {
					if (state.isClickedOn(startX, startY)) {
						xOffset = x - startX;
						yOffset = y - startY;
						break;
					}
				}
				for (StateObject state : this.tempStateList) {
					state.setCoordinates(state.getXCoordinate() + xOffset,
							state.getYCoordinate() + yOffset);
					state.highlight(false);
				}
				this.refresh();
				break;
			}
			break;
		default:
			break;
		}
		this.tmpTransition = null;
		this.tmpState = null;
		this.tmpStateHighlight = null;
		this.temp = null;
		this.tempStateList = null;
	}

	/**
	 * Fügt, wenn angeklickt, einen Zustand zu der Liste der angeklickten
	 * Zustände hinzu.
	 * 
	 * @param x
	 *            X-Koordinate, auf die geklickt wurde.
	 * @param y
	 *            Y-Koordinate, auf die geklickt wurde.
	 */
	public void addObjectsToMoveObjects(int x, int y) {
		if (this.temp != null) {
			this.temp.highlight(false);
			this.temp = null;
		}
		this.resetHoverField();
		if (this.menuMode == FsmMenuMode.editMode) {
			if (this.tempStateList == null) {
				this.tempStateList = new LinkedList<StateObject>();
			}
			for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
				if (state.isClickedOn(x, y)) {
					this.tempStateList.add(state);
					state.highlight(true);
				}
			}
		}
	}

	/**
	 * Setzt den Namen bzw. den Text für in diesem Bezug editierbare Elemente
	 * neu.
	 * 
	 * @param text
	 *            Der neue Text, der gesetzt werden soll.
	 * @param x
	 *            die X-Koordinate, an dem das Element gesetzt werden soll, wenn
	 *            noch nicht geschehen.
	 * @param y
	 *            die Y-Koordinate, an dem das Element gesetzt werden soll, wenn
	 *            noch nicht geschehen.
	 */
	public void setNameForEditableObjects(String text, int x, int y) {
		this.resetHoverField();
		switch (this.menuMode) {
		case editMode:
			if (text == null || text.equals("")) {
				this.temp = null;
				this.setLocked(false);
				break;
			} else if (this.temp != null && this.temp instanceof StateObject) {
				if (this.temp.getName().equals(text)) {
					this.temp = null;
					this.setLocked(false);
				} else if (text.matches("z[0-9]+")) {
					this.temp = null;
					this.setLocked(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null,
									"Ein Zustand darf nicht mit z+<Zahl>"
											+ " benannt werden!",
									"Endlicher Automat-Exception",
									JOptionPane.ERROR_MESSAGE, null);
						}
					});
				} else if (!this.validName(text)) {
					this.temp = null;
					this.setLocked(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane
									.showMessageDialog(
											null,
											"Der Name enth�lt ung�ltige Zeichen!\nErlaubt sind nur Buchstaben und Ziffern!",
											"Endlicher Automat-Exception",
											JOptionPane.ERROR_MESSAGE, null);
						}
					});
				} else if (this.alreadyExists(text)) {
					this.temp = null;
					this.setLocked(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane
									.showMessageDialog(
											null,
											"Ein anderer Zustand hat bereits denselben Namen!",
											"Endlicher Automat-Exception",
											JOptionPane.ERROR_MESSAGE, null);
						}
					});
				} else {
					this.temp.setName(text);
					this.temp = null;
					this.panel.modified();
					this.refresh();
					this.setLocked(false);
				}
			} else if (this.temp != null && this.temp instanceof CommentObject) {
				this.temp.setName(text);
				this.temp = null;
				this.panel.modified();
				this.refresh();
				this.setLocked(false);
			} else {
				this.panel.createComment(text, x, y);
			}
			break;
		case createCommentMode:
			if (text != null && !text.equals("")) {
				this.panel.createComment(text, x, y);
			}
			this.setLocked(false);
			this.setModeType(FsmMenuMode.editMode);
			this.panel.setEditModeSelected();
			break;
		default:
			break;
		}
	}

	private boolean validName(String str) {
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!Character.isLetterOrDigit(ch)) {
				return false;
			}
		}
		return true;
	}

	private boolean alreadyExists(String str) {
		CopyOnWriteArrayList<StateObject> list = this.panel.getFsmProgram()
				.getAllStates();
		for (StateObject obj : list) {
			if (obj.getName().equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Setzt das temporäre Objekt von außen, z.B. in Handlern.
	 * 
	 * @param temp
	 *            temporäres Object, welches gesetzt werden soll.
	 */
	public void setTempObject(RenderableObject temp) {
		this.temp = temp;
	}

	/**
	 * Methode um dieses Panel neu zu zeichnen.
	 */
	@Override
	public void updateObject() {
		this.refresh();
	}

	/**
	 * Methode, die neben einem Repaint auch noch beachtet, ob Scrollbars
	 * gegebenenfalls erforderlich sind.
	 */
	public void refresh() {
		this.updateBounds();
		this.repaint();
	}

	/**
	 * Überprüft, ob ScrollBars benötigt werden und legt diese ggf. fest.
	 */
	public void updateBounds() {
		boolean changed = false;
		this.farthestX = 0;
		this.farthestY = 0;
		for (StateObject state : this.panel.getFsmProgram().getAllStates()) {
			this.getDistance(state);
			for (FsmObject transition : state.getChilds()) {
				for (FsmObject transitionDesc : transition.getChilds()) {
					this.getDistance(transitionDesc);
				}
			}
		}
		for (CommentObject comment : this.panel.getFsmProgram()
				.getAllComments()) {
			this.getDistance(comment);
		}
		if (this.farthestX > this.area.width) {
			this.area.width = this.farthestX; // dibo
			changed = true;
		}
		if (this.farthestY > this.area.height) {
			this.area.height = this.farthestY; // dibo
			changed = true;
		}
		if (changed) {
//		    JScrollBar verticalScrollBar   = this.panel.getScrollPane().getVerticalScrollBar();
//		    JScrollBar horizontalScrollBar = this.panel.getScrollPane().getHorizontalScrollBar();
//		    verticalScrollBar.setValue(area.height);
//		    horizontalScrollBar.setValue(area.width);
		    this.setPreferredSize(this.area);
			this.revalidate();
		}
	}

	/**
	 * Zur Berechnung der Werte, um das AutomataPanel mit ScrollBars zu
	 * zeichnen.
	 * 
	 * @param element
	 *            RenderableObject, für den diese Berechnung gerade
	 *            durchgeführt werden soll.
	 */
	private void getDistance(RenderableObject element) {
		if (this.farthestX < element.getXCoordinate() + element.getWidth()) {
			this.farthestX = element.getXCoordinate() + element.getWidth();
		}
		if (this.farthestY < element.getYCoordinate() + element.getHeight()) {
			this.farthestY = element.getYCoordinate() + element.getHeight();
		}
	}
}
