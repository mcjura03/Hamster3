package de.hamster.editor.view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import de.hamster.compiler.model.HamsterLexer;
import de.hamster.compiler.model.JavaToken;

/**
 * Fungiert als einheitliche Schnittstelle (Oberklasse) für alle 
 * Document-Style-Klassen der im Hamstersimulator verwendeter 
 * Programmiersprachen. Im Moment lediglich nur als Platzhalter für 
 * ein Hamster-StyleDocument vorgesehen..
 * 
 */
public class HamsterDocument extends DefaultStyledDocument  implements DocumentListener
{
	boolean highlighting;
	LinkedList tokens;
	HamsterLexer scanner;
	
	public HamsterDocument()
	{
		tokens = new LinkedList();
	}
	
	JavaToken first() {
		return (JavaToken) tokens.getFirst();
	}

	int copyAllBefore(int pos, LinkedList newTokens) {
		int start = 0;
		while (!tokens.isEmpty()
				&& first().getStart() + first().getText().length() < pos) {
			start = first().getStart() + first().getText().length();
			//			System.out.println("copy-before: " + first());
			newTokens.addLast(tokens.removeFirst());
		}
		return start;
	}
	
	static TabSet calcTabs(int charactersPerTab, Font font) {
		FontMetrics fm = new JPanel().getFontMetrics(font);
		int charWidth = fm.charWidth('w');
		int tabWidth = charWidth * charactersPerTab;

		TabStop[] tabs = new TabStop[100];
		tabs[0] = new TabStop(tabWidth);

		for (int j = 1; j < tabs.length; j++) {
			tabs[j] = new TabStop(tabs[j - 1].getPosition() + tabWidth);
		}

		TabSet tabSet = new TabSet(tabs);
		return tabSet;
	}

	public JavaToken getTokenAt(int pos) {
		int start = 0;
		for (Iterator iter = tokens.iterator(); iter.hasNext();) {
			JavaToken token = (JavaToken) iter.next();
			if (token.getStart() >= pos)
				return token;
		}
		return null;
	}

	boolean exists(JavaToken t, int offset) {
		while (!tokens.isEmpty()) {
			if (first().getStart() + offset == t.getStart())
				return true;
			else if (first().getStart() + offset < t.getStart())
				tokens.removeFirst();
			else
				return false;
		}
		return false;
	}

	void copyAllAfter(LinkedList newTokens, int offset) {
		while (!tokens.isEmpty()) {
			JavaToken t = first();
			t.setStart(t.getStart() + offset);
			//			System.out.println("copy-after: " + t);
			newTokens.addLast(tokens.removeFirst());
		}
	}

	void setAttributes(JavaToken t) {
		if (t.getType() == HamsterLexer.COMMENT) {
			setCharacterAttributes(t.getStart(), t.getText().length(),
					getStyle("comment"), true);
		} else if (t.getType() == HamsterLexer.KEYWORD) {
			setCharacterAttributes(t.getStart(), t.getText().length(),
					getStyle("keyword"), true);
		} else if (t.getType() == HamsterLexer.LITERAL) {
			setCharacterAttributes(t.getStart(), t.getText().length(),
					getStyle("literal"), true);
		} else {
			setCharacterAttributes(t.getStart(), t.getText().length(),
					getStyle("plain"), true);
		}
	}

	public boolean isHighlighting() {
		return highlighting;
	}

	// Prolog: changed method visibility to public.
	public void rehighlight(int pos, int len) {
		highlighting = true;
		long s = System.currentTimeMillis();
		LinkedList newTokens = new LinkedList();
		int start = copyAllBefore(pos, newTokens);

		try {
			scanner.init(0, start, getText(start, getLength() - start));
		} catch (BadLocationException e1) {
			// TODO should not happen
		}
		while (scanner.ready()) {
			JavaToken t = scanner.nextToken();
			if (t == null)
				break;
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

	public void changedUpdate(DocumentEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
		SwingUtilities.invokeLater(new RunRehighlight(e.getOffset(), e
				.getLength(), this));
	}

	public void removeUpdate(DocumentEvent e) {
		SwingUtilities.invokeLater(new RunRehighlight(e.getOffset(), -e
				.getLength(), this));
	}
}
