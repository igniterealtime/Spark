package freeseawind.lf.basic.button;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;

/**
 * <pre>
 * 按钮配置颜色工厂类。
 *
 * Button Color factory class.
 * </pre>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckButtonColorFactory
{
    /**
     * Blue button color configuration information
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getBlueBtnInfo()
    {
        Color normal = new ColorUIResource(9, 163, 200);

        Color pressed = new ColorUIResource(5, 141, 192);

        Color rollver = new ColorUIResource(75, 202, 255);

        Color font = new ColorUIResource(Color.WHITE);

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }

    /**
     * Yellow button color configuration information
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getNaturalsBtnInfo()
    {
        Color normal = new ColorUIResource(203, 143, 81);

        Color pressed = new ColorUIResource(203, 132, 40);

        Color rollver = new ColorUIResource(245, 171, 84);

        Color font = new ColorUIResource(Color.WHITE);

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }

    /**
     * Purple button color configuration information
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getVioletBtnInfo()
    {
        Color normal = new ColorUIResource(76, 76, 177);

        Color pressed = new ColorUIResource(50, 43, 200);

        Color rollver = new ColorUIResource(91, 88, 241);

        Color font = new ColorUIResource(Color.WHITE);

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }

    /**
     * Green button color configuration information
     *
     * @return <code>LuckButtonColorInfo</code>
     */
    public static LuckButtonColorInfo getGreenBtnInfo()
    {
        Color normal = new ColorUIResource(39, 214, 138);

        Color rollver = new ColorUIResource(81, 249, 183);

        Color pressed = new ColorUIResource(36, 207, 145);

        Color font = new ColorUIResource(Color.WHITE);

        return new LuckButtonColorInfo(normal, rollver, pressed, font);
    }
}
