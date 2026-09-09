package de.hamster.flowchart.controller;

import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.flowchart.controller.handler.UpdateHandler;
import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.DecisionObject;
import de.hamster.flowchart.model.FlowchartMethod;
import de.hamster.flowchart.model.FlowchartObject;
import de.hamster.flowchart.model.FlowchartTransition;
import de.hamster.flowchart.model.ProcedureObject;
import de.hamster.flowchart.model.StartStopObject;
import de.hamster.flowchart.model.command.GibCommandObject;
import de.hamster.flowchart.model.command.LinksUmCommandObject;
import de.hamster.flowchart.model.command.NimmCommandObject;
import de.hamster.flowchart.model.command.VorCommandObject;
import de.hamster.flowchart.model.decision.KornDaDecisionObject;
import de.hamster.flowchart.model.decision.MaulLeerDecisionObject;
import de.hamster.flowchart.model.decision.VornFreiDecisionObject;
import de.hamster.flowchart.view.FlowchartDrawPanel;
import de.hamster.flowchart.view.FlowchartTextBox;
import de.hamster.interpreter.HamsterException;
import de.hamster.workbench.Workbench;

/**
 * eine interne Repräsentation eines Flowchart-Programmes
 * 
 * @author Gerrit Apeler
 * 
 */
public class FlowchartProgram extends Thread {

	FlowchartHamsterFile file;
	protected boolean isPaused;
	protected boolean isStopped;
	FlowchartObject runningElement;
	protected UpdateHandler updateHandler;
	private FlowchartController controller;
	private FlowchartObject prevRunningElement;

	/**
	 * Copy-Konstruktor: erzeugt eine wertegleiche Kopie des übergebenen
	 * Flowchart-Programms
	 * 
	 * @param p
	 *            ein existierendes Flowchart-Programm
	 * @param file
	 *            das zugeordnete HamsterFile
	 */
	public FlowchartProgram(FlowchartProgram p, FlowchartHamsterFile file) {
		super(p);
		this.file = file;

		this.isPaused = p.isPaused;
		this.isStopped = p.isStopped;

		this.setController(p.getController());
		this.runningElement = p.runningElement;
		this.updateHandler = p.updateHandler;

	}

	/**
	 * Copy-Konstruktor: erzeugt eine wertegleiche Kopie des übergebenen
	 * Flowchart-Programms
	 * 
	 * @param p
	 *            ein existierendes Flowchart-Programm
	 * @param file
	 *            das zugeordnete HamsterFile
	 */
	public FlowchartProgram(FlowchartProgram p) {
		this(p, p.file);
		this.setController(p.getController());
	}

	/**
	 * Default Konstruktor - erstellt ein leeres Flowchart Programm
	 * 
	 * @param flowchartHamsterFile
	 * @param flowchartController
	 */
	public FlowchartProgram(FlowchartHamsterFile flowchartHamsterFile) {
		super();

		this.isPaused = false;
		this.isStopped = false;

		this.file = flowchartHamsterFile;
		// einen neuen Controller inititalisieren
		// true bedeutet, dass die main-Methode inititalisiert wird
		this.controller = new FlowchartController(true, this.file);
	}

	/**
	 * Ausführung des Programms
	 */
	@Override
	public void run() {

		FlowchartHamster hamster = FlowchartHamster.getFlowchartHamster();
		Stack<FlowchartObject> recursionStack = new Stack<FlowchartObject>();

		// setzte doHightlight bei allen Elementen auf false
		for (FlowchartObject o : this.getController().getElements()) {
			o.doHighlight(false);
		}

		// set start to runningElement
		recursionStack.push(this.controller.getStart());
		try {
			this.runningElement = recursionStack.pop();
			while (!recursionStack.isEmpty()
					|| (this.runningElement != null && !this.runningElement.terminate)) {

				// dibo
				if (this.prevRunningElement != null) {
					this.prevRunningElement.doHighlight(false);
				}
				if (this.runningElement != null) {
					this.runningElement.doHighlight(true);
				}

				// gehe alle Methoden durch
				for (final FlowchartMethod m : this.getController()
						.getMethods()) {
					// gehe alle Componetnen im JTabbedPane durch
					for (Component comp : this.getController()
							.getFlowchartTabbedPane().getComponents()) {
						JViewport viewport = ((JScrollPane) comp).getViewport();
						// hole das FlowchartDrawPanel aus dem JScrollPane
						FlowchartDrawPanel draw = (FlowchartDrawPanel) viewport
								.getView();
						// gehe alle FlowchartObjekte in der aktuellen
						// Methode
						// durch
						for (FlowchartObject o : m.getElemList()) {
							// wenn das ausführende Element gefunden
							// und der Methoden-Name mit dem Component
							// übereinstimmt ...
							if (o.equals(this.runningElement)
									&& draw.getName().equals(m.name)) {
								// ... dann aktiviere den Tab
								this.getController().getFlowchartTabbedPane()
										.setSelectedComponent(comp);
							}
						}
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							m.getDrawPanel().repaint();
						}
					});
				}
				// repaint, damit die Elemente neu gezeichnet werden
				// damit man sehen kann, dass sie aktiv sind

				for (final FlowchartMethod m : this.getController()
						.getMethods()) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							m.getDrawPanel().repaint();
						}
					});
				}

				Object currentExec;

				// Ausführen des Elements
				if (this.runningElement != null) {
					currentExec = this.runningElement.execute(this);
				} else {
					throw new Exception("no_element");
				}
				// find the next elemente
				if (currentExec instanceof Boolean) {
					if ((Boolean) currentExec) {
						if (this.runningElement instanceof StartStopObject
								&& this.runningElement.getText().equals("Stop")) {
							// do nothing
						} else if (this.runningElement.getTrueChild() != null) {
							recursionStack.push(this.runningElement
									.getTrueChild());
						} else {
							throw new Exception("no_element");
						}
					} else {
						recursionStack
								.push(((DecisionObject) this.runningElement)
										.getFalseChild());
					}
				} else if (currentExec instanceof String) {

					// step into procedure
					// only procedure returns String
					recursionStack.push(this.runningElement.getTrueChild());

					// find the procedure
					search_proc: {
						for (FlowchartMethod method : this.controller
								.getMethods()) {
							// when procedure found ...
							if (method.getName().equals(currentExec)) {
								// find startObject in that procedure
								for (FlowchartObject o : (method.getElemList())) {
									// if found then set to runningElement
									if (o.getText().equals("Start")) {
										recursionStack.push(o);
										break search_proc;
									}
								}
							}
						}
						throw new Exception("no_start_proc");
					}

				} else {
					// this should never happen
				}

				// holt sich das nächste Elemente
				if (!recursionStack.isEmpty()) {
					this.prevRunningElement = this.runningElement;
					this.runningElement = recursionStack.pop();
				} else {
					break;
				}

				final FlowchartController tmpController = this.controller;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						tmpController.getFlowchartTabbedPane().repaint();
					}
				});
				Thread.yield();
				Thread.sleep(1);
			}
			if (this.prevRunningElement != null) {
				this.prevRunningElement.doHighlight(false);
			}

			if (this.runningElement != null) {
				this.runningElement.doHighlight(true);
				final FlowchartController tmpController = this.controller;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						tmpController.getFlowchartTabbedPane().repaint(); // dibo
					}
				});
				this.runningElement.sleep(); // dibo
			}

		} catch (Exception exc) {
			if (recursionStack.isEmpty() && runningElement == null) {
				JOptionPane.showMessageDialog(null,
						"Es gibt kein Element mehr zum Ausf�hren.\n"
								+ "Ein Stop-Element wurde nicht erreicht.",
						"Ausf�hrung", JOptionPane.ERROR_MESSAGE);
			} else if (exc.getMessage() != null
					&& exc.getMessage().equals("no_element")) {
				JOptionPane
						.showMessageDialog(
								null,
								"Es gibt kein n�chstes Element zum Ausf�hren.\n"
										+ "Bitte �berpr�fen, ob das Stop-Element vorhanden/verbunden ist.",
								"Ausf�hrung", JOptionPane.ERROR_MESSAGE);
			} else if (exc.getMessage() != null
					&& exc.getMessage().equals("no_start_proc")) {
				JOptionPane.showMessageDialog(null,
						"Das Start-Element im Unterprogramm fehlt.",
						"Ausf�hrung", JOptionPane.ERROR_MESSAGE);
			} else if (exc instanceof HamsterException) {
				JOptionPane.showMessageDialog(null, exc.toString(), "Hamster",
						JOptionPane.ERROR_MESSAGE);
			}
			// exc.printStackTrace(); dibo
		} finally {
			// dibo
			if (this.prevRunningElement != null) {
				this.prevRunningElement.doHighlight(false);
			}
			if (this.runningElement != null) {
				this.runningElement.doHighlight(false);
			}
			final FlowchartController tmpController = this.controller;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tmpController.getFlowchartTabbedPane().repaint(); // dibo
				}
			});
			this.isPaused = false;
			this.isStopped = false;
			this.finish(hamster);
		}

	}

	/**
	 * Diese Methode versucht das Programm zu stoppen.
	 */
	public synchronized void tryPause() {
		if (this.isPaused) {
			try {
				this.wait();
			} catch (InterruptedException exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * Diese Methode gibt zurück, ob das Programm gestoppt ist, oder gerade
	 * läuft.
	 * 
	 * @return true wenn das Programm gestoppt ist, false sonst
	 */
	public synchronized boolean isStopped() {
		return this.isStopped;
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Stopp-Button gedrückt hat.
	 */
	public synchronized void stopProgram() {
		if (this.runningElement != null) {
			this.runningElement.doHighlight(false);
		}
		if (this.prevRunningElement != null)
			this.prevRunningElement.doHighlight(false);
		this.notify();
		this.isStopped = true;
		for (FlowchartMethod m : this.getController().getMethods()) {
			m.getDrawPanel().repaint();
		}
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Pause-Button gedrückt hat.
	 */
	public synchronized void pauseProgram() {
		this.isPaused = true;
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Resume-Button gedrückt hat.
	 */
	public synchronized void resumeProgram() {
		for (FlowchartObject o : this.getController().getElements()) {
			o.doHighlight(false);
		}
		this.isPaused = false;
		this.notify();
	}

	/**
	 * Wird aufgerufen, wenn der Nutzer den Step-Button gedrückt hat.
	 */
	public synchronized void stepInto() {
		if (this.prevRunningElement != null) {
			this.prevRunningElement.doHighlight(false);
		}
		this.runningElement.doHighlight(true);
		this.prevRunningElement = this.runningElement;
		this.notify();
		this.isPaused = true;
		for (FlowchartMethod m : this.getController().getMethods()) {
			m.getDrawPanel().repaint();
		}
	}

	/**
	 * muss in allen Fällen aufgerufen werden, in denen das Programm beendet
	 * wird
	 * 
	 * @param hamster
	 */
	private void finish(FlowchartHamster hamster) {
		hamster.setProgramFinished();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Workbench.getWorkbench().getDebuggerController()
						.getDebuggerModel().setState(DebuggerModel.NOT_RUNNING);
				Workbench.getWorkbench().getEditor().getTabbedTextArea()
						.propertyChange(FlowchartProgram.this.file, false);
			}
		});
	}

	/**
	 * Konvertiert das Flowchart-Programm in ein äquivalentes imperatives
	 * Java-Hamster-Programm und liefert hierzu den Quellcode
	 * 
	 * @return Quellcode des äquivalenten imperativen Java-Hamster-Programms
	 */
	public String getSourceCode() {

		String code = "";
		for (FlowchartMethod method : this.controller.getMethods()) {
			code += "void " + method.name + "() {\n";
			code += "\tint element = " + method.getStart() + ";\n";
			code += "\twhile (element>-1) {\n";
			code += "\t\tswitch (element) {\n";
			for (FlowchartObject object : method.getElemList()) {
				code += "\t\t\tcase " + object.getId() + " : \n";
				if (!(object instanceof DecisionObject)) {

					if (!(object.getPerform().equals("")))
						code += "\t\t\t\t" + object.getPerform() + "();\n";
					code += "\t\t\t\telement = " + object.getTrueChildId()
							+ ";\n";
				} else {
					code += "\t\t\t\tif (" + object.getPerform() + "()) {\n";
					code += "\t\t\t\t\telement = " + object.getTrueChildId()
							+ ";\n";
					code += "\t\t\t\t} else {\n";
					code += "\t\t\t\t\telement = "
							+ ((DecisionObject) object).getFalseChildId()
							+ ";\n";
					code += "\t\t\t\t}\n";
				}
				code += "\t\t\t\tbreak;\n";
			}
			code += "\t\t}\n\t}\n}\n";
		}
		return code;

	}

	/**
	 * Erstellt aus der übergebenen Datei ein Flowchart-Programm
	 * 
	 * @param xmlFile
	 *            eine XML-Datei mit einem abgespeicherten Flowchart-Programm
	 */
	public void loadProgram(File xmlFile) {

		this.isPaused = false;
		this.isStopped = false;

		this.controller = new FlowchartController(false, this.file);

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);

		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);

			FlowchartMethod tmpMethod;
			// parse flowchart elements

			NodeList methodNodeList = doc.getElementsByTagName("method");

			NodeList transitionNodeList = doc
					.getElementsByTagName("transition");

			for (int m = 0; m < methodNodeList.getLength(); m++) {
				tmpMethod = new FlowchartMethod(this.file,
						((Element) methodNodeList.item(m)).getAttribute("name"));
				tmpMethod.setElementNodeList((Element) methodNodeList.item(m));
				tmpMethod.setStart(((Element) methodNodeList.item(m))
						.getAttribute("startId"));
				tmpMethod.getDrawPanel().setTmpTransitionList(
						this.getController().getTransitions());
				this.controller.addMethod(tmpMethod);
				if (!this.controller.PROCEDURES.contains(tmpMethod.getName()))
					this.controller.PROCEDURES.add(tmpMethod.getName());

				// tmpMethod.setDrawPanel(new FlowchartDrawPanel(tmpMethod,
				// true, this.file));

				NodeList commentNodeList = tmpMethod.getElementList()
						.getElementsByTagName("comment");

				for (int k = 0; k < commentNodeList.getLength(); k++) {
					Element commentElement = (Element) commentNodeList.item(k);
					String x = commentElement.getAttribute("x");
					String y = commentElement.getAttribute("y");
					String commentText = commentElement.getAttribute("text");
					String relId = commentElement.getAttribute("relId");

					CommentObject tmpCommentObject = new CommentObject(
							commentText);
					tmpCommentObject.setCoordinates(Integer.valueOf(x),
							Integer.valueOf(y));
					tmpCommentObject.setTextBox(new FlowchartTextBox(
							tmpCommentObject, tmpMethod, new Point(Integer
									.valueOf(x), Integer.valueOf(y)), tmpMethod
									.getDrawPanel(), this.file));
					tmpCommentObject.getTextBox().setVisible(false);
					if (relId.length() > 0) {

						tmpCommentObject.setRelatedObjectId(Integer
								.valueOf(relId));

					}
				}
			}

			for (FlowchartMethod methodDoc : this.controller.getMethods()) {
				NodeList elementNodeList = methodDoc.getElementList()
						.getElementsByTagName("element");

				String startId = methodDoc.getStart();

				for (int c = 0; c < elementNodeList.getLength(); c++) {
					Element commandElement = (Element) elementNodeList.item(c);
					String text = commandElement.getAttribute("string");
					String id = commandElement.getAttribute("id");
					String perform = commandElement.getAttribute("perform");
					String type = commandElement.getAttribute("type");

					int x = Integer.parseInt(commandElement.getAttribute("x"));
					int y = Integer.parseInt(commandElement.getAttribute("y"));
					Boolean doNegate = Boolean.parseBoolean(commandElement
							.getAttribute("not"));

					NodeList childList = commandElement
							.getElementsByTagName("nextId");

					FlowchartObject element = null;

					if (type.equals("command")) {
						if (perform.equals("vor")) {
							element = new VorCommandObject(text);
						} else if (perform.equals("linksUm")) {
							element = new LinksUmCommandObject(text);
						} else if (perform.equals("nimm")) {
							element = new NimmCommandObject(text);
						} else if (perform.equals("gib")) {
							element = new GibCommandObject(text);
						} else {
							// that element does not exist
							element = null;
						}
					} else if (type.equals("decision")) {
						if (perform.equals("vornFrei")) {
							element = new VornFreiDecisionObject(text, doNegate);
						} else if (perform.equals("kornDa")) {
							element = new KornDaDecisionObject(text, doNegate);
						} else if (perform.equals("maulLeer")) {
							element = new MaulLeerDecisionObject(text, doNegate);
						} else {
							// that element does not exist
							element = null;
						}
					} else if (type.equals("procedure")) {
						// search if procedure exists
						for (FlowchartMethod method : this.controller
								.getMethods()) {
							// if procedure found ...
							if (perform.equals(method.getName())) {
								// .. create new procedure with given parameters
								element = new ProcedureObject(text,
										method.getName());
							}
						}
					} else if (type.equals("startstop")) {
						element = new StartStopObject(text, null);
						if (methodDoc.name.equals("main")) {
							if (id.equals(startId)) {
								this.controller.setStart(element);
							}
							if (text.equals("Stop")) {
								this.controller.setStop(element);
								// stop in main is allways terminiating
								this.controller.getStop().setTerminate();
							}

						}
					} else {
						// das element gibts nicht
					}

					// die Objekt Parameter setzen
					element.setId(Integer.parseInt(id));
					element.setCoordinates(x, y);
					element.setPerform(perform);
					element.setType(type);

					// set trueChildId
					for (int r = 0; r < childList.getLength(); r++) {
						Element nextId = (Element) childList.item(r);
						if (Boolean.parseBoolean(nextId.getAttribute("return"))) {
							element.setTrueChildId(Integer.parseInt(nextId
									.getAttribute("value")));
						} else {
							((DecisionObject) element).setFalseChildId(Integer
									.parseInt(nextId.getAttribute("value")));
						}
					}

					// Element zum Controller hinzufügen
					this.controller.addElement(element);
					methodDoc.addElementToList(element);
				}

				// set childs
				// wenn i ein parent von j ist, dann ist j ein child von i
				for (FlowchartObject i : this.controller.getElements()) {
					for (FlowchartObject j : this.controller.getElements()) {
						if (j.getId() == i.getTrueChildId()) {
							i.setTrueChild(j);
						}
						if (i instanceof DecisionObject
								&& j.getId() == ((DecisionObject) i)
										.getFalseChildId()) {
							((DecisionObject) i).setFalseChild(j);
						}
					}
				}
			}

			for (FlowchartMethod m : this.getController().getMethods()) {
				for (CommentObject c : m.getCommentList()) {
					for (FlowchartObject f : m.getElemList()) {
						if (c.getRelId() == f.getId()) {
							c.setRelatedObject(f);
						}
					}
				}
			}

			// load transitions from xml

			for (int t = 0; t < transitionNodeList.getLength(); t++) {

				Element tran = ((Element) transitionNodeList.item(t));

				int sourceId = Integer.valueOf(tran.getAttribute("sourceId"));
				int sourceOrientation = Integer.valueOf(tran
						.getAttribute("orientationS"));

				String d = tran.getAttribute("destId");
				Integer destId = null;
				Integer destOrientation = null;
				Integer tmpDest_x = null;
				Integer tmpDest_y = null;
				Boolean childIs = Boolean.valueOf(tran.getAttribute("childIs"));
				if (tran.getAttribute("childIs").length() < 1) {
					childIs = true;
				}

				if (d.length() > 0) {
					destId = Integer.valueOf(d);
					destOrientation = Integer.valueOf(tran
							.getAttribute("orientationD"));
				} else {
					String tmpX = tran.getAttribute("tmpDest_x");
					if (tmpX != null) {
						tmpDest_x = new Integer(Integer.valueOf(tmpX));
					}
					String tmpY = tran.getAttribute("tmpDest_y");
					if (tmpY != null) {
						tmpDest_y = new Integer(Integer.valueOf(tmpY));
					}
				}

				String aX = String.valueOf(tran.getAttribute("nodeA_x"));
				String aY = String.valueOf(tran.getAttribute("nodeA_y"));
				String bX = String.valueOf(tran.getAttribute("nodeB_x"));
				String bY = String.valueOf(tran.getAttribute("nodeB_y"));
				String cX = String.valueOf(tran.getAttribute("nodeC_x"));
				String cY = String.valueOf(tran.getAttribute("nodeC_y"));

				// find the FlowchartElement
				for (FlowchartObject o : this.controller.getElements()) {
					if (o.getId() == sourceId) {
						FlowchartTransition tmpTransition = new FlowchartTransition(
								o, sourceOrientation);

						this.controller.addTransition(tmpTransition);

						if (d.length() > 0) {
							for (FlowchartObject p : this.controller
									.getElements()) {
								if (p.getId() == destId) {
									if (childIs) {
										tmpTransition.setDestinationObject(p,
												destOrientation, true);
									} else {
										tmpTransition.setDestinationObject(p,
												destOrientation, false);
									}
								}
							}

						} else {
							tmpTransition.updateTmpDestinationPoint(new Point(
									tmpDest_x, tmpDest_y));
						}

						if (aX.length() > 0) {
							Point aPoint = new Point(Integer.valueOf(aX),
									Integer.valueOf(aY));
							tmpTransition.createNodeA(aPoint, true);
						}

						if (bX.length() > 0) {
							Point bPoint = new Point(Integer.valueOf(bX),
									Integer.valueOf(bY));
							tmpTransition.createNodeB(bPoint, true);
						}

						if (cX.length() > 0) {
							Point cPoint = new Point(Integer.valueOf(cX),
									Integer.valueOf(cY));
							tmpTransition.createNodeC(cPoint, true);
						}
					}

				}

			}

			// magie
			for (FlowchartMethod m : this.getController().getMethods()) {
				m.getDrawPanel().setTmpTransitionList(
						this.getController().getTransitions());
			}

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * Schreibt die Programm-Informationen in den XML writer
	 * 
	 * @param writer
	 *            Der Writer, in den die Informationen geschrieben werden.
	 * @throws XMLStreamException
	 */
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		for (FlowchartMethod method : this.controller.getMethods()) {
			writer.writeCharacters("\t");
			writer.writeStartElement("method");
			writer.writeAttribute("name", method.name);

			// die startId existiert immer
			// (aber nur wenn ein StartElement vorhanden ist :( )
			if (method.getStart() != null)
				writer.writeAttribute("startId", method.getStart());
			writer.writeCharacters("\n");
			for (FlowchartObject flow : method.getElemList()) {
				flow.toXML(writer);
			}
			for (CommentObject comment : method.getCommentList()) {
				comment.toXML(writer);
			}
			writer.writeCharacters("\t");
			writer.writeEndElement();
			writer.writeCharacters("\n");
		}

		for (FlowchartTransition transtion : this.controller.getTransitions()) {
			transtion.toXML(writer);
		}
	}

	/**
	 * Gibt die Elemente zurück Diese Methode ist deprecated. Benutze den
	 * FlowchartController!!!
	 * 
	 * @return Liste mit Elementen
	 */
	@Deprecated
	public CopyOnWriteArrayList<FlowchartObject> getElements() {
		return this.controller.getElements();
	}

	/**
	 * Gibt die Methoden zurück Diese Methode ist deprecated. Benutze den
	 * FlowchartController!!!
	 * 
	 * @return Liste mit Methoden
	 */
	@Deprecated
	public CopyOnWriteArrayList<FlowchartMethod> getMethods() {
		return this.controller.getMethods();
	}

	/**
	 * Gibt die Methoden zurück Diese Methode ist deprecated. Benutze den
	 * FlowchartController!!!
	 * 
	 * @return Liste mit Transitionen
	 */
	@Deprecated
	public CopyOnWriteArrayList<FlowchartTransition> getTransitions() {
		return this.controller.getTransitions();
	}

	/**
	 * Setzt das Start Objekt Diese Methode ist deprecated. Benutze den
	 * FlowchartController!!!
	 * 
	 * @param o
	 *            das Start-Objekt
	 */
	@Deprecated
	public void setStart(FlowchartObject o) {
		this.controller.setStart(o);
	}

	/**
	 * Setzte das JTabbedPane, in dem die FlowchartDrawPanel entahlten sein
	 * werden.
	 * 
	 * @param drawPanel
	 *            das JTabbedPane, in dem die Panel enthalten sind
	 */
	@Deprecated
	public synchronized void setTabbedDrawPanel(JTabbedPane drawPanel) {
		this.controller.setFlowchartTabbedPane(drawPanel);
	}

	/**
	 * Gibt die JTabbedPane des Programms zurück Achtung! Deprecated! Benutze
	 * den FlowchartController!
	 * 
	 * @return die JTabbedPane (enthält die FlowchartDrawPanel)
	 */
	@Deprecated
	public synchronized JTabbedPane getDrawPanel() {
		return this.controller.getFlowchartTabbedPane();
	}

	/**
	 * Updated eine Methode, die verändert worden ist. Beim Threading müssen
	 * an bestimmten Stellen die Daten aktualisiert werden.
	 * 
	 * @param updatedMethod
	 *            Die Methode, die geupdated werden soll.
	 */
	@Deprecated
	public void updateMethod(FlowchartMethod updatedMethod) {
		FlowchartMethod tmpMethod = null;
		for (FlowchartMethod m : this.controller.getMethods()) {
			if (m.getName().equals(updatedMethod.getName())) {
				// tmpMethod = updatedMethod;
				// m = updatedMethod;
				this.controller.removeMethod(m);
				this.controller.addMethod(tmpMethod);
			}
		}
		// evtl. sogar komplett neue Methode hinzufügen
		if (tmpMethod == null) {
			this.controller.addMethod(tmpMethod);
		}
	}

	/**
	 * Gibt den Controller dieses Programms zurück
	 * 
	 * @return den Controller
	 */
	public synchronized FlowchartController getController() {
		return this.controller;
	}

	/**
	 * Setzt den FlowchartController neu. Kann genutzt werden um sicher zu
	 * stellen, dass der Controller auch aktuell ist.
	 * 
	 * @param currentController
	 *            - ist der aktuelle Controller
	 */
	public synchronized void setController(FlowchartController currentController) {
		this.controller = currentController;
	}

}
