package freeseawind.lf.basic.menuitem;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.img.LuckIcon;

/**
 * <p>MenuItemUI资源绑定类。<p>
 *
 * <p>A MenuItemUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckMenuItemUIBundle extends LuckResourceBundle
{

    /**
     * <p>MenuItem选中时背景颜色属性key。</p>
     *
     * <p>MenuItem background color properties when selected.</p>
     */
    public static final String SELECTIONBG = "MenuItem.selectionBackground";

    /**
     * <p>MenuItem选中时字体颜色属性key。</p>
     *
     * <p>MenuItem font color properties when selected.</p>
     */
    public static final String SELECTIONFG = "MenuItem.selectionForeground";

    /**
     * <p>MenuItem背景颜色属性key。</p>
     *
     * <p>MenuItem background color properties.</p>
     */
    public static final String BACKGROUND = "MenuItem.background";

    /**
     * <p>MenuItem边框属性key。</p>
     *
     * <p>MenuItem border properties.</p>
     */
    public static final String BORDER = "MenuItem.border";

    /**
     * <p>MenuItem单选框图标属性key。</p>
     *
     * <p>MenuItem check icon properties.</p>
     */
    public static final String CHECK_ICON = "MenuItem.checkIcon";

    /**
     * <p>MenuItem箭头图标属性key。</p>
     *
     * <p>MenuItem arrow icon properties.</p>
     */
    public static final String ARROW_ICON = "MenuItem.arrowIcon";

    /**
     * <p>MenuItem文本和复选框图标间距属性key。</p>
     *
     * <p>MenuItem after check icon gap properties.</p>
     */
    public static final String AFTERCHECKICONGAP = "MenuItem.afterCheckIconGap";

    /**
     * <p>MenuItem最小文本偏移宽度属性key。</p>
     *
     * <p>MenuItem minimum text offset properties.</p>
     */
    public static final String MINIMUMTEXTOFFSET = "MenuItem.minimumTextOffset";

    /**
     * <p>MenuItem复选框图标偏移x轴距离属性key。</p>
     *
     * <p>MenuItem check icon offset properties.</p>
     */
    public static final String CHECKICONOFFSET = "MenuItem.checkIconOffset";

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(SELECTIONBG, getColorRes(60, 175, 210));

        table.put(SELECTIONFG, getColorRes(Color.WHITE));

        table.put(BACKGROUND, getColorRes(Color.WHITE));
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        table.put(BORDER, getBorderRes(BorderFactory.createEmptyBorder()));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(CHECK_ICON, getIconRes(new LuckIcon(0, 0)));

        table.put(ARROW_ICON, getIconRes(new LuckIcon(0, 0)));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        UIManager.put(AFTERCHECKICONGAP, 4);
        UIManager.put(MINIMUMTEXTOFFSET, 0);
        UIManager.put(CHECKICONOFFSET, 4);
    }
}
