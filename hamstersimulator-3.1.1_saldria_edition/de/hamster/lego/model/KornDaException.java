package de.hamster.lego.model;

import de.hamster.model.HamsterException;
import de.hamster.debugger.model.Hamster;
import java.io.Serializable;
import de.hamster.workbench.Utils;
/**
 *
 * @author Christian
 */
public class KornDaException extends HamsterException implements Serializable {
        
        int reihe;
        int spalte;
        protected Hamster hamster;
        
        public KornDaException(Hamster hamster, int reihe, int spalte) {
                super(hamster);
                this.hamster = hamster;
                this.reihe = reihe;
		this.spalte = spalte;
                
        }
        
        public int getReihe() {
                return reihe;
        }
        
        public int getSpalte() {
                return spalte;
        }
        
        public String toString() {
                return Utils.getResource("hamster.KornDaException") + " (" + reihe + ", " + spalte + ")";
        }
        
        
}
