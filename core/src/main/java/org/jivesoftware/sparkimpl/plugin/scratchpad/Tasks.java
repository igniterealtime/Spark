/**
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.JxmppContext;

/**
 * @author Derek DeMoro
 */
public class Tasks implements PrivateData {

    private List<Task> tasks = new ArrayList<>();

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
     */
    @Override
    public String getElementName() {
        return "scratchpad";
    }

    /**
     * Returns the root element XML namespace.
     */
    @Override
    public String getNamespace() {
        return "scratchpad:tasks";
    }

    /**
     * Returns the XML representation of the PrivateData.
     */
    @Override
    public String toXML() {
        StringBuilder buf = new StringBuilder();
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
     */
    public static class Provider implements PrivateDataProvider {
        private final Tasks tasks = new Tasks();

        public Provider() {
            super();
        }

        @Override
        public PrivateData parsePrivateData(XmlPullParser parser, JxmppContext jxmppContext) throws XmlPullParserException, IOException {
            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "tasks".equals(parser.getName())) {
                    String showAll = parser.getAttributeValue("", "showAll");
                    ScratchPadPlugin.SHOW_ALL_TASKS = Boolean.parseBoolean(showAll);
                }

                if (eventType == XmlPullParser.Event.START_ELEMENT && "task".equals(parser.getName())) {
                    tasks.addTask(getTask(parser));
                } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if ("scratchpad".equals(parser.getName())) {
                        done = true;
                    }
                }
            }

            return tasks;
        }
    }

    public static Task getTask(XmlPullParser parser) throws XmlPullParserException, IOException {
        final Task task = new Task();

        boolean done = false;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT && "title".equals(parser.getName())) {
                task.setTitle(parser.nextText());
            }

            if (eventType == XmlPullParser.Event.START_ELEMENT && "dueDate".equals(parser.getName())) {
                String dueDate = parser.nextText();
                task.setDueDate(Long.parseLong(dueDate));
            }

            if (eventType == XmlPullParser.Event.START_ELEMENT && "creationDate".equals(parser.getName())) {
                String creationDate = parser.nextText();
                task.setCreatedDate(Long.parseLong(creationDate));
            }

            if (eventType == XmlPullParser.Event.START_ELEMENT && "completed".equals(parser.getName())) {
                String completed = parser.nextText();
                if (ModelUtil.hasLength(completed)) {
                    task.setCompleted(Boolean.parseBoolean(completed));
                }
            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if ("task".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        return task;
    }


    public static void saveTasks(Tasks tasks, XMPPConnection con) {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();

        PrivateDataManager.addPrivateDataProvider("scratchpad", "scratchpad:tasks", new Tasks.Provider());
        try {
            manager.setPrivateData(tasks);
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }
    }

    static {
        PrivateDataManager.addPrivateDataProvider("scratchpad", "scratchpad:tasks", new Tasks.Provider());
    }

    public static Tasks getTaskList() {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();

        Tasks tasks = null;
        try {
            tasks = (Tasks) manager.getPrivateData("scratchpad", "scratchpad:tasks");
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
        }

        return tasks;
    }

    /**
     * Delete task
     */
    public static void deleteTask(Task task) {
        List<TaskUI> taskList = ScratchPadPlugin.getTaskList();
        // find and delete task in list
        for (int i = 0; i < taskList.size(); i++) {
            Task t = taskList.get(i).getTask();
            if (t == task) {
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
