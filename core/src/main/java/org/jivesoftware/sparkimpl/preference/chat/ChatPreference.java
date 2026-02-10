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
package org.jivesoftware.sparkimpl.preference.chat;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Handles the preferences for Chatting. This handles preferences used in chatting such as the nickname
 * to be used and showing dates and times of chat posts.
 */
public class ChatPreference implements Preference {
    private final ChatPreferencePanel panel = new ChatPreferencePanel();
    private String errorMessage = "Error";

    /**
     * Define the Namespace used for this preference.
     */
    public static final String NAMESPACE = "http://www.jivesoftware.org/spark/chatwindow";

    @Override
	public String getTitle() {
        return Res.getString("title.general.chat.settings");
    }

    @Override
	public String getListName() {
        return Res.getString("title.chat");
    }

    @Override
	public String getTooltip() {
        return Res.getString("title.general.chat.settings");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.USER1_MESSAGE_24x24);
    }

    @Override
	public void load() {
        SwingWorker thread = new SwingWorker() {
            LocalPreferences localPreferences;

            @Override
			public Object construct() {
                localPreferences = SettingsManager.getLocalPreferences();
                return localPreferences;
            }

            @Override
			public void finished() {
                boolean showTime = localPreferences.isTimeDisplayedInChat();
                boolean notificationsOn = localPreferences.isChatRoomNotificationsOn();
                boolean chatHistoryHidden = !localPreferences.isChatHistoryEnabled();
                boolean prevChatHistoryHidden = !localPreferences.isPrevChatHistoryEnabled();
                boolean tabsOnTop = localPreferences.isTabTopPosition();
                boolean buzzAllowed = localPreferences.isBuzzEnabled();
                boolean closeUnreadMessage = localPreferences.isCloseUnreadMessage();
                boolean isChatHistoryAscending = localPreferences.getChatHistoryAscending();
                boolean tabsScroll = localPreferences.isTabsScroll();
                panel.setShowTime(showTime);
                panel.setGroupChatNotificationsOn(notificationsOn);
                panel.setChatHistoryHidden(chatHistoryHidden);
                panel.setPrevChatHistoryHidden(prevChatHistoryHidden);
                panel.setChatTimeoutTime(localPreferences.getChatLengthDefaultTimeout());
                panel.setTabsOnTop(tabsOnTop);
                panel.setTabsScrollBox(tabsScroll);
                panel.setBuzzEnabled(buzzAllowed);
                panel.setCloseUnreadMessageEnabled(closeUnreadMessage);
                panel.setSortChatHistoryAscending(isChatHistoryAscending);
            }
        };

        thread.start();

    }

    @Override
	public void commit() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        pref.setTimeDisplayedInChat(panel.getShowTime());
        if( panel.getShowTime() )
        {
      	  pref.setTimeFormat(panel.getFormatTime());
        }
        pref.setChatRoomNotifications(panel.isGroupChatNotificationsOn());
        pref.setChatHistoryEnabled(!panel.isChatHistoryHidden());
        pref.setPrevChatHistoryEnabled(!panel.isPrevChatHistoryHidden());
        pref.setChatLengthDefaultTimeout(panel.getChatTimeoutTime());
        pref.setTabsOnTop(panel.isTabsOnTop());
        pref.setTabsScroll(panel.isTabScroll());
        pref.setBuzzEnabled(panel.isBuzzEnabled());
        pref.setCloseUnreadMessageEnabled(panel.isCloseUnreadMessageEnabled());
        pref.setChatHistoryAscending(panel.isSortChatHistoryAscending());

        // Do not commit if not changed.
        if (ModelUtil.hasLength(panel.getPassword()) && ModelUtil.hasLength(panel.getConfirmationPassword())) {
            try {
                AccountManager.getInstance( SparkManager.getConnection() ).changePassword(panel.getPassword());
            }
            catch (XMPPException | SmackException | InterruptedException passwordEx) {
            	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.unable.to.save.password"),
                    Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                Log.error("Unable to change password", passwordEx);
            }
        }
    }

    @Override
	public Object getData() {
        return SettingsManager.getLocalPreferences();
    }

    @Override
	public String getErrorMessage() {
        return errorMessage;
    }


    @Override
	public boolean isDataValid() {
        boolean dataIsValid = true;
        if (ModelUtil.hasLength(panel.getPassword()) && ModelUtil.hasLength(panel.getConfirmationPassword())) {
            if (!panel.getPassword().equals(panel.getConfirmationPassword())) {
                errorMessage = Res.getString("message.passwords.no.match");
                dataIsValid = false;
            }
        }
        return dataIsValid;
    }

    @Override
	public JComponent getGUI() {
        return panel;
    }

    @Override
	public String getNamespace() {
        return NAMESPACE;
    }

    @Override
	public void shutdown() {
        commit();
    }


}
