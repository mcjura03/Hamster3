package de.hamster.compiler.model;

import java.util.HashMap;

/**
 * Diese Klasse implementiert einen Lexer fuer Hamster-FSM-Programme.
 */
// FSM
public class HamsterFSMLexer extends HamsterLexer {

	public HamsterFSMLexer() {
		this.KEYWORDS = new String[] { "and" };

		// Rufe wiederholt die Initialisierungsmethode auf.
		keywords = new HashMap<>(); // jrahn: Raw HashMap durch generische HashMap ersetzt
		for (String keyword : KEYWORDS) { // jrahn: for-each Schleife statt Indexschleife
			keywords.put(keyword, keyword); // jrahn: typsicher, sofern keywords in HamsterLexer generisch deklariert ist
		}
	}
}
