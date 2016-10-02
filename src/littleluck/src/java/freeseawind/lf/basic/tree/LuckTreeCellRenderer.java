package freeseawind.lf.basic.tree;

import java.awt.Graphics;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * <p>Tree单元处理渲染器，去除焦点边框处理。</p>
 *
 * <p>Tree renderer processing unit, removing the focus frame processing.</p>
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
        // not to draw the focus frame.
        hasFocus = false;

        super.paint(g);
    }
}
