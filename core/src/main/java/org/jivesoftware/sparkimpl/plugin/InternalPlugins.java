package org.jivesoftware.sparkimpl.plugin;

import org.jivesoftware.spark.plugin.PublicPlugin;

import java.util.List;

public class InternalPlugins {

    public static List<PublicPlugin> getInternalPlugins() {
        return List.of(
            new PublicPlugin("Jabber Browser", "org.jivesoftware.sparkimpl.plugin.jabber.JabberBrowser", "1.1"),
//        new PublicPlugin("Asterisks Phone Plugin", "org.jivesoftware.sparkimpl.plugin.phone.PhonePlugin", "1.1"),
            new PublicPlugin("Jabber Version", "org.jivesoftware.sparkimpl.plugin.jabber.JabberVersion", "1.1"),
            new PublicPlugin("Sounds Plugin", "org.jivesoftware.sparkimpl.preference.sounds.SoundPlugin", "1.1"),
            new PublicPlugin("Layout Plugin", "org.jivesoftware.sparkimpl.plugin.layout.LayoutPlugin", "1.1"),
            new PublicPlugin("Chat Post Loader", "org.jivesoftware.sparkimpl.plugin.chat.ChatArgumentsPlugin", "1.1"),
            new PublicPlugin("Presence Change Plugin", "org.jivesoftware.sparkimpl.plugin.chat.PresenceChangePlugin", "1.1"),
            new PublicPlugin("Plugin Viewer Plugin", "org.jivesoftware.sparkimpl.plugin.viewer.PluginViewer", "1.1"),
            new PublicPlugin("Emoticon Plugin", "org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonPlugin", "1.1"),
            new PublicPlugin("Sparklers Plugin", "org.jivesoftware.sparkimpl.plugin.sparklers.SparklersPlugin", "1.1"),
            new PublicPlugin("Notes Plugin", "org.jivesoftware.sparkimpl.plugin.scratchpad.ScratchPadPlugin", "1.1"),
            new PublicPlugin("Buzz Plugin", "org.jivesoftware.sparkimpl.plugin.alerts.BuzzPlugin", "1.1"),
            new PublicPlugin("Notifications Plugin", "org.jivesoftware.sparkimpl.preference.notifications.NotificationPlugin", "1.1"),
            new PublicPlugin("Shortcut Plugin", "org.jivesoftware.sparkimpl.plugin.chat.ShortcutPlugin", "1.1"),
            new PublicPlugin("History Plugin", "org.jivesoftware.sparkimpl.plugin.history.ConversationHistoryPlugin", "1.1"),
            new PublicPlugin("Contact List Assistant", "org.jivesoftware.sparkimpl.plugin.chat.ContactListAssistantPlugin", "1.1"),
            new PublicPlugin("My Favorites", "org.jivesoftware.sparkimpl.plugin.history.FrequentContactsPlugin", "1.1"),
            new PublicPlugin("Language Plugin", "org.jivesoftware.sparkimpl.plugin.language.LanguagePlugin", "1.1"),
            new PublicPlugin("SystemTray", "org.jivesoftware.sparkimpl.plugin.systray.SysTrayPlugin", "1.1"),
            new PublicPlugin("UserIdle", "org.jivesoftware.sparkimpl.plugin.idle.UserIdlePlugin", "1.1"),
            new PublicPlugin("Privacy Lists", "org.jivesoftware.sparkimpl.plugin.privacy.PrivacyPlugin", "2.1")
        );
    }
}
