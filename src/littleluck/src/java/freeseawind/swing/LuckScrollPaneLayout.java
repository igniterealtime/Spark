package freeseawind.swing;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

/**
 * ScrollPane布局实现类，主要针对{@link LuckScrollPane}实现
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckScrollPaneLayout extends ScrollPaneLayout
{
    private static final long serialVersionUID = 8125767787388625029L;
    protected JLayeredPane layerPane;

    public void layoutContainer(Container parent)
    {
        JScrollPane scrollPane = (JScrollPane)parent;

        Rectangle availR = scrollPane.getBounds();
        availR.x = availR.y = 0;

        Insets insets = parent.getInsets();
        availR.x = insets.left;
        availR.y = insets.top;
        availR.width -= insets.left + insets.right;
        availR.height -= insets.top + insets.bottom;

        int x = insets.left;
        int y = insets.top;
        int width = availR.width - insets.left + insets.right;
        int height = availR.height - insets.top + insets.bottom;

        super.layoutContainer(parent);

        if(scrollPane.getRowHeader() != null)
        {
            int rowHeaderW = scrollPane.getRowHeader().getWidth();
            int rowHeaderH = height;
            int startY = y;
            
            if(upperLeft != null)
            {
                startY += upperLeft.getHeight();;
                rowHeaderH -= upperLeft.getHeight();
            }
            
            if(lowerLeft != null)
            {
                rowHeaderH -= lowerLeft.getHeight();
            }
            
            scrollPane.getRowHeader().setBounds(x, startY, rowHeaderW, rowHeaderH);
            
            x += scrollPane.getRowHeader().getSize().width;
        }

        if(scrollPane.getColumnHeader() != null)
        {
            int columnHeaderW =  width;
            int columnHeaderH = scrollPane.getColumnHeader().getSize().height;
            
            if(upperLeft != null)
            {
                columnHeaderW -= upperLeft.getWidth();
            }
            
            if(upperRight != null)
            {
                columnHeaderW -= upperRight.getWidth();
            }
            
            scrollPane.getColumnHeader().setBounds(x, y, columnHeaderW, columnHeaderH);
            
            y += scrollPane.getColumnHeader().getSize().height;
        }

        layerPane.setBounds(0, 0, width, height);

        if (viewport != null)
        {
            viewport.setBounds(x, y, width, height);
        }
    }

    public void syncWithScrollPane(JScrollPane sp)
    {
        super.syncWithScrollPane(sp);

        if(sp instanceof LuckScrollPane)
        {
            layerPane = ((LuckScrollPane) sp).getLayeredPane();
        }
    }
}
