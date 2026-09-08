package de.hamster.fsm.model.transition.input;

import de.hamster.fsm.controller.FsmProgram;
import de.hamster.fsm.model.FsmObject;
import de.hamster.fsm.model.state.IsNondeterministicException;
import de.hamster.fsm.model.transition.BooleanObject;

/**
 * Klasse, die einen Oder-Operator für das Objekt im Input eines endlichen Automaten repräsentiert.
 * @author Raffaela Ferrari
 *
 */
public class OrObject extends BooleanObject{

	/**
	 * Konstruktor
	 * @param positioning Gibt an, ob das Objekt rechts oder links in einem 
	 * anderen BooleanObjekt positioniert werden soll.
	 */
	public OrObject(int positioning) {
		super("oder", positioning);
	}

	@Override
	public FsmObject clone() {
		OrObject clonedOrObject = new OrObject(this.positioning);
		clonedOrObject.setChilds(this.childs);
		clonedOrObject.setParent(this.parent);
		clonedOrObject.setCoordinates(this.xStart, this.yStart);
		return clonedOrObject;
	}

	@Override
	public Object checkPerform(FsmProgram program) {
		//linke Boolesche Variable auswerten
		boolean leftBooleanVariable = true;
		if(this.childs.size()==2) {
			leftBooleanVariable = (Boolean) ((BooleanObject) this.childs.get(0)).checkPerform(program);
		} else if(this.childs.size() == 1 && ((BooleanObject)this.childs.get(0))
				.getPositioning() == 0) {
				leftBooleanVariable = (Boolean) ((BooleanObject) this.childs.get(0)).checkPerform(program);
		}
		
		if(leftBooleanVariable) {
			return true;
		}
		
		//rechte Boolesche Variable auswerten
		boolean rightBooleanVariable = true;
		if(this.childs.size()==2) {
			rightBooleanVariable = (Boolean) ((BooleanObject) this.childs.get(1)).checkPerform(program);
		} else if(this.childs.size() == 1 && ((BooleanObject)this.childs.get(0))
			.getPositioning() == 1) {
			rightBooleanVariable = (Boolean) ((BooleanObject) this.childs.get(1)).checkPerform(program);
		}
		
		//damit ergibt sich
		return rightBooleanVariable;
	}

	@Override
	public Object performImplementation(FsmProgram program)  throws IsNondeterministicException{
		//linke Boolesche Variable auswerten
		boolean leftBooleanVariable = true;
		if(this.childs.size()==2) {
			leftBooleanVariable = (Boolean) this.childs.get(0).perform(program);
		} else if(this.childs.size() == 1 && ((BooleanObject)this.childs.get(0))
				.getPositioning() == 0) {
				leftBooleanVariable = (Boolean) this.childs.get(0).perform(program);
		}
		
		if(leftBooleanVariable) {
			return true;
		}
		
		//rechte Boolesche Variable auswerten
		boolean rightBooleanVariable = true;
		if(this.childs.size()==2) {
			rightBooleanVariable = (Boolean) this.childs.get(1).perform(program);
		} else if(this.childs.size() == 1 && ((BooleanObject)this.childs.get(0))
			.getPositioning() == 1) {
			rightBooleanVariable = (Boolean) this.childs.get(0).perform(program);
		}
		
		//damit ergibt sich
		return rightBooleanVariable;
	}

	@Override
	public void writeSourceCode(StringBuffer buffer, int indentation){
		startLine(buffer, indentation);
		buffer.append("(");
		if(this.childs.size() == 2) {
			((BooleanObject) this.childs.get(0)).writeSourceCode(buffer, 0);
			buffer.append(" || ");
			((BooleanObject) this.childs.get(1)).writeSourceCode(buffer, 0);
		} else if(this.childs.size() == 1) {
			if(((BooleanObject)this.childs.get(0)).getPositioning() == 1) {
				buffer.append(" true || ");
				((BooleanObject) this.childs.get(0)).writeSourceCode(buffer, 0);
			} else {
				((BooleanObject) this.childs.get(0)).writeSourceCode(buffer, 0);
				buffer.append(" || true");
			}
		} else {
			buffer.append("true || true");
		}
		buffer.append(")");
	};
}
