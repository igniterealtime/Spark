package freeseawind.lf.basic.tabbedpane;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * TabbedPane 资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTabbedPaneUIBundle extends LuckResourceBundle
{
    /**
     * TabbedPane背景颜色key
     */
    public static final String BACKGROUNDCOLOR = "TabbedPane.background";

    /**
     * 内容面板颜色key
     */
    public static final String CONTENTAREACOLOR = "TabbedPane.contentAreaColor";

    /**
     * 选项卡被选中时颜色key
     */
    public static final String SELECTEDCOLOR = "TabbedPane.selected";
    
    /**
     * TabbedPane选中时字体颜色属性key
     */
    public static final String SELECTEDFOREGROUND = "TabbedPane.selectedForeground";

    /**
     * 内容面板是否透明key
     */
    public static final String CONTENTOPAQUE = "TabbedPane.contentOpaque";

    /**
     * TabbedPane是否透明key
     */
    public static final String OPAQUE = "TabbedPane.opaque";

    /**
     * 是否使用半透明边框key
     */
    public static final String TABSOVERLAPBORDER = "TabbedPane.tabsOverlapBorder";

    /**
     * 内容边框间距key
     */
    public static final String CONTENTBORDERINSETS = "TabbedPane.contentBorderInsets";

    /**
     * 选项卡选中时的间距配置key
     */
    public static final String SELECTEDTABPADINSETS = "TabbedPane.selectedTabPadInsets";

    /**
     * 选项卡间距配置key
     */
    public static final String TABAREAINSETS = "TabbedPane.tabAreaInsets";

    /**
     * 边框颜色key
     */
    public static final String SHADOW = "TabbedPane.shadow";
    
    /**
     * [自定义属性]选项卡选中时的边框颜色属性key
     */
    public static final String SELECTEDSHADOW = "TabbedPane.selectedShadow";

    /**
     * Tab间距属性key,可通过此属性控制Tab的宽高
     */
    public static final String TABINSETS = "TabbedPane.tabInsets";

    @Override
    protected void installColor()
    {
        UIManager.put(BACKGROUNDCOLOR, Color.WHITE);

        UIManager.put(CONTENTAREACOLOR, Color.WHITE);
        
        UIManager.put(SELECTEDFOREGROUND, Color.BLACK);

        //171, 225, 235
        UIManager.put(SELECTEDCOLOR, getColorRes(201,201,201));

        UIManager.put(SHADOW, getColorRes(221, 220, 227));
        
        UIManager.put(SELECTEDSHADOW, getColorRes(221, 220, 227));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(CONTENTOPAQUE, false);

        UIManager.put(OPAQUE, true);

        UIManager.put(TABSOVERLAPBORDER, false);

        UIManager.put(SELECTEDTABPADINSETS, new InsetsUIResource(0, 0, 0, 0));

        UIManager.put(TABAREAINSETS, new InsetsUIResource(3, 2, 0, 2));

        UIManager.put(CONTENTBORDERINSETS, new InsetsUIResource(1, 1, 1, 1));

        UIManager.put(TABINSETS, new InsetsUIResource(3, 9, 3, 9));
    }
}
