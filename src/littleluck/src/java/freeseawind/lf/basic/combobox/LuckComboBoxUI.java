package freeseawind.lf.basic.combobox;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.border.LuckShapeBorder;
import freeseawind.lf.event.LuckBorderFocusHandle;

/**
 * <pre>
 * ComboBoxUI实现类, ，在{@link BasicComboBoxUI}基础上做了如下改变：
 * <li>改变控件属性为不完全透明</li>
 * <li>自定义焦点边框</li>
 * <li>自定义弹出框</li>
 * <li>自定义JList单元渲染处理</li>
 *
 * 以下代码片段演示了如何自定义Combobox弹出框：
 * ----------------------------------------------------------------------------
 * ComboBoxUI View UI implementation class, based on{@link BasicComboBoxUI} made the following changes:
 * <li>The setting Component is not completely transparent</li>
 * <li>Customize the focus borders</li>
 * <li>Customize the pop-up box</li>
 * <li>Customize the JList cell rendering process</li>
 *
 * The following code fragment demonstrates how to customize the Combobox popup box:
 * <code>
 *
 * // 重写默认JPopupMenu实现类
 * public class MyPopup extends {@link BasicComboPopup} {
 *
 *    protected {@link JScrollPane} createScroller() {
 *      // 覆盖该方法可以重写Combobox滚动面板的实现
 *      // Overriding this method overrides the implementation of the Combobox scroll panel
 *    }
 *
 *    protected {@link JList} createList() {
 *      // 覆盖该方法可以重写Combobox内容面板的实现, Combobox使用JList做为内容显示组件
 *      // Overriding this method overrides the implementation of the Combobox
 *      // content panel, Combobox uses the JList as the content display component
 *    }
 *
 *    protected void configurePopup() {
 *      // 这里可以设置{@link JPopupMenu}的边框和显示位置
 *      // here you can set the border and display position of PopupMenu
 *    }
 *     ..........
 *  }
 *
 *  public class MyComboboxUI extends {@link LuckComboBoxUI} {
 *
 *     protected ComboPopup createPopup() {
 *         return new MyPopup(comboBox);
 *     }
 *  }
 *
 *  // 替换UI
 *  yourCombobox.setUI(new MyCombobxUI());
 *
 *  yourCombobox.updateUI();
 *
 *  .......
 *
 * </code>
 * </pre>
 *
 * @see LuckComboBoxButton
 * @see LuckComboboxPopup
 * @see LuckComboBoxRenderer
 * @see LuckComboBoxUIBundle
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckComboBoxUI extends BasicComboBoxUI implements LuckBorderField
{
    // 边框焦点处理监听器
    // Border focus listener
    private LuckBorderFocusHandle handle;

    // 内容面板背景形状(注：和边框须保持一致)
    // Content panel background shape (Note: and the border must be consistent)
    private RectangularShape contentShape;

    // 边框形状
    // Border shape
    private RectangularShape borderShape;

    // 是否获取焦点
    // Whether to get focus
    private boolean isFocusGained;

    // 是否使用焦点边框，只能在初始化时赋值
    // Whether to use the focus of the border, can only
    // be assigned in the initialization
    private boolean isFocusBorder;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckComboBoxUI();
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);

        LookAndFeel.installProperty(c, "opaque", Boolean.FALSE);

        if(c.getBorder() instanceof LuckShapeBorder)
        {
            installFocusListener(c);
        }
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        uninstallFocusListener(c);

        contentShape = null;

        borderShape = null;
    }

    @Override
    public void paint(Graphics g, JComponent c)
    {
        // 修改此处, 去除选中背景, 原代码：<code>comboBox.hasFocus()</code>
        // modify here, remove the selected background, the source code:<code>comboBox.hasFocus()</code>
        hasFocus = false;

        if (!comboBox.isEditable())
        {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValueBackground(g, r, hasFocus);
            paintCurrentValue(g, r, hasFocus);
        }
    }

    /**
     * <pre>
     * 重写绘制背景方法， 面板不完全透明也绘制背景
     *
     * Rewrite the background method to draw, the panel is not completely
     * transparent also draw the background
     * </pre>
     */
    @Override
    public void update(Graphics g, JComponent c)
    {
        g.setColor(c.getBackground());

        Graphics2D g2d = (Graphics2D) g;

        if(contentShape != null)
        {
            contentShape.setFrame(0, 0, c.getWidth() - 1, c.getHeight() - 1);

            g2d.fill(contentShape);
        }
        else
        {
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        paint(g, c);
    }

    /**
     * <pre>
     * 初始化边框焦点监听器
     *
     * Initializes the border focus listener
     * <pre>
     *
     * @param c
     */
    protected void installFocusListener(JComponent c)
    {
        handle = new LuckComboboxFocusHandle();

        isFocusBorder = UIManager.getBoolean(LuckComboBoxUIBundle.ISFOCUSBORDER);

        if (isFocusBorder)
        {
            contentShape = new RoundRectangle2D.Float(0, 0, 0, 0, 8, 8);

            borderShape = new RoundRectangle2D.Float(0, 0, 0, 0, 8, 8);

            c.addMouseListener(handle);

            c.addFocusListener(handle);
        }
    }

    /**
     * remove focus Listener
     *
     * @param c
     */
    protected void uninstallFocusListener(JComponent c)
    {
        if(handle != null)
        {
            c.removeMouseListener(handle);

            c.removeFocusListener(handle);

            handle = null;
        }
    }

    /**
     * <pre>
     * 重写下拉按钮,增加焦点颜色
     *
     * Rewrite the drop-down button to increase the focus color
     * </pre>
     *
     * @return arrow button info
     */
    protected JButton createArrowButton()
    {
        JButton button = new LuckComboBoxButton(BasicArrowButton.SOUTH)
        {
            private static final long serialVersionUID = -7259590635997077859L;

            @Override
            public LuckBorderField getBorderField()
            {
                return LuckComboBoxUI.this;
            }

            @Override
            public JComponent getParentComp()
            {
                return LuckComboBoxUI.this.comboBox;
            }
        };

        button.setName("ComboBox.arrowButton");

        return button;
    }

    /**
     * <pre>
     * 重写该方法自定义弹出框
     *
     * Override the method to use a custom popup
     * </pre>
     *
     * @return <code>ComboPopup</code>
     */
    protected ComboPopup createPopup()
    {
        return new LuckComboboxPopup(comboBox);
    }

    /**
     * <pre>
     * 重写该方法使用自定义单元渲染处理类
     *
     * Overriding this method uses a custom ListCellRender
     * </pre>
     *
     * @return <code>ListCellRender</code>
     */
    protected ListCellRenderer<?> createRenderer()
    {
        return new LuckComboBoxRenderer();
    }

    public void setFocusGained(boolean isFoucusGaind)
    {
        this.isFocusGained = isFoucusGaind;
    }

    public boolean isFocusGaind()
    {
        return isFocusGained;
    }

    public RectangularShape getBorderShape()
    {
        return borderShape;
    }

    public void setBorderShape(RectangularShape shape)
    {
        this.borderShape = shape;
    }

    public RectangularShape getContentShape()
    {
        return contentShape;
    }

    public void setContentShape(RectangularShape contentShape)
    {
        this.contentShape = contentShape;
    }

    public boolean isFocusBorder()
    {
        return isFocusBorder;
    }

    /**
     * 下拉列表焦点处理器
     *
     */
    public class LuckComboboxFocusHandle extends LuckBorderFocusHandle
    {
        public LuckComboboxFocusHandle()
        {
        }

        protected void handleFocusLost()
        {
            if (getBorderField().isFocusGaind())
            {
                getBorderField().setFocusGained(false);

                getComponent().repaint();
            }
        }

        protected void handleFocusGained()
        {
            if (!getBorderField().isFocusGaind())
            {
                getBorderField().setFocusGained(true);

                getComponent().repaint();
            }
        }

        @Override
        public JComponent getComponent()
        {
            return LuckComboBoxUI.this.comboBox;
        }

        @Override
        public LuckBorderField getBorderField()
        {
            return LuckComboBoxUI.this;
        }
    }
}
