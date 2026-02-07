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
package org.jivesoftware.spark.ui.conferences;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.form.Form;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

/**
 * Configure chat room
 */
public class DataFormDialog extends JPanel {
    private final DataFormUI dataFormUI;
    private final JDialog dialog;

    public static void openDataFormDialog(MultiUserChat chat) {
        ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
        Form form;
        // Create the room
        try {
            form = chat.getConfigurationForm();
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
            MessageDialog.showErrorDialog(Res.getString("group.send_config.error"), e);
            return;
        }
        new DataFormDialog(chatFrame, chat, form);
    }

    public DataFormDialog(JFrame parent, MultiUserChat chat, Form form) {
        dialog = new JDialog(parent, true);
        dialog.setTitle(Res.getString("title.configure.chat.room"));

        this.setLayout(new GridBagLayout());

        this.dataFormUI = new DataFormUI(form.getDataForm());
        this.add(dataFormUI);

        JButton button = new JButton();
        ResourceUtils.resButton(button, Res.getString("button.update"));
        button.addActionListener(e -> {
            dialog.dispose();
            // Now submit all information
            updateRoomConfiguration(chat);
        });

        final JScrollPane pane = new JScrollPane(this);
        pane.getVerticalScrollBar().setBlockIncrement(200);
        pane.getVerticalScrollBar().setUnitIncrement(20);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(pane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(button);

        JButton cancelButton = new JButton();
        ResourceUtils.resButton(cancelButton, Res.getString("button.cancel"));
        cancelButton.addActionListener(actionEvent -> dialog.dispose());

        bottomPanel.add(cancelButton);

        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(800, 600);
        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);
    }

    private void updateRoomConfiguration(MultiUserChat chat) {
        FillableForm submitForm = dataFormUI.getFilledForm();
        try {
            chat.sendConfigurationForm(submitForm);
            // Now recheck the new settings and update bookmark
            MultiUserChatManager mucManager = SparkManager.getMucManager();
            RoomInfo info = mucManager.getRoomInfo(chat.getRoom());
            // Remove bookmark if any for a non-persistent room
            if (!info.isPersistent()) {
                BookmarkManager.getBookmarkManager(SparkManager.getConnection()).removeBookmarkedConference(info.getRoom());
            }
            //TODO update bookmark password if it was set
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
            MessageDialog.showErrorDialog(Res.getString("group.send_config.error"), e);
        }
    }
}
