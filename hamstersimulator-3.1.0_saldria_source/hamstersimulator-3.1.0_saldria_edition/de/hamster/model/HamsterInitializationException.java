package de.hamster.model;

import java.io.Serializable;

import de.hamster.debugger.model.Hamster;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class HamsterInitializationException extends HamsterInitialisierungsException
		implements Serializable {
	public HamsterInitializationException(Hamster hamster) {
		super(hamster);
	}
	public HamsterInitializationException(HamsterInitialisierungsException e) {
		super(e.hamster);
	}

}
