/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import javax.swing.JTextArea;

import java.awt.Dimension;

/**
 * Creates a simple Wrappable label to display Multi-Line Text.
 *
 * @author Derek DeMoro
 */
public class WrappedLabel extends JTextArea {

    /**
     * Create a simple Wrappable label.
     */
    public WrappedLabel() {
        this.setEditable(false);
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setOpaque(false);
    }

    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }
}