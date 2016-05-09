/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/

package com.jtattoo.plaf;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * @author Michael Hagen
 */
abstract public class AbstractLookAndFeel extends MetalLookAndFeel {

    // Workaround to avoid a bug in the java 1.3 VM
    static {
        try {
            if (JTattooUtilities.getJavaVersion() < 1.4) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
        } catch (Exception ex) {
        }
    }

    private static AbstractTheme myTheme = null;

    abstract public AbstractBorderFactory getBorderFactory();

    abstract public AbstractIconFactory getIconFactory();

    protected void initSystemColorDefaults(UIDefaults table) {
        Object[] systemColors = {
            "desktop", getDesktopColor(), // Color of the desktop background

            "activeCaption", getWindowTitleBackgroundColor(), // Color for captions (title bars) when they are active.
            "activeCaptionLight", getWindowTitleColorLight(),
            "activeCaptionDark", getWindowTitleColorDark(),
            "activeCaptionText", getWindowTitleForegroundColor(), // Text color for text in captions (title bars).
            "activeCaptionBorder", getWindowBorderColor(), // Border color for caption (title bar) window borders.

            "inactiveCaption", getWindowInactiveTitleBackgroundColor(), // Color for captions (title bars) when not active.
            "inactiveCaptionLight", getWindowInactiveTitleColorLight(), //
            "inactiveCaptionDark", getWindowInactiveTitleColorDark(), //
            "inactiveCaptionText", getWindowInactiveTitleForegroundColor(), // Text color for text in inactive captions (title bars).
            "inactiveCaptionBorder", getWindowInactiveBorderColor(), // Border color for inactive caption (title bar) window borders.

            "window", getInputBackgroundColor(), // Default color for the interior of windows, list, tree etc
            "windowBorder", getBackgroundColor(), // ???
            "windowText", getControlForegroundColor(), // ???

            "menu", getMenuBackgroundColor(), // Background color for menus
            "menuText", getMenuForegroundColor(), // Text color for menus
            "MenuBar.rolloverEnabled", Boolean.TRUE,

            "text", getBackgroundColor(), // Text background color
            "textText", getControlForegroundColor(), // Text foreground color
            "textHighlight", getSelectionBackgroundColor(), // Text background color when selected
            "textHighlightText", getSelectionForegroundColor(), // Text color when selected
            "textInactiveText", getDisabledForegroundColor(), // Text color when disabled

            "control", getControlBackgroundColor(), // Default color for controls (buttons, sliders, etc)
            "controlText", getControlForegroundColor(), // Default color for text in controls
            "controlHighlight", getControlHighlightColor(), // Specular highlight (opposite of the shadow)
            "controlLtHighlight", getControlHighlightColor(), // Highlight color for controls
            "controlShadow", getControlShadowColor(), // Shadow color for controls
            "controlDkShadow", getControlDarkShadowColor(), // Dark shadow color for controls

            "scrollbar", getControlBackgroundColor(), // Scrollbar background (usually the "track")
            "info", getTooltipBackgroundColor(), // ToolTip Background
            "infoText", getTooltipForegroundColor() // ToolTip Text
        };

        for (int i = 0; i < systemColors.length; i += 2) {
            table.put((String) systemColors[i], systemColors[i + 1]);
        }
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        BaseBorders.initDefaults();
        BaseIcons.initDefaults();

        Object textFieldBorder = getBorderFactory().getTextFieldBorder();
        Object comboBoxBorder = getBorderFactory().getComboBoxBorder();
        Object scrollPaneBorder = getBorderFactory().getScrollPaneBorder();
        Object tableScrollPaneBorder = getBorderFactory().getTableScrollPaneBorder();
        Object tabbedPaneBorder = getBorderFactory().getTabbedPaneBorder();
        Object buttonBorder = getBorderFactory().getButtonBorder();
        Object toggleButtonBorder = getBorderFactory().getToggleButtonBorder();
        Object titledBorderBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{getFrameColor()});
        Object menuBarBorder = getBorderFactory().getMenuBarBorder();
        Object popupMenuBorder = getBorderFactory().getPopupMenuBorder();
        Object menuItemBorder = getBorderFactory().getMenuItemBorder();
        Object toolBarBorder = getBorderFactory().getToolBarBorder();
        Object progressBarBorder = getBorderFactory().getProgressBarBorder();
        Object toolTipBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{getFrameColor()});
        Object focusCellHighlightBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{getFocusCellColor()});
        Object optionPaneBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        Object optionPaneMessageAreaBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Object optionPaneButtonAreaBorder = BorderFactory.createEmptyBorder(0, 8, 8, 8);
        Object windowBorder = getBorderFactory().getInternalFrameBorder();

        Color c = getBackgroundColor();
        ColorUIResource progressBarBackground = new ColorUIResource(ColorHelper.brighter(c, 20));

        // DEFAULTS TABLE
        Object[] defaults = {
            "controlTextFont", getControlTextFont(),
            "systemTextFont ", getSystemTextFont(),
            "userTextFont", getUserTextFont(),
            "menuTextFont", getMenuTextFont(),
            "windowTitleFont", getWindowTitleFont(),
            "subTextFont", getSubTextFont(),

            "Label.font", getUserTextFont(),
            "Label.background", getBackgroundColor(),
            "Label.foreground", getForegroundColor(),
            "Label.disabledText", getDisabledForegroundColor(),
            "Label.disabledShadow", getWhite(),
            // Text (Note: many are inherited)
            "TextField.border", textFieldBorder,
            "TextField.foreground", getInputForegroundColor(),
            "TextField.background", getInputBackgroundColor(),
            "TextField.disabledForeground", getDisabledForegroundColor(),
            "TextField.disabledBackground", getDisabledBackgroundColor(),
            "TextField.inactiveForeground", getDisabledForegroundColor(),
            "TextField.inactiveBackground", getDisabledBackgroundColor(),
            "TextArea.foreground", getInputForegroundColor(),
            "TextArea.background", getInputBackgroundColor(),
            "TextArea.disabledForeground", getDisabledForegroundColor(),
            "TextArea.disabledBackground", getDisabledBackgroundColor(),
            "TextArea.inactiveForeground", getDisabledForegroundColor(),
            "TextArea.inactiveBackground", getDisabledBackgroundColor(),
            "EditorPane.foreground", getInputForegroundColor(),
            "EditorPane.background", getInputBackgroundColor(),
            "EditorPane.disabledForeground", getDisabledForegroundColor(),
            "EditorPane.disabledBackground", getDisabledBackgroundColor(),
            "EditorPane.inactiveForeground", getDisabledForegroundColor(),
            "EditorPane.inactiveBackground", getDisabledBackgroundColor(),
            "FormattedTextField.border", textFieldBorder,
            "FormattedTextField.foreground", getInputForegroundColor(),
            "FormattedTextField.background", getInputBackgroundColor(),
            "FormattedTextField.disabledForeground", getDisabledForegroundColor(),
            "FormattedTextField.disabledBackground", getDisabledBackgroundColor(),
            "FormattedTextField.inactiveForeground", getDisabledForegroundColor(),
            "FormattedTextField.inactiveBackground", getDisabledBackgroundColor(),
            "PasswordField.border", textFieldBorder,
            "PasswordField.foreground", getInputForegroundColor(),
            "PasswordField.background", getInputBackgroundColor(),
            "PasswordField.disabledForeground", getDisabledForegroundColor(),
            "PasswordField.disabledBackground", getDisabledBackgroundColor(),
            "PasswordField.inactiveForeground", getDisabledForegroundColor(),
            "PasswordField.inactiveBackground", getDisabledBackgroundColor(),
            // Buttons
            "Button.background", getButtonBackgroundColor(),
            "Button.foreground", getButtonForegroundColor(),
            "Button.disabledText", getDisabledForegroundColor(),
            "Button.disabledShadow", getWhite(),
            "Button.select", getSelectionBackgroundColor(),
            "Button.border", buttonBorder,
            "Button.frame", getFrameColor(),
            "Button.focus", getFocusColor(),
            "Button.rolloverColor", getTheme().getRolloverColor(),
            "CheckBox.font", getUserTextFont(),
            "CheckBox.background", getBackgroundColor(),
            "CheckBox.foreground", getForegroundColor(),
            "CheckBox.disabledText", getDisabledForegroundColor(),
            "CheckBox.disabledShadow", getWhite(),
            "Checkbox.select", getSelectionBackgroundColor(),
            "CheckBox.focus", getFocusColor(),
            "CheckBox.icon", getIconFactory().getCheckBoxIcon(),
            "RadioButton.font", getUserTextFont(),
            "RadioButton.background", getBackgroundColor(),
            "RadioButton.foreground", getForegroundColor(),
            "RadioButton.disabledText", getDisabledForegroundColor(),
            "RadioButton.disabledShadow", getWhite(),
            "RadioButton.select", getSelectionBackgroundColor(),
            "RadioButton.icon", getIconFactory().getRadioButtonIcon(),
            "RadioButton.focus", getFocusColor(),
            "ToggleButton.background", getButtonBackgroundColor(),
            "ToggleButton.foreground", getButtonForegroundColor(),
            "ToggleButton.select", getSelectionBackgroundColor(),
            "ToggleButton.text", getButtonForegroundColor(),
            "ToggleButton.disabledText", getDisabledForegroundColor(),
            "ToggleButton.disabledShadow", getWhite(),
            "ToggleButton.disabledSelectedText", getDisabledForegroundColor(),
            "ToggleButton.disabledBackground", getButtonBackgroundColor(),
            "ToggleButton.disabledSelectedBackground", getSelectionBackgroundColor(),
            "ToggleButton.focus", getFocusColor(),
            "ToggleButton.border", toggleButtonBorder,
            // ToolTip
            "ToolTip.border", toolTipBorder,
            "ToolTip.foreground", getTooltipForegroundColor(),
            "ToolTip.background", getTooltipBackgroundColor(),
            // Slider
            "Slider.border", null,
            "Slider.foreground", getFrameColor(),
            "Slider.background", getBackgroundColor(),
            "Slider.focus", getFocusColor(),
            "Slider.focusInsets", new InsetsUIResource(0, 0, 0, 0),
            "Slider.trackWidth", new Integer(7),
            "Slider.majorTickLength", new Integer(6),
            // Progress Bar
            "ProgressBar.border", progressBarBorder,
            "ProgressBar.background", progressBarBackground,
            "ProgressBar.selectionForeground", getSelectionForegroundColor(),
            "ProgressBar.selectionBackground", getForegroundColor(),
            // Combo Box
            "ComboBox.border", comboBoxBorder,
            "ComboBox.background", getInputBackgroundColor(),
            "ComboBox.foreground", getInputForegroundColor(),
            "ComboBox.selectionBackground", getSelectionBackgroundColor(),
            "ComboBox.selectionForeground", getSelectionForegroundColor(),
            "ComboBox.selectionBorderColor", getFocusColor(),
            "ComboBox.disabledBackground", getDisabledBackgroundColor(),
            "ComboBox.disabledForeground", getDisabledForegroundColor(),
            "ComboBox.listBackground", getInputBackgroundColor(),
            "ComboBox.listForeground", getInputForegroundColor(),
            "ComboBox.font", getUserTextFont(),
            // Panel
            "Panel.foreground", getForegroundColor(),
            "Panel.background", getBackgroundColor(),
            "Panel.darkBackground", getTheme().getBackgroundColorDark(),
            "Panel.lightBackground", getTheme().getBackgroundColorLight(),
            "Panel.alterBackground", getTheme().getAlterBackgroundColor(),
            "Panel.font", getUserTextFont(),
            // RootPane
            "RootPane.frameBorder", windowBorder,
            "RootPane.plainDialogBorder", windowBorder,
            "RootPane.informationDialogBorder", windowBorder,
            "RootPane.errorDialogBorder", windowBorder,
            "RootPane.colorChooserDialogBorder", windowBorder,
            "RootPane.fileChooserDialogBorder", windowBorder,
            "RootPane.questionDialogBorder", windowBorder,
            "RootPane.warningDialogBorder", windowBorder,
            // InternalFrame
            "InternalFrame.border", getBorderFactory().getInternalFrameBorder(),
            "InternalFrame.font", getWindowTitleFont(),
            "InternalFrame.paletteBorder", getBorderFactory().getPaletteBorder(),
            "InternalFrame.paletteTitleHeight", new Integer(11),
            "InternalFrame.paletteCloseIcon", getIconFactory().getPaletteCloseIcon(),
            "InternalFrame.icon", getIconFactory().getMenuIcon(),
            "InternalFrame.iconifyIcon", getIconFactory().getIconIcon(),
            "InternalFrame.maximizeIcon", getIconFactory().getMaxIcon(),
            "InternalFrame.minimizeIcon", getIconFactory().getMinIcon(),
            "InternalFrame.closeIcon", getIconFactory().getCloseIcon(),

            // Titled Border
            "TitledBorder.titleColor", getForegroundColor(),
            "TitledBorder.border", titledBorderBorder,
            // List
            "List.focusCellHighlightBorder", focusCellHighlightBorder,
            "List.font", getUserTextFont(),
            "List.foreground", getInputForegroundColor(),
            "List.background", getInputBackgroundColor(),
            "List.selectionForeground", getSelectionForegroundColor(),
            "List.selectionBackground", getSelectionBackgroundColor(),
            "List.disabledForeground", getDisabledForegroundColor(),
            "List.disabledBackground", getDisabledBackgroundColor(),
            // ScrollBar
            "ScrollBar.background", getControlBackgroundColor(),
            "ScrollBar.highlight", getControlHighlightColor(),
            "ScrollBar.shadow", getControlShadowColor(),
            "ScrollBar.darkShadow", getControlDarkShadowColor(),
            "ScrollBar.thumb", getControlBackgroundColor(),
            "ScrollBar.thumbShadow", getControlShadowColor(),
            "ScrollBar.thumbHighlight", getControlHighlightColor(),
            "ScrollBar.width", new Integer(17),
            "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE,
            // ScrollPane
            "ScrollPane.border", scrollPaneBorder,
            "ScrollPane.foreground", getForegroundColor(),
            "ScrollPane.background", getBackgroundColor(),
            // Viewport
            "Viewport.foreground", getForegroundColor(),
            "Viewport.background", getBackgroundColor(),
            "Viewport.font", getUserTextFont(),

            // Tabbed Pane
            "TabbedPane.boder", tabbedPaneBorder,
            "TabbedPane.background", getBackgroundColor(),
            "TabbedPane.tabAreaBackground", getTabAreaBackgroundColor(),
            "TabbedPane.unselectedBackground", getControlColorDark(),
            "TabbedPane.foreground", getControlForegroundColor(),
            "TabbedPane.selected", getBackgroundColor(),
            "TabbedPane.selectedForeground", getTabSelectionForegroundColor(),
            "TabbedPane.tabAreaInsets", new InsetsUIResource(5, 5, 5, 5),
            "TabbedPane.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0),
            "TabbedPane.tabInsets", new InsetsUIResource(1, 6, 1, 6),
            "TabbedPane.focus", getFocusColor(),
            // TabbedPane ScrollButton
            "TabbedPane.selected", getButtonBackgroundColor(),
            "TabbedPane.shadow", new ColorUIResource(180, 180, 180),
            "TabbedPane.darkShadow", new ColorUIResource(120, 120, 120),
            "TabbedPane.highlight", new ColorUIResource(Color.white),
            // Tab Colors in Netbeans
            "tab_unsel_fill", getControlBackgroundColor(),
            "tab_sel_fill", getControlBackgroundColor(),
            // Table
            "Table.focusCellHighlightBorder", focusCellHighlightBorder,
            "Table.scrollPaneBorder", tableScrollPaneBorder,
            "Table.foreground", getInputForegroundColor(),
            "Table.background", getInputBackgroundColor(),
            "Table.gridColor", getGridColor(),
            "TableHeader.foreground", getControlForegroundColor(),
            "TableHeader.background", getBackgroundColor(),
            "TableHeader.cellBorder", getBorderFactory().getTableHeaderBorder(),
            // MenuBar
            "MenuBar.border", menuBarBorder,
            "MenuBar.foreground", getMenuForegroundColor(),
            "MenuBar.background", getMenuBackgroundColor(),
            // Menu
            "Menu.border", menuItemBorder,
            "Menu.borderPainted", Boolean.TRUE,
            "Menu.foreground", getMenuForegroundColor(),
            "Menu.background", getMenuBackgroundColor(),
            "Menu.selectionForeground", getMenuSelectionForegroundColor(),
            "Menu.selectionBackground", getMenuSelectionBackgroundColor(),
            "Menu.disabledForeground", getDisabledForegroundColor(),
            "Menu.acceleratorForeground", getMenuForegroundColor(),
            "Menu.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "Menu.arrowIcon", getIconFactory().getMenuArrowIcon(),
            // Popup Menu
            "PopupMenu.background", getMenuBackgroundColor(),
            "PopupMenu.border", popupMenuBorder,
            // Menu Item
            "MenuItem.border", menuItemBorder,
            "MenuItem.borderPainted", Boolean.TRUE,
            "MenuItem.foreground", getMenuForegroundColor(),
            "MenuItem.background", getMenuBackgroundColor(),
            "MenuItem.selectionForeground", getMenuSelectionForegroundColor(),
            "MenuItem.selectionBackground", getMenuSelectionBackgroundColor(),
            "MenuItem.disabledForeground", getDisabledForegroundColor(),
            "MenuItem.disabledShadow", getWhite(),
            "MenuItem.acceleratorForeground", getMenuForegroundColor(),
            "MenuItem.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "CheckBoxMenuItem.border", menuItemBorder,
            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
            "CheckBoxMenuItem.foreground", getMenuForegroundColor(),
            "CheckBoxMenuItem.background", getMenuBackgroundColor(),
            "CheckBoxMenuItem.selectionForeground", getMenuSelectionForegroundColor(),
            "CheckBoxMenuItem.selectionBackground", getMenuSelectionBackgroundColor(),
            "CheckBoxMenuItem.disabledForeground", getDisabledForegroundColor(),
            "CheckBoxMenuItem.disabledShadow", getWhite(),
            "CheckBoxMenuItem.acceleratorForeground", getMenuForegroundColor(),
            "CheckBoxMenuItem.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "CheckBoxMenuItem.checkIcon", getIconFactory().getMenuCheckBoxIcon(),
            "RadioButtonMenuItem.border", menuItemBorder,
            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
            "RadioButtonMenuItem.foreground", getMenuForegroundColor(),
            "RadioButtonMenuItem.background", getMenuBackgroundColor(),
            "RadioButtonMenuItem.selectionForeground", getMenuSelectionForegroundColor(),
            "RadioButtonMenuItem.selectionBackground", getMenuSelectionBackgroundColor(),
            "RadioButtonMenuItem.disabledForeground", getDisabledForegroundColor(),
            "RadioButtonMenuItem.disabledShadow", getWhite(),
            "RadioButtonMenuItem.acceleratorForeground", getMenuForegroundColor(),
            "RadioButtonMenuItem.acceleratorSelectionForeground", getMenuSelectionForegroundColor(),
            "RadioButtonMenuItem.checkIcon", getIconFactory().getMenuRadioButtonIcon(),
            // OptionPane.
            "OptionPane.errorIcon", getIconFactory().getOptionPaneErrorIcon(),
            "OptionPane.informationIcon", getIconFactory().getOptionPaneInformationIcon(),
            "OptionPane.warningIcon", getIconFactory().getOptionPaneWarningIcon(),
            "OptionPane.questionIcon", getIconFactory().getOptionPaneQuestionIcon(),
            "OptionPane.border", optionPaneBorder,
            "OptionPane.messageAreaBorder", optionPaneMessageAreaBorder,
            "OptionPane.buttonAreaBorder", optionPaneButtonAreaBorder,
            // File View
            "FileView.directoryIcon", getIconFactory().getTreeFolderIcon(),
            "FileView.fileIcon", getIconFactory().getTreeLeafIcon(),
            "FileView.computerIcon", getIconFactory().getTreeComputerIcon(),
            "FileView.hardDriveIcon", getIconFactory().getTreeHardDriveIcon(),
            "FileView.floppyDriveIcon", getIconFactory().getTreeFloppyDriveIcon(),
            // File Chooser
            "FileChooser.detailsViewIcon", getIconFactory().getFileChooserDetailViewIcon(),
            "FileChooser.homeFolderIcon", getIconFactory().getFileChooserHomeFolderIcon(),
            "FileChooser.listViewIcon", getIconFactory().getFileChooserListViewIcon(),
            "FileChooser.newFolderIcon", getIconFactory().getFileChooserNewFolderIcon(),
            "FileChooser.upFolderIcon", getIconFactory().getFileChooserUpFolderIcon(),
            // Separator
            "Separator.background", getBackgroundColor(),
            "Separator.foreground", getControlForegroundColor(),
            // SplitPane
            "SplitPane.centerOneTouchButtons", Boolean.TRUE,
            "SplitPane.dividerSize", new Integer(7),
            "SplitPane.border", BorderFactory.createEmptyBorder(),
            // Tree
            "Tree.background", getInputBackgroundColor(),
            "Tree.foreground", getInputForegroundColor(),
            "Tree.textForeground", getInputForegroundColor(),
            "Tree.textBackground", getInputBackgroundColor(),

            "Tree.openIcon", getIconFactory().getTreeFolderIcon(),
            "Tree.closedIcon", getIconFactory().getTreeFolderIcon(),
            "Tree.leafIcon", getIconFactory().getTreeLeafIcon(),
            "Tree.expandedIcon", getIconFactory().getTreeExpandedIcon(),
            "Tree.collapsedIcon", getIconFactory().getTreeCollapsedIcon(),
            "Tree.selectionBorderColor", getFocusCellColor(),
            "Tree.line", getFrameColor(), // horiz lines
            "Tree.hash", getFrameColor(), // legs

            // ToolBar
            "JToolBar.isRollover", Boolean.TRUE,
            "ToolBar.border", toolBarBorder,
            "ToolBar.background", getToolbarBackgroundColor(),
            "ToolBar.foreground", getToolbarForegroundColor(),
            "ToolBar.dockingBackground", getToolbarBackgroundColor(),
            "ToolBar.dockingForeground", getToolbarDockingColor(),
            "ToolBar.floatingBackground", getToolbarBackgroundColor(),
            "ToolBar.floatingForeground", getToolbarForegroundColor(),};
        table.putDefaults(defaults);

        if (JTattooUtilities.getJavaVersion() >= 1.5) {
            table.put("Spinner.font", getControlTextFont());
            table.put("Spinner.background", getButtonBackgroundColor());
            table.put("Spinner.foreground", getButtonForegroundColor());
            table.put("Spinner.border", getBorderFactory().getSpinnerBorder());
            table.put("Spinner.arrowButtonInsets", null);
            table.put("Spinner.arrowButtonBorder", BorderFactory.createEmptyBorder());
            table.put("Spinner.editorBorderPainted", Boolean.FALSE);
        }
    }

    public static void setTheme(AbstractTheme theme) {
        if (theme == null) {
            return;
        }

        MetalLookAndFeel.setCurrentTheme(theme);
        myTheme = (AbstractTheme) theme;
        if (isWindowDecorationOn()) {
            DecorationHelper.decorateWindows(Boolean.TRUE);
        } else {
            DecorationHelper.decorateWindows(Boolean.FALSE);
        }
    }

    /**
     * Set a theme by name. Allowed themes may come from the list returned by getThemes
     */
    public static void setTheme(String name) {
        // Overwrite this in derived classes
    }

    public static AbstractTheme getTheme() {
        return myTheme;
    }

    public static MetalTheme getCurrentTheme() {
        return myTheme;
    }

    public static java.util.List getThemes() {
        ArrayList themes = new ArrayList();
        themes.add(getTheme().getName());
        return themes;
    }

    public static boolean isWindowDecorationOn() {
        return getTheme().isWindowDecorationOn();
    }

    public static ColorUIResource getForegroundColor() {
        return getTheme().getForegroundColor();
    }

    public static ColorUIResource getDisabledForegroundColor() {
        return getTheme().getDisabledForegroundColor();
    }

    public static ColorUIResource getBackgroundColor() {
        return getTheme().getBackgroundColor();
    }

    public static ColorUIResource getAlterBackgroundColor() {
        return getTheme().getAlterBackgroundColor();
    }

    public static ColorUIResource getDisabledBackgroundColor() {
        return getTheme().getDisabledBackgroundColor();
    }

    public static ColorUIResource getInputForegroundColor() {
        return getTheme().getInputForegroundColor();
    }

    public static ColorUIResource getInputBackgroundColor() {
        return getTheme().getInputBackgroundColor();
    }

    public static ColorUIResource getFocusColor() {
        return getTheme().getFocusColor();
    }

    public static ColorUIResource getFocusCellColor() {
        return getTheme().getFocusCellColor();
    }

    public static ColorUIResource getFrameColor() {
        return getTheme().getFrameColor();
    }

    public static ColorUIResource getGridColor() {
        return getTheme().getGridColor();
    }

    public static ColorUIResource getSelectionForegroundColor() {
        return getTheme().getSelectionForegroundColor();
    }

    public static ColorUIResource getSelectionBackgroundColor() {
        return getTheme().getSelectionBackgroundColor();
    }

    public static ColorUIResource getButtonForegroundColor() {
        return getTheme().getButtonForegroundColor();
    }

    public static ColorUIResource getButtonBackgroundColor() {
        return getTheme().getButtonBackgroundColor();
    }

    public static ColorUIResource getButtonColorLight() {
        return getTheme().getButtonColorLight();
    }

    public static ColorUIResource getButtonColorDark() {
        return getTheme().getButtonColorDark();
    }

    public static ColorUIResource getControlForegroundColor() {
        return getTheme().getControlForegroundColor();
    }

    public static ColorUIResource getControlBackgroundColor() {
        return getTheme().getControlBackgroundColor();
    }

    public ColorUIResource getControlHighlightColor() {
        return getTheme().getControlHighlightColor();
    }

    public ColorUIResource getControlShadowColor() {
        return getTheme().getControlShadowColor();
    }

    public ColorUIResource getControlDarkShadowColor() {
        return getTheme().getControlDarkShadowColor();
    }

    public static ColorUIResource getControlColorLight() {
        return getTheme().getControlColorLight();
    }

    public static ColorUIResource getControlColorDark() {
        return getTheme().getControlColorDark();
    }

    public static ColorUIResource getWindowTitleForegroundColor() {
        return getTheme().getWindowTitleForegroundColor();
    }

    public static ColorUIResource getWindowTitleBackgroundColor() {
        return getTheme().getWindowTitleBackgroundColor();
    }

    public static ColorUIResource getWindowTitleColorLight() {
        return getTheme().getWindowTitleColorLight();
    }

    public static ColorUIResource getWindowTitleColorDark() {
        return getTheme().getWindowTitleColorDark();
    }

    public static ColorUIResource getWindowBorderColor() {
        return getTheme().getWindowBorderColor();
    }

    public static ColorUIResource getWindowInactiveTitleForegroundColor() {
        return getTheme().getWindowInactiveTitleForegroundColor();
    }

    public static ColorUIResource getWindowInactiveTitleBackgroundColor() {
        return getTheme().getWindowInactiveTitleBackgroundColor();
    }

    public static ColorUIResource getWindowInactiveTitleColorLight() {
        return getTheme().getWindowInactiveTitleColorLight();
    }

    public static ColorUIResource getWindowInactiveTitleColorDark() {
        return getTheme().getWindowInactiveTitleColorDark();
    }

    public static ColorUIResource getWindowInactiveBorderColor() {
        return getTheme().getWindowInactiveBorderColor();
    }

    public static ColorUIResource getMenuForegroundColor() {
        return getTheme().getMenuForegroundColor();
    }

    public static ColorUIResource getMenuBackgroundColor() {
        return getTheme().getMenuBackgroundColor();
    }

    public static ColorUIResource getMenuSelectionForegroundColor() {
        return getTheme().getMenuSelectionForegroundColor();
    }

    public static ColorUIResource getMenuSelectionBackgroundColor() {
        return getTheme().getMenuSelectionBackgroundColor();
    }

    public static ColorUIResource getMenuColorLight() {
        return getTheme().getMenuColorLight();
    }

    public static ColorUIResource getMenuColorDark() {
        return getTheme().getMenuColorDark();
    }

    public static ColorUIResource getToolbarForegroundColor() {
        return getTheme().getToolbarForegroundColor();
    }

    public static ColorUIResource getToolbarBackgroundColor() {
        return getTheme().getToolbarBackgroundColor();
    }

    public static ColorUIResource getToolbarColorLight() {
        return getTheme().getToolbarColorLight();
    }

    public static ColorUIResource getToolbarColorDark() {
        return getTheme().getToolbarColorDark();
    }

    public static ColorUIResource getToolbarDockingColor() {
        return getTheme().getFocusColor();
    }

    public static ColorUIResource getTabAreaBackgroundColor() {
        return getTheme().getTabAreaBackgroundColor();
    }

    public static ColorUIResource getTabSelectionForegroundColor() {
        return getTheme().getTabSelectionForegroundColor();
    }

    public static ColorUIResource getDesktopColor() {
        return getTheme().getDesktopColor();
    }

    public static ColorUIResource getTooltipForegroundColor() {
        return getTheme().getTooltipForegroundColor();
    }

    public static ColorUIResource getTooltipBackgroundColor() {
        return getTheme().getTooltipBackgroundColor();
    }
}
