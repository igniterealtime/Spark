package freeseawind.lf.basic.splitpane;

import java.awt.Graphics;

import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class LuckSplitPaneDivider extends BasicSplitPaneDivider
{
    private static final long serialVersionUID = 4593944441869463007L;

    public LuckSplitPaneDivider(BasicSplitPaneUI ui)
    {
        super(ui);
        
        setBorder(null);
    }
    
    public void paint(Graphics g)
    {
        super.paint(g);
    }
}
