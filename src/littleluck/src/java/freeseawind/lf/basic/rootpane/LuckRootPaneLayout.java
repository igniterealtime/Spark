package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import freeseawind.lf.event.WindowPropertyListener;
import freeseawind.lf.layout.AbstractLayout;
import freeseawind.lf.utils.LuckWindowUtil;

/**
 * 根窗格布局类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRootPaneLayout extends AbstractLayout
{
    public void layoutContainer(Container parent)
    {
        JRootPane root = (JRootPane) parent;
        Rectangle bound = root.getBounds();
        Insets inset = root.getInsets();

        // 获取内容面板实际宽度, 减去左右边框面积
        int w = bound.width - inset.right - inset.left;

        // 获取内容面板实际高度, 减去上下边框面积
        int h = bound.height - inset.top - inset.bottom;

        // 设置层级面板在根窗格中的位置
        if(root.getLayeredPane() != null)
        {
            root.getLayeredPane().setBounds(inset.left, inset.top, w, h);
        }

        // 玻璃窗格是在层级面板中,所以坐标从(0, 0)开始
        if(root.getGlassPane() != null)
        {
            root.getGlassPane().setBounds(inset.left, inset.top, w, h);
        }

        // 获取当前内容面板
        Container content = root.getContentPane();

        LuckRootPaneUI rootPaneUI = (LuckRootPaneUI) root.getUI();

        if(!(content instanceof LuckBackgroundPanel))
        {
            Window window = SwingUtilities.getWindowAncestor(root);

           	boolean isResizeableOnInit = LuckWindowUtil.isResizable(window);

        	int initStyle = root.getWindowDecorationStyle();

            if(initStyle != JRootPane.NONE)
            {
                //
                LuckTitlePanel titlePanel = rootPaneUI.createTitlePanel(initStyle, isResizeableOnInit);

                titlePanel.setTitle(LuckWindowUtil.getWindowTitle(window));

                Image img = LuckWindowUtil.getWindowImage(window);

                if(img != null)
                {
                    titlePanel.setIcon(new ImageIcon(img));
                }

                LuckBackgroundPanel background = rootPaneUI.createContentPane(titlePanel, content);

                window.addPropertyChangeListener(new WindowPropertyListener(background.getTitlePanel()));
                
                root.remove(content);

                root.setContentPane(background);
            }
        }

        root.getContentPane().setBounds(0, 0, w, h);
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        JRootPane root = (JRootPane) parent;

        int h = 0;

        Dimension cpd = null;

        if (root.getContentPane() != null)
        {
            cpd = root.getContentPane().getPreferredSize();

			if (!(root.getContentPane() instanceof LuckBackgroundPanel)
			        && root.getWindowDecorationStyle() != JRootPane.NONE)
			{
				h += UIManager.getInt(LuckRootPaneUIBundle.TITLEPANEL_HEIGHT);
			}
        }
        else
        {
            cpd = root.getSize();
        }

        h += cpd.height;

        return getDimension(insets, cpd.width, h);
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        JRootPane root = (JRootPane) parent;

        Dimension cpd = null;

        if (root.getContentPane() != null)
        {
            cpd = root.getContentPane().getMinimumSize();
        }
        else
        {
            cpd = root.getSize();
        }

        return getDimension(insets, cpd.width, cpd.height);
    }

    public Dimension maximumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        JRootPane root = (JRootPane) parent;

        Dimension cpd = null;

        if (root.getContentPane() != null)
        {
            cpd = root.getContentPane().getMaximumSize();
        }
        else
        {
            cpd = root.getSize();
        }

        return getDimension(insets, cpd.width, cpd.height);
    }
}
