package freeseawind.swing;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;

/**
 * 完全不透明的JCheckBoxMenuItem实现类, 用于防止字体走样
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckCheckBoxMenuItem extends JCheckBoxMenuItem
        implements LuckCanvas
{
    private static final long serialVersionUID = 4270705804759628124L;

    private LuckOpaquePainter painter = new LuckOpaquePainter();

    public LuckCheckBoxMenuItem()
    {
        super();
    }

    public LuckCheckBoxMenuItem(Action a)
    {
        super(a);
    }

    public LuckCheckBoxMenuItem(Icon icon)
    {
        super(icon);
    }

    public LuckCheckBoxMenuItem(String text, boolean b)
    {
        super(text, b);
    }

    public LuckCheckBoxMenuItem(String text, Icon icon, boolean b)
    {
        super(text, icon, b);
    }

    public LuckCheckBoxMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }

    public LuckCheckBoxMenuItem(String text)
    {
        super(text);
    }

    @Override
    public void paint(Graphics g)
    {
        painter.paintOpaque(g, this, this);
    }

    public void drawComponent(Graphics g, JComponent c)
    {
        super.paint(g);
    }
}
