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
 * 一种反走样的绘图工具类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckOpaquePainter
{
    /**
     * 如果面板是半透明则使用父面板的方式进行绘制，否则绘制一个完全不透明的背景，防止字体走样
     *
     * @param g 绘图画笔对象信息
     * @param component 需要绘制的容器类
     * @param canvas 调用父类绘制方法的回调接口
     * @param <T> <code> JComponent </code> 子类
     * @param <E> 绘图回调接口实现类
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
     * 如果面板是半透明则使用父面板的方式进行绘制，否则绘制一个完全不透明的背景，防止字体走样
     *
     * @param g 绘图画笔对象信息
     * @param component 需要绘图的容器对象信息
     * @param canvas 绘图回调接口
     * @param shape 需要绘制的形状, 如果是规则面板则这里传NUL
     * @param <T> <code> JComponent </code> 子类
     * @param <E> 绘图回调接口实现类
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
     * 在完全不透明的背景上绘制组件，防止字体走样
     *
     * @param g 绘图画笔对象信息
     * @param component 需要绘图的容器对象信息
     * @param canvas 绘图回调接口
     * @param shape 需要绘制的形状, 如果是规则面板则这里传NULL
     * @param <T> <code> JComponent </code> 子类
     * @param <E> 绘图回调接口实现类
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
