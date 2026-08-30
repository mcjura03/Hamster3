package de.hamster.scratch.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.hamster.scratch.ScratchPanel;
import de.hamster.scratch.ScratchUtils;

public class RenameFunctionFrame extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1270644035404964811L;
	public static final int WIDTH = 300;
	public static final int HEIGHT = 200;
	
	public static JFrame parent = ScratchUtils.getWorkbenchFrame();
	
	private JButton cancelButton;
	private JButton okButton;
	private JTextField nameField;
	private JLabel statusLabel;
	private ScratchPanel scratchPanel;
	private String fromName;
	
	public RenameFunctionFrame(ScratchPanel scratchPanel, String fromName) {
		super(parent, true);
		
		this.fromName = fromName;
		this.scratchPanel = scratchPanel;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Methode \"" + fromName + "\" umbenennen");
		center();
		setLayout(null);
		
		cancelButton = new JButton("Abbrechen");
		cancelButton.setBounds(WIDTH / 2 + 5, HEIGHT - 80, 100, 30);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelClick();
			}
		});
		add(cancelButton);
		
		okButton = new JButton("OK");
		okButton.setBounds(WIDTH / 2 - 110, HEIGHT - 80, 100, 30);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okClick();
			}
		});
		okButton.setEnabled(false);
		add(okButton);
		
		JLabel nameLabel = new JLabel("name");
		nameLabel.setBounds(10, 85, 40, 20);
		add(nameLabel);
		
		nameField = new JTextField();
		nameField.setBounds(50, 85, 215, 20);
		nameField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					okClick();
					
				nameFieldChanged();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		add(nameField);
		
		statusLabel = new JLabel("<HTML><BODY>Methode \"" + fromName + "\" umbenennen.</BODY></HTML>");
		statusLabel.setVerticalAlignment(SwingConstants.TOP);
		statusLabel.setBounds(20, 20, 250, 50);
		add(statusLabel);
		
		setVisible(true);
	}
	
	private void cancelClick() {
		dispose();
	}
	
	private void okClick() {
		if (!nameFieldChanged())
			return;
		
		scratchPanel.renameMethod(fromName, nameField.getText());
		dispose();
	}
	
	private boolean nameFieldChanged() {
		try {
			scratchPanel.checkJavaIdentifier(nameField.getText());
			statusLabel.setText("<HTML><BODY>Methode \"" + fromName + "\" umbenennen.</BODY></HTML>");
			okButton.setEnabled(true);
			return true;
		} catch (InvalidIdentifierException e) {
			statusLabel.setText("<HTML><BODY>" + e.getMessage() + "</BODY></HTML>");
			okButton.setEnabled(false);
		}
		return false;
	}
	
	public void center() {
		this.setBounds(parent.getLocationOnScreen().x + (parent.getWidth() - WIDTH) / 2, parent.getLocationOnScreen().y + (parent.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
	}
}
