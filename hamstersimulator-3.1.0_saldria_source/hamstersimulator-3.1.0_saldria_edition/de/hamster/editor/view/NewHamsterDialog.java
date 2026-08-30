package de.hamster.editor.view;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.hamster.model.HamsterFile;
import de.hamster.workbench.Utils;

/**
 * Diese Klasse stellt einen Dialog dar, in dem der Benutzer den Typ eines neuen
 * Hamsterprogramms auswaehlen kann. Dies ist entweder ein imperatives Programm,
 * ein objektorientiertes Hamsterprogramm oder eine einfach Java-Klasse.
 * 
 * @author Daniel Jasper
 */
public class NewHamsterDialog extends JDialog {
	public class ProgramType {
		String string;

		char type;

		public ProgramType(String string, char type) {
			this.string = string;
			this.type = type;
		}

		public String toString() {
			return string;
		}
	}

	protected ProgramType[] programTypes = null;

	protected JOptionPane optionPane;

	protected JComboBox types;

	protected Component parent;

	public NewHamsterDialog(Component parent) {

		super(JOptionPane.getFrameForComponent(parent), "Neu", true);
		this.parent = parent;

		Vector<ProgramType> programTypesList = new Vector<ProgramType>();

		programTypesList.add(new ProgramType(Utils.getResource("editor.impp"),
				HamsterFile.IMPERATIVE));
		programTypesList.add(new ProgramType(Utils.getResource("editor.oop"),
				HamsterFile.OBJECT));
		programTypesList.add(new ProgramType(
				Utils.getResource("editor.klasse"), HamsterFile.HAMSTERCLASS));
		
		// Scratch
		if (Utils.SCRATCH) {
			programTypesList
					.add(new ProgramType(Utils.getResource("editor.scratchp"),
							HamsterFile.SCRATCHPROGRAM));
		}
		if (Utils.FSM) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.fsmp"), HamsterFile.FSM));
		}
		if (Utils.FLOWCHART) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.flowchartp"), HamsterFile.FLOWCHART));
		}
		
		// Python
		if (Utils.PYTHON) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.pythonp"), HamsterFile.PYTHONPROGRAM));
		}

		// JavaScript
		if (Utils.JAVASCRIPT) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.javascriptp"), HamsterFile.JAVASCRIPTPROGRAM));
		}

		// Ruby
		if (Utils.RUBY) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.rubyp"), HamsterFile.RUBYPROGRAM));
		}


		// Christian (lego)
		if (Utils.LEGO) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.legop"), HamsterFile.LEGOPROGRAM));
		}
		// Martin
		if (Utils.SCHEME) {
			programTypesList.add(new ProgramType(Utils
					.getResource("editor.schemep"), HamsterFile.SCHEMEPROGRAM));
		}
		// Prolog
		if (Utils.PROLOG) {
			// Prüfe nochmals nach, ob der Prolog-Interpreter
			// auch wirklich auffindbar ist..
			try {
				Runtime runtime = Runtime.getRuntime();
				// dibo 261109 Process process =
				// runtime.exec("plcon.exe -version");
				Process process = runtime.exec(Utils.PLCON + " -version");
				process.destroy();
			} catch (IOException e) {
				Utils.PROLOG = false;
				System.err.println(Utils.PROLOG_MSG);
				JOptionPane.showMessageDialog(null, Utils.PROLOG_MSG,
						"Prolog-Initialisierungsfehler",
						JOptionPane.ERROR_MESSAGE, null);
			}

			if (Utils.PROLOG) {
				programTypesList.add(new ProgramType(Utils
						.getResource("editor.prologp"),
						HamsterFile.PROLOGPROGRAM));
			}
		}



		programTypes = new ProgramType[programTypesList.size()];
		programTypesList.toArray(programTypes);

		types = new JComboBox(programTypes);
		optionPane = new JOptionPane();
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setMessage(new Object[] {
				Utils.getResource("editor.programmtyp"), types });
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if (isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					setVisible(false);
				}
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setContentPane(optionPane);
		pack();
	}

	public char askForType() {
		optionPane.setValue(null);
		setLocationRelativeTo(parent);
		setVisible(true);
		int value = ((Integer) optionPane.getValue()).intValue();
		if (value == JOptionPane.YES_OPTION)
			return ((ProgramType) types.getSelectedItem()).type;
		else
			return (char) -1;
	}
}