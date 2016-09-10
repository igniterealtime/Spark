package freeseawind.swing;

import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;

/**
 * 完全不透明的JPanel实现类, 用于防止字体走样
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckPanel extends JPanel implements LuckCanvas
{
	private static final long serialVersionUID = -2421362559783273921L;

	private LuckOpaquePainter painter = new LuckOpaquePainter();

    public LuckPanel()
	{
		super();
	}

	public LuckPanel(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
	}

	public LuckPanel(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
	}

	public LuckPanel(LayoutManager layout)
	{
		super(layout);
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
