package de.hamster.scratch;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hamster.scratch.Renderable.RType;
import de.hamster.scratch.elements.RootBooleanObject;
import de.hamster.scratch.elements.RootVoidObject;
import de.hamster.scratch.elements.voids.FunctionResultException;

/**
 * Die Klasse Method speichert alle Inhalte einer Methode. Die komplette
 * Definition einer Methode und die zu- sätzlichen Inhalte, die im
 * Bearbeitungsfenster angezeigt werden, jedoch nicht für die Ausführung
 * notwendig sind.
 * 
 * @author HackZ
 * 
 */
public class Method {
	private int top = 0;
	private int left = 0;
	private Renderable rootElement;
	private ArrayList<Renderable> renderables;
	private String name;
	private Renderable.RType rType;
	private boolean opened;
	private boolean selected = false;

	/**
	 * Erstelle eine neue Methode mit dem übergebenen Namen und Typ
	 * 
	 * @param name
	 *            Name der Methode
	 * @param rType
	 *            Typ der Methode (VOID, BOOLEAN, ...)
	 */
	public Method(String name, Renderable.RType rType) {
		this.name = name;
		this.rType = rType;
		renderables = new ArrayList<Renderable>();
		if (rType == Renderable.RType.BOOLEAN)
			rootElement = new RootBooleanObject();
		else
			rootElement = new RootVoidObject();
		rootElement.moveTo(ScratchPanel.OPTIONS_WIDTH + 20,
				ScratchPanel.TAB_PANEL_HEIGHT);
	}

	/**
	 * Liefert das erste ausfuehrbare Renderable Objekt dieser Methode
	 * 
	 * @return
	 */
	public Renderable getRootElement() {
		return rootElement;
	}

	/**
	 * Liefert alle nicht ausfuehrbaren Renderstacks dieser Methode
	 * 
	 * @return
	 */
	public ArrayList<Renderable> getRenderables() {
		return renderables;
	}

	/**
	 * Liefert die y-Achsenverschiebung des Fensters dieser Methode
	 * 
	 * @return
	 */
	public int getTop() {
		return top;
	}

	/**
	 * Liefert die x-Achsenverschiebung des Fensters dieser Methode
	 * 
	 * @return
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * Setzt die y-Achsenverschiebung des Fensters dieser Methode
	 * 
	 * @param top
	 */
	public void setTop(int top) {
		this.top = top;
	}

	/**
	 * Setzt die x-Achsenverschiebung des Fensters dieser Methode
	 * 
	 * @param left
	 */
	public void setLeft(int left) {
		this.left = left;
	}

	/**
	 * Liefert den Namen dieser Methode
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Liefert den Typ dieser Methode
	 * 
	 * @return
	 */
	public Renderable.RType getType() {
		return rType;
	}

	/**
	 * Liefert, ob der Tab dieser Methode geöffnet ist
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return opened;
	}

	/**
	 * Setzt fest, ob der Tab dieser Methode geöffnet ist
	 * 
	 * @param opened
	 */
	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean sel) {
		this.selected = sel;
	}

	/**
	 * Fragt ab, ob das Element mit dem übergebenen Namen in dieser Methode in
	 * irgendeiner Art verwendet wird. Dies ist notwendig, um das Element zu
	 * löschen, denn es darf nirgendwo verwendet worden sein.
	 * 
	 * @param name
	 *            Name des Elements, nach dem gesucht werden soll
	 * @return
	 */
	public boolean inUse(String name) {
		for (Renderable r : renderables)
			if (r.inUse(name))
				return true;

		if (rootElement.inUse(name))
			return true;

		return false;
	}

	/**
	 * Benennt die Methode mit dem Namen fromName in den Namen toName. Diese
	 * Methode ruft rekursiv die Methoden der Kinder auf.
	 * 
	 * @param fromName
	 * @param toName
	 */
	public void rename(String fromName, String toName) {
		if (this.name.equals(fromName))
			this.name = toName;

		for (Renderable r : renderables)
			r.rename(fromName, toName);

		rootElement.rename(fromName, toName);
	}

	/**
	 * Schreibt den Inhalt der Methode in ein XML-Document. Das XML-Document
	 * muss von ausserhalb bereits vorbereitet worden sein, mit dem
	 * Startelement.
	 * 
	 * @param writer
	 *            Der writer, in dem das XML aufgebaut wird.
	 * @throws XMLStreamException
	 */
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("METHOD");
		writer.writeAttribute("NAME", name);
		writer.writeAttribute("TYPE", rType.toString());
		writer.writeAttribute("OPENED", opened ? "T" : "F");

		// dibo 13.08.2010
		writer.writeAttribute("SELECTED", selected ? "T" : "F");

		// Root Element schreiben
		writer.writeStartElement("ROOTELEMENT");
		Renderable root = rootElement.next;
		if (root != null)
			root.toXML(writer);
		writer.writeEndElement();

		// Renderables schreiben
		for (Renderable r : renderables) {
			writer.writeStartElement("RENDERSTACK");
			writer.writeAttribute("TOP", r.getY() + "");
			writer.writeAttribute("LEFT", r.getX() + "");
			r.toXML(writer);
			writer.writeEndElement();
		}

		writer.writeEndElement();
	}

	/**
	 * Lädt die Methode anhand der übergebenen NodeList im DOM-Baum. Dafür
	 * müssen alle Kinderknoten des Elements <tt>METHOD</tt> übergeben werden
	 * 
	 * @param childNodes
	 */
	public void loadProgram(NodeList childNodes) {
		// RootElement laden
		NodeList rootNodes = getRootNodeList(childNodes);
		if (rootNodes != null && rootNodes.getLength() > 0) {
			Element rootElem = (Element) rootNodes.item(0);
			Renderable rootRenderable = ScratchUtils.getRenderableByName(
					rootElem.getAttribute("NAME"), rootElem
							.getAttribute("TYPE"));
			rootElement.addAsNext(rootRenderable);
			rootRenderable.loadProgram(rootElem.getChildNodes());
			rootRenderable.updateBounds();
			rootRenderable.updateChilds();
		}

		// RenderStacks laden
		for (int i = 0; i < childNodes.getLength(); i++) {
			Element child = (Element) childNodes.item(i);
			if (!child.getLocalName().equals("RENDERSTACK"))
				continue;

			loadRenderstack(child);
		}
	}

	/**
	 * Findet in den übergebenen Kinbderelementen eines DOM-Baums, das Element
	 * <tt>ROOTELEMENT</tt> und gibt dessen Kinderelemente zurück.
	 * 
	 * @param childNodes
	 *            Kinderelemente von <tt>METHOD</tt>
	 * @return Kinderelemente von <tt>ROOTELEMENT</tt>
	 */
	private NodeList getRootNodeList(NodeList childNodes) {
		for (int i = 0; i < childNodes.getLength(); i++)
			if (childNodes.item(i).getLocalName().equals("ROOTELEMENT"))
				return childNodes.item(i).getChildNodes();

		return null;
	}

	/**
	 * Lädt alle Elemente des Renderstacks
	 * 
	 * @param child
	 *            ein <tt>RENDERSTACK</tt> aus <tt>METHOD</tt>
	 */
	private void loadRenderstack(Element child) {
		int posX = new Integer(child.getAttribute("LEFT"));
		int posY = new Integer(child.getAttribute("TOP"));
		Element childNodes = (Element) child.getChildNodes().item(0);
		Renderable childRenderable = ScratchUtils.getRenderableByName(
				childNodes.getAttribute("NAME"), childNodes
						.getAttribute("TYPE"));
		childRenderable.loadProgram(childNodes.getChildNodes());
		childRenderable.updateBounds();
		childRenderable.getRootElement().updateChilds();

		childRenderable.moveTo(posX, posY);
		renderables.add(childRenderable);
	}

	/**
	 * Schreibt den Inhalt dieser Methode in den übergebenen Buffer
	 * als Programmcode in Hamster-Simulator Convention.
	 * @param buffer
	 */
	public void writeSourceCode(StringBuffer buffer) {
		// Schreibe den Methodenkopf
		String returnS = (rType == RType.BOOLEAN) ? "boolean" : "void";
		buffer.append(returnS + " " + name + "() {" + Renderable.NEWLINE);
		
		// Schreibe den Methodenrumpf
		try {
			rootElement.writeSourceCode(buffer, 1, false, (rType == RType.BOOLEAN));
		} catch (FunctionResultException e) {}
		
		// dibo 19.01.2011
		/*
		// Schreibe Kommentierten Code
		for (Renderable r : renderables) {
			buffer.append(Renderable.NEWLINE + "\t// Nicht ausfuehrbarer Code" + Renderable.NEWLINE);
			try {
				r.writeSourceCode(buffer, 1, true, false);
				
				// Booleans enden nicht mit einer NEWLINE
				if (r.getType() == RType.BOOLEAN)
					buffer.append(Renderable.NEWLINE);
			} catch (FunctionResultException e) {}
		}
		*/
		
		// Schliesse den Methodenrumpf
		buffer.append("}" + Renderable.NEWLINE + Renderable.NEWLINE);
	}
}
