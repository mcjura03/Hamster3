package de.hamster.python.model;

import java.util.Properties;

import javax.swing.JOptionPane;

import org.python.util.PythonInterpreter;

import de.hamster.interpreter.Hamster;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

public class PythonHamster extends Hamster {

	static PythonHamster hamster = new PythonHamster();

	public static PythonInterpreter interpreter;

	public static boolean initPython() {
		try {
			interpreter = new PythonInterpreter();
//			 Properties props = new Properties();
//			 props.setProperty("python.home", "C:\\Programme\\jython2.5.1");
//			 PythonInterpreter.initialize(System.getProperties(), props,
//			 new String[] { "" });
//			 interpreter.exec("import random");

			if (Utils.runlocally) {
				interpreter.setIn(Workbench.getWorkbench().getView()
						.getConsole().getIn());
				interpreter.setOut(Workbench.getWorkbench().getView()
						.getConsole().getOut());
				interpreter.setErr(Workbench.getWorkbench().getView()
						.getConsole().getErr());
			}
			String command = "from de.hamster.python.model import PythonHamster;";
			command = command + "Hamster = PythonHamster;";
			command = command
					+ "intern_shuwjndkdwlhdh = Hamster.getStandardHamsterIntern();";
			command = command + "vor = intern_shuwjndkdwlhdh.vor;";
			command = command + "linksUm = intern_shuwjndkdwlhdh.linksUm;";
			command = command + "gib = intern_shuwjndkdwlhdh.gib;";
			command = command + "nimm = intern_shuwjndkdwlhdh.nimm;";
			command = command + "vornFrei = intern_shuwjndkdwlhdh.vornFrei;";
			command = command + "kornDa = intern_shuwjndkdwlhdh.kornDa;";
			command = command + "maulLeer = intern_shuwjndkdwlhdh.maulLeer;";
			command = command + "\n";
			interpreter.exec(command);
		} catch (Throwable exc) {
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	private PythonHamster() {
		super(true);
	}

	public PythonHamster(int reihe, int spalte, int blickrichtung,
			int anzahlKoerner) {
		super(reihe, spalte, blickrichtung, anzahlKoerner);
	}

	public PythonHamster(Hamster hamster) {
		super(hamster);
	}

	/**
	 * Mit dieser Funktion kann Python-Code geladen und gestartet werden werden
	 * 
	 * @param string
	 *            Python-Code
	 */
	public static void load(String string, boolean start) {
		try {
			Hamster._re_init();
			initPython();
			interpreter.exec(string);
		} catch (Throwable e) {
			if (!de.hamster.debugger.model.DebuggerModel.isStop) {
				JOptionPane.showMessageDialog(null, e.toString(),
						"Python-Exception", JOptionPane.ERROR_MESSAGE, null);
			}
			de.hamster.debugger.model.DebuggerModel.isStop = false;
		}
	}

}
