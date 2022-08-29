package org.jivesoftware.spark.ui.themes;

import com.formdev.flatlaf.FlatLaf;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.jivesoftware.spark.ui.themes.lafs.SparkLightLaf;

/**
 * Manages the Look and Feel instances that can be used by Spark.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class LookAndFeelManager {

    /**
     * All non-system provided look and feel implementations that are available.
     */
    public static final Class<? extends LookAndFeel> lafs[] = new Class[]{
        //flatlaf
        org.jivesoftware.spark.ui.themes.lafs.SparkLightLaf.class,
        org.jivesoftware.spark.ui.themes.lafs.SparkIntelliJLaf.class
};

    static {
        // Add all additional look and feels to the UIManager.
        for (Class<? extends LookAndFeel> laf : lafs) {
            String name;
            try {
                name = laf.newInstance().getName();
            } catch (InstantiationException | IllegalAccessException e) {
                name = laf.getTypeName();
            }
            UIManager.installLookAndFeel(name, laf.getName());

        }
    }

    private static transient Map<String, String> lookAndFeelClassByName = null;

    /**
     * Gets the (human readable) name of a Look and Feel, based on its class
     * name.
     *
     * @param className A class name (eg:
     * com.jtattoo.plaf.luna.LunaLookAndFeel).
     * @return a human readable name, or null if the class name is not
     * recognized.
     */
    public static String getName(String className) {
        final Map<String, String> lookAndFeelClassByName = getLookAndFeelClassByName();
        for (final Map.Entry<String, String> entry : lookAndFeelClassByName.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(className)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Gets the class name of a Look and Feel, based on its (human readable)
     * name.
     *
     * @param name a human readable name (eg: "Luna")
     * @return A class name (eg: com.jtattoo.plaf.luna.LunaLookAndFeel) or null
     * if the class name is not recognized
     */
    public static String getClassName(String name) {
        return getLookAndFeelClassByName().get(name);
    }

    /**
     * Determines if setting the look and feel as identified by it's human
     * readable name requires a Spark restart.
     *
     * @param name The (human readable name (eg: "Luna").
     * @return true when Spark must be restarted to apply this Look And Feel.
     */
    public static boolean requiresRestart(String name) {
        // TODO: the original code required a restart for the java-provided LaFs and Synthetica, but perhaps that's no longer needed?
        return false;
    }

    private synchronized static Map<String, String> getLookAndFeelClassByName() {
        // Lazy loading of the ordered collection.
        if (lookAndFeelClassByName == null) {
            final UIManager.LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();

            lookAndFeelClassByName = new TreeMap<>(String::compareToIgnoreCase);
            for (final UIManager.LookAndFeelInfo lafi : lafis) {
                //Add only custom Laf, drop system Laf
                if(lafi.getClassName().contains("org.jivesoftware.spark.ui.themes.lafs.")){
                    lookAndFeelClassByName.put(lafi.getName(), lafi.getClassName());
                }
            }
        }
        return lookAndFeelClassByName;
    }

    private static String getLookandFeel(LocalPreferences preferences) {
        String result;

        String whereToLook = Spark.isMac() ? Default.DEFAULT_LOOK_AND_FEEL_MAC : Default.DEFAULT_LOOK_AND_FEEL;

        if (!Default.getBoolean(Default.LOOK_AND_FEEL_DISABLED)) {
            result = preferences.getLookAndFeel();
        } else if (Default.getString(whereToLook).length() > 0) {
            result = Default.getString(whereToLook);
        } else {
            result = UIManager.getSystemLookAndFeelClassName();
        }

        return result;
    }

    /**
     * Handles the Loading of the Look And Feel, as defined as the prefered Look
     * And Feel in the local settings.
     */
    public static void loadPreferredLookAndFeel() {
        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
        final String laf = getLookandFeel(preferences);

        if (laf.toLowerCase().contains("substance")) {
            EventQueue.invokeLater(() -> doSetLookAndFeel(laf));
        } else {
            doSetLookAndFeel(laf);
        }
    }

    private static void doSetLookAndFeel(String laf) {
        try {
            if (Spark.isWindows()) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
            }
            //skip loading from preference and use flatlaf as default
            UIManager.put( "TabbedPane.tabLayoutPolicy", "scroll" );
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.hasFullBorder", true);
            UIManager.put("TabbedPane.underlineColor", new Color(242, 159, 97));
            UIManager.put("TitlePane.unifiedBackground",false);
            // Add "eye" button to show password for all passwordField
            UIManager.put("PasswordField.showRevealButton",true);
            if(!laf.contains("org.jivesoftware.spark.ui.themes.lafs.")){
                UIManager.setLookAndFeel(Default.getString(Default.DEFAULT_LOOK_AND_FEEL));
                SettingsManager.getLocalPreferences().setLookAndFeel(Default.getString(Default.DEFAULT_LOOK_AND_FEEL));
            }
            UIManager.setLookAndFeel(laf);
            FlatLaf.updateUILater();
        } catch (Exception e) {
            Log.error("An exception occurred while trying to load the look and feel.", e);
        }
    }

    /**
     * Returns the human readable name of all available Look And Feels.
     *
     * @return An array of names, never null.
     */
    public static String[] getLookAndFeelNames() {
        return getLookAndFeelClassByName().keySet().toArray(new String[0]);
    }
}
