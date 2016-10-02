package freeseawind.lf.basic.list;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.border.Border;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>ListUI 资源绑定类。</p>
 *
 * <p>A ListUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckListUIBundle extends LuckResourceBundle
{
    /**
     * <p>List单元格边框属性key</p>
     *
     * <p>List border properties.</p>
     */
    public static final String CELL_BORDER = "List.focusSelectedCellHighlightBorder";

    /**
     * <p>List单元格无焦点边框属性key</p>
     *
     * <p>List cell no focus border properties.</p>
     */
    public static final String CELL_NOFOCUSBORDER = "List.cellNoFocusBorder";

    /**
     * <p>List选中背景颜色key</p>
     *
     * <p>List background color properties when selected.</p>
     */
    public static final String SELECTIONBACKGROUND = "List.selectionBackground";

    /**
     * <p>List选中字体颜色key</p>
     *
     * <p>List font color properties when selected.</p>
     */
    public static final String SELECTIONFOREGROUND = "List.selectionForeground";

    /**
     * <p>List背景颜色key</p>
     *
     * <p>List background color properties.</p>
     */
    public static final String BACKGROUND = "List.background";

    @Override
    protected void installBorder(UIDefaults table)
    {
        Border cellBorder = BorderFactory.createEmptyBorder(5, 5, 5, 0);
        
        table.put(CELL_BORDER, getBorderRes(cellBorder));
        
        Border nofocusBorder = BorderFactory.createEmptyBorder(5, 5, 5, 0);

        table.put(CELL_NOFOCUSBORDER, getBorderRes(nofocusBorder));
    }

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(SELECTIONBACKGROUND, getColorRes(60, 175, 210));

        table.put(SELECTIONFOREGROUND, getColorRes(Color.WHITE));

        table.put(BACKGROUND, getColorRes(Color.white));
    }
}
