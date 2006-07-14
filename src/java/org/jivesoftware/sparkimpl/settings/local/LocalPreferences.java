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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Represents the LocalPreference Model for this system.
 */
public class LocalPreferences {
    private int xmppPort = 5222;
    private int timeOut = 40;
    private String password;
    private String username;
    private String server;
    private boolean autoLogin;
    private boolean savePassword;
    private boolean idleOn = true;
    private int secondIdleTime = 30;
    private Set folderList = new HashSet();
    private boolean newInstall = true;
    private boolean indexing;
    private boolean SSL;
    private boolean prompted = true;
    private String downloadDir;

    private String xmppHost;
    private boolean hostAndPortConfigured;

    private String resource = "Spark";


    // Handle proxy info
    private boolean proxyEnabled;
    private String protocol;
    private String host;
    private String port;
    private String proxyUsername;
    private String proxyPassword;

    // Handle Chat settings
    private String defaultNickname;

    // Handle updates
    private Date lastCheckForUpdates;
    private boolean timeDisplayedInChat = true;

    // Start In System Tray
    private boolean startedHidden;
    private boolean spellCheckerDisable;
    private boolean chatRoomNotificationsOff;

    private boolean hideChatHistory;
    private boolean emptyGroupsShown;

    /**
     * Empty Constructor.
     */
    public LocalPreferences() {
    }

    /**
     * Returns the XMPP Port to communicate on.
     *
     * @return the XMPP Port to communicate on. Default is 5222.
     */
    public int getXmppPort() {
        return xmppPort;
    }

    /**
     * Sets the XMPP Port to communicate on.
     *
     * @param xmppPort the XMPP Port to communicate on. Default is 5222.
     */
    public void setXmppPort(int xmppPort) {
        this.xmppPort = xmppPort;
    }

    /**
     * Return the smack timeout for requests. Default is 5 seconds.
     *
     * @return the smack timeout for requests.
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * Sets the smack timeout for requests. The default is 5 seconds, but you may wish
     * to increase this number for low bandwidth users.
     *
     * @param timeOut the smack timeout.
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * Returns the encoded password.
     *
     * @return the encoded password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encoded password.
     *
     * @param password the encoded password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Return true if the IDLE feature is on. The IDLE feature allows to monitor
     * computer activity and set presence accordingly.
     *
     * @return true if IDLE is on.
     */
    public boolean isIdleOn() {
        return idleOn;
    }

    /**
     * Set the IDLE feature on or off. The IDLE feature allows to monitor
     * computer activity and set presence accordingly.
     *
     * @param idleOn true to turn idle on.
     */
    public void setIdleOn(boolean idleOn) {
        this.idleOn = idleOn;
    }

    /**
     * Returns the number of minutes to set to unavailable if the computer has
     * no activity.
     *
     * @return the number of minutes before checking for IDLE computer.
     */
    public int getSecondIdleTime() {
        return secondIdleTime;
    }

    /**
     * Set the number of minutes to set to unavailable if the computer has
     * no activity.
     *
     * @param secondIdleTime the number of minutes.
     */
    public void setSecondIdleTime(int secondIdleTime) {
        this.secondIdleTime = secondIdleTime;
    }

    /**
     * Return true if Auto Login is on.
     *
     * @return true if Auto Login is on.
     */
    public boolean isAutoLogin() {
        return autoLogin;
    }

    /**
     * Turn on or off Auto Login. Auto Login allows a user to login  to the system without
     * inputting their signing information.
     *
     * @param autoLogin true if Auto Login should be on.
     */
    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    /**
     * Return true if the password should be encoded and persisted.
     *
     * @return true if the password is encoded and persisted.
     */
    public boolean isSavePassword() {
        return savePassword;
    }

    /**
     * Set to true to encode and save password. You would use this if you
     * wish to not always input ones password.
     *
     * @param savePassword true if the password should be saved.
     */
    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    /**
     * Add an email folder for future parsing.
     *
     * @param folder folder to add.
     */
    public void addFolder(String folder) {
        folderList.add(folder);
    }

    /**
     * Returns a Collection of all Email folders.
     *
     * @return a Collection of all Email Folders.
     */
    public Collection getFolderList() {
        return folderList;
    }

    /**
     * Returns the users username.
     *
     * @return the username of the agent.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the Agents username.
     *
     * @param username the agents username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the last Server accessed.
     *
     * @return the last Server accessed.
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the last Server accessed.
     *
     * @param server the last Server accessed.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Return true if this is a fresh install.
     *
     * @return true if a fresh install.
     */
    public boolean isNewInstall() {
        return newInstall;
    }

    /**
     * Set if this is a fresh install.
     *
     * @param newInstall true if this is a fresh install.
     */
    public void setNewInstall(boolean newInstall) {
        this.newInstall = newInstall;
    }

    /**
     * Returns true if indexing is turned on.
     *
     * @return true if indexing is turned on.
     */
    public boolean isIndexing() {
        return indexing;
    }

    /**
     * Set to true if indexing is turned on.
     *
     * @param indexing true if indexing is turned on.
     */
    public void setIndexing(boolean indexing) {
        this.indexing = indexing;
    }

    /**
     * Returns true to use SSL.
     *
     * @return true if we should connect via SSL.
     */
    public boolean isSSL() {
        return SSL;
    }

    /**
     * Sets if the agent should use SSL for connecting.
     *
     * @param SSL true if we should be using SSL.
     */
    public void setSSL(boolean SSL) {
        this.SSL = SSL;
    }


    public boolean isPrompted() {
        return prompted;
    }

    public void setPrompted(boolean prompted) {
        this.prompted = prompted;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDefaultNickname() {
        return defaultNickname;
    }

    public void setDefaultNickname(String defaultNickname) {
        this.defaultNickname = defaultNickname;
    }


    public Date getLastCheckForUpdates() {
        return lastCheckForUpdates;
    }

    public void setLastCheckForUpdates(Date lastCheckForUpdates) {
        this.lastCheckForUpdates = lastCheckForUpdates;
    }

    public String getXmppHost() {
        return xmppHost;
    }

    public void setXmppHost(String xmppHost) {
        this.xmppHost = xmppHost;
    }

    public boolean isHostAndPortConfigured() {
        return hostAndPortConfigured;
    }

    public void setHostAndPortConfigured(boolean hostAndPortConfigured) {
        this.hostAndPortConfigured = hostAndPortConfigured;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public boolean isStartedHidden() {
        return startedHidden;
    }

    public void setStartedHidden(boolean startedHidden) {
        this.startedHidden = startedHidden;
    }

    public boolean isTimeDisplayedInChat() {
        return timeDisplayedInChat;
    }

    public void setTimeDisplayedInChat(boolean timeDisplayedInChat) {
        this.timeDisplayedInChat = timeDisplayedInChat;
    }

    public boolean isSpellCheckerDisable() {
        return spellCheckerDisable;
    }

    public void setSpellCheckerDisable(boolean spellCheckerDisable) {
        this.spellCheckerDisable = spellCheckerDisable;
    }

    public boolean isChatRoomNotificationsOff() {
        return chatRoomNotificationsOff;
    }

    public void setChatRoomNotificationsOff(boolean chatRoomNotificationsOff) {
        this.chatRoomNotificationsOff = chatRoomNotificationsOff;
    }

    public boolean isHideChatHistory() {
        return hideChatHistory;
    }

    public void setHideChatHistory(boolean hideChatHistory) {
        this.hideChatHistory = hideChatHistory;
    }

    public boolean isEmptyGroupsShown() {
        return emptyGroupsShown;
    }

    public void setEmptyGroupsShown(boolean shown) {
        this.emptyGroupsShown = shown;
    }
}
