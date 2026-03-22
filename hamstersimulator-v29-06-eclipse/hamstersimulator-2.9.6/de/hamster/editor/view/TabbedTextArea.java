package de.hamster.editor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Element;

import de.hamster.editor.controller.EditorController;
import de.hamster.flowchart.FlowchartPanel;
import de.hamster.flowchart.controller.FlowchartHamsterFile;
import de.hamster.fsm.controller.FsmHamsterFile;
import de.hamster.fsm.view.FsmPanel;
import de.hamster.model.HamsterFile;
import de.hamster.scratch.ScratchPanel;
import de.hamster.workbench.Utils;

/**
 * TODO: Statuszeile schoener machen
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.6 $
 */
public class TabbedTextArea extends JPanel implements PropertyChangeListener,
		CaretListener, ChangeListener {
	protected JTabbedPane tabbedPane;

	protected HashMap textAreas;

	protected HashMap scrollPanes;

	protected JPanel statusBar;

	protected JTextField lineNumber;

	protected JTextField colNumber;

	protected EditorController controller;

	private boolean locked = false; // dibo 290710

	public TabbedTextArea(EditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		this.textAreas = new HashMap();
		this.scrollPanes = new HashMap();
		this.tabbedPane = new JTabbedPane();
		this.add(BorderLayout.CENTER, this.tabbedPane);
		this.tabbedPane.addChangeListener(controller);
		this.tabbedPane.addChangeListener(this);

		this.statusBar = new JPanel();
		this.statusBar.setLayout(new GridLayout(1, 0, 3, 3));
		this.statusBar.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		this.statusBar.add(new JLabel(""));
		this.statusBar.add(new JLabel(""));
		this.statusBar.add(new JLabel(""));
		this.statusBar.add(new JLabel(""));

		JTextField line = new JTextField(Utils.getResource("editor.zeile"));
		line.setEditable(false);
		this.statusBar.add(line);

		this.lineNumber = new JTextField("1");
		this.lineNumber.setEditable(false);
		this.statusBar.add(this.lineNumber);

		JTextField col = new JTextField(Utils.getResource("editor.spalte"));
		col.setEditable(false);
		this.statusBar.add(col);

		this.colNumber = new JTextField("1");
		this.colNumber.setEditable(false);
		this.statusBar.add(this.colNumber);

		this.add(BorderLayout.SOUTH, this.statusBar);
	}

	public void refreshFiles() {
		for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
			TextArea textArea = this.getTextArea(i);
			HamsterFile file = textArea.getFile();
			textArea.refreshFile();
		}
	}

	// dibo 230309
	public void changeFontSize(int size) {
		for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
			TextArea textArea = this.getTextArea(i);
			textArea.changeFontSize(size);
			textArea.getLineNumberPanel().changeFontSize(size);
		}
	}

	public String getText() {
		if (this.tabbedPane.getTabCount() == 0) {
			return null;
		}
		return this.getActiveTextArea().getText();
	}

	public boolean ensureSaved(HamsterFile file) {
		if (file.isDirectory()) {
			HamsterFile[] files = file.getChildren();
			for (int i = 0; i < files.length; i++) {
				if (!this.ensureSaved(files[i])) {
					return false;
				}
			}
		} else {
			if (this.getTextArea(file) != null) {
				if (file.isModified()) {
					this.openFile(file);
					int val = Utils.confirm(this, "editor.dialog.save");
					if (val == Utils.YES) {
						if (!file.exists()) {
							boolean v = this.controller.saveAs(file.getName());
							if (!v) {
								return false;
							}
						} else {
							this.getActiveTextArea().save();
						}
					} else if (val == Utils.CANCEL) {
						return false;
					} else if (val == Utils.NO) {
						return false; // dibo 15.11.2010
					}
				}
			}
		}
		return true;
	}

	public boolean ensureSaved2(HamsterFile file) { // dibo 17.11.2010
		if (file.isDirectory()) {
			HamsterFile[] files = file.getChildren();
			for (int i = 0; i < files.length; i++) {
				if (!this.ensureSaved2(files[i])) {
					return false;
				}
			}
		} else {
			if (this.getTextArea(file) != null) {
				if (file.isModified()) {
					this.openFile(file);
					int val = Utils.confirm(this, "editor.dialog.save");
					if (val == Utils.YES) {
						if (!file.exists()) {
							boolean v = this.controller.saveAs(file.getName());
							if (!v) {
								return false;
							}
						} else {
							this.getActiveTextArea().save();
						}
					} else if (val == Utils.CANCEL) {
						return false;
					} else if (val == Utils.NO) {
						return true; // diff
					}
				}
			}
		}
		return true;
	}

	public TextArea openFile(HamsterFile file) {
		if (this.isLocked()) {
			return null;
		}
		if (this.textAreas.get(file) != null) {
			TextArea h = (TextArea) this.textAreas.get(file);
			this.tabbedPane.setSelectedIndex(this.getIndex(h));
			return h;
		} else {
			TextArea h = null;
			JScrollPane scrollPane = null;

			if (file.getType() == HamsterFile.SCRATCHPROGRAM) {
				ScratchPanel p = new ScratchPanel();
				h = new TextArea(this, this.controller, file, p);
				p.setTextArea(h);
				p.unmodified();
				scrollPane = new JScrollPane(h.getScratchPanel());
			} else if (file.getType() == HamsterFile.FSM) {
				FsmPanel p = new FsmPanel((FsmHamsterFile) file);
				h = new TextArea(this, this.controller, file, p);
				p.setTextArea(h);
				p.unmodified();
				scrollPane = new JScrollPane(h.getFSMPanel());
			} else if (file.getType() == HamsterFile.FLOWCHART) {
				FlowchartPanel p = new FlowchartPanel(
						(FlowchartHamsterFile) file);
				h = new TextArea(this, this.controller, file, p);
				p.setTextArea(h);
				p.unmodified();
				scrollPane = new JScrollPane(h.getFlowchartPanel());
			} else {
				h = new TextArea(this, this.controller, file);
				scrollPane = new JScrollPane(h);
				scrollPane.setRowHeaderView(h.getLineNumberPanel());
			}

			scrollPane.setBorder(BorderFactory.createEmptyBorder());

			h.addCaretListener(this);
			h.getDocument().addDocumentListener(this.controller);
			file.addPropertyChangeListener(this);
			this.textAreas.put(file, h);

			this.scrollPanes.put(h, scrollPane);

			h.setEditable(!file.isLocked());
			if (file.isLocked()) {
				this.tabbedPane.addTab(file.getName(),
						Utils.getIcon("Play16.gif"), scrollPane);
				h.setBackground(this.getBackground());
			} else {
				this.tabbedPane.addTab(file.getName(), scrollPane);
			}
			this.tabbedPane.setSelectedComponent(scrollPane);

			return h;
		}
	}

	public void closeFile(HamsterFile file) {
		if (this.isLocked()) {
			return; // dibo 290710
		}
		if (file.isDirectory()) {
			HamsterFile[] files = file.getChildren();
			for (int i = 0; i < files.length; i++) {
				this.closeFile(files[i]);
			}
		} else {
			TextArea h = (TextArea) this.textAreas.get(file);
			if (h == null) {
				return;
			}
			file.removePropertyChangeListener(this);
			JScrollPane s = (JScrollPane) this.scrollPanes.get(h);
			s.setViewportView(null);
			this.tabbedPane.removeTabAt(this.getIndex(h));
			this.textAreas.remove(file);
			this.scrollPanes.remove(h);

			// Workaround fuer JTabbedPane-Fehler, wenn bei zwei Tabs der
			// vordere
			// geschlossen wird, wird kein Event gefeuert.
			this.controller.stateChanged(null);
		}
	}

	public TextArea getActiveTextArea() {
		if (this.tabbedPane.getSelectedComponent() == null) {
			return null;
		}
		return this.getTextArea(this.tabbedPane.getSelectedIndex());
	}

	public HamsterFile getActiveFile() {
		if (this.getActiveTextArea() == null) {
			return null;
		}
		return this.getActiveTextArea().getFile();
	}

	public void setFile(TextArea textArea, HamsterFile file) {
		textArea.setFile(file);
		int index = this.getIndex(textArea);
		this.tabbedPane.setIconAt(index, null);
		this.tabbedPane.setTitleAt(index, file.getName());
	}

	// dibo 290710
	public void lock(boolean locked) {
		// for (int i=0; i<this.tabbedPane.getTabCount(); i++) {
		// this.tabbedPane.setEnabledAt(i, !locked);
		// }
		// this.locked = locked;
	}

	public boolean isLocked() {
		// return this.locked;
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		HamsterFile file = (HamsterFile) evt.getSource();
		TextArea textArea = (TextArea) this.textAreas.get(file);
		int index = this.getIndex(textArea);
		if (evt.getPropertyName() == HamsterFile.MODIFIED) {
			if (((Boolean) evt.getNewValue()).booleanValue()) {
				this.tabbedPane.setIconAt(index, Utils.getIcon("save64.png"));
			} else {
				this.tabbedPane.setIconAt(index, null);
			}
		} else if (evt.getPropertyName() == HamsterFile.LOCKED) {
			if (((Boolean) evt.getNewValue()).booleanValue()) {"play64.png"));
				textArea.setEditable(false);
				textArea.setBackground(new Color(230, 230, 230)); // getBackground());
				this.lock(true);
			} else {
				this.tabbedPane.setIconAt(index, null);
				textArea.setEditable(true);
				textArea.removeLineHighlight();
				textArea.setBackground(Color.WHITE);
				this.lock(false);
			}
		}
	}

	// dibo
	public void propertyChange(HamsterFile file, boolean locked) {
		// Scratch
		TextArea textArea = (TextArea) this.textAreas.get(file);
		if (textArea == null) {
			return;
		}
		if (textArea.isScratch()) {
			ScratchPanel panel = textArea.getScratchPanel();
			panel.setLocked(locked);
		} else if (textArea.isFSM()) {
			FsmPanel panel = textArea.getFSMPanel();
			panel.setLocked(locked);
		} else if (textArea.isFlowchart()) {
			FlowchartPanel panel = textArea.getFlowchartPanel();
			panel.setLocked(locked);
		} else {
			if (locked) {
				textArea.setEditable(false);
				textArea.setBackground(new Color(230, 230, 230)); // getBackground());
			} else {
				textArea.setEditable(true);
				textArea.removeLineHighlight();
				textArea.setBackground(Color.WHITE);
			}
		}
		this.lock(locked);
	}

	private TextArea getTextArea(int index) {
		JScrollPane scrollPane = (JScrollPane) this.tabbedPane
				.getComponentAt(index);
		Component view = scrollPane.getViewport().getView();
		if (view instanceof ScratchPanel) {
			return ((ScratchPanel) view).getTextArea();
		} else if (view instanceof FsmPanel) {
			return ((FsmPanel) view).getTextArea();
		} else if (view instanceof FlowchartPanel) {
			return ((FlowchartPanel) view).getTextArea();
		} else {
			return (TextArea) view;
		}
	}

	public TextArea getTextArea(HamsterFile file) {
		return (TextArea) this.textAreas.get(file);
	}

	private int getIndex(TextArea textArea) {
		return this.tabbedPane.indexOfComponent((JScrollPane) this.scrollPanes
				.get(textArea));
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		this.updateStatusBar();
	}

	public void updateStatusBar() {
		TextArea t = this.getActiveTextArea();
		if (t == null) {
			return;
		}
		int dot = t.getCaretPosition();
		Element element = t.getDocument().getDefaultRootElement();
		int line = element.getElementIndex(dot);
		int col = dot - element.getElement(line).getStartOffset();
		this.lineNumber.setText(line + 1 + "");
		this.colNumber.setText(col + 1 + "");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateStatusBar();
	}
}