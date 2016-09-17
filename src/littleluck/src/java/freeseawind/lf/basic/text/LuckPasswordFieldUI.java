package freeseawind.lf.basic.text;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.event.LuckBorderFocusHandle;

/**
 *
 * @author freeseawind@github
 *
 */
public class LuckPasswordFieldUI extends BasicPasswordFieldUI
        implements LuckBorderField
{
    protected LuckBorderFocusHandle handle;
    private RectangularShape contentShape;
    private RectangularShape borderShape;
    private boolean isFocusGained;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckPasswordFieldUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
        
        LookAndFeel.installProperty(c, "opaque", Boolean.FALSE);

        contentShape = new RoundRectangle2D.Float(0, 0, 0, 0, 8, 8);

        borderShape = new RoundRectangle2D.Float(0, 0, 0, 0, 8, 8);

        handle = createFocusHandle();

        c.addMouseListener(handle);

        c.addFocusListener(handle);
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        c.removeMouseListener(handle);

        c.removeFocusListener(handle);
    }
    
    protected LuckBorderFocusHandle createFocusHandle()
    {
        return new LuckFocusHandler();
    }

    @Override
    protected void paintBackground(Graphics g)
    {
        System.out.println(999);
        JTextComponent editor = this.getComponent();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(editor.getBackground());
        contentShape.setFrame(10, 10, editor.getWidth() - 1, editor.getHeight() - 1);
        g2d.fill(contentShape);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
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
    
    public class LuckFocusHandler extends LuckBorderFocusHandle
    {
        @Override
        public JComponent getComponent()
        {
            return LuckPasswordFieldUI.this.getComponent();
        }

        @Override
        public LuckBorderField getBorderField()
        {
            return LuckPasswordFieldUI.this;
        }
    }
}
