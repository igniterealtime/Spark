/*
 * Copyright 2011 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
        originalHeaderRenderer = header.getDefaultRenderer();
        if ((originalHeaderRenderer != null)
                && "sun.swing.table.DefaultTableCellHeaderRenderer".equals(originalHeaderRenderer.getClass().getName())) {
            header.setDefaultRenderer(new BaseDefaultHeaderRenderer());
        }
    }

    public void uninstallUI(JComponent c) {
        if (header.getDefaultRenderer() instanceof BaseDefaultHeaderRenderer) {
            header.setDefaultRenderer(originalHeaderRenderer);
        }
        super.uninstallUI(c);
    }

    public void installListeners() {
        super.installListeners();
        myMouseAdapter = new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (sortingAllowed || header.getReorderingAllowed()) {
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
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (sortingAllowed || header.getReorderingAllowed()) {
                    int oldRolloverCol = rolloverCol;
                    rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                    updateRolloverColumn(oldRolloverCol, rolloverCol);
                }
            }

            public void mouseExited(MouseEvent e) {
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (sortingAllowed || header.getReorderingAllowed()) {
                    int oldRolloverCol = rolloverCol;
                    rolloverCol = -1;
                    updateRolloverColumn(oldRolloverCol, rolloverCol);
                }
            }
        };
        myMouseMotionAdapter = new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (sortingAllowed || header.getReorderingAllowed()) {
                    if (header.getDraggedColumn() == null) {
                        int oldRolloverCol = rolloverCol;
                        rolloverCol = header.getColumnModel().getColumnIndexAtX(e.getX());
                        updateRolloverColumn(oldRolloverCol, rolloverCol);
                    }
                }
            }

            public void mouseDragged(MouseEvent e) {
                boolean sortingAllowed = false;
                if (JTattooUtilities.getJavaVersion() >= 1.6) {
                    sortingAllowed = header.getTable().getRowSorter() != null;
                }
                if (sortingAllowed || header.getReorderingAllowed()) {
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
        TableColumn tabCol = header.getColumnModel().getColumn(col);
        TableCellRenderer renderer = tabCol.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        return renderer.getTableCellRendererComponent(header.getTable(), tabCol.getHeaderValue(), false, false, -1, col);
    }

    private int getHeaderHeight() {
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
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight());
    }

    protected void updateRolloverColumn(int oldColumn, int newColumn) {
        header.repaint(header.getHeaderRect(oldColumn));
        header.repaint(header.getHeaderRect(newColumn));
    }

    protected void rolloverColumnUpdated(int oldColumn, int newColumn) {
        // Empty to avoid multiple paints
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

    protected void paintCell(Graphics g, Rectangle cellRect, int col) {
        Component component = getHeaderRenderer(col);
        if (!(component instanceof BaseDefaultHeaderRenderer)) {
            paintBackground(g, cellRect, col);
        }
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
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
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setHorizontalAlignment(JLabel.CENTER);
            setHorizontalTextPosition(SwingConstants.LEADING);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            if (JTattooUtilities.getJavaVersion() >= 1.6) {
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
            if (header.getDraggedColumn() != null) {
                draggedColumn = header.getColumnModel().getColumnIndex(header.getDraggedColumn().getIdentifier());
            }
            if (table.isEnabled() && (col == rolloverCol || col == draggedColumn)) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), 0, 0, getWidth(), getHeight());
                if (drawRolloverBar()) {
                    g.setColor(AbstractLookAndFeel.getFocusColor());
                    g.drawLine(0, 0, getWidth() - 1, 0);
                    g.drawLine(0, 1, getWidth() - 1, 1);
                    g.drawLine(0, 2, getWidth() - 1, 2);
                }
            } else if (drawAllwaysActive() || JTattooUtilities.isFrameActive(header)) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), 0, 0, getWidth(), getHeight());
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getInActiveColors(), 0, 0, getWidth(), getHeight());
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
