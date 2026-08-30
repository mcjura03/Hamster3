/*
 * Created on 15.05.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package de.hamster.editor.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class JComponentPrintable implements Printable {
	JComponent printComponent;
	
	public JComponentPrintable(JComponent p, PageFormat pageFormat) {
		printComponent = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 *      java.awt.print.PageFormat, int)
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		
		Dimension oldSize = printComponent.getSize();
		printComponent.setSize((int) pageFormat.getImageableWidth(), (int) printComponent
				.getPreferredSize().getHeight());
		
		double length = printComponent.getHeight();
		double pageHeight = (((int) pageFormat.getImageableHeight()) / 12) * 12;
		double pageWidth = (int) pageFormat.getImageableWidth();
		if (pageIndex <= length / pageHeight) {
			Graphics2D g = (Graphics2D) graphics
					.create((int) pageFormat.getImageableX(), (int) (pageFormat
							.getImageableY() - pageIndex * pageHeight), printComponent
							.getWidth(), printComponent.getHeight());

			g.clipRect(0, (int) (pageIndex * pageHeight), (int) pageWidth,
					(int) pageHeight);
			printComponent.print(g);
			printComponent.setSize(oldSize);
			return Printable.PAGE_EXISTS;
		} else {
			printComponent.setSize(oldSize);
			return Printable.NO_SUCH_PAGE;
		}
	}

}