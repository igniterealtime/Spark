package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import freeseawind.lf.layout.AbstractLayout;
import freeseawind.lf.utils.LuckWindowUtil;

/**
 * <p>RootPane布局实现类。</p>
 * 
 * <p>RootPane layout manager implement class.</p>
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
        // Calculate the actual width
        int w = bound.width - inset.right - inset.left;

        // 获取内容面板实际高度, 减去上下边框面积
        int h = bound.height - inset.top - inset.bottom;

        // 设置层级面板在根窗格中的位置
        // Calculate the actual height
        if(root.getLayeredPane() != null)
        {
            root.getLayeredPane().setBounds(inset.left, inset.top, w, h);
        }

        // 布局玻璃窗格
        // layout LayeredPane
        if(root.getGlassPane() != null)
        {
            root.getGlassPane().setBounds(inset.left, inset.top, w, h);
        }

        // 获取当前内容面板
        // get current ContentPane
        Container content = root.getContentPane();

        LuckRootPaneUI rootPaneUI = (LuckRootPaneUI) root.getUI();

        // 使用 <code>LuckBackgroundPanel</code>替换当前的内容面板
        // Use <code>LuckBackgroundPanel</code> replace the current contents of the panel
        if(!(content instanceof LuckBackgroundPanel))
        {
            Window window = SwingUtilities.getWindowAncestor(root);

           	boolean isResizeableOnInit = LuckWindowUtil.isResizable(window);

        	int initStyle = root.getWindowDecorationStyle();

            if(initStyle != JRootPane.NONE)
            {
                //
                LuckTitlePanel titlePanel = rootPaneUI.createTitlePanel(initStyle, isResizeableOnInit);

                LuckBackgroundPanel background = rootPaneUI.createContentPane(titlePanel, content);

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
