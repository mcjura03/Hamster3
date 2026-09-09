package de.hamster.prolog.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import de.hamster.prolog.controller.PrologController;

public class PrologKonsoleListener implements ActionListener, MouseListener, KeyListener, WindowListener,
		ComponentListener
{	
	/*
	 * Schalter zum Ignorieren der Eingabe bestimmer Zeichen (Tasten).
	 */
	private boolean consumeKeyEvent;	

	public void actionPerformed(ActionEvent arg0)
	{
		if(arg0.getActionCommand().equals("listing"))
		{
			PrologKonsole.get().execute("listing.\n", "listing.\n");			
		}
		else if(arg0.getActionCommand().equals("trace"))
		{
			String command = PrologKonsole.get().isTracing() ? "notrace.\n" : "trace.\n";  
			PrologKonsole.get().execute(command, command);
		}
		else if(arg0.getActionCommand().equals("debug"))
		{
			String command = PrologKonsole.get().isDebugging() ? "nodebug.\n" : "debug.\n";  
			PrologKonsole.get().execute(command, command);
		}
		else if(arg0.getActionCommand().equals("creep"))
		{
			PrologKonsole.get().execute("creep.\n", "creep\n");
		}
		else if(arg0.getActionCommand().equals("skip"))
		{
			PrologKonsole.get().execute("skip.\n", "skip\n");		
		}
		else if(arg0.getActionCommand().equals("retry"))
		{
			PrologKonsole.get().execute("rertry.\n","retry\n");
		}
		else if(arg0.getActionCommand().equals("fail"))
		{
			PrologKonsole.get().execute("fail.\n","fail.\n");
		}
		else if(arg0.getActionCommand().equals("abort"))
		{
			PrologKonsole.get().execute("abort.\n","abort.\n");
		}
	}

	public void mouseClicked(MouseEvent arg0)
	{
		int endPos = PrologKonsole.get().getTextArea().getDocument().getEndPosition().getOffset();
		PrologKonsole.get().getTextArea().setCaretPosition(endPos-1);
	}

	public void mouseEntered(MouseEvent arg0){}

	public void mouseExited(MouseEvent arg0){}

	public void mousePressed(MouseEvent arg0){}

	public void mouseReleased(MouseEvent arg0){}

	public void keyPressed(KeyEvent arg0)
	{
		Element rootElem = PrologKonsole.get().getTextArea().getDocument().getDefaultRootElement();		
		int caretPos = PrologKonsole.get().getTextArea().getCaretPosition();
		int caretPosLineIndex = rootElem.getElementIndex(caretPos);
		int caretPosLineStartOffset = rootElem.getElement(caretPosLineIndex).getStartOffset();
		int lastComTextPos = PrologKonsole.get().getLastComTextPos();
		int lastComPosLineIndex = rootElem.getElementIndex(lastComTextPos);
		int lastComPosLineStartOffset = rootElem.getElement(lastComPosLineIndex).getStartOffset();
		int docEndPos = PrologKonsole.get().getTextArea().getDocument().getEndPosition().getOffset();
		int docEndPosLineIndex = rootElem.getElementIndex(docEndPos);
		
		char keyChar = arg0.getKeyChar();
		String lastAddedLine = PrologKonsole.get().getLastAddedLine().trim();
		String[] last2Lines = PrologKonsole.get().getLastLines(2);
		String lastLine0 = last2Lines[0];
		String userInput = PrologKonsole.get().getUserInput();
		
		boolean isTracing = PrologKonsole.get().isTracing();
		boolean isTraceGoalSetted = PrologKonsole.get().isTraceGoalIsSetted();
		
		boolean isCommandCorrection = lastLine0.contains("Correct to:") ||
			lastLine0.contains("'y' or 'n'");
		
		boolean isBasicUnificationActionQuery = !lastAddedLine.contains("?-") && 
			!lastAddedLine.contains(".") && userInput.length() == 0 &&
			(lastAddedLine.contains("=") || lastAddedLine.contains("true") ||
				lastAddedLine.contains("false"))
			|| lastAddedLine.contains("Action?");

		
		
		
		
		/*
		 *  Schreiben in den Text (oberhalb der lastComPosLineIndex-Marke) 
		 *  soll unterbunden werden..
		 */
		if(caretPosLineIndex < lastComPosLineIndex)
		{
			consumeKeyEvent = true;
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_HOME)
		{
			if(caretPosLineIndex == lastComPosLineIndex)
			{
				PrologKonsole.get().getTextArea().setCaretPosition(lastComTextPos);
				if(arg0.isShiftDown())
					PrologKonsole.get().getTextArea().select(lastComTextPos, caretPos);
					
				consumeKeyEvent = true;
			}
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			if(caretPos == lastComTextPos)
			{
				consumeKeyEvent = true;
			}
			if(PrologKonsole.get().getTextArea().getSelectionStart() < lastComTextPos)
			{
				consumeKeyEvent = true;
			}
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_DELETE)
		{
			if(PrologKonsole.get().getTextArea().getSelectionStart() < lastComTextPos)
			{
				consumeKeyEvent = true;
			}
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_UP)
		{						
			if(caretPosLineIndex == lastComPosLineIndex)
			{
				consumeKeyEvent = true;
				String currentCommand = PrologKonsole.get().getCurrentHistoryCommand();
				if( (userInput.equals("") || userInput.equals(currentCommand)) &&
					PrologKonsole.get().getLastAddedLine().contains("?-"))
				{
					PrologKonsole.get().showHistoryCommand(
							PrologKonsole.get().getNextCommand());
				}
			}
			else if(caretPosLineIndex-1 == lastComPosLineIndex)
			{
				int lastComPosIndent = lastComTextPos - lastComPosLineStartOffset;
				int caretPosIndent = caretPos - caretPosLineStartOffset;
				if(caretPosIndent < lastComPosIndent)
				{
					PrologKonsole.get().getTextArea().setCaretPosition(lastComTextPos);
					consumeKeyEvent = true;
				}
			}
			else if(caretPosLineIndex < lastComPosLineIndex)
			{
				PrologKonsole.get().getTextArea().setCaretPosition(lastComTextPos);
				consumeKeyEvent = true;
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_DOWN)
		{
			if(caretPosLineIndex == docEndPosLineIndex)
			{
				consumeKeyEvent = true;
				String currentCommand = PrologKonsole.get().getCurrentHistoryCommand();
				if( (userInput.length() == 0 || userInput.equals(currentCommand)) &&
					PrologKonsole.get().getLastAddedLine().contains("?-") )
				{
					PrologKonsole.get().showHistoryCommand(
							PrologKonsole.get().getPreviousCommand());
				}
			}
			else if(caretPosLineIndex < lastComPosLineIndex)
			{
				PrologKonsole.get().getTextArea().setCaretPosition(lastComTextPos);
				consumeKeyEvent = true;
			}
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_LEFT)
		{
			if(caretPos == lastComTextPos)
			{
				consumeKeyEvent = true;
			}
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
		{
			int befehlsLaenge = docEndPos - lastComTextPos;
			String befehl = "";
			try
			{
				if(befehlsLaenge >= 0)
				{
					befehl = PrologKonsole.get().getTextArea().
						getText(lastComTextPos,befehlsLaenge).trim();
					// Entferne Zeilenumbrüche..
					befehl = befehl.replace("\n", " ");
				}
			}
			catch (BadLocationException e1){}
			
			if(PrologKonsole.get().isTracing())
			{
				if(PrologKonsole.get().getLastAddedLine().contains("|:"))
				{					
					if(befehl.endsWith("."))
					{
						PrologKonsole.get().execute(befehl+"\n", "\n");
						consumeKeyEvent = true;
					}
				}
				// Tracing Aktionen: creep..
				else if(PrologKonsole.get().getLastAddedLine().contains("?"))
				{
					if(befehl.length() > 0)
					{
						PrologKonsole.get().execute(befehl+"\n", null);
					}
					else
					{
						if(!PrologController.get().isWaitingForPrologAnswer())
						{
							PrologController.get().setWaitingForPrologAnswer(true);
							PrologKonsole.get().execute("c.\n", "creep\n");				
							consumeKeyEvent = true;
						}
						else
						{
							//System.out.println("wait for prolog answer..");
							consumeKeyEvent = true;
						}
					}					
				}
				// Aktionen bei der Unifikation..
				else if(PrologKonsole.get().getLastLines(1)[0].contains("=") || 
						PrologKonsole.get().getLastLines(1)[0].endsWith("true") || 
						PrologKonsole.get().getLastLines(1)[0].endsWith("false") ||
						PrologKonsole.get().getLastLines(1)[0].contains("Action?"))
				{						
					PrologKonsole.get().execute("\n", ".");
					consumeKeyEvent = true;
					PrologKonsole.get().setTracing(false);
					PrologKonsole.get().getHelperThread().setEndOfQuery();
				}
				// Normal-Modus.. Befehl+"."+Enter
				else
				{
					if(befehl.endsWith("."))
					{
						PrologKonsole.get().execute(befehl+"\n", null);
					}
				}
			}
			// Kein Tracing Modus..
			else
			{
				// Normal-Modus.. Befehl+"."+Enter
				if(befehl.length() > 0)
				{
					if(befehl.endsWith("."))
					{
						PrologKonsole.get().execute(befehl+"\n", null);
					}
				}
				// Aktionen bei der Unifikation..
				else
				{
					if( PrologKonsole.get().getLastLines(1)[0].contains("=") || 
						PrologKonsole.get().getLastLines(1)[0].endsWith("true") || 
						PrologKonsole.get().getLastLines(1)[0].endsWith("false") ||
						PrologKonsole.get().getLastLines(1)[0].contains("Action?"))
					{
						PrologKonsole.get().execute("\n", ".");
						consumeKeyEvent = true;
						PrologKonsole.get().getHelperThread().setEndOfQuery();
					}
					// 'leap' im trace modus - anomalie..
					else if(PrologKonsole.get().getLastLines(1)[0].endsWith("?"))
					{						
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("c.\n", "creep\n");				
						consumeKeyEvent = true;
					}
				}
			}
		}
		/*
		 * 'Ctrl+D' kann dazu verwendet werden, den PrologProzess über die 
		 * Konsole neu zu starten.
		 */
		else if(arg0.getKeyCode() == KeyEvent.VK_D && arg0.isControlDown())
		{
			PrologKonsole.get().execute("end_of_file.\n", null);
		}
		
		// Anfrage zur Befehls-Korrekur..
		else if(isCommandCorrection)
		{
			if(Character.isLetter(keyChar))
			{
				String taste = arg0.getKeyChar()+"";
				if(taste.startsWith("y"))	// yes
				{
					PrologKonsole.get().execute("y\n", "yes\n");
					consumeKeyEvent = true;
				}
				else if(taste.startsWith("n"))	// no
				{
					PrologKonsole.get().execute("n\n", "no\n");
					consumeKeyEvent = true;					
				}
				// Alle anderen Tasten in diesem Fall ignorieren..
				else if(arg0.getKeyCode() != KeyEvent.VK_SHIFT &&
						  arg0.getKeyCode() != KeyEvent.VK_CONTROL &&
						  arg0.getKeyCode() != KeyEvent.VK_ALT)
				{
					// Einblenden, nur wenn nicht bereits geschehen ist..
					if(!PrologKonsole.get().getLastLines(1)[0].contains("'y' or 'n'?"))
					{
						PrologKonsole.get().execute("#\n", "\n");
					}
					consumeKeyEvent = true;
				}
			}
			// Alle anderen Tasten, besonders wenn es keine Buchstaben sind, ignorieren..
			else
			{
				consumeKeyEvent = true;
			}
		}
		
		
		// Eingabe einer der vordefinierten Actions wird erwartet.
		else if(isBasicUnificationActionQuery && !isTracing)							
		{					
			if(keyChar == ';' || keyChar == 'n' || keyChar == 'r' || keyChar == ' ' || keyChar == '\t')
			{
				PrologKonsole.get().execute(";\n", ";\n");
				consumeKeyEvent = true;
			}
			else if(keyChar == 'b') // break
			{
				PrologKonsole.get().execute("b\n", "\n");
				consumeKeyEvent = true;
			}
			else if(keyChar == 'w') // write
			{
				PrologKonsole.get().execute("w\n", " [write]\n");
				consumeKeyEvent = true;
			}
			else if(keyChar == 'p') // print
			{
				PrologKonsole.get().execute("p\n", " [print]\n");
				consumeKeyEvent = true;
			}
			else if(keyChar == 'h' || keyChar == '?') // help
			{
				PrologKonsole.get().execute(keyChar+"\n", " "+keyChar+" ");
				consumeKeyEvent = true;
			}
			else if(keyChar == 't') // trace
			{	
				PrologKonsole.get().setTraceGoalIsSetted(true);					
				PrologKonsole.get().execute("t\n", " [trace]\n");
				consumeKeyEvent = true;
			}
			else if(keyChar == 'c' || keyChar == 'a' || 
					arg0.getKeyCode() == KeyEvent.VK_ENTER) // exit
			{
				PrologKonsole.get().execute("c\n", ".\n");
				consumeKeyEvent = true;
				PrologKonsole.get().getHelperThread().setEndOfQuery();
			}
			else if(arg0.getKeyCode() != KeyEvent.VK_SHIFT &&
					arg0.getKeyCode() != KeyEvent.VK_CONTROL &&
					arg0.getKeyCode() != KeyEvent.VK_BACK_SPACE &&
					arg0.getKeyCode() != KeyEvent.VK_ALT &&
					arg0.getKeyCode() != KeyEvent.VK_UP &&
					arg0.getKeyCode() != KeyEvent.VK_RIGHT)
			{
				if(!PrologKonsole.get().getLastLines(1)[0].contains("Action?"))
				{
					PrologKonsole.get().execute(keyChar+"\n", " ");						
				}
				consumeKeyEvent = true;
			}
		}
		
		// ## Bisher alle Tasten einzeln betrachtet, ohne Beachtung der Modien
		
		/*
		 * Schnelle Eingabe der Befehle im Trace-Modus über die Befehls-Kürzel. 
		 * (Nur der erste Buchstabe wird gelesen, und falls dieser den Beginn
		 * eines vordefinierten Befehls darstellt, wird dieser Befehl aufgerufen.)
		 */
		else if(isTracing && isTraceGoalSetted)
		{			
			// Interaktive Eingabe im Tracing-Modus.
			if(lastAddedLine.startsWith("|:"))
			{
				// tue nichts.. lass den Benutzer seine ausgabe machen..
			}
			// Befehle im Tracing-Modus.
			else if(lastAddedLine.endsWith("?") && 
				(Character.isLetter(keyChar) || keyChar == '?' || keyChar == '.' || 
					keyChar == '+' || keyChar == '-'))
			{
				if(!PrologController.get().isWaitingForPrologAnswer())
				{
					String taste = arg0.getKeyChar()+"";				
					
					// Behandle den 'spy | no spy | repeat find'-Befehl
					if(keyChar == '+' || keyChar == '-' || keyChar == '.')
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute(keyChar+"\n", keyChar+"\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'abort'-Befehl
					else if(taste.startsWith("a"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("a\n", "abort\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'break'-Befehl
					else if(taste.startsWith("b"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("b\n", "break\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'depth'-Befehl
					else if(taste.startsWith("d"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("d\n", "depth\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'fail'-Befehl
					else if(taste.startsWith("f"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("f\n", "fail\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'help'-Befehl
					else if(taste.startsWith("h") || keyChar == '?')
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("h\n", "help\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'leap'-Befehl
					else if(taste.startsWith("l"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("l\n", "leap\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'no debug'-Befehl
					else if(taste.startsWith("n"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("n\n", "no debug\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'retry'-Befehl
					else if(taste.startsWith("r"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("r\n", "retry\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'up'-Befehl
					else if(taste.startsWith("u"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("u\n", "up\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'exception details'-Befehl
					else if(taste.startsWith("m"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("m\n", "exception details\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'toggle show context'-Befehl
					else if(taste.startsWith("C"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						String meldung = "Show context";
						if(lastAddedLine.contains("["))
							meldung = "No show context";
						PrologKonsole.get().execute("C\n", meldung+"\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'Alternatives'-Befehl
					else if(taste.startsWith("A"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("A\n", "alternatives\n");
						consumeKeyEvent = true;
					}								
					// Behandle den 'creep'-Befehl
					else if(taste.startsWith("c"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("c\n", "creep\n");
						consumeKeyEvent = true;				
					}
					// Behandle den 'exit'-Befehl
					else if(taste.startsWith("e"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("e\n", "exit\n");
						consumeKeyEvent = true;				
					}
					// Behandle den 'goals (backtrace)'-Befehl
					else if(taste.startsWith("g"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("g\n", "goals\n");
						consumeKeyEvent = true;				
					}
					// Behandle den 'ignore'-Befehl
					else if(taste.startsWith("i"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("i\n", "ignore\n");
						consumeKeyEvent = true;				
					}
					// Behandle den 'listing'-Befehl
					else if(taste.startsWith("L"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("L\n", "listing\n");
						consumeKeyEvent = true;				
					}
					// Behandle den 'print'-Befehl
					else if(taste.startsWith("p"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("p\n", "print\n");
						consumeKeyEvent = true;				
					}				
					// Behandle den 'skip'-Befehl
					else if(taste.startsWith("s"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("s\n", "skip\n");
						consumeKeyEvent = true;
					}
					// Behandle den 'write'-Befehl
					else if(taste.startsWith("w"))
					{
						PrologController.get().setWaitingForPrologAnswer(true);
						PrologKonsole.get().execute("w\n", "write\n");
						consumeKeyEvent = true;				
					}
				}
			}
		}
		// Alles andere, was bis hierhin nicht abgefangen wurde..
		else
		{
			//System.out.println("letzter else fall..");
		}
		
		if(consumeKeyEvent)
		{
			arg0.consume();
		}
	}
	
	public void keyTyped(KeyEvent arg0)
	{
		if(consumeKeyEvent)
		{
			arg0.consume();
		}
	}

	public void keyReleased(KeyEvent arg0)
	{
		if(consumeKeyEvent)
		{
			arg0.consume();
			consumeKeyEvent = false;
		}
	}

	public void windowActivated(WindowEvent arg0){}

	public void windowClosed(WindowEvent arg0){}

	public void windowClosing(WindowEvent arg0)
	{
		PrologKonsole.get().hidePrologKonsole();
	}

	public void windowDeactivated(WindowEvent arg0){}

	public void windowDeiconified(WindowEvent arg0){}

	public void windowIconified(WindowEvent arg0){}

	public void windowOpened(WindowEvent arg0){}

	public void componentHidden(ComponentEvent arg0){}
	
	public void componentMoved(ComponentEvent arg0){}

	public void componentResized(ComponentEvent arg0){}

	public void componentShown(ComponentEvent arg0)
	{
		PrologKonsole.get().getTextArea().requestFocusInWindow();
	}
}
