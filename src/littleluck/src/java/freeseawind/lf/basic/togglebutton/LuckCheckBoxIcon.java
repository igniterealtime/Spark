package freeseawind.lf.basic.togglebutton;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;
import static freeseawind.lf.basic.togglebutton.LuckToggleButtonUIBundle.*;

/**
 * <p>复选框图标实现类, 此类参考Beautyeye的实现。</p>
 *
 * <p>CheckBoxIcon implement class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckCheckBoxIcon implements Icon, Serializable
{
    private static final long serialVersionUID = 2241809293789517288L;

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        AbstractButton cb = (AbstractButton) c;

        ButtonModel model = cb.getModel();

        Image image = null;

        boolean isPressed = (model.isArmed() && model.isPressed());

        boolean isRollver = (model.isRollover() && cb.isRolloverEnabled());

        if(model.isSelected())
        {
            if(isPressed)
            {
                image = (Image) UIManager.get(CHECKBOX_UNPRESSED_IMG);
            }
            else
            {
                image = (Image) UIManager.get(CHECKBOX_PRESSED_IMG);
            }
        }
        else
        {
            if(isRollver && !isPressed)
            {
                image = (Image) UIManager.get(CHECKBOX_ROLLVER_IMG);
            }
            else if(isRollver && isPressed)
            {
                image = (Image) UIManager.get(CHECKBOX_UNROLLVER_IMG);
            }
            else
            {
                image = (Image) UIManager.get(CHECKBOX_NORMAL_IMG);
            }
        }

        if(image != null)
        {
            g.drawImage(image, x, y, getIconWidth(), getIconHeight(), null);
        }
    }

    /**
     * Gets icon width.
     */
    public int getIconWidth()
    {
        return 16;
    }

    /**
     * Gets icon height.
     */
    public int getIconHeight()
    {
        return 16;
    }
}
