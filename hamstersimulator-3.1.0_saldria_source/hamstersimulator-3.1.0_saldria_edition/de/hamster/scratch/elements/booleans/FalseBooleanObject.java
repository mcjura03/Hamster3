package de.hamster.scratch.elements.booleans;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.BooleanMethodObject;
import de.hamster.scratch.elements.voids.FunctionResultException;
import de.hamster.workbench.Utils;

public class FalseBooleanObject extends BooleanMethodObject {
	public FalseBooleanObject() {
		super("falsch", Utils.getFalse(), getParameter());
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		return parameter;
	}

	@Override
	public Renderable clone() {
		FalseBooleanObject temp = new FalseBooleanObject();
		return temp;
	}

	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		return false;
	}
	
	@Override
	public void writeSourceCode(StringBuffer buffer, int layer, boolean comment, boolean needsReturn) {
		startLine(buffer, layer, comment);
		buffer.append("false");
	}
}
