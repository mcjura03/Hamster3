package de.hamster.scheme.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.hamster.workbench.Utils;

/**
 * Diese Klasse stellt ein Fenster dar, welches eine Eingabe für die read-Funktion in Scheme
 * entgegen nimmt.
 * @author momo
 *
 */
public class ReadFenster extends JFrame implements ActionListener {

	JLabel text;
	JTextField input;
	JButton submit;
	JComboBox auswahlKlasse; 
	
	Object eingabe;
	
	public ReadFenster() {
		
		super("Scheme - (read)");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(null);
		
		text = new JLabel(Utils.getResource("scheme.view.eingabe"));	
		add(text);
		Dimension d = new Dimension(text.getPreferredSize());		
		text.setBounds(20, 20, d.width, d.height);
		
		auswahlKlasse = new JComboBox();
		auswahlKlasse.setMaximumRowCount(2);
		auswahlKlasse.setModel(new javax.swing.DefaultComboBoxModel(new String[] { Utils.getResource("scheme.view.zahl"), Utils.getResource("scheme.view.zeichenkette") }));
		//add(auswahlKlasse);
		d = new Dimension(auswahlKlasse.getPreferredSize());		
		auswahlKlasse.setBounds(100, 20, d.width, d.height);
		
		input = new JTextField(20);
		add(input);
		d = new Dimension(input.getPreferredSize());		
		input.setBounds(20, 50, d.width, d.height);
		
		submit = new JButton(Utils.getResource("scheme.view.senden"));
		submit.addActionListener(this);
		add(submit);
		d = new Dimension(submit.getPreferredSize());		
		submit.setBounds(135-(d.width/2), 85, d.width, d.height);
		
		setSize(270, 150);
		setLocation(250, 130);
		
		eingabe = "";
	}

	public void actionPerformed(ActionEvent arg0) {
		eingabe = input.getText();
		input.setText("");
		setVisible(false);
	}
	
	public JComboBox getAuswahlKlasse() {
		return auswahlKlasse;
	}

	public Object getEingabe() {
		return eingabe;
	}
	
}
