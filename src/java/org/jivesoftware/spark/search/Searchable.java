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
