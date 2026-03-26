package de.hamster.compiler.model;

import de.hamster.model.HamsterFile;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Dies ist das Model der Compiler Komponente. Es kombiniert im Wesentlichen den
 * Precompiler und den JavaCompiler und bietet den Benachrichtungsmechanismus
 * fuer die View-Komponenten.
 *
 * @author $Author: djasper, jrahn $
 * @version $Revision: 1.2 $
 */
public class CompilerModel {

	/**
	 * Dieses Argument benachrichtigt ueber eine Aenderung der Compilerfehler.
	 */
	public static final String COMPILER_ERRORS = "compiler-errors";

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Der Precompiler
	 */
	protected final Precompiler precompiler;

	/**
	 * Der JavaCompiler
	 */
	protected final JavaCompiler javaCompiler;

	/**
	 * Die Liste der Fehler, die bei der letzten Compilierung aufgetreten sind.
	 *
	 * Typ: JavaCompiler.compile(file) liefert offensichtlich eine Liste von Fehler-Objekten.
	 * Ohne den Code von JavaCompiler zu kennen, ist die einzig sichere, korrekte Typisierung:
	 * List<?> (oder List<Object>).
	 *
	 * Wenn du mir die Rückgabe-Typen von JavaCompiler.compile(...) zeigst, kann ich es
	 * zu z.B. List<CompilerError> präzisieren.
	 */
	protected List<?> compilerErrors = Collections.emptyList();

	/**
	 * Der Konstruktor des CompilerModels. Erzeugt lediglich Instanzen von
	 * Precompiler und JavaCompiler.
	 */
	public CompilerModel() {
		this.precompiler = new Precompiler();
		this.javaCompiler = new JavaCompiler();
	}

	// --- PropertyChangeListener API (modern statt Observable) ---

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	/**
	 * Fuehrt eine Kompilierung der uebergebenen Datei durch.
	 *
	 * @param file Die zu kompilierende Datei.
	 * @throws IOException Falls ein Fehler auftritt
	 */
	public boolean compile(HamsterFile file) throws IOException {
		if (!precompiler.precompile(file)) {
			return false;
		}

		List<?> oldErrors = this.compilerErrors;

		// kann null sein -> in eine leere Liste normalisieren
		List<?> newErrors = javaCompiler.compile(file);
		this.compilerErrors = (newErrors != null) ? newErrors : Collections.emptyList();

		// Event feuern (analog zu notifyObservers(COMPILER_ERRORS))
		pcs.firePropertyChange(COMPILER_ERRORS, oldErrors, this.compilerErrors);

		return this.compilerErrors.isEmpty();
	}

	/**
	 * Liefert die Fehlerliste
	 *
	 * @return Die Liste der Fehler, die bei der letzten Kompilierung aufgetreten sind.
	 */
	public List<?> getCompilerErrors() {
		return compilerErrors;
	}
}