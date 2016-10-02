package freeseawind.lf.basic.internalframe;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

/**
 * <p>
 * 内部窗口托盘图标UI实现类, 重写{@link BasicDesktopIconUI#installComponents()}方法, 使用自定义边框。
 * </p>
 *
 * <p>
 * The internal window tray icon UI implementation class, overriding the
 * {@link BasicDesktopIconUI#installComponents()} , using a custom border
 * </p>
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

    /**
     * <p>重写该方法，使用自定义边框</p>
     *
     * <p>Override the method to use a custom border</p>
     */
    protected void installComponents()
    {
        iconPane = new LuckInternalFrameTitlePane(frame);

        desktopIcon.setLayout(new BorderLayout());

        desktopIcon.add(iconPane, BorderLayout.CENTER);

        // install border
        desktopIcon.setBorder(UIManager.getBorder(LuckInternalFrameUIBundle.BORDER));
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
}
