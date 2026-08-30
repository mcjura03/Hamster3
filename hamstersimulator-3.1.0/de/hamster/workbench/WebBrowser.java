package de.hamster.workbench;

import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;

public class WebBrowser extends JEditorPane implements HyperlinkListener {
	WebBrowser(String title, String url) {
		try {
			setEditable(false);
			addHyperlinkListener(this);
			setPage(new URL(url));
			JFrame f = new JFrame(title);
			f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			f.setLocation(10, 10);
			f.setSize(1000, 800);
			f.add(new JScrollPane(this));
			f.setVisible(true);
		} catch (Throwable e) {
			// e.printStackTrace();
			return;
		}
	}
	
	public WebBrowser(String title, URL url) {
		try {
			this.setEditable(false);
			this.addHyperlinkListener(this);
			this.setPage(url);
			JFrame f = new JFrame(title);
			f.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			f.setLocation(10, 10);
			f.setSize(800, 600);
			f.add(new JScrollPane(this));
			f.setVisible(true);
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		HyperlinkEvent.EventType typ = event.getEventType();
		if (typ == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				setPage(event.getURL());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Invalid Link: "
						+ event.getURL().toExternalForm(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}