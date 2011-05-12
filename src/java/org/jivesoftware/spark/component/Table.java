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
package org.jivesoftware.spark.component;

import org.jivesoftware.spark.util.GraphicUtils;
import org.jdesktop.swingx.JXTable;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>Table</code> class can be used to maintain quality look and feel
 * throughout the product. This is mainly from the rendering capabilities.
 *
 * @version 1.0, 03/12/14
 */
public abstract class Table extends JXTable {
   
    private static final long serialVersionUID = -6511813002260596088L;

    private Table.JiveTableModel tableModel;

    /**
     * Define the color of row and column selections.
     */
    public static final Color SELECTION_COLOR = new Color(166, 202, 240);

    /**
     * Define the color used in the tooltips.
     */
    public static final Color TOOLTIP_COLOR = new Color(166, 202, 240);

    private final Map<Integer,Object> objectMap = new HashMap<Integer,Object>();

    /**
     * Empty Constructor.
     */
    protected Table() {
    }

    public String getToolTipText(MouseEvent e) {
        int r = rowAtPoint(e.getPoint());
        int c = columnAtPoint(e.getPoint());
        Object value;
        try {
            value = getValueAt(r, c);
        }
        catch (Exception e1) {
            // If we encounter a row that should not actually exist and therefore
            // has a null value. Just return an empty string for the tooltip.
            return "";
        }

        String tooltipValue = null;

        if (value instanceof JLabel) {
            tooltipValue = ((JLabel)value).getToolTipText();
        }

        if (value instanceof JLabel && tooltipValue == null) {
            tooltipValue = ((JLabel)value).getText();
        }
        else if (value != null && tooltipValue == null) {
            tooltipValue = value.toString();
        }
        else if (tooltipValue == null) {
            tooltipValue = "";
        }

        return GraphicUtils.createToolTip(tooltipValue);
    }

    // Handle image rendering correctly
    public TableCellRenderer getCellRenderer(int row, int column) {
        Object o = getValueAt(row, column);
        if (o != null) {
            if (o instanceof JLabel) {
                return new JLabelRenderer(false);
            }
        }
        return super.getCellRenderer(row, column);
    }

    /**
     * Creates a table using the specified table headers.
     *
     * @param headers the table headers to use.
     */
    protected Table(String[] headers) {
        tableModel = new Table.JiveTableModel(headers, 0, false);
        setModel(tableModel);

        // Handle JDK 1.5 bug with preferred size on table headers.
        JTableHeader header = getTableHeader();
        Dimension dim = header.getPreferredSize();
        dim.height = 20;
        header.setPreferredSize(dim);


        getTableHeader().setReorderingAllowed(false);
        setGridColor(Color.white);
        setRowHeight(20);
        getColumnModel().setColumnMargin(0);
        setSelectionBackground(SELECTION_COLOR);
        setSelectionForeground(Color.black);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    e.consume();
                    enterPressed();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {

            }
        });
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(getBackground());
        }
        else if (isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(SELECTION_COLOR);
        }
        else {
            // If not shaded, match the table's background
            c.setBackground(getBackground());
            //c.setBackground(new Color(217, 232, 250));
        }
        return c;
    }


    /**
     * Adds a list to the table model.
     *
     * @param list the list to add to the model.
     */
    public void add(List<Object> list) {
        for (Object aList : list) {
            Object[] newRow = (Object[]) aList;
            tableModel.addRow(newRow);
        }
    }

    /**
     * Get the object array of a row.
     *
     * @return the object array of a row.
     */
    public Object[] getSelectedRowObject() {
        return getRowObject(getSelectedRow());
    }

    /**
     * Returns the object[] of a row.
     *
     * @param selectedRow the row to retrieve.
     * @return the object[] of a row.
     */
    public Object[] getRowObject(int selectedRow) {
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

    /**
     * Removes all columns and rows from table.
     */
    public void clearTable() {
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            getTableModel().removeRow(0);
        }
    }

    /**
     * The internal Table Model.
     */
    public static class JiveTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 2256144012470569949L;
	private boolean isEditable;

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
            this.isEditable = isEditable;
        }

        /**
         * Returns true if cell is editable.
         *
         * @param row    the row to check.
         * @param column the column to check.
         * @return true if the cell is editable.
         */
        public boolean isCellEditable(int row, int column) {
            return isEditable;
        }
    }

    /**
     * A swing renderer used to display labels within a table.
     */
    public class JLabelRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 4433780600297455731L;
	Border unselectedBorder;
        Border selectedBorder;
        boolean isBordered = true;

        /**
         * JLabelConstructor to build ui.
         *
         * @param isBordered true if the border should be shown.
         */
        public JLabelRenderer(boolean isBordered) {
            setOpaque(true);
            this.isBordered = isBordered;
        }

        public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
            final String text = ((JLabel)color).getText();
            if (text != null) {
                setText(" " + text);
            }
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

    /**
     * A swing renderer to dispaly Textareas within a table.
     */
    public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = -8533968851464831361L;

	/**
         * Create new renderer with font.
         *
         * @param font the font to use in the renderer.
         */
        public TextAreaCellRenderer(Font font) {
            setLineWrap(true);
            setWrapStyleWord(true);
            setFont(font);
        }

        public Component getTableCellRendererComponent(JTable jTable, Object obj, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // set color & border here
            setText(obj == null ? "" : obj.toString());
            setSize(jTable.getColumnModel().getColumn(column).getWidth(),
                    getPreferredSize().height);
            if (jTable.getRowHeight(row) != getPreferredSize().height) {
                jTable.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }

    /**
     * A swing renderer used to display Buttons within a table.
     */
    public class JButtonRenderer extends JButton implements TableCellRenderer {

	private static final long serialVersionUID = 1268514163461994738L;
	Border unselectedBorder;
        Border selectedBorder;
        boolean isBordered = true;

        /**
         * Empty Constructor.
         */
        public JButtonRenderer() {
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

    public class ComboBoxRenderer extends JComboBox implements TableCellRenderer {

	private static final long serialVersionUID = -545496178928790522L;

	public ComboBoxRenderer() {

        }

        public ComboBoxRenderer(String[] items) {
            super(items);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }

    public class MyComboBoxEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 6097118754932234992L;

	public MyComboBoxEditor(String[] items) {
            super(new JComboBox(items));
        }
    }

    /**
     * Returns the table model.
     *
     * @return the table model.
     */
    public Table.JiveTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Clears all objects from map.
     */
    public void clearObjectMap() {
        objectMap.clear();
    }

    /**
     * Associate an object with a row.
     *
     * @param row    - the current row
     * @param object - the object to associate with the row.
     */
    public void addObject(int row, Object object) {
        objectMap.put(row, object);
    }

    /**
     * Returns the associated row object.
     *
     * @param row - the row associated with the object.
     * @return The object associated with the row.
     */
    public Object getObject(int row) {
        return objectMap.get(row);
    }

    /**
     * Override to handle when enter is pressed.
     */
    public void enterPressed() {
    }

}