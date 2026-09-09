package de.hamster.scratch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.hamster.editor.view.TextArea;
import de.hamster.scratch.Renderable.RType;
import de.hamster.scratch.elements.BooleanObject;
import de.hamster.scratch.elements.BooleanPrintRoot;
import de.hamster.scratch.elements.VoidPrintRoot;
import de.hamster.scratch.elements.voids.ReturnBooleanObject;
import de.hamster.scratch.gui.ClickHandler;
import de.hamster.scratch.gui.ContextMenu;
import de.hamster.scratch.gui.ContextMenuHandler;
import de.hamster.scratch.gui.DeleteMethodException;
import de.hamster.scratch.gui.ElementList;
import de.hamster.scratch.gui.InvalidIdentifierException;
import de.hamster.scratch.gui.LocationChangeHandler;
import de.hamster.scratch.gui.MenuItem;
import de.hamster.scratch.gui.MethodChangeHandler;
import de.hamster.scratch.gui.NextMethodHandler;
import de.hamster.scratch.gui.OptionsPanel;
import de.hamster.scratch.gui.RefreshHandler;
import de.hamster.scratch.gui.RenameFunctionFrame;
import de.hamster.scratch.gui.RenameMethodException;
import de.hamster.scratch.gui.ScratchScrollBar;
import de.hamster.scratch.gui.SwapFunctionFrame;
import de.hamster.scratch.gui.TabButton;
import de.hamster.scratch.gui.TabChangedHandler;
import de.hamster.scratch.gui.TabOpenedHandler;
import de.hamster.scratch.gui.TabPanel;

/**
 * Diese Klasse ist die Herzst�ck GUI des Programms. Diese Klasse ist das
 * Hauptpanel auf dem alles gerendert wird und das die interne Logik
 * zusammenfasst. Das Panel muss an der richtigen Stelle im Hamster-Programm
 * eingebunden werden.
 * 
 * @author HackZ
 * 
 */
public class ScratchPanel extends JPanel implements MouseListener,
		MouseMotionListener, TabChangedHandler, RefreshHandler,
		ContextMenuHandler, MethodChangeHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6049901089566290960L;
	public static Color BACKGROUND_COLOR = new Color(124, 128, 131);
	public static Color SECONDARY_COLOR = new Color(102, 105, 107);
	public static Color THIRD_COLOR = new Color(117, 120, 123);
	public static int OPTIONS_WIDTH = 147;
	public static int OPTIONS_HEIGHT = 94;
	public static int TAB_PANEL_HEIGHT = 40;

	private static BufferedImage imgTop;
	private static BufferedImage imgBottom;
	private static BufferedImage imgLeft;
	private static BufferedImage imgRight;
	private static BufferedImage imgTopLeft;
	private static BufferedImage imgTopRight;
	private static BufferedImage imgBottomLeft;
	private static BufferedImage imgBottomRight;

	private Method method;
	private TextArea textArea;
	private Renderable selected;
	private Renderable docking;
	private Renderable rootElement;
	private ArrayList<Renderable> renderables;
	private Point tempClick;
	private ElementList elements;
	private OptionsPanel optionsPanel;
	private TabPanel tabPanel;
	private ScratchScrollBar vScrollbar;
	private ScratchScrollBar hScrollbar;
	private ContextMenu contextMenu;

	private boolean panelLocked = false; // dibo 290710

	public ScratchPanel() {
		this.textArea = null;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setLayout(null);
		this.setBackground(ScratchPanel.BACKGROUND_COLOR);
		this.loadImages();

		// Context menu on TOP
		this.contextMenu = new ContextMenu();
		this.contextMenu.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.contextMenu.addRefreshHandler(this);
		this.add(this.contextMenu);

		// Elementliste
		this.elements = new ElementList(this);
		this.elements.setBounds(0, ScratchPanel.OPTIONS_HEIGHT,
				ScratchPanel.OPTIONS_WIDTH, this.getHeight()
						- ScratchPanel.OPTIONS_HEIGHT);
		this.add(this.elements);

		// Optionsmenue
		this.optionsPanel = new OptionsPanel(this.elements, this);
		this.optionsPanel.setBounds(0, 0, ScratchPanel.OPTIONS_WIDTH,
				ScratchPanel.OPTIONS_HEIGHT);
		this.add(this.optionsPanel);

		// Scrollbars
		this.vScrollbar = new ScratchScrollBar(ScratchScrollBar.VERTICAL);
		this.vScrollbar.setBounds(this.getWidth() - 25, 10,
				ScratchScrollBar.WIDTH, this.getHeight() - 20);
		this.vScrollbar.addLocationChangeHandler(new VerticalLocationHandler());
		this.add(this.vScrollbar);

		this.hScrollbar = new ScratchScrollBar(ScratchScrollBar.HORIZONTAL);
		this.hScrollbar.setBounds(ScratchPanel.OPTIONS_WIDTH + 10,
				this.getHeight() - 25, this.getWidth() - 20
						- ScratchPanel.OPTIONS_WIDTH, ScratchScrollBar.WIDTH);
		this.hScrollbar
				.addLocationChangeHandler(new HorizontalLocationHandler());
		this.add(this.hScrollbar);

		// TabPanel
		this.tabPanel = new TabPanel(this);
		this.tabPanel.setBounds(ScratchPanel.OPTIONS_WIDTH, 0, this.getWidth()
				- ScratchPanel.OPTIONS_WIDTH, ScratchPanel.TAB_PANEL_HEIGHT);
		this.tabPanel.addTabChangedHandler(this);
		this.tabPanel.addContextMenuHandler(this);
		this.tabPanel.addMethodChangeHandler(this);
		this.tabPanel.addTabOpenedHandler(new OpenedMethodHandler());
		this.tabPanel.refresh();
		this.add(this.tabPanel);
	}

	/**
	 * Liefert alle Elemente, die am RootElement h�ngen als Bild zur�ck. Sollten
	 * keine Elemente am RootElement angedockt sein, so wird null
	 * zur�ckgeliefert.
	 * 
	 * @return Bild der Elemente oder null.
	 */
	public BufferedImage getImage() {
		Renderable element = null;

		if (method.getType() == RType.BOOLEAN)
			element = new BooleanPrintRoot(method.getName());
		else
			element = new VoidPrintRoot(method.getName());

		Renderable r = rootElement.next;
		element.addAsNext(r);

		BoundingBox bounding = element.getGlobalBounding();
		BufferedImage img = new BufferedImage(bounding.getWidth() + 1,
				bounding.getHeight() + 4, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.translate(-element.getX(), -element.getY());
		element.render(g);

		return img;
	}

	public BufferedImage[] getImages() {
		Method[] methods = getStorageController().getAllMethods();
		BufferedImage[] images = new BufferedImage[methods.length];
		for (int i = 0; i < images.length; i++) {
			images[i] = getImage(methods[i]);
		}
		return images;
	}

	public BufferedImage getImage(Method m) {
		Renderable element = null;

		if (m.getType() == RType.BOOLEAN)
			element = new BooleanPrintRoot(m.getName());
		else
			element = new VoidPrintRoot(m.getName());

		Renderable r2 = m.getRootElement().next;
		element.addAsNext(r2);

		BoundingBox bounding = element.getGlobalBounding();
		BufferedImage img = new BufferedImage(bounding.getWidth() + 1,
				bounding.getHeight() + 4, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.translate(-element.getX(), -element.getY());
		element.render(g);

		return img;
	}

	/**
	 * Beim jeden Erstellen des Panels wird versucht die statischen
	 * Hintergrundbilder zu laden. Sollte bereits eines der Bilder geladen
	 * worden sein, so wird die Methode abgebrochen.
	 */
	private void loadImages() {
		if (ScratchPanel.imgTop != null) {
			return; // Bilder sind bereits geladen
		}

		ScratchPanel.imgTop = ScratchUtils
				.getImage("scratch/backgrounds/top_elementlist.png");
		ScratchPanel.imgBottom = ScratchUtils
				.getImage("scratch/backgrounds/bottom_elementlist.png");
		ScratchPanel.imgLeft = ScratchUtils
				.getImage("scratch/backgrounds/left.png");
		ScratchPanel.imgRight = ScratchUtils
				.getImage("scratch/backgrounds/right.png");
		ScratchPanel.imgTopLeft = ScratchUtils
				.getImage("scratch/backgrounds/top_left_elementlist.png");
		ScratchPanel.imgTopRight = ScratchUtils
				.getImage("scratch/backgrounds/top_right_elementlist.png");
		ScratchPanel.imgBottomLeft = ScratchUtils
				.getImage("scratch/backgrounds/bottom_left_elementlist.png");
		ScratchPanel.imgBottomRight = ScratchUtils
				.getImage("scratch/backgrounds/bottom_right_elementlist.png");
	}

	/**
	 * Setzt die zugeordnete TextArea (nicht ver�ndern)
	 */
	public void setTextArea(TextArea area) {
		this.textArea = area;
		((ScratchHamsterFile)this.textArea.getFile()).getProgram()
				.addRefreshHandler(this);
		((ScratchHamsterFile) this.textArea.getFile()).getProgram()
				.addNextMethodHandler(new OnNextMethodHandler());

		String selName = this.getStorageController().getSelectedMethod();

		ArrayList<String> openedTabs = this.getStorageController()
				.getOpenedMethods();
		for (String s : openedTabs) {
			this.tabPanel.addTab(s);
		}

		this.tabPanel.setSelected(selName);

		this.optionsPanel.loadLastActive();
	}

	/**
	 * Liefert die zugeordnete TextArea (nicht ver�ndern)
	 */
	public TextArea getTextArea() {
		return this.textArea;
	}

	/**
	 * Liefert den StorageController, in dem die Methoden gespeichert sind.
	 * Dieser kann aus dem TextArea ausgelesen werden.
	 * 
	 * @return
	 */
	public StorageController getStorageController() {
		StorageController c = ((ScratchHamsterFile) this.textArea.getFile())
				.getProgram().getProgram();
		return c;
	}

	public TabPanel getTabPanel() {
		return this.tabPanel;
	}

	/**
	 * Wird aufgerufen, wenn ein Scratch-Programm gestartet bzw. beendet wird.
	 * W�hrend einer Programm-Ausf�hrung sollte das Programm nicht ver�ndert
	 * werden k�nnen (locked == true)
	 */
	public void setLocked(boolean locked) {
		// begin dibo 290710
		if (locked == this.panelLocked) {
			return;
		}
		this.panelLocked = locked;
		// end dibo 290710

		this.elements.setLocked(locked);
		if (locked) {
			this.removeMouseListener(this);
		} else {
			this.addMouseListener(this);
		}
	}

	/**
	 * Da beim Docken mehrere Parameter zur�ckgeliefert werden m�ssten, wird
	 * diese Methode verwendet, um w�hrend des dockVorgang berechnet, an welches
	 * Objekt angedockt werden kann.
	 * 
	 * @param r
	 */
	public void setDocking(Renderable r) {
		this.docking = r;
	}

	/**
	 * Teil der Methode paint(Graphics g). Weitere Elemente werden gerendert.
	 * 
	 * @param g
	 */
	public void render(Graphics g) {
		this.rootElement.render(g);
		for (Renderable r : this.renderables) {
			if (r == this.selected) {
				continue;
			}
			r.render(g);
		}

		this.highlightDockings(g);
		this.paintBorders(g);
		this.paintChildren(g);

		if (this.selected != null) {
			this.selected.renderShadow(g);
			this.selected.render(g);
		}
	}

	/**
	 * Zeichnet die Randbilder. Das Look-and-Feel von Scratch.
	 * 
	 * @param g
	 */
	private void paintBorders(Graphics g) {
		// g.drawImage(imgTop, 0, 0, getWidth(), 8, BACKGROUND_COLOR, null);
		for (int i = 0; i < ScratchPanel.imgTop.getHeight(); i++) {
			g.setColor(new Color(ScratchPanel.imgTop.getRGB(0, i)));
			g.drawLine(ScratchPanel.OPTIONS_WIDTH, i
					+ ScratchPanel.TAB_PANEL_HEIGHT, this.getWidth(), i
					+ ScratchPanel.TAB_PANEL_HEIGHT);
		}

		// g.drawImage(imgBottom, 0, getHeight() - 8, getWidth(), 8,
		// BACKGROUND_COLOR, null);
		for (int i = 0; i < ScratchPanel.imgBottom.getHeight(); i++) {
			int top = i + this.getHeight() - ScratchPanel.imgBottom.getHeight();
			g.setColor(new Color(ScratchPanel.imgBottom.getRGB(0, i)));
			g.drawLine(ScratchPanel.OPTIONS_WIDTH, top, this.getWidth(), top);
		}

		// g.drawImage(imgLeft, 0, 0, 8, getHeight(), BACKGROUND_COLOR, null);
		for (int i = 0; i < ScratchPanel.imgLeft.getWidth(); i++) {
			g.setColor(new Color(ScratchPanel.imgLeft.getRGB(i, 0)));
			g.drawLine(ScratchPanel.OPTIONS_WIDTH + i,
					ScratchPanel.TAB_PANEL_HEIGHT, ScratchPanel.OPTIONS_WIDTH
							+ i, this.getHeight());
		}

		// g.drawImage(imgRight, getWidth() - 5, 0, 5, getHeight(),
		// BACKGROUND_COLOR, null);
		for (int i = 0; i < ScratchPanel.imgRight.getWidth(); i++) {
			int left = i + this.getWidth() - ScratchPanel.imgRight.getWidth();
			g.setColor(new Color(ScratchPanel.imgRight.getRGB(i, 0)));
			g.drawLine(left, ScratchPanel.TAB_PANEL_HEIGHT, left,
					this.getHeight());
		}

		g.drawImage(ScratchPanel.imgTopLeft, ScratchPanel.OPTIONS_WIDTH,
				ScratchPanel.TAB_PANEL_HEIGHT, null);
		g.drawImage(ScratchPanel.imgTopRight, this.getWidth()
				- ScratchPanel.imgTopRight.getWidth(),
				ScratchPanel.TAB_PANEL_HEIGHT, null);
		g.drawImage(ScratchPanel.imgBottomLeft, ScratchPanel.OPTIONS_WIDTH,
				this.getHeight() - ScratchPanel.imgBottomLeft.getHeight(), null);
		g.drawImage(ScratchPanel.imgBottomRight, this.getWidth()
				- ScratchPanel.imgBottomRight.getWidth(), this.getHeight()
				- ScratchPanel.imgBottomRight.getHeight(), null);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		this.updateScrollbars();
		this.elements.setBounds(0, ScratchPanel.OPTIONS_HEIGHT,
				ScratchPanel.OPTIONS_WIDTH, this.getHeight()
						- ScratchPanel.OPTIONS_HEIGHT);
		this.vScrollbar.setBounds(this.getWidth() - 25,
				10 + ScratchPanel.TAB_PANEL_HEIGHT, ScratchScrollBar.WIDTH,
				this.getHeight() - ScratchPanel.TAB_PANEL_HEIGHT
						- (this.hScrollbar.isVisible() ? 40 : 20));
		this.hScrollbar.setBounds(ScratchPanel.OPTIONS_WIDTH + 10,
				this.getHeight() - 25,
				this.getWidth() - (this.vScrollbar.isVisible() ? 40 : 20)
						- ScratchPanel.OPTIONS_WIDTH, ScratchScrollBar.WIDTH);
		this.tabPanel.setBounds(ScratchPanel.OPTIONS_WIDTH, 0, this.getWidth()
				- ScratchPanel.OPTIONS_WIDTH, ScratchPanel.TAB_PANEL_HEIGHT);
		this.contextMenu.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.contextMenu.setVisible(false);
	}

	@Override
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}

	/**
	 * Rendert neben den Elementen die DockingBoxen, an die das selektierte
	 * Element angedockt werden kann.
	 * 
	 * @param g
	 */
	private void highlightDockings(Graphics g) {
		if (this.selected == null) {
			return;
		}

		// Andocken an Root Element
		int dockIndex = this.rootElement.dockingTest(this.selected, this);
		if (dockIndex >= 0) {
			this.docking.highlightDocking(g, dockIndex);
			return;
		} else if (dockIndex == -2) {
			this.docking.highlightParentDocking(g);
			return;
		} else if (dockIndex == -3) {
			this.docking.highlightNextDocking(g);
			return;
		}

		for (Renderable r : this.renderables) {
			dockIndex = r.dockingTest(this.selected, this);
			if (dockIndex >= 0) {
				this.docking.highlightDocking(g, dockIndex);
				return;
			} else if (dockIndex == -2) {
				this.docking.highlightParentDocking(g);
				return;
			} else if (dockIndex == -3) {
				this.docking.highlightNextDocking(g);
				return;
			}
		}
	}

	/**
	 * F�gt das �bergebene Renderable hinzu, so dass dieses im HauptFenster
	 * angezeigt wird.
	 * 
	 * @param r
	 */
	public void addRenderable(Renderable r) {
		this.renderables.add(r);
		this.modified();
		this.repaint();
	}

	/**
	 * F�gt ein Renderable an Position x, y in der Elementenliste hinzu und
	 * f�hrt gleichzeitig ein mouseDown Event aus
	 * 
	 * @param r
	 *            Das Renderable, das hinzugef�gt werden soll
	 * @param x
	 *            Koordinate x
	 * @param y
	 *            Koordinate y
	 */
	public void addRenderable(Renderable r, MouseEvent e) {
		this.addRenderable(r);
		this.selected = r;
		this.tempClick = new Point(e.getX(), e.getY());
		this.modified();
	}

	@Override
	public void paint(Graphics g) {
		this.paintBackground(g);
		synchronized (this) {
			this.render(g);
		}
	}

	/**
	 * Zeichnet den Hintergrund (Gestreift). Look-and-Feel von Scratch.
	 * 
	 * @param g
	 */
	private void paintBackground(Graphics g) {
		g.setColor(ScratchPanel.BACKGROUND_COLOR);
		g.fillRect(ScratchPanel.OPTIONS_WIDTH, 0, this.getWidth()
				- ScratchPanel.OPTIONS_WIDTH, this.getHeight());

		int i = ScratchPanel.OPTIONS_WIDTH;
		g.setColor(ScratchPanel.SECONDARY_COLOR);
		while (i <= this.getWidth()) {
			g.drawLine(i, 0, i, this.getHeight());
			i += 4;
		}

		i = ScratchPanel.OPTIONS_WIDTH + 2;
		g.setColor(ScratchPanel.THIRD_COLOR);
		while (i <= this.getWidth()) {
			g.drawLine(i, 0, i, this.getHeight());
			i += 4;
		}
	}

	/**
	 * Liefert die Gesamth�he aller Elemente. Notwendig, um die ScrollBars so
	 * anzupassen, dass diese bis zu der maximalen H�he gescrollt werden k�nnen.
	 * 
	 * @return
	 */
	public int getInnerHeight() {
		int h = 0;
		for (Renderable r : this.renderables) {
			BoundingBox temp = r.getGlobalBounding();
			int tempH = temp.getY() + temp.getHeight();
			if (tempH > h) {
				h = tempH;
			}
		}

		BoundingBox temp = this.rootElement.getGlobalBounding();
		int tempH = temp.getY() + temp.getHeight();
		if (tempH > h) {
			h = tempH;
		}

		return h + (this.hScrollbar.isVisible() ? 25 : 0) + 20
				+ this.method.getTop();
	}

	/**
	 * Liefert die Gesamtbreite aller Elemente. Notwendig, um die ScrollBars so
	 * anzupassen, dass diese bis zu der maximalen Breite gescrollt werden
	 * k�nnen.
	 * 
	 * @return
	 */
	public int getInnerWidth() {
		int w = 0;
		for (Renderable r : this.renderables) {
			BoundingBox temp = r.getGlobalBounding();
			int tempW = temp.getX() + temp.getWidth();
			if (tempW > w) {
				w = tempW;
			}
		}

		BoundingBox temp = this.rootElement.getGlobalBounding();
		int tempW = temp.getX() + temp.getWidth();
		if (tempW > w) {
			w = tempW;
		}

		return w + (this.vScrollbar.isVisible() ? 25 : 0) + 20
				+ this.method.getLeft();
	}

	/**
	 * Berechnet die notwendige Breite und H�he f�r die Darstellung aller
	 * Elemente und zeigt die Scroll- balken, falls notwendig.
	 */
	private void updateScrollbars() {
		this.updateScrollbarsIntern();
		this.updateScrollbarsIntern();
		this.vScrollbar.setBounds(this.getWidth() - 25,
				10 + ScratchPanel.TAB_PANEL_HEIGHT, ScratchScrollBar.WIDTH,
				this.getHeight() - ScratchPanel.TAB_PANEL_HEIGHT
						- (this.hScrollbar.isVisible() ? 40 : 20));
		this.hScrollbar.setBounds(ScratchPanel.OPTIONS_WIDTH + 10,
				this.getHeight() - 25,
				this.getWidth() - (this.vScrollbar.isVisible() ? 40 : 20)
						- ScratchPanel.OPTIONS_WIDTH, ScratchScrollBar.WIDTH);
	}

	/**
	 * Passt die H�hen und Breiten der ScrollBars an.
	 */
	private void updateScrollbarsIntern() {
		if (this.getHeight() == 0) {
			return;
		}

		int vScrollHeight = this.getInnerHeight() - this.getHeight();
		if (vScrollHeight > 0) {
			this.vScrollbar.setVisible(true);
			this.vScrollbar.setMaximum(vScrollHeight);
		} else {
			this.vScrollbar.setVisible(false);
			for (Renderable r : this.renderables) {
				r.moveRelative(0, this.method.getTop());
			}
			this.rootElement.moveRelative(0, this.method.getTop());
			this.method.setTop(0);
		}

		int hScrollWidth = this.getInnerWidth() - this.getWidth();
		if (hScrollWidth > 0) {
			this.hScrollbar.setVisible(true);
			this.hScrollbar.setMaximum(hScrollWidth);
		} else {
			this.hScrollbar.setVisible(false);
			for (Renderable r : this.renderables) {
				r.moveRelative(this.method.getLeft(), 0);
			}
			this.rootElement.moveRelative(this.method.getLeft(), 0);
			this.method.setLeft(0);
		}
	}

	/**
	 * Pr�ft, ob der �bergebene JavaIdentifier f�r eine neue Methode m�glich
	 * ist.
	 * 
	 * @param name
	 *            Name f�r eine neue Methode.
	 * @throws InvalidIdentifierException
	 *             wird geworfen, falls
	 *             <ol>
	 *             <li>Der Bezeichner hat keine g�ltige Javakonvention.</li>
	 *             <li>Der Bezeichner ist bereits vergeben.</li>
	 *             <li>Der Bezeichner ist ein reserviertes Javakeyword.</li>
	 *             </ol>
	 */
	public void checkJavaIdentifier(String name)
			throws InvalidIdentifierException {
		this.getStorageController().allocName(name);
	}

	public void createMethod(String name, RType rType) {
		switch (rType) {
		case VOID:
			this.getStorageController().addVoidMethod(name);
			break;
		case BOOLEAN:
			this.getStorageController().addBooleanMethod(name);
			break;
		}
		this.tabPanel.addTab(name);
		this.optionsPanel.refreshElementList();
		this.modified();
	}

	/**
	 * Setzt fest, dass das Panel modifiziert wurde, so dass der Speicherbutton
	 * enabled wird.
	 */
	public void modified() {
		if (this.textArea == null) {
			return;
		}

		this.textArea.getFile().setModified(true);
	}

	/**
	 * Setzt fest, dass das Panel nicht modifiziert wurde, so dass der
	 * Speicherbutton disabled wird.
	 */
	public void unmodified() {
		if (this.textArea == null) {
			return;
		}

		this.textArea.getFile().setModified(false);
	}

	/**
	 * �ffnet einen Tab mit dem �bergebenen Namen.
	 * 
	 * @param name
	 */
	public void openTab(String name) {
		if (this.getStorageController().existsMethod(name)) {
			this.tabPanel.addTab(name);
		}
	}

	@Override
	public void openContextMenu(ArrayList<MenuItem> menuItems, int x, int y) {
		this.contextMenu.openMenu(menuItems, x, y);
	}

	/**
	 * �ffnet ein ContextMenu an der �bergebenen Position
	 * 
	 * @param x
	 *            x-Koordinate des Menus.
	 * @param y
	 *            y-Koordinate des Menus.
	 */
	public void openContextMenu(int x, int y) {
		for (int i = 0; i < this.renderables.size(); i++) {
			Renderable temp = this.renderables.get(i).hitTest(x, y);
			if (temp != null) {
				this.openContextMenu(temp, x, y, false);
				return;
			}
		}

		Renderable temp = this.rootElement.hitTest(x, y);
		if (temp != null) {
			this.openContextMenu(temp, x, y, false);
		}
	}

	/**
	 * �ffnet ein ContextMenu an der �bergebenen Position, das das �bergebene
	 * Renderable bearbeiten soll.
	 * 
	 * @param elem
	 *            Das Element, das mit diesem Menu bearbeitet wird.
	 * @param x
	 *            x-Koordinate des Menus.
	 * @param y
	 *            y-Koordinate des Menus.
	 * @param elementList
	 *            Wurde diese Methode aus der ElementList heraus aufgerufen.
	 */
	public void openContextMenu(Renderable elem, int x, int y,
			boolean elementList) {
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();

		boolean en = this.getStorageController().existsMethod(elem.getName());

		MenuItem temp = new MenuItem("�ffnen", new OpenClickHandler(
				elem.getName()));
		temp.setEnabled(en);
		items.add(temp);

		MenuItem renameItem = new MenuItem("Umbenennen",
				new RenameClickHandler(elem.getName()));
		renameItem.setEnabled(en);
		items.add(renameItem);

		String delName = elementList ? "L�schen" : "Entfernen";
		ClickHandler delHandler = elementList ? new DeleteClickHandler(
				elem.getName()) : new RemoveClickHandler(elem);
		MenuItem deleteItem = new MenuItem(delName, delHandler);
		deleteItem.setEnabled(!elementList || en);
		items.add(deleteItem);

		// dibo 13.08.2010 2 neue Items
		if (!elementList) {
			MenuItem procItem = new MenuItem("Auslagern in Prozedur",
					new SwapClickHandler(elem, RType.VOID));
			boolean en2 = !BooleanObject.class.isInstance(elem);
			procItem.setEnabled(en2);
			items.add(procItem);

			MenuItem funcItem = new MenuItem("Auslagern in Funktion",
					new SwapClickHandler(elem, RType.BOOLEAN));
			boolean en3 = true;
			funcItem.setEnabled(en3);
			items.add(funcItem);
		}

		this.openContextMenu(items, x, y);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			this.openContextMenu(e.getX(), e.getY());
			return;
		}

		if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() % 2 != 0)
			return;

		for (int i = 0; i < this.renderables.size(); i++) {
			Renderable temp = this.renderables.get(i).hitTest(e.getX(),
					e.getY());
			if (temp != null) {
				this.openTab(temp.getName());
				return;
			}
		}

		Renderable temp = this.rootElement.hitTest(e.getX(), e.getY());
		if (temp != null) {
			this.openTab(temp.getName());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// textArea.getFile().setModified(true);

		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}

		for (int i = this.renderables.size() - 1; i >= 0; i--) {
			Renderable temp = this.renderables.get(i).hitTest(e.getX(),
					e.getY());
			if (temp != null) {
				this.selected = temp;
				Renderable selectedParent = this.selected.parent;

				if (this.selected.removeFromParent()) {
					this.addRenderable(this.selected);
					selectedParent.updateBounds();
					selectedParent.getRootElement().updateBounds();
					selectedParent.getRootElement().updateChilds();
				} else {
					// Bring to top
					this.renderables.remove(this.selected);
					this.renderables.add(this.selected);
				}
				this.tempClick = e.getPoint();
				return;
			}
		}

		// Abdocken vom Root Element
		Renderable temp = this.rootElement.hitTest(e.getX(), e.getY());
		if (temp != null) {
			this.selected = temp;
			Renderable selectedParent = this.selected.parent;

			if (this.selected.removeFromParent()) {
				this.addRenderable(this.selected);
				selectedParent.updateBounds();
				selectedParent.getRootElement().updateBounds();
				selectedParent.getRootElement().updateChilds();
			} else {
				// Bring to top
				this.renderables.remove(this.selected);
				this.renderables.add(this.selected);
			}
			this.tempClick = e.getPoint();
			return;
		}

		this.selected = null;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}

		if (this.selected == null) {
			return;
		}

		// Andocken an Root Element
		boolean docked = false;
		int dockIndex = this.rootElement.dockingTest(this.selected, this);
		if (dockIndex >= 0) {
			this.docking.add(this.selected, dockIndex);
			this.renderables.remove(this.selected);
			this.docking.updateBounds();
			this.docking.getRootElement().updateChilds();
			docked = true;
		} else if (dockIndex == -2) {
			this.docking.addAsParent(this.selected);
			this.docking.updateBounds();
			this.selected.getRootElement().updateChilds();
			this.renderables.remove(this.docking);
			docked = true;
		} else if (dockIndex == -3) {
			this.docking.addAsNext(this.selected);
			this.selected.updateBounds();
			this.selected.getRootElement().updateChilds();
			this.renderables.remove(this.selected);
			docked = true;
		}

		if (!docked) {
			// Andocken an eines der anderen Elemente
			for (Renderable r : this.renderables) {
				dockIndex = r.dockingTest(this.selected, this);
				if (dockIndex >= 0) {
					this.docking.add(this.selected, dockIndex);
					this.renderables.remove(this.selected);
					this.docking.updateBounds();
					this.docking.getRootElement().updateChilds();
					break;
				} else if (dockIndex == -2) {
					this.docking.addAsParent(this.selected);
					this.docking.updateBounds();
					this.selected.getRootElement().updateChilds();
					this.renderables.remove(this.docking);
					break;
				} else if (dockIndex == -3) {
					this.docking.addAsNext(this.selected);
					this.selected.updateBounds();
					this.selected.getRootElement().updateChilds();
					this.renderables.remove(this.selected);
					break;
				}
			}
		}

		if (this.selected.getY() < ScratchPanel.TAB_PANEL_HEIGHT) {
			this.selected.moveRelative(0, ScratchPanel.TAB_PANEL_HEIGHT
					- this.selected.getY());
		}

		if (this.selected.getX() < ScratchPanel.OPTIONS_WIDTH) {
			if (e.getX() < ScratchPanel.OPTIONS_WIDTH) {
				this.renderables.remove(this.selected);
			} else {
				this.selected.moveTo(ScratchPanel.OPTIONS_WIDTH,
						this.selected.getY());
			}
		}

		this.selected = null;
		this.updateScrollbars();
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.selected == null) {
			return;
		}

		this.selected.moveRelative(e.getX() - (int) this.tempClick.getX(),
				e.getY() - (int) this.tempClick.getY());
		this.modified(); // dibo 290710

		this.tempClick = e.getPoint();
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void onDelete(TabButton button) {
		this.onDelete(button.getName());
	}

	/**
	 * L�scht die Methode mit dem �bergebenen Namen.
	 * 
	 * @param name
	 *            Name der Methode, die gel�scht werden soll.
	 */
	private void onDelete(String name) {
		int confirmResult = JOptionPane
				.showConfirmDialog(
						null,
						"Soll die Methode \""
								+ name
								+ "\" gel�scht werden? Dieser Vorgang kann nicht r�ckg�ngig gemacht werden!",
						"L�schen", JOptionPane.YES_NO_OPTION);

		if (confirmResult == JOptionPane.YES_OPTION) {
			try {
				this.getStorageController().deleteMethod(name);
				this.tabPanel.closeTab(name);
				this.optionsPanel.refreshElementList();
			} catch (DeleteMethodException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler",
						JOptionPane.OK_OPTION);
			}
		}
	}

	@Override
	public void onRename(String name) {
		new RenameFunctionFrame(this, name);
		this.modified();
	}

	/**
	 * Entfernt das angegebene Element und vereinigt das Vaterelement mit dem
	 * Kindelement.
	 * 
	 * Anmerkung dibo: Ist nun so angepasst, dass neben dem Element selber nun
	 * immer auch alle Kinder des Elementes entfernt werden
	 * 
	 * @param elem
	 *            Element, das entfernt werden soll.
	 */
	public void onRemove(Renderable elem) {
		Renderable root = elem.getRootElement();
		Renderable parent = elem.parent;
		Renderable child = elem.next;
		this.modified();

		elem.removeFromParent();

		/*
		 * dibo 13.08.2010 if (child != null) { child.removeFromParent(); }
		 */

		if (parent == null) {
			if (child != null) {
				// dibo this.addRenderable(child); 13.08.2010
			}

			this.renderables.remove(elem);
			return;
		}

		if (child != null) {
			// dibo parent.addAsNext(child); 13.08.2010
		}

		this.renderables.remove(elem);
		parent.updateBounds();
		root.updateBounds();
		root.updateChilds();
		this.repaint();
	}

	// dibo 13.08.2010
	/**
	 * Auslagerung des �bergebenen Elementes in eine Prozedur oder Funktion
	 */
	public void onSwap(Renderable elem, RType rType) {
		new SwapFunctionFrame(this, rType, elem);
		this.updateScrollbars();
		this.repaint();
	}

	/**
	 * tauscht aus
	 * 
	 * @param name
	 * @param rType
	 * @param removeElem
	 * @return
	 */
	public Method swapMethod(String name, RType rType, Renderable removeElem) {
		Method method = null;
		switch (rType) {
		case VOID:
			method = this.getStorageController().addVoidMethod(name);
			break;
		case BOOLEAN:
			method = this.getStorageController().addBooleanMethod(name);
			break;
		}

		// Instanz erzeugen
		Renderable newElem = ScratchUtils.getRenderableByName(name, rType);

		// Stack gegen neue Prozedur austauschen
		this.onChange(removeElem, newElem, rType);

		// Stack in neue Prozedur einfuegen
		if (!BooleanObject.class.isInstance(removeElem)) {
			method.getRootElement().addAsNext(removeElem);
			method.getRootElement().updateChilds();
		} else {
			Renderable p = new ReturnBooleanObject();
			p.add(removeElem, 0);
			p.updateBounds();
			p.updateChilds();
			method.getRootElement().addAsNext(p);
			method.getRootElement().updateChilds();
		}

		this.tabPanel.addTab(name);
		this.optionsPanel.refreshElementList();
		this.modified();
		return method;
	}

	// dibo 13.08.2010
	/**
	 * tauscht aus
	 */
	public void onChange(Renderable removeElem, Renderable newElem, RType rType) {
		Renderable root = removeElem.getRootElement();
		Renderable parent = removeElem.parent;

		int index = 0;
		if (parent != null) {
			index = parent.getChildIndex(removeElem);
		}

		removeElem.removeFromParent();
		this.renderables.remove(removeElem);

		if (rType == RType.VOID) {
			if (parent == null) {
				newElem.setX(removeElem.getX());
				newElem.setY(removeElem.getY());
				this.renderables.add(newElem);
				newElem.updateBounds();
				newElem.updateChilds();
				newElem.updatePosition();
			} else {
				if (index == 0) {
					parent.addAsNext(newElem);
				} else {
					parent.add(newElem, index);
				}
				parent.updateBounds();
				parent.updateChilds();
			}
		} else { // BOOLEAN
			if (parent == null) {
				newElem.setX(removeElem.getX());
				newElem.setY(removeElem.getY());
				this.renderables.add(newElem);
				newElem.updateBounds();
				newElem.updateChilds();
				newElem.updatePosition();
			} else {
				parent.add(newElem, index);
				parent.updateBounds();
				parent.updateChilds();
			}
		}

		root.updateBounds();
		root.updateChilds();

		this.modified();
		this.repaint();
	}

	/**
	 * Benennt dalle Elemente mit dem Namen fromName in toName
	 * 
	 * @param fromName
	 * @param toName
	 */
	public void renameMethod(String fromName, String toName) {
		try {
			this.getStorageController().rename(fromName, toName);
			this.tabPanel.renameTab(fromName, toName);
			this.optionsPanel.refreshElementList();
			this.modified();
			this.repaint();
		} catch (RenameMethodException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler",
					JOptionPane.OK_OPTION);
		}
	}

	@Override
	public void tabChanged(TabButton button) {
		Method method = this.getStorageController().getMethod(button.getName());
		if (method == null) {
			return;
		}

		int left = method.getLeft();
		int top = method.getTop();
		this.method = method;
		this.renderables = method.getRenderables();
		this.rootElement = method.getRootElement();

		this.hScrollbar.setValue(0);
		this.vScrollbar.setValue(0);
		this.updateScrollbars();
		this.hScrollbar.setValue(left);
		this.vScrollbar.setValue(top);
	}

	@Override
	public void refresh() {
		this.repaint();
	}

	/**
	 * Diese Klasse ist der LocationChangeHandler f�r den vertikalen
	 * Scrollbalken. Die darin enthaltene Methode onLocationChange() wird
	 * aufgerufen, sobald sich die Position des vertikalen Scrollbalkens
	 * ver�ndert. Dabei wird der sichtbare Bereich abh�ngig von der Position des
	 * Scrollbakens verschoben.
	 * 
	 * @author HackZ
	 * 
	 */
	class VerticalLocationHandler implements LocationChangeHandler {
		@Override
		public void onLocationChanged() {
			int delta = ScratchPanel.this.vScrollbar.getValue()
					- ScratchPanel.this.method.getTop();
			ScratchPanel.this.method.setTop(ScratchPanel.this.vScrollbar
					.getValue());
			for (Renderable r : ScratchPanel.this.renderables) {
				r.moveRelative(0, -delta);
			}

			ScratchPanel.this.rootElement.moveRelative(0, -delta);

			ScratchPanel.this.repaint();
		}
	}

	/**
	 * Diese Klasse ist der LocationChangeHandler f�r den horizontalen
	 * Scrollbalken. Die darin enthaltene Methode onLocationChange() wird
	 * aufgerufen, sobald sich die Position des horizontalen Scrollbalkens
	 * ver�ndert. Dabei wird der sichtbare Bereich abh�ngig von der Position des
	 * Scrollbakens verschoben.
	 * 
	 * @author HackZ
	 * 
	 */
	class HorizontalLocationHandler implements LocationChangeHandler {
		@Override
		public void onLocationChanged() {
			int delta = ScratchPanel.this.hScrollbar.getValue()
					- ScratchPanel.this.method.getLeft();
			ScratchPanel.this.method.setLeft(ScratchPanel.this.hScrollbar
					.getValue());
			for (Renderable r : ScratchPanel.this.renderables) {
				r.moveRelative(-delta, 0);
			}

			ScratchPanel.this.rootElement.moveRelative(-delta, 0);

			ScratchPanel.this.repaint();
		}
	}

	/**
	 * Dieser ClickHandler wird an das ContextMenu �bergeben und die darin
	 * enthaltene Methode onClick wird aufgerufen, wenn auf das MenuItem
	 * "�ffnen" geklickt wurde und somit ein neues Tab ge�ffnet werden soll.
	 * 
	 * @author HackZ
	 * 
	 */
	class OpenClickHandler implements ClickHandler {
		private String name;

		public OpenClickHandler(String name) {
			this.name = name;
		}

		@Override
		public void onClick() {
			ScratchPanel.this.openTab(this.name);
		}
	}

	/**
	 * Dieser ClickHandler wird an das ContextMenu �bergeben und die darin
	 * enthaltene Methode onClick wird aufgerufen, wenn auf das MenuItem
	 * "umbenennen" geklickt wurde. Dies soll das RenameFrame �ffnen, wo der
	 * andere Name eingegeben werden soll.
	 * 
	 * @author HackZ
	 * 
	 */
	class RenameClickHandler implements ClickHandler {
		private String name;

		public RenameClickHandler(String name) {
			this.name = name;
		}

		@Override
		public void onClick() {
			ScratchPanel.this.onRename(this.name);
		}
	}

	/**
	 * Dieser ClickHandler wird an das ContextMenu �bergeben und die darin
	 * enthaltene Methode onClick wird aufgerufen, wenn auf das MenuItem
	 * "l�schen" geklickt wurde. Dies soll das Element mit dem Namen l�schen,
	 * wenn es nirgendwo gebraucht wurde.
	 * 
	 * @author HackZ
	 * 
	 */
	class DeleteClickHandler implements ClickHandler {
		private String name;

		public DeleteClickHandler(String name) {
			this.name = name;
		}

		@Override
		public void onClick() {
			ScratchPanel.this.onDelete(this.name);
		}
	}

	/**
	 * Dieser ClickHandler wird an das ContextMenu �bergeben und die darin
	 * enthaltene Methode onClick wird aufgerufen, wenn auf das MenuItem
	 * "entfernen" geklickt wurde. Dies soll das Element vom Panel entfernen.
	 * 
	 * @author HackZ
	 * 
	 */
	class RemoveClickHandler implements ClickHandler {
		private Renderable elem;

		public RemoveClickHandler(Renderable elem) {
			this.elem = elem;
		}

		@Override
		public void onClick() {
			ScratchPanel.this.onRemove(this.elem);
		}
	}

	// dibo 13.08.2010
	/**
	 * Dieser ClickHandler wird an das ContextMenu �bergeben und die darin
	 * enthaltene Methode onClick wird aufgerufen, wenn auf das MenuItem
	 * "auslagern" geklickt wurde.
	 * 
	 * @author HackZ
	 * 
	 */
	class SwapClickHandler implements ClickHandler {
		private Renderable elem;
		private RType rType;

		public SwapClickHandler(Renderable elem, RType rType) {
			this.elem = elem;
			this.rType = rType;
		}

		@Override
		public void onClick() {
			ScratchPanel.this.onSwap(this.elem, this.rType);
		}
	}

	/**
	 * Dieser Handler sorgt daf�r, dass neu ge�ffnete Tabs im StorageController
	 * als offen markiert werden und geschlossene Tabs als geschlossen. Das
	 * enablet auch den Speichernbutton, da die offenen Tabs mitgespeichert
	 * werden.
	 * 
	 * @author HackZ
	 * 
	 */
	class OpenedMethodHandler implements TabOpenedHandler {
		public void tabOpened(String name, boolean opened) {
			ScratchPanel.this.getStorageController().setOpened(name, opened);
			ScratchPanel.this.modified();
		}
	}

	/**
	 * Dieser Handler wird an den StorageController �bergeben, der die darin
	 * enthaltene Methode nextMethod aufruft, falls bei Schrittweiser Ausf�hrung
	 * der Tab ge�nder werden soll.
	 * 
	 * @author HackZ
	 * 
	 */
	class OnNextMethodHandler implements NextMethodHandler {
		public void nextMethod(String name) {
			ScratchPanel.this.tabPanel.addTab(name);
		}
	}
}
