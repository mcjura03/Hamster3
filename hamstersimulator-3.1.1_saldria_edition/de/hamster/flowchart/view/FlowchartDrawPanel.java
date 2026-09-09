package de.hamster.flowchart.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.DecisionObject;
import de.hamster.flowchart.model.FlowchartAnchor;
import de.hamster.flowchart.model.FlowchartMethod;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.FlowchartTransition;
import de.hamster.flowchart.model.StartStopObject;

/**
 * Diese Klasse repräsentiert die Zeichenfläche innerhalb der Tabs vom
 * Flowchart-Programm.
 * 
 * @author gerrit
 * 
 */
public class FlowchartDrawPanel extends JPanel implements MouseInputListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7365194737074714285L;
	private FlowchartMethod method;
	private Dimension area;
	private BufferedImage ghostObject;
	private Point ghostPoint = new Point();
	private Point oldPoint;
	private FlowchartObject dragObject;
	private boolean internal;
	private Point diff = new Point();
	private FlowchartHamsterFile file;
	private CopyOnWriteArrayList<FlowchartTransition> tmpTransitionList;
	private FlowchartTransition tmpTransition;
	private FlowchartAnchor tmpAnchor;
	private Point tmpNode;
	private CommentObject dragComment;
	private Graphics graph;
	private Boolean dragObjektChanged = false;
	private FlowchartObject tmpO;

	/**
	 * FlowchartDrawPanel Konstruktor
	 * 
	 * @param m
	 *            Die Methode die dargestellt ist.
	 * @param isDoubleBuffered
	 * 
	 * @param file
	 *            Das Hamster-PAP-File
	 */
	public FlowchartDrawPanel(FlowchartMethod m, boolean isDoubleBuffered,
			FlowchartHamsterFile file) {
		super(isDoubleBuffered);
		this.method = m;
		this.setLayout(null);
		area = new Dimension(0, 0);
		this.setPreferredSize(area);
		this.setName(m.getName());
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.file = file;

	}

	/**
	 * Setzt die Transitionsliste, damit diverse Dinge gezeichnet werden können.
	 * 
	 * @param list
	 *            Die Liste mit den Transitionen.
	 */
	public void setTmpTransitionList(
			CopyOnWriteArrayList<FlowchartTransition> list) {
		this.tmpTransitionList = list;
	}

	public void removeTransition(FlowchartTransition t) {
		this.tmpTransitionList.remove(t);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		Graphics2D line = (Graphics2D) g;
		line.setStroke(new BasicStroke(3));

		CopyOnWriteArrayList<FlowchartObject> elements = method.getElemList();

		for (CommentObject comment : this.method.getCommentList()) {
			comment.draw(g);
		}

		for (FlowchartObject command : elements) {
			command.draw(g);
			updateBounds();
		}

		if (this.ghostObject != null && ghostPoint != null) {
			g.drawImage(this.ghostObject, ghostPoint.x, ghostPoint.y, null);
		}

		if (this.dragObject != null) {
			dragObject.draw(g);
		}

		if (tmpTransitionList != null) {
			for (FlowchartTransition t : this.tmpTransitionList) {
				if (this.method.getElemList().contains(t.getSourceObject())) {
					t.draw(g);
				}
			}
		} else {
			// aus irgend einem Grund war beim Erstellen dieses Panel die
			// Transitionsliste nicht verfügbar und muss nachgeladen werden.
			this.tmpTransitionList = this.file.getProgram().getController()
					.getTransitions();
		}

		if (tmpAnchor != null) {
			g.drawImage(FlowchartUtil.ANHCORIMG, tmpAnchor.x - 5,
					tmpAnchor.y - 5, null);
		}

		this.graph = g;

	}

	/**
	 * Gibt die Grafik zurück
	 * 
	 * @return die Grafik
	 */
	public Graphics getGraph() {
		return this.graph;
	}

	/**
	 * updating the bounds from the DrapPanel TODO : updateBounds nicht fertig
	 */
	private void updateBounds() {
		boolean changed = false;
		int tmpX = 0;
		int tmpY = 0;
		for (FlowchartObject object : method.getElemList()) {
			if ((object.getX() + object.getWidth()) > tmpX)
				tmpX = object.getX() + object.getWidth();
			if ((object.getY() + object.getHeight() + 5) > tmpY)
				tmpY = object.getY() + object.getHeight() + 5;
		}

		if (tmpX > area.width) {
			area.width = tmpX;
			changed = true;
		}
		if (tmpY > area.height) {
			area.height = tmpY;
			changed = true;
		}

		if (changed) {
			setPreferredSize(area);
			revalidate();
		}

	}

	/**
	 * 
	 * @param mouseEvent
	 * @param flowchartElement
	 * @param diff
	 */
	public void dragFlowchartObject(MouseEvent mouseEvent,
			FlowchartObject flowchartElement, Point diff) {
		if (this.ghostObject == null) {
			setGhostObject(flowchartElement);
		}

		// cloning the object because coordinates inside are different from
		// glassPane
		this.dragObject = flowchartElement.clone();
		this.dragObject.setCoordinates(mouseEvent.getX() + diff.x,
				mouseEvent.getY() + diff.y);

		this.ghostPoint.setLocation(this.getGridPoint(mouseEvent.getX()
				+ diff.x, mouseEvent.getY() + diff.y));
	}

	public void dragFlowchartObjectInternal(Point point) {
		if (this.ghostObject == null) {
			setGhostObject(this.dragObject);
		}

		this.dragObject.setCoordinates(point.x + diff.x, point.y + diff.y);

		this.ghostPoint.setLocation(this.getGridPoint(point.x + diff.x, point.y
				+ diff.y));

	}

	private void setGhostObject(FlowchartObject flowchartElement) {
		if (!(flowchartElement instanceof DecisionObject)
				&& !(flowchartElement instanceof StartStopObject)) {
			this.ghostObject = FlowchartUtil
					.getImage("flowchart/ghost_rect.png");
		} else if (flowchartElement instanceof StartStopObject) {
			this.ghostObject = FlowchartUtil
					.getImage("flowchart/ghost_rectround.png");
		} else {
			this.ghostObject = FlowchartUtil
					.getImage("flowchart/ghost_diam.png");
		}
	}

	public Point getGridPoint(int i, int j) {
		Point tmpP = new Point(i, j);
		if (tmpP.x < 0) {
			tmpP.x = 0;
		} else if (tmpP.x % 150 > 0) {
			tmpP.x += 75;
			tmpP.x = tmpP.x / 150 * 150;
		}

		if (tmpP.y < 0) {
			tmpP.y = 0;
		} else if (tmpP.y % 100 > 0) {
			tmpP.y += 50;
			tmpP.y = tmpP.y / 100 * 100;
		}

		tmpP.x += 25;
		tmpP.y += 25;
		return tmpP;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			for (CommentObject comment : this.method.getCommentList()) {
				if (e.getPoint().x >= comment.getX()
						&& e.getPoint().x <= comment.getX() + 90
						&& e.getPoint().y >= comment.getY()
						&& e.getPoint().y <= comment.getY() + 60) {
					comment.getTextBox().setVisible(true);
					comment.getTextBox().requestFocus();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (internal && dragObject != null && oldPoint != null) {
			dragObject.setCoordinates(oldPoint);
			internal = false;
		}
		this.ghostObject = null;
		this.dragObject = null;
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {

		for (CommentObject c : this.method.getCommentList()) {
			if (e.getX() >= c.x && e.getX() <= c.x + 90 && e.getY() >= c.y
					&& e.getY() <= c.y + 60) {
				dragComment = c;
				diff.setLocation(c.x - e.getX(), c.y - e.getY());
			}
		}

		if (!FlowchartUtil.TRANSITIONMODE) {
			dragObject = findFlowchartObject(e.getPoint());

			// prevent dragObject from changing position when clicking on it
			if (dragObject != null) {
				oldPoint = new Point(dragObject.x, dragObject.y);
				ghostPoint.setLocation(dragObject.x, dragObject.y);
			}
			internal = true;

		} else {

			for (FlowchartTransition t : this.file.getProgram().getController()
					.getTransitions()) {
				this.tmpNode = t.getNodeAtMouse(e.getPoint());
				if (tmpNode != null)
					break;
			}

			// find object where clicked
			tmpO = findFlowchartObjectAtRadius(e.getPoint(), 10);
			
		}
	}

	/**
	 * Sucht ein FlowchartObjekt am übergebenen Punkt und gibt dieses dann
	 * zurück
	 * 
	 * @param p
	 *            Der Punkt an dem gesucht werden soll
	 * @return Das FlowchartObjekt, wenn eins gefunden wurde, null sonst.
	 */
	private FlowchartObject findFlowchartObject(Point p) {
		for (FlowchartObject o : this.method.getElemList()) {
			if (p.x >= o.getX() && p.x <= o.getX() + 90 && p.y >= o.getY()
					&& p.y <= o.getY() + 60) {
				diff.setLocation(o.getX() - p.x, o.getY() - p.y);
				return o;
			}
		}

		return null;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (dragComment != null && FlowchartUtil.TRANSITIONMODE) {
			dragComment.setRelatedObject(findFlowchartObject(e.getPoint()));
			dragComment.setTmpPoint(null);
			this.file.setModified(true);
		}

		place_object: if (dragObject != null) {

			// überprüfen ob bereits ein anderes Objekt an der Position vom
			// ghostPoint liegt
			// wenn ja, dann bewege das dragObject wieder zurück an den alten
			// Ort
			for (FlowchartObject o : method.getElemList()) {
				if (!o.equals(dragObject) && o.getX() == ghostPoint.x
						&& o.getY() == ghostPoint.y) {
					dragObject.setCoordinates(this.oldPoint);
					break place_object;
				}
			}
			dragObject.setCoordinates(this.ghostPoint);

			file.setModified(true);
		}

		if (this.dragObject != null && dragObjektChanged
				&& oldPoint.x != this.dragObject.x
				&& oldPoint.y != this.dragObject.y) {
			for (FlowchartTransition t : this.file.getProgram().getController()
					.getTransitions()) {
				if (t.getSourceObject().equals(this.dragObject)) {
					t.resetNodes();
				}
				if (t.getDestinationObject().equals(this.dragObject)) {
					t.resetNodes();
				}
			}
		}
		this.dragObjektChanged = false;
		this.ghostObject = null;
		this.dragComment = null;
		this.dragObject = null;
		this.internal = false;
		this.repaint();

		if (e.getButton() == 3) {
			FlowchartObject tmp = findFlowchartObject(e.getPoint());
			if (tmp != null) {
				FlowchartChoicePopup popup = new FlowchartChoicePopup(tmp,
						method, new Point(e.getX(), e.getY()), this, this.file,
						true);
				popup.show(this, e.getX(), e.getY());

				file.setModified(true);
			}

			for (CommentObject c : this.method.getCommentList()) {
				if (e.getX() >= c.x && e.getX() <= c.x + 90 && e.getY() >= c.y
						&& e.getY() <= c.y + 60) {
					CommentObject tmpComment = c;
					FlowchartChoicePopup popup = new FlowchartChoicePopup(
							tmpComment, method, new Point(e.getX(), e.getY()),
							this, this.file, false);
					popup.show(this, e.getX(), e.getY());
				}
			}
		}

		// reset tmpTransition
		if (tmpTransition != null) {

			// sucht ein FlowchartObject an dem Punkt wo die Transition
			// losgelassen wurde.
			FlowchartObject tmpO = findFlowchartObjectAtRadius(e.getPoint(), 10);
			if (tmpO != null && tmpAnchor != null) {
				if (!(tmpTransition.getSourceObject() instanceof DecisionObject)) {
					tmpTransition.setDestinationObject(tmpO,
							tmpAnchor.getOrientation(), true);
				} else {
					(new DecistionTransionPopup(tmpO, tmpTransition,
							tmpTransitionList, this, tmpAnchor.getOrientation()))
							.show(this, e.getX(), e.getY());
				}
				// file.getProgram().
				file.setModified(true);
			} else {
				tmpTransitionList.remove(tmpTransition);
			}

			tmpTransition = null;
			this.repaint();
		}

		this.tmpNode = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.ghostObject != null) {
			this.repaint();
		}

		if (this.dragObject != null && internal) {
			dragFlowchartObjectInternal(e.getPoint());
			dragObjektChanged = true;
		}

		if (this.dragComment != null) {
			if (!FlowchartUtil.TRANSITIONMODE) {
				this.dragComment.setCoordinates(new Point(e.getX() + diff.x, e
						.getY() + diff.y));
				this.dragComment.getTextBox().updateLocation();
			} else {
				this.dragComment.setTmpPoint(e.getPoint());
			}
			this.repaint();
		}

		
		if (tmpO != null && tmpAnchor != null && tmpNode == null) {
			if (!(tmpO instanceof DecisionObject)) {
				// important! remove TrueChild
				tmpO.setTrueChild(null);

				// search if there is already a transition at this object
				for (FlowchartTransition t : this.tmpTransitionList) {
					if (t.getSourceObject().getId() == tmpO.getId()) {
						// found a transition
						this.tmpTransition = t;
						this.tmpTransitionList.remove(t);
						t.removeDestinationObject();
						// break;
					}
				}

				// not found any transition -> create new
				this.tmpTransition = new FlowchartTransition(tmpO,
						tmpAnchor.getOrientation());

			} else {
				// neue Transition erstellen
				this.tmpTransition = new FlowchartTransition(tmpO,
						tmpAnchor.getOrientation());

				// die bestehende Transition muss beim MouseReleased
				// ausgetauscht werden.
			}

			tmpTransitionList.add(tmpTransition);
			this.tmpO = null;

		}
		
		
		if (tmpTransition != null && tmpNode == null) {

			FlowchartObject tmpFound = findFlowchartObjectAtRadius(
					e.getPoint(), 10);

			if (tmpFound != null) {
				tmpAnchor = tmpFound.getAnchorAtMouse(e.getPoint());
				if (tmpAnchor != null) {
				}
			} else {
				tmpAnchor = null;
			}

			tmpTransition.updateTmpDestinationPoint(e.getPoint());
			this.repaint();
		}

		// setzt die Koordinaten des Knoten neu
		if (this.tmpNode != null) {
			// tmpNode.x = e.getX();
			// tmpNode.y = e.getY();
			tmpNode.setLocation(e.getX(), e.getY());
			file.setModified(true);
			this.repaint();
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if (FlowchartUtil.TRANSITIONMODE) {
			FlowchartObject tmpFound = null;

			tmpFound = findFlowchartObjectAtRadius(e.getPoint(), 10);

			if (tmpFound != null) {
				tmpAnchor = tmpFound.getAnchorAtMouse(e.getPoint());
				if (tmpAnchor != null) {
				}
			} else {
				tmpAnchor = null;
			}
			this.repaint();
		}
	}

	/**
	 * Findet ein FlowchartObjekt an einem übergebenen Punkt innerhalb eines
	 * Radius.
	 * 
	 * @param p
	 *            Der Punkt an dem zentral gesucht werden soll.
	 * @param radius
	 *            Der Radius in dem das Objekt gesucht werden soll.
	 * @return Das FlowchartObject, wenn eins gefunden worden ist, null sonst.
	 */
	private FlowchartObject findFlowchartObjectAtRadius(Point p, int radius) {
		FlowchartObject tmpFoo = null;
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				tmpFoo = findFlowchartObject(new Point(p.x + i, p.y + j));
				if (tmpFoo != null)
					break;
			}
			if (tmpFoo != null)
				break;
		}
		return tmpFoo;
	}

}

/**
 * Popup Menu für das Festlegen der Transitionen bei DecistionObjecten
 * 
 * @author gerrit
 * 
 */
class DecistionTransionPopup extends JPopupMenu implements
		PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8070250426669398620L;
	private JMenuItem trueEntry;
	private JMenuItem falseEntry;
	private FlowchartObject destObject;
	private FlowchartTransition tmpTransition;
	private CopyOnWriteArrayList<FlowchartTransition> tmpTransitionList;
	private FlowchartDrawPanel panel;

	/**
	 * Der Konstruktor
	 * 
	 * @param destO
	 *            das Ziel-Objekt
	 * @param tmpT
	 *            die aktuell gezeichnete Transition
	 * @param tList
	 *            die Liste aller Transitionen
	 * @param panelT
	 *            das Panel, damit man es neu zeichnen kann
	 */
	public DecistionTransionPopup(FlowchartObject destO,
			FlowchartTransition tmpT,
			CopyOnWriteArrayList<FlowchartTransition> tList,
			FlowchartDrawPanel panelT, final int destOrientation) {
		super();

		this.destObject = destO;
		this.tmpTransition = tmpT;
		this.tmpTransitionList = tList;
		this.panel = panelT;
		this.addPropertyChangeListener(this);

		trueEntry = new JMenuItem("ja");  // dibo
		falseEntry = new JMenuItem("nein"); // dibo

		trueEntry.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FlowchartTransition t : tmpTransitionList) {
					if (t.getSourceObject() != null
							&& tmpTransition.getSourceObject() != null
							&& tmpTransitionList != null
							&& t.hasTrueChild != null)
						if (t.getSourceObject().equals(
								tmpTransition.getSourceObject())
								&& t.hasTrueChild) {
							t.setSourceObject(null);
							tmpTransitionList.remove(t);
						}
				}
				tmpTransition.setDestinationObject(destObject, destOrientation,
						true);
				panel.repaint();

			}
		});

		falseEntry.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FlowchartTransition t : tmpTransitionList) {
					if (t.getSourceObject() != null
							&& tmpTransition.getSourceObject() != null
							&& tmpTransitionList != null
							&& t.hasTrueChild != null)
						if (t.getSourceObject().equals(
								tmpTransition.getSourceObject())
								&& !t.hasTrueChild) {
							t.setSourceObject(null);
							tmpTransitionList.remove(t);
						}
				}
				tmpTransition.setDestinationObject(destObject, destOrientation,
						false);
				panel.repaint();
			}
		});

		this.add(trueEntry);
		this.add(falseEntry);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Boolean closed = null;
		if (evt.getPropertyName().equals("visible"))
			closed = (Boolean) evt.getNewValue();
		if (evt.getPropertyName().equals("visible") && !closed) {

			// Timer wird benötigt weil das PropertyChangeEvent vor den Actions
			// vom MenuEntry ausgeführt wird.
			Timer timer = new Timer("");
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (tmpTransition.getDestinationObject() == null) {
						tmpTransition.setSourceObject(null);
						panel.repaint();
						panel.removeTransition(tmpTransition);
					}
				}
			}, 100);

		}
	}

}