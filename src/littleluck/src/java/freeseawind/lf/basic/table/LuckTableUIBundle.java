package freeseawind.lf.basic.table;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.UIDefaults;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * <p>TableUI and TableHeaderUI 资源绑定类。</p>
 *
 * <p>TableUI and TableHeaderUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTableUIBundle extends LuckResourceBundle
{
    /**
     * <p>Table选中时背景颜色属性。</p>
     *
     * <p>Table background color properties when selected.</p>
     */
    public static final String SELECTIONBACKGROUND = "Table.selectionBackground";

    /**
     * <p>Table选中时字体颜色属性。</p>
     *
     * <p>Table font color properties when selected.</p>
     */
    public static final String SELECTIONFOREGROUND = "Table.selectionForeground";

    /**
     * <p>Table升序箭头图标属性。</p>
     *
     * <p>Table ascending arrow icon property.</p>
     */
    public static final String ASC_ICON = "Table.ascendingSortIcon";

    /**
     * <p>Table降序箭头图标属性。</p>
     *
     * <p>Table descending arrow icon property.</p>
     */
    public static final String DESC_ICON = "Table.descendingSortIcon";

    @Override
    protected void installColor(UIDefaults table)
    {
        table.put(SELECTIONBACKGROUND, getColorRes(60, 175, 210));

        table.put(SELECTIONFOREGROUND, getColorRes(Color.white));
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        ImageIcon ascIcon = new ImageIcon(LuckRes.getImage("table/asc.png"));

        table.put(ASC_ICON, getIconRes(ascIcon));

        ImageIcon descIcon = new ImageIcon(LuckRes.getImage("table/desc.png"));

        table.put(DESC_ICON, getIconRes(descIcon));
    }
}
