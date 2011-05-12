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
package org.jivesoftware.sparkimpl.preference.chat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * The Preference UI used to handle changing of Chat Preferences.
 */
public class ChatPreferencePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8910938026549098719L;
	private JCheckBox showTimeBox = new JCheckBox();
    private ButtonGroup timeFormat = new ButtonGroup();
    private JRadioButton format12 = new JRadioButton("12:00 PM", true);
    private JRadioButton format24 = new JRadioButton("24:00", false);
    private JCheckBox groupChatNotificationBox = new JCheckBox();
    private JPanel generalPanel = new JPanel();
    private JPanel chatWindowPanel = new JPanel();

    // Password changing
    private JPasswordField passwordField = new JPasswordField();
    private JPasswordField confirmationPasswordField = new JPasswordField();
    private JLabel passwordLabel = new JLabel();
    private JLabel confirmationPasswordLabel = new JLabel();
    private JCheckBox hideChatHistory = new JCheckBox();
    private JCheckBox hidePrevChatHistory = new JCheckBox();
    private JCheckBox tabsOnTopBox = new JCheckBox();
    private JTextField chatTimeoutField = new JTextField();
    private JCheckBox buzzBox = new JCheckBox();

    /**
     * Constructor invokes UI setup.
     */
    public ChatPreferencePanel() {
        // Build the UI
        createUI();
    }

    private void createUI() {
        setLayout(new VerticalFlowLayout());
        timeFormat.add(format24);
        timeFormat.add(format12);
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        if(pref.getTimeFormat().equals("HH:mm"))
        {
      	  format24.setSelected(true);
        }
        else
        {
      	  format12.setSelected(true);
        }
        // Setup Mnemonics
        ResourceUtils.resButton(showTimeBox, Res.getString("checkbox.show.time.in.chat.window"));
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.change.password.to") + ":");
        ResourceUtils.resLabel(confirmationPasswordLabel, confirmationPasswordField, Res.getString("label.confirm.password") + ":");
        ResourceUtils.resButton(groupChatNotificationBox, Res.getString("checkbox.show.notifications.in.conference"));
        ResourceUtils.resButton(hideChatHistory, Res.getString("checkbox.disable.chat.history"));
        ResourceUtils.resButton(hidePrevChatHistory, Res.getString("checkbox.disable.prev.chat.history"));
        ResourceUtils.resButton(tabsOnTopBox, Res.getString("checkbox.tabs.on.top"));
        ResourceUtils.resButton(buzzBox, Res.getString("checkbox.allow.buzz"));

        generalPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("group.general.information")));
        chatWindowPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("group.chat.window.information")));

        if (!Default.getBoolean(Default.CHANGE_PASSWORD_DISABLED)) {
        	add(generalPanel);
        }
        add(chatWindowPanel);

        generalPanel.setLayout(new GridBagLayout());
        chatWindowPanel.setLayout(new GridBagLayout());

        // Chat Window Panel settings
        chatWindowPanel.add(showTimeBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(format24, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(format12, new GridBagConstraints(2, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        chatWindowPanel.add(groupChatNotificationBox, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(hideChatHistory, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(hidePrevChatHistory, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(tabsOnTopBox, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(buzzBox, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
       
        JLabel chatTimeoutLabel = new JLabel();
        ResourceUtils.resLabel(chatTimeoutLabel, chatTimeoutField, Res.getString("label.minutes.before.stale.chat") + ":");
        chatWindowPanel.add(chatTimeoutLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(chatTimeoutField, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));


        generalPanel.add(passwordLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(passwordField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));
        generalPanel.add(confirmationPasswordLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(confirmationPasswordField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));
        
        showTimeBox.addActionListener(new ActionListener()
 	     {
      	  public void actionPerformed(ActionEvent e)
      	  {
      		  if(showTimeBox.isSelected())
      		  {
      			  format12.setEnabled(true);
      			  format24.setEnabled(true);
      		  }
      		  else
      		  {
      			  format12.setEnabled(false);
      			  format24.setEnabled(false);
      		  }
      	  }
 	     });
        
        hideChatHistory.addActionListener(this);
    }

    /**
     * Set to true to have the ChatWindow show the timestamp of each message.
     *
     * @param showTime true to show timestamp of each message.
     */
    public void setShowTime(boolean showTime) {
        showTimeBox.setSelected(showTime);
    }

    /**
     * Returns true if the ChatWindow should show a timestamp of each message.
     *
     * @return true if the ChatWindow should show a timestamp of each message.
     */
    public boolean getShowTime() {
        return showTimeBox.isSelected();
    }
    
    public String getFormatTime() {
       if(format24.isSelected())
       {
      	 return "HH:mm";
       }
       else
       {
      	 return "h:mm a"; 
       }
    }

    /**
     * Returns the new password to use.
     *
     * @return the new password to use.
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Returns the confirmation password used to compare to the first password.
     *
     * @return the confirmation password used to compare to the first password.
     */
    public String getConfirmationPassword() {
        return new String(confirmationPasswordField.getPassword());
    }

    public void setGroupChatNotificationsOn(boolean on) {
        groupChatNotificationBox.setSelected(on);
    }

    public boolean isGroupChatNotificationsOn() {
        return groupChatNotificationBox.isSelected();
    }

    public void setChatHistoryHidden(boolean hide) {
        hideChatHistory.setSelected(hide);
    }

    public boolean isChatHistoryHidden() {
        return hideChatHistory.isSelected();
    }
    
    public void setPrevChatHistoryHidden(boolean hide) {
        hidePrevChatHistory.setSelected(hide);
    }

    public boolean isPrevChatHistoryHidden() {
        return hidePrevChatHistory.isSelected();
    }

    public void setChatTimeoutTime(int time) {
        chatTimeoutField.setText(Integer.toString(time));
    }

    public void setTabsOnTop(boolean top){
        tabsOnTopBox.setSelected(top);
    }

    public boolean isTabsOnTop(){
        return tabsOnTopBox.isSelected();
    }

    public void setBuzzEnabled(boolean allowBuzz){
        buzzBox.setSelected(allowBuzz);
    }

    public boolean isBuzzEnabled(){
        return buzzBox.isSelected();
    }

    public int getChatTimeoutTime() {
        try {
            return Integer.parseInt(chatTimeoutField.getText());
        }
        catch (NumberFormatException e) {
            return 15;
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (hideChatHistory.isSelected()) {
            int ok = JOptionPane.showConfirmDialog(this, Res.getString("message.delete.all.history"), Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                File transcriptDir = new File(SparkManager.getUserDirectory(), "transcripts");
                File[] files = transcriptDir.listFiles();

                hidePrevChatHistory.setEnabled(false);
                hidePrevChatHistory.setSelected(false);

                for (File transcriptFile : files) {
                    transcriptFile.delete();
                }
            }
    } else {
            hidePrevChatHistory.setEnabled(true);            
        }
    }

}
