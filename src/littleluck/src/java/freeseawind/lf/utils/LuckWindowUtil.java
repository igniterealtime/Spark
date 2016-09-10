package freeseawind.lf.utils;

import java.awt.Dialog;
import java.awt.Image;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 * 主要是获取当前窗体的一些简化操作
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckWindowUtil
{
    /**
     * 获取窗体标题
     *
     * @param window 窗体
     * @return 窗体标题
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
     * 获取窗体图标
     *
     * @param window 窗体
     * @return 窗体图标
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
     * 获取窗体根窗格
     *
     * @param window 窗体
     * @return 返回窗体根窗格
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
     * 判断窗体是否可以拉伸
     *
     * @param window 窗体
     * @return 可以拉伸返回true，否则false
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
     * 窗体转为<code>JFrame</code>
     *
     * @param window 窗体
     * @return 如果窗体是<code>JFrame</code>返回<code>JFrame</code>，否则NULL
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
