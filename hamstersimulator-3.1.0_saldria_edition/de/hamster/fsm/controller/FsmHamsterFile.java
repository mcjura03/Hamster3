package de.hamster.fsm.controller;

import java.io.File;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.model.HamsterFile;

/**
 * Repräsentiert eine einem FSM-Programm (intern) zugeordnete Datei.
 * <p>
 * </p>
 * Ob es sich bei einem Programm um ein FSM-Programm handelt, wird daran
 * erkannt, das die zugrunde liegende XML-Datei den Tag "<FSM>" enthält
 * 
 * @author dibo
 * 
 */
public class FsmHamsterFile extends HamsterFile {

	/**
	 * Die Datei muss diesen Tag enthalten
	 */
	public final static String FSM_TAG = "FSM";

	/**
	 * hier hinter verbirgt sich das eigentliche Programm
	 */
	protected FsmProgram program;

	// nicht veraendern!!!!
	// wird intern aufgerufen, wenn ein neues FSM-Programm erstellt werden soll
	public FsmHamsterFile(char type) {
		super(type);
		this.program = new FsmProgram(this);
	}

	// nicht veraendern!!!!
	// wird intern aufgerufen, wenn ein abgespeichertes FSM-Programm geladen
	// werden soll
	public FsmHamsterFile(File f) {
		super(f);
		if (this.file.exists()) {
			this.program = this.loadProgram(this.file);
		} else {
			this.program = new FsmProgram(this);
		}
	}

	// nicht veraendern!!!!
	// liefert das zugeordnete FSM-Programm
	public FsmProgram getProgram() {
		return this.program;
	}

	// nicht veraendern!!!!
	// �ndert das zugeordnete FSM-Programm
	public void setProgram(FsmProgram p) {
		this.program = p;
	}

	// nicht veraendern!!!!
	// liefert die XML-Repräsentation des FSM-Programms
	@Override
	public String load() {
		super.load();
		if (this.file.exists()) {
			this.program = this.loadProgram(this.file);
		}
		return FsmHamsterFile.toXML(this.program);
	}

	// nicht veraendern!!!!
	// speichert das Programm ab; der Parameter wird nicht genutzt!
	@Override
	public void save(String dummy) {
		String xmlString = FsmHamsterFile.toXML(this.program);
		super.save(xmlString);
	}

	// nicht veraendern!!!!
	// Liest eine XML-Datei ein und liefert das darin repräsentierte
	// FSM-Programm
	public FsmProgram loadProgram(File xmlFile) {
		FsmProgram program = new FsmProgram(this);
		program.loadProgram(xmlFile);
		return program;
	}


	/**
	 * Generiert zu einem FSM-Programm eine XML-Repräsentation und liefert diese
	 * als String
	 * 
	 * @param program
	 *            das FSM-Programm
	 * @return einen String mit der XML-Repräsentation des FSM-Programms
	 */
	public static String toXML(FsmProgram program) {
		XMLStreamWriter writer = null;
		StringWriter strWriter = new StringWriter();

		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(strWriter);
			writer.writeStartDocument("ISO-8859-1", "1.0");
			writer.writeCharacters("\n");
			// writer.writeDTD("<!DOCTYPE FSM [\n <!ELEMENT FSM (startState, numberingOfStates, typeOfFsm, state*, comment*)> \n <!ELEMENT startState EMPTY> \n <!ATTLIST startState \n name CDATA #REQUIRED \n > \n <!ELEMENT numberingOfStates EMPTY> \n <!ATTLIST numberingOfStates \n int CDATA #REQUIRED \n > \n <!ELEMENT typeOfFsm (#PCDATA)> \n <!ELEMENT state (transition*)> \n <!ATTLIST state \n name CDATA #REQUIRED \n initial CDATA #REQUIRED \n final CDATA #REQUIRED \n x CDATA #REQUIRED \n y CDATA #REQUIRED \n> \n <!ELEMENT transition (descriptions)> \n <!ATTLIST transition \n fromState CDATA #REQUIRED \n toState CDATA #REQUIRED \n x CDATA #IMPLIED \n y CDATA #IMPLIED \n > \n <!ELEMENT descriptions (description+)> \n <!ELEMENT description (input, output)> \n <!ELEMENT input (booleanObject)> \n <!ELEMENT booleanObject (booleanObject*)> \n <!ATTLIST booleanObject \n name CDATA #REQUIRED \n x CDATA #REQUIRED \n y CDATA #REQUIRED \n > \n <!ELEMENT output (voidObject*)> \n <!ELEMENT voidObject EMPTY> \n <!ATTLIST voidObject \n name CDATA #REQUIRED \n x CDATA #REQUIRED \n y CDATA #REQUIRED \n > \n <!ELEMENT comment (#PCDATA)> \n <!ATTLIST comment \n x CDATA #REQUIRED \n y CDATA #REQUIRED\n >\n ]>");
			// writer.writeCharacters("\n");
			writer.writeStartElement(FsmHamsterFile.FSM_TAG);

			writer.writeCharacters("\n");
			writer.writeCharacters("\t");
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
