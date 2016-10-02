package freeseawind.lf.basic.tree;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;

/**
 * <pre>
 * TreeUI实现类，在{@link BasicTreeUI}基础上做了如下改变：
 * <li>设置组件为不完全透明</li>
 * <li>使用{@link LuckTreeCellRenderer}替换原有TreeCellRenderer实现</li>
 * ------------------------------------------------------------------------------------------
 * Tree View UI implementation class, based on {@link BasicTreeUI} made the following changes:
 * <li>Set components are not fully transparent</li>
 * <li>Replace the original TreeCellRenderer implementation with {@link LuckTreeCellRenderer}</li>
 * </pre>
 *
 * @see LuckTreeUIBundle
 * @see LuckTreeCellRenderer
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
     * <P>使用自定义TreeCellRenderer， 去除焦点边框绘制。</p>
     *
     * <P>Use custom TreeCellRenderer, removes the focus border drawing.</P>
     */
    protected TreeCellRenderer createDefaultCellRenderer()
    {
        return new LuckTreeCellRenderer();
    }
}
