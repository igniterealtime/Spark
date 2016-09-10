package freeseawind.lf.basic.splitpane;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.UIManager;

import freeseawind.lf.border.LuckLineBorder;
import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * SplitPaneUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckSplitPaneUIBundle extends LuckResourceBundle
{
    public static final String BORDER = "SplitPane.border";

    public static final String DIVIDERBORDER = "SplitPaneDivider.border";

    public static final String BACKGROUND = "SplitPane.background";

    public static final String DIVIDERSIZE = "SplitPane.dividerSize";

    @Override
    protected void installBorder()
    {
        UIManager.put(BORDER, BorderFactory.createEmptyBorder());

        UIManager.put(DIVIDERBORDER, new LuckLineBorder(new Insets(1, 1, 1, 1), 2));

        UIManager.put(BACKGROUND, Color.white);

        UIManager.put(DIVIDERSIZE, 5);
    }
}
