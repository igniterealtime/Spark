/**
 * Copyright (C) 2004-2013 Jive Software. All rights reserved.
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
package org.jivesoftware.spark.component.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jivesoftware.spark.ui.ContactItem;
/**
 * Extends ContactItem because ContactItem is a JPanel
 * When ContactItem will be redesigned, and won't implement JPanel, we will
 * extend JPanel here directly and customize here.
 *
 */
public class JContactItemRenderer extends ContactItem implements ListCellRenderer {
	JPanelRenderer basicPanelRenderer;
	
	public JContactItemRenderer() {
		super("", "", "");
        setOpaque(true);
		basicPanelRenderer = new JPanelRenderer();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, 
			boolean isSelected, boolean cellHasFocus) {
		
		basicPanelRenderer.getListCellRendererComponent(list, this, index, isSelected, cellHasFocus);
		ContactItem renderItem = (ContactItem)value;
		setFocusable(false);
		setNickname(renderItem.getNickname());
		setAlias(renderItem.getAlias());
                if (this.getDisplayName().trim().isEmpty()) {
                    // Fallback hack to show something other than empty string.
                    // JID can't be set after object creation, so alias is reset.
                    setAlias(renderItem.getDisplayName());
                }
		setIcon(renderItem.getIcon());
		setStatus(renderItem.getStatus());
		getNicknameLabel().setFont(renderItem.getNicknameLabel().getFont());
		getNicknameLabel().setForeground(renderItem.getNicknameLabel().getForeground());
		getDescriptionLabel().setFont(renderItem.getDescriptionLabel().getFont());
		getDescriptionLabel().setText(renderItem.getDescriptionLabel().getText());
		getSpecialImageLabel().setIcon(renderItem.getSpecialImageLabel().getIcon());
		getSideIcon().setIcon(renderItem.getSideIcon().getIcon());
		return this;
	}
}
