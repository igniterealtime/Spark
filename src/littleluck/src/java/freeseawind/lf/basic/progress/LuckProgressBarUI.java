package freeseawind.lf.basic.progress;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import freeseawind.ninepatch.common.RepeatType;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * ProgressBarUI实现类，使用点九图来实现进度条的绘制。
 * <p>
 * 另请参见 {@link LuckProgressBarUIBundle}
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckProgressBarUI extends BasicProgressBarUI
{
    // 单元进度条和背景进度条间距属性
    private Insets cellBarInsets;

    // 水平进度条背景点九绘图对象
    private SwingNinePatch horizontalNp;

    // 水平进度条单元进度点九绘图对象
    private SwingNinePatch horizontalCellNp;

    // 垂直进度条单元进度点九绘图对象
    private SwingNinePatch verticalNp;

    // 垂直进度条单元进度点九绘图对象
    private SwingNinePatch verticalCellNp;


    public static ComponentUI createUI(JComponent x)
    {
        return new LuckProgressBarUI();
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);

        // -------------初始化扩展属性-------------- //

        cellBarInsets = UIManager.getInsets(LuckProgressBarUIBundle.CELLBAR_INSETS);

        //
        BufferedImage horizontalImg = (BufferedImage) UIManager
                .get(LuckProgressBarUIBundle.HORIZONTALICON);

        horizontalNp = new SwingNinePatch(horizontalImg);

        //
        BufferedImage horizontalCellImg = (BufferedImage) UIManager
                .get(LuckProgressBarUIBundle.HORIZONTALCELLICON);

        horizontalCellNp = new SwingNinePatch(horizontalCellImg,
                RepeatType.HORIZONTAL);

        //
        BufferedImage verticalImg = (BufferedImage) UIManager
                .get(LuckProgressBarUIBundle.VERTICALICON);

        verticalNp = new SwingNinePatch(verticalImg);

        //
        BufferedImage verticalCellImg = (BufferedImage) UIManager
                .get(LuckProgressBarUIBundle.VERTICALCELLICON);

        verticalCellNp = new SwingNinePatch(verticalCellImg,
                RepeatType.VERTICAL);
    }

    /**
     * 不确定进度的进度条绘制方法
     */
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

        // 判断是否是水平进度条
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

    /**
     * 已确定进度的进度条绘制方法
     */
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

        // 当前进度条加载的进度, 如果是水平进度条,返回的是宽度值,否则是高度值
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        Graphics2D g2 = (Graphics2D) g;

        boolean isHorizontal = (progressBar.getOrientation() == JProgressBar.HORIZONTAL);

        paintProgressBarBg(g2, b.left, b.top, barRectWidth, barRectHeight, isHorizontal);

        // 以下的处理主要是为了让单元进度和背景保持一定的间距，这样比较美观
        int cellWidth = barRectWidth;

        int cellHeight = barRectHeight;

        int startx = b.left;

        int starty = b.top;

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL)
        {
            cellWidth = cellWidth - cellBarInsets.left - cellBarInsets.right;

            cellHeight = cellHeight - cellBarInsets.top - cellBarInsets.bottom;

            // 因为起始坐标和背景的起始坐标不一样，如过仍按原宽度绘制，会导进度条超出背景宽度
            amountFull = (amountFull < cellWidth ? amountFull : cellWidth);

            cellWidth = amountFull;

            // 重新计算起始坐标
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
     * 绘制进度条背景
     *
     * @param g2d 绘图画笔对象
     * @param x 起始x坐标
     * @param y 起始y坐标
     * @param w 绘制的宽度
     * @param h 绘制的高度
     * @param isHorizontal 水平进度条返回true，否则false
     */
    protected void paintProgressBarBg(Graphics2D g2d,
                                      int x,
                                      int y,
                                      int w,
                                      int h,
                                      boolean isHorizontal)
    {
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
     * 绘制进度条进度
     *
     * @param g2d 绘图画笔对象
     * @param x 起始x坐标
     * @param y 起始y坐标
     * @param w 绘制的宽度
     * @param h 绘制的高度
     * @param isHorizontal 水平进度条返回true，否则false
     */
    protected void paintProgressBarCell(Graphics2D g2d,
                                        int x,
                                        int y,
                                        int w,
                                        int h,
                                        boolean isHorizontal)
    {
        if(isHorizontal)
        {
            horizontalCellNp.drawNinePatch(g2d, x, y, w, h);
        }
        else
        {
            verticalCellNp.drawNinePatch(g2d, x, y, w, h);
        }
    }
}
