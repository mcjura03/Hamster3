package de.hamster.simulation.model;

/**
 * @author Daniel
 */
public interface LogSink {
	public void logEntry(LogEntry logEntry);
	public void clearLog();
}
