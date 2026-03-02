package de.hamster.simulation.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.FilteredImageSource;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.hamster.simulation.model.Hamster;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class SimulationPanel extends JPanel implements Observer, MouseMotionListener {
	int zoom, oldZoom;

	Image[][] hamster;

	Image[] corn;

	Image wall;

	int width;

	int height;

	int dragStartX, dragStartY, dragX, dragY;

	Cursor defaultCursor;

	SimulationModel model;

	SimulationTools tools;
	
	Font font;

	public SimulationPanel(SimulationModel model, SimulationTools tools) {
		this.model = model;
		model.addObserver(this);
		this.tools = tools;

		this.zoom = 32;  // 3
		this.loadImages();
		this.createCursors();
		this.addMouseMotionListener(this);
		this.setBackground(new Color(240, 244, 246)); // dibo 230309
		
		//font = new Font("SansSerif", Font.BOLD, 12);
	}

	void loadImages() {
		this.hamster = new Image[Utils.COLORS.length][4];
		Image[] orig = new Image[4];
		orig[0] = Utils.getImage("hamsternorth.png");
		orig[1] = Utils.getImage("hamstereast.png");
		orig[2] = Utils.getImage("hamstersouth.png");
		orig[3] = Utils.getImage("hamsterwest.png");
		for (int i = 0; i < this.hamster.length; i++) {
			ColorFilter c = new ColorFilter(i);
			for (int j = 0; j < 4; j++) {
				this.hamster[i][j] = this.createImage(new FilteredImageSource(orig[j].getSource(), c));

			}
		}
		this.corn = new Image[12];
		for (int i = 0; i < this.corn.length; i++) {
			this.corn[i] = Utils.getImage(i + 1 + "Corn32.png");
		}
		this.wall = Utils.getImage("Wall32.png");

	}

	void createCursors() {
		this.defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	}

	public int getCellWidth() {
		//return 2 * (2 << this.zoom);
		return zoom;
	}

	private void updateSize() {
		if (this.model.getTerrain().getWidth() == this.width && this.model.getTerrain().getHeight() == this.height
				&& this.zoom == this.oldZoom) {
			return;
		}
		this.oldZoom = this.zoom;
		this.width = this.model.getTerrain().getWidth();
		this.height = this.model.getTerrain().getHeight();
		this.setPreferredSize(
				new Dimension(this.width * (this.getCellWidth() + 1) + 5, this.height * (this.getCellWidth() + 1) + 5));
		if (this.getParent() != null) {
			this.getParent().doLayout();
		}
	}

	int getX(int col) {
		int centerX = (int) (this.getSize().getWidth() / 2);
		return centerX + col * (this.getCellWidth() + 1) - this.width * (this.getCellWidth() + 1) / 2;
	}

	int getY(int row) {
		int centerY = (int) (this.getSize().getHeight() / 2);
		return centerY + row * (this.getCellWidth() + 1) - this.height * (this.getCellWidth() + 1) / 2;
	}

	public int getCol(int x) {
		int centerX = (int) (this.getSize().getWidth() / 2);
		int col = (x - centerX + this.width * (this.getCellWidth() + 1) / 2) / (this.getCellWidth() + 1);
		return col;
	}

	public int getRow(int y) {
		int centerY = (int) (this.getSize().getHeight() / 2);
		int row = (y - centerY + this.height * (this.getCellWidth() + 1) / 2) / (this.getCellWidth() + 1);
		return row;
	}

	boolean withinTerrain(int col, int row) {
		return col >= 0 && row >= 0 && col < this.width && row < this.height;
	}

	public void drawImage(Graphics2D g, Image image, int i, int j) {
		g.drawImage(image, this.getX(i) + 1, this.getY(j) + 1, this.getCellWidth(), this.getCellWidth(), this);
	}

	public void drawHamsterAlt(Graphics2D g, int nr, int i, int j, int dir, int color) {
		if (nr >= this.hamster.length) {
			nr = this.hamster.length - 1;
		}
		this.drawImage(g, this.hamster[nr][dir], i, j);
	}

	public void drawHamster(Graphics2D g, int nr, int i, int j, int dir, int color) {
		if (color <= -1) {
			color = nr;
		}
		if (color >= this.hamster.length) {
			color = this.hamster.length - 1;
		}
		this.drawImage(g, this.hamster[color][dir], i, j);
	}

	public void drawCorn(Graphics2D g, int i, int j, int count) {
		if (count == 0) {
			return;
		}
		int countR = Math.min(count, 12);
		int w = this.getCellWidth();
		g.drawImage(this.corn[countR - 1], this.getX(i) + 1, this.getY(j) + 1, w, w, this);
//		if (count > 12) {
//			String str = "" + count;
//			FontMetrics m = g.getFontMetrics();
//			Rectangle2D r = m.getStringBounds(str, g);
//			g.drawString(str, this.getX(i) + 1 + w, this.getY(j) + 1 + w);
//		}
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		//graphics.setFont(font);
		this.updateSize();
		if (this.model == null) {
			return;
		}
		Graphics2D g = (Graphics2D) graphics;
		for (int i = 0; i < this.width + 1; i++) {
			g.drawLine(this.getX(i), this.getY(0), this.getX(i), this.getY(this.height));
		}
		for (int i = 0; i < this.height + 1; i++) {
			g.drawLine(this.getX(0), this.getY(i), this.getX(this.width), this.getY(i));
		}
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (this.model.getTerrain().getWall(i, j)) {
					this.drawImage(g, this.wall, i, j);
				} else {
					this.drawCorn(g, i, j, this.model.getTerrain().getCornCount(i, j));
				}
			}
		}
		Hamster h = this.model.getTerrain().getDefaultHamster();
		this.drawHamster(g, 0, h.getX(), h.getY(), h.getDir(), Utils.COLOR);
		for (int i = 0; i < this.model.getHamster().size(); i++) {
			h = (Hamster) this.model.getHamster().get(i);
			// dibo
			// drawHamster(g, i + 1, h.getX(), h.getY(), h.getDir());
			if (h.getId() != -1) {
				this.drawHamster(g, h.getId() + 1, h.getX(), h.getY(), h.getDir(), h.getColor());
			}
		}
		this.tools.paint(g);
	}

	public void zoomIn() {
		this.zoom += 4;
		this.repaint();
	}

	public void zoomOut() {
		if (this.zoom > 4) {
			this.zoom -= 4;
			this.repaint();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int row = this.getRow(e.getY());
		int col = this.getCol(e.getX());
		Hamster ham = this.model.getTerrain().getDefaultHamster();
		if (row < 0 || row >= this.model.getTerrain().getHeight() || col < 0
				|| col >= this.model.getTerrain().getWidth()) {
			this.setToolTipText(null);
			return;
		}
		if (this.model.getTerrain().getWall(col, row)) {
			this.setToolTipText("<html>" + Utils.getResource("simulation.view.reihe") + ": " + row + "<br>"
					+ Utils.getResource("simulation.view.spalte") + ": " + col + "<br>"
					+ Utils.getResource("simulation.view.hamkoerner") + ": " + ham.getMouth() + "<br>"
					+ Utils.getResource("simulation.view.mauer") + "</html>");
		} else {
			this.setToolTipText("<html>" + Utils.getResource("simulation.view.reihe") + ": " + row + "<br>"
					+ Utils.getResource("simulation.view.spalte") + ": " + col + "<br>"
					+ Utils.getResource("simulation.view.koerner") + ": "
					+ this.model.getTerrain().getCornCount(col, row) + "<br>"
					+ Utils.getResource("simulation.view.hamkoerner") + ": " + ham.getMouth() + "<br>" + "</html>");
		}
	}
}