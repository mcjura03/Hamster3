package de.hamster.fsm;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.input.AndObject;
import de.hamster.fsm.model.transition.input.EpsilonBooleanObject;
import de.hamster.fsm.model.transition.input.KornDaObject;
import de.hamster.fsm.model.transition.input.MaulLeerObject;
import de.hamster.fsm.model.transition.input.NotObject;
import de.hamster.fsm.model.transition.input.OrObject;
import de.hamster.fsm.model.transition.input.VornFreiObject;
import de.hamster.fsm.model.transition.output.EpsilonFunctionObject;
import de.hamster.fsm.model.transition.output.GibObject;
import de.hamster.fsm.model.transition.output.LinksUmObject;
import de.hamster.fsm.model.transition.output.NimmObject;
import de.hamster.fsm.model.transition.output.VorObject;
import de.hamster.workbench.Workbench;

/**
 * Wichtige allgemeine Funktionen
 * @author Raffaela Ferrari
 *
 */
public class FsmUtils {

	private static final Insets TOOLBAR_MARGIN = new Insets(0,0,0,0);

	/**
	 * Berechnet die Höhe eines Fonts;
	 * @param font für den die Höhe berechnet werden soll
	 * @return
	 */
	public static int getTextHeight(Font font) {
		BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = temp.getGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
	    return metrics.getHeight();		
	}

	/**
	 * Berechnet die Breite eines Textes für einen bestimmten Font
	 * @param text der genaue Text
	 * @param font der spezielle Font
	 * @return
	 */
	public static int getTextWidth(String text, Font font) {
		BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = temp.getGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
	    return metrics.stringWidth(text);		
	}

	/**
	 * Erstellt ein RenderableElement, das dem übergebenen Namen entspricht und ein 
	 * Element ist, das als Kind von einem TransistionDescriptionObject eingesetzt wird 
	 * und liefert dieses zurück.
	 * @param name
	 * 		Name des RenderableElements
	 * @return das entsprechende RenderableElement
	 */
	public static FsmObject getTransistionDescriptionELementByName(String name) {
		// Feste voids
		if (name.equals("vor"))
			return new VorObject();
		
		if (name.equals("linksUm"))
			return new LinksUmObject();
		
		if (name.equals("nimm"))
			return new NimmObject();
		
		if (name.equals("gib"))
			return new GibObject();
		
		if (name.equals("epsilonFunction"))
			return new EpsilonFunctionObject();
		
		// Feste booleans
		if (name.equals("vornFrei"))
			return new VornFreiObject(0);
		
		if (name.equals("kornDa"))
			return new KornDaObject(0);
		
		if (name.equals("maulLeer"))
			return new MaulLeerObject(0);
		
		if (name.equals("und"))
			return new AndObject(0);
		
		if (name.equals("oder"))
			return new OrObject(0);
		
		if (name.equals("nicht"))
			return new NotObject(0);
		
		if (name.equals("epsilonBoolean"))
			return new EpsilonBooleanObject(0);
		
		else
			return null;
	}

	/**
	 * erzeugt einen JToogleButton in der für das Menu bestimmten Größe
	 * @param icon der für diesen Button angezeigt wird
	 * @return der entsprechende Button
	 */
	public static JToggleButton createToggleButton(ImageIcon icon) {
		JToggleButton button = new JToggleButton(icon);
		button.setText(null);
		button.setMargin(TOOLBAR_MARGIN);
		return button;
	}

	/**
	 * erzeugt einen JButton in der für das Menu bestimmten Größe
	 * @param icon der für diesen Button angezeigt wird
	 * @return der entsprechende Button
	 */
	public static JButton createButton(ImageIcon icon) {
		JButton button = new JButton(icon);
		button.setText(null);
		button.setMargin(TOOLBAR_MARGIN);
		return button;
	}
	
	/**
	 * Gibt alle Input Objects zurück
	 * @return
	 */
	public static LinkedList<FsmObject> getAllInputObjects() {
		LinkedList<FsmObject> booleanObjects = new LinkedList<FsmObject>();
		booleanObjects.add(new EpsilonBooleanObject(0));
		booleanObjects.add(new KornDaObject(0));
		booleanObjects.add(new VornFreiObject(0));
		booleanObjects.add(new MaulLeerObject(0));
		booleanObjects.add(new NotObject(0));
		booleanObjects.add(new AndObject(0));
		booleanObjects.add(new OrObject(0));
		return booleanObjects;
	}
	
	/**
	 * Gibt alle OutputObjects zurück
	 * @return
	 */
	public static LinkedList<FsmObject> getAllOutputObjects() {
		LinkedList<FsmObject> outputObjects = new LinkedList<FsmObject>();
		outputObjects.add(new EpsilonFunctionObject());
		outputObjects.add(new GibObject());
		outputObjects.add(new VorObject());
		outputObjects.add(new NimmObject());
		outputObjects.add(new LinksUmObject());
		return outputObjects;
	}
}
