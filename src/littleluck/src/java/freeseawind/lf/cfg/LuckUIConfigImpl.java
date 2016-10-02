package freeseawind.lf.cfg;

import javax.swing.UIDefaults;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import freeseawind.lf.basic.button.LuckButtonUI;
import freeseawind.lf.basic.checkboxmenuitem.LuckCheckBoxMenuItemUI;
import freeseawind.lf.basic.combobox.LuckComboBoxUI;
import freeseawind.lf.basic.filechooser.LuckFileChooserUI;
import freeseawind.lf.basic.internalframe.LuckDesktopIconUI;
import freeseawind.lf.basic.internalframe.LuckInternalFrameUI;
import freeseawind.lf.basic.list.LuckListUI;
import freeseawind.lf.basic.menu.LuckMenuBarUI;
import freeseawind.lf.basic.menu.LuckMenuUI;
import freeseawind.lf.basic.menuitem.LuckMenuItemUI;
import freeseawind.lf.basic.optionpane.LuckOptionPaneUI;
import freeseawind.lf.basic.popupmenu.LuckPopupMenuSeparatorUI;
import freeseawind.lf.basic.popupmenu.LuckPopupMenuUI;
import freeseawind.lf.basic.progress.LuckProgressBarUI;
import freeseawind.lf.basic.radiomenuitem.LuckRadioBtnMenuItemUI;
import freeseawind.lf.basic.rootpane.LuckRootPaneUI;
import freeseawind.lf.basic.scroll.LuckScrollBarUI;
import freeseawind.lf.basic.scroll.LuckScrollPaneUI;
import freeseawind.lf.basic.scroll.LuckViewportUI;
import freeseawind.lf.basic.slider.LuckSliderUI;
import freeseawind.lf.basic.spinner.LuckSpinnerUI;
import freeseawind.lf.basic.splitpane.LuckSplitPaneUI;
import freeseawind.lf.basic.tabbedpane.LuckTabbedPaneUI;
import freeseawind.lf.basic.table.LuckTableHeaderUI;
import freeseawind.lf.basic.table.LuckTableUI;
import freeseawind.lf.basic.text.LuckFormattedTextFieldUI;
import freeseawind.lf.basic.text.LuckPasswordFieldUI;
import freeseawind.lf.basic.text.LuckTexFieldUI;
import freeseawind.lf.basic.toolbar.LuckToolBarUI;
import freeseawind.lf.basic.toolips.LuckToolTipUI;
import freeseawind.lf.basic.tree.LuckTreeUI;
import freeseawind.lf.constant.LuckUIClassKey;

/**
 * LittleLuck LookAndFeel {@code ComponentUI}s bundle class.
 * 
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckUIConfigImpl implements LuckUIConfig
{
    public void initClassDefaults(UIDefaults table)
    {
        table.put(LuckUIClassKey.ROOTPANEUI, LuckRootPaneUI.class.getName());

        table.put(LuckUIClassKey.BUTTONUI, LuckButtonUI.class.getName());

        table.put(LuckUIClassKey.INTERNALFRAMEUI, LuckInternalFrameUI.class.getName());

        table.put(LuckUIClassKey.OPTIONPANEUI, LuckOptionPaneUI.class.getName());

        table.put(LuckUIClassKey.DESKTOPICONUI, LuckDesktopIconUI.class.getName());

        table.put(LuckUIClassKey.TEXTFIELDUI, LuckTexFieldUI.class.getName());

        table.put(LuckUIClassKey.PASSWORDFIELDUI, LuckPasswordFieldUI.class.getName());

        table.put(LuckUIClassKey.FORMATTEDTEXTFIELDUI, LuckFormattedTextFieldUI.class.getName());

        table.put(LuckUIClassKey.COMBOBOXUI, LuckComboBoxUI.class.getName());

        table.put(LuckUIClassKey.POPUPMENUUI, LuckPopupMenuUI.class.getName());

        table.put(LuckUIClassKey.TABBEDPANEUI, LuckTabbedPaneUI.class.getName());

        table.put(LuckUIClassKey.SCROLLPANEUI, LuckScrollPaneUI.class.getName());

        table.put(LuckUIClassKey.SCROLLBARUI, LuckScrollBarUI.class.getName());

        table.put(LuckUIClassKey.VIEWPORTUI, LuckViewportUI.class.getName());

        table.put(LuckUIClassKey.MENUBARUI, LuckMenuBarUI.class.getName());

        table.put(LuckUIClassKey.MENUUI, LuckMenuUI.class.getName());

        table.put(LuckUIClassKey.MENUITEMUI, LuckMenuItemUI.class.getName());

        table.put(LuckUIClassKey.CHECKBOXMENUITEMUI, LuckCheckBoxMenuItemUI.class.getName());

        table.put(LuckUIClassKey.RADIOBUTTONMENUITEMUI, LuckRadioBtnMenuItemUI.class.getName());

        table.put(LuckUIClassKey.TREEUI, LuckTreeUI.class.getName());

        table.put(LuckUIClassKey.LISTUI, LuckListUI.class.getName());

        table.put(LuckUIClassKey.POPUPMENUSEPARATORUI, LuckPopupMenuSeparatorUI.class.getName());

        table.put(LuckUIClassKey.TOOLTIPUI, LuckToolTipUI.class.getName());

        table.put(LuckUIClassKey.SPINNERUI, LuckSpinnerUI.class.getName());

        table.put(LuckUIClassKey.SLIDERUI, LuckSliderUI.class.getName());

        table.put(LuckUIClassKey.TABLEHEADERUI, LuckTableHeaderUI.class.getName());

        table.put(LuckUIClassKey.TABLEUI, LuckTableUI.class.getName());

        table.put(LuckUIClassKey.PROGRESSBARUI, LuckProgressBarUI.class.getName());

        table.put(LuckUIClassKey.FILECHOOSERUI, LuckFileChooserUI.class.getName());

        table.put(LuckUIClassKey.TOOLBARUI, LuckToolBarUI.class.getName());

        table.put(LuckUIClassKey.SPLITPANEUI, LuckSplitPaneUI.class.getName());

        table.put(LuckUIClassKey.TOGGLEBUTTONUI, BasicToggleButtonUI.class.getName());
    }
}
