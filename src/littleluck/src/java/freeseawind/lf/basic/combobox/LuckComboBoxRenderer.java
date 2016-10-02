package freeseawind.lf.basic.combobox;

import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * <pre>
 * ComboBoxRenderer实现类， 使用自定义边框改变内容显示间距。
 *
 * The ComboBoxRenderer implementation class uses a custom border to change
 * the content display spacing.
 * </pre>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckComboBoxRenderer extends BasicComboBoxRenderer
{
    private static final long serialVersionUID = 3856963267590149277L;

    public LuckComboBoxRenderer()
    {
        super();

        // 设置内边框, 用来控制内容之间的显示间距
        // Set the inner border, used to control the display spacing
        // between the content.
        setBorder(UIManager.getBorder(LuckComboBoxUIBundle.RENDERERBORDER));
    }
}
