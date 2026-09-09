package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchPanel;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.Renderable.RType;
import de.hamster.scratch.elements.booleans.AndBooleanObject;
import de.hamster.scratch.elements.booleans.FalseBooleanObject;
import de.hamster.scratch.elements.booleans.KornDaBooleanObject;
import de.hamster.scratch.elements.booleans.MaulLeerBooleanObject;
import de.hamster.scratch.elements.booleans.NotBooleanObject;
import de.hamster.scratch.elements.booleans.OrBooleanObject;
import de.hamster.scratch.elements.booleans.TrueBooleanObject;
import de.hamster.scratch.elements.booleans.VornFreiBooleanObject;
import de.hamster.scratch.elements.controls.DoWhileObject;
import de.hamster.scratch.elements.controls.IfElseObject;
import de.hamster.scratch.elements.controls.IfObject;
import de.hamster.scratch.elements.controls.WhileObject;
import de.hamster.scratch.elements.voids.GibVoidObject;
import de.hamster.scratch.elements.voids.LinksUmVoidObject;
import de.hamster.scratch.elements.voids.NimmVoidObject;
import de.hamster.scratch.elements.voids.ReturnBooleanObject;
import de.hamster.scratch.elements.voids.ReturnVoidObject;
import de.hamster.scratch.elements.voids.VorVoidObject;
import de.hamster.scratch.gui.ScratchButton.ButtonType;

/**
 * Das Optionspanel ist die Auswahl der Kategorien.
 * Es bietet die M�glichkeit die referenzierte
 * ElementListe zu f�llen, abh�ngig von der gew�hlten
 * Kategorie.
 * @author HackZ
 *
 */
public class OptionsPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7119491510774040356L;
	public static Color BACKGROUND_COLOR = new Color(149, 154, 159);
	
	public static int lastActiveElementList = 0;

	private static BufferedImage imgTop;
	private static BufferedImage imgBottom;
	private static BufferedImage imgLeft;
	private static BufferedImage imgRight;
	private static BufferedImage imgTopLeft;
	private static BufferedImage imgTopRight;
	private static BufferedImage imgBottomLeft;
	private static BufferedImage imgBottomRight;
	
	private ElementList elements;
	private ScratchButton akciveButton = null;
	private ScratchButton button1;
	private ScratchButton button2;
	private ScratchButton button3;
	private ScratchPanel scratchPanel;
	
	/**
	 * Erzeugt ein neues OptionsPanel mit der Referenz
	 * aud die damit verbundene ElementList. Beim ersten
	 * Erstellen werden die dazugeh�rigen Bilder geladen.
	 * @param elements
	 * Die ElementListe, mit dem das OptionsPanel verkn�pft
	 * ist. Beim Laden einer Kategorie werden Elemente in
	 * diese ElementList gelade.
	 * @param storageController
	 * Die Referenz auf den StorageController, f�r den Zugriff
	 * auf die darin enthaltenen Funktionen
	 */
	public OptionsPanel(ElementList elements, ScratchPanel scratchPanel) {
		this.elements = elements;
		this.scratchPanel = scratchPanel;
		this.setLayout(null);
		this.setBackground(BACKGROUND_COLOR);
		
		button1 = new ScratchButton(15, 15, ButtonType.ANWEISUNGEN);
		button1.addActionListener(new Button1Clicked());
		this.add(button1);
		
		button2 = new ScratchButton(15, 39, ButtonType.BOOL);
		button2.addActionListener(new Button2Clicked());
		this.add(button2);
		
		button3 = new ScratchButton(15, 63, ButtonType.STEUERUNG);
		button3.addActionListener(new Button3Clicked());
		this.add(button3);
		
		loadImages();
	}
	
	/**
	 * Wird im Konstruktor aufgerufen und l�dt LookAndFeel Bilder.
	 * Diese Methode wird nur einmalig ausgef�hrt, da die Bilder
	 * statisch in der Klasse sind.
	 */
	private void loadImages() {
		if (imgTop != null)
			return; // Bilder sind bereits geladen

		imgTop = ScratchUtils.getImage("scratch/backgrounds/top.png");
		imgBottom = ScratchUtils.getImage("scratch/backgrounds/bottom.png");
		imgLeft = ScratchUtils.getImage("scratch/backgrounds/left.png");
		imgRight = ScratchUtils.getImage("scratch/backgrounds/right.png");
		imgTopLeft = ScratchUtils.getImage("scratch/backgrounds/top_left.png");
		imgTopRight = ScratchUtils.getImage("scratch/backgrounds/top_right.png");
		imgBottomLeft = ScratchUtils.getImage("scratch/backgrounds/bottom_left.png");
		imgBottomRight = ScratchUtils.getImage("scratch/backgrounds/bottom_right.png");
	}
	
	@Override
	public void paint(Graphics g) {
		paintBackground(g);
		paintBorders(g);
		super.paintComponents(g);
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
	 * Zeichnet die R�nder und Ecken der Komponente mit den daf�r
	 * geladenen Bildern. Die R�nder werden zum Schluss gezeichnet,
	 * so dass Transparenzeffekte m�glich sind und die darin
	 * enthaltenen Komponenten unter den R�ndern erscheinen.
	 * @param g
	 * Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBorders(Graphics g) {
//		g.drawImage(imgTop, 0, 0, getWidth(), 8, BACKGROUND_COLOR, null);
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
	 * Setzt den �bergebenen Button als aktiv (farbig hervorgehoben)
	 * und l�scht die alte Einstellung.
	 * @param button
	 */
	public void setActive(ScratchButton button) {
		if (akciveButton != null)
			akciveButton.setActive(false);
		
		button.setActive(true);
		akciveButton = button;
	}
	
	/**
	 * Veranlasst das neu Laden der Elemetliste. Wird aufgerufen, wenn
	 * ein neues Element erstellt worden ist
	 */
	public void refreshElementList() {
		if (button1.isActive())
			loadVoidMethods();
		
		if (button2.isActive())
			loadBooleanMethods();
		
		if (button3.isActive())
			loadControllerMethods();
	}
	
	public void loadLastActive() {
		switch (lastActiveElementList) {
		case 0:
			loadVoidMethods();
			break;
		case 1:
			loadBooleanMethods();
			break;
		case 2:
			loadControllerMethods();
			break;
		}
	}
	
	/**
	 * Leert die Elementliste und F�gt alle vorhandenen void Methoden hinzu
	 */
	private void loadVoidMethods() {
		setActive(button1);
		if (elements == null)
			return;
		
		elements.clearList();
		elements.addCreateButton(RType.VOID);
		elements.addElement(new ReturnVoidObject());
		elements.addElement(new ReturnBooleanObject());
		elements.addElement(new VorVoidObject());
		elements.addElement(new LinksUmVoidObject());
		elements.addElement(new NimmVoidObject());
		elements.addElement(new GibVoidObject());

		
		// Weitere Methode aus dem StorageController
		ArrayList<Renderable> methods = scratchPanel.getStorageController().getAllVoidMethods();
		for (Renderable r : methods)
			elements.addElement(r);
	}
	
	/**
	 * Leert die Elementliste und F�gt alle vorhandenen boolean Methoden hinzu
	 */
	private void loadBooleanMethods() {
		setActive(button2);
		if (elements == null)
			return;
		
		elements.clearList();
		elements.addCreateButton(RType.BOOLEAN);

		elements.addElement(new TrueBooleanObject());
		elements.addElement(new FalseBooleanObject());
		elements.addElement(new NotBooleanObject());
		elements.addElement(new AndBooleanObject());
		elements.addElement(new OrBooleanObject());
		elements.addElement(new VornFreiBooleanObject());
		elements.addElement(new KornDaBooleanObject());
		elements.addElement(new MaulLeerBooleanObject());
		
		// Weitere Methode aus dem StorageController
		ArrayList<Renderable> methods = scratchPanel.getStorageController().getAllBooleanMethods();
		for (Renderable r : methods)
			elements.addElement(r);
	}
	
	/**
	 * Leert die Elementliste und F�gt alle vorhandenen Steuerungsfunktionen hinzu
	 */
	private void loadControllerMethods() {
		setActive(button3);
		if (elements == null)
			return;
		
		elements.clearList();
		elements.addElement(new IfObject());
		elements.addElement(new IfElseObject());
		elements.addElement(new WhileObject());
		elements.addElement(new DoWhileObject());
	}
	
	/**
	 * Der ClickHandler f�r den ersten Button "Anweisungen". Die
	 * darin enthaltene Methode actionPerformed(ActionEvent e)
	 * wird aufgerufen sobald der erste Button geklickt worden ist.
	 * @author HackZ
	 *
	 */
	class Button1Clicked implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			loadVoidMethods();
			lastActiveElementList = 0;
		}
	}
	
	/**
	 * Der ClickHandler f�r den zweiten Button "Boolsche Ausdr�cke". Die
	 * darin enthaltene Methode actionPerformed(ActionEvent e)
	 * wird aufgerufen sobald der erste Button geklickt worden ist.
	 * @author HackZ
	 *
	 */
	class Button2Clicked implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			loadBooleanMethods();
			lastActiveElementList = 1;
		}
	}
	
	/**
	 * Der ClickHandler f�r den dritten Button "Steuerung". Die
	 * darin enthaltene Methode actionPerformed(ActionEvent e)
	 * wird aufgerufen sobald der erste Button geklickt worden ist.
	 * @author HackZ
	 *
	 */
	class Button3Clicked implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			loadControllerMethods();
			lastActiveElementList = 2;
		}
	}
}
