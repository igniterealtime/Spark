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

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class ScratchPadPlugin implements Plugin {
    private ContactList contactList;


    public void initialize() {
        contactList = SparkManager.getWorkspace().getContactList();
        contactList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F6"), "viewNotes");

        contactList.getActionMap().put("viewNotes", new AbstractAction("viewNotes") {
            public void actionPerformed(ActionEvent evt) {
                // Retrieve notes and dispaly in editor.
                retrieveNotes();
            }
        });

        contactList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F5"), "viewTasks");

        contactList.getActionMap().put("viewTasks", new AbstractAction("viewTasks") {
            public void actionPerformed(ActionEvent evt) {
                // Retrieve notes and dispaly in editor.
                showTaskList();
            }
        });
    }

    private void showTaskList() {
        final JFrame frame = new JFrame("Tasks");
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());

        final Map map = new HashMap();
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        mainPanel.setBackground(Color.white);

        final RolloverButton addButton = new RolloverButton("Add Task...");
        addButton.setForeground(Color.gray);
        mainPanel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String taskTitle = JOptionPane.showInputDialog(frame, "Enter Task:", "Create Task", JOptionPane.INFORMATION_MESSAGE);
                if (!ModelUtil.hasLength(taskTitle)) {
                    return;
                }
                Task task = new Task();
                task.setTitle(taskTitle);
                final JCheckBox box = new JCheckBox();
                box.setOpaque(false);
                box.setText(task.getTitle());
                mainPanel.add(box);
                map.put(box, task);
                mainPanel.invalidate();
                mainPanel.validate();
                mainPanel.repaint();

                box.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        mainPanel.remove(box);
                        map.remove(box);
                        mainPanel.invalidate();
                        mainPanel.validate();
                        mainPanel.repaint();
                    }
                });

            }
        });

        Tasks tasks = Tasks.getTaskList(SparkManager.getConnection());
        Iterator taskIter = tasks.getTasks().iterator();
        while (taskIter.hasNext()) {
            Task task = (Task)taskIter.next();
            if (task.isCompleted()) {
                continue;
            }
            final JCheckBox box = new JCheckBox();
            box.setOpaque(false);
            box.setText(task.getTitle());
            mainPanel.add(box);
            map.put(box, task);
            mainPanel.invalidate();
            mainPanel.validate();
            mainPanel.repaint();

            box.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mainPanel.remove(box);

                    Task task = (Task)map.get(box);
                    task.setCompleted(true);
                    mainPanel.invalidate();
                    mainPanel.validate();
                    mainPanel.repaint();
                }
            });
        }


        final JScrollPane pane = new JScrollPane(mainPanel);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pane, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(400, 400);

        final Action saveAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                // Save it.
                Tasks tasks = new Tasks();
                Iterator iter = map.values().iterator();
                while (iter.hasNext()) {
                    Task task = (Task)iter.next();
                    tasks.addTask(task);
                }

                Tasks.saveTasks(tasks, SparkManager.getConnection());
            }
        };

        addButton.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();

                    saveAction.actionPerformed(null);
                }
            }
        });


        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                saveAction.actionPerformed(null);
            }
        });


        GraphicUtils.centerWindowOnComponent(frame, SparkManager.getMainWindow());
        frame.setVisible(true);
    }

    /**
     * Retrieve private notes from server.
     */
    private void retrieveNotes() {
        // Retrieve private notes from server.
        final SwingWorker notesWorker = new SwingWorker() {
            public Object construct() {
                return PrivateNotes.getPrivateNotes();
            }

            public void finished() {
                final PrivateNotes privateNotes = (PrivateNotes)get();
                showPrivateNotes(privateNotes);
            }
        };

        notesWorker.start();
    }

    private void showPrivateNotes(final PrivateNotes privateNotes) {
        String text = privateNotes.getNotes();

        final JLabel titleLabel = new JLabel("Notepad");
        titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);


        final JTextPane pane = new JTextPane();
        pane.setFont(new Font("Dialog", Font.PLAIN, 12));

        pane.setOpaque(false);

        final JScrollPane scrollPane = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        pane.setText(text);
        final RolloverButton button = new RolloverButton("Save", null);
        final RolloverButton cancelButton = new RolloverButton("Cancel", null);
        ResourceUtils.resButton(button, "&Save");
        ResourceUtils.resButton(cancelButton, "&Cancel");

        final JFrame frame = new JFrame("Notes");


        final JPanel mainPanel = new JPanel();

        pane.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();

                    // Save it.
                    String text = pane.getText();
                    privateNotes.setNotes(text);
                    PrivateNotes.savePrivateNotes(privateNotes);
                }
            }
        });

        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new GridBagLayout());
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        frame.getContentPane().add(mainPanel);

        //   mainPanel.add(titleLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        mainPanel.add(scrollPane, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(button, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(cancelButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        frame.pack();
        frame.setSize(400, 400);


        GraphicUtils.centerWindowOnComponent(frame, SparkManager.getMainWindow());
        frame.setVisible(true);
        pane.setCaretPosition(0);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();

                // Save it.
                String text = pane.getText();
                privateNotes.setNotes(text);
                PrivateNotes.savePrivateNotes(privateNotes);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
            }
        });
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
    }

    private class DragWindowAdapter extends MouseAdapter
            implements MouseMotionListener {
        private JFrame m_msgWnd;
        private int m_mousePrevX,
                m_mousePrevY;
        private int m_frameX,
                m_frameY;

        public DragWindowAdapter(JFrame mw) {
            m_msgWnd = mw;
        }

        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            m_mousePrevX = e.getX();
            m_mousePrevY = e.getY();
            m_frameX = 0;
            m_frameY = 0;
        }

        public void mouseDragged(MouseEvent e) {
            int X = e.getX();
            int Y = e.getY();
            int MsgX = m_msgWnd.getX();
            int MsgY = m_msgWnd.getY();

            int moveX = X - m_mousePrevX;  // Negative if move left
            int moveY = Y - m_mousePrevY;  // Negative if move down
            if (moveX == 0 && moveY == 0) return;
            m_mousePrevX = X - moveX;
            m_mousePrevY = Y - moveY;

            //System.out.println("mouseDragged x,y = (" + X + "," + Y +
            //        ") diff (" + moveX + "," + moveY +
            //        ") MsgX/MsgY = " + MsgX + "," + MsgY);

            // mouseDragged caused by setLocation() on frame.
            if (m_frameX == MsgX && m_frameY == MsgY) {
                m_frameX = 0;
                m_frameY = 0;
                return;
            }

            // '-' would cause wrong direction for movement.
            int newFrameX = MsgX + moveX;
            // '-' would cause wrong
            int newFrameY = MsgY + moveY;

            m_frameX = newFrameX;
            m_frameY = newFrameY;
            m_msgWnd.setLocation(newFrameX, newFrameY);
        }

        public void mouseMoved(MouseEvent e) {
        }
    }
}
