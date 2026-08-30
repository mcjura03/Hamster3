package de.hamster.flowchart.model.command;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.CommandObject;

/**
 * PAP implementierung vom Hamster Vor-Befehl
 * 
 * @author gerrit
 * 
 */
public class VorCommandObject extends CommandObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3376936040996805344L;

	public VorCommandObject(String command) {
		super(command);
		this.setType("command");
		this.setPerform("vor");
		this.setString("vor"); // dibo
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		hamster.vor();
		return true;
	}

}
