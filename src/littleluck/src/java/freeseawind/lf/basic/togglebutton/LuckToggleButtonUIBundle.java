package freeseawind.lf.basic.togglebutton;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * ToggleButtonUI and RadioButtonUI and CheckBoxUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckToggleButtonUIBundle extends LuckResourceBundle
{
    /**
     * [自定义属性]RadioButton无状态下颜色属性key
     */
    public static final String RADIO_NORMAL_COLOR = "RadioButton.normalColor";

    /**
     * [自定义属性]RadioButton有焦点时颜色属性key
     */
    public static final String RADIO_FOCUS_COLOR = "RadioButton.focusColor";

    /**
     * [自定义属性]RadioButton点击时内阴影颜色属性key
     */
    public static final String RADIO_SHADOW_COLOR = "RadioButton.shadowColor";

    /**
     * 单选按钮背景颜色属性key
     */
    public static final String RADIO_BACKGROUND = "RadioButton.background";

    /**
     * 复选按钮背景颜色属性key
     */
    public static final String CHECKBOX_BACKGROUND = "CheckBox.background";

    /**
     * [自定义属性]RadioButton小圆点颜色属性key
     */
    public static final String RADIO_CHECK_COLOR = "RadioButton.checkColor";

    /**
     * ChecBoxButton图标属性key
     */
    public static final String CHECKBOX_ICON = "CheckBox.icon";

    /**
     * [自定义属性]ChecBoxButton无状态下图片属性key
     */
    public static final String CHECKBOX_NORMAL_ICON = "CheckBox.normal";

    /**
     * [自定义属性]ChecBoxButton非选中状态下鼠标经过图片属性key
     */
    public static final String CHECKBOX_ROLLVER_ICON = "CheckBox.rollver";

    /**
     * [自定义属性]ChecBoxButton选中状态下鼠标经过图片属性key
     */
    public static final String CHECKBOX_UNROLLVER_ICON = "CheckBox.unrollver";

    /**
     * [自定义属性]ChecBoxButton无状态下点击图片属性key
     */
    public static final String CHECKBOX_PRESSED_ICON = "CheckBox.pressed";

    /**
     * [自定义属性]ChecBoxButton选中状态下鼠标点击图片属性key
     */
    public static final String CHECKBOX_UNPRESSED_ICON = "CheckBox.unpressed";

    /**
     * ToggleButton边框属性key
     */
    public static final String TOGGLEBUTTON_BORDER = "ToggleButton.border";


    /**
     * RadioButton图标属性key
     */
    public static final String RADIO_ICON = "RadioButton.icon";

    @Override
    protected void installColor()
    {
        UIManager.put(RADIO_BACKGROUND, Color.white);
        UIManager.put(CHECKBOX_BACKGROUND, Color.white);
        UIManager.put(RADIO_NORMAL_COLOR, getColorRes(178, 178, 178));
        UIManager.put(RADIO_FOCUS_COLOR, getColorRes(5, 141, 192));
        UIManager.put(RADIO_SHADOW_COLOR, getColorRes(120, 175, 217));
        UIManager.put(RADIO_CHECK_COLOR, getColorRes(5, 141, 192));
    }

    @Override
    protected void installBorder()
    {
        UIManager.put(TOGGLEBUTTON_BORDER, BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    @Override
    protected void loadImages()
    {
        //
        UIManager.put(CHECKBOX_ICON, new LuckCheckBoxIcon());
        UIManager.put(CHECKBOX_NORMAL_ICON, LuckRes.getImage("checkbox/cb_normal.png"));
        UIManager.put(CHECKBOX_ROLLVER_ICON, LuckRes.getImage("checkbox/cb_rollver.png"));
        UIManager.put(CHECKBOX_UNROLLVER_ICON, LuckRes.getImage("checkbox/cb_un_rollver.png"));
        UIManager.put(CHECKBOX_PRESSED_ICON, LuckRes.getImage("checkbox/cb_pressed.png"));
        UIManager.put(CHECKBOX_UNPRESSED_ICON, LuckRes.getImage("checkbox/cb_un_pressed.png"));

        //
        UIManager.put(RADIO_ICON, new LuckRadioIcon());
    }
}
