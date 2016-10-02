package freeseawind.lf.basic.menuitem;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * <p>
 * MenuItemUI实现类, 设置组件为不完全透明, 组件最小高度为20。
 * </p>
 *
 * <p>
 * MenuItemUI implementation class, the menu minimum height of 20.
 * </p>
 * 
 * @see LuckMenuItemUIBundle
 *  
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMenuItemUI extends BasicMenuItemUI
{

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckMenuItemUI();
    }

    @Override
    protected void installDefaults()
    {
        super.installDefaults();
    }

    /**
     * <p>
     * 重写方法，设置菜单的最小高度为20， 否则会出现菜单项大小不一致的情况。
     * </p>
     *
     * <p>
     * Rewrite method, set the minimum height of the menu is 20, otherwise the
     * menu item size will be inconsistent situation.
     * </p>
     */
    @Override
    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                 Icon checkIcon,
                                                 Icon arrowIcon,
                                                 int defaultTextIconGap)
    {

        Dimension dimension = super.getPreferredMenuItemSize(c, checkIcon,
                arrowIcon, defaultTextIconGap);

        if (dimension != null && dimension.height < 20)
        {
            dimension.setSize(dimension.width, 20);
        }

        return dimension;
    }
}
