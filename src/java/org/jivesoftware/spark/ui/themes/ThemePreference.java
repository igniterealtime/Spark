/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.themes;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 */
public class ThemePreference implements Preference {

    private ThemePanel panel;

    public static String NAMESPACE = "themes";

    public ThemePreference() {

    }


    public String getTitle() {
        return "Appearance";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.HISTORY_24x24_IMAGE);
    }

    public String getTooltip() {
        return "Change the appearance of your chats.";
    }

    public String getListName() {
        return "Appearance";
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public JComponent getGUI() {
        if (panel == null) {
            panel = new ThemePanel();
        }
        return panel;
    }

    public void loadFromFile() {

    }

    public void load() {
    }

    public boolean isDataValid() {
        return true;
    }

    public String getErrorMessage() {
        return null;
    }

    public Object getData() {
        return null;
    }

    public void commit() {

    }


    public void shutdown() {

    }


}

