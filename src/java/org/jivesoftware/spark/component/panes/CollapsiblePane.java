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
package org.jivesoftware.spark.component.panes;

import org.jivesoftware.spark.util.ModelUtil;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * CollapsiblePane provides a component which can collapse or expand its content area
 * with animation and fade in/fade out effects. It also acts as a standard container for
 * other Swing components.
 *
 * @author Derek DeMoro
 */
/**
 *
 */
public class CollapsiblePane extends JPanel {

	private static final long serialVersionUID = -6770924580102536726L;
	private BaseCollapsibleTitlePane titlePane;
    private JPanel mainPanel;

    private List<CollapsiblePaneListener> listeners = new ArrayList<CollapsiblePaneListener>();

    private boolean subPane;

    public CollapsiblePane() {
        setLayout(new BorderLayout());

        titlePane = new CollapsibleTitlePane();
        mainPanel = new JPanel();


        add(titlePane, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.setLayout(new BorderLayout());

        titlePane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    return;
                }
                boolean isCollapsed = titlePane.isCollapsed();
                setCollapsed(!isCollapsed);
            }
        });
    }

    /**
     * Creates a CollapsiblePane.
     * @param title the title to use.
     */
    public CollapsiblePane(String title) {
        this();
        setTitle(title);
    }

    /**
     * Set the title of the Collapsible Pane.
     * @param title the collapsible pane title.
     */
    public void setTitle(String title) {
        titlePane.setTitle(title);
    }

    public void setIcon(Icon icon) {
        titlePane.setIcon(icon);
    }

    public void setCollapsed(boolean collapsed) {
        titlePane.setCollapsed(collapsed);
        mainPanel.setVisible(!collapsed);

        if (collapsed) {
            firePaneCollapsed();
        }
        else {
            firePaneExpanded();
        }
    }

    public void setContentPane(Component comp) {
        mainPanel.add(comp);
    }

    public void addCollapsiblePaneListener(CollapsiblePaneListener listener) {
        listeners.add(listener);
    }

    public void removeCollapsiblePaneListener(CollapsiblePaneListener listener) {
        listeners.remove(listener);
    }

    private void firePaneExpanded() {
        final Iterator<CollapsiblePaneListener> iter = ModelUtil.reverseListIterator(listeners.listIterator());
        while (iter.hasNext()) {
            (iter.next()).paneExpanded();
        }
    }

    private void firePaneCollapsed() {
        final Iterator<CollapsiblePaneListener> iter = ModelUtil.reverseListIterator(listeners.listIterator());
        while (iter.hasNext()) {
            (iter.next()).paneCollapsed();
        }
    }


    public BaseCollapsibleTitlePane getTitlePane() {
        return titlePane;
    }

    protected void setTitlePane(BaseCollapsibleTitlePane titlePane) {
        this.titlePane = titlePane;
    }

    public boolean isCollapsed() {
        return titlePane.isCollapsed();
    }

    public boolean isSubPane() {
        return subPane;
    }

    public void setSubPane(boolean subPane) {
        this.subPane = subPane;

        titlePane.setSubPane(subPane);
    }
}
