/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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