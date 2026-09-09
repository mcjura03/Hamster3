package de.hamster.flowchart.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import de.hamster.flowchart.FlowchartPanel;
import de.hamster.flowchart.controller.FlowchartController;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.FlowchartMethod;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.RenderObject;

public class FlowchartGlassPane extends JComponent implements
		MouseInputListener, MouseWheelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 20411927724097593L;
	private Component currentComponent;
	private Point diff;
	private FlowchartHamsterFile file;
	private FlowchartObject flowchartElement;

	private FlowchartPanel flowchartPanel;
	private Component lastComponent;
	private FlowchartController localController;
	private JScrollBar tmpScrollBar;
	private CommentObject commentElement;

	public FlowchartGlassPane(JTabbedPane drawPanel, JComponent toolbox,
			FlowchartPanel flowchartPanel, FlowchartHamsterFile file) {
		super();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.flowchartPanel = flowchartPanel;
		this.setName("GlassPane");
		this.diff = new Point();
		this.file = file;
		this.localController = file.getProgram().getController();
	}

	public FlowchartController getLocalController() {
		return this.localController;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		redispatchMouseEvent(e, 0);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		redispatchMouseEvent(e, 2);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		redispatchMouseEvent(e, 0);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		redispatchMouseEvent(e, 0);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		redispatchMouseEvent(e, 0);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		redispatchMouseEvent(e, 1);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.tmpScrollBar = null;
		redispatchMouseEvent(e, 3);
		this.flowchartElement = null;
		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		redispatchMouseEvent(e, 0);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		if (this.flowchartElement != null) {
			this.flowchartElement.draw(g);
		}
		if (this.commentElement != null) {
			this.commentElement.draw(g);
		}
	}

	private void redispatchMouseEvent(MouseEvent e, int method) {
		this.file.getProgram().setController(localController);
		Point glassPanePoint = e.getPoint();

		Container container = flowchartPanel.getMainPane();
		Point containerPoint = SwingUtilities.convertPoint(this,
				glassPanePoint, container);

		if (containerPoint.y < 0) { // we're not in the content pane
			if (containerPoint.y >= 0) {
				// The mouse event is over the menu bar.
				// Could handle specially.
			} else {
				// The mouse event is over non-system window
				// decorations, such as the ones provided by
				// the Java look and feel.
				// Could handle specially.
			}
		} else {

			// The mouse event is probably over the content pane.
			// Find out exactly which component it's over.

			if (lastComponent != null && currentComponent != null
					&& lastComponent != currentComponent) {
				lastComponent.dispatchEvent(new MouseEvent(lastComponent,
						MouseEvent.MOUSE_EXITED, e.getWhen(), e.getModifiers(),
						e.getX(), e.getY(), e.getClickCount(), e
								.isPopupTrigger(), e.getButton()));
			}

			lastComponent = currentComponent;

			Component component = SwingUtilities.getDeepestComponentAt(
					container, containerPoint.x, containerPoint.y);

			if (component instanceof JScrollBar) {
				this.tmpScrollBar = (JScrollBar) component;
			}

			// dispatch MouseWheelEvent
			if (e instanceof MouseWheelEvent)
				component.dispatchEvent(e);

			currentComponent = component;

			// SwingUtilities.getDeepestComponentAt(
			// container, containerPoint.x, containerPoint.y);
			if ((component instanceof JComponent)) {
				// Forward events over the check box.
				Point componentPoint = SwingUtilities.convertPoint(this,
						glassPanePoint, component);
				if ((e instanceof MouseEvent)
						&& !(e instanceof MouseWheelEvent)) {

					MouseEvent tmpMouseEvent = new MouseEvent(component,
							e.getID(), e.getWhen(), e.getModifiers(),
							componentPoint.x, componentPoint.y,
							e.getClickCount(), e.isPopupTrigger(),
							e.getButton());

					component.dispatchEvent(tmpMouseEvent);

					if (tmpScrollBar != null
							&& e.getID() == MouseEvent.MOUSE_DRAGGED) {
						if (component instanceof JScrollBar) {
							tmpScrollBar.dispatchEvent(new MouseEvent(
									tmpScrollBar, MouseEvent.MOUSE_DRAGGED, e
											.getWhen(), e.getModifiers(),
									componentPoint.x, componentPoint.y, e
											.getClickCount(), e
											.isPopupTrigger(), e.getButton()));
						} else {
							tmpScrollBar.dispatchEvent(new MouseEvent(
									tmpScrollBar, MouseEvent.MOUSE_DRAGGED,
									e.getWhen(),
									e.getModifiers(),
									// Der glassPanePoint muss verschoben
									// werden, damit der ScrollBalken nicht
									// "springt"
									glassPanePoint.x - 115,
									glassPanePoint.y - 75, e.getClickCount(), e
											.isPopupTrigger(), e.getButton()));
						}
					}

					if (component instanceof Toolbox && method == 1) {

						RenderObject tmpRender = ((Toolbox) component)
								.getItemAtPoint(componentPoint);
						if (tmpRender instanceof FlowchartObject) {
							this.flowchartElement = (FlowchartObject) tmpRender;
						} else if (tmpRender instanceof CommentObject) {
							this.commentElement = (CommentObject) tmpRender;
						}

						if (this.flowchartElement != null) {
							// difference between root and clickpoint
							diff.x = this.flowchartElement.x - componentPoint.x;
							diff.y = this.flowchartElement.y - componentPoint.y;

						}

						if (this.commentElement != null) {
							// difference between root and clickpoint
							diff.x = this.commentElement.x - componentPoint.x;
							diff.y = this.commentElement.y - componentPoint.y;
						}
					}

					// Mouse dragged
					if (method == 2) {
						if (this.flowchartElement != null) {
							this.flowchartElement.setCoordinates(e.getX()
									+ diff.x, e.getY() + diff.y);
							this.repaint();
						}

						if (this.commentElement != null) {
							this.commentElement.setCoordinates(e.getX()
									+ diff.x, e.getY() + diff.y);
							this.repaint();
						}

						if (component instanceof FlowchartDrawPanel
								&& this.flowchartElement != null) {
							((FlowchartDrawPanel) component)
									.dragFlowchartObject(
											new MouseEvent(component,
													e.getID(), e.getWhen(), e
															.getModifiers(),
													componentPoint.x,
													componentPoint.y, e
															.getClickCount(), e
															.isPopupTrigger(),
													e.getButton()),
											this.flowchartElement, diff);
						}
					}

					// Mouse released
					if (method == 3) {
						if (component instanceof FlowchartDrawPanel) {
							for (FlowchartMethod m : localController
									.getMethods()) {
								if (m != null
										&& component.getName().equals(
												m.getName())) {
									if (flowchartElement != null) {
										FlowchartChoicePopup popup = new FlowchartChoicePopup(
												flowchartElement, m, new Point(
														componentPoint.x
																+ diff.x,
														componentPoint.y
																+ diff.y),
												component, this.file, false);
										popup.show(e.getComponent(), e.getX()
												+ diff.x, e.getY() + diff.y);
										this.flowchartElement = null;
									} else if (this.commentElement != null) {
										FlowchartTextBox textbox = new FlowchartTextBox(
												commentElement.clone(), m,
												new Point(componentPoint.x
														+ diff.x,
														componentPoint.y
																+ diff.y),
												(FlowchartDrawPanel) component,
												this.file);
										((JComponent) component).add(textbox);
										textbox.requestFocus();
										this.commentElement = null;
									}

									component.repaint();
								}
							}

							this.repaint();
						}

					}
				}
			}
		}

	}
}