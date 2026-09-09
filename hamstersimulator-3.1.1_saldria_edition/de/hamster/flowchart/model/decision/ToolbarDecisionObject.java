package de.hamster.flowchart.model.decision;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.DecisionObject;

/**
 * Dummy Objekt für die Toolbar
 * 
 * @author gerrit
 * 
 */
public class ToolbarDecisionObject extends DecisionObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -963744998114844318L;

	public ToolbarDecisionObject(String decision) {
		super(decision);
		this.setType("decision");
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		return null;
	}

}
