package freeseawind.lf.basic.scroll;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * ScrollPaneUI and ScrollBarUI and ViewportUI 资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckScrollUIBundle extends LuckResourceBundle
{
    /**
     * [自定义属性]滚动条点九图属性key
     */
    public static final String SCROLLBAR_THUMBICON = "Scrollbar.thumbIcon";

    /**
     * 滚动条边框属性key
     */
    public static final String SCROLLBAR_BORDER = "ScrollBar.border";

    /**
     * 滚动面板边框属性key
     */
    public static final String SCROLLPANE_BORDER = "ScrollPane.border";

    /**
     * 滚动条背景颜色属性key
     */
    public static final String SCROLLBAR_BACKGROUND = "ScrollBar.background";

    /**
     * 滚动条宽度属性key
     */
    public static final String SCROLLBAR_WIDTH = "ScrollBar.width";

    /**
     * 滑块最小大小属性key
     */
    public static final String MINIMUMTHUMBSIZE = "ScrollBar.minimumThumbSize";


    @Override
    protected void installBorder()
    {
        UIManager.put(SCROLLBAR_BORDER, BorderFactory.createEmptyBorder());

        UIManager.put(SCROLLPANE_BORDER, new LineBorder(new Color(200, 200, 200)));
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(SCROLLBAR_THUMBICON, LuckRes.getImage("scroll/thumb.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(SCROLLBAR_WIDTH, 9);

        UIManager.put(SCROLLBAR_BACKGROUND, Color.WHITE);

        UIManager.put(MINIMUMTHUMBSIZE, new Dimension(48, 48));
    }
}
