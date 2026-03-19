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

import org.jivesoftware.smack.util.XmlStringBuilder;
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
 * Tasks for a TO-DO list
 * @author Derek DeMoro
 */
public class Tasks implements PrivateData {
    public static final String ELEMENT = "scratchpad";
    public static final String NAMESPACE = "scratchpad:tasks";
    private List<Task> tasks = new ArrayList<>();
    private boolean showAll;

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder buf = new XmlStringBuilder();
        buf.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE).rightAngleBracket();
        buf.halfOpenElement("tasks");
        buf.attribute("showAll", isShowAll());
        buf.rightAngleBracket();
        for (Task task : getTasks()) {
            buf.openElement("task");
            buf.optElement("title", task.getTitle());
            buf.optElement("dueDate", task.getDueDate());
            buf.optElement("creationDate", task.getCreatedDate());
            if (task.isCompleted()) {
                buf.optElement("completed", "true");
            }
            buf.closeElement("task");
        }
        buf.closeElement("tasks");
        buf.closeElement(ELEMENT);
        return buf;
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
                    tasks.setShowAll(Boolean.parseBoolean(showAll));
                }

                if (eventType == XmlPullParser.Event.START_ELEMENT && "task".equals(parser.getName())) {
                    tasks.addTask(getTask(parser));
                } else if (eventType == XmlPullParser.Event.END_ELEMENT && ELEMENT.equals(parser.getName())) {
                    done = true;
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
            } else if (eventType == XmlPullParser.Event.END_ELEMENT && "task".equals(parser.getName())) {
                done = true;
            }
        }

        return task;
    }


    static {
        PrivateDataManager.addPrivateDataProvider(ELEMENT, NAMESPACE, new Tasks.Provider());
    }

    public static void saveTasks(Tasks tasks) {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();
        try {
            manager.setPrivateData(tasks);
        } catch (Exception e) {
            Log.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Tasks getTaskList() {
        PrivateDataManager manager = SparkManager.getSessionManager().getPersonalDataManager();
        try {
            PrivateData privateData = manager.getPrivateData(ELEMENT, NAMESPACE);
            return privateData != null ? (Tasks) privateData : new Tasks();
        } catch (Exception e) {
            Log.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
