/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import java.util.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class PulsarLookAndFeel extends AbstractLookAndFeel {

    private static PulsarDefaultTheme myTheme = null;

    private static final ArrayList themesList = new ArrayList();
    private static final HashMap themesMap = new HashMap();
    private static final Properties smallFontProps = new Properties();
    private static final Properties mediumFontProps = new Properties();
    private static final Properties largeFontProps = new Properties();
    
    static {
        mediumFontProps.setProperty("controlTextFont", "Dialog 15");
        mediumFontProps.setProperty("systemTextFont", "Dialog 15");
        mediumFontProps.setProperty("userTextFont", "Dialog 15");
        mediumFontProps.setProperty("menuTextFont", "Dialog 15");
        mediumFontProps.setProperty("windowTitleFont", "Dialog bold 15");
        mediumFontProps.setProperty("subTextFont", "Dialog 13");

        largeFontProps.setProperty("controlTextFont", "Dialog 16");
        largeFontProps.setProperty("systemTextFont", "Dialog 16");
        largeFontProps.setProperty("userTextFont", "Dialog 16");
        largeFontProps.setProperty("menuTextFont", "Dialog 16");
        largeFontProps.setProperty("windowTitleFont", "Dialog bold 16");
        largeFontProps.setProperty("subTextFont", "Dialog 14");
        
        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Medium-Font");
        themesList.add("Large-Font");
        
        themesMap.put("Default", smallFontProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Medium-Font", mediumFontProps);
        themesMap.put("Large-Font", largeFontProps);
    }
    
    public static java.util.List getThemes() { 
        return themesList;
    }
    
    public static Properties getThemeProperties(String name) {
        return ((Properties)themesMap.get(name));
    }
    
    public static void setTheme(String name) {
        if (myTheme != null)
            myTheme.setInternalName(name);
        setTheme((Properties)themesMap.get(name));
    }
    
    public static void setTheme(String name, String licenseKey, String logoString) {
        Properties props = (Properties)themesMap.get(name);
        props.put("licenseKey", licenseKey);
        props.put("logoString", logoString); 
        if (myTheme != null)
            myTheme.setInternalName(name);
        setTheme(props);
    }
    
    public static void setTheme(Properties themesProps) {
        if (myTheme == null)
           myTheme = new PulsarDefaultTheme();
        if ((myTheme != null) && (themesProps != null)) {
            myTheme.setUpColor();
            myTheme.setProperties(themesProps);
            myTheme.setUpColorArrs();
            AbstractLookAndFeel.setTheme(myTheme);
        }
    }
    
    public static void setCurrentTheme(Properties themesProps) {
        setTheme(themesProps);
    }
    
    public String getName()
    { return "Pulsar"; }
    
    public String getID()
    { return "Pulsar"; }
    
    public String getDescription()
    { return "The Pulsar Look and Feel"; }
    
    public boolean isNativeLookAndFeel()
    { return false; }
    
    public boolean isSupportedLookAndFeel()
    { return true; }
    
    public AbstractBorderFactory getBorderFactory()
    { return PulsarBorderFactory.getInstance(); }
    
    public AbstractIconFactory getIconFactory()
    { return PulsarIconFactory.getInstance(); }
    
    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new PulsarDefaultTheme();
        }
        setTheme(myTheme);
    }
    
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {
            // BaseLookAndFeel classes
            "LabelUI", BaseLabelUI.class.getName(),
            "ToggleButtonUI", BaseToggleButtonUI.class.getName(),
            "SeparatorUI", BaseSeparatorUI.class.getName(),
            "TextFieldUI", BaseTextFieldUI.class.getName(),
            "TextAreaUI", BaseTextAreaUI.class.getName(),
            "EditorPaneUI", BaseEditorPaneUI.class.getName(),
            "PasswordFieldUI", BasePasswordFieldUI.class.getName(),
            "ComboBoxUI", BaseComboBoxUI.class.getName(),
            "ToolTipUI", BaseToolTipUI.class.getName(),
            "TreeUI", BaseTreeUI.class.getName(),
            "TableUI", BaseTableUI.class.getName(),
            "TableHeaderUI", BaseTableHeaderUI.class.getName(),
            "ScrollBarUI", BaseScrollBarUI.class.getName(),
            "ScrollPaneUI", BaseScrollPaneUI.class.getName(),
            "ProgressBarUI", BaseProgressBarUI.class.getName(),
            "PanelUI", BasePanelUI.class.getName(),
            "SplitPaneUI", BaseSplitPaneUI.class.getName(),
            "SliderUI", BaseSliderUI.class.getName(),
            "FileChooserUI", BaseFileChooserUI.class.getName(),

            "MenuBarUI", BaseMenuBarUI.class.getName(),
            "PopupMenuUI", BasePopupMenuUI.class.getName(),
            "PopupMenuSeparatorUI", BaseSeparatorUI.class.getName(),
            
            // PulsarLookAndFeel classes
            "RadioButtonUI", PulsarRadioButtonUI.class.getName(),
            "CheckBoxUI", PulsarCheckBoxUI.class.getName(),
            "ButtonUI", PulsarButtonUI.class.getName(),
            "MenuUI", PulsarMenuUI.class.getName(),
            "MenuItemUI", PulsarMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", PulsarCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", PulsarRadioButtonMenuItemUI.class.getName(),
            "TabbedPaneUI", PulsarTabbedPaneUI.class.getName(),
            "ToolBarUI", PulsarToolBarUI.class.getName(),
            "InternalFrameUI", PulsarInternalFrameUI.class.getName(),
            "RootPaneUI", PulsarRootPaneUI.class.getName(),
        };
        table.putDefaults(uiDefaults);
        if (JTattooUtilities.getJavaVersion() >= 1.5) {
            table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
            table.put("SpinnerUI", BaseSpinnerUI.class.getName());
        }
    }
    
}