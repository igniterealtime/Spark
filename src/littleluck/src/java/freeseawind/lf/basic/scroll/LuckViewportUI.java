package freeseawind.lf.basic.scroll;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;

/**
 * ViewportUI实现类，设置组件为不完全透明。
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckViewportUI extends BasicViewportUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckViewportUI();
    }

    protected void installDefaults(JComponent c)
    {
        LookAndFeel.installColorsAndFont(c, "Viewport.background",
                "Viewport.foreground", "Viewport.font");

        LookAndFeel.installProperty(c, "opaque", Boolean.FALSE);
    }
}
