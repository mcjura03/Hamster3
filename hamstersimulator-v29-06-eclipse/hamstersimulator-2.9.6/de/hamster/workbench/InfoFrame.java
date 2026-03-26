/*
 * Created on 31.05.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.hamster.workbench;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class InfoFrame extends JFrame {
	private static InfoFrame instance;

	protected JOptionPane optionPane;

	private InfoFrame() {
		setTitle("Info");
		optionPane = new JOptionPane();
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		optionPane.setMessage("<html><h1>Hamster-Simulator</h1><br>" +
				"Version 2.9.6<br>" +
				Utils.getResource("workbench.info") + " <h3>D. Jasper, D. Boles<br>" +
				"www.java-hamster-modell.de</h3></html>");
		optionPane.setIcon(Utils.getIcon("info.gif"));
		optionPane.setOptions(new Object[] {new JLabel()});
		
		setContentPane(optionPane);
		pack();
	}
	
	public static InfoFrame getInstance() {
		if(instance == null) {
			instance = new InfoFrame();
		}
		return instance;
	}
}
