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
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.GraphicUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
 * Adds a simple feature to list your most "Popular" contacts. Popular contacts is basically who
 * you talk with the most.
 */
public class FrequentContactsPlugin implements Plugin {

    private File transcriptDir;

    private final DefaultListModel model = new DefaultListModel();
    private JList contacts;
    private Window window;

    private Map<JLabel, String> jidMap = new HashMap<JLabel, String>();

    public void initialize() {
        transcriptDir = new File(SparkManager.getUserDirectory(), "transcripts");

        contacts = new JList(model);
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

        // Add KeyMappings
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "favoritePeople");
        SparkManager.getMainWindow().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "favoritePeople");
        SparkManager.getMainWindow().getRootPane().getActionMap().put("favoritePeople", new AbstractAction("favoritePeople") {
			private static final long serialVersionUID = 6836584242669218932L;

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
        // Get Transcript Directory
        if (!transcriptDir.exists()) {
            return;
        }

        jidMap.clear();
        model.clear();


        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        for (final String user : getFavoriteContacts()) {
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
     * Returns a collection of your most popular contacts based on previous conversations.
     *
     * @return the collection of favorite people (jids)
     */
    private Collection<String> getFavoriteContacts() {
        if (!transcriptDir.exists()) {
            return Collections.emptyList();
        }

        final File[] transcriptFiles = transcriptDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.contains("_current") && !name.equals("conversations.xml");
            }
        });
        final List<File> files = Arrays.asList(transcriptFiles);

        Collections.sort(files, sizeComparator);

        int size = files.size();
        if (size > 10) {
            size = 10;
        }

        final List<String> jidList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            File file = files.get(i);
            String jid;

            final String fileName = file.getName();
            final int dot = fileName.lastIndexOf('.');
            jid = dot > 0 ? fileName.substring(0, dot) : fileName;

            jidList.add(jid);
        }

        return jidList;
    }


    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
    }

    /**
     * Internal handling of a JLabel Renderer.
     */
    public class InternalRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = -2925096995694392323L;

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

    /**
     * Sorts files by largest to smallest.
     */
    final Comparator<File> sizeComparator = new Comparator<File>() {
        public int compare(File item1, File item2) {
            long int1 = item1.length();
            long int2 = item2.length();

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
