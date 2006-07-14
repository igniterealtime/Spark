/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.preference;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * The <code>Preference</code> class allows plugin developers to add their own preferences
 * to the Spark client.
 */
public interface Preference {

    /**
     * Return the title to use in the preference window.
     *
     * @return the title to use inside the preferences list.
     */
    String getTitle();

    /**
     * Return the icon to use inside the Preferences list. The standard icon size
     * for preferences is 24x24.
     *
     * @return the icon to use inside the Preferences list.
     */
    Icon getIcon();

    /**
     * Return the tooltip to use for this preference. The tooltip is displayed
     * whenever a user places their mouse cursor over the icon.
     *
     * @return the tooltip to display.
     */
    String getTooltip();

    /**
     * Return the title to use inside the Preferences list. The title is displayed below
     * and centered of the icon.
     *
     * @return the title to use inside the preferences list.
     */
    String getListName();

    /**
     * Returns the associated namespace of this preference.
     *
     * @return the associated namespace of this preference.
     */
    String getNamespace();

    /**
     * Return the UI to display whenever this preference is selected in the preference dialog.
     *
     * @return the UI to display when this preference is selected.
     */
    JComponent getGUI();

    /**
     * Called whenever the preference is invoked from the Preference list.
     */
    void load();

    /**
     * Called whenever the preference should be saved.
     */
    void commit();

    /**
     * Return true if the data supplied is valid, otherwise return false.
     *
     * @return true if the data supplied is valid.
     */
    boolean isDataValid();

    /**
     * The error message to display if #isDataDisplayed returns false.
     *
     * @return the error message to display.
     */
    String getErrorMessage();

    /**
     * Returns the data model representing this preference.
     *
     * @return the data model representing this preference.
     */
    Object getData();

    /**
     * Called when Spark is closing. This should be used to persist any information at that time.
     */
    void shutdown();
}