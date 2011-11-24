/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *  
 * Copyright (C) 2011 eZuce Inc. All rights reserved.
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
package org.jivesoftware.spark;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;

public class ButtonFactory {

	private static ButtonFactory instance = new ButtonFactory();

	protected ButtonFactory() {
		// nothing
	}

	public static ButtonFactory getInstance() {
		return instance;
	}

	public ChatRoomButton createChatTranscriptButton() {
		return new ChatRoomButton(SparkRes.getImageIcon(SparkRes.HISTORY_24x24_IMAGE));
	}

	public ChatRoomButton createSendFileButton() {
		return new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.SEND_FILE_24x24));
	}

	public ChatRoomButton createScreenshotButton() {
		return new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.PHOTO_IMAGE));
	}

	public ChatRoomButton createInviteConferenceButton() {
		return new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_24x24));
	}

	public RolloverButton createBuzzButton() {
		return new RolloverButton(SparkRes.getImageIcon(SparkRes.BUZZ_IMAGE));
	}

	public RolloverButton createEmoticonButton() {
		final EmoticonManager emoticonManager = EmoticonManager.getInstance();
		final String activeEmoticonSetName = emoticonManager.getActiveEmoticonSetName();
		final Emoticon smileEmoticon = emoticonManager.getEmoticon(activeEmoticonSetName, ":)");
		URL smileURL = emoticonManager.getEmoticonURL(smileEmoticon);
		ImageIcon icon = new ImageIcon(smileURL);

		return new RolloverButton(icon);
	}

	public JLabel createDivider() {
		return new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
	}
	
    public RolloverButton createSettingsButton() {
        RolloverButton settings = new RolloverButton(SparkRes.getImageIcon(SparkRes.SETTINGS_IMAGE_16x16));
        settings.setToolTipText(Res.getString("title.configure.room"));
        return settings;
    }

    public RolloverButton createTemaButton() {
        RolloverButton thema = new RolloverButton(SparkRes.getImageIcon(SparkRes.TYPING_TRAY));
        thema.setToolTipText(Res.getString("menuitem.change.subject"));
        return thema;
    }

    public RolloverButton createRegisterButton() {
        RolloverButton register = new RolloverButton(SparkRes.getImageIcon(SparkRes.PEOPLE_IMAGE));
        register.setToolTipText(Res.getString("button.register").replace("&", ""));
        return register;
    }

    public RolloverButton createAlwaysOnTop(boolean isAlwaysOnTopActive) {
        RolloverButton alwaysOnTopItem = new RolloverButton();
        if (isAlwaysOnTopActive) {
            alwaysOnTopItem.setIcon(SparkRes.getImageIcon("FRAME_ALWAYS_ON_TOP_ACTIVE"));
        } else {
            alwaysOnTopItem.setIcon(SparkRes.getImageIcon("FRAME_ALWAYS_ON_TOP_DEACTIVE"));
        }

        alwaysOnTopItem.setToolTipText(Res.getString("menuitem.always.on.top"));
        return alwaysOnTopItem;

    }
    
    public ChatRoomButton createOtrButton() {
    	return new ChatRoomButton();
    }    
}
