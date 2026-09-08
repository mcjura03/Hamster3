package de.hamster.model;

import de.hamster.workbench.Utils;
import java.io.Serializable;

/**
 * Diese Instruktions-Klasse umfasst alle Befehle, die von einem Hamster
 * ausgefuehrt werden koennen, ohne dass dazu weitere Parameter notwenig sind.
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class HamsterInstruction extends Instruction implements Serializable {
	public static final int FORWARD = 1;

	public static final int TURN_LEFT = 2;

	public static final int LAY_DOWN = 3;

	public static final int PICK_UP = 4;

	public static final int FREE = 5;

	public static final int CORN_AVAILABLE = 6;

	public static final int MOUTH_EMPTY = 7;

	public static final int GET_ROW = 8;

	public static final int GET_COL = 9;

	public static final int GET_DIR = 10;

	public static final int GET_MOUTH = 11;

	public static final int GET_DATA = 12;

	/**
	 * Dieses Attribut gibt an, welcher Hamster den Befehl ausgefuehrt hat.
	 */
	protected int hamster;

	public HamsterInstruction(int type, int hamster) {
		super(type);
		this.hamster = hamster;
	}

	public int getHamster() {
		return hamster;
	}

	public String toString() {

		String keybase = "hamster.";

		if (Utils.getResource("python.alias.usage").equals("true")){
			keybase = "python.alias.";
		}
		switch (getType()) {
		case FORWARD:
			return Utils.getResource(keybase+"vor") + "()";
		case TURN_LEFT:
			return Utils.getResource(keybase+"linksUm") + "()";
		case LAY_DOWN:
			return Utils.getResource(keybase+"gib") + "()";
		case PICK_UP:
			return Utils.getResource(keybase+"nimm") + "()";
		case FREE:
			return Utils.getResource(keybase+"vornFrei") + "()";
		case CORN_AVAILABLE:
			return Utils.getResource(keybase+"kornDa") + "()";
		case MOUTH_EMPTY:
			return Utils.getResource(keybase+"maulLeer") + "()";

		case GET_ROW:
			return Utils.getResource("hamster.getReihe") + "()";
		case GET_COL:
			return Utils.getResource("hamster.getSpalte") + "()";
		case GET_DIR:
			return Utils.getResource("hamster.getBlickrichtung") + "()";
		case GET_MOUTH:
			return Utils.getResource("hamster.getAnzahlKoerner") + "()";

		case GET_DATA:
			return "";
		}
		return super.toString();
	}
}