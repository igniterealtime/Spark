package freeseawind.lf.basic.table;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;

/**
 * <p>
 * TableHeaderUI实现类，使用{@link LuckTableCellHeaderRenderer}作为默认单元渲染处理器。
 * </p>
 *
 * <p>
 * TableHeaderUI implement class, use {@link LuckTableCellHeaderRenderer} as
 * default renderer.
 * </p>
 *
 * <p>
 * See Also: {@link LuckTableCellHeaderRenderer}
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckTableHeaderUI extends BasicTableHeaderUI
{
    private TableCellRenderer tableCellRender;

    public static ComponentUI createUI(JComponent h)
    {
        return new LuckTableHeaderUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);

        // replace default renderer.
        tableCellRender = header.getDefaultRenderer();

        if (tableCellRender instanceof UIResource)
        {
            header.setDefaultRenderer(new LuckTableCellHeaderRenderer());
        }
    }
}
