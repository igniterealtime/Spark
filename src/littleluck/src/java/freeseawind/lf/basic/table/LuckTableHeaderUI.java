package freeseawind.lf.basic.table;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;

/**
 * TableHeaderUI实现类
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
        
        tableCellRender = header.getDefaultRenderer();
        
        if (tableCellRender instanceof UIResource) 
        {
            header.setDefaultRenderer(new LuckTableCellHeaderRenderer());
        }
    }

    public void paint(Graphics g, JComponent c)
    {
        super.paint(g, c);
    }
}
