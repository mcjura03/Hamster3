package de.hamster.scratch.gui;

/**
 * Die InvalidIdentifierException wird geworfen, wenn eine neue
 * Methode erstellt werden soll, deren Name ungültig in irgendeiner
 * Form ist. Beispielsweise entspricht der Name nicht den Java
 * Konventionen oder ist bereits vergeben.
 * @author HackZ
 *
 */
public class InvalidIdentifierException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8751976428424902863L;
	
	private String message;

	/**
	 * Erstelle eine neue Exception mit der übergebenen Message
	 * @param message
	 */
	public InvalidIdentifierException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
