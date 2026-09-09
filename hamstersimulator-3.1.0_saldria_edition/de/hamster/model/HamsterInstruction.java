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

		boolean alias = Utils.PYTHON_ALIAS_USAGE;

		
		switch (getType()) {
		case FORWARD:
			if (alias) {
				return Utils.PYTHON_ALIAS_VOR + "()";
			}
			else	{
				return Utils.getResource("hamster.vor") + "()";
			}
		case TURN_LEFT:
			if (alias) {
				return Utils.PYTHON_ALIAS_LINKSUM + "()";
			}
			else	{
				return Utils.getResource("hamster.linksUm") + "()";
			}
			
		case LAY_DOWN:
			if (alias) {
				return Utils.PYTHON_ALIAS_GIB + "()";
			}
			else	{
				return Utils.getResource("hamster.gib") + "()";
			}
			
		case PICK_UP:
			if (alias) {
				return Utils.PYTHON_ALIAS_NIMM + "()";
			}
			else	{
				return Utils.getResource("hamster.nimm") + "()";
			}
		case FREE:
			if (alias) {
				return Utils.PYTHON_ALIAS_VORNFREI + "()";
			}
			else	{
				return Utils.getResource("hamster.vornFrei") + "()";
			}
		case CORN_AVAILABLE:
			if (alias) {
				return Utils.PYTHON_ALIAS_KORNDA + "()";
			}
			else	{
				return Utils.getResource("hamster.kornDa") + "()";
			}
			
		case MOUTH_EMPTY:
			if (alias) {
				return Utils.PYTHON_ALIAS_MAULLEER + "()";
			}
			else	{
				return Utils.getResource("hamster.maulLeer") + "()";
			}
			
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