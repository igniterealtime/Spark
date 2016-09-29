package freeseawind.lf.basic.button;

import java.awt.Color;
import java.io.Serializable;

/**
 * 按钮颜色属性类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckButtonColorInfo implements Serializable
{
    private static final long serialVersionUID = -6352677081768535770L;

    // 初始状态颜色
    private Color normalColor;

    // 鼠标经过时颜色
    private Color rollverColor;

    // 鼠标点击时颜色
    private Color pressedColor;

    // 字体颜色
    private Color fontColor;

    /**
     *
     * @param normal 无状态下按钮颜色
     *
     * @param rollver 鼠标经过时按钮颜色
     *
     * @param pressed 鼠标点击时按钮颜色
     *
     * @param font 按钮字体颜色
     *
     */
    public LuckButtonColorInfo(Color normal,
                               Color rollver,
                               Color pressed,
                               Color font)
    {
        this.normalColor = normal;
        this.rollverColor = rollver;
        this.pressedColor = pressed;
        this.fontColor = font;
    }

    /**
     *
     * @return <code>Color</code> 无状态下按钮颜色
     */
    public Color getNormalColor()
    {
        return normalColor;
    }

    /**
    *
    * @return <code>Color</code> 鼠标经过时按钮颜色
    */
    public Color getRollverColor()
    {
        return rollverColor;
    }

    /**
    *
    * @return <code>Color</code> 鼠标点击时按钮颜色
    */
    public Color getPressedColor()
    {
        return pressedColor;
    }

    /**
    *
    * @return <code>Color</code> 按钮字体颜色
    */
    public Color getFontColor()
    {
        return fontColor;
    }
}
