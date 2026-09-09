package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

import de.hamster.scratch.ScratchUtils;

/**
 * Ein ScrollBalken, mit dem der sichtbare Bereich
 * anderer Komponenten verändert werden kann. Dafür 
 * muss dem ScratchScrollBar ein LocationChangedHandler
 * hinzugefügt werden, dessen Methode onLocationChanged()
 * aufgerufen wird, sobald der Scrollbalken bewegt wird.
 * @author HackZ
 *
 */
public class ScratchScrollBar extends JComponent implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5539671868626801354L;
	
	public static final int MIN_BAR_SPACING = 3;
	public static final int MIN_BAR_SIZE = 12;
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;
	public static final int WIDTH = 16;
	public static final int TRANSPARENCY = 128;
	
	private static BufferedImage imgTop;
	private static BufferedImage imgBottom;
	private static BufferedImage imgLeft;
	private static BufferedImage imgRight;
	private static BufferedImage imgMiddle;
	private static BufferedImage imgBarTop;
	private static BufferedImage imgBarBottom;
	private static BufferedImage imgBarLeft;
	private static BufferedImage imgBarRight;
	private static BufferedImage imgBarMiddle;
	
	private int orientation = 0;
	private int minimum = 0;
	private int maximum = 0;
	private int value = 0;
	private int panelWidth;
	private int panelHeight;
	private int barPosition = 0;
	private int maxBarPosition = 0;
	private int barSize = 0;
	private boolean pressed = false;
	private int startDown = 0;
	private int startPos = 0;
	private ArrayList<LocationChangeHandler> locationHandlers = new ArrayList<LocationChangeHandler>();
	
	/**
	 * Erzeugt einen neuen ScrollBalken mit der Ausrichtung
	 * <tt>orientation</tt>. Dieser Parameter kann nur die
	 * Werte der Konstanten <tt>VERTICAL</tt> oder <tt>HORIZONTAL</tt>
	 * annehmen. Beim ersten Erstellen werden die dazugehörigen
	 * Bilder geladen.
	 * @param orientation
	 * <tt>VERTICAL</tt> oder <tt>HORIZONTAL</tt>
	 */
	public ScratchScrollBar(int orientation) {
		this.orientation = orientation;
		this.setPanelSize(100, 500);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
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

		imgTop = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bg_top.png");
		imgBottom = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bg_bottom.png");
		imgLeft = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bg_left.png");
		imgRight = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bg_right.png");
		imgMiddle = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bg.png");
		imgBarTop = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bar_top.png");
		imgBarBottom = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bar_bottom.png");
		imgBarLeft = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bar_left.png");
		imgBarRight = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bar_right.png");
		imgBarMiddle = ScratchUtils.getImage("scratch/backgrounds/scrollbar/bar.png");
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintBackground(g);
		paintBar(g);
	}
	
	/**
	 * Zeichnet den Hintergrund des Scrollbalkens abhängig
	 * von der im Konstruktor ausgewählten Ausrichtung.
	 * Ein semitransparenter, grauer Hintergrund, mit
	 * abgerundeten Kanten.
	 * @param g
	 * Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBackground(Graphics g) {
		if (orientation == VERTICAL) {
			if (getHeight() <= WIDTH)
				return;
			
			g.drawImage(imgTop, 0, 0, null);
			g.drawImage(imgBottom, 0, getHeight() - imgBottom.getHeight(), null);
			
			for (int i = 0; i < imgMiddle.getWidth(); i++) {
				Color c = new Color(imgMiddle.getRGB(i, 0));
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TRANSPARENCY));
				g.drawLine(i, imgTop.getHeight(), i, getHeight() - imgBottom.getHeight() - 1);
			}
		} else {
			if (getWidth() <= WIDTH)
				return;
			
			g.drawImage(imgLeft, 0, 0, null);
			g.drawImage(imgRight, getWidth() - imgRight.getWidth(), 0, null);
			
			for (int i = 0; i < imgMiddle.getWidth(); i++) {
				Color c = new Color(imgMiddle.getRGB(i, 0));
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TRANSPARENCY));
				g.drawLine(imgLeft.getWidth(), i, getWidth() - imgRight.getWidth() - 1, i);
			}
		}
	}
	
	/**
	 * Zeichnet den Scrollbalken  abhängig von der im Konstruktor
	 * ausgewählten Ausrichtung und der internen grössen.
	 * @param g
	 * Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBar(Graphics g) {
		if (orientation == VERTICAL) {
			if (getHeight() <= WIDTH)
				return;
			
			g.drawImage(imgBarTop, 3, barPosition + MIN_BAR_SPACING, null);
			g.drawImage(imgBarBottom, 3, barPosition + barSize - imgBarBottom.getHeight() + MIN_BAR_SPACING, null);
			
			for (int i = 0; i < imgBarMiddle.getWidth(); i++) {
				g.setColor(new Color(imgBarMiddle.getRGB(i, 0)));
				g.drawLine(i + 3, barPosition + imgBarTop.getHeight() + MIN_BAR_SPACING, i + 3, barPosition + barSize - imgBarBottom.getHeight() + MIN_BAR_SPACING - 1);
			}
		} else {
			if (getWidth() <= WIDTH)
				return;
			
			g.drawImage(imgBarLeft, barPosition + MIN_BAR_SPACING, 3, null);
			g.drawImage(imgBarRight, barPosition + barSize - imgBarRight.getWidth() + MIN_BAR_SPACING, 3, null);

			for (int i = 0; i < imgBarMiddle.getWidth(); i++) {
				g.setColor(new Color(imgBarMiddle.getRGB(i, 0)));
				g.drawLine(barPosition + imgBarLeft.getWidth() + MIN_BAR_SPACING, i + 3, barPosition + barSize - imgBarLeft.getWidth() + MIN_BAR_SPACING - 1, i + 3);
			}
		}
	}
	
	/**
	 * Updatet die internen Größenvariablen des Scrollbalken.
	 */
	private void updateBarSize() {
		this.barSize = calcBarSize();
		this.barPosition = calcBarPosition();
		this.maxBarPosition = calcMaxBarPosition();
	}
	
	/**
	 * Liefert die Höhe (bei vertikalem) oder die Breite (bei
	 * horizontalem) Scrollbalken in Abhängigkeit der Größe
	 * des sichtbaren Bereichs und des maximal möglichen Bereichs
	 * und der Höhe des Scroll-Balken-Hintergrunds. 
	 * @return
	 */
	private int calcBarSize() {
		int delta = maximum - minimum; 			// Bereich, der bewegt werden kann
		int fullHeight = delta + ((orientation == VERTICAL) ? panelHeight : panelWidth);	// Gesamter Bereich
		float aspect = (float)((orientation == VERTICAL) ? panelHeight : panelWidth) / (float)fullHeight;
		int size = (int)((((orientation == VERTICAL) ? getHeight() : getWidth()) - 2 * MIN_BAR_SPACING) * aspect);
		return (size < MIN_BAR_SIZE) ? MIN_BAR_SIZE : size;
	}
	
	/**
	 * Liefert die Position des Balkens in Pixeln (von oben, bei
	 * vertikalem oder von links bei horizontalem Scrollbalken)
	 * @return
	 */
	private int calcBarPosition() {
		float valueProcent = (float)(value - minimum) / (float)(maximum - minimum);
		int maxMovementPixel = ((orientation == VERTICAL) ? getHeight() : getWidth()) - 2 * MIN_BAR_SPACING - calcBarSize();
		return (int)(valueProcent * maxMovementPixel);
	}
	
	/**
	 * Liefert den maximale Verschiebung des Balkens in Pixeln 
	 * (von oben, bei vertikalem oder von links bei horizontalem
	 * Scrollbalken)
	 * @return
	 */
	private int calcMaxBarPosition() {
		return ((orientation == VERTICAL) ? getHeight() : getWidth()) - 2 * MIN_BAR_SPACING - calcBarSize();
	}
	
	/**
	 * Testet, ob die Koordinate den ScrollBalken trifft.
	 * @param x
	 * x-Koordinate.
	 * @param y
	 * y-Koordinate.
	 * @return
	 * true, wenn die Koordinate über dem Scrollbalken liegt.
	 */
	private boolean hitTest(int x, int y) {
		if (orientation == VERTICAL) {
			if (x < 3 || x > WIDTH - 3)
				return false;
			
			if (y < barPosition || y > barPosition + barSize)
				return false;
		} else {
			if (y < 3 || y > WIDTH - 3)
				return false;
			
			if (x < barPosition || x > barPosition + barSize)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Setzt die Position des Scrollbalkens und führt
	 * den registrierten LocationChangedHandler aus.
	 * @param position
	 * Neue Position des ScrollBalkens.
	 */
	private void setBarPosition(int position) {
		barPosition = position;
		if (barPosition < 0)
			barPosition = 0;
		
		if (barPosition > maxBarPosition)
			barPosition = maxBarPosition;
		
		float procentPosition = (float)barPosition / (float)maxBarPosition;
		value = (int)(procentPosition * (maximum - minimum)) + minimum;
		onLocationChanged();
	}
	
	@Override
	public void setBounds(int x, int y, int w, int h) {
		if (orientation == VERTICAL)
			super.setBounds(x, y, WIDTH, h);
		else
			super.setBounds(x, y, w, WIDTH);
		
		updateBarSize();
	}
	
	@Override
	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
			
	}

	/**
	 * Setzt ein neues Minimum des Scrollbalkens.
	 * @param minimum
	 */
	public void setMinimum(int minimum) {
		this.minimum = minimum;
		updateBarSize();
	}

	/**
	 * Lifert den minimal möglichen Einstellungswert
	 * des Scrollbalkens
	 * @return
	 */
	public int getMinimum() {
		return minimum;
	}

	/**
	 * Setzt den maximal möglichen Einstellungswert
	 * des Scrollbalkens
	 * @param maximum
	 */
	public void setMaximum(int maximum) {
		this.maximum = maximum;
		
		if (maximum < value) {
			value = maximum;
			onLocationChanged();
		}
		
		updateBarSize();
	}

	/**
	 * Liefert den maximal möglichen Einstellungswert
	 * des Scrollbalkens
	 * @return
	 */
	public int getMaximum() {
		return maximum;
	}

	/**
	 * Setzt den aktuellen Wert des Scrollbalkens.
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
		setBarPosition(calcBarPosition());
	}

	/**
	 * Liefert den aktuellen Wert des Scrollbalkens.
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Setzt die gesamte Größe des übergeordneten Panels
	 * in dem Bereich verschoben werden kann. Diese Größen
	 * geben den Ausschlag für die Größe der Scrollbalken.
	 * @param width
	 * @param height
	 */
	public void setPanelSize(int width, int height) {
		this.panelWidth = width;
		this.panelHeight = height;
	}
	
	/**
	 * Registriert den übergebenen LocationChangeHandler an
	 * diesem ScrollBalken. Sobald die Position des Balkens
	 * geändert wird, wird die darin enthaltene Methode
	 * onLocationChanged() aufgerufen.
	 * @param handler
	 */
	public void addLocationChangeHandler(LocationChangeHandler handler) {
		this.locationHandlers.add(handler);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {	}

	@Override
	public void mouseEntered(MouseEvent arg0) {	}

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			pressed = hitTest(e.getX(), e.getY());
			startPos = barPosition;
			if (orientation == VERTICAL)
				startDown = e.getY();
			else
				startDown = e.getX();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			pressed = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!pressed)
			return;
		
		int delta = ((orientation == VERTICAL) ? e.getY() : e.getX()) - startDown;
		setBarPosition(startPos + delta);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) { }
	
	/**
	 * Ruft die onLocationChanged() Methode aller registrierten
	 * LocationChangedHandler auf
	 */
	private void onLocationChanged() {
		for (LocationChangeHandler handler : locationHandlers)
			handler.onLocationChanged();
	}
}
