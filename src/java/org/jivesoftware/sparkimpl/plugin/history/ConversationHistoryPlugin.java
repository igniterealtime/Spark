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
package org.jivesoftware.sparkimpl.plugin.history;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 * Allows users to see the last 10 people they have talked with.
 *
 * @author Derek DeMoro
 */
public class ConversationHistoryPlugin implements Plugin {

    private List<String> historyList = new ArrayList<String>();
    private File transcriptDir;
    private File conFile;

    private final DefaultListModel model = new DefaultListModel();
    private JList contacts;
    private Window window;

    private Map<JLabel, String> jidMap = new HashMap<JLabel, String>();

    public void initialize() {
        transcriptDir = new File(SparkManager.getUserDirectory(), "transcripts");
        conFile = new File(transcriptDir, "conversations.xml");

        contacts = new JList(model);
        contacts.setCellRenderer(new InternalRenderer());

        window = new Window(SparkManager.getMainWindow());


        final JPanel mainPanel = new JPanel(new BorderLayout());
        final JLabel titleLabel = new JLabel(Res.getString("label.recent.conversation"));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contacts, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

        window.add(mainPanel);

        // Add Listeners
	contacts.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {

		    contacts.setSelectedIndex(contacts.locationToIndex(e
			    .getPoint()));
		    String user = jidMap.get((JLabel) contacts
			    .getSelectedValue());
		    ContactItem contact = SparkManager.getContactList()
			    .getContactItemByJID(user);
		    SparkManager.getContactList().setSelectedUser(contact.getJID());
		    SparkManager.getContactList().showPopup(contacts, e,
			    contact);
		}

		if (e.getClickCount() == 2) {
		    final JLabel label = (JLabel) contacts.getSelectedValue();
		    String user = jidMap.get(label);
		    if (user != null) {
			final String contactUsername = SparkManager
				.getUserManager().getUserNicknameFromJID(user);
			SparkManager.getChatManager().activateChat(user,
				contactUsername);
			window.dispose();
		    }
		}
	    }
	});

        contacts.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    final JLabel label = (JLabel) contacts.getSelectedValue();
                    String user = jidMap.get(label);
                    if (user != null) {
                        final String contactUsername = SparkManager.getUserManager().getUserNicknameFromJID(user);
                        SparkManager.getChatManager().activateChat(user, contactUsername);
                        window.dispose();
                    }
                }
                else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    window.dispose();
                }
            }
        });

        contacts.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {

            }

            public void focusLost(FocusEvent e) {
                window.dispose();
            }
        });

        // Load Previous History
        loadPreviousHistory();

        // Add Keymapping to ContactList
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "historyPeople");
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "historyPeople");

        SparkManager.getMainWindow().getRootPane().getActionMap().put("historyPeople", new AbstractAction("historyPeople") {
			private static final long serialVersionUID = 2465628887318732082L;

			public void actionPerformed(ActionEvent e) {
                // Show History Popup
                showHistoryPopup();
            }
        });

        // Persist order of conversations.
        SparkManager.getChatManager().addMessageFilter(new MessageFilter() {
            public void filterOutgoing(ChatRoom room, Message message) {
                addUserToHistory(room);
            }

            public void filterIncoming(ChatRoom room, Message message) {
                addUserToHistory(room);
            }
        });
    }

    /**
     * Adds the last user to the history tags.
     *
     * @param room the ChatRoom where the conversation took place.
     */
    private void addUserToHistory(ChatRoom room) {
        if (room instanceof ChatRoomImpl) {
            ChatRoomImpl roomImpl = (ChatRoomImpl) room;
            String jid = roomImpl.getParticipantJID();
            jid = StringUtils.parseBareAddress(jid);
            historyList.remove(jid);
            historyList.add(0, jid);
        }
    }

    /**
     * Displays the Previous Conversation Window.
     */
    private void showHistoryPopup() {
        // Get Transcript Directory
        if (!transcriptDir.exists()) {
            return;
        }

        jidMap.clear();
        model.clear();


        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        int limit = historyList.size() > 10 ? 10 : historyList.size();

        for (final String user : historyList.subList(0, limit)) {

            ContactItem contactItem = contactList.getContactItemByJID(user);
            Icon icon;
            if (contactItem != null) {
                icon = contactItem.getIcon();
                if (icon == null) {
                    icon = SparkRes.getImageIcon(SparkRes.CLEAR_BALL_ICON);
                }

                JLabel label = new JLabel();
                label.setText(contactItem.getDisplayName());
                label.setIcon(icon);

                model.addElement(label);
                jidMap.put(label, user);
            }
        }


        window.setSize(200, 200);
        GraphicUtils.centerWindowOnComponent(window, SparkManager.getMainWindow());


        if (model.size() > 0) {
            contacts.setSelectedIndex(0);
        }

        window.setVisible(true);


    }

    /**
     * Loads the previous history.
     */
    private void loadPreviousHistory() {
        if (!conFile.exists()) {
            return;
        }

        // Otherwise load it.
        try {
            final MXParser parser = new MXParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(conFile), "UTF-8"));
            parser.setInput(in);
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG && "user".equals(parser.getName())) {
                    String jid = StringUtils.parseBareAddress(parser.nextText());
                    historyList.add(jid);
                }
                else if (eventType == XmlPullParser.END_TAG && "conversations".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    public void shutdown() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<conversations>");
        for (String user : historyList) {
            builder.append("<user>").append(user).append("</user>");
        }
        builder.append("</conversations>");

        // Write out to file system.

        if (!transcriptDir.exists()) {
            transcriptDir.mkdirs();
        }

        // Write out new File
        try {
            File conFile = new File(transcriptDir, "conversations.xml");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(conFile), "UTF-8"));
            out.write(builder.toString());
            out.close();
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
    }

    /**
     * Internal handling of a Jlabel Renderer.
     */
    public class InternalRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1812281106979897477L;

		/**
         * Construct Default Renderer.
         */
        public InternalRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            JLabel label = (JLabel) value;
            setText(label.getText());
            setIcon(label.getIcon());
            return this;
        }
    }


}
