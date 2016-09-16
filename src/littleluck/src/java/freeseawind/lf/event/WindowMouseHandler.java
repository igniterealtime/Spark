package freeseawind.lf.event;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;

import freeseawind.lf.basic.rootpane.LuckRootPaneUIBundle;
import freeseawind.lf.geom.LuckProperty;
import freeseawind.lf.geom.LuckProperty.LuckPropertyType;
import freeseawind.lf.geom.LuckRectangle;
import freeseawind.lf.utils.LuckWindowUtil;


/**
 * Window move and resize event handler.
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class WindowMouseHandler implements MouseInputListener
{
    /**
     * true move window, false resize window.
     */
    private boolean isMovingWindow;

    /**
     * X location the mouse went down on for a drag operation.
     */
    private int dragOffsetX;

    /**
     * Y location the mouse went down on for a drag operation.
     */
    private int dragOffsetY;

    /**
     * Width of the window when the drag started.
     */
    private int dragWidth;

    /**
     * Height of the window when the drag started.
     */
    private int dragHeight;

    /**
     * default mouse cursor.
     */
    private Cursor lastCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    /**
     * Window drag area.
     */
    private LuckRectangle dragArea;

    /**
     * Window title area.
     */
    private LuckRectangle titleArea;

    //
    private int dragCursor;

    public WindowMouseHandler(JComponent parent)
    {
        int titleHeight = UIManager.getInt(LuckRootPaneUIBundle.TITLEPANEL_HEIGHT);

        LuckProperty<Integer> startxProp = new LuckProperty<Integer>(0);

        LuckProperty<Integer> startyProp = new LuckProperty<Integer>(0);

        LuckProperty<Integer> widthProp = new LuckProperty<Integer>(0);

        LuckProperty<Integer> heightProp = new LuckProperty<Integer>(LuckPropertyType.FIX, titleHeight);

        titleArea = new LuckRectangle(startxProp, startyProp, widthProp, heightProp, parent);

        dragArea = titleArea;
    }

    /**
     * handle double click titlePane resize window event.
     */
    public void mouseClicked(MouseEvent e)
    {
        Window window = (Window) e.getSource();

        if(window instanceof JFrame)
        {
            JFrame frame = (JFrame) window;

            JRootPane root = frame.getRootPane();

            // window is undecorated return.
            if (root.getWindowDecorationStyle() == JRootPane.NONE)
            {
                return;
            }

            if(!titleArea.contains(e.getPoint()))
            {
                return;
            }

            // check is double click.
            if ((e.getClickCount() % 2) == 0 && ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0))
            {
                int state = frame.getExtendedState();

                if (frame.isResizable())
                {
                    if ((state & JFrame.MAXIMIZED_BOTH) != 0)
                    {
                        frame.setExtendedState(state & ~JFrame.MAXIMIZED_BOTH);
                    }
                    else
                    {
                        frame.setExtendedState(state | JFrame.MAXIMIZED_BOTH);
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent e)
    {
        Window window = (Window) e.getSource();

        JRootPane root = LuckWindowUtil.getRootPane(window);

        if (root == null || root.getWindowDecorationStyle() == JRootPane.NONE)
        {
            return;
        }

        //fix custom drag area bug (v1.0.1): check dragCurosr is move or resize.
        if (dragArea.contains(e.getPoint())
                && dragCursor == Cursor.DEFAULT_CURSOR)
        {
            if(window instanceof JFrame)
            {
                JFrame frame = (JFrame)window;

                // 如果当前窗体是全屏状态则直接返回
                if(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
                {
                    return;
                }
            }

            // 设置为可以移动并记录当前坐标
            isMovingWindow = true;

            dragOffsetX = e.getPoint().x;

            dragOffsetY = e.getPoint().y;
        }
        else if(LuckWindowUtil.isResizable(window))
        {
            dragOffsetX = e.getPoint().x;

            dragOffsetY = e.getPoint().y;

            dragWidth = window.getWidth();

            dragHeight = window.getHeight();

            JRootPane rootPane = LuckWindowUtil.getRootPane(window);

            if(rootPane != null && LuckWindowUtil.isResizable(window))
            {
                dragCursor = getCursor(dragWidth, dragHeight, e.getPoint(), rootPane.getInsets());
            }
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        // set flag resize.
        isMovingWindow = false;

        dragCursor = 0;
    }

    public void mouseMoved(MouseEvent e)
    {
        Window window = (Window)e.getSource();

        window.toFront();

        int w = window.getWidth();

        int h = window.getHeight();

        Point point = e.getPoint();

        JRootPane rootPane = LuckWindowUtil.getRootPane(window);

        int cursor = 0;

        if(rootPane != null && LuckWindowUtil.isResizable(window))
        {
            cursor = getCursor(w, h, point, rootPane.getInsets());
        }

        if(cursor != Cursor.DEFAULT_CURSOR)
        {
            window.setCursor(Cursor.getPredefinedCursor(cursor));
        }
        else
        {
            window.setCursor(lastCursor);
        }
        
        //fix bug (v1.0.1): set last cursor, when custom drag area.
        dragCursor = cursor;
    }

    public void mouseEntered(MouseEvent e)
    {
        mouseMoved(e);
    }

    public void mouseExited(MouseEvent e)
    {
        Window w = (Window)e.getSource();
        w.setCursor(lastCursor);
    }

    public void mouseDragged(MouseEvent e)
    {
        Window w = (Window) e.getSource();

        if (isMovingWindow)
        {
            Point prePoint = e.getLocationOnScreen();

            w.setLocation(prePoint.x - dragOffsetX, prePoint.y - dragOffsetY);
        }
        else if(dragCursor != Cursor.DEFAULT_CURSOR)
        {
            updateBound(e.getPoint(), w);
        }
    }

    public int getCursor(int w, int h, Point point, Insets inset)
    {
        int radius = -1;

        //
        int startX = inset.left + radius;
        int endX = w - inset.right + radius;
        int startY = inset.top + radius;
        int endY = h - inset.bottom + radius;

        if (point.x <= startX && point.y <= startY)
        {
            return Cursor.NW_RESIZE_CURSOR;
        }
        else if (point.x >= endX && point.y <= startY)
        {
            return Cursor.NE_RESIZE_CURSOR;
        }
        else if (point.x <= startX && point.y >= endY)
        {
            return Cursor.SW_RESIZE_CURSOR;
        }
        else if(point.x >= endX && point.y >=  endY)
        {
            return Cursor.SE_RESIZE_CURSOR;
        }
        else if(point.x <= startX && point.y > startY && point.y < endY)
        {
            return Cursor.W_RESIZE_CURSOR;
        }
        else if(point.y <= startY && point.x > startX && point.x < endX)
        {
            return Cursor.N_RESIZE_CURSOR;
        }
        else if(point.x >= endX && point.y > startY && point.y < endY)
        {
            return Cursor.E_RESIZE_CURSOR;
        }
        else if(point.y >= endY && point.x > startX && point.x < endX)
        {
            return Cursor.S_RESIZE_CURSOR;
        }
        else
        {
            return Cursor.DEFAULT_CURSOR;
        }
    }

    public void updateBound(Point pt, Window w)
    {
        Rectangle r = w.getBounds();
        Rectangle startBounds = new Rectangle(r);
        Dimension min = w.getMinimumSize();

        switch (dragCursor)
        {
            case Cursor.E_RESIZE_CURSOR:

                adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX) - r.width, 0);

                break;

            case Cursor.S_RESIZE_CURSOR:

                adjust(r, min, 0, 0, 0, pt.y + (dragHeight - dragOffsetY) - r.height);

                break;

            case Cursor.N_RESIZE_CURSOR:

                adjust(r, min, 0, pt.y - dragOffsetY, 0, -(pt.y - dragOffsetY));

                break;

            case Cursor.W_RESIZE_CURSOR:

                adjust(r, min, pt.x - dragOffsetX, 0, -(pt.x - dragOffsetX), 0);

                break;

            case Cursor.NE_RESIZE_CURSOR:

                adjust(r, min, 0, pt.y - dragOffsetY, pt.x + (dragWidth - dragOffsetX)
                        - r.width, -(pt.y - dragOffsetY));
                break;
            case Cursor.SE_RESIZE_CURSOR:

                adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX) - r.width,
                        pt.y + (dragHeight - dragOffsetY) - r.height);

                break;

            case Cursor.NW_RESIZE_CURSOR:

                adjust(r, min, pt.x - dragOffsetX, pt.y - dragOffsetY,
                        -(pt.x - dragOffsetX), -(pt.y - dragOffsetY));

                break;

            case Cursor.SW_RESIZE_CURSOR:

                adjust(r, min, pt.x - dragOffsetX, 0, -(pt.x - dragOffsetX),
                        pt.y + (dragHeight - dragOffsetY) - r.height);

                break;

            default:

                break;
        }

        if(!r.equals(startBounds))
        {
            w.setBounds(r);

            if (Toolkit.getDefaultToolkit().isDynamicLayoutActive())
            {
                w.validate();
                w.repaint();
            }
        }
    }


    private void adjust(Rectangle bounds,
                        Dimension min,
                        int deltaX,
                        int deltaY,
                        int deltaWidth,
                        int deltaHeight)
    {
        bounds.x += deltaX;
        bounds.y += deltaY;
        bounds.width += deltaWidth;
        bounds.height += deltaHeight;

        if (min != null)
        {
            if (bounds.width < min.width)
            {
                int correction = min.width - bounds.width;

                if (deltaX != 0)
                {
                    bounds.x -= correction;
                }

                bounds.width = min.width;
            }

            if (bounds.height < min.height)
            {
                int correction = min.height - bounds.height;

                if (deltaY != 0)
                {
                    bounds.y -= correction;
                }

                bounds.height = min.height;
            }
        }
    }

    public void setDragArea(LuckRectangle dragArea)
    {
        this.dragArea = dragArea;
    }

    public void setTitleArea(LuckRectangle titleArea)
    {
        this.titleArea = titleArea;
    }
}
