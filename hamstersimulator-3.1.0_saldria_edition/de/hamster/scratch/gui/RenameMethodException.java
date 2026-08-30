package de.hamster.scratch.gui;

public class RenameMethodException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -693242178659318623L;

	private String message;
	
	public RenameMethodException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
