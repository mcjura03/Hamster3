package de.hamster.fsm.model.transition.output;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.VoidObject;

/**
 * Klasse, die ein Nimm-Operand im Output eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class NimmObject extends VoidObject{

	/**
	 * Konstruktor
	 */
	public NimmObject() {
		super("nimm");
	}

	@Override
	public FsmObject clone() {
		NimmObject clonedNimmObject = new NimmObject();
		clonedNimmObject.setParent(this.parent);
		clonedNimmObject.setCoordinates(this.xStart, this.yStart);
		return clonedNimmObject;
	}

	@Override
	public Object performImplementation(FsmProgram program) {
		this.hamster.nimm();
		return null;
	}
}
