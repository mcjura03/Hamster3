package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.hamster.scratch.BoundingBox;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchPanel;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.Renderable.RType;
import de.hamster.workbench.Utils;

/**
 * Eine ElementList dient zur Vorschau von Elementen ener
 * Kategorie. Dabei werden die verschiedenen Elemente durch
 * die Methode addElement(Renderable r) hinzugefügt und
 * automatisch untereinander angezeigt. Wird die Größe
 * der ElementList so weit geändert, dass nicht alle Elemente
 * angezeigt werden können, so erscheinen Scrollbalken, die
 * den sichtbaren Bereich verschieben.
 * @author HackZ
 *
 */
public class ElementList extends JPanel implements MouseListener, MouseMotionListener, RefreshHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1262273016948709460L;
	public static final int ELEMENT_SPACING = 7;
	public static final int ELEMENT_LEFT = 10;
	public static Color BACKGROUND_COLOR = new Color(124, 128, 131);

	private static BufferedImage imgTop;
	private static BufferedImage imgBottom;
	private static BufferedImage imgLeft;
	private static BufferedImage imgRight;
	private static BufferedImage imgTopLeft;
	private static BufferedImage imgTopRight;
	private static BufferedImage imgBottomLeft;
	private static BufferedImage imgBottomRight;
	
	private ArrayList<Renderable> elements;
	private int currentInsertY;
	private ScratchPanel parent;
	private int top = 0;
	private int left = 0;
	private int maxWidthElement = 0;
	private ScratchScrollBar vScrollbar;
	private ScratchScrollBar hScrollbar;
	private ElementListButton createVoidButton;
	private ElementListButton createBoolButton;
	
	/**
	 * Erzeugt eine neue ElementList mit der Referenz auf das
	 * Parent-Objekt, in das neue Elemente, die aus der Liste
	 * ausgewählt wurden, hinzugefürt werden. Beim ersten
	 * Erstellen werden die dazugehörigen Bilder geladen.
	 * @param parent
	 * Der Renderer, in das gewählte Elemente aus der Liste
	 * hinzugefügt werden.
	 */
	public ElementList(ScratchPanel parent) {
		this.parent = parent;
		elements = new ArrayList<Renderable>();
		currentInsertY = ELEMENT_SPACING;
		this.setBackground(BACKGROUND_COLOR);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setLayout(null);
		
		vScrollbar = new ScratchScrollBar(ScratchScrollBar.VERTICAL);
		vScrollbar.setBounds(getWidth() - 25, 10, ScratchScrollBar.WIDTH, getHeight() - 20);
		vScrollbar.addLocationChangeHandler(new VerticalLocationHandler());
		this.add(vScrollbar);
		
		hScrollbar = new ScratchScrollBar(ScratchScrollBar.HORIZONTAL);
		hScrollbar.setBounds(10, getHeight() - 25, getWidth() - 20, ScratchScrollBar.WIDTH);
		hScrollbar.addLocationChangeHandler(new HorizontalLocationHandler());
		this.add(hScrollbar);
		
		// Buttons erstellen, aber nicht anzeigen
		createVoidButton = new ElementListButton(Utils.getNewProcedure());
		this.addMouseListener(createVoidButton);
		this.addMouseMotionListener(createVoidButton);
		createVoidButton.addRefreshHandler(this);
		createVoidButton.addClickHandler(new NewVoidClickHandler());
		
		createBoolButton = new ElementListButton(Utils.getNewFunction());
		this.addMouseListener(createBoolButton);
		this.addMouseMotionListener(createBoolButton);
		createBoolButton.addRefreshHandler(this);
		createBoolButton.addClickHandler(new NewBooleanClickHandler());
		
		loadImages();
	}
	
	/**
	 * Wird im Konstruktor aufgerufen und lädt LookAndFeel Bilder.
	 * Diese Methode wird nur einmalig ausgeführt, da die Bilder
	 * statisch in der Klasse sind.
	 */
	private void loadImages() {
		if (imgTop != null)
			return; // Bilder sind bereits geladen

		imgTop = ScratchUtils.getImage("scratch/backgrounds/top_elementlist.png");
		imgBottom = ScratchUtils.getImage("scratch/backgrounds/bottom_elementlist.png");
		imgLeft = ScratchUtils.getImage("scratch/backgrounds/left.png");
		imgRight = ScratchUtils.getImage("scratch/backgrounds/right.png");
		imgTopLeft = ScratchUtils.getImage("scratch/backgrounds/top_left_elementlist.png");
		imgTopRight = ScratchUtils.getImage("scratch/backgrounds/top_right_elementlist.png");
		imgBottomLeft = ScratchUtils.getImage("scratch/backgrounds/bottom_left_elementlist.png");
		imgBottomRight = ScratchUtils.getImage("scratch/backgrounds/bottom_right_elementlist.png");
	}
	
	/**
	 * Wird aufgerufen, wenn ein Scratch-Programm gestartet bzw. beendet wird.
	 * Während einer Programm-Ausführung sollte das Programm nicht verändert
	 * werden können (locked == true)
	 */
	public void setLocked(boolean locked) {
		if (locked) {
			this.removeMouseListener(this);
			this.removeMouseListener(createBoolButton);
			this.removeMouseListener(createVoidButton);
			this.removeMouseMotionListener(createBoolButton);
			this.removeMouseMotionListener(createVoidButton);
		} else {
			this.addMouseListener(this);
			this.addMouseListener(createBoolButton);
			this.addMouseListener(createVoidButton);
			this.addMouseMotionListener(createBoolButton);
			this.addMouseMotionListener(createVoidButton);
		}
	}
	
	/**
	 * Fügt ein neues Element der Liste hinzu. Das übergebene
	 * Renderable <tt>r</tt> wird dabei ganz unten mit einem
	 * Abstand <tt>ELEMENT_SPACING</tt> zum vorangegangenen
	 * Element eingefügt und die Liste wird neu gezeichnet.
	 * @param r
	 */
	public void addElement(Renderable r) {
		elements.add(r);
		r.moveTo(ELEMENT_LEFT, currentInsertY);
		
		BoundingBox temp = r.getGlobalBounding();
		int elemHeight = temp.getHeight();
		maxWidthElement = (temp.getWidth() > maxWidthElement) ? temp.getWidth() : maxWidthElement;
		currentInsertY += elemHeight += ELEMENT_SPACING;
		updateScrollbars();
		repaint();
	}
	
	public void addCreateButton(RType rType) {
		if (rType == RType.VOID) {
			createVoidButton.setPosition(ELEMENT_LEFT, currentInsertY);
			createVoidButton.setVisible(true);
			currentInsertY += ElementListButton.HEIGHT + ELEMENT_SPACING;
		}
		if (rType == RType.BOOLEAN) {
			createBoolButton.setPosition(ELEMENT_LEFT, currentInsertY);
			createBoolButton.setVisible(true);
			currentInsertY += ElementListButton.HEIGHT + ELEMENT_SPACING;
		}
	}
	
	/**
	 * Löscht alle Elemente aus der Liste und zeichnet
	 * die Komponente neu.<br />
	 * Diese Methode sollte verwendet werden, um neue
	 * Kategorien zu laden (Es wird keine neue ElementList
	 * erzeugt).
	 */
	public void clearList() {
		createVoidButton.setVisible(false);
		createBoolButton.setVisible(false);
		top = 0;
		left = 0;
		maxWidthElement = 0;
		elements.clear();
		currentInsertY = ELEMENT_SPACING;
		updateScrollbars();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		paintBackground(g);
		
		for (Renderable r : elements)
			r.render(g);
		
		super.paintChildren(g);
		createVoidButton.paint(g);
		createBoolButton.paint(g);
		paintBorders(g);
	}
	
	/**
	 * Zeichnet den Hintergrund der Komponente in angegebener
	 * Hintergrundfarbe.
	 * @param g
	 * Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBackground(Graphics g) {
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	/**
	 * Zeichnet die Ränder und Ecken der Komponente mit den dafür
	 * geladenen Bildern. Die Ränder werden zum Schluss gezeichnet,
	 * so dass Transparenzeffekte möglich sind und die Elemente
	 * der Liste unter den Rändern erscheinen.
	 * @param g
	 * Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBorders(Graphics g) {
//		g.drawImage(imgTop, 0, 0, getWidth(), 5, BACKGROUND_COLOR, null);
		for (int i = 0; i < imgTop.getHeight(); i++) {
			g.setColor(new Color(imgTop.getRGB(0, i)));
			g.drawLine(0, i, getWidth(), i);
		}
		
//		g.drawImage(imgBottom, 0, getHeight() - 8, getWidth(), 8, BACKGROUND_COLOR, null);
		for (int i = 0; i < imgBottom.getHeight(); i++) {
			int top = i + getHeight() - imgBottom.getHeight();
			g.setColor(new Color(imgBottom.getRGB(0, i)));
			g.drawLine(0, top, getWidth(), top);
		}
		
//		g.drawImage(imgLeft, 0, 0, 8, getHeight(), BACKGROUND_COLOR, null);
		for (int i = 0; i < imgLeft.getWidth(); i++) {
			g.setColor(new Color(imgLeft.getRGB(i, 0)));
			g.drawLine(i, 0, i, getHeight());
		}
		
//		g.drawImage(imgRight, getWidth() - 5, 0, 5, getHeight(), BACKGROUND_COLOR, null);
		for (int i = 0; i < imgRight.getWidth(); i++) {
			int left = i + getWidth() - imgRight.getWidth();
			g.setColor(new Color(imgRight.getRGB(i, 0)));
			g.drawLine(left, 0, left, getHeight());
		}
		
		g.drawImage(imgTopLeft, 0, 0, null);
		g.drawImage(imgTopRight, getWidth() - imgTopRight.getWidth(), 0, null);
		g.drawImage(imgBottomLeft, 0, getHeight() - imgBottomLeft.getHeight(), null);
		g.drawImage(imgBottomRight, getWidth() - imgBottomRight.getWidth(), getHeight() - imgBottomRight.getHeight(), null);
	}
	
	/**
	 * Liefert die gesamte Höhe des gesamt möglichen sichtbaren Bereichs.
	 * Die Höhe aller Elemente untereinander mit den <tt>ELEMENT_SPACING</tt>.
	 * @return
	 */
	public int getInnerHeight() {
		return currentInsertY + ELEMENT_SPACING + (hScrollbar.isVisible() ? 25 : 0);
	}
	
	/**
	 * Liefert die gesamte Breite des gesamt möglichen sichtbaren Bereichs
	 * Die Breite des breitesten Elements.
	 * @return
	 */
	public int getInnerWidth() {
		return 2 * ELEMENT_LEFT + maxWidthElement + (vScrollbar.isVisible() ? 25 : 0);
	}
	
	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		updateScrollbars();
		vScrollbar.setBounds(getWidth() - 25, 10, ScratchScrollBar.WIDTH, getHeight() - (hScrollbar.isVisible() ? 40 : 20));
		hScrollbar.setBounds(10, getHeight() - 25, getWidth() - (vScrollbar.isVisible() ? 40 : 20), ScratchScrollBar.WIDTH);
	}
	
	@Override
	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Berechnet die notwendige Breite und Höhe für die Darstellung
	 * aller Elemente und zeigt die Scrollbalken, falls notwendig.
	 * Diese Methode ruft intern zwei mal updateScrollbarsIntern()
	 * auf, da diese den horizontalen und den verticalen Balken
	 * berechnet und dabei sowohl der erste vom zweiten, als auch
	 * der zweite vom ersten abhängig ist.
	 */
	private void updateScrollbars() {
		updateScrollbarsIntern();
		updateScrollbarsIntern();
	}
	
	/**
	 * Berechnet die notwendige Breite und Höhe für die Darstellung
	 * aller Elemente und zeigt die Scrollbalken, falls notwendig.
	 */
	private void updateScrollbarsIntern() {
		int vScrollHeight = getInnerHeight() - getHeight();
		if (vScrollHeight > 0) {
			vScrollbar.setVisible(true);
			vScrollbar.setMaximum(vScrollHeight);
		}
		else {
			vScrollbar.setVisible(false);
			createVoidButton.moveRelative(0, top);
			createBoolButton.moveRelative(0, top);
			for (Renderable r : elements)
				r.moveRelative(0, top);
			top = 0;
		}
		
		int hScrollWidth = getInnerWidth() - getWidth();
		if (hScrollWidth > 0) {
			hScrollbar.setVisible(true);
			hScrollbar.setMaximum(hScrollWidth);
		}
		else {
			hScrollbar.setVisible(false);
			createVoidButton.moveRelative(left, 0);
			createBoolButton.moveRelative(left, 0);
			for (Renderable r : elements)
				r.moveRelative(left, 0);
			left = 0;
		}
	}

	@Override
	public void refresh() {
		repaint();
	}
	
	public void openContextMenu(int x, int y) {
		for (int i = 0; i < elements.size(); i++) {
			Renderable temp = elements.get(i).hitTest(x, y);
			if (temp != null) {
				parent.openContextMenu(temp, x, y + ScratchPanel.OPTIONS_HEIGHT, true);
				return;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			openContextMenu(e.getX(), e.getY());
			return;
		}
		
		if (e.getButton() != MouseEvent.BUTTON1 || (e.getClickCount() % 2) != 0)
			return;
		
		for (int i = 0; i < elements.size(); i++) {
			Renderable temp = elements.get(i).hitTest(e.getX(), e.getY());
			if (temp != null) {
				parent.openTab(temp.getName());
				return;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		
		for (Renderable r : elements) {
			Renderable temp = r.hitTest(e.getX(), e.getY());
			if (temp != null) {
				Renderable newRenderable = temp.clone();
				newRenderable.moveTo(temp.getX(), temp.getY() + ScratchPanel.OPTIONS_HEIGHT);
				parent.addRenderable(newRenderable, e);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) { 
		parent.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		parent.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) { }
	
	/**
	 * Diese Klasse ist der LocationChangeHandler für den vertikalen
	 * Scrollbalken. Die darin enthaltene Methode onLocationChange()
	 * wird aufgerufen, sobald sich die Position des vertikalen
	 * Scrollbalkens verändert. Dabei wird der sichtbare Bereich
	 * abhängig von der Position des Scrollbakens verschoben.
	 * @author HackZ
	 *
	 */
	class VerticalLocationHandler implements LocationChangeHandler {
		@Override
		public void onLocationChanged() {
			int delta = vScrollbar.getValue() - top;
			top = vScrollbar.getValue();
			createVoidButton.moveRelative(0, -delta);
			createBoolButton.moveRelative(0, -delta);
			for (Renderable r : elements)
				r.moveRelative(0, -delta);
			
			repaint();
		}
	}
	
	/**
	 * Diese Klasse ist der LocationChangeHandler für den horizontalen
	 * Scrollbalken. Die darin enthaltene Methode onLocationChange()
	 * wird aufgerufen, sobald sich die Position des horizontalen
	 * Scrollbalkens verändert. Dabei wird der sichtbare Bereich
	 * abhängig von der Position des Scrollbakens verschoben.
	 * @author HackZ
	 *
	 */
	class HorizontalLocationHandler implements LocationChangeHandler {
		@Override
		public void onLocationChanged() {
			int delta = hScrollbar.getValue() - left;
			left = hScrollbar.getValue();
			createVoidButton.moveRelative(-delta, 0);
			createBoolButton.moveRelative(-delta, 0);
			for (Renderable r : elements)
				r.moveRelative(-delta, 0);
			
			repaint();
		}
	}
	
	class NewVoidClickHandler implements ClickHandler {
		@Override
		public void onClick() {
			new CreateFunctionFrame(parent, RType.VOID);
		}
	}
	
	class NewBooleanClickHandler implements ClickHandler {
		@Override
		public void onClick() {
			new CreateFunctionFrame(parent, RType.BOOLEAN);
		}
	}
}
