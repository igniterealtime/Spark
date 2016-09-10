package freeseawind.lf.basic.togglebutton;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;

public class LuckCheckBoxUI extends MetalCheckBoxUI
{
    public static ComponentUI createUI(JComponent b)
    {
        return new LuckCheckBoxUI();
    }
}
