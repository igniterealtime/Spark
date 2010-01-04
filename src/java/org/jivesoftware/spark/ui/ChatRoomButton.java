/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
