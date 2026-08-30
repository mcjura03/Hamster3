package de.hamster.lego.model;


import de.hamster.model.HamsterException;
import de.hamster.debugger.model.Hamster;
import java.io.Serializable;
import de.hamster.workbench.Utils;
/**
 *
 * @author Christian
 */
public class MaulNichtLeerException extends HamsterException implements Serializable {
        
        protected Hamster hamster;
        
        public MaulNichtLeerException(Hamster hamster) {
                super(hamster);
                this.hamster = hamster;
                
        }
        
        public String toString() {
                return Utils.getResource("hamster.MaulNichtLeerException");
        }
        
        
        
        
}

