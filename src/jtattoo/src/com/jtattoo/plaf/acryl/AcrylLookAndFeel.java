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
 
package com.jtattoo.plaf.acryl;

import com.jtattoo.plaf.*;
import java.util.*;
import javax.swing.UIDefaults;

/**
 * @author Michael Hagen
 */
public class AcrylLookAndFeel extends AbstractLookAndFeel {

    private static AcrylDefaultTheme myTheme = null;

    private static final ArrayList themesList = new ArrayList();
    private static final HashMap themesMap = new HashMap();
    private static final Properties defaultProps = new Properties();
    private static final Properties smallFontProps = new Properties();
    private static final Properties largeFontProps = new Properties();
    private static final Properties giantFontProps = new Properties();
    private static final Properties greenProps = new Properties();
    private static final Properties greenSmallFontProps = new Properties();
    private static final Properties greenLargeFontProps = new Properties();
    private static final Properties greenGiantFontProps = new Properties();
    private static final Properties lemmonProps = new Properties();
    private static final Properties lemmonSmallFontProps = new Properties();
    private static final Properties lemmonLargeFontProps = new Properties();
    private static final Properties lemmonGiantFontProps = new Properties();
    private static final Properties redProps = new Properties();
    private static final Properties redSmallFontProps = new Properties();
    private static final Properties redLargeFontProps = new Properties();
    private static final Properties redGiantFontProps = new Properties();

    static {
        smallFontProps.setProperty("controlTextFont", "Dialog 10");
        smallFontProps.setProperty("systemTextFont", "Dialog 10");
        smallFontProps.setProperty("userTextFont", "Dialog 10");
        smallFontProps.setProperty("menuTextFont", "Dialog 10");
        smallFontProps.setProperty("windowTitleFont", "Dialog bold 10");
        smallFontProps.setProperty("subTextFont", "Dialog 8");

        largeFontProps.setProperty("controlTextFont", "Dialog 14");
        largeFontProps.setProperty("systemTextFont", "Dialog 14");
        largeFontProps.setProperty("userTextFont", "Dialog 14");
        largeFontProps.setProperty("menuTextFont", "Dialog 14");
        largeFontProps.setProperty("windowTitleFont", "Dialog bold 14");
        largeFontProps.setProperty("subTextFont", "Dialog 12");

        giantFontProps.setProperty("controlTextFont", "Dialog 18");
        giantFontProps.setProperty("systemTextFont", "Dialog 18");
        giantFontProps.setProperty("userTextFont", "Dialog 18");
        giantFontProps.setProperty("menuTextFont", "Dialog 18");
        giantFontProps.setProperty("windowTitleFont", "Dialog 18");
        giantFontProps.setProperty("subTextFont", "Dialog 16");

        greenProps.setProperty("backgroundColor", "232 229 222");
        greenProps.setProperty("alterBackgroundColor", "220 216 205");
        greenProps.setProperty("frameColor", "28 64 43");
        greenProps.setProperty("selectionBackgroundColor", "54 126 85");
        greenProps.setProperty("menuSelectionBackgroundColor", "54 126 85");
        greenProps.setProperty("controlColorLight", "64 149 100");
        greenProps.setProperty("controlColorDark", "48 112 75");
        greenProps.setProperty("rolloverColor", "255 213 113");
        greenProps.setProperty("rolloverColorLight", "255 213 113");
        greenProps.setProperty("rolloverColorDark", "240 168 0");
        greenProps.setProperty("windowTitleBackgroundColor", "64 149 100");
        greenProps.setProperty("windowTitleColorLight", "64 149 100");
        greenProps.setProperty("windowTitleColorDark", "48 112 75");
        greenProps.setProperty("windowBorderColor", "40 94 63");
        greenProps.setProperty("windowInactiveTitleBackgroundColor", "77 179 120");
        greenProps.setProperty("windowInactiveTitleColorLight", "77 179 120");
        greenProps.setProperty("windowInactiveTitleColorDark", "64 149 100");
        greenProps.setProperty("windowInactiveBorderColor", "64 149 100");
        greenProps.setProperty("menuBackgroundColor", "232 229 222");
        greenProps.setProperty("menuColorLight", "238 236 232");
        greenProps.setProperty("menuColorDark", "232 229 222");
        greenProps.setProperty("toolbarBackgroundColor", "232 229 222");
        greenProps.setProperty("toolbarColorLight", "238 236 232");
        greenProps.setProperty("toolbarColorDark", "232 229 222");
        greenProps.setProperty("desktopColor", "244 242 232");

        lemmonProps.setProperty("backgroundColor", "240 243 242");
        lemmonProps.setProperty("frameColor", "100 133 14");
        lemmonProps.setProperty("selectionForegroundColor", "0 0 0");
        lemmonProps.setProperty("selectionBackgroundColor", "175 232 28");
        lemmonProps.setProperty("rolloverColor", "231 253 104");
        lemmonProps.setProperty("rolloverColorLight", "243 254 180");
        lemmonProps.setProperty("rolloverColorDark", "231 253 104");
        lemmonProps.setProperty("windowTitleForegroundColor", "243 254 180");
        lemmonProps.setProperty("windowTitleBackgroundColor", "164 217 23");
        lemmonProps.setProperty("windowTitleColorLight", "164 217 23");
        lemmonProps.setProperty("windowTitleColorDark", "140 186 20");
        lemmonProps.setProperty("windowBorderColor", "106 140 15");
        lemmonProps.setProperty("windowInactiveTitleForegroundColor", "243 254 180");
        lemmonProps.setProperty("windowInactiveTitleBackgroundColor", "148 196 21");
        lemmonProps.setProperty("windowInactiveTitleColorLight", "148 196 21");
        lemmonProps.setProperty("windowInactiveTitleColorDark", "126 167 18");
        lemmonProps.setProperty("windowInactiveBorderColor", "92 123 13");
        lemmonProps.setProperty("controlColorLight", "207 245 35");
        lemmonProps.setProperty("controlColorDark", "155 211 18");
        lemmonProps.setProperty("menuBackgroundColor", "240 243 242");
        lemmonProps.setProperty("menuSelectionForegroundColor", "0 0 0");
        lemmonProps.setProperty("menuSelectionBackgroundColor", "175 232 28");
        lemmonProps.setProperty("menuColorLight", "244 247 245");
        lemmonProps.setProperty("menuColorDark", "232 236 235");
        lemmonProps.setProperty("toolbarBackgroundColor", "240 243 242");
        lemmonProps.setProperty("toolbarColorLight", "244 247 245");
        lemmonProps.setProperty("toolbarColorDark", "232 236 235");

        redProps.setProperty("backgroundColor", "244 244 244");
        redProps.setProperty("frameColor", "64 48 48");
        redProps.setProperty("selectionForegroundColor", "255 255 255");
        redProps.setProperty("selectionBackgroundColor", "220 0 0");
        redProps.setProperty("rolloverColor", "222 222 190");
        redProps.setProperty("rolloverColorLight", "248 248 180");
        redProps.setProperty("rolloverColorDark", "200 200 120");
        redProps.setProperty("windowTitleForegroundColor", "255 255 255");
        redProps.setProperty("windowTitleBackgroundColor", "160 0 0");
        redProps.setProperty("windowTitleColorLight", "230 12 12");
        redProps.setProperty("windowTitleColorDark", "190 0 0");
        redProps.setProperty("windowBorderColor", "160 0 0");
        redProps.setProperty("windowInactiveTitleForegroundColor", "255 255 255");
        redProps.setProperty("windowInactiveTitleBackgroundColor", "180 0 0");
        redProps.setProperty("windowInactiveTitleColorLight", "255 24 24");
        redProps.setProperty("windowInactiveTitleColorDark", "180 0 0");
        redProps.setProperty("windowInactiveBorderColor", "180 0 0");
        redProps.setProperty("controlColorLight", "255 24 24");
        redProps.setProperty("controlColorDark", "190 0 0");
        redProps.setProperty("menuBackgroundColor", "248 248 248");
        redProps.setProperty("menuSelectionForegroundColor", "255 255 255");
        redProps.setProperty("menuSelectionBackgroundColor", "220 0 0");
        redProps.setProperty("menuColorLight", "248 248 248");
        redProps.setProperty("menuColorDark", "236 236 236");
        redProps.setProperty("toolbarBackgroundColor", "248 248 248");
        redProps.setProperty("toolbarColorLight", "248 248 248");
        redProps.setProperty("toolbarColorDark", "236 236 236");

        String key = null;
        String value = null;
        Iterator iter = smallFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = smallFontProps.getProperty(key);
            greenSmallFontProps.setProperty(key, value);
            lemmonSmallFontProps.setProperty(key, value);
            redSmallFontProps.setProperty(key, value);
        }
        iter = largeFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = largeFontProps.getProperty(key);
            greenLargeFontProps.setProperty(key, value);
            lemmonLargeFontProps.setProperty(key, value);
            redLargeFontProps.setProperty(key, value);
        }
        iter = giantFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = giantFontProps.getProperty(key);
            greenGiantFontProps.setProperty(key, value);
            lemmonGiantFontProps.setProperty(key, value);
            redGiantFontProps.setProperty(key, value);
        }

        iter = greenProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = greenProps.getProperty(key);
            greenSmallFontProps.setProperty(key, value);
            greenLargeFontProps.setProperty(key, value);
            greenGiantFontProps.setProperty(key, value);
        }
        iter = lemmonProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = lemmonProps.getProperty(key);
            lemmonSmallFontProps.setProperty(key, value);
            lemmonLargeFontProps.setProperty(key, value);
            lemmonGiantFontProps.setProperty(key, value);
        }
        iter = redProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = redProps.getProperty(key);
            redSmallFontProps.setProperty(key, value);
            redLargeFontProps.setProperty(key, value);
            redGiantFontProps.setProperty(key, value);
        }

        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Large-Font");
        themesList.add("Giant-Font");

        themesList.add("Green");
        themesList.add("Green-Small-Font");
        themesList.add("Green-Large-Font");
        themesList.add("Green-Giant-Font");

        themesList.add("Lemmon");
        themesList.add("Lemmon-Small-Font");
        themesList.add("Lemmon-Large-Font");
        themesList.add("Lemmon-Giant-Font");

        themesList.add("Red");
        themesList.add("Red-Small-Font");
        themesList.add("Red-Large-Font");
        themesList.add("Red-Giant-Font");

        themesMap.put("Default", defaultProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Large-Font", largeFontProps);
        themesMap.put("Giant-Font", giantFontProps);

        themesMap.put("Green", greenProps);
        themesMap.put("Green-Small-Font", greenSmallFontProps);
        themesMap.put("Green-Large-Font", greenLargeFontProps);
        themesMap.put("Green-Giant-Font", greenGiantFontProps);

        themesMap.put("Lemmon", lemmonProps);
        themesMap.put("Lemmon-Small-Font", lemmonSmallFontProps);
        themesMap.put("Lemmon-Large-Font", lemmonLargeFontProps);
        themesMap.put("Lemmon-Giant-Font", lemmonGiantFontProps);

        themesMap.put("Red", redProps);
        themesMap.put("Red-Small-Font", redSmallFontProps);
        themesMap.put("Red-Large-Font", redLargeFontProps);
        themesMap.put("Red-Giant-Font", redGiantFontProps);
    }

    public static java.util.List getThemes() {
        return themesList;
    }

    public static Properties getThemeProperties(String name) {
        return ((Properties) themesMap.get(name));
    }

    public static void setTheme(String name) {
        setTheme((Properties) themesMap.get(name));
        if (myTheme != null) {
            AbstractTheme.setInternalName(name);
        }
    }

    public static void setTheme(String name, String licenseKey, String logoString) {
        Properties props = (Properties) themesMap.get(name);
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
            myTheme = new AcrylDefaultTheme();
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

    public String getName() {
        return "Acryl";
    }

    public String getID() {
        return "Acryl";
    }

    public String getDescription() {
        return "The Acryl Look and Feel";
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public AbstractBorderFactory getBorderFactory() {
        return AcrylBorderFactory.getInstance();
    }

    public AbstractIconFactory getIconFactory() {
        return AcrylIconFactory.getInstance();
    }

    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new AcrylDefaultTheme();
        }
        setTheme(myTheme);
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {
            // BaseLookAndFeel classes
            "ToggleButtonUI", BaseToggleButtonUI.class.getName(),
            "LabelUI", BaseLabelUI.class.getName(),
            "SeparatorUI", BaseSeparatorUI.class.getName(),
            "TextFieldUI", BaseTextFieldUI.class.getName(),
            "TextAreaUI", BaseTextAreaUI.class.getName(),
            "EditorPaneUI", BaseEditorPaneUI.class.getName(),
            "PasswordFieldUI", BasePasswordFieldUI.class.getName(),
            "CheckBoxUI", BaseCheckBoxUI.class.getName(),
            "RadioButtonUI", BaseRadioButtonUI.class.getName(),
            "SplitPaneUI", BaseSplitPaneUI.class.getName(),
            "ToolTipUI", BaseToolTipUI.class.getName(),
            "TreeUI", BaseTreeUI.class.getName(),
            "TableUI", BaseTableUI.class.getName(),
            "SliderUI", BaseSliderUI.class.getName(),
            "ProgressBarUI", BaseProgressBarUI.class.getName(),
            "ScrollPaneUI", BaseScrollPaneUI.class.getName(),
            "PanelUI", BasePanelUI.class.getName(),
            "TableHeaderUI", BaseTableHeaderUI.class.getName(),
            "FileChooserUI", BaseFileChooserUI.class.getName(),
            "MenuBarUI", BaseMenuBarUI.class.getName(),
            "MenuUI", BaseMenuUI.class.getName(),
            "PopupMenuUI", BasePopupMenuUI.class.getName(),
            "MenuItemUI", BaseMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", BaseCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", BaseRadioButtonMenuItemUI.class.getName(),
            "PopupMenuSeparatorUI", BaseSeparatorUI.class.getName(),
            "DesktopPaneUI", BaseDesktopPaneUI.class.getName(),

            // AcrylLookAndFeel classes
            "ButtonUI", AcrylButtonUI.class.getName(),
            "ComboBoxUI", AcrylComboBoxUI.class.getName(),
            "TabbedPaneUI", AcrylTabbedPaneUI.class.getName(),
            "ToolBarUI", AcrylToolBarUI.class.getName(),
            "InternalFrameUI", AcrylInternalFrameUI.class.getName(),
            "RootPaneUI", AcrylRootPaneUI.class.getName(),
            "ScrollBarUI", AcrylScrollBarUI.class.getName(),
        };
        table.putDefaults(uiDefaults);
        if (JTattooUtilities.getJavaVersion() >= 1.5) {
            table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
            table.put("SpinnerUI", AcrylSpinnerUI.class.getName());
        }
    }
}