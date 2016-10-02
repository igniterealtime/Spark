package freeseawind.lf.basic.list;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

/**
 * <p>ListUI实现类， LuckListUI没有作代码改动， 通过配置的方式进行扩展。</p>
 *
 * <p>A ListUI implement class.</p>
 *
 * @see LuckListUIBundle
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckListUI extends BasicListUI
{
    public static ComponentUI createUI(JComponent list)
    {
        return new LuckListUI();
    }
}
