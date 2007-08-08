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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        if (task.isCompleted()) {
            box.setText("<html><body><del>" + task.getTitle() + "</del></body></html>");
        }
        else {
            box.setText(task.getTitle());
        }

        add(box, BorderLayout.CENTER);

        box.addActionListener(this);
    }

    public boolean isSelected() {
        return box.isSelected();
    }


    public void actionPerformed(ActionEvent e) {
        if (isSelected()) {
            box.setText("<del>" + task.getTitle() + "</del>");
            task.setCompleted(true);
        }
        else {
            box.setText(task.getTitle());
            task.setCompleted(false);
        }
    }

    public Task getTask() {
        return task;
    }
}
