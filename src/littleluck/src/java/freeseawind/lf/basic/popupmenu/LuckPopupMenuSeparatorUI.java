package freeseawind.lf.basic.popupmenu;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

/**
 * PopupMenuSeparatorUI实现类，默认是保持上下各1个像素的间距，这里设置为0。
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckPopupMenuSeparatorUI();
    }

    @Override
    public void paint(Graphics g, JComponent c)
    {
        Dimension s = c.getSize();

        g.setColor(UIManager.getColor(LuckPopupMenuUIBundle.SEPEREATOR_COLOR));

        g.drawLine(0, 0, s.width, 0);
    }

    @Override
    public Dimension getPreferredSize(JComponent c)
    {
        return new Dimension(0, 1);
    }
}
