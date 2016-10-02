package freeseawind.lf.basic.rootpane;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JMenuBar;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.swing.LuckPanel;

/**
 * <p>
 * RootPane的背景面板，使用它替换默认内容面板主要是为了解决透明窗体照成字体渲染反走样問題。因為面板為完全不透明，所帶來的
 * 問題是對圓角或不規則的面板支持不是很友好。也许这是一个只能从UI设计上解决的问题，让显示区域和边界区域保持一定的间距，是目前能想到的一种解决方案。
 * </p>
 * 
 * <p>
 * RootPane background panel, use it to replace the default content is mainly to
 * solve the transparent window panel according to the anti-aliasing font
 * rendering problem. Because the panel is completely opaque, brought Is the
 * problem of irregular or rounded panel support is not very friendly. Perhaps
 * this is only a settlement from the UI design problems, so that the display
 * area and the border region to maintain a certain distance, is able to think
 * of a solution.
 * </p>
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckBackgroundPanel extends LuckPanel
{
    // 内容面板
    // RootPane's content.
    private Container contentPane;

    // 标题面板
    // LittleLuck title panel.
    private LuckTitlePanel titlePanel;

    // 系统菜单
    // RootPane's menu bar.
    private JMenuBar menuBar;

    // 重绘界面时的回调接口,增加的原因是绘制一个背景
    // draw call back.
    private LuckCanvas painter;

    private static final long serialVersionUID = 5279628284333016497L;

    public LuckBackgroundPanel(Container content, LuckTitlePanel titlePanel)
    {
        setBorder(null);

        super.setLayout(createLayout());

        setBackground(Color.WHITE);

        this.contentPane = content;

        setTitlePanel(titlePanel);

        super.add(contentPane);
    }

    /**
     * Transfer JMenubar from JRootPane.
     * 
     * @param menuBar
     */
    public void installJMenubar(JMenuBar menuBar)
    {
        if (menuBar != null && !menuBar.equals(this.menuBar))
        {
            this.menuBar = menuBar;

            super.add(menuBar);
        }
    }

    /**
     * remove JMenubar.
     * 
     * @param isRemove
     */
    public void uninstallMenubar(boolean isRemove)
    {
        if (menuBar == null)
        {
            return;
        }

        if (isRemove)
        {
            super.remove(menuBar);

            menuBar = null;
        }
    }

    /**
     * create LuckBackground layout manager.
     * 
     * @return
     */
    public LayoutManager createLayout()
    {
        return new LuckBgPanelLayout();
    }

    /**
     * <p>重写该方法，增加回调接口。</p>
     * 
     * <p>Override this method to increase the callback interface.</p>
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (painter != null)
        {
            painter.drawComponent(g, this);
        }
    }

    /**
     *
     * @return <code>LuckTitlePanel</code>
     */
    public LuckTitlePanel getTitlePanel()
    {
        return titlePanel;
    }

    /**
     *
     * @return RootPane's actual content pane.
     */
    public Container getContentPane()
    {
        return contentPane;
    }

    /**
     * set draw call back interface.
     *
     * @param painter
     */
    public void setPainter(LuckCanvas painter)
    {
        this.painter = painter;
    }

    /**
     * <p>重新设置标题面板，覆盖原有面板。<p>
     * 
     * <p>Re-set the title panel.<p>
     *
     * @param titlePanel <code>LuckTitlePanel</code>
     */
    public void setTitlePanel(LuckTitlePanel titlePanel)
    {
        if (this.titlePanel != null && this.titlePanel.equals(titlePanel))
        {
            return;
        }

        this.titlePanel = titlePanel;

        super.add(titlePanel);
    }

    protected JMenuBar getJMenuBar()
    {
        return menuBar;
    }
}
