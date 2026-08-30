package de.hamster.scratch.elements.voids;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.ReturnObject;
import de.hamster.workbench.Utils;


public class ReturnBooleanObject extends ReturnObject {
	public ReturnBooleanObject() {
		super(Utils.getBoolReturn(), getParameter());
		next = null;
		nextDock = null;
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		parameter.add(RType.BOOLEAN);
		return parameter;
	}
	
	@Override
	public String getName() {
		return "returnB";
	}
	
	@Override
	public Renderable clone() {
		return new ReturnBooleanObject();
	}

	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		boolean bool = true;
		if (childs.get(0) != null)
			bool = (Boolean)childs.get(0).perform(program);
		
		throw new FunctionResultException(bool);
	}
	
	@Override
	public void writeSourceCode(StringBuffer buffer, int layer, boolean comment, boolean needsReturn) throws FunctionResultException {
		startLine(buffer, layer, comment);
		buffer.append("return ");
		
		if (childs.get(0) == null)
			buffer.append("true");
		else
			childs.get(0).writeSourceCode(buffer, 0, false, false);
		
		buffer.append(";" + NEWLINE);
		
		throw new FunctionResultException(true);
	}
}
