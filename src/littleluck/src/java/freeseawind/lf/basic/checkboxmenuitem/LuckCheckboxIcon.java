package freeseawind.lf.basic.checkboxmenuitem;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * 复选框图标实现类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckCheckboxIcon implements Icon, Serializable
{
    private static final long serialVersionUID = 2241809293789517288L;

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        AbstractButton btn = (AbstractButton) c;

        ButtonModel model = btn.getModel();

        Image image = getPreImg(c, model);

        if (image != null)
        {
            g.drawImage(image, x, y, getIconWidth(), getIconHeight(), null);
        }
    }

    /**
     * 根据按钮状态, 获取当前状态下图片信息
     *
     * @param c 图标所属容器类
     * @param model 按钮状态模型
     * @return <code>Image</code>，非选中状态返回空，否则返回当前状态下图片信息
     */
    public Image getPreImg(Component c, ButtonModel model)
    {
        if (!model.isSelected())
        {
            return null;
        }

        if (model.isArmed())
        {
            return getRollverImg();
        }
        else
        {
            return getNormalImg();
        }
    }

    /**
     * 获取鼠标经过时图片信息
     *
     * @return <code>Image</code>
     */
    public Image getRollverImg()
    {
        return (Image) UIManager.get(LuckCheckboxMenuItemUIBundle.ROLLVER_ICON);
    }

    /**
     * 获取无状态时图片信息
     *
     * @return <code>Image</code>
     */
    public Image getNormalImg()
    {
        return (Image) UIManager.get(LuckCheckboxMenuItemUIBundle.NORMAL_ICON);
    }

    /**
     * 获取图片宽度
     */
    public int getIconWidth()
    {
        return 16;
    }

    /**
     * 获取图片高度
     */
    public int getIconHeight()
    {
        return 16;
    }
}
