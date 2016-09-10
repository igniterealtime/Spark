package freeseawind.lf.cfg;

import java.awt.Color;

import javax.swing.UIManager;

import freeseawind.lf.utils.LuckRes;

/**
 * 全局资源绑定类
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckGlobalBundle extends LuckResourceBundle
{
    /**
     *  [自定义属性]应用默认图标
     */
    public static final String APPLICATION_ICON = "Application.icon";

    /**
     *  [自定义属性]应用默认标题
     */
    public static final String APPLICATION_TITLE = "Application.title";

    /**
     *[自定义属性] 完全透明
     */
    public static final String TRANSLUCENT_COLOR = "translucent.color";

    /**
     * 面板背景颜色属性key
     */
    public static final String PANEL_BACKGROUND = "Panel.background";

    @Override
    protected void installColor()
    {
        UIManager.put(TRANSLUCENT_COLOR, new Color(0, 0, 0, 0));
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(APPLICATION_ICON, getIconRes("frame/default_frame_icon.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(APPLICATION_TITLE, LuckRes.getString("default.title"));

        UIManager.put(PANEL_BACKGROUND, Color.WHITE);
        
        UIManager.put("ColorChooserUI.background", Color.WHITE);
    }
}
