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
package org.jivesoftware.sparkimpl.plugin.history;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

/**
 * Adds a simple feature to list your most "Popular" contacts.
 * Popular contacts is basically who you talk with the most.
 */
public class FrequentContactsPlugin implements Plugin {

    private File transcriptDir = SparkManager.getTranscriptDir();

    private final DefaultListModel<JLabel> model = new DefaultListModel<>();
    private JList<JLabel> contacts;
    private Window window;

    private final Map<JLabel, EntityBareJid> jidMap = new HashMap<>();

    @Override
	public void initialize() {
        contacts = new JList<>(model);
        contacts.setCellRenderer(new InternalRenderer());

        window = new Window(SparkManager.getMainWindow());
        final JPanel mainPanel = new JPanel(new BorderLayout());
        final JLabel titleLabel = new JLabel(Res.getString("label.frequent.contacts"));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contacts, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

        window.add(mainPanel);
        // Add Listeners
	contacts.addMouseListener(new MouseAdapter() {
	    @Override
		public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {

		    contacts.setSelectedIndex(contacts.locationToIndex(e
			    .getPoint()));
            EntityBareJid user = jidMap.get(contacts.getSelectedValue());
		    ContactItem contact = SparkManager.getContactList().getContactItemByJID(user);
		    SparkManager.getContactList().setSelectedUser(contact.getJid());
		    SparkManager.getContactList().showPopup(contacts, e,
			    contact);
		}

		if (e.getClickCount() == 2) {
		    final JLabel label = contacts.getSelectedValue();
            EntityBareJid user = jidMap.get(label);
            if (user != null) {
                String contactUsername = SparkManager.getUserManager().getUserNicknameFromJID(user);
                SparkManager.getChatManager().activateChat(user, contactUsername);
                window.dispose();
            }
		}
	    }
	});

        contacts.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    final JLabel label = contacts.getSelectedValue();
                    EntityBareJid user = jidMap.get(label);
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
            @Override
			public void focusGained(FocusEvent e) {
            }

            @Override
			public void focusLost(FocusEvent e) {
                window.dispose();
            }
        });

        // Add KeyMappings
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "favoritePeople");
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "favoritePeople");
        SparkManager.getMainWindow().getRootPane().getActionMap().put("favoritePeople", new AbstractAction("favoritePeople") {
			@Override
			public void actionPerformed(ActionEvent e) {
                // Show History Popup
                showPopup();
            }
        });
    }

    /**
     * Displays your favorite contacts.
     */
    private void showPopup() {
        jidMap.clear();
        model.clear();
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        for (EntityBareJid user : getFavoriteContacts()) {
            ContactItem contactItem = contactList.getContactItemByJID(user);
            Icon icon;
            if (contactItem != null) {
                icon = contactItem.getIcon();
                if (icon == null) {
                    icon = SparkRes.getImageIcon(SparkRes.Icon.CLEAR_BALL_ICON);
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
        if (!model.isEmpty()) {
            contacts.setSelectedIndex(0);
        }
        window.setVisible(true);
    }

    /**
     * Returns a collection of your most popular contacts based on previous conversations.
     */
    private List<EntityBareJid> getFavoriteContacts() {
        final File[] transcriptFiles = transcriptDir.listFiles( ( dir, name ) -> !name.contains("_current") && !name.equals("conversations.xml") );
        if (transcriptFiles == null || transcriptFiles.length == 0) {
            return List.of();
        }
        Arrays.sort(transcriptFiles, sizeComparator);
        int size = Math.min(transcriptFiles.length, 10);
        final List<EntityBareJid> jidList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            File file = transcriptFiles[i];
            String jid;
            final String fileName = file.getName();
            final int dot = fileName.lastIndexOf('.');
            jid = dot > 0 ? fileName.substring(0, dot) : fileName;
            EntityBareJid entityBareJid = JidCreate.entityBareFromOrNull(jid);
            if (entityBareJid != null) {
                jidList.add(entityBareJid);
            }
        }
        return jidList;
    }

    @Override
	public void shutdown() {
    }

    @Override
	public boolean canShutDown() {
        return true;
    }

    @Override
	public void uninstall() {
    }

    /**
     * Internal handling of a JLabel Renderer.
     */
    public static class InternalRenderer extends JLabel implements ListCellRenderer<Object> {
        public InternalRenderer() {
            setOpaque(true);
        }

        @Override
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

    /**
     * Sorts files by largest to smallest.
     */
    final Comparator<File> sizeComparator = ( item1, item2 ) -> {
        long int1 = item1.length();
        long int2 = item2.length();

        return Long.compare(int2, int1);
    };

}
