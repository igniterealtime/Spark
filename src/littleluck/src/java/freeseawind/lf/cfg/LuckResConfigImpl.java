package freeseawind.lf.cfg;

import javax.swing.UIDefaults;

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
 * A <code>LuckResConfig</code> implement class.
 *
 * @see LuckResConfig
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckResConfigImpl implements LuckResConfig
{
    public void loadResources(UIDefaults table)
    {
        getGlobalBundl().installDefaults(table);

        getRootPanelUIBundle().installDefaults(table);

        getInternalFrameUIBundle().installDefaults(table);

        getOptionPanelUIBundle().installDefaults(table);

        getButtonUIBundle().installDefaults(table);

        getToggleButtonUIBundle().installDefaults(table);

        getTextUIBundle().installDefaults(table);

        getComboboxUIBundle().installDefaults(table);

        getMenuUIBundle().installDefaults(table);

        getMenItemUIBundle().installDefaults(table);

        getCheckboxMenItemUIBundle().installDefaults(table);

        getRadioBtnMenItemUIBundle().installDefaults(table);

        getPopupMenuUIBundle().installDefaults(table);

        getTabbedPaneUIBundle().installDefaults(table);

        getScrollUIBundle().installDefaults(table);

        getTreeUIBundle().installDefaults(table);

        getListUIBundle().installDefaults(table);

        getToolipUIBundle().installDefaults(table);

        getSpinnerUIBundle().installDefaults(table);

        getSliderUIBundle().installDefaults(table);

        getTableUIBundle().installDefaults(table);

        getProgressBarUIBundle().installDefaults(table);

        getFileChooserUIBundle().installDefaults(table);
        
        getSplitPaneUIBundle().installDefaults(table);
    }
    
    public void removeResource()
    {
        getGlobalBundl().uninitialize();

        getRootPanelUIBundle().uninitialize();

        getInternalFrameUIBundle().uninitialize();

        getOptionPanelUIBundle().uninitialize();

        getButtonUIBundle().uninitialize();

        getToggleButtonUIBundle().uninitialize();

        getTextUIBundle().uninitialize();

        getComboboxUIBundle().uninitialize();

        getMenuUIBundle().uninitialize();

        getMenItemUIBundle().uninitialize();

        getCheckboxMenItemUIBundle().uninitialize();

        getRadioBtnMenItemUIBundle().uninitialize();

        getPopupMenuUIBundle().uninitialize();

        getTabbedPaneUIBundle().uninitialize();

        getScrollUIBundle().uninitialize();

        getTreeUIBundle().uninitialize();

        getListUIBundle().uninitialize();

        getToolipUIBundle().uninitialize();

        getSpinnerUIBundle().uninitialize();

        getSliderUIBundle().uninitialize();

        getTableUIBundle().uninitialize();

        getProgressBarUIBundle().uninitialize();

        getFileChooserUIBundle().uninitialize();
        
        getSplitPaneUIBundle().uninitialize();
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
     * @return RootPanelUI resource bundle object.
     */
    protected LuckResourceBundle getRootPanelUIBundle()
    {
        return new LuckRootPaneUIBundle();
    }

    /**
     *
     * @return InternalFrameUI resource bundle object.
     */
    protected LuckResourceBundle getInternalFrameUIBundle()
    {
        return new LuckInternalFrameUIBundle();
    }

    /**
     * @return OptionPanelUI resource bundle object.
     */
    protected LuckResourceBundle getOptionPanelUIBundle()
    {
        return new LuckOptionPaneUIBundle();
    }

    /**
     * @return ButtonUI resource bundle object.
     */
    protected LuckResourceBundle getButtonUIBundle()
    {
        return new LuckButtonUIBundle();
    }

    /**
     * @return ToggleButtonUI resource bundle object.
     */
    protected LuckResourceBundle getToggleButtonUIBundle()
    {
        return new LuckToggleButtonUIBundle();
    }

    /**
     * @return 文本控件UI resource bundle object.
     */
    protected LuckResourceBundle getTextUIBundle()
    {
        return new LuckTextUIBundle();
    }

    /**
     *
     * @return 下拉列表UI resource bundle object.
     */
    protected LuckResourceBundle getComboboxUIBundle()
    {
        return new LuckComboBoxUIBundle();
    }

    /**
     *
     * @return PopupMenuUI resource bundle object.
     */
    protected LuckResourceBundle getPopupMenuUIBundle()
    {
        return new LuckPopupMenuUIBundle();
    }

    /**
     *
     * @return TabbedPaneUI resource bundle object.
     */
    protected LuckResourceBundle getTabbedPaneUIBundle()
    {
        return new LuckTabbedPaneUIBundle();
    }

    /**
     *
     * @return MenuUI resource bundle object.
     */
    protected LuckResourceBundle getMenuUIBundle()
    {
        return new LuckMenuUIBundle();
    }

    /**
     * 
     * @return MenItemUI resource bundle object.
     */
    protected LuckResourceBundle getMenItemUIBundle()
    {
        return new LuckMenuItemUIBundle();
    }

    /**
     * 
     * @return CheckboxMenItemUI resource bundle object.
     */
    protected LuckResourceBundle getCheckboxMenItemUIBundle()
    {
        return new LuckCheckboxMenuItemUIBundle();
    }

    /**
     * 
     * @return RadioBtnMenItemUI resource bundle object.
     */
    protected LuckResourceBundle getRadioBtnMenItemUIBundle()
    {
        return new LuckRadioBtnMenuItemUIBundle();
    }

    /**
     *
     * @return ScrollUI resource bundle object.
     */
    protected LuckResourceBundle getScrollUIBundle()
    {
        return new LuckScrollUIBundle();
    }

    /**
     *
     * @return TreeUI resource bundle object.
     */
    protected LuckResourceBundle getTreeUIBundle()
    {
        return new LuckTreeUIBundle();
    }

    /**
     *
     * @return ListUI resource bundle object.
     */
    protected LuckResourceBundle getListUIBundle()
    {
        return new LuckListUIBundle();
    }

    /**
     *
     * @return ToolipUI resource bundle object.
     */
    protected LuckResourceBundle getToolipUIBundle()
    {
        return new LuckToolipUIBundle();
    }

    /**
     *
     * @return SpinnerUI resource bundle object.
     */
    protected LuckResourceBundle getSpinnerUIBundle()
    {
        return new LuckSpinnerUIBundle();
    }

    /**
     *
     * @return SliderUI resource bundle object.
     */
    protected LuckResourceBundle getSliderUIBundle()
    {
        return new LuckSliderUIBundle();
    }

    /**
     *
     * @return TableUI resource bundle object.
     */
    protected LuckResourceBundle getTableUIBundle()
    {
        return new LuckTableUIBundle();
    }

    /**
     *
     * @return ProgressBarUI resource bundle object.
     */
    protected LuckResourceBundle getProgressBarUIBundle()
    {
        return new LuckProgressBarUIBundle();
    }

    /**
     * 
     * @return FileChooserUI resource bundle object.
     */
    protected LuckResourceBundle getFileChooserUIBundle()
    {
        return new LuckFileChooserUIBundle();
    }
    
    /**
     * 
     * @return SplitPaneUI resource bundle object.
     */
    protected LuckResourceBundle getSplitPaneUIBundle()
    {
        return new LuckSplitPaneUIBundle();
    }
}
