package de.hamster.model;

import java.io.Serializable;

import de.hamster.debugger.model.Hamster;
import de.hamster.workbench.Utils;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class TileEmptyException extends KachelLeerException
		implements
			Serializable {

	
	public TileEmptyException(Hamster hamster, int reihe, int spalte) {
		super(hamster, reihe, spalte);

	}
	
	public TileEmptyException(KachelLeerException e) {
		super(e.hamster, e.reihe, e.spalte);

	}

}