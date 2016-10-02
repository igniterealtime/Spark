package freeseawind.lf.basic.progress;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import freeseawind.lf.utils.LuckUtils;
import freeseawind.ninepatch.common.RepeatType;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * <p>
 * ProgressBarUI实现类，使用点九图来实现进度条的绘制。
 * </p>
 *
 * <p>
 * ProgressBarUI implementation class, the use of NinePatch image to achieve the
 * progress of the drawing.
 * </p>
 * <p>
 * See Also: {@link LuckProgressBarUIBundle}.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckProgressBarUI extends BasicProgressBarUI
{
    // 单元进度条和背景进度条间距属性
    // progress bar progress and background spacing properties
    private Insets cellBarInsets;

    // 水平进度条背景点九绘图对象
    // Horizontal progress bar background NinePatch objects
    private SwingNinePatch horizontalNp;

    // 水平进度条单元进度点九绘图对象
    // Horizontal progress bar progress NinePatch objects
    private SwingNinePatch horizontalCellNp;

    // 垂直进度条单元进度点九绘图对象
    // Vertical progress bar background NinePatch objects
    private SwingNinePatch verticalNp;

    // 垂直进度条单元进度点九绘图对象
    // Vertical progress bar progress NinePatch objects
    private SwingNinePatch verticalCellNp;

    public static ComponentUI createUI(JComponent x)
    {
        return new LuckProgressBarUI();
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);

        cellBarInsets = UIManager.getInsets(LuckProgressBarUIBundle.CELLBAR_INSETS);
    }

    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);

        horizontalNp = null;

        horizontalCellNp = null;

        verticalNp = null;

        verticalCellNp = null;
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c)
    {
        if (!(g instanceof Graphics2D))
        {
            return;
        }

        // area for border
        Insets b = progressBar.getInsets();
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0)
        {
            return;
        }

        boolean isHorizontal = (progressBar.getOrientation() == JProgressBar.HORIZONTAL);

        Graphics2D g2 = (Graphics2D)g;

        paintProgressBarBg(g2, 0, 0, progressBar.getWidth(), progressBar.getHeight(), isHorizontal);

        // Paint the bouncing box.
        boxRect = getBox(boxRect);

        if (boxRect != null)
        {
            paintProgressBarCell(g2, boxRect.x, boxRect.y, boxRect.width, boxRect.height, isHorizontal);
        }

        // 父类中该处调用的是私有的paintString方法。
        // 通读方法后发现实际上有做兼容处理，所以这样调用并不会影响原有效果
        // The parent class calls the private paintString method.
        // Read through the method and found that in fact do compatible
        // processing, so this call will not affect the original effect
        if (progressBar.isStringPainted())
        {
            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL)
            {
                paintString(g2, b.left, b.top, barRectWidth, barRectHeight, 0, b);
            }
            else
            {
                paintString(g2, b.left, b.top, barRectWidth, barRectHeight, 0, b);
            }
        }
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c)
    {
        if (!(g instanceof Graphics2D))
        {
            return;
        }

        // area for border
        Insets b = progressBar.getInsets();

        int barRectWidth = progressBar.getWidth() - (b.right + b.left);

        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0)
        {
            return;
        }

        // Progress of the current progress bar loading, if the horizontal
        // progress bar, return the width value, otherwise the height value
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        Graphics2D g2 = (Graphics2D) g;

        boolean isHorizontal = (progressBar.getOrientation() == JProgressBar.HORIZONTAL);

        paintProgressBarBg(g2, b.left, b.top, barRectWidth, barRectHeight, isHorizontal);

        // 以下的处理主要是为了让单元进度和背景保持一定的间距，这样比较美观
        // The following is mainly to deal with the progress of the unit and the
        // background to maintain a certain distance, so beautiful
        int cellWidth = barRectWidth;

        int cellHeight = barRectHeight;

        int startx = b.left;

        int starty = b.top;

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL)
        {
            cellWidth = cellWidth - cellBarInsets.left - cellBarInsets.right;

            cellHeight = cellHeight - cellBarInsets.top - cellBarInsets.bottom;

            // 因为起始坐标和背景的起始坐标不一样，如果仍按原宽度绘制，会导进度条超出背景宽度
            // Because the starting coordinates and the coordinates of the background is not the same.
            // if still drawing the original width,will lead the progress bar beyond the background width.
            amountFull = (amountFull < cellWidth ? amountFull : cellWidth);

            cellWidth = amountFull;

            // 重新计算起始坐标
            // Recalculate start coordinates
            startx =  b.left + cellBarInsets.left;

            starty = b.top + cellBarInsets.top;
        }
        else
        {
            cellWidth = cellWidth - cellBarInsets.top - cellBarInsets.bottom;

            cellHeight = cellHeight - cellBarInsets.left - cellBarInsets.right;

            amountFull = (amountFull < cellHeight ? amountFull : cellHeight);

            cellHeight = amountFull;

            startx =  b.left + cellBarInsets.top;

            starty = b.top + barRectHeight - amountFull - cellBarInsets.top;
        }

        if(amountFull > 0)
        {
            paintProgressBarCell(g2, startx, starty, cellWidth, cellHeight, isHorizontal);
        }

        // Deal with possible text painting
        if (progressBar.isStringPainted())
        {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        }
    }

    /**
     * Draw progress bar background
     *
     * @param g2d The drawing canvas object
     * @param x The starting x coordinate
     * @param y The starting y coordinate
     * @param w The width of the drawing
     * @param h The height of the drawing
     * @param isHorizontal The horizontal progress bar returns true, otherwise false
     */
    protected void paintProgressBarBg(Graphics2D g2d,
                                      int x,
                                      int y,
                                      int w,
                                      int h,
                                      boolean isHorizontal)
    {
        configureProgressBarBg(isHorizontal);

        if(isHorizontal)
        {
            horizontalNp.drawNinePatch(g2d, x, y, w, h);
        }
        else
        {
            verticalNp.drawNinePatch(g2d, x, y, w, h);
        }
    }

    /**
     * Draw progress bar progress
     *
     * @param g2d The drawing canvas object
     * @param x The starting x coordinate
     * @param y The starting y coordinate
     * @param w The width of the drawing
     * @param h The height of the drawing
     * @param isHorizontal The horizontal progress bar returns true, otherwise false
     */
    protected void paintProgressBarCell(Graphics2D g2d,
                                        int x,
                                        int y,
                                        int w,
                                        int h,
                                        boolean isHorizontal)
    {
        configureProgressBarCell(isHorizontal);

        if(isHorizontal)
        {
            horizontalCellNp.drawNinePatch(g2d, x, y, w, h);
        }
        else
        {
            verticalCellNp.drawNinePatch(g2d, x, y, w, h);
        }
    }

    protected void configureProgressBarBg(boolean isHorizontal)
    {
        if(isHorizontal && horizontalNp == null)
        {
            //
            horizontalNp = LuckUtils.createNinePatch(LuckProgressBarUIBundle.HORIZONTALIMG);
        }
        else if (!isHorizontal && verticalNp == null)
        {
            //
            verticalNp = LuckUtils.createNinePatch(LuckProgressBarUIBundle.VERTICALIMG);
        }
    }

    protected void configureProgressBarCell(boolean isHorizontal)
    {
        if(isHorizontal && horizontalCellNp == null)
        {
            //
            horizontalCellNp = LuckUtils.createNinePatch(
                    LuckProgressBarUIBundle.HORIZONTALCELLIMG, RepeatType.HORIZONTAL);
        }
        else if (!isHorizontal && verticalCellNp == null)
        {
            //
            verticalCellNp = LuckUtils.createNinePatch(
                    LuckProgressBarUIBundle.VERTICALCELLIMG, RepeatType.VERTICAL);
        }
    }
}
