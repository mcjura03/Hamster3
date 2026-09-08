package de.hamster.scheme.view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JTextArea;

public class PopupListener implements ActionListener {
	
	private Clipboard systemClipboard;
	private StringSelection selection; 
	private Object content;
	private Transferable transferData;
	private DataFlavor [] dataFlavor; 
	
	final static int CUT = 1;
	final static int COPY = 2;
	final static int PASTE = 3;
	
	int aktion;
	
	public PopupListener(int a) {
		this.aktion = a;
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(this.aktion == CUT) {
			selection = new StringSelection(((JTextArea) (arg0.getSource())).getSelectedText());
		    systemClipboard.setContents(selection,selection); 
		}else if(this.aktion == COPY) {
			selection = new StringSelection(((JTextArea) (arg0.getSource())).getSelectedText());
		    systemClipboard.setContents(selection,selection); 
		}else if(this.aktion == PASTE) {
			transferData = systemClipboard.getContents(null);
		      dataFlavor = transferData.getTransferDataFlavors();

		      try
		      {
		         content = transferData.getTransferData(dataFlavor[0]);
		      }
		      catch (UnsupportedFlavorException e)
		      {
		      }
		      catch (IOException e)
		      {
		      }

		      if (content != null)
		      {
		    	  ((JTextArea) (arg0.getSource())).setText(content.toString());
		      } 
		}
	}

}
