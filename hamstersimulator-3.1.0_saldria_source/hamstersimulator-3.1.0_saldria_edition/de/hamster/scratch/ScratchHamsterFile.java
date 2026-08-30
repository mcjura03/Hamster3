package de.hamster.scratch;

import java.io.File;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import de.hamster.model.HamsterFile;

/**
 * Ob es sich bei einem Programm um ein Scratch-Programm handelt, wird daran
 * erkannt, das die zugrunde liegende XML-Datei den Tag "<SCRATCHPROGRAM>" enthält
 * 
 * @author dibo
 * 
 */
public class ScratchHamsterFile extends HamsterFile {

	/**
	 * Die Datei muss mit diesen Tag enthalten
	 */
	public final static String SCRATCH_TAG = "SCRATCHPROGRAM";

	/**
	 * hier hinter verbirgt sich das eigentliche Programm
	 */
	protected ScratchProgram program;

	// nicht veraendern!!!!
	public ScratchHamsterFile(char type) {
		super(type);
		this.program = new ScratchProgram();
	}

	// nicht veraendern!!!!
	public ScratchHamsterFile(File f) {
		super(f);
		if (this.file.exists()) {
			this.program = ScratchHamsterFile.loadProgram(this.file);
		} else {
			this.program = new ScratchProgram();
		}
	}

	// nicht veraendern!!!!
	public ScratchProgram getProgram() {
		return this.program;
	}

	// nicht veraendern!!!!
	public void setProgram(ScratchProgram p) {
		this.program = p;
	}

	// nicht veraendern!!!!
	@Override
	public String load() {
		super.load();
		if (this.file.exists()) {
			this.program = ScratchHamsterFile.loadProgram(this.file);
		}
		return ScratchHamsterFile.toXML(this.program);
	}

	// nicht veraendern!!!!
	@Override
	public void save(String dummy) {
		String xmlString = ScratchHamsterFile.toXML(this.program);
		super.save(xmlString);
	}

	// diese Methode muss angepasst werden
	/**
	 * Liest eine XML-Datei ein und liefert das darin repräsentierte
	 * Scratch-Programm
	 * 
	 * @param xmlFile
	 * @return
	 */
	public static ScratchProgram loadProgram(File xmlFile) {
		ScratchProgram program = new ScratchProgram();
		program.loadProgram(xmlFile);
		return program;
	}

	// diese Methode muss angepasst werden
	/**
	 * Generiert zu einem Scratch-Programm eine XML-Repräsentation und liefert
	 * diese als String
	 * 
	 * @param program
	 * @return
	 */
	public static String toXML(ScratchProgram program) {
		XMLStreamWriter writer = null;
		StringWriter strWriter = new StringWriter();

		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(strWriter);
			writer.writeStartDocument();
			writer.writeStartElement(ScratchHamsterFile.SCRATCH_TAG);

			StorageController controller = program.getProgram();
			if (controller != null) {
				program.getProgram().toXML(writer);
			}

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
