package de.hamster.fsm.model.transition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.controller.handler.DeleteObjectInContextMenu;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.view.ContextMenuPanel;

/**
 * Oberklasse, von der alle Booleschen Operatoren und Operanden abgeleitet
 * werden.
 * 
 * @author Raffaela Ferrari
 * 
 */
public class BooleanObject extends FsmObject {

	protected static final Color TEXT_COLOR = new Color(255, 255, 255);
	protected static final Color COLOR_1 = new Color(0, 0, 0);
	protected static final Color COLOR_2 = new Color(98, 194, 19);
	protected static final Color BACKGROUND = new Color(78, 140, 27);
	protected static final Color BACKGROUND_LINE = new Color(68, 90, 51);
	protected static final int PADDING = 3;
	protected static final int RECHEIGHT = 13;
	protected static final int RECWIDTH = 20;

	protected RoundRectangle2D roundRec;
	protected RoundRectangle2D roundRecLeft;
	protected RoundRectangle2D roundRecRight;

	protected int positioning;
	protected boolean leftChildHighlighting;
	protected boolean canIntegrate;

	/**
	 * Konstruktor
	 * 
	 * @param name
	 *            Name des Boolschen Operatoren/Operanden.
	 * @param positioning
	 *            Gibt an, ob das Objekt rechts oder links in einem anderen
	 *            BooleanObjekt positioniert werden soll.
	 */
	public BooleanObject(String name, int positioning) {
		this.name = name;
		this.positioning = positioning;
		setTextCoordinates(this.name);
	}

	@Override
	public int getWidth() {
		int child0 = RECWIDTH;
		int child1 = RECWIDTH;
		if (this.childs.size() > 0) {
			child0 = this.childs.get(0).getWidth();
		}
		if (this.childs.size() > 1) {
			child1 = this.childs.get(1).getWidth();
		}
		return (int) child0 + child1 + textWidth + 4 * PADDING;
	}

	@Override
	public int getHeight() {
		int maxChildHeight = RECHEIGHT;
		if (this.childs.size() > 0) {
			maxChildHeight = this.childs.get(0).getHeight();
			if (this.childs.size() > 1) {
				if (maxChildHeight < this.childs.get(1).getHeight()) {
					maxChildHeight = this.childs.get(1).getHeight();
				}
			}
		}
		return (int) maxChildHeight + 2 * PADDING;
	}

	/**
	 * Gibt die Stelle zurück, an der das Objekt bei einem anderen
	 * BooleanObject eingefügt werden soll.
	 * 
	 * @return
	 */
	public int getPositioning() {
		return this.positioning;
	}

	/**
	 * Setzt die Stelle, an der das Objekt eingefügt werden soll bei einem
	 * anderen BooleanObject.
	 * 
	 * @param positioning
	 *            Angabe der Position.
	 */
	public void setPositioning(int positioning) {
		this.positioning = positioning;
	}

	@Override
	public List<JMenuItem> getContextMenuItems(ContextMenuPanel panel) {
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();

		JMenuItem deleteVoid = new JMenuItem(this.name + " l�schen");
		deleteVoid
				.addActionListener(new DeleteObjectInContextMenu(this, panel));
		menuItemList.add(deleteVoid);

		return menuItemList;
	}

	@Override
	public boolean isClickedOn(int x, int y) {
		return roundRec.contains(x, y);
	}

	@Override
	public FsmObject isClicked(int x, int y) {
		for (FsmObject child : this.childs) {
			FsmObject tmp = child.isClicked(x, y);
			if (tmp != null) {
				return tmp;
			}
		}
		if (isClickedOn(x, y)) {
			return this;
		}
		return null;
	}

	/**
	 * Überprüft ob das rechte oder linke Rechteck angeklickt wurde oder die
	 * Entsprechenden bei den Kind-Elementen.
	 * 
	 * @param x
	 *            X-Koordinate des Klicks.
	 * @param y
	 *            Y-Koordinate des Klicks.
	 * @return Das Objekt, bei dem das rechte oder linke Rechteck angeklickt
	 *         wurde.
	 */
	public BooleanObject isClickedChildRec(int x, int y) {
		for (FsmObject child : this.childs) {
			BooleanObject tmp = ((BooleanObject) child).isClickedChildRec(x, y);
			if (tmp != null) {
				return tmp;
			}
		}
		if (isClickedOnChildRec(x, y)) {
			return this;
		}
		return null;
	}

	/**
	 * Gibt zurück, ob das rechte oder linke Rechteck angeklickt wurde.
	 * 
	 * @param x
	 *            X-Koordinate des Klicks.
	 * @param y
	 *            Y-Koordinate des Klicks.
	 * @return true, wenn eins der Rechtecke angeklickt wurden.
	 */
	public boolean isClickedOnChildRec(int x, int y) {
		boolean isClicked = false;
		if (roundRecLeft != null) {
			isClicked = roundRecLeft.contains(x, y);
		}
		if (!isClicked && roundRecRight != null) {
			isClicked = roundRecRight.contains(x, y);
		}
		return isClicked;
	}

	/**
	 * Gibt zurück, ob das rechte Rechteck angeklickt wurde.
	 * 
	 * @param x
	 *            X-Koordinate des Klicks.
	 * @param y
	 *            Y-Koordinate des Klicks.
	 * @return true, wenn das rechte Rechteck angeklickt wurde.
	 */
	public boolean isClickedOnChildRecRight(int x, int y) {
		if (roundRecRight != null) {
			return roundRecRight.contains(x, y);
		} else {
			return false;
		}
	}

	/**
	 * Soll die dazugehörige Variablen setzen, damit klar ist ob der
	 * Integrierungsbereich gehighlightet werden soll.
	 * 
	 * @param canIntegrate
	 *            true, wenn der Integrierungsbereich gehighlightet werden soll.
	 * @param leftChild
	 *            true, wenn das Linke Element gehighlightet werden soll.
	 */
	public void setCanIntegrate(boolean leftChild, boolean canIntegrate) {
		this.leftChildHighlighting = leftChild;
		this.canIntegrate = canIntegrate;
	}

	/**
	 * Fügt ein Element hinzu, wenn nicht schon ein Element an der
	 * entsprechenden Position vorhanden ist.
	 */
	@Override
	public void add(FsmObject element) {
		if (this.childs.size() < 2) {
			if (this.childs.size() == 1) {
				if (((BooleanObject) element).getPositioning() == 0) {
					if (((BooleanObject) this.childs.get(0)).getPositioning() != 0) {
						this.childs.add(0, element);
					}
				} else {
					if (((BooleanObject) this.childs.get(0)).getPositioning() != 1) {
						this.childs.add(element);
					}
				}
			} else {
				this.childs.add(element);
			}
		}
	}

	@Override
	public void render(Graphics g) {
		int child0Width = RECWIDTH;
		int child0Height = RECHEIGHT;
		int child1Width = RECWIDTH;
		int child1Height = RECHEIGHT;
		if (this.childs.size() > 0) {
			if (((BooleanObject) this.childs.get(0)).getPositioning() == 1) {
				child1Height = this.childs.get(0).getHeight();
				child1Width = this.childs.get(0).getWidth();
				this.childs.get(0).setCoordinates(
						this.xStart + child0Width + textWidth + 3 * PADDING,
						this.yStart + PADDING);
			} else {
				child0Width = this.childs.get(0).getWidth();
				child0Height = this.childs.get(0).getHeight();
				this.childs.get(0).setCoordinates(this.xStart + PADDING,
						this.yStart + PADDING);
			}
		}
		if (this.childs.size() == 2) {
			child1Width = this.childs.get(1).getWidth();
			child1Height = this.childs.get(1).getHeight();
			this.childs.get(1).setCoordinates(
					this.xStart + child0Width + textWidth + 3 * PADDING,
					this.yStart + PADDING);
		}

		int maxChildHeight = (child0Height > child1Height ? child0Height
				: child1Height);
		int height = maxChildHeight + 2 * PADDING;

		Graphics2D g2d = (Graphics2D) g;

		Stroke stroke = new BasicStroke((float) (1 * ZOOMFACTOR));
		g2d.setStroke(stroke);

		roundRec = new RoundRectangle2D.Double(this.xStart, this.yStart,
				child0Width + child1Width + textWidth + 4 * PADDING, height, 5,
				5);
		g2d.setPaint(COLOR_2);
		g2d.fill(roundRec);
		if (highlight) {
			g2d.setColor(HIGHLIGHTCOLOR);
		} else {
			g2d.setColor(COLOR_1);
		}
		g2d.draw(roundRec);

		roundRecLeft = new RoundRectangle2D.Double(this.xStart + PADDING,
				this.yStart + PADDING, child0Width, child0Height, 2, 2);
		roundRecRight = new RoundRectangle2D.Double(this.xStart + textWidth
				+ child0Width + 3 * PADDING, this.yStart + PADDING,
				child1Width, child1Height, 2, 2);
		if (this.canIntegrate) {
			if (this.leftChildHighlighting) {
				g2d.setColor(INTEGRATE_COLOR);
				g2d.fill(roundRecLeft);
				g2d.setColor(BACKGROUND);
				g2d.fill(roundRecRight);
			} else {
				g2d.setColor(BACKGROUND);
				g2d.fill(roundRecLeft);
				g2d.setColor(INTEGRATE_COLOR);
				g2d.fill(roundRecRight);
			}
		} else {
			g2d.setColor(BACKGROUND);
			g2d.fill(roundRecLeft);
			g2d.fill(roundRecRight);
		}

		g2d.setColor(BACKGROUND_LINE);
		g2d.draw(roundRecLeft);
		g2d.draw(roundRecRight);

		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(this.name, this.xStart + 2 * PADDING + child0Width,
				this.yStart + height / 2 + textHeight / 2 - PADDING);

		super.render(g);
	}

	/**
	 * Methode für die Transformation des Endlichen Automaten in ein
	 * imperatives Java-Program.
	 * 
	 * @param buffer
	 *            Buffer, indem der SourceCode geschrieben wird.
	 * @param indentation
	 *            Einrückfaktor
	 */
	public void writeSourceCode(StringBuffer buffer, int indentation) {
		// nur die Subklassen haben diese Funktion implementiert
	}

	@Override
	public FsmObject clone() {
		return null;
	}

	@Override
	public Object performImplementation(FsmProgram program)
			throws IsNondeterministicException {
		return null;
	}

	/**
	 * Simuliert die Ausführung des booleschen Ausdrucks.
	 * 
	 * @param program
	 *            FsmProgram, dass ausgeführt werden soll.
	 * @return
	 */
	public Object checkPerform(FsmProgram program) {
		return null;
	}

	@Override
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 6);
		writer.writeStartElement("booleanObject");
		writer.writeAttribute("name", this.name);
		writer.writeAttribute("positioning", String.valueOf(this.positioning));
		for (FsmObject child : this.childs) {
			writer.writeCharacters(NEWLINE);
			setLinefeed(writer, 6);
			writer.writeStartElement("child"); // dibo
			child.toXML(writer);
			writer.writeCharacters(NEWLINE);
			setLinefeed(writer, 6);
			writer.writeEndElement(); // dibo
			writer.writeCharacters(NEWLINE); // dibo
		}
		writer.writeCharacters(NEWLINE);
		setLinefeed(writer, 6);
		writer.writeEndElement();
		writer.writeCharacters(NEWLINE);
	}

	/**
	 * Methode, um aus der Xml-Repräsentation des endlichen Automaten Objecte
	 * zu generieren.
	 * 
	 * @param booleanElement
	 *            Mit diesem Element können die entsprechenden Xml-Elemente
	 *            ausgelesen werden, um das BooleanElement zu erstellen.
	 */
	public void loadProgramm(Element booleanElement) {
		// Lade alle Attribute
		this.positioning = Integer.parseInt(booleanElement
				.getAttribute("positioning"));
		Element[] childs = new Element[2];
		int a = 0;
		NodeList children = booleanElement.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node currentChild = children.item(i);

			switch (currentChild.getNodeType()) {

			case Node.ELEMENT_NODE:
				childs[a++] = (Element) children.item(i);
				break;
			}
		}

		if (childs[0] != null) {
			Element booleanMethodChild0 = (Element) childs[0]
					.getElementsByTagName("booleanObject").item(0);
			if (booleanMethodChild0 != null) {
				String name = booleanMethodChild0.getAttribute("name");
				BooleanObject booleanObject = (BooleanObject) FsmUtils
						.getTransistionDescriptionELementByName(name);
				booleanObject.loadProgramm(booleanMethodChild0);
				booleanObject.setParent(this);
				add(booleanObject);

				if (childs[1] != null) {
					Element booleanMethodChild1 = (Element) childs[1]
							.getElementsByTagName("booleanObject").item(0);
					if (booleanMethodChild1 != null) {
						name = booleanMethodChild1.getAttribute("name");
						booleanObject = (BooleanObject) FsmUtils
								.getTransistionDescriptionELementByName(name);
						booleanObject.loadProgramm(booleanMethodChild1);
						booleanObject.setParent(this);
						add(booleanObject);
					}
				}
			}
		}
	}
	// public void loadProgramm(Element booleanElement) {
	// // Lade alle Attribute
	// this.positioning = Integer.parseInt(booleanElement
	// .getAttribute("positioning"));
	//
	// Element booleanMethodChild0 = (Element) booleanElement
	// .getElementsByTagName("booleanObject").item(0);
	// if (booleanMethodChild0 != null) {
	// String name = booleanMethodChild0.getAttribute("name");
	// BooleanObject booleanObject = (BooleanObject) FsmUtils
	// .getTransistionDescriptionELementByName(name);
	// booleanObject.loadProgramm(booleanMethodChild0);
	// booleanObject.setParent(this);
	// add(booleanObject);
	//
	// Element booleanMethodChild1 = (Element) booleanElement
	// .getElementsByTagName("booleanObject").item(1);
	// if (booleanMethodChild1 != null) {
	// name = booleanMethodChild1.getAttribute("name");
	// booleanObject = (BooleanObject) FsmUtils
	// .getTransistionDescriptionELementByName(name);
	// booleanObject.loadProgramm(booleanMethodChild1);
	// booleanObject.setParent(this);
	// add(booleanObject);
	// }
	// }
	// }
}
