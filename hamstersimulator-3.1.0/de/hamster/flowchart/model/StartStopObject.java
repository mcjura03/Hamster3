package de.hamster.flowchart.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.controller.FlowchartProgram;

public class StartStopObject extends FlowchartObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4225942771880449486L;
	private BufferedImage background;
	private BufferedImage image;
	private BufferedImage highlight;
	private FlowchartMethod method;

	public StartStopObject(String name, FlowchartMethod method) {
		super();
		setString(name);
		this.image = FlowchartUtil.getImage("flowchart/start_stop.png");
		this.background = FlowchartUtil
				.getImage("flowchart/start_stop_background.png");
		this.highlight = FlowchartUtil
				.getImage("flowchart/start_stop_highlight.png");
		this.method = method;
		this.setId(FlowchartUtil.generateId());
		this.setType("startstop");
		this.setPerform("");
		this.setString(name);

		// Setze StartId oder Terminate Boolean
		if (method != null) {
			if (name.equals("Start")) {
				method.setStart(String.valueOf(this.getId()));
			} else if (name.equals("Stop") && method.getName().equals("main")) {
				// only terminate at stop object in main procedure!
				this.setTerminate();
			}
		}
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
		g.setColor(Color.WHITE);
	}

	@Override
	public FlowchartObject clone() {
		FlowchartObject tmp = new StartStopObject(this.getText(), method) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -667635360169744955L;

			@Override
			public Object executeImpl(FlowchartProgram program) {
				return null;
			}
		};

		tmp.setCoordinates(this.x, this.y);

		return tmp;
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		hamster.sleep();
		return true;
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
