package de.hamster.javascript.view;

/**
 * Diese Klasse stellt eine JFrame als Konsole für JavaScript-Code dar.
 */
public class JavaScriptKonsole extends de.hamster.console.Console {

	// Es gibt nur eine Instanz der Konsole
	private static JavaScriptKonsole konsole = null;

	/*
	 * nicht-öffentlicher Konstruktor
	 */
	private JavaScriptKonsole() {

		// super("JavaScript - " + Utils.getResource("python.view.konsole"));
		// setSize(600, 400);
		// setLocationRelativeTo(Workbench.getWorkbench().getView()
		// .getSimulationFrame());
		// addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent e) {
		// // workbench.close(simulation);
		// setVisible(false);
		// Workbench.winPyKon.setState(false);
		// }
		// });

	}

	public static JavaScriptKonsole getJavaScriptKonsole() {
		if (konsole == null) {
			konsole = new JavaScriptKonsole();
		}
		return konsole;
	}

	public void init() {

	}

}
