/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
public class CollapsiblePane extends JPanel {

    private CollapsibleTitlePane titlePane;
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
        final Iterator iter = ModelUtil.reverseListIterator(listeners.listIterator());
        while (iter.hasNext()) {
            ((CollapsiblePaneListener)iter.next()).paneExpanded();
        }
    }

    private void firePaneCollapsed() {
        final Iterator iter = ModelUtil.reverseListIterator(listeners.listIterator());
        while (iter.hasNext()) {
            ((CollapsiblePaneListener)iter.next()).paneCollapsed();
        }
    }


    public CollapsibleTitlePane getTitlePane() {
        return titlePane;
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
