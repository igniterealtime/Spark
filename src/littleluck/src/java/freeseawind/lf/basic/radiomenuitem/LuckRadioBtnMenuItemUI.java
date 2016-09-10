package freeseawind.lf.basic.radiomenuitem;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

/**
 * RadioButtonMenuItemUI实现类, 设置组件为不完全透明, 组件最小高度为20
 * <p>
 * 另请参见 {@link LuckRadioBtnMenuItemUIBundle}
 * </p>
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRadioBtnMenuItemUI extends BasicRadioButtonMenuItemUI
{
    public static ComponentUI createUI(JComponent b)
    {
        return new LuckRadioBtnMenuItemUI();
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
