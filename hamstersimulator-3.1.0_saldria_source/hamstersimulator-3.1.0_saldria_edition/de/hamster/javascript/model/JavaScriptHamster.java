package de.hamster.javascript.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JOptionPane;

import de.hamster.interpreter.Hamster;

public class JavaScriptHamster extends Hamster {

	static JavaScriptHamster hamster = new JavaScriptHamster();
	static ScriptEngine engine = null;

	public static boolean initJavaScript() {
		// if (engine != null) {
		// return true;
		// }
		try {
			engine = new ScriptEngineManager().getEngineByName("JavaScript");
		} catch (Exception exc) {
			return false;
		}
		return true;
	}

	private JavaScriptHamster() {
		super(true);
	}

	public JavaScriptHamster(int reihe, int spalte, int blickrichtung,
			int anzahlKoerner) {
		super(reihe, spalte, blickrichtung, anzahlKoerner);
	}

	public JavaScriptHamster(Hamster hamster) {
		super(hamster);
	}

	/**
	 * Mit dieser Funktion kann JavaScript-Code geladen und gestartet werden
	 * werden
	 * 
	 * @param string
	 *            JavaScript-Code
	 */
	public static void load(String string, boolean start) {
		try {
			Hamster._re_init();
			initJavaScript();
			String command = "";
			command += "load(\"nashorn:mozilla_compat.js\");\n";
			command += "importPackage(Packages.de.hamster.interpreter);\n";
			command += "function vor() {\n";
			command += "Hamster.getStandardHamsterIntern().vor();\n";
			command += "}\n";
			command += "function linksUm() {\n";
			command += "Hamster.getStandardHamsterIntern().linksUm();\n";
			command += "}\n";
			command += "function gib() {\n";
			command += "Hamster.getStandardHamsterIntern().gib();\n";
			command += "}\n";
			command += "function nimm() {\n";
			command += "Hamster.getStandardHamsterIntern().nimm();\n";
			command += "}\n";
			command += "function vornFrei() {\n";
			command += "return Hamster.getStandardHamsterIntern().vornFrei();\n";
			command += "}\n";
			command += "function maulLeer() {\n";
			command += "return Hamster.getStandardHamsterIntern().maulLeer();\n";
			command += "}\n";
			command += "function kornDa() {\n";
			command += "return Hamster.getStandardHamsterIntern().kornDa();\n";
			command += "}\n";
			command += buildJSHamsterClass();
			command += string;
			engine.eval(command);
		} catch (Throwable e) {
			if (!de.hamster.debugger.model.DebuggerModel.isStop) {
				JOptionPane
						.showMessageDialog(null, e.toString(),
								"JavaScript-Exception",
								JOptionPane.ERROR_MESSAGE, null);
			}
			de.hamster.debugger.model.DebuggerModel.isStop = false;
		}
	}

	static String buildJSHamsterClass() {
		String str = "";
		str += "function JSHamster(r, s, b, k) {\n";
		str += "this.hamster = new Hamster(r, s, b, k);\n";
		str += "this.vor = function() {\n";
		str += "this.hamster.vor();\n";
		str += "}\n";
		str += "this.linksUm = function() {\n";
		str += "this.hamster.linksUm();\n";
		str += "}\n";
		str += "this.gib = function() {\n";
		str += "this.hamster.gib();\n";
		str += "}\n";
		str += "this.nimm = function() {\n";
		str += "this.hamster.nimm();\n";
		str += "}\n";
		str += "this.vornFrei = function() {\n";
		str += "return this.hamster.vornFrei();\n";
		str += "}\n";
		str += "this.kornDa = function() {\n";
		str += "return this.hamster.kornDa();\n";
		str += "}\n";
		str += "this.maulLeer = function() {\n";
		str += "return this.hamster.maulLeer();\n";
		str += "}\n";
		str += "this.schreib = function(nachricht) {\n";
		str += "this.hamster.schreib(nachricht);\n";
		str += "}\n";
		str += "this.liesZeichenkette = function(nachricht) {\n";
		str += "return this.hamster.liesZeichenkette(nachricht);\n";
		str += "}\n";
		str += "this.liesZahl = function(nachricht) {\n";
		str += "return this.hamster.liesZahl(nachricht);\n";
		str += "}\n";
		str += "this.getReihe = function() {\n";
		str += "return this.hamster.getReihe();\n";
		str += "}\n";
		str += "this.getSpalte = function() {\n";
		str += "return this.hamster.getSpalte();\n";
		str += "}\n";
		str += "this.getBlickrichtung = function() {\n";
		str += "return this.hamster.getBlickrichtung();\n";
		str += "}\n";
		str += "this.getAnzahlKoerner = function() {\n";
		str += "return this.hamster.getAnzahlKoerner();\n";
		str += "}\n";
		str += "}\n";
		return str;
	}

}
