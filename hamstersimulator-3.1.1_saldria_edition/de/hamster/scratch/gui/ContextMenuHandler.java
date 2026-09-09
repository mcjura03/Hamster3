package de.hamster.scratch.gui;

import java.util.ArrayList;

public interface ContextMenuHandler {
	abstract public void openContextMenu(ArrayList<MenuItem> menuItems, int x, int y);
}
