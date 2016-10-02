package freeseawind.lf.basic.internalframe;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import freeseawind.lf.canvas.LuckCanvas;
import freeseawind.lf.canvas.LuckOpaquePainter;
import freeseawind.lf.layout.AbstractLayout;
import freeseawind.ninepatch.swing.SwingNinePatch;

/**
 * <pre>
 * 内部窗体标题UI实现类，在{@link BasicInternalFrameTitlePane}基础上做了如下改变：
 * <li><strong>去掉标题栏系统菜单。</strong></li>
 * <li><strong>使用图片作为标题面板背景。</strong></li>
 * <li><strong>重写布局设置标题栏默认高度。</strong></li>
 * <li><strong>扁平化标题栏按钮。</strong></li>
 * -----------------------------------------------------------------------------
 * InternalFrame title pane implement class, based on {@link BasicInternalFrameTitlePane}
 * made the following changes:
 * <li><strong>Remove the title bar system menu.</strong></li>
 * <li><strong>Use the image as the title panel background.</strong></li>
 * <li><strong>Rewrite Layout Sets the title bar's default height.</strong></li>
 * <li><strong>Flatten the title bar button.</strong></li>
 * </pre>
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckInternalFrameTitlePane extends BasicInternalFrameTitlePane
        implements LuckCanvas
{
    private static final long serialVersionUID = 370015938063841015L;
    private LuckOpaquePainter painter = new LuckOpaquePainter();
    private SwingNinePatch np;

    public LuckInternalFrameTitlePane(JInternalFrame f)
    {
        super(f);

        Object obj = UIManager.get(LuckInternalFrameUIBundle.TITLEPANEL_BG_IMG);

        if(obj != null)
        {
            np = new SwingNinePatch((BufferedImage) obj);
        }
    }

    @Override
    public void paint(Graphics g)
    {
        painter.paintOpaque(g, this, this, null);
    }

    /**
     * call super paint method.
     */
    public void drawComponent(Graphics g, JComponent c)
    {
        super.paint(g);
    }

    /**
     * release resource.
     */
    protected void uninstallDefaults()
    {
        super.uninstallDefaults();

        maxIcon = null;
        minIcon = null;
        iconIcon = null;
        closeIcon = null;
        np = null;
    }

    /**
     * <p>
     * 如果设置了背景图片，则使用图片绘制标题栏背景。
     * </p>
     *
     * <p>
     * If you set the background image, then use the picture to draw the title
     * bar background.
     * </p>
     */
    protected void paintTitleBackground(Graphics g)
    {
        if(np == null)
        {
            super.paintTitleBackground(g);
        }
        else
        {
            np.drawNinePatch((Graphics2D) g, 0, 0, getWidth(), getHeight());
        }
    }

    /**
     * <pre>
     * use custom title pane layout.
     *
     * disable JMenubar.
     * </pre>
     */
    protected void installTitlePane()
    {
        super.installTitlePane();

        // 使用自定义布局
        // set custom layout manager.
        setLayout(new LuckTitlePaneLayout());

        // 禁用系统菜单
        // disable MenuBar
        menuBar.setEnabled(false);
    }

    /**
     * <p>根据窗体状态显示不同的按钮图标<p>
     *
     * <p>Display different button icons according to the status of the form.<p>
     */
    protected void setButtonIcons()
    {
        super.setButtonIcons();

        if(frame.isMaximum())
        {
            setBtnAtrr(maxButton,
                    LuckInternalFrameUIBundle.MAXICON_NORMAL,
                    LuckInternalFrameUIBundle.MAXICON_ROLLVER,
                    LuckInternalFrameUIBundle.MAXICON_PRESSED);
        }
        else
        {
            setBtnAtrr(maxButton,
                    LuckInternalFrameUIBundle.MAXMINIMIZEICON_NORMAL,
                    LuckInternalFrameUIBundle.MAXMINIMIZEICON_ROLLVER,
                    LuckInternalFrameUIBundle.MAXMINIMIZEICON_PRESSED);
        }

        if (frame.isIcon())
        {
            setBtnAtrr(iconButton,
                    LuckInternalFrameUIBundle.MAXICON_NORMAL,
                    LuckInternalFrameUIBundle.MAXICON_ROLLVER,
                    LuckInternalFrameUIBundle.MAXICON_PRESSED);

            setBtnAtrr(maxButton,
                    LuckInternalFrameUIBundle.MAXMINIMIZEICON_NORMAL,
                    LuckInternalFrameUIBundle.MAXMINIMIZEICON_ROLLVER,
                    LuckInternalFrameUIBundle.MAXMINIMIZEICON_PRESSED);
        }
        else
        {
            setBtnAtrr(iconButton,
                    LuckInternalFrameUIBundle.ICONIFYICON_NORMAL,
                    LuckInternalFrameUIBundle.ICONIFYICON_ROLLVER,
                    LuckInternalFrameUIBundle.ICONIFYICON_PRESSED);
        }

        //
        setBtnAtrr(closeButton,
                LuckInternalFrameUIBundle.CLOSEICON_NORMAL,
                LuckInternalFrameUIBundle.CLOSEICON_ROLLVER,
                LuckInternalFrameUIBundle.CLOSEICON_PRESSED);
    }

    /**
     * set Button attribute.
     *
     * @param btn Button object.
     * @param normalIcon  button icon properties.
     * @param rollverIcon button icon properties when mouse over.
     * @param pressedIcon button icon properties when mouse click.
     */
    private void setBtnAtrr(JButton btn,
                            String normalIcon,
                            String rollverIcon,
                            String pressedIcon)
    {
        btn.setBorder(null);

        btn.setFocusPainted(false);

        btn.setFocusable(false);

        btn.setBackground(null);

        btn.setContentAreaFilled(false);

        btn.setEnabled(false);

        btn.setIcon(UIManager.getIcon(normalIcon));

        btn.setRolloverIcon(UIManager.getIcon(rollverIcon));

        btn.setPressedIcon(UIManager.getIcon(pressedIcon));

        btn.setEnabled(true);
    }

    public class LuckTitlePaneLayout extends AbstractLayout
    {
        @Override
        public Dimension preferredLayoutSize(Container parent)
        {
            return minimumLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container c)
        {
            // Calculate width.
            int width = 22;

            int height = UIManager.getInt(LuckInternalFrameUIBundle.TITLEPANE_HEIGHT);

            if (frame.isClosable())
            {
                width += closeButton.getWidth();
            }

            if (frame.isMaximizable())
            {
                width += maxButton.getWidth();
            }

            if (frame.isIconifiable())
            {
                width += iconButton.getWidth();
            }

            FontMetrics fm = frame.getFontMetrics(getFont());

            String frameTitle = frame.getTitle();

            int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;

            int title_length = frameTitle != null ? frameTitle.length() : 0;

            // Leave room for three characters in the title.
            if (title_length > 3)
            {
                int subtitle_w = fm.stringWidth(frameTitle.substring(0, 3) + "...");

                width += (title_w < subtitle_w) ? title_w : subtitle_w;
            }
            else
            {
                width += title_w;
            }

            Dimension dim = new Dimension(width + 4, height);

            // Take into account the border insets if any.
            if (getBorder() != null)
            {
                Insets insets = getBorder().getBorderInsets(c);
                dim.height += insets.top + insets.bottom;
                dim.width += insets.left + insets.right;
            }

            return dim;
        }

        public void layoutContainer(Container c)
        {
            boolean leftToRight = frame.getComponentOrientation().isLeftToRight();

            int w = getWidth();

            Rectangle bound = c.getBounds();

            int startX = (leftToRight) ? 4 : w - 16 - 5;

            menuBar.setBounds(startX, 4, 16, 16);

            // 起始x坐标
            startX = leftToRight ? bound.width : 0;

            if (frame.isClosable() && closeButton.getIcon() != null)
            {
                int closeBtnW = closeButton.getIcon().getIconWidth();

                int closeBtnH = closeButton.getIcon().getIconHeight();

                startX = leftToRight ? (startX - closeBtnW) : startX;

                closeButton.setBounds(startX, 0, closeBtnW, closeBtnH);

                startX = leftToRight ? startX : startX + closeBtnW;
            }

            if (frame.isMaximizable() && maxButton.getIcon() != null)
            {
                int maximizeBtnW = maxButton.getIcon().getIconWidth();

                int maximizeBtnH = maxButton.getIcon().getIconHeight();

                startX = leftToRight ? (startX - maximizeBtnW) : startX;

                maxButton.setBounds(startX, 0, maximizeBtnW, maximizeBtnH);

                startX = leftToRight ? startX : startX + maximizeBtnW;
            }

            if (frame.isIconifiable() && iconButton.getIcon() != null)
            {
                int minBtnW = iconButton.getIcon().getIconWidth();

                int minBtnH = iconButton.getIcon().getIconHeight();

                startX = leftToRight ? (startX - minBtnW) : startX;

                iconButton.setBounds(startX, 0, minBtnW, minBtnH);
            }
        }
    }
}
