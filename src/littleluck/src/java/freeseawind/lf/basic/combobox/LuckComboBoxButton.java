package freeseawind.lf.basic.combobox;

import java.awt.Color;

import javax.swing.ButtonModel;
import javax.swing.JComponent;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.controller.LuckArrowButton;

/**
 * Combobox下拉按钮,没有找到合适的图片素材替换(T T), 这里使用Java2D绘制的箭头按钮实现类
 *
 * @see LuckArrowButton
 * @see LuckBorderField
 * @author freeseawind@github
 * @version 1.0
 *
 */
public abstract class LuckComboBoxButton extends LuckArrowButton
{
    private static final long serialVersionUID = 7344386766250021707L;

    public LuckComboBoxButton(int direction)
    {
        super(direction);
    }

    /**
     * Gets the border property of the parent container
     *
     * @return <code>LuckBorderField</code>
     */
    public abstract LuckBorderField getBorderField();

    /**
     * Gets the parent container
     *
     * @return <code>JComponent</code>
     */
    public abstract JComponent getParentComp();

    /**
     * Gets the arrow color
     *
     * @param model <code>ButtonModel</code>
     * @return <code>Color</code> Gets the color in the current state.
     */
    protected Color getArrowColor(ButtonModel model)
    {
        if (getModel().isPressed() || getModel().isRollover()
                || getBorderField().isFocusGaind())
        {
            return highlight;
        }

        return normal;
    }
}
