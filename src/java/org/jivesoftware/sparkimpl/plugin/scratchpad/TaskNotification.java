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

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;

/**
 *
 */
public class TaskNotification {
    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");


    public TaskNotification() {
        TimerTask task = new TimerTask() {
            public void run() {
                notifyUser();
            }
        };

        long twoHours = (60 * 1000) * 120;
        TaskEngine.getInstance().scheduleAtFixedRate(task, (10 * 1000), twoHours);
    }

    private void notifyUser() {
    	
    	TimerTask newTask = new TimerTask() {
			
			@Override
			public void run() {
 	 		     final JPanel mainPanel = new JPanel();
 	 	        mainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
 	 	        mainPanel.setBackground(Color.white);

 	 	        long now = System.currentTimeMillis();
 	 	        Tasks tasks = Tasks.getTaskList(SparkManager.getConnection());
 	 	        Iterator<Task> taskIter = tasks.getTasks().iterator();


 	 	        final JPanel titlePanel = new JPanel(new BorderLayout()) {
 	 		    private static final long serialVersionUID = -8871487137643685431L;

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
 	 	        final JLabel taskLabel = new JLabel("Due   ");
 	 	        taskLabel.setFont(taskLabel.getFont().deriveFont(Font.BOLD));
 	 	        titlePanel.add(taskLabel, BorderLayout.EAST);
 	 	        mainPanel.add(titlePanel);

 	 	        boolean hasItems = false;
 	 	        while (taskIter.hasNext()) {
 	 	            Task task = (Task)taskIter.next();
 	 	            if (task.isCompleted()) {
 	 	                continue;
 	 	            }

 	 	            long dueDate = task.getDueDate();
 	 	            if (dueDate != -1) {
 	 	                if (now > dueDate) {
 	 	                    final JPanel item = new JPanel(new BorderLayout());
 	 	                    item.setOpaque(false);
 	 	                    JLabel label = new JLabel(task.getTitle());
 	 	                    item.add(label, BorderLayout.CENTER);

 	 	                    JLabel dueItem = new JLabel(formatter.format(new Date(task.getDueDate())));
 	 	                    item.add(dueItem, BorderLayout.EAST);
 	 	                    mainPanel.add(item);
 	 	                    hasItems = true;
 	 	                }
 	 	            }
 	 	        }

 	 	        if (hasItems) {
 	 	            SparkToaster toaster = new SparkToaster();
 	 	            toaster.setDisplayTime(30000);
 	 	            toaster.setToasterHeight(175);
 	 	            toaster.setToasterWidth(300);

 	 	            toaster.setBorder(BorderFactory.createBevelBorder(0));
 	 	            JScrollPane pane = new JScrollPane(mainPanel);
 	 	            pane.getViewport().setBackground(Color.white);
 	 	            toaster.showToaster(Res.getString("title.task.notification"), pane);
 	 	        }			
			}
		};
    	TaskEngine.getInstance().schedule(newTask, 500);
    	
    }
}
