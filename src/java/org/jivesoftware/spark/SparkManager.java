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
package org.jivesoftware.spark;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.preference.PreferenceManager;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.ChatPrinter;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;

/**
 * Used as the System Manager for the Spark IM client. The SparkManager is responsible for
 * the loading of other system managers on an as needed basis to prevent too much upfront loading
 * of resources. Some of the Managers and components you can access from here are:
 * <p/>
 * <p/>
 * <p/>
 * <h3>Managers</h3>
 * ChatManager - Used for adding, removing and appending listeners to Chat Rooms.
 * <br/>
 * PreferenceManager - Used for adding and removing Preferences.
 * <br/>
 * SoundManager - Used for playing sounds within Spark.
 * <br/>
 * SearchManager - Used for adding own search objects to Spark.
 * <br/>
 * SparkTransferManager - Used for all file transfer operations within Spark.
 * <br/>
 * ChatAssistantManager - Used to add ChatRoom plugins. ChatRoomPlugins are installed in their own pane on the
 * right side of the ChatRoom.
 * <br/>
 * VCardManager - Handles all profile handling within Spark. Use this to retrieve profile information on users.
 * <br/>
 * <h3>Windows and Components</h3>
 * <br/>
 * MainWindow - The frame containing the Spark client. Use for updating menus, and referencing parent frames.
 * <br/>
 * Workspace - The inner pane of the Spark client. Use for adding or removing tabs to the main Spark panel.
 * <br/>
 * Notifications - Use to display tray icon notifications (system specific), such as toaster popups or changing
 * the icon of the system tray.
 *
 * @author Derek DeMoro
 * @version 1.0, 03/12/14
 */


public final class SparkManager {

    /**
     * The Date Formatter to use in Spark.
     */
    private static final String dateFormat = ((SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL,SimpleDateFormat.MEDIUM)).toPattern();
    public static final SimpleDateFormat DATE_SECOND_FORMATTER = new SimpleDateFormat(dateFormat);

    private static SessionManager sessionManager;
    private static SoundManager soundManager;
    private static PreferenceManager preferenceManager;
    private static MessageEventManager messageEventManager;
    private static UserManager userManager;
    private static ChatManager chatManager;
    private static VCardManager vcardManager;
    private static NativeManager nativeManager;

    private static Component focusedComponent;


    private SparkManager() {
        // Do not allow initialization
    }

    static {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener(
                e -> {
                    String prop = e.getPropertyName();
                    if (("focusOwner".equals(prop)) && (e.getNewValue() != null)) {
                        focusedComponent = (Component)e.getNewValue();
                    }
                }
        );
    }


    /**
     * Gets the {@link MainWindow} instance. The MainWindow is the frame container used
     * to hold the Workspace container and Menubar of the Spark Client.
     *
     * @return MainWindow instance.
     */
    public static MainWindow getMainWindow() {
        return MainWindow.getInstance();
    }


    /**
     * Gets the {@link SessionManager} instance.
     *
     * @return the SessionManager instance.
     */
    public static SessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    /**
     * Gets the {@link SoundManager} instance.
     *
     * @return the SoundManager instance
     */
    public static SoundManager getSoundManager() {
        if (soundManager == null) {
            soundManager = new SoundManager();
        }
        return soundManager;
    }

    /**
     * Gets the {@link PreferenceManager} instance.
     *
     * @return the PreferenceManager instance.
     */
    public static PreferenceManager getPreferenceManager() {
        if (preferenceManager == null) {
            preferenceManager = new PreferenceManager();
        }
        return preferenceManager;
    }

    /**
     * Gets the {@link XMPPConnection} instance.
     *
     * @return the {@link XMPPConnection} associated with this session.
     */
    public static XMPPConnection getConnection() {
        return sessionManager.getConnection();
    }

    /**
     * Returns the <code>UserManager</code> for LiveAssistant. The UserManager
     * keeps track of all users in current chats.
     *
     * @return the <code>UserManager</code> for LiveAssistant.
     */
    public static UserManager getUserManager() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }


    /**
     * Returns the ChatManager. The ChatManager is responsible for creation and removal of
     * chat rooms, transcripts, and transfers and room invitations.
     *
     * @return the <code>ChatManager</code> for this instance.
     */
    public static ChatManager getChatManager() {
        if (chatManager == null) {
            chatManager = ChatManager.getInstance();
        }
        return chatManager;
    }

    /**
     * Retrieves the inner container for Spark. The Workspace is the container for all plugins into the Spark
     * install. Plugins would use this for the following:
     * <p/>
     * <ul>
     * <li>Add own tab to the main tabbed pane. ex.
     * <p/>
     * <p/>
     * Workspace workspace = SparkManager.getWorkspace();
     * JButton button = new JButton("HELLO SPARK USERS");
     * workspace.getWorkspacePane().addTab("MyPlugin", button);
     * </p>
     * <p/>
     * <li>Retrieve the ContactList.
     *
     * @return Workspace the spark manager is associated with.
     */
    public static Workspace getWorkspace() {
   	 
        return Workspace.getInstance();
    }

    /**
     * Returns the <code>MessageEventManager</code> used in Spark. The MessageEventManager is responsible
     * for XMPP specific operations such as notifying users that you have received their message or
     * inform a users that you are typing a message to them.
     *
     * @return the MessageEventManager used in Spark.
     */
    public static MessageEventManager getMessageEventManager() {
        if (messageEventManager == null) {
            messageEventManager = MessageEventManager.getInstanceFor( getConnection() );
        }
        return messageEventManager;
    }

    /**
     * Returns the VCardManager. The VCardManager is responsible for handling all users profiles and updates
     * to their profiles. Use the VCardManager to access a users profile based on their Jabber User ID (JID).
     *
     * @return the VCardManager.
     */
    public static VCardManager getVCardManager() {
        if (vcardManager == null) {
            vcardManager = new VCardManager();
        }
        return vcardManager;
    }

    /**
     * Returns the NativeManager. The NativeManager allows for native handling of Spark.
     *
     * @return the NativeManager.
     */
    public static NativeManager getNativeManager() {
        if (nativeManager == null) {
            nativeManager = new NativeManager();
        }

        return nativeManager;
    }


    /**
     * Prints the transcript of a given chat room.
     *
     * @param room the chat room that contains the transcript to print.
     */
    public static void printChatRoomTranscript(ChatRoom room) {
        final ChatPrinter printer = new ChatPrinter();
        final TranscriptWindow currentWindow = room.getTranscriptWindow();
        if (currentWindow != null) {
            printer.print(currentWindow);
        }
    }

    /**
     * Returns the String in the system clipboard. If not string is found,
     * null will be returned.
     *
     * @return the contents of the system clipboard. If none found, null is returned.
     */
    public static String getClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)t.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception e) {
            Log.error("Could not retrieve info from clipboard.", e);
        }
        return null;
    }

    /**
     * Adds a string to the system clipboard.
     *
     * @param str the string to add the clipboard.
     */
    public static void setClipboard(String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     * Displays a print dialog to print the transcript found in a <code>TranscriptWindow</code>
     *
     * @param transcriptWindow the <code>TranscriptWindow</code> containing the transcript.
     */
    public static void printChatTranscript(TranscriptWindow transcriptWindow) {
        final ChatPrinter printer = new ChatPrinter();
        printer.print(transcriptWindow);
    }


    /**
     * Returns the <code>SparkTransferManager</code>. This is used
     * for any transfer operations within Spark. You may use the manager to
     * intercept file transfers for filtering of transfers or own plugin operations
     * with the File Transfer object.
     *
     * @return the SpartTransferManager.
     */
    public static SparkTransferManager getTransferManager() {
        return SparkTransferManager.getInstance();
    }

    /**
     * Returns the <code>SearchManager</code>. This is used to allow
     * plugins to register their own search service.
     *
     * @return the SearchManager.
     */
    public static SearchManager getSearchManager() {
        return SearchManager.getInstance();
    }

    /**
     * Returns the <code>ContactList</code> used within Spark.
     *
     * @return the ContactList.
     */
    public static ContactList getContactList() {
        return getWorkspace().getContactList();
    }

    /**
     * Returns the User Directory to used by individual users. This allows for
     * Multi-User Support.
     *
     * @return the UserDirectory for Spark.
     */
    public static File getUserDirectory() {
        final String bareJID = sessionManager.getBareAddress();
        File userDirectory = new File(Spark.getSparkUserHome(), "/user/" + bareJID);
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        return userDirectory;
    }

    /**
     * Return the component in focus at any particular instance.
     *
     * @return the focused component
     */
    public static Component getFocusedComponent() {
        return focusedComponent;
    }

    /**
     * Adds a feature that can be discovered through Disco.
     *
     * @param namespace the namespace of the feature.
     */
    public static void addFeature(String namespace) {
        // Obtain the ServiceDiscoveryManager associated with my XMPPConnection
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(getConnection());

        // Register that a new feature is supported by this XMPP entity
        discoManager.addFeature(namespace);
    }

    /**
     * Removes a feature that can be discovered through Disco.
     *
     * @param namespace the namespace to remove.
     */
    public static void removeFeature(String namespace) {
        // Obtain the ServiceDiscoveryManager associated with my XMPPConnection
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(getConnection());

        // Register that a new feature is supported by this XMPP entity
        discoManager.removeFeature(namespace);
    }

    /**
     * Returns the image to use with most dialogs.
     *
     * @return the image to use with most dialogs.
     */
    public static ImageIcon getApplicationImage() {
        ImageIcon mainImage = Default.getImageIcon(Default.FRAME_IMAGE);
        if (mainImage == null) {
            mainImage = SparkRes.getImageIcon(SparkRes.MAIN_IMAGE);
        }
        return mainImage;
    }

}
