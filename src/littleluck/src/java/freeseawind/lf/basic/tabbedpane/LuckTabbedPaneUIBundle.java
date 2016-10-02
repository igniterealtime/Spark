package freeseawind.lf.basic.tabbedpane;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>TabbedPane资源绑定类。</p>
 *
 * <p>TabbedPane resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTabbedPaneUIBundle extends LuckResourceBundle
{
    /**
     * <p>TabbedPane背景颜色key。</p>
     *
     * <p>TabbedPane background color property.</p>
     */
    public static final String BACKGROUNDCOLOR = "TabbedPane.background";

    /**
     * <p>内容面板颜色key。</p>
     *
     * <p>TabbedPane content area background color property.</p>
     */
    public static final String CONTENTAREACOLOR = "TabbedPane.contentAreaColor";

    /**
     * <p>选项卡被选中时颜色key。</p>
     *
     * <p>TabbedPane tab background color properties when tab selected.</p>
     */
    public static final String SELECTEDCOLOR = "TabbedPane.selected";

    /**
     * <p>TabbedPane选中时字体颜色属性key。</p>
     *
     * <p>TabbedPane tab font color properties when tab selected.</p>
     */
    public static final String SELECTEDFOREGROUND = "TabbedPane.selectedForeground";

    /**
     * <p>内容面板是否透明key。</p>
     *
     * <p>TabbedPane content panel is transparent properties.</p>
     */
    public static final String CONTENTOPAQUE = "TabbedPane.contentOpaque";

    /**
     * <p>TabbedPane是否透明key。</p>
     *
     * <p>TabbedPane is transparent properties.</p>
     */
    public static final String OPAQUE = "TabbedPane.opaque";

    /**
     * <p>是否使用半透明边框key。</p>
     *
     * <p>TabbedPane is transparent border properties.</p>
     */
    public static final String TABSOVERLAPBORDER = "TabbedPane.tabsOverlapBorder";

    /**
     * <p>内容边框间距key。</p>
     *
     * <p>Content padding properties.</p>
     */
    public static final String CONTENTBORDERINSETS = "TabbedPane.contentBorderInsets";

    /**
     * <p>选项卡选中时的间距配置key。</p>
     *
     * <p>TabbedPane tab padding properties when tab selected.</p>
     */
    public static final String SELECTEDTABPADINSETS = "TabbedPane.selectedTabPadInsets";

    /**
     * <p>选项卡间距配置key。</p>
     *
     * <p>TabbedPane tab padding properties.</p>
     */
    public static final String TABAREAINSETS = "TabbedPane.tabAreaInsets";

    /**
     * <p>边框颜色key。</p>
     *
     * <p>TabbedPane tab border color properties.</p>
     */
    public static final String SHADOW = "TabbedPane.shadow";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong>选项卡选中时的边框颜色属性key。
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong>TabbedPane tab border color
     * properties when selected.
     * </p>
     */
    public static final String SELECTEDSHADOW = "TabbedPane.selectedShadow";

    /**
     * <p>Tab间距属性key,可通过此属性控制Tab的宽高。</p>
     *
     * <p>Tab spacing attribute key, you can control the Tab width and height by this property.</p>
     */
    public static final String TABINSETS = "TabbedPane.tabInsets";

    public void uninitialize()
    {
        UIManager.put(BACKGROUNDCOLOR, null);

        UIManager.put(CONTENTAREACOLOR, null);

        UIManager.put(SELECTEDFOREGROUND, null);

        UIManager.put(SELECTEDCOLOR, null);

        UIManager.put(SHADOW, null);

        UIManager.put(SELECTEDSHADOW, null);

        UIManager.put(CONTENTOPAQUE, null);

        UIManager.put(OPAQUE, null);

        UIManager.put(TABSOVERLAPBORDER, null);

        UIManager.put(SELECTEDTABPADINSETS, null);

        UIManager.put(TABAREAINSETS, null);

        UIManager.put(CONTENTBORDERINSETS, null);

        UIManager.put(TABINSETS, null);
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        UIManager.put(BACKGROUNDCOLOR, getColorRes(Color.WHITE));

        UIManager.put(CONTENTAREACOLOR, getColorRes(Color.WHITE));

        UIManager.put(SELECTEDFOREGROUND, getColorRes(Color.WHITE));

        //171, 225, 235
        UIManager.put(SELECTEDCOLOR, getColorRes(9, 163, 200));

        UIManager.put(SHADOW, getColorRes(221, 220, 227));

        UIManager.put(SELECTEDSHADOW, getColorRes(221, 220, 227));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        UIManager.put(CONTENTOPAQUE, Boolean.FALSE);

        UIManager.put(OPAQUE, Boolean.TRUE);

        UIManager.put(TABSOVERLAPBORDER, Boolean.FALSE);

        UIManager.put(SELECTEDTABPADINSETS, new InsetsUIResource(0, 0, 0, 0));

        UIManager.put(TABAREAINSETS, new InsetsUIResource(3, 2, 0, 2));

        UIManager.put(CONTENTBORDERINSETS, new InsetsUIResource(1, 1, 1, 1));

        UIManager.put(TABINSETS, new InsetsUIResource(3, 9, 3, 9));
    }
}
