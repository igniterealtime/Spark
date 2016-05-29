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
package org.jivesoftware.spark.ui;

import org.jivesoftware.spark.component.RolloverButton;

import javax.swing.Icon;

/**
 * Button to use with ChatRooms to allow for conformity in the Chat Room look and feel.
 */
public class ChatRoomButton extends RolloverButton {
	private static final long serialVersionUID = -2292789979004158240L;

	/**
     * Create a new ChatRoomButton.
     */
    public ChatRoomButton() {
    }

    /**
     * Create a new ChatRoomButton
     *
     * @param icon the icon to use on the button.
     */
    public ChatRoomButton(Icon icon) {
        super(icon);
    }

    /**
     * Create a new ChatRoomButton.
     *
     * @param text the button text.
     * @param icon the button icon.
     */
    public ChatRoomButton(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Creates a new ChatRoomButton.
     *
     * @param text the text to display on the button.
     */
    public ChatRoomButton(String text) {
        super(text);

    }





}
