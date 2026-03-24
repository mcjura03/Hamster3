package de.hamster.compiler.model;

import java.util.HashMap;


/**
 * Diese Klasse implementiert einen Lexer fuer Hamster-Flowchart-Programme.
 */
// Flowchart
public class HamsterFlowchartLexer extends HamsterLexer {

	public HamsterFlowchartLexer() {
		this.KEYWORDS = new String[] { "and" };

		// Rufe wiederholt die Initialisierungsmethode auf.
		keywords = new HashMap<>(); // jrahn: Raw HashMap durch generische HashMap ersetzt
            for (String KEYWORDS1 : KEYWORDS) {
                keywords.put(KEYWORDS1, KEYWORDS1); // jrahn: jetzt typsicher (String, String)
            }
	}
}