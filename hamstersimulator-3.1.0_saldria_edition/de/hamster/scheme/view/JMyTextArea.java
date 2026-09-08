package de.hamster.scheme.view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Stack;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import de.hamster.workbench.Utils;

/**
 * Angepasstes JTextField, das ein Popup-Menu mit Unterpunkten bietet: -
 * Ausschneiden (Strg+X) - Kopieren (Strg+C) - Einfügen (Strg+V) - Löschen
 * (Strg+D) - Alles markieren
 * 
 * @author Timmah
 * @date 21.04.2006
 */
public class JMyTextArea extends JTextArea implements ActionListener,
		MouseListener, CaretListener, KeyListener {

	private JPopupMenu popup;

	private JMenuItem kopieren;

	private JMenuItem einfuegen;

	private JMenuItem allesMarkieren;

	private Timer timer;

	private Clipboard systemClipboard;

	private Object content;

	private StringSelection selection;

	private Transferable transferData;

	private DataFlavor[] dataFlavor;

	private boolean editable;

	public JMyTextArea(boolean e) {
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		popup = new JPopupMenu();
		kopieren = new JMenuItem(Utils.getResource("scheme.view.kopieren"));
		//if (e)
			einfuegen = new JMenuItem(Utils.getResource("scheme.view.einfuegen"));
		allesMarkieren = new JMenuItem(Utils.getResource("scheme.view.allesmarkieren"));

		//popup.add(kopieren);
		//if (e)
			popup.add(einfuegen);
		popup.add(new JSeparator());
		//popup.add(allesMarkieren);

		kopieren.setAccelerator(KeyStroke.getKeyStroke('C',
				InputEvent.CTRL_MASK));
		if (e)
			einfuegen.setAccelerator(KeyStroke.getKeyStroke('V',
					InputEvent.CTRL_MASK));
		allesMarkieren.setAccelerator(KeyStroke.getKeyStroke('A',
				InputEvent.CTRL_MASK));
		kopieren.addActionListener(this);
		if (e)
			einfuegen.addActionListener(this);
		allesMarkieren.addActionListener(this);

		kopieren.setEnabled(false);
		if (e)
			einfuegen.setEnabled(false);

		editable = e;

		// Der Timer feuert nach 1000 Millisekunden ein actionPerformed
		timer = new Timer(1000, this);

		addMouseListener(this);
		addKeyListener(this);
		addCaretListener(this);

		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				//selectAll();

			}
		});
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == kopieren) {
			kopieren();
		} else if (e.getSource() == einfuegen) {
			einfuegen();
		} else if (e.getSource() == allesMarkieren) {
			markieren();
		}
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && editable) {
			//popup.show(this, e.getX(), e.getY());
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void caretUpdate(CaretEvent e) {
		// Dann ist ein Bereich markiert, den man dann Kopieren, Ausschneiden,
		// Löschen kann
		if (e.getMark() != e.getDot()) {
			kopieren.setEnabled(true);
		} else {
			kopieren.setEnabled(false);
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		// Solange, wie Tastatureingaben gemacht werden, wird der Timer immer
		// wieder
		// neu gestartet, so dass die Texteingabe noch nicht als Strg+Z-Element
		// gespeichert
		// wird.
		timer.restart();

	}

	public void keyReleased(KeyEvent e) {
		// Es wird ein Knopf losgelassen. Jetzt hat der Benutzer die gegebene
		// Zeit die Möglichkeit,
		// die Eingabe weiter zu tätigen. Die Zeit läuft...
		timer.start();
	}

	public void kopieren() {
		selection = new StringSelection(getSelectedText());
		systemClipboard.setContents(selection, selection);
		einfuegen.setEnabled(true);
	}

	public void einfuegen() {
		transferData = systemClipboard.getContents(null);
		dataFlavor = transferData.getTransferDataFlavors();

		try {
			content = transferData.getTransferData(dataFlavor[0]);
		} catch (UnsupportedFlavorException e) {
		} catch (IOException e) {
		}

		if (content != null) {
			setText(content.toString());
		}
	}

	public void markieren() {
		selectAll();
	}

	public boolean erlaubeEinfuegen(boolean b) {
		if (editable) {
			einfuegen.setEnabled(b);
		}
		return editable;
	}

	// Initialisiert die relevanten Daten
	public void initialisieren() {
		kopieren.setEnabled(false);
		einfuegen.setEnabled(false);
	}
}