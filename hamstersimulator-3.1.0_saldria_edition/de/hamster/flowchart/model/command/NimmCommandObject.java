package de.hamster.flowchart.model.command;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.CommandObject;

/**
 * PAP implementierung vom Hamster Nimm-Befehl
 * @author gerrit
 *
 */
public class NimmCommandObject extends CommandObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3253253010997697530L;

	public NimmCommandObject(String command) {
		super(command);
		this.setType("command");
		this.setPerform("nimm");
		this.setString("nimm"); // dibo
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		hamster.nimm();
		return true;
	}

}
