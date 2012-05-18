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

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.PluginRes;
import org.jivesoftware.spark.SparkManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.UIManager;

import org.jivesoftware.spark.util.Encryptor;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;

/**
 * Represents the LocalPreference Model for this system.
 */
public class LocalPreferences {

    	private Properties props;

	public LocalPreferences(Properties props) {
		this.props = props;
	}

	public LocalPreferences() {
		this.props = new Properties();
	}

	public Properties getProperties() {
		return props;
	}

	/**
	 * Returns the XMPP Port to communicate on.
	 *
	 * @return the XMPP Port to communicate on. Default is 5222.
	 */
	public int getXmppPort() {
		return Integer.parseInt(props.getProperty("xmppPort", "5222"));
	}

	/**
	 * Sets the XMPP Port to communicate on.
	 *
	 * @param xmppPort
	 *            the XMPP Port to communicate on. Default is 5222.
	 */
	public void setXmppPort(int xmppPort) {
		props.setProperty("xmppPort", Integer.toString(xmppPort));
	}

	/**
	 * Return the smack timeout for requests. Default is 5 seconds.
	 *
	 * @return the smack timeout for requests.
	 */
	public int getTimeOut() {
		return Integer.parseInt(props.getProperty("timeout", "10"));
	}

	/**
	 * Sets the smack timeout for requests. The default is 5 seconds, but you
	 * may wish to increase this number for low bandwidth users.
	 *
	 * @param timeOut
	 *            the smack timeout.
	 */
	public void setTimeOut(int timeOut) {
		props.setProperty("timeout", Integer.toString(timeOut));
	}

	/**
	 * Returns the encoded password.
	 *
	 * @return the encoded password.
	 */
//	public String getPassword() {
//		return props.getProperty("password");
//	}

	/**
	 * Sets the encoded password.
	 *
	 * @param password
	 *            the encoded password.
	 */
//	public void setPassword(String password) {
//		props.setProperty("password", password);
//	}

	/**
	 * returns the password for an encrypted jid
	 * @param barejid
	 * @return
	 */
	public String getPasswordForUser(String barejid)
	{
	    try {
		String pw = "password"+Encryptor.encrypt(barejid);
		return Encryptor.decrypt(props.getProperty(pw));
	    } catch(Exception e){
		return null;
	    }
	}

	/**
	 * Sets the password for barejid<br>
	 * both will be encrypted
	 * @param barejid
	 * @param password
	 * @throws Exception
	 */
	public void setPasswordForUser(String barejid, String password) throws Exception
	{
	    String user = "password"+Encryptor.encrypt(barejid);
	    String pw = Encryptor.encrypt(password);
	    props.setProperty(user, pw);
	}

	/**
	 * Return true if the IDLE feature is on. The IDLE feature allows to monitor
	 * computer activity and set presence accordingly.
	 *
	 * @return true if IDLE is on.
	 */
	public boolean isIdleOn() {
		return Boolean.parseBoolean(props.getProperty("idleOn", "true"));
	}

	/**
	 * Set the IDLE feature on or off. The IDLE feature allows to monitor
	 * computer activity and set presence accordingly.
	 *
	 * @param idleOn
	 *            true to turn idle on.
	 */
	public void setIdleOn(boolean idleOn) {
		props.setProperty("idleOn", Boolean.toString(idleOn));
	}

	/**
	 * Returns true if it is wanted that a new ad hoc room to be created every time Actions/Start conference room is chosen,
	 * or, from a chat window - invite to group chat room icon is pressed.
	 * Returns false if it is wanted that the bookmarked room (if any) to be opened every time Actions/Start conference room is chosen,
	 * or, from a chat window - invite to group chat room icon is pressed.
	 * @param adHocRoom
	 * @return
	 */
	public boolean isUseAdHocRoom() {
	    String adhoc = PluginRes.getPreferenceRes("useAdHocRoom");
	    return getBoolean("useAdHocRoom", adhoc != null ? new Boolean(adhoc) : true);
	}

    /**
     * Set useAdHocRoom on or off. When disabled, if there is at least one bookmark room, that
     * room will be used in Actions/Start conference room Invitation Dialog (instead of creating an ad-hoc room)
     * If there is more than one bookmark room, you can select the bookmarked room that you want to be used
     * Also, when invite to join group chat room will be sent from the chat window, the bookmark room will automatically
     * be opened.
     * @param adHocRoom
     * @return
     */
	public void setUseAdHocRoom(boolean adHocRoom) {
	    setBoolean("useAdHocRoom", adHocRoom);
	}
	/**
	 * Returns the Idle Message to Display when going automatically away
	 * @return
	 */
	public String getIdleMessage(){
	    return props.getProperty("idleOnMessage",Res.getString("status.away"));
	}

	/**
	 * Sets the idle Message when going automatically away
	 * @param message
	 */
	public void setIdleMessage(String message){
	    props.setProperty("idleOnMessage",message);
	}

	/**
	 * Returns the number of minutes to set to unavailable if the computer has
	 * no activity.
	 *
	 * @return the number of minutes before checking for IDLE computer.
	 */
	public int getIdleTime() {
		return Integer.parseInt(props.getProperty("idleTime", "3"));
	}

	/**
	 * Set the number of minutes to set to unavailable if the computer has no
	 * activity.
	 *
	 * @param secondIdleTime
	 *            the number of minutes.
	 */
	public void setIdleTime(int secondIdleTime) {
		props.setProperty("idleTime", Integer.toString(secondIdleTime));
	}

	/**
	 * Return true if Auto Login is on.
	 *
	 * @return true if Auto Login is on.
	 */
	public boolean isAutoLogin() {
		return Boolean.parseBoolean(props.getProperty("autoLoginEnabled",
				"false"));
	}

	/**
	 * Turn on or off Auto Login. Auto Login allows a user to login to the
	 * system without inputting their signing information.
	 *
	 * @param autoLogin
	 *            true if Auto Login should be on.
	 */
	public void setAutoLogin(boolean autoLogin) {
		props.setProperty("autoLoginEnabled", Boolean.toString(autoLogin));
	}

	/**
	 * Return true if the password should be encoded and persisted.
	 *
	 * @return true if the password is encoded and persisted.
	 */
	public boolean isSavePassword() {
		return Boolean
				.parseBoolean(props.getProperty("passwordSaved", "false"));
	}

	/**
	 * Set to true to encode and save password. You would use this if you wish
	 * to not always input ones password.
	 *
	 * @param savePassword
	 *            true if the password should be saved.
	 */
	public void setSavePassword(boolean savePassword) {
		props.setProperty("passwordSaved", Boolean.toString(savePassword));
	}

	/**
	 * Returns the last used Username
	 *
	 * @return the username of the agent.
	 */
	public String getLastUsername() {
		return props.getProperty("username");
	}

	/**
	 * Sets the Agents username.
	 *
	 * @param username
	 *            the agents username.
	 */
	public void setLastUsername(String username) {
		props.setProperty("username", username);
	}

	/**
	 * Returns the last Server accessed.
	 *
	 * @return the last Server accessed.
	 */
	public String getServer() {
		return props.getProperty("server");
	}

	/**
	 * Sets the last Server accessed.
	 *
	 * @param server
	 *            the last Server accessed.
	 */
	public void setServer(String server) {
		props.setProperty("server", server);
	}

	/**
	 * Return true if this is a fresh install.
	 *
	 * @return true if a fresh install.
	 */
	public boolean isNewInstall() {
		return Boolean.parseBoolean(props.getProperty("newInstall", "false"));
	}

	/**
	 * Set if this is a fresh install.
	 *
	 * @param newInstall
	 *            true if this is a fresh install.
	 */
	public void setNewInstall(boolean newInstall) {
		props.setProperty("newInstall", Boolean.toString(newInstall));
	}

	/**
	 * Returns true to use SSL.
	 *
	 * @return true if we should connect via SSL.
	 */
	public boolean isSSL() {
		return Boolean.parseBoolean(props.getProperty("sslEnabled", "false"));
	}

	/**
	 * Sets if the agent should use SSL for connecting.
	 *
	 * @param ssl
	 *            true if we should be using SSL.
	 */
	public void setSSL(boolean ssl) {
		props.setProperty("sslEnabled", Boolean.toString(ssl));
	}

	/**
	 * Returns the Download Directory, doesnt return <code>null</code>
	 * @return {@link String}
	 */
	public String getDownloadDir() {

		File downloadedDir = null;
		if (Spark.isLinux() || Spark.isMac()) {
			downloadedDir = new File(System.getProperty("user.home") + "/Downloads/");
            Log.error(downloadedDir.getAbsolutePath());
		} else if (Spark.isWindows()) {

			String regpath = WinRegistryReader.getMyDocumentsFromWinRegistry();
			if (regpath != null) {
				downloadedDir = new File(regpath + "\\Downloads");
				if (!downloadedDir.exists()) {
					downloadedDir.mkdir();
				}
			}
			else
			{
			    // if for some Reason there is no "My Documents" Folder we should select the Desktop
				downloadedDir = new File(System.getProperty("user.home") + "\\Desktop\\");
			}
		}

		return props.getProperty("downloadDirectory", downloadedDir.getAbsolutePath());
	}

	public void setDownloadDir(String downloadDir) {
		props.setProperty("downloadDirectory", downloadDir);
	}

	public String getFileExplorer() {
		return props.getProperty("fileExplorer");
	}

	public void setFileExplorer(String fileExplorer) {
		props.setProperty("fileExplorer", fileExplorer);
	}

	public boolean isProxyEnabled() {
		return getBoolean("proxyEnabled", false);
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		setBoolean("proxyEnabled", proxyEnabled);
	}

	public String getHost() {
		return props.getProperty("host");
	}

	public void setHost(String host) {
		props.setProperty("host", host);
	}

	public String getPort() {
		return props.getProperty("port");
	}

	public void setPort(String port) {
		props.setProperty("port", port);
	}

	public String getProxyUsername() {
		return props.getProperty("proxyUsername");
	}

	public void setProxyUsername(String proxyUsername) {
		props.setProperty("proxyUsername", proxyUsername);
	}

	public String getProxyPassword() {
		return props.getProperty("proxyPassword");
	}

	public void setProxyPassword(String proxyPassword) {
		props.setProperty("proxyPassword", proxyPassword);
	}

	public String getProtocol() {
		return props.getProperty("protocol");
	}

	public void setProtocol(String protocol) {
		props.setProperty("protocol", protocol);
	}

	public String getDefaultNickname() {
		return props.getProperty("defaultNickname");
	}

	public void setDefaultNickname(String defaultNickname) {
		props.setProperty("defaultNickname", defaultNickname);
	}

	public int getCheckForUpdates() {
		return Integer.parseInt(props.getProperty("checkForUpdates", "7"));
	}

	public Date getLastCheckForUpdates() {
		String date = props.getProperty("lastUpdateCheck");
		if (date == null) {
			return null;
		}

		// Convert to long
		long time = Long.parseLong(date);
		return new Date(time);
	}

	public void setLastCheckForUpdates(Date lastCheckForUpdates) {
		String time = Long.toString(lastCheckForUpdates.getTime());
		props.setProperty("lastUpdateCheck", time);
	}

	public String getXmppHost() {
		return props.getProperty("xmppHost");
	}

	public void setXmppHost(String xmppHost) {
		props.setProperty("xmppHost", xmppHost);
	}

	public boolean isHostAndPortConfigured() {
		return getBoolean("hostAndPort", false);
	}

	public void setHostAndPortConfigured(boolean configured) {
		setBoolean("hostAndPort", configured);
	}

        public String getResource() {
            if (props.getProperty("resource", "spark").equals("spark"))
            {
                setResource(Default.getString(Default.SHORT_NAME) + " " + JiveInfo.getVersion());
                return Default.getString(Default.SHORT_NAME) + " " + JiveInfo.getVersion();
            }

        return props.getProperty("resource", Default.getString(Default.SHORT_NAME) + " " + JiveInfo.getVersion());
    }

	public void setResource(String resource) {
		props.setProperty("resource", resource);
	}

	public boolean isStartedHidden() {
		return getBoolean("startHidden", false);
	}

	public void setStartedHidden(boolean startedHidden) {
		setBoolean("startHidden", startedHidden);
	}

	public boolean isUsingSingleTrayClick() {
		return getBoolean("useSingleTrayClick", true);
	}

	public void setUsingSingleTrayClick(boolean useSingle) {
		setBoolean("useSingleTrayClick", useSingle);
	}
	
	public boolean isTimeDisplayedInChat() {
		return getBoolean("timeDisplayed", true);
	}

	public void setTimeDisplayedInChat(boolean timeDisplayedInChat) {
		setBoolean("timeDisplayed", timeDisplayedInChat);
	}

	public void setTimeFormat(String format) {
		props.setProperty("timeFormat", format);
	}

	public String getTimeFormat() {
		return props.getProperty("timeFormat", "HH:mm");
	}

	public boolean isSpellCheckerEnabled() {
		return getBoolean("spellCheckerEnabled", true);
	}

	public void setSpellCheckerEnabled(boolean enabled) {
		setBoolean("spellCheckerEnabled", enabled);
	}

	public boolean isChatRoomNotificationsOn() {
		return getBoolean("chatNotificationOn", true);
	}

	public void setChatRoomNotifications(boolean on) {
		setBoolean("chatNotificationOn", on);
	}

	public boolean isChatHistoryEnabled() {
		return getBoolean("showHistory", true);
	}

	public void setChatHistoryEnabled(boolean hidePrevChatHistory) {
		setBoolean("showHistory", hidePrevChatHistory);
	}

	public boolean isPrevChatHistoryEnabled() {
		return getBoolean("showPrevHistory", true);
	}

	public void setPrevChatHistoryEnabled(boolean hidePrevChatHistory) {
		setBoolean("showPrevHistory", hidePrevChatHistory);
	}

	public boolean isEmptyGroupsShown() {
		return getBoolean("showEmptyGroups", false);
	}

	public void setEmptyGroupsShown(boolean shown) {
		setBoolean("showEmptyGroups", shown);
	}

	public boolean isOfflineUsersShown() {
		return getBoolean("showOfflineUsers", false);
	}

	public void setOfflineUsersShown(boolean shown) {
		setBoolean("showOfflineUsers", shown);
	}

	public boolean isTypingNotificationShown() {
	    String showTypingNotification = PluginRes.getPreferenceRes("showTypingNotification");
	    return getBoolean("showTypingNotification", showTypingNotification != null ? new Boolean(showTypingNotification) : false);
	}

	public void setSystemTrayNotificationEnabled(boolean shown) {
		setBoolean("SystemTrayNotificationEnabled", shown);
	}

	public boolean isSystemTrayNotificationEnabled() {
	    String SystemTrayNotificationEnabled = PluginRes.getPreferenceRes("SystemTrayNotificationEnabled");
	    return getBoolean("SystemTrayNotificationEnabled", SystemTrayNotificationEnabled != null ? new Boolean(SystemTrayNotificationEnabled) : false);
	}

	public void setTypingNotificationOn(boolean shown) {
		setBoolean("showTypingNotification", shown);
	}

	public int getFileTransferTimeout() {
		return Integer.parseInt(props.getProperty("fileTransferTimeout", "30"));
	}

	public void setFileTransferTimeout(int minutes) {
		props.setProperty("fileTransferTimeout", Integer.toString(minutes));
	}

	public void setChatLengthDefaultTimeout(int minutes) {
		props
				.setProperty("defaultChatLengthTimeout", Integer
						.toString(minutes));
	}

	public int getChatLengthDefaultTimeout() {
		return Integer.parseInt(props.getProperty("defaultChatLengthTimeout",
				"15"));
	}

	public void setNickname(String nickname) {
		props.setProperty("nickname", nickname);
	}

	public String getNickname() {
		return props.getProperty("nickname", SparkManager.getUserManager()
				.getNickname());
	}

	public void setShowToasterPopup(boolean show) {
		setBoolean("toasterPopup", show);
	}

	public boolean getShowToasterPopup() {
	    String toasterPopup = PluginRes.getPreferenceRes("toasterPopup");
	    return getBoolean("toasterPopup", toasterPopup != null ? new Boolean(toasterPopup) : false);
	}

	public void setWindowTakesFocus(boolean focus) {
		setBoolean("windowTakesFocus", focus);
	}

	public boolean getWindowTakesFocus() {
		return getBoolean("windowTakesFocus", false);
	}

	public void setStartOnStartup(boolean startup) {
		setBoolean("startOnStartup", startup);
	}

	public boolean getStartOnStartup() {
		return getBoolean("startOnStartup", false);
	}

	/**
	 * Sets the Reconnection display type</p>
	 * 0 = ReconnectPanel </p>
	 * 1 = Reconnect as Group</p>
	 * 2 = Reconnect as Icon</p>
	 * @param reconnect
	 */
	public void setReconnectPanelType(int reconnect) {
	    setInt("ReconnectPanelType", reconnect);
	}

        /**
         * Sets the Reconnection display type</p>
         * 0 = ReconnectPanel </p>
         * 1 = Reconnect as Group </p>
         * 2 = Reconnect as Icon</p>
         * @return
         */
	public int getReconnectPanelType() {
	    return getInt("ReconnectPanelType", 0);
	}

	public void setCompressionEnabled(boolean on) {
		setBoolean("compressionOn", on);
	}

	public boolean isCompressionEnabled() {
		return getBoolean("compressionOn", false);
	}

	public void setTheme(String theme) {
		props.setProperty("theme", theme);
	}

	public String getTheme() {
		return props.getProperty("theme", "Default");
	}

	public void setEmoticonPack(String pack) {
		props.setProperty("emoticonPack", pack);
	}

	public String getEmoticonPack() {
		return props.getProperty("emoticonPack", "Default");
	}

	public void setOfflineNotifications(boolean notify) {
		setBoolean("notifyOnOffline", notify);
	}

	public boolean isOfflineNotificationsOn() {
	    String notifyOnOffline = PluginRes.getPreferenceRes("notifyOnOffline");
	    return getBoolean("notifyOnOffline", notifyOnOffline != null ? new Boolean(notifyOnOffline) : false);
	}

	public void setOnlineNotifications(boolean notify) {
		setBoolean("notifyOnOnline", notify);
	}

	public boolean isOnlineNotificationsOn() {
	    String notifyOnOnline = PluginRes.getPreferenceRes("notifyOnOnline");
	    return getBoolean("notifyOnOnline", notifyOnOnline != null ? new Boolean(notifyOnOnline) : false);
	}

	public void setDockingEnabled(boolean dockingEnabled) {
		setBoolean("dockingEnabled", dockingEnabled);
	}

	public boolean isDockingEnabled() {
		return getBoolean("dockingEnabled", false);
	}

	public void setAutoCloseChatRoomsEnabled(boolean autoCloseChatRoomsEnabled) {
		setBoolean("autoCloseChatRoomsEnabled", autoCloseChatRoomsEnabled);
	}

	public boolean isAutoCloseChatRoomsEnabled() {
		return getBoolean("autoCloseChatRoomsEnabled", true);
	}

	public void setTabsOnTop(boolean onTop) {
		setBoolean("tabsOnTop", onTop);
	}

	public boolean isTabTopPosition() {
		return getBoolean("tabsOnTop", true);
	}

	public void setBuzzEnabled(boolean enabled) {
		setBoolean("buzzEnabled", enabled);
	}

	public boolean isBuzzEnabled() {
		return getBoolean("buzzEnabled", true);
	}

	public void setOfflineGroupVisible(boolean visible) {
		setBoolean("offlineGroupVisible", visible);
	}

	public boolean isOfflineGroupVisible() {
		return getBoolean("offlineGroupVisible", true);
	}

	public void setEmoticonsEnabled(boolean enabled) {
		setBoolean("emoticonsEnabled", enabled);
	}

	public boolean areEmoticonsEnabled() {
		return getBoolean("emoticonsEnabled", true);
	}

	public void setLookAndFeel(String laf)
	{
	    setString("LookAndFeel",laf);
	}

    public String getLookAndFeel() {
	String defaultstring = "";
	try {
	    defaultstring = Spark.isMac() ? Default.getString(Default.DEFAULT_LOOK_AND_FEEL_MAC)
		    : Default.getString(Default.DEFAULT_LOOK_AND_FEEL);
	} catch (Exception e) {
	    defaultstring = UIManager.getSystemLookAndFeelClassName();
	}
	if (defaultstring.length() < 1) {
	    defaultstring = UIManager.getSystemLookAndFeelClassName();
	}
	return getString("LookAndFeel", defaultstring);
    }

	public void setCheckForBeta(boolean checkForBeta) {
		setBoolean("checkForBeta", checkForBeta);
	}

	public boolean isBetaCheckingEnabled() {
		return getBoolean("checkForBeta", false);
	}

	public boolean isMucHighNameEnabled() {
		return getBoolean("isMucHighNameOn", false);
	}

	public boolean isMucHighTextEnabled() {
		return getBoolean("isMucHighTextOn", false);
	}

	public boolean isMucRandomColors(){
	    return getBoolean("isMucRandomColors", true);
	}

	public void setMucRandomColors(boolean value){
	    setBoolean("isMucRandomColors", value);
	}

	public boolean isMucHighToastEnabled() {
		return getBoolean("isMucHighToastOn", false);
	}

	public boolean isShowingRoleIcons() {
	    return getBoolean("isShowingRoleIcons",false);
	}

	public boolean isShowJoinLeaveMessagesEnabled() {
	    return getBoolean("isShowJoinLeaveMessagesOn", true);
	}

	public void setShowJoinLeaveMessagesEnabled(boolean enabled) {
	    setBoolean("isShowJoinLeaveMessagesOn", enabled);
	}

	public void setMucHighNameEnabled(boolean setMucNHigh) {
	    setBoolean("isMucHighNameOn", setMucNHigh);
	}

	public void setMucHighTextEnabled(boolean setMucTHigh) {
	    setBoolean("isMucHighTextOn", setMucTHigh);
	}

	public void setMuchHighToastEnabled(boolean setMucPHigh) {
	    setBoolean("isMucHighToastOn", setMucPHigh);
	}

	public void setShowRoleIconInsteadStatusIcon(boolean roleicons){
		setBoolean("isShowingRoleIcons",roleicons);
	}

	public void setSSOEnabled(boolean enabled) {
	    setBoolean("ssoEnabled", enabled);
	}

	public boolean isSSOEnabled() {
		return getBoolean("ssoEnabled", false);
	}

	public void setSSOAdv(boolean enabled) {
		setBoolean("ssoAdv", enabled);
	}

	public boolean getSSOAdv() {
		return getBoolean("ssoAdv", false);
	}

	public void setSSOMethod(String method) {
		props.setProperty("ssoMethod", method);
	}

	public String getSSOMethod() {
		return props.getProperty("ssoMethod");
	}

	public void setSSORealm(String realm) {
		props.setProperty("ssoRealm", realm);
	}

	public String getSSORealm() {
		return props.getProperty("ssoRealm");
	}

	public void setSSOKDC(String kdc) {
		props.setProperty("ssoKDC", kdc);
	}

	public String getSSOKDC() {
		return props.getProperty("ssoKDC");
	}

	public void setPKIEnabled(boolean enabled) {
		setBoolean("pkiEnabled", enabled);
	}

	public boolean isPKIEnabled() {
		return getBoolean("pkiEnabled", false);
	}

	public void setPKIStore(String type) {
		props.setProperty("pkiStore", type);
	}

	public String getPKIStore() {
		return props.getProperty("pkiStore");
	}

	public void setJKSPath(String file) {
		props.setProperty("jksPath", file);
	}

	public String getJKSPath() {
		return props.getProperty("jksPath");
	}

	public void setPKCS11Library(String file) {
		props.setProperty("pkcs11Library", file);
	}

	public String getPKCS11Library() {
		return props.getProperty("pkcs11Library");
	}

	public void setTrustStorePath(String file) {
		props.setProperty("trustStorePath", file);
	}

	public String getTrustStorePath() {
		return props.getProperty("trustStorePath");
	}

	public void setTrustStorePassword(String password) {
		props.setProperty("trustStorePassword", password);
	}

	public String getTrustStorePassword() {
		return props.getProperty("trustStorePassword");
	}

	public boolean getDebug() {
		return getBoolean("debug", false);
	}

	public void setDebug(boolean debug) {
		setBoolean("debug", debug);
	}

	public void setDebuggerEnabled(boolean enabled) {
		setBoolean("debuggerEnabled", enabled);
	}

	public boolean isDebuggerEnabled() {
		return getBoolean("debuggerEnabled", false);
	}

	public void setContactListFontSize(int fontSize) {
		setInt("contactListFontSize", fontSize);
	}

	public int getContactListFontSize() {
		return getInt("contactListFontSize", 11);
	}

	public void setContactListIconSize(int iconSize) {
		setInt("contactListIconSize", iconSize);
	}

	public int getContactListIconSize() {
		return getInt("contactListIconSize", 24);
	}

	public void setChatRoomFontSize(int fontSize) {
		setInt("chatRoomFontSize", fontSize);
	}

	public int getChatRoomFontSize() {
		return getInt("chatRoomFontSize", 12);
	}

	public void setLanguage(String language) {
		props.setProperty("language", language);
	}

	public String getLanguage() {
		return props.getProperty("language", "");
	}

	public void setAvatarVisible(boolean visible) {
		setBoolean("showAvatar", visible);
	}

	public boolean areAvatarsVisible() {
		return getBoolean("showAvatar", false);
	}

	public void setVCardsVisible(boolean visible) {
		setBoolean("showVCards", visible);
	}

	public boolean areVCardsVisible() {
		return getBoolean("showVCards", true);
	}

	public void setAudioDevice(String device) {
		 props.setProperty("audioDevice", device);
	}

	public String getAudioDevice() {
		return props.getProperty("audioDevice","javasound://");
	}

	public void setVideoDevice(String device) {
		 props.setProperty("videoDevice", device);
	}

	public String getVideoDevice() {
		return props.getProperty("videoDevice",null);
	}

	public boolean isMainWindowAlwaysOnTop() {
		return getBoolean("MainWindowAlwaysOnTop", false);
	}

	public void setMainWindowAlwaysOnTop(boolean onTop) {
		setBoolean("MainWindowAlwaysOnTop", onTop);
	}

	public boolean isChatWindowAlwaysOnTop() {
		return getBoolean("ChatWindowAlwaysOnTop", false);
	}

	public void setChatWindowAlwaysOnTop(boolean onTop) {
		setBoolean("ChatWindowAlwaysOnTop", onTop);
	}

	public String getSelectedCodecs() {
		return getString("SelectedCodecs", null);
	}

	public String getStunFallbackHost()
	{
	    return getString("stunFallbackHost", "");
	}

	public int getStunFallbackPort()
	{
	    return getInt("stunFallbackPort", 3478);
	}

	public void setStunFallbackHost(String host) {
	    setString("stunFallbackHost", host);
	}

	public void setStunFallbackPort(int port) {
	    setInt("stunFallbackPort", port);
	}

	public boolean getShowTransportTab()
	{
	    return getBoolean("useTabForTransport", false);
	}

	public void setShowTransportTab(boolean value)
	{
	    setBoolean("useTabForTransport", value);
	}

	public boolean isShowConferenceTab()
	{
	    return getBoolean("useTabForConference", true);
	}

	public void setShowConferenceTab(boolean value)
	{
	    setBoolean("useTabForConference", value);
	}

	public String getAvailableCodecs() {
		return getString("AvailableCodecs", null);
	}

	public void setSelectedCodecs(String value) {
		setString("SelectedCodecs", value);
	}

	public void setAvailableCodecs(String value) {
		setString("AvailableCodecs", value);
	}

	private boolean getBoolean(String property, boolean defaultValue) {
		return Boolean.parseBoolean(props.getProperty(property, Boolean
				.toString(defaultValue)));
	}

	private void setBoolean(String property, boolean value) {
		props.setProperty(property, Boolean.toString(value));
	}

	private int getInt(String property, int defaultValue) {
		return Integer.parseInt(props.getProperty(property, Integer
				.toString(defaultValue)));
	}

	private void setInt(String property, int value) {
		props.setProperty(property, Integer.toString(value));
	}

	private String getString(String property, String defaultValue) {
		return props.getProperty(property, defaultValue);
	}

	private void setString(String property, String value) {
		props.setProperty(property, value);
	}

    public boolean isAutoAcceptMucInvite() {
	return getBoolean("autoAcceptMucInvite", false);
    }

    public void setAutoAcceptMucInvite(boolean autoAcceptMuc) {
	setBoolean("autoAcceptMucInvite", autoAcceptMuc);

    }

    public String getDefaultBookmarkedConf() {
        return props.getProperty("defaultBookmarkedConf");
    }

    public void setDefaultBookmarkedConf(String bookmarkedConferenceJid) {
        setString("defaultBookmarkedConf",bookmarkedConferenceJid);
    }

    /**
     * Returns the Maximum visible amount of History entries
     * Default is 5000
     * @return int
     */
    public int getMaximumHistory() {
	int x = getInt("maximumHistory", -1);
	if (x == -1) {
	    x = 5000;
	    setInt("maximumHistory", x);
	}
	return x;
    }

    public List<String> getDeactivatedPlugins()
    {
	String plugs = getString("deactivatedPlugins", "");
	ArrayList<String> liste = new ArrayList<String>();

	StringTokenizer tokenz = new StringTokenizer(plugs, ",");

	while(tokenz.hasMoreTokens())
	{
	    String x = tokenz.nextToken();
	    liste.add(x);
	}
	return liste;
    }

    public void setDeactivatedPlugins(List<String> list) {

	// [hallo, hallo, hallo, hallo, hallo]
	// =
	// hallo,hallo,hallo,hallo,hallo
	if (list.size() > 0) {
	    String liste = list.toString().substring(1,
		    list.toString().length() - 1);
	    liste = liste.replace(", ", ",");
	    setString("deactivatedPlugins", liste);
	} else {
	    setString("deactivatedPlugins", "");
	}

    }


}
