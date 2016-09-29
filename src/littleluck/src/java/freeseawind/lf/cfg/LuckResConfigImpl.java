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
 *
 * @author freeseawind@github
 * @version 1.0
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
     * @return global resource bundle.
     */
    protected LuckResourceBundle getGlobalBundl()
    {
        return new LuckGlobalBundle();
    }

    /**
     *
     * @return RootPanelUI resource bundle.
     */
    protected LuckResourceBundle getRootPanelUIBundle()
    {
        return new LuckRootPaneUIBundle();
    }

    /**
     *
     * @return InternalFrameUI resource bundle.
     */
    protected LuckResourceBundle getInternalFrameUIBundle()
    {
        return new LuckInternalFrameUIBundle();
    }

    /**
     * @return OptionPanelUI resource bundle.
     */
    protected LuckResourceBundle getOptionPanelUIBundle()
    {
        return new LuckOptionPaneUIBundle();
    }

    /**
     * @return ButtonUI resource bundle.
     */
    protected LuckResourceBundle getButtonUIBundle()
    {
        return new LuckButtonUIBundle();
    }

    /**
     * @return ToggleButtonUI resource bundle.
     */
    protected LuckResourceBundle getToggleButtonUIBundle()
    {
        return new LuckToggleButtonUIBundle();
    }

    /**
     * @return TextUI resource bundle.
     */
    protected LuckResourceBundle getTextUIBundle()
    {
        return new LuckTextUIBundle();
    }

    /**
     *
     * @return ComboBoxUI resource bundle.
     */
    protected LuckResourceBundle getComboboxUIBundle()
    {
        return new LuckComboBoxUIBundle();
    }

    /**
     *
     * @return PopupMenuUI resource bundle.
     */
    protected LuckResourceBundle getPopupMenuUIBundle()
    {
        return new LuckPopupMenuUIBundle();
    }

    /**
     *
     * @return TabbedPaneUI resource bundle.
     */
    protected LuckResourceBundle getTabbedPaneUIBundle()
    {
        return new LuckTabbedPaneUIBundle();
    }

    /**
     *
     * @return MenuUI resource bundle.
     */
    protected LuckResourceBundle getMenuUIBundle()
    {
        return new LuckMenuUIBundle();
    }

    /**
     * 
     * @return MenItemUI resource bundle.
     */
    protected LuckResourceBundle getMenItemUIBundle()
    {
        return new LuckMenuItemUIBundle();
    }

    /**
     * 
     * @return CheckboxMenItemUI resource bundle.
     */
    protected LuckResourceBundle getCheckboxMenItemUIBundle()
    {
        return new LuckCheckboxMenuItemUIBundle();
    }

    /**
     * 
     * @return RadioBtnMenItemUI resource bundle.
     */
    protected LuckResourceBundle getRadioBtnMenItemUIBundle()
    {
        return new LuckRadioBtnMenuItemUIBundle();
    }

    /**
     *
     * @return ScrollUI resource bundle.
     */
    protected LuckResourceBundle getScrollUIBundle()
    {
        return new LuckScrollUIBundle();
    }

    /**
     *
     * @return TreeUI resource bundle.
     */
    protected LuckResourceBundle getTreeUIBundle()
    {
        return new LuckTreeUIBundle();
    }

    /**
     *
     * @return ListUI resource bundle.
     */
    protected LuckResourceBundle getListUIBundle()
    {
        return new LuckListUIBundle();
    }

    /**
     *
     * @return ToolipUI resource bundle.
     */
    protected LuckResourceBundle getToolipUIBundle()
    {
        return new LuckToolipUIBundle();
    }

    /**
     *
     * @return SpinnerUI resource bundle.
     */
    protected LuckResourceBundle getSpinnerUIBundle()
    {
        return new LuckSpinnerUIBundle();
    }

    /**
     *
     * @return SliderUI resource bundle.
     */
    protected LuckResourceBundle getSliderUIBundle()
    {
        return new LuckSliderUIBundle();
    }

    /**
     *
     * @return TableUI resource bundle.
     */
    protected LuckResourceBundle getTableUIBundle()
    {
        return new LuckTableUIBundle();
    }

    /**
     *
     * @return ProgressBarUI resource bundle.
     */
    protected LuckResourceBundle getProgressBarUIBundle()
    {
        return new LuckProgressBarUIBundle();
    }

    /**
     * 
     * @return FileChooserUI resource bundle.
     */
    protected LuckResourceBundle getFileChooserUIBundle()
    {
        return new LuckFileChooserUIBundle();
    }
    
    /**
     * 
     * @return SplitPaneUI resource bundle.
     */
    protected LuckResourceBundle getSplitPaneUIBundle()
    {
        return new LuckSplitPaneUIBundle();
    }
}
