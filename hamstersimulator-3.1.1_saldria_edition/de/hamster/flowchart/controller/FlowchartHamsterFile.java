package de.hamster.flowchart.controller;

import java.io.File;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.model.HamsterFile;

/**
 * Repräsentiert eine einem Flowchart-Programm (intern) zugeordnete Datei.
 * <p>
 * </p>
 * Ob es sich bei einem Programm um ein Flowchart-Programm handelt, wird daran
 * erkannt, das die zugrunde liegende XML-Datei den Tag "<FLOWCHART>" enthält
 * 
 * @author dibo
 * 
 */
public class FlowchartHamsterFile extends HamsterFile {

	/**
	 * Die Datei muss diesen Tag enthalten
	 */
	public final static String FLOWCHART_TAG = "FLOWCHART";

	/**
	 * hier hinter verbirgt sich das eigentliche Programm
	 */
	protected FlowchartProgram program;

	// nicht veraendern!!!!
	// wird intern aufgerufen, wenn ein neues Flowchart-Programm erstellt werden
	// soll
	public FlowchartHamsterFile(char type) {
		super(type);
		this.program = new FlowchartProgram(this);
	}

	// nicht veraendern!!!!
	// wird intern aufgerufen, wenn ein abgespeichertes Flowchart-Programm
	// geladen
	// werden soll
	public FlowchartHamsterFile(File f) {
		super(f);
		if (this.file.exists()) {
			this.program = this.loadProgram(this.file);
		} else {
			this.program = new FlowchartProgram(this);
		}
	}

	// nicht veraendern!!!!
	// liefert das zugeordnete Flowchart-Programm
	public FlowchartProgram getProgram() {
		return this.program;
	}

	// nicht veraendern!!!!
	// ändert das zugeordnete Flowchart-Programm
	public void setProgram(FlowchartProgram p) {
		this.program = p;
	}

	// nicht veraendern!!!!
	// liefert die XML-Repräsentation des Flowchart-Programms
	@Override
	public String load() {
		super.load();
		if (this.file.exists()) {
			this.program = this.loadProgram(this.file);
		}
		return null;
		// return FlowchartHamsterFile.toXML(this.program);
	}

	// nicht veraendern!!!!
	// speichert das Programm ab; der Parameter wird nicht genutzt!
	@Override
	public void save(String dummy) {
		String xmlString = FlowchartHamsterFile.toXML(this.program);
		super.save(xmlString);
	}

	// nicht veraendern!!!!
	// Liest eine XML-Datei ein und liefert das darin repräsentierte
	// Flowchart-Programm
	public FlowchartProgram loadProgram(File xmlFile) {
		FlowchartProgram program = new FlowchartProgram(this);
		program.loadProgram(xmlFile);
		return program;
	}

	// diese Methode muss angepasst werden
	/**
	 * Generiert zu einem Flowchart-Programm eine XML-Repräsentation und liefert
	 * diese als String
	 * 
	 * @param program
	 *            das Flowchart-Programm
	 * @return einen String mit der XML-Repräsentation des Flowchart-Programms
	 */
	public static String toXML(FlowchartProgram program) {

		XMLStreamWriter writer = null;
		StringWriter strWriter = new StringWriter();

		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(strWriter);
			writer.writeStartDocument();
			// TODO : DTD here!
			writer.writeCharacters("\n");
			writer.writeStartElement("FLOWCHART");

			writer.writeCharacters("\n");
			program.toXML(writer);

			writer.writeEndElement();
			writer.writeEndDocument();
		} catch (Throwable exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Throwable exc) {
				exc.printStackTrace();
			}
		}
		return strWriter.toString();
	}

}
