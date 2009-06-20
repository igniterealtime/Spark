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
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.resource.SparkRes;

/**
 *
 */
public class TaskUI extends JPanel implements ActionListener {

    private static final long serialVersionUID = -8443764502684168188L;
    private Task task;
    private JCheckBox box;
    private String dateShortFormat = ((SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)).toPattern();

    public TaskUI(final Task task) {
        setLayout(new BorderLayout());
        setOpaque(false);

        this.task = task;

        box = new JCheckBox();
        box.setOpaque(false);
        JLabel dueLabel = new JLabel();
        dueLabel.setOpaque(false);
        
        JPanel p_east = new JPanel(new BorderLayout());
        p_east.setBackground(Color.white);
        
        JLabel btn_del = new JLabel(SparkRes.getImageIcon(SparkRes.TASK_DELETE_IMAGE));

        p_east.add(btn_del, BorderLayout.EAST);
        p_east.add(new JLabel("  "), BorderLayout.CENTER);
        p_east.add(dueLabel, BorderLayout.WEST);
        
        add(box, BorderLayout.WEST);
        
        //add(dueLabel, BorderLayout.EAST);
        add(p_east, BorderLayout.EAST);

        long dueDate = task.getDueDate();
        if (dueDate != -1) {
            Date d = new Date(dueDate);
            SimpleDateFormat formatter = new SimpleDateFormat(dateShortFormat);
            String theDate = formatter.format(d);
            dueLabel.setText(theDate);
        }

        int diff = DateUtils.getDaysDiff(dueDate, new Date().getTime());

        if (diff > 0){
            dueLabel.setForeground(Color.red);
        }

        box.setText(task.getTitle());

        updateTitleFont();

        box.addActionListener(this);
        
        btn_del.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                    Tasks.deleteTask(task);
            }

            public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub
            }
            public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub
            }
            public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub
            }
            public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub
            }        	
        });
    }

    public boolean isSelected() {
        return box.isSelected();
    }

    public void updateTitleFont() {
        if (task.isCompleted()) {
            Font font = box.getFont();

            Map attribs = font.getAttributes();

            attribs.put(TextAttribute.STRIKETHROUGH, true);

            box.setFont(new Font(attribs));
            box.setSelected(true);
        }
        else {
            Font font = box.getFont();

            Map Attribs = font.getAttributes();

            Attribs.put(TextAttribute.STRIKETHROUGH, false);

            box.setFont(new Font(Attribs));
            box.setSelected(false);
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

        if (ScratchPadPlugin.SHOW_ALL_TASKS) {
            setVisible(true);
        }
        else if (task.isCompleted()) {
            setVisible(false);
        }
    }

    public Task getTask() {
        return task;
    }
}
