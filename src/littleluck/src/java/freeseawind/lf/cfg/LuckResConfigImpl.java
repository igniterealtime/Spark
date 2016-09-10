package freeseawind.lf.cfg;

import freeseawind.lf.basic.button.LuckButtonUIBundle;
import freeseawind.lf.basic.checkboxmenuitem.LuckCheckboxMenuItemUIBundle;
import freeseawind.lf.basic.combobox.LuckComboBoxUIBundle;
import freeseawind.lf.basic.filechooser.LuckFileChooserUIBundle;
import freeseawind.lf.basic.internalframe.LuckInternalFrameUIBundle;
import freeseawind.lf.basic.list.LuckListUIBundle;
import freeseawind.lf.basic.menu.LuckMenuUIBundle;
import freeseawind.lf.basic.menuitem.LuckMenuItemUIBundle;
import freeseawind.lf.basic.optionpane.LuckOptionPaneUIBundle;
import freeseawind.lf.basic.popupmenu.LuckPopupMenuUIBundle;
import freeseawind.lf.basic.progress.LuckProgressBarUIBundle;
import freeseawind.lf.basic.radiomenuitem.LuckRadioBtnMenuItemUIBundle;
import freeseawind.lf.basic.rootpane.LuckRootPaneUIBundle;
import freeseawind.lf.basic.scroll.LuckScrollUIBundle;
import freeseawind.lf.basic.slider.LuckSliderUIBundle;
import freeseawind.lf.basic.spinner.LuckSpinnerUIBundle;
import freeseawind.lf.basic.splitpane.LuckSplitPaneUIBundle;
import freeseawind.lf.basic.tabbedpane.LuckTabbedPaneUIBundle;
import freeseawind.lf.basic.table.LuckTableUIBundle;
import freeseawind.lf.basic.text.LuckTextUIBundle;
import freeseawind.lf.basic.togglebutton.LuckToggleButtonUIBundle;
import freeseawind.lf.basic.toolips.LuckToolipUIBundle;
import freeseawind.lf.basic.tree.LuckTreeUIBundle;

/**
 * 工厂类
 *
 * @author freeseawind@github
 *
 */
public class LuckResConfigImpl implements LuckResConfig
{
    public void loadResources()
    {
        getGlobalBundl().installDefaults();

        getRootPanelUIBundle().installDefaults();

        getInternalFrameUIBundle().installDefaults();

        getOptionPanelUIBundle().installDefaults();

        getButtonUIBundle().installDefaults();

        getToggleButtonUIBundle().installDefaults();

        getTextUIBundle().installDefaults();

        getComboboxUIBundle().installDefaults();

        getMenuUIBundle().installDefaults();

        getMenItemUIBundle().installDefaults();

        getCheckboxMenItemUIBundle().installDefaults();

        getRadioBtnMenItemUIBundle().installDefaults();

        getPopupMenuUIBundle().installDefaults();

        getTabbedPaneUIBundle().installDefaults();

        getScrollUIBundle().installDefaults();

        getTreeUIBundle().installDefaults();

        getListUIBundle().installDefaults();

        getToolipUIBundle().installDefaults();

        getSpinnerUIBundle().installDefaults();

        getSliderUIBundle().installDefaults();

        getTableUIBundle().installDefaults();

        getProgressBarUIBundle().installDefaults();

        getFileChooserUIBundle().installDefaults();
        
        getSplitPaneUIBundle().installDefaults();
    }

    /**
     * @return 全局资源配置信息
     */
    protected LuckResourceBundle getGlobalBundl()
    {
        return new LuckGlobalBundle();
    }

    /**
     *
     * @return RootPanelUI资源绑定信息
     */
    protected LuckResourceBundle getRootPanelUIBundle()
    {
        return new LuckRootPaneUIBundle();
    }

    /**
     *
     * @return InternalFrameUI资源绑定信息
     */
    protected LuckResourceBundle getInternalFrameUIBundle()
    {
        return new LuckInternalFrameUIBundle();
    }

    /**
     * @return OptionPanelUI资源绑定信息
     */
    protected LuckResourceBundle getOptionPanelUIBundle()
    {
        return new LuckOptionPaneUIBundle();
    }

    /**
     * @return ButtonUI资源绑定信息
     */
    protected LuckResourceBundle getButtonUIBundle()
    {
        return new LuckButtonUIBundle();
    }

    /**
     * @return ToggleButtonUI资源绑定信息
     */
    protected LuckResourceBundle getToggleButtonUIBundle()
    {
        return new LuckToggleButtonUIBundle();
    }

    /**
     * @return 文本控件UI资源绑定信息
     */
    protected LuckResourceBundle getTextUIBundle()
    {
        return new LuckTextUIBundle();
    }

    /**
     *
     * @return 下拉列表UI资源绑定信息
     */
    protected LuckResourceBundle getComboboxUIBundle()
    {
        return new LuckComboBoxUIBundle();
    }

    /**
     *
     * @return PopupMenuUI资源绑定信息
     */
    protected LuckResourceBundle getPopupMenuUIBundle()
    {
        return new LuckPopupMenuUIBundle();
    }

    /**
     *
     * @return TabbedPaneUI资源绑定信息
     */
    protected LuckResourceBundle getTabbedPaneUIBundle()
    {
        return new LuckTabbedPaneUIBundle();
    }

    /**
     *
     * @return MenuUI资源绑定信息
     */
    protected LuckResourceBundle getMenuUIBundle()
    {
        return new LuckMenuUIBundle();
    }

    /**
     * 
     * @return MenItemUI资源绑定信息
     */
    protected LuckResourceBundle getMenItemUIBundle()
    {
        return new LuckMenuItemUIBundle();
    }

    /**
     * 
     * @return CheckboxMenItemUI资源绑定信息
     */
    protected LuckResourceBundle getCheckboxMenItemUIBundle()
    {
        return new LuckCheckboxMenuItemUIBundle();
    }

    /**
     * 
     * @return RadioBtnMenItemUI资源绑定信息
     */
    protected LuckResourceBundle getRadioBtnMenItemUIBundle()
    {
        return new LuckRadioBtnMenuItemUIBundle();
    }

    /**
     *
     * @return ScrollUI资源绑定信息
     */
    protected LuckResourceBundle getScrollUIBundle()
    {
        return new LuckScrollUIBundle();
    }

    /**
     *
     * @return TreeUI资源绑定信息
     */
    protected LuckResourceBundle getTreeUIBundle()
    {
        return new LuckTreeUIBundle();
    }

    /**
     *
     * @return ListUI资源绑定信息
     */
    protected LuckResourceBundle getListUIBundle()
    {
        return new LuckListUIBundle();
    }

    /**
     *
     * @return ToolipUI资源绑定信息
     */
    protected LuckResourceBundle getToolipUIBundle()
    {
        return new LuckToolipUIBundle();
    }

    /**
     *
     * @return SpinnerUI资源绑定信息
     */
    protected LuckResourceBundle getSpinnerUIBundle()
    {
        return new LuckSpinnerUIBundle();
    }

    /**
     *
     * @return SliderUI资源绑定信息
     */
    protected LuckResourceBundle getSliderUIBundle()
    {
        return new LuckSliderUIBundle();
    }

    /**
     *
     * @return TableUI资源绑定信息
     */
    protected LuckResourceBundle getTableUIBundle()
    {
        return new LuckTableUIBundle();
    }

    /**
     *
     * @return ProgressBarUI资源绑定信息
     */
    protected LuckResourceBundle getProgressBarUIBundle()
    {
        return new LuckProgressBarUIBundle();
    }

    /**
     * 
     * @return FileChooserUI资源绑定信息
     */
    protected LuckResourceBundle getFileChooserUIBundle()
    {
        return new LuckFileChooserUIBundle();
    }
    
    /**
     * 
     * @return SplitPaneUI资源绑定信息
     */
    protected LuckResourceBundle getSplitPaneUIBundle()
    {
        return new LuckSplitPaneUIBundle();
    }
}
