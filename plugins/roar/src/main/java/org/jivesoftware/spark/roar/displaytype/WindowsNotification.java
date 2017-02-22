package org.jivesoftware.spark.roar.displaytype;

import java.awt.SystemTray;
import java.awt.TrayIcon;

public class WindowsNotification {

    public static void sendNotification(String title, String bodyText) {

        TrayIcon[] trayIcon = SystemTray.getSystemTray().getTrayIcons();
        if (trayIcon.length == 1) {
            trayIcon[0].displayMessage(title, bodyText, TrayIcon.MessageType.INFO);
        }

    }

}
