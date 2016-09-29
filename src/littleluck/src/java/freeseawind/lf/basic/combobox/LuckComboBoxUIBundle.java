package freeseawind.lf.basic.combobox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.border.LuckNinePatchBorder;
import freeseawind.lf.border.LuckShapeBorder;
import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * ComboboxUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckComboBoxUIBundle extends LuckResourceBundle
{
    /**
     * Combobox背景颜色
     */
    public static final String BACKGROUND = "ComboBox.background";

    /**
     * Combobox选中时内容背景颜色 (注: 本主题中没有用到该属性)
     */
    public static final String SELECTIONBACKGROUND = "ComboBox.selectionBackground";

    /**
     * Combobox弹出菜单中选中时字体颜色
     */
    public static final String SELECTIONFOREGROUND = "ComboBox.selectionForeground";

    /**
     * [自定义属性]下拉按钮背景颜色
     */
    public static final String BUTTONBACKGROUND = "ComboBox.buttonBackground";

    /**
     * [自定义属性]下拉按钮获取焦点时背景颜色
     */
    public static final String BUTTONFOCUS = "ComboBox.buttonFocus";

    /**
     * Combobox边框属性key， 默认间距（3, 4, 3, 4）
     */
    public static final String BORDER = "ComboBox.border";

    /**
     * [自定义属性] 下拉列表边框属性key， 默认间距（5, 3, 6, 3）
     */
    public static final String POPUPBORDER = "ComboBox.poupBorder";

    /**
     * [自定义属性] 列表单元边框属性key， 默认间距（0, 4, 0, 0）
     */
    public static final String RENDERERBORDER = "ComboBox.rendererBorder";

    /**
     * [自定义属性] 是否启用焦点边框
     */
    public static final String ISFOCUSBORDER = "ComboBox.isFocusBorder";

    /**
     * [自定义属性] 设置弹出窗口偏移坐标
     */
    public static final String POPUPLOCATION = "ComboBox.popLocation";

    @Override
    protected void installColor()
    {
        UIManager.put(BACKGROUND, Color.WHITE);

        UIManager.put(SELECTIONBACKGROUND, new Color(0, 150, 201, 200));

        UIManager.put(BUTTONBACKGROUND, Color.WHITE);

        UIManager.put(BUTTONFOCUS, new Color(0, 150, 201, 200));

        UIManager.put(SELECTIONFOREGROUND, Color.WHITE);
    }

    @Override
    protected void installBorder()
    {
        Insets insets = new Insets(3, 4, 3, 4);

        UIManager.put(BORDER, new LuckShapeBorder(insets)
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

        UIManager.put(POPUPBORDER, new LuckNinePatchBorder(new Insets(5, 3, 6, 3), img));

        UIManager.put(RENDERERBORDER, new EmptyBorder(new Insets(0, 4, 0, 0)));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(POPUPLOCATION, new Point(0, 1));

        UIManager.put(ISFOCUSBORDER, Boolean.TRUE);
    }
}
