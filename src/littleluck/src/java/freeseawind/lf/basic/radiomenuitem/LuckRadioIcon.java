package freeseawind.lf.basic.radiomenuitem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;

import freeseawind.lf.basic.togglebutton.LuckToggleButtonUIBundle;

/**
 * <p>单选框图标实现类,Java2D绘制的单选按钮。</p>
 *
 * <p>Radio button icon implement class, use Java2D drawn radio button.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRadioIcon implements Icon, Serializable
{
    private static final long serialVersionUID = -6004636765123462175L;

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        AbstractButton cb = (AbstractButton) c;

        ButtonModel model = cb.getModel();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        y = y + 2;

        drawOval(g2d, x, y, model.isArmed());

        if(model.isSelected())
        {
            fillOval(g2d, x, y, model.isArmed());
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    protected void drawOval(Graphics2D g2d, int x, int y,  boolean isFocus)
    {
        if(isFocus)
        {
            g2d.setColor(Color.WHITE);
        }
        else
        {
            g2d.setColor(UIManager.getColor(LuckToggleButtonUIBundle.RADIO_FOCUS_COLOR));
        }

        g2d.drawOval(x, y, getIconWidth() - 3, getIconHeight() - 3);
    }

    protected void fillOval(Graphics2D g2d, int x, int y, boolean isFocus)
    {
        if(isFocus)
        {
            g2d.setColor(Color.WHITE);
        }
        else
        {
            g2d.setColor(UIManager.getColor(LuckToggleButtonUIBundle.RADIO_CHECK_COLOR));
        }

        g2d.fillOval(x + 4, y + 4 , getIconWidth() - 10, getIconHeight() - 10);
    }

    public int getIconWidth()
    {
        return 13;
    }

    public int getIconHeight()
    {
        return 13;
    }
}
