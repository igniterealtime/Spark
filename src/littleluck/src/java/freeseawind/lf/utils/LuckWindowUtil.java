package freeseawind.lf.utils;

import java.awt.Dialog;
import java.awt.Image;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 * Window tool class.
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckWindowUtil
{
    /**
     * get window title.
     *
     * @param window 
     * @return title.
     */
    public static String getWindowTitle(Window window)
    {
        if(window instanceof JFrame)
        {
            return ((JFrame)window).getTitle();
        }

        if(window instanceof JDialog)
        {
            return ((JDialog)window).getTitle();
        }

        return null;
    }

    /**
     * get window iconImage.
     *
     * @param window 
     * @return iconImage.
     */
    public static Image getWindowImage(Window window)
    {
        try
        {
            if (window instanceof JFrame)
            {
                return ((JFrame) window).getIconImage();
            }

            if (window instanceof JDialog)
            {
                return ((JDialog) window).getIconImages().get(0);
            }
        }
        catch (Exception e)
        {

        }

        return null;
    }

    /**
     * get window's rootPane.
     *
     * @param window 
     * @return rootPane.
     */
    public static JRootPane getRootPane(Window window)
    {
        if(window instanceof JFrame)
        {
            return ((JFrame)window).getRootPane();
        }

        if(window instanceof JDialog)
        {
            return ((JDialog)window).getRootPane();
        }

        return null;
    }

    /**
     * check window is resizable enable.
     *
     * @param window 
     * @return enable return true,otherwise false.
     */
    public static boolean isResizable(Window window)
    {
        JFrame frame = null;
        JDialog dialog = null;

        if (window instanceof JFrame)
        {
            frame = (JFrame) window;

            return (frame.isResizable() && (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH));
        }
        else if (window instanceof Dialog)
        {
            dialog = (JDialog) window;

            return (dialog != null && dialog.isResizable());

        }

        return false;
    }

    /**
     * Window transformation <code>JFrame</code>
     *
     * @param window 
     * @return if <code>window instanceof JFrame</code> return <code>JFrame</code>,otherwise return null.
     */
    public static JFrame getJFrame(Window window)
    {
        if (window instanceof JFrame)
        {
            return (JFrame) window;
        }

        return null;
    }
}
