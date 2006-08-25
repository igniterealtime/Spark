/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.spark.SparkManager;

import java.io.File;
import java.util.Date;
import java.util.Properties;


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
     * @param xmppPort the XMPP Port to communicate on. Default is 5222.
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
        return Integer.parseInt(props.getProperty("timeout", "5"));
    }

    /**
     * Sets the smack timeout for requests. The default is 5 seconds, but you may wish
     * to increase this number for low bandwidth users.
     *
     * @param timeOut the smack timeout.
     */
    public void setTimeOut(int timeOut) {
        props.setProperty("timeout", Integer.toString(timeOut));
    }

    /**
     * Returns the encoded password.
     *
     * @return the encoded password.
     */
    public String getPassword() {
        return props.getProperty("password");
    }

    /**
     * Sets the encoded password.
     *
     * @param password the encoded password.
     */
    public void setPassword(String password) {
        props.setProperty("password", password);
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
     * @param idleOn true to turn idle on.
     */
    public void setIdleOn(boolean idleOn) {
        props.setProperty("idleOn", Boolean.toString(idleOn));
    }

    /**
     * Returns the number of minutes to set to unavailable if the computer has
     * no activity.
     *
     * @return the number of minutes before checking for IDLE computer.
     */
    public int getIdleTime() {
        return Integer.parseInt(props.getProperty("idleTime", "1"));
    }

    /**
     * Set the number of minutes to set to unavailable if the computer has
     * no activity.
     *
     * @param secondIdleTime the number of minutes.
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
        return Boolean.parseBoolean(props.getProperty("autoLoginEnabled", "false"));
    }

    /**
     * Turn on or off Auto Login. Auto Login allows a user to login  to the system without
     * inputting their signing information.
     *
     * @param autoLogin true if Auto Login should be on.
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
        return Boolean.parseBoolean(props.getProperty("passwordSaved", "false"));
    }

    /**
     * Set to true to encode and save password. You would use this if you
     * wish to not always input ones password.
     *
     * @param savePassword true if the password should be saved.
     */
    public void setSavePassword(boolean savePassword) {
        props.setProperty("passwordSaved", Boolean.toString(savePassword));
    }

    /**
     * Returns the users username.
     *
     * @return the username of the agent.
     */
    public String getUsername() {
        return props.getProperty("username");
    }

    /**
     * Sets the Agents username.
     *
     * @param username the agents username.
     */
    public void setUsername(String username) {
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
     * @param server the last Server accessed.
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
     * @param newInstall true if this is a fresh install.
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
     * @param ssl true if we should be using SSL.
     */
    public void setSSL(boolean ssl) {
        props.setProperty("sslEnabled", Boolean.toString(ssl));
    }

    public String getDownloadDir() {
        File downloadedDir = new File(SparkManager.getUserDirectory(), "downloads");

        return props.getProperty("downloadDirectory", downloadedDir.getAbsolutePath());
    }

    public void setDownloadDir(String downloadDir) {
        props.setProperty("downloadDirectory", downloadDir);
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
        return props.getProperty("resource", "spark");
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

    public boolean isTimeDisplayedInChat() {
        return getBoolean("timeDisplayed", true);
    }

    public void setTimeDisplayedInChat(boolean timeDisplayedInChat) {
        setBoolean("timeDisplayed", timeDisplayedInChat);
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

    public void setChatHistoryEnabled(boolean hideChatHistory) {
        setBoolean("showHistory", hideChatHistory);
    }

    public boolean isEmptyGroupsShown() {
        return getBoolean("showEmptyGroups", false);
    }

    public void setEmptyGroupsShown(boolean shown) {
        setBoolean("showEmptyGroups", shown);
    }

    public int getFileTransferTimeout() {
        return Integer.parseInt(props.getProperty("fileTransferTimeout", "1"));
    }

    public void setFileTransferTimeout(int minutes) {
        props.setProperty("fileTransferTimeout", Integer.toString(minutes));
    }

    public void setChatLengthDefaultTimeout(int minutes) {
        props.setProperty("defaultChatLengthTimeout", Integer.toString(minutes));
    }

    public int getChatLengthDefaultTimeout() {
        return Integer.parseInt(props.getProperty("defaultChatLengthTimeout", "15"));
    }

    public void setNickname(String nickname) {
        props.setProperty("nickname", nickname);
    }

    public String getNickname() {
        return props.getProperty("nickname", SparkManager.getUserManager().getNickname());
    }

    public void setShowToasterPopup(boolean show) {
        setBoolean("toasterPopup", show);
    }

    public boolean getShowToasterPopup() {
        return getBoolean("toasterPopup", false);
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

    private boolean getBoolean(String property, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }

}
