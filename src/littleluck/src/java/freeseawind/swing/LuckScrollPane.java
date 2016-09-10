package freeseawind.swing;

import java.awt.Component;

import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 * 滚动条悬浮的JScrollPane
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckScrollPane extends JScrollPane
{
    protected JLayeredPane layeredPane;
    private static final long serialVersionUID = -9008522455170803317L;
    
    public LuckScrollPane()
    {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public LuckScrollPane(Component view, int vsbPolicy, int hsbPolicy)
    {
        super(view, vsbPolicy, hsbPolicy);

        setLayout(new LuckScrollPaneLayout());
        
        if(view instanceof JTable)
        {
            setColumnHeaderView(((JTable)view).getTableHeader());
        }
    }

    public LuckScrollPane(Component view)
    {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public LuckScrollPane(int vsbPolicy, int hsbPolicy)
    {
        this(null, vsbPolicy, hsbPolicy);
    }

    public void setViewport(JViewport viewport)
    {
        createLayeredPane();

        JViewport old = getViewport();

        this.viewport = viewport;

        if (viewport != null)
        {
            layeredPane.add(viewport, JLayeredPane.FRAME_CONTENT_LAYER);
        }
        else if (old != null)
        {
            layeredPane.remove(old);
        }

        firePropertyChange("viewport", old, viewport);

        if (accessibleContext != null)
        {
            ((AccessibleJScrollPane) accessibleContext).resetViewPort();
        }

        revalidate();
        repaint();
    }

    public void setVerticalScrollBar(JScrollBar verticalScrollBar)
    {
        createLayeredPane();

        JScrollBar old = getVerticalScrollBar();

        this.verticalScrollBar = verticalScrollBar;

        if(verticalScrollBar != null)
        {
            verticalScrollBar.setOpaque(false);

            layeredPane.add(verticalScrollBar, JLayeredPane.POPUP_LAYER);
        }
        else if (old != null)
        {
            layeredPane.remove(old);
        }


        firePropertyChange("verticalScrollBar", old, verticalScrollBar);

        revalidate();

        repaint();
    }

    public void setHorizontalScrollBar(JScrollBar horizontalScrollBar)
    {
        createLayeredPane();

        JScrollBar old = getHorizontalScrollBar();

        this.horizontalScrollBar = horizontalScrollBar;

        if (horizontalScrollBar != null)
        {
            horizontalScrollBar.setOpaque(false);

            layeredPane.add(horizontalScrollBar, JLayeredPane.POPUP_LAYER);
        }
        else if (old != null)
        {
            layeredPane.remove(old);
        }

        firePropertyChange("horizontalScrollBar", old, horizontalScrollBar);

        revalidate();

        repaint();
    }

    protected void createLayeredPane()
    {
        if (layeredPane == null)
        {
            layeredPane = new JLayeredPane();

            layeredPane.setName(this.getName() + ".layeredPane");

            this.add(layeredPane, -1);
        }
    }

    public JLayeredPane getLayeredPane()
    {
        return layeredPane;
    }
}
