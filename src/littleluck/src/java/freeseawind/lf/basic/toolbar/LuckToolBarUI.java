package freeseawind.lf.basic.toolbar;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

/**
 * <p>
 * LuckToolBarUI实现类，设置组件为不完全透明。
 * </p>
 *
 * <p>
 * A LuckToolBarUI implementation class, setting the component is not completely
 * transparent.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckToolBarUI extends BasicToolBarUI
{
    public static ComponentUI createUI( JComponent c )
    {
        return new LuckToolBarUI();
    }

    public void installUI( JComponent c )
    {
        super.installUI(c);

        LookAndFeel.installProperty(c, "opaque", Boolean.FALSE);
    }
}
