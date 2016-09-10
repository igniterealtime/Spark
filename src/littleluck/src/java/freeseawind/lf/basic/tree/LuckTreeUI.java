package freeseawind.lf.basic.tree;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;

/**
 * TreeUI实现类,设置组件为不完全透明
 * <p>
 * 另请参见 {@link LuckTreeUIBundle}, {@link LuckTreeCellRenderer}
 * </p>
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTreeUI extends BasicTreeUI
{
    public static ComponentUI createUI(JComponent x)
    {
        return new LuckTreeUI();
    }

    protected void installDefaults()
    {
        super.installDefaults();

        LookAndFeel.installProperty(tree, "opaque", Boolean.FALSE);
    }
    
    /**
     * 使用自定义TreeCellRenderer， 去除焦点边框绘制
     */
    protected TreeCellRenderer createDefaultCellRenderer()
    {
        return new LuckTreeCellRenderer();
    }
}
