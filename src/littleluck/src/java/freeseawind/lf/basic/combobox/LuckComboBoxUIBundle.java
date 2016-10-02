package freeseawind.lf.basic.combobox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.border.LuckShapeBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <pre>
 * ComboboxUI资源绑定类。
 *
 * A ComboboxUI resource bundle class.
 * </pre>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckComboBoxUIBundle extends LuckResourceBundle
{
    /**
     * <p>
     * Combobox背景颜色属性key
     * </p>
     *
     * <p>
     * Combobox background color properties.
     * </p>
     */
    public static final String BACKGROUND = "ComboBox.background";

    /**
     * <p>
     * Combobox选中时内容背景颜色<strong> (注: 本主题中没有用到该属性)</strong>
     * </p>
     *
     * <p>
     * Combobox background color properties when selected.
     * <strong> (Note: This property is not used in this topic)</strong>
     * </p>
     */
    public static final String SELECTIONBACKGROUND = "ComboBox.selectionBackground";

    /**
     * <p>
     * Combobox弹出菜单中选中时字体颜色
     * </p>
     *
     * <p>
     * Combobox popup menu font color properties when selected.
     * </p>
     */
    public static final String SELECTIONFOREGROUND = "ComboBox.selectionForeground";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong>下拉按钮背景颜色属性key, 默认true
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> arrow button background properties.
     * </p>
     */
    public static final String BUTTONBACKGROUND = "ComboBox.buttonBackground";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong>下拉按钮获取焦点时背景颜色
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> arrow shape color properties
     * when button has focus.
     * </p>
     */
    public static final String BUTTONFOCUS = "ComboBox.buttonFocus";

    /**
     * <p>
     * Combobox边框属性key， 默认间距(3, 4, 3, 4)
     * </p>
     *
     * <p>
     * Combobox border property key, default spacing (3, 4, 3, 4)
     * </p>
     */
    public static final String BORDER = "ComboBox.border";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 下拉列表边框属性key， 默认间距(5, 3, 6, 3)
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Popup Menu border property key,
     * default spacing (5, 3, 6, 3)
     * </p>
     */
    public static final String POPUPBORDER = "ComboBox.poupBorder";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 列表单元边框属性key， 默认间距(0, 4, 0, 0)
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Popup Menu renderer border
     * property key, default spacing (5, 3, 6, 3)
     * </p>
     */
    public static final String RENDERERBORDER = "ComboBox.rendererBorder";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 是否启用焦点边框
     * </p>
     *
     * <p>
     * <strong>[LittleLuck Attributes]</strong> Whether to enable the focus
     * border, default true.
     * </p>
     */
    public static final String ISFOCUSBORDER = "ComboBox.isFocusBorder";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 设置弹出窗口偏移坐标
     * </p>
     *
     * <p>
     * <strong>[LittleLuck Attributes]</strong> Popup window offset coordinates
     * properties.
     * </p>
     */
    public static final String POPUPLOCATION = "ComboBox.popLocation";

    public void uninitialize()
    {
        UIManager.put(BUTTONBACKGROUND, null);
        UIManager.put(BUTTONFOCUS, null);
        UIManager.put(POPUPBORDER, null);
        UIManager.put(RENDERERBORDER, null);
        UIManager.put(ISFOCUSBORDER, null);
        UIManager.put(POPUPLOCATION, null);
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(BACKGROUND, getColorRes(Color.WHITE));

        table.put(SELECTIONBACKGROUND, getColorRes(0, 150, 201, 200));

        table.put(BUTTONBACKGROUND, getColorRes(Color.WHITE));

        table.put(BUTTONFOCUS, getColorRes(245, 171, 84));

        table.put(SELECTIONFOREGROUND, getColorRes(Color.WHITE));
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        Insets insets = new Insets(3, 4, 3, 4);

        table.put(BORDER, new LuckShapeBorder(insets)
        {
            private static final long serialVersionUID = 8164006958194911458L;

            @Override
            public LuckBorderField getBorderField(Component c)
            {
                JComboBox<?> combox = (JComboBox<?>) c;

                if(combox.getUI() instanceof LuckBorderField)
                {
                    return (LuckBorderField)combox.getUI();
                }

                return null;
            }
        });

        BufferedImage img = LuckRes.getImage("popupmenu/shadow_border.9.png");

        UIManager.put(POPUPBORDER, getBorderRes(new LuckNinePatchBorder(new Insets(5, 3, 6, 3), img)));

        table.put(RENDERERBORDER, getBorderRes(new EmptyBorder(new Insets(0, 4, 0, 0))));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(POPUPLOCATION, new Point(0, 1));

        table.put(ISFOCUSBORDER, Boolean.TRUE);
    }
}
