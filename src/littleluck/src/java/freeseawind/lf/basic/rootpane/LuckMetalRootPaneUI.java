package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalRootPaneUI;

import freeseawind.lf.utils.LuckWindowUtil;

/**
 * <p>RootPaneUI实现类，此类参考{@link MetalRootPaneUI}实现。</p>
 * 
 * <p>RootPaneUI implement class, Such reference MetalRootPaneL achieve.</p>
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckMetalRootPaneUI extends LuckRootPaneUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckMetalRootPaneUI();
    }

    @Override
    public LayoutManager createLayout()
    {
        return new LuckMetalRootPaneLayout();
    }

    @Override
    protected LuckBackgroundPanel createContentPane(LuckTitlePanel titlePanel,
                                                    Container oldContent)
    {
        return null;
    }

    @Override
    protected void installClientDecorations(JRootPane root)
    {
        super.installClientDecorations(root);

        Window window = SwingUtilities.getWindowAncestor(root);

        boolean isResize = LuckWindowUtil.isResizable(window);

        int style = root.getWindowDecorationStyle();

        installTitlePane(root, createTitlePanel(style, isResize), window);
    }

    @Override
    protected void uninstallClientDecorations(JRootPane root)
    {
        super.uninstallClientDecorations(root);

        installTitlePane(root, null, null);
    }

    protected void installTitlePane(JRootPane root, LuckTitlePanel titlePane, Window window)
    {
        JLayeredPane layeredPane = root.getLayeredPane();

        JComponent oldTitlePane = getTitlePane();

        if (oldTitlePane != null)
        {
            oldTitlePane.setVisible(false);

            layeredPane.remove(oldTitlePane);
        }

        if (titlePane != null)
        {
            layeredPane.add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);

            titlePane.setVisible(true);
        }

        this.titlePane = titlePane;
    }
}
