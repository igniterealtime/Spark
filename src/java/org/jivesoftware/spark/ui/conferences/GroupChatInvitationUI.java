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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.ChatNotFoundException;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Conference Invitation UI.
 *
 * @author Derek DeMoro
 */
public class GroupChatInvitationUI extends JPanel implements ActionListener {

    private static final long serialVersionUID = 6066796370413837508L;

    private RolloverButton acceptButton;

    private String room;
    private String inviter;
    private String password;

    public GroupChatInvitationUI(String room, String inviter, String password, String reason) {
        setLayout(new GridBagLayout());

        setBackground(new Color(230, 239, 249));

        this.room = room;
        this.inviter = inviter;
        this.password = password;

        // Build invitation time label.
        final Date now = new Date();
        String invitationDateFormat = ( (SimpleDateFormat) SimpleDateFormat.getTimeInstance( SimpleDateFormat.MEDIUM ) ).toPattern();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat( invitationDateFormat );
        final String invitationTime = dateFormatter.format(now);

        // Get users nickname, if there is one.
        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(inviter);


        JLabel iconLabel = new JLabel(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_48x48));

        JTextPane titleLabel = new JTextPane();
        titleLabel.setOpaque(false);
        titleLabel.setEditable(false);
        titleLabel.setBackground(new Color(230, 239, 249));

        acceptButton = new RolloverButton(Res.getString("button.accept").replace("&",""), SparkRes.getImageIcon(SparkRes.ACCEPT_INVITE_IMAGE));
        acceptButton.setForeground(new Color(63, 158, 61));

        RolloverButton rejectButton = new RolloverButton(Res.getString("button.reject"), SparkRes.getImageIcon(SparkRes.REJECT_INVITE_IMAGE));
        rejectButton.setForeground(new Color(185, 33, 33));

        add(iconLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        add(acceptButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        add(rejectButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

        final SimpleAttributeSet styles = new SimpleAttributeSet();
        StyleConstants.setForeground(styles, new Color(13, 104, 196));

        Document document = titleLabel.getDocument();
        try {
            document.insertString(0, "[" + invitationTime + "] ", styles);
            StyleConstants.setBold(styles, true);            
            document.insertString(document.getLength(), Res.getString("message.invite.to.groupchat",nickname) , styles);

            if (ModelUtil.hasLength(reason)) {
                StyleConstants.setBold(styles, false);
                document.insertString(document.getLength(), "\nMessage: " + reason, styles);
            }
        }
        catch (BadLocationException e) {
            Log.error(e);
        }

        acceptButton.addActionListener(this);
        rejectButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == acceptButton) {
            acceptInvitation();
        }
        else {
            rejectInvitation();
        }
    }

    /**
     * Action taking when a user clicks on the accept button.
     */
    private void acceptInvitation() {
        setVisible(false);
        String name = XmppStringUtils.parseLocalpart(room);
        ConferenceUtils.enterRoomOnSameThread(name, room, password);

        final TimerTask removeUITask = new SwingTimerTask() {
            public void doRun() {
                removeUI();
            }
        };

        TaskEngine.getInstance().schedule(removeUITask, 2000);
    }


    /**
     * Action taking when a user clicks on the reject button.
     */
    private void rejectInvitation()
    {
        removeUI();

        try {
            ChatRoom chatRoom = SparkManager.getChatManager().getGroupChat(room);
            if (chatRoom instanceof GroupChatRoom) {
                GroupChatRoom gcr = (GroupChatRoom)chatRoom;
                if (!gcr.getMultiUserChat().isJoined()) {
                    chatRoom.closeChatRoom();
                }
            }
        }
        catch (ChatNotFoundException e) {
            // Ignore
        }

        try
        {
            MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).decline( room, inviter, "No thank you");
        }
        catch ( SmackException.NotConnectedException e )
        {
            Log.warning( "Unable to decline inviation from " + inviter + " to join room " + room, e );
        }
    }

    /**
     * Removes this interface from it's parent.
     */
    private void removeUI() {
        final Container par = getParent();
        if (par != null) {
            par.remove(this);
            par.invalidate();
            par.validate();
            par.repaint();
        }

    }
}
