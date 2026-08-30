package de.hamster.flowchart.model;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.controller.FlowchartProgram;

public class ProcedureObject extends FlowchartObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5223304199530418006L;
	private BufferedImage background;
	private BufferedImage highlight;
	private BufferedImage image;

	public ProcedureObject(String procedure, String perform) {
		super();
		setString(procedure);
		this.image = FlowchartUtil.getImage("flowchart/procedure.png");
		this.background = FlowchartUtil
				.getImage("flowchart/command_background.png");
		this.highlight = FlowchartUtil
				.getImage("flowchart/command_highlight.png");
		this.setId(FlowchartUtil.generateId());
		this.setType("procedure");
		this.setPerform(perform);
		this.setString(procedure);
	}

	@Override
	public FlowchartObject clone() {
		FlowchartObject tmp = new ProcedureObject(this.getText(), "") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1259821568416067127L;

			@Override
			public Object executeImpl(FlowchartProgram program) {
				return null;
			}
		};

		tmp.setCoordinates(this.x, this.y);

		return tmp;
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
				this.getHeight(), Transparency.BITMASK);
		textLabel.paint(textLabelImage.getGraphics());
		g.drawImage(textLabelImage, this.x + 12, this.y, null);
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		hamster.sleep();
		return this.getPerform();
	}

	@Override
	public int getHeight() {
		return background.getHeight();
	}

	@Override
	public int getWidth() {
		return background.getWidth();
	}

	@Override
	public boolean isActivated(int x, int y) {
		return false;
	}

}
