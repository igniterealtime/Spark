/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.plugin;

/**
 * The Invokable interface can be used to identify a class as being capable
 * of being "invoked".
 *
 * @author Derek DeMoro
 */
public interface Invokable {

    /**
     * Invokes the object.
     *
     * @param params optional arguments from the invoker.
     * @return true if the invocation was successful, false if it failed, or was aborted.
     */
    boolean invoke(Object... params);
}