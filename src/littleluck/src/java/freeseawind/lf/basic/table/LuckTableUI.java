package freeseawind.lf.basic.table;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;

/**
 * <p>TableUI实现类。</p>
 *
 * <p>TableUI implement class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTableUI extends BasicTableUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckTableUI();
    }
}
