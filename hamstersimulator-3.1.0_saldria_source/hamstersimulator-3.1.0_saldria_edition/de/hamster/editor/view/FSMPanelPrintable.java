/*
 * Created on 15.05.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package de.hamster.editor.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import de.hamster.fsm.view.FsmPanel;

public class FSMPanelPrintable implements Printable {
	FsmPanel fsmPanel;
	BufferedImage[] images;

	public FSMPanelPrintable(FsmPanel p, PageFormat pageFormat) {
		fsmPanel = p;
		images = null;
	}
	
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (images == null) {
			images = fsmPanel.getImages();
		}
		if (pageIndex >= images.length) {
			images = null;
			return Printable.NO_SUCH_PAGE;
		}
		final int iResMul = 1; // 1 = 72 dpi; 4 = 288 dpi
		int iPosX = 1;
		int iPosY = 1;
		int iWdth = (int) Math.round(pageFormat.getImageableWidth() * iResMul) - 3;
		int iHght = (int) Math.round(pageFormat.getImageableHeight() * iResMul) - 3;
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2.scale(1.0 / iResMul, 1.0 / iResMul);

		BufferedImage img = images[pageIndex];
		if (img.getWidth() < iWdth
				&& img.getHeight() < iHght) {
			g.drawImage(images[pageIndex], iPosX, iPosY, null);
		} else {
			double sx = 1.0 * iWdth / img.getWidth();
			double sy = 1.0 * iHght / img.getHeight();
			double s = Math.min(sx,  sy);
			g.drawImage(img, iPosX, iPosY, (int)(img.getWidth() * s), (int)(img.getHeight() * s), null);
		}
		return Printable.PAGE_EXISTS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 * java.awt.print.PageFormat, int)
	 */
	public int printAlt(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (images == null) {
			images = fsmPanel.getImages();
		}
		if (pageIndex >= images.length) {
			images = null;
			return Printable.NO_SUCH_PAGE;
		}
		Graphics2D g2d=(Graphics2D)g;
		double breiteF=1; // Skalierungsfaktoren
        double hoeheF=1;
        if (pageFormat.getImageableWidth()<images[0].getWidth()) { // bei Bedarf anpassen
        	breiteF=pageFormat.getImageableWidth() / images[0].getWidth();
        }
        if (pageFormat.getImageableHeight()<images[0].getHeight()) {
        	hoeheF=pageFormat.getImageableHeight() / images[0].getHeight();
        }
		if (breiteF>hoeheF) { // kleineren Faktor verwenden
			g2d.scale(hoeheF, hoeheF);
		} else {
			g2d.scale(breiteF, breiteF);
		}
		g.drawImage(images[pageIndex], (int) pageFormat.getImageableX(),
				(int) pageFormat.getImageableY(), null);
		return Printable.PAGE_EXISTS;
	}
}