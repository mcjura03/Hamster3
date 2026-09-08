package de.hamster.ruby.model;

import javax.swing.JOptionPane;

import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ScriptingContainer;

import de.hamster.interpreter.Hamster;

public class RubyHamster extends Hamster {

	static RubyHamster hamster = new RubyHamster();

	private static ScriptingContainer container;

	public static boolean initRuby() {
		try {
			container = new ScriptingContainer();
			String command = "include Java\n";
			command += "require \"hamstersimulator.jar\"\n";
			command += "include_class \"de.hamster.ruby.model.RubyHamster\"\n";
			command += "class Hamster < RubyHamster\n";
			command += "end\n";
			command += "def vor\n";
			command += "Hamster.getStandardHamsterIntern().vor()\n";
			command += "end\n";
			command += "def linksUm\n";
			command += "Hamster.getStandardHamsterIntern().linksUm()\n";
			command += "end\n";
			command += "def nimm\n";
			command += "Hamster.getStandardHamsterIntern().nimm()\n";
			command += "end\n";
			command += "def gib\n";
			command += "Hamster.getStandardHamsterIntern().gib()\n";
			command += "end\n";
			command += "def vornFrei\n";
			command += "return Hamster.getStandardHamsterIntern().vornFrei()\n";
			command += "end\n";
			command += "def kornDa\n";
			command += "return Hamster.getStandardHamsterIntern().kornDa()\n";
			command += "end\n";
			command += "def maulLeer\n";
			command += "return Hamster.getStandardHamsterIntern().maulLeer()\n";
			command += "end\n";
			command += "def Hamster.NORD\n";
			command += "return 0\n";
			command += "end\n";
			command += "def Hamster.OST\n";
			command += "return 1\n";
			command += "end\n";
			command += "def Hamster.SUED\n";
			command += "return 2\n";
			command += "end\n";
			command += "def Hamster.WEST\n";
			command += "return 3\n";
			command += "end\n";
			container.runScriptlet(command);
		} catch (Throwable exc) {
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	private RubyHamster() {
		super(true);
	}

	public RubyHamster(int reihe, int spalte, int blickrichtung,
			int anzahlKoerner) {
		super(reihe, spalte, blickrichtung, anzahlKoerner);
	}

	public RubyHamster(Hamster hamster) {
		super(hamster);
	}

	/**
	 * Mit dieser Funktion kann Ruby-Code geladen und gestartet werden werden
	 * 
	 * @param string
	 *            Ruby-Code
	 */
	public static void load(String string, boolean start) {
		try {
			Hamster._re_init();
			initRuby();
			container.runScriptlet(string);
		} catch (ThreadDeath td) {
		} catch (EvalFailedException e) {
			if (!de.hamster.debugger.model.DebuggerModel.isStop) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Ruby-Exception", JOptionPane.ERROR_MESSAGE, null);
			}
			de.hamster.debugger.model.DebuggerModel.isStop = false;

		} catch (Throwable e) {
			System.out.println(e.getClass().getName());
			if (!de.hamster.debugger.model.DebuggerModel.isStop) {
				JOptionPane.showMessageDialog(null, e.toString(),
						"Ruby-Exception", JOptionPane.ERROR_MESSAGE, null);
			}
			de.hamster.debugger.model.DebuggerModel.isStop = false;
		} 
	}

}
