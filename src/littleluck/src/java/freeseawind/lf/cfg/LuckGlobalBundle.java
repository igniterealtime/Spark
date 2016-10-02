package freeseawind.lf.cfg;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * <p>全局资源绑定类</p>
 * 
 * <p>Global resource bundle.</p>
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckGlobalBundle extends LuckResourceBundle
{
    /**
     * <p><strong>[LittleLuck属性]</strong>窗体默认图标。</p>
     * 
     * <p><strong>[LittLeLuck Attributes]</strong> Default window icon properties.</p>
     */
    public static final String APPLICATION_ICON = "Application.icon";

    /**
     * <p><strong>[LittleLuck属性]</strong>窗体默认标题。</p>
     * 
     * <p><strong>[LittLeLuck Attributes]</strong> Default window title properties.</p>
     */
    public static final String APPLICATION_TITLE = "Application.title";

    /**
     * <p><strong>[LittleLuck属性]</strong>窗体透明颜色。</p>
     * 
     * <p><strong>[LittLeLuck Attributes]</strong> Translucent color properties.</p>
     */
    public static final String TRANSLUCENT_COLOR = "translucent.color";

    /**
     * <p><strong>[LittleLuck属性]</strong>面板背景颜色属性key。</p>
     * 
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Panel background color properties.
     * </p>
     */
    public static final String PANEL_BACKGROUND = "Panel.background";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong>颜色选择器背景颜色属性key。
     * </p>
     * 
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> ColorChooser background color properties.
     * </p>
     */
    public static final String COLORCHOOSERUI_BACKGROUND = "ColorChooserUI.background";
    
    public void uninitialize()
    {
        UIManager.put(TRANSLUCENT_COLOR, null);
        
        UIManager.put(APPLICATION_ICON, null);
        
        UIManager.put(APPLICATION_TITLE, null);
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(TRANSLUCENT_COLOR, getColorRes(0, 0, 0, 0));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(APPLICATION_ICON, getIconRes("frame/default_frame_icon.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(APPLICATION_TITLE, "");

        table.put(PANEL_BACKGROUND, getColorRes(Color.WHITE));

        table.put(COLORCHOOSERUI_BACKGROUND, getColorRes(Color.WHITE));
    }
}
