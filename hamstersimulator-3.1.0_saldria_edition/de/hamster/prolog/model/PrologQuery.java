package de.hamster.prolog.model;

public class PrologQuery
{
	private static int globalQueryID = 0;
	
	private int id;
	private String command;
	private boolean result;
	
	public PrologQuery(String command)
	{
		this.command = command;
		this.id = globalQueryID++;
		this.result = false;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public boolean isResult()
	{
		return result;
	}

	public void setResult(boolean result)
	{
		this.result = result;
	}

	public String getCommand()
	{
		return command;
	}
	
	@Override
	public String toString()
	{
		return new String("querry:[id:"+id+",\n\tcommand:'"+command
			+",\n\tresult:"+result+"]");
	}

	public static int getGlobalQueryId()
	{
		return globalQueryID;
	}

	public static void setGlobalQueryId(int globalQueryId)
	{
		PrologQuery.globalQueryID = globalQueryId;
	}
}
