/*
 * Created on 15.05.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package de.hamster.editor.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import de.hamster.scratch.ScratchPanel;

public class ScratchPanelPrintable implements Printable {
	ScratchPanel scratchPanel;
	BufferedImage[] images;

	public ScratchPanelPrintable(ScratchPanel p, PageFormat pageFormat) {
		scratchPanel = p;
		images = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 * java.awt.print.PageFormat, int)
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (images == null) {
			images = scratchPanel.getImages();
		}
		if (pageIndex >= images.length) {
			images = null;
			return Printable.NO_SUCH_PAGE;
		}
		// Image img = scratchPanel.getImage();
		g.drawImage(images[pageIndex], (int) pageFormat.getImageableX(),
				(int) pageFormat.getImageableY(), null);
		return Printable.PAGE_EXISTS;
	}

}