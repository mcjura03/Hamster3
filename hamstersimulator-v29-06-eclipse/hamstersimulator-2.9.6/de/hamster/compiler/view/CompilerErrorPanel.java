package de.hamster.compiler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.hamster.compiler.controller.CompilerController;
import de.hamster.compiler.model.CompilerModel;
import de.hamster.compiler.model.JavaError;

/**
 * Diese Klasse packt einen ErrorTable in eine ScrollPanes und updated diesen
 * bei Aenderung des Models
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class CompilerErrorPanel extends JPanel implements Observer {
	/**
	 * Der ErrorTable
	 */
	protected ErrorTable errorTable;

	/**
	 * Die Model-Komponente, dessen Aenderungen ueberwacht und Daten angezeigt
	 * werden.
	 */
	protected CompilerModel compilerModel;

	/**
	 * Der Konstruktor
	 * 
	 * @param model
	 *            Das Model der Compiler-Komponente
	 * @param controller
	 *            Der Controller der Compiler-Komponente
	 */
	public CompilerErrorPanel(CompilerModel model, CompilerController controller) {
		super(new BorderLayout());

		this.compilerModel = model;
		model.addObserver(this);

		errorTable = new ErrorTable();
		JScrollPane scrollPane = new JScrollPane(errorTable);
		add(BorderLayout.CENTER, scrollPane);

		errorTable.addActionListener(controller);
		setPreferredSize(new Dimension(200, 100));
	}

	/**
	 * Ueber diese Methode benachrichtigt das Model das ErrorPanel ueber eine
	 * Aenderung
	 */
	public void update(Observable o, Object arg) {
		((ErrorTableModel) errorTable.getModel()).setErrors(compilerModel
				.getCompilerErrors());
		doLayout();
	}

	/**
	 * Liefert den ausgewaehlten Fehler aus dem ErrorTable.
	 * 
	 * @return null wenn kein Fehler ausgewaehlt ist, den Fehler sonst.
	 */
	public JavaError getSelectedError() {
		ErrorTableModel model = (ErrorTableModel) errorTable.getModel();
		return model.getError(errorTable.getSelectedRow());
	}
}