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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.workgroup.agent.AgentSession;
import org.jivesoftware.smackx.workgroup.ext.history.AgentChatHistory;
import org.jivesoftware.smackx.workgroup.ext.history.AgentChatSession;
import org.jivesoftware.smackx.workgroup.packet.Transcript;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

public class ChatHistory extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel model = new DefaultListModel();
    private AgentSession agentSession;
    private JList list;
    private JFrame mainFrame;
    private JFrame frame;

    public ChatHistory() {
        list = new JList(model);

        list.setCellRenderer(new HistoryItemRenderer());

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.white);


        final BackgroundPane titlePane = new BackgroundPane() {
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                size.width = 0;
                return size;
            }
        };

        titlePane.setLayout(new GridBagLayout());
        titlePane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));


        JLabel userImage = new JLabel();
        userImage.setHorizontalAlignment(JLabel.LEFT);
        userImage.setText(FpRes.getString("title.previous.chats"));
        userImage.setIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24));
        titlePane.add(userImage, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        userImage.setFont(new Font("Dialog", Font.BOLD, 12));
        mainPanel.add(titlePane, BorderLayout.NORTH);

        mainPanel.add(list, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        init();

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    HistoryItem historyItem = (HistoryItem)list.getSelectedValue();
                    showTranscript(historyItem.getSessionID());
                }
            }
        });
    }

    public void showDialog() {
        AgentSession agentSession = FastpathPlugin.getAgentSession();
        String workgroupName = StringUtils.parseName(agentSession.getWorkgroupJID());


        if (mainFrame == null) {
            mainFrame = new JFrame(FpRes.getString("title.personal.chats"));
        }
        if (mainFrame.isVisible()) {
            return;
        }
        mainFrame.setIconImage(SparkManager.getMainWindow().getIconImage());
        mainFrame.getContentPane().setLayout(new BorderLayout());

        final JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        mainFrame.getContentPane().add(scrollPane);
        mainFrame.pack();
        mainFrame.setSize(400, 400);
        mainFrame.setLocationRelativeTo(SparkManager.getMainWindow());
        mainFrame.setVisible(true);
    }

    public void init() {
        model.removeAllElements();


        AgentChatHistory history = null;


        agentSession = FastpathPlugin.getAgentSession();
        String jid = SparkManager.getSessionManager().getBareAddress();
        try {
            history = agentSession.getAgentHistory(jid, 10, null);
        }
        catch (XMPPException e1) {
            Log.error("Error retrieving chat history.", e1);
        }

        try {
            model.removeAllElements();
            Collection sessions = history.getAgentChatSessions();
            Iterator iter = sessions.iterator();
            while (iter.hasNext()) {
                AgentChatSession chatSession = (AgentChatSession)iter.next();

                // Then were in a group chat
                final String nickname = chatSession.getVisitorsName();


                String email = chatSession.getVisitorsEmail();
                String sessionID = chatSession.getSessionID();
                String duration = ModelUtil.getTimeFromLong(chatSession.getDuration());
                String question = chatSession.getQuestion();
                if (!ModelUtil.hasLength(question)) {
                    question = "No question was asked.";
                }

                Date startDate = chatSession.getStartDate();
                HistoryItem historyItem = new HistoryItem(nickname, startDate, email, question, duration);
                historyItem.setSessionID(sessionID);
                model.addElement(historyItem);
            }

            list.validate();
            list.repaint();

        }
        catch (Exception e1) {
            Log.error("Error retrieving chat history.", e1);
        }
    }

    private void showTranscript(String sessionID) {
        if (frame == null) {
            frame = new JFrame(FpRes.getString("title.chat.transcript"));
            frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        }

        if (frame.isVisible()) {
            return;
        }

        Transcript transcript = null;
        try {
            transcript = FastpathPlugin.getAgentSession().getTranscript(sessionID);
        }
        catch (XMPPException e) {
            Log.error("Error showing transcripts.", e);
        }

        if (transcript == null) {
            JOptionPane.showMessageDialog(this, FpRes.getString("message.transcript.not.found.error"), FpRes.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        final ChatViewer chatViewer = new ChatViewer(transcript);
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(chatViewer, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(600, 400);

        frame.setLocationRelativeTo(mainFrame);
        frame.setVisible(true);
    }


    /**
     * Lets make sure that the panel doesn't stretch past the
     * scrollpane view pane.
     *
     * @return the preferred dimension
     */
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }

}


