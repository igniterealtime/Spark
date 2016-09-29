package freeseawind.lf.basic.button;

import java.awt.Color;

/**
 * 按钮颜色配置工厂类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckButtonColorFactory
{
    /**
     * 蓝色按钮颜色配置信息
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getBlueBtnInfo()
    {
        Color normal = new Color(9, 163, 200);

        Color pressed = new Color(5, 141, 192);

        Color rollver = new Color(75, 202, 255);

        Color font = Color.WHITE;

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }

    /**
     * 黄色按钮颜色配置信息
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getNaturalsBtnInfo()
    {
        Color normal = new Color(203, 143, 81);

        Color pressed = new Color(203, 132, 40);

        Color rollver = new Color(245, 171, 84);

        Color font = Color.WHITE;

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }

    /**
     * 紫色按钮颜色配置信息
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getVioletBtnInfo()
    {
        Color normal = new Color(76, 76, 177);

        Color pressed = new Color(50, 43, 200);

        Color rollver = new Color(91, 88, 241);

        Color font = Color.WHITE;

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }

    /**
     * 绿色按钮颜色配置信息
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getGreenBtnInfo()
    {
        Color normal = new Color(39, 214, 138);

        Color rollver = new Color(81, 249, 183);

        Color pressed = new Color(36, 207, 145);

        Color font = Color.WHITE;

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }
}
