/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.search;

import javax.swing.Icon;

/**
 * Plugin writers will implement <code>Searchable</code> in order to tie into the
 * find feature in Spark.
 */
public interface Searchable {

    /**
     * Return the icon you wish to use in the IconTextField.
     *
     * @return the icon you wish to use in the icon text field.
     */
    Icon getIcon();

    /**
     * Return the name of your plugin.
     *
     * @return the name of your searchable object.
     */
    String getName();

    /**
     * Return the default text that appears in the textfield when a user selects
     * it in the dropdown list.
     *
     * @return the default text.
     */
    String getDefaultText();

    /**
     * Return the text you wish to show in the tooltip when a user hovers over the
     * searchable find field.
     *
     * @return the tooltip text.
     */
    String getToolTip();

    /**
     * Is called whenver a user does an explict search within Spark. You are responsible
     * for the searching and displaying of the search results.
     *
     * @param query the explict query.
     */
    void search(String query);
}
