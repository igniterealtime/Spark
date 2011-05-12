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
package org.jivesoftware.sparkimpl.plugin.bookmarks;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smackx.bookmark.BookmarkedConference;
import org.jivesoftware.smackx.bookmark.BookmarkedURL;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.GraphicUtils;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 */
public class BookmarkUI extends JPanel {
	private static final long serialVersionUID = 2724141541874364121L;
	private DefaultListModel model;
    private JList list;


    public BookmarkUI() {
        setLayout(new BorderLayout());
        CollapsiblePane pane = new CollapsiblePane();
        pane.setTitle(Res.getString("title.bookmarks"));

        model = new DefaultListModel();
        list = new JList(model);

        add(pane, BorderLayout.CENTER);
        pane.setContentPane(list);
        list.setCellRenderer(new JPanelRenderer());

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    BookmarkItem item = (BookmarkItem)list.getSelectedValue();
                    item.invokeAction();
                }
            }
        });


        pane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                list.setCursor(GraphicUtils.HAND_CURSOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                list.setCursor(GraphicUtils.DEFAULT_CURSOR);
            }
        });
    }

    public void addURL(BookmarkedURL url) {
        BookmarkItem item = new BookmarkItem();
        item.addURL(url);
        model.addElement(item);
    }

    public void addConference(BookmarkedConference conference) {
        BookmarkItem item = new BookmarkItem();
        item.addConferenceRoom(conference);

        model.addElement(item);
        if (conference.isAutoJoin()) {
            item.invokeAction();
        }
    }


}
