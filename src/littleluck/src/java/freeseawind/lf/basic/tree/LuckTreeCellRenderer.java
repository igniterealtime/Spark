package freeseawind.lf.basic.tree;

import java.awt.Graphics;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Tree单元处理渲染器，出去焦点边框处理
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTreeCellRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = -536835143324806896L;

    public void paint(Graphics g)
    {
        // 不绘制焦点边框
        hasFocus = false;

        super.paint(g);
    }
}
