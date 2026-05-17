/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     */
    String getTitle();

    /**
     * Return the icon to use inside the Preferences list. The standard icon size
     * for preferences is 24x24.
     */
    Icon getIcon();

    /**
     * Return the tooltip to use for this preference. The tooltip is displayed
     * whenever a user places their mouse cursor over the icon.
     */
    String getTooltip();

    /**
     * Return the title to use inside the Preferences list. The title is displayed below
     * and centered of the icon.
     */
    String getListName();

    /**
     * Returns the associated namespace of this preference.
     */
    String getNamespace();

    /**
     * Return the UI to display whenever this preference is selected in the preference dialog.
     * Loads preferences into a GUI panel.
     */
    JComponent getGUI();

    /**
     * Called whenever the preference is invoked from the Preference list.
     * Loads the underlined settings data model from a file.
     * Use {@link #getData()} to retrieve the settings data model.
     */
    void load();

    /**
     * Called whenever the preference should be saved.
     */
    void commit();

    /**
     * Return true if the data supplied is valid, otherwise return false.
     */
    boolean isDataValid();

    /**
     * The error message to display if #isDataDisplayed returns false.
     */
    String getErrorMessage();

    /**
     * Returns the data model representing this preference.
     * You can retrieve it with {@link PreferenceManager#getPreferenceData(String)}.
     * If you don't use the settings data model then return null.
     */
    default Object getData() {
        return null;
    }

    /**
     * Called when Spark is closing. This should be used to persist any information at that time.
     * @deprecated the saving of properties should happen only on commit(). Not used anymore.
     */
    @Deprecated
    default void shutdown() {}
}
