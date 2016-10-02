package freeseawind.lf.basic.rootpane;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;

import freeseawind.lf.layout.AbstractLayout;

/**
 * <p>
 * 标题面板布局类, 主要对窗体的标题、图标和按钮就行布局。
 * </p>
 * 
 * <p>
 * A custom layout manager that is responsible for the layout of window button,
 * window icon, window title, if one has been installed.
 * <p>
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTitlePanelLayout extends AbstractLayout
{
    public void layoutContainer(Container parent)
    {
        LuckTitlePanel titlePanel = (LuckTitlePanel) parent;

        Rectangle bound = parent.getBounds();

        Insets insets = parent.getInsets();

        //
        int w = bound.width - insets.right - insets.left;

        if (w <= 0)
        {
            return;
        }

        // 判断从左到右进行布局还是从右到左进行布局
        // get component orientation
        boolean isLeftToRight = titlePanel.isLeftToRight();

        // 布局关闭按钮
        // layout close button
        JButton closebtn = titlePanel.getCloseBtn();

        // 起始x坐标
        // Starting x coordinate
        int startX = isLeftToRight ? (bound.width - insets.right) : 0;

        // 起始y坐标
        // Starting y coordinate
        int startY = insets.top;

        if(closebtn != null && closebtn.isVisible())
        {
            int closeBtnW = closebtn.getIcon().getIconWidth();

            int closeBtnH = closebtn.getIcon().getIconHeight();

            startX = isLeftToRight ? (startX - closeBtnW) : startX;

            closebtn.setBounds(startX, startY, closeBtnW, closeBtnH);

            startX = isLeftToRight ? startX : startX + closeBtnW;
        }

        // 布局放大或缩小按钮
        // layout maximize button
        JButton maximizeBtn = titlePanel.getMaximizeBtn();

        if(maximizeBtn != null && maximizeBtn.isVisible())
        {
            int maximizeBtnW = maximizeBtn.getIcon().getIconWidth();

            int maximizeBtnH = maximizeBtn.getIcon().getIconHeight();

            startX = isLeftToRight ? startX - maximizeBtnW : startX;

            maximizeBtn.setBounds(startX, startY, maximizeBtnW, maximizeBtnH);

            startX = isLeftToRight ? startX : startX + maximizeBtnW;
        }

        // 布局最小化按钮
        // layout minimize button
        JButton minBtn = titlePanel.getMinBtn();

        if (minBtn != null && minBtn.isVisible())
        {
            int minBtnW = minBtn.getIcon().getIconWidth();

            int minBtnH = minBtn.getIcon().getIconHeight();

            startX = isLeftToRight ? startX - minBtnW : startX;

            minBtn.setBounds(startX, startY, minBtnW, minBtnH);

            startX = isLeftToRight ? startX : startX + minBtnW;
        }

        //
        JLabel titleLabel = titlePanel.getLabel();

        String text = titleLabel.getText();

        FontMetrics ff = titleLabel.getFontMetrics(titleLabel.getFont());

        int labelW = 0;

        if(text != null)
        {
            labelW = ff.stringWidth(text) + titleLabel.getIconTextGap();
        }

        int labelH = ff.getHeight();

        Icon labelIcon = titleLabel.getIcon();

        if(labelIcon != null)
        {
            labelW += labelIcon.getIconWidth();

            labelH = Math.max(labelH, labelIcon.getIconHeight());
        }

        startX = isLeftToRight ?  0 : ((w - labelW) / 2);

        Insets titleInsets = UIManager.getInsets(LuckRootPaneUIBundle.APPLICATION_TITLE_INSETS);

        titleLabel.setBounds(startX + titleInsets.left, 0 + titleInsets.top, labelW, labelH);
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        LuckTitlePanel root = (LuckTitlePanel) parent;

        Insets i = parent.getInsets();

        // 高度由父面板决定
        // Height is determined by the parent panel
        return new Dimension(0 + i.left + i.right, root.getHeight() + i.top + i.bottom);
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        return preferredLayoutSize(parent);
    }

    public Dimension maximumLayoutSize(Container parent)
    {
        return preferredLayoutSize(parent);
    }
}
