package de.hamster.prolog.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import de.hamster.prolog.controller.PrologController;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse ist eine Konsolen-Anwendung zur direkten Kommunikation mit dem
 * Prolog-Interpreter aus dem Hamster-Simulator. Für alle aus dem Programm
 * gestarteten, interaktiven Aktionen mit dem Prolog-Interpreter wird die
 * PrologKonsole (zur Eingabe) eingeblendet.
 * 
 * @author Andreas Schäfer
 */
public class PrologKonsole extends JFrame {
	private static final long serialVersionUID = -3860368145942218103L;

	/**
	 * Instanzvariable der PrologKonsole. Es sollte nur eine Instanz des Konsole
	 * existieren.
	 */
	private static PrologKonsole prologKonsole;

	/**
	 * Textfeld zur Auflistung der Prolog-Ausgaben sowie zum Entgegennehmen der
	 * Benutzereingaben.
	 */
	private PrologTextArea textArea;

	/**
	 * Die zum Textfeld hinzugehörige Scrollpane.
	 */
	private JScrollPane textScrollPane;

	/**
	 * Trace Button zum An/Ausschalten des Tracing-Modus.
	 */
	private JButton traceButton;

	/**
	 * Debug Button zum An/Ausschalten des Debugging-Modus.
	 */
	private JButton debugButton;

	/**
	 * Listing Button zum Auflisten der Prolog-Datenbank-Inhalte.
	 */
	private JButton listingButton;

	/**
	 * Creep-Action Button - Äquivalent zum 'Enter' im 'trace'-Modus
	 */
	private JButton creepButton;

	/**
	 * Skip-Aktion Button - Springe zum nächsten Ziel (im 'trace'-Modus)
	 */
	private JButton skipButton;

	/**
	 * Retry(Redo)-Aktion Button - Neuaswertung des aktuellen Ziels. (im
	 * 'trace'-Modus).
	 */
	private JButton retryButton;

	/**
	 * Fail-Aktion Button - Manuelles Fehlschlagen des aktuellen Ziels.
	 */
	private JButton failButton;

	/**
	 * Abort-Aktion Button - Abbruch der Auswertung des aktuellen Ziels.
	 */
	private JButton abortButton;

	/**
	 * Speichert die Startposition für die Eingabe (neuer) Befehle innerhlab des
	 * Textfeldes. Die Texteingabe in der ("History") also den Bereichen vor der
	 * letzten Startposition sollte unterbunden werden.
	 */
	private int lastComTextPos;

	/**
	 * Die zuletzt zum Textfeld(area) hinzugefügte Zeile.
	 */
	private String lastAddedLine;

	/**
	 * Hilfs-Thread zur Verwaltung der "Zeilen-Präfixe". Zuständig
	 * ausschließlich nur für optische Aufbereitung der Ausgaben in der
	 * Prolog-Konsole.
	 */
	private PrologKonsoleHelper helperThread;

	/**
	 * Schlater für den Tracing und Debugging Modien.
	 */
	private boolean tracing, debugging;

	/**
	 * Verwaltet die Anzeige des Break-Levels des Prolog-Interpreters.
	 */
	private int breakLevel;

	/**
	 * Gibt an, das Ziel für das Tracing angegeben wurde, oder nicht.
	 */
	private boolean traceGoalIsSetted;

	/**
	 * Spechert eine endliche Liste der vom Benutzer eingegebenen
	 * Konsolenbefehle. Die Größe der Liste wird durch die Konstante
	 * HISTORY_SIZE vorgegeben.
	 */
	private ArrayList<String> commandHistory;

	/**
	 * Maximale Anzahl der vom Benutzer eingegebenen Konsolenbefehle.
	 */
	private static final int HISTORY_SIZE = 20;

	/**
	 * Nummer des gerade (vor)selektiernten Konsolenbefehls (Selektierung über
	 * Nach-Oben / Nach-Unten - Taste). Negativer Wert bedeutet keine Selektion.
	 */
	private int commandNr = -1;

	/**
	 * Darstellung des gerade (vor)selektierten Konsolenbefehls als String.
	 */
	private String currentHistoryCommand = "";

	private PrologKonsole() {
		prologKonsole = this;
		setTitle("Prolog - Konsole");
		setSize(650, 305);
		setLocationRelativeTo(Workbench.getWorkbench().getView()
				.getSimulationFrame());
		// setLocation(140, 170);
		setMinimumSize(new Dimension(650, 1));

		PrologKonsoleListener listener = new PrologKonsoleListener();
		addComponentListener(listener);
		addKeyListener(listener);
		addMouseListener(listener);
		addWindowListener(listener);

		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new BorderLayout(0, 0));

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JPanel genButtons = initGenericButtons(listener);
		genButtons.setBorder(new CompoundBorder(new CompoundBorder(
				new EmptyBorder(0, -2, 0, 0), new TitledBorder(
						new EtchedBorder(), "Allgemeine Funktionen",
						TitledBorder.CENTER, TitledBorder.CENTER)),
				new EmptyBorder(0, 2, 2, 2)));
		buttons.add(genButtons);
		JPanel tracingButtons = initTracingButtons(listener);
		tracingButtons.setBorder(new CompoundBorder(new TitledBorder(
				new EtchedBorder(), "Tracing/Debug-Modus", TitledBorder.CENTER,
				TitledBorder.CENTER), new EmptyBorder(0, 2, 2, 2)));
		buttons.add(tracingButtons);
		toolPanel.add(buttons, BorderLayout.NORTH);

		textArea = new PrologTextArea();
		textArea.addKeyListener(listener);
		textArea.addMouseListener(listener);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		textScrollPane = new JScrollPane(textArea);
		textScrollPane.getVerticalScrollBar().setBlockIncrement(
				textArea.getFontMetrics(textArea.getFont()).getHeight()
						- textArea.getFontMetrics(textArea.getFont())
								.getLeading());

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(4, 4));
		panel.setBorder(new EmptyBorder(4, 4, 4, 4));
		panel.add(toolPanel, BorderLayout.NORTH);
		panel.add(textScrollPane, BorderLayout.CENTER);
		getContentPane().add(panel);

		reset();
		commandHistory = new ArrayList<String>();
	}

	public static synchronized PrologKonsole get() {
		if (prologKonsole == null) {
			prologKonsole = new PrologKonsole();
		}
		return prologKonsole;
	}

	private JPanel initGenericButtons(ActionListener al) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 3);
		listingButton = new JButton("listing");
		listingButton.setPreferredSize(new Dimension(70, 20));
		listingButton.addActionListener(al);
		listingButton.setActionCommand("listing");
		listingButton.setFocusable(false);
		panel.add(listingButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 3);
		traceButton = new JButton("trace");
		traceButton.setPreferredSize(new Dimension(80, 20));
		traceButton.addActionListener(al);
		traceButton.setActionCommand("trace");
		traceButton.setFocusable(false);
		panel.add(traceButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		debugButton = new JButton("debug");
		debugButton.setPreferredSize(new Dimension(85, 20));
		debugButton.addActionListener(al);
		debugButton.setActionCommand("debug");
		debugButton.setFocusable(false);
		panel.add(debugButton, gbc);

		return panel;
	}

	private JPanel initTracingButtons(ActionListener al) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 3);
		creepButton = new JButton("creep");
		creepButton.setPreferredSize(new Dimension(70, 20));
		creepButton.setEnabled(false);
		creepButton.addActionListener(al);
		creepButton.setActionCommand("creep");
		creepButton.setFocusable(false);
		panel.add(creepButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 3);
		skipButton = new JButton("skip");
		skipButton.setPreferredSize(new Dimension(70, 20));
		skipButton.setEnabled(false);
		skipButton.addActionListener(al);
		skipButton.setActionCommand("skip");
		skipButton.setFocusable(false);
		panel.add(skipButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 3);
		retryButton = new JButton("retry");
		retryButton.setPreferredSize(new Dimension(70, 20));
		retryButton.setEnabled(false);
		retryButton.addActionListener(al);
		retryButton.setActionCommand("retry");
		retryButton.setFocusable(false);
		panel.add(retryButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 7;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 3);
		failButton = new JButton("fail");
		failButton.setPreferredSize(new Dimension(70, 20));
		failButton.setEnabled(false);
		failButton.addActionListener(al);
		failButton.setActionCommand("fail");
		failButton.setFocusable(false);
		panel.add(failButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 8;
		gbc.gridy = 0;
		abortButton = new JButton("abort");
		abortButton.setPreferredSize(new Dimension(70, 20));
		abortButton.setEnabled(false);
		abortButton.addActionListener(al);
		abortButton.setActionCommand("abort");
		abortButton.setFocusable(false);
		panel.add(abortButton, gbc);

		return panel;
	}

	/**
	 * Stellt den initialen Zustand der Konsole wieder her. Wird aufgerufen wenn
	 * ein Neustart der PrologEngine erfolgte.
	 */
	public void reset() {
		PrologController.get().setShowOutput(true);
		lastComTextPos = textArea.getDocument().getEndPosition().getOffset();
		if (lastAddedLine == null)
			lastAddedLine = "";
		breakLevel = 0;
		setTracing(false);
		setDebugging(false);
		if (helperThread == null) {
			helperThread = new PrologKonsoleHelper();
			// helperThread.start();
		} else {
			helperThread.getLinesBuffer().clear();
		}
	}

	/**
	 * Fügt eine neue Zeile zur Textarea in der PrologKonsole hinzu.
	 */
	public synchronized void addLine(final String line) {
		// Speichere die letzte Konsolenzeile ab.
		if (!lastAddedLine.endsWith("\n"))
			setLastAddedLine(getLastAddedLine() + line);
		else
			setLastAddedLine(line);

		updateGUI(new Runnable() {
			public void run() {
				textArea.append(line);
				// Aktualisiere die Text-Start-Position (wichtig für die neuen
				// Eingaben)..
				int endPos = textArea.getDocument().getEndPosition()
						.getOffset() - 1;
				textArea.setCaretPosition(endPos);
				lastComTextPos = endPos;

				// Scrolle runter bis zum Ende des Dokuments.
				int maxScrollPos = textScrollPane.getVerticalScrollBar()
						.getMaximum();
				textScrollPane.getVerticalScrollBar().setValue(maxScrollPos);
			}
		});

		helperThread.addLine(line);
		showPrologKonsole();
	}

	public synchronized void execute(String command, String toConsole) {
		/*
		 * Halte fest, wenn beim Übergang in den Tracing-Modus die eigentliche
		 * Zielklausel, also das was anaysiert wird, mit angegeben worden ist.
		 */
		if (command.contains("trace,")
				|| (tracing && !command.contains("trace."))) {
			traceGoalIsSetted = true;
		}

		/*
		 * Nicht jede Anweisung soll in genau gleicher Form auf der Konsole
		 * erscheinen. 'toConsole' gibt die von 'command' abweichende
		 * Darstellung des Befehls für die Konsole an. In diesem Fall ist es
		 * auch kein richtiger Benutzerbefehl.
		 */
		if (toConsole != null) {
			addLine(toConsole);
		}
		/*
		 * Ein vom Benutzer eingetippter Befehl wird gerade ausgeführt.
		 */
		else {
			// Füge den Benutzerbefehl der History hinzu..
			if (!command.contains("end_of_file")) {
				addToHistory(command.replace(" ", ""));
				// printHistory();
				currentHistoryCommand = "";
				commandNr = -1;
			}

			// Speichere die letzte Konsolenzeile ab.
			if (!lastAddedLine.endsWith("\n"))
				setLastAddedLine(getLastAddedLine() + command);
			else
				setLastAddedLine(command);
		}

		// Disable Toolbar in Simulation-View..
		Workbench.getWorkbench().getSimulation().getSimulationTools()
				.setSimulationPanelListenerEnabled(false);

		try {
			PrologController.get().getPrologProcessWriter().write(command);
			PrologController.get().getPrologProcessWriter().flush();
			PrologController.get().getPrologProcessReader()
					.setUserInputOccured(true);
		} catch (Exception e) {
		}
	}

	public void setTracing(final boolean tracing) {
		if (EventQueue.isDispatchThread()) {
			setTracingIntern(tracing);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						setTracingIntern(tracing);
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * Setzt den Tracing-Modus um.
	 */
	void setTracingIntern(boolean tracing) {
		this.tracing = tracing;
		traceButton.setText(tracing ? "notrace" : "trace");

		if (tracing) {
			// Aktiviere die Tracing/Debug-Buttons.
			prologKonsole.creepButton.setEnabled(true);
			prologKonsole.creepButton.setBackground(Color.GREEN);
			prologKonsole.skipButton.setEnabled(true);
			prologKonsole.skipButton.setBackground(Color.BLUE);
			prologKonsole.retryButton.setEnabled(true);
			prologKonsole.retryButton.setBackground(Color.ORANGE);
			prologKonsole.failButton.setEnabled(true);
			prologKonsole.failButton.setBackground(Color.RED);
			prologKonsole.abortButton.setEnabled(true);
			prologKonsole.abortButton.setBackground(Color.CYAN);
		} else {
			prologKonsole.creepButton.setEnabled(false);
			prologKonsole.creepButton.setBackground(prologKonsole
					.getBackground());
			prologKonsole.skipButton.setEnabled(false);
			prologKonsole.skipButton.setBackground(prologKonsole
					.getBackground());
			prologKonsole.retryButton.setEnabled(false);
			prologKonsole.retryButton.setBackground(prologKonsole
					.getBackground());
			traceGoalIsSetted = false;
		}
	}

	/*
	 * Setzt den Debugging-Modus um.
	 */
	public void setDebugging(final boolean debugg) {
		if (EventQueue.isDispatchThread()) {
			setDebuggingIntern(debugg);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						setDebuggingIntern(debugg);
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * Setzt den Debugging-Modus um.
	 */
	void setDebuggingIntern(final boolean debugg) {
		debugging = debugg;
		if (debugg) {
			prologKonsole.failButton.setEnabled(true);
			prologKonsole.failButton.setBackground(Color.RED);
			prologKonsole.abortButton.setEnabled(true);
			prologKonsole.abortButton.setBackground(Color.CYAN);
		} else {
			prologKonsole.failButton.setEnabled(false);
			prologKonsole.failButton.setBackground(prologKonsole
					.getBackground());
			prologKonsole.abortButton.setEnabled(false);
			prologKonsole.abortButton.setBackground(prologKonsole
					.getBackground());
			traceGoalIsSetted = false;
		}
		debugButton.setText(debugging ? "nodebug" : "debug");

	}

	/**
	 * Bringt die PrologKonsole in den Vordergrund.
	 */
	public void showPrologKonsole() {
		updateGUI(new Runnable() {
			public void run() {
				if (Workbench.getWorkbench().getView().getEditorFrame()
						.isVisible()) {
					prologKonsole.setVisible(true);

					if (Workbench.winPKon != null
							&& !Workbench.winPKon.getState())
						Workbench.winPKon.setState(true);
					prologKonsole.toFront();
					textArea.requestFocus();
				}
			}
		});
	}

	/**
	 * Macht die PrologKonsole unsichtbar.
	 */
	public void hidePrologKonsole() {
		updateGUI(new Runnable() {
			public void run() {
				if (Workbench.winPKon != null && Workbench.winPKon.getState())
					Workbench.winPKon.setState(false);
				PrologKonsole.get().setVisible(false);
			}
		});
	}

	/**
	 * Führt über die SwingUtilities GUI-relevante Anweisungen aus.
	 * 
	 * @param doRun
	 *            Ein Runnuble Objekt welcher die GUI relevanten Anweisungen
	 *            definiert.
	 */
	private void updateGUI(Runnable doRun) {
		try {
			SwingUtilities.invokeLater(doRun);
		} catch (Exception e) {
		}
	}

	public int getBreakLevel() {
		return breakLevel;
	}

	public void setBreakLevel(int level) {
		this.breakLevel = level;
	}

	public boolean isTracing() {
		return tracing;
	}

	public boolean isDebugging() {
		return debugging;
	}

	public synchronized int getLastComTextPos() {
		return lastComTextPos;
	}

	public synchronized void setLastComTextPos(int lastComTextPos) {
		this.lastComTextPos = lastComTextPos;
	}

	public synchronized String getLastAddedLine() {
		return lastAddedLine;
	}

	public synchronized PrologKonsoleHelper getHelperThread() {
		return helperThread;
	}

	public synchronized PrologTextArea getTextArea() {
		return textArea;
	}

	public synchronized void setLastAddedLine(String lastAddedLine) {
		this.lastAddedLine = lastAddedLine;
	}

	/**
	 * Liefert ein String Array mit 'count' letzten Zeilen in der TextArea der
	 * PrologKonsole. Der erste Eintrag des Arrays wird mit der letzten Zeile
	 * der TextArea gefüllt. Alle nachfolgenden Einträge entsprechend mit den
	 * nächst Höheren..
	 * 
	 * @param count
	 *            Anzahl Zeilen, die geliefert werden sollten.
	 * @return String-Array mit den Zeilen aus der TextArea.
	 */
	public synchronized String[] getLastLines(int count) {
		String[] res = new String[count];
		for (int i = 0; i < res.length; i++) {
			res[i] = ""; // Vorbelegung mit Initialwerten
		}

		if (textArea != null
				&& count >= 1
				&& count <= textArea.getDocument().getDefaultRootElement()
						.getElementCount()) {
			Element rootElem = textArea.getDocument().getDefaultRootElement();
			int endLineIndex = rootElem.getElementCount() - 1;

			for (int i = 0; i < count; i++) {
				int index = endLineIndex - i;
				int startOffset = rootElem.getElement(index).getStartOffset();
				int endOffset = rootElem.getElement(index).getEndOffset();

				try {
					res[i] = textArea.getText(startOffset,
							endOffset - startOffset).trim();
				} catch (BadLocationException e) {
				}
			}
		}
		return res;
	}

	/**
	 * Liefert die letzte Eingabe des Benutzers. Rückgabewert kann nicht null
	 * sein, da der Text zwischen 'lastComTextPos' und dem Dokumenten-Ende
	 * mindestens gleich "" ist.
	 */
	public String getUserInput() {
		int endPos = textArea.getDocument().getEndPosition().getOffset() - 1;
		if (lastComTextPos == endPos) {
			return "";
		} else {
			String command = "";
			try {
				command = textArea.getText(lastComTextPos,
						endPos - lastComTextPos).trim();
				command = command.replace("\n", "");
			} catch (BadLocationException e1) {
			}
			return command;
		}
	}

	/**
	 * Fügt ein History-Eintrag zur Textarea der PrologKonsole hinzu.
	 * 
	 * @param command
	 *            anzuzeigender History-Eintrag.
	 */
	public void showHistoryCommand(final String command) {
		if (command == null)
			return;

		updateGUI(new Runnable() {
			public void run() {
				// Entferne den alten History Eintrag, falls einer da ist..
				int endPos = textArea.getDocument().getEndPosition()
						.getOffset() - 1;
				try {
					textArea.getDocument().remove(lastComTextPos,
							endPos - lastComTextPos);
				} catch (BadLocationException e) {
				}
				textArea.append(command);
				endPos = textArea.getDocument().getEndPosition().getOffset() - 1;
				textArea.setCaretPosition(endPos);

				// Scrolle runter bis zum Dokumentende.
				int maxScrollPos = textScrollPane.getVerticalScrollBar()
						.getMaximum();
				textScrollPane.getVerticalScrollBar().setValue(maxScrollPos);
			}
		});
	}

	/**
	 * Liefert den nächsten History-Eintrag (von 0 bis n) und inkrementiert den
	 * Eintrag-Zeiger.
	 * 
	 * @return nächster History-Eintrag.
	 */
	public String getNextCommand() {
		if (commandHistory.size() > 0) {
			commandNr = commandNr < commandHistory.size() - 1 ? commandNr + 1
					: commandNr;
			currentHistoryCommand = commandHistory.get(commandNr);
			return currentHistoryCommand;
		} else {
			return "";
		}
	}

	/**
	 * Liefert den vorherigen History-Eintrag und dekrementiert den
	 * Eintrag-Zeiger (bis -1). Eintrag mit der Nummer -1 ist der "leere"
	 * Eintrag.
	 * 
	 * @return verheriger History-Eintrag in der Liste.
	 */
	public String getPreviousCommand() {
		if (commandHistory.size() > 0 && commandNr != -1) {
			commandNr = commandNr > -1 ? commandNr - 1 : commandNr;
			if (commandNr >= 0)
				currentHistoryCommand = commandHistory.get(commandNr);
			else
				currentHistoryCommand = "";
			return currentHistoryCommand;
		} else {
			return "";
		}
	}

	/**
	 * Fügt ein Eintrag zur History-HamsterCommand-Liste hinzu. Bei gleichen Einträgen
	 * wird lediglich die Reihenfolge geändert. Beim Überschreiten der maximal
	 * zulässigen Listen-Größe wird der am seltensten verwendete Eintrag
	 * entfernt, sodass die Listen-Größe gleich bleibt.
	 * 
	 * @param command
	 *            Nutzer-Befehl.
	 */
	public void addToHistory(String command) {
		command = command.replace("\n", "");
		if (commandHistory.size() < HISTORY_SIZE) {
			// Prüfe, ob bereits drin ist..
			for (int i = 0; i < commandHistory.size(); i++) {
				if (commandHistory.get(i).equals(command)) {
					commandHistory.remove(i);
					break;
				}
			}
			commandHistory.add(0, command);
		}
		// Große ist bereits überschritten..
		else {
			// Prüfe, ob bereits drin ist..
			boolean founded = false;
			for (int i = 0; i < commandHistory.size(); i++) {
				if (commandHistory.get(i).equals(command)) {
					founded = true;
					commandHistory.remove(i);
					break;
				}
			}
			if (founded) {
				commandHistory.add(0, command);
			} else {
				commandHistory.remove(commandHistory.size() - 1);
				commandHistory.add(0, command);
			}
		}
	}

	/**
	 * Listet die Einträge der HamsterCommand History auf der Konsole auf.
	 */
	public void printHistory() {
		System.out.println("HamsterCommand History: ");
		for (int i = 0; i < commandHistory.size(); i++) {
			System.out.println("\tHistory entry: " + commandHistory.get(i));
		}
	}

	public boolean isTraceGoalIsSetted() {
		return traceGoalIsSetted;
	}

	public void setTraceGoalIsSetted(boolean traceGoalIsSetted) {
		this.traceGoalIsSetted = traceGoalIsSetted;
	}

	public String getCurrentHistoryCommand() {
		return currentHistoryCommand;
	}
}
