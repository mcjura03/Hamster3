package de.hamster.compiler.view;

import de.hamster.compiler.controller.CompilerController;
import de.hamster.compiler.model.CompilerModel;
import de.hamster.workbench.ForwardAction;
import de.hamster.workbench.Utils;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * Diese Klasse fügt den Kompilieren-Knopf in die Toolbar ein und erzeugt ein
 * Menü mit einem Item "Kompilieren".
 *
 * Modernisiert: PropertyChangeListener statt java.util.Observable/Observer.
 *
 * @author $Author: djasper, jrahn $
 * @version $Revision: 1.2 $
 */
public class CompilerTools implements PropertyChangeListener {

	/**
	 * Das Model dieser Komponente
	 */
	protected final CompilerModel compilerModel;

	/**
	 * Der Controller (Daniel, 08.04.2007)
	 */
	protected final CompilerController controller;

	/**
	 * Diese Aktion löst das Einstellen des CLASSPATH aus.
	 */
	public class ClasspathAction extends ForwardAction {
		public ClasspathAction() {
			super("compiler.classpath", CompilerController.ACTION_CLASSPATH);
		}
	}
	private final ClasspathAction classpathAction = new ClasspathAction();

	/**
	 * Diese Aktion löst das Kompilieren aus.
	 */
	public class CompileAction extends ForwardAction {
		public CompileAction() {
			super("compiler.compile", CompilerController.ACTION_COMPILE);
		}
	}
	private final CompileAction compileAction = new CompileAction();

	/**
	 * Der Konstruktor
	 *
	 * @param model      Das Model der Compiler-Komponente
	 * @param controller Der Controller der Compiler-Komponente
	 */
	public CompilerTools(CompilerModel model, CompilerController controller) {
		this.compilerModel = model;
		this.controller = controller;

		// "Leaking this in constructor" vermeiden: Listener nach Konstruktor-Ende registrieren
		SwingUtilities.invokeLater(() -> this.compilerModel.addPropertyChangeListener(CompilerTools.this));

		JMenu compileMenu = controller.getWorkbench().getView().findMenu("editor", "compile");
		compileMenu.add(new JMenuItem(compileAction));
		compileMenu.add(new JMenuItem(classpathAction));

		JToolBar toolBar = controller.getWorkbench().getView().findToolBar("editor");
		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));
		toolBar.add(Utils.createButton(compileAction));

		compileAction.addActionListener(controller);
		classpathAction.addActionListener(controller);

		compileAction.setEnabled(false);
		// CLASSPATH kann nicht eingestellt werden, wenn lokal ausgeführt wird
		// classpathAction.setEnabled(!Utils.runlocally);
	}

	/**
	 * Reagiert auf Änderungen des CompilerModels.
	 * (Aktuell ist hier kein UI-Update nötig – aber die Schnittstelle ist vorbereitet.)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Falls du später reagieren willst:
		// if (CompilerModel.COMPILER_ERRORS.equals(evt.getPropertyName())) { ... }
	}

	/**
	 * Optional aber sinnvoll: beim Entfernen aus der UI wieder abmelden.
	 * (Falls CompilerTools nie "entfernt" wird, schadet es trotzdem nicht.)
	 */
	public void dispose() {
		compilerModel.removePropertyChangeListener(this);
	}

	// Martin
	/**
	 * Mit dieser Funktion können die Buttons zum Kompilieren deaktiviert werden.
	 */
	public void setButtonsVisible(boolean visible) {
		compileAction.setEnabled(visible);
		// Daniel: Classpath muss ja nicht deaktiviert werden (hängt von runlocally ab)
		// classpathAction.setEnabled(visible);
	}
}