package de.hamster.fsm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.StateObject;
import de.hamster.fsm.model.transition.TransitionDescriptionObject;
import de.hamster.fsm.model.transition.TransitionObject;

/**
 * Dialog, indem zwei Zustände aus den zur Verfügung stehenden ausgewählt
 * werden, zwischen denen dann eine Transition gezogen wird.
 * 
 * @author Raffaela Ferrari
 * 
 */
public class FsmCreateTransitionDialog extends JDialog {

	private final FsmPanel parentPanel;
	private JComboBox listFromState;
	private JComboBox listToState;
	private JButton cancel;;
	private JButton create;

	/**
	 * Privater Konstruktor
	 * 
	 * @param parentPanel
	 *            {@link FsmPanel} über den die auswählbaren States geholt
	 *            werden.
	 * @param x
	 *            X-Koordinate, an dem der Dialog geöffnet werden soll
	 * @param y
	 *            Y-Koordinate, an dem der Dialog geöffnet werden soll
	 */
	private FsmCreateTransitionDialog(final FsmPanel parentPanel, int x, int y) {
		this.parentPanel = parentPanel;
		String description = "Zustands�bergang definieren";
		Label fromStateLabel = new Label("Von Zustand:");
		Label toStateLabel = new Label("Zum Zustand:");
		JPanel panel = new JPanel(new BorderLayout());

		setTitle(description);
		setModal(true);

		StateObject[] states = new StateObject[this.parentPanel.getFsmProgram()
				.getAllStates().size()];
		this.parentPanel.getFsmProgram().getAllStates().toArray(states);
		listFromState = new JComboBox(states);
		listFromState.setSelectedIndex(0);

		listToState = new JComboBox(states);
		listToState.setSelectedIndex(0);

		JPanel contentPanel = new JPanel(new GridLayout(0, 1));
		contentPanel.setPreferredSize(new Dimension(280, 100));
		contentPanel.add(fromStateLabel);
		contentPanel.add(listFromState);
		contentPanel.add(toStateLabel);
		contentPanel.add(listToState);

		cancel = new JButton("abbrechen");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		create = new JButton("erstellen");
		create.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				StateObject fromState = (StateObject) listFromState
						.getSelectedItem();
				StateObject toState = (StateObject) listToState
						.getSelectedItem();

				TransitionObject transition = null;
				for (FsmObject tmp : fromState.getChilds()) {
					if (((TransitionObject) tmp).getToState().equals(toState)) {
						transition = (TransitionObject) tmp;
						break;
					}
				}
				if (transition == null) {
					transition = new TransitionObject(fromState, toState);
				}
				TransitionDescriptionObject tdo = new TransitionDescriptionObject(
						transition);
				transition.add(tdo);
				TransitionDescriptionDialog.createTransitionDescriptionDialog(
						parentPanel.getAutomataPanel(),
						((TransitionDescriptionObject) transition.getChilds()
								.getLast()));
			}
		});

		JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
		buttonPanel.add(cancel);
		buttonPanel.add(create);
		
		JPanel fill1 = new JPanel();
		fill1.setPreferredSize(new Dimension(1, 20));
		JPanel fill2 = new JPanel();
		fill2.setPreferredSize(new Dimension(20, 1));
		JPanel fill3 = new JPanel();
		fill3.setPreferredSize(new Dimension(20, 1));

		panel.add(contentPanel, BorderLayout.NORTH);
		panel.add(fill1, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.add(fill2, BorderLayout.WEST);
		panel.add(fill3, BorderLayout.EAST);

		setContentPane(panel);
		setResizable(false);
		setLocation(x, y);

		pack();
		setVisible(true);
	}

	/**
	 * Statische Methode, die einen neuen FsmCreateTransitionDialog zurückgibt.
	 * 
	 * @param parentPanel
	 *            {@link FsmPanel} über den die auswählbaren States geholt
	 *            werden.
	 * @param x
	 *            X-Koordinate, an dem der Dialog geöffnet werden soll
	 * @param y
	 *            Y-Koordinate, an dem der Dialog geöffnet werden soll
	 * @return FsmCreateTransitionDialog
	 */
	public static FsmCreateTransitionDialog createFsmCreateTransitionDialog(
			final FsmPanel parentPanel, int x, int y) {
		return new FsmCreateTransitionDialog(parentPanel, x, y);
	}
}
