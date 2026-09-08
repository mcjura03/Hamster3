package de.hamster.prolog.view;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.model.HamsterFile;
import de.hamster.prolog.controller.PrologController;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse hat die Funktion eines "Wächters" über die korekkte
 * Funktionsweise der Prolog-Engine. Sollte die Engine aufgrund einer Exception
 * oder per Benutzerbefehl beendet werden, wird diese automatisch neugestartet,
 * damit alle Konsolen-Eingaben auch eine entsprechende Wirkung haben. Parallel
 * dazu kümmert sich die Klasse um die Aktualisierung des Zeilenpräfixes ([break
 * level] [mode] ?-) in der Konsole.
 * 
 * @author Andreas Schäfer
 */
public class PrologKonsoleHelper extends Thread {
	/**
	 * Maximale Anzahl der Fehlversuche beim Neustarten der Prolog-Engine.
	 */
	private static final int ENGINE_MAX_RESTART = 1;

	/**
	 * Terminierungs-Flag.
	 */
	private boolean terminate;

	/**
	 * Signalisiert das Ende einer Prolog-Anfrage. In diesem Fall geschieht
	 * darauffolgend die Aktualisierung des Zeilenpräfixes.
	 */
	private boolean endOfQuery;

	/**
	 * Mutex-Object, zum warten auf den Helper.
	 */
	private Object mutex;

	private ArrayList<String> linesBuffer;
	private final int LINE_BUFFER_MAX = 10;

	public PrologKonsoleHelper() {
		setPriority(MIN_PRIORITY);
		mutex = new Object();
		linesBuffer = new ArrayList<String>();
	}

	public void run() {
		while (!terminate) {
			try {
				synchronized (this) {
					if (endOfQuery) {
						String[] lastLines = new String[2];
						if (linesBuffer.size() > 0) {
							lastLines[0] = linesBuffer
									.get(linesBuffer.size() - 1);
							if (linesBuffer.size() > 1) {
								lastLines[1] = linesBuffer.get(linesBuffer
										.size() - 2);
							}
						} else
							lastLines[0] = "\n\n";

						// System.out.println("lastLines[0] <"+lastLines[0]+">");
						// System.out.println("lastLines[1] <"+lastLines[1]+">");

						if (lastLines[0] != null
								&& !lastLines[0].trim().endsWith("?-")) {
							if (lastLines[0].trim().length() > 0) {
								int lbCount = getCountOfLineBreaks(lastLines[0]);
								if (lbCount == 0) {
									linesBuffer.set(linesBuffer.size() - 1,
											lastLines[0] + "\n");
									PrologKonsole.get().addLine("\n\n");
								} else
									PrologKonsole.get().addLine("\n");
							} else if (lastLines[0].trim().length() == 0) {
								if (lastLines[1] != null
										&& lastLines[1].trim().length() > 0) {
									if (!lastLines[0].endsWith("\n")) {
										linesBuffer.set(linesBuffer.size() - 1,
												lastLines[0] + "\n");
									} else
										PrologKonsole.get().addLine("\n");
								}
							}
							/*
							 * Füge gemäß dem aktuellen Zustand entsprechends
							 * Query-Präfix hinzu.
							 */
							boolean tracing = PrologKonsole.get().isTracing();
							boolean debugging = PrologKonsole.get()
									.isDebugging();
							int breaklevel = PrologKonsole.get()
									.getBreakLevel();
							String prefix = tracing ? "[trace] "
									: debugging ? "[debug] "
											: breaklevel > 0 ? "[" + breaklevel
													+ "] " : "";
							PrologKonsole.get().addLine(prefix + "?- ");
						}

						this.endOfQuery = false;

						synchronized (mutex) {
							try {
								mutex.notifyAll();
							} catch (Exception e) {
							}
						}

						// Unlock Editor..
						Workbench.getWorkbench().getDebuggerController()
								.getDebuggerModel().setState(
										DebuggerModel.NOT_RUNNING);
						DebuggerModel.isStop = false;
						HamsterFile activeHamsterFile = Workbench
								.getWorkbench().getEditor().getTabbedTextArea()
								.getActiveFile();
						if (activeHamsterFile != null) {
							Workbench.getWorkbench().getEditor()
									.getTabbedTextArea().propertyChange(
											activeHamsterFile, false);
						}

						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								// Aktiviere die Toolbar-Elemente im
								// Simulationsfenster..
								Workbench
										.getWorkbench()
										.getSimulation()
										.getSimulationTools()
										.setSimulationPanelListenerEnabled(true);
							}
						});
					}

				} // End of synchronized(this)

				/*
				 * Prüfe zur Sicherheit nach, ob der Prolog-Interpreter-Process
				 * noch weiterhin ordnungsgemäß ausgeführt wird. Falls nicht,
				 * schreibe ein Hinweis auf die PrologKonsole und starte ihn
				 * neu..
				 */
				if (PrologController.get().getPrologEngineStartMisses() < ENGINE_MAX_RESTART) {
					boolean isRestarted = PrologController.get()
							.ensurePrologEngineIsRunning();
					if (isRestarted) {
						// Blende den Query-Prefix ein..
						this.endOfQuery = true;
					}
				} else {
					terminate = true;
				}

				/*
				 * Nochmals ide Checkbox für die PrologKonsole überprüfen. Dies
				 * ist notwendig, da PrologKonsole durchaus früher erstellt
				 * werden kann als die entsprechende Checkbox.
				 */
				if (Workbench.winPKon != null) {
					if (PrologKonsole.get().isVisible()
							&& !Workbench.winPKon.getState()) {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								Workbench.winPKon.setState(true);
							}
						});
					} else if (!PrologKonsole.get().isVisible()
							&& Workbench.winPKon.getState()) {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								Workbench.winPKon.setState(false);
							}
						});
					}
				}

				sleep(100L);
				// System.out.println("PKH alive");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void waitForBunner() {
		synchronized (mutex) {
			try {
				// Starte, falls noch nicht geschehen, den PrologProcessReader..
				if (!PrologController.get().getPrologProcessReader().isAlive())
					PrologController.get().getPrologProcessReader().start();
				// Starte, falls noch nicht geschehen, den Helper..
				if (!isAlive())
					start();

				// Der Helper-Tread selbst soll hier nie warten..
				if (Thread.currentThread().getId() != this.getId()) {
					mutex.wait();
				}
			} catch (Exception e) {
			}
		}
	}

	public synchronized void addLine(String line) {
		if (linesBuffer.size() < LINE_BUFFER_MAX) {
			linesBuffer.add(line);
		} else {
			linesBuffer.remove(0);
			linesBuffer.add(line);
		}
	}

	public ArrayList<String> getLinesBuffer() {
		return linesBuffer;
	}

	private int getCountOfLineBreaks(String line) {
		int count = 0;
		while (line.contains("\n")) {
			count++;
			line = line.substring(line.indexOf("\n") + 1);
		}
		return count;
	}

	public void setEndOfQuery() {
		endOfQuery = true;
	}

	public synchronized boolean getEndOfQuery() {
		return endOfQuery;
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}
}