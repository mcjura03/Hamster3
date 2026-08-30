package de.hamster.scratch;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hamster.scratch.elements.BooleanMethodObject;
import de.hamster.scratch.elements.VoidObject;
import de.hamster.scratch.gui.DeleteMethodException;
import de.hamster.scratch.gui.InvalidIdentifierException;
import de.hamster.scratch.gui.RenameMethodException;

/**
 * Der StorageController beinhaltet alle Inhalte verschiedener Methoden. Die
 * Inhalte können anhand des Methodennamen (String) aus einer HashMap
 * herausgefunden werden. Dieser ist auch für das Speichern und Laden der
 * Methoden aus XML Dateien verantworlich.
 * 
 * @author HackZ
 * 
 */
public class StorageController {
	public static ArrayList<String> constantNames;
	public static ArrayList<String> constantReserved;

	private HashMap<String, Method> voidMethods;
	private ArrayList<Method> voidMethodsList;
	private HashMap<String, Method> booleanMethods;
	private ArrayList<Method> booleanMethodsList;

	/**
	 * Erstellt einen neuen StorageController, in dem die Methode main gleich
	 * erstellt wird und als offen deklariert wird.
	 */
	public StorageController() {
		this.fillConstantNames();
		this.voidMethods = new HashMap<String, Method>();
		this.voidMethodsList = new ArrayList<Method>();
		this.booleanMethods = new HashMap<String, Method>();
		this.booleanMethodsList = new ArrayList<Method>();

		this.addVoidMethod("main");
		this.getVoidMethod("main").setOpened(true);
	}

	/**
	 * Das statische Attribut constatnNames wird mit verschiedenen Konstanten
	 * gefüllt. Dies geschieht nur einmalig beim ersten Aufruf, sollte ein
	 * weiteres ScratchPanel geöffnet werden, so sind die Varieblen bereits
	 * enthalten und die Funktion wird abgebrochen.
	 */
	private void fillConstantNames() {
		if (StorageController.constantNames != null) {
			return;
		}

		StorageController.constantNames = new ArrayList<String>();
		StorageController.constantReserved = new ArrayList<String>();

		StorageController.constantNames.add("vor");
		StorageController.constantNames.add("linksUm");
		StorageController.constantNames.add("gib");
		StorageController.constantNames.add("nimm");
		StorageController.constantNames.add("vornFrei");
		StorageController.constantNames.add("kornDa");
		StorageController.constantNames.add("maulLeer");

		StorageController.constantReserved.add("wahr");
		StorageController.constantReserved.add("falsch");
		StorageController.constantReserved.add("true");
		StorageController.constantReserved.add("false");
		StorageController.constantReserved.add("falls");
		StorageController.constantReserved.add("solange");
		StorageController.constantReserved.add("tueSolange");
		StorageController.constantReserved.add("null");
		StorageController.constantReserved.add("und");
		StorageController.constantReserved.add("oder");
		StorageController.constantReserved.add("nicht");

		StorageController.constantReserved.add("abstract");
		StorageController.constantReserved.add("assert");
		StorageController.constantReserved.add("boolean");
		StorageController.constantReserved.add("break");
		StorageController.constantReserved.add("byte");
		StorageController.constantReserved.add("case");
		StorageController.constantReserved.add("catch");
		StorageController.constantReserved.add("char");
		StorageController.constantReserved.add("class");
		StorageController.constantReserved.add("const");
		StorageController.constantReserved.add("continue");
		StorageController.constantReserved.add("default");
		StorageController.constantReserved.add("do");
		StorageController.constantReserved.add("double");
		StorageController.constantReserved.add("else");
		StorageController.constantReserved.add("enum");
		StorageController.constantReserved.add("extends");
		StorageController.constantReserved.add("final");
		StorageController.constantReserved.add("finally");
		StorageController.constantReserved.add("float");
		StorageController.constantReserved.add("for");
		StorageController.constantReserved.add("goto");
		StorageController.constantReserved.add("if");
		StorageController.constantReserved.add("implements");
		StorageController.constantReserved.add("import");
		StorageController.constantReserved.add("instanceof");
		StorageController.constantReserved.add("int");
		StorageController.constantReserved.add("interface");
		StorageController.constantReserved.add("long");
		StorageController.constantReserved.add("native");
		StorageController.constantReserved.add("new");
		StorageController.constantReserved.add("package");
		StorageController.constantReserved.add("private");
		StorageController.constantReserved.add("protected");
		StorageController.constantReserved.add("public");
		StorageController.constantReserved.add("return");
		StorageController.constantReserved.add("short");
		StorageController.constantReserved.add("static");
		StorageController.constantReserved.add("strictfp");
		StorageController.constantReserved.add("super");
		StorageController.constantReserved.add("switch");
		StorageController.constantReserved.add("synchronized");
		StorageController.constantReserved.add("this");
		StorageController.constantReserved.add("throw");
		StorageController.constantReserved.add("throws");
		StorageController.constantReserved.add("transient");
		StorageController.constantReserved.add("try");
		StorageController.constantReserved.add("void");
		StorageController.constantReserved.add("volatile");
		StorageController.constantReserved.add("while");
	}

	/**
	 * Liefert alle Methoden als ArrayList zurück, die geöffnet sind. Ist
	 * sinnvoll beim Laden des Programms, um festzustellen welche Tabs offen
	 * sind und diese im Programm zu öffnen.
	 * 
	 * @return
	 */
	public ArrayList<String> getOpenedMethods() {
		ArrayList<String> res = new ArrayList<String>();

		for (Method m : this.voidMethodsList) {
			if (m.isOpened()) {
				res.add(m.getName());
			}
		}

		for (Method m : this.booleanMethodsList) {
			if (m.isOpened()) {
				res.add(m.getName());
			}
		}

		return res;
	}

	public String getSelectedMethod() {
		for (Method m : this.voidMethodsList) {
			if (m.isSelected()) {
				return m.getName();
			}
		}

		for (Method m : this.booleanMethodsList) {
			if (m.isSelected()) {
				return m.getName();
			}
		}
		return null;
	}

	/**
	 * Setzt fest, ob die Methode mit dem übergebenen Namen offen oder
	 * geschlossen ist.
	 * 
	 * @param name
	 *            Name der Methode.
	 * @param opened
	 */
	public void setOpened(String name, boolean opened) {
		Method m = this.getMethod(name);

		if (m == null) {
			return;
		}

		m.setOpened(opened);
	}

	public void setSelected(String name, boolean sel) {
		Method m = this.getMethod(name);

		if (m == null) {
			return;
		}

		m.setSelected(sel);
	}

	/**
	 * Fügt eine neue void-Methode hinzu. Der name darf noch nicht vorhanden
	 * sein.
	 * 
	 * @param name
	 */
	public Method addVoidMethod(String name) {
		if (this.voidMethods.containsKey(name)) {
			return null;
		}

		Method m = new Method(name, Renderable.RType.VOID);
		this.voidMethods.put(name, m);
		this.voidMethodsList.add(m);
		return m;
	}

	/**
	 * Liefert die Methode mit dem übergebenen Namen.
	 * 
	 * @param name
	 *            Name der Methode
	 * @return
	 */
	public Method getVoidMethod(String name) {
		if (!this.voidMethods.containsKey(name)) {
			return null;
		}

		return this.voidMethods.get(name);
	}

	/**
	 * Liefert alle void-Methoden als ArrayListe. Wird verwendet, um alle
	 * void-Methoden in der ElementListe anzuzeigen.
	 * 
	 * @return
	 */
	public ArrayList<Renderable> getAllVoidMethods() {
		ArrayList<Renderable> result = new ArrayList<Renderable>();
		for (Method m : this.voidMethodsList) {
			result.add(new VoidObject(m.getName(),
					new ArrayList<Renderable.RType>()));
		}

		return result;
	}

	/**
	 * Fügt eine neue boolean-Methode hinzu. Der name darf noch nicht vorhanden
	 * sein.
	 * 
	 * @param name
	 */
	public Method addBooleanMethod(String name) {
		if (this.booleanMethods.containsKey(name)) {
			return null;
		}

		Method m = new Method(name, Renderable.RType.BOOLEAN);
		this.booleanMethods.put(name, m);
		this.booleanMethodsList.add(m);
		return m;
	}

	/**
	 * Liefert die Methode mit dem übergebenen Namen
	 * 
	 * @param name
	 *            Name der Methode
	 * @return
	 */
	public Method getBooleanMethod(String name) {
		if (!this.booleanMethods.containsKey(name)) {
			return null;
		}

		return this.booleanMethods.get(name);
	}

	/**
	 * Liefert alle boolean-Methoden als ArrayListe. Wird verwendet, um alle
	 * boolean-Methoden in der ElementListe anzuzeigen.
	 * 
	 * @return
	 */
	public ArrayList<Renderable> getAllBooleanMethods() {
		ArrayList<Renderable> result = new ArrayList<Renderable>();
		for (Method m : this.booleanMethodsList) {
			result.add(new BooleanMethodObject(m.getName(),
					new ArrayList<Renderable.RType>()));
		}

		return result;
	}

	public Method[] getAllMethods() {
		Method[] methods = new Method[voidMethodsList.size()
				+ booleanMethodsList.size()];
		int index = 0;
		for (Method m : voidMethodsList) {
			methods[index++] = m;
		}
		for (Method m : booleanMethodsList) {
			methods[index++] = m;
		}
		return methods;
	}

	/**
	 * Liefer die void- oder boolean-Methode mit dem übergebenen Namen.
	 * 
	 * @param name
	 * @return
	 */
	public Method getMethod(String name) {
		Method m = this.getVoidMethod(name);
		if (m != null) {
			return m;
		}

		return this.getBooleanMethod(name);
	}

	/**
	 * Liefert das erste ausfuehrbare Renderable aus der main-Methode, mit dem
	 * das Programm anfängt.
	 * 
	 * @return
	 */
	public Renderable getMainRoot() {
		Method main = this.voidMethods.get("main");
		return main.getRootElement();
	}

	/**
	 * Überprüft, ob der übergebene Name noch nicht vergeben ist oder wirft eine
	 * Exception mit der passenden Fehlerbeschreibung. Es wird dabei der name
	 * jedoch noch nicht registriert, sodern dient nur der reinen Überprüfung.
	 * 
	 * @param name
	 *            name eine Methode, die erstellt werden soll
	 * @throws InvalidIdentifierException
	 *             wird geworfen, falls
	 *             <ol>
	 *             <li>Der Bezeichner hat keine gültige Javakonvention.</li>
	 *             <li>Der Bezeichner ist bereits vergeben.</li>
	 *             <li>Der Bezeichner ist ein reserviertes Javakeyword.</li>
	 *             </ol>
	 */
	public void allocName(String name) throws InvalidIdentifierException {
		ScratchUtils.checkJavaIdentifier(name);

		if (this.getBooleanMethod(name) != null) {
			throw new InvalidIdentifierException(
					"Dieser Bezeichnername ist bereits vergeben!");
		}

		if (this.getVoidMethod(name) != null) {
			throw new InvalidIdentifierException(
					"Dieser Bezeichnername ist bereits vergeben!");
		}

		for (String s : StorageController.constantNames) {
			if (s.equals(name)) {
				throw new InvalidIdentifierException(
						"Dieser Bezeichnername ist bereits vergeben!");
			}
		}

		for (String s : StorageController.constantReserved) {
			if (s.equals(name)) {
				throw new InvalidIdentifierException(
						"Keywords sind reserviert und dürfen nicht als Bezeichner verwendet werden!");
			}
		}
	}

	/**
	 * Überprüft, ob die Methode mit dem übergebenen Namen im Programm verwendet
	 * wird.
	 * 
	 * @param name
	 *            Name der Methode, die getestet werden soll.
	 * @return true, wenn diese Methode im Programm verwendet wird.
	 */
	public boolean inUse(String name) {
		// Alle Void Methoden testen
		for (Method m : this.voidMethodsList) {
			if (!m.getName().equals(name) && m.inUse(name)) {
				return true;
			}
		}

		for (Method m : this.booleanMethodsList) {
			if (!m.getName().equals(name) && m.inUse(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Löscht die Methode mit dem übergebenen Namen
	 * 
	 * @param name
	 * @throws DeleteMethodException
	 *             wird geworfen, falls versucht wird
	 *             <ol>
	 *             <li>die Methode <tt>main</tt> zu löschen.</li>
	 *             <li>eine Methode zu löschen, die in einer Anderen Methode
	 *             noch verwendet wird.</li>
	 *             <li>eine feste Methode oder einen Javakey zu löschen.</li>
	 *             </ol>
	 */
	public void deleteMethod(String name) throws DeleteMethodException {
		if (name.equals("main")) {
			throw new DeleteMethodException(
					"Die Methode \"main\" ist der Startpunkt des Programms und kann weder gelöscht, noch umbenannt werden!");
		}

		if (this.inUse(name)) {
			throw new DeleteMethodException(
					"Die Methode "
							+ name
							+ " wird bereits im Programm verwendet. Es können nur nicht verwendete Methoden gelöscht werden!");
		}

		// Versuche void Methode zu löschen
		if (this.voidMethods.containsKey(name)) {
			Method temp = this.voidMethods.get(name);
			this.voidMethods.remove(name);
			this.voidMethodsList.remove(temp);
			return;
		}

		// Versuche boolean Methode zu löschen
		if (this.booleanMethods.containsKey(name)) {
			Method temp = this.booleanMethods.get(name);
			this.booleanMethods.remove(name);
			this.booleanMethodsList.remove(temp);
			return;
		}

		// Name ist wahrscheinlich ein Key oder feste Methode (vor, linksUm...)
		throw new DeleteMethodException(
				"Methoden aus dem Standardwortschatz des Hamsters, sowie Javakeywörter dürfen nicht gelöscht werden!");
	}

	/**
	 * Benennt eine Methode um
	 * 
	 * @param fromName
	 * @param toName
	 * @throws RenameMethodException
	 *             Wird geworfen falls versucht wird
	 *             <ol>
	 *             <li>die Methode <tt>main</tt> umzubenennen.</li>
	 *             <li>der neue Methodenname keine gültige Javakonvetion hat.</li>
	 *             <li>der neue Methodenname bereits vergeben ist.</li>
	 *             <li>eine feste Methode oder einen Javakey umzubenennen.</li>
	 *             </ol>
	 */
	public void rename(String fromName, String toName)
			throws RenameMethodException {
		if (fromName.equals("main")) {
			throw new RenameMethodException(
					"Die Methode \"main\" ist der Startpunkt des Programms und kann weder gelöscht, noch umbenannt werden!");
		}

		try {
			this.allocName(toName);
		} catch (InvalidIdentifierException e) {
			throw new RenameMethodException(e.getMessage());
		}

		for (Method m : this.voidMethodsList) {
			m.rename(fromName, toName);
		}

		for (Method m : this.booleanMethodsList) {
			m.rename(fromName, toName);
		}

		if (this.voidMethods.containsKey(fromName)) {
			Method temp = this.voidMethods.remove(fromName);
			this.voidMethods.put(toName, temp);
		}

		if (this.booleanMethods.containsKey(fromName)) {
			Method temp = this.booleanMethods.remove(fromName);
			this.booleanMethods.put(toName, temp);
		}
	}

	/**
	 * Überprüft, ob die übergebene Methode existiert
	 * 
	 * @param name
	 * @return true, wenn eine selbst erstellte Methode existiert. False, wenn
	 *         die Methode nicht existiert, oder ein Javakeyword ist oder zum
	 *         Sprachwortschatz des Hamsters gehört
	 */
	public boolean existsMethod(String name) {
		for (Method m : this.voidMethodsList) {
			if (m.getName().equals(name)) {
				return true;
			}
		}

		for (Method m : this.booleanMethodsList) {
			if (m.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Schreibt den Inhalt des StorageControllers in ein XML-Document. Das
	 * XML-Document muss von ausserhalb bereits vorbereitet worden sein, mit dem
	 * Startelement.
	 * 
	 * @param writer
	 *            Der writer, der bereits das Startelement enthält.
	 * @throws XMLStreamException
	 */
	public void toXML(XMLStreamWriter writer) throws XMLStreamException {
		// Problem: Reihenfolge
		for (Method m : this.voidMethodsList) {
			m.toXML(writer);
		}

		for (Method m : this.booleanMethodsList) {
			m.toXML(writer);
		}
	}

	/**
	 * Lädt das Programm anhand der übergebenen NodeList. Dies sind alle
	 * Kinderelemente des Wurzelelements <tt>SCRATCHPROGRAM</tt>.
	 * 
	 * @param methods
	 *            Kinderelemente von <tt>SCRATCHPROGRAM</tt>
	 */
	public void loadProgram(NodeList methods) {
		this.voidMethods.clear();
		this.voidMethodsList.clear();
		this.booleanMethods.clear();
		this.booleanMethodsList.clear();

		for (int i = 0; i < methods.getLength(); i++) {
			Element method = (Element) methods.item(i);
			String name = method.getAttribute("NAME");
			String type = method.getAttribute("TYPE");
			String opened = method.getAttribute("OPENED");
			// dibo 13.08.2010
			String selected = method.getAttribute("SELECTED");

			Method m = null;
			if (type.equals("VOID")) {
				this.addVoidMethod(name);
				m = this.getVoidMethod(name);
			} else {
				this.addBooleanMethod(name);
				m = this.getBooleanMethod(name);
			}

			m.loadProgram(method.getChildNodes());
			m.setOpened(opened.equals("T"));

			if (selected == null) {
				m.setSelected(false);
			} else {
				m.setSelected(selected.equals("T"));
			}
		}
	}

	/**
	 * Übernimmt den Inhalt des übergebenen Controllers in diesem
	 * 
	 * @param program
	 */
	public void setNewController(StorageController controller) {
		this.booleanMethods = controller.booleanMethods;
		this.booleanMethodsList = controller.booleanMethodsList;
		this.voidMethods = controller.voidMethods;
		this.voidMethodsList = controller.voidMethodsList;
	}

	/**
	 * Schreibt den Quellcode dieses Programms in den übergebenen Buffer als
	 * Programmcode in Hamster-Simulator Convention.
	 * 
	 * @param buffer
	 */
	public void writeSourceCode(StringBuffer buffer) {
		for (Method m : voidMethodsList)
			m.writeSourceCode(buffer);

		for (Method m : booleanMethodsList)
			m.writeSourceCode(buffer);
	}

}
