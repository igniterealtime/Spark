package freeseawind.lf.basic.spinner;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.border.LuckLineBorder;
import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>SpinnerUI资源绑定类。</p>
 *
 * <p>SpinnerUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckSpinnerUIBundle extends LuckResourceBundle
{
    /**
     * <p>Spinner边框属性key。</p>
     *
     * <p>Spinner border properties.</p>
     */
    public static final String BORDER = "Spinner.border";

    /**
     * <p>Spinner箭头按钮边框属性key。</p>
     *
     * <p>Spinner arrow button border properties.</p>
     */
    public static final String ARROWBUTTONBORDER = "Spinner.arrowButtonBorder";

    /**
     * <p>Spinner背景颜色属性key。</p>
     *
     * <p>Spinner background color properties.</p>
     */
    public static final String BACKGROUND = "Spinner.background";

    /**
     * <p>Spinner箭头按钮间距属性key。</p>
     *
     * <p>Spinner arrow button insets properties.</p>
     */
    public static final String ARROWBUTTONINSETS = "Spinner.arrowButtonInsets";

    @Override
    protected void installBorder(UIDefaults table)
    {
        Border border = new LuckLineBorder(new Insets(4, 5, 4, 5));
        
        table.put(BORDER, getBorderRes(border));
        
        Border arrowBtnBorder = new LuckLineBorder(new Insets(1, 1, 1, 1), 6);

        table.put(ARROWBUTTONBORDER, getBorderRes(arrowBtnBorder));
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(BACKGROUND, getColorRes(Color.white));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(ARROWBUTTONINSETS, new InsetsUIResource(1, 0, 0, 0));
    }
}
