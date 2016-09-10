package freeseawind.lf.event;

import java.awt.Image;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import freeseawind.lf.basic.rootpane.LuckTitlePanel;
import freeseawind.lf.utils.LuckWindowUtil;

/**
 * 窗体属性变化监听器
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class WindowPropertyListener implements PropertyChangeListener
{
    private LuckTitlePanel titlePanel;

    public WindowPropertyListener(LuckTitlePanel titlePanel)
    {
        this.titlePanel = titlePanel;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();

        Window window = (Window)evt.getSource();

        // 改变标题
        if(WindowPropertyEventType.TITLE_EVENT.equals(name))
        {
            handleTitleEvent(window);

            return;
        }

        // 改变窗体图标
        if(WindowPropertyEventType.ICONIMAGE_EVENT.equals(name))
        {
            handleIconEvent(window);

            return;
        }

        // 改变拉伸状体
        if(WindowPropertyEventType.RESIZABLE_EVENT.equals(name))
        {
        	handleResizableEvent(window);

            return;
        }

        // 改变窗体布局
        if(WindowPropertyEventType.COMPONENTORIENTATION_EVENT.equals(name))
        {
            handleComponetOrientation(window);

            return;
        }
    }

    protected void handleTitleEvent(Window window)
    {
        String newTitle = LuckWindowUtil.getWindowTitle(window);

        titlePanel.setTitle(newTitle);
    }

    protected void handleIconEvent(Window window)
    {
        Image newImage = LuckWindowUtil.getWindowImage(window);

        titlePanel.setIcon(new ImageIcon(newImage));
    }

    protected void handleResizableEvent(Window window)
    {
        // 该事件只对JFrame有效
        if(window instanceof JFrame)
        {
            JFrame frame = (JFrame)window;

            titlePanel.setResizeable(frame.isResizable());
        }
    }

    protected void handleComponetOrientation(Window window)
	{
		titlePanel.revalidate();
		titlePanel.repaint();
	}
}
