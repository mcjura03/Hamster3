package de.hamster.compiler.model;

import java.util.HashMap;

/**
 * Diese Klasse implementiert einen Lexer fuer Hamster-Scheme-Programme. 
 */
public class HamsterSchemeLexer extends HamsterLexer {
		
	public HamsterSchemeLexer() {
		this.KEYWORDS = new String[] { "define",
				"cdr", "car", "if", "cond", "cons" };

		// Rufe wiederholt die Initialisierungsmethode auf.
		keywords = new HashMap<>(); // jrahn: Raw HashMap durch generische HashMap ersetzt
		for (String keyword : KEYWORDS) { // jrahn: for-each Schleife statt Indexschleife
			keywords.put(keyword, keyword); // jrahn: typsichere Befüllung
		}
	}
}