package freeseawind.swing;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;

/**
 * 完全不透明的JRadioButtonMenuItem实现类, 用于防止字体走样
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckRadioButtonMenuItem extends JRadioButtonMenuItem
        implements LuckCanvas
{
    private static final long serialVersionUID = -3807321347405962293L;

    private LuckOpaquePainter painter = new LuckOpaquePainter();

    public LuckRadioButtonMenuItem()
    {
        super();
    }

    public LuckRadioButtonMenuItem(Action a)
    {
        super(a);
    }

    public LuckRadioButtonMenuItem(Icon icon, boolean selected)
    {
        super(icon, selected);
    }

    public LuckRadioButtonMenuItem(Icon icon)
    {
        super(icon);
    }

    public LuckRadioButtonMenuItem(String text, boolean selected)
    {
        super(text, selected);
    }

    public LuckRadioButtonMenuItem(String text, Icon icon, boolean selected)
    {
        super(text, icon, selected);
    }

    public LuckRadioButtonMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }

    public LuckRadioButtonMenuItem(String text)
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
