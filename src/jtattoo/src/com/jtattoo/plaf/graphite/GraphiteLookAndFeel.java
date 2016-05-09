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
 
package com.jtattoo.plaf.graphite;

import com.jtattoo.plaf.*;
import java.util.*;
import javax.swing.UIDefaults;

/**
 * @author Michael Hagen
 */
public class GraphiteLookAndFeel extends AbstractLookAndFeel {
    private static GraphiteDefaultTheme myTheme = null;
    private static final ArrayList themesList = new ArrayList();
    private static final HashMap themesMap = new HashMap();
    private static final Properties defaultProps = new Properties();
    private static final Properties smallFontProps = new Properties();
    private static final Properties mediumFontProps = new Properties();
    private static final Properties largeFontProps = new Properties();
    private static final Properties greenProps = new Properties();
    private static final Properties greenSmallFontProps = new Properties();
    private static final Properties greenMediumFontProps = new Properties();
    private static final Properties greenLargeFontProps = new Properties();
    private static final Properties blueProps = new Properties();
    private static final Properties blueSmallFontProps = new Properties();
    private static final Properties blueMediumFontProps = new Properties();
    private static final Properties blueLargeFontProps = new Properties();

    static {
        smallFontProps.setProperty("controlTextFont", "Dialog 12");
        smallFontProps.setProperty("systemTextFont", "Dialog 12");
        smallFontProps.setProperty("userTextFont", "Dialog 12");
        smallFontProps.setProperty("menuTextFont", "Dialog 12");
        smallFontProps.setProperty("windowTitleFont", "Dialog bold 12");
        smallFontProps.setProperty("subTextFont", "Dialog 11");

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

        greenProps.setProperty("windowTitleForegroundColor", "255 255 255");
        greenProps.setProperty("windowTitleBackgroundColor", "0 96 52");
        greenProps.setProperty("windowTitleColorLight", "0 136 57");
        greenProps.setProperty("windowTitleColorDark", "0 88 47");
        greenProps.setProperty("windowBorderColor", "0 88 47");
        greenProps.setProperty("windowInactiveTitleForegroundColor", "255 255 255");
        greenProps.setProperty("windowInactiveTitleBackgroundColor", "0 96 50");
        greenProps.setProperty("windowInactiveTitleColorLight", "0 119 51");
        greenProps.setProperty("windowInactiveTitleColorDark", "0 88 47");
        greenProps.setProperty("windowInactiveBorderColor", "0 88 47");
        greenProps.setProperty("backgroundColor", "231 244 219");
        greenProps.setProperty("backgroundColorLight", "255 255 255");
        greenProps.setProperty("backgroundColorDark", "208 234 185");
        greenProps.setProperty("alterBackgroundColor", "208 234 185");
        greenProps.setProperty("frameColor", "114 180 54");
        greenProps.setProperty("disabledForegroundColor", "96 96 96");
        greenProps.setProperty("disabledBackgroundColor", "228 240 216");
        greenProps.setProperty("selectionForegroundColor", "255 255 255");
        greenProps.setProperty("selectionBackgroundColor", "48 136 53");
        greenProps.setProperty("controlBackgroundColor", "231 244 219");
        greenProps.setProperty("controlColorLight", "40 170 60");
        greenProps.setProperty("controlColorDark", "48 136 53");
        greenProps.setProperty("controlDarkShadowColor", "112 176 53");
        greenProps.setProperty("buttonColorLight", "255 255 255");
        greenProps.setProperty("buttonColorDark", "231 244 219");
        greenProps.setProperty("menuBackgroundColor", "255 255 255");
        greenProps.setProperty("menuColorDark", "218 238 200");
        greenProps.setProperty("menuColorLight", "231 244 219");
        greenProps.setProperty("menuSelectionBackgroundColor", "72 51 0");
        greenProps.setProperty("toolbarBackgroundColor", "231 244 219");
        greenProps.setProperty("toolbarColorLight", "231 244 219");
        greenProps.setProperty("toolbarColorDark", "208 234 185");

        blueProps.setProperty("windowTitleForegroundColor", "255 255 255");
        blueProps.setProperty("windowTitleBackgroundColor", "1 49 157");
        blueProps.setProperty("windowTitleColorLight", "1 40 131");
        blueProps.setProperty("windowTitleColorDark", "1 30 97");
        blueProps.setProperty("windowBorderColor", "0 24 83");
        blueProps.setProperty("windowInactiveTitleForegroundColor", "255 255 255");
        blueProps.setProperty("windowInactiveTitleBackgroundColor", "55 87 129");
        blueProps.setProperty("windowInactiveTitleColorLight", "1 35 116");
        blueProps.setProperty("windowInactiveTitleColorDark", "1 30 97");
        blueProps.setProperty("windowInactiveBorderColor", "0 24 83");
        blueProps.setProperty("backgroundColor", "228 235 243");
        blueProps.setProperty("backgroundColorLight", "255 255 255");
        blueProps.setProperty("backgroundColorDark", "188 204 226");
        blueProps.setProperty("alterBackgroundColor", "208 220 234");
        blueProps.setProperty("frameColor", "120 153 197");
        blueProps.setProperty("disabledForegroundColor", "96 96 96");
        blueProps.setProperty("disabledBackgroundColor", "225 232 240");
        blueProps.setProperty("selectionForegroundColor", "255 255 255");
        blueProps.setProperty("selectionBackgroundColor", "1 38 124");
        blueProps.setProperty("controlBackgroundColor", "228 235 243");
        blueProps.setProperty("controlColorLight", "1 40 131");
        blueProps.setProperty("controlColorDark", "1 30 98");
        blueProps.setProperty("controlDarkShadowColor", "64 100 149");
        blueProps.setProperty("buttonColorLight", "255 255 255");
        blueProps.setProperty("buttonColorDark", "228 235 243");
        blueProps.setProperty("menuBackgroundColor", "255 255 255");
        blueProps.setProperty("menuColorDark", "208 220 234");
        blueProps.setProperty("menuColorLight", "228 235 243");
        blueProps.setProperty("menuSelectionBackgroundColor", "72 51 0");
        blueProps.setProperty("toolbarBackgroundColor", "228 235 243");
        blueProps.setProperty("toolbarColorLight", "228 235 243");
        blueProps.setProperty("toolbarColorDark", "188 204 226");

        String key = null;
        String value = null;
        Iterator iter = smallFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = smallFontProps.getProperty(key);
            greenSmallFontProps.setProperty(key, value);
            blueSmallFontProps.setProperty(key, value);
        }

        iter = mediumFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = mediumFontProps.getProperty(key);
            greenMediumFontProps.setProperty(key, value);
            blueMediumFontProps.setProperty(key, value);
        }

        iter = largeFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = largeFontProps.getProperty(key);
            greenLargeFontProps.setProperty(key, value);
            blueLargeFontProps.setProperty(key, value);
        }

        iter = greenProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = greenProps.getProperty(key);
            greenSmallFontProps.setProperty(key, value);
            greenMediumFontProps.setProperty(key, value);
            greenLargeFontProps.setProperty(key, value);
        }

        iter = blueProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = blueProps.getProperty(key);
            blueSmallFontProps.setProperty(key, value);
            blueMediumFontProps.setProperty(key, value);
            blueLargeFontProps.setProperty(key, value);
        }

        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Medium-Font");
        themesList.add("Large-Font");

        themesList.add("Green");
        themesList.add("Green-Small-Font");
        themesList.add("Green-Medium-Font");
        themesList.add("Green-Large-Font");

        themesList.add("Blue");
        themesList.add("Blue-Small-Font");
        themesList.add("Blue-Medium-Font");
        themesList.add("Blue-Large-Font");

        themesMap.put("Default", defaultProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Medium-Font", mediumFontProps);
        themesMap.put("Large-Font", largeFontProps);

        themesMap.put("Green", greenProps);
        themesMap.put("Green-Small-Font", greenSmallFontProps);
        themesMap.put("Green-Medium-Font", greenMediumFontProps);
        themesMap.put("Green-Large-Font", greenLargeFontProps);

        themesMap.put("Blue", blueProps);
        themesMap.put("Blue-Small-Font", blueSmallFontProps);
        themesMap.put("Blue-Medium-Font", blueMediumFontProps);
        themesMap.put("Blue-Large-Font", blueLargeFontProps);
    }

    public static java.util.List getThemes() {
        return themesList;
    }

    public static Properties getThemeProperties(String name) {
        return ((Properties)themesMap.get(name));
    }

    public static void setTheme(String name) {
        setTheme((Properties)themesMap.get(name));
        if (myTheme != null) {
            AbstractTheme.setInternalName(name);
        }
    }

    public static void setTheme(String name, String licenseKey, String logoString) {
        Properties props = (Properties)themesMap.get(name);
        if (props != null) {
            props.put("licenseKey", licenseKey);
            props.put("logoString", logoString);
            setTheme(props);
            if (myTheme != null) {
                AbstractTheme.setInternalName(name);
            }
        }
    }

    public static void setTheme(Properties themesProps) {
        if (myTheme == null) {
           myTheme = new GraphiteDefaultTheme();
        }
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
    { return "Graphite"; }

    public String getID()
    { return "Graphite"; }

    public String getDescription()
    { return "The Graphite Look and Feel"; }

    public boolean isNativeLookAndFeel()
    { return false; }

    public boolean isSupportedLookAndFeel()
    { return true; }

    public AbstractBorderFactory getBorderFactory()
    { return GraphiteBorderFactory.getInstance(); }

    public AbstractIconFactory getIconFactory()
    { return GraphiteIconFactory.getInstance(); }

    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new GraphiteDefaultTheme();
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
            "DesktopPaneUI", BaseDesktopPaneUI.class.getName(),

            // GraphiteLookAndFeel classes
            "RadioButtonUI", GraphiteRadioButtonUI.class.getName(),
            "CheckBoxUI", GraphiteCheckBoxUI.class.getName(),
            "ButtonUI", GraphiteButtonUI.class.getName(),
            "MenuUI", GraphiteMenuUI.class.getName(),
            "MenuItemUI", GraphiteMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", GraphiteCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", GraphiteRadioButtonMenuItemUI.class.getName(),
            "TabbedPaneUI", GraphiteTabbedPaneUI.class.getName(),
            "ToolBarUI", GraphiteToolBarUI.class.getName(),
            "InternalFrameUI", GraphiteInternalFrameUI.class.getName(),
            "RootPaneUI", GraphiteRootPaneUI.class.getName(),
        };
        table.putDefaults(uiDefaults);
        if (JTattooUtilities.getJavaVersion() >= 1.5) {
            table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
            table.put("SpinnerUI", BaseSpinnerUI.class.getName());
        }
    }

}