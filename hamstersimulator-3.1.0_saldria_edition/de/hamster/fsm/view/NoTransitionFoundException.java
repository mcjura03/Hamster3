package de.hamster.fsm.view;

/**
 * Exception, die ausgelöst wird, wenn bei der Ausführung kein gültiger Übergang gefunden wurde.
 * @author Raffaela Ferrari
 *
 */
public class NoTransitionFoundException extends Exception{
	
	/**
	 * Exception-Text
	 */
	private String message;
	
	/**
	 * Konstruktor
	 * @param stateName Name des Zustands, für den keine Transition gefunden wurde.
	 */
	public NoTransitionFoundException(String stateName) {
		this.message = "Es wurde f�r den Zustand " + stateName + " keine g�ltige Transition gefunden"
				+ " und " + stateName + " ist kein Endzustand!";
	}

	/**
	 * Gibt den Exception-Text zurück
	 */
	public String getMessage() {
		return this.message;
    }
}
