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
package org.jivesoftware.fastpath.workspace.assistants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.panes.BackgroundPane;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.workgroup.agent.AgentSession;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

public class Notes extends JPanel {
	private static final long serialVersionUID = -7789684145607565950L;
	private JFrame notesFrame;
    private JScrollPane scrollPane;
    private JTextPane textPane;

    private JToolBar toolBar;
    private RolloverButton saveButton;

    private String sessionID;
    private boolean hasClickedInPane;
    private JLabel statusLabel;

    private ChatRoom chatRoom;
    private boolean updated;

    public Notes(String sessionID, ChatRoom room) {
        setLayout(new BorderLayout());

        this.chatRoom = room;

        this.sessionID = sessionID;

        textPane = new JTextPane();
        textPane.setText(FpRes.getString("message.click.to.add.notes"));
        textPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!hasClickedInPane) {
                    textPane.setText("");
                    hasClickedInPane = true;
                }
            }
        });

        scrollPane = new JScrollPane(textPane);

        this.add(scrollPane, BorderLayout.CENTER);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        saveButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.SAVE_AS_16x16));

        toolBar.add(saveButton);

        ResourceUtils.resButton(saveButton, FpRes.getString("button.save.note"));

        final BackgroundPane titlePanel = new BackgroundPane();
        titlePanel.setLayout(new GridBagLayout());

        JLabel notesLabel = new JLabel();
        notesLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        ResourceUtils.resLabel(notesLabel, textPane, FpRes.getString("label.notes"));

        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setText(FpRes.getString("message.chat.notes"));

        titlePanel.add(notesLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        titlePanel.add(descriptionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 5), 0, 0));
        titlePanel.add(saveButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 5), 0, 0));

        //add(titlePanel, BorderLayout.NORTH);

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                saveButton.setEnabled(true);
                updated = true;
            }

            public void insertUpdate(DocumentEvent e) {
                saveButton.setEnabled(true);
                updated = true;
            }

            public void removeUpdate(DocumentEvent e) {
                saveButton.setEnabled(true);
                updated = true;
            }
        });

        saveButton.setEnabled(false);

        // Add status label
        statusLabel = new JLabel();
        this.add(statusLabel, BorderLayout.SOUTH);

        chatRoom.addClosingListener(new ChatRoomClosingListener() {
            public void closing() {
                if (updated) {
                    saveNotes();
                }
            }
        });
    }

    public void showDialog() {
        if (notesFrame != null && notesFrame.isVisible()) {
            return;
        }

        notesFrame = new JFrame(FpRes.getString("title.chat.notes"));
        notesFrame.setIconImage(SparkManager.getMainWindow().getIconImage());
        notesFrame.getContentPane().setLayout(new BorderLayout());
        notesFrame.getContentPane().add(new JScrollPane(this), BorderLayout.CENTER);
        notesFrame.pack();
        notesFrame.setSize(500, 400);

        notesFrame.setLocationRelativeTo(SparkManager.getChatManager().getChatContainer());
        notesFrame.setVisible(true);

        textPane.requestFocusInWindow();
    }


    private void saveNotes() {
        String note = textPane.getText();

        // Check for empty note.
        if (!ModelUtil.hasLength(note)) {
            return;
        }

        // Save note.
        AgentSession agentSession = FastpathPlugin.getAgentSession();
        try {
            agentSession.setNote(sessionID, note);
            saveButton.setEnabled(false);
            statusLabel.setText(" "+ FpRes.getString("message.notes.updated"));
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e1) {
                        Log.error(e1);
                    }
                    return true;
                }

                public void finished() {
                    statusLabel.setText("");
                }
            };
            worker.start();
        }
        catch (XMPPException e1) {
            showError(FpRes.getString("message.unable.to.update.notes"));
            Log.error("Could not commit note.", e1);
        }

    }

    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, FpRes.getString("title.notes"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Replaces all instances of oldString with newString in string.
     *
     * @param string    the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replace(String string, String oldString, String newString) {
        if (string == null) {
            return null;
        }
        // If the newString is null or zero length, just return the string since there's nothing
        // to replace.
        if (newString == null) {
            return string;
        }
        int i = 0;
        // Make sure that oldString appears at least once before doing any processing.
        if ((i = string.indexOf(oldString, i)) >= 0) {
            // Use char []'s, as they are more efficient to deal with.
            char[] string2 = string.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = string.indexOf(oldString, i)) > 0) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(string2, j, string2.length - j);
            return buf.toString();
        }
        return string;
    }

    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        size.height = 300;
        return size;
    }

}
