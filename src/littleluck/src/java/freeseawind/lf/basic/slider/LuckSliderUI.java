package freeseawind.lf.basic.slider;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * SliederUI实现类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckSliderUI extends BasicSliderUI
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
        super(b);
    }

    public static ComponentUI createUI(JComponent b)
    {
        return new LuckSliderUI((JSlider) b);
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        horizontalNp = new SwingNinePatch((BufferedImage) UIManager
                .get(LuckSliderUIBundle.TRACK_HORIZONTAL));

        horizontalHighlightNp = new SwingNinePatch((BufferedImage) UIManager
                .get(LuckSliderUIBundle.TRACK_HORIZONTAL_H));

        verticalNp = new SwingNinePatch((BufferedImage) UIManager
                .get(LuckSliderUIBundle.TRACK_VERTICAL));

        verticalHighlightNp = new SwingNinePatch((BufferedImage) UIManager
                .get(LuckSliderUIBundle.TRACK_VERTICAL_H));

        horizontaltThumbImg = (BufferedImage) UIManager
                .get(LuckSliderUIBundle.THUMB_HORIZONTAL);

        verticalThumbImg = (BufferedImage) UIManager
                .get(LuckSliderUIBundle.THUMB_VERTICAL);

        size = UIManager.getInt(LuckSliderUIBundle.TRACK_SIZE);
    }

    public void paintTrack(Graphics g)
    {
        Rectangle trackBounds = trackRect;

        if (slider.getOrientation() == JSlider.HORIZONTAL)
        {
            int cy = (trackBounds.height / 2) - 2;

            g.translate(trackBounds.x, trackBounds.y + cy);

            horizontalNp.drawNinePatch((Graphics2D) g, 0, 0, trackBounds.width, size);

            horizontalHighlightNp.drawNinePatch((Graphics2D) g, 0, 0, thumbRect.x - 2, size);

            g.translate(-trackBounds.x, -(trackBounds.y + cy));
        }
        else
        {
            int cx = (trackBounds.width / 2) - 2;

            g.translate(trackBounds.x + cx, trackBounds.y);

            verticalNp.drawNinePatch((Graphics2D) g, 0, 0, size, trackBounds.height);

            verticalHighlightNp.drawNinePatch((Graphics2D) g, 0, thumbRect.y, size, trackBounds.height - thumbRect.y);

            g.translate(-(trackBounds.x + cx), - trackBounds.y);
        }
    }

    public void paintThumb(Graphics g)
    {
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
}
