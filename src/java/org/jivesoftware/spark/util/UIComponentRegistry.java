/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *  
 * Copyright (C) 2011 eZuce Inc. All rights reserved.
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
package org.jivesoftware.spark.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.jivesoftware.LoginDialog;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ButtonFactory;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.CommandPanel;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactInfoWindow;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.conferences.ConferenceServices;
import org.jivesoftware.spark.ui.conferences.GroupChatParticipantList;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.themes.ThemePanel;
import org.jivesoftware.spark.util.log.Log;

/**
 * This is a registry for components that may be replaced by plugins. Also
 * doubles as a factory to instantiate those components.
 *
 */
public final class UIComponentRegistry {
    // use Spark defaults, so without any plugins we still have Spark's
    // functionality
    private static Class<? extends ContactItem> contactItemClass = ContactItem.class;
    private static Class<? extends ContactInfoWindow> contactInfoWindowClass = ContactInfoWindow.class;
    private static Class<? extends ContactGroup> contactGroupClass = ContactGroup.class;
    private static Class<? extends ContactList> contactListClass = ContactList.class;
    private static Class<? extends StatusBar> statusBarClass = StatusBar.class;
    private static Class<? extends CommandPanel> commandPanelClass = CommandPanel.class;
    private static Class<? extends SparkTabbedPane> workspaceTabPaneClass = SparkTabbedPane.class;
    private static Class<? extends LoginDialog> loginDialogClass = LoginDialog.class;
    private static Class<? extends ThemePanel> themePanelClass = ThemePanel.class;
    private static Class<? extends ConferenceServices> conferenceServicesClass = ConferenceServices.class;
    private static Class<? extends TranscriptWindow> transcriptWindowClass = TranscriptWindow.class;
    private static Class<? extends ChatRoom> chatRoomClass = ChatRoomImpl.class;
        private static Class<? extends GroupChatRoom> groupChatRoomClass=GroupChatRoom.class;
        private static Class<? extends GroupChatParticipantList> groupChatParticipantListClass=GroupChatParticipantList.class;
    private static Class<? extends ChatContainer> chatContainerClass = ChatContainer.class;
    private static Class<? extends ButtonFactory> buttonFactoryClass = ButtonFactory.class;



    private UIComponentRegistry() {
        // disable instantiation
    }

    /**
     * Registers a new class implementing a contact item.
     *
     * @param clazz
     */
    public static void registerLoginDialog(Class<? extends LoginDialog> clazz) {
        if (loginDialogClass != clazz) {
            Log.debug("Registering new contract item class: "
                    + clazz.getName());
            loginDialogClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a contact item.
     *
     * @param clazz
     */
    public static void registerContactItem(Class<? extends ContactItem> clazz) {
        if (contactItemClass != clazz) {
            Log.debug("Registering new contract item class: "
                    + clazz.getName());
            contactItemClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a contact info window.
     *
     * @param clazz
     */
    public static void registerContactInfoWindow(
            Class<? extends ContactInfoWindow> clazz) {
        if (contactInfoWindowClass != clazz) {
            Log.debug("Registering new contact info window class: "
                    + clazz.getName());
            contactInfoWindowClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a contact group.
     *
     * @param clazz
     */
    public static void registerContactGroup(Class<? extends ContactGroup> clazz) {
        if (contactGroupClass != clazz) {
            Log.debug("Registering new contact group class: "
                    + clazz.getName());
            contactGroupClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a contact group.
     *
     * @param clazz
     */
    public static void registerStatusBar(Class<? extends StatusBar> clazz) {
        if (statusBarClass != clazz) {
            Log.debug("Registering new status bar class: " + clazz.getName());
            statusBarClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a command panel.
     *
     * @param clazz
     */
    public static void registerCommandPanel(Class<? extends CommandPanel> clazz) {
        if (commandPanelClass != clazz) {
            Log.debug("Registering new command panel class: "
                    + clazz.getName());
            commandPanelClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a tab panel, for use within the main
     * application window.
     *
     * @param clazz
     */
    public static void registerWorkspaceTabPanel(Class<? extends SparkTabbedPane> clazz) {
        if (workspaceTabPaneClass != clazz) {
            Log.debug("Registering new search panel class: " + clazz.getName());
            workspaceTabPaneClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a contact group. Registers a new class
     * implementing a contact list.
     *
     * @param clazz
     */
    public static void registerContactList(Class<? extends ContactList> clazz) {
        if (contactListClass != clazz) {
            Log.debug("Registering new contact list class: " + clazz.getName());
            contactListClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a theme panel.
     *
     * @param clazz
     */
    public static void registerThemePanel(Class<? extends ThemePanel> clazz) {
        if (themePanelClass != clazz) {
            Log.debug("Registering new theme panel class: " + clazz.getName());
            themePanelClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing conference services.
     *
     * @param clazz
     */
    public static void registerConferenceServices(Class<? extends ConferenceServices> clazz) {
        if (conferenceServicesClass != clazz) {
            Log.debug("Registering new conference services class: " + clazz.getName());
            conferenceServicesClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing transcript window.
     *
     * @param clazz
     */
    public static void registerTranscriptWindow(Class<? extends TranscriptWindow> clazz) {
        if (transcriptWindowClass != clazz) {
            Log.debug("Registering new transcript window class: " + clazz.getName());
            transcriptWindowClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a chat room.
     *
     * @param clazz
     */
    public static void registerChatRoom(Class<? extends ChatRoom> clazz) {
        if (chatRoomClass != clazz) {
            Log.debug("Registering new chat room class: " + clazz.getName());
            chatRoomClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

        public static void registerGroupChatRoom(Class<? extends GroupChatRoom> clazz) {
                if (groupChatRoomClass != clazz){
                    Log.debug("Registering new group chat room class: " + clazz.getName());
                    groupChatRoomClass = clazz;
                } else {
                    Log.warning("Class " + clazz.getName() + " already registered.");
                }
        }

        public static void registerGroupChatParticipantList(Class<? extends GroupChatParticipantList> clazz) {
                if (groupChatParticipantListClass != clazz){
                    Log.debug("Registering new group chat participant list class: " + clazz.getName());
                    groupChatParticipantListClass = clazz;
                } else {
                    Log.warning("Class " + clazz.getName() + " already registered.");
                }
        }

    /**
     * Registers a new class implementing a chat room.
     *
     * @param clazz
     */
    public static void registerChatContainer(Class<? extends ChatContainer> clazz) {
        if (chatContainerClass != clazz) {
            Log.debug("Registering new chat room class: " + clazz.getName());
            chatContainerClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Registers a new class implementing a button factory.
     *
     * @param clazz
     */
    public static void registerButtonFactory(Class<? extends ButtonFactory> clazz) {
        if (buttonFactoryClass != clazz) {
            Log.debug("Registering new button factory class: " + clazz.getName());
            buttonFactoryClass = clazz;
        } else {
            Log.warning("Class " + clazz.getName() + " already registered.");
        }
    }

    /**
     * Creates a new contact item object.
     *
     * @param alias
     * @param nickname
     * @param fullyQualifiedJID
     *
     * @return A new instance of the class currently registered as contact item.
     */
    public static ContactItem createContactItem(String alias, String nickname,
            String fullyQualifiedJID) {
        // null breaks instantiation by reflection
        final String nick = nickname != null ? nickname : "";
        final String jid = fullyQualifiedJID != null ? fullyQualifiedJID : "";
        final String aliass = alias != null ? alias : "";

        return instantiate(contactItemClass, aliass, nick, jid);
    }

    /**
     * Retrieves the contact info window instance (this is implemented as a
     * singleton).
     *
     * @return The instance of the class currently registered as contact info
     *         window.
     */
    public static ContactInfoWindow getContactInfoWindow() {
        ContactInfoWindow instance = null;
        try {
            final Method m = contactInfoWindowClass.getMethod("getInstance");
            final Object o = m.invoke(contactInfoWindowClass);
            instance = contactInfoWindowClass.cast(o);
        } catch (final Exception e) {
            // not pretty but we're catching 5 exceptions we can do little about
            Log.error(
                    "Error calling getInstance for "
                            + contactInfoWindowClass.getName(), e);
        }

        return instance;
    }

    /**
     * Creates a new contact group object.
     *
     * @param name
     *
     * @return A new instance of the class currently registered as contact
     *         group.
     */
    public static ContactGroup createContactGroup(String name) {
        return instantiate(contactGroupClass, name);
    }

    /**
     * Creates a new contact group object
     *
     * @param name
     *
     * @return A new instance of the class currently registered as status bar.
     */
    public static StatusBar createStatusBar() {
        return instantiate(statusBarClass);
    }

    /**
     * Creates a new command panel object
     *
     * @param name
     *
     * @return A new instance of the class currently registered as status bar.
     */
    public static CommandPanel createCommandPanel() {
        return instantiate(commandPanelClass);
    }

    /**
     * Creates a new workspace tab panel object
     *
     * @param name
     *
     * @return
     */
    public static SparkTabbedPane createWorkspaceTabPanel(int tabPosition) {
        return instantiate(workspaceTabPaneClass, new Integer(tabPosition));
    }

    /**
     * Creates a new login dialog panel object
     *
     * @param name
     *
     * @return
     */
    public static LoginDialog createLoginDialog() {
        return instantiate(loginDialogClass);
    }

    /**
     * Creates a new contact list object.
     *
     * @return A new instance of the class currently registered as contact list.
     */
    public static ContactList createContactList() {
        return instantiate(contactListClass);
    }

    /**
     * Creates a new theme panel object.
     *
     * @return A new instance of the class currently registered as theme panel.
     */
    public static ThemePanel createThemePanel() {
        return instantiate(themePanelClass);
    }

    /**
     * Creates a new conference services object.
     *
     * @return A new instance of the class currently registered as conference
     *         services.
     */
    public static ConferenceServices createConferenceServices() {
        return instantiate(conferenceServicesClass);
    }

    /**
     * Creates a new transcript window object.
     *
     * @return A new instance of the class currently registered as transcript
     *         window.
     */
    public static TranscriptWindow createTranscriptWindow() {
        return instantiate(transcriptWindowClass);
    }

    /**
     * Creates a new chat room object.
     *
     * @return A new instance of the class currently registered as chat room.
     */
    public static ChatRoom createChatRoom(String participantJID, String participantNickname, String title) {
        return instantiate(chatRoomClass, participantJID, participantNickname, title);
    }

        public static GroupChatRoom createGroupChatRoom(MultiUserChat muc) {
        return instantiate(groupChatRoomClass, muc);
    }

        public static GroupChatParticipantList createGroupChatParticipantList(){
            return instantiate(groupChatParticipantListClass);
        }

    /**
     * Creates a new chat container object.
     *
     * @return A new instance of the class currently registered as chat
     *         container.
     */
    public static ChatContainer createChatContainer() {
        return instantiate(chatContainerClass);
    }

    /**
     * Retrieves the button factory instance (this is implemented as a
     * singleton).
     *
     * @return The instance of the class currently registered as button factory.
     */
    public static ButtonFactory getButtonFactory() {
        ButtonFactory instance = null;
        try {
            final Method m = buttonFactoryClass.getMethod("getInstance");
            final Object o = m.invoke(buttonFactoryClass);
            instance = buttonFactoryClass.cast(o);
        } catch (final Exception e) {
            // not pretty but we're catching 5 exceptions we can do little about
            Log.error("Error calling getInstance for " + buttonFactoryClass.getName(), e);
        }

        return instance;
    }

    /**
     * Instantiate a given class.
     *
     * @param currentClass
     *            Class to instantiate.
     * @param args
     *            Arguments for the class constructor.
     * @return New instance, what else?
     */
    private static <T> T instantiate(Class<? extends T> currentClass, Object... args) {
        T instance = null;

        Log.debug("Args: " + Arrays.toString(args));
        try {
            if (args != null) {
                Class<? extends Object>[] classes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    classes[i] = args[i].getClass();
                }
                final Constructor<? extends T> ctor = currentClass.getDeclaredConstructor(classes);
                instance = ctor.newInstance(args);
            } else {
                final Constructor<? extends T> ctor = currentClass.getDeclaredConstructor();
                instance = ctor.newInstance();
            }
        } catch (final Exception e) {
            // not pretty but we're catching several exceptions we can do little
            // about
            Log.error("Error calling constructor for " + currentClass.getName(), e);
        }
        return instance;
    }

}