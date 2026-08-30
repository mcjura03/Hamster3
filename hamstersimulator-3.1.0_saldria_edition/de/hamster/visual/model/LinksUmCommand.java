package de.hamster.visual.model;

public class LinksUmCommand extends HamsterCommand {

	@Override
	public Object perform() {
		hamster.linksUm();
		System.out.println("linksUm();");
		return null;
	}
}
