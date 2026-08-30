package de.hamster.visual.model;

public class FalseExpression implements BooleanExpression {

	@Override
	public Object perform() {
		return false;
	}
}
