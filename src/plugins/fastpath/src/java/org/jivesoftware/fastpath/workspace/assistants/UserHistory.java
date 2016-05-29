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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.workspace.panes.ChatViewer;
import org.jivesoftware.fastpath.workspace.panes.HistoryItemRenderer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.workgroup.packet.Transcript;
import org.jivesoftware.smackx.workgroup.packet.Transcripts;
import org.jivesoftware.smackx.workgroup.packet.Transcripts.TranscriptSummary;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

public class UserHistory extends JPanel {
	private static final long serialVersionUID = -1067239194964815379L;
	private DefaultListModel model = new DefaultListModel();
    private JFrame userFrame;
    private JList list;
    private String userID;

    private JFrame frame;

    public UserHistory(String userID) {
        this.userID = userID;

        list = new JList(model);
        list.setCellRenderer(new HistoryItemRenderer());

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.white);
        mainPanel.add(list, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    UserHistoryItem historyItem = (UserHistoryItem)list.getSelectedValue();
                    showTranscript(historyItem.getSessionID());
                }
            }
        });


    }

    public void loadHistory() {

        SwingWorker transcriptThread = new SwingWorker() {
            final List<TranscriptSummary> transcriptList = new ArrayList<TranscriptSummary>();

            public Object construct() {
                try {
                    Transcripts transcripts = FastpathPlugin.getAgentSession().getTranscripts(userID);
                    Iterator<TranscriptSummary> iter = transcripts.getSummaries().iterator();
                    while (iter.hasNext()) {
                        Transcripts.TranscriptSummary summary = (Transcripts.TranscriptSummary)iter.next();
                        transcriptList.add(summary);
                    }
                }
                catch (XMPPException | SmackException e) {
                    Log.error("Error getting transcripts.", e);
                }

                Collections.sort(transcriptList, timeComparator);
                return transcriptList;
            }

            public void finished() {
                init(transcriptList);
            }
        };

        transcriptThread.start();
    }

    public void init(Collection<Transcripts.TranscriptSummary> transcriptList) {
        model.removeAllElements();
        Iterator<Transcripts.TranscriptSummary> iter = transcriptList.iterator();
        while (iter.hasNext()) {
            Transcripts.TranscriptSummary summary = iter.next();


            UserHistoryItem item = new UserHistoryItem(summary.getAgentDetails(), summary.getJoinTime(), summary.getLeftTime());
            item.setSessionID(summary.getSessionID());
            model.addElement(item);
        }

        list.validate();
        list.repaint();

    }

    private void showTranscript(String sessionID) {
        if (frame == null) {
            frame = new JFrame(FpRes.getString("title.transcript"));
            frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        }

        if (frame.isVisible()) {
            return;
        }

        Transcript transcript = null;
        try {
            transcript = FastpathPlugin.getAgentSession().getTranscript(sessionID);
        }
        catch (XMPPException | SmackException e) {
            Log.error("Error showing transcripts.", e);
        }

        if (transcript == null) {
            return;
        }
        final ChatViewer chatViewer = new ChatViewer(transcript);
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(chatViewer, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(600, 400);

        frame.setLocationRelativeTo(SparkManager.getMainWindow());
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

    private final Comparator timeComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            final Transcripts.TranscriptSummary item1 = (Transcripts.TranscriptSummary)o1;
            final Transcripts.TranscriptSummary item2 = (Transcripts.TranscriptSummary)o2;

            long int1 = item1.getJoinTime().getTime();
            long int2 = item2.getJoinTime().getTime();


            if (int1 == int2) {
                return 0;
            }

            if (int1 > int2) {
                return -1;
            }

            if (int1 < int2) {
                return 1;
            }

            return 0;
        }
    };


}


