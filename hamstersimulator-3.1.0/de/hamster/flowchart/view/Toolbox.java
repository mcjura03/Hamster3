package de.hamster.flowchart.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.List;

import javax.swing.JPanel;

import de.hamster.flowchart.FlowchartUtil;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.RenderObject;

public class Toolbox extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Font f = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	private List<RenderObject> flowchartElements;

	private ArrowModeButton toggle;

	public Toolbox(String text) {
		super();
		this.setPreferredSize(new Dimension(100, 1));
		this.setName(text);
		this.setLayout(new GridLayout(1, 1));
		this.flowchartElements = FlowchartUtil.getRenderObjects();
		this.toggle = new ArrowModeButton();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		int yOffset = 20;
		for (RenderObject o : this.flowchartElements) {
			o.setCoordinates(5, yOffset);
			o.draw(g);
			yOffset += o.getHeight() + 5;
		}
		this.toggle.setCoords(5, yOffset);
		toggle.draw(g);

	}

	public RenderObject getItemAtPoint(Point componentPoint) {
		for (RenderObject o : this.flowchartElements) {
			int x = o.getX();
			int y = o.getY();
			if (componentPoint.x >= x && componentPoint.x <= (x + o.getWidth())
					&& componentPoint.y >= y
					&& componentPoint.y <= (y + o.getHeight())) {

				if (o instanceof FlowchartObject)
					return ((FlowchartObject) o).clone();

				if (o instanceof CommentObject)
					return ((CommentObject) o).clone();

			}
		}

		if (componentPoint.x >= toggle.getCoords().x
				&& componentPoint.x <= (toggle.getCoords().x + toggle
						.getWidth())
				&& componentPoint.y >= toggle.getCoords().y
				&& componentPoint.y <= (toggle.getCoords().y + toggle
						.getHeight())) {
			toggle.toggle();
		}

		return null;
	}

}
