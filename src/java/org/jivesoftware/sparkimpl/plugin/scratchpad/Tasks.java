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

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.PrivateData;
import org.jivesoftware.smackx.provider.PrivateDataProvider;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Derek DeMoro
 */
public class Tasks implements PrivateData {

    private List tasks = new ArrayList();

    /**
     * Required Empty Constructor to use Tasks.
     */
    public Tasks() {
    }


    public List getTasks() {
        return tasks;
    }

    public void setTasks(List tasks) {
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
        buf.append("<tasks>");

        Iterator iter = getTasks().iterator();
        while (iter.hasNext()) {
            Task task = (Task)iter.next();
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
                task.setDueDate(Integer.parseInt(dueDate));
            }

            if (eventType == XmlPullParser.START_TAG && "creationDate".equals(parser.getName())) {
                String creationDate = parser.nextText();
                task.setCreatedDate(Integer.parseInt(creationDate));
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
}
