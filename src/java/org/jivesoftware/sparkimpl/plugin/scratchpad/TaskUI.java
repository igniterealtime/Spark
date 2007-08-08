/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.scratchpad;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 */
public class TaskUI extends JPanel implements ActionListener {

    private Task task;
    private JCheckBox box;


    public TaskUI(Task task) {
        setLayout(new BorderLayout());
        setOpaque(false);

        this.task = task;

        box = new JCheckBox();

        add(box, BorderLayout.CENTER);

        box.setText(task.getTitle());

        updateTitleFont();

        box.addActionListener(this);
    }

    public boolean isSelected() {
        return box.isSelected();
    }

    public void updateTitleFont() {
        if (isSelected()) {
            Font font = box.getFont();

            Map Attribs = font.getAttributes();

            Attribs.put(TextAttribute.STRIKETHROUGH, true);

            box.setFont(new Font(Attribs));
        }
        else {
            Font font = box.getFont();

            Map Attribs = font.getAttributes();

            Attribs.put(TextAttribute.STRIKETHROUGH, false);

            box.setFont(new Font(Attribs));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (isSelected()) {
            task.setCompleted(true);
        }
        else {
            task.setCompleted(false);
        }

        updateTitleFont();
    }

    public Task getTask() {
        return task;
    }
}
