package freeseawind.lf.basic.slider;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSliderUI;

import freeseawind.lf.utils.LuckUtils;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * <p>
 * SliederUI实现类, 使用点九图来作为滑到和进度背景。
 * </p>
 * 
 * <p>
 * SliederUI implement class, use nine patch image as a slide background.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckSliderUI extends MetalSliderUI
{
    private SwingNinePatch horizontalNp;
    private SwingNinePatch horizontalHighlightNp;
    private SwingNinePatch verticalNp;
    private SwingNinePatch verticalHighlightNp;
    private BufferedImage horizontaltThumbImg;
    private BufferedImage verticalThumbImg;
    private int size;

    public LuckSliderUI(JSlider b)
    {
        super();
    }

    public static ComponentUI createUI(JComponent b)
    {
        return new LuckSliderUI((JSlider) b);
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        size = UIManager.getInt(LuckSliderUIBundle.TRACK_SIZE);
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        // release resources
        horizontalNp = null;
        
        horizontalHighlightNp = null;
        
        verticalNp = null;
        
        verticalHighlightNp = null;
        
        horizontaltThumbImg = null;
        
        verticalThumbImg = null;
    }

    @Override
    public void paintTrack(Graphics g)
    {
        // 如果还未初始化图片资源，则先初始化。
        // The initialization of the resources required.
        initRes(slider.getOrientation());
        
        Rectangle trackBounds = trackRect;
        
        Graphics2D g2d = (Graphics2D) g;

        if (slider.getOrientation() == JSlider.HORIZONTAL)
        {
            // 垂直居中，偏下两个像素。
            // Vertical center, under two partial pixels.
            int cy = (trackBounds.height / 2) - 2;

            g.translate(trackBounds.x, trackBounds.y + cy);

            horizontalNp.drawNinePatch(g2d, 0, 0, trackBounds.width, size);

            horizontalHighlightNp.drawNinePatch(g2d, 0, 0, thumbRect.x - 2, size);

            g.translate(-trackBounds.x, -(trackBounds.y + cy));
        }
        else
        {
            // 水平居中偏右连个像素。
            // Horizontal center-right two pixels.
            int cx = (trackBounds.width / 2) - 2;

            g.translate(trackBounds.x + cx, trackBounds.y);

            verticalNp.drawNinePatch(g2d, 0, 0, size, trackBounds.height);

            verticalHighlightNp.drawNinePatch(g2d, 0, thumbRect.y, size, trackBounds.height - thumbRect.y);

            g.translate(-(trackBounds.x + cx), -trackBounds.y);
        }
    }

    public void paintThumb(Graphics g)
    {
        // 如果还未初始化图片资源，则先初始化。
        // The initialization of the resources required.
        initRes(slider.getOrientation());

        Rectangle knobBounds = thumbRect;

        g.translate(knobBounds.x, knobBounds.y);

        if (slider.getOrientation() == JSlider.HORIZONTAL)
        {
            g.drawImage(horizontaltThumbImg, 0, 0, null);
        }
        else
        {
            g.drawImage(verticalThumbImg, 0, 0, null);
        }

        g.translate(-knobBounds.x, -knobBounds.y);
    }

    /**
     * <p>初始化点九图片资源。</p>
     * 
     * <p> initialization nine patch image resource.</p>
     * 
     * @param orientation
     */
    protected void initRes(int orientation)
    {
        if (orientation == JSlider.HORIZONTAL)
        {
            initHorizontalRes();
        }
        else
        {
            initVerticalRes();
        }
    }

    /**
     * <p> 初始化水平滑块图片资源。</p>
     * 
     * <p> initialization horizontal slider's nine patch image resource.</p>
     */
    protected void initHorizontalRes()
    {
        if (horizontalNp == null)
        {
            horizontalNp = LuckUtils.createNinePatch(LuckSliderUIBundle.TRACK_HORIZONTAL);
        }

        if (horizontalHighlightNp == null)
        {
            horizontalHighlightNp = LuckUtils.createNinePatch(LuckSliderUIBundle.TRACK_HORIZONTAL_H);
        }

        if (horizontaltThumbImg == null)
        {
            horizontaltThumbImg = LuckUtils.getUiImage(LuckSliderUIBundle.THUMB_HORIZONTAL);
        }
    }

    /**
     * <p> 初始化垂直滑块图片资源。</p>
     * 
     * <p> initialization vertical slider's nine patch image resource.</p>
     */
    protected void initVerticalRes()
    {
        if (verticalNp == null)
        {
            verticalNp = LuckUtils.createNinePatch(LuckSliderUIBundle.TRACK_VERTICAL);
        }

        if (verticalHighlightNp == null)
        {
            verticalHighlightNp = LuckUtils.createNinePatch(LuckSliderUIBundle.TRACK_VERTICAL_H);
        }

        if (verticalThumbImg == null)
        {
            verticalThumbImg = LuckUtils.getUiImage(LuckSliderUIBundle.THUMB_VERTICAL);
        }
    }
}
