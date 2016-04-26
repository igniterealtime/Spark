package org.jivesoftware.spark.roar.displaytype;

import java.io.File;

import org.jivesoftware.Spark;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class MacNotificationCenter {

    interface NSUserNotificationsBridge extends Library {

        final File dylib = new File(Spark.getPluginDirectory().getAbsolutePath()
                + "/roar/NSUserNotificationsBridge.dylib");

        NSUserNotificationsBridge instance = (NSUserNotificationsBridge) Native.loadLibrary(dylib.getAbsolutePath(),
                NSUserNotificationsBridge.class);

        public int sendNotification(String title, String subtitle, String text, int timeoffset, String sound);
    }

    public static void sendNotification(String title, String bodyText) {
        NSUserNotificationsBridge.instance.sendNotification(title, "", bodyText, 0, "Ping");
    }

}
