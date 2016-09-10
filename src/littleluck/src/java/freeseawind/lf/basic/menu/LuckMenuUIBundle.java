package freeseawind.lf.basic.menu;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.img.LuckIcon;
import freeseawind.lf.utils.LuckRes;

/**
 * JMenubar and JMenu 资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMenuUIBundle extends LuckResourceBundle
{
    /**
     * JMenubar边框属性key
     */
    public static final String MENUBAR_BORDER = "MenuBar.border";

    /**
     * JMenu边框属性key
     */
    public static final String MENU_BORDER = "Menu.border";

    /**
     * JMenu选中时背景颜色属性key
     */
    public static final String MENU_SELECTIONBG = "Menu.selectionBackground";

    /**
     * JMenu选中时字体颜色属性key
     */
    public static final String MENU_SELECTIONFG = "Menu.selectionForeground";

    /**
     * JMenuBar背景颜色属性key
     */
    public static final String MENUBAR_BACKGROUND = "MenuBar.background";

    /**
     * JMenu背景颜色属性key
     */
    public static final String MENU_BACKGROUND = "Menu.background";

    /**
     * [自定义属性] JCheckBoxMenuItem箭头图标
     */
    public static final String ARROW_NORMAL_ICON = "Menu.arrowNormalIcon";

    /**
     * [自定义属性] JCheckBoxMenuItem鼠标经过时箭头图标
     */
    public static final String ARROW_ROLLVER_ICON = "Menu.arrowRollverIcon";

    /**
     * MenuItem子菜单图标属性key
     */
    public static final String ARROW_ICON = "Menu.arrowIcon";

    /**
     * JMenu复选框图标属性key
     */
    public static final String CHECK_ICON = "Menu.checkIcon";

    /**
     * JMenu弹出菜单沿x轴偏移量(littleluck暂时没有使用该属性)
     */
    public static final String MENUPOPUPOFFSETX = "Menu.menuPopupOffsetX";

    /**
     * JMenu弹出菜单沿y轴偏移量(littleluck暂时没有使用该属性)
     */
    public static final String MENUPOPUPOFFSETY = "Menu.menuPopupOffsetY";

    /**
     * JMenu弹出子菜单沿x轴偏移量(littleluck暂时没有使用该属性)
     */
    public static final String SUBMENUPOPUPOFFSETX = "Menu.submenuPopupOffsetX";

    /**
     * JMenu弹出子菜单沿y轴偏移量(littleluck暂时没有使用该属性)
     */
    public static final String SUBMENUPOPUPOFFSETY = "Menu.submenuPopupOffsetY";

    /**
     * JMenu文本和复选框图标间距属性key，默认值4
     */
    public static final String AFTERCHECKICONGAP = "Menu.afterCheckIconGap";

    /**
     * JMenu最小文本偏移宽度属性key，默认值0
     */
    public static final String MINIMUMTEXTOFFSET = "Menu.minimumTextOffset";

    /**
     * JMenu复选框图标偏移x轴距离属性key，默认值4
     */
    public static final String CHECKICONOFFSET = "Menu.checkIconOffset";

    @Override
    protected void installBorder()
    {
        UIManager.put(MENUBAR_BORDER, BorderFactory.createEmptyBorder(3, 0, 3, 0));

        UIManager.put(MENU_BORDER, BorderFactory.createEmptyBorder());
    }

    @Override
    protected void installColor()
    {
        UIManager.put(MENU_SELECTIONBG, getColorRes(60, 175, 210));

        UIManager.put(MENU_SELECTIONFG, Color.WHITE);

        UIManager.put(MENU_BACKGROUND, Color.WHITE);
        
        UIManager.put(MENUBAR_BACKGROUND, Color.WHITE);
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(ARROW_NORMAL_ICON, LuckRes.getImage("menu/arrow_normal.png"));

        UIManager.put(ARROW_ROLLVER_ICON, LuckRes.getImage("menu/arrow_rollver.png"));

        UIManager.put(ARROW_ICON, new LuckArrowIcon());

        UIManager.put(CHECK_ICON, getIconRes(new LuckIcon(16, 0)));
    }

    @Override
    protected void installOther()
    {
        // 注： 下列三个属性决定JMenu在弹出菜单中的布局
        UIManager.put(AFTERCHECKICONGAP, 4);

        UIManager.put(MINIMUMTEXTOFFSET, 0);

        UIManager.put(CHECKICONOFFSET, 4);
    }
}
