package freeseawind.lf.event;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;

import freeseawind.lf.basic.rootpane.LuckBackgroundPanel;
import freeseawind.lf.basic.rootpane.LuckRootPaneUIBundle;

/**
 * 窗体状态变化监听器, 窗体最大化时去除边框，达到完全全屏的效果，否则会有空隙出现(实际是边框阴影)。在恢复正常状态时，重置边框。
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckWindowAdapter extends WindowAdapter
{
    public LuckWindowAdapter()
    {

    }

    public void windowStateChanged(WindowEvent e)
    {
        Window window = (Window) e.getSource();

        if (window instanceof JFrame)
        {
            JFrame frame = (JFrame) window;

            JRootPane rootPane = frame.getRootPane();

            LuckBackgroundPanel bgPanel = (LuckBackgroundPanel) rootPane.getContentPane();

            bgPanel.getTitlePanel().setState(e.getNewState());

            if (e.getNewState() == JFrame.MAXIMIZED_BOTH)
            {
            	rootPane.setBorder(null);
            }
            else if (e.getNewState() == JFrame.NORMAL)
            {
            	rootPane.setBorder(UIManager.getBorder(LuckRootPaneUIBundle.FRAME_BORDER));
            }
        }
    }
}
