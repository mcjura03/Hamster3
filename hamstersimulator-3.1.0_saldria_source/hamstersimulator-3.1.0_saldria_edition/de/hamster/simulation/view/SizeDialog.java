package de.hamster.simulation.view;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.2 $
 */
public class SizeDialog extends JDialog {
	protected JOptionPane optionPane;
	protected JTextField cols, rows;
	protected Component parent;

	protected Dimension size;

	public SizeDialog(Component parent) {
		super(JOptionPane.getFrameForComponent(parent), Utils
				.getResource("simulation.dialog.terrainsize.title"), true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.parent = parent;

		cols = new JTextField("10");
		rows = new JTextField("10");

		optionPane = new JOptionPane();
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		optionPane.setMessage(new Object[]{new JLabel(Utils.getResource("simulation.view.spalten")), cols,
				new JLabel(Utils.getResource("simulation.view.zeilen")), rows});
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if (isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					setVisible(false);
				}
			}
		});

		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setContentPane(optionPane);
		pack();
	}

	public Dimension requestSize() {
		setLocationRelativeTo(parent);
		optionPane.setValue(null);
		setVisible(true);
		if (optionPane.getValue() == null) {
			return null;
		}
		int value = ((Integer) optionPane.getValue()).intValue();
		if (value == JOptionPane.YES_OPTION) {
			return new Dimension(Integer.parseInt(cols.getText()), Integer
					.parseInt(rows.getText()));
		} else {
			return null;
		}
	}
}