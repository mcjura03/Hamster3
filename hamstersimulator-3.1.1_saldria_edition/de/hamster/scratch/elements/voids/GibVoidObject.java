package de.hamster.scratch.elements.voids;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.VoidObject;

public class GibVoidObject extends VoidObject {
	public GibVoidObject() {
		super("gib", getParameter());
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		return parameter;
	}
	
	@Override
	public Renderable clone() {
		return new GibVoidObject();
	}
	
	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		hamster.gib();
		return null;
	}
}
