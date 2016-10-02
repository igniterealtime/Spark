package freeseawind.lf.basic.button;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;


/**
 * <pre>
 * 按钮ViewUI实现类，在{@link BasicButtonUI}基础上做了如下改变：
 * <li>设置按钮为不完全透明</li>
 * <li>按钮扁平化</li>
 * <li>按钮颜色可配置</li>
 * <li>点击按钮有弹簧效果</li>
 *
 * <strong>以下两种情况将不会绘制按钮背景颜色</strong>：
 *
 * 1.button.setContentAreaFilled(false);
 *
 * 2.button.setIcon(...);
 *
 * <strong>如需要在有图标的时候仍绘制背景，使用如下代码</strong>：
 *
 * <code>button.putClientProperty(IS_PAINTBG, "")</code>
 *
 * 以下代码片段演示了如何给按钮自定义颜色：
 * ---------------------------------------------------------------------------------------
 * Button View UI implementation class, based on BasicButtonUI made the following changes:
 * <li>The setting button is not completely transparent</li>
 * <li>Button flattened</li>
 * <li>Button colors can be configured</li>
 * <li>Click the button Spring Effect</li>
 *
 * <strong>The button background color will not be drawn in the following two cases</strong>：
 *
 * 1.button.setContentAreaFilled(false);
 *
 * 2.button.setIcon(...);
 *
 * If you need to draw the background when there is an icon, use the following code：
 *
 * <code>button.putClientProperty(IS_PAINTBG, "")</code>
 *
 * <strong>The following code snippet demonstrates how to customize a color for a button</strong>：
 * <code>
 * {@link JButton} btn = new {@link JButton}("test");
 *
 * {@link LuckButtonUI} ui = ({@link LuckButtonUI})btn.getUI();
 *
 * ui.setBtnColorInfo({@link LuckButtonColorInfo} colorInfo);
 *
 * btn.updateUI();
 * </code>
 * </pre>
 * @see LuckButtonUIBundle
 * @see LuckButtonColorInfo
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckButtonUI extends BasicButtonUI
{
    private LuckButtonColorInfo btnColorInfo;
    private PropertyChangeListener listener;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckButtonUI();
    }

    public void installDefaults(AbstractButton b)
    {
        super.installDefaults(b);

        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);

        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);

        btnColorInfo = (LuckButtonColorInfo) UIManager.get(LuckButtonUIBundle.COLOR_INFO);

        // 使用配置颜色替换默认字体颜色
        // Replace the default font color with the configured color
        if(b.getForeground() instanceof ColorUIResource)
        {
            b.setForeground(btnColorInfo.getFontColor());
        }
        
        listener = new ButtonPropertyChangeListener();
        
        b.addPropertyChangeListener(listener);
    }

    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);
        
        c.removePropertyChangeListener(listener);

        btnColorInfo = null;
        
        listener = null;
    }

    public void paint(Graphics g, JComponent c)
    {
        AbstractButton b = (AbstractButton) c;

        ButtonModel model = b.getModel();

        paintBg(g, (AbstractButton) c);

        // 设置组件偏移，以达到视觉上的按下和弹起效果
        // Set the component offsets to achieve visual depress and bounce
        if(model.isPressed() && model.isArmed() && b.getIcon() == null)
        {
            g.translate(2, 1);
        }

        super.paint(g, c);

        if(model.isPressed() && model.isArmed() && b.getIcon() == null)
        {
            g.translate(-2, -1);
        }
    }

    /**
     * <pre>
     * 绘制圆角背景, 设置组件偏移实现按钮按下和弹起效果。
     * -------------------------------------------------------------------------------------------------
     * Draw a rounded background.
     * set the component offset to achieve the button press and pop-up effect.
     * </pre>
     *
     * @param g Graphics to paint to
     * @param b AbstractButton painting on
     * @return paint background return true, otherwise return false.
     */
    protected void paintBg(Graphics g, AbstractButton b)
    {
        if(!checkIsPaintBg(b))
        {
            return;
        }

        int w = b.getWidth();

        int h = b.getHeight();

        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(b.getModel().isPressed() && b.getModel().isArmed())
        {
            // 点击按钮
            // pressed button
            g2d.setColor(btnColorInfo.getPressedColor());
        }
        else if(b.getModel().isRollover() && b.isRolloverEnabled())
        {
            // 鼠标经过
            // mouse enter button
            g2d.setColor(btnColorInfo.getRollverColor());
        }
        else
        {
            g2d.setColor(btnColorInfo.getNormalColor());
        }

        g2d.fillRoundRect(0, 0, w, h, 8, 8);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * check if use the default button style.
     *
     * @param b
     * @return default style return true, otherwise return false.
     */
    private boolean checkIsPaintBg(AbstractButton b)
    {
        if (!b.isContentAreaFilled())
        {
            return false;
        }
        
        Object isPaintBg = b.getClientProperty(LuckButtonUIBundle.IS_PAINTBG);

        if (b.getIcon() != null && isPaintBg == null)
        {
            return false;
        }

        return true;
    }

    /**
     * set current Button color information
     *
     * @param btnColorInfo Button color information
     */
    public void setBtnColorInfo(LuckButtonColorInfo btnColorInfo)
    {
        this.btnColorInfo = btnColorInfo;
    }
    
    public class ButtonPropertyChangeListener implements PropertyChangeListener
    {
        private static final String CONTENTAREAFILLED = "contentAreaFilled";
        
        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (evt.getPropertyName().equals(CONTENTAREAFILLED))
            {
                JButton btn = (JButton) evt.getSource();

                boolean isDefaultColor = (btn.getForeground() instanceof ColorUIResource);

                if (!checkIsPaintBg(btn) && isDefaultColor)
                {
                    btn.setForeground(UIManager.getColor(LuckButtonUIBundle.FOREGROUND));
                }
                else
                {
                    btn.setForeground(btnColorInfo.getFontColor());
                }
            }
        }
    }
}
