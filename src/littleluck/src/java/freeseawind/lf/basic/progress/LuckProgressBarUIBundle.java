package freeseawind.lf.basic.progress;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * ProgressBarUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckProgressBarUIBundle extends LuckResourceBundle
{
    /**
     * 进度条边框属性key
     */
    public static final String BORDER = "ProgressBar.border";

    /**
     * 水平进度条大小属性key
     */
    public static final String HORIZONTALSIZE = "ProgressBar.horizontalSize";

    /**
     * 垂直进度条大小属性key
     */
    public static final String VERTICALSIZE = "ProgressBar.verticalSize";

    /**
     * [自定义属性] 水平进度条背景图标属性key
     */
    public static final String HORIZONTALICON = "ProgressBar.horizontalIcon";

    /**
     * [自定义属性] 水平进度条图标属性key
     */
    public static final String HORIZONTALCELLICON = "ProgressBar.horizontalCellIcon";

    /**
     * [自定义属性] 垂直进度条背景图标属性key
     */
    public static final String VERTICALICON = "ProgressBar.verticalIcon";

    /**
     * [自定义属性] 垂直进度条图标属性key
     */
    public static final String VERTICALCELLICON = "ProgressBar.verticalCellIcon";

    /**
     * [自定义属性] 进度条间距属性key
     */
    public static final String CELLBAR_INSETS = "ProgressBar.thumbInsets";

    /**
     * [自定义属性] 是否使用平铺的方式绘制点九图属性key
     */
    public static final String ISREPEAT = "ProgressBar.repeatNp";

    @Override
    protected void installBorder()
    {
        UIManager.put(BORDER, getBorderRes(BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    }

    @Override
    protected void loadImages()
    {
        UIManager.put(HORIZONTALICON, LuckRes.getImage("progress/progressbar.9.png"));

        UIManager.put(HORIZONTALCELLICON, LuckRes.getImage("progress/progressbar_cell.9.png"));

        UIManager.put(VERTICALICON, LuckRes.getImage("progress/progressbar_v.9.png"));

        UIManager.put(VERTICALCELLICON, LuckRes.getImage("progress/progressbar_cell_v.9.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(HORIZONTALSIZE, getDimensionRes(160, 10));

        UIManager.put(VERTICALSIZE, getDimensionRes(10, 160));

        UIManager.put(CELLBAR_INSETS, new InsetsUIResource(1, 2, 1, 2));
    }
}
