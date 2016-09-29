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
 * window property listener.
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

        // change title text.
        if(WindowPropertyEventType.TITLE_EVENT.equals(name))
        {
            handleTitleEvent(window);

            return;
        }

        // change window icon.
        if(WindowPropertyEventType.ICONIMAGE_EVENT.equals(name))
        {
            handleIconEvent(window);

            return;
        }

        // resize window.
        if(WindowPropertyEventType.RESIZABLE_EVENT.equals(name))
        {
        	handleResizableEvent(window);

            return;
        }

        // change window orientation.
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
