package de.hamster.prolog.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.hamster.interpreter.Hamster;
import de.hamster.model.HamsterFile;
import de.hamster.prolog.controller.PrologController;
import de.hamster.simulation.model.Terrain;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse repräsentiert einen Prolog-Hamster. Sie implementiert alle 
 * Basisbefehle des Hamsters und stellt diese dem Programmierer zur Verfügung.
 * Es kann ausschließlich nur ein einziger Prolog-Hamster (Instanz) zur selben
 * Zeit existieren. 
 */
public class PrologHamster extends Hamster
{
	/*
	 * Instanzvariable der PrologHamters. Es sollte nur eine Instanz des Hamsters existieren.
	 */
	private static PrologHamster prologHamster;
	
	// Stringdarstellung der Hamsterbefehle, welche Exceptions werfen.
	public String VOR = "vor";
	public String NIMM = "nimm";
	public String GIB = "gib";
	public String LINKS_UM = "linksUm";
	public String VORN_FREI = "vornFrei";
	public String KORN_DA = "kornDa";
	public String MAUL_LEER = "maulLeer";
	
	public enum TerObject {TERRITORIUM, HAMSTER, MAUER, KORN}
	
	/**
	 * Konstruktor des Prolog-Hamsters kann nur innerhalb der Klasse aufgerufen werden.
	 */
	private PrologHamster()
	{
		super(true);
		prologHamster = this;
	}
	
	/**
	 * Initialisiert den Prolog-Hamster. Nach dem Laden der Prologbasierten Implementierungen der
	 * Hamsterbefehle und dem Aufrähmen in der Prolog-Datenbank erfolgt der Export des aktuellen
	 * Hamster-Territoriums ins Prolog. Jegliche Konsolen-Ausgaben während der Export-Aktion
	 * werden ausgeblendet.
	 */
	public void initPrologHamster()
	{	
		// Zeige nachfolgend keine Ausgaben in der Konsole.
		PrologController.get().setShowOutput(false);
		
		// Lade die Grundbefehle des Prolog-Hamsters neu..
		String pfadZumProgramm = "prolog/hamsterBasics.pl";		
		String ladeAnweisung = "compile_predicates([message_hook/3])," +
				"reconsult('"+ pfadZumProgramm +"').";
		PrologController.get().execute(ladeAnweisung);
		
		// Entferne die alten Klauseln, falls vorhanden, aus der Prolog-Datenbank..
		cleanUp();
		
		// Exportiere das aktuelle Territoriums zur Prolog-Engine.
		exportTerrain();
		
		// Konsolen-Ausgaben wieder aktivieren.
		PrologController.get().setShowOutput(true);
	}
	
	/**
	 * Diese Methode wird zum Starten des Prolog-Hamster-Programms aufgerufen.
	 * Dabei erfolgt der Export des aktuellen Territorims in Form von Prolog-
	 * Termen zur Prolog-Engine. Darauffolgend wird das Prolog-Programm des
	 * Nutzers eingelesen und die main-Klausel des Programms aufgerufen. 
	 * 
	 * @param file Hamsterfile vom Typ 'PROLOGPROGRAMM', welches ausgeführt werden soll.
	 */
	public void start(HamsterFile file)
	{	
		PrologController.get().setShowOutput(false);
		
		// Lade das Benutzerprogramm (Konvertiere zuvor die Pfadtrennzeichen.)
		String pfadZumProgramm = file.getFile().getAbsolutePath().replace("\\", "/");		
		String anweisung = "reconsult('"+pfadZumProgramm+"').";
		
		HashMap<String,String> erg = PrologController.get().execute(anweisung);
		PrologController.get().setShowOutput(true);
		
		// Falls die Kompilierung bzw. Interpretierung erfolgreich war, starte die "main.".
		if(erg != null)
		{						
			// Haupteinstiegspunkt im Benutzerprogramm.
			// Unterbinde jegliches Backtracking!
			//anweisung = "main,!.";
			// Backtracking kann vom Programmierer beeinflusst werden.
			anweisung = "main."; 
			erg = PrologController.get().execute(anweisung);
			if(erg != null)
			{
				//System.out.println("main war erfolgreich");
			}
		}
		else
		{		
			//System.out.println("Kompelierung war nicht erfolgreich");
		}
	}
	
	/**
	 * Ermöglicht von der Prolog-Engine aus ein Hamsterbefehl mit Exception-Handling
	 * auszuführen. Die eventuell Aufgetretenen exceptions werden selbst nicht weiter 
	 * an Prolog gereicht. Es wird lediglich ein boolean, als Statuswert der Aktions-
	 * ausführung zurückgegeben.
	 * 
	 * @param befehl Stringdarstellung des Hamsterbefehls: (vor,nimm,gibm,linksUm,kornDa,maulLeer).
	 */
	public boolean hamsterBefehl(String befehl)
	{
		boolean status = true;
		try			
		{ 
			if(befehl.equals(VOR))
			{
				vor();
			}
			else if(befehl.equals(NIMM))
			{
				nimm();
			}
			else if(befehl.equals(GIB))
			{
				gib();
			}
			else if(befehl.equals(LINKS_UM))
			{
				linksUm();
			}
			else if(befehl.equals(KORN_DA))
			{
				status = kornDa();
			}
			else if(befehl.equals(MAUL_LEER))
			{
				status = maulLeer();
			}
		}
		catch (final Exception e)
		{
			status = false;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Hamster-Exception", JOptionPane.ERROR_MESSAGE, null);
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}

		}		
		return status;
	}	
	
	/**
	 * Exportiert das im Hamster-Simulator angezeiges Territorium ins Prolog. 
	 * Dabei werden werden unterschiedliche Prädikate der Prolog-Datenbank
	 * hinzugefügt.
	 */
	public void exportTerrain() {
		
		// Hole die Referenz auf das Workbench-Objekt. 
		Workbench workbench = Workbench.getWorkbench();
		// Hole die Refernz zum Territorium-Objekt des Simulators
		Terrain terrain = workbench.getSimulation().getSimulationModel().getTerrain();
		int terReihen = terrain.getHeight();
		int terSpalten = terrain.getWidth();				
		String ladeAnweisung = "assert(territorium("+terReihen+","+terSpalten+"))";
		
		// Mauern - Terme.
		for (int reihe = 0; reihe < terReihen; reihe++){
			for (int spalte = 0; spalte < terSpalten; spalte++){
				if( terrain.getWall(spalte,reihe)){
					ladeAnweisung += " ,assert(mauer("+reihe+","+spalte+"))";
				}
			}
		}		
		// Korn - Terme.
		for (int reihe = 0; reihe < terReihen; reihe++){
			for (int spalte = 0; spalte < terSpalten; spalte++){
				int kornAnzahl = terrain.getCornCount(spalte,reihe);
				if( kornAnzahl > 0 ){
					ladeAnweisung += " ,assert(korn("+reihe+","+spalte+","+kornAnzahl+"))";
				}
			}
		}	
		// Hamster - Term.
		de.hamster.simulation.model.Hamster hamster = terrain.getDefaultHamster();				
		String blickRichtung;
		if (hamster.getDir() == Hamster.NORD) 		blickRichtung = "NORD";
		else if (hamster.getDir() == Hamster.OST)	blickRichtung = "OST";
		else if (hamster.getDir() == Hamster.SUED)	blickRichtung = "SUED";
		else										blickRichtung = "WEST";
		ladeAnweisung += ", assert(hamster("+hamster.getY()+","+hamster.getX()+",'"+
			blickRichtung+"',"+hamster.getMouth()+")).";
		PrologController.get().execute(ladeAnweisung);
	}
	
	/**
	 * Entfernt alle hamsterrelevante Prädikate aus der Prolog-Datenbank.
	 */
	public void cleanUp() 
	{
		String ladeAnweisung = "retractall(territorium(X,Y)), ";
		ladeAnweisung += "retractall(hamster(X,Y,Z,A)), ";
		ladeAnweisung += "retractall(mauer(X,Y)), ";
		ladeAnweisung += "retractall(korn(X,Y,Z)).";
		PrologController.get().execute(ladeAnweisung);
	}
	
	public void updateHamster()
	{
		// Hole die Referenz auf das Workbench-Objekt. 
		Workbench workbench = Workbench.getWorkbench();
		// Hole die Refernz zum Territorium-Objekt des Simulators
		Terrain terrain = workbench.getSimulation().getSimulationModel().getTerrain();
		// Hamster - Term.
		de.hamster.simulation.model.Hamster hamster = terrain.getDefaultHamster();		
		
		String blickRichtung;
		if (hamster.getDir() == Hamster.NORD) 		blickRichtung = "NORD";
		else if (hamster.getDir() == Hamster.OST)	blickRichtung = "OST";
		else if (hamster.getDir() == Hamster.SUED)	blickRichtung = "SUED";
		else										blickRichtung = "WEST";
		
		String ladeAnweisung = "retractall(hamster(X,Y,Z,A))";
		ladeAnweisung += ", assert(hamster("+hamster.getY()+","+hamster.getX()+",'"+
			blickRichtung+"',"+hamster.getMouth()+")).";
		PrologController.get().execute(ladeAnweisung);
	}
	
	public void updateMauern()
	{
		// Hole die Referenz auf das Workbench-Objekt. 
		Workbench workbench = Workbench.getWorkbench();
		// Hole die Refernz zum Territorium-Objekt des Simulators
		Terrain terrain = workbench.getSimulation().getSimulationModel().getTerrain();
		int terReihen = terrain.getHeight();
		int terSpalten = terrain.getWidth();
		String ladeAnweisung = "retractall(mauer(X,Y))";

		for (int reihe = 0; reihe < terReihen; reihe++){
			for (int spalte = 0; spalte < terSpalten; spalte++){
				if( terrain.getWall(spalte,reihe)){
					ladeAnweisung += ", assert(mauer("+reihe+","+spalte+"))";
				}
			}
		}		
		PrologController.get().execute(ladeAnweisung+".");
		/*
		 * Aufgrund dessen, dass Wände auch Körner "überschreiben,
		 * 0üssen die Koerner ebenfalls aktualisiert werden.
		 */
		updateKoerner();
	}
	
	public void updateKoerner()
	{
		// Hole die Referenz auf das Workbench-Objekt. 
		Workbench workbench = Workbench.getWorkbench();
		// Hole die Refernz zum Territorium-Objekt des Simulators
		Terrain terrain = workbench.getSimulation().getSimulationModel().getTerrain();
		int terReihen = terrain.getHeight();
		int terSpalten = terrain.getWidth();
		String ladeAnweisung = "retractall(korn(X,Y,Z))";

		for (int reihe = 0; reihe < terReihen; reihe++){
			for (int spalte = 0; spalte < terSpalten; spalte++){
				int kornAnzahl = terrain.getCornCount(spalte,reihe);
				if( kornAnzahl > 0 ){
					ladeAnweisung += ", assert(korn("+reihe+","+spalte+","+kornAnzahl+"))";
				}
			}
		}		
		PrologController.get().execute(ladeAnweisung+".");
	}
	
	/**
	 * Liefert die Referenz auf den PrologHamster.
	 * @return Referenz auf den PrologHamster.
	 */
	public static synchronized PrologHamster get()
	{
		if(prologHamster == null)
		{
			prologHamster = new PrologHamster();
		}
		return prologHamster;
	}
}
