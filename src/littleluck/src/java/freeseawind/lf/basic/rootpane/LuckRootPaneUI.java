package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.Cursor;
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
import freeseawind.lf.cfg.LuckGlobalBundle;
import freeseawind.lf.event.WindowMouseHandler;
import freeseawind.lf.geom.LuckRectangle;

/**
 * <p>RootPaneUI实现类， 使用完全不透明面板解决字体反走样问题。</p>
 * 
 * <p>RootPaneUI implement class, use {@link LuckBackgroundPanel} to replace default content.
 *    solve the problem of translucent window font.
 * </p>
 * 
 * The following code fragment demonstrates how to use the picture as a background form:
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
 * See Also: {@link LuckBackgroundPanel},{@link LuckBackgroundPanel},
 * {@link LuckRootPaneUIBundle}.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckRootPaneUI extends BasicRootPaneUI
{
    public static final String WINDOWDECORATIONSTYLE_EVENT = "windowDecorationStyle";

    public static final String ANCESTOR_EVENT = "ancestor";

    protected LuckTitlePanel titlePane;

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

    private static final String[] borderKeys = new String[] { null,
            LuckRootPaneUIBundle.FRAME_BORDER,
            LuckRootPaneUIBundle.PLAINDIALOG_BORDER,
            LuckRootPaneUIBundle.INFORMATIONDIALOG_BORDER,
            LuckRootPaneUIBundle.ERRORDIALOG_BORDER,
            LuckRootPaneUIBundle.COLORCHOOSERDIALOG_BORDER,
            LuckRootPaneUIBundle.FILECHOOSERDIALOG_BORDER,
            LuckRootPaneUIBundle.QUESTIONDIALOG_BORDER,
            LuckRootPaneUIBundle.WARNINGDIALOG_BORDER };

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
     * <p>创建JRootPane布局。</p>
     * 
     * <p>create JRootPane layout manager.</p>
     *
     * @return <code>LayoutManager</code> layout manager.
     */
    public LayoutManager createLayout()
    {
        // 使用自定义布局器
        // use custom layout manager.
        return new LuckRootPaneLayout();
    }

    /**
     * <p>设置窗体可拖动区域。</p>
     * 
     * <p>Set the form draggable area.</p>
     *
     * @param dragArea
     */
    public void setDragArea(LuckRectangle dragArea)
    {
        mouseInputListener.setDragArea(dragArea);
    }

    /**
     * <p>创建标题面板</p>
     * 
     * <p>create TitlePane</p>
     *
     * @param isResizeableOnInit
     * @param initStyle
     * @return
     */
    protected LuckTitlePanel createTitlePanel(int style, boolean isResize)
    {
        titlePane = new LuckTitlePanel(isResize, style);

        return titlePane;
    }


    /**
     * <p>创建JRootPane内容面板</p>
     * 
     * <p>Create JRootPane ContentPane to replace default ContentPane</p>
     *
     * @param titlePanel 
     * @param oldContent 
     * @return <code>LuckBackgroundPanel</code> new ContentPane
     */
    protected LuckBackgroundPanel createContentPane(LuckTitlePanel titlePanel,
                                                    Container oldContent)
    {
        return new LuckBackgroundPanel(oldContent, titlePanel);
    }

    /**
     * <p>创建窗体鼠标监听器, 处理窗体的移动和拖拽事件</p>
     * 
     * <p>Create Window mouse listener, handle window move and drag event.</p>
     *
     * @param root <code>JRootPane</code>
     * @return <code>MouseInputListener</code> window move and drag event listener.
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
     * <p>移除窗体鼠标监听器</p>
     * 
     * <p>Remove Window move and drag event listener.</p>
     *
     * @param root <code>JRootPane</code>
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

    protected void installClientDecorations(JRootPane root)
    {
        installBorder(root);

        installWindowListeners(root);

        installLayout(root);

        installOther(root);
    }

    protected void uninstallClientDecorations(JRootPane root)
    {
        uninstallBorder(root);

        uninstallWindowListener(root);

        uninstallLayout(root);

        uninstallOther(root);
    }

    /**
     * <p>给根窗格安装边框</p>
     * 
     * <p>To install JRootPane border</p>
     *
     * @param root <code>JRootPane</code>
     */
    protected void installBorder(JRootPane root)
    {
        int style = root.getWindowDecorationStyle();

        // 这句必须，否则会出现无法安装边框的情况
        // Sentence must, otherwise there will not be installed border situation
        root.setBorder(null);

        root.setBorder(UIManager.getBorder(borderKeys[style]));
    }

    /**
     * <p>安装布局</p>
     * 
     * <p>set JRootPane layout</p>
     *
     * @param root
     */
    protected void installLayout(JRootPane root)
    {
        if (layoutManager == null)
        {
            layoutManager = createLayout();
        }

        savedOldLayout = root.getLayout();

        root.setLayout(layoutManager);
    }

    protected void installOther(JRootPane root)
    {
        Window window = (Window) root.getParent();

        // 设置窗体为完全透明
        // set window translucent
        window.setBackground(UIManager.getColor(LuckGlobalBundle.TRANSLUCENT_COLOR));
    }

    /**
     * <p>去除窗格边框</p>
     * 
     * <p>remove JRootPane border.</p>
     *
     * @param root
     */
    protected void uninstallBorder(JRootPane root)
    {
        LookAndFeel.uninstallBorder(root);

        root.setBorder(null);
    }

    protected void uninstallLayout(JRootPane root)
    {
        if (savedOldLayout != null)
        {
            root.setLayout(savedOldLayout);

            savedOldLayout = null;
        }

        layoutManager = null;
    }

    protected void uninstallOther(JRootPane root)
    {
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

        Window window = SwingUtilities.getWindowAncestor(root);

        if (window != null)
        {
            window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public LuckTitlePanel getTitlePane()
    {
        return titlePane;
    }
}
