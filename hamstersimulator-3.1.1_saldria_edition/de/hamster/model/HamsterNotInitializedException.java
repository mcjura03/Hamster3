package de.hamster.model;

import java.io.Serializable;

import de.hamster.debugger.model.Hamster;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class HamsterNotInitializedException extends
		HamsterNichtInitialisiertException implements Serializable {
	public HamsterNotInitializedException(Hamster hamster) {
		super(hamster);
	}

	public HamsterNotInitializedException(HamsterNichtInitialisiertException e) {
		super(e.hamster);
	}

}