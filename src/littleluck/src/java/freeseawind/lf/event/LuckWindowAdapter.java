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
 * Window state listener. set border null when is max, otherwise set border.
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
