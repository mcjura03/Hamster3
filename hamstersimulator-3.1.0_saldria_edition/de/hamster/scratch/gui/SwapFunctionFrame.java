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
import javax.swing.WindowConstants;

import de.hamster.scratch.Method;
import de.hamster.scratch.Renderable;
import de.hamster.scratch.ScratchPanel;
import de.hamster.scratch.ScratchUtils;
import de.hamster.scratch.Renderable.RType;

// dibo 13.08.2010
public class SwapFunctionFrame extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1270644035404964812L;
	public static final int WIDTH = 300;
	public static final int HEIGHT = 200;

	public static JFrame parent = ScratchUtils.getWorkbenchFrame();

	private JButton cancelButton;
	private JButton okButton;
	private JTextField nameField;
	private JLabel statusLabel;
	private ScratchPanel scratchPanel;
	private RType rType;
	private Renderable removeElem;

	private Method method;

	public SwapFunctionFrame(ScratchPanel scratchPanel, RType rType,
			Renderable rElem) {
		super(SwapFunctionFrame.parent, true);

		this.rType = rType;
		this.scratchPanel = scratchPanel;
		this.method = null;
		this.removeElem = rElem;

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("Neue " + (rType == RType.VOID ? "Prozedur" : "Funktion"));
		this.center();
		this.setLayout(null);

		this.cancelButton = new JButton("Abbrechen");
		this.cancelButton.setBounds(SwapFunctionFrame.WIDTH / 2 + 5,
				SwapFunctionFrame.HEIGHT - 80, 100, 30);
		this.cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwapFunctionFrame.this.cancelClick();
			}
		});
		this.add(this.cancelButton);

		this.okButton = new JButton("OK");
		this.okButton.setBounds(SwapFunctionFrame.WIDTH / 2 - 110,
				SwapFunctionFrame.HEIGHT - 80, 100, 30);
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwapFunctionFrame.this
						.okClick(SwapFunctionFrame.this.removeElem);
			}
		});
		this.okButton.setEnabled(false);
		this.add(this.okButton);

		JLabel nameLabel = new JLabel("Name");
		nameLabel.setBounds(10, 85, 40, 20);
		this.add(nameLabel);

		this.nameField = new JTextField();
		this.nameField.setBounds(50, 85, 215, 20);
		this.nameField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					SwapFunctionFrame.this
							.okClick(SwapFunctionFrame.this.removeElem);
				}

				SwapFunctionFrame.this.nameFieldChanged();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		this.add(this.nameField);

		this.statusLabel = new JLabel("<HTML><BODY>Erstelle eine neue "
				+ (rType == RType.VOID ? "Prozedur" : "Funktion")
				+ "</BODY></HTML>");
		this.statusLabel.setVerticalAlignment(SwingConstants.TOP);
		this.statusLabel.setBounds(20, 20, 250, 50);
		this.add(this.statusLabel);

		this.setVisible(true);
	}

	private void cancelClick() {
		this.dispose();
	}

	private void okClick(Renderable removeElem) {
		if (!this.nameFieldChanged()) {
			return;
		}

		this.method = this.scratchPanel.swapMethod(this.nameField.getText(),
				this.rType, removeElem);

		this.dispose();
	}

	public Method getMethod() {
		return this.method;
	}

	private boolean nameFieldChanged() {
		try {
			this.scratchPanel.checkJavaIdentifier(this.nameField.getText());
			this.statusLabel.setText("<HTML><BODY>Erstelle eine neue "
					+ (this.rType == RType.VOID ? "Prozedur" : "Funktion")
					+ "</BODY></HTML>");
			this.okButton.setEnabled(true);
			return true;
		} catch (InvalidIdentifierException e) {
			this.statusLabel.setText("<HTML><BODY>" + e.getMessage()
					+ "</BODY></HTML>");
			this.okButton.setEnabled(false);
		}
		return false;
	}

	public void center() {
		this
				.setBounds(
						SwapFunctionFrame.parent.getLocationOnScreen().x
								+ (SwapFunctionFrame.parent.getWidth() - SwapFunctionFrame.WIDTH)
								/ 2,
						SwapFunctionFrame.parent.getLocationOnScreen().y
								+ (SwapFunctionFrame.parent.getHeight() - SwapFunctionFrame.HEIGHT)
								/ 2, SwapFunctionFrame.WIDTH,
						SwapFunctionFrame.HEIGHT);
	}
}
