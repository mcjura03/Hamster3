package de.hamster.scratch.elements.voids;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.VoidObject;

public class NimmVoidObject extends VoidObject {
	public NimmVoidObject() {
		super("nimm", getParameter());
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		return parameter;
	}
	
	@Override
	public Renderable clone() {
		return new NimmVoidObject();
	}
	
	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		hamster.nimm();
		return null;
	}
}
