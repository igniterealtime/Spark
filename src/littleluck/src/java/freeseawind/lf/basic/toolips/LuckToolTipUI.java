package freeseawind.lf.basic.toolips;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

import freeseawind.lf.utils.LuckUtils;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * <p>
 * ToolTipUI实现类，设置组件为不完全透明，使用点九图作为背景， 使用空边框来保持内容和阴影之间的间距。
 * </p>
 *
 * <p>
 * The ToolTipUI implementation class,Set the component to be not completely
 * transparent, using a point nine as the background, uses an empty border to
 * keep the space between the content and the shadow.
 * </p>
 *
 * @see LuckToolipUIBundle
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckToolTipUI extends BasicToolTipUI
{
    private static LuckToolTipUI sharedInstance = new LuckToolTipUI();

    private SwingNinePatch np;

    public static ComponentUI createUI(JComponent c)
    {
        return sharedInstance;
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        c.setOpaque(false);

        if(np == null)
        {
            np = LuckUtils.createNinePatch(LuckToolipUIBundle.BGIMG);
        }
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        np = null;
    }

    public void paint(Graphics g, JComponent c)
    {
        if(np != null)
        {
            np.drawNinePatch((Graphics2D) g, 0, 0, c.getWidth(), c.getHeight());
        }

        super.paint(g, c);
    }
}
