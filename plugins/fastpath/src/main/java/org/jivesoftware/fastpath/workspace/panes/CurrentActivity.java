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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.workgroup.agent.AgentRoster;
import org.jivesoftware.smackx.workgroup.agent.AgentRosterListener;
import org.jivesoftware.smackx.workgroup.packet.AgentStatus;
import org.jivesoftware.smackx.workgroup.packet.AgentStatus.ChatInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;

/**
 * UI to show all chats occuring.
 */
public final class CurrentActivity extends JPanel {

	private static final long serialVersionUID = 1L;
	private final DefaultListModel<ConversationItem> model = new DefaultListModel<>();
    private final JList<ConversationItem> list = new JList<>(model);
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
            Collection<EntityBareJid> agentSet;

            public Object construct() {
                try
                {
                    agentRoster = FastpathPlugin.getAgentSession().getAgentRoster();
                }
                catch ( SmackException.NotConnectedException | InterruptedException e )
                {
                    Log.error( "Unable to get agent roster.", e );
                }
                agentSet = agentRoster.getAgents();
                return agentSet;
            }

            public void finished() {
                agentRoster.addListener(new AgentRosterListener() {
                    @Override
                    public void agentAdded(EntityBareJid jid) {
                    }

                    @Override
                    public void agentRemoved(EntityBareJid jid) {
                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        BareJid agentJID = presence.getFrom().asBareJid();
                        AgentStatus agentStatus = presence.getExtension("agent-status", "http://jabber.org/protocol/workgroup");

                        if (agentStatus != null) {
                            List<ChatInfo> list = agentStatus.getCurrentChats();

                            // Add new ones.
                            for (ChatInfo chatInfo : list) {
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


    private void addAgentChat(Jid agent, String visitor, String email, String question, Date startDate, String session) {
        // Update counter.
        counter++;
        activeConversations.setText(Integer.toString(counter));

        // Conversation Item
        ConversationItem item = new ConversationItem(agent.asUnescapedString(), visitor, startDate, email, question, session);
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
                    ConversationItem item = list.getSelectedValue();
                    final String sessionID = item.getSessionID();


                    Action joinAction = new AbstractAction() {
						private static final long serialVersionUID = -3198414924157880065L;

						public void actionPerformed(ActionEvent actionEvent) {
                            // Get Conference
                            try {
                                final MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() );
                                List<DomainBareJid> col = multiUserChatManager.getMucServiceDomains();
                                if (col.size() == 0) {
                                    return;
                                }

                                DomainBareJid serviceName = col.iterator().next();
                                EntityBareJid roomName = JidCreate.entityBareFromOrThrowUnchecked(sessionID + "@" + serviceName);

                                final LocalPreferences pref = SettingsManager.getLocalPreferences();
                                final Resourcepart nickname = pref.getNickname();
                                MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomName);

                                ConferenceUtils.enterRoom(muc, roomName, nickname, null);

                                if (muc.isJoined()) {
                                    // Try and remove myself as an owner if I am one.
                                    Collection<Affiliate> owners;
                                    try {
                                        owners = muc.getOwners();
                                    }
                                    catch (XMPPException | SmackException e1) {
                                        return;
                                    }
                                    Iterator<Affiliate> iter = owners.iterator();

                                    List<Jid> list = new ArrayList<>();
                                    while (iter.hasNext()) {
                                        Affiliate affiliate = iter.next();
                                        Jid jid = affiliate.getJid();
                                        if (!jid.equals(SparkManager.getSessionManager().getUserBareAddress())) {
                                            list.add(jid);
                                        }
                                    }
                                    if (list.size() > 0) {
                                        FillableForm form = muc.getConfigurationForm().getFillableForm();
                                        List<String> jidStrings = new ArrayList<>(list.size());
                                        JidUtil.toStrings(list, jidStrings);
                                        form.setAnswer("muc#roomconfig_roomowners", jidStrings);

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
                                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() );
                                List<DomainBareJid> col = manager.getMucServiceDomains();
                                if (col.size() == 0) {
                                    return;
                                }

                                DomainBareJid serviceName = col.iterator().next();
                                EntityBareJid roomName = JidCreate.entityBareFromOrThrowUnchecked(sessionID + "@" + serviceName);

                                LocalPreferences pref = SettingsManager.getLocalPreferences();
                                final Resourcepart nickname = pref.getNickname();
                                MultiUserChat muc = manager.getMultiUserChat( roomName );

                                ConferenceUtils.enterRoom(muc, roomName, nickname, null);
                            }
                            catch (XMPPException | SmackException | InterruptedException e1) {
                                Log.error(e1);
                            }
                        }
                    };

                    monitorAction.putValue(Action.NAME, FpRes.getString("menuitem.monitor.chat"));
                    menu.add(monitorAction);
                    menu.show(list, e.getX(), e.getY());
                }
            }
            catch (XMPPException | SmackException | InterruptedException e1) {
                Log.error(e1);
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
