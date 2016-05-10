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
package org.jivesoftware.fastpath.workspace.panes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.smackx.workgroup.agent.QueueUsersListener;
import org.jivesoftware.smackx.workgroup.agent.WorkgroupQueue;


/**
 * UI for displaying all queue information pertaining to a single workgroup.
 */
public final class QueueActivity extends JPanel implements QueueUsersListener {

	private static final long serialVersionUID = 1L;
	private DefaultListModel model = new DefaultListModel();
    private JList list;

    private Map<WorkgroupQueue, QueueItem> queues = new HashMap<WorkgroupQueue, QueueItem>();

    /**
     * Add Listeners and build UI.
     */
    public QueueActivity() {
        init();
        FastpathPlugin.getAgentSession().addQueueUsersListener(this);
    }

    public void removeListener() {
        FastpathPlugin.getAgentSession().removeQueueUsersListener(this);
    }

    private void init() {
        list = new JList(model);
        list.setCellRenderer(new FastpathPanelRenderer());

        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setForeground(Color.white);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);
    }


    public void usersUpdated(WorkgroupQueue queue, Set users) {
        QueueItem item = queues.get(queue);
        if (item == null) {
            statusUpdated(queue, queue.getStatus());
        }
        else {
            item.setNumberOfUsersInQueue(users.size());
        }

        update(item);
    }

    private void update(QueueItem item) {
        list.invalidate();
        list.validate();
        list.repaint();
    }

    public void oldestEntryUpdated(WorkgroupQueue queue, Date date) {
        QueueItem item = queues.get(queue);
        if (item != null) {
            item.setOldestEntryDate(date);
        }
        update(item);
    }

    public void averageWaitTimeUpdated(WorkgroupQueue queue, int waitTime) {
        QueueItem item = queues.get(queue);
        if (item == null) {
            return;
        }
        item.setAverageWaitTime(waitTime);
        update(item);
    }

    public void statusUpdated(final WorkgroupQueue queue, WorkgroupQueue.Status status) {
        
        EventQueue.invokeLater( () -> {
            String oldestEntry = queue.getOldestEntry() != null ? queue.getOldestEntry().toString() : "";

            QueueItem item = queues.get(queue);
            if (item != null) {
                item.setNumberOfUsersInQueue(queue.getUserCount());
                item.setAverageWaitTime(queue.getAverageWaitTime());
                update(item);
                return;
            }
            item = new QueueItem(queue.getName(), queue.getUserCount(), queue.getAverageWaitTime(), oldestEntry);
            queues.put(queue, item);
            model.addElement(item);
        } );
    }
}