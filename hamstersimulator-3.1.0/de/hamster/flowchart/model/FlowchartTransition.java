package de.hamster.flowchart.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.flowchart.FlowchartUtil;

/**
 * Die Klasse FlowchartTransition ist da, um die Verbindungen zwischen den
 * verschiedenen Flowchart Objekten zu zeichnen. Außerdem werden hier dann auch
 * die Kindobjekte zugewiesen.
 * 
 * @author gerrit
 * 
 */
public class FlowchartTransition {

	// transition from source to destination
	private FlowchartObject source;
	private FlowchartObject destination;

	// while dragging mouse
	private Point tmpDestination;

	// 0=north; 1=east; 2=south; 3=west
	private int sourceOriantation;
	private int destinationOriantation = -1;

	// nodes for drawing the transition
	// depends on orientation
	private TransitionNode nodeA;
	private TransitionNode nodeB;
	private TransitionNode nodeC;
	private FlowchartAnchor sourceAnchor;
	private FlowchartAnchor destAnchor;

	// for decision transitions
	public Boolean hasTrueChild;

	/**
	 * This is the Flowchart Transition Object for representing the Connections
	 * between FlowchartObjects
	 */
	public FlowchartTransition(FlowchartObject source, int orientationS) {
		super();
		this.source = source;
		this.sourceOriantation = orientationS;

		// changes while moving the mouse
		this.tmpDestination = new Point(source.x, source.y);
	}

	/**
	 * Zeichenmethode zum Zeichnen der Transition.
	 * 
	 * @param g
	 *            Die Grafik, auf die die Transition gezeichnet wird.
	 */
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		Graphics2D line = (Graphics2D) g;
		line.setStroke(new BasicStroke(3));

		// Ankerpunkt um die Linie korrekt an das Objekt zu zeichnen
		sourceAnchor = this.source.getAnchor(this.sourceOriantation);

		int tmpDestOrient = 0;
		if (destinationOriantation < 0) {
			int degree = (int) (Math.toDegrees((Math
					.atan((double) (tmpDestination.y - sourceAnchor.y)
							/ (double) (tmpDestination.x - sourceAnchor.x)))));
			if (tmpDestination.y <= sourceAnchor.y
					&& tmpDestination.x < sourceAnchor.x) {
				degree += 180;
			} else if (tmpDestination.y < sourceAnchor.y
					&& tmpDestination.x >= sourceAnchor.x) {
				degree = 360 + degree;
			} else if (tmpDestination.y >= sourceAnchor.y
					&& tmpDestination.x < sourceAnchor.x) {
				degree += 180;
			}

			if (degree > 315 || degree <= 45) {
				tmpDestOrient = 1;
			} else if (degree > 45 && degree <= 135) {
				tmpDestOrient = 2;
			} else if (degree > 135 && degree <= 225) {
				tmpDestOrient = 3;
			} else {
				tmpDestOrient = 0;
			}

		} else {
			tmpDestOrient = destinationOriantation;
		}
		Polygon p = drawArrow(tmpDestOrient);

		if (this.destination == null) {
			// malt einfach nur eine grade Linie zum Mauszeiger
			line.drawLine(sourceAnchor.x, sourceAnchor.y, tmpDestination.x,
					tmpDestination.y);
			line.fillPolygon(p);
		} else {
			destAnchor = this.destination.getAnchor(destinationOriantation);

			line.fillPolygon(p);

			if (destAnchor.y > sourceAnchor.y - 20) {
				// Ziel liegt unter der Quelle
				int signX = 1;
				if (source.x >= destination.x)
					signX = -1;

				if (sourceOriantation == 0) {
					if (destinationOriantation == 0) {
						if (source.x != destination.x) {
							createNodeA(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									sourceAnchor.y - 20));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y,
									destAnchor.x, nodeA.y);
							line.drawLine(destAnchor.x, nodeA.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
						} else {
							createNodeB(new Point(
									source.x + 110,
									sourceAnchor.y
											- 20
											+ ((destAnchor.y - 20) - (sourceAnchor.y - 20))
											/ 2));
							createNodeA(new Point(source.x + 78,
									sourceAnchor.y - 20));
							createNodeC(new Point(source.x + 78,
									destAnchor.y - 20));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x,
									nodeA.y);
							line.drawLine(nodeB.x, nodeA.y, nodeB.x, nodeC.y);
							line.drawLine(nodeB.x, nodeC.y, destAnchor.x,
									nodeC.y);
							line.drawLine(destAnchor.x, nodeC.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
							nodeB.setVertical();
							nodeC.setHorizontal();
						}
					} else if ((destinationOriantation == 1 && sourceAnchor.x < destAnchor.x + 80)
							|| (destinationOriantation == 3 && sourceAnchor.x >= destAnchor.x - 85)) {
						if (destinationOriantation == 3)
							signX = -1;
						if (destinationOriantation == 1)
							signX = 1;
						createNodeA(new Point(sourceAnchor.x
								+ (destAnchor.x + 20 * signX - sourceAnchor.x)
								/ 2, sourceAnchor.y - 20));
						createNodeB(new Point(destAnchor.x + 20 * signX,
								sourceAnchor.y
										+ (destAnchor.y - sourceAnchor.y - 20)
										/ 2));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, destAnchor.y);
						line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
					} else if (destinationOriantation == 2) {
						int xB = sourceAnchor.x
								+ (destAnchor.x - sourceAnchor.x) / 2;
						if (source.x == destination.x)
							xB += 65;
						createNodeB(new Point(xB, sourceAnchor.y
								+ (destAnchor.y - sourceAnchor.y) / 2));
						createNodeA(new Point(sourceAnchor.x
								+ (nodeB.x - sourceAnchor.x) / 2,
								sourceAnchor.y - 20));
						createNodeC(new Point(destAnchor.x
								- (destAnchor.x - nodeB.x) / 2,
								destAnchor.y + 20));
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, nodeC.y);
						line.drawLine(nodeB.x, nodeC.y, destAnchor.x, nodeC.y);
						line.drawLine(destAnchor.x, nodeC.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
						nodeC.setHorizontal();
					} else if ((destinationOriantation == 3 && destAnchor.x > source.x + 90 + 40)
							|| (destinationOriantation == 1)) {
						int xB = destination.x + 45
								+ ((source.x + 45) - (destination.x + 45)) / 2;
						createNodeB(new Point(xB, sourceAnchor.y - 20
								+ (destAnchor.y - (sourceAnchor.y - 20)) / 2));
						createNodeA(new Point(nodeB.x
								+ (sourceAnchor.x - nodeB.x) / 2,
								sourceAnchor.y - 20));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, destAnchor.y);
						line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
					} else {
						// this should never happen
					}
				} else if (sourceOriantation == 1) {
					if (destinationOriantation == 0) {

						if (destAnchor.x >= sourceAnchor.x + 20) {
							nodeA = null;
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									destAnchor.x, sourceAnchor.y);
							line.drawLine(destAnchor.x, sourceAnchor.y,
									destAnchor.x, destAnchor.y);
						} else {
							createNodeB(new Point(destAnchor.x
									+ (sourceAnchor.x + 20 - destAnchor.x) / 2,
									source.getAnchor(2).y
											+ (destAnchor.y - source
													.getAnchor(2).y) / 2));

							createNodeA(new Point(sourceAnchor.x + 20,
									sourceAnchor.y + (nodeB.y - sourceAnchor.y)
											/ 2));

							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, destAnchor.x,
									nodeB.y);
							line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
						}
					} else if (destinationOriantation == 1
							|| (destinationOriantation == 3 && sourceAnchor.x <= destination.x - 40)) {
						int nAX = 0;
						if (destinationOriantation == 1) {
							if (source.x <= destination.x)
								nAX = destAnchor.x + 20;
							if (source.x > destination.x)
								nAX = sourceAnchor.x + 20;
						} else {
							nAX = sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2;
						}
						if (source.y != destination.y
								|| destinationOriantation == 3) {
							createNodeA(new Point(nAX, sourceAnchor.y
									+ (destAnchor.y - sourceAnchor.y) / 2));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									destAnchor.y);
							line.drawLine(nodeA.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
						} else {

							createNodeB(new Point(nAX + (destAnchor.x - nAX)
									/ 2, sourceAnchor.y + 50));
							createNodeA(new Point(sourceAnchor.x + 20,
									sourceAnchor.y + (nodeB.y - sourceAnchor.y)
											/ 2));
							createNodeC(new Point(destAnchor.x + 20,
									destAnchor.y + (nodeB.y - destAnchor.y) / 2));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, nodeC.x, nodeB.y);
							line.drawLine(nodeC.x, nodeB.y, nodeC.x,
									destAnchor.y);
							line.drawLine(nodeC.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
							nodeC.setVertical();
						}

					} else if (destinationOriantation == 3
							&& (sourceAnchor.x > destination.x - 40)
							|| destinationOriantation == 3
							&& sourceAnchor.x <= destination.x + 40) {
						if (sourceAnchor.x > destination.x - 40) {
							if (destination.y != source.y) {
								createNodeB(new Point(
										sourceAnchor.x
												+ (destAnchor.x - sourceAnchor.x)
												/ 2,
										sourceAnchor.y
												+ (destAnchor.y - sourceAnchor.y)
												/ 2));
							} else {
								createNodeB(new Point(
										sourceAnchor.x
												+ (destAnchor.x - sourceAnchor.x)
												/ 2,
										sourceAnchor.y
												+ (destAnchor.y - sourceAnchor.y)
												/ 2 + 45));
							}
							createNodeA(new Point(sourceAnchor.x + 20,
									sourceAnchor.y + (nodeB.y - sourceAnchor.y)
											/ 2));

							createNodeC(new Point(destAnchor.x - 20, nodeB.y
									+ (destAnchor.y - nodeB.y) / 2));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, nodeC.x, nodeB.y);
							line.drawLine(nodeC.x, nodeB.y, nodeC.x,
									destAnchor.y);
							line.drawLine(nodeC.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
							nodeC.setVertical();
						} else {

						}
					} else if (destinationOriantation == 2) {
						createNodeA(new Point(sourceAnchor.x + 20,
								sourceAnchor.y
										+ (destAnchor.y + 20 - sourceAnchor.y)
										/ 2));
						createNodeB(new Point(sourceAnchor.x + 20
								+ (destAnchor.x - sourceAnchor.x - 20) / 2,
								destAnchor.y + 20));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x, nodeB.y);
						line.drawLine(nodeA.x, nodeB.y, destAnchor.x, nodeB.y);
						line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
						nodeB.setHorizontal();
					} else {
						// this should never happen
					}
				} else if (sourceOriantation == 2) {
					if (destinationOriantation == 0) {
						createNodeA(new Point(sourceAnchor.x
								+ (destination.x - source.x) / 2,
								sourceAnchor.y
										+ (destAnchor.y - sourceAnchor.y) / 2));
						nodeB = null;
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, destAnchor.x,
								nodeA.y);
						line.drawLine(destAnchor.x, nodeA.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
					} else if ((destinationOriantation == 1 && sourceAnchor.x <= destAnchor.x + 20)
							|| (destinationOriantation == 3 && sourceAnchor.x >= destAnchor.x - 20)) {
						if (destinationOriantation == 3)
							signX = -1;
						if (destinationOriantation == 1)
							signX = 1;
						createNodeA(new Point(sourceAnchor.x
								+ ((destAnchor.x + 20) - sourceAnchor.x) / 2,
								sourceAnchor.y
										+ (destination.y - sourceAnchor.y) / 2));
						createNodeB(new Point(destAnchor.x + 20 * signX,
								destAnchor.y - (destAnchor.y - nodeA.y) / 2));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, destAnchor.y);
						line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
					} else if (destinationOriantation == 2) {
						if (source.x != destination.x) {
							createNodeA(new Point(sourceAnchor.x
									+ ((destAnchor.x - sourceAnchor.x) / 2),
									destAnchor.y + 20));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y,
									destAnchor.x, nodeA.y);
							line.drawLine(destAnchor.x, nodeA.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
						} else {
							createNodeB(new Point(
									source.x + 110,
									sourceAnchor.y
											+ 20
											+ ((destAnchor.y + 20) - (sourceAnchor.y + 20))
											/ 2));
							createNodeA(new Point(source.x + 78,
									sourceAnchor.y + 20));
							createNodeC(new Point(source.x + 78,
									destAnchor.y + 20));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x,
									nodeA.y);
							line.drawLine(nodeB.x, nodeA.y, nodeB.x, nodeC.y);
							line.drawLine(nodeB.x, nodeC.y, destAnchor.x,
									nodeC.y);
							line.drawLine(destAnchor.x, nodeC.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
							nodeB.setVertical();
							nodeC.setHorizontal();
						}
					} else if ((destinationOriantation == 3 && source.x < destination.x)
							|| (destinationOriantation == 1 && source.x > destination.x)) {
						nodeA = null;
						nodeB = null;
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, destAnchor.y);
						line.drawLine(sourceAnchor.x, destAnchor.y,
								destAnchor.x, destAnchor.y);
					} else {
						// this should never happen
					}

				} else if (sourceOriantation == 3) {
					if (destinationOriantation == 0) {

						if (destAnchor.x <= sourceAnchor.x - 20) {
							nodeA = null;
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									destAnchor.x, sourceAnchor.y);
							line.drawLine(destAnchor.x, sourceAnchor.y,
									destAnchor.x, destAnchor.y);
						} else {
							createNodeA(new Point(
									sourceAnchor.x - 20,
									sourceAnchor.y
											+ (destAnchor.y - 20 - sourceAnchor.y)
											/ 2));
							createNodeB(new Point(sourceAnchor.x - 20
									+ (destAnchor.x - sourceAnchor.x + 20) / 2,
									source.getAnchor(2).y
											+ (destAnchor.y - source
													.getAnchor(2).y) / 2));
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, destAnchor.x,
									nodeB.y);
							line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();

						}

					} else if (destinationOriantation == 1
							&& sourceAnchor.x <= destAnchor.x + 40) {
						if (source.y != destination.y) {
							createNodeB(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									sourceAnchor.y
											+ (destAnchor.y - sourceAnchor.y)
											/ 2));
						} else {
							createNodeB(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									sourceAnchor.y
											+ (destAnchor.y - sourceAnchor.y)
											/ 2 - 45));
						}
						createNodeA(new Point(sourceAnchor.x - 20,
								sourceAnchor.y + (nodeB.y - sourceAnchor.y) / 2));
						createNodeC(new Point(destAnchor.x + 20, nodeB.y
								+ (destAnchor.y - nodeB.y) / 2));
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x, nodeB.y);
						line.drawLine(nodeA.x, nodeB.y, nodeC.x, nodeB.y);
						line.drawLine(nodeC.x, nodeB.y, nodeC.x, destAnchor.y);
						line.drawLine(nodeC.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
						nodeB.setHorizontal();
						nodeC.setVertical();
					} else if (destinationOriantation == 2) {
						createNodeA(new Point(sourceAnchor.x - 20,
								sourceAnchor.y
										+ (destAnchor.y + 20 - sourceAnchor.y)
										/ 2));
						createNodeB(new Point(sourceAnchor.x
								+ (destAnchor.x - sourceAnchor.x - 20) / 2,
								destAnchor.y + 20));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x, nodeB.y);
						line.drawLine(nodeA.x, nodeB.y, destAnchor.x, nodeB.y);
						line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
						nodeB.setHorizontal();
					} else if (destinationOriantation == 3
							|| (destinationOriantation == 1 && sourceAnchor.x > destAnchor.x + 40)) {

						int nAX = 0;
						if (destinationOriantation == 3) {
							if (source.x >= destination.x)
								nAX = destAnchor.x - 20;
							if (source.x < destination.x)
								nAX = sourceAnchor.x - 20;
						} else {
							nAX = sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2;
						}

						if (source.y != destination.y
								|| destinationOriantation == 1) {
							createNodeA(new Point(nAX, sourceAnchor.y
									+ (destAnchor.y - sourceAnchor.y) / 2));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									destAnchor.y);
							line.drawLine(nodeA.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
						} else {

							createNodeB(new Point(nAX + (destAnchor.x - nAX)
									/ 2, sourceAnchor.y + 50));
							createNodeA(new Point(sourceAnchor.x - 20,
									sourceAnchor.y + (nodeB.y - sourceAnchor.y)
											/ 2));
							createNodeC(new Point(destAnchor.x - 20,
									destAnchor.y + (nodeB.y - destAnchor.y) / 2));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, nodeC.x, nodeB.y);
							line.drawLine(nodeC.x, nodeB.y, nodeC.x,
									destAnchor.y);
							line.drawLine(nodeC.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
							nodeC.setVertical();
						}
					} else {
						// this should never happen
					}
				} else {
					// this should never happen
				}

			} else if (destAnchor.y <= sourceAnchor.y - 20) {
				if (sourceOriantation == 0) {
					if (destinationOriantation == 0) {
						if (destination.x != source.x) {
							createNodeA(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									destAnchor.y - 20));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y,
									destAnchor.x, nodeA.y);
							line.drawLine(destAnchor.x, nodeA.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
						} else {
							createNodeB(new Point(
									source.x + 110,
									sourceAnchor.y
											- 20
											+ ((destAnchor.y - 20) - (sourceAnchor.y - 20))
											/ 2));
							createNodeA(new Point(source.x + 78,
									sourceAnchor.y - 20));
							createNodeC(new Point(source.x + 78,
									destAnchor.y - 20));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x,
									nodeA.y);
							line.drawLine(nodeB.x, nodeA.y, nodeB.x, nodeC.y);
							line.drawLine(nodeB.x, nodeC.y, destAnchor.x,
									nodeC.y);
							line.drawLine(destAnchor.x, nodeC.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
							nodeB.setVertical();
							nodeC.setHorizontal();
						}
					} else if (destinationOriantation == 1) {

						if (destAnchor.x + 20 <= sourceAnchor.x) {
							nodeA = null;
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, destAnchor.y);
							line.drawLine(sourceAnchor.x, destAnchor.y,
									destAnchor.x, destAnchor.y);
						} else {

							createNodeA(new Point(sourceAnchor.x
									+ (destAnchor.x + 20 - sourceAnchor.x) / 2,
									sourceAnchor.y + 10
											+ (destAnchor.y - sourceAnchor.y)
											/ 2));
							createNodeB(new Point(destAnchor.x + 20,
									destAnchor.y
											+ (sourceAnchor.y - destAnchor.y)
											/ 4));
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x,
									nodeA.y);
							line.drawLine(nodeB.x, nodeA.y, nodeB.x,
									destAnchor.y);
							line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
							nodeB.setVertical();
						}
					} else if (destinationOriantation == 2) {
						createNodeA(new Point(sourceAnchor.x
								+ (destAnchor.x - sourceAnchor.x) / 2,
								destAnchor.y + (sourceAnchor.y - destAnchor.y)
										/ 2));
						nodeB = null;
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, destAnchor.x,
								nodeA.y);
						line.drawLine(destAnchor.x, nodeA.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
					} else if (destinationOriantation == 3) {
						if (destAnchor.x - 20 >= sourceAnchor.x) {
							nodeA = null;
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, destAnchor.y);
							line.drawLine(sourceAnchor.x, destAnchor.y,
									destAnchor.x, destAnchor.y);
						} else {
							createNodeA(new Point((destAnchor.x - 20)
									+ (sourceAnchor.x - (destAnchor.x - 20))
									/ 2, destAnchor.y + 30
									+ (sourceAnchor.y - destAnchor.y - 30) / 2));
							createNodeB(new Point(destAnchor.x - 20,
									destAnchor.y + (nodeA.y - destAnchor.y) / 2));
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x,
									nodeA.y);
							line.drawLine(nodeB.x, nodeA.y, nodeB.x,
									destAnchor.y);
							line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
							nodeB.setVertical();
						}
					} else {
						// this should never happen
					}
				} else if (sourceOriantation == 1) {
					if (destinationOriantation == 0) {

						if (destination.x - 50 <= sourceAnchor.x) {
							createNodeA(new Point(
									sourceAnchor.x + 20,
									destAnchor.y
											- 20
											+ (sourceAnchor.y - (destAnchor.y - 20))
											/ 2));
						} else {
							createNodeA(new Point(
									sourceAnchor.x
											+ (destination.x - sourceAnchor.x)
											/ 2,
									destAnchor.y
											- 20
											+ (sourceAnchor.y - (destAnchor.y - 20))
											/ 2));
						}
						createNodeB(new Point(nodeA.x
								+ (destAnchor.x - nodeA.x) / 2,
								destAnchor.y - 20));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x, nodeB.y);
						line.drawLine(nodeA.x, nodeB.y, destAnchor.x, nodeB.y);
						line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
						nodeB.setHorizontal();
					} else if (destinationOriantation == 1) {
						int nAX = 0;
						if (destinationOriantation == 1) {
							if (source.x <= destination.x)
								nAX = destAnchor.x + 20;
							if (source.x > destination.x)
								nAX = sourceAnchor.x + 20;
						} else {
							nAX = sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2;
						}
						createNodeA(new Point(nAX, sourceAnchor.y
								+ (destAnchor.y - sourceAnchor.y) / 2));
						nodeB = null;
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
								destAnchor.y);
						line.drawLine(nodeA.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
					} else if (destinationOriantation == 2) {

						if (destAnchor.x >= sourceAnchor.x + 20) {
							nodeA = null;
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									destAnchor.x, sourceAnchor.y);
							line.drawLine(destAnchor.x, sourceAnchor.y,
									destAnchor.x, destAnchor.y);

						} else {
							createNodeB(new Point(sourceAnchor.x + 20
									+ (destAnchor.x - (sourceAnchor.x + 20))
									/ 2, destAnchor.y
									+ (source.y - destAnchor.y) / 2));
							createNodeA(new Point(sourceAnchor.x + 20, nodeB.y
									+ (sourceAnchor.y - nodeB.y) / 2));
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, destAnchor.x,
									nodeB.y);
							line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
						}

					} else if (destinationOriantation == 3) {

						if (destAnchor.x - 20 >= sourceAnchor.x + 20) {
							createNodeA(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									destAnchor.y
											+ (sourceAnchor.y - destAnchor.y)
											/ 2));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									destAnchor.y);
							line.drawLine(nodeA.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
						} else {
							createNodeB(new Point(destAnchor.x
									+ (sourceAnchor.x - destAnchor.x) / 2,
									destAnchor.y
											+ (sourceAnchor.y - destAnchor.y)
											/ 2));
							createNodeA(new Point(sourceAnchor.x + 20, nodeB.y
									+ (sourceAnchor.y - nodeB.y) / 2));
							createNodeC(new Point(destAnchor.x - 20,
									destAnchor.y + (nodeB.y - destAnchor.y) / 2));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, nodeC.x, nodeB.y);
							line.drawLine(nodeC.x, nodeB.y, nodeC.x,
									destAnchor.y);
							line.drawLine(nodeC.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
							nodeC.setVertical();
						}
					} else {
						// this should never happen
					}
				} else if (sourceOriantation == 2) {
					if (destinationOriantation == 0) {
						int xB = sourceAnchor.x
								+ (destAnchor.x - sourceAnchor.x) / 2;
						if (source.x == destination.x)
							xB -= 65;
						createNodeB(new Point(xB, destAnchor.y
								+ (sourceAnchor.y - destAnchor.y) / 2));
						createNodeA(new Point(sourceAnchor.x
								+ (nodeB.x - sourceAnchor.x) / 2,
								sourceAnchor.y + 20));
						createNodeC(new Point(nodeB.x
								+ (destAnchor.x - nodeB.x) / 2,
								destAnchor.y - 20));
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, nodeC.y);
						line.drawLine(nodeB.x, nodeC.y, destAnchor.x, nodeC.y);
						line.drawLine(destAnchor.x, nodeC.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
						nodeC.setHorizontal();
					} else if (destinationOriantation == 1) {
						createNodeA(new Point(sourceAnchor.x
								+ (destAnchor.x + 20 - sourceAnchor.x) / 2,
								sourceAnchor.y + 20));
						createNodeB(new Point(destAnchor.x + 20, destAnchor.y
								+ (sourceAnchor.y + 20 - destAnchor.y) / 2));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, destAnchor.y);
						line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
					} else if (destinationOriantation == 2) {
						if (source.x != destination.x) {
							createNodeA(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									sourceAnchor.y + 20));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y,
									destAnchor.x, nodeA.y);
							line.drawLine(destAnchor.x, nodeA.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
						} else {
							createNodeB(new Point(
									source.x + 110,
									sourceAnchor.y
											+ 20
											+ ((destAnchor.y + 20) - (sourceAnchor.y + 20))
											/ 2));
							createNodeA(new Point(source.x + 78,
									sourceAnchor.y + 20));
							createNodeC(new Point(source.x + 78,
									destAnchor.y + 20));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									sourceAnchor.x, nodeA.y);
							line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x,
									nodeA.y);
							line.drawLine(nodeB.x, nodeA.y, nodeB.x, nodeC.y);
							line.drawLine(nodeB.x, nodeC.y, destAnchor.x,
									nodeC.y);
							line.drawLine(destAnchor.x, nodeC.y, destAnchor.x,
									destAnchor.y);
							nodeA.setHorizontal();
							nodeB.setVertical();
							nodeC.setHorizontal();
						}
					} else if (destinationOriantation == 3) {
						createNodeA(new Point(sourceAnchor.x
								+ (destAnchor.x - 20 - sourceAnchor.x) / 2,
								sourceAnchor.y + 20));
						createNodeB(new Point(destAnchor.x - 20, destAnchor.y
								+ (sourceAnchor.y + 20 - destAnchor.y) / 2));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y,
								sourceAnchor.x, nodeA.y);
						line.drawLine(sourceAnchor.x, nodeA.y, nodeB.x, nodeA.y);
						line.drawLine(nodeB.x, nodeA.y, nodeB.x, destAnchor.y);
						line.drawLine(nodeB.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setHorizontal();
						nodeB.setVertical();
					} else {
						// this should never happen
					}
				} else if (sourceOriantation == 3) {
					if (destinationOriantation == 0) {
						createNodeA(new Point(sourceAnchor.x - 20, destAnchor.y
								- 20 + (sourceAnchor.y - destAnchor.y - 20) / 2));
						createNodeB(new Point(sourceAnchor.x - 20
								+ (destAnchor.x - sourceAnchor.x) / 2,
								destAnchor.y - 20));
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x, nodeB.y);
						line.drawLine(nodeA.x, nodeB.y, destAnchor.x, nodeB.y);
						line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
						nodeB.setHorizontal();
					} else if (destinationOriantation == 1) {

						if (destAnchor.x <= sourceAnchor.x - 40) {
							createNodeA(new Point(destAnchor.x
									+ (sourceAnchor.x - destAnchor.x) / 2,
									sourceAnchor.y
											- (sourceAnchor.y - destAnchor.y)
											/ 2));
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									destAnchor.y);
							line.drawLine(nodeA.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
						} else {

							createNodeB(new Point(sourceAnchor.x
									+ (destAnchor.x - sourceAnchor.x) / 2,
									destAnchor.y
											+ (sourceAnchor.y - destAnchor.y)
											/ 2));
							createNodeA(new Point(sourceAnchor.x - 20, nodeB.y
									+ (sourceAnchor.y - nodeB.y) / 2));
							createNodeC(new Point(destAnchor.x + 20,
									destAnchor.y + (nodeB.y - destAnchor.y) / 2));
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, nodeC.x, nodeB.y);
							line.drawLine(nodeC.x, nodeB.y, nodeC.x,
									destAnchor.y);
							line.drawLine(nodeC.x, destAnchor.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
							nodeC.setVertical();
						}
					} else if (destinationOriantation == 2) {
						if (destAnchor.x <= sourceAnchor.x - 20) {
							nodeA = null;
							nodeB = null;
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									destAnchor.x, sourceAnchor.y);
							line.drawLine(destAnchor.x, sourceAnchor.y,
									destAnchor.x, destAnchor.y);
						} else {
							createNodeB(new Point(sourceAnchor.x - 20
									+ (destAnchor.x - (sourceAnchor.x - 20))
									/ 2, destAnchor.y
									+ (source.y - destAnchor.y) / 2));
							createNodeA(new Point(sourceAnchor.x - 20, nodeB.y
									+ (sourceAnchor.y - nodeB.y) / 2));
							nodeC = null;
							line.drawLine(sourceAnchor.x, sourceAnchor.y,
									nodeA.x, sourceAnchor.y);
							line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
									nodeB.y);
							line.drawLine(nodeA.x, nodeB.y, destAnchor.x,
									nodeB.y);
							line.drawLine(destAnchor.x, nodeB.y, destAnchor.x,
									destAnchor.y);
							nodeA.setVertical();
							nodeB.setHorizontal();
						}
					} else if (destinationOriantation == 3) {
						int nAX = 0;
						if (source.x >= destination.x)
							nAX = destAnchor.x - 20;
						if (source.x < destination.x)
							nAX = sourceAnchor.x - 20;
						createNodeA(new Point(nAX, destAnchor.y
								+ (sourceAnchor.y - destAnchor.y) / 2));
						nodeB = null;
						nodeC = null;
						line.drawLine(sourceAnchor.x, sourceAnchor.y, nodeA.x,
								sourceAnchor.y);
						line.drawLine(nodeA.x, sourceAnchor.y, nodeA.x,
								destAnchor.y);
						line.drawLine(nodeA.x, destAnchor.y, destAnchor.x,
								destAnchor.y);
						nodeA.setVertical();
					} else {
						// this should never happen
					}
				} else {
					// this should never happen
				}

			} else {
				// this should never happen
			}

			// zeige die Knoten im Transition-Bearbeitungsmodus
			if (FlowchartUtil.TRANSITIONMODE) {
				if (nodeA != null)
					g.drawImage(FlowchartUtil.ANHCORIMG, nodeA.x - 5,
							nodeA.y - 5, null);
				if (nodeB != null)
					g.drawImage(FlowchartUtil.ANHCORIMG, nodeB.x - 5,
							nodeB.y - 5, null);
				if (nodeC != null)
					g.drawImage(FlowchartUtil.ANHCORIMG, nodeC.x - 5,
							nodeC.y - 5, null);
			}

			// zeichne "ja" und "nein" an die Entscheidungen.
			if (source instanceof DecisionObject) {
				if (this.hasTrueChild) {
					switch (sourceOriantation) {
					case 0:
						g.drawString("ja", sourceAnchor.x + 10,
								sourceAnchor.y - 5);
						break;
					case 1:
						g.drawString("ja", sourceAnchor.x - 5,
								sourceAnchor.y - 15);
						break;

					case 2:
						g.drawString("ja", sourceAnchor.x + 10,
								sourceAnchor.y + 5);
						break;

					case 3:
						g.drawString("ja", sourceAnchor.x - 15,
								sourceAnchor.y + 15);
						break;

					default:
						break;
					}

				} else {
					switch (sourceOriantation) {
					case 0:
						g.drawString("nein", sourceAnchor.x + 10,
								sourceAnchor.y - 5);
						break;
					case 1:
						g.drawString("nein", sourceAnchor.x - 10,
								sourceAnchor.y - 10);
						break;

					case 2:
						g.drawString("nein", sourceAnchor.x + 10,
								sourceAnchor.y + 5);
						break;

					case 3:
						g.drawString("nein", sourceAnchor.x - 15,
								sourceAnchor.y + 15);
						break;

					default:
						break;
					}
				}
			}
		}
		// }
	}

	/**
	 * Zeichnet die Pfeilspitze in abhängigkeit der Orientierung.
	 * 
	 * @param o
	 *            Die Orientierung
	 * @return Die Pfeilspitze als Polygon
	 */
	private Polygon drawArrow(int o) {
		Polygon p = new Polygon();
		if (tmpDestination != null) {
			if (o == 2) {
				p.addPoint(tmpDestination.x, tmpDestination.y + 5);
				p.addPoint(tmpDestination.x - 5, tmpDestination.y - 7);
				p.addPoint(tmpDestination.x + 5, tmpDestination.y - 7);
			} else if (o == 1) {
				p.addPoint(tmpDestination.x + 5, tmpDestination.y);
				p.addPoint(tmpDestination.x - 7, tmpDestination.y - 5);
				p.addPoint(tmpDestination.x - 7, tmpDestination.y + 5);
			} else if (o == 3) {
				p.addPoint(tmpDestination.x - 5, tmpDestination.y);
				p.addPoint(tmpDestination.x + 7, tmpDestination.y + 5);
				p.addPoint(tmpDestination.x + 7, tmpDestination.y - 5);
			} else if (o == 0) {
				p.addPoint(tmpDestination.x, tmpDestination.y - 5);
				p.addPoint(tmpDestination.x - 5, tmpDestination.y + 7);
				p.addPoint(tmpDestination.x + 5, tmpDestination.y + 7);
			} else {
				// this should never happen
			}
		} else if (destination != null) {
			Point tmpPoint = new Point(destination.getAnchor(o));
			if (o == 0) {
				p.addPoint(tmpPoint.x, tmpPoint.y + 5);
				p.addPoint(tmpPoint.x - 5, tmpPoint.y - 7);
				p.addPoint(tmpPoint.x + 5, tmpPoint.y - 7);
			} else if (o == 3) {
				p.addPoint(tmpPoint.x + 5, tmpPoint.y);
				p.addPoint(tmpPoint.x - 7, tmpPoint.y - 5);
				p.addPoint(tmpPoint.x - 7, tmpPoint.y + 5);
			} else if (o == 1) {
				p.addPoint(tmpPoint.x - 5, tmpPoint.y);
				p.addPoint(tmpPoint.x + 7, tmpPoint.y + 5);
				p.addPoint(tmpPoint.x + 7, tmpPoint.y - 5);
			} else if (o == 2) {
				p.addPoint(tmpPoint.x, tmpPoint.y - 5);
				p.addPoint(tmpPoint.x - 5, tmpPoint.y + 7);
				p.addPoint(tmpPoint.x + 5, tmpPoint.y + 7);
			} else {
				// this should never happen
			}
		} else {
			// this should never happen
		}
		return p;
	}

	/**
	 * upgrade the tmpDestination while destination is not set should be
	 * triggered when moving the mouse
	 * 
	 * @param d
	 *            the temporary point of destination
	 */
	public void updateTmpDestinationPoint(Point d) {
		this.tmpDestination = d;
	}

	/**
	 * set the dertermined destination should be called when mouse is released
	 * and a Flowchartobject at that point is found
	 * 
	 * @param o
	 *            the destination object
	 * @param orientationD
	 *            the oriantation at the destination object
	 * @param childAttribut
	 */
	public void setDestinationObject(FlowchartObject o, int orientationD,
			boolean childAttribut) {
		this.destination = o;
		this.destinationOriantation = orientationD;
		this.hasTrueChild = childAttribut;

		if (childAttribut) {
			this.source.setTrueChildId(o.getId());
			this.source.setTrueChild(o);
		} else {
			((DecisionObject) this.source).setFalseChildId(o.getId());
			((DecisionObject) this.source).setFalseChild(o);
		}

		if (source.equals(destination)) {
			this.destinationOriantation = (sourceOriantation + 1) % 4;
		}

		this.tmpDestination = null;
	}

	/**
	 * removing the transition removes also the child from source
	 */
	public void remove() {
		this.source.setTrueChild(null);
		this.source = null;
		this.destination = null;
		this.destinationOriantation = 0;
		this.sourceOriantation = 0;
		this.nodeA = null;
		this.nodeB = null;
		this.nodeC = null;
	}

	/**
	 * Schreibt die Transitionsdaten in den writer
	 * 
	 * @param writer
	 *            Der XMLStreamWriter in den die Informationen geschrieben
	 *            werden
	 * @throws XMLStreamException
	 */
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters("\t");
		writer.writeEmptyElement("transition");
		writer.writeAttribute("sourceId", String.valueOf(this.source.getId()));

		if (destination != null) {
			writer.writeAttribute("destId",
					String.valueOf(this.destination.getId()));
		}

		writer.writeAttribute("orientationS",
				String.valueOf(this.sourceOriantation));

		if (this.destinationOriantation > -1) {
			writer.writeAttribute("orientationD",
					String.valueOf(this.destinationOriantation));
		}
		if (nodeA != null && nodeA.getModified()) {
			writer.writeAttribute("nodeA_x", String.valueOf(nodeA.x));
			writer.writeAttribute("nodeA_y", String.valueOf(nodeA.y));
		}
		if (nodeB != null && nodeB.getModified()) {
			writer.writeAttribute("nodeB_x", String.valueOf(nodeB.x));
			writer.writeAttribute("nodeB_y", String.valueOf(nodeB.y));
		}
		if (nodeC != null && nodeC.getModified()) {
			writer.writeAttribute("nodeC_x", String.valueOf(nodeC.x));
			writer.writeAttribute("nodeC_y", String.valueOf(nodeC.y));
		}

		if (hasTrueChild != null) {
			writer.writeAttribute("childIs", String.valueOf(hasTrueChild));
		}

		if (tmpDestination != null) {
			writer.writeAttribute("tmpDest_x",
					String.valueOf(this.tmpDestination.x));
			writer.writeAttribute("tmpDest_y",
					String.valueOf(this.tmpDestination.y));
		}
		writer.writeCharacters("\n");
	}

	/**
	 * Gibt das Quell-FlowchartObjekt zurück
	 * 
	 * @return Das Quell-Objekt
	 */
	public FlowchartObject getSourceObject() {
		return this.source;
	}

	/**
	 * Entfernt das Ziel Objekt. Wird bspw. aufgerufen, wenn die Transition
	 * verschoben wird.
	 */
	public void removeDestinationObject() {
		if (this.hasTrueChild) {
			this.source.setTrueChild(null);
		} else {
			((DecisionObject) this.source).setFalseChild(null);
		}
		this.destination = null;
		this.destinationOriantation = -1;
	}

	/**
	 * Setzt ein neues Source-FlowchartObjekt
	 * 
	 * @param newSource
	 *            Das neue FlowchartObjekt
	 */
	public void setSourceObject(FlowchartObject newSource) {
		this.source = newSource;
	}

	/**
	 * Gibt das Ziel-FlowchartObjekt zurück
	 * 
	 * @return Das Ziel-Objekt
	 */
	public FlowchartObject getDestinationObject() {
		return this.destination;
	}

	/**
	 * Erzeugt einen neuen Knoten A.
	 * 
	 * @param p
	 *            Point an dem der Knoten erzeugt wird.
	 */
	public void createNodeA(Point p) {
		if (nodeA != null && !nodeA.getModified()) {
			nodeA.x = p.x;
			nodeA.y = p.y;
		} else if (nodeA == null) {
			nodeA = new TransitionNode(p, this);
		}
	}

	/**
	 * nodeA: Wird aufgerufen, wenn in der geladenen XML modifizierte
	 * Koordinaten geladen werden.
	 * 
	 * @param p
	 *            Die Koordinate vom Anker
	 * @param loaded
	 *            der Boolean, der angibt ob es eine geladene Koordinate ist.
	 */
	public void createNodeA(Point p, Boolean loaded) {
		createNodeA(p);
		if (loaded)
			nodeA.setModified();
	}

	/**
	 * Setzt die Position von Knoten A in abhängigkeit der Linie, damit der
	 * Knoten die Linie auf der er ist nicht verlässt.
	 * 
	 * @param x
	 *            gewünschte x-Koordinate
	 * @param y
	 *            gewünschte y-Koordinate
	 */
	public void setLocationNodeA(int x, int y) {

		Point nextNode;
		if (nodeB != null) {
			nextNode = nodeB;
		} else {
			nextNode = destAnchor;
		}

		if (nodeA.isHorizontal()) {
			nodeA.y = y;
			if (sourceAnchor.x < nextNode.x) {
				if (sourceAnchor.x > x) {
					nodeA.x = sourceAnchor.x;
				} else if (nextNode.x < x) {
					nodeA.x = nextNode.x;
				} else {
					nodeA.x = x;
				}
			} else if (sourceAnchor.x > nextNode.x) {
				if (nextNode.x > x) {
					nodeA.x = nextNode.x;
				} else if (sourceAnchor.x < x) {
					nodeA.x = sourceAnchor.x;
				} else {
					nodeA.x = x;
				}

			} else {
				// nodeA.x = x;
			}

		} else {
			nodeA.x = x;
			if (sourceAnchor.y < nextNode.y) {
				if (sourceAnchor.y > y) {
					nodeA.y = sourceAnchor.y;
				} else if (nextNode.y < y) {
					nodeA.y = nextNode.y;
				} else {
					nodeA.y = y;
				}
			} else if (sourceAnchor.y > nextNode.y) {
				if (nextNode.y > y) {
					nodeA.y = nextNode.y;
				} else if (sourceAnchor.y < y) {
					nodeA.y = sourceAnchor.y;
				} else {
					nodeA.y = y;
				}

			} else {
				// nodeA.y = y;
			}

		}

		if (nodeB != null) {
			setLocationNodeB(nodeB.x, nodeB.y);
		}
	}

	/**
	 * Erzeugt einen neuen Knoten B.
	 * 
	 * @param p
	 *            Point an dem der Knoten erzeugt wird.
	 */
	public void createNodeB(Point p) {
		if (nodeB != null && !nodeB.getModified()) {
			nodeB.x = p.x;
			nodeB.y = p.y;
		} else if (nodeB == null) {
			nodeB = new TransitionNode(p, this);
		}
	}

	/**
	 * nodeB: Wird aufgerufen, wenn in der geladenen XML modifizierte
	 * Koordinaten geladen werden.
	 * 
	 * @param p
	 *            Die Koordinate vom Anker
	 * @param loaded
	 *            der Boolean, der angibt ob es eine geladene Koordinate ist.
	 */
	public void createNodeB(Point p, Boolean loaded) {
		createNodeB(p);
		if (loaded)
			nodeB.setModified();
	}

	/**
	 * Setzt die Position von Knoten B in abhängigkeit der Linie, damit der
	 * Knoten die Linie auf der er ist nicht verlässt.
	 * 
	 * @param x
	 *            gewünschte x-Koordinate
	 * @param y
	 *            gewünschte y-Koordinate
	 */
	public void setLocationNodeB(int x, int y) {
		Point prevNode = nodeA;
		Point nextNode;
		if (nodeC != null) {
			nextNode = nodeC;
		} else {
			nextNode = destAnchor;
		}

		if (nodeB.isHorizontal()) {
			nodeB.y = y;
			if (prevNode.x < nextNode.x) {
				if (prevNode.x > x) {
					nodeB.x = prevNode.x;
				} else if (nextNode.x < x) {
					nodeB.x = nextNode.x;
				} else {
					nodeB.x = x;
				}

			} else if (prevNode.x > nextNode.x) {
				if (nextNode.x > x) {
					nodeB.x = nextNode.x;
				} else if (prevNode.x < x) {
					nodeB.x = prevNode.x;
				} else {
					nodeB.x = x;
				}

			} else {
				// nodeB.x = x;
			}

		} else {
			nodeB.x = x;
			if (prevNode.y < nextNode.y) {
				if (prevNode.y > y) {
					nodeB.y = prevNode.y;
				} else if (nextNode.y < y) {
					nodeB.y = nextNode.y;
				} else {
					nodeB.y = y;
				}
			} else if (prevNode.y > nextNode.y) {
				if (nextNode.y > y) {
					nodeB.y = nextNode.y;
				} else if (prevNode.y < y) {
					nodeB.y = prevNode.y;
				} else {
					nodeB.y = y;
				}

			} else {
				// nodeB.y = y;
			}

		}

		if (nodeC != null) {
			setLocationNodeC(nodeC.x, nodeC.y);
		}
	}

	/**
	 * Erzeugt einen neuen Knoten C.
	 * 
	 * @param p
	 *            Point an dem der Knoten erzeugt wird.
	 */
	public void createNodeC(Point p) {
		if (nodeC != null && !nodeC.getModified()) {
			nodeC.x = p.x;
			nodeC.y = p.y;
		} else if (nodeC == null) {
			nodeC = new TransitionNode(p, this);
		}
	}

	/**
	 * nodeC: Wird aufgerufen, wenn in der geladenen XML modifizierte
	 * Koordinaten geladen werden.
	 * 
	 * @param p
	 *            Die Koordinate vom Anker
	 * @param loaded
	 *            der Boolean, der angibt ob es eine geladene Koordinate ist.
	 */
	public void createNodeC(Point p, Boolean loaded) {
		createNodeC(p);
		if (loaded)
			nodeC.setModified();
	}

	/**
	 * Setzt die Position von Knoten C in abhängigkeit der Linie, damit der
	 * Knoten die Linie auf der er ist nicht verlässt.
	 * 
	 * @param x
	 *            gewünschte x-Koordinate
	 * @param y
	 *            gewünschte y-Koordinate
	 */
	public void setLocationNodeC(int x, int y) {
		Point prevNode = nodeB;
		Point nextNode = destAnchor;

		if (nodeC.isHorizontal()) {
			nodeC.y = y;
			if (prevNode.x < nextNode.x) {
				if (prevNode.x > x) {
					nodeC.x = prevNode.x;
				} else if (nextNode.x < x) {
					nodeC.x = nextNode.x;
				} else {
					nodeC.x = x;
				}
			} else if (prevNode.x > nextNode.x) {
				if (nextNode.x > x) {
					nodeC.x = nextNode.x;
				} else if (prevNode.x < x) {
					nodeC.x = prevNode.x;
				} else {
					nodeC.x = x;
				}

			} else {
			}

		} else {
			nodeC.x = x;
			if (prevNode.y < nextNode.y) {
				if (prevNode.y > y) {
					nodeC.y = prevNode.y;
				} else if (nextNode.y < y) {
					nodeC.y = nextNode.y;
				} else {
					nodeC.y = y;
				}
			} else if (prevNode.y > nextNode.y) {
				if (nextNode.y > y) {
					nodeC.y = nextNode.y;
				} else if (prevNode.y < y) {
					nodeC.y = prevNode.y;
				} else {
					nodeC.y = y;
				}

			} else {
			}

		}
	}

	/**
	 * Setzt die Knoten alle auf nicht-modifiziert.
	 */
	public void resetNodes() {
		if (nodeA != null)
			nodeA.resetModified();
		if (nodeB != null)
			nodeB.resetModified();
		if (nodeC != null)
			nodeC.resetModified();
	}

	/**
	 * Gibt nodeA zurück
	 * 
	 * @return TransitionNode nodeA
	 */
	public TransitionNode getNodeA() {
		return this.nodeA;
	}

	/**
	 * Gibt nodeB zurück
	 * 
	 * @return TransitionNode nodeB
	 */
	public TransitionNode getNodeB() {
		return this.nodeB;
	}

	/**
	 * Gibt node C zurück
	 * 
	 * @return TransitionNode nodeC
	 */
	public TransitionNode getNodeC() {
		return this.nodeC;
	}

	/**
	 * Gibt einem den Knoten an einem bestimmten Punkt (zB Maus Punkt)
	 * 
	 * @param m
	 *            Der Punkt an dem ein Knoten gesucht wird.
	 * @return Der evtl. gefundene Knoten, null sonst.
	 */
	public TransitionNode getNodeAtMouse(Point m) {
		for (int i = -10; i <= 10; i++) {
			for (int j = -10; j <= 10; j++) {
				if (nodeA != null)
					if (m.x + i == nodeA.x && m.y + j == nodeA.y) {
						nodeA.setModified();
						return nodeA;
					}
				if (nodeB != null)
					if (m.x + i == nodeB.x && m.y + j == nodeB.y) {
						nodeB.setModified();
						return nodeB;
					}
				if (nodeC != null)
					if (m.x + i == nodeC.x && m.y + j == nodeC.y) {
						nodeC.setModified();
						return nodeC;
					}

			}
		}
		return null;
	}

}

/**
 * Ein erweiterter Point, der ein modified Attribut besitzt.
 * 
 * @author gerrit
 * 
 */
class TransitionNode extends Point {

	private boolean modified;
	private boolean horizontal;
	private FlowchartTransition transition;
	private boolean vertical;

	/**
	 * Konstrukter ohne Übergabewert. Setzt modified auf false.
	 */
	public TransitionNode() {
		super();
		this.modified = false;
	}

	/**
	 * Konstruktor mit Koordinaten als Übergabewert (x, y) Setzt modified auf
	 * false.
	 * 
	 * @param x
	 *            Die x-Koordinate.
	 * @param y
	 *            Die y-Koordinate.
	 */
	public TransitionNode(int x, int y) {
		super(x, y);
		this.modified = false;
	}

	/**
	 * Konstruktor mit Koordinaten als Übergabewert (Point(x,y)) Setzt modified
	 * auf false.
	 * 
	 * @param p
	 *            Point mit x- und y-Koordinate.
	 */
	public TransitionNode(Point p) {
		super(p);
		this.modified = false;
	}

	/**
	 * Konstruktor, der augerufen werden sollte, wenn auf die Transition
	 * zugegriffen werden muss, zu der der Knoten gehört.
	 * 
	 * @param p
	 * @param flowchartTransition
	 */
	public TransitionNode(Point p, FlowchartTransition flowchartTransition) {
		this(p);
		this.transition = flowchartTransition;
	}

	/**
	 * Setzt modified auf true.
	 */
	public void setModified() {
		this.modified = true;
	}

	/**
	 * Gibt den modified Wert zurück
	 * 
	 * @return
	 */
	public Boolean getModified() {
		return this.modified;
	}

	/**
	 * Setzt modified zurück auf false.
	 */
	public void resetModified() {
		this.modified = false;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7971489179354271396L;

	/**
	 * Warnung: diese Methode ist übershcrieb
	 */
	@Override
	public void setLocation(int x, int y) {
		if (transition.getNodeA().equals(this)) {
			transition.setLocationNodeA(x, y);
		} else if (transition.getNodeB().equals(this)) {
			transition.setLocationNodeB(x, y);
			transition.setLocationNodeA(transition.getNodeA().x,
					transition.getNodeA().y);
		} else if (transition.getNodeC().equals(this)) {
			transition.setLocationNodeC(x, y);
			transition.setLocationNodeB(transition.getNodeB().x,
					transition.getNodeB().y);
			transition.setLocationNodeA(transition.getNodeA().x,
					transition.getNodeA().y);
		}
	}

	/**
	 * Wenn die Linie durch den Knoten horizontal verläuft, muss diese Methode
	 * aufgerufen werden.
	 */
	public void setHorizontal() {
		this.horizontal = true;
		this.vertical = false;
	}

	/**
	 * Wenn die Linie durch den Knoten vertikal verlöuft, muss diese Methode
	 * aufgerufen werden.
	 */
	public void setVertical() {
		this.vertical = true;
		this.horizontal = false;
	}

	/**
	 * Eigenschaft: Linie durch Konten ist horizontal, oder nicht.
	 * 
	 * @return true, wenn die Linie horizontal, false sonst
	 */
	public Boolean isHorizontal() {
		return this.horizontal;
	}

	/**
	 * Eigenschaft: Linie durch Knoten ist vertikal, oder nicht.
	 * 
	 * @return true, wenn die Linie vertikal, false sonst
	 */
	public Boolean isVertical() {
		return this.vertical;
	}

}
