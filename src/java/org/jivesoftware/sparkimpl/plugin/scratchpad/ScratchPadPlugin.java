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
package org.jivesoftware.sparkimpl.plugin.scratchpad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;

/**
 *
 */
public class ScratchPadPlugin implements Plugin {

    public static boolean SHOW_ALL_TASKS = true;

    private static final String dateShortFormat = ((SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)).toPattern();
    private SimpleDateFormat formatter = new SimpleDateFormat(dateShortFormat);
    private static List<TaskUI> taskList = new ArrayList<TaskUI>();
    private static JPanel panel_events;
    private static JPanel mainPanel;
    private static JFrame frame;

    public void initialize() {
   	 
    	TimerTask startTask = new TimerTask() {
			@Override
			public void run() {
				panel_events = new JPanel();
				mainPanel = new JPanel();
			}
		};
   	 
 		TaskEngine.getInstance().schedule(startTask, 500);
 		
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F6"), "viewNotes");

        contactList.getActionMap().put("viewNotes", new AbstractAction("viewNotes") {
	    private static final long serialVersionUID = -3258500919859584696L;

	    public void actionPerformed(ActionEvent evt) {
                // Retrieve notes and dispaly in editor.
                retrieveNotes();
            }
        });

        contactList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F5"), "viewTasks");

        contactList.getActionMap().put("viewTasks", new AbstractAction("viewTasks") {
	    private static final long serialVersionUID = 8589614513097901484L;

	    public void actionPerformed(ActionEvent evt) {
                // Retrieve notes and dispaly in editor.
                showTaskList();
            }
        });

        //TODO REMOVE
        @SuppressWarnings("unused")
        int index = -1;
        JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();
        for (int i = 0; i < commandPanel.getComponentCount(); i++) {
            if (commandPanel.getComponent(i) instanceof JLabel) {
                index = i;
                break;
            }
        }

        
        JMenuItem taskMenu = new JMenuItem(Res.getString("button.view.tasklist"),
        	SparkRes.getImageIcon(SparkRes.DESKTOP_IMAGE));
        
        JMenuItem notesMenu = new JMenuItem(Res.getString("button.view.notes"),
        	SparkRes.getImageIcon(SparkRes.DOCUMENT_16x16));

        taskMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTaskList();
            }
        });

        notesMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retrieveNotes();
            }
        });

        // Add To toolbar
      final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
	      actionsMenu.addSeparator();
	      actionsMenu.add(taskMenu);
	      actionsMenu.add(notesMenu);


        // Start notifications.
        new TaskNotification();

    }

    private void showTaskList() {
        frame = new JFrame(Res.getString("title.tasks"));
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        panel_events.removeAll();
        mainPanel.removeAll();

        //final List<TaskUI> taskList = new ArrayList<TaskUI>();
        //final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        mainPanel.setBackground(Color.white);

        final JPanel topPanel = new JPanel(new GridBagLayout());
        final JTextField taskField = new JTextField();
        final JTextField dueDateField = new JTextField();

        final JButton addButton = new JButton(Res.getString("add"));
        final JLabel addTaskLabel = new JLabel(Res.getString("label.add.task"));
        topPanel.setOpaque(false);

        topPanel.add(addTaskLabel, new GridBagConstraints(0, 0, 1, 1, .9, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        topPanel.add(taskField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));

        topPanel.add(dueDateField, new GridBagConstraints(1, 1, 1, 1, 0.1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 50, 0));
        topPanel.add(addButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 0));

        topPanel.add(new JLabel(Res.getString("label.timeformat", formatter.toPattern())), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        mainPanel.add(topPanel);

        // Add Selection
        final JPanel middlePanel = new JPanel(new GridBagLayout());
        final JLabel showLabel = new JLabel(Res.getString("label.show"));
        final JToggleButton allButton = new JToggleButton(Res.getString("button.tasks.all"));
        final JToggleButton activeButton = new JToggleButton(Res.getString("button.tasks.active"));
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(allButton);
        buttonGroup.add(activeButton);
        middlePanel.setOpaque(false);

        middlePanel.add(showLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        middlePanel.add(allButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        middlePanel.add(activeButton, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        mainPanel.add(middlePanel);
        mainPanel.setBackground(Color.white);

        final JPanel titlePanel = new JPanel(new BorderLayout()) {
	    private static final long serialVersionUID = -8812868562658925280L;

	    public void paintComponent(Graphics g) {
                Color startColor = Color.white;
                Color endColor = new Color(198, 211, 247);

                Graphics2D g2 = (Graphics2D)g;

                int w = getWidth();
                int h = getHeight();

                GradientPaint gradient = new GradientPaint(0, 0, startColor, w, h, endColor, true);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, w, h);
            }

        };
        final JLabel taskLabel = new JLabel(Res.getString("label.due") + "        ");
        taskLabel.setFont(taskLabel.getFont().deriveFont(Font.BOLD));
        titlePanel.add(taskLabel, BorderLayout.EAST);
        mainPanel.add(titlePanel);

        Action showAllAction = new AbstractAction() {
	    private static final long serialVersionUID = -7031122285194582204L;

	    public void actionPerformed(ActionEvent e) {
                for (TaskUI ui : taskList) {
                    ui.setVisible(true);
                }

                SHOW_ALL_TASKS = true;
            }
        };

        Action showActiveAction = new AbstractAction() {
	    private static final long serialVersionUID = -7551153291479117311L;

	    public void actionPerformed(ActionEvent e) {
                for (TaskUI ui : taskList) {
                    if (ui.isSelected()) {
                        ui.setVisible(false);
                    }
                }

                SHOW_ALL_TASKS = false;
            }
        };

        final Action addAction = new AbstractAction() {
	    private static final long serialVersionUID = -5937301529216080813L;

	    public void actionPerformed(ActionEvent e) {
                String taskTitle = taskField.getText();
                if (!ModelUtil.hasLength(taskTitle)) {
                    return;
                }
                Task task = new Task();
                task.setTitle(taskTitle);

                // Set creation time.
                final Date creationDate = new Date();
                task.setCreatedDate(creationDate.getTime());

                // Set due date.
                String dueDate = dueDateField.getText();
                if (ModelUtil.hasLength(dueDate)) {

                    try {
                        Date date = formatter.parse(dueDate);
                        task.setDueDate(date.getTime());
                    }
                    catch (ParseException e1) {
                        // Nothing to do
                    }

                }

                taskField.setText("");

                final TaskUI taskUI = new TaskUI(task);
                //mainPanel.add(taskUI);
                panel_events.add(taskUI);
                taskList.add(taskUI);
                
                panel_events.invalidate();
                panel_events.validate();
                panel_events.repaint();                
                mainPanel.invalidate();
                mainPanel.validate();
                mainPanel.repaint();
                frame.invalidate();
                frame.validate();
                frame.repaint();                
            }
        };
        
        mainPanel.add(panel_events);
        panel_events.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        panel_events.setBackground(Color.white);
        
        allButton.addActionListener(showAllAction);
        activeButton.addActionListener(showActiveAction);

        GraphicUtils.makeSameSize(allButton, activeButton);

        addButton.addActionListener(addAction);

        Tasks tasks = Tasks.getTaskList(SparkManager.getConnection());
        updateTaskUI(tasks);

        if (SHOW_ALL_TASKS) {
            allButton.setSelected(true);
        }
        else {
            activeButton.setSelected(true);
            showActiveAction.actionPerformed(null);
        }

        long tomorrow = DateUtils.addDays(new Date().getTime(), 1);
        SimpleDateFormat formatter = new SimpleDateFormat(dateShortFormat);
        dueDateField.setText(formatter.format(new Date(tomorrow)));


        final JScrollPane pane = new JScrollPane(mainPanel);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pane, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(400, 400);

        final Action saveAction = new AbstractAction() {
	    private static final long serialVersionUID = -4287799161421970177L;

	    public void actionPerformed(ActionEvent actionEvent) {
                // Save it.
                Tasks tasks = new Tasks();
                for (TaskUI ui : taskList) {
                    Task task = ui.getTask();
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

        taskField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    addAction.actionPerformed(null);
                }
            }
        });


        GraphicUtils.centerWindowOnComponent(frame, SparkManager.getMainWindow());
        frame.setVisible(true);
    }
    
    /**
     * Updates the GUI of Tasks
     * 
     * @param tasks Tasks
     */
    public static void updateTaskUI(Tasks tasks) {
    	panel_events.removeAll();
    	taskList.clear();
    	
        for (Object o : tasks.getTasks()) {
            Task task = (Task) o;
            final TaskUI taskUI = new TaskUI(task);
            
            if ( SHOW_ALL_TASKS == false ) {
            	if ( taskUI.isSelected() ) {
            		taskUI.setVisible(false);
            	}else {
            		taskUI.setVisible(true);
            	}
            }
            
            panel_events.add(taskUI);
            taskList.add(taskUI);
        }
        
        panel_events.invalidate();
        panel_events.validate();
        panel_events.repaint();
        mainPanel.invalidate();
        mainPanel.validate();
        mainPanel.repaint();
        frame.invalidate();
        frame.validate();
        frame.repaint();
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
        final RolloverButton button = new RolloverButton(Res.getString("save"), null);
        final RolloverButton cancelButton = new RolloverButton(Res.getString("cancel"), null);
        ResourceUtils.resButton(button, Res.getString("button.save"));
        ResourceUtils.resButton(cancelButton, Res.getString("button.cancel"));

        final JFrame frame = new JFrame(Res.getString("title.notes"));


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
    
    public static List<TaskUI> getTaskList() {
        return taskList;
    }

//    private class DragWindowAdapter extends MouseAdapter
//            implements MouseMotionListener {
//        private JFrame m_msgWnd;
//        private int m_mousePrevX,
//                m_mousePrevY;
//        private int m_frameX,
//                m_frameY;
//
//        public DragWindowAdapter(JFrame mw) {
//            m_msgWnd = mw;
//        }
//
//        public void mousePressed(MouseEvent e) {
//            super.mousePressed(e);
//            m_mousePrevX = e.getX();
//            m_mousePrevY = e.getY();
//            m_frameX = 0;
//            m_frameY = 0;
//        }
//
//        public void mouseDragged(MouseEvent e) {
//            int X = e.getX();
//            int Y = e.getY();
//            int MsgX = m_msgWnd.getX();
//            int MsgY = m_msgWnd.getY();
//
//            int moveX = X - m_mousePrevX;  // Negative if move left
//            int moveY = Y - m_mousePrevY;  // Negative if move down
//            if (moveX == 0 && moveY == 0) return;
//            m_mousePrevX = X - moveX;
//            m_mousePrevY = Y - moveY;
//
//            //System.out.println("mouseDragged x,y = (" + X + "," + Y +
//            //        ") diff (" + moveX + "," + moveY +
//            //        ") MsgX/MsgY = " + MsgX + "," + MsgY);
//
//            // mouseDragged caused by setLocation() on frame.
//            if (m_frameX == MsgX && m_frameY == MsgY) {
//                m_frameX = 0;
//                m_frameY = 0;
//                return;
//            }
//
//            // '-' would cause wrong direction for movement.
//            int newFrameX = MsgX + moveX;
//            // '-' would cause wrong
//            int newFrameY = MsgY + moveY;
//
//            m_frameX = newFrameX;
//            m_frameY = newFrameY;
//            m_msgWnd.setLocation(newFrameX, newFrameY);
//        }
//
//        public void mouseMoved(MouseEvent e) {
//        }
//    }
}
