package freeseawind.lf.basic.optionpane;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

import freeseawind.lf.canvas.LuckCanvas;

/**
 * <p>
 * 弹出窗口UI实现类，设置组件为不完全透明， 实现弹出窗口背景重绘。
 * </p>
 *
 * <p>
 * Pop-up window UI implementation class, set the component is not completely
 * transparent, to achieve a pop-up window background redraw.
 * </p>
 *
 * <p>
 * See Also:{@link LuckOptionPaneUIBundle}.
 * </p>
 *
 * @see LuckCanvas
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckOptionPaneUI extends BasicOptionPaneUI
{
    private LuckCanvas painter;

    public static ComponentUI createUI(JComponent x)
    {
        return new LuckOptionPaneUI();
    }

    @Override
    public void paint(Graphics g, JComponent c)
    {
        super.paint(g, c);

        if(painter != null)
        {
            painter.drawComponent(g, c);
        }
    }

    protected void installDefaults()
    {
        super.installDefaults();

        LookAndFeel.installProperty(optionPane, "opaque", Boolean.FALSE);
    }

    /**
     * 重写该方法，设置内容显示区域为不完全透明
     */
    @Override
    protected Container createMessageArea()
    {
        JComponent messageArea = (JComponent) super.createMessageArea();

        messageArea.setOpaque(false);

        JComponent realBody = (JComponent) messageArea.getComponent(0);

        realBody.setOpaque(false);

        for(Component child : realBody.getComponents())
        {
            ((JComponent)child).setOpaque(false);
        }

        return messageArea;
    }

    /**
     * 重写该方法，设置按钮区域为不完全透明
     */
    @Override
    protected Container createButtonArea()
    {
        JPanel panel = (JPanel) super.createButtonArea();

        panel.setOpaque(false);

        return panel;
    }

    /**
     * 设置绘图回调接口
     * @param painter 绘图回调接口
     */
    public void setPainter(LuckCanvas painter)
    {
        this.painter = painter;
    }
}
