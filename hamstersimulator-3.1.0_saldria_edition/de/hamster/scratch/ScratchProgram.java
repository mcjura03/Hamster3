package de.hamster.scratch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.scratch.elements.voids.FunctionResultException;
import de.hamster.scratch.gui.NextMethodHandler;
import de.hamster.scratch.gui.RefreshHandler;
import de.hamster.workbench.Workbench;

/**
 * Ein ScratchProgramm beinhaltet eine Sequenz von Anweisungen (hier Statements)
 * 
 * @author dibo
 * 
 */
public class ScratchProgram extends Thread {
	private static Renderable active;

	private boolean setStopped;
	private boolean setPaused;
	private ScratchHamsterFile file;
	private StorageController program;
	private ArrayList<RefreshHandler> refreshHandler = new ArrayList<RefreshHandler>();
	private ArrayList<NextMethodHandler> nextMethodHandler = new ArrayList<NextMethodHandler>();
	private String openedMethod = "";
	private Stack<String> methodStack = new Stack<String>();

	public ScratchProgram() {
		super();
		this.setStopped = false;
		this.setPaused = false;
		this.program = new StorageController();
	}

	public ScratchProgram(ScratchProgram p) {
		super(p);
		this.setStopped = false;
		this.setPaused = false;
		this.setProgram(p.getProgram());
	}

	public void setFile(ScratchHamsterFile f) {
		this.file = f;
	}
	
	/**
	 * Liefert den Quellcode für das gesamte Programm, in Convention zum
	 * Hamster-Simulator, so dass dieser direkt als imperatives Programm
	 * verwendet werden darf.
	 * 
	 * @return
	 */
	public String getSourceCode() {
		StringBuffer buffer = new StringBuffer();
		this.program.writeSourceCode(buffer);
		return buffer.toString();
	}

	public void setProgram(StorageController program) {
		this.program = program;
	}

	public StorageController getProgram() {
		return this.program;
	}

	public static void setActive(Renderable r) {
		ScratchProgram.active = r;
	}

	public static boolean isActive(Renderable r) {
		return ScratchProgram.active == r;
	}

	public ArrayList<RefreshHandler> getRefreshHandler() {
		return this.refreshHandler;
	}

	public void setRefreshHandler(ArrayList<RefreshHandler> refreshHandler) {
		this.refreshHandler = refreshHandler;
	}

	public void addRefreshHandler(RefreshHandler handler) {
		this.refreshHandler.add(handler);
	}

	public ArrayList<NextMethodHandler> getNextMethodHandler() {
		return this.nextMethodHandler;
	}

	public void setNextMethodHandler(
			ArrayList<NextMethodHandler> nextMethodHandler) {
		this.nextMethodHandler = nextMethodHandler;
	}

	public void addNextMethodHandler(NextMethodHandler handler) {
		this.nextMethodHandler.add(handler);
	}

	public void nexMethod(String name) {
		this.methodStack.push(name);
	}

	public void endMmethod() {
		this.methodStack.pop();
	}

	private void refresh() {
		for (RefreshHandler handler : this.refreshHandler) {
			handler.refresh();
		}
	}

	private void onMethodChanged() {
		for (NextMethodHandler handler : this.nextMethodHandler) {
			handler.nextMethod(this.methodStack.peek());
		}
	}

	@Override
	public void run() {
		this.methodStack = new Stack<String>();
		this.methodStack.push("main");
		this.openedMethod = "";
		Renderable rootElement = this.program.getMainRoot();
		try {
			rootElement.perform(this);
		} catch (FunctionResultException e) {
			// Main Funktion wurde mit return verlassen,
			// es soll nichts weiter unternommen werden
		} catch (final Throwable exc) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, exc.toString(),
							"Scratch-Exception", JOptionPane.ERROR_MESSAGE,
							null);
				}
			});
		} finally {
			ScratchProgram.active = null;
			this.refresh();
			this.setStopped = false;
			this.setPaused = false;
			ScratchHamster.getScratchHamster().setProgramFinished();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Workbench.getWorkbench().getDebuggerController()
							.getDebuggerModel().setState(
									DebuggerModel.NOT_RUNNING);
					Workbench.getWorkbench().getEditor().getTabbedTextArea()
							.propertyChange(ScratchProgram.this.file, false);
				}
			});
		}
	}

	public synchronized void stopProgram() {
		this.notify(); // für den Fall, dass das Programm gerade pausiert ist
		this.setStopped = true;
	}

	public synchronized void pauseProgram() {
		this.setPaused = true;
	}

	public synchronized void resumeProgram() {
		this.setPaused = false;
		ScratchProgram.active = null;
		this.refresh();
		this.notify();
	}

	public synchronized void stepOver() {
		this.stepInto();
	}

	public synchronized void stepInto() {	
		this.notify();
		this.setPaused = true;
	}

	public synchronized boolean checkStop() {
		return this.setStopped;
	}

	public synchronized void checkPause() {
		if (this.setPaused) {
			if (!this.openedMethod.equals(this.methodStack.peek())) {
				this.openedMethod = this.methodStack.peek();
				this.onMethodChanged();
			}
			this.refresh();
			try {
				this.wait();
			} catch (InterruptedException exc) {

			}
		}
	}

	public void loadProgram(File xmlFile) {
		this.setStopped = false;
		this.setPaused = false;
		this.program = new StorageController();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			NodeList methods = doc.getElementsByTagName("METHOD");
			this.program.loadProgram(methods);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
