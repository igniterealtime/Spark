package freeseawind.lf.basic.menu;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

/**
 * <p>
 * MenuBarUI实现类, 设置组件为不完全透明。
 * </p>
 *
 * <p>
 * MenuBarUI implementation class,set the component is not completely
 * transparent.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckMenuBarUI extends BasicMenuBarUI
{
    public static ComponentUI createUI(JComponent x)
    {
        return new LuckMenuBarUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        LookAndFeel.installProperty(menuBar, "opaque", Boolean.FALSE);
    }
}
