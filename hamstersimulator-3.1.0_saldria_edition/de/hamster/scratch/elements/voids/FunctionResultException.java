package de.hamster.scratch.elements.voids;

public class FunctionResultException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8267826142041636221L;
	
	protected Object result;

	public FunctionResultException() {
		this(null);
	}

	public FunctionResultException(Object result) {
		this.result = result;
	}

	public Object getReturnValue() {
		return result;
	}

	public void setReturnValue(Object result) {
		this.result = result;
	}
}
