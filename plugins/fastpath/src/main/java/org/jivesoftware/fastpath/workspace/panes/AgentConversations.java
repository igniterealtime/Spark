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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
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
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPaneListener;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * UI to show all chats occuring.
 */
public final class AgentConversations extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private final DefaultListModel<AgentConversation> model = new DefaultListModel<>();
    private JList<AgentConversation> list;

    private final Map<String, AgentConversation> sessionMap = new HashMap<>();

    /**
     * Add listeners and construct UI.
     */
    public AgentConversations() {
        FastpathPlugin.getUI().getMainPanel().addSparkTabbedPaneListener(new SparkTabbedPaneListener() {
            public void tabRemoved(SparkTab tab, Component component, int index) {
            }

            public void tabAdded(SparkTab tab, Component component, int index) {
            }

            public void tabSelected(SparkTab tab, Component component, int index) {
                stateChanged(null);
            }

            public void allTabsRemoved() {
            }


            public boolean canTabClose(SparkTab tab, Component component) {
                return true;
            }
        });
    }

    private void init() {
        list = new JList<>(model);

        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setForeground(Color.white);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);

        list.setCellRenderer(new FastpathPanelRenderer());

        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                int index = list.locationToIndex(mouseEvent.getPoint());
                list.setSelectedIndex(index);
                checkPopup(mouseEvent);
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                int index = list.locationToIndex(mouseEvent.getPoint());
                list.setSelectedIndex(index);
                checkPopup(mouseEvent);
            }
        });
    }


    private void addAgentChat(EntityBareJid agentID, String name, String email, String question, Date startDate, String sessionID) {
        if (sessionMap.containsKey(sessionID)) {
            return;
        }

        AgentConversation item = new AgentConversation(agentID, name, startDate, email, question, sessionID);
        model.addElement(item);
        sessionMap.put(sessionID, item);
    }

    private void removeConversation(String sessionID) {
        AgentConversation item = sessionMap.get(sessionID);
        if (item != null) {
            model.removeElement(item);
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (FastpathPlugin.getUI().getMainPanel().getSelectedComponent() == this && list == null) {
            init();

            SwingWorker agentWorker = new SwingWorker() {
                AgentRoster agentRoster;
                Collection<EntityBareJid> agentSet;

                public Object construct() {
                    try
                    {
                        agentRoster = FastpathPlugin.getAgentSession().getAgentRoster();
                    }
                    catch ( SmackException.NotConnectedException | InterruptedException e1 )
                    {
                        Log.warning( "Unable to get agent roster.", e1 );
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
                            EntityBareJid agentJID = presence.getFrom().asEntityBareJidOrThrow();
                            AgentStatus agentStatus = presence.getExtension("agent-status", "http://jabber.org/protocol/workgroup");

                            if (agentStatus != null) {
                                List<AgentStatus.ChatInfo> list = agentStatus.getCurrentChats();

                                removeOldChats(agentJID, list);

                                // Add new ones.
                                for (Object o : list) {
                                    AgentStatus.ChatInfo chatInfo = (AgentStatus.ChatInfo) o;
                                    Date startDate = chatInfo.getDate();
                                    String username = chatInfo.getUserID();

                                    String nickname = chatInfo.getUsername();
                                    if (!ModelUtil.hasLength(nickname)) {
                                        nickname = "Not specified";
                                    }

                                    String question = chatInfo.getQuestion();
                                    if (!ModelUtil.hasLength(question)) {
                                        question = "No question asked";
                                    }

                                    String email = chatInfo.getEmail();
                                    if (!ModelUtil.hasLength(email)) {
                                        email = "Not specified";
                                    }
                                    addAgentChat(agentJID, nickname, email, question, startDate, chatInfo.getSessionID());
                                }
                            }
                            calculateNumberOfChats(agentRoster);
                        }
                    });
                }
            };
            agentWorker.start();
        }
    }

    private void calculateNumberOfChats(AgentRoster agentRoster) {
        int counter = 0;
        // TODO: CHECK FASTPATH
        for (EntityBareJid agent : agentRoster.getAgents()) {
            Presence presence = agentRoster.getPresence(agent);
            if (presence.isAvailable()) {
                AgentStatus agentStatus = presence.getExtension("agent-status", "http://jabber.org/protocol/workgroup");
                if (agentStatus != null) {
                    counter += agentStatus.getCurrentChats().size();
                }
            }
        }

        FastpathPlugin.getUI().setTitleForComponent(FpRes.getString("message.current.chats", counter), this);
    }

    private boolean newListHasSession(String sessionID, List<AgentStatus.ChatInfo> chatList) {
        // Add new ones.
        for (AgentStatus.ChatInfo item : chatList) {
            String session = item.getSessionID();
            if (session.equalsIgnoreCase(sessionID)) {
                return true;
            }
        }
        return false;
    }

    private void removeOldChats(EntityBareJid agentJID, List<AgentStatus.ChatInfo> chatList) {
        for (AgentConversation agent : sessionMap.values()) {
            if (agent.getAgentJID().equals(agentJID)) {
                String sessionID = agent.getSessionID();
                boolean listHasID = newListHasSession(sessionID, chatList);
                if (!listHasID) {
                    removeConversation(sessionID);
                }
            }
        }
    }


    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // Check if monitor
            try {
                AgentConversation item = list.getSelectedValue();
                boolean isMonitor = FastpathPlugin.getAgentSession().hasMonitorPrivileges(SparkManager.getConnection());
                if (isMonitor) {
                    JPopupMenu menu = new JPopupMenu();

                    final String sessionID = item.getSessionID();


                    Action joinAction = new AbstractAction() {
						private static final long serialVersionUID = 8239167390330425891L;

						public void actionPerformed(ActionEvent actionEvent) {
                            // Get Conference
                            try {
                                final MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() );
                                List<DomainBareJid> col = multiUserChatManager.getMucServiceDomains();
                                if (col.size() == 0) {
                                    return;
                                }

                                DomainBareJid serviceName = col.iterator().next();
                                EntityBareJid roomName = JidCreate.entityBareFrom(sessionID + "@" + serviceName);

                                LocalPreferences pref = SettingsManager.getLocalPreferences();

                                final Resourcepart nickname = pref.getNickname();
                                MultiUserChat muc = multiUserChatManager.getMultiUserChat( roomName );

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
                                        Affiliate affilitate = iter.next();
                                        Jid jid = affilitate.getJid();
                                        if (!jid.equals(SparkManager.getSessionManager().getUserBareAddress())) {
                                            list.add(jid);
                                        }
                                    }
                                    if (list.size() > 0) {
                                        FillableForm form = muc.getConfigurationForm().getFillableForm();
                                        List<String> listStrings = JidUtil.toStringList(list);
                                        form.setAnswer("muc#roomconfig_roomowners", listStrings);

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
						private static final long serialVersionUID = -2072254190661466657L;

						public void actionPerformed(ActionEvent actionEvent) {

                            // Make user an owner.
                            try {
                                FastpathPlugin.getAgentSession().makeRoomOwner(SparkManager.getConnection(), sessionID);

                                final MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() );
                                List<DomainBareJid> col = multiUserChatManager.getMucServiceDomains();
                                if (col.size() == 0) {
                                    return;
                                }

                                DomainBareJid serviceName = col.iterator().next();
                                EntityBareJid roomName = JidCreate.entityBareFrom(sessionID + "@" + serviceName);

                                LocalPreferences pref = SettingsManager.getLocalPreferences();
                                final Resourcepart nickname = pref.getNickname();
                                MultiUserChat muc = multiUserChatManager.getMultiUserChat( roomName);

                                ConferenceUtils.enterRoom(muc, roomName, nickname, null);

                            }
                            catch (XMPPException | SmackException | InterruptedException | XmppStringprepException e1) {
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


}
