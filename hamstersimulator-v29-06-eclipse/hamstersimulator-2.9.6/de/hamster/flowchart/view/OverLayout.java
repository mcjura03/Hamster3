package de.hamster.flowchart.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import de.hamster.flowchart.FlowchartPanel;

public class OverLayout implements LayoutManager {

	private int preferredWidth = 0, preferredHeight = 0;
	private FlowchartPanel flowchartPanel;

	public OverLayout(FlowchartPanel flowchartPanel) {
		this.flowchartPanel = flowchartPanel;
	}

	@Override
	public void layoutContainer(Container parent) {

		int nComps = parent.getComponentCount();

		for (int i = 0; i < nComps; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {

				// Set the component's size and position.
				c.setBounds(0, 0, flowchartPanel.getWidth(),
						flowchartPanel.getHeight());
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		setSizes(parent);

		// Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = preferredWidth + insets.left + insets.right;
		dim.height = preferredHeight + insets.top + insets.bottom;

		return dim;
	}

	private void setSizes(Container parent) {
		int nComps = parent.getComponentCount();
		for (int i = 0; i < nComps; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {
				Dimension d = c.getPreferredSize();

				// Set the component's size and position.
				c.setBounds(0, 0, d.width, d.height);

			}
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		setSizes(parent);

		// Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = preferredWidth + insets.left + insets.right;
		dim.height = preferredHeight + insets.top + insets.bottom;

		return dim;
	}

	@Override
	public void removeLayoutComponent(Component arg0) {
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

}
