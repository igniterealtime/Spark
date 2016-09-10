package freeseawind.lf.basic.button;

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * 按钮资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckButtonUIBundle extends LuckResourceBundle
{
    /**
     * 按钮边框属性key，边框默认间距（3, 14, 3, 15）
     */
    public static final String BORDER = "Button.border";

    /**
     * [自定义属性]按钮颜色配置信息属性key，默认蓝色按钮
     */
    public static final String COLOR_INFO = "Button.colorInfo";
    
    /**
     * [自定义属性]如需要在有图标的时候仍绘制背景, button.putClientProperty(IS_PAINTBG, "");
     */
    public static final String IS_PAINTBG = "Button.isPaintBG";
    

    @Override
    protected void installBorder()
    {
        UIManager.put(BORDER, new EmptyBorder(3, 14, 3, 15));
    }

    @Override
    protected void installColor()
    {
        UIManager.put(COLOR_INFO, LuckButtonColorFactory.getBlueBtnInfo());
    }
}
