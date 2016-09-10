package freeseawind.lf.event;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.border.LuckShapeBorder;

/**
 * 焦点边框监听器
 *
 * @author freeseawind@github
 * @version 1.0
 */
public abstract class LuckBorderFocusHandle extends LuckFocusHandle
{
    public LuckBorderFocusHandle()
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        if(isLuckLineBorder())
        {
            handleFocusGained();
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        Component source = (Component) e.getSource();

        if (isLuckLineBorder() && !source.contains(e.getPoint()))
        {
            handleFocusLost();
        }
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        if(isLuckLineBorder())
        {
            handleFocusLost();
        }
    }

    /**
     * 获取事件源
     *
     * @return <code>JComponent</code>获取焦点的组件
     */
    public abstract JComponent getComponent();

    /**
     * 获取边框属性
     *
     * @return <code>LuckBorderField</code>
     */
    public abstract LuckBorderField getBorderField();

    /**
     * 失去焦点事件
     */
    protected void handleFocusLost()
    {
        if(getBorderField().isFoucusGaind() && !getComponent().isFocusOwner())
        {
            getBorderField().setFocusGained(false);

            getComponent().repaint();
        }
    }

    /**
     * 获取焦点事件
     */
    protected void handleFocusGained()
    {
        if(!getBorderField().isFoucusGaind())
        {
            getBorderField().setFocusGained(true);

            getComponent().repaint();
        }
    }

    /**
     * 判断是否是指定的焦点边框
     *
     * @return
     */
    private boolean isLuckLineBorder()
    {
        if (getComponent().getBorder() instanceof LuckShapeBorder)
        {
            return true;
        }

        return false;
    }
}
