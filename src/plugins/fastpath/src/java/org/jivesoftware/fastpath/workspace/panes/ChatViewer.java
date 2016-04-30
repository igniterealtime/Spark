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
package org.jivesoftware.fastpath.workspace.panes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.smackx.workgroup.agent.AgentSession;
import org.jivesoftware.smackx.workgroup.ext.notes.ChatNotes;
import org.jivesoftware.smackx.workgroup.packet.Transcript;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.ui.ChatPrinter;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jxmpp.util.XmppStringUtils;

/**
 * Displays Fastpath transcripts.
 */
public class ChatViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
     * Display a Fastpath transcript.
     *
     * @param transcript the <code>Transcript</code>
     */
    public ChatViewer(final Transcript transcript) {
        List<Stanza> stanzas = transcript.getPackets();

        final TranscriptWindow chatWindow = new TranscriptWindow();
        chatWindow.setBackground(Color.white);
        final List<Message> chatTranscript = new ArrayList<Message>();

        Iterator<Stanza> iter = stanzas.iterator();
        while (iter.hasNext()) {
            Stanza stanza = iter.next();
            if (stanza instanceof Message) {
                Message message = (Message)stanza;
                String from = XmppStringUtils.parseResource(message.getFrom());
                DelayInformation delayInformation = message.getExtension("delay", "urn:xmpp:delay");
                Date stamp = null;
                if (delayInformation != null) {
                    stamp = delayInformation.getStamp();
                }
                message.removeExtension(delayInformation);
                chatWindow.insertMessage(from, message, ChatManager.TO_COLOR);
                final Map<String, Object> properties = new HashMap<>();
                properties.put( "date", stamp );
                message.addExtension( new JivePropertiesExtension( properties ) );
                message.setFrom(from);
                chatTranscript.add(message);
            }
            else {
                Presence presence = (Presence)stanza;
                String from = XmppStringUtils.parseResource(presence.getFrom());
                if (presence.getType() == Presence.Type.available) {
                    from = FpRes.getString("message.user.joined.room", from);
                }
                else {
                    from = FpRes.getString("message.user.left.room", from);
                }
                chatWindow.insertNotificationMessage(from, ChatManager.NOTIFICATION_COLOR);
                Message message = new Message();
                message.setBody(from);
                message.setFrom("Room Notice");
                chatTranscript.add(message);
            }
        }

        final RolloverButton saveTranscriptButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.SAVE_AS_16x16));
        final RolloverButton printChatButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.PRINTER_IMAGE_16x16));
        final JPanel toolbar = new JPanel();
        toolbar.setOpaque(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(saveTranscriptButton);
        toolbar.add(printChatButton);
        saveTranscriptButton.setToolTipText(GraphicUtils.createToolTip(FpRes.getString("tooltip.save.transcript")));
        printChatButton.setToolTipText(GraphicUtils.createToolTip(FpRes.getString("tooltip.print.transcript")));

        final BackgroundPane mainPanel = new BackgroundPane();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(toolbar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(chatWindow);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        this.setLayout(new GridBagLayout());


        saveTranscriptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chatWindow.saveTranscript(transcript.getSessionID() + ".html", chatTranscript, null);
            }
        });

        printChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ChatPrinter printer = new ChatPrinter();
                printer.print(chatWindow);
            }
        });

        chatWindow.setCaretPosition(0);


        final JPanel notesPanel = new JPanel();

        // Create Notes Pane to show text and do not allow editing.
        JTextPane pane = new JTextPane();
        pane.setBackground(Color.white);
        pane.setEditable(false);

        JScrollPane notesScroller = new JScrollPane(pane);
        notesScroller.setMaximumSize(new Dimension(1000, 100));
        notesPanel.setLayout(new BorderLayout());

        AgentSession agentSession = FastpathPlugin.getAgentSession();
        try {
            ChatNotes note = agentSession.getNote(transcript.getSessionID());
            pane.setText(note.getNotes());
        }
        catch (XMPPException | SmackException e) {
            pane.setText("");
            // Log.error(e);
        }
        notesScroller.setPreferredSize(new Dimension(400, 100));
        notesPanel.add(notesScroller, BorderLayout.CENTER);
        pane.setCaretPosition(0);

        final SparkTabbedPane tabbedPane = new SparkTabbedPane();
        tabbedPane.getMainPanel().setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
        tabbedPane.addTab(FpRes.getString("tab.transcript"), null, mainPanel);
        tabbedPane.addTab(FpRes.getString("tab.notes"), null, notesPanel);

        setBackground(Color.white);


        add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
}
