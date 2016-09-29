package freeseawind.lf.basic.table;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;

/**
 * TableUI实现类
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
    
    public void installUI(JComponent c)
    {
        super.installUI(c);
    }
}
