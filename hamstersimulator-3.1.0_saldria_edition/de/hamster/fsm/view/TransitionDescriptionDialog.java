package de.hamster.fsm.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hamster.fsm.FsmUtils;
import de.hamster.fsm.controller.FsmMenuMode;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.StateObject;
import de.hamster.fsm.model.transition.BooleanObject;
import de.hamster.fsm.model.transition.TransitionDescriptionObject;
import de.hamster.fsm.model.transition.TransitionObject;
import de.hamster.fsm.model.transition.input.EpsilonBooleanObject;
import de.hamster.fsm.model.transition.output.EpsilonFunctionObject;

/**
 * Dialog zur Auswahl des Inputs und des Outputs einer Beschriftung eines Zustandüberganges
 * @author Raffaela Ferrari
 *
 */
public class TransitionDescriptionDialog extends JDialog {
	private static final Insets INSET =  new Insets(10,5,10,5);
	
	private FsmAutomataPanel parent;
	private TransitionDescriptionObject description;
	private DragAndDropJPanel dadInputPanel;
	private DragAndDropJPanel dadOutputPanel;
	private DragAndDropGlassPane dialogGlassPane;
	
	/**
	 * privater Konstruktor
	 * @param parent Panel, das nach dem Dialog neu gezeichnet werden muss.
	 * @param description Das Object, zu dem der Input und Output hinzugefügt werden soll.
	 */
	private TransitionDescriptionDialog(FsmAutomataPanel parent,TransitionDescriptionObject description) {
		this.parent = parent;
		this.description = description;
		String title = "Beschriftung einer Transition";
		setTitle(title);
		setModal(true);
		
		this.dialogGlassPane = new DragAndDropGlassPane();
		setGlassPane(this.dialogGlassPane);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		

		JLabel header = new JLabel("Beschriftung f�r die Transition von '" + 
				((TransitionObject)description.getParentRenderable()).getParentRenderable().getName() + "' nach '" 
				+ ((TransitionObject)description.getParentRenderable()).getToState().getName() + "'");
		c.gridwidth = GridBagConstraints.REMAINDER;
	    c.anchor = GridBagConstraints.CENTER;
	    c.insets = INSET;
	    panel.add(header, c);
	    
	    JPanel inputPanel = getInputPanel();
	    c.anchor = GridBagConstraints.WEST;
	    panel.add(inputPanel,c);
		
	    JPanel outputPanel = getOutputPanel();
	    panel.add(outputPanel,c);

		JButton cancel=new JButton("abbrechen");
		cancel.addActionListener(new CancelHandler());
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		panel.add(cancel, c);

		JButton ok=new JButton("fertig");
		ok.addActionListener(new ReadyHandler());
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		panel.add(ok, c);

		addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		    	  cancelDialog();
		      }
		});
		add(panel);
		
	    setResizable(false);
		pack();
		setLocation(100, 100);
		setVisible(true);
	}

	/**
	 * InputPanel
	 * @return Panel, dass das große Panel um die auswählbaren und tatsächlich ausgewählten Objekte
	 * für den Input umschließt sowie die dazugehörigen Überschriften.
	 */
	private JPanel getInputPanel() {
		JPanel input = new JPanel(new BorderLayout());
		JPanel dummy = new JPanel(new BorderLayout());
		

		JLabel selectableObjects = new JLabel("Testbefehle und Operatoren", JLabel.CENTER);
		dummy.add(selectableObjects, BorderLayout.WEST);
	    JLabel selection = new JLabel("ausgew�hlte Bedingung", JLabel.CENTER);
	    dummy.add(selection, BorderLayout.EAST);
	    
	    dadInputPanel = new DragAndDropJPanel(FsmUtils.getAllInputObjects(), 
	    		this.description.getInput().getChilds(), true, this);
	   
	    input.add(dummy, BorderLayout.NORTH);
	    input.add(dadInputPanel, BorderLayout.CENTER);
	    return input;
	}

	/**
	 * OutputPanel
	 * @return Panel, dass das große Panel um die auswählbaren und tatsächlich ausgewählten Objekte
	 * für den Output umschließt sowie die dazugehörigen Überschriften.
	 */
	private JPanel getOutputPanel() {
		JPanel output = new JPanel(new BorderLayout());
		JPanel dummy = new JPanel(new BorderLayout());
		
		JLabel selectableObjects = new JLabel("Befehle", JLabel.CENTER);
		dummy.add(selectableObjects, BorderLayout.WEST);
	    
	    JLabel selection = new JLabel("ausgew�hlte Befehlssequenz", JLabel.CENTER);
	    dummy.add(selection, BorderLayout.EAST);
	    
	    LinkedList<FsmObject> selectedInput = new LinkedList<FsmObject>();
	    selectedInput.add(this.description.getInput());
	    dadOutputPanel = new DragAndDropJPanel(FsmUtils.getAllOutputObjects(), 
	    		this.description.getOutput().getChilds(), false, this);

	    output.add(dummy, BorderLayout.NORTH);
	    output.add(dadOutputPanel, BorderLayout.CENTER);

	    return output;
	}

	/**
	 * Statische Methode, der einen neuen TransitionDescriptionDialog zurückgibt
	 * @param parent Panel, das nach dem Dialog neu gezeichnet werden muss.
	 * @param description Das Object, zu dem der Input und Output hinzugefügt werden soll.
	 * @return neuer TransitionDescriptionDialog
	 */
	public static TransitionDescriptionDialog createTransitionDescriptionDialog(FsmAutomataPanel parent,
			TransitionDescriptionObject description) {
		return new TransitionDescriptionDialog(parent, description);
	}

	/**
	 * Methode, die ausgeführt wird, wenn der Dialog durch Fensterschließen oder Abbrechen beendet wird
	 */
	private void cancelDialog() {
		setVisible(false);
		if(description.getInput().getChilds().size()==0) {
			description.getInput().add(new EpsilonBooleanObject(0));
			description.getOutput().add(new EpsilonFunctionObject());
			((StateObject)description.getParentRenderable().getParentRenderable())
			.addTransition((TransitionObject)description.getParentRenderable());
			parent.getPanel().modified();
			parent.refresh();
		}
	}

	/**
	 * Handler, der auf das Klicken des Abbrechen-Buttons reagiert und den Dialog mit 
	 * Standardantworten abbricht (EpsilonObjekte), wenn keine Objekte bisher ausgewählt wurden
	 * @author Raffaela Ferrari
	 *
	 */
	class CancelHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			cancelDialog();
			parent.setModeType(FsmMenuMode.editMode);
			parent.getPanel().setEditModeSelected();
		}		
	}

	/**
	 * Handler, der auf das Klicken des Fertug-Buttons reagiert und der TransitionDescription
	 * die selektierten Objekte für den Input und Output hinzufügt.
	 * @author Raffaela Ferrari
	 *
	 */
	class ReadyHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			if(dadInputPanel.getSelectedObjects().getObjects().size() == 0) {
				description.getInput().add(new EpsilonBooleanObject(0));
			} else {
				description.getInput().add((BooleanObject)dadInputPanel.getSelectedObjects()
						.getObjects().get(0));
			}
			if(dadOutputPanel.getSelectedObjects().getObjects().size() == 0) {
				description.getOutput().getChilds().removeAll(description.getOutput().getChilds());
				description.getOutput().add(new EpsilonFunctionObject());
			} else {
				description.getOutput().getChilds().removeAll(description.getOutput().getChilds());
				for(FsmObject child : dadOutputPanel.getSelectedObjects().getObjects()) {
					description.getOutput().add(child);
				}
			}
			if(!((StateObject)description.getParentRenderable().getParentRenderable())
				.getChilds().contains((TransitionObject)description.getParentRenderable())) {
				((StateObject)description.getParentRenderable().getParentRenderable())
				.addTransition((TransitionObject)description.getParentRenderable());	
			}
			parent.getPanel().modified();
			parent.setModeType(FsmMenuMode.editMode);
			parent.getPanel().setEditModeSelected();
			parent.refresh();
		}
	}

	/**
	 * Gibt die GlassPane für diesen Dialog zurück
	 */
	public DragAndDropGlassPane getDialogGlassPane() {
		return this.dialogGlassPane;
	}
}
