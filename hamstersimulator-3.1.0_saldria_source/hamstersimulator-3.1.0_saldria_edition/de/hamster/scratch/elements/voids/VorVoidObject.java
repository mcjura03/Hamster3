package de.hamster.scratch.elements.voids;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.VoidObject;

public class VorVoidObject extends VoidObject {
	public VorVoidObject() {
		super("vor", getParameter());
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		return parameter;
	}
	
	@Override
	public Renderable clone() {
		return new VorVoidObject();
	}
	
	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		hamster.vor();
		return null;
	}
}
