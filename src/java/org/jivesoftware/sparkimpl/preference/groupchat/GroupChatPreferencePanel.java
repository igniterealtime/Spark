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

package org.jivesoftware.sparkimpl.preference.groupchat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

/**
 * The Preference UI used to handle changing of Chat Preferences.
 */
public class GroupChatPreferencePanel extends JPanel {

    private static final long serialVersionUID = -4216417602756915148L;
    private JCheckBox highlightMyName 		= new JCheckBox();
    private JCheckBox highlightMyText 		= new JCheckBox();
    private JCheckBox highlightPopName		= new JCheckBox();
    private JCheckBox showjoinleavemessage 	= new JCheckBox();
    private JCheckBox showroleicons 	= new JCheckBox();
    private JCheckBox _autoAcceptInvites = new JCheckBox();
    private JCheckBox _randomcolors = new JCheckBox();
    private JCheckBox inviteToBookmark = new JCheckBox();

    private JPanel gCPanel = new JPanel();
    /**
     * Constructor invokes UI setup.
     */
    public GroupChatPreferencePanel() {
        // Build the UI
        createUI();
    }

    private void createUI() {
        setLayout(new VerticalFlowLayout());

        ResourceUtils.resButton(highlightMyName		, Res.getString("menuitem.add.groupchat.myname"));
        ResourceUtils.resButton(highlightMyText		, Res.getString("menuitem.add.groupchat.mytext"));
        ResourceUtils.resButton(highlightPopName	, Res.getString("menuitem.add.groupchat.popname"));
        ResourceUtils.resButton(showjoinleavemessage	, Res.getString("menuitem.add.groupchat.showjoinleavemessage"));
        ResourceUtils.resButton(showroleicons		, Res.getString("menuitem.add.groupchat.showrolesinsteadofstatus"));
        ResourceUtils.resButton(_autoAcceptInvites 	, Res.getString("menuitem.add.groupchat.auto.accept.invite"));
        ResourceUtils.resButton(_randomcolors	 	, Res.getString("menuitem.add.groupchat.random.colors"));
        ResourceUtils.resButton(inviteToBookmark       , Res.getString("menuitem.add.groupchat.invitetobookmark"));

        gCPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("title.group.chat.settings")));

        add(gCPanel);

        gCPanel.setLayout(new GridBagLayout());

        gCPanel.add(highlightMyName	, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(highlightMyText	, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(_randomcolors	, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(highlightPopName	, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(showjoinleavemessage, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(showroleicons	, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(_autoAcceptInvites	, new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(inviteToBookmark  , new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

    }

    public void setMucHighNameEnabled(boolean mucNHigh) {
        highlightMyName.setSelected(mucNHigh);
    }

    public void setMucHighTextEnabled(boolean mucTHigh) {
        highlightMyText.setSelected(mucTHigh);
    }

    public void setMuchHighToastEnabled(boolean mucPHigh) {
        highlightPopName.setSelected(mucPHigh);
    }

    public void setShowJoinLeaveMessagesEnabled(boolean mucPHigh) {
	showjoinleavemessage.setSelected(mucPHigh);
    }

    public void setShowRoleIconInsteadStatusIcon(boolean roleicons){
	showroleicons.setSelected(roleicons);
    }

    public boolean isShowJoinLeaveMessagesEnabled() {
        return showjoinleavemessage.isSelected();
    }

    public boolean isMucHighNameEnabled() {
        return highlightMyName.isSelected();
    }

    public boolean isMucHighTextEnabled() {
        return highlightMyText.isSelected();
    }

    public boolean isMucHighToastEnabled() {
        return highlightPopName.isSelected();
    }

    public boolean isShowingRoleIcons() {
	return showroleicons.isSelected();
    }

    public void setAutoAcceptMuc(boolean accept) {
	_autoAcceptInvites.setSelected(accept);
    }

    public boolean isAutoAcceptMuc() {
	return _autoAcceptInvites.isSelected();
    }

    public void setRandomColors(boolean random) {
	_randomcolors.setSelected(random);
    }

    public boolean isRandomColors() {
	return _randomcolors.isSelected();
    }

    public void setInviteToBookmark(boolean invite) {
        inviteToBookmark.setSelected(invite);
    }

    public boolean isInviteToBookmark() {
        return inviteToBookmark.isSelected();
    }


}
