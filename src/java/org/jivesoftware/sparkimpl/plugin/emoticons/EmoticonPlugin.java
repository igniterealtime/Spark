/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.themes.ThemePreference;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonUI.EmoticonPickListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

/**
 * Adds an EmoticonPickList to each ChatRoom.
 */
public class EmoticonPlugin implements Plugin {


    public void initialize() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(final ChatRoom room) {
                final EmoticonManager manager = EmoticonManager.getInstance();
                String name = manager.getActiveEmoticonSetName();

                final Emoticon smileEmoticon = manager.getEmoticon(name, ":)");
                URL smileURL = manager.getEmoticonURL(smileEmoticon);

                // Add Emoticon button
                ImageIcon icon = new ImageIcon(smileURL);
                final RolloverButton emoticonPicker = new RolloverButton(icon);
                room.getEditorBar().add(emoticonPicker);

                emoticonPicker.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        // Show popup
                        final JPopupMenu popup = new JPopupMenu();
                        EmoticonUI ui = new EmoticonUI();
                        ui.setEmoticonPickListener(new EmoticonPickListener() {
                            public void emoticonPicked(String emoticon) {
                                try {
                                    popup.setVisible(false);
                                    final ChatInputEditor editor = room.getChatInputEditor();
                                    String currentText = editor.getText();
                                    if (currentText.length() == 0 || currentText.endsWith(" ")) {
                                        room.getChatInputEditor().insertText(emoticon + " ");
                                    }
                                    else {
                                        room.getChatInputEditor().insertText(" " + emoticon + " ");
                                    }
                                    room.getChatInputEditor().requestFocus();
                                }
                                catch (BadLocationException e1) {
                                    Log.error(e1);
                                }

                            }
                        });


                        popup.add(ui);
                        popup.show(emoticonPicker, e.getX(), e.getY());
                    }
                });

                room.addClosingListener(new ChatRoomClosingListener() {
                    public void closing() {
                        room.getEditorBar().remove(emoticonPicker);
                    }
                });
            }
        });

        // Add Preferences
        SparkManager.getPreferenceManager().addPreference(new ThemePreference());
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
        // Do nothing.
    }

}
