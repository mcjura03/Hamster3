package de.hamster.editor.view;

import java.awt.Color;

import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import de.hamster.compiler.model.HamsterPrologLexer;
import de.hamster.workbench.Workbench;

public class PrologDocument extends JavaDocument
{
	private static final long serialVersionUID = -2158878842179979282L;

	public PrologDocument(boolean printing)
	{
		super(printing);
		scanner = new HamsterPrologLexer();					
		//addStyle("plain", getStyle("default"));
		addDocumentListener(this);
		initStyles(printing, Workbench.getWorkbench().getFontSize());
		/* dibo 210110
		if (!printing) {
			StyleConstants.setFontFamily(getStyle("plain"), "Monospaced");
			StyleConstants.setFontSize(getStyle("plain"), 12);
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
		} 
		else 
		{
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
	}
}
