package org.jivesoftware.game.reversi;

import org.jivesoftware.resource.UTF8Control;
import org.jivesoftware.spark.util.log.Log;

import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class ReversiRes {
    private static PropertyResourceBundle prb;
    public static final String REVERSI_ICON = "REVERSI_ICON";
    public static final String REVERSI_BOARD="REVERSI_BOARD";
    public static final String REVERSI_SCORE_WHITE="REVERSI_SCORE_WHITE";
    public static final String REVERSI_SCORE_BLACK="REVERSI_SCORE_BLACK";
    public static final String REVERSI_LABEL_BLACK="REVERSI_LABEL_BLACK";
    public static final String REVERSI_LABEL_WHITE="REVERSI_LABEL_WHITE";
    public static final String REVERSI_RESIGN="REVERSI_RESIGN";
    public static final String REVERSI_YOU="REVERSI_YOU";
    public static final String REVERSI_THEM="REVERSI_THEM";

    private ReversiRes() {

    }

    private static final ClassLoader cl = ReversiRes.class.getClassLoader();

    static {
        ReversiRes.prb = (PropertyResourceBundle) ResourceBundle.getBundle("reversi", new UTF8Control());
    }

    public static String getString(String propertyName) {
        return ReversiRes.prb.getString(propertyName);
    }

    public static ImageIcon getImageIcon(String imageName) {
        try {
            final String iconURI = ReversiRes.getString(imageName);
            final URL imageURL = ReversiRes.cl.getResource(iconURI);
            if (imageURL != null) {
                return new ImageIcon(imageURL);
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
        return ReversiRes.cl.getResource(ReversiRes.getString(propertyName));
    }
}
