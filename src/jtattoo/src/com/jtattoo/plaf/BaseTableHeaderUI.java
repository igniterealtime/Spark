/*
 * Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
 *  
 * JTattoo is multiple licensed. If your are an open source developer you can use
 * it under the terms and conditions of the GNU General Public License version 2.0
 * or later as published by the Free Software Foundation.
 *  
 * see: gpl-2.0.txt
 * 
 * If you pay for a license you will become a registered user who could use the
 * software under the terms and conditions of the GNU Lesser General Public License
 * version 2.0 or later with classpath exception as published by the Free Software
 * Foundation.
 * 
 * see: lgpl-2.0.txt
 * see: classpath-exception.txt
 * 
 * Registered users could also use JTattoo under the terms and conditions of the 
 * Apache License, Version 2.0 as published by the Apache Software Foundation.
 *  
 * see: APACHE-LICENSE-2.0.txt
 */
package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.*;

/**
 *
 * @author Michael Hagen
 */
public class BaseTableHeaderUI extends BasicTableHeaderUI {

    private TableCellRenderer originalHeaderRenderer;
    protected MouseAdapter myMouseAdapter = null;
    protected MouseMotionAdapter myMouseMotionAdapter = null;
    protected int rolloverCol = -1;

    public static ComponentUI createUI(JComponent h) {
        return new BaseTableHeaderUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        if ((header != null) && header.getTable() != null) {
            originalHeaderRenderer = header.getDefaultRenderer();
            if ((originalHeaderRenderer != null)
                    && "sun.swing.table.DefaultTableCellHeaderRenderer".equals(originalHeaderRenderer.getClass().getName())) {
                header.setDefaultRenderer(new BaseDefaultHeaderRenderer());
            }
        }
    }

    public void uninstallUI(JComponent c) {
        if ((header != null) && (header.getTable() != null)) {
            if (header.getDefaultRenderer() instanceof BaseDefaultHeaderRenderer) {
                header.setDefaultRenderer(originalHeaderRenderer);
            }
        }
        super.uninstallUI(c);
    }

    public void installListeners() {
        super.installListeners();
        myMouseAdapter = new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if ((header == null) || (header.getTable() == null)) {
                    return;
                }
                boolean rolloverEnabled = Boolean.TRUE.equals(header.getClientProperty("rolloverEnabled"));
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (rolloverEnabled || sortingAllowed || header.getReorderingAllowed()) {
                    if (header.getBounds().contains(e.getPoint())) {
                        int oldRolloverCol = rolloverCol;
                        rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                        updateRolloverColumn(oldRolloverCol, rolloverCol);
                    } else {
                        int oldRolloverCol = rolloverCol;
                        rolloverCol = -1;
                        updateRolloverColumn(oldRolloverCol, rolloverCol);
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {
                if ((header == null) || (header.getTable() == null)) {
                    return;
                }
                boolean rolloverEnabled = Boolean.TRUE.equals(header.getClientProperty("rolloverEnabled"));
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (rolloverEnabled || sortingAllowed || header.getReorderingAllowed()) {
                    int oldRolloverCol = rolloverCol;
                    rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                    updateRolloverColumn(oldRolloverCol, rolloverCol);
                }
            }

            public void mouseExited(MouseEvent e) {
                if ((header == null) || (header.getTable() == null)) {
                    return;
                }
                boolean rolloverEnabled = Boolean.TRUE.equals(header.getClientProperty("rolloverEnabled"));
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (rolloverEnabled || sortingAllowed || header.getReorderingAllowed()) {
                    int oldRolloverCol = rolloverCol;
                    rolloverCol = -1;
                    updateRolloverColumn(oldRolloverCol, rolloverCol);
                }
            }
        };
        myMouseMotionAdapter = new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if ((header == null) || (header.getTable() == null)) {
                    return;
                }
                boolean rolloverEnabled = Boolean.TRUE.equals(header.getClientProperty("rolloverEnabled"));
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (rolloverEnabled || sortingAllowed || header.getReorderingAllowed()) {
                    if (header.getDraggedColumn() == null) {
                        int oldRolloverCol = rolloverCol;
                        rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                        updateRolloverColumn(oldRolloverCol, rolloverCol);
                    }
                }
            }

            public void mouseDragged(MouseEvent e) {
                if ((header == null) || (header.getTable() == null)) {
                    return;
                }
                boolean rolloverEnabled = Boolean.TRUE.equals(header.getClientProperty("rolloverEnabled"));
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (rolloverEnabled || sortingAllowed || header.getReorderingAllowed()) {
                    if (header.getDraggedColumn() != null && header.getDraggedColumn().getIdentifier() != null) {
                        rolloverCol = header.getColumnModel().getColumnIndex(header.getDraggedColumn().getIdentifier());
                    } else if (header.getResizingColumn() != null) {
                        rolloverCol = -1;
                    }
                }
            }
        };
        header.addMouseListener(myMouseAdapter);
        header.addMouseMotionListener(myMouseMotionAdapter);
    }

    public void uninstallListeners() {
        header.removeMouseListener(myMouseAdapter);
        header.removeMouseMotionListener(myMouseMotionAdapter);
        super.uninstallListeners();
    }

    protected boolean drawAllwaysActive() {
        return false;
    }

    protected boolean drawRolloverBar() {
        return false;
    }

    protected Component getHeaderRenderer(int col) {
        if ((header == null) || (header.getTable() == null)) {
            return null;
        }
        TableColumn tabCol = header.getColumnModel().getColumn(col);
        TableCellRenderer renderer = tabCol.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        return renderer.getTableCellRendererComponent(header.getTable(), tabCol.getHeaderValue(), false, false, -1, col);
    }

    private int getHeaderHeight() {
        if ((header == null) || (header.getTable() == null)) {
            return 0;
        }
        int height = 0;
        boolean accomodatedDefault = false;
        TableColumnModel columnModel = header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            boolean isDefault = (aColumn.getHeaderRenderer() == null);

            if (!isDefault || !accomodatedDefault) {
                Component comp = getHeaderRenderer(column);
                int rendererHeight = comp.getPreferredSize().height;
                height = Math.max(height, rendererHeight);

                // Configuring the header renderer to calculate its preferred size
                // is expensive. Optimise this by assuming the default renderer
                // always has the same height as the first non-zero height that
                // it returns for a non-null/non-empty value.
                if (isDefault && rendererHeight > 0) {
                    Object headerValue = aColumn.getHeaderValue();
                    if (headerValue != null) {
                        headerValue = headerValue.toString();

                        if (headerValue != null && !headerValue.equals("")) {
                            accomodatedDefault = true;
                        }
                    }
                }
            }
        }
        return height + 2;
    }

    /**
     * Return the preferred size of the header. The preferred height is the maximum of the preferred heights of all of
     * the components provided by the header renderers. The preferred width is the sum of the preferred widths of each
     * column (plus inter-cell spacing).
     */
    public Dimension getPreferredSize(JComponent c) {
        if ((header == null) || (header.getTable() == null)) {
            return new Dimension(0, 0);
        }
        long width = 0;
        Enumeration enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn) enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight());
    }

    protected void updateRolloverColumn(int oldColumn, int newColumn) {
        if ((header == null) || (header.getTable() == null)) {
            return;
        }
        header.repaint(header.getHeaderRect(oldColumn));
        header.repaint(header.getHeaderRect(newColumn));
    }

    protected void rolloverColumnUpdated(int oldColumn, int newColumn) {
        // Empty to avoid multiple paints
    }

    public void paint(Graphics g, JComponent c) {
        if ((header == null) || (header.getTable() == null) || header.getColumnModel().getColumnCount() <= 0) {
            return;
        }
        
        boolean ltr = header.getComponentOrientation().isLeftToRight();
        Rectangle clip = g.getClipBounds();
        Point left = clip.getLocation();
        Point right = new Point(clip.x + clip.width - 1, clip.y);
        TableColumnModel cm = header.getColumnModel();
        int cMin = header.columnAtPoint(ltr ? left : right);
        int cMax = header.columnAtPoint(ltr ? right : left);
        // This should never happen.
        if (cMin == -1) {
            cMin = 0;
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
            cMax = cm.getColumnCount() - 1;
        }

        TableColumn draggedColumn = header.getDraggedColumn();
        Rectangle cellRect = header.getHeaderRect(ltr ? cMin : cMax);
        int columnWidth;
        TableColumn aColumn;
        if (ltr) {
            for (int column = cMin; column <= cMax; column++) {
                aColumn = cm.getColumn(column);
                columnWidth = aColumn.getWidth();
                cellRect.width = columnWidth;
                if (aColumn != draggedColumn) {
                    paintCell(g, cellRect, column);
                }
                cellRect.x += columnWidth;
            }
        } else {
            for (int column = cMax; column >= cMin; column--) {
                aColumn = cm.getColumn(column);
                columnWidth = aColumn.getWidth();
                cellRect.width = columnWidth;
                if (aColumn != draggedColumn) {
                    paintCell(g, cellRect, column);
                }
                cellRect.x += columnWidth;
            }
        }

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
            int draggedColumnIndex = viewIndexForColumn(draggedColumn);
            Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex);
            // Draw a gray well in place of the moving column.
            g.setColor(header.getParent().getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);
            draggedCellRect.x += header.getDraggedDistance();

            // Fill the background.
            g.setColor(header.getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }

        // Remove all components in the rendererPane.
        rendererPane.removeAll();
    }

    protected void paintBackground(Graphics g, Rectangle cellRect, int col) {
        Component component = getHeaderRenderer(col);
        int x = cellRect.x;
        int y = cellRect.y;
        int w = cellRect.width;
        int h = cellRect.height;
        if (header.getBackground() instanceof ColorUIResource) {
            if ((col == rolloverCol) && (component != null) && component.isEnabled()) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x, y, w, h);
            } else if (drawAllwaysActive() || JTattooUtilities.isFrameActive(header)) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), x, y, w, h);
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getInActiveColors(), x, y, w, h);
            }
        } else {
            g.setColor(header.getBackground());
            g.fillRect(x, y, w, h);
        }
    }

    protected void paintCell(Graphics g, Rectangle cellRect, int col) {
        if ((header == null) || (header.getTable() == null)) {
            return;
        }
        Component component = getHeaderRenderer(col);
        if (!(component instanceof BaseDefaultHeaderRenderer)) {
            paintBackground(g, cellRect, col);
        }
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        if ((header == null) || (header.getTable() == null)) {
            return -1;
        }
        TableColumnModel cm = header.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

//----------------------------------------------------------------------------------------------------------------------
// inner classes
//----------------------------------------------------------------------------------------------------------------------
    private class BaseDefaultHeaderRenderer extends DefaultTableCellRenderer {

        public BaseDefaultHeaderRenderer() {
            super();
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return new MyRenderComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private class MyRenderComponent extends JLabel {

        private JTable table = null;
        private int col = 0;
        private int gv = 0;

        public MyRenderComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            super();
            this.table = table;
            this.col = col;
            if (value != null) {
                setText(value.toString());
            } else {
                setText("");
            }
            setOpaque(false);
            setFont(UIManager.getFont("TableHeader.font"));
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setHorizontalAlignment(JLabel.CENTER);
            setHorizontalTextPosition(SwingConstants.LEADING);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            if ((JTattooUtilities.getJavaVersion() >= 1.6) && (UIManager.getLookAndFeel() instanceof AbstractLookAndFeel)) {
                RowSorter rowSorter = table == null ? null : table.getRowSorter();
                List keyList = rowSorter == null ? null : rowSorter.getSortKeys();
                if ((keyList != null) && (keyList.size() > 0)) {
                    RowSorter.SortKey sortKey = (RowSorter.SortKey) keyList.get(0);
                    if (sortKey.getColumn() == table.convertColumnIndexToModel(col)) {
                        AbstractIconFactory iconFactory = ((AbstractLookAndFeel) UIManager.getLookAndFeel()).getIconFactory();
                        if (sortKey.getSortOrder().equals(SortOrder.ASCENDING)) {
                            setIcon(iconFactory.getUpArrowIcon());
                        } else if (sortKey.getSortOrder().equals(SortOrder.DESCENDING)) {
                            setIcon(iconFactory.getDownArrowIcon());
                        }
                    }
                }
            }
            gv = ColorHelper.getGrayValue(AbstractLookAndFeel.getTheme().getRolloverColor());
        }

        protected void paintBackground(Graphics g) {
            int draggedColumn = -1;
            if ((header != null) && (header.getTable() != null) && header.getDraggedColumn() != null) {
                draggedColumn = header.getColumnModel().getColumnIndex(header.getDraggedColumn().getIdentifier());
            }
            int w = getWidth();
            int h = getHeight();
            if ((table != null) && table.isEnabled() && (col == rolloverCol || col == draggedColumn)) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), 0, 0, w, h);
                if (drawRolloverBar()) {
                    g.setColor(AbstractLookAndFeel.getFocusColor());
                    g.drawLine(0, 0, w - 1, 0);
                    g.drawLine(0, 1, w - 1, 1);
                    g.drawLine(0, 2, w - 1, 2);
                }
            } else if (drawAllwaysActive() || JTattooUtilities.isFrameActive(header)) {
                if (header.getBackground() instanceof ColorUIResource) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), 0, 0, w, h);
                } else {
                    g.setColor(header.getBackground());
                    g.fillRect(0, 0, w, h);
                }
            } else {
                if (header.getBackground() instanceof ColorUIResource) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getInActiveColors(), 0, 0, w, h);
                } else {
                    g.setColor(header.getBackground());
                    g.fillRect(0, 0, w, h);
                }
            }
        }

        public void paint(Graphics g) {
            paintBackground(g);
            if (rolloverCol == col) {
                if (gv > 128) {
                    setForeground(Color.black);
                } else {
                    setForeground(Color.white);
                }
            }
            super.paint(g);
        }
    }
}
