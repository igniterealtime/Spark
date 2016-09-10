package freeseawind.lf.basic.combobox;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JComponent;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.controller.LuckArrowButton;

/**
 * Combobox下拉按钮,没有找到合适的图片素材替换(T T), 这里使用Java2D绘制的箭头按钮实现类
 *
 * <p>另请参见 {@link LuckArrowButton},{@link LuckBorderField}</p>
 *
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

    public void paintTriangle(Graphics g,
                              int x,
                              int y,
                              int size,
                              int direction)
    {
        super.paintTriangle(g, x, y, size, direction);
    }

    /**
     * 获取父容器的边框属性
     *
     * @return LuckBorderField
     */
    public abstract LuckBorderField getBorderField();

    /**
     * 获取父容器
     *
     * @return 父容器对象信息
     */
    public abstract JComponent getParentComp();

    /**
     * 重写方法, 给父容器传递焦点事件
     *
     * @param g 图形画笔对象
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }

    /**
     * 获取箭头颜色
     *
     * @param model 按钮状态模型
     * @return 无状态颜色或高亮颜色(鼠标进过或点击)
     */
    protected Color getArrowColor(ButtonModel model)
    {
        if (getModel().isPressed() || getModel().isRollover()
                || getBorderField().isFoucusGaind())
        {
            return highlight;
        }

        return normal;
    }
}
