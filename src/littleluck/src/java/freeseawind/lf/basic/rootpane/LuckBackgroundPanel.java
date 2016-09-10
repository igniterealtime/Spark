package freeseawind.lf.basic.rootpane;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.event.LuckWindowAdapter;

/**
 * <p>
 * RootPane的背景面板，使用它替换默认内容面板主要是为了解决透明窗体照成字体渲染反走样問題。因為面板為完全不透明，所帶來的
 * 問題是對圓角或不規則的面板支持不是很友好。也许这是一个只能从UI设计上解决的问题，让显示区域和边界区域保持一定的间距，是目前能想到的一种解决方案。
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckBackgroundPanel extends JPanel
{
    // 内容面板
    private Container contentPane;

    // 标题面板
    private LuckTitlePanel titlePanel;

    // 窗体变化状态监听器
    private WindowAdapter windowAdapter;

    // 当前容器所属的窗体
    private Window window;

    // 系统菜单
    private JMenuBar menuBar;

    // 重绘界面时的回调接口,增加的原因是绘制一个背景
    private LuckCanvas painter;

    private static final long serialVersionUID = 5279628284333016497L;

	public LuckBackgroundPanel(Container content, LuckTitlePanel titlePanel)
	{
        setBorder(null);

        setLayout(createLayout());

        setBackground(Color.WHITE);

        this.contentPane = content;
        
        setTitlePanel(titlePanel);

        add(contentPane);
    }

    @Override
    public void paint(Graphics g)
    {
        // 计算宽高和起始坐标
        Insets insets = this.getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        // 设置画布背景
        BufferedImage contentImage = new BufferedImage(width, height, Transparency.OPAQUE);

        Graphics2D contentG2d = contentImage.createGraphics();

        // 绘制边框和其它
        super.paint(contentG2d);

        Graphics2D g2d = (Graphics2D)g;

        g2d.drawImage(contentImage, x, y, width, height, null);

        // 释放资源
        contentG2d.dispose();
    }

    /**
     * 组件被添加至父容器时触发
     */
    public void addNotify()
    {
        super.addNotify();

        window = getWindow();

        if(window != null && window instanceof JFrame)
        {
            if(windowAdapter == null)
            {
                windowAdapter = new LuckWindowAdapter();
            }

            window.addWindowStateListener(windowAdapter);
        }
    }

    public void removeNotify()
    {
        super.removeNotify();

        if(window != null && windowAdapter != null && window instanceof JFrame)
        {
            window.removeWindowStateListener(windowAdapter);
        }

        window = null;

        windowAdapter = null;

        if(menuBar != null)
        {
        	remove(menuBar);
        }
    }

    public void installJMenubar(JMenuBar menuBar)
    {
    	if(menuBar != null && !menuBar.equals(this.menuBar))
    	{
    		this.menuBar = menuBar;

    		add(menuBar);
    	}
    }

    public void uninstallMenubar(boolean isRemove)
    {
    	if(menuBar == null)
    	{
    		return;
    	}

    	if(isRemove)
    	{
    		remove(menuBar);

    		menuBar = null;
    	}
    }

	public LayoutManager createLayout()
    {
        return new LuckBgPanelLayout();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(painter != null)
        {
        	painter.drawComponent(g, this);
        }
    }
    
    /**
     * 
     * @return 返回标题面板
     */
    public LuckTitlePanel getTitlePanel()
    {
        return titlePanel;
    }

    /**
     * 
     * @return 返回当前内容面板
     */
    public Container getContentPane()
    {
        return contentPane;
    }

    /**
     * 设置绘图回调接口
     * 
     * @param painter 绘图回调接口
     */
	public void setPainter(LuckCanvas painter)
	{
		this.painter = painter;
	}

    /**
     * 重新设置标题面板，覆盖原有面板
     * 
     * @param titlePanel 标题面板
     */
	public void setTitlePanel(LuckTitlePanel titlePanel)
	{
		if(this.titlePanel != null && this.titlePanel.equals(titlePanel))
		{
			return;
		}

		this.titlePanel = titlePanel;

		add(titlePanel);
	}
	
	protected JMenuBar getJMenuBar()
	{
	    return menuBar;
	}

    private Window getWindow()
    {
        return SwingUtilities.getWindowAncestor(this);
    }
}
