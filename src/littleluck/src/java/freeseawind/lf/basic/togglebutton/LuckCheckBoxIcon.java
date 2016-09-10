package freeseawind.lf.basic.togglebutton;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * 复选框图标实现类, 此类参考Beautyeye的实现
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
                image = (Image) UIManager.get(LuckToggleButtonUIBundle.CHECKBOX_UNPRESSED_ICON);
            }
            else
            {
                image = (Image) UIManager.get(LuckToggleButtonUIBundle.CHECKBOX_PRESSED_ICON);
            }
        }
        else
        {
            if(isRollver && !isPressed)
            {
                image = (Image) UIManager.get(LuckToggleButtonUIBundle.CHECKBOX_ROLLVER_ICON);
            }
            else if(isRollver && isPressed)
            {
                image = (Image) UIManager.get(LuckToggleButtonUIBundle.CHECKBOX_UNROLLVER_ICON);
            }
            else
            {
                image = (Image) UIManager.get(LuckToggleButtonUIBundle.CHECKBOX_NORMAL_ICON);
            }
        }

        if(image != null)
        {
            g.drawImage(image, x, y, getIconWidth(), getIconHeight(), null);
        }
    }

    public int getIconWidth()
    {
        return 16;
    }

    public int getIconHeight()
    {
        return 16;
    }
}
