package de.hamster.simulation.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.hamster.model.HamsterInstruction;
import de.hamster.simulation.model.LogEntry;
import de.hamster.simulation.model.LogSink;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class LogPanel extends JPanel implements LogSink {
	JTextPane textPane;

	private StyledDocument document;

	public LogPanel() {
		super(new BorderLayout());

		textPane = new JTextPane();
		textPane.setEditable(false);
		document = (StyledDocument) textPane.getDocument();

		for (int i = -2; i < Utils.COLORS.length - 1; i++) {
			document.addStyle("hamster" + i, document.getStyle("default"));
			StyleConstants.setBold(document.getStyle("hamster" + i), true);
		}

		StyleConstants.setForeground(document.getStyle("hamster-1"),
				Utils.COLORS[0]);
		for (int i = 0; i < Utils.COLORS.length - 1; i++) {
			StyleConstants.setForeground(document.getStyle("hamster" + i),
					Utils.COLORS[i + 1].darker());
		}
		JPanel buffer = new JPanel(new BorderLayout());
		buffer.add(BorderLayout.CENTER, textPane);
		add(BorderLayout.CENTER, new JScrollPane(buffer));
	}

	public void logEntry(LogEntry logEntry) {
		String text = logEntry.getInstruction().toString();
		if (text == null || text.equals("") || logEntry.getResult() == null)
			return;
		if (text.contains("$_dibo_intern$")) {
			return;
		}
		if (text.contains("$_dibo_p_intern$")) {
			return;
		}
		if (!logEntry.getResult().equals("ok")) {
			text += " : " + logEntry.getResult();
		} else {
			text += ";"; // dibo 31.01.2007
		}
		StyledDocument d = (StyledDocument) textPane.getDocument();

		int color = -2;
		SimulationModel model = de.hamster.workbench.Workbench.getWorkbench()
				.getSimulationController().getSimulationModel();
		int hid = -3;
		try {
			int i = -2;
			if (logEntry.getInstruction() instanceof HamsterInstruction) {
				HamsterInstruction hi = (HamsterInstruction) logEntry
						.getInstruction();
				i = Math.min(hi.getHamster(), Utils.COLORS.length - 2);

				hid = hi.getHamster();
				if (model.getHamster(hid) == null)
					return;
				color = model.getHamster(hid).getColor();
				if (hid == -1) {
					color = model.getHamster(hid).getColor() - 1;
				} else if (color <= -1) {
					color = i;
				} else if (color >= Utils.COLORS.length) {
					color = Utils.COLORS.length - 2;
				} else {
					color = color - 1;
				}

			}

			// dibo 27.10.2006
			int offset = d.getEndPosition().getOffset() - 1;
			if (offset > 20000) {
				String str = d.getText(0, 300);
				int end = str.indexOf('\n');
				if (end <= 0)
					end = 10;
				d.remove(0, end + 1);
			}
			offset = d.getEndPosition().getOffset() - 1;
			// d.insertString(offset, text + "\n", d.getStyle("hamster" + i));
			d.insertString(offset, text + "\n", d.getStyle("hamster" + color));
			textPane.setCaretPosition(d.getEndPosition().getOffset() - 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void clearLog() {
		StyledDocument d = (StyledDocument) textPane.getDocument();
		try {
			d.remove(0, d.getEndPosition().getOffset());
		} catch (Exception e) {
		}
		textPane.setText("");
	}

	public void logEntry(String text, String result, boolean hamsterIns, int id) {
		if (text.equals("") || result == null)
			return;
		if (!result.equals("ok"))
			text += " : " + result;
		StyledDocument d = (StyledDocument) textPane.getDocument();

		int color = -2;
		SimulationModel model = de.hamster.workbench.Workbench.getWorkbench()
				.getSimulationController().getSimulationModel();
		int hid = -3;
		try {
			int i = -2;
			if (hamsterIns) {
				i = Math.min(id, Utils.COLORS.length - 2);

				hid = id;
				if (model.getHamster(hid) == null)
					return;
				color = model.getHamster(hid).getColor();
				if (hid == -1) {
					color = model.getHamster(hid).getColor() - 1;
				} else if (color <= -1) {
					color = i;
				} else if (color >= Utils.COLORS.length) {
					color = Utils.COLORS.length - 2;
				} else {
					color = color - 1;
				}
			}

			// dibo 27.10.2006
			int offset = d.getEndPosition().getOffset() - 1;
			if (offset > 20000) {
				String str = d.getText(0, 300);
				int end = str.indexOf('\n');
				if (end <= 0)
					end = 10;
				d.remove(0, end + 1);
			}
			offset = d.getEndPosition().getOffset() - 1;
			// d.insertString(offset, text + "\n", d.getStyle("hamster" + i));
			d.insertString(offset, text + "\n", d.getStyle("hamster" + color));
			textPane.setCaretPosition(d.getEndPosition().getOffset() - 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}