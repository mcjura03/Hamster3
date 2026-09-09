package de.hamster.fsm.model.transition.input;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.BooleanMethodObject;

/**
 * Klasse, die ein VornFrei-Operand im Input eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class VornFreiObject extends BooleanMethodObject{

	/**
	 * Konstruktor
	 * @param positioning Gibt an, ob das Objekt rechts oder links in einem 
	 * anderen BooleanObjekt positioniert werden soll.
	 */
	public VornFreiObject(int positioning) {
		super("vornFrei", positioning);
	}

	@Override
	public FsmObject clone() {
		VornFreiObject clonedVornFreiObject = new VornFreiObject(this.positioning);
		clonedVornFreiObject.setParent(this.parent);
		clonedVornFreiObject.setCoordinates(this.xStart, this.yStart);
		return clonedVornFreiObject;
	}

	@Override
	public Object checkPerform(FsmProgram program) {
		return this.hamster.vornFreiQuiet();
	}

	@Override
	public Object performImplementation(FsmProgram program) {
		return this.hamster.vornFrei();
	}
}
