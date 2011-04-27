/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;

/**
 *
 * @author  Michael Hagen
 */
public class BaseTableHeaderUI extends BasicTableHeaderUI {

    protected MouseAdapter myMouseAdapter = null;
    protected MouseMotionAdapter myMouseMotionAdapter = null;
    protected int rolloverCol = -1;

    public static ComponentUI createUI(JComponent c) {
        return new BaseTableHeaderUI();
    }

    public void installListeners() {
        super.installListeners();
        myMouseAdapter = new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (!header.getReorderingAllowed()) {
                    return;
                }
                if (header.getBounds().contains(e.getPoint())) {
                    rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                    header.repaint();
                } else {
                    rolloverCol = -1;
                    header.repaint();
                }
            }

            public void mouseEntered(MouseEvent e) {
                if (!header.getReorderingAllowed()) {
                    return;
                }
                rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                header.repaint();
            }

            public void mouseExited(MouseEvent e) {
                if (!header.getReorderingAllowed()) {
                    return;
                }
                rolloverCol = -1;
                header.repaint();
            }
        };
        myMouseMotionAdapter = new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {
                if (!header.getReorderingAllowed()) {
                    return;
                }
                if (header.getDraggedColumn() == null) {
                    rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                    header.repaint();
                }
            }

            public void mouseDragged(MouseEvent e) {
                if (!header.getReorderingAllowed()) {
                    return;
                }
                if (header.getDraggedColumn() != null) {
                    try {
                        rolloverCol = header.getColumnModel().getColumnIndex(header.getDraggedColumn().getIdentifier());
                    } catch (Exception ex) {
                    }
                } else if (header.getResizingColumn() != null) {
                    rolloverCol = -1;
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

    private int getHeaderHeight() {
        int height = 0;
        boolean accomodatedDefault = false;
        TableColumnModel columnModel = header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            // Configuring the header renderer to calculate its preferred size is expensive.
            // Optimise this by assuming the default renderer always has the same height.
            if (aColumn.getHeaderRenderer() != null || !accomodatedDefault) {
                Component comp = getHeaderRenderer(column);
                int rendererHeight = comp.getPreferredSize().height;
                height = Math.max(height, rendererHeight);
                // If the header value is empty (== "") in the
                // first column (and this column is set up
                // to use the default renderer) we will
                // return zero from this routine and the header
                // will disappear altogether. Avoiding the calculation
                // of the preferred size is such a performance win for
                // most applications that we will continue to
                // use this cheaper calculation, handling these
                // issues as `edge cases'.
                if (rendererHeight > 0) {
                    accomodatedDefault = true;
                }
            }
        }
        return height + 2;
    }

    private Dimension createHeaderSize(long width) {
        // None of the callers include the intercell spacing, do it here.
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight());
    }

    /**
     * Return the preferred size of the header. The preferred height is the 
     * maximum of the preferred heights of all of the components provided 
     * by the header renderers. The preferred width is the sum of the 
     * preferred widths of each column (plus inter-cell spacing).
     */
    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn) enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createHeaderSize(width);
    }

    public void paint(Graphics g, JComponent c) {
        if (header.getColumnModel().getColumnCount() <= 0) {
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

    private Component getHeaderRenderer(int col) {
        TableColumn tabCol = header.getColumnModel().getColumn(col);
        TableCellRenderer renderer = tabCol.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        return renderer.getTableCellRendererComponent(header.getTable(), tabCol.getHeaderValue(), false, false, -1, col);
    }

    protected void paintCell(Graphics g, Rectangle cellRect, int col) {
        Component component = getHeaderRenderer(col);
        paintBackground(g, cellRect, col);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    protected void paintBackground(Graphics g, Rectangle cellRect, int col) {
        Component component = getHeaderRenderer(col);
        int x = cellRect.x;
        int y = cellRect.y;
        int w = cellRect.width;
        int h = cellRect.height;
        if (col == rolloverCol && component.isEnabled()) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x, y, w, h);
        } else if (JTattooUtilities.isFrameActive(header)) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), x, y, w, h);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getInActiveColors(), x, y, w, h);
        }
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = header.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }
}
