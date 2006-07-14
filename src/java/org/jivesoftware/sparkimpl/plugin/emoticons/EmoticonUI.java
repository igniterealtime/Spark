/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.jivesoftware.resource.EmotionRes;
import org.jivesoftware.spark.component.RolloverButton;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

public class EmoticonUI extends JPanel {
    private Map emoticons;
    private EmoticonPickListener listener;

    public EmoticonUI() {
        setBackground(Color.white);

        emoticons = EmotionRes.getEmoticonMap();

        int no = emoticons.size();

        int rows = no / 5;

        setLayout(new GridLayout(rows, 5));

        // Add Emoticons
        Iterator iter = emoticons.keySet().iterator();
        while (iter.hasNext()) {
            final String text = (String)iter.next();
            ImageIcon icon = EmotionRes.getImageIcon(text);

            RolloverButton emotButton = new RolloverButton();
            emotButton.setIcon(icon);
            emotButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.emoticonPicked(text);
                }
            });
            add(emotButton);
        }
    }

    public void setEmoticonPickListener(EmoticonPickListener listener) {
        this.listener = listener;
    }

    public interface EmoticonPickListener {

        void emoticonPicked(String emoticon);
    }
}
