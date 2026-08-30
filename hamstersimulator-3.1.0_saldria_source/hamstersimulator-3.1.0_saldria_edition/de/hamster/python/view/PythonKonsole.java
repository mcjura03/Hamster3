package de.hamster.python.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.python.util.InteractiveConsole;

import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse stellt eine JFrame als Konsole für Python-Code dar.
 */
public class PythonKonsole extends de.hamster.console.Console {

	// Es gibt nur eine Instanz der Konsole
	private static PythonKonsole konsole = null;

	InteractiveConsole interactiveConsole = null;

	/*
	 * nicht-öffentlicher Konstruktor
	 */
	private PythonKonsole() {

		super("Python - " + Utils.getResource("python.view.konsole"));
		setSize(600, 400);
		setLocationRelativeTo(Workbench.getWorkbench().getView()
				.getSimulationFrame());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// workbench.close(simulation);
				setVisible(false);
				Workbench.winPyKon.setState(false);
			}
		});

	}

	public static PythonKonsole getPythonKonsole() {
		if (konsole == null) {
			konsole = new PythonKonsole();
		}
		return konsole;
	}

	public void init() {
		if (interactiveConsole != null) {
			return; // init only once
		}
		interactiveConsole = new InteractiveConsole();
		interactiveConsole.setIn(this.getIn());
		interactiveConsole.setOut(this.getOut());
		interactiveConsole.setErr(this.getErr());

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
		interactiveConsole.exec(command);

		new Thread(new Runnable() {
			public void run() {
				interactiveConsole.interact();
			}
		}).start();
	}

}
