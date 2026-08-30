package de.hamster.simulation.view;

import java.awt.Image;

/**
 * @author Daniel
 */
public abstract class TerrainTool {
	boolean dragTool;
	Image image;
	public TerrainTool(boolean dragTool) {
		this.dragTool = dragTool;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public Image getImage() {
		return image;
	}
	public abstract void done();

	public boolean isDragTool() {
		return dragTool;
	}
}
