package org.jivesoftware.spark.component.focus;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Arrays;
import java.util.List;

public class SpecifiedOrderFocusTraversalPolicy extends FocusTraversalPolicy {
	
	private List<Component> list;
	private Component[] order;
	
	public SpecifiedOrderFocusTraversalPolicy(Component[] order)
	{
		this.order = order;
		list = Arrays.asList(order); 
	}
	
	public  Component getFirstComponent(Container focusCycleRoot) {
		return null;
	}
	
	public  Component getLastComponent(Container focusCycleRoot) {
		return null;
	}
	
	public  Component getComponentAfter(Container focusCycleRoot, 
			Component aComponent) {
		int index = list.indexOf(aComponent);
		if (index == (order.length - 1))
		{
			return order[0];
		}
		return order[index + 1];
    }
	
	public  Component getComponentBefore(Container focusCycleRoot, 
			Component aComponent) {
		int index = list.indexOf(aComponent);
		if (index == 0)
			return order[order.length - 1];
		return order[index - 1];
	}
	
	public Component getDefaultComponent(Container focusCycleRoot) {
		return order[0];
	}
}
