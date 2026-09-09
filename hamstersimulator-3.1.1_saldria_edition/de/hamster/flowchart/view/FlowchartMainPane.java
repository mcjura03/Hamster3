package de.hamster.flowchart.view;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public class FlowchartMainPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8592990061489476095L;

	public FlowchartMainPane() {
		super();
	}

	public FlowchartMainPane(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public FlowchartMainPane(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public FlowchartMainPane(LayoutManager layout) {
		super(layout);
	}

}
