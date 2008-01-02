/**
 * $Revision: 22540 $
 * $Date: 2005-10-10 08:44:25 -0700 (Mon, 10 Oct 2005) $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package com.jivesoftware.spark.plugin.apple;

import com.apple.cocoa.application.NSApplication;
import com.apple.cocoa.application.NSImage;
import com.apple.cocoa.foundation.NSData;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.SparkManager;

/**
 * Utilities for dealing with the apple dock
 *
 * @author Andrew Wright
 */
public final class AppleUtils {


    private boolean flash;
    private boolean usingDefaultIcon = true;

    public AppleUtils() {

        final Thread iconThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (!flash) {
                        if (!usingDefaultIcon) {
                            // Set default icon
                            NSImage defaultImage = getDefaultImage();
                            NSApplication.sharedApplication().setApplicationIconImage(defaultImage);
                            usingDefaultIcon = true;
                        }
                        try {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e) {
                            Log.error(e);
                        }
                    }
                    else {
                        final NSImage image = getImageForMessageCountOn();

                        NSApplication.sharedApplication().setApplicationIconImage(image);
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e) {
                            // Nothing to do
                        }
                        final NSImage image2 = getImageForMessageCountOff();
                        NSApplication.sharedApplication().setApplicationIconImage(image2);
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e) {
                            // Nothing to do
                        }

                        usingDefaultIcon = false;
                    }


                }
            }
        });

        iconThread.start();

    }

    /**
     * Bounce the application's dock icon to get the user's attention.
     *
     * @param critical Bounce the icon repeatedly if this is true. Bounce it
     *                 only for one second (usually just one bounce) if this is false.
     */
    public void bounceDockIcon(boolean critical) {
        int howMuch = (critical) ?
                NSApplication.UserAttentionRequestCritical :
                NSApplication.UserAttentionRequestInformational;

        final int requestID = NSApplication.sharedApplication().requestUserAttention(howMuch);

        // Since NSApplication.requestUserAttention() seems to ignore the
        // param and always bounces the dock icon continuously no matter
        // what, make sure it gets cancelled if appropriate.
        // This is Apple bug #3414391
        if (!critical) {
            Thread cancelThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10000);
                    }
                    catch (InterruptedException e) {
                        // ignore
                    }
                    NSApplication.sharedApplication().cancelUserAttentionRequest(requestID);
                }
            });
            cancelThread.start();

        }

        flash = true;
    }

    /**
     * Creates a {@link com.apple.cocoa.application.NSImage} from a string that points to an image in the class
     *
     * @param image classpath path of an image
     * @return an cocoa image object
     */
    public static NSImage getImage(String image) {
        InputStream in = ApplePlugin.class.getResourceAsStream(image);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buff = new byte[10 * 1024];
        int len;
        try {
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        }
        catch (IOException e) {
            Log.error(e.getMessage(), e);
        }

        NSData data = new NSData(out.toByteArray());
        return new NSImage(data);
    }

    /**
     * Creates a {@link com.apple.cocoa.application.NSImage} from a string that points to an image in the class
     *
     * @param url URL to retrieve image from.
     * @return an cocoa image object
     */
    public static NSImage getImage(URL url) {
        InputStream in = null;
        try {
            in = url.openStream();
        }
        catch (IOException e) {
            Log.error(e.getMessage(), e);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buff = new byte[10 * 1024];
        int len;
        try {
            if (in != null) {
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                in.close();
            }
            out.close();
        }
        catch (IOException e) {
            Log.error(e.getMessage(), e);
        }

        NSData data = new NSData(out.toByteArray());
        return new NSImage(data);
    }

    public void resetDock() {
        if (flash) {
            flash = false;
        }
    }


    public static NSImage getImageForMessageCountOn() {
        int no = SparkManager.getChatManager().getChatContainer().getTotalNumberOfUnreadMessages();
        ClassLoader loader = ApplePlugin.class.getClassLoader();
        if (no > 10) {
            no = 10;
        }

        if (no == 0) {
            URL url = loader.getResource("images/Spark-Dock-256-On.png");
            return getImage(url);
        }

        URL url = loader.getResource("images/Spark-Dock-256-" + no + "-On.png");
        return getImage(url);
    }

    public static NSImage getImageForMessageCountOff() {
        int no = SparkManager.getChatManager().getChatContainer().getTotalNumberOfUnreadMessages();
        ClassLoader loader = ApplePlugin.class.getClassLoader();
        if (no > 10) {
            no = 10;
        }

        if (no == 0) {
            URL url = loader.getResource("images/Spark-Dock-256-On.png");
            return getImage(url);
        }

        URL url = loader.getResource("images/Spark-Dock-256-" + no + "-Off.png");
        return getImage(url);
    }


    public static NSImage getDefaultImage() {
        ClassLoader loader = ApplePlugin.class.getClassLoader();
        URL url = loader.getResource("images/Spark-Dock-256-On.png");
        return getImage(url);
    }
}
