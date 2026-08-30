package de.hamster.scratch.elements.booleans;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.BooleanMethodObject;
import de.hamster.scratch.elements.voids.FunctionResultException;
import de.hamster.workbench.Utils;


public class NotBooleanObject extends BooleanMethodObject {
	public NotBooleanObject() {
		super("nicht", Utils.getNot(), getParameter());
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		parameter.add(RType.BOOLEAN);
		return parameter;
	}

	@Override
	public Renderable clone() {
		NotBooleanObject temp = new NotBooleanObject();
		return temp;
	}

	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		// Boolean auswerten
		boolean bool = true;
		if (childs.get(0) != null)
			bool = (Boolean)childs.get(0).perform(program);
		
		// Gesamte Auswertung
		return (!bool);
	}
	
	@Override
	public void writeSourceCode(StringBuffer buffer, int layer, boolean comment, boolean needsReturn) throws FunctionResultException {
		startLine(buffer, layer, comment);
		buffer.append("!");
		
		if (childs.get(0) == null)
			buffer.append("true");
		else
			childs.get(0).writeSourceCode(buffer, 0, false, false);
	}
}
