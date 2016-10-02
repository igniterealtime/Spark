package freeseawind.lf.basic.radiomenuitem;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.img.LuckIcon;

/**
 * <p>RadioBtnMenuItemUI资源绑定类。<p>
 *
 * <p>A RadioBtnMenuItemUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRadioBtnMenuItemUIBundle extends LuckResourceBundle
{
    /**
     * <p>JRadioButtonMenuItem背景颜色属性key。</p>
     *
     * <p>JRadioButtonMenuItem background color properties.</p>
     */
    public static final String BACKGROUND = "RadioButtonMenuItem.background";

    /**
     * <p>JRadioButtonMenuItem选中时背景颜色属性key。</p>
     *
     * <p>JRadioButtonMenuItem background color properties when selected.</p>
     */
    public static final String SELECTIONBG = "RadioButtonMenuItem.selectionBackground";

    /**
     * <p>JRadioButtonMenuItem选中时字体颜色属性key。</p>
     *
     * <p>RadioButtonMenuItem font color properties when selected.</p>
     */
    public static final String SELECTIONFG = "RadioButtonMenuItem.selectionForeground";

    /**
     * <p>RadioButtonMenuItem边框属性key。</p>
     *
     * <p>RadioButtonMenuItem border properties.</p>
     */
    public static final String BORDER = "RadioButtonMenuItem.border";

    /**
     * <p>RadioButtonMenuItem单选框图标属性key。</p>
     *
     * <p>RadioButtonMenuItem check icon properties.</p>
     */
    public static final String CHECK_ICON = "RadioButtonMenuItem.checkIcon";

    /**
     * <p>RadioButtonMenuItem箭头图标属性key。</p>
     *
     * <p>RadioButtonMenuItem arrow icon properties.</p>
     */
    public static final String ARROW_ICON = "RadioButtonMenuItem.arrowIcon";

    /**
     * <p>RadioButtonMenuItem文本和复选框图标间距属性key。</p>
     *
     * <p>RadioButtonMenuItem after check icon gap properties.</p>
     */
    public static final String AFTERCHECKICONGAP = "RadioButtonMenuItem.afterCheckIconGap";

    /**
     * <p>RadioButtonMenuItem最小文本偏移宽度属性key。</p>
     *
     * <p>RadioButtonMenuItem minimum text offset properties.</p>
     */
    public static final String MINIMUMTEXTOFFSET = "RadioButtonMenuItem.minimumTextOffset";

    /**
     * <p>RadioButtonMenuItem复选框图标偏移x轴距离属性key。</p>
     *
     * <p>RadioButtonMenuItem check icon offset properties,</p>
     */
    public static final String CHECKICONOFFSET = "RadioButtonMenuItem.checkIconOffset";


    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(BACKGROUND, getColorRes(Color.WHITE));

        table.put(SELECTIONBG, getColorRes(60, 175, 210));

        table.put(SELECTIONFG, getColorRes(Color.WHITE));
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        table.put(BORDER, getBorderRes(BorderFactory.createEmptyBorder()));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(CHECK_ICON, getIconRes(new LuckRadioIcon()));

        table.put(ARROW_ICON, getIconRes(new LuckIcon(0, 0)));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(AFTERCHECKICONGAP, 4);

        table.put(MINIMUMTEXTOFFSET, 0);

        table.put(CHECKICONOFFSET, 4);
    }
}
