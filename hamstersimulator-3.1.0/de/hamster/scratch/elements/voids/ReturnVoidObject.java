package de.hamster.scratch.elements.voids;

import java.util.ArrayList;

import de.hamster.scratch.Renderable;
import de.hamster.scratch.elements.ReturnObject;
import de.hamster.workbench.Utils;

public class ReturnVoidObject extends ReturnObject {
	public ReturnVoidObject() {
		super(Utils.getVoidReturn(), getParameter());
		next = null;
		nextDock = null;
	}

	private static ArrayList<RType> getParameter() {
		ArrayList<RType> parameter = new ArrayList<RType>();
		return parameter;
	}

	@Override
	public Renderable clone() {
		return new ReturnVoidObject();
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int layer,
			boolean comment, boolean needsReturn)
			throws FunctionResultException {
		startLine(buffer, layer, comment);

		buffer.append("return;" + NEWLINE);

		throw new FunctionResultException(true);
	}

	// @Override
	// public void writeSourceCode(StringBuffer buffer, int layer,
	// boolean comment, boolean needsReturn)
	// throws FunctionResultException {
	// startLine(buffer, layer, comment);
	// if (needsReturn)
	// buffer.append("return true;" + NEWLINE);
	// else
	// buffer.append("return;" + NEWLINE);
	//
	// throw new FunctionResultException(true);
	// }
}
