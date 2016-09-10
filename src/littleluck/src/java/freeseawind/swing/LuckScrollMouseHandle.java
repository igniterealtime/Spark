package freeseawind.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class LuckScrollMouseHandle extends MouseAdapter
{
    private List<JScrollPane> scrollpanes;

    public LuckScrollMouseHandle()
    {
        scrollpanes = new LinkedList<JScrollPane>();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        for(JScrollPane sc : scrollpanes)
        {
            sc.getVerticalScrollBar().setEnabled(false);

            sc.getHorizontalScrollBar().setEnabled(false);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        for (JScrollPane sc : scrollpanes)
        {
            Point point = SwingUtilities.convertPoint(
                    (Component) e.getSource(), e.getPoint(), sc);

            if (sc.contains(point))
            {
                sc.getVerticalScrollBar().setEnabled(true);

                sc.getHorizontalScrollBar().setEnabled(true);
            }
        }
    }

    public void addScrollPane(JScrollPane sc)
    {
        scrollpanes.add(sc);
    }
}
