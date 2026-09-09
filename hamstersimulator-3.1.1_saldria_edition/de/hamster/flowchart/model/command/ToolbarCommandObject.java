package de.hamster.flowchart.model.command;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.CommandObject;

/**
 * Dummy Objekt für die Toolbar
 * 
 * @author gerrit
 * 
 */
public class ToolbarCommandObject extends CommandObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7987744516360100294L;

	public ToolbarCommandObject(String command) {
		super(command);
		this.setType("command");
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		return null;
	}

}
