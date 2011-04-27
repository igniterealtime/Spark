/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.lang.reflect.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author  Michael Hagen
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
}
