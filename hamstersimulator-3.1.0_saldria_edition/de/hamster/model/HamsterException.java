package de.hamster.model;

import java.io.Serializable;

import de.hamster.debugger.model.Hamster;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class HamsterException extends RuntimeException implements Serializable {
	protected Hamster hamster;

	public HamsterException(Hamster hamster) {
		this.hamster = hamster;
	}

	public void setHamster(Hamster hamster) {
		this.hamster = hamster;
	}

	public Hamster getHamster() {
		return hamster;
	}
}