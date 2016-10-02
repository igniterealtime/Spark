package freeseawind.lf.canvas;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;


/**
 * Opaque draw class.
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckOpaquePainter
{
    /**
     * If the panel is translucent parent panel is used to draw the way,
     * otherwise draw a completely opaque background to prevent aliasing font.
     *
     * @param g Drawing Canvas object information.
     * @param component 
     * @param canvas call parent paint interface.
     * @param <T> <code> JComponent </code> 子类
     * @param <E> canvas callback interface.
     *
     */
    public <T extends JComponent, E extends LuckCanvas> void paintOpaque(Graphics g,
                                                                         T component,
                                                                         E canvas)
    {
    	if(!component.isOpaque())
    	{
    	    canvas.drawComponent(g, component);

    		return;
    	}

    	paintOpaque(g, component, canvas, null);
	}

    /**
     * If the panel is translucent parent panel is used to draw the way,
     * otherwise draw a completely opaque background to prevent aliasing font.
     * 
     * @param g Drawing Canvas object information.
     * @param component 
     * @param canvas call parent paint interface.
     * @param shape pane shape.
     * @param <T> <code> JComponent </code> sub class.
     * @param <E> canvas callback interface.
     */
    public <T extends JComponent, E extends LuckCanvas> void paintOpaqueShap(Graphics g,
                                                                             T component,
                                                                             E canvas,
                                                                             Shape shape)
    {
        if(!component.isOpaque())
        {
            canvas.drawComponent(g, component);

            return;
        }

        paintOpaque(g, component, canvas, shape);
    }

    /**
    *
    * @param g Drawing Canvas object information
    * @param component 
    * @param canvas call parent paint interface.
    * @param shape pane shape.
    * @param <T> <code> JComponent </code> sub class.
    * @param <E> canvas callback interface.
    */
    public <T extends JComponent, E extends LuckCanvas> void paintOpaque(Graphics g,
                                                                         T component,
                                                                         E canvas,
                                                                         Shape shape)
    {
        // 计算宽高和起始坐标
        Insets insets = component.getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = component.getWidth() - insets.left - insets.right;
        int height = component.getHeight() - insets.top - insets.bottom;

        // 设置画布背景
        BufferedImage contentImage = new BufferedImage(width, height, Transparency.OPAQUE);

        Graphics2D contentG2d = contentImage.createGraphics();

        // 绘制边框和其它
        canvas.drawComponent(contentG2d, component);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(shape != null)
        {
            g2d.setComposite(AlphaComposite.Src);
            g2d.fill(shape);
            g2d.setComposite(AlphaComposite.SrcAtop);
        }

        g.drawImage(contentImage, x, y, width, height, null);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 释放资源
        contentG2d.dispose();
    }
}
