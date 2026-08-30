package de.hamster.workbench;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 * Diese Klasse wird vom JFileChooser benutzt um die Ansicht aufs Dateisystem
 * festzulegen. In dieser Klasse wird dann das Hamsterprogramm-Verzeichnis als
 * Wurzel dargestellt, so dass nichts ausserhalb davon erreichbar ist.
 * 
 * @author Daniel Jasper
 */
public class HamsterFileSystemView extends FileSystemView {
	public File getHomeDirectory() {
		return createFileObject(Utils.HOME);
	}

	public File getDefaultDirectory() {
		return getHomeDirectory();
	}

	public File createNewFolder(File containingDir) throws IOException {
		File f = new File(containingDir.getAbsolutePath() + File.separatorChar
				+ Utils.getResource("workbench.neuerordner"));
		for (int i = 2; f.exists(); i++) {
			f = new File(containingDir.getAbsolutePath() + File.separatorChar
					+ Utils.getResource("workbench.neuerordner") + " " + i);

		}
		f.mkdir();
		return f;
	}

	public File[] getRoots() {
		return new File[] { getHomeDirectory() };
	}
}
