package freeseawind.lf.basic.combobox;

import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * 使用自定义边框改变内容显示间距
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

        // 设置内边框, 用来控制字体显示间距
        setBorder(UIManager.getBorder(LuckComboBoxUIBundle.RENDERERBORDER));
    }
}
