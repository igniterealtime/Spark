package freeseawind.lf.basic.button;

import java.awt.Color;
import java.io.Serializable;

/**
 * Button color attribute class
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckButtonColorInfo implements Serializable
{
    private static final long serialVersionUID = -6352677081768535770L;

    // Initial state color
    private Color normalColor;

    // The color of the mouse over time
    private Color rollverColor;

    // The color of the mouse click
    private Color pressedColor;

    // The Button font color
    private Color fontColor;

    /**
     *
     * @param normal Initial state color
     *
     * @param rollver The color of the mouse over time
     *
     * @param pressed The color of the mouse click
     *
     * @param font The Button font color
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
     * @return <code>Color</code>
     */
    public Color getNormalColor()
    {
        return normalColor;
    }

    /**
     *
     * @return <code>Color</code>
     */
    public Color getRollverColor()
    {
        return rollverColor;
    }

    /**
     *
     * @return <code>Color</code>
     */
    public Color getPressedColor()
    {
        return pressedColor;
    }

    /**
     *
     * @return <code>Color</code>
     */
    public Color getFontColor()
    {
        return fontColor;
    }
}
