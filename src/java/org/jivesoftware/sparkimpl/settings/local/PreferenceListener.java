/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings.local;

/**
 * Implement this interface to be notified that the preferences have been changed.
 *
 * @author Derek DeMoro
 */
public interface PreferenceListener {

    /**
     * Notified when preferences are changed.
     *
     * @param preference the new preferences.
     */
    void preferencesChanged(LocalPreferences preference);
}
