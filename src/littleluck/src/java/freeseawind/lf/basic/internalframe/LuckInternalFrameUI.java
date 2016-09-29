package freeseawind.lf.basic.internalframe;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * 内部窗体UI实现， 设置组件为不完全透明，使用自定义标题面板。
 * <p>
 * 另请参见: {@link LuckInternalFrameTitlePane}, {@link LuckInternalFrameUIBundle}
 * </p>
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

    public void installUI(JComponent c)
    {
        super.installUI(c);

        LookAndFeel.installProperty(frame, "opaque", Boolean.FALSE);
    }

    protected JComponent createNorthPane(JInternalFrame w)
    {
        titlePane = new LuckInternalFrameTitlePane(w);

        return titlePane;
    }

    protected void installComponents()
    {
        super.installComponents();
    }
}
