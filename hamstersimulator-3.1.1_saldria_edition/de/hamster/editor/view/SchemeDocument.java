package de.hamster.editor.view;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import de.hamster.compiler.model.HamsterLexer;
import de.hamster.compiler.model.HamsterSchemeLexer;
import de.hamster.compiler.model.JavaToken;
import de.hamster.workbench.Workbench;

/**
 * @version $Revision: 1.1 $
 */

// Martin
public class SchemeDocument extends JavaDocument // DefaultStyledDocument dibo 230309
		implements
			DocumentListener {

	public SchemeDocument(boolean printing) {
		super(printing);
		scanner = new HamsterSchemeLexer();
		// addStyle("plain", getStyle("default"));
		addDocumentListener(this);
		initStyles(printing, Workbench.getWorkbench().getFontSize());
		/* dibo 210110
		if (!printing) {
			StyleConstants.setFontFamily(getStyle("plain"), "Monospaced");
			StyleConstants.setFontSize(getStyle("plain"), Workbench.getWorkbench().getFontSize());
			TabStop[] ts = new TabStop[10];
			for (int i = 0; i < ts.length; i++)
				ts[i] = new TabStop((i + 1) * 28);
			StyleConstants.setTabSet(getStyle("plain"), new TabSet(ts));
			setParagraphAttributes(0, getLength(), getStyle("plain"), true);

			addStyle("keyword", getStyle("plain"));
			StyleConstants.setBold(getStyle("keyword"), true);
			StyleConstants.setForeground(getStyle("keyword"), Color.MAGENTA
					.darker().darker());

			addStyle("comment", getStyle("plain"));
			StyleConstants.setForeground(getStyle("comment"), new Color(63,
					127, 95));

			addStyle("literal", getStyle("plain"));
			StyleConstants.setForeground(getStyle("literal"), Color.BLUE);
		} else {
			StyleConstants.setFontFamily(getStyle("plain"), "Courier New");
			StyleConstants.setFontSize(getStyle("plain"), 10);
			TabStop[] ts = new TabStop[10];
			for (int i = 0; i < ts.length; i++)
				ts[i] = new TabStop((i + 1) * 28);
			StyleConstants.setTabSet(getStyle("plain"), new TabSet(ts));
			setParagraphAttributes(0, getLength(), getStyle("plain"), true);

			addStyle("keyword", getStyle("plain"));
			StyleConstants.setBold(getStyle("keyword"), true);

			addStyle("comment", getStyle("plain"));
			StyleConstants.setForeground(getStyle("comment"), Color.LIGHT_GRAY);

			addStyle("literal", getStyle("plain"));
			StyleConstants.setForeground(getStyle("literal"), Color.LIGHT_GRAY);
		}
		*/
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