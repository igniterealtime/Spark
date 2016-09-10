package freeseawind.lf.basic.menu;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.UIManager;

import freeseawind.lf.basic.checkboxmenuitem.LuckCheckboxIcon;

/**
 * JMenu弹出子菜单箭头按钮图标实现类
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
     * 获取选中时焦点图片
     *
     * @return <code>Image</code>
     */
    public Image getRollverImg()
    {
        return (Image) UIManager.get(LuckMenuUIBundle.ARROW_ROLLVER_ICON);
    }

    /**
     * 获取选中时图片
     *
     * @return <code>Image</code>
     */
    public Image getNormalImg()
    {
        return (Image) UIManager.get(LuckMenuUIBundle.ARROW_NORMAL_ICON);
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
