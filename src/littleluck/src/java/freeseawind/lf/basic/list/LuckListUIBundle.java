package freeseawind.lf.basic.list;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * ListUI 资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckListUIBundle extends LuckResourceBundle
{
    /**
     * List单元格边框属性key
     */
    public static final String CELL_BORDER = "List.focusSelectedCellHighlightBorder";

    /**
     * List单元格无焦点边框属性key
     */
    public static final String CELL_NOFOCUSBORDER = "List.cellNoFocusBorder";

    /**
     * List选中背景颜色key
     */
    public static final String SELECTIONBACKGROUND = "List.selectionBackground";

    /**
     * List选中字体颜色key
     */
    public static final String SELECTIONFOREGROUND = "List.selectionForeground";

    /**
     * List背景颜色key
     */
    public static final String BACKGROUND = "List.background";

    @Override
    protected void installBorder()
    {
        UIManager.put(CELL_BORDER, BorderFactory.createEmptyBorder(5, 5, 5, 0));

        UIManager.put(CELL_NOFOCUSBORDER, new BorderUIResource(
                BorderFactory.createEmptyBorder(5, 5, 5, 0)));
    }

    @Override
    protected void installColor()
    {
        UIManager.put(SELECTIONBACKGROUND, new Color(60, 175, 210));

        UIManager.put(SELECTIONFOREGROUND, Color.WHITE);

        UIManager.put(BACKGROUND, Color.white);
    }
}
