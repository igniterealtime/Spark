package freeseawind.lf.basic.optionpane;

import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * OptionPaneUI资源绑定类
 * 
 * @author freeseawind@github
 * @version 1.0
 * 
 */
public class LuckOptionPaneUIBundle extends LuckResourceBundle
{
    /**
     * 问题图标属性key
     */
    public static final String QUESTION_ICON = "OptionPane.questionIcon";
    
    /**
     * 警告图标属性key
     */
    public static final String WARNING_ICON = "OptionPane.warningIcon";
    
    /**
     * 信息图标属性key
     */
    public static final String INFORMATION_ICON = "OptionPane.informationIcon";
    
    /**
     * 错误图标属性key
     */
    public static final String ERROR_ICON = "OptionPane.errorIcon";

    @Override
    protected void loadImages()
    {
        UIManager.put(QUESTION_ICON, getIconRes("optionpane/question.png"));
        UIManager.put(WARNING_ICON, getIconRes("optionpane/warn.png"));
        UIManager.put(INFORMATION_ICON, getIconRes("optionpane/info.png"));
        UIManager.put(ERROR_ICON, getIconRes("optionpane/error.png"));
    }
}
