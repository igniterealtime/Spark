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

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.PrivateData;
import org.jivesoftware.smackx.provider.PrivateDataProvider;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author Derek DeMoro
 */
public class Tasks implements PrivateData {

    private List<Task> tasks = new ArrayList<Task>();

    /**
     * Required Empty Constructor to use Tasks.
     */
    public Tasks() {
    }


    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the root element name.
     *
     * @return the element name.
     */
    public String getElementName() {
        return "scratchpad";
    }
    
    /**
     * Returns the root element XML namespace.
     *
     * @return the namespace.
     */
    public String getNamespace() {
        return "scratchpad:tasks";
    }

    /**
     * Returns the XML reppresentation of the PrivateData.
     *
     * @return the private data as XML.
     */
    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<scratchpad xmlns=\"scratchpad:tasks\">");
        buf.append("<tasks showAll=\"").append(ScratchPadPlugin.SHOW_ALL_TASKS).append("\">");

        for (Task task : getTasks()) {
            buf.append("<task>");
            buf.append("<title>").append(task.getTitle()).append("</title>");
            buf.append("<dueDate>").append(task.getDueDate()).append("</dueDate>");
            buf.append("<creationDate>").append(task.getCreatedDate()).append("</creationDate>");
            if (task.isCompleted()) {
                buf.append("<completed>true</completed>");
            }
            buf.append("</task>");
        }

        buf.append("</tasks>");

        buf.append("</scratchpad>");
        return buf.toString();
    }

    /**
     * The IQ Provider for Tasks
     *
     * @author Derek DeMoro
     */
    public static class Provider implements PrivateDataProvider {

        Tasks tasks = new Tasks();

        /**
         * Empty Constructor for PrivateDataProvider.
         */
        public Provider() {
            super();
        }

        public PrivateData parsePrivateData(XmlPullParser parser) throws Exception {
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG && "tasks".equals(parser.getName())) {
                    String showAll = parser.getAttributeValue("", "showAll");
                    ScratchPadPlugin.SHOW_ALL_TASKS = Boolean.parseBoolean(showAll);
                }

                if (eventType == XmlPullParser.START_TAG && "task".equals(parser.getName())) {
                    tasks.addTask(getTask(parser));
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if ("scratchpad".equals(parser.getName())) {
                        done = true;
                    }
                }
            }


            return tasks;
        }
    }

    public static Task getTask(XmlPullParser parser) throws Exception {
        final Task task = new Task();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && "title".equals(parser.getName())) {
                task.setTitle(parser.nextText());
            }

            if (eventType == XmlPullParser.START_TAG && "dueDate".equals(parser.getName())) {
                String dueDate = parser.nextText();
                task.setDueDate(Long.parseLong(dueDate));
            }

            if (eventType == XmlPullParser.START_TAG && "creationDate".equals(parser.getName())) {
                String creationDate = parser.nextText();
                task.setCreatedDate(Long.parseLong(creationDate));
            }

            if (eventType == XmlPullParser.START_TAG && "completed".equals(parser.getName())) {
                String completed = parser.nextText();
                if (ModelUtil.hasLength(completed)) {
                    task.setCompleted(Boolean.parseBoolean(completed));
                }
            }

            else if (eventType == XmlPullParser.END_TAG) {
                if ("task".equals(parser.getName())) {
                    done = true;
                }
            }
        }


        return task;
    }


    public static void saveTasks(Tasks tasks, XMPPConnection con) {
        PrivateDataManager manager = new PrivateDataManager(con);

        PrivateDataManager.addPrivateDataProvider("scratchpad", "scratchpad:tasks", new Tasks.Provider());
        try {
            manager.setPrivateData(tasks);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
    }

    public static Tasks getTaskList(XMPPConnection con) {
        PrivateDataManager manager = new PrivateDataManager(con);

        PrivateDataManager.addPrivateDataProvider("scratchpad", "scratchpad:tasks", new Tasks.Provider());


        Tasks tasks = null;

        try {
            tasks = (Tasks)manager.getPrivateData("scratchpad", "scratchpad:tasks");
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        return tasks;
    }
    
    /**
     * Delete task
     * 
     * @param task : task to delete
     */
    public static void deleteTask(Task task) {
    	
    	List<TaskUI> taskList = (List<TaskUI>) ScratchPadPlugin.getTaskList();
    	
    	// find and delete task in list
    	for ( int i = 0; i < taskList.size(); i++ ) {
            Task t = taskList.get(i).getTask();
            if ( t == task ) {
            	taskList.remove(i);
            	break;
            }
        }
    	
    	// save Tasks
    	Tasks tasks = new Tasks();
        for (TaskUI ui : taskList) {
            Task nTask = ui.getTask();            
            tasks.addTask(nTask);
        }
        
        // update GUI
        ScratchPadPlugin.updateTaskUI(tasks);
    }
}
