package de.hamster.scratch;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hamster.scratch.elements.voids.FunctionResultException;

/**
 * Die Klasse Renderable ist das Herzstück des Programms, denn diese beschreibt
 * die einzelnen Elemente, die verschoben und zusammengesteckt werden können.
 * Die Klasse bietet hilfreiche Methoden, um erfolgreich zu entscheiden, ob bei
 * einem Mausklick dieses Objekt getroffen wurde, oder beim verschieben dieses
 * Objekt an ein anderes angedockt werden kann. Desweiteren bietet es
 * verschiedene Andockmethoden, die sofort veranlasst die Vaterelemente und
 * Kinderelemente neu zu berechnen und neu zu rendern.
 * 
 * @author HackZ
 * 
 */
public abstract class Renderable {
	/**
	 * Der Typ beschreibt, von welchem Typ eine Methode ist. Boolean, Numeric
	 * oder Void. Dies ist hilfreich, da void-Methoden nur an void-Methoden
	 * angehängt werden können und boolsche Ausdrücke nur in die dafür
	 * vorgesehenen Plätze passen müssen
	 * 
	 * @author HackZ
	 * 
	 */
	public enum RType {
		VOID {
			@Override
			public int getWidth() {
				return 0;
			}

			@Override
			public int getHeight() {
				return 0;
			}

			@Override
			public void render(Graphics g, int x, int y) {
				// hier soll nichts gemacht werden
			}
		},
		BOOLEAN {
			@Override
			public int getWidth() {
				return 25;
			}

			@Override
			public int getHeight() {
				return 13;
			}

			@Override
			public void render(Graphics g, int x, int y) {
				int w = getWidth();
				int h = getHeight() - 1;
				Color COLOR_PRIMARY = new Color(99, 166, 44);
				Color COLOR_DARK = new Color(68, 90, 51);
				Color COLOR_LIGHT = new Color(154, 218, 101);

				g.setColor(COLOR_PRIMARY);
				int[] xPoints = new int[8];
				int[] yPoints = new int[8];
				xPoints[0] = x;
				yPoints[0] = y + h / 2;
				xPoints[1] = x + h / 2;
				yPoints[1] = y;
				xPoints[2] = x + w - h / 2;
				yPoints[2] = y;
				xPoints[3] = x + w;
				yPoints[3] = y + h / 2;
				xPoints[4] = x + w;
				yPoints[4] = y + h - h / 2;
				xPoints[5] = x + w - h / 2;
				yPoints[5] = y + h;
				xPoints[6] = x + h / 2;
				yPoints[6] = y + h;
				xPoints[7] = x;
				yPoints[7] = y + h - h / 2;
				g.fillPolygon(xPoints, yPoints, 8);

				g.setColor(COLOR_LIGHT);
				g.drawLine(x, y + h - h / 2, x + h / 2, y + h);
				g.drawLine(x + h / 2, y + h, x + w - h / 2 - 1, y + h);
				g.drawLine(x + w - h / 2 - 1, y + h, x + w - 1, y + h - h / 2);
				g.drawLine(x + w - h / 2 - 2, y + h, x + w - 2, y + h - h / 2);
				g.drawLine(x + h / 2, y + h - 1, x + w - h / 2 - 1, y + h - 1);

				g.setColor(COLOR_DARK);
				g.drawLine(x, y + h / 2, x + h / 2, y);
				g.drawLine(x + h / 2, y, x + w - h / 2 - 1, y);
				g.drawLine(x + h / 2, y + 1, x + w - h / 2, y + 1);
				g.drawLine(x + 1, y + h / 2, x + h / 2, y + 1);
				g.drawLine(x + w - h / 2 - 1, y, x + w - 2, y + h / 2 - 1);
			}
		},
		NUMERIC {
			@Override
			public int getWidth() {
				return 20;
			}

			@Override
			public int getHeight() {
				return 10;
			}

			@Override
			public void render(Graphics g, int x, int y) {
				// noch nicht implementiert
			}
		};

		/**
		 * Liefert die Breite des leeren Parameterfeldes
		 * 
		 * @return
		 */
		abstract public int getWidth();

		/**
		 * Liefert die Höhe des leeren Parameterfeldes
		 * 
		 * @return
		 */
		abstract public int getHeight();

		/**
		 * Rendert ein leeres Parameterfeld
		 * 
		 * @param g
		 *            Das Graphics Objekt auf dem gerendert werden soll
		 * @param x
		 *            x-Koordinate, wo gerendert werden soll
		 * @param y
		 *            y-Koordinate, wo gerendert werden soll
		 */
		abstract public void render(Graphics g, int x, int y);
	}

	public static final String NEWLINE = "\r\n";
	public static final int SHADOW_SIZE = 5;
	public static Color DOCKING_HEIGHTLIGHT_COLOR = new Color(255, 255, 255,
			100);
	public static Color DOCKING_HEIGHTLIGHT_LINE_COLOR = new Color(255, 255,
			255, 200);
	public static Color SHADOW_COLOR = new Color(50, 50, 50, 128);
	public static Color ACTIVE_PRIMARY = new Color(194, 68, 19);
	public static Color ACTIVE_DARK = new Color(155, 48, 15);
	public static Color ACTIVE_LIGHT = new Color(209, 76, 21);
	public static Color ACTIVE_VLIGHT = new Color(237, 107, 54);

	protected ScratchHamster hamster = ScratchHamster.hamster;
	protected BoundingBox bounding;
	protected int maxChilds = 1;
	protected HashMap<Integer, DockingBox> dockings = new HashMap<Integer, DockingBox>();
	protected HashMap<Integer, Renderable> childs = new HashMap<Integer, Renderable>();
	protected Renderable parent;
	protected DockingBox parentDock = null;
	protected Renderable next;
	protected DockingBox nextDock = null;
	private boolean moveable = true;
	protected String name = "";
	protected String drawName = "";

	// dibo 13.08.2010
	public int getChildIndex(Renderable elem) {
		Set<Integer> ints = childs.keySet();
		for (int i : ints) {
			Renderable r = childs.get(i);
			if (r == elem) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Liefert die x-Koordinate des Renderables
	 * 
	 * @return
	 */
	public int getX() {
		return bounding.getX();
	}

	public void setX(int x) {
		bounding.setX(x);
	}

	/**
	 * Liefert die y-Koordinate des Renderables
	 * 
	 * @return
	 */
	public int getY() {
		return bounding.getY();
	}

	public void setY(int y) {
		bounding.setY(y);
	}

	/**
	 * Setzt den Namen des Renderables fest
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		this.drawName = name;
	}

	/**
	 * Liefert den Namen des Renderables ACHTUNG: Im Normallfall wird das
	 * Attribut name zurückgeliefert. Dieser wird auch verwendet, um das Objekt
	 * zu speichern. Sollte man etwas anderes anzeigen wollen, als gespeichert
	 * werden soll, so muss diese Methode überschrieben werden und einen festen
	 * Wert zurückliefern.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Liefert das nächste Element. Nur void-Elemente haben ein nächstes
	 * Element, was im Normallfall auch ein void-Element ist. Dies ist das
	 * Element, was am unteren Ende angedockt wird.
	 * 
	 * @return
	 */
	public Renderable getNext() {
		return next;
	}

	/**
	 * Liefert den Typ des Renderables, das über die Andockmöglichkeiten
	 * entscheidet
	 * 
	 * @return
	 */
	abstract public RType getType();

	/**
	 * Rendert das Renderable auf das übergebene Graphics Objekt
	 * 
	 * @param g
	 *            Graphics, auf dem gerendert werden soll
	 */
	abstract public void render(Graphics g);

	/**
	 * Rendert das Renderable als Schatten
	 * 
	 * @param g
	 *            Graphics, auf dem gerendert werden soll
	 */
	abstract public void renderShadow(Graphics g);

	private void renderDock(Graphics g, int x, int y, int w, int h) {
		g.setColor(DOCKING_HEIGHTLIGHT_COLOR);
		g.fillRect(x, y, w, h);

		g.setColor(DOCKING_HEIGHTLIGHT_LINE_COLOR);
		g.drawRect(x - 1, y - 1, w + 1, h + 1);
		g.drawLine(x - 2, y - 1, x - 2, y + h);
		g.drawLine(x + w + 1, y - 1, x + w + 1, y + h);
		g.drawLine(x - 1, y - 2, x + w, y - 2);
		g.drawLine(x - 1, y + h + 1, x + w, y + h + 1);
	}

	/**
	 * Highlighted den anvisierten Andockpunkt
	 * 
	 * @param g
	 *            Graphic Objekt auf dem gerendert werden soll
	 * @param dockIndex
	 *            Index des Andockpunktes
	 */
	void highlightDocking(Graphics g, int dockIndex) {
		if (dockIndex < 0 || dockIndex >= dockings.size())
			return;

		int x = bounding.getX() + dockings.get(dockIndex).getX();
		int y = bounding.getY() + dockings.get(dockIndex).getY();
		int w = dockings.get(dockIndex).getWidth();
		int h = dockings.get(dockIndex).getHeight();
		renderDock(g, x, y, w, h);
	}

	/**
	 * Highlighted den anvisierten Andockpunkt
	 * 
	 * @param g
	 *            Graphic Objekt auf dem gerendert werden soll
	 */
	void highlightParentDocking(Graphics g) {
		int x = bounding.getX() + parentDock.getX();
		int y = bounding.getY() + parentDock.getY();
		int w = parentDock.getWidth();
		int h = parentDock.getHeight();
		renderDock(g, x, y, w, h);
	}

	/**
	 * Highlighted den anvisierten Andockpunkt
	 * 
	 * @param g
	 *            Graphic Objekt auf dem gerendert werden soll
	 */
	void highlightNextDocking(Graphics g) {
		int x = bounding.getX() + nextDock.getX();
		int y = bounding.getY() + nextDock.getY();
		int w = nextDock.getWidth();
		int h = nextDock.getHeight();
		renderDock(g, x, y, w, h);
	}

	/**
	 * Testet, ob das übergebene Renderable an dieses Renderable angedockt
	 * werden kann und gibt den Dockingindex zurück. Dabei wird outDocking auf
	 * dieses Renderable gesetzt (dies muss vom Renderer als Referenz übergeben
	 * werden)
	 * 
	 * @param r
	 *            Renderable, das angedockt werden soll
	 * @param outDocking
	 *            Renderable, an das angedockt wird. Eine lokale Variable im
	 *            Renderer, die als Referenz übergeben wird
	 * @return <ol>
	 *         <li>-3: Das übergebene Renderable kann als als nächster
	 *         Nachfolger (unterstes Objekt) eingefügt werden.</li>
	 *         <li>-2: Das übergebene Renderable kann als Parent an das aktuelle
	 *         angebunden werden</li>
	 *         <li>-1: Wenn das übergebene Renderable keine Position zum
	 *         andocken hat.</li>
	 *         <li>Index des Andockpunktes.</li>
	 *         </ol>
	 */
	public int dockingTest(Renderable r, ScratchPanel renderer) {
		if (r == this)
			return -1;

		// ParentDock zuerst prüfen, nur wenn kein parent gesetzt ist
		if (r.getType() != RType.BOOLEAN && parent == null && parentDock != null
				&& r.isParentable()) {
			BoundingBox globalDockTemp = new BoundingBox(parentDock.getX(),
					parentDock.getY(), parentDock.getWidth(),
					parentDock.getHeight());
			globalDockTemp.add(bounding.getX(), bounding.getY());
			if (globalDockTemp.hitTest(r.getGlobalBounding())) {
				renderer.setDocking(this);
				return -2;
			}
		}

		// Prüfe die Parameter
		for (int i = 0; i < dockings.size(); i++) {
			if (childs.get(i) != null && r.getType() == RType.BOOLEAN) {
				int dockIndex = childs.get(i).dockingTest(r, renderer);
				if (dockIndex != -1)
					return dockIndex;
			}

			if (r.getType() != dockings.get(i).getType())
				continue;

			if (dockings.get(i).getType() == RType.BOOLEAN
					&& childs.get(i) != null)
				continue;

			BoundingBox globalDockTemp = new BoundingBox(
					dockings.get(i).getX(), dockings.get(i).getY(), dockings
							.get(i).getWidth(), dockings.get(i).getHeight());
			globalDockTemp.add(bounding.getX(), bounding.getY());
			if (globalDockTemp.hitTest(r.getGlobalBounding())) {
				if (childs.get(i) == null || r.isParentable()) {
					renderer.setDocking(this);
					return i;
				}
			}
		}

		if (r.getType() != RType.BOOLEAN)
			for (int i = 0; i < dockings.size(); i++) {
				if (childs.get(i) != null) {
					int dockIndex = childs.get(i).dockingTest(r, renderer);
					if (dockIndex != -1)
						return dockIndex;
				}
			}

		// Prüfe den NextDock
		if (r.getType() != RType.BOOLEAN && nextDock != null) {
			BoundingBox globalDockTemp = new BoundingBox(nextDock.getX(),
					nextDock.getY(), nextDock.getWidth(), nextDock.getHeight());
			globalDockTemp.add(bounding.getX(), bounding.getY());
			if (globalDockTemp.hitTest(r.getGlobalBounding())) {
				if (next == null || r.isParentable()) {
					renderer.setDocking(this);
					return -3;
				}
			}
		}

		// Nächsten prüfen
		if (next != null) {
			int dockIndex = next.dockingTest(r, renderer);
			if (dockIndex >= 0 || dockIndex == -3)
				return dockIndex;
		}

		renderer.setDocking(null);
		return -1;
	}

	/**
	 * Liefert die BoundingBox, die das Renderable samt Kindern umschliesst
	 * (Umbox).
	 * 
	 * @return Gesamte BoundingBox
	 */
	public BoundingBox getGlobalBounding() {
		BoundingBox temp = new BoundingBox(bounding.getX(), bounding.getY(),
				bounding.getWidth(), bounding.getHeight());

		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null)
				temp = BoundingBox.union(temp, childs.get(i)
						.getGlobalBounding());

		if (next != null)
			temp = BoundingBox.union(temp, next.getGlobalBounding());

		return temp;
	}

	/**
	 * Testet ob die Position (x, y) zu dem Renderable Objekt gehört (Für
	 * Picking). Es werden auch alle childs überprüft und das selectierte object
	 * returnt
	 * 
	 * @param x
	 *            x Koordinate
	 * @param y
	 *            y Koordinate
	 * @return Das Renderable, wenn der Punkt (x, y) in den Boundings des
	 *         Renderables liegt (auch die childs werden getestet). Oder null
	 *         wird returnt, wenn keines der Renderables getroffen wurde.
	 */
	public Renderable hitTest(int x, int y) {
		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null) {
				Renderable temp = childs.get(i).hitTest(x, y);
				if (temp != null)
					return temp;
			}

		if (next != null) {
			Renderable temp = next.hitTest(x, y);
			if (temp != null)
				return temp;
		}

		if (bounding.hitTest(x, y))
			return this;

		return null;
	}

	/**
	 * Bewegt das Renderable um den x und y Wert von der aktuellen Position
	 * 
	 * @param x
	 *            Delta zum Bewegen in x Richtung
	 * @param y
	 *            Delta zum Bewegen in y Richtung
	 */
	public void moveRelative(int x, int y) {
		bounding.add(x, y);

		updateChilds();
	}

	/**
	 * Bewegt das Renderable auf die übergebenen Koordinaten
	 * 
	 * @param x
	 *            x Koordinate
	 * @param y
	 *            y Koordinate
	 */
	public void moveTo(int x, int y) {
		bounding.setPosition(x, y);

		updateChilds();
	}

	/**
	 * Das übergebene child wird in dieses Renderable am Andockpunkt hinzugefügt
	 * 
	 * @param child
	 *            das Renderable, das hinzugefügt werden soll
	 * @param dockingIndex
	 *            index an dem angedockt werden soll
	 */
	public void add(Renderable child, int dockingIndex) {
		if (dockingIndex < 0 || dockingIndex >= maxChilds)
			return;

		if (childs.get(dockingIndex) != null) {
			Renderable child2 = childs.get(dockingIndex);
			child2.removeFromParent();

			add(child, dockingIndex);
			Renderable tempChild = child.getDeepestVoid();

			int childDockIndex = 0;
			for (; childDockIndex < tempChild.maxChilds; childDockIndex++)
				if (tempChild.dockings.get(childDockIndex).getType() == RType.VOID)
					break;

			tempChild.add(child2, childDockIndex);
		}

		childs.put(dockingIndex, child);
		child.parent = this;
		updateChilds();
	}

	/**
	 * Dockt das übergebene Renderable als Parent an. Dabei wird das tiefste
	 * VOID Objekt von p als parent angedockt
	 * 
	 * @param p
	 */
	public void addAsParent(Renderable p) {
		// Suche den Tiefsten VOID child von Renderable p
		Renderable temp = p.getDeepestVoid();
		int x = getX();
		int y = getY();
		temp.addAsNext(this);
		p.moveRelative(x - getX(), y - getY());
	}

	/**
	 * Fügt das übergebene Renderable-Objekt als next-Element an dieses Objekt
	 * an. Sollte dieses Element einen Nachfolger haben, so wird das übergebene
	 * Renderable dazwischen geschaltet.
	 * 
	 * @param child
	 */
	public void addAsNext(Renderable child) {
		if (next != null) {
			Renderable child2 = next;
			next = child;
			child.parent = this;

			Renderable last = child.getDeepestVoid();
			last.next = child2;
			child2.parent = last;
		} else {
			// Hat noch keinen Nachfolger
			child.parent = this;
			next = child;
		}
		updateChilds();
	}

	/**
	 * liefert für dieses Renderable das Kindelement, was am tiefsten
	 * hineinreicht. Jedoch gelten die Abzweigungen der if-Abfragen nicht,
	 * sondern bei if wird das nächstfolgende Element genommen.
	 * 
	 * @return
	 */
	public Renderable getDeepestVoid() {
		if (next != null)
			return next.getDeepestVoid();

		return this;
	}

	/**
	 * Liefert für dieses Renderable das Wurzelelement, an dem alles hängt, oder
	 * dieses Element, wenn es kein Vaterelement besitzt (und somit das
	 * Wurzelelement ist).
	 * 
	 * @return
	 */
	public Renderable getRootElement() {
		if (parent == null)
			return this;

		return parent.getRootElement();
	}

	/**
	 * Entfernt dieses Renderable von seinem Parentobjekt
	 * 
	 * @return true, wenn das Renderable einen Parent hatte und entfernt wurde
	 */
	public boolean removeFromParent() {
		if (parent == null)
			return false;

		parent.removeChild(this);
		parent = null;
		return true;
	}

	/**
	 * Sucht in diesem Renderable den übergebenen child und entfernt diesen als
	 * Referenz
	 * 
	 * @param child
	 *            Zu entfernendes Childobjekt
	 */
	public void removeChild(Renderable child) {
		if (next == child) {
			next = null;
			return;
		}

		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) == child) {
				childs.remove(i);
				return;
			}
	}

	/**
	 * updatet die Position abhängig vom Parent
	 */
	protected void updatePosition() {
		DockingBox dockBox = getParentDockingBox();
		if (dockBox == null)
			return;

		bounding.setPosition(parent.bounding.getX() + dockBox.getDockX(),
				parent.bounding.getY() + dockBox.getDockY());
	}

	/**
	 * Liefert für dieses Renderable die DockingBox des übergeordneten
	 * Parentrenderables
	 * 
	 * @return
	 */
	private DockingBox getParentDockingBox() {
		if (parent == null)
			return null;

		if (parent.next == this)
			return parent.nextDock;

		int parentDockIndex = 0;
		for (; parentDockIndex < parent.maxChilds; parentDockIndex++)
			if (parent.childs.get(parentDockIndex) == this)
				break;

		return parent.dockings.get(parentDockIndex);
	}

	/**
	 * Updatet die Positionen aller Kinderelemente von diesem Renderable.
	 */
	protected void updateChilds() {
		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null) {
				childs.get(i).updatePosition();
				childs.get(i).updateChilds();
			}

		if (next != null) {
			next.updatePosition();
			next.updateChilds();
		}
	}

	/**
	 * Updatet die BoundingBoxen für dieses Element, abhängig von den Parametern
	 * und den Kinderelementen. Da dies für jedes Element unterschiedlich ist,
	 * muss jedes abgeleitete Element selbst dafür sorgen, dass die
	 * größenverhältnisse richtig angepasst werden.
	 */
	abstract public void updateBounds();

	/**
	 * Updatet die Positionen und Größen für das Vaterelement. Dies ist
	 * notwendig, wenn dieses Element vom Vaterelement entfernt wurde und sich
	 * somit die größe des Vaterelement geändert haben könnte.
	 */
	public void updateParent() {
		if (parent == null)
			return;

		parent.updateChilds();
		parent.updateBounds();
	}

	/**
	 * Setzt fest, ob dieses Element bewegt werden kann. Dies ist nur für die
	 * ElementList erforderlich, da dort die Element in einer Liste angeordnet
	 * werden und nicht vom Benutzer bewegt werden können.
	 * 
	 * @param value
	 */
	public void setMoveable(boolean value) {
		moveable = value;
	}

	/**
	 * Fragt ab, ob das Renderable vom Benutzer bewegt werden kann.
	 * 
	 * @return
	 */
	public boolean isMoveable() {
		return moveable;
	}

	/**
	 * Liefert true zurück, wenn das aktuelle Renderable samt Kindern als Parent
	 * angedockt werden kann
	 * 
	 * @return
	 */
	public boolean isParentable() {
		Renderable temp = getDeepestVoid();
		return temp.nextDock != null;
	}

	/**
	 * Liefert eine Kopie von diesem Renderable.
	 */
	abstract public Renderable clone();

	/**
	 * Setzt das Vaterelement von diesem Renderable fest.
	 * 
	 * @param parent
	 */
	public void setParent(Renderable parent) {
		this.parent = parent;
	}

	/**
	 * Liefert die DockingBox für das next-Element. Im Normalfall ist diese
	 * direkt unterhalb des Elements.
	 * 
	 * @return
	 */
	public DockingBox getNextDock() {
		return nextDock;
	}

	/**
	 * Führt die interne Coderepräsentation dieses Renderables aus. Dabei wird
	 * darauf geachtet, ob das Programm gestoppt oder pausiert wurde.
	 * 
	 * @param program
	 *            Das ScratchProgramm, in derm auch der StorageController samt
	 *            Methoden enthalten ist. Dieser ist zuständig für Stopp und
	 *            Pause.
	 * @return Liefert null bei void Methoden oder den entsprechenden boolschen
	 *         Wert.
	 * @throws FunctionResultException
	 *             Damit "return" in einer Methode auch sofort die Methode
	 *             verlässt, wird das Ergebnis in einer Exception übergeben, die
	 *             beim Aufruf jeder Methode aufgefangen wird.
	 */
	public Object perform(ScratchProgram program)
			throws FunctionResultException {
		// Setze im Programm, dass dieses Renderable aktiv ist
		ScratchProgram.setActive(this);

		// Wenn Programm gestoppt ist, sofort wieder verlassen
		if (program.checkStop()) {
			return false;
		}
		program.checkPause();

		// während einer Pause wird stop gedrückt (dibo)
		if (program.checkStop()) {
			return false;
		}

		Object temp = performImplementation(program);

		// Pause und Stopp überprüfen
		if (program.checkStop()) {
			return false;
		}

		// Nächstes Element ausführen
		if (next != null)
			next.perform(program);

		return temp;
	}

	/**
	 * Führt den Befehl von diesem Renderable aus. Jedes Objekt ist selbst
	 * zuständig für die richtige Ausführung der Funktion.
	 * 
	 * @return
	 */
	public abstract Object performImplementation(ScratchProgram program)
			throws FunctionResultException;

	/**
	 * Gibt an, ob das Renderable mit dem übergebenen Namen bereits verwendet
	 * wird. Dies ist notwendig, wenn man ein Renderable löschen will, so darf
	 * dieses Renderable nirgends vorkommen. Rekursiv werden alle Kinder nach
	 * diesem Element durchsucht.
	 * 
	 * @param name
	 *            Name des Elements, das gesucht werden soll.
	 * @return true, wenn das Element bereits verwendet wird.
	 */
	public boolean inUse(String name) {
		if (this.name.equals(name))
			return true;

		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null && childs.get(i).inUse(name))
				return true;

		if (next != null)
			return next.inUse(name);

		return false;
	}

	/**
	 * Benennt alle Elemente mit dem Namen fromName in den Namen toName um.
	 * Rekursiv wird diese Methode an alle Kinder weitergereicht.
	 * 
	 * @param fromName
	 * @param toName
	 */
	public void rename(String fromName, String toName) {
		if (this.name.equals(fromName))
			setName(toName);

		for (int i = 0; i < maxChilds; i++)
			if (childs.get(i) != null)
				childs.get(i).rename(fromName, toName);

		if (next != null)
			next.rename(fromName, toName);
	}
	
	public Renderable getChild(int index) {
		for (int i = 0; i < maxChilds; i++) {
			if (index == i) {
				return childs.get(i);
			}
		}
		return null;
	}

	/**
	 * Beschreibt den übergebenen XMLStreamWriter. Somit wird das Renderable
	 * gespeichert. Aufgrund der hierarchischen Struktur, wird dies rekursiv auf
	 * die Kinderelemente angewandt.
	 * 
	 * @param writer
	 * @throws XMLStreamException
	 */
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("RENDERABLE");
		writer.writeAttribute("NAME", getName());
		writer.writeAttribute("TYPE", getType().toString());

		// Parameter schreiben
		for (int i = 0; i < maxChilds; i++) {
			if (childs.get(i) == null)
				continue;

			writer.writeStartElement("CHILD");
			writer.writeAttribute("POS", i + "");
			childs.get(i).toXML(writer);
			writer.writeEndElement();
		}

		// Next schreiben
		if (next != null) {
			writer.writeStartElement("NEXT");
			next.toXML(writer);
			writer.writeEndElement();
		}

		writer.writeEndElement();
	}

	/**
	 * Lädt die Attribute dieses Renderables abhängig von der übergebenen
	 * NodeList.
	 * 
	 * @param childNodes
	 *            Die Kinderelemente des Elements <tt>RENDERABLE</tt>
	 */
	public void loadProgram(NodeList childNodes) {
		// Lade die childNodes
		for (int i = 0; i < childNodes.getLength(); i++) {
			Element child = (Element) childNodes.item(i);
			if (child.getLocalName().equals("CHILD"))
				loadChild(child);

			if (child.getLocalName().equals("NEXT"))
				loadNext(child);
		}
	}

	/**
	 * Lädt die Kinderelemente
	 * 
	 * @param child
	 *            Das Element <tt>CHILD</tt> aus <tt>RENDERABLE</tt>
	 */
	private void loadChild(Element child) {
		int pos = new Integer(child.getAttribute("POS"));
		Element childNodes = (Element) child.getChildNodes().item(0);
		Renderable childRenderable = ScratchUtils.getRenderableByName(
				childNodes.getAttribute("NAME"),
				childNodes.getAttribute("TYPE"));
		add(childRenderable, pos);
		childRenderable.updateBounds();
		childRenderable.getRootElement().updateChilds();
		childRenderable.loadProgram(childNodes.getChildNodes());
	}

	/**
	 * Lädt das next-Element
	 * 
	 * @param child
	 *            das Element <tt>NEXT</tt> aus <tt>RENDERABLE</tt>
	 */
	private void loadNext(Element child) {
		Element childNodes = (Element) child.getChildNodes().item(0);
		Renderable childRenderable = ScratchUtils.getRenderableByName(
				childNodes.getAttribute("NAME"),
				childNodes.getAttribute("TYPE"));
		addAsNext(childRenderable);
		childRenderable.updateBounds();
		childRenderable.getRootElement().updateChilds();
		childRenderable.loadProgram(childNodes.getChildNodes());
	}

	/**
	 * Schreibt den Inhalt dieses Elements auf den übergebenen buffer als
	 * Programmcode in Hamster-Simulator Convention.
	 * 
	 * @param buffer
	 *            Buffer auf den geschrieben werden soll.
	 * @param layer
	 *            Ebene, in der der Code liegt (Anzahl Tabs).
	 * @param comment
	 *            Ist der Code Auskommentiert?
	 * @param needsReturn
	 *            Es ist eine Boolean Methode, die am Ende ein return benötigt.
	 */
	public abstract void writeSourceCode(StringBuffer buffer, int layer,
			boolean comment, boolean needsReturn)
			throws FunctionResultException;

	/**
	 * Schreibt in dem übergebenen buffer abhängig von den Parametern den Anfang
	 * einer Zeile. Schreibt den Kommentarstring und die Anzahl an Tabs (KEINE
	 * Zeilenübergänge!!!).
	 * 
	 * @param buffer
	 * @param layer
	 * @param comment
	 */
	public static void startLine(StringBuffer buffer, int layer, boolean comment) {
		if (comment)
			buffer.append("//");

		for (int i = 0; i < layer; i++)
			buffer.append("\t");
	}
}
