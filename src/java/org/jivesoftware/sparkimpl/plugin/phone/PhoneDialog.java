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
