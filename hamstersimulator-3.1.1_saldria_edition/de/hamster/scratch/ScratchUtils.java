package de.hamster.scratch;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import de.hamster.scratch.Renderable.RType;
import de.hamster.scratch.elements.BooleanMethodObject;
import de.hamster.scratch.elements.VoidObject;
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
import de.hamster.scratch.gui.InvalidIdentifierException;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Die ScratchUtils bieten einige nütliche statische Funktionen, so
 * dass der Sourcecode minimiert wird.
 * @author HackZ
 *
 */
public class ScratchUtils {
	/**
	 * Lädt die Ressource als Bild von der übergebenen
	 * Position <tt>name</tt>.
	 * @param name
	 * Lokale Refferenz zu der Ressource.
	 * @return
	 */
	public static BufferedImage getImage(String name) {
		Image img = Utils.getIcon(name).getImage();
		BufferedImage buffImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = buffImg.getGraphics();
		g.drawImage(img, 0, 0, null);
		return buffImg;
		
//		File imgFile = new File("resources/" + name);
//		try {
//			return ImageIO.read(imgFile);
//		} catch (IOException e) {
//			System.out.println("Bild '" + name + "' konnten nicht geladen werden!");
//			e.printStackTrace();
//		}
//		return null;
	}
	
	/**
	 * Liefert die Breite zu dem übergebenen Text mit der übergebenen
	 * Schriftart.
	 * @param text
	 * Text zu dem die Breite geprüft werden soll.
	 * @param font
	 * Schriftart, mit der der Text geschrieben wird.
	 * @return
	 * Breite in Pixeln.
	 */
	public static int getTextWidth(String text, Font font) {
		BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = temp.getGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
	    return metrics.stringWidth(text);
	}
	
	/**
	 * Liefert die Höhe zu dem übergebenen Text mit der übergebenen
	 * Schriftart.
	 * @param text
	 * Text zu dem die Höhe geprüft werden soll.
	 * @param font
	 * Schriftart, mit der der Text geschrieben wird.
	 * @return
	 * Höhe in Pixeln.
	 */
	public static int getTextHeight(String text, Font font) {
		BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = temp.getGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
	    return metrics.getHeight();
	}
	
	/**
	 * Liefert das WorkbenchFrame des HamsterSimulators.
	 * @return
	 */
	public static JFrame getWorkbenchFrame() {
		return Workbench.getWorkbench().getView().getEditorFrame();
	}
	
	/**
	 * Überprüft, ob der übergebene text allen Javaidentifier 
	 * Konventionen entspricht.
	 * @param text
	 * text, der geprüft wird.
	 * @throws InvalidIdentifierException
	 * Wird geworfen, falls der Bezeichner nicht den
	 * Javakonventionen entspricht.
	 */
	public static void checkJavaIdentifier(String text) throws InvalidIdentifierException {
		if (text.equals(""))
			throw new InvalidIdentifierException("Der Bezeichner darf nicht leer sein!");
		
		if (!isIdentifierChar(text.charAt(0)))
			throw new InvalidIdentifierException("Der Bezeichner muss mit einem Buchstaben, Dollarzeichen oder Unterstrich beginnen!");
		
		for (int i = 1; i < text.length(); i++)
			if (!(isIdentifierChar(text.charAt(i)) || isNumeric(text.charAt(i))))
				throw new InvalidIdentifierException("Der Bezeichner enthält unzulässige Zeichen!");
	}
	
	/**
	 * Überprüft, ob der Buchtabe in einem Javaidentifier vorkommen darf
	 * @param c
	 * Zu prüfender Buchstabe.
	 * @return
	 * true, wenn der Buchtabe verwendet werden darf.
	 */
	public static boolean isIdentifierChar(char c) {
		if (c == '$')
			return true;
		
		if (c == '_')
			return true;
		
		if (c >= 'a' && c <= 'z')
			return true;
		
		if (c >= 'A' && c <= 'Z')
			return true;
		
		return false;
	}
	
	/**
	 * Überprüft, ob der Character numerisch ist
	 * @param c
	 * Zu prüfender Character
	 * @return
	 * true, wenn der Character numerisch ist (0-9).
	 */
	public static boolean isNumeric(char c) {
		if (c >= '0' && c <= '9')
			return true;
		
		return false;
	}
	
	/**
	 * Erstellt ein Redenerable, das dem übergebenen Namen entspricht
	 * und liefert diesen zurück.
	 * @param name
	 * Name des Renderables
	 * @param type
	 * Typ des Renderables
	 * @return
	 */
	public static Renderable getRenderableByName(String name, String type) {
		// Feste voids
		if (name.equals("vor"))
			return new VorVoidObject();
		
		if (name.equals("linksUm"))
			return new LinksUmVoidObject();
		
		if (name.equals("nimm"))
			return new NimmVoidObject();
		
		if (name.equals("gib"))
			return new GibVoidObject();
		
		if (name.equals("return"))
			return new ReturnVoidObject();
		
		if (name.equals("returnB"))
			return new ReturnBooleanObject();
		
		// Feste booleans
		if (name.equals("vornFrei"))
			return new VornFreiBooleanObject();
		
		if (name.equals("kornDa"))
			return new KornDaBooleanObject();
		
		if (name.equals("maulLeer"))
			return new MaulLeerBooleanObject();
		
		if (name.equals("wahr"))
			return new TrueBooleanObject();
		
		if (name.equals("falsch"))
			return new FalseBooleanObject();
		
		if (name.equals("und"))
			return new AndBooleanObject();
		
		if (name.equals("oder"))
			return new OrBooleanObject();
		
		if (name.equals("nicht"))
			return new NotBooleanObject();
		
		// Controller
		if (name.equals("falls"))
			return new IfObject();
		
		if (name.equals("fallsSonst"))
			return new IfElseObject();

		if (name.equals("solange"))
			return new WhileObject();
		
		if (name.equals("tueSolange"))
			return new DoWhileObject();
		
		if (type.toUpperCase().equals("VOID"))
			return new VoidObject(name, new ArrayList<RType>());
		else
			return new BooleanMethodObject(name,  new ArrayList<RType>());
	}
	
	public static Renderable getRenderableByName(String name, RType rType) {
		switch (rType) {
		case VOID:
			return ScratchUtils.getRenderableByName(name, "VOID");
		default:
			return ScratchUtils.getRenderableByName(name, "BOOLEAN");
		}
	}
}
