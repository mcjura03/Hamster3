/*
 * Created on 15.12.2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package de.hamster.debugger.view;

import com.sun.jdi.Value;

/**
 * @author Daniel
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class NameValue {
	String name;
	Value value;
	public NameValue(String name, Value value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public Value getValue() {
		return value;
	}
	
	public String toString() {
		return name + " = " + value;
	}
	
//	public boolean equals(Object o) {
//		if(!(o instanceof NameValue)) return false;
//		NameValue n = (NameValue)o;
//		return n.name.equals(name);
//	}
}
