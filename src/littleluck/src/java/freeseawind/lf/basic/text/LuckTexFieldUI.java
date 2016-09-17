package freeseawind.lf.basic.text;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.event.LuckBorderFocusHandle;

/**
 *
 * @author freeseawind@github
 *
 */
public class LuckTexFieldUI extends BasicTextFieldUI implements LuckBorderField
{
    protected LuckBorderFocusHandle handle;
    private RectangularShape contentShape;
    private RectangularShape borderShape;
    private boolean isFocusGained;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckTexFieldUI();
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
        JTextComponent editor = this.getComponent();
        Graphics2D g2d = (Graphics2D)g;
        g.setColor(editor.getBackground());
        contentShape.setFrame(0, 0, editor.getWidth() - 1, editor.getHeight() - 1);
        g2d.fill(contentShape);
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
            return LuckTexFieldUI.this.getComponent();
        }

        @Override
        public LuckBorderField getBorderField()
        {
            return LuckTexFieldUI.this;
        }
    }
}
