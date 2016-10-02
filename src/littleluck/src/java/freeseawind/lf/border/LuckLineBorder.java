package freeseawind.lf.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A class implements AbstractBorder. This provides base class from which draws
 * the specified edge by rule.
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckLineBorder extends AbstractBorder
{
    /**
     * TOP border
     */
    public static final int NORTH = 1;

    /**
     * Bottom border
     */
    public static final int SOUTH = 2;

    /**
     * Left border
     */
    public static final int WEST = 4;

    /**
     * right border
     */
    public static final int EAST = 8;

    // 默认绘制矩形
    // edge rule.
    private int rule = 15;

    //边框间距
    // border insets.
    private Insets insets;

    // 边框颜色,默认是灰色
    // border color.
    private Color color;

    private static final long serialVersionUID = 8187996726188029495L;

    public LuckLineBorder(Insets insets)
    {
        this(insets, 15);
    }

    /**
     *
     * @param insets 
     * @param rule {@link LuckLineBorder#NORTH},{@link LuckLineBorder#SOUTH}
     *             {@link LuckLineBorder#WEST},{@link LuckLineBorder#EAST}
     */
    public LuckLineBorder(Insets insets, int rule)
    {
        this(insets, rule, new Color(200, 200, 200));
    }

    public LuckLineBorder(Insets insets, int rule, Color color)
    {
        this.color = color;

        this.insets = insets;

        if(rule > 0 && rule <= 15)
        {
            this.rule = rule;
        }
    }

    @Override
    public void paintBorder(Component c,
                            Graphics g,
                            int x,
                            int y,
                            int width,
                            int height)
    {
        Color oldColor = g.getColor();

        g.setColor(color);

        // draw top
        if((rule & NORTH) != 0)
        {
            g.drawLine(0, 0, width, 0);
        }

        // draw left
        if((rule & WEST) != 0)
        {
            g.drawLine(0, 0, 0, height);
        }

        // draw bottom
        if((rule & SOUTH) != 0)
        {
            g.drawLine(0, height - 1, width, height - 1);
        }

        // draw right
        if((rule & EAST) != 0)
        {
            g.drawLine(width - 1, 0, width - 1, height );
        }

        g.setColor(oldColor);
    }

    @Override
    public Insets getBorderInsets(Component c)
    {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
        return getBorderInsets(c);
    }
}
