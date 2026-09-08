package de.hamster.visual.model;

public class TrueExpression implements BooleanExpression {

	@Override
	public Object perform() {
		return true;
	}
}
