package freeseawind.lf.basic.checkboxmenuitem;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.img.LuckIcon;
import freeseawind.lf.utils.LuckRes;

/**
 * <pre>
 * LuckCheckBoxMenuItemUI资源绑定类。
 *
 * LuckCheckBoxMenuItemUI resource bundle class.
 * </pre>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckCheckboxMenuItemUIBundle extends LuckResourceBundle
{
    /**
     * <p>
     * CheckBoxMenuItem选中时背景颜色属性key
     * </p>
     *
     * <p>
     * CheckBoxMenuItem background color properties when selected.
     * </p>
     */
    public static final String SELECTIONBG = "CheckBoxMenuItem.selectionBackground";

    /**
     * <p>
     * CheckBoxMenuItem选中时字体颜色属性key
     * </p>
     *
     * <p>
     * CheckBoxMenuItem font color properties when selected.
     * </p>
     */
    public static final String SELECTIONFG = "CheckBoxMenuItem.selectionForeground";

    /**
     * <p>
     * CheckBoxMenuItem选中时背景颜色属性key
     * </p>
     *
     * <p>
     * CheckBoxMenuItem background color properties when selected.
     * </p>
     */
    public static final String BACKGROUND = "CheckBoxMenuItem.background";

    /**
     * <p>
     * CheckBoxMenuItem边框属性key>
     * </p>
     *
     * <p>
     * CheckBoxMenuItem border properties.
     * </p>
     */
    public static final String BORDER = "CheckBoxMenuItem.border";

    /**
     * <p>
     * CheckBoxMenuItem复选框图标属性key
     * </p>
     *
     * <p>
     * CheckBoxMenuItem check Icon properties.
     * </p>
     */
    public static final String CHECK_ICON = "CheckBoxMenuItem.checkIcon";

    /**
     * <p>
     * CheckBoxMenuItem箭头图标
     * </p>
     *
     * <p>
     * CheckBoxMenuItem arrow icon properties.
     * </p>
     */
    public static final String ARROW_ICON = "CheckBoxMenuItem.arrowIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> JCheckBoxMenuItem选中时图片属性key
     * </p>
     *
     * <p>
     * <strong>[LittleLuck Attributes]</strong> JCheckBoxMenuItem image properties when is selected.
     * </p>
     */
    public static final String NORMAL_IMG = "CheckBoxMenuItem.checkNormlIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> JCheckBoxMenuItem选中时鼠标经过图片属性key
     * </p>
     *
     * <p>
     * <strong>[LittleLuck Attributes]</strong> JCheckBoxMenuItem image properties when the mouse passes and is selected.
     * </p>
     */
    public static final String ROLLVER_IMG = "CheckBoxMenuItem.checkRollverIcon";

    /**
     * <p>
     * CheckBoxMenuItem文本和复选框图标间距属性key, 默认值4。
     * </p>
     *
     * <p>
     * CheckBoxMenuItem after check icon gap properties, the default value is 4.
     * </p>
     */
    public static final String AFTERCHECKICONGAP = "CheckBoxMenuItem.afterCheckIconGap";

    /**
     * <p>
     * CheckBoxMenuItem最小文本偏移宽度属性key, 默认值0。
     * </p>
     *
     * <p>
     * CheckBoxMenuItem minimum text offset properties, the default value is 0.
     * </p>
     */
    public static final String MINIMUMTEXTOFFSET = "CheckBoxMenuItem.minimumTextOffset";

    /**
     * <p>
     * CheckBoxMenuItem复选框图标偏移x轴距离属性key, 默认值4。
     * </p>
     *
     * <p>
     * CheckBoxMenuItem check icon offset properties, the default value is 4.
     * </p>
     */
    public static final String CHECKICONOFFSET = "CheckBoxMenuItem.checkIconOffset";

    public void uninitialize()
    {
        UIManager.put(NORMAL_IMG, null);

        UIManager.put(ROLLVER_IMG, null);
    }

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
        table.put(CHECK_ICON, getIconRes(new LuckCheckboxIcon()));

        table.put(ARROW_ICON, getIconRes(new LuckIcon(0, 0)));

        table.put(NORMAL_IMG, LuckRes.getImage("menu/checkbox_normal.png"));

        table.put(ROLLVER_IMG, LuckRes.getImage("menu/checkbox_rollver.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(AFTERCHECKICONGAP, 4);

        table.put(MINIMUMTEXTOFFSET, 0);

        table.put(CHECKICONOFFSET, 4);
    }
}
