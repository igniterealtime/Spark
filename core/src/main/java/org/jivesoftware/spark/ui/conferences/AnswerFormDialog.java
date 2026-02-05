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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;

/**
 * Answer Form Dialog
 *
 * @author wolf.posdorfer
 */
public class AnswerFormDialog {
    private final JDialog dialog;
    private final JPanel centerPanel;

    private final HashMap<String, JComponent> _map = new HashMap<>();

    public AnswerFormDialog(JFrame parent, final MultiUserChat chat, final DataForm form) {
        centerPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        dialog = new JDialog(parent, true);
        dialog.setTitle(Res.getString("button.register").replace("&", ""));

        int row = 0;
        for (final FormField formfield : form.getFields()) {
            JLabel label = new JLabel(formfield.getLabel());
            FormField.Type type = formfield.getType();

            JComponent comp = null;
            if (type == FormField.Type.text_single) {
                comp = new JTextField();
            } else if (type == FormField.Type.text_multi) {
                comp = new JTextArea();
                comp.setBorder(new JTextField().getBorder());
            }

            if (comp != null) {
                addComponent(label, comp, row, formfield.getFieldName());
                row++;
            }
        }

        JButton updateButton = new JButton();
        ResourceUtils.resButton(updateButton, Res.getString("apply"));
        updateButton.addActionListener(e -> {
            dialog.dispose();
            sendAnswerForm(new FillableForm(form), chat);
        });

        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(updateButton);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(centerPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(600, 400);
        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);
    }

    private void addComponent(JLabel label, JComponent comp, int row, String variable) {
        centerPanel.add(label, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        centerPanel.add(comp, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        _map.put(variable, comp);
    }

    /**
     * Sends the Answer Form
     * @param answer must be an answer-form
     */
    private void sendAnswerForm(FillableForm answer, MultiUserChat chat) {
        ChatRoom room = SparkManager.getChatManager().getChatRoom(chat.getRoom());
        for (String key : _map.keySet()) {
            String value = getValueFromComponent(key);
            answer.setAnswer(key, value);
        }
        try {
            chat.sendRegistrationForm(answer);
            String reg = Res.getString("message.groupchat.registered.member", chat.getRoom());
            room.getTranscriptWindow().insertNotificationMessage(reg, ChatManager.NOTIFICATION_COLOR);
        } catch (XMPPException | SmackException | InterruptedException e) {
            room.getTranscriptWindow().insertNotificationMessage(e.getMessage(), ChatManager.ERROR_COLOR);
        }
    }

    /**
     * returns the Component specific value
     */
    private String getValueFromComponent(String label) {
        Component comp = _map.get(label);
        if (comp instanceof JTextField) {
            return ((JTextField) comp).getText();
        } else if (comp instanceof JTextArea) {
            return ((JTextArea) comp).getText();
        } else {
            return null;
        }
    }

}
