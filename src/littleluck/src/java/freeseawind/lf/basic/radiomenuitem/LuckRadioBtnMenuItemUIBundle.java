package freeseawind.lf.basic.radiomenuitem;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.img.LuckIcon;

/**
 * RadioBtnMenuItemUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRadioBtnMenuItemUIBundle extends LuckResourceBundle
{
    /**
     * JRadioButtonMenuItem背景颜色属性key
     */
    public static final String BACKGROUND = "RadioButtonMenuItem.background";

    /**
     * JRadioButtonMenuItem选中时背景颜色属性key
     */
    public static final String SELECTIONBG = "RadioButtonMenuItem.selectionBackground";

    /**
     * JRadioButtonMenuItem选中时字体颜色属性key
     */
    public static final String SELECTIONFG = "RadioButtonMenuItem.selectionForeground";

    /**
     * RadioButtonMenuItem边框属性key
     */
    public static final String BORDER = "RadioButtonMenuItem.border";

    /**
     * RadioButtonMenuItem单选框图标属性key
     */
    public static final String CHECK_ICON = "RadioButtonMenuItem.checkIcon";

    /**
     * RadioButtonMenuItem箭头图标属性key
     */
    public static final String ARROW_ICON = "RadioButtonMenuItem.arrowIcon";

    /**
     * RadioButtonMenuItem文本和复选框图标间距属性key
     */
    public static final String AFTERCHECKICONGAP = "RadioButtonMenuItem.afterCheckIconGap";

    /**
     * RadioButtonMenuItem最小文本偏移宽度属性key
     */
    public static final String MINIMUMTEXTOFFSET = "RadioButtonMenuItem.minimumTextOffset";

    /**
     * RadioButtonMenuItem复选框图标偏移x轴距离属性key
     */
    public static final String CHECKICONOFFSET = "RadioButtonMenuItem.checkIconOffset";


    @Override
    protected void installColor()
    {
        UIManager.put(BACKGROUND, Color.WHITE);

        UIManager.put(SELECTIONBG, getColorRes(60, 175, 210));

        UIManager.put(SELECTIONFG, Color.WHITE);
    }

    @Override
    protected void installBorder()
    {
        UIManager.put(BORDER, BorderFactory.createEmptyBorder());
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(CHECK_ICON, new LuckRadioIcon());

        UIManager.put(ARROW_ICON, new IconUIResource(new LuckIcon(0, 0)));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(AFTERCHECKICONGAP, 4);

        UIManager.put(MINIMUMTEXTOFFSET, 0);

        UIManager.put(CHECKICONOFFSET, 4);
    }
}
