package de.hamster.fsm.model.transition.output;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.transition.VoidObject;

/**
 * Klasse, die ein LinksUm-Operand im Output eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class LinksUmObject extends VoidObject{

	/**
	 * Konstruktor
	 */
	public LinksUmObject() {
		super("linksUm");
	}

	@Override
	public FsmObject clone() {
		LinksUmObject clonedLinksUmObject = new LinksUmObject();
		clonedLinksUmObject.setParent(this.parent);
		clonedLinksUmObject.setCoordinates(this.xStart, this.yStart);
		return clonedLinksUmObject;
	}

	@Override
	public Object performImplementation(FsmProgram program) {
		this.hamster.linksUm();
		return null;
	}
}
