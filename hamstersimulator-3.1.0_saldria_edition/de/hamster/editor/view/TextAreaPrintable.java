/*
 * Created on 15.05.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package de.hamster.editor.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class TextAreaPrintable implements Printable {
	TextArea t;

	public TextAreaPrintable(String text, PageFormat pageFormat) {
		t = new TextArea(text);
		t.setSize((int) pageFormat.getImageableWidth(), (int) t
				.getPreferredSize().getHeight());
		t.setSize((int) pageFormat.getImageableWidth(), (int) t
				.getPreferredSize().getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 * java.awt.print.PageFormat, int)
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		double length = t.getHeight();
		double pageHeight = (((int) pageFormat.getImageableHeight()) / 12) * 12;
		double pageWidth = (int) pageFormat.getImageableWidth();
		if (pageIndex <= length / pageHeight) {
			Graphics2D g = (Graphics2D) graphics
					.create((int) pageFormat.getImageableX(),
							(int) (pageFormat.getImageableY() - pageIndex
									* pageHeight), t.getWidth(), t.getHeight());

			g.clipRect(0, (int) (pageIndex * pageHeight), (int) pageWidth,
					(int) pageHeight);
			t.print(g);
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

}