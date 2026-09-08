package de.hamster.flowchart.model.command;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.CommandObject;

/**
 * PAP implementierung vom Hamster Gib-Befehl
 * @author gerrit
 *
 */
public class GibCommandObject extends CommandObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4294218919455286506L;

	public GibCommandObject(String command) {
		super(command);
		this.setType("command");
		this.setPerform("gib");
		this.setString("gib"); // dibo
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		hamster.gib();
		return true;
	}

}
