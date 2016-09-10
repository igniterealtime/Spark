package freeseawind.lf.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.border.AbstractBorder;

import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * 点九边框，使用点九图片绘制边框。
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckNinePatchBorder extends AbstractBorder
{
    private static final long serialVersionUID = -7721953876241263779L;
    private Insets insets;
    private SwingNinePatch np = null;

    public LuckNinePatchBorder(Insets insets, BufferedImage img)
    {
        this.insets = insets;

        this.np = new SwingNinePatch(img);
    }

    public LuckNinePatchBorder(Insets insets, SwingNinePatch np)
    {
        this.insets = insets;

        this.np = np;
    }

    public void paintBorder(Component c,
                            Graphics g,
                            int x,
                            int y,
                            int width,
                            int height)
    {
        np.drawNinePatch((Graphics2D) g, x, y, width, height);
    }

    public Insets getBorderInsets(Component c)
    {
        return insets;
    }

    public Insets getBorderInsets(Component c, Insets insets)
    {
        return this.insets;
    }
}
