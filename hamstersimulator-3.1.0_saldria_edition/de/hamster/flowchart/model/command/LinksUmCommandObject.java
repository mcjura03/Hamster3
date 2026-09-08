package de.hamster.flowchart.model.command;

import de.hamster.flowchart.controller.FlowchartProgram;
import de.hamster.flowchart.model.CommandObject;

/**
 * PAP implementierung vom Hamster LinksUm-Befehl
 * 
 * @author gerrit
 * 
 */
public class LinksUmCommandObject extends CommandObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4730484546732961085L;

	public LinksUmCommandObject(String command) {
		super(command);
		this.setType("command");
		this.setPerform("linksUm");
		this.setString("linksUm"); // dibo
	}

	@Override
	public Object executeImpl(FlowchartProgram program) {
		hamster.linksUm();
		return true;
	}

}
