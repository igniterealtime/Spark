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
package org.jivesoftware.fastpath.workspace.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.panes.BackgroundPane;
import org.jivesoftware.fastpath.workspace.panes.ChatViewer;
import org.jivesoftware.fastpath.workspace.panes.HistoryItemRenderer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.workgroup.agent.AgentSession;
import org.jivesoftware.smackx.workgroup.packet.Transcript;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.search.Searchable;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.util.JidUtil;

public class ChatSearch implements Searchable {

    public Icon getIcon() {
        return FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16);
    }

    public String getName() {
        return FpRes.getString("title.chat.transcripts");
    }

    public String getDefaultText() {
        return FpRes.getString("message.find.previous.conversations");
    }

    public String getToolTip() {
        return getDefaultText();
    }

    public void search(String query) {
        final List<ChatSearchResult> results = new ArrayList<>();
        AgentSession agentSession = FastpathPlugin.getAgentSession();
        try {
            Form form = agentSession.getTranscriptSearchForm();
            Form filledForm = form.createAnswerForm();
            filledForm.setAnswer("queryString", query);

            // Define Workgroups
            final List<Jid> workgroups = new ArrayList<>();
            workgroups.add(FastpathPlugin.getWorkgroup().getWorkgroupJID());
            List<String> workgroupStrings = JidUtil.toStringList(workgroups);
            filledForm.setAnswer("workgroups", workgroupStrings);

            ReportedData reportedData;
            try {
                reportedData = agentSession.searchTranscripts(filledForm);
                for ( final ReportedData.Row row : reportedData.getRows() ) {
                    ChatSearchResult result = new ChatSearchResult(row, query);
                    results.add(result);
                }
            }
            catch (XMPPException | SmackException | InterruptedException e) {
                Log.error(e);
            }


            results.sort(dateComparator);
            DefaultListModel<SearchItem> model = new DefaultListModel<>();
            final JList<SearchItem> list = new JList<>(model);
            list.setCellRenderer(new HistoryItemRenderer());

            for (ChatSearchResult result : results) {
                String person = result.getUsername();
                String question = result.getQuestion();
                String sessionID = result.getSessionID();
                Date date = result.getStartDate();

                final SearchItem item = new SearchItem(person, date, question);
                item.setSessionID(sessionID);
                model.addElement(item);
            }

            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        SearchItem item = list.getSelectedValue();

                        Transcript transcript = null;
                        try {
                            transcript = FastpathPlugin.getAgentSession().getTranscript(item.getSessionID());
                        }
                        catch (XMPPException | SmackException | InterruptedException ee) {
                            Log.error("Error showing transcripts.", ee);
                        }

                        if (transcript == null) {
                            return;
                        }

                        ChatViewer chatViewer = new ChatViewer(transcript);
                        final JFrame frame = new JFrame(FpRes.getString("title.chat.transcript"));
                        frame.setIconImage(SparkManager.getMainWindow().getIconImage());
                        frame.getContentPane().setLayout(new BorderLayout());
                        frame.getContentPane().add(chatViewer, BorderLayout.CENTER);
                        frame.pack();
                        frame.setSize(600, 400);

                        frame.setLocationRelativeTo(SparkManager.getMainWindow());
                        frame.setVisible(true);
                    }
                }
            });


            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.getViewport().setBackground(Color.white);


            JFrame frame = new JFrame(FpRes.getString("title.search.results"));
            frame.setIconImage(SparkManager.getMainWindow().getIconImage());
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            final BackgroundPane titlePane = new BackgroundPane() {
				private static final long serialVersionUID = -5603280927139789177L;

				public Dimension getPreferredSize() {
                    final Dimension size = super.getPreferredSize();
                    size.width = 0;
                    return size;
                }
            };

            titlePane.setLayout(new GridBagLayout());
            titlePane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));


            JLabel userImage = new JLabel(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24));
            userImage.setHorizontalAlignment(JLabel.LEFT);
            userImage.setText(FpRes.getString("title.chat.transcripts.search"));
            titlePane.add(userImage, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            userImage.setFont(new Font("Dialog", Font.BOLD, 12));

            JLabel itemsFound = new JLabel(Integer.toString(results.size()));
            titlePane.add(new JLabel(FpRes.getString("title.number.of.conversations.found")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            titlePane.add(itemsFound, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            JLabel searchTerm = new JLabel(FpRes.getString("query") +": " + query);
            titlePane.add(searchTerm, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));


            frame.getContentPane().add(titlePane, BorderLayout.NORTH);


            frame.pack();
            frame.setSize(400, 400);
            GraphicUtils.centerWindowOnScreen(frame);
            frame.setVisible(true);
        }
        catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }

    }


    /**
     * Sorts all SearchResults by Relevance.
     */
    final Comparator<ChatSearchResult> dateComparator = (o1, o2) -> {

        long int1 = o1.getStartDate().getTime();
        long int2 = o2.getStartDate().getTime();

        return Long.compare(int2, int1);
    };
}
