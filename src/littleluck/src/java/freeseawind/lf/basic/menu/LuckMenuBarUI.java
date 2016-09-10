package freeseawind.lf.basic.menu;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

/**
 * MenuBarUI实现类, 设置组件为不完全透明, JMenuBar的外观展示通过 {@link LuckMenuUIBundle}来配置
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
