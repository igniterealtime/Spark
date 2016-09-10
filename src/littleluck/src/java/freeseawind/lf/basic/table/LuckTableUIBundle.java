package freeseawind.lf.basic.table;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;

import freeseawind.lf.cfg.LuckResourceBundle;
import freeseawind.lf.utils.LuckRes;

/**
 * TableUI and TableHeaderUI 资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTableUIBundle extends LuckResourceBundle
{
    public static final String SELECTIONBACKGROUND = "Table.selectionBackground";

    public static final String SELECTIONFOREGROUND = "Table.selectionForeground";

    public static final String ASC_ICON = "Table.ascendingSortIcon";

    public static final String DESC_ICON = "Table.descendingSortIcon";

    @Override
    protected void installColor()
    {
        UIManager.put(SELECTIONBACKGROUND, new Color(60, 175, 210));

        UIManager.put(SELECTIONFOREGROUND, Color.white);
    }

    @Override
    protected void loadImages()
    {
        ImageIcon ascIcon = new ImageIcon(LuckRes.getImage("table/asc.png"));

        UIManager.put(ASC_ICON, new IconUIResource(ascIcon));

        ImageIcon descIcon = new ImageIcon(LuckRes.getImage("table/desc.png"));

        UIManager.put(DESC_ICON, new IconUIResource(descIcon));
    }
}
