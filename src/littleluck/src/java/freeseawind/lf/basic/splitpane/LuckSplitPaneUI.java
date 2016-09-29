package freeseawind.lf.basic.splitpane;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckSplitPaneUI extends BasicSplitPaneUI
{
    public static ComponentUI createUI(JComponent x)
    {
        return new LuckSplitPaneUI();
    }

    public BasicSplitPaneDivider createDefaultDivider()
    {
        return new LuckSplitPaneDivider(this);
    }
}
