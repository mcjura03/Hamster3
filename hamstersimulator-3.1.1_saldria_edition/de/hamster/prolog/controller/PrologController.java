package de.hamster.prolog.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;

import javax.swing.JOptionPane;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.prolog.model.PrologHamster;
import de.hamster.prolog.model.PrologQuery;
import de.hamster.prolog.model.PrologHamster.TerObject;
import de.hamster.prolog.view.PrologKonsole;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Der PrologController realisiert und verwaltet die Kommunikation des
 * Hamstersimulators mit einem im extern gestarteten Java-Prozess eingebetteten
 * Prologinterpreter. Der Controler beinhaltet zudem den {@link PrologHamster},
 * repräsentativ für den Standard-Hamster des Hamstersimulators, und die
 * {@link PrologKonsole}. Die {@link PrologKonsole} bietet eine alternative
 * Möglichkeit zur Kommunikation mit dem Prologinterpreter sowie weitere, für
 * das Debugging(Tracing) hilfreichen, unterstützenden Werkzeuge.
 * 
 * @author Andreas Schäfer
 */
public class PrologController {
	/**
	 * Instanzvariable des PrologControllers.
	 */
	private static PrologController prologController;

	/**
	 * Externer Prozess zur Verwaltung einern zentralen konsolenbasierten
	 * Prolog-Sitzung. Die Kommunikation mit dem Prolog-Interpreter erfolgt über
	 * die Input- und Outputstreams des Prozesses.
	 */
	private Process prologProcess;

	/**
	 * StreamWriter zum Schreiben in den InputStream des Prolog-Prozesses.
	 */
	private BufferedWriter prologProcessWriter;

	/**
	 * StreamReader (ausgelagert in einem Thread) zum Lesen der Prolog-Ausgaben.
	 */
	private PrologOutputReader prologProcessReader;

	/**
	 * Gibt an, ob die aktuellen Prolog-Ausgaben direkt an den Java-Standard-
	 * Ausgabestream sowie PrologKonsole weitergeleitet werden sollen.
	 * Unwichtige/redundante Ausgaben des Interpreters können dadurch
	 * ausgeblendet werden.
	 */
	private boolean showOutput;

	/**
	 * Gibt an, ob der Prolog-Prozess bereits gestartet wurde.
	 */
	private boolean prologEngineStarted;

	/**
	 * Verwaltet die Status und die Ergebnisse einer Prolog-Querry.
	 */
	private PrologQuery prologQuery;

	/**
	 * Speichert die zuletzt (vom Controller) behandelte Ausgabezeile von
	 * Prolog.
	 */
	private String lastHandledOutputLine;

	/**
	 * Gibt an, ab sich bei der aktuellen Prologausgabe nicht um die eines
	 * "listing"-Befehls handelt. Wird lediglich zur besseren optischen
	 * Aufbereitung der Ausgaben werwendet.
	 */
	private boolean listingRule;

	/**
	 * Beim Auftreten eines (syntax)-Fehlers wird dieser Schalter auf true
	 * gesetzt.
	 */
	private boolean errorOccured;

	/**
	 * Der SWI-Prolog-Banner soll lediglich nur einmal, am Anfang einer Sitzung
	 * eingeblendet werden. Dieser Schlater gibt an, ob der Banner angezeigt
	 * wird.
	 */
	private boolean showBanner;

	/**
	 * Gibt an die Anzahl der Fehlversuche zum starten der Prolog-Engine.
	 */
	private int prologEngineStartMisses;

	/**
	 * Globale Anzahl der (Neu)Starts der PrologEngine (bzw des Prozesses).
	 */
	private int prologEngineRestartCount;

	/**
	 * Nach dem Ausführen (Abschicken) einer einzelnen Anweisung (im trace
	 * Modus) sollte man Richtigerweise erst auf die Rückmeldung von Prolog
	 * warten, bevor man die nächste Anweisung annimt und abschickt.
	 */
	private boolean waitingForPrologAnswer;

	public static boolean checkProlog() {
		try {
			String s = initMessageHook();
			String initArgs = " -s " + s + " -q -f none -nosignals -tty";
			Process prologProcess = Runtime.getRuntime().exec(
					// dibo 261109 "plcon" + initArgs);
					Utils.PLCON + initArgs);
			prologProcess.destroy();
		} catch (Throwable th) {
			return false;
		}
		return true;
	}

	private PrologController() {
		prologController = this;
		showOutput = true;
		showBanner = true;
		lastHandledOutputLine = "";

		PrologKonsole.get();
		PrologHamster.get();

		// Starte die PrologEngine.
		ensurePrologEngineIsRunning();
	}

	/**
	 * Dient zur einmaligen Erzeugung des PrologControllers (Singleton-Pattern).
	 */
	public static PrologController get() {
		if (PrologController.prologController == null) {
			PrologController.prologController = new PrologController();
		}
		return prologController;
	}

	/**
	 * Startet einen neuen konsolenbasierten Prolog-Prozess, falls nicht bereits
	 * gestartet, mit zusätzlichen Initialisierungsparametern.
	 */
	public synchronized boolean startPrologEngine() {
		if (prologProcess != null) {
			int exitValue = Integer.MIN_VALUE;
			try {
				exitValue = PrologController.get().getPrologProcess()
						.exitValue();
			} catch (IllegalThreadStateException e2) {
			}
			if (exitValue == Integer.MIN_VALUE) {
				return false;
			}
		}

		Runtime runtime = Runtime.getRuntime();
		try {
			String s = initMessageHook();
			if (s != null) {
				/*
				 * Verwendeten Initialisierungsparameter des SWI-Prolog
				 * Interpreters. -s <file> Angabe der Datei zur Initialisierung
				 * des Interperters. -q (auch -quiet) unterbindet Ausgabe
				 * zusätzlicher Informationen -f none -> verwende keine
				 * -default-Initialisierungsdatei beim Start des Interpreters.
				 */
				String initArgs = " -s " + s + " -q -f none -nosignals -tty";
				/*
				 * TODO: Damit die Executable des Prolog-Interpreters gefunden
				 * wird, muss die PATH-Variable des Systems um den Pfad zum
				 * "Installationsordner ../bin" vom Prolog erweitert werden.
				 */
				// dibo 261109 prologProcess = runtime.exec("plcon" + initArgs);
				prologProcess = runtime.exec(Utils.PLCON + initArgs);
			} else {
				if (prologEngineStartMisses == 0) {
					JOptionPane.showMessageDialog(null,
							"Beim Initialisieren der Prolog-Engine trat"
									+ " ein Fehler auf.",
							"Prolog-Initialisierungsfehler",
							JOptionPane.ERROR_MESSAGE, null);
				}
				prologEngineStartMisses++;
				return false;
			}
		} catch (IOException e) {
			if (prologEngineStartMisses == 0) {
				Utils.PROLOG = false;
				JOptionPane
						.showMessageDialog(
								null,
								Utils.PROLOG_MSG,
								"Prolog-Initialisierungsfehler",
								JOptionPane.ERROR_MESSAGE, null);
			}
			prologEngineStartMisses++;
			return false;
		}

		prologProcessWriter = new BufferedWriter(new OutputStreamWriter(
				prologProcess.getOutputStream()));
		prologProcessReader = new PrologOutputReader(prologProcess
				.getInputStream());
		prologEngineStarted = true;
		prologEngineRestartCount++;
		return true;
	}

	/**
	 * Stoppt den Prozess, falls bereits gestartet, mit dem
	 * SWIProlog-Interpreter.
	 */
	@SuppressWarnings("deprecation")
	public synchronized void stopPrologEngine() {
		if (prologEngineStarted) {
			prologProcessReader.setTerminate(true);
			try {
				prologProcessReader.join();
			} catch (InterruptedException e) {
			}
			prologProcess.destroy();
			prologProcess = null;

			// Halte auch die Simulation an im Hamstersimulator an.
			Workbench workbench = Workbench.getWorkbench();
			if (workbench != null) {
				DebuggerModel.isStop = true;
				workbench.getDebuggerController().getDebuggerModel().setState(
						DebuggerModel.NOT_RUNNING);
				Thread hamThread = workbench.getDebuggerController()
						.getDebuggerModel().getHamsterThread();
				if (hamThread != null && hamThread.isAlive()) {
					hamThread.stop();
				}
			}
			prologEngineStarted = false;
			handlePrologOutput("INFO: Execution aborted\n", true, null);
		} else {
			// System.err.println("Kein Prolog-Prozess gestartet.");
		}
	}

	public boolean ensurePrologEngineIsRunning() {
		boolean isRunning = true;
		if (prologProcess != null) {
			int exitValue = Integer.MIN_VALUE;
			try {
				exitValue = PrologController.get().getPrologProcess()
						.exitValue();
			} catch (IllegalThreadStateException e2) {
			}
			if (exitValue != Integer.MIN_VALUE) {
				isRunning = false;
			}
		}
		// prologProcess ist null
		else {
			isRunning = false;
		}

		/*
		 * Falls die Prolog-Engine nicht mehr läuft, starte diese neu und
		 * initialisiere auch die übrigen Komponenten..
		 */
		if (!isRunning) {
			prologEngineStarted = false;
			boolean engineStarted = startPrologEngine();
			if (engineStarted) {
				// Initialisiere bzw. setze zurück die PrologKonsole.
				PrologKonsole.get().reset();
				// Warte bis der SWIProlog-Banner vollständig eingeblendet ist.
				PrologKonsole.get().getHelperThread().waitForBunner();
				// Importiere die Basis-Befehle des Hamsters.
				PrologHamster.get().initPrologHamster();
			} else {
				// throw new RuntimeException();
				// System.out.println("engine konnte nicht gestarted werden.");
			}
		}
		return !isRunning;
	}

	/**
	 * Vor der Auführung einer jeden neuen Query, sollte gewartet werden bis die
	 * bisherige Query, falls vorhanden, beendet ist.
	 */
	public HashMap<String, String> execute(String command) {
		HashMap<String, String> resultMap = null;
		prologQuery = new PrologQuery(command);

		// Aktualisiere einige Einstellungen in der PrologKonsole.
		if (command.contains("trace,")
				|| (PrologKonsole.get().isTracing() && !command
						.contains("trace."))) {
			PrologKonsole.get().setTraceGoalIsSetted(true);
		}

		if (showOutput) {
			// System.out.println("BEGIN of EXECUTION: "+prologQuery.getId()+
			// ", '"+prologQuery.getCommand()+"'");
			PrologKonsole.get().addLine(prologQuery.getCommand() + "\n");
		}

		try {
			prologProcessWriter.write(prologQuery.getCommand() + "\n");
			prologProcessWriter.flush();
		} catch (IOException e) {
			// System.out.println("IOException raised");
			return resultMap;
		}

		// Warte bis diese Query ausgeführt ist.
		try {
			synchronized (prologQuery) {
				// System.out.println("PC: warte auf die querry " +
				// prologQuery.getId()+", '" +prologQuery.getCommand());

				prologQuery.wait();
			}

			// System.out.println("PC: notified for querry end "+
			// prologQuery.getId()+", '" +prologQuery.getCommand()+"'");
		} catch (InterruptedException e) {
		}

		// System.out.println("END EXECUTION: "+prologQuery.getId()+
		// ", '"+prologQuery.getCommand()+"', result: "+prologQuery.isResult());

		// Extrahiere die Ergebnisse der Querry und leite diese weiter..
		if (prologQuery.isResult()) {
			resultMap = new HashMap<String, String>();
		}

		prologQuery = null;
		return resultMap;
	}

	/**
	 * Behandelt die Ausgaben des Prologinterpreters. Dies wird dadurch
	 * unterstützt dass die Ausgabezeilen von Prolog je nach aktuellem Kontext
	 * mit entsprechenden Zeilenpräfixen versehen sind. Danach kann der
	 * Controller die Ausgabe leichter interpretieren und entsprechende Aktionen
	 * ausführen..
	 */
	public void handlePrologOutput(String line,
			boolean putLineBreakAtEndOfLine, String nextLine) {
		updateSettings();

		// System.out.println("Handle new Line <"+line+">");

		/*
		 * Reagiere auf die Prolog-Ausgaben anhand den zuvor in der
		 * 'message_hook.pl' definierten Schlüsselwörtern. Reagiere aber nicht
		 * darauf, wenn gerade der Inhalt der 'message_hook.pl'-Datei, bei einem
		 * 'listing'-Befehl beispielsweise, selbst auf den user_output geleitet
		 * wurde. In der 'message_hook.pl' ist vor jedem Schlüsselwort der
		 * String "severity_prefix" plaziert. Dies kann nachfolgend abgefangen
		 * werden.
		 */
		if (line.contains("severity_prefix")) {
			// Leite die Ausgaben direkt weiter..
			if (showOutput) {
				// System.out.println(line);
				PrologKonsole.get().addLine(line + "\n");
				PrologKonsole.get().showPrologKonsole();
			}
		}
		// Verarbeite aktuelle Zeile anhand der vordefinierten Präfixe.
		else {
			if (line.startsWith("SILENT:")) {
				line = line.substring(line.indexOf("SILENT: ") + 8);
				// System.out.println("betrete block SILENT");

				/*
				 * Prüfe nach, ob der'trace'- oder 'debug'-Modus sich geändert
				 * hat.
				 */
				if (line.contains("Trace mode")) {
					if (line.endsWith("on")) {
						PrologKonsole.get().setTracing(true);
					} else if (line.endsWith("off")) {
						PrologKonsole.get().setTracing(false);
					}
				} else if (line.contains("Debug mode")) {
					if (line.endsWith("on")) {
						PrologKonsole.get().setDebugging(true);
					} else if (line.endsWith("off")) {
						PrologKonsole.get().setDebugging(false);
					}
				}

				if (showOutput) {
					// System.out.println(line);
					// PrologKonsole.get().addLine(line+"\n");
				}

				if (errorOccured) {
					if (nextLine == null) {
						PrologKonsole.get().getHelperThread().setEndOfQuery();
					}
				}
			}
			/*
			 * Behandle die Informationsnachrichten (Hinweise).
			 */
			else if (line.contains("INFO:")) {
				line = line.substring(line.indexOf("INFO:"));
				line = line.substring(line.indexOf("INFO:") + 5);

				// System.out.println("betrete block INFO");

				/*
				 * Solle die Ausführung der Query abgebrochen sein, muss dies an
				 * den PrologController-Thread weitergereicht werden.
				 */
				if (line.contains("Execution Aborted")) {
					if (prologQuery != null) {
						// Markiere die aktuelle Query als fehlgeschlagen.
						synchronized (prologQuery) {
							prologQuery.setResult(true);
							prologQuery.notify();
						}
					}
				}
				/*
				 * Mit dem Kommando 'break' kann der break-level in der
				 * PrologEngine erhöht werden. Für die entsprechend korrekte
				 * Darstellung in der PrologKonsole muss die break-level
				 * Variable inkrementiert werden.
				 */
				else if (line.contains("Break level")) {
					PrologKonsole.get().setBreakLevel(
							PrologKonsole.get().getBreakLevel() + 1);
				}
				/*
				 * Folgende Prolog-Ausgabe signalisiert break-level
				 * Dekrementierung.
				 */
				else if (line.contains("Exit break level")) {
					PrologKonsole.get().setBreakLevel(
							PrologKonsole.get().getBreakLevel() - 1);
				}

				if (showOutput) {
					// Konvertiere absolute Pfade in relative..
					if (line.contains("compiled")) {
						String pfadAlt = line.substring(0,
								line.indexOf("compiled")).trim();
						String pfadNeu = pfadAlt;
						File file = new File(pfadAlt);
						if (file.isFile()) {
							pfadNeu = file.getName();
						}
						line = line.replace(pfadAlt, pfadNeu);
					}

					if (PrologKonsole.get().getLastAddedLine().endsWith("\n")) {
						// System.out.println(line);
						PrologKonsole.get().addLine("% " + line + "\n");
					} else {
						String prefixNL = "";
						if (PrologKonsole.get().getTextArea().getText()
								.length() > 0) {
							prefixNL = "\n";
						}
						// System.out.println(prefixNL+line);
						PrologKonsole.get().addLine(
								prefixNL + "% " + line + "\n");
					}

					if (line.contains("Execution Aborted")
							|| line.contains("Break level")) {
						PrologKonsole.get().getHelperThread().setEndOfQuery();
					}
				}
			}
			/*
			 * Behandle die Banner-Typisierten Nachrichten(bzw. Ausgaben) von
			 * Prolog.
			 */
			else if (line.startsWith("BANNER:")) {
				// System.out.println("betrete block BANNER");

				if (showOutput && showBanner) {
					// Blende die Hinweise auf die Hilfe aus, da diese nicht
					// verfügbar sind.
					// if(!line.contains("help") && !line.equals(lastAddedLine))
					// if( !line.contains("help") && line.length() != 0 &&
					// !line.equals("BANNER:"))
					// {
					String lineOut = line
							.substring(line.indexOf("BANNER: ") + 8);
					PrologKonsole.get().addLine(lineOut + "\n");
					// }
				}

				if (line.contains("BANNER:") && nextLine == null) {
					showBanner = false;
					PrologKonsole.get().getHelperThread().setEndOfQuery();
				}
			}
			/*
			 * Behandle die Hilfshinweise von Prolog.
			 */
			else if (line.startsWith("HELP:")) {
				// System.out.println("betrete block HELP");

				line = line.substring(line.indexOf("HELP: ") + 6);
				if (showOutput) {
					String lineBreak = " ";
					if (!line.endsWith("?")) {
						lineBreak = "\n";
					}
					// System.out.print(line+lineBreak);
					PrologKonsole.get().addLine(line + lineBreak);

					if (!line.contains("?") && nextLine == null)
						PrologKonsole.get().getHelperThread().setEndOfQuery();
				}
			}
			/*
			 * Behandle die Fehlermeldungen von Prolog.
			 */
			else if (line.startsWith("ERROR:")) {
				// System.out.println("betrete block ERROR");
				// line = line.substring(line.indexOf("ERROR: ")+7);
				errorOccured = true;

				/*
				 * Ist die prologQuery-Variable ungleich 'null', so handelt es
				 * sich dabei bei der aktuellen Query um eine aus dem
				 * Hamstersimulator (und nicht durch die PrologKonsole)
				 * gesendete Abfrage. In diesem Fall ist es notwendig eine
				 * entsprechende Fehlermeldung für den Benutzer einzublenden.
				 */
				if (prologQuery != null) {
					// Konvertiere den absoluten Pfad zur Datei in einen
					// relativen..
					if (line.contains(".ham")) {
						String pfadAlt = line.substring(
								line.indexOf("ERROR: ") + 7, line
										.indexOf(".ham") + 4);
						String pfadNeu = pfadAlt;
						File file = new File(pfadAlt);
						if (file.isFile()) {
							pfadNeu = file.getName();
						}
						line = line.replace(pfadAlt, pfadNeu);
					}

					// Blende eine Fehlermeldung ein.
					JOptionPane.showMessageDialog(null, line, "Prolog-Fehler",
							JOptionPane.ERROR_MESSAGE, null);

					// Markiere die aktuelle Query als fehlgeschlagen.
					synchronized (prologQuery) {
						prologQuery.setResult(false);
						prologQuery.notify();
					}
				}

				/*
				 * Fehlermeldungen sollten immer auf der Konsole ausgegeben
				 * werden, unabhänging davon, welchen Wert die
				 * 'showOutput'-Variable hat.
				 */
				// System.out.println(line);
				PrologKonsole.get().addLine(line + "\n");

				if (nextLine == null) {
					PrologKonsole.get().getHelperThread().setEndOfQuery();
				}
				PrologKonsole.get().showPrologKonsole();
			}
			/*
			 * Behandle Warnungen.
			 */
			else if (line.startsWith("WARNING:")) {
				// System.out.println("betrete block WARNING");

				line = line.substring(line.indexOf("WARNING: ") + 9);
				if (showOutput) {
					// System.out.println(line);
					PrologKonsole.get().addLine(line + "\n");
				}
			}
			/*
			 * Sonstige Ausgaben: Dazu gehören auch die Statusmeldungen zur
			 * einer Query ('true.', 'false.')
			 */
			else if (line.contains("OTHER:")) {
				// System.out.println("betrete block OTHER");

				line = line.substring(line.indexOf("OTHER:"));
				line = line.substring(line.indexOf("OTHER: ") + 7);

				/*
				 * Beim 'leap' Befehl im tracing-Modus wird komischerweise der
				 * tracing modus ausgeschaltet, obwohl der trace noch
				 * weitergeht.. Die nachfolgenden Zeilen werden dann von der
				 * PrologKonsole nicht mehr richtig interpretiert..
				 */
				if (line.contains("Call:") || line.contains("Exit:")
						|| line.contains("Fail:") || line.contains("Redo:")) {
					if (!PrologKonsole.get().isTracing()
							|| !PrologKonsole.get().isTraceGoalIsSetted()) {
						// System.out.println("######### mache trace wieder an..");
						PrologKonsole.get().setTracing(true);
						PrologKonsole.get().setTraceGoalIsSetted(true);
					}
				}

				/*
				 * Im Trace-Mode macht plcon merkwürdigerweise hin und wieder
				 * '^'-Zeichen der Ausgabe hinzu. Bei plwin sind die aber nicht
				 * dabei. An dieser Stelle sollen diese ausgefiltert werden.
				 */
				if (PrologKonsole.get().isTracing()) {
					line = line.replace("^", " ");
				}

				if (showOutput) {
					/*
					 * Führe nachfolgend einige Umformatierungen der
					 * Prolog-Ausgaben durch und leite diese anschließend an die
					 * PrologKonsole weiter.
					 */
					String preLineBreak = "", lineBreak = "", questionMark = "";

					/*
					 * Sollte eine Zeile ein "=", ein 'true' oder 'false' (ohne
					 * einen abschließenden Punkt) oder ein '?' aufweisen, so
					 * handelt es sich hierbei um eine Frage seitens der
					 * Prolog-Engine. Füge nachfolgend, zur Verdeutlichung
					 * diesen Umstands, falls notwendig, ein Fragezeichen am
					 * Ende der aktuellen Zeile hinzu.
					 */
					if (line.contains("=") || line.endsWith("true")
							|| line.endsWith("false") || line.endsWith("?")) {
						if (line.endsWith("?"))
							questionMark = " ";
						PrologKonsole.get().showPrologKonsole();
					}
					/*
					 * Sollte die Zeile hingegen mit einem Punkt oder einer
					 * runden Klammer abgeschlossen sein, so handelt es sich
					 * hier um eine normale, in sich abgeschlossene,
					 * Ausgabe-Zeile. Am Ende der aktuellen Zeile wird daher ein
					 * LineBreak hinzugefügt.
					 */
					else if (line.endsWith(".") || line.endsWith(")")
							|| (line.contains("[") && line.contains("]")) // alternatives
					// im
					// tracing mode..
					) {
						lineBreak = "\n";
						if (line.endsWith(".")) {
							if (!PrologKonsole.get().getLastAddedLine()
									.endsWith("\n")) {
								preLineBreak = "\n";
							}
							if (PrologKonsole.get().isTracing()) {
								PrologKonsole.get().setTracing(false);
							}
							if (line.contains("true") || line.contains("false")
									|| nextLine == null) {
								PrologKonsole.get().getHelperThread()
										.setEndOfQuery();
							}
						} else if (line.endsWith("query(yes)")) {
							PrologKonsole.get().getHelperThread()
									.setEndOfQuery();
						}
						// alternatives im trace modus..
						else if (line.contains("[") && line.contains("]")) {
							preLineBreak += "     ";
						}
					} else if (line.length() == 0) {
						if (PrologKonsole.get().isTracing())
							PrologKonsole.get().setTracing(false);
					}

					// System.out.print(preLineBreak + line + questionMark +
					// lineBreak);
					PrologKonsole.get().addLine(
							preLineBreak + line + questionMark + lineBreak);

					if (line.contains("=") && line.endsWith(".")) {
						PrologKonsole.get().getHelperThread().setEndOfQuery();
					}
				}

				/*
				 * Nachfolgend wird versucht den Abschluss-Ergebnis einer Query
				 * zu erkennen.
				 */
				if (prologQuery != null) {
					if (line.contains("true.") || line.contains("false.")
							|| line.contains("query(yes)")
							|| line.length() == 0) {
						synchronized (prologQuery) {
							if (line.contains("true.")
									|| line.contains("query(yes)")
									|| line.length() == 0) {
								prologQuery.setResult(true);
							} else if (line.contains("false.")) {
								prologQuery.setResult(false);
							}

							// System.out.println("OR: rufe notify bei der query "+
							// prologQuery.getId()+" auf..");

							/*
							 * Benachrichtige den PrologController, dass die
							 * Query abgeschlossen ist.
							 */
							prologQuery.notify();

							// System.out.println("OR: PC wurde benachrichtigt (query ("+
							// prologQuery.getId()+") ende..)");
						}
					}
				}
			}
			/*
			 * Reagiere nachfolgend auf die Prolog-Ausgaben OHNE einen explizit
			 * vorangestellten Schlüsselwort.
			 */
			else {
				// System.out.println("betrete block OHNE PRÄFIX..");

				/*
				 * Handelt es sich dabei um ein Hamsterbefehl?
				 */
				if (line.startsWith("prologhamster:")
						&& !line.contains("write")) {
					/*
					 * Nach der Ausführung eines jeden Hamsterbefehls erwartet
					 * der Prolog- Interpreter ein Rückgabewert (bzw. ein Term).
					 * In unseren Fall wird dies entweder ein 'true.' oder ein
					 * 'false.' sein. Ein 'true.' sollte signalisieren, dass die
					 * Ausführung des Hamsterbefehls ohne eine Exception
					 * erfolgte. Ein 'false.' signalisiert wiederum die Tatsache
					 * einer im Nachhinein abgefangener Exception.
					 */
					String hamsterBefehl = line.substring(line
							.indexOf("prologhamster:") + 14);
					boolean hamsterRueckgabeWert = PrologHamster.get()
							.hamsterBefehl(hamsterBefehl);
					String rueckgabeWertAnProlog = hamsterRueckgabeWert + ".";

					try {
						prologProcessWriter.write(rueckgabeWertAnProlog + "\n");
						prologProcessWriter.flush();
					} catch (IOException e) {
					}

					if (showOutput && PrologKonsole.get().isTracing()) {
						// System.out.println(line);
						PrologKonsole.get().addLine(line + "\n");
						PrologKonsole.get().showPrologKonsole();
					}
				}
				/*
				 * Zum Warten auf die Ausführung eines Hamsterbefehls wird auf
				 * Prolog-Seite das 'read(Term)'-Kommando verwendet. Dieses
				 * macht aber automatisch eine "|:"-Ausgabe auf die Konsole.
				 * Damit der Benutzer zuvor erwähnte Ausgabe an dieser Stelle
				 * nicht sieht, wird diese nachfolgend abgefangen.
				 */
				else if (line.contains("|:")) {
					/*
					 * Wenn die letzte Ausgabe 'prologhamster:...' war, dann
					 * handelt es sich um ein hamsterbefehl, worauf PrologEngine
					 * eine Eingabe auf die in der Implementierung des
					 * Hamsterbefehls integriertes read(status). Das "|:" sollte
					 * in diesem Fall bei der Aktionsausführung nicht auf der
					 * Konsole eingeblendet werden, da die Eingabe automatisch
					 * vom PrologController geschieht und der Benutzer in diesem
					 * Fall ganz unbeteiligt ist.. Anderenfalls hat die
					 * PrologEngine offensichtlich in den inter- aktiven Modus
					 * geschaltet. Dann muss nachfolgend die PrologKonsole in
					 * den Vorder- grund geschafft werden, damit der Benutzer
					 * seine Eingaben tätigen kann.
					 */
					if (lastHandledOutputLine.startsWith("prologhamster:")
							&& !lastHandledOutputLine.contains("write")) {
						// Leite nichts auf die Konsole weiter..
					}
					/*
					 * Andernfalls wird mit diesem Token der interaktive Modus
					 * eingeleitet, worauf die PrologKonsole für
					 * Benutzereingaben in den Vordergrund gelegt werden sollte.
					 */
					else {
						// System.out.print(line);
						PrologKonsole.get().addLine(line + " ");

						PrologKonsole.get().showPrologKonsole();
					}
				}
				/*
				 * Alle übrigen Ausgaben sollten einfach direkt an die
				 * PrologKonsole weitergereicht werden.
				 */
				else {
					if (showOutput) {
						if (line.length() > 0) {
							// Formatierte die Ausgabe beim 'listing.'-Befehl
							// entsprechend vor..
							String tab = "";
							if (listingRule) {
								tab = "\t";
							}
							if (line.contains(":-") && !line.endsWith(".")) {
								listingRule = true;
							} else if (line.endsWith(".")) {
								listingRule = false;
							}

							// Leite die Ausgabe auf die Konsole..
							if (putLineBreakAtEndOfLine) {
								// System.out.println(tab+line);
								PrologKonsole.get().addLine(tab + line + "\n");
							} else // no lineBreak..
							{
								// System.out.print(tab+line);
								PrologKonsole.get().addLine(tab + line);
							}

							// Nach einem Fehler lasse am Ende den Query-Präfix
							// einblenden..
							if (errorOccured && nextLine == null) {
								PrologKonsole.get().getHelperThread()
										.setEndOfQuery();
							}
							PrologKonsole.get().showPrologKonsole();
						}
						// Im Tracing-Mode auch leere Zeilen ausgeben..
						else if (PrologKonsole.get().isTracing()) {
							// System.out.println(line);
							PrologKonsole.get().addLine(line + "\n");
							PrologKonsole.get().showPrologKonsole();
						}
					}
				}
			}
		}

		lastHandledOutputLine = line;
	}

	private void updateSettings() {
		/*
		 * Nach einer abgeschlossenen Querry soll der Error-Flag zurückgesetzt
		 * werden.
		 */
		if (PrologKonsole.get().getLastAddedLine().contains("?-")) {
			errorOccured = false;
		}
	}

	/**
	 * Erstellt eine temporäre Datei zur initialisierung des
	 * Prolog-Interpreters.
	 * 
	 * @return Pfad zur erstellten Initialisierungs-Datei.
	 */
	private static String initMessageHookAlt() {
		File tmpFile;
		try {
			tmpFile = File.createTempFile("message_hook", ".tmp");
		} catch (IOException ioexception) {
			tmpFile = new File("message_hook.tmp");
		}
		FileReader freader = null;
		try {
			// freader = new FileReader(
			// "de/hamster/prolog/controller/message_hook.pl");
			URI uri = Utils.getFileURL(
					"de/hamster/prolog/controller/message_hook.pl").toURI();
			System.out.println(uri);
			freader = new FileReader(new File(uri));
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		BufferedReader breader = new BufferedReader(freader);
		if (tmpFile == null || breader == null)
			return null;
		try {
			PrintWriter printwriter = new PrintWriter(new FileWriter(tmpFile));
			for (String s = null; (s = breader.readLine()) != null;)
				printwriter.println(s);
			breader.close();
			printwriter.close();
		} catch (FileNotFoundException e) {
		} catch (IOException ioexception1) {
		}

		return tmpFile.getAbsolutePath();
	}

	private static String initMessageHook() {
		File tmpFile;
		try {
			tmpFile = File.createTempFile("message_hook", ".tmp");
		} catch (IOException ioexception) {
			tmpFile = new File("message_hook.tmp");
		}
		copyFile("de/hamster/prolog/controller/message_hook.pl", tmpFile);

		return tmpFile.getAbsolutePath();
	}

	public static void copyFile(String from, File to) {
		BufferedReader in = null;
		PrintStream out = null;
		try {
			InputStream is = PrologController.class.getClassLoader()
					.getResourceAsStream(from);
			in = new BufferedReader(new InputStreamReader(is));
			out = new PrintStream(new FileOutputStream(to));
			String eingabe = null;
			while ((eingabe = in.readLine()) != null) {
				out.println(eingabe);
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException exc) {
			}
		}
	}

	/**
	 * Sagt an, ob der Schlater zur Anzeige der Prolog-Ausgaben in der
	 * {@link PrologKonsole} gesetzt ist oder nicht.
	 */
	public boolean isShowOutput() {
		return showOutput;
	}

	/**
	 * Setzt den Schalter zum Anzeigen der Prolog-Prozess-Ausgaben in der
	 * {@link PrologKonsole}.
	 */
	public void setShowOutput(boolean value) {
		showOutput = value;
	}

	/**
	 * Liefert den EingabeStream des Prolog-Prozesses. Falls der
	 * PrologController noch nicht Initialisiert ist, erfolgt eine Fehlermeldung
	 * in std.err.
	 */
	public BufferedWriter getPrologProcessWriter() {
		return prologProcessWriter;
	}

	/**
	 * Liefert die die Referenz auf den Prolog-Prozess zurück.
	 */
	public Process getPrologProcess() {
		return prologProcess;
	}

	public boolean isPrologEngineStarted() {
		return prologEngineStarted;
	}

	public void updateTerrainObject(TerObject objType) {
		// System.out.println("UPDATE CALLED with type "+ objType);
		switch (objType) {
		case TERRITORIUM: {
			// Lese das gesamte Territorium neu ein..
			setShowOutput(false);
			PrologHamster.get().cleanUp();
			PrologHamster.get().exportTerrain();
			setShowOutput(true);
			break;
		}
		case HAMSTER: {
			setShowOutput(false);
			PrologHamster.get().updateHamster();
			setShowOutput(true);
			break;
		}
		case KORN: {
			setShowOutput(false);
			PrologHamster.get().updateKoerner();
			setShowOutput(true);
			break;
		}
		case MAUER: {
			setShowOutput(false);
			PrologHamster.get().updateMauern();
			setShowOutput(true);
			break;
		}
		default:
			break;
		}
	}

	public PrologOutputReader getPrologProcessReader() {
		return prologProcessReader;
	}

	public int getPrologEngineStartMisses() {
		return prologEngineStartMisses;
	}

	public synchronized boolean isWaitingForPrologAnswer() {
		return waitingForPrologAnswer;
	}

	public synchronized void setWaitingForPrologAnswer(boolean waitingForAnswer) {
		this.waitingForPrologAnswer = waitingForAnswer;
	}

	public synchronized int getPrologEngineRestartCount() {
		return prologEngineRestartCount;
	}
}
