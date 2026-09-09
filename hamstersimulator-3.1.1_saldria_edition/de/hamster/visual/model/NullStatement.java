package de.hamster.visual.model;

public class NullStatement implements Statement {

	public NullStatement() {
	}

	@Override
	public Object perform() {
		// do nothing
		return null;
	}
}
