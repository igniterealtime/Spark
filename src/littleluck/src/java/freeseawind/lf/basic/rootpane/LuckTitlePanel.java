package freeseawind.lf.basic.rootpane;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.cfg.LuckGlobalBundle;
import freeseawind.lf.event.LuckWindowAdapter;
import freeseawind.lf.event.WindowBtnMouseAdapter;
import freeseawind.lf.event.WindowPropertyListener;
import freeseawind.lf.utils.LuckWindowUtil;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * <p>窗体标题面板实现类, 当使用非系统窗体装饰时，该类会被创建。</p>
 *
 *
 * <p>
 * Class that manages a JLF awt.Window-descendant class's title bar.
 * This class assumes it will be created with a particular window
 * decoration style, and that if the style changes, a new one will
 * be created.
 * <p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTitlePanel extends JPanel
{
    private static final long serialVersionUID = -8111081484864247330L;
    private static final int ICON_HEIGHT = 16;
    private static final int ICON_WIDTH = 16;
    protected JButton maximizeBtn;
    protected JButton minBtn;
    protected JButton closeBtn;
    protected JLabel label;
    protected WindowPropertyListener listener;
    protected WindowAdapter windowAdapter;
    protected Window window;
    private SwingNinePatch np;
    private LuckCanvas painter;
    private int initStyle;
    private boolean isResizeableOnInit;
    private int state;

    public LuckTitlePanel(boolean isResizeableOnInit, int initStyle)
    {
        setOpaque(false);

        setLayout(createLayout());

        this.isResizeableOnInit = isResizeableOnInit;

        this.initStyle = initStyle;

        this.state = JFrame.NORMAL;

        Object obj = UIManager.get(LuckRootPaneUIBundle.TITLEPANEL_BG_IMG);

        if(obj != null)
        {
            np = new SwingNinePatch((BufferedImage) obj);
        }

        installTitle();

        installBtn();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(np != null)
        {
            np.drawNinePatch((Graphics2D) g, 0, 0, getWidth(), getHeight());
        }

        if(painter != null)
        {
            painter.drawComponent(g, this);
        }
    }

    public boolean isLeftToRight()
    {
        boolean leftToRight = (getWindow() == null)
                ? getRootPane().getComponentOrientation().isLeftToRight()
                : getWindow().getComponentOrientation().isLeftToRight();

        return leftToRight;
    }

    public void addNotify()
    {
        super.addNotify();

        window = getWindow();

        if(window != null)
        {
            String title = LuckWindowUtil.getWindowTitle(window);

            setTitle(title);

            Image image = LuckWindowUtil.getWindowImage(window);

            if(image != null)
            {
                setIcon(new ImageIcon(image));
            }

            if(listener == null)
            {
                listener = new WindowPropertyListener(this);

                window.addPropertyChangeListener(listener);
            }

            if(windowAdapter == null && window instanceof JFrame)
            {
                windowAdapter = new LuckWindowAdapter();

                window.addWindowStateListener(windowAdapter);
            }
        }
    }

    public void removeNotify()
    {
        super.removeNotify();

        if(window != null)
        {
            if(listener != null)
            {
                window.removePropertyChangeListener(listener);
            }

            if(windowAdapter != null && window instanceof JFrame)
            {
                window.removeWindowStateListener(windowAdapter);
            }

            window = null;

            listener = null;
        }
    }

    /**
     * <p>设置窗体拉伸状体,根据状态隐藏或显示放大按钮</p>
     *
     * <p>Set the form stretched state, according to the state to hide or display the zoom button</p>
     *
     * @param isResizeable resize able return true, otherwise return false
     */
    public void setResizeable(boolean isResizeable)
    {
        if(isResizeable)
        {
            maximizeBtn.setVisible(true);
        }
        else
        {
            maximizeBtn.setVisible(false);
        }
    }

    /**
     * <p>创建标题面板布局<p>
     *
     * <p>Create TitlePane layout manager</p>
     *
     * @return 标题面板布局对象
     */
    public LayoutManager createLayout()
    {
        return new LuckTitlePanelLayout();
    }

    /**
     * <p>安装放大、缩小、关闭按钮<p>
     *
     * <p>Installation Form button<p>
     */
    protected void installBtn()
    {
        closeBtn = new JButton();

        closeBtn.addMouseListener(new CloseMouseAdapter(closeBtn,
                LuckRootPaneUIBundle.CLOSE_NORMAL_ICON,
                LuckRootPaneUIBundle.CLOSE_ROVER_ICON,
                LuckRootPaneUIBundle.CLOSE_PRESSED_ICON));

        setBtnAtrr(closeBtn);

        add(closeBtn);

        // 如果是Frame则显示退出和放大按钮
        // If the Frame is displayed exits and zoom buttons
        if(initStyle == JRootPane.FRAME)
        {
            minBtn = new JButton();

            minBtn.addMouseListener(new MinMouseAdapter(minBtn,
                    LuckRootPaneUIBundle.MIN_NORMAL_ICON,
                    LuckRootPaneUIBundle.MIN_ROVER_ICON,
                    LuckRootPaneUIBundle.MIN_PRESSED_ICON));

            setBtnAtrr(minBtn);

            add(minBtn);

            maximizeBtn = new JButton();

            maximizeBtn.addMouseListener(new MaximizeMouseAdapter(maximizeBtn,
                    LuckRootPaneUIBundle.MAX_NORMAL_ICON,
                    LuckRootPaneUIBundle.MAX_ROVER_ICON,
                    LuckRootPaneUIBundle.MAX_PRESSED_ICON));

            setBtnAtrr(maximizeBtn);

            add(maximizeBtn);

            // 判断窗体是否可以拉伸
            // Determine whether the form can be stretched
            if(!isResizeableOnInit)
            {
                maximizeBtn.setVisible(false);
            }
        }
    }

    /**
     * <p>安装窗体图标和标题<p>
     *
     * <p>Installation Window icon and title<p>
     */
    protected void installTitle()
    {
        label = new JLabel();

        label.setIcon(UIManager.getIcon(LuckGlobalBundle.APPLICATION_ICON));

        label.setText(UIManager.getString(LuckGlobalBundle.APPLICATION_TITLE));

        label.setHorizontalAlignment(JLabel.CENTER);

        label.setIconTextGap(UIManager.getInt(LuckRootPaneUIBundle.APPLICATION_TITLE_TEXTGAP));

        label.setForeground(UIManager.getColor(LuckRootPaneUIBundle.TITLE_FONT_COLOR));

        add(label);
    }

    /**
     * <p>设置按钮属性</p>
     *
     * <p>set window button attribute</p>
     *
     * @param btn
     */
    protected void setBtnAtrr(JButton btn)
    {
        btn.setOpaque(false);

        btn.setBorder(null);

        btn.setFocusPainted(false);

        btn.setFocusable(false);

        btn.setBackground(null);

        btn.setContentAreaFilled(false);
    }

    /**
     * <p>获取面板所在的窗体</p>
     *
     * <p>get parent window</p>
     *
     * @return
     */
    private Window getWindow()
    {
        return SwingUtilities.getWindowAncestor(this);
    }

    /**
     * update Maximize button icon when window state change.
     */
    private void updateMaximizeBtn()
    {
        if((state & JFrame.MAXIMIZED_BOTH) != 0)
        {
            maximizeBtn.setIcon(UIManager.getIcon(LuckRootPaneUIBundle.MAXIMIZE_NORMAL_ICON));
            maximizeBtn.setRolloverIcon(UIManager.getIcon(LuckRootPaneUIBundle.MAXIMIZE_ROVER_ICON));
            maximizeBtn.setPressedIcon(UIManager.getIcon(LuckRootPaneUIBundle.MAXIMIZE_PRESSED_ICON));
        }
        else
        {
            maximizeBtn.setIcon(UIManager.getIcon(LuckRootPaneUIBundle.MAX_NORMAL_ICON));
            maximizeBtn.setRolloverIcon(UIManager.getIcon(LuckRootPaneUIBundle.MAX_ROVER_ICON));
            maximizeBtn.setPressedIcon(UIManager.getIcon(LuckRootPaneUIBundle.MAX_PRESSED_ICON));
        }
    }

    // window close listener.
    private class CloseMouseAdapter extends WindowBtnMouseAdapter
    {
        public CloseMouseAdapter(JButton btn,
                                 String normalIconKey,
                                 String hoverIconKey,
                                 String pressIconKey)
        {
            super(btn, normalIconKey, hoverIconKey, pressIconKey);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            Window window = getWindow();

            if(window != null)
            {
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    // window minimize listener.
    private class MinMouseAdapter extends WindowBtnMouseAdapter
    {
        public MinMouseAdapter(JButton btn,
                               String normalIconKey,
                               String hoverIconKey,
                               String pressIconKey)
        {
            super(btn, normalIconKey, hoverIconKey, pressIconKey);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            Window window = getWindow();

            if(window instanceof JFrame)
            {
                // 必须先禁用按钮否则无法取消焦点事件
                minBtn.setEnabled(false);

                ((JFrame)window).setExtendedState(state | Frame.ICONIFIED);

                minBtn.setEnabled(true);
            }
        }
    }

    // 窗体放大缩小按钮鼠标事件
    // Window maximize listener.
    private class MaximizeMouseAdapter extends WindowBtnMouseAdapter
    {
        public MaximizeMouseAdapter(JButton btn,
                                    String normalIconKey,
                                    String hoverIconKey,
                                    String pressIconKey)
        {
            super(btn, normalIconKey, hoverIconKey, pressIconKey);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            Window window = getWindow();

            if(window instanceof JFrame)
            {
                JFrame frame = ((JFrame)window);

                // 必须先禁用按钮否则无法取消焦点事件
                // must set enable false here.
                maximizeBtn.setEnabled(false);

                if ((state & JFrame.ICONIFIED) != 0)
                {
                    frame.setExtendedState(state & ~JFrame.ICONIFIED);
                }
                else if((state & JFrame.MAXIMIZED_BOTH) != 0)
                {
                    frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
                }
                else
                {
                    frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
                }

                maximizeBtn.setEnabled(true);
            }
        }
    }

    /**
     * @return TitlePane height
     */
    public int getHeight()
    {
        return UIManager.getInt(LuckRootPaneUIBundle.TITLEPANEL_HEIGHT);
    }

    public JButton getMaximizeBtn()
    {
        return maximizeBtn;
    }

    public JButton getMinBtn()
    {
        return minBtn;
    }

    public JButton getCloseBtn()
    {
        return closeBtn;
    }

    public void setIcon(ImageIcon systemIcon)
    {
        if(systemIcon == null)
        {
            return;
        }

        Image image = systemIcon.getImage();

        if(image.getHeight(null) < ICON_HEIGHT || image.getWidth(null) < ICON_WIDTH)
        {
            return;
        }

        if(image.getHeight(null) > ICON_HEIGHT || image.getWidth(null) > ICON_WIDTH)
        {
            image = image.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);

            systemIcon = new ImageIcon(image);
        }

        this.label.setIcon(systemIcon);
    }

    public void setTitle(String title)
    {
        this.label.setText(title);
    }

    public void setTitleForeground(Color color)
    {
        this.label.setForeground(color);
    }

    public void setCallback(LuckCanvas painter)
    {
        this.painter = painter;
    }

    public void setState(int state)
    {
        this.state = state;

        updateMaximizeBtn();
    }

    public void setBackgroundNP(SwingNinePatch np)
    {
        this.np = np;
    }

    protected JLabel getLabel()
    {
        return label;
    }
}
