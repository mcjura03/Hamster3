package de.hamster.workbench;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

/**
 * Diese Klasse wird vom JFileChooser benutzt, um Dateien darzustellen. Sie
 * liefert zu jeder Datei einen Namen und ein Bild, durch die die Datei
 * repraesentiert wird.
 * 
 * @author Daniel Jasper
 */
public class HamsterFileView extends FileView {
	public String getName(File file) {
		String name = file.getName();
		if (file.isFile()) {
			name = name.substring(0, name.lastIndexOf('.'));
			return name;
		} else {
			return super.getName(file);
		}
	}
	public Icon getIcon(File file) {
		String name = file.getName();
		return super.getIcon(file);
	}
}
