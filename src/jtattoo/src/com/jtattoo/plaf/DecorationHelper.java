/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/

package com.jtattoo.plaf;

import java.awt.*;
import java.lang.reflect.Method;
import javax.swing.*;

/**
 * @author Michael Hagen
 */
public class DecorationHelper {

    private DecorationHelper() {
    }

    public static void decorateWindows(Boolean decorate) {
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            try {
                Class classParams[] = {Boolean.TYPE};
                Method m = JFrame.class.getMethod("setDefaultLookAndFeelDecorated", classParams);
                Object methodParams[] = {decorate};
                m.invoke(null, methodParams);
                m = JDialog.class.getMethod("setDefaultLookAndFeelDecorated", classParams);
                m.invoke(null, methodParams);
                System.setProperty("sun.awt.noerasebackground", "true");
                System.setProperty("sun.awt.erasebackgroundonresize", "false");
            } catch (Exception ex) {
            }
        }
    }

    public static int getWindowDecorationStyle(JRootPane root) {
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            try {
                Class paramTypes[] = null;
                Object args[] = null;
                Method m = root.getClass().getMethod("getWindowDecorationStyle", paramTypes);
                Integer i = (Integer) m.invoke(root, args);
                return i.intValue();
            } catch (Exception ex) {
            }
        }
        return 0;
    }

    public static int getExtendedState(Frame frame) {
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            try {
                Class paramTypes[] = null;
                Object args[] = null;
                Method m = frame.getClass().getMethod("getExtendedState", paramTypes);
                Integer i = (Integer) m.invoke(frame, args);
                return i.intValue();
            } catch (Exception ex) {
            }
        }
        return 0;
    }

    public static void setExtendedState(Frame frame, int state) {
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            try {
                Class classParams[] = {Integer.TYPE};
                Method m = frame.getClass().getMethod("setExtendedState", classParams);
                Object methodParams[] = {new Integer(state)};
                m.invoke(frame, methodParams);
            } catch (Exception ex) {
            }
        }
    }

    public static boolean isFrameStateSupported(Toolkit tk, int state) {
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            try {
                Class classParams[] = {Integer.TYPE};
                Method m = tk.getClass().getMethod("isFrameStateSupported", classParams);
                Object methodParams[] = {new Integer(state)};
                Boolean b = (Boolean) m.invoke(tk, methodParams);
                return b.booleanValue();
            } catch (Exception ex) {
            }
        }
        return false;
    }

    public static boolean isTranslucentWindowSupported() {
        return (JTattooUtilities.getJavaVersion() >= 1.6010) && (JTattooUtilities.isMac() || JTattooUtilities.isWindows());
    }

    public static void setTranslucentWindow(Window wnd, boolean translucent) {
        if (isTranslucentWindowSupported()) {
            if (JTattooUtilities.getJavaVersion() >= 1.7) {
                if (translucent) {
                    if (!wnd.getBackground().equals(new Color(0, 0, 0, 0))) {
                        wnd.setBackground(new Color(0, 0, 0, 0));
                    }
                } else {
                    if (!wnd.getBackground().equals(new Color(0, 0, 0, 0xff))) {
                        wnd.setBackground(new Color(0, 0, 0, 0xff));
                    }
                }
            } else if (JTattooUtilities.getJavaVersion() >= 1.6010) {
                try {
                    Class clazz = Class.forName("com.sun.awt.AWTUtilities");
                    Class classParams[] = {Window.class, Boolean.TYPE};
                    Method method = clazz.getMethod("setWindowOpaque", classParams);
                    if (translucent) {
                        Object methodParams[] = {wnd, Boolean.FALSE};
                        method.invoke(wnd, methodParams);
                    } else {
                        Object methodParams[] = {wnd, Boolean.TRUE};
                        method.invoke(wnd, methodParams);
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
    
}
