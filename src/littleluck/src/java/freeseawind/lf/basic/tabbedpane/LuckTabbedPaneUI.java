package freeseawind.lf.basic.tabbedpane;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author freeseawind@github
 *
 */
public class LuckTabbedPaneUI extends BasicTabbedPaneUI
{
    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 4;
    public static final int EAST = 8;
    protected Color outerGradientStart;
    protected Color outerGradientEnd;
    protected Color innerGradientStart;
    protected Color innerGradientEnd;
    protected Color higlightGradientStart;
    protected Color higlightGradientEnd;

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckTabbedPaneUI();
    }


    public void installUI(JComponent c)
    {
        super.installUI(c);

        configureColor();
    }

    protected void configureColor()
    {
        outerGradientStart = new Color(252, 252, 252);

        outerGradientEnd = new Color(226, 226, 226);

        innerGradientStart = new Color(238, 238, 238);

        innerGradientEnd = new Color(217, 217, 217);

        higlightGradientStart = new Color(217, 217, 217);

        higlightGradientEnd = new Color(217, 217, 217);
    }

    protected void paintTab(Graphics g,
                            int tabPlacement,
                            Rectangle[] rects,
                            int tabIndex,
                            Rectangle iconRect,
                            Rectangle textRect)
    {
        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    }

    protected void paintTabBackground(Graphics g,
                                      int tabPlacement,
                                      int tabIndex,
                                      int x,
                                      int y,
                                      int w,
                                      int h,
                                      boolean isSelected)
    {

        GradientPaint outerGradient = null;

        GradientPaint innerGradient = null;

        Color innerStart = innerGradientStart;

        Color innerEnd = innerGradientEnd;

        if(isSelected)
        {
            innerEnd = innerStart;
        }

        switch (tabPlacement)
        {
            case LEFT:

                outerGradient = new GradientPaint(x, y, outerGradientStart, x,
                        y + h - 1, outerGradientEnd);

                ((Graphics2D) g).setPaint(outerGradient);

                g.fillRect(x, y + 1, w, h - 3);

                innerGradient = new GradientPaint(x + 1, y + 1, innerStart, x + 1,
                        y + h - 2, innerEnd);

                ((Graphics2D) g).setPaint(innerGradient);

                g.fillRect(x + 1, y + 3, w - 2, h - 6);

                break;

            case RIGHT:

                outerGradient = new GradientPaint(x, y, outerGradientStart, x,
                        y + h - 1, outerGradientEnd);

                ((Graphics2D) g).setPaint(outerGradient);

                g.fillRect(x, y + 1, w, h - 3);

                innerGradient = new GradientPaint(x + 1, y + 1, innerStart, x + 1,
                        y + h - 2, innerEnd);

                ((Graphics2D) g).setPaint(innerGradient);

                g.fillRect(x + 2, y + 3, w - 3, h - 6);

                break;

            case TOP:

            case BOTTOM:

                outerGradient = new GradientPaint(x, y, outerGradientStart, x,
                        y + h - 1, outerGradientEnd);

                ((Graphics2D) g).setPaint(outerGradient);

                g.fillRect(x + 2, y + 1, w - 2, h - 1);

                innerGradient = new GradientPaint(1, 1, innerStart, 1,
                        h - 2, innerEnd);

                ((Graphics2D) g).setPaint(innerGradient);

                g.fillRect(x + 3, y + 2, w - 5, h - 3);

                break;

            default:
                break;
        }
    }

    /**
     *
     * 重写方法取消边框绘制
     */
    protected void paintTabBorder(Graphics g,
                                  int tabPlacement,
                                  int tabIndex,
                                  int x,
                                  int y,
                                  int w,
                                  int h,
                                  boolean isSelected)
    {
        g.setColor(UIManager.getColor(LuckTabbedPaneUIBundle.SHADOW));

        switch (tabPlacement)
        {
            case LEFT:

            case RIGHT:

                paintBorder(g, x, y + 1, w - 1, h - 2, new Color(201, 201, 201), NORTH +  SOUTH);

                break;

            case TOP:

                paintBorder(g, x + 1, y, w - 2, h, new Color(201, 201, 201), NORTH + WEST + EAST);

                break;

            case BOTTOM:

                paintBorder(g, x + 1, y, w - 2, h, new Color(201, 201, 201), SOUTH + WEST + EAST);

                break;

            default:

                break;
        }
    }

    /**
     * 重写方法取消边框绘制
     */
    protected void paintContentBorderTopEdge(Graphics g,
                                             int tabPlacement,
                                             int selectedIndex,
                                             int x,
                                             int y,
                                             int w,
                                             int h)
    {
        if(tabPlacement == TOP)
        {
            g.setColor(UIManager.getColor(LuckTabbedPaneUIBundle.SHADOW));
            g.drawLine(x, y, x + w, y);
        }
    }

    protected void paintContentBorderLeftEdge(Graphics g,
                                              int tabPlacement,
                                              int selectedIndex,
                                              int x,
                                              int y,
                                              int w,
                                              int h)
    {
        if(tabPlacement == LEFT)
        {
            g.setColor(UIManager.getColor(LuckTabbedPaneUIBundle.SHADOW));
            g.drawLine(x, y, x, y + h);
        }
    }

    protected void paintContentBorderBottomEdge(Graphics g,
                                                int tabPlacement,
                                                int selectedIndex,
                                                int x,
                                                int y,
                                                int w,
                                                int h)
    {
        if(tabPlacement == BOTTOM)
        {
            g.setColor(UIManager.getColor(LuckTabbedPaneUIBundle.SHADOW));
            g.drawLine(x, y + h, x + w, y + h);
        }
    }

    protected void paintContentBorderRightEdge(Graphics g,
                                               int tabPlacement,
                                               int selectedIndex,
                                               int x,
                                               int y,
                                               int w,
                                               int h)
    {
        if(tabPlacement == RIGHT)
        {
            g.setColor(UIManager.getColor(LuckTabbedPaneUIBundle.SHADOW));
            g.drawLine(x + w, y, x + w, y + h);
        }
    }

    private void paintBorder(Graphics g,
                             int x,
                             int y,
                             int width,
                             int height,
                             Color color,
                             int rule)
    {
        Color oldColor = g.getColor();

        g.setColor(color);

        // draw top
        if((rule & NORTH) != 0)
        {
            g.drawLine(x, y, x + width, y);
        }

        // draw left
        if((rule & WEST) != 0)
        {
            g.drawLine(x, y, x, y + height);
        }

        // draw bottom
        if((rule & SOUTH) != 0)
        {
            g.drawLine(x, y + height - 1, x + width, y + height - 1);
        }

        // draw right
        if((rule & EAST) != 0)
        {
            g.drawLine(x + width, y, x + width, y + height - 1);
        }

        g.setColor(oldColor);
    }

    protected int getTabLabelShiftY(int tabPlacement,
                                    int tabIndex,
                                    boolean isSelected)
    {
        return 0;
    }

    protected int getTabLabelShiftX(int tabPlacement,
                                    int tabIndex,
                                    boolean isSelected)
    {
        return 0;
    }
}
