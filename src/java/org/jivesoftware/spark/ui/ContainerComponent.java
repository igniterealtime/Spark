/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 *
 */
public interface ContainerComponent {

    public abstract String getTabTitle();

    public abstract String getFrameTitle();

    public abstract ImageIcon getTabIcon();

    public abstract JComponent getGUI();

    public abstract String getToolTipDescription();

    public abstract boolean closing();
}
