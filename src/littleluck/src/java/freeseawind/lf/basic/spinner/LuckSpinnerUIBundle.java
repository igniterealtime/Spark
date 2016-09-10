package freeseawind.lf.basic.spinner;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.UIManager;

import freeseawind.lf.border.LuckLineBorder;
import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * SpinnerUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckSpinnerUIBundle extends LuckResourceBundle
{
    /**
     * Spinner边框属性key
     */
    public static final String BORDER = "Spinner.border";

    /**
     * Spinner箭头按钮边框属性key
     */
    public static final String ARROWBUTTONBORDER = "Spinner.arrowButtonBorder";

    /**
     * Spinner背景颜色属性key
     */
    public static final String BACKGROUND = "Spinner.background";

    /**
     * Spinner箭头按钮间距属性key
     */
    public static final String ARROWBUTTONINSETS = "Spinner.arrowButtonInsets";

    @Override
    protected void installBorder()
    {
        UIManager.put(BORDER, new LuckLineBorder(new Insets(4, 5, 4, 5)));

        UIManager.put(ARROWBUTTONBORDER, new LuckLineBorder(new Insets(1, 1, 1, 1), 6));
    }

    @Override
    protected void installColor()
    {
        UIManager.put(BACKGROUND, getColorRes(Color.white));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(ARROWBUTTONINSETS, new Insets(1, 0, 0, 0));
    }
}
