/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.tabbedPane;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.ModelUtil;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SparkTabbedPane extends JPanel implements MouseListener {
    private JPanel tabs;
    private JPanel mainPanel;

    private Window parentWindow;

    private boolean closeButtonEnabled;
    private Icon closeInactiveButtonIcon;
    private Icon closeActiveButtonIcon;

    private boolean popupAllowed;

    private boolean activeButtonBold;

    private Map<Component, JFrame> framesMap = new HashMap<Component, JFrame>();


    private int tabPlacement = JTabbedPane.TOP;

    /**
     * Listeners
     */
    private List<SparkTabbedPaneListener> listeners = new ArrayList<SparkTabbedPaneListener>();

    public SparkTabbedPane() {
        createUI();
    }

    public SparkTabbedPane(int placement) {
        this.tabPlacement = placement;

        createUI();
    }

    private void createUI() {
        setLayout(new BorderLayout());

        tabs = new JPanel(new
                FlowLayout(FlowLayout.LEFT, 0, 0)) {
            public Dimension getPreferredSize() {
                if (getParent() == null)
                    return getPreferredSize();
                // calculate the preferred size based on the flow of components
                FlowLayout flow = (FlowLayout)getLayout();
                int w = getParent().getWidth();
                int h = flow.getVgap();
                int x = flow.getHgap();
                int rowH = 0;
                Dimension d;
                Component[] comps = getComponents();
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i].isVisible()) {
                        d = comps[i].getPreferredSize();
                        if (x + d.width > w && x > flow.getHgap()) {
                            x = flow.getHgap();
                            h += rowH;
                            rowH = 0;
                            h += flow.getVgap();
                        }
                        rowH = Math.max(d.height, rowH);
                        x += d.width + flow.getHgap();
                    }
                }
                h += rowH;
                return new Dimension(w, h);
            }
        };


        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.add(new JLabel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.setOpaque(false);

        // Add Tabs panel to top of panel.
        if (tabPlacement == JTabbedPane.TOP) {
            topPanel.add(tabs, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
            add(topPanel, BorderLayout.NORTH);
        }
        else {
            topPanel.add(tabs, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));
            add(topPanel, BorderLayout.SOUTH);
        }

        // Create mainPanel
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel, BorderLayout.CENTER);

        //  mainPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));

        // Initialize close button
        closeInactiveButtonIcon = SparkRes.getImageIcon(SparkRes.CLOSE_WHITE_X_IMAGE);
        closeActiveButtonIcon = SparkRes.getImageIcon(SparkRes.CLOSE_DARK_X_IMAGE);

        setOpaque(false);
        tabs.setOpaque(false);
    }

    public SparkTab addTab(String text, Icon icon, final Component component, String tooltip) {
        SparkTab tab = addTab(text, icon, component);
        tab.setToolTipText(tooltip);
        return tab;
    }


    public SparkTab addTab(String text, Icon icon, final Component component) {
        final SparkTab tab = new SparkTab(icon, text);
        tab.setTabPlacement(tabPlacement);
        //tabs.add(tab, new GridBagConstraints(tabs.getComponentCount(), 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));

        tabs.add(tab);
        // Add Component to main panel
        mainPanel.add(tab.getActualText(), component);
        tab.addMouseListener(this);

        // Add Close Button
        if (isCloseButtonEnabled()) {
            final RolloverButton closeButton = new RolloverButton(closeInactiveButtonIcon);
            tab.addComponent(closeButton);
            closeButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent mouseEvent) {
                    closeButton.setIcon(closeActiveButtonIcon);
                }

                public void mouseExited(MouseEvent mouseEvent) {
                    closeButton.setIcon(closeInactiveButtonIcon);
                }
            });
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    close(tab, component);
                }

            });
        }

        /*
        if (isPopupAllowed()) {
            RolloverButton popButton = new RolloverButton(LaRes.getImageIcon(LaRes.SMALL_PIN_BLUE));
            tab.addPop(popButton);

            popButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    int index = getIndex(tab);

                    // Close Tab

                    String title = tab.getActualText();

                    // Create Frame
                    final JFrame frame = new JFrame();
                    frame.setTitle(title);

                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(component, BorderLayout.CENTER);
                    frame.pack();

                    GraphicUtils.centerWindowOnScreen(frame);
                    frame.setVisible(true);

                    mainPanel.remove(component);
                    tabs.remove(tab);


                    tabs.invalidate();
                    tabs.validate();
                    tabs.repaint();

                    mainPanel.invalidate();
                    mainPanel.validate();
                    mainPanel.repaint();

                    fireTabRemoved(tab, component, index);
                    Component[] comps = tabs.getComponents();
                    if (comps.length == 0) {
                        allTabsClosed();
                    }
                    else {
                        findSelectedTab(index);
                    }

                }
            });
        }
        */

        if (getSelectedIndex() == -1) {
            setSelectedTab(tab);
        }


        fireTabAdded(tab, component, getIndex(tab));
        return tab;
    }

    public int getSelectedIndex() {
        Component[] comps = tabs.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            SparkTab tab = (SparkTab)c;
            if (tab.isSelected()) {
                return i;
            }
        }

        return -1;
    }

    public void setSelectedIndex(int index) {
        Component[] comps = tabs.getComponents();
        if (index <= comps.length) {
            SparkTab tab = (SparkTab)comps[index];
            setSelectedTab(tab);
        }
    }

    public int getTabCount() {
        return tabs.getComponents().length;
    }

    public int indexOfComponent(Component comp) {
        Component[] comps = mainPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c == comp) {
                return i;
            }
        }

        return -1;
    }

    public Component getComponentAt(int index) {
        Component[] comps = mainPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (i == index) {
                return c;
            }
        }

        return null;
    }

    public void removeTabAt(int index) {
        SparkTab tab = getTabAt(index);
        Component comp = getComponentAt(index);
        close(tab, comp);
    }


    public SparkTab getTabAt(int index) {
        Component[] comps = tabs.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (i == index) {
                return (SparkTab)c;
            }
        }

        return null;
    }

    public void removeComponent(Component comp) {
        Component[] comps = mainPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c == comp) {

            }
        }
    }

    public int getIndex(SparkTab tab) {
        Component[] comps = tabs.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c instanceof SparkTab && c == tab) {
                return i;
            }
        }

        return -1;
    }

    public void close(SparkTab tab, Component comp) {
        int index = getIndex(tab);

        // Close Tab
        mainPanel.remove(comp);
        tabs.remove(tab);


        tabs.invalidate();
        tabs.validate();
        tabs.repaint();

        mainPanel.invalidate();
        mainPanel.validate();
        mainPanel.repaint();

        fireTabRemoved(tab, comp, index);
        Component[] comps = tabs.getComponents();
        if (comps.length == 0) {
            allTabsClosed();
        }
        else {
            findSelectedTab(index);
        }
    }

    public int indexOfTab(String title) {
        Component[] comps = tabs.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c instanceof SparkTab) {
                SparkTab tab = (SparkTab)c;
                if (tab.getTitleLabel().getText().equals(title)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void findSelectedTab(int previousIndex) {
        Component[] comps = tabs.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c instanceof SparkTab && i == previousIndex) {
                setSelectedTab(((SparkTab)c));
                return;
            }
        }

        if (comps.length > 0 && comps.length == previousIndex) {
            SparkTab tab = (SparkTab)comps[previousIndex - 1];
            setSelectedTab(tab);
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void setSelectedTab(SparkTab tab) {
        CardLayout cl = (CardLayout)mainPanel.getLayout();
        cl.show(mainPanel, tab.getActualText());
        tab.setBoldWhenActive(isActiveButtonBold());
        tab.setSelected(true);
        deselectAllTabsExcept(tab);

        fireTabSelected(tab, getSelectedComponent(), getIndex(tab));
    }

    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof SparkTab) {
            SparkTab tab = (SparkTab)e.getSource();
            setSelectedTab(tab);
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    private void deselectAllTabsExcept(SparkTab tab) {
        Component[] comps = tabs.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c instanceof SparkTab) {
                SparkTab sparkTab = (SparkTab)c;
                if (sparkTab != tab) {
                    sparkTab.setSelected(false);
                    sparkTab.showBorder(true);
                }
                else if (sparkTab == tab) {
                    int j = i - 1;
                    if (j >= 0) {
                        SparkTab previousTab = (SparkTab)comps[j];
                        previousTab.showBorder(false);
                    }
                }
            }

        }

    }

    public Component getSelectedComponent() {
        Component[] comps = mainPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (c.isShowing()) {
                return c;
            }
        }

        return null;
    }


    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }


    public boolean isCloseButtonEnabled() {
        return closeButtonEnabled;
    }

    public void setCloseButtonEnabled(boolean closeButtonEnabled) {
        this.closeButtonEnabled = closeButtonEnabled;
    }

    public Icon getCloseInactiveButtonIcon() {
        return closeInactiveButtonIcon;
    }

    public void setCloseInactiveButtonIcon(Icon closeInactiveButtonIcon) {
        this.closeInactiveButtonIcon = closeInactiveButtonIcon;
    }

    public Icon getCloseActiveButtonIcon() {
        return closeActiveButtonIcon;
    }

    public void setCloseActiveButtonIcon(Icon closeActiveButtonIcon) {
        this.closeActiveButtonIcon = closeActiveButtonIcon;
    }


    public boolean isPopupAllowed() {
        return popupAllowed;
    }

    public void setPopupAllowed(boolean popupAllowed) {
        this.popupAllowed = popupAllowed;
    }


    public void addSparkTabbedPaneListener(SparkTabbedPaneListener listener) {
        listeners.add(listener);
    }

    public void removeSparkTabbedPaneListener(SparkTabbedPaneListener listener) {
        listeners.remove(listener);
    }

    public void fireTabAdded(SparkTab tab, Component component, int index) {
        final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
        while (list.hasNext()) {
            ((SparkTabbedPaneListener)list.next()).tabAdded(tab, component, index);
        }
    }

    public void fireTabRemoved(SparkTab tab, Component component, int index) {
        final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
        while (list.hasNext()) {
            ((SparkTabbedPaneListener)list.next()).tabRemoved(tab, component, index);
        }
    }

    public void fireTabSelected(SparkTab tab, Component component, int index) {
        final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
        while (list.hasNext()) {
            ((SparkTabbedPaneListener)list.next()).tabSelected(tab, component, index);
        }
    }

    public void allTabsClosed() {
        final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
        while (list.hasNext()) {
            ((SparkTabbedPaneListener)list.next()).allTabsRemoved();
        }
    }

    public boolean isActiveButtonBold() {
        return activeButtonBold;
    }

    public void setActiveButtonBold(boolean activeButtonBold) {
        this.activeButtonBold = activeButtonBold;
    }

    public static void main(String args[]) {
        JFrame f = new JFrame();
        SparkTabbedPane pane = new SparkTabbedPane(JTabbedPane.BOTTOM);
        pane.setCloseButtonEnabled(true);
        pane.setPopupAllowed(true);
        for (int i = 0; i < 3; i++) {
            pane.addTab("Hello" + i, SparkRes.getImageIcon(SparkRes.SMALL_AGENT_IMAGE), new JButton("BUTTON" + i));
        }


        f.add(pane);
        f.pack();
        f.setVisible(true);
    }
}
