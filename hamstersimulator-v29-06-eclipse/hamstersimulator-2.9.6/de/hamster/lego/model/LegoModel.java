package de.hamster.lego.model;

import de.hamster.model.HamsterFile;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * Dies ist das Model der Lego Komponente. Es kombiniert den LegoCompiler,
 * die .nxj-Code Erzeugung und den Upload.
 *
 * @author Christian
 */
public class LegoModel {

	/**
	 * Dieses Argument benachrichtigt über die Änderung des Uploadstatus.
	 */
	public static final String LEGO_UPLOAD = "upload";

	/**
	 * Dieses Argument informiert darüber, dass gerade kein Upload stattfindet.
	 */
	public static final int NOT_CONNECTED = 0;

	/**
	 * Dieses Argument benachrichtigt über den Erfolg eines Uploads.
	 */
	public static final int SUCCESS = 1;

	/**
	 * Dieses Argument benachrichtigt über einen gescheiterten Uploadversuch.
	 */
	public static final int FAILURE = 2;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final LegoCompiler legoCompiler;
	private final LegoPrecompiler precompiler;
	private final Upload upload;

	private int state;

	/**
	 * Der Konstruktor von LegoModel erzeugt lediglich Instanzen von
	 * LegoCompiler, dem Lego-Precompiler und Upload.
	 */
	public LegoModel() {
		this.state = NOT_CONNECTED;
		this.legoCompiler = new LegoCompiler();
		this.precompiler = new LegoPrecompiler();
		this.upload = new Upload();
	}

	// --- PropertyChangeListener API (modern statt Observable) ---

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	/**
	 * Lädt ein nxj Hamsterfile auf den NXT.
	 *
	 * @param file Das hochzuladene HamsterFile.
	 */
	public void uploadFile(HamsterFile file) {
		if (upload.connect()) {
			try {
				upload.createNXJ(file);
				upload.uploadFile(file);
				setState(SUCCESS);
			} catch (ExceptionInInitializerError ex) {
				setState(FAILURE);
			}
		} else {
			setState(FAILURE);
		}
	}

	/**
	 * Ruft den Precompiler und den JavaCompiler auf.
	 *
	 * @param file Das HamsterFile, das vom JavaCompiler kompiliert werden soll.
	 */
	public void legoCompile(HamsterFile file) throws IOException {
		precompiler.precompile(file);
		legoCompiler.compile(file);
	}

	/**
	 * Liefert den Upload-Status.
	 *
	 * @return Der aktuelle Status.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Setzt den Status des Uploads und feuert ein Event.
	 *
	 * @param state Der neue Status.
	 */
	public void setState(int state) {
		int old = this.state;
		this.state = state;

		// alt: setChanged(); notifyObservers(LEGO_UPLOAD);
		// neu:
		pcs.firePropertyChange(LEGO_UPLOAD, old, this.state);
	}
}