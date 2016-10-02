package freeseawind.lf.basic.scroll;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import freeseawind.lf.utils.LuckUtils;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * <p>
 * ScrollBarUI实现类，设置组件为不完全透明， 使用点九图绘制滑块，取消了滑道和方向按钮的绘制。
 * </p>
 *
 * <p>
 * The ScrollBarUI implementation class draws the thumb with a nine patch image and
 * skips the track and direction buttons.
 * Set the component to be not completely transparent.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckScrollBarUI extends BasicScrollBarUI
{
    private SwingNinePatch np;

    // scrollbarWdith是jdk1.7以后才使用的属性, 这里做下兼容
    // ScrollbarWdith jdk1.7 later is the use of the attributes here to do the
    // next compatibility.
    private int width;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckScrollBarUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        np = LuckUtils.createNinePatch(LuckScrollUIBundle.SCROLLBAR_THUMBIMG);

        width = UIManager.getInt(LuckScrollUIBundle.SCROLLBAR_WIDTH);
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        np = null;
    }

    protected void installDefaults()
    {
        super.installDefaults();

        LookAndFeel.installProperty(scrollbar, "opaque", Boolean.FALSE);
    }

    /**
     * <p>设置按钮宽和高为0, 达到隐藏该按钮的效果。</p>
     *
     * <p>Set the button width and height to 0, to hide the button.</p>
     */
    protected JButton createDecreaseButton(int orientation)
    {
        JButton btn = new JButton();

        btn.setVisible(false);

        btn.setPreferredSize(new Dimension(0, 0));

        return btn;
    }

    /**
     * <p>设置按钮宽和高为0, 达到隐藏该按钮的效果。</p>
     *
     * <p>Set the button width and height to 0, to hide the button.</p>
     */
    protected JButton createIncreaseButton(int orientation)
    {
        JButton btn = new JButton();

        btn.setVisible(false);

        btn.setPreferredSize(new Dimension(0, 0));

        return btn;
    }

    /**
     * <p>绘制滑道的方法, 这里屏蔽绘制滑道的实现。</p>
     *
     * <p>Sketch the method, here masking the realization of the track chute.</p>
     */
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {

    }

    /**
     * <p>使用点九图绘制滑块。</p>
     *
     * <p>use nine patch image to draw the thumb.</p>
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

        // 为了美观，这里和内容面板保持一个像素的间距。
        // For aesthetic reasons, this and the content panel maintain a pixel pitch.
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
     * <p>获取Scroll bar大小。</p>
     *
     * <p>Gets the Scrollbar size.</p>
     */
    public Dimension getPreferredSize(JComponent c)
    {
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL)
                ? new Dimension(width, 48)
                : new Dimension(48, width);
    }
}
