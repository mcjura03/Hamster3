package de.hamster.compiler.view;

import java.awt.AWTEventMulticaster;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import de.hamster.compiler.controller.CompilerController;
import de.hamster.workbench.Utils;

/**
 * Diese von JTable abgeleitete Tabelle stellt Compilerfehler in einer Tabelle
 * dar. Klickt man auf eine Zeile dieser Tabelle, so wird ein Event erzeugt und
 * zum CompilerController geschickt.
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class ErrorTable extends JTable {
	/**
	 * Hier werden die Listener gespeichert, die auf den Klick auf einen Fehler
	 * warten.
	 */
	protected ActionListener listener = null;

	/**
	 * Der Konstruktor. Setzt einige Anzeigeeigenschaften aus der Klasse JTable.
	 */
	public ErrorTable() {
		super(new ErrorTableModel());
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		setShowGrid(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		getColumnModel().getColumn(0).setMaxWidth(18);
		getColumnModel().getColumn(0).setHeaderValue("");
		getColumnModel().getColumn(1).setHeaderValue(Utils.getResource("compiler.problem"));
		getColumnModel().getColumn(1).setPreferredWidth(150);
		getColumnModel().getColumn(2).setHeaderValue("");
		getColumnModel().getColumn(2).setPreferredWidth(150);
		getColumnModel().getColumn(3).setMaxWidth(50);
		getColumnModel().getColumn(3).setHeaderValue(Utils.getResource("compiler.zeile"));
		getColumnModel().getColumn(4).setPreferredWidth(80);
		getColumnModel().getColumn(4).setHeaderValue(Utils.getResource("compiler.datei"));
	}

	/**
	 * Mit dieser Methode wird der ActionListener l zu der Liste der
	 * ActionListener hinzugefuegt.
	 * 
	 * @param l
	 *            Der neue ActionListener
	 */
	public void addActionListener(ActionListener l) {
		listener = AWTEventMulticaster.add(listener, l);
	}

	/**
	 * Diese Methode wird automatisch aufgerufen, wenn sich die Ausgewaehlte
	 * Zeile im JTable aendert. Es wird ein ActionEvent generiert und an die
	 * Listener geschickt.
	 */
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		if (e.getValueIsAdjusting())
			return;
		if (getSelectedRowCount() != 0) {
			if (listener != null) {
				listener.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED,
						CompilerController.ACTION_SELECT));
				clearSelection();
			}
		}
	}
}