package org.jivesoftware.spark.roar.displaytype;

import java.io.File;

import org.jivesoftware.Spark;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Provides Notification-Center integration for OSX 10.8+<br>
 * <br>
 * 
 * NSUserNotificationsBridge is based on
 * https://github.com/petesh/OSxNotificationCenter
 * 
 * @author wolf.posdorfer
 */
public class MacNotificationCenter {

    interface NSUserNotificationsBridge extends Library {

        File dylib = new File(Spark.getPluginDirectory().getAbsolutePath()
                + File.separator + "roar" + File.separator + "native" + File.separator + "NSUserNotificationsBridge.dylib" );

        NSUserNotificationsBridge instance = Native.load(dylib.getAbsolutePath(), NSUserNotificationsBridge.class);

        int sendNotification(String title, String subtitle, String text, int timeoffset, String sound);
    }

    public static void sendNotification(String title, String bodyText) {
        NSUserNotificationsBridge.instance.sendNotification(title, "", bodyText, 0, "Ping");
    }

}
