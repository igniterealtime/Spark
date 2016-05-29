/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
