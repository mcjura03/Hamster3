package de.hamster.flowchart.model.decision;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.DecisionObject;

/**
 * PAP implementierung vom Hamster KornDa-Befehl
 * 
 * @author gerrit
 * 
 */
public class KornDaDecisionObject extends DecisionObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2278175184817579863L;
	private Boolean doNegate;

	public KornDaDecisionObject(String decision, Boolean not) {
		super(decision);
		this.doNegate = not;
		if (not)
			this.setString("!" + this.getText());
		this.setType("decision");
		this.setPerform("kornDa");
		this.setString("kornDa?"); // dibo
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		if (doNegate) {
			return !hamster.kornDa();
		} else {
			return hamster.kornDa();
		}
	}

}
