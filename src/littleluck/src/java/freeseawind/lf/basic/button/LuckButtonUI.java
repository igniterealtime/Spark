package freeseawind.lf.basic.button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;


/**
 * <p>一种纯色系扁平化按钮UI实现类, 默认带弹簧效果。</p>
 * 以下两种情况将不会绘制颜色按钮：
 * <p>1.JButton.setContentAreaFilled(false);</p>
 * <p>2.JButton.setIcon(Icon icon);</p>
 * <p>如需要在有图标的时候仍绘制背景，调用<code>button.putClientProperty(IS_PAINTBG, "")</code>即可。 </p>
 * 扩展描述：
 * <ul>
 * <li>设置按钮为不完全透明</li>
 * <li>按钮扁平化</li>
 * <li>按钮颜色可配置</li>
 * </ul>
 * <p>以下代码片段演示了如何给按钮自定义颜色：</p>
 *
 * <pre>
 * <code>
 * {@link JButton} btn = new {@link JButton}("test");
 * {@link LuckButtonUI} ui = ({@link LuckButtonUI})btn.getUI();
 * ui.setBtnColorInfo({@link LuckButtonColorInfo} colorInfo);
 * btn.updateUI();
 * </code>
 * </pre>
 * <p>
 * 另请参见 {@link LuckButtonUIBundle}， {@link LuckButtonColorInfo}
 *
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckButtonUI extends BasicButtonUI
{
    private Color oldFontColor;

    private LuckButtonColorInfo btnColorInfo;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckButtonUI();
    }

    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        btnColorInfo = null;
    }

    public void paint(Graphics g, JComponent c)
    {
        AbstractButton b = (AbstractButton) c;

        ButtonModel model = b.getModel();

        oldFontColor = b.getForeground();

        boolean isPaintBg = paintBg(g, (AbstractButton) c);

        if(isPaintBg)
        {
            b.setForeground(btnColorInfo.getFontColor());
        }

        // 弹簧效果
        if(model.isPressed() && model.isArmed() && b.getIcon() == null)
        {
            g.translate(2, 1);
        }

        super.paint(g, c);

        if(model.isPressed() && model.isArmed() && b.getIcon() == null)
        {
            g.translate(-2, -1);
        }

        b.setForeground(oldFontColor);
    }

    protected void installDefaults(AbstractButton b)
    {
        super.installDefaults(b);

        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);

        // 设置按钮为不完全透明
        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);

        btnColorInfo = (LuckButtonColorInfo) UIManager.get(
                LuckButtonUIBundle.COLOR_INFO);
    }

    /**
     * 绘制按钮背景
     *
     * @param g 绘图画笔对象
     * @param b 当前按钮对象
     * @return 绘制颜色背景返回true，否则false
     */
    protected boolean paintBg(Graphics g, AbstractButton b)
    {
        Object isPaintBg = b.getClientProperty(LuckButtonUIBundle.IS_PAINTBG);
        
        if (!b.isContentAreaFilled()
                || (b.getIcon() != null && isPaintBg == null))
        {
            return false;
        }

        int w = b.getWidth();

        int h = b.getHeight();

        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(b.getModel().isPressed() && b.getModel().isArmed())
        {
            // 点击按钮
            g2d.setColor(btnColorInfo.getPressedColor());
        }
        else if(b.getModel().isRollover() && b.isRolloverEnabled())
        {
            // 鼠标经过
            g2d.setColor(btnColorInfo.getRollverColor());
        }
        else
        {
            g2d.setColor(btnColorInfo.getNormalColor());
        }

        g2d.fillRoundRect(0, 0, w, h, 8, 8);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        return true;
    }

    /**
     * 设置按钮颜色信息
     *
     * @param btnColorInfo 按钮颜色信息
     */
    public void setBtnColorInfo(LuckButtonColorInfo btnColorInfo)
    {
        this.btnColorInfo = btnColorInfo;
    }
}
