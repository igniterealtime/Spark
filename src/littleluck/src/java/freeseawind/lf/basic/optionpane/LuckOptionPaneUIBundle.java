package freeseawind.lf.basic.optionpane;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>OptionPaneUI资源绑定类。</p>
 *
 * <p>A OptionPaneUI resource bundle class.</p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckOptionPaneUIBundle extends LuckResourceBundle
{
    /**
     * <p>问题图标属性key</p>
     *
     * <p>OptionPane question Icon properties.</p>
     */
    public static final String QUESTION_ICON = "OptionPane.questionIcon";

    /**
     * <p>警告图标属性key</p>
     *
     * <p>OptionPane warning Icon properties.</p>
     */
    public static final String WARNING_ICON = "OptionPane.warningIcon";

    /**
     * <p>信息图标属性key</p>
     *
     * <p>OptionPane information Icon properties.</p>
     */
    public static final String INFORMATION_ICON = "OptionPane.informationIcon";

    /**
     * <p>错误图标属性key</p>
     *
     * <p>OptionPane error Icon properties.</p>
     */
    public static final String ERROR_ICON = "OptionPane.errorIcon";

    public void uninitialize()
    {
        UIManager.put(QUESTION_ICON, null);
        UIManager.put(WARNING_ICON, null);
        UIManager.put(INFORMATION_ICON, null);
        UIManager.put(ERROR_ICON, null);
    }

    @Override
    protected void loadImages(UIDefaults table)
    {
        UIManager.put(QUESTION_ICON, getIconRes("optionpane/question.png"));
        UIManager.put(WARNING_ICON, getIconRes("optionpane/warn.png"));
        UIManager.put(INFORMATION_ICON, getIconRes("optionpane/info.png"));
        UIManager.put(ERROR_ICON, getIconRes("optionpane/error.png"));
    }
}
