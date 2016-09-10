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
import freeseawind.lf.event.LuckBorderFocusHandle;

/**
 * <p>下拉列表UI实现类, 带焦点边框。</p>
 * 扩展描述：
 * <ul>
 * <li>改变控件属性为不完全透明</li>
 * <li>自定义焦点边框</li>
 * <li>自定义弹出框</li>
 * <li>自定义JList单元渲染处理</li>
 * </ul>
 * 以下代码片段演示了如何自定义Combobox弹出框：
 *
 * <pre>
 * <code>
 *
 * // 重写默认JPopupMenu实现类
 * public class MyPopup extends {@link BasicComboPopup} {
 *
 *    protected {@link JScrollPane} createScroller() {
 *      // 覆盖该方法可以重写Combobox滚动面板的实现
 *    }
 *
 *    protected {@link JList} createList() {
 *      // 覆盖该方法可以重写Combobox内容面板的实现, Combobox使用JList做为内容显示组件
 *    }
 *
 *    protected void configurePopup() {
 *      // 这里可以设置{@link JPopupMenu}的边框和显示位置
 *    }
 *     ..........
 *  }
 *
 *  // 使用自定义UI
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
 * <p>另请参见 {@link LuckComboBoxButton}, {@link LuckComboboxPopup},
 * {@link LuckComboBoxRenderer}, {@link LuckComboBoxUIBundle}</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckComboBoxUI extends BasicComboBoxUI implements LuckBorderField
{
    // 边框焦点处理
    private LuckBorderFocusHandle handle;

    // 内容面板背景形状(注：和边框须保持一致)
    private RectangularShape contentShape;

    // 边框形状
    private RectangularShape borderShape;

    // 是否获取焦点
    private boolean isFocusGained;

    // 只能在初始化时赋值
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

        contentShape = new RoundRectangle2D.Float(0, 0, 0, 0, 8, 8);

        borderShape = new RoundRectangle2D.Float(0, 0, 0, 0, 8, 8);

        handle = new LuckComboboxFocusHandle();

        isFocusBorder = UIManager.getBoolean(LuckComboBoxUIBundle.ISFOCUSBORDER);

        if (isFocusBorder)
        {
            c.addMouseListener(handle);

            c.addFocusListener(handle);
        }
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        c.removeMouseListener(handle);

        c.removeFocusListener(handle);
    }

    @Override
    public void paint(Graphics g, JComponent c)
    {
        // 修改此处, 去除选中背景, 原代码：comboBox.hasFocus()
        hasFocus = false;

        if (!comboBox.isEditable())
        {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValueBackground(g, r, hasFocus);
            paintCurrentValue(g, r, hasFocus);
        }
    }

    /**
     * 重写绘制背景方法， 面板不完全透明也绘制背景
     *
     */
    @Override
    public void update(Graphics g, JComponent c)
    {
        g.setColor(c.getBackground());

        Graphics2D g2d = (Graphics2D) g;

        contentShape.setFrame(0, 0, c.getWidth() - 1, c.getHeight() - 1);

        g2d.fill(contentShape);

        paint(g, c);
    }

    /**
     * 重写下拉按钮,增加焦点颜色
     *
     * @return 返回箭头按钮信息
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
     * 重写该方法自定义弹出框
     *
     * @return <code>ComboPopup</code> 弹出框
     */
    protected ComboPopup createPopup()
    {
        return new LuckComboboxPopup(comboBox);
    }

    /**
     * 重写该方法使用自定义单元渲染处理类
     *
     * @return <code>ListCellRender</code> 单元渲染处理对象
     */
    protected ListCellRenderer<?> createRenderer()
    {
        return new LuckComboBoxRenderer();
    }

    public void setFocusGained(boolean isFoucusGaind)
    {
        this.isFocusGained = isFoucusGaind;
    }

    public boolean isFoucusGaind()
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
            if (getBorderField().isFoucusGaind())
            {
                getBorderField().setFocusGained(false);

                getComponent().repaint();
            }
        }

        protected void handleFocusGained()
        {
            if (!getBorderField().isFoucusGaind())
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
