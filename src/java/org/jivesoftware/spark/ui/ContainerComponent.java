/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui;

import javax.swing.JComponent;

import java.awt.Component;

/**
 *
 */
public abstract class ContainerComponent extends JComponent {

    public abstract String getTabTitle();

    public abstract void closing();
}
