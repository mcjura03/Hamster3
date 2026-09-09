package de.hamster.editor.view;

import de.hamster.compiler.model.HamsterPythonLexer;
import de.hamster.workbench.Workbench;

// Python
public class PythonDocument extends JavaDocument {
	private static final long serialVersionUID = -2158878842179979282L;

	public PythonDocument(boolean printing) {
		super(printing);
		// Python
		scanner = new HamsterPythonLexer();
		// addStyle("plain", getStyle("default"));
		addDocumentListener(this);
		initStyles(printing, Workbench.getWorkbench().getFontSize());
	}
}