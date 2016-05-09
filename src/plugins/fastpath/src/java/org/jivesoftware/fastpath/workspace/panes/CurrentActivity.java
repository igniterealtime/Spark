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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.workgroup.agent.AgentRoster;
import org.jivesoftware.smackx.workgroup.agent.AgentRosterListener;
import org.jivesoftware.smackx.workgroup.packet.AgentStatus;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * UI to show all chats occuring.
 */
public final class CurrentActivity extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);
    private JFrame mainFrame;
    private JLabel activeConversations;
    private int counter = 0;

    /**
     * Add listeners and construct UI.
     */
    public CurrentActivity() {
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());


        final BackgroundPane titlePane = new BackgroundPane() {
			private static final long serialVersionUID = 3127229816651522537L;

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
        userImage.setText(FpRes.getString("title.current.active.conversation"));
        titlePane.add(userImage, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        userImage.setFont(new Font("Dialog", Font.BOLD, 12));

        activeConversations = new JLabel("0");
        titlePane.add(new JLabel(FpRes.getString("title.number.of.active.conversations") +":"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titlePane.add(activeConversations, new GridBagConstraints(1, 1, 1, 3, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        this.add(titlePane, BorderLayout.NORTH);

        this.add(list, BorderLayout.CENTER);

        list.setCellRenderer(new HistoryItemRenderer());

        // Add current chats
        addCurrentChats();

        list.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent mouseEvent) {
                checkPopup(mouseEvent);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                checkPopup(mouseEvent);
            }
        });
    }

    public void addCurrentChats() {
        SwingWorker agentWorker = new SwingWorker() {
            AgentRoster agentRoster;
            Collection agentSet;

            public Object construct() {
                agentRoster = FastpathPlugin.getAgentSession().getAgentRoster();
                agentSet = agentRoster.getAgents();
                return agentSet;
            }

            public void finished() {
                agentRoster.addListener(new AgentRosterListener() {
                    public void agentAdded(String jid) {
                    }

                    public void agentRemoved(String jid) {
                    }

                    public void presenceChanged(Presence presence) {
                        String agentJID = StringUtils.parseBareAddress(presence.getFrom());
                        agentJID = UserManager.unescapeJID(agentJID);
                        AgentStatus agentStatus = (AgentStatus)presence.getExtension("agent-status", "http://jabber.org/protocol/workgroup");

                        String status = presence.getStatus();
                        if (status == null) {
                            status = "Available";
                        }

                        if (agentStatus != null) {
                            List list = agentStatus.getCurrentChats();

                            // Add new ones.
                            Iterator iter = list.iterator();
                            while (iter.hasNext()) {
                                AgentStatus.ChatInfo chatInfo = (AgentStatus.ChatInfo)iter.next();
                                Date startDate = chatInfo.getDate();
                                String username = chatInfo.getUserID();

                                String nickname = chatInfo.getUsername();
                                if (!ModelUtil.hasLength(nickname)) {
                                    nickname = FpRes.getString("message.not.specified");
                                }

                                String question = chatInfo.getQuestion();
                                if (!ModelUtil.hasLength(question)) {
                                    question = "No question asked";
                                }

                                String email = chatInfo.getEmail();
                                if (!ModelUtil.hasLength(email)) {
                                    email = FpRes.getString("message.not.specified");
                                }
                                addAgentChat(agentJID, nickname, email, question, startDate, chatInfo.getSessionID());
                            }
                        }

                    }
                });
            }
        };

        agentWorker.start();
    }


    private void addAgentChat(String agent, String visitor, String email, String question, Date startDate, String session) {
        // Update counter.
        counter++;
        activeConversations.setText(Integer.toString(counter));

        // Conversation Item
        ConversationItem item = new ConversationItem(agent, visitor, startDate, email, question, session);
        model.addElement(item);
    }

    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // Check if monitor
            try {
                boolean isMonitor = FastpathPlugin.getAgentSession().hasMonitorPrivileges(SparkManager.getConnection());
                if (isMonitor) {
                    JPopupMenu menu = new JPopupMenu();

                    int location = list.locationToIndex(e.getPoint());
                    list.setSelectedIndex(location);
                    ConversationItem item = (ConversationItem)list.getSelectedValue();
                    final String sessionID = item.getSessionID();


                    Action joinAction = new AbstractAction() {
						private static final long serialVersionUID = -3198414924157880065L;

						public void actionPerformed(ActionEvent actionEvent) {
                            // Get Conference
                            try {
                                Collection col = MultiUserChat.getServiceNames(SparkManager.getConnection());
                                if (col.size() == 0) {
                                    return;
                                }

                                String serviceName = (String)col.iterator().next();
                                String roomName = sessionID + "@" + serviceName;

                                final LocalPreferences pref = SettingsManager.getLocalPreferences();
                                final String nickname = pref.getNickname();
                                MultiUserChat muc = new MultiUserChat(SparkManager.getConnection(), roomName);

                                ConferenceUtils.enterRoom(muc, roomName, nickname, null);

                                if (muc.isJoined()) {
                                    // Try and remove myself as an owner if I am one.
                                    Collection owners = null;
                                    try {
                                        owners = muc.getOwners();
                                    }
                                    catch (XMPPException e1) {
                                        return;
                                    }
                                    Iterator iter = owners.iterator();

                                    List<String> list = new ArrayList<String>();
                                    while (iter.hasNext()) {
                                        Affiliate affilitate = (Affiliate)iter.next();
                                        String jid = affilitate.getJid();
                                        if (!jid.equals(SparkManager.getSessionManager().getBareAddress())) {
                                            list.add(jid);
                                        }
                                    }
                                    if (list.size() > 0) {
                                        Form form = muc.getConfigurationForm().createAnswerForm();
                                        form.setAnswer("muc#roomconfig_roomowners", list);

                                        // new DataFormDialog(groupChat, form);
                                        muc.sendConfigurationForm(form);
                                    }
                                }
                            }
                            catch (Exception e1) {
                                Log.error(e1);
                            }
                        }
                    };

                    joinAction.putValue(Action.NAME, FpRes.getString("menuitem.join.chat"));
                    menu.add(joinAction);

                    Action monitorAction = new AbstractAction() {
						private static final long serialVersionUID = 7292337790553806820L;

						public void actionPerformed(ActionEvent actionEvent) {

                            // Make user an owner.
                            try {
                                FastpathPlugin.getAgentSession().makeRoomOwner(SparkManager.getConnection(), sessionID);

                                Collection<String> col = MultiUserChat.getServiceNames(SparkManager.getConnection());
                                if (col.size() == 0) {
                                    return;
                                }

                                String serviceName = (String)col.iterator().next();
                                String roomName = sessionID + "@" + serviceName;

                                LocalPreferences pref = SettingsManager.getLocalPreferences();
                                final String nickname = pref.getNickname();
                                MultiUserChat muc = new MultiUserChat(SparkManager.getConnection(), roomName);

                                ConferenceUtils.enterRoom(muc, roomName, nickname, null);

                            }
                            catch (XMPPException e1) {
                                Log.error(e1);
                            }
                        }
                    };

                    monitorAction.putValue(Action.NAME, FpRes.getString("menuitem.monitor.chat"));
                    menu.add(monitorAction);
                    menu.show(list, e.getX(), e.getY());
                }
            }
            catch (XMPPException e1) {
                Log.error(e1);
                return;
            }
        }
    }

    public void showDialog() {
        if (mainFrame == null) {
            mainFrame = new JFrame(FpRes.getString("title.current.conversations"));
        }
        if (mainFrame.isVisible()) {
            return;
        }
        mainFrame.setIconImage(SparkManager.getMainWindow().getIconImage());
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.getContentPane().add(new JScrollPane(this));
        mainFrame.pack();
        mainFrame.setSize(400, 400);
        mainFrame.setLocationRelativeTo(SparkManager.getMainWindow());
        mainFrame.setVisible(true);
    }


}