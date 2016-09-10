package freeseawind.lf.basic.menu;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * MenuUI实现类, 设置组件为不完全透明, 组件最小高度为20, JMenu的外观展示通过 {@link LuckMenuUIBundle}来配置
 * <p>
 * 另请参见 {@link LuckMenuUIBundle}
 * </p>
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
     * 重写方法，设置菜单的最小高度为20， 否则会出现菜单项大小不一致的情况。
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
