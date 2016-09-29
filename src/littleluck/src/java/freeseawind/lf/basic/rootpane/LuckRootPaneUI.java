package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.event.WindowMouseHandler;
import freeseawind.lf.geom.LuckRectangle;

/**
 * <p>RootPaneUI实现类， 使用完全不透明面板解决字体反走样问题。</p>
 *
 *
 * 以下代码片段演示了如何使用图片作为窗体背景：
 *
 * <pre>
 * <code>
 *
 *  public class MyRootPaneUI extends {@link LuckRootPaneUI} {
 *
 *      public LuckBackgroundPanel createContentPane(LuckTitlePanel titlePane, Container oldContent)} {
 *
 *          {@link LuckBackgroundPanel} contentPane = super.createContentPane(titlePane, oldContent);
 *
 *          titlePane.setBackgroundNP(null);
 *
 *          contentPane.setPainter(new {@link LuckCanvas}() {
 *
 *          public void drawComponent(Graphics g, JComponent c) {
 *
 *              // draw image
 *              .........
 *          }
 *      });
 *      }
 *  }
 *
 *
 * </code>
 * </pre>
 *
 * <p>
 * 另请参见 {@link LuckBackgroundPanel}，{@link LuckBackgroundPanel}，
 * {@link LuckRootPaneUIBundle}
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRootPaneUI extends BasicRootPaneUI
{
    // 改变窗体装饰事件
    public static final String WINDOWDECORATIONSTYLE_EVENT = "windowDecorationStyle";

    public static final String ANCESTOR_EVENT = "ancestor";

    private static final String[] borderKeys = new String[] { null,
            LuckRootPaneUIBundle.FRAME_BORDER,
            LuckRootPaneUIBundle.PLAINDIALOG_BORDER,
            LuckRootPaneUIBundle.INFORMATIONDIALOG_BORDER,
            LuckRootPaneUIBundle.ERRORDIALOG_BORDER,
            LuckRootPaneUIBundle.COLORCHOOSERDIALOG_BORDER,
            LuckRootPaneUIBundle.FILECHOOSERDIALOG_BORDER,
            LuckRootPaneUIBundle.QUESTIONDIALOG_BORDER,
            LuckRootPaneUIBundle.WARNINGDIALOG_BORDER };

    /**
     * <code>MouseInputListener</code> that is added to the parent
     * <code>Window</code> the <code>JRootPane</code> is contained in.
     */
    private WindowMouseHandler mouseInputListener;

    /**
     * The <code>LayoutManager</code> that is set on the <code>JRootPane</code>.
     */
    private LayoutManager layoutManager;

    /**
     * <code>LayoutManager</code> of the <code>JRootPane</code> before we
     * replaced it.
     */
    private LayoutManager savedOldLayout;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckRootPaneUI();
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);

        JRootPane root = (JRootPane) c;

        int style = root.getWindowDecorationStyle();

        if (style != JRootPane.NONE)
        {
            installClientDecorations(root);
        }
    }

    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        uninstallClientDecorations((JRootPane) c);
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        super.propertyChange(e);

        String propertyName = e.getPropertyName();

        if (propertyName == null)
        {
            return;
        }

        JRootPane root = (JRootPane) e.getSource();
        
        Container parent = root.getParent();

        if (!(parent instanceof Window))
        {
            return;
        }

        if (WINDOWDECORATIONSTYLE_EVENT.equals(propertyName))
        {
            int style = root.getWindowDecorationStyle();

            uninstallClientDecorations(root);

            if (style != JRootPane.NONE)
            {
                installClientDecorations(root);
            }
        }
        else if (ANCESTOR_EVENT.equals(propertyName))
        {
            uninstallWindowListener(root);

            if (((JRootPane) e.getSource()).getWindowDecorationStyle() != JRootPane.NONE)
            {
                installWindowListeners(root);
            }
        }
    }

    /**
     * 创建JRootPane布局
     *
     * @return <code>LayoutManager</code>自定义布局管理器
     */
    public LayoutManager createLayout()
    {
        // 使用自定义布局器
        return new LuckRootPaneLayout();
    }
    
    /**
     * 设置面板可拖动区域
     * 
     * @param dragArea
     */
    public void setDragArea(LuckRectangle dragArea)
    {
        mouseInputListener.setDragArea(dragArea);
    }
    
    /**
     * 创建标题面板
     * 
     * @param isResizeableOnInit
     * @param initStyle
     * @return
     */
    protected LuckTitlePanel createTitlePanel(int initStyle,
                                              boolean isResizeableOnInit)
    {
        return new LuckTitlePanel(isResizeableOnInit, initStyle);
    }


    /**
     * 创建JRootPane内容面板
     * 
     * @param titlePanel 标题面板
     * @param oldContent 默认内容面板
     * @return <code>LuckBackgroundPanel</code>新的内容面板
     */
    protected LuckBackgroundPanel createContentPane(LuckTitlePanel titlePanel,
                                                    Container oldContent)
    {
        return new LuckBackgroundPanel(oldContent, titlePanel);
    }

    /**
     * 创建窗体鼠标监听器
     * 
     * @param root 窗体所包含的根窗格
     * @return <code>MouseInputListener</code>窗体移动、缩放、和点击放大缩小鼠标监听器
     */
    protected MouseInputListener installWindowListeners(JRootPane root)
    {
        Window window = SwingUtilities.getWindowAncestor(root);

        if (window != null)
        {
            if (mouseInputListener == null)
            {
                mouseInputListener = new WindowMouseHandler(root);
            }

            window.addMouseListener(mouseInputListener);

            window.addMouseMotionListener(mouseInputListener);
        }

        return mouseInputListener;
    }

    /**
     * 移除窗体鼠标监听器
     * 
     * @param root 根窗格
     */
    protected void uninstallWindowListener(JRootPane root)
    {
        Window window = SwingUtilities.getWindowAncestor(root);

        if (window != null && mouseInputListener != null)
        {
            window.removeMouseListener(mouseInputListener);

            window.removeMouseMotionListener(mouseInputListener);
        }
    }

    private void installClientDecorations(JRootPane root)
    {
        installBorder(root);

        installWindowListeners(root);

        installLayout(root);

        // 设置窗体为完全透明
        Window window = (Window) root.getParent();

        window.setBackground(UIManager.getColor(LuckRootPaneUIBundle.ROOTPANE_BACKGROUND_COLOR));
    }

    private void uninstallClientDecorations(JRootPane root)
    {
        uninstallBorder(root);

        uninstallWindowListener(root);

        uninstallLayout(root);
    }

    /**
     * 给根窗格安装边框
     *
     * @param root
     */
    private void installBorder(JRootPane root)
    {
        int style = root.getWindowDecorationStyle();

        // 这句必须，否则会出现无法安装边框的情况
        root.setBorder(null);

        root.setBorder(UIManager.getBorder(borderKeys[style]));
    }

    /**
     * 去除窗格边框
     *
     * @param root
     */
    private void uninstallBorder(JRootPane root)
    {
        LookAndFeel.uninstallBorder(root);

        root.setBorder(null);
    }

    /**
     * 安装布局
     *
     * @param root
     */
    private void installLayout(JRootPane root)
    {
        if (layoutManager == null)
        {
            layoutManager = createLayout();
        }

        savedOldLayout = root.getLayout();

        root.setLayout(layoutManager);
    }

    private void uninstallLayout(JRootPane root)
    {
        if (savedOldLayout != null)
        {
            root.setLayout(savedOldLayout);
            
            savedOldLayout = null;
        }

        layoutManager = null;

        Container content = root.getContentPane();

        if (content != null && content instanceof LuckBackgroundPanel)
        {
            LuckBackgroundPanel bgPanel = (LuckBackgroundPanel) content;
            
            root.setContentPane(bgPanel.getContentPane());
            
            root.setJMenuBar(bgPanel.getJMenuBar());
            
            bgPanel.uninstallMenubar(true);
        }
        
        int style = root.getWindowDecorationStyle();
        
        if (style == JRootPane.NONE) 
        {
            root.repaint();
            
            root.revalidate();
        }
    }
}
