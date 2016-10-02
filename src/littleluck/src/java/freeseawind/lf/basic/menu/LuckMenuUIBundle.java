package freeseawind.lf.basic.menu;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.img.LuckIcon;
import freeseawind.lf.utils.LuckRes;

/**
 * <p>JMenubar and JMenu 资源绑定类</p>
 *
 * <p>JMenubar and JMenu resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMenuUIBundle extends LuckResourceBundle
{
    /**
     * <p>JMenubar边框属性key</p>
     *
     * <p>JMenubar border properties.</p>
     */
    public static final String MENUBAR_BORDER = "MenuBar.border";

    /**
     * <p>JMenu边框属性key<p>
     *
     * <p>JMenu border properties.</p>
     */
    public static final String MENU_BORDER = "Menu.border";

    /**
     * <p>JMenu选中时背景颜色属性key<p>
     *
     * <p>JMenu background color properties when selected.</p>
     */
    public static final String MENU_SELECTIONBG = "Menu.selectionBackground";

    /**
     * <p>JMenu选中时字体颜色属性key<p>
     *
     * <p>JMenu font color properties when selected.</p>
     */
    public static final String MENU_SELECTIONFG = "Menu.selectionForeground";

    /**
     * <p>JMenuBar背景颜色属性key<p>
     *
     * <p>JMenuBar background color properties</p>
     */
    public static final String MENUBAR_BACKGROUND = "MenuBar.background";

    /**
     * <p>JMenu背景颜色属性key<p>
     *
     * <p>JMenu background color properties.</p>
     */
    public static final String MENU_BACKGROUND = "Menu.background";

    /**
     * <p><strong>[LittleLuck属性]</strong> JMenu箭头图标<p>
     *
     * <p><strong>[LittLeLuck Attributes]</strong> JMenu arrow image properties.</p>
     */
    public static final String ARROW_NORMAL_IMG = "Menu.arrowNormalIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> JMenu鼠标经过时箭头图标
     * <p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> JMenu arrow image properties
     * when selected.
     * </p>
     */
    public static final String ARROW_ROLLVER_IMG = "Menu.arrowRollverIcon";

    /**
     * <p>JMenu子菜单图标属性key<p>
     *
     * <p>JMenu arrow Icon properties.</p>
     */
    public static final String ARROW_ICON = "Menu.arrowIcon";

    /**
     * <p>JMenu复选框图标属性key<p>
     *
     * <p>JMenu check Icon properties.</p>
     */
    public static final String CHECK_ICON = "Menu.checkIcon";

    /**
     * <p>JMenu弹出菜单沿x轴偏移量(LittleLuck暂时没有使用该属性)<p>
     *
     * <p>JMenu popup menu along the x-axis offset (LittleLuck temporarily not use this property)</p>
     */
    public static final String MENUPOPUPOFFSETX = "Menu.menuPopupOffsetX";

    /**
     * <p>JMenu弹出菜单沿y轴偏移量(LittleLuck暂时没有使用该属性)<p>
     *
     * <p>JMenu popup menu along the y-axis offset (LittleLuck temporarily not use this property)</p>
     */
    public static final String MENUPOPUPOFFSETY = "Menu.menuPopupOffsetY";

    /**
     * <p>JMenu弹出子菜单沿x轴偏移量(LittleLuck暂时没有使用该属性)<p>
     *
     * <p>JMenu pop-up sub-menu along the x-axis offset (LittleLuck temporarily not use this property)</p>
     */
    public static final String SUBMENUPOPUPOFFSETX = "Menu.submenuPopupOffsetX";

    /**
     * <p>JMenu弹出子菜单沿y轴偏移量(LittleLuck暂时没有使用该属性)<p>
     *
     * <p>JMenu pop-up sub-menu along the y-axis offset (LittleLuck temporarily not use this property)</p>
     */
    public static final String SUBMENUPOPUPOFFSETY = "Menu.submenuPopupOffsetY";

    /**
     * <p>Menu文本和复选框图标间距属性key。</p>
     *
     * <p>Menu after check icon gap properties.</p>
     */
    public static final String AFTERCHECKICONGAP = "Menu.afterCheckIconGap";

    /**
     * <p>Menu最小文本偏移宽度属性key。</p>
     *
     * <p>Menu minimum text offset properties.</p>
     */
    public static final String MINIMUMTEXTOFFSET = "Menu.minimumTextOffset";

    /**
     * <p>Menu复选框图标偏移x轴距离属性key, 默认值4。</p>
     *
     * <p>Menu check icon offset properties.</p>
     */
    public static final String CHECKICONOFFSET = "Menu.checkIconOffset";

    public void uninitialize()
    {
        UIManager.put(ARROW_NORMAL_IMG, null);

        UIManager.put(ARROW_ROLLVER_IMG, null);
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        table.put(MENUBAR_BORDER, getBorderRes(BorderFactory.createEmptyBorder(3, 0, 3, 0)));

        table.put(MENU_BORDER, getBorderRes(BorderFactory.createEmptyBorder()));
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(MENU_SELECTIONBG, getColorRes(60, 175, 210));

        table.put(MENU_SELECTIONFG, getColorRes(Color.WHITE));

        table.put(MENU_BACKGROUND, getColorRes(Color.WHITE));

        table.put(MENUBAR_BACKGROUND, Color.WHITE);
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(ARROW_NORMAL_IMG, LuckRes.getImage("menu/arrow_normal.png"));

        table.put(ARROW_ROLLVER_IMG, LuckRes.getImage("menu/arrow_rollver.png"));

        table.put(ARROW_ICON, getIconRes(new LuckArrowIcon()));

        table.put(CHECK_ICON, getIconRes(new LuckIcon(16, 0)));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        // 注： 下列三个属性决定JMenu在弹出菜单中的布局
        table.put(AFTERCHECKICONGAP, 4);

        table.put(MINIMUMTEXTOFFSET, 0);

        table.put(CHECKICONOFFSET, 4);
    }
}
