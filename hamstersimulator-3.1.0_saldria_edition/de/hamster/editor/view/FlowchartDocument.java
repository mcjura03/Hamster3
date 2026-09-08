package de.hamster.editor.view;

import de.hamster.compiler.model.HamsterFSMLexer;
import de.hamster.workbench.Workbench;

// Ruby
public class FlowchartDocument extends JavaDocument {
	private static final long serialVersionUID = -2158878842179979284L;

	public FlowchartDocument(boolean printing) {
		super(printing);
		// Scratch
		scanner = new HamsterFSMLexer();
		// addStyle("plain", getStyle("default"));
		addDocumentListener(this);
		initStyles(printing, Workbench.getWorkbench().getFontSize());
	}
}
