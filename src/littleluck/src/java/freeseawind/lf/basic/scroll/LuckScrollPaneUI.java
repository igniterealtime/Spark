package freeseawind.lf.basic.scroll;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * <p>
 * ScrollPaneUI实现类，设置组件为不完全透明。
 * </p>
 *
 * <p>
 * The ScrollPaneUI implementation class sets the component to be completely
 * transparent.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckScrollPaneUI extends BasicScrollPaneUI
{
    public static ComponentUI createUI(JComponent x)
    {
        return new LuckScrollPaneUI();
    }

    protected void installDefaults(JScrollPane scrollpane)
    {
        super.installDefaults(scrollpane);

        LookAndFeel.installProperty(scrollpane, "opaque", Boolean.FALSE);
    }
}
