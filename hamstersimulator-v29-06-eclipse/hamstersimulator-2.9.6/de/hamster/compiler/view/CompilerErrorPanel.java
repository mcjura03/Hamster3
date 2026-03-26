package de.hamster.compiler.view;

import de.hamster.compiler.controller.CompilerController;
import de.hamster.compiler.model.CompilerModel;
import de.hamster.compiler.model.JavaError;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Diese Klasse packt einen ErrorTable in eine ScrollPane und aktualisiert diesen
 * bei Änderung des Models.
 *
 * Modernisiert: verwendet PropertyChangeListener statt java.util.Observable/Observer.
 *
 * @author $Author: djasper, jrahn $
 * @version $Revision: 1.2 $
 */
public class CompilerErrorPanel extends JPanel implements PropertyChangeListener {

	/**
	 * Der ErrorTable
	 */
	protected final ErrorTable errorTable;

	/**
	 * Die Model-Komponente, deren Änderungen überwacht und angezeigt werden.
	 */
	protected final CompilerModel compilerModel;

	/**
	 * Der Konstruktor
	 *
	 * @param model      Das Model der Compiler-Komponente
	 * @param controller Der Controller der Compiler-Komponente
	 */
	public CompilerErrorPanel(CompilerModel model, CompilerController controller) {
		super(new BorderLayout());

		this.compilerModel = model;

		// "Leaking this in constructor" vermeiden: Listener nach Konstruktor-Ende registrieren
		SwingUtilities.invokeLater(() -> this.compilerModel.addPropertyChangeListener(CompilerErrorPanel.this));

		this.errorTable = new ErrorTable();
		JScrollPane scrollPane = new JScrollPane(errorTable);
		add(BorderLayout.CENTER, scrollPane);

		errorTable.addActionListener(controller);
		setPreferredSize(new Dimension(200, 100));
	}

	/**
	 * Reagiert auf Änderungen des CompilerModels.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Optional: nur auf das relevante Event reagieren
		if (!CompilerModel.COMPILER_ERRORS.equals(evt.getPropertyName())) {
			return;
		}

		// UI-Update immer auf EDT
		if (SwingUtilities.isEventDispatchThread()) {
			updateTable();
		} else {
			SwingUtilities.invokeLater(this::updateTable);
		}
	}

	private void updateTable() {
		((ErrorTableModel) errorTable.getModel()).setErrors(compilerModel.getCompilerErrors());

		// revalidate/repaint ist in Swing die bessere Wahl als doLayout()
		revalidate();
		repaint();
	}

	/**
	 * Wichtig: wenn das Panel entfernt/geschlossen wird, Listener sauber abmelden.
	 */
	@Override
	public void removeNotify() {
		compilerModel.removePropertyChangeListener(this);
		super.removeNotify();
	}

	/**
	 * Liefert den ausgewählten Fehler aus dem ErrorTable.
	 *
	 * @return null wenn kein Fehler ausgewählt ist, sonst der Fehler.
	 */
	public JavaError getSelectedError() {
		ErrorTableModel model = (ErrorTableModel) errorTable.getModel();
		return model.getError(errorTable.getSelectedRow());
	}
}