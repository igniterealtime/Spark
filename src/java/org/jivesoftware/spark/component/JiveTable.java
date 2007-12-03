/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

/**
 * <code>JiveTable</code> class can be used to maintain quality look and feel
 * throughout the product. This is mainly from the rendering capabilities.
 *
 * @version 1.0, 03/12/14
 */
public class JiveTable extends JTable {
    private JiveTable.JiveTableModel tableModel;


    public JiveTable(String[] headers, Integer[] columnsToUseRenderer) {
        tableModel = new JiveTable.JiveTableModel(headers, 0, false);
        this.setModel(tableModel);

        getTableHeader().setReorderingAllowed(false);
        setGridColor(java.awt.Color.white);
        setRowHeight(20);
        getColumnModel().setColumnMargin(0);
        setSelectionBackground(new java.awt.Color(57, 109, 206));
        setSelectionForeground(java.awt.Color.white);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(false);

    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 3 || column == 4) {
            return new JiveTable.JButtonRenderer(false);
        }
        else if (column == 1) {
            return new JiveTable.JLabelRenderer(false);
        }
        else {
            return super.getCellRenderer(row, column);
        }
    }

    public void add(List list) {
        for (Object aList : list) {
            Object[] newRow = (Object[]) aList;
            tableModel.addRow(newRow);
        }
    }

    public Object[] getSelectedObject() {
        int selectedRow = getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int columnCount = getColumnCount();

        Object[] obj = new Object[columnCount];
        for (int j = 0; j < columnCount; j++) {
            Object objs = tableModel.getValueAt(selectedRow, j);
            obj[j] = objs;
        }

        return obj;
    }

    public class JiveTableModel extends DefaultTableModel {
        private boolean _isEditable;

        /**
         * Use the JiveTableModel in order to better handle the table. This allows
         * for consistency throughout the product.
         *
         * @param columnNames - String array of columnNames
         * @param numRows     - initial number of rows
         * @param isEditable  - true if the cells are editable, false otherwise.
         */
        public JiveTableModel(Object[] columnNames, int numRows, boolean isEditable) {
            super(columnNames, numRows);
            _isEditable = isEditable;
        }

        /**
         * Returns true if cell is editable.
         */
        public boolean isCellEditable(int row, int column) {
            return _isEditable;
        }
    }

    class JLabelRenderer extends JLabel implements TableCellRenderer {
        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;

        public JLabelRenderer(boolean isBordered) {
            super();
        }

        public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
            final String text = ((JLabel)color).getText();
            setText(text);

            final Icon icon = ((JLabel)color).getIcon();
            setIcon(icon);

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else {
                setForeground(Color.black);
                setBackground(Color.white);
                if (row % 2 == 0) {
                    //setBackground( new Color( 156, 207, 255 ) );
                }
            }

            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                }
                else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }
            return this;
        }
    }


    class JButtonRenderer extends JButton implements TableCellRenderer {
        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;

        public JButtonRenderer(boolean isBordered) {
            super();
            //this.isBordered = isBordered;
            //setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
            final String text = ((JButton)color).getText();
            setText(text);

            final Icon icon = ((JButton)color).getIcon();
            setIcon(icon);

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else {
                setForeground(Color.black);
                setBackground(Color.white);
                if (row % 2 == 0) {
                    //setBackground( new Color( 156, 207, 255 ) );
                }
            }

            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                }
                else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }
            return this;
        }
    }

    public JiveTable.JiveTableModel getTableModel() {
        return tableModel;
    }

}