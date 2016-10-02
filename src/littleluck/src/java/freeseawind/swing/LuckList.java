package freeseawind.swing;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;

/**
 * 完全不透明的JLuckList实现类, 用于防止字体走样
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckList<E> extends JList<E> implements LuckCanvas
{
    private static final long serialVersionUID = 7805966728571612336L;
    private LuckOpaquePainter painter = new LuckOpaquePainter();

    public LuckList()
    {
        super();
    }

    public LuckList(E[] listData)
    {
        super(listData);
    }

    public LuckList(ListModel<E> dataModel)
    {
        super(dataModel);
    }

    public LuckList(Vector<? extends E> listData)
    {
        super(listData);
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
