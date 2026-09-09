package de.hamster.compiler.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import de.hamster.compiler.model.*;
import de.hamster.workbench.Utils;

/**
 * Dies ist das Datenmodell der Klasse ErrorTableModel. Es ordnet den einzelnen
 * Spalten der Tabelle die einzelnen Attribute der Klasse JavaError zu.
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class ErrorTableModel extends AbstractTableModel {
	/**
	 * Die Fehlerliste
	 */
	protected List errors;

	/**
	 * Der Konstruktor, initialisiert errors mit einer leeren Liste.
	 */
	public ErrorTableModel() {
		errors = new ArrayList();
	}

	/**
	 * Aktualisiert die Daten, die im Model gespeichert sind.
	 * 
	 * @param javaErrors
	 */
	public void setErrors(List javaErrors) {
		errors.removeAll(errors);
		errors.addAll(javaErrors);
		fireTableDataChanged();
	}

	/*
	 * Gibt die Anzahl der Spalten zurueck
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 5;
	}

	/*
	 * Gibt die Anzahl der Reihen zurueck
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return errors.size();
	}

	/*
	 * Gibt den Typ eine bestimmten Spalte zurueck. Im JTable koennen nicht nur
	 * Text sondern auch Grafiken angezeigt werden.
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return ImageIcon.class;
		if (columnIndex == 3)
			return Integer.class;
		return super.getColumnClass(columnIndex);
	}

	/*
	 * Gibt den Wert eine bestimmten Zelle zurueck.
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		JavaError error = (JavaError) errors.get(rowIndex);
		switch (columnIndex) {
			case 0 :
				return Utils.getIcon("Stop16.gif");
			case 1 :
				return error.getMessage();
			case 2 :
				if (error.getExtra().size() > 0)
					return error.getExtra().get(0);
				else
					return "";
			case 3 :
				return new Integer(error.getLine());
			case 4 :
				return error.getFile();
		}
		return null;
	}

	/**
	 * Liefert den Fehler in Zeile i
	 * 
	 * @param i
	 *            Die Zeile des Fehlers
	 * @return Der Fehler als JavaError
	 */
	public JavaError getError(int i) {
		return (JavaError) errors.get(i);
	}

}