package freeseawind.lf.img;

import javax.swing.ImageIcon;

/**
 * 定义一个占位图标
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckIcon extends ImageIcon
{
    private static final long serialVersionUID = 8254398003035368906L;
    private int width;
    private int hegiht;

    public LuckIcon(int w, int h)
    {
        super();

        this.width = w;

        this.hegiht = h;
    }

    @Override
    public int getIconHeight()
    {
        return hegiht;
    }

    @Override
    public int getIconWidth()
    {
        return width;
    }
}
