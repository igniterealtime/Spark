package freeseawind.lf.basic.scroll;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.DimensionUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <p>ScrollPaneUI and ScrollBarUI and ViewportUI 资源绑定类。</p>
 *
 * <p>ScrollPaneUI and ScrollBarUI and ViewportUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckScrollUIBundle extends LuckResourceBundle
{
    /**
     * <p>[LittleLuck属性]滚动条点九图属性key。</p>
     *
     * <p>[LittLeLuck Attributes] Scroll bar thumb image properties.</p>
     */
    public static final String SCROLLBAR_THUMBIMG = "Scrollbar.thumbIcon";

    /**
     * <p>滚动条边框属性key。</p>
     *
     * <p>Scroll bar border properties.</p>
     */
    public static final String SCROLLBAR_BORDER = "ScrollBar.border";

    /**
     * <p>滚动面板边框属性key。</p>
     *
     * <p>Scroll pane border properties.</p>
     */
    public static final String SCROLLPANE_BORDER = "ScrollPane.border";

    /**
     * <p>滚动条背景颜色属性key。</p>
     *
     * <p>Scroll bar background color properties.</p>
     */
    public static final String SCROLLBAR_BACKGROUND = "ScrollBar.background";

    /**
     * <p>滚动条宽度属性key, 默认值为9。</p>
     *
     * <p>Scroll bar width properties, default value 9.</p>
     */
    public static final String SCROLLBAR_WIDTH = "ScrollBar.width";

    /**
     * <p>
     * 滑块最小大小属性key, 默认值为Dimension(48,48)。
     * </p>
     *
     * <p>
     * Scroll bar minimum thumb size properties, default value
     * <code>Dimension(48, 48)</code>.
     * </p>
     */
    public static final String MINIMUMTHUMBSIZE = "ScrollBar.minimumThumbSize";

    public void uninitialize()
    {
        UIManager.put(SCROLLBAR_THUMBIMG, null);
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        table.put(SCROLLBAR_BORDER, getBorderRes(BorderFactory.createEmptyBorder()));

        table.put(SCROLLPANE_BORDER, getBorderRes(new LineBorder(new Color(200, 200, 200))));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        table.put(SCROLLBAR_THUMBIMG, LuckRes.getImage("scroll/thumb.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        table.put(SCROLLBAR_WIDTH, 9);

        table.put(SCROLLBAR_BACKGROUND, getColorRes(Color.WHITE));

        table.put(MINIMUMTHUMBSIZE, new DimensionUIResource(48, 48));
    }
}
