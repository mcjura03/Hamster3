package de.hamster.flowchart.model.decision;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.DecisionObject;

/**
 * PAP implementierung vom Hamster MaulLeer-Befehl
 * 
 * @author gerrit
 * 
 */
public class MaulLeerDecisionObject extends DecisionObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2060006334836261810L;
	private Boolean doNegate;

	public MaulLeerDecisionObject(String decision, Boolean not) {
		super(decision);
		this.doNegate = not;
		if (not)
			this.setString("!" + this.getText());
		this.setType("decision");
		this.setPerform("maulLeer");
		this.setString("maulLeer?"); // dibo
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		if (doNegate) {
			return !hamster.maulLeer();
		} else {
			return hamster.maulLeer();
		}
	}

}
