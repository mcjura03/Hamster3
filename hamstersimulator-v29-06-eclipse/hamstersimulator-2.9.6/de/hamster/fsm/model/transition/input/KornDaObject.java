package de.hamster.fsm.model.transition.input;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.BooleanMethodObject;

/**
 * Klasse, die ein KornDa-Operand im Input eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class KornDaObject extends BooleanMethodObject{

	/**
	 * Konstruktor
	 * @param positioning Gibt an, ob das Objekt rechts oder links in einem 
	 * anderen BooleanObjekt positioniert werden soll.
	 */
	public KornDaObject(int positioning) {
		super("kornDa", positioning);
	}

	@Override
	public FsmObject clone() {
		KornDaObject clonedKornDa = new KornDaObject(this.positioning);
		clonedKornDa.setCoordinates(this.xStart, this.yStart);
		clonedKornDa.setParent(this.parent);
		return clonedKornDa;
	}

	@Override
	public Object checkPerform(FsmProgram program) {
		return this.hamster.kornDaQuiet();
	}

	@Override
	public Object performImplementation(FsmProgram program) {
		return this.hamster.kornDa();
	}
}
