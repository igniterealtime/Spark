/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.phone;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.GraphicUtils;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.BorderLayout;

public class PhoneDialog {

    public static JFrame invoke(JComponent comp, String title, String description, ImageIcon icon) {
        final JFrame frame = new JFrame();

        frame.setIconImage(SparkRes.getImageIcon(SparkRes.TELEPHONE_24x24).getImage());
        frame.setTitle(title);

        frame.getContentPane().setLayout(new BorderLayout());

        frame.getContentPane().add(comp, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(300, 200);

        // Center panel on screen
        GraphicUtils.centerWindowOnScreen(frame);

        frame.setVisible(true);

        return frame;
    }
}
