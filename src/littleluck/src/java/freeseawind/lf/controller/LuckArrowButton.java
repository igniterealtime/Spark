package freeseawind.lf.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * 箭头按钮实现类, 参考BasciArrowButton实现
 *
 * @see BasicArrowButton
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckArrowButton extends JButton implements SwingConstants
{
    /**
     * The direction of the arrow. One of {@code SwingConstants.NORTH},
     * {@code SwingConstants.SOUTH}, {@code SwingConstants.EAST} or
     * {@code SwingConstants.WEST}.
     */
    protected int direction;
    protected Color normal;
    protected Color highlight;
    private static final long serialVersionUID = -5455122549884120624L;

    /**
     * Creates a {@code BasicArrowButton} whose arrow is drawn in the specified
     * direction and with the specified colors.
     *
     * @param direction the direction of the arrow; one of
     *            {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH},
     *            {@code SwingConstants.EAST} or {@code SwingConstants.WEST}
     * @since 1.4
     */
    public LuckArrowButton(int direction)
    {
        this(direction, new Color(122, 138, 153), new Color(60, 175, 210));
    }


    public LuckArrowButton(int direction,
                           Color normal,
                           Color highlight)
    {
        super();
        setRequestFocusEnabled(false);
        setDirection(direction);
        this.normal = normal;
        this.highlight = highlight;
        setContentAreaFilled(false);
    }

    /**
     * @return the direction of the arrow.
     */
    public int getDirection()
    {
        return direction;
    }

    /**
     * Sets the direction of the arrow.
     *
     * @param direction
     *            the direction of the arrow; one of of
     *            {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH},
     *            {@code SwingConstants.EAST} or {@code SwingConstants.WEST}
     */
    public void setDirection(int direction)
    {
        this.direction = direction;
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (getModel().isPressed())
        {
            g.translate(1, 1);
        }

        // Draw the arrow
        int w = getSize().width;
        int h = getSize().height;

        int size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 3);

        paintTriangle(g, (w - size) / 2, (h - size) / 2, size, direction);

        // Reset the Graphics back to it's original settings
        if (getModel().isPressed())
        {
            g.translate(-1, -1);
        }
    }

    /**
     * Paints a triangle.
     *
     * @param g
     *            the {@code Graphics} to draw to
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @param size
     *            the size of the triangle to draw
     * @param direction
     *            the direction in which to draw the arrow; one of
     *            {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH},
     *            {@code SwingConstants.EAST} or {@code SwingConstants.WEST}
     */
    public void paintTriangle(Graphics g,
                              int x,
                              int y,
                              int size,
                              int direction)
    {
        int mid = 0;
        int i = 0;
        int j = 0;

        size = Math.min(4, size);

        mid = (size / 2);

        g.translate(x, y);

        g.setColor(getArrowColor(model));

        switch (direction)
        {
            case NORTH:

                for (i = 0; i < size; i++)
                {
                    g.drawLine(mid - i, i, mid + i, i);
                }

                break;

            case SOUTH:

                j = 0;

                for (i = size - 1; i >= 0; i--)
                {
                    g.drawLine(mid - i, j, mid + i, j);

                    j++;
                }

                break;

            case WEST:

                for (i = 0; i < size; i++)
                {
                    g.drawLine(i, mid - i, i, mid + i);
                }

                break;

            case EAST:

                j = 0;

                for (i = size - 1; i >= 0; i--)
                {
                    g.drawLine(j, mid - i, j, mid + i);

                    j++;
                }

                break;
        }

        g.translate(-x, -y);
    }

    protected Color getArrowColor(ButtonModel model)
    {
        if(model.isPressed())
        {
            return highlight;
        }

        return normal;
    }

    /**
     * Returns the preferred size of the {@code BasicArrowButton}.
     *
     * @return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(16, 16);
    }

    /**
     * Returns the minimum size of the {@code BasicArrowButton}.
     *
     * @return the minimum size
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(5, 5);
    }

    /**
     * Returns the maximum size of the {@code BasicArrowButton}.
     *
     * @return the maximum size
     */
    public Dimension getMaximumSize()
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns whether the arrow button should get the focus.
     * {@code BasicArrowButton}s are used as a child component of composite
     * components such as {@code JScrollBar} and {@code JComboBox}. Since the
     * composite component typically gets the focus, this method is overriden to
     * return {@code false}.
     *
     * @return {@code false}
     */
    public boolean isFocusTraversable()
    {
        return false;
    }
}
