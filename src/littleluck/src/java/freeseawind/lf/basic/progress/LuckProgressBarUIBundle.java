package freeseawind.lf.basic.progress;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <p>ProgressBarUI资源绑定类。</p>
 *
 * <p>A ProgressBarUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckProgressBarUIBundle extends LuckResourceBundle
{
    /**
     * <p>进度条边框属性key</p>
     *
     * <p>ProgressBar border properties.</p>
     */
    public static final String BORDER = "ProgressBar.border";

    /**
     * <p>水平进度条大小属性key</p>
     *
     * <p>Horizontal ProgressBar size properties.</p>
     */
    public static final String HORIZONTALSIZE = "ProgressBar.horizontalSize";

    /**
     * <p>垂直进度条大小属性key</p>
     *
     * <p>Vertical ProgressBar size properties.</p>
     */
    public static final String VERTICALSIZE = "ProgressBar.verticalSize";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 水平进度条背景图片属性key
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Horizontal ProgressBar
     * background image properties.
     * </p>
     */
    public static final String HORIZONTALIMG = "ProgressBar.horizontalIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 水平进度条图片属性key
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Horizontal ProgressBar progress
     * image properties.
     * </p>
     */
    public static final String HORIZONTALCELLIMG = "ProgressBar.horizontalCellIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 垂直进度条背景图片属性key
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Vertical ProgressBar background
     * image properties.
     * </p>
     */
    public static final String VERTICALIMG = "ProgressBar.verticalIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 垂直进度条图片属性key
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Vertical ProgressBar progress
     * image properties.
     * </p>
     */
    public static final String VERTICALCELLIMG = "ProgressBar.verticalCellIcon";

    /**
     * <p>
     * <strong>[LittleLuck属性]</strong> 进度条间距属性key
     * </p>
     *
     * <p>
     * <strong>[LittLeLuck Attributes]</strong> Vertical ProgressBar thumb
     * insets properties.
     * </p>
     */
    public static final String CELLBAR_INSETS = "ProgressBar.thumbInsets";

    public void uninitialize()
    {
        UIManager.put(HORIZONTALIMG, null);
        UIManager.put(HORIZONTALCELLIMG, null);
        UIManager.put(VERTICALIMG, null);
        UIManager.put(VERTICALCELLIMG, null);
        UIManager.put(CELLBAR_INSETS, null);
    }

    @Override
    protected void installBorder(UIDefaults table)
    {
        UIManager.put(BORDER, getBorderRes(BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        UIManager.put(HORIZONTALIMG, LuckRes.getImage("progress/progressbar.9.png"));

        UIManager.put(HORIZONTALCELLIMG, LuckRes.getImage("progress/progressbar_cell.9.png"));

        UIManager.put(VERTICALIMG, LuckRes.getImage("progress/progressbar_v.9.png"));

        UIManager.put(VERTICALCELLIMG, LuckRes.getImage("progress/progressbar_cell_v.9.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        UIManager.put(HORIZONTALSIZE, getDimensionRes(160, 10));

        UIManager.put(VERTICALSIZE, getDimensionRes(10, 160));

        UIManager.put(CELLBAR_INSETS, new InsetsUIResource(1, 2, 1, 2));
    }
}
