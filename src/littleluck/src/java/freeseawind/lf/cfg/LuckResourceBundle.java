package freeseawind.lf.cfg;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.IconUIResource;

import freeseawind.lf.utils.LuckRes;

/**
 * UI resource bundle class.
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public abstract class LuckResourceBundle
{
    public final void installDefaults(UIDefaults table)
    {
        installColor(table);

        installFont(table);

        installBorder(table);

        installListener(table);

        loadImages(table);

        installOther(table);
    }
    
    /**
     * <p>卸载观感时移除资源。</p>
     * 
     * <p>when uninstall LookAndFeel remove resource. </p>
     */
    public void uninitialize()
    {
        
    }

    /**
     * 初始化颜色配置
     */
    protected void installColor(UIDefaults table)
    {

    }

    /**
     * 初始化字体配置
     */
    protected void installFont(UIDefaults table)
    {

    }

    /**
     * 初始化边框配置
     */
    protected void installBorder(UIDefaults table)
    {

    }

    /**
     * 初始化监听器
     */
    protected void installListener(UIDefaults table)
    {

    }

    /**
     * 初始化图片资源
     */
    protected void loadImages(UIDefaults table)
    {

    }

    /**
     * 初始化其它
     */
    protected void installOther(UIDefaults table)
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
    
    protected ColorUIResource getColorRes(int r, int g, int b, int a)
    {
        return new ColorUIResource(new Color(r, g, b, a));
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
