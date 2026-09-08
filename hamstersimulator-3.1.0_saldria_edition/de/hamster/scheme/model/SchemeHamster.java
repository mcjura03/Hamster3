package de.hamster.scheme.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import jscheme.JScheme;
import jscheme.SchemePair;
import de.hamster.interpreter.Hamster;
import de.hamster.scheme.view.SchemeKonsole;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.model.Terrain;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse repräsentiert den Scheme-Hamster. Es wird die eine abgewandelte
 * Hamster-Klasse genutzt, die einzelne Befehle entgegennehmen kann. Dieser
 * Hamster kann mit Scheme-Programmen und einzelnen Scheme-Funktionen gesteuert
 * werden.
 * 
 * @author momo
 * 
 */
public class SchemeHamster extends Hamster { // dibo 18.02.2010

	// Instanz des Schemehamster.
	private static SchemeHamster ham = new SchemeHamster();

	// Arbeitsbereich des Simulators, nötig um andere Komponenten zu erreichen.
	private static Workbench workbench;

	// Synonyme um Befehle aus den Scheme-Programmen zu codieren
	final static char VOR = 'v';

	final static char LINKSUM = 'l';

	final static char NIMM = 'n';

	final static char GIB = 'g';

	final static char VORNFREI = 'f';

	final static char MAULLEER = 'm';

	final static char KORNDA = 'k';

	// Instanz des Scheme-Interpreters JScheme
	private static JScheme js;

	public static boolean initScheme() {
		js = new JScheme();
		// Hamster-Scheme-Befehle laden
		try {
			// String pfad = "HamsterBasics.scm";
			// js.load(new FileReader(pfad));

			String pfad = "de" + Utils.FSEP + "hamster" + Utils.FSEP + "scheme"
					+ Utils.FSEP + "model" + Utils.FSEP + "HamsterBasics.scm";
			pfad = "de/hamster/scheme/model/HamsterBasics.scm";
			InputStream in = ham.getClass().getClassLoader()
					.getResourceAsStream(pfad);
			InputStreamReader rin = new InputStreamReader(in);
			js.load(rin);

			return true;
		} catch (Exception e) {
			System.err
					.println("Datei HamsterBasics.scm nicht gefunden...:" + e);
			return false;
		}
	}

	/**
	 * Nicht-öffentlicher Konstruktor. Es gibt nur eine Instanz des
	 * Schemehamsters. *
	 */
	private SchemeHamster() {
		super(true);
	}

	/**
	 * Mit dieser Funktion kann Scheme-code in den Scheme-Interpreter geladen
	 * werden
	 * 
	 * @param string
	 *            Scheme-Code
	 */
	public static void load(String string, boolean start) {
		try {
			js.load(string);
			if (start) {
				go();
			}
		} catch (Exception e) {
			// new SchemeExceptionPanel(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Scheme-Exception", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	/**
	 * Startet den Scheme-Hamster indem er die start-funktion des
	 * SchemeProgramms aufruft
	 */
	public static void go() {
		// Log des Hamster-Simulators leeren

		getWorkbench().getSimulationController().getLogPanel().clearLog();

		try {
			// Start-Funktion im Scheme-Interpreter aufrufen
			initProgram();
			js.call("start", getTerritorium());
		} catch (Exception e) {
			if (!de.hamster.debugger.model.DebuggerModel.isStop) {
				// Fenster mit Fehlermeldung erzeugen
				// new SchemeExceptionPanel(e.getMessage());
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Scheme-Exception", JOptionPane.ERROR_MESSAGE, null);
			}
			de.hamster.debugger.model.DebuggerModel.isStop = false;
		}

	}

	private static void initProgram() {
		String s = "(define (read)(de.hamster.scheme.model.SchemeHamster.readP))";
		SchemeHamster.getJS().load(s);
		s = "(define (display text)(de.hamster.scheme.model.SchemeHamster.displayP text))";
		SchemeHamster.getJS().load(s);
		s = "(define (newline)(de.hamster.scheme.model.SchemeHamster.displayP \"\n\"))";
		SchemeHamster.getJS().load(s);
	}

	/**
	 * Diese Funktion wird in den Hamster-Standard-Befehlen aufgerufen und gibt
	 * die Befehle an den Hamster-Simulator weiter
	 * 
	 * @param befehl
	 *            Java-Hamster-Standard-Befehl
	 */
	public static void checkBefehl(String befehl) {

		SimulationModel simModel = workbench.getSimulation()
				.getSimulationModel();
		simModel.checkOnly(true);
		try {

			char befehlsCode = befehl.charAt(0);

			switch (befehlsCode) {
			case VOR:
				ham.vor();
				break;
			case LINKSUM:
				ham.linksUm();
				break;
			case NIMM:
				ham.nimm();
				break;
			case GIB:
				ham.gib();
				break;
			case VORNFREI:
				ham.vornFrei();
				break;
			case MAULLEER:
				ham.maulLeer();
				break;
			case KORNDA:
				ham.kornDa();
				break;
			}
		} finally {
			simModel.checkOnly(false);
		}

	}

	/**
	 * Diese Funktion bildet die Scheme-display_Funktion für den
	 * Hamster-Simulator.
	 * 
	 * @param obj
	 *            Anzuzeigender Text
	 */
	public static void display(Object obj) {
		// den anzuzeigenden Text der Konsole hinzufügen
		SchemeKonsole.addDisplayText(obj.toString());
	}

	public static void displayP(Object obj) {
		// den anzuzeigenden Text der Konsole hinzufügen
		System.out.print(obj.toString());
	}

	public static void exc(Object txt) {
		throw new RuntimeException("function " + txt + " not available");
	}

	/**
	 * Diese Funktion bildet die Scheme-read-Funktion für den Hamster-Simulator
	 * 
	 * @return Eingelesenes Objekt
	 */
	public static Object read() {
		String eingabe = JOptionPane.showInputDialog(null, "Eingabe:",
				"Scheme-Eingabe", JOptionPane.PLAIN_MESSAGE);

		try {
			return new Integer(eingabe);
		} catch (NumberFormatException e1) {
			try {
				return new Double(eingabe);
			} catch (NumberFormatException e2) {
				return eingabe;
			}
		}
	}

	// Einlesen eines double
	public static Object readP() {
		String eingabe = "";
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			eingabe = input.readLine();
			try {
				Integer string_to_int = new Integer(eingabe);
				return string_to_int.intValue();
			} catch (NumberFormatException e1) {
				try {
					Double string_to_double = new Double(eingabe);
					return string_to_double.doubleValue();
				} catch (NumberFormatException e2) {
					return eingabe;
				}
			}
		} catch (Exception e) {
			return eingabe;
		}
	}

	/**
	 * Liest das aktuelle Territorium aus dem Hamster-Simulator aus und erzeugt
	 * daraus die benötigte Datenstruktur für den Scheme-Hamster.
	 * 
	 * @return das Territorium als passende Liste zum Scheme-Hamster
	 */
	public static SchemePair getTerritorium() {

		// Das Territorium aus dem Simulator auslesen
		Terrain terr = workbench.getSimulation().getSimulationModel()
				.getTerrain();

		SchemePair zeile;
		SchemePair feld = JScheme.list();

		// Feld erzeugen
		for (int i = (terr.getHeight() - 1); i >= 0; i--) {
			zeile = JScheme.list();
			for (int y = (terr.getWidth() - 1); y >= 0; y--) {
				SchemePair tmp;
				if (terr.getWall(y, i)) {
					tmp = JScheme.list("Mauer");
				} else {
					tmp = JScheme.list("Kachel", terr.getCornCount(y, i));
				}
				zeile = JScheme.add(tmp, zeile);
			}
			feld = JScheme.add(zeile, feld);
		}

		// Hamster erzeugen
		// (Hamster, Koerner, X-Pos., Y-Pos., Richtung)
		de.hamster.simulation.model.Hamster ham = workbench.getSimulation()
				.getSimulationModel().getHamster(-1);
		// Richtung festlegen
		String richtung;
		if (ham.getDir() == Hamster.NORD) {
			richtung = "Nord";
		} else if (ham.getDir() == Hamster.OST) {
			richtung = "Ost";
		} else if (ham.getDir() == Hamster.SUED) {
			richtung = "Sued";
		} else {
			richtung = "West";
		}
		SchemePair hamster = JScheme.list("Hamster", 
				ham.getY(), ham.getX(), ham.getMouth(), richtung);

		// Territoriums-Liste aus Feld und Hamster erstellen
		SchemePair liste = JScheme.list(feld, hamster);

		return liste;
	}

	public static SchemePair setTerritorium(SchemePair terrList) {
		// Das Territorium aus dem Simulator auslesen
		SimulationModel simModel = workbench.getSimulation()
				.getSimulationModel();
		Terrain terr = simModel.getTerrain();
		de.hamster.simulation.model.Hamster defHamster = terr
				.getDefaultHamster();

		// Feld
		SchemePair feld = (SchemePair) terrList.getFirst();
		int reihen = anzahlElemente(feld);
		if (reihen != terr.getHeight()) {
			throw new RuntimeException("falsche Territoriumsgröße");
		}
		for (int r = 0; r < reihen; r++) {
			SchemePair reihe = (SchemePair) feld.nth(r);
			int spalten = anzahlElemente(reihe);
			if (spalten != terr.getWidth()) {
				throw new RuntimeException("falsche Territoriumsgröße");
			}
			for (int s = 0; s < spalten; s++) {
				SchemePair kachel = (SchemePair) reihe.nth(s);
				String type = (String) kachel.nth(0);
				if (type.equals("Kachel")) {
					int koerner = (Integer) kachel.nth(1);
					terr.setWall(s, r, false);
					terr.setCornCount(s, r, koerner);
				} else if (type.equals("Mauer")) {
					terr.setWall(s, r, true);
				} else {
					throw new RuntimeException(
							"Ungueltige Territoriumsstruktur");
				}
			}
		}

		// hamster
		SchemePair hamster = (SchemePair) terrList.getRest();
		hamster = (SchemePair) hamster.getFirst();
		int y = (Integer) hamster.nth(1);
		int x = (Integer) hamster.nth(2);
		int anzahlKoerner = (Integer) hamster.nth(3);
		String blickrichtung = (String) hamster.nth(4);

		// setzen
		defHamster.setXY(x, y);
		defHamster.setMouth(anzahlKoerner);
		if (blickrichtung.equals("Nord")) {
			defHamster.setDir(Hamster.NORD);
		} else if (blickrichtung.equals("Ost")) {
			defHamster.setDir(Hamster.OST);
		} else if (blickrichtung.equals("Sued")) {
			defHamster.setDir(Hamster.SUED);
		} else {
			defHamster.setDir(Hamster.WEST);
		}

		// aktualisieren
		simModel.setChanged();
		simModel.notifyObservers(SimulationModel.TERRAIN);
		return terrList;
	}

	static int anzahlElemente(SchemePair liste) {
		return liste.length();
	}

	public static JScheme getJS() {
		return js;
	}

	public static void setWorkbench(Workbench wbench) {
		workbench = wbench;
	}

	public static Workbench getWorkbench() {
		return workbench;
	}

	public static SchemeHamster getHam() {
		return ham;
	}

}
