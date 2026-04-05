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
package org.jivesoftware.resource;

import org.jivesoftware.spark.PluginRes;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class SparkRes {

    private static final Properties prb;

    public enum Icon {
        ACCEPT_CHAT("ACCEPT_CHAT"),
        ACCEPT_INVITE_IMAGE("ACCEPT_INVITE_IMAGE"),
        ADDRESS_BOOK_16x16("ADDRESS_BOOK_16x16"),
        ADD_BOOKMARK_ICON("ADD_BOOKMARK_ICON"),
        ADD_CONTACT_IMAGE("ADD_CONTACT_IMAGE"),
        ADD_IMAGE_24x24("ADD_IMAGE_24x24"),
        ADD_LINK_TO_CHAT("ADD_LINK_TO_CHAT"),
        ADD_TO_CHAT("ADD_TO_CHAT"),
        ADD_TO_KB("ADD_TO_KB"),
        ALERT("ALERT"),
        ALL_CHATS("ALL_CHATS"),
        AVAILABLE_USER("AVAILABLE_USER"),
        AWAY_USER("AWAY_USER"),
        BACKGROUND_IMAGE("BACKGROUND_IMAGE"),
        BLANK_24x24("BLANK_24x24"),
        BLANK_IMAGE("BLANK_IMAGE"),
        BLOCK_CONTACT_16x16("BLOCK_CONTACT_16x16"),
        BLUE_BALL("BLUE_BALL"),
        BOOKMARK_ICON("BOOKMARK_ICON"),
        BRICKWALL_IMAGE("BRICKWALL_IMAGE"),
        BUSY_IMAGE("BUSY_IMAGE"),
        BUZZ_IMAGE("BUZZ_IMAGE"),
        CALENDAR_IMAGE("CALENDAR_IMAGE"),
        CALL_ICON("CALL_ICON"),
        CANCEL_IMAGE("CANCEL_IMAGE"),
        CHANGELOG_IMAGE("CHANGELOG_IMAGE"),
        CHATTING_AGENT_IMAGE("CHATTING_AGENT_IMAGE"),
        CHATTING_CUSTOMER_IMAGE("CHATTING_CUSTOMER_IMAGE"),
        CHAT_QUEUE("CHAT_QUEUE"),
        CHAT_WORKSPACE("CHAT_WORKSPACE"),
        CHECK_UPDATE("CHECK_UPDATE"),
        CIRCLE_CHECK_IMAGE("CIRCLE_CHECK_IMAGE"),
        CLEAR_BALL_ICON("CLEAR_BALL_ICON"),
        CLIPBOARD("CLIPBOARD"),
        CLOSE_IMAGE("CLOSE_IMAGE"),
        CLOSE_RED_X_IMAGE("CLOSE_RED_X_IMAGE"),
        CLOSE_WHITE_X_IMAGE("CLOSE_WHITE_X_IMAGE"),
        COLOR_ICON("COLOR_ICON"),
        COMPUTER_IMAGE_16x16("COMPUTER_IMAGE_16x16"),
        CONFERENCE_IMAGE_16x16("CONFERENCE_IMAGE_16x16"),
        CONFERENCE_IMAGE_24x24("CONFERENCE_IMAGE_24x24"),
        CONFERENCE_IMAGE_48x48("CONFERENCE_IMAGE_48x48"),
        COPY_16x16("COPY_16x16"),
        CO_BROWSER_TAB_TITLE("CO_BROWSER_TAB_TITLE"),
        CREATE_FAQ_ENTRY("CREATE_FAQ_ENTRY"),
        CREATE_FAQ_TITLE("CREATE_FAQ_TITLE"),
        CURRENT_AGENTS("CURRENT_AGENTS"),
        CURRENT_CHATS("CURRENT_CHATS"),
        DATA_DELETE_16x16("DATA_DELETE_16x16"),
        DATA_REFRESH_16x16("DATA_REFRESH_16x16"),
        DEFAULT_AVATAR_16x16_IMAGE("DEFAULT_AVATAR_16x16_IMAGE"),
        DEFAULT_AVATAR_32x32_IMAGE("DEFAULT_AVATAR_32x32_IMAGE"),
        DEFAULT_AVATAR_64x64_IMAGE("DEFAULT_AVATAR_64x64_IMAGE"),
        DELETE_BOOKMARK_ICON("DELETE_BOOKMARK_ICON"),
        DESKTOP_IMAGE("DESKTOP_IMAGE"),
        DIAL_PHONE_IMAGE_16x16("DIAL_PHONE_IMAGE_16x16"),
        DIAL_PHONE_IMAGE_24x24("DIAL_PHONE_IMAGE_24x24"),
        DIVIDER_IMAGE("DIVIDER_IMAGE"),
        DOCUMENT_16x16("DOCUMENT_16x16"),
        DOCUMENT_EXCHANGE_IMAGE("DOCUMENT_EXCHANGE_IMAGE"),
        DOCUMENT_FIND_16x16("DOCUMENT_FIND_16x16"),
        DOCUMENT_INFO_32x32("DOCUMENT_INFO_32x32"),
        DOOR_IMAGE("DOOR_IMAGE"),
        DOWNLOAD_16x16("DOWNLOAD_16x16"),
        DOWN_ARROW_IMAGE("DOWN_ARROW_IMAGE"),
        DOWN_OPTION_IMAGE("DOWN_OPTION_IMAGE"),
        EARTH_LOCK_16x16("EARTH_LOCK_16x16"),
        EARTH_VIEW_16x16("EARTH_VIEW_16x16"),
        END_BUTTON_24x24("END_BUTTON_24x24"),
        ERASER_IMAGE("ERASER_IMAGE"),
        ERROR_DIALOG_TITLE("ERROR_DIALOG_TITLE"),
        ERROR_INVALID_WORKGROUP("ERROR_INVALID_WORKGROUP"),
        FACEBOOK_TRANSPORT_ACTIVE_IMAGE("FACEBOOK_TRANSPORT_ACTIVE_IMAGE"),
        FACEBOOK_TRANSPORT_INACTIVE_IMAGE("FACEBOOK_TRANSPORT_INACTIVE_IMAGE"),
        FAQ_TAB_TITLE("FAQ_TAB_TITLE"),
        FASTPATH_IMAGE_16x16("FASTPATH_IMAGE_16x16"),
        FASTPATH_IMAGE_24x24("FASTPATH_IMAGE_24x24"),
        FASTPATH_IMAGE_32x32("FASTPATH_IMAGE_32x32"),
        FASTPATH_IMAGE_64x64("FASTPATH_IMAGE_64x64"),
        FASTPATH_OFFLINE_IMAGE_16x16("FASTPATH_OFFLINE_IMAGE_16x16"),
        FASTPATH_OFFLINE_IMAGE_24x24("FASTPATH_OFFLINE_IMAGE_24x24"),
        FILTER_LABEL("FILTER_LABEL"),
        FIND_IMAGE("FIND_IMAGE"),
        FIND_TEXT_IMAGE("FIND_TEXT_IMAGE"),
        FOLDER("FOLDER"),
        FOLDER_CLOSED("FOLDER_CLOSED"),
        FONT_16x16("FONT_16x16"),
        FORUM_TAB_TITLE("FORUM_TAB_TITLE"),
        FRAME_ALWAYS_ON_TOP_ACTIVE("FRAME_ALWAYS_ON_TOP_ACTIVE"),
        FRAME_ALWAYS_ON_TOP_DEACTIVE("FRAME_ALWAYS_ON_TOP_DEACTIVE"),
        FREE_TO_CHAT_IMAGE("FREE_TO_CHAT_IMAGE"),
        FUNNEL_DOWN_16x16("FUNNEL_DOWN_16x16"),
        GADUGADU_TRANSPORT_ACTIVE_IMAGE("GADUGADU_TRANSPORT_ACTIVE_IMAGE"),
        GADUGADU_TRANSPORT_INACTIVE_IMAGE("GADUGADU_TRANSPORT_INACTIVE_IMAGE"),
        GO("GO"),
        GREEN_BALL("GREEN_BALL"),
        GREEN_FLAG_16x16("GREEN_FLAG_16x16"),
        HEADSET_IMAGE("HEADSET_IMAGE"),
        HELP2_24x24("HELP2_24x24"),
        HISTORY_16x16("HISTORY_16x16"),
        HISTORY_24x24_IMAGE("HISTORY_24x24"),
        ID_CARD_48x48("ID_CARD_48x48"),
        IM_AVAILABLE_STALE_IMAGE("IM_AVAILABLE_STALE_IMAGE"),
        IM_AWAY("IM_AWAY"),
        IM_AWAY_STALE_IMAGE("IM_AWAY_STALE_IMAGE"),
        IM_DND("IM_DND"),
        IM_DND_STALE_IMAGE("IM_DND_STALE_IMAGE"),
        IM_FREE_CHAT_STALE_IMAGE("IM_FREE_CHAT_STALE_IMAGE"),
        IM_UNAVAILABLE_STALE_IMAGE("IM_UNAVAILABLE_STALE_IMAGE"),
        IM_XA("IM_XA"),
        IM_XA_STALE_IMAGE("IM_XA_STALE_IMAGE"),
        INFORMATION_ICO("INFORMATION_ICO"),
        INFORMATION_IMAGE("INFORMATION_IMAGE"),
        INVALID_USERNAME_PASSWORD("INVALID_USERNAME_PASSWORD"),
        INVISIBLE("INVISIBLE"),
        INVITE_MORE_IMAGE("INVITE_MORE_IMAGE"),
        IRC_TRANSPORT_ACTIVE_IMAGE("IRC_TRANSPORT_ACTIVE_IMAGE"),
        IRC_TRANSPORT_INACTIVE_IMAGE("IRC_TRANSPORT_INACTIVE_IMAGE"),
        JOIN_GROUPCHAT_IMAGE("JOIN_GROUPCHAT_IMAGE"),
        KNOWLEDGE_BASE_TAB_TITLE("KNOWLEDGE_BASE_TAB_TITLE"),
        LANGUAGE_ICON("LANGUAGE_ICON"),
        LEFT_ARROW_IMAGE("LEFT_ARROW_IMAGE"),
        LIGHTBULB_ON_16x16_IMAGE("LIGHTBULB_ON_16x16_IMAGE"),
        LIGHTING_BOLT_IMAGE("LIGHTING_BOLT_IMAGE"),
        LINK_16x16("LINK_16x16"),
        LINK_DELETE_16x16("LINK_DELETE_16x16"),
        LOCK_16x16("LOCK_16x16"),
        LOCK_UNLOCK_16x16("LOCK_UNLOCK_16x16"),
        LOGIN_DIALOG_AUTHENTICATING("LOGIN_DIALOG_AUTHENTICATING"),
        LOGIN_DIALOG_LOGIN("LOGIN_DIALOG_LOGIN"),
        LOGIN_DIALOG_LOGIN_TITLE("LOGIN_DIALOG_LOGIN_TITLE"),
        LOGIN_DIALOG_PASSWORD("LOGIN_DIALOG_PASSWORD"),
        LOGIN_DIALOG_QUIT("LOGIN_DIALOG_QUIT"),
        LOGIN_DIALOG_USERNAME("LOGIN_DIALOG_USERNAME"),
        LOGIN_DIALOG_WORKSPACE("LOGIN_DIALOG_WORKSPACE"),
        LOGIN_KEY_IMAGE("LOGIN_KEY_IMAGE"),
        MAGICIAN_IMAGE("MAGICIAN_IMAGE"),
        MAIL_16x16("MAIL_16x16"),
        MAIL_FORWARD_16x16("MAIL_FORWARD_16x16"),
        MAIL_IMAGE_32x32("MAIL_IMAGE_32x32"),
        MAIL_INTO_16x16("MAIL_INTO_16x16"),
        MAIN_ICNS_FILE("MAIN_ICNS_FILE"),
        MAIN_IMAGE("MAIN_IMAGE"),
        MAIN_IMAGE_ICO("MAIN_IMAGE_ICO"),
        MAIN_TITLE("MAIN_TITLE"),
        MEGAPHONE_16x16("MEGAPHONE_16x16"),
        MESSAGE_AWAY("MESSAGE_AWAY"),
        MESSAGE_DND("MESSAGE_DND"),
        MESSAGE_NEW_TRAY("MESSAGE_NEW_TRAY"),
        MESSAGE_NEW_TRAY_LINUX("MESSAGE_NEW_TRAY_LINUX"),
        MINUS_SIGN("MINUS_SIGN"),
        MOBILE_PHONE_IMAGE("MOBILE_PHONE_IMAGE"),
        MODERATOR_IMAGE("MODERATOR_IMAGE"),
        MYSPACE_TRANSPORT_ACTIVE_IMAGE("MYSPACE_TRANSPORT_ACTIVE_IMAGE"),
        MYSPACE_TRANSPORT_INACTIVE_IMAGE("MYSPACE_TRANSPORT_INACTIVE_IMAGE"),
        NEW_MESSAGE("NEW_MESSAGE"),
        NOTEBOOK_IMAGE("NOTEBOOK_IMAGE"),
        NOTE_EDIT_16x16("NOTE_EDIT_16x16"),
        NOTIFICATIONS("NOTIFICATIONS"),
        OFFLINE_ICO("OFFLINE_ICO"),
        OFFLINE_IMAGE("OFFLINE_IMAGE"),
        ONLINE_ICO("ONLINE_ICO"),
        ON_PHONE_IMAGE("ON_PHONE_IMAGE"),
        PALETTE_24x24_IMAGE("PALETTE_24x24_IMAGE"),
        PANE_DOWN_ARROW_IMAGE("PANE_DOWN_ARROW_IMAGE"),
        PANE_UP_ARROW_IMAGE("PANE_UP_ARROW_IMAGE"),
        PAWN_GLASS_GREEN("PAWN_GLASS_GREEN"),
        PAWN_GLASS_RED("PAWN_GLASS_RED"),
        PAWN_GLASS_WHITE("PAWN_GLASS_WHITE"),
        PAWN_GLASS_YELLOW("PAWN_GLASS_YELLOW"),
        PEOPLE_IMAGE("PEOPLE_IMAGE"),
        PHOTO_IMAGE("PHOTO_IMAGE"),
        PLUGIN_IMAGE("PLUGIN_IMAGE"),
        PLUS_SIGN("PLUS_SIGN"),
        POWERED_BY_IMAGE("POWERED_BY_IMAGE"),
        PREFERENCES_IMAGE("PREFERENCES_IMAGE"),
        PRINTER_IMAGE_16x16("PRINTER_IMAGE_16x16"),
        PRIVACY_CHECK("PRIVACY_CHECK"),
        PRIVACY_DEACTIVATE_LIST("PRIVACY_DEACTIVATE_LIST"),
        PRIVACY_ICON("PRIVACY_ICON"),
        PRIVACY_ICON_SMALL("PRIVACY_ICON_SMALL"),
        PRIVACY_LIGHTNING("PRIVACY_LIGHTNING"),
        PRIVACY_MSG_ALLOW("PRIVACY_MSG_ALLOW"),
        PRIVACY_MSG_DENY("PRIVACY_MSG_DENY"),
        PRIVACY_PIN_ALLOW("PRIVACY_PIN_ALLOW"),
        PRIVACY_PIN_DENY("PRIVACY_PIN_DENY"),
        PRIVACY_POUT_ALLOW("PRIVACY_POUT_ALLOW"),
        PRIVACY_POUT_DENY("PRIVACY_POUT_DENY"),
        PRIVACY_QUERY_ALLOW("PRIVACY_QUERY_ALLOW"),
        PRIVACY_QUERY_DENY("PRIVACY_QUERY_DENY"),
        PROFILE_ICON("PROFILE_ICON"),
        PROFILE_IMAGE_16x16("PROFILE_IMAGE_16x16"),
        PROFILE_IMAGE_24x24("PROFILE_IMAGE_24x24"),
        PROFILE_TAB_TITLE("PROFILE_TAB_TITLE"),
        PUSH_URL_16x16("PUSH_URL_16x16"),
        QQ_TRANSPORT_ACTIVE_IMAGE("QQ_TRANSPORT_ACTIVE_IMAGE"),
        QQ_TRANSPORT_INACTIVE_IMAGE("QQ_TRANSPORT_INACTIVE_IMAGE"),
        QUESTIONS_ANSWERS("QUESTIONS_ANSWERS"),
        README_IMAGE("README_IMAGE"),
        RED_BALL("RED_BALL"),
        RED_FLAG_16x16("RED_FLAG_16x16"),
        REFRESH_IMAGE("REFRESH_IMAGE"),
        REJECT_CHAT("REJECT_CHAT"),
        REJECT_INVITE_IMAGE("REJECT_INVITE_IMAGE"),
        RIGHT_ARROW_IMAGE("RIGHT_ARROW_IMAGE"),
        SAMETIME_TRANSPORT_ACTIVE_IMAGE("SAMETIME_TRANSPORT_ACTIVE_IMAGE"),
        SAMETIME_TRANSPORT_INACTIVE_IMAGE("SAMETIME_TRANSPORT_INACTIVE_IMAGE"),
        SAVE_AS_16x16("SAVE_AS_16x16"),
        SEARCH("SEARCH"),
        SEARCH_IMAGE_32x32("SEARCH_IMAGE_32x32"),
        SEARCH_USER_16x16("SEARCH_USER_16x16"),
        SEND("SEND"),
        SEND_FILE_24x24("SEND_FILE_24x24"),
        SEND_FILE_ICON("SEND_FILE_ICON"),
        SEND_MAIL_IMAGE_16x16("SEND_MAIL_IMAGE_16x16"),
        SERVER_ICON("SERVER_ICON"),
        SERVER_UNAVAILABLE("SERVER_UNAVAILABLE"),
        SETTINGS_IMAGE_16x16("SETTINGS_IMAGE_16x16"),
        SETTINGS_IMAGE_24x24("SETTINGS_IMAGE_24x24"),
        SIMPLE_TRANSPORT_ACTIVE_IMAGE("SIMPLE_TRANSPORT_ACTIVE_IMAGE"),
        SIMPLE_TRANSPORT_INACTIVE_IMAGE("SIMPLE_TRANSPORT_INACTIVE_IMAGE"),
        SMALL_ABOUT_IMAGE("SMALL_ABOUT_IMAGE"),
        SMALL_ADD_IMAGE("SMALL_ADD_IMAGE"),
        SMALL_AGENT_IMAGE("SMALL_AGENT_IMAGE"),
        SMALL_ALARM_CLOCK("SMALL_ALARM_CLOCK"),
        SMALL_ALL_AGENTS_IMAGE("SMALL_ALL_AGENTS_IMAGE"),
        SMALL_ALL_CHATS_IMAGE("SMALL_ALL_CHATS_IMAGE"),
        SMALL_BUSINESS_MAN_VIEW("SMALL_BUSINESS_MAN_VIEW"),
        SMALL_CHECK("SMALL_CHECK"),
        SMALL_CIRCLE_DELETE("SMALL_CIRCLE_DELETE"),
        SMALL_CLOSE_BUTTON("SMALL_CLOSE_BUTTON"),
        SMALL_CURRENT_AGENTS("SMALL_CURRENT_AGENTS"),
        SMALL_DATA_FIND_IMAGE("SMALL_DATA_FIND_IMAGE"),
        SMALL_DELETE("SMALL_DELETE"),
        SMALL_DOCUMENT_ADD("SMALL_DOCUMENT_ADD"),
        SMALL_DOCUMENT_VIEW("SMALL_DOCUMENT_VIEW"),
        SMALL_ENTRY("SMALL_ENTRY"),
        SMALL_MESSAGE_EDIT_IMAGE("SMALL_MESSAGE_EDIT_IMAGE"),
        SMALL_MESSAGE_IMAGE("SMALL_MESSAGE_IMAGE"),
        SMALL_PIN_BLUE("SMALL_PIN_BLUE"),
        SMALL_PROFILE_IMAGE("SMALL_PROFILE_IMAGE"),
        SMALL_QUESTION("SMALL_QUESTION"),
        SMALL_SCROLL_REFRESH("SMALL_SCROLL_REFRESH"),
        SMALL_STOP("SMALL_STOP"),
        SMALL_USER1_INFORMATION("SMALL_USER1_INFORMATION"),
        SMALL_USER1_MESSAGE("SMALL_USER1_MESSAGE"),
        SMALL_USER1_MOBILEPHONE("SMALL_USER1_MOBILEPHONE"),
        SMALL_USER1_NEW("SMALL_USER1_NEW"),
        SMALL_USER1_STOPWATCH("SMALL_USER1_STOPWATCH"),
        SMALL_USER1_TIME("SMALL_USER1_TIME"),
        SMALL_USER_DELETE("SMALL_USER_DELETE"),
        SMALL_USER_ENTER("SMALL_USER_ENTER"),
        SMALL_WORKGROUP_QUEUE_IMAGE("SMALL_WORKGROUP_QUEUE_IMAGE"),
        SOUND_PREFERENCES_IMAGE("SOUND_PREFERENCES_IMAGE"),
        SPARK_IMAGE("SPARK_IMAGE"),
        SPARK_IMAGE_32x32("SPARK_IMAGE_32x32"),
        SPARK_LOGOUT_IMAGE("SPARK_LOGOUT_IMAGE"),
        SPELL_CHECK_IMAGE("SPELL_CHECK_IMAGE"),
        STAR_ADMIN("STAR_ADMIN"),
        STAR_BLUE_IMAGE("STAR_BLUE_IMAGE"),
        STAR_GREEN_IMAGE("STAR_GREEN_IMAGE"),
        STAR_GREY_IMAGE("STAR_GREY_IMAGE"),
        STAR_MODERATOR("STAR_MODERATOR"),
        STAR_OWNER("STAR_OWNER"),
        STAR_RED_IMAGE("STAR_RED_IMAGE"),
        STAR_YELLOW_IMAGE("STAR_YELLOW_IMAGE"),
        STICKY_NOTE_IMAGE("STICKY_NOTE_IMAGE"),
        TASK_DELETE_IMAGE("TASK_DELETE_IMAGE"),
        TELEPHONE_24x24("TELEPHONE_24x24"),
        TEXT_BOLD("TEXT_BOLD"),
        TEXT_ITALIC("TEXT_ITALIC"),
        TEXT_NORMAL("TEXT_NORMAL"),
        TEXT_UNDERLINE("TEXT_UNDERLINE"),
        TIME_LEFT("TIME_LEFT"),
        TOOLBAR_BACKGROUND("TOOLBAR_BACKGROUND"),
        TOOLBOX("TOOLBOX"),
        TRAFFIC_LIGHT_IMAGE("TRAFFIC_LIGHT_IMAGE"),
        TRANSFER_IMAGE_24x24("TRANSFER_IMAGE_24x24"),
        TRANSPORT_ICON("TRANSPORT_ICON"),
        TRAY_AWAY("TRAY_AWAY"),
        TRAY_AWAY_LINUX("TRAY_AWAY_LINUX"),
        TRAY_CONNECTING("TRAY_CONNECTING"),
        TRAY_CONNECTING_LINUX("TRAY_CONNECTING_LINUX"),
        TRAY_DND("TRAY_DND"),
        TRAY_DND_LINUX("TRAY_DND_LINUX"),
        TRAY_IMAGE("TRAY_IMAGE"),
        TRAY_IMAGE_LINUX("TRAY_IMAGE_LINUX"),
        TRAY_OFFLINE("TRAY_OFFLINE"),
        TRAY_OFFLINE_LINUX("TRAY_OFFLINE_LINUX"),
        TRAY_XAWAY("TRAY_XAWAY"),
        TRAY_XAWAY_LINUX("TRAY_XAWAY_LINUX"),
        TYPING_TRAY("TYPING_TRAY"),
        TYPING_TRAY_LINUX("TYPING_TRAY_LINUX"),
        UNBLOCK_CONTACT_16x16("UNBLOCK_CONTACT_16x16"),
        UNRECOVERABLE_ERROR("UNRECOVERABLE_ERROR"),
        UPLOAD_ICON("UPLOAD_ICON"),
        USER1_32x32("USER1_32x32"),
        USER1_ADD_16x16("USER1_ADD_16x16"),
        USER1_BACK_16x16("USER1_BACK_16x16"),
        USER1_MESSAGE_24x24("USER1_MESSAGE_24x24"),
        USER_HEADSET_24x24("USER_HEADSET_24x24"),
        VIEW("VIEW"),
        VIEW_IMAGE("VIEW_IMAGE"),
        WORKGROUP_QUEUE("WORKGROUP_QUEUE"),
        XMPP_TRANSPORT_ACTIVE_IMAGE("XMPP_TRANSPORT_ACTIVE_IMAGE"),
        XMPP_TRANSPORT_INACTIVE_IMAGE("XMPP_TRANSPORT_INACTIVE_IMAGE"),
        YELLOW_BALL("YELLOW_BALL"),
        YELLOW_FLAG_16x16("YELLOW_FLAG_16x16"),
        ;
        final String propertyName;

        Icon(String propertyName) {
            this.propertyName = propertyName;
        }
    }

    public static final String APP_NAME = "APP_NAME";
    public static final String DUMMY_CONTACT_IMAGE = "DUMMY_CONTACT_IMAGE";
    public static final String EXECUTABLE_NAME = "EXECUTABLE_NAME";
    public static final String WELCOME = "WELCOME";

    private static final Map<Icon, ImageIcon> iconsCache = new EnumMap<>(Icon.class);

    private static final ClassLoader cl = SparkRes.class.getClassLoader();

    static {
        prb = new Properties();
        try {
            InputStream resourceAsStream = cl.getResourceAsStream("spark.properties");
            if (resourceAsStream != null) {
                prb.load(resourceAsStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String propertyName) {
        String pluginString = PluginRes.getSparkRes(propertyName);
        return pluginString != null ? pluginString : prb.getProperty(propertyName);
    }

    public static ImageIcon getImageIcon(Icon imageName) {
        ImageIcon cachedImageIcon = iconsCache.get(imageName);
        if (cachedImageIcon != null) {
            return cachedImageIcon;
        }
        try {
            final String iconURI = getString(imageName.propertyName);
            final URL imageURL = cl.getResource(iconURI);
            if (imageURL != null) {
                ImageIcon imageIcon = new ImageIcon(imageURL);
                iconsCache.put(imageName, imageIcon);
                return imageIcon;
            } else {
                Log.warning(imageName + " not found.");
            }
        }
        catch (Exception e) {
            Log.warning("Unable to load image " + imageName, e);
        }
        return null;
    }

    public static URL getURL(String propertyName) {
        URL pluginUrl = PluginRes.getSparkURL(propertyName);
        return pluginUrl != null ? pluginUrl : cl.getResource(getString(propertyName));
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());

        JEditorPane pane = new JEditorPane();
        frame.getContentPane().add(new JScrollPane(pane));

        StringBuilder buf = new StringBuilder();
        Enumeration<String> enumeration = (Enumeration<String>) prb.propertyNames();
        while (enumeration.hasMoreElements()) {
            String token = enumeration.nextElement();
            String value = prb.getProperty(token).toLowerCase();
            if (value.endsWith(".gif") || value.endsWith(".png") || value.endsWith(".jpg") || value.endsWith(".jpeg")) {
//                SparkRes.getImageIcon(token);
                SparkRes.getImageIcon(null);
            }
            String str = "public static final String " + token + " = \"" + token + "\";\n";
            buf.append(str);
        }

        checkImageDir();
        pane.setText(buf.toString());
        frame.pack();
        frame.setVisible(true);
    }

    private static void checkImageDir() {
        File[] files = new File("c:\\code\\liveassistant\\client\\resources\\images").listFiles(File::isFile);
        if (files == null) {
            return;
        }
        for (File imageFile : files) {
            try {
                String name = imageFile.getName();
                // Check to see if the name of the file exists
                boolean exists = false;
                Enumeration<String> enumeration = (Enumeration<String>) prb.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String token = enumeration.nextElement();
                    String value = prb.getProperty(token).toLowerCase();
                    if (value.endsWith(name)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    Log.error(imageFile.getAbsolutePath() + " is not used.");
                }
            } catch (NullPointerException e) {
                // TODO: Should we worry about this?
            }
        }
    }

}
