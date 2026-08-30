package de.hamster.fsm.model.transition.input;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.BooleanMethodObject;

/**
 * Klasse, die ein MaulLeer-Operand im Input eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class MaulLeerObject extends BooleanMethodObject{

	/**
	 * Konstruktor
	 * @param positioning Gibt an, ob das Objekt rechts oder links in einem 
	 * anderen BooleanObjekt positioniert werden soll.
	 */
	public MaulLeerObject(int positioning) {
		super("maulLeer", positioning);
	}

	@Override
	public FsmObject clone() {
		MaulLeerObject clonedMaulLeerObject = new MaulLeerObject(this.positioning);
		clonedMaulLeerObject.setCoordinates(this.xStart, this.yStart);
		clonedMaulLeerObject.setParent(this.parent);
		return clonedMaulLeerObject;
	}

	@Override
	public Object checkPerform(FsmProgram program) {
		return this.hamster.maulLeerQuiet();
	}

	@Override
	public Object performImplementation(FsmProgram program) {
		return this.hamster.maulLeer();
	}
}
