package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JMenuBar;
import javax.swing.JRootPane;

import freeseawind.lf.layout.AbstractLayout;

/**
 * 根窗格背景面板布局类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckBgPanelLayout extends AbstractLayout
{
    public void layoutContainer(Container parent)
    {
        LuckBackgroundPanel root = (LuckBackgroundPanel) parent;

        Rectangle bound = root.getBounds();

        Insets inset = root.getInsets();

        // 获取内容面板实际宽度, 减去左右边框面积
        int w = bound.width - inset.right - inset.left;

        // 获取内容面板实际高度, 减去上下边框面积
        int h = bound.height - inset.top - inset.bottom;

        int nextY = inset.top;

        // 避免无用的绘制
        if (w <= 0 || h <= 0)
        {
            return;
        }

        // 先布局标题面板
        LuckTitlePanel titlePanel = root.getTitlePanel();

        // 如果未取消窗体装饰
		if (root.getRootPane() != null && titlePanel != null
		        && JRootPane.NONE != root.getRootPane().getWindowDecorationStyle())
		{
            titlePanel.setBounds(inset.left, inset.top, w, titlePanel.getHeight());

            nextY += titlePanel.getHeight();
        }

        // 布局JMenuBar
        JMenuBar menuBar = root.getRootPane().getJMenuBar();

        if(menuBar != null && menuBar.isVisible())
        {
        	root.installJMenubar(menuBar);

        	menuBar.setBounds(inset.left, nextY, w, menuBar.getPreferredSize().height);

            nextY += menuBar.getPreferredSize().getHeight();
        }

        //
        if(menuBar == null || !menuBar.isVisible())
        {
        	root.uninstallMenubar(menuBar == null);
        }

        // 布局内容面板
        Container contentPane = root.getContentPane();

        contentPane.setBounds(inset.left, nextY, w, h - nextY);
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        LuckBackgroundPanel baPanel = (LuckBackgroundPanel) parent;

        int h = 0;

        // 标题面板大小
        if (JRootPane.NONE != baPanel.getRootPane().getWindowDecorationStyle())
        {
            Dimension titleDm = baPanel.getTitlePanel().getPreferredSize();

            h += titleDm.height;
        }

        if (baPanel.getRootPane().getJMenuBar() != null
                && baPanel.getRootPane().getJMenuBar().isVisible())
        {
            Dimension menuBarDm = baPanel.getRootPane().getJMenuBar().getPreferredSize();

            h += menuBarDm.height;
        }

        Dimension contentDm = baPanel.getContentPane().getPreferredSize();

        h += contentDm.height;

        return getDimension(insets, contentDm.width, h);
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        LuckBackgroundPanel baPanel = (LuckBackgroundPanel) parent;

        int h = 0;

        // 标题面板大小
        if (JRootPane.NONE != baPanel.getRootPane().getWindowDecorationStyle())
        {
            Dimension titleDm = baPanel.getTitlePanel().getMinimumSize();

            h += titleDm.height;
        }

        if (baPanel.getRootPane().getJMenuBar() != null
                && baPanel.getRootPane().getJMenuBar().isVisible())
        {
            Dimension menuBarDm = baPanel.getRootPane().getJMenuBar().getMinimumSize();

            h += menuBarDm.height;
        }

        Dimension contentDm = baPanel.getContentPane().getMinimumSize();

        h += contentDm.height;

        return getDimension(insets, contentDm.width, h);
    }

    public Dimension maximumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        LuckBackgroundPanel baPanel = (LuckBackgroundPanel) parent;

        int h = 0;

        // 标题面板大小
        if (JRootPane.NONE != baPanel.getRootPane().getWindowDecorationStyle())
        {
            Dimension titleDm = baPanel.getTitlePanel().getMaximumSize();

            h += titleDm.height;
        }

        if (baPanel.getRootPane().getJMenuBar() != null
                && baPanel.getRootPane().getJMenuBar().isVisible())
        {
            Dimension menuBarDm = baPanel.getRootPane().getJMenuBar().getMaximumSize();

            h += menuBarDm.height;
        }

        Dimension contentDm = baPanel.getContentPane().getMaximumSize();

        h += contentDm.height;

        return getDimension(insets, contentDm.width, h);
    }
}
