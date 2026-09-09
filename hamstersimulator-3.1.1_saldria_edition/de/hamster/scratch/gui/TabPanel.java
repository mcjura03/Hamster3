package de.hamster.scratch.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.hamster.scratch.ScratchPanel;
import de.hamster.scratch.ScratchUtils;

public class TabPanel extends JPanel implements RefreshHandler,
		TabChangedHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 127272748699016187L;

	public static Color BACKGROUND_COLOR = new Color(149, 154, 159);
	public static final int CHOSE_BUTTON_WIDTH = 55;
	public static final int START_LEFT = 15;

	private static BufferedImage imgTop;
	private static BufferedImage imgBottom;
	private static BufferedImage imgLeft;
	private static BufferedImage imgRight;
	private static BufferedImage imgTopLeft;
	private static BufferedImage imgTopRight;
	private static BufferedImage imgBottomLeft;
	private static BufferedImage imgBottomRight;

	private ArrayList<TabChangedHandler> tabChangedHandler;
	private ArrayList<ContextMenuHandler> contextMenuHandler;
	private ArrayList<MethodChangeHandler> methodChangeHandler;
	private ArrayList<TabOpenedHandler> tabOpenedHandler;
	private ArrayList<TabButton> tabs;
	private ArrayList<TabButton> invisibleButtons;
	private TabButton extraTab;
	private TabButton selected = null;

	private ScratchPanel scratchPanel;

	public TabPanel(ScratchPanel p) {
		this.scratchPanel = p;
		this.loadImages();
		this.tabs = new ArrayList<TabButton>();
		this.invisibleButtons = new ArrayList<TabButton>();
		this.tabChangedHandler = new ArrayList<TabChangedHandler>();
		this.contextMenuHandler = new ArrayList<ContextMenuHandler>();
		this.methodChangeHandler = new ArrayList<MethodChangeHandler>();
		this.tabOpenedHandler = new ArrayList<TabOpenedHandler>();

		this.extraTab = new TabButton("...");
		this.extraTab.addRefreshHandler(this);
		this.extraTab.addTabChangedHandler(new ExtraTabClickHandler());
		this.addMouseListener(this.extraTab);
	}
	
	public ArrayList<TabButton> getOpenTabs() {
		return tabs;
	}
	
	public ArrayList<TabButton> getInvisibleTabs() {
		return invisibleButtons;
	}

	/**
	 * Wird im Konstruktor aufgerufen und lädt LookAndFeel Bilder. Diese Methode
	 * wird nur einmalig ausgeführt, da die Bilder statisch in der Klasse sind.
	 */
	private void loadImages() {
		if (TabPanel.imgTop != null) {
			return; // Bilder sind bereits geladen
		}

		TabPanel.imgTop = ScratchUtils.getImage("scratch/backgrounds/top.png");
		TabPanel.imgBottom = ScratchUtils
				.getImage("scratch/backgrounds/bottom.png");
		TabPanel.imgLeft = ScratchUtils
				.getImage("scratch/backgrounds/left.png");
		TabPanel.imgRight = ScratchUtils
				.getImage("scratch/backgrounds/right.png");
		TabPanel.imgTopLeft = ScratchUtils
				.getImage("scratch/backgrounds/top_left.png");
		TabPanel.imgTopRight = ScratchUtils
				.getImage("scratch/backgrounds/top_right.png");
		TabPanel.imgBottomLeft = ScratchUtils
				.getImage("scratch/backgrounds/bottom_left.png");
		TabPanel.imgBottomRight = ScratchUtils
				.getImage("scratch/backgrounds/bottom_right.png");
	}

	@Override
	public void paint(Graphics g) {
		this.paintBackground(g);

		for (TabButton button : this.tabs) {
			button.setVisible(false);
		}

		// Zeichne die Buttons
		int left = TabPanel.START_LEFT;
		int selectedLeft = -1;
		for (int i = 0; i < this.tabs.size(); i++) {
			if (left + this.tabs.get(i).getWidth() >= this.getWidth()
					- TabPanel.CHOSE_BUTTON_WIDTH) {
				break;
			}

			if (this.tabs.get(i) == this.selected) {
				selectedLeft = left;
				left += this.tabs.get(i).getWidth();
				this.selected.setVisible(true);
				continue;
			}

			this.tabs.get(i).setVisible(true);
			this.tabs.get(i)
					.paint(g, left, this.getHeight() - TabButton.HEIGHT);
			left += this.tabs.get(i).getWidth();
		}

		this.invisibleButtons.clear();
		for (TabButton button : this.tabs) {
			if (!button.isVisible()) {
				this.invisibleButtons.add(button);
			}
		}

		if (!this.invisibleButtons.isEmpty()) {
			this.extraTab.setVisible(true);
			this.extraTab.paint(g, this.getWidth() - 50, this.getHeight()
					- TabButton.HEIGHT);
		} else {
			this.extraTab.setVisible(false);
		}

		this.paintBorders(g);
		super.paintComponents(g);

		if (this.selected != null && selectedLeft >= 0) {
			this.selected.paint(g, selectedLeft, this.getHeight()
					- TabButton.HEIGHT);
		}
	}

	/**
	 * Zeichnet den Hintergrund der Komponente in angegebener Hintergrundfarbe.
	 * 
	 * @param g
	 *            Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBackground(Graphics g) {
		g.setColor(TabPanel.BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	/**
	 * Zeichnet die Ränder und Ecken der Komponente mit den dafür geladenen
	 * Bildern. Die Ränder werden zum Schluss gezeichnet, so dass
	 * Transparenzeffekte möglich sind und die darin enthaltenen Komponenten
	 * unter den Rändern erscheinen.
	 * 
	 * @param g
	 *            Das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	private void paintBorders(Graphics g) {
		// g.drawImage(imgTop, 0, 0, getWidth(), 8, BACKGROUND_COLOR, null);
		for (int i = 0; i < TabPanel.imgTop.getHeight(); i++) {
			g.setColor(new Color(TabPanel.imgTop.getRGB(0, i)));
			g.drawLine(0, i, this.getWidth(), i);
		}

		// g.drawImage(imgBottom, 0, getHeight() - 8, getWidth(), 8,
		// BACKGROUND_COLOR, null);
		for (int i = 0; i < TabPanel.imgBottom.getHeight(); i++) {
			int top = i + this.getHeight() - TabPanel.imgBottom.getHeight();
			g.setColor(new Color(TabPanel.imgBottom.getRGB(0, i)));
			g.drawLine(0, top, this.getWidth(), top);
		}

		// g.drawImage(imgLeft, 0, 0, 8, getHeight(), BACKGROUND_COLOR, null);
		for (int i = 0; i < TabPanel.imgLeft.getWidth(); i++) {
			g.setColor(new Color(TabPanel.imgLeft.getRGB(i, 0)));
			g.drawLine(i, 0, i, this.getHeight());
		}

		// g.drawImage(imgRight, getWidth() - 5, 0, 5, getHeight(),
		// BACKGROUND_COLOR, null);
		for (int i = 0; i < TabPanel.imgRight.getWidth(); i++) {
			int left = i + this.getWidth() - TabPanel.imgRight.getWidth();
			g.setColor(new Color(TabPanel.imgRight.getRGB(i, 0)));
			g.drawLine(left, 0, left, this.getHeight());
		}

		g.drawImage(TabPanel.imgTopLeft, 0, 0, null);
		g.drawImage(TabPanel.imgTopRight, this.getWidth()
				- TabPanel.imgTopRight.getWidth(), 0, null);
		g.drawImage(TabPanel.imgBottomLeft, 0, this.getHeight()
				- TabPanel.imgBottomLeft.getHeight(), null);
		g.drawImage(TabPanel.imgBottomRight, this.getWidth()
				- TabPanel.imgBottomRight.getWidth(), this.getHeight()
				- TabPanel.imgBottomRight.getHeight(), null);
	}

	public void tabChanged(TabButton button) {
		if (this.selected != null) {
			this.selected.setSelected(false);
			scratchPanel.getStorageController().setSelected(
					this.selected.getName(), false);
		}

		this.selected = button;
		this.selected.setSelected(true);
		scratchPanel.getStorageController().setSelected(
				this.selected.getName(), true);
		this.scratchPanel.modified();

		if (!this.selected.isVisible()) {
			this.tabs.remove(this.selected);
			this.tabs.add(0, this.selected);
			this.refresh();
		}

		for (TabChangedHandler handler : this.tabChangedHandler) {
			handler.tabChanged(button);
		}
	}


	public TabButton getSelected() {
		return this.selected;
	}

	public void tabChanged(String name) {
		TabButton button = null;
		for (int i = 0; i < this.tabs.size(); i++) {
			if (this.tabs.get(i).getName().equals(name)) {
				button = this.tabs.get(i);
				break;
			}
		}

		this.tabChanged(button);
	}

	/**
	 * Fügt einen TabChangedHandler zu diesem TabPanel hinzu, dessen interne
	 * Methode tabChanged aufgerufen wird, sobald sich der aktive Tab ändert.
	 * 
	 * @param handler
	 *            Handler, der beim Tabwechsel aufgerufen werden soll
	 */
	public void addTabChangedHandler(TabChangedHandler handler) {
		this.tabChangedHandler.add(handler);
	}

	/**
	 * Fügt einen TabOpenedHandler zu diesem TabPanel hinzu, dessen interne
	 * Methode tabChanged aufgerufen wird, sobald ein Tab geschlossen wird
	 * 
	 * @param handler
	 *            Handler, der beim Tabwechsel aufgerufen werden soll
	 */
	public void addTabOpenedHandler(TabOpenedHandler handler) {
		this.tabOpenedHandler.add(handler);
	}

	/**
	 * Fügt einen MethodChangeHandler zu diesem TabPanel hinzu, dessen interne
	 * Methode onDelete oder onRename aufgerufen werden, sobald sich diese
	 * Menupunkte aus dem Contextmenu aufgerufen wurden.
	 * 
	 * @param handler
	 *            Handler, der beim Löschen oder Umbenennen aufgerufen werden
	 *            soll
	 */
	public void addMethodChangeHandler(MethodChangeHandler handler) {
		this.methodChangeHandler.add(handler);
	}

	/**
	 * Fügt einen ContextMenuHandler zu diesem TabPanel hinzu, dessen interne
	 * Methode openContextMenu aufgerufen wird, sobald ein Menu aufgerufen wird.
	 * 
	 * @param handler
	 *            Handler
	 */
	public void addContextMenuHandler(ContextMenuHandler handler) {
		this.contextMenuHandler.add(handler);
	}

	private void openContextMenu(TabButton sender, int x, int y) {
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();

		MenuItem temp = new MenuItem("Schließen", new CloseTabClickHandler(
				sender));
		temp.setEnabled(this.tabs.size() != 1);
		items.add(temp);

		MenuItem temp2 = new MenuItem("Andere schließen",
				new CloseAllTabsExpectClickHandler(sender));
		temp2.setEnabled(this.tabs.size() != 1);
		items.add(temp2);

		items.add(new MenuItemSeparator());

		MenuItem renameItem = new MenuItem("Umbenennen",
				new RenameMethodClickHandler(sender.getName()));
		renameItem.setEnabled(!sender.getName().equals("main"));
		items.add(renameItem);

		MenuItem deleteItem = new MenuItem("Löschen",
				new DeleteMethodClickHandler(sender));
		deleteItem.setEnabled(!sender.getName().equals("main"));
		items.add(deleteItem);

		for (ContextMenuHandler handler : this.contextMenuHandler) {
			handler.openContextMenu(items, x + ScratchPanel.OPTIONS_WIDTH
					- MenuItem.PADDING, y + MenuItem.PADDING);
		}
	}

	private void openNotVisibleContextMenu() {
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();

		for (TabButton button : this.tabs) {
			if (!button.isVisible()) {
				items.add(new MenuItem(button.getName(),
						new ExtraMenuItemClickHandler(button)));
			}
		}

		for (ContextMenuHandler handler : this.contextMenuHandler) {
			handler.openContextMenu(items, this.getWidth()
					+ ScratchPanel.OPTIONS_WIDTH - 20,
					ScratchPanel.TAB_PANEL_HEIGHT + 5);
		}
	}

	/**
	 * Fügt einen neuen TabButton zu diesem TabPanel hinzu
	 * 
	 * @param name
	 *            Text auf dem Button
	 */
	public void addTab(String name) {
		// Überprüfe, ob dieser Tab bereits existiert
		if (this.existsTab(name)) {
			this.tabChanged(name);
			return;
		}

		this.onTabOpened(name, true);
		TabButton button = new TabButton(name);
		this.tabs.add(button);
		this.addMouseListener(button);
		button.addRefreshHandler(this);
		button.addTabChangedHandler(this);
		button.addTabClickHandler(new TabButtonRightClickHandler());
		this.tabChanged(button);

		this.repaint();
	}

	public boolean existsTab(String name) {
		for (TabButton tab : this.tabs) {
			if (tab.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public TabButton getTab(String name) {
		for (TabButton tab : this.tabs) {
			if (tab.getName().equals(name)) {
				return tab;
			}
		}

		return null;
	}

	public void setSelected(String selName) {
		if (selName == null) {
			return;
		}
		for (TabButton tab : this.tabs) {
			if (selName.equals(tab.getName())) {
				tabChanged(tab);
				return;
			}
		}
	}

	/**
	 * Schliesst den übergebenen Tab. Vorausgesetzt es ist nicht der letzte.
	 * 
	 * @param button
	 */
	public void closeTab(TabButton button) {
		this.removeMouseListener(button);
		this.tabs.remove(button);
		this.onTabOpened(button.getName(), false);

		if (this.tabs.size() <= 0) {
			this.addTab("main");
			this.onTabOpened("main", true);
		}

		if (this.selected == button) {
			this.tabChanged(this.tabs.get(0));
		}

		this.refresh();
	}

	/**
	 * Schliesst den übergebenen Tab. Vorausgesetzt es ist nicht der letzte,
	 * ansonsten wird main geöffnet.
	 * 
	 * @param button
	 */
	public void closeTab(String name) {
		TabButton button = this.getTab(name);
		if (button == null) {
			return;
		}

		this.closeTab(button);
	}

	public void renameTab(String fromName, String toName) {
		for (TabButton tab : this.tabs) {
			if (tab.getName().equals(fromName)) {
				tab.setName(toName);
			}
		}

		this.refresh();
	}

	/**
	 * Schliesst alle Tabs bis auf den übergebenen Tab.
	 * 
	 * @param button
	 */
	public void closeAllTabsExcept(TabButton button) {
		while (!this.tabs.isEmpty()) {
			TabButton temp = this.tabs.remove(0);
			this.removeMouseListener(temp);
			this.onTabOpened(temp.getName(), false);
		}

		this.tabs.add(button);
		this.addMouseListener(button);
		this.tabChanged(button);
		this.onTabOpened(button.getName(), true);

		this.refresh();
	}

	@Override
	public void refresh() {
		this.repaint();
	}

	public void onTabOpened(String name, boolean opened) {
		for (TabOpenedHandler handler : this.tabOpenedHandler) {
			handler.tabOpened(name, opened);
		}
	}

	class ExtraTabClickHandler implements TabChangedHandler {
		@Override
		public void tabChanged(TabButton button) {
			TabPanel.this.openNotVisibleContextMenu();
		}
	}

	class TabButtonRightClickHandler implements TabClickHandler {
		@Override
		public void onClick(TabButton sender, int x, int y) {
			TabPanel.this.openContextMenu(sender, x, y);
		}
	}

	class ExtraMenuItemClickHandler implements ClickHandler {
		private TabButton button;

		public ExtraMenuItemClickHandler(TabButton button) {
			this.button = button;
		}

		@Override
		public void onClick() {
			TabPanel.this.tabChanged(this.button);
		}
	}

	class CloseTabClickHandler implements ClickHandler {
		private TabButton button;

		public CloseTabClickHandler(TabButton button) {
			this.button = button;
		}

		@Override
		public void onClick() {
			TabPanel.this.closeTab(this.button);
		}
	}

	class CloseAllTabsExpectClickHandler implements ClickHandler {
		private TabButton button;

		public CloseAllTabsExpectClickHandler(TabButton button) {
			this.button = button;
		}

		@Override
		public void onClick() {
			TabPanel.this.closeAllTabsExcept(this.button);
		}
	}

	class DeleteMethodClickHandler implements ClickHandler {
		private TabButton button;

		public DeleteMethodClickHandler(TabButton button) {
			this.button = button;
		}

		@Override
		public void onClick() {
			for (MethodChangeHandler handler : TabPanel.this.methodChangeHandler) {
				handler.onDelete(this.button);
			}
		}
	}

	class RenameMethodClickHandler implements ClickHandler {
		private String name;

		public RenameMethodClickHandler(String name) {
			this.name = name;
		}

		@Override
		public void onClick() {
			for (MethodChangeHandler handler : TabPanel.this.methodChangeHandler) {
				handler.onRename(this.name);
			}
		}
	}
}
