package freeseawind.lf.basic.menu;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * <p>
 * MenuUI实现类, 组件最小高度为20。
 * </p>
 *
 * <p>
 * MenuUI implementation class, the menu minimum height of 20.
 * </p>
 *
 * @see LuckMenuUIBundle
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMenuUI extends BasicMenuUI
{
    public static ComponentUI createUI(JComponent x)
    {
        return new LuckMenuUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
    }

    protected void installDefaults()
    {
        super.installDefaults();

        LookAndFeel.installProperty(menuItem, "opaque", Boolean.FALSE);
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
