package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import de.hamster.scratch.ScratchUtils;

/**
 * Der farbige ScratchButton, der im OptionaPanel
 * vorkommt. Die Klasse ist abgeleitet von dem standard
 * Swing Button <tt>JButton</tt> mit der überschriebenen
 * Methode paint(Graphics g), die dafür sorgt, dass der
 * Button dem LookAndFeel von Scratch angepasst wird
 * @author HackZ
 *
 */
public class ScratchButton extends JButton implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8284667104153302058L;
	public static final int LEFT_SIDE_WIDTH = 6;
	public static final int MIDDLE_WIDTH = 105;
	public static final int RIGHT_SIDE_WIDTH = 6;
	public static final int HEIGHT = 18;
	public static final Font FONT = new Font("Verdana", Font.BOLD, 9);
	private static Image imgButton;
	private static Image imgButtonR;
	private static Image imgButtonHovered;
	private static Image imgButtonRHovered;
	private static Image imgButtonClickedT1;
	private static Image imgButtonClickedT2;
	private static Image imgButtonClickedT3;
	private static Image imgButtonRClickedT1;
	private static Image imgButtonRClickedT2;
	private static Image imgButtonRClickedT3;
	private static Image imgButtonT1;
	private static Image imgButtonT2;
	private static Image imgButtonT3;
	
	private boolean hovered = false;
	private boolean clicked = false;
	private ButtonType type;
	private boolean active = false;
	
	/**
	 * Erzeugt einen neuen ScratchButton an der Position
	 * <tt>x</tt>, <tt>y</tt> mit dem ButtonType <tt>type</tt>,
	 * der für die Farbe und Überschrift des Buttons sorgt.
	 * Beim ersten Erstellen werden die dazugehörigen Bilder
	 * geladen.
	 * @param x
	 * x-Koordinate des Buttons.
	 * @param y
	 * y-Koordinate des Buttons.
	 * @param type
	 * Ein Typ aus der Enumeration <tt>ButtonType</tt>, der für
	 * Farbe und Überschrift des Buttons sorgt.
	 */
	public ScratchButton(int x, int y, ButtonType type) {
		loadImages();
		
		this.type = type;
		this.addMouseListener(this);
		this.setBounds(x, y, LEFT_SIDE_WIDTH+ MIDDLE_WIDTH + RIGHT_SIDE_WIDTH, HEIGHT);
	}
	
	/**
	 * Wird im Konstruktor aufgerufen und lädt LookAndFeel Bilder.
	 * Diese Methode wird nur einmalig ausgeführt, da die Bilder
	 * statisch in der Klasse sind.
	 */
	private void loadImages() {
		if (imgButton != null)
			return; // Bilder sind bereits geladen

		imgButton = ScratchUtils.getImage("scratch/buttons/button.png");
		imgButtonR = ScratchUtils.getImage("scratch/buttons/button_r.png");
		imgButtonHovered = ScratchUtils.getImage("scratch/buttons/button_hover.png");
		imgButtonRHovered = ScratchUtils.getImage("scratch/buttons/button_r_hover.png");
		imgButtonClickedT1 = ScratchUtils.getImage("scratch/buttons/button_click_type1.png");
		imgButtonClickedT2 = ScratchUtils.getImage("scratch/buttons/button_click_type2.png");
		imgButtonClickedT3 = ScratchUtils.getImage("scratch/buttons/button_click_type3.png");
		imgButtonRClickedT1 = ScratchUtils.getImage("scratch/buttons/button_r_click_type1.png");
		imgButtonRClickedT2 = ScratchUtils.getImage("scratch/buttons/button_r_click_type2.png");
		imgButtonRClickedT3 = ScratchUtils.getImage("scratch/buttons/button_r_click_type3.png");
		imgButtonT1 = ScratchUtils.getImage("scratch/buttons/button_type1.png");
		imgButtonT2 = ScratchUtils.getImage("scratch/buttons/button_type2.png");
		imgButtonT3 = ScratchUtils.getImage("scratch/buttons/button_type3.png");
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(type.getLeftSideImage(), 0, 0, OptionsPanel.BACKGROUND_COLOR, null);
		if (clicked) {
			if (hovered) {
				g.drawImage(type.getMiddleClickedImage(), LEFT_SIDE_WIDTH, 0, MIDDLE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
				g.drawImage(type.getRightClickImage(), LEFT_SIDE_WIDTH + MIDDLE_WIDTH, 0, RIGHT_SIDE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
			} else {
				g.drawImage(imgButtonHovered, LEFT_SIDE_WIDTH, 0, MIDDLE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
				g.drawImage(imgButtonRHovered, LEFT_SIDE_WIDTH + MIDDLE_WIDTH, 0, RIGHT_SIDE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
			}
		} else {
			if (isActive()) {
				g.drawImage(type.getMiddleClickedImage(), LEFT_SIDE_WIDTH, 0, MIDDLE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
				g.drawImage(type.getRightClickImage(), LEFT_SIDE_WIDTH + MIDDLE_WIDTH, 0, RIGHT_SIDE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
			} else {
				if (hovered) {
					g.drawImage(imgButtonHovered, LEFT_SIDE_WIDTH, 0, MIDDLE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
					g.drawImage(imgButtonRHovered, LEFT_SIDE_WIDTH + MIDDLE_WIDTH, 0, RIGHT_SIDE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
				} else {
					g.drawImage(imgButton, LEFT_SIDE_WIDTH, 0, MIDDLE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
					g.drawImage(imgButtonR, LEFT_SIDE_WIDTH + MIDDLE_WIDTH, 0, RIGHT_SIDE_WIDTH, HEIGHT, OptionsPanel.BACKGROUND_COLOR, null);
				}
			}
		}
		
		g.setFont(FONT);
		g.setColor(Color.white);
		g.drawString(type.getButtonText(), LEFT_SIDE_WIDTH + 4, 13);
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) {
		hovered = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		hovered = false;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) { 
		clicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		clicked = false;
	}

	/**
	 * Kennzeichnet diesen Button als aktiv, was diesem
	 * einen farbigen Hintergrund verleiht umd es zu
	 * kennzeichnen.
	 * @param value
	 */
	public void setActive(boolean value) {
		active = value;
		repaint();
	}
	
	/**
	 * Liefert den boolschen Wert zurück, ob dieser Button
	 * als aktiv gekennzeichnet ist.
	 * @return
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Enumeration für die Typen der Buttons, die für den
	 * Konstruktor des ScratchButtons verwendet werden. Diese
	 * Enthalten die LookAndFeel Bilder und die Überschriften.
	 * @author HackZ
	 *
	 */
	public enum ButtonType {
		ANWEISUNGEN {
			@Override
			public Image getLeftSideImage() {
				return imgButtonT1;
			}

			@Override
			public String getButtonText() {
				return "Anweisungen";
			}

			@Override
			public Image getMiddleClickedImage() {
				return imgButtonClickedT1;
			}

			@Override
			public Image getRightClickImage() {
				return imgButtonRClickedT1;
			}
		},
		STEUERUNG {
			@Override
			public Image getLeftSideImage() {
				return imgButtonT2;
			}

			@Override
			public String getButtonText() {
				return "Steuerung";
			}

			@Override
			public Image getMiddleClickedImage() {
				return imgButtonClickedT2;
			}

			@Override
			public Image getRightClickImage() {
				return imgButtonRClickedT2;
			}
		},
		BOOL {
			@Override
			public Image getLeftSideImage() {
				return imgButtonT3;
			}

			@Override
			public String getButtonText() {
				return "Boolesche Ausdrücke";
			}

			@Override
			public Image getMiddleClickedImage() {
				return imgButtonClickedT3;
			}

			@Override
			public Image getRightClickImage() {
				return imgButtonRClickedT3;
			}
		};
		
		/**
		 * Liefert das kleine farbige Bild auf der linken Seite
		 * @return
		 */
		abstract public Image getLeftSideImage();
		
		/**
		 * Lifert das Bild für die mittlere Wiederholung
		 * @return
		 */
		abstract public Image getMiddleClickedImage();
		
		/**
		 * Liefert das Bild, mit dem der Button rechts abschliesst
		 * @return
		 */
		abstract public Image getRightClickImage();
		
		/**
		 * Text für den Button
		 * @return
		 */
		abstract public String getButtonText();
	}
}
