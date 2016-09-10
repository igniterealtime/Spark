package freeseawind.lf.basic.toolips;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * ToolTipUI实现类, 设置UI组件为不完全透明, 使用点九图作为背景和边框
 * <p>
 * 另请参见 {@link LuckToolipUIBundle}
 * </p>
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckToolTipUI extends BasicToolTipUI
{
    private static LuckToolTipUI sharedInstance = new LuckToolTipUI();

    // 由于JToolip是共享的, 所以这里这里只需要初始化一次
    private static SwingNinePatch np = new SwingNinePatch(
            (BufferedImage) UIManager.get(LuckToolipUIBundle.BGIMG));

    public static ComponentUI createUI(JComponent c)
    {
        return sharedInstance;
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
    }

    public void installDefaults(JComponent c)
    {
        super.installDefaults(c);

        c.setOpaque(false);
    }

    public void paint(Graphics g, JComponent c)
    {
        np.drawNinePatch((Graphics2D) g, 0, 0, c.getWidth(), c.getHeight());

        super.paint(g, c);
    }
}
