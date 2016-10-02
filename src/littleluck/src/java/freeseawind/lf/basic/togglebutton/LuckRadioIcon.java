package freeseawind.lf.basic.togglebutton;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * <p>单选框图标实现类。</p>
 * 
 * <p>RadioIcon implement class.</p>
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

        boolean isPressed = (model.isArmed() && model.isPressed());

        boolean isRollver = (model.isRollover() && cb.isRolloverEnabled());

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawOval(g2d, x, y, (isRollver || isPressed));

        if(model.isSelected())
        {
            fillOval(g2d, x, y);
        }
        else if(isRollver && isPressed)
        {
            drawOvalShadow(g2d, x, y);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * 
     * @param g2d
     * @param x
     * @param y
     * @param isFocus
     */
    protected void drawOval(Graphics2D g2d, int x, int y,  boolean isFocus)
    {
        if(!isFocus)
        {
            g2d.setColor(UIManager.getColor(LuckToggleButtonUIBundle.RADIO_NORMAL_COLOR));
        }
        else
        {
            g2d.setColor(UIManager.getColor(LuckToggleButtonUIBundle.RADIO_FOCUS_COLOR));
        }

        g2d.drawOval(x + getLeftInset(), y + getTopInset(), getIconWidth() - 3,
                getIconHeight() - 3);
    }

    /**
     * 
     * @param g2d
     * @param x
     * @param y
     */
    protected void drawOvalShadow(Graphics2D g2d, int x, int y)
    {
        g2d.setColor(UIManager.getColor(LuckToggleButtonUIBundle.RADIO_SHADOW_COLOR));

        g2d.drawOval(x + 1 + getLeftInset(), y + 1 + getTopInset(),
                getIconWidth() - 5, getIconHeight() - 5);
    }

    /**
     * draw selected oval
     * 
     * @param g2d
     * @param x
     * @param y
     */
    protected void fillOval(Graphics2D g2d, int x, int y)
    {
        g2d.setColor(UIManager.getColor(LuckToggleButtonUIBundle.RADIO_CHECK_COLOR));

        g2d.fillOval(x + 4 + getLeftInset(), y + 4 + getTopInset(),
                getIconWidth() - 10, getIconHeight() - 10);
    }

    public int getIconWidth()
    {
        return 15;
    }

    public int getIconHeight()
    {
        return 15;
    }

    public int getTopInset()
    {
        return 0;
    }

    public int getLeftInset()
    {
        return 0;
    }
}
