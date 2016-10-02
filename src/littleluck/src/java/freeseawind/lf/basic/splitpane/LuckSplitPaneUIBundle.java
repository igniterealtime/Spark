package freeseawind.lf.basic.splitpane;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;

import freeseawind.lf.border.LuckLineBorder;
import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>SplitPaneUI资源绑定类</p>
 *
 * <p>SplitPaneUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckSplitPaneUIBundle extends LuckResourceBundle
{
    /**
     * <p>SplitPane边框属性key。</p>
     *
     * <p>SplitPane border properties.</p>
     */
    public static final String BORDER = "SplitPane.border";

    /**
     * <p>SplitPaneDivider边框属性key。</p>
     *
     * <p>SplitPaneDivider border properties.</p>
     */
    public static final String DIVIDERBORDER = "SplitPaneDivider.border";

    /**
     * <p>SplitPane背景颜色属性key。</p>
     *
     * <p>SplitPane background color properties.</p>
     */
    public static final String BACKGROUND = "SplitPane.background";

    /**
     * <p>SplitPane分隔区域大小属性key, 当前默认值5。</p>
     *
     * <p>SplitPane divider size properties, default size 5.</p>
     */
    public static final String DIVIDERSIZE = "SplitPane.dividerSize";

    @Override
    protected void installBorder(UIDefaults table)
    {
        table.put(BORDER, getBorderRes(BorderFactory.createEmptyBorder()));

        table.put(DIVIDERBORDER, getBorderRes(new LuckLineBorder(new Insets(1, 1, 1, 1), 2)));

        table.put(BACKGROUND, getColorRes(Color.white));

        table.put(DIVIDERSIZE, 5);
    }
}
