package freeseawind.lf.basic.menu;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.UIManager;

import freeseawind.lf.basic.checkboxmenuitem.LuckCheckboxIcon;

/**
 * <p>JMenu弹出子菜单箭头按钮图标实现类。</p>
 *
 * <p>JMenu Arrow icon implementation class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckArrowIcon extends LuckCheckboxIcon
{
    private static final long serialVersionUID = 6698085109009711664L;

    public Image getPreImg(Component c, ButtonModel model)
    {
        JMenu menu = (JMenu) c;

        if (menu.getItemCount() > 0)
        {
            if (model.isSelected())
            {
                return getRollverImg();
            }
            else
            {
                return getNormalImg();
            }
        }

        return null;
    }

    /**
     * Gets the selected focus image
     *
     * @return <code>Image</code>
     */
    public Image getRollverImg()
    {
        return (Image) UIManager.get(LuckMenuUIBundle.ARROW_ROLLVER_IMG);
    }

    /**
     * Gets the selected image
     *
     * @return <code>Image</code>
     */
    public Image getNormalImg()
    {
        return (Image) UIManager.get(LuckMenuUIBundle.ARROW_NORMAL_IMG);
    }

    public int getIconWidth()
    {
        return 6;
    }

    public int getIconHeight()
    {
        return 10;
    }
}
