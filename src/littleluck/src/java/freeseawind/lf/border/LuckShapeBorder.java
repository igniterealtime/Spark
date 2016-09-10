package freeseawind.lf.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RectangularShape;

import javax.swing.border.AbstractBorder;

/**
 * 具有焦点处理的线型边框
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public abstract class LuckShapeBorder extends AbstractBorder
{
    private static final long serialVersionUID = -6529775355312979219L;
    private Insets insets;
    private Color normalColor;
    private Color focusColor;
    private Color outShadowColor;
    private Color innerShadowColor;

    public LuckShapeBorder(Insets i)
    {
        this.insets = i;
        this.normalColor = new Color(190, 190, 190);
        this.focusColor = new Color(3, 158, 211);
        this.outShadowColor = new Color(179, 218, 231);
        this.innerShadowColor = new Color(221, 242, 249);
    }

    public void paintBorder(Component c,
                            Graphics g,
                            int x,
                            int y,
                            int width,
                            int height)
    {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        RectangularShape shape = getBorderShape(c);

        if(shape != null)
        {
            shape.setFrame(x, y, width - 1, height - 1);

            if(!isFocusGained(c))
            {
                // 非焦点状态下
                g2d.setColor(normalColor);

                g2d.draw(shape);
            }
            else
            {
                // 获取焦点状态下
                g2d.setColor(outShadowColor);

                g2d.draw(shape);

                g2d.setColor(innerShadowColor);

                g2d.drawRect(x + 2, y + 2, width - 5, height - 5);

                g2d.setColor(focusColor);

                shape.setFrame(x + 1, y + 1, width - 3, height - 3);

                g2d.draw(shape);
            }
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public Insets getBorderInsets(Component c)
    {
        return insets;
    }

    public Insets getBorderInsets(Component c, Insets insets)
    {
        return this.insets;
    }

    public boolean isFocusGained(Component c)
    {
        LuckBorderField field = getBorderField(c);

        return isFoucusGaind(field);
    }

    public RectangularShape getBorderShape(Component c)
    {
        LuckBorderField field = getBorderField(c);

        return getBorderShape(field, c);
    }

    /**
     * 获取边框属性
     *
     * @param c 边框所属容器类
     * @return 当前边框属性
     */
    public abstract LuckBorderField getBorderField(Component c);

    /**
     * 返回边框形状
     * @param field
     * @param c
     * @return
     */
    private RectangularShape getBorderShape(LuckBorderField field, Component c)
    {
        if(field != null)
        {
            return field.getBorderShape();
        }

        return c.getBounds();
    }

    /**
     * 组件是否获取焦点
     * @param field
     * @return
     */
    private boolean isFoucusGaind(LuckBorderField field)
    {
        if(field != null)
        {
            return field.isFoucusGaind();
        }

        return false;
    }
}
