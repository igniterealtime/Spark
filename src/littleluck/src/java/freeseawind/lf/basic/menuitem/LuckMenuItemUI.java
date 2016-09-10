package freeseawind.lf.basic.menuitem;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * MenuItemUI实现类, 设置组件最小高度为20
 * <p>
 * 另请参见 {@link LuckMenuItemUIBundle}
 * </p>
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
     * 重写方法，设置菜单的最小高度为20， 否则会出现菜单项大小不一致的情况。
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
