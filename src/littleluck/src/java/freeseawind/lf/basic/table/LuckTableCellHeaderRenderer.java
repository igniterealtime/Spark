package freeseawind.lf.basic.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import freeseawind.lf.border.LuckLineBorder;

/**
 * TableHeader单元渲染处理实现类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTableCellHeaderRenderer extends JLabel
        implements TableCellRenderer, Serializable
{
    private static final long serialVersionUID = 4540501865134659334L;
    protected Border noramlBorder;
    protected Border endColumnBorder;
    protected Color outerGradientStart;
    protected Color outerGradientEnd;
    protected Color innerGradientStart;
    protected Color innerGradientEnd;

    public LuckTableCellHeaderRenderer()
    {
        super();

        setOpaque(false);

        setHorizontalAlignment(JLabel.CENTER);

        setHorizontalTextPosition(JLabel.LEFT);

        setIconTextGap(6);

        setName("Table.cellRenderer");

        outerGradientStart = new Color(252, 252, 252);

        outerGradientEnd = new Color(226, 226, 226);

        innerGradientStart = new Color(238, 238, 238);

        innerGradientEnd = new Color(217, 217, 217);

        noramlBorder = new LuckLineBorder(new Insets(0, 0, 1, 1), 10);

        endColumnBorder = new LuckLineBorder(new Insets(0, 0, 1, 0), 2);
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        if (table == null)
        {
            return this;
        }

        if(column == table.getColumnCount() - 1)
        {
            setBorder(endColumnBorder);
        }
        else
        {
            setBorder(noramlBorder);
        }

        boolean isPaintingForPrint = false;

        if(table.getTableHeader() != null)
        {
            isPaintingForPrint = table.getTableHeader().isPaintingForPrint();
        }

        Icon sortIcon = null;

        if (!isPaintingForPrint && table.getRowSorter() != null)
        {
            SortOrder sortOrder = getColumnSortOrder(table, column);

            if (sortOrder != null)
            {
                switch (sortOrder)
                {
                    case ASCENDING:

                        sortIcon = UIManager.getIcon("Table.ascendingSortIcon");

                        break;

                    case DESCENDING:

                        sortIcon = UIManager.getIcon("Table.descendingSortIcon");

                        break;

                    case UNSORTED:

                        break;
                }
            }
        }

        setIcon(sortIcon);

        setFont(table.getFont());

        setValue(value);

        return this;
    }

    public static SortOrder getColumnSortOrder(JTable table, int column)
    {
        SortOrder rv = null;

        if (table == null || table.getRowSorter() == null)
        {
            return rv;
        }

        java.util.List<? extends RowSorter.SortKey> sortKeys = table
                .getRowSorter().getSortKeys();

        if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() == table
                .convertColumnIndexToModel(column))
        {
            rv = sortKeys.get(0).getSortOrder();
        }

        return rv;
    }

    public void paint(Graphics g)
    {
        super.paint(g);
    }

    protected void paintComponent(Graphics g)
    {
        Rectangle dimension = getBounds();

        Insets i = getInsets();

        int w = dimension.width;

        int h = dimension.height;

        GradientPaint outerGradient = new GradientPaint(0, 0,
                outerGradientStart, 0, h - 2, outerGradientEnd);

        ((Graphics2D)g).setPaint(outerGradient);

        g.fillRect(0, 0, w - i.left -i.right, h - 1);

        GradientPaint innerGradient = new GradientPaint(1, 1,
                innerGradientStart, 1, h - 2, innerGradientEnd);

        ((Graphics2D)g).setPaint(innerGradient);

        g.fillRect(1, 1, w - i.left - i.right - 2, h - 3);

        super.paintComponent(g);
    }

    /**
     * Overrides <code>JComponent.setForeground</code> to assign
     * the unselected-foreground color to the specified color.
     *
     * @param c set the foreground color to this value
     */
    public void setForeground(Color c)
    {
        super.setForeground(c);
    }

    /**
     * Overrides <code>JComponent.setBackground</code> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground(Color c)
    {
        super.setBackground(c);
    }

    /**
     * Notification from the <code>UIManager</code> that the look and feel
     * [L&amp;F] has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI()
    {
        super.updateUI();
        setForeground(null);
        setBackground(null);
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public boolean isOpaque()
    {
        Color back = getBackground();

        Component p = getParent();

        if (p != null)
        {
            p = p.getParent();
        }

        // p should now be the JTable.
        boolean colorMatch = (back != null) && (p != null)
                && back.equals(p.getBackground()) && p.isOpaque();

        return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     *
     * @since 1.5
     */
    public void invalidate()
    {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    public void validate()
    {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    public void revalidate()
    {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    public void repaint(long tm, int x, int y, int width, int height)
    {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    public void repaint(Rectangle r)
    {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     *
     * @since 1.5
     */
    public void repaint()
    {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    protected void firePropertyChange(String propertyName,
                                      Object oldValue,
                                      Object newValue)
    {
        // Strings get interned...
        if(propertyName.equals("text"))
        {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }

        if(propertyName.equals("labelFor"))
        {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }

        if(propertyName.equals("displayedMnemonic"))
        {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }

        if ((propertyName.equals("font") || propertyName.equals("foreground"))
                && oldValue != newValue
                && javax.swing.plaf.basic.BasicHTML.propertyKey != null)
        {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    public void firePropertyChange(String propertyName,
                                   boolean oldValue,
                                   boolean newValue)
    {
    }

    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     *
     * @param value
     *            the string value for this cell; if value is <code>null</code>
     *            it sets the text value to an empty string
     * @see JLabel#setText
     *
     */
    protected void setValue(Object value)
    {
        setText((value == null) ? "" : value.toString());
    }
}
