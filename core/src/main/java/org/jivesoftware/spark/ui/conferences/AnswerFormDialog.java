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

import java.awt.*;
import javax.swing.*;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;

import static org.jivesoftware.spark.ChatManager.ERROR_COLOR;
import static org.jivesoftware.spark.ChatManager.NOTIFICATION_COLOR;

/**
 * Answer Form Dialog.
 * Used for registration in the room.
 *
 * @author wolf.posdorfer
 */
public class AnswerFormDialog {
    private final DataFormUI dataFormUI;
    private final JDialog dialog;

    public AnswerFormDialog(JFrame parent, final MultiUserChat chat, final DataForm form) {
        this.dataFormUI = new DataFormUI(form);
        dialog = new JDialog(parent, true);
        dialog.setTitle(Res.getString("button.register").replace("&", ""));
        dialog.setLayout(new GridBagLayout());

        JButton updateButton = new JButton();
        ResourceUtils.resButton(updateButton, Res.getString("apply"));
        updateButton.addActionListener(e -> {
            dialog.dispose();
            sendAnswerForm(chat);
        });

        JButton cancelButton = new JButton();
        ResourceUtils.resButton(cancelButton, Res.getString("button.cancel"));
        cancelButton.addActionListener(actionEvent -> dialog.dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(updateButton);
        bottomPanel.add(cancelButton);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(dataFormUI, BorderLayout.CENTER);
        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
//        dialog.setSize(600, 400);
        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);
    }

    /**
     * Sends the Answer Form
     */
    private void sendAnswerForm(MultiUserChat chat) {
        ChatRoom room = SparkManager.getChatManager().getChatRoom(chat.getRoom());
        FillableForm filledForm = dataFormUI.getFilledForm();
        try {
            chat.sendRegistrationForm(filledForm);
            String reg = Res.getString("message.groupchat.registered.member", chat.getRoom());
            room.getTranscriptWindow().insertNotificationMessage(reg, NOTIFICATION_COLOR);
        } catch (XMPPException | SmackException | InterruptedException e) {
            room.getTranscriptWindow().insertNotificationMessage(e.getMessage(), ERROR_COLOR);
        }
    }

}
