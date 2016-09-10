package freeseawind.lf.cfg;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.IconUIResource;

import freeseawind.lf.utils.LuckRes;

/**
 * UI初始化资源绑定类
 *
 * @author freeseawind@github
 *
 */
public abstract class LuckResourceBundle
{
    public final void installDefaults()
    {
        installColor();

        installFont();

        installBorder();

        installListener();

        loadImages();

        installOther();
    }

    /**
     * 初始化颜色配置
     */
    protected void installColor()
    {

    }

    /**
     * 初始化字体配置
     */
    protected void installFont()
    {

    }

    /**
     * 初始化边框配置
     */
    protected void installBorder()
    {

    }

    /**
     * 初始化监听器
     */
    protected void installListener()
    {

    }

    /**
     * 初始化图片资源
     */
    protected void loadImages()
    {

    }

    /**
     * 初始化其它
     */
    protected void installOther()
    {

    }

    protected IconUIResource getIconRes(String key)
    {
        return new IconUIResource(new ImageIcon(LuckRes.getImage(key)));
    }

    protected IconUIResource getIconRes(Icon icon)
    {
        return new IconUIResource(icon);
    }

    protected ColorUIResource getColorRes(int r, int g, int b)
    {
        return new ColorUIResource(r, g, b);
    }

    protected ColorUIResource getColorRes(Color color)
    {
        return new ColorUIResource(color);
    }

    protected DimensionUIResource getDimensionRes(int w, int h)
    {
        return new DimensionUIResource(w, h);
    }

    protected BorderUIResource getBorderRes(Border border)
    {
        return new BorderUIResource(border);
    }
}
