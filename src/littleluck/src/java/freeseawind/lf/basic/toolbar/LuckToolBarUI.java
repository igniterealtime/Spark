package freeseawind.lf.basic.toolbar;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

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
