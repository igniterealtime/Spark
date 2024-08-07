/**
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
package org.jivesoftware.sparkimpl.plugin.emoticons;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.themes.ThemePreference;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Adds an EmoticonPickList to each ChatRoom.
 */
public class EmoticonPlugin implements Plugin, ChatRoomListener {

	private EmoticonManager emoticonManager;
	private ChatManager chatManager;

	@Override
	public void initialize() {
		emoticonManager = EmoticonManager.getInstance();
		chatManager = SparkManager.getChatManager();
		addChatRoomListener();
	}

	/**
	 * Listen for rooms opening to add emoticon picker.
	 */
	private void addChatRoomListener() {
		// Adds the listener
		chatManager.addChatRoomListener(this);

		// Add Preferences
		SparkManager.getPreferenceManager()
				.addPreference(new ThemePreference());
	}

	@Override
	public void chatRoomOpened(final ChatRoom room) {
		// Check to see if emoticons are enabled.
		if (!SettingsManager.getLocalPreferences().areEmoticonsEnabled()) {
			return;
		}

		// final String activeEmoticonSetName =
		// emoticonManager.getActiveEmoticonSetName();

		emoticonManager = EmoticonManager.getInstance();
		Collection<String> emoticonPacks;
		emoticonPacks = emoticonManager.getEmoticonPacks();

		if (emoticonPacks != null) {

			// Add Emoticon button
			final RolloverButton emoticonPicker = UIComponentRegistry.getButtonFactory().createEmoticonButton();

			room.addEditorComponent(emoticonPicker);

			emoticonPicker.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// Show popup
					final JPopupMenu popup = new JPopupMenu();
					EmoticonUI emoticonUI = new EmoticonUI();
					emoticonUI
							.setEmoticonPickListener( emoticon -> {
                                try {
                                    popup.setVisible(false);
                                    final ChatInputEditor editor = room.getChatInputEditor();
                                    String currentText = editor.getText();
                                    if (currentText.length() == 0 || currentText.endsWith(" ")) {
                                        room.getChatInputEditor().insertText(emoticon + " ");
                                    } else {
                                        room.getChatInputEditor()
                                                .insertText(" " + emoticon + " ");
                                    }
                                    room.getChatInputEditor().requestFocus();
                                } catch (BadLocationException e1) {
                                    Log.error(e1);
                                }

                            } );

					popup.add(emoticonUI);
                    int actualX = e.getX()+10;
                    int actualY = e.getY();
                    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    final ChatContainer chat = SparkManager.getChatManager().getChatContainer();
                    // if height Spark Chat Windows > height 0.9* screenSize we should put emoticon window above
                    if(chat.getHeight() > screenSize.getHeight()*0.90){
                        actualY = e.getY()-100;
                    }
					popup.show(emoticonPicker, actualX, actualY);
				}
			});

			room.addClosingListener( () -> room.removeEditorComponent(emoticonPicker) );
		}
	}

	@Override
	public void chatRoomLeft(ChatRoom room) {
	}

	@Override
	public void chatRoomClosed(ChatRoom room) {
	}

	@Override
	public void chatRoomActivated(ChatRoom room) {
	}

	@Override
	public void userHasJoined(ChatRoom room, String userid) {
	}

	@Override
	public void userHasLeft(ChatRoom room, String userid) {
	}

	@Override
	public void shutdown() {

	}

	@Override
	public boolean canShutDown() {
		return false;
	}

	@Override
	public void uninstall() {
		// Do nothing.
	}

}
