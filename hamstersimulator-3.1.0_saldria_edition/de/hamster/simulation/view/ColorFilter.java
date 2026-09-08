package de.hamster.simulation.view;

import java.awt.image.RGBImageFilter;

import de.hamster.workbench.Utils;

/**
 * @author Daniel Jasper
 */
public class ColorFilter extends RGBImageFilter {
	int type;

	public ColorFilter(int type) {
		this.type = Math.min(type, Utils.COLORS.length - 1);
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		int blue = rgb & 0xff;
		int sr = Utils.COLORS[type].getRed();
		int sg = Utils.COLORS[type].getGreen();
		int sb = Utils.COLORS[type].getBlue();
		return (rgb & 0xff000000) | ((sr & blue) << 16) | ((sg & blue) << 8) | (sb & blue);
	}
}
