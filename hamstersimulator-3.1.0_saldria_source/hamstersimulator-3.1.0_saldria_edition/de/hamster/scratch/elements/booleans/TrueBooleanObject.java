package de.hamster.scratch.elements.booleans;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchProgram;
import de.hamster.scratch.elements.BooleanMethodObject;
import de.hamster.scratch.elements.voids.FunctionResultException;
import de.hamster.workbench.Utils;

public class TrueBooleanObject extends BooleanMethodObject {
	public TrueBooleanObject() {
		super("wahr", Utils.getTrue(), getParameter());
	}
	
	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		return parameter;
	}

	@Override
	public Renderable clone() {
		TrueBooleanObject temp = new TrueBooleanObject();
		return temp;
	}

	@Override
	public Object performImplementation(ScratchProgram program) throws FunctionResultException {
		return true;
	}
	
	@Override
	public void writeSourceCode(StringBuffer buffer, int layer, boolean comment, boolean needsReturn) {
		startLine(buffer, layer, comment);
		buffer.append("true");
	}
}
