package de.hamster.flowchart.model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.model.command.ToolbarCommandObject;

/**
 * Ober-Klasse für PAP-Hamster-Befehle
 * 
 * @author gerrit
 * 
 */
public abstract class CommandObject extends FlowchartObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5986329020819810129L;
	private BufferedImage background;
	private BufferedImage image;
	private BufferedImage highlight;

	public CommandObject(String command) {
		super();
		setString(command);
		this.image = FlowchartUtil.getImage("flowchart/command.png");
		this.background = FlowchartUtil
				.getImage("flowchart/command_background.png");
		this.highlight = FlowchartUtil
				.getImage("flowchart/command_highlight.png");
		this.setId(FlowchartUtil.generateId());
		this.setToolTipText(this.getText());
	}

	@Override
	public void draw(Graphics g) {
		g.setFont(f);
		if (this.isHighlighted) {
			g.drawImage(this.highlight, this.x - 30, this.y - 30, null);
		} else {
			g.drawImage(this.background, this.x, this.y, null);
		}
		g.drawImage(this.image, this.x, this.y, null);
		JLabel textLabel = new JLabel(this.getText(), SwingConstants.CENTER);
		textLabel.setBounds(0, 0, this.getWidth() - 24, this.getHeight());
		textLabel.setLocation(this.x, this.y);
		BufferedImage textLabelImage = new BufferedImage(this.getWidth(),
				this.getHeight(), BufferedImage.BITMASK);
		textLabel.paint(textLabelImage.getGraphics());
		g.drawImage(textLabelImage, this.x + 12, this.y, null);
	}

	@Override
	public FlowchartObject clone() {
		FlowchartObject tmp = new ToolbarCommandObject(this.getText());
		tmp.setCoordinates(this.x, this.y);
		return tmp;
	}

	@Override
	public boolean isActivated(int x, int y) {
		return false;
	}

	@Override
	public int getWidth() {
		return background.getWidth();
	}

	@Override
	public int getHeight() {
		return background.getHeight();
	}

}
