package de.hamster.scratch.gui;

public class DeleteMethodException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 631728129944515016L;

	private String message;
	
	public DeleteMethodException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
