package freeseawind.swing;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;

/**
 * 完全不透明的JMenuItem实现类, 用于防止字体走样
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckMenuItem extends JMenuItem implements LuckCanvas
{
    private static final long serialVersionUID = -9052894913121320415L;

    private LuckOpaquePainter painter = new LuckOpaquePainter();

    public LuckMenuItem()
    {
        super();
    }

    public LuckMenuItem(Action a)
    {
        super(a);
    }

    public LuckMenuItem(Icon icon)
    {
        super(icon);
    }

    public LuckMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }

    public LuckMenuItem(String text, int mnemonic)
    {
        super(text, mnemonic);
    }

    public LuckMenuItem(String text)
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
