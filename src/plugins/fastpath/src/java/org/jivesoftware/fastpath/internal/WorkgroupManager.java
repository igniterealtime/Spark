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
package org.jivesoftware.fastpath.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.workgroup.settings.ChatSetting;
import org.jivesoftware.smackx.workgroup.settings.ChatSettings;
import org.jivesoftware.smackx.workgroup.user.Workgroup;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.ContactListListener;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.conferences.RoomInvitationListener;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;


/**
 * Responsible for retrieving and writing out workgroup form information.
 */
public class WorkgroupManager {
    /**
     * Stores the ChatSettings of each workgroup, and will be updated
     * when packet date of workgroup changes.
     */
    private Map<String, ChatSettings> chatSettings = new HashMap<String, ChatSettings>();
    private Set<String> invites = new HashSet<String>();


    private static WorkgroupManager singleton;
    private static final Object LOCK = new Object();

    /**
     * Returns the singleton instance of <CODE>WorkgroupManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>WorkgroupManager</CODE>
     */
    public static WorkgroupManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                WorkgroupManager controller = new WorkgroupManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private WorkgroupManager() {
        // Add own invitation listener to handle invites after the queue.
        SparkManager.getChatManager().addInvitationListener(new InviteListener());

        // Always check for new contact item to see if it's a workgroup.
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContactListListener(new ContactListListener() {
            public void contactItemAdded(ContactItem item) {
                handleContactItem(item);
            }

            public void contactItemRemoved(ContactItem item) {

            }

            public void contactGroupAdded(ContactGroup group) {

            }

            public void contactGroupRemoved(ContactGroup group) {

            }


            public void contactItemDoubleClicked(ContactItem item) {
            }


            public void contactItemClicked(ContactItem item) {
            }
        });
    }

    /**
     * Returns the chat settings for a particular workgroup.
     *
     * @param key           the key in the chat settings.
     * @param workgroupName the name of the workgroup
     * @return the <code>ChatSetting</code> found with the specified key.
     */
    public ChatSetting getChatSetting(String key, String workgroupName) {
        ChatSettings settings = null;
        if (chatSettings.containsKey(workgroupName)) {
            settings = (ChatSettings)chatSettings.get(workgroupName);
        }
        else {
            XMPPConnection connection = SparkManager.getConnection();
            Workgroup workgroup = new Workgroup(workgroupName, connection);
            try {
                settings = workgroup.getChatSettings();
                chatSettings.put(workgroupName, settings);
            }
            catch (XMPPException e) {
                Log.error("Error retrieving chat setting using key=" + key + " and workgroup=" + workgroupName, e);
            }
        }
        if (settings != null) {
            return settings.getChatSetting(key);
        }
        return null;
    }


    private void showWorkgroup(final ContactItem contactItem) throws Exception {
        VCard vcard = SparkManager.getVCardManager().getVCard();

        final Map<String, String> variables = new HashMap<String, String>();
        String firstName = vcard.getFirstName();
        String lastName = vcard.getLastName();

        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            variables.put("username", firstName + " " + lastName);
        }
        else if (ModelUtil.hasLength(firstName)) {
            variables.put("username", firstName);
        }
        else if (ModelUtil.hasLength(lastName)) {
            variables.put("username", lastName);
        }

        String email = vcard.getEmailHome();
        String emailWork = vcard.getEmailWork();

        if (ModelUtil.hasLength(emailWork)) {
            email = emailWork;
        }

        if (ModelUtil.hasLength(email)) {
            variables.put("email", email);
        }


        String workgroupJID = contactItem.getJID();
        String nameOfWorkgroup = StringUtils.parseName(workgroupJID);
        final JDialog workgroupDialog = new JDialog(SparkManager.getMainWindow(), "Contact " + nameOfWorkgroup + " Workgroup");
        Workgroup workgroup = new Workgroup(workgroupJID, SparkManager.getConnection());

        final Form workgroupForm = workgroup.getWorkgroupForm();
        String welcomeText = FormText.getWelcomeText(workgroupJID);
        String startButton = FormText.getStartButton(workgroupJID);

        final WorkgroupDataForm formUI = new WorkgroupDataForm(workgroupForm, variables);
        formUI.setBackground(Color.white);

        final JPanel titlePane = new LiveTitlePane("Contact Workgroup", FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24)) {
			private static final long serialVersionUID = -4484940286068835770L;

			public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                size.width = 400;
                return size;
            }
        };


        formUI.add(titlePane, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final JButton submitButton = new JButton("Start Chat!");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateForm(workgroupDialog, workgroupForm, formUI.getFilledForm())) {
                    enterQueue(contactItem.getJID(), formUI.getFilledForm());
                    workgroupDialog.dispose();
                }
            }
        });


        formUI.setEnterListener(new WorkgroupDataForm.EnterListener() {
            public void enterPressed() {
                if (validateForm(workgroupDialog, workgroupForm, formUI.getFilledForm())) {
                    enterQueue(contactItem.getJID(), formUI.getFilledForm());
                    workgroupDialog.dispose();
                }
            }
        });


        formUI.add(submitButton, new GridBagConstraints(1, 100, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        workgroupDialog.getContentPane().setLayout(new BorderLayout());
        workgroupDialog.getContentPane().add(formUI, BorderLayout.CENTER);

        workgroupDialog.pack();
        GraphicUtils.centerWindowOnScreen(workgroupDialog);
        workgroupDialog.setVisible(true);
    }

    private static boolean validateForm(JDialog parent, Form workgroupForm, Form form) {
        Iterator iter = form.getFields();
        while (iter.hasNext()) {
            FormField field = (FormField)iter.next();
            if (field.isRequired() && !field.getValues().hasNext()) {
                String variable = field.getVariable();
                String elementName = workgroupForm.getField(variable).getLabel();
                JOptionPane.showMessageDialog(parent, variable + " is required to complete the form.", "Incomplete Form", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public void handleContactItem(final ContactItem contactItem) {
        Presence presence = contactItem.getPresence();

        PacketExtension workgroup = presence.getExtension("workgroup", "http://jivesoftware.com/protocol/workgroup");
        PacketExtension notifyQueue = presence.getExtension("notify-queue", "http://jabber.org/protocol/workgroup");

        if (workgroup == null && notifyQueue == null) {
            return;
        }

        if (!presence.isAway()){
            contactItem.setIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
        }
        else {
            contactItem.setIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_OFFLINE_IMAGE_16x16));
            contactItem.setStatus(null);
        }
    }

    private void enterQueue(String workgroupJID, Form form) {
        String workgroupName = StringUtils.parseName(workgroupJID).toUpperCase();

        final JDialog workgroupDialog = new JDialog(SparkManager.getMainWindow(), workgroupName + " Workgroup");


        final Workgroup workgroup = new Workgroup(workgroupJID, SparkManager.getConnection());
        try {
            workgroup.joinQueue(form);
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        final JPanel titlePane = new LiveTitlePane("Waiting in Queue", FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24)) {
			private static final long serialVersionUID = -7370226759188539384L;

			public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                size.width = 400;
                return size;
            }
        };


        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(titlePane, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final JLabel queuePostionLabel = new JLabel();
        final JLabel queueWaitTime = new JLabel();
        queuePostionLabel.setText("Waiting for position information.");
        queueWaitTime.setText("Gathering wait time.");

        mainPanel.add(queuePostionLabel, new GridBagConstraints(0, 1, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(queueWaitTime, new GridBagConstraints(0, 2, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final JButton leaveQueueButton = new JButton("Leave Queue");
        mainPanel.add(queueWaitTime, new GridBagConstraints(0, 3, 4, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        leaveQueueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (workgroup.isInQueue()) {
                    try {
                        invites.add(workgroup.getWorkgroupJID());
                        workgroup.departQueue();
                    }
                    catch (XMPPException e1) {
                        Log.error(e1);
                    }
                }
            }
        });

        mainPanel.add(leaveQueueButton, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        mainPanel.setBackground(Color.white);

        workgroupDialog.getContentPane().add(mainPanel);
        workgroupDialog.pack();
        GraphicUtils.centerWindowOnScreen(workgroupDialog);
        workgroupDialog.setVisible(true);

        workgroupDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (workgroup.isInQueue()) {
                    try {
                        workgroup.departQueue();
                    }
                    catch (XMPPException e1) {
                        Log.error(e1);
                    }
                }
            }
        });

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                while (true) {
                    if (workgroup.isInQueue()) {
                        queuePostionLabel.setText("Current Position in Queue: " + workgroup.getQueuePosition());
                        queueWaitTime.setText("It is estimated your wait time to now be " + FormUtils.getTimeFromLong(workgroup.getQueueRemainingTime()));
                    }
                    else {
                        return true;
                    }

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        Log.error(e);
                    }

                }
            }

            public void finished() {
                workgroupDialog.setVisible(false);

                if (invites.contains(workgroup.getWorkgroupJID())) {
                    invites.remove(workgroup.getWorkgroupJID());
                }
                else {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Unable to contact a member of the workgroup. Please try back later.", "No Answer", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };

        worker.start();
    }

    private class InviteListener implements RoomInvitationListener {
        // Add own invitation listener
    	@Override
        public boolean handleInvitation(final Connection conn, final String room, final String inviter, final String reason, final String password, final Message message) {
            invites.add(inviter);

            if (message.getExtension("workgroup", "http://jabber.org/protocol/workgroup") != null) {
                String workgroupName = StringUtils.parseName(inviter);
                GroupChatRoom groupChatRoom = ConferenceUtils.enterRoomOnSameThread(workgroupName, room, password);

                int tabLocation = SparkManager.getChatManager().getChatContainer().indexOfComponent(groupChatRoom);
                groupChatRoom.setTabIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
                if (tabLocation != -1) {
                    SparkTab tab = SparkManager.getChatManager().getChatContainer().getTabAt(tabLocation);
                    tab.setIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
                }
                return true;
            }

            return false;
        }
    }
}
