package de.hamster.scheme.view;

import java.awt.Dimension;

import javax.swing.*;

import de.hamster.workbench.Utils;

/**
 * Diese Klasse stellt ein JFrame dar, welches Exceptins vom JScheme-Interpreter
 * entgegen nimmt und diese grafisch darstellt.
 * 
 * @author momo
 * 
 */
// dibo TODO: Formatierung
public class SchemeExceptionPanelDB extends JFrame {

	protected JOptionPane optionPane;

	public SchemeExceptionPanelDB(String txt) {
		super("Scheme-Exception");

		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// vor jedes Warning und Error neue Zeile beginnen
		txt = txt.replaceAll("ERROR", "ERROR");
		txt = txt.replaceAll("WARNING", "WARNING");
		txt = txt.replaceAll("SchemeException", "SchemeException");
		// ' rausnehmen wegen html-code
		txt = txt.replaceAll("'", "");

		String newTxt = "";
		int i = 0;
		while (i < txt.length()) {
			if (i != 0 && i % 70 == 0) {
				while (i < txt.length() && txt.charAt(i) != ' ') {
					newTxt += txt.charAt(i);
					i++;
				}
				if (txt.charAt(i) == ' ') {
					newTxt += "\n";
					i++;
				}
			} else {
				newTxt += txt.charAt(i);
				i++;
			}

		}

		optionPane = new JOptionPane();
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		optionPane.setMessage(newTxt);
		//optionPane.setIcon(Utils.getIcon("info.gif"));
		optionPane.setOptions(new Object[] {new JLabel()});
		
		setContentPane(optionPane);
		pack();
		setLocation(220, 220);
		this.toFront();
		setVisible(true);
		this.toFront();
	}

}
