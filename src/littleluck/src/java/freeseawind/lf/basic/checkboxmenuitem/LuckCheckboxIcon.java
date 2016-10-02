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
 * <pre>
 * CheckBoxMenuItem复选框图标实现类, 根据CheckBoxMenuItem的状态绘制相应的图标,
 * 当前图标大小为16x16。
 *
 * CheckBoxMenuItem Check box icon implementation class, according to the state of
 * CheckBoxMenuItem draw the corresponding icon,the current icon size is 16x16.
 * </pre>
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
     * <pre>
     * 根据按钮状态, 获取当前状态下图片信息。
     *
     * According to the button state, access to the current
     * state of the picture information.
     * </pre>
     *
     * @param c <code>CheckBoxMenuItem</code> object.
     * @param model <code>ButtonModel</code>
     * @return <code>Image</code> when is selected return current image, otherwise return null.
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
     * <pre>
     * 获取鼠标经过时的图片信息。
     *
     * Get the picture information when mouse over.
     * </pre>
     *
     * @return <code>Image</code>
     */
    public Image getRollverImg()
    {
        return (Image) UIManager.get(LuckCheckboxMenuItemUIBundle.ROLLVER_IMG);
    }

    /**
     * <pre>
     * 获取非选中状态时的图片信息。
     *
     * Gets the picture information when unselected.
     * </pre>
     *
     * @return <code>Image</code>
     */
    public Image getNormalImg()
    {
        return (Image) UIManager.get(LuckCheckboxMenuItemUIBundle.NORMAL_IMG);
    }

    /**
     * <pre>
     * 获取图片宽度
     *
     * Gets the width of the image
     * </pre>
     */
    public int getIconWidth()
    {
        return 16;
    }

    /**
     * <pre>
     * 获取图片高度
     *
     * Gets the height of the image
     * <pre>
     */
    public int getIconHeight()
    {
        return 16;
    }
}
