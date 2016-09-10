package freeseawind.lf.basic.scroll;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * ScrollBarUI实现类， 使用点九图绘制滑块，取消了滑道和方向按钮的绘制。
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckScrollBarUI extends BasicScrollBarUI
{
    private SwingNinePatch np;

    // scrollbarWdith是jdk1.7以后才使用的属性, 这里做下兼容
    private int width;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckScrollBarUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        np = new SwingNinePatch((BufferedImage) UIManager.get(LuckScrollUIBundle.SCROLLBAR_THUMBICON));

        width = UIManager.getInt(LuckScrollUIBundle.SCROLLBAR_WIDTH);
    }

    protected void installDefaults()
    {
        super.installDefaults();

        LookAndFeel.installProperty(scrollbar, "opaque", Boolean.FALSE);
    }

    /**
     * 设置按钮宽和高为0, 达到隐藏该按钮的效果
     */
    protected JButton createDecreaseButton(int orientation)
    {
        JButton btn = new JButton();

        btn.setVisible(false);

        btn.setPreferredSize(new Dimension(0, 0));

        return btn;
    }

    /**
     * 设置按钮宽和高为0, 达到隐藏该按钮的效果
     */
    protected JButton createIncreaseButton(int orientation)
    {
        JButton btn = new JButton();

        btn.setVisible(false);

        btn.setPreferredSize(new Dimension(0, 0));

        return btn;
    }

    /**
     * 绘制滑道的方法, 这里屏蔽绘制滑道的实现
     */
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {

    }

    /**
     * 使用点九图绘制滑块
     */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
        {
            return;
        }

        int w = thumbBounds.width;
        int h = thumbBounds.height;

        g.translate(thumbBounds.x, thumbBounds.y);

        if(scrollbar.getOrientation() == JScrollBar.VERTICAL)
        {
            w = w - 1;
        }
        else if(scrollbar.getOrientation() == JScrollBar.HORIZONTAL)
        {
            h = h - 1;
        }

        if (np != null)
        {
            np.drawNinePatch((Graphics2D) g, 0, 0, w, h);
        }

        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    /**
     * 获取Scrollbar大小
     */
    public Dimension getPreferredSize(JComponent c)
    {
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL)
                ? new Dimension(width, 48)
                : new Dimension(48, width);
    }
}
