package de.hamster.prolog.controller;

import java.io.InputStream;
import java.util.Stack;

/**
 * Diese Klasse liest Zeilenweise die Ausgaben des Prolog-Prozesses und leitet
 * diese an den {@link PrologController} weiter. Dabei wird für jede
 * Ausgabezeile die Methode {@link PrologController#handlePrologOutput}
 * aufgerufen.
 * 
 * @author Andreas Schäfer
 */
public class PrologOutputReader extends Thread
{
	private InputStream inputStream;
	private boolean terminate;

	private boolean userInput;

	public PrologOutputReader(InputStream is)
	{
		this.inputStream = is;
		this.terminate = false;
		this.userInput = false;
	}

	public void run()
	{
		this.setPriority(MIN_PRIORITY);
		String lastHandledOutputLine = "";

		while (!this.terminate)
		{
			try
			{
				if(inputStream.available() > 0)
				{
					byte abyte0[] = new byte[inputStream.available()];
					inputStream.read(abyte0);
					String bufferedString = new String(abyte0);					
					String[] lines = bufferedString.split("\\n");

					// Splitte von Prolog automatisch konkatenierten Linien..
					lines = splitMergedLines(lines);
					
					for (int i = 0; i < lines.length; i++)
					{
						//System.out.println("LINE_"+(i+1)+" of "+lines.length);
						
						lines[i] = lines[i].trim();
						
						// Ignoriere leere Zeilen..
						if(lines[i].length() == 0)
							continue;

						boolean hamsterBefehl = lines[i].contains("prologhamster:") && 
							!lines[i].contains("write");

						boolean doppelteZeile = lines[i].equals(lastHandledOutputLine) && 
							!hamsterBefehl && !isUserInputOccured();
						
						setUserInputOccured(false);

						if(!doppelteZeile)
						{
							/*
							 * Prüfe ob ein HamsterBefehl mit dabei ist. Falls ja, behandle 
							 * ihn gesondert..
							 */
							String line = lines[i];
							if(line.contains("prologhamster:"))
							{
								// Zeichnekette vom Hamsterbefehl..
								String part1 = line.substring(0, line.indexOf("prologhamster:"));
								// Zeichenkette ab dem Hamsterbefehl..
								String part2 = line.substring(line.indexOf("prologhamster:"));								
								// Prüfe nach, dass dies keine 'listing'-Ausgabe ist.
								if(!part1.contains("write"))
								{
									// Nur wenn nicht leer ist es von Bedeutung..
									if(part1.trim().length() > 0)
									{
										PrologController.get().handlePrologOutput(part1, false, part2);
									}

									String nextLine = ((i + 1) < lines.length) ? lines[i + 1] : null;
									PrologController.get().handlePrologOutput(part2, true, nextLine);
								}
								// Es ist eine Zeile der Listing-Ausgabe, kein richtiger Hamsterbefehl.
								else
								{
									String nextLine = ((i + 1) < lines.length) ? lines[i + 1] : null;
									PrologController.get().handlePrologOutput(lines[i], true, nextLine);
									PrologController.get().setWaitingForPrologAnswer(false);
								}
							}
							// Eine Zeile die defenitiv kein Hamsterbefehl enthält..
							else
							{
								String nextLine = ((i + 1) < lines.length) ? lines[i + 1] : null;
								PrologController.get().handlePrologOutput(lines[i], true, nextLine);
								//if(!hamsterBefehl) 
									PrologController.get().setWaitingForPrologAnswer(false);
							}
						}

						lastHandledOutputLine = lines[i];
					}
				}
				//System.out.println("POR alive");
				sleep(50L);
			}
			catch (Exception e)
			{
			}
		}
	}

	private String[] splitMergedLines(String[] lines)
	{
		Stack<String> lineStack = new Stack<String>();

		for (int i = 0; i < lines.length; i++)
		{
			String[] linesSplitted = lines[i].split("OTHER: ");
			if(linesSplitted.length > 1 && !lines[i].contains("severity_prefix"))
			{
				for (int j = 0; j < linesSplitted.length; j++)
				{
					if(linesSplitted[j].trim().length() > 0)
					{
						String lb = lines[i].endsWith("\n") ? "\n" : "";
						lineStack.push("OTHER: " + linesSplitted[j] + lb);
					}
				}
			}
			else
			{
				lineStack.push(lines[i]);
			}
		}
		String[] result = new String[lineStack.size()];
		for (int i = result.length - 1; i >= 0; i--)
		{
			result[i] = lineStack.pop();
		}
		return result;
	}

	public boolean isTerminate()
	{
		return terminate;
	}

	public void setTerminate(boolean terminate)
	{
		this.terminate = terminate;
	}

	public synchronized boolean isUserInputOccured()
	{
		return userInput;
	}

	public synchronized void setUserInputOccured(boolean userInput)
	{
		this.userInput = userInput;
	}
}
