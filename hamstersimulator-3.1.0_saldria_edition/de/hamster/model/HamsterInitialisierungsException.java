package de.hamster.model;

import java.io.Serializable;

import de.hamster.debugger.model.Hamster;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class HamsterInitialisierungsException extends HamsterException
		implements Serializable {
	public HamsterInitialisierungsException(Hamster hamster) {
		super(hamster);
	}

	public String toString() {
		return Utils.getResource("hamster.HamsterInitialisierungsException");
	}
}
