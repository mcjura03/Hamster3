package de.hamster.flowchart.view;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.FlowchartMethod;

public class FlowchartTextBox extends JTextField implements KeyListener,
		MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -202307938279631610L;
	private CommentObject tmpComment;
	private FlowchartDrawPanel component;
	private FlowchartHamsterFile file;

	public FlowchartTextBox(CommentObject commentElement, FlowchartMethod m,
			Point point, FlowchartDrawPanel comp, FlowchartHamsterFile file) {
		super();
		this.component = comp;
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.file = file;
		tmpComment = commentElement;
		tmpComment.setCoordinates(point);
		tmpComment.setTextBox(this);
		m.addComment(tmpComment);

		this.component.repaint();
		this.setBounds(point.x + 5, point.y + 5, 80, 50);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.tmpComment.setString(this.getText());
			this.setVisible(false);
			this.file.setModified(true);
			this.component.repaint();

		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.setText(this.tmpComment.getText());
			this.setVisible(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setVisible(true);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.setVisible(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Aktualisiert die Position vom TextFeld.
	 */
	public void updateLocation() {
		this.setBounds(tmpComment.x + 5, tmpComment.y + 5, 80, 50);

	}

}
