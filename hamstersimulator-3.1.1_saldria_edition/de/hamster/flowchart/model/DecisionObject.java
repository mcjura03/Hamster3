package de.hamster.flowchart.model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.controller.FlowchartProgram;

public abstract class DecisionObject extends FlowchartObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4405355677734538618L;
	private BufferedImage background;
	private FlowchartObject falseChild;
	private int falseChildId;
	private BufferedImage image;
	private BufferedImage highlight;

	/**
	 * Konstruktor für Entscheidungs-Element
	 * 
	 * @param decision
	 */
	public DecisionObject(String decision) {
		super();
		setString(decision);
		this.image = FlowchartUtil.getImage("flowchart/decision.png");
		this.background = FlowchartUtil
				.getImage("flowchart/decision_background.png");
		this.highlight = FlowchartUtil
				.getImage("flowchart/decision_highlight.png");
		this.setId(FlowchartUtil.generateId());
	}

	/**
	 * Setzt das falseChild
	 * 
	 * @param o
	 *            das false FlowchartObject
	 */
	public void setFalseChild(FlowchartObject o) {
		this.falseChild = o;
	}

	/**
	 * Gibt das falseChild zurück
	 * 
	 * @return das false FlowchartObject
	 */
	public FlowchartObject getFalseChild() {
		return falseChild;
	}

	/**
	 * Setzt die falseChild Id
	 * 
	 * @param attribute
	 *            die Id
	 */
	public void setFalseChildId(int attribute) {
		this.falseChildId = attribute;

	}

	/**
	 * Gibt die false ChildId zurück
	 * 
	 * @return die Id
	 */
	public int getFalseChildId() {
		return falseChildId;
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
		FlowchartObject tmp = new DecisionObject(this.getText()) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7438209674857472232L;

			@Override
			public Object executeImpl(FlowchartProgram program) {
				return null;
			}
		};

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
