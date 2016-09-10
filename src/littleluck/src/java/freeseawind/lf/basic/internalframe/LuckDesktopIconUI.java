package freeseawind.lf.basic.internalframe;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

/**
 * 内部窗口托盘图标UI实现类, 重写installComponents()方法, 使用自定义边框
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckDesktopIconUI extends BasicDesktopIconUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckDesktopIconUI();
    }

    public Dimension getPreferredSize(JComponent c)
    {
        return getMinimumSize(c);
    }

    public Dimension getMinimumSize(JComponent c)
    {
        return new Dimension(UIManager.getInt("DesktopIcon.width"),
                desktopIcon.getLayout().minimumLayoutSize(desktopIcon).height);
    }

    protected void installComponents()
    {
        iconPane = new LuckInternalFrameTitlePane(frame);

        desktopIcon.setLayout(new BorderLayout());

        desktopIcon.add(iconPane, BorderLayout.CENTER);

        desktopIcon.setBorder(UIManager.getBorder(LuckInternalFrameUIBundle.BORDER));
    }
}
