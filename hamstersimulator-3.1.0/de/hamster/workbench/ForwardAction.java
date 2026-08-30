package de.hamster.workbench;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * Diese Klasse erweitert die Klasse AbstractAction auf zwei Arten. Zum einen
 * ist es moeglich, eine Action ueber einen Schluessel zu initialisieren,
 * woraufhin sie sich die noetigen Daten aus dem ResourceBundle heraussucht. Zum
 * anderen wird ein Ereignis nicht mehr direkt in der Klasse verwaltet, sondern
 * eine ForwardAction hat eine Reihe von ActionListenern, an die sie die Aktion
 * weiterleitet.
 * 
 * @author Daniel Jasper
 */
public class ForwardAction extends AbstractAction {
	/**
	 * Hier werden mit Hilfe der Klasse AWTEventMulticaster die ActionListener
	 * gespeichert, die sich an dieser ForwardAction angemeldet haben.
	 */
	ActionListener listener;

	/**
	 * Mit diesem Konstructor wird eine ForwardAction erzeugt. Ihr wird der
	 * Schluessel aus dem ResourceBundle und ein commandKey uebergeben. Über den
	 * commandKey kann eine ausgeloeste Action nachher vom ActionListener
	 * identifiziert werden.
	 * 
	 * @param key
	 *            Der Schluessel aus dem ResourceBundle
	 * @param commandKey
	 *            Der commandKey
	 */
	public ForwardAction(String key, String commandKey) {
		super();
		Utils.setData(this, key);
		putValue(ACTION_COMMAND_KEY, commandKey);
	}

	public ForwardAction(String name, Icon icon) {
		super(name, icon);
	}

	/**
	 * Diese Methode wird aufgerufen, wenn die Action ausgeloest wird. Sie
	 * leitet den ActionEvent an die angemeldeten Listener weiter.
	 */
	public void actionPerformed(ActionEvent e) {
		if (listener != null)
			listener.actionPerformed(e);
	}

	/**
	 * Mit dieser Methode kann sich ein ActionListener an dieser ForwardAction
	 * anmelden.
	 * 
	 * @param l
	 *            Der anzumeldende ActionListener
	 */
	public void addActionListener(ActionListener l) {
		listener = AWTEventMulticaster.add(listener, l);
	}

	/**
	 * Mit dieser Methode kann sich ein ActionListener von dieser ForwardAction
	 * abmelden.
	 * 
	 * @param l
	 *            der abzumeldende ActionListener
	 */
	public void removeActionListener(ActionListener l) {
		listener = AWTEventMulticaster.remove(listener, l);
	}
}
