package freeseawind.lf.basic.checkboxmenuitem;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

/**
 * <pre>
 * LuckCheckBoxMenuItemUI实现类, ，在{@link BasicCheckBoxMenuItemUI}基础上做了如下改变：
 * <li>设置CheckBoxMenuItem为不完全透明</li>
 * <li>设置CheckBoxMenuItem最小高度为20</li>
 * -------------------------------------------------------------------------------
 * LuckCheckBoxMenuItem View UI implementation class,based on
 * {@link BasicCheckBoxMenuItemUI} made the following changes:
 * <li>Set CheckBoxMenuItem to be not completely transparent</li>
 * <li>Set the CheckBoxMenuItem minimum height to 20</li>
 * </pre>
 *
 * @see LuckCheckboxMenuItemUIBundle
 * @see LuckCheckboxIcon
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckCheckBoxMenuItemUI();
    }

    /**
     * <pre>
     * 重写方法，设置菜单的最小高度为20， 否则会出现菜单项大小不一致的情况。
     *
     * Rewrite method, set the minimum height of the menu is 20, otherwise the
     * menu item size will be inconsistent situation.
     * </pre>
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
