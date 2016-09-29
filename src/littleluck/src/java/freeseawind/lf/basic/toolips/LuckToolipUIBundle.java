package freeseawind.lf.basic.toolips;

import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * LuckToolipUI资源绑定类
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckToolipUIBundle extends LuckResourceBundle
{
    /**
     * JToolip边框属性key
     */
    public static final String BORDER = "ToolTip.border";
    
    /**
     * [自定义属性]JToolip背景图片属性key
     */
    public static final String BGIMG = "ToolTip.bgImg";

    @Override
    protected void installBorder()
    {
        // 由于不能直接在JToolip上加半透明阴影边框, 这里的边框只作为内边框使用
        // 间距根据背景图片阴影来设置
        UIManager.put(BORDER, new EmptyBorder(new Insets(6, 8, 8, 8)));
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(BGIMG, LuckRes.getImage("toolip/shadow_border.9.png"));
    }
}
