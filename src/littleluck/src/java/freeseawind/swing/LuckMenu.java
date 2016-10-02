package freeseawind.swing;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;

/**
 * 完全不透明的JMenu实现类, 用于防止字体走样
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckMenu extends JMenu implements LuckCanvas
{
    private static final long serialVersionUID = 7104357009445446247L;

    private LuckOpaquePainter painter = new LuckOpaquePainter();

    public LuckMenu()
    {
        super();
    }

    public LuckMenu(Action a)
    {
        super(a);
    }

    public LuckMenu(String s, boolean b)
    {
        super(s, b);
    }

    public LuckMenu(String s)
    {
        super(s);
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
