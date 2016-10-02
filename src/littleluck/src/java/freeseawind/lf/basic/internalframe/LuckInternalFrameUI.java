package freeseawind.lf.basic.internalframe;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * <p>
 * 内部窗体UI实现， 设置组件为不完全透明，使用自定义标题面板。
 * </p>
 *
 * <p>
 * Inside the form UI implementation, set the component to be not completely
 * transparent, using the customize title panel.
 * </p>
 *
 * @see LuckInternalFrameTitlePane
 * @see LuckInternalFrameUIBundle
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckInternalFrameUI extends BasicInternalFrameUI
{
    public LuckInternalFrameUI(JInternalFrame b)
    {
        super(b);
    }

    public static ComponentUI createUI(JComponent b)
    {
        return new LuckInternalFrameUI((JInternalFrame) b);
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);

        LookAndFeel.installProperty(frame, "opaque", Boolean.FALSE);
    }

    @Override
    protected JComponent createNorthPane(JInternalFrame w)
    {
        titlePane = new LuckInternalFrameTitlePane(w);

        return titlePane;
    }
}
