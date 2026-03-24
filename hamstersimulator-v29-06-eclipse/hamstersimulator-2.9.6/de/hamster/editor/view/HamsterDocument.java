package de.hamster.editor.view;

import de.hamster.compiler.model.HamsterLexer;
import de.hamster.compiler.model.JavaToken;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style; // jrahn: fuer fett gesetzten Methodenstil ergänzt
import javax.swing.text.StyleConstants;

/**
 * Fungiert als einheitliche Schnittstelle (Oberklasse) fuer alle
 * Document-Style-Klassen der im Hamstersimulator verwendeten
 * Programmiersprachen.
 */
public class HamsterDocument extends DefaultStyledDocument implements DocumentListener {

	boolean highlighting;
	LinkedList<JavaToken> tokens; // jrahn: LinkedList typisiert
	HamsterLexer scanner;

	public HamsterDocument() {
		tokens = new LinkedList<>(); // jrahn: generische LinkedList verwendet

		Style methodStyle = this.getStyle("method"); // jrahn: Methodenstil prüfen
		if (methodStyle == null) {
			methodStyle = this.addStyle("method", this.getStyle("plain")); // jrahn: Methodenstil anlegen
		}
		StyleConstants.setBold(methodStyle, true); // jrahn: Methoden fett darstellen
	}

	JavaToken first() {
		return tokens.getFirst(); // jrahn: Cast durch Generics entfernt
	}

	int copyAllBefore(int pos, LinkedList<JavaToken> newTokens) { // jrahn: LinkedList typisiert
		int start = 0;
		while (!tokens.isEmpty()
				&& first().getStart() + first().getText().length() < pos) {
			start = first().getStart() + first().getText().length();
			newTokens.addLast(tokens.removeFirst());
		}
		return start;
	}

	static javax.swing.text.TabSet calcTabs(int charactersPerTab, Font font) { // jrahn: TabSet voll qualifiziert
		FontMetrics fm = new JPanel().getFontMetrics(font);
		int charWidth = fm.charWidth('w');
		int tabWidth = charWidth * charactersPerTab;

		javax.swing.text.TabStop[] tabs = new javax.swing.text.TabStop[100]; // jrahn: TabStop voll qualifiziert
		tabs[0] = new javax.swing.text.TabStop(tabWidth); // jrahn

		for (int j = 1; j < tabs.length; j++) {
			tabs[j] = new javax.swing.text.TabStop(
					tabs[j - 1].getPosition() + tabWidth); // jrahn
		}

		return new javax.swing.text.TabSet(tabs); // jrahn
	}

	public JavaToken getTokenAt(int pos) {
		for (JavaToken token : tokens) { // jrahn: Iterator durch for-each ersetzt
			if (token.getStart() >= pos) {
				return token;
			}
		}
		return null;
	}

	boolean exists(JavaToken t, int offset) {
		while (!tokens.isEmpty()) {
			if (first().getStart() + offset == t.getStart()) {
				return true;
			} else if (first().getStart() + offset < t.getStart()) {
				tokens.removeFirst();
			} else {
				return false;
			}
		}
		return false;
	}

	void copyAllAfter(LinkedList<JavaToken> newTokens, int offset) { // jrahn: LinkedList typisiert
		while (!tokens.isEmpty()) {
			JavaToken t = first();
			t.setStart(t.getStart() + offset);
			newTokens.addLast(tokens.removeFirst());
		}
	}

	void setAttributes(JavaToken t) {
		switch (t.getType()) { // jrahn: if-Kette durch switch ersetzt
			case HamsterLexer.COMMENT -> setCharacterAttributes(t.getStart(), t.getText().length(),
						getStyle("comment"), true);
			case HamsterLexer.KEYWORD -> setCharacterAttributes(t.getStart(), t.getText().length(),
						getStyle("keyword"), true);
			case HamsterLexer.LITERAL -> setCharacterAttributes(t.getStart(), t.getText().length(),
						getStyle("literal"), true);
			case HamsterLexer.METHOD -> // jrahn: Methoden-Tokentyp ergänzt
				setCharacterAttributes(t.getStart(), t.getText().length(),
						getStyle("method"), true); // jrahn: Methodenstil anwenden
			default -> setCharacterAttributes(t.getStart(), t.getText().length(),
						getStyle("plain"), true);
		}
            // jrahn: if-Kette durch switch ersetzt
            	}

	public boolean isHighlighting() {
		return highlighting;
	}

	// Prolog: changed method visibility to public.
	public void rehighlight(int pos, int len) {
		highlighting = true;
		LinkedList<JavaToken> newTokens = new LinkedList<>(); // jrahn: LinkedList typisiert
		int start = copyAllBefore(pos, newTokens);

		try {
			scanner.init(0, start, getText(start, getLength() - start));
		} catch (BadLocationException e1) {
			// should not happen
		}

		while (scanner.ready()) {
			JavaToken t = scanner.nextToken();
			if (t == null) {
				break;
			}
			if (t.getStart() > pos && exists(t, len)) {
				copyAllAfter(newTokens, len);
				break;
			}
			setAttributes(t);
			newTokens.addLast(t);
		}
		tokens = newTokens;

		highlighting = false;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		SwingUtilities.invokeLater(new RunRehighlight(e.getOffset(),
				e.getLength(), this));
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		SwingUtilities.invokeLater(new RunRehighlight(e.getOffset(),
				-e.getLength(), this));
	}
}