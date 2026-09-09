package de.hamster.debugger.view;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class VariableTreeNode extends DefaultMutableTreeNode {
	boolean loaded;

	public VariableTreeNode(String name, Value value) {
		super(new NameValue(name, value));
		loaded = false;
	}
	public int getChildCount() {
		NameValue nv = (NameValue) getUserObject();
		if (nv.getValue() instanceof ObjectReference) {
			if (!loaded) {
				loaded = true;
				if (nv.getValue() instanceof ArrayReference) {
					ArrayReference a = (ArrayReference) nv.getValue();
					List list = a.getValues();
					for (int i = 0; i < list.size(); i++) {
						add(new VariableTreeNode("[" + i + "]", (Value) list
								.get(i)));
					}
				} else {
					ObjectReference o = (ObjectReference) nv.getValue();
					List list = o.referenceType().allFields();
					for (int i = 0; i < list.size(); i++) {
						Field f = (Field) list.get(i);
						if (f.declaringType().name().equals(
								"de.hamster.debugger.model.Hamster")
								&& (f.name().equals("count") || f.name()
										.equals("standard")))
							continue;
						if (f.declaringType().name().equals(
								"de.hamster.debugger.model.IHamster")
								&& (f.name().equals("id") || f.name().equals(
										"processor")))
							continue;
						Value v = o.getValue(f);
						add(new VariableTreeNode(f.name(), v));
					}
				}
			}
		}
		return super.getChildCount();
	}
	//	public boolean equals(Object o) {
	//		if(!(o instanceof VariableTreeNode)) return false;
	//		VariableTreeNode node = (VariableTreeNode) o;
	//		return getUserObject().equals(node.getUserObject());
	//	}
}