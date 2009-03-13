/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import java.awt.Component;
import java.io.File;
import java.util.Collection;

/**
 * The <code>FileDropListener</code> interface is one of the interfaces extension
 * writers use to add functionality to Spark.
 * <p/>
 * In general, you implement this interface in order to listen
 * for file drops onto components.
 */
public interface FileDropListener {

    /**
     * Called when a file(s) has been Drag and Dropped onto a component.
     *
     * @param files     the Collection of Files.
     * @param component the Component the files were dropped on.
     */
    void filesDropped(Collection<File> files, Component component);

}
