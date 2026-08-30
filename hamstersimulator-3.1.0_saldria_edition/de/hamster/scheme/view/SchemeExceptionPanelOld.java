package de.hamster.scheme.view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Diese Klasse stellt ein JFrame dar, welches Exceptins vom JScheme-Interpreter
 * entgegen nimmt und diese grafisch darstellt.
 * 
 * @author momo
 * 
 */
// dibo TODO: Formatierung
public class SchemeExceptionPanelOld extends JFrame {

	JLabel error = new JLabel();;

	public SchemeExceptionPanelOld(String txt) {
		super("Scheme-Exception");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// vor jedes Warning und Error neue Zeile beginnen
		txt = txt.replaceAll("ERROR", "<br>ERROR");
		txt = txt.replaceAll("WARNING", "<br>WARNING");
		txt = txt.replaceAll("SchemeException", "<br>SchemeException");
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
					newTxt += "<br>";
					i++;
				}
			} else {
				newTxt += txt.charAt(i);
				i++;
			}

		}

		error.setText("<html>" + newTxt + "</html>");
		add(error);

		Dimension d = new Dimension(error.getPreferredSize());
		setSize(d.width + 50, d.height + 50);
		setLocation(220, 220);
		setVisible(true);
	}

}
