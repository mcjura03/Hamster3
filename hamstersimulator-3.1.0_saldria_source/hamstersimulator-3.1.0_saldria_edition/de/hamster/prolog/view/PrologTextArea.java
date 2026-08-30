package de.hamster.prolog.view;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import de.hamster.editor.view.PrologDocument;

public class PrologTextArea extends JTextPane
{
	private static final long serialVersionUID = 6294869352563716809L;

	public PrologTextArea()
	{
		super(new PrologDocument(false));
	}
	
	public void append(String line)
	{	
		PrologDocument doc = (PrologDocument) getDocument();
		Element rootElem = doc.getDefaultRootElement();
		AttributeSet attr = rootElem.getAttributes().copyAttributes();
		try
		{	
			int startPos = doc.getLength();
			doc.insertString(startPos, line, attr);								
			//doc.rehighlight(startPos,startPos+line.length());
		}
		catch (BadLocationException e){}
	}
}
