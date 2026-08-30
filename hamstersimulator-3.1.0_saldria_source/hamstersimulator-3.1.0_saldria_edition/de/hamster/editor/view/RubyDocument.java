package de.hamster.editor.view;

import de.hamster.compiler.model.HamsterRubyLexer;
import de.hamster.workbench.Workbench;

// Ruby
public class RubyDocument extends JavaDocument {
	private static final long serialVersionUID = -2158878842179979282L;

	public RubyDocument(boolean printing) {
		super(printing);
		// Ruby
		scanner = new HamsterRubyLexer();
		// addStyle("plain", getStyle("default"));
		addDocumentListener(this);
		initStyles(printing, Workbench.getWorkbench().getFontSize());
	}
}
