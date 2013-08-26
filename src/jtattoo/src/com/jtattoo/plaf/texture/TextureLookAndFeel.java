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
 
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.*;
import java.util.*;
import javax.swing.UIDefaults;
import javax.swing.plaf.InsetsUIResource;

/**
 * @author Michael Hagen
 */
public class TextureLookAndFeel extends AbstractLookAndFeel {

    private static TextureDefaultTheme myTheme = null;
    private static final ArrayList themesList = new ArrayList();
    private static final HashMap themesMap = new HashMap();
    private static final Properties defaultProps = new Properties();
    private static final Properties smallFontProps = new Properties();
    private static final Properties mediumFontProps = new Properties();
    private static final Properties largeFontProps = new Properties();
    private static final Properties rockProps = new Properties();
    private static final Properties rockSmallFontProps = new Properties();
    private static final Properties rockMediumFontProps = new Properties();
    private static final Properties rockLargeFontProps = new Properties();
    private static final Properties textileProps = new Properties();
    private static final Properties textileSmallFontProps = new Properties();
    private static final Properties textileMediumFontProps = new Properties();
    private static final Properties textileLargeFontProps = new Properties();
    private static final Properties snowProps = new Properties();
    private static final Properties snowSmallFontProps = new Properties();
    private static final Properties snowMediumFontProps = new Properties();
    private static final Properties snowLargeFontProps = new Properties();

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

        rockProps.setProperty("textureSet", "Rock");
        rockProps.setProperty("backgroundColor", "240 240 240");
        rockProps.setProperty("backgroundColorLight", "220 220 220");
        rockProps.setProperty("backgroundColorDark", "200 200 200");
        rockProps.setProperty("alterBackgroundColor", "180 180 180");

        rockProps.setProperty("frameColor", "164 164 164");
        rockProps.setProperty("gridColor", "196 196 196");

        rockProps.setProperty("disabledForegroundColor", "96 96 96");
        rockProps.setProperty("disabledBackgroundColor", "240 240 240");

        rockProps.setProperty("rolloverColor", "160 160 160");
        rockProps.setProperty("rolloverColorLight", "230 230 230");
        rockProps.setProperty("rolloverColorDark", "210 210 210");

        rockProps.setProperty("controlBackgroundColor", "248 248 248");
        rockProps.setProperty("controlShadowColor", "160 160 160");
        rockProps.setProperty("controlDarkShadowColor", "110 110 110");
        rockProps.setProperty("controlColorLight", "248 248 248");
        rockProps.setProperty("controlColorDark", " 210 210 210");

        rockProps.setProperty("buttonColorLight", "255 255 255");
        rockProps.setProperty("buttonColorDark", "230 230 230");

        rockProps.setProperty("menuBackgroundColor", "64 64 64");
        rockProps.setProperty("menuColorLight", "96 96 96");
        rockProps.setProperty("menuColorDark", "48 48 48");
        rockProps.setProperty("menuSelectionBackgroundColor", "48 48 48");
        rockProps.setProperty("toolbarBackgroundColor", "64 64 64");
        rockProps.setProperty("toolbarColorLight", "96 96 96");
        rockProps.setProperty("toolbarColorDark", "48 48 48");
        rockProps.setProperty("desktopColor", "220 220 220");
        rockProps.setProperty("tooltipBackgroundColor", "248 248 248");

        textileProps.setProperty("textureSet", "Textile");
        textileProps.setProperty("backgroundColor", "240 240 240");
        textileProps.setProperty("backgroundColorLight", "184 194 218");
        textileProps.setProperty("backgroundColorDark", "152 168 201");
        textileProps.setProperty("alterBackgroundColor", "180 180 180");

        textileProps.setProperty("frameColor", "164 164 164");
        textileProps.setProperty("gridColor", "196 196 196");

        textileProps.setProperty("disabledForegroundColor", "96 96 96");
        textileProps.setProperty("disabledBackgroundColor", "240 240 240");

        textileProps.setProperty("selectionForegroundColor", "0 0 0");
        textileProps.setProperty("selectionBackgroundColor", "184 194 218");
        textileProps.setProperty("selectionBackgroundColorLight", "184 194 218");
        textileProps.setProperty("selectionBackgroundColorDark", "112 134 180");

        textileProps.setProperty("rolloverForegroundColor", "255 255 255");
        textileProps.setProperty("rolloverColor", "132 148 176");
        textileProps.setProperty("rolloverColorLight", "184 194 218");
        textileProps.setProperty("rolloverColorDark", "152 168 201");

        textileProps.setProperty("controlBackgroundColor", "248 248 248");
        textileProps.setProperty("controlShadowColor", "160 160 160");
        textileProps.setProperty("controlDarkShadowColor", "160 160 160");
        textileProps.setProperty("controlColorLight", "240 240 240");
        textileProps.setProperty("controlColorDark", " 210 210 210");

        textileProps.setProperty("pressedForegroundColor", "0 0 0");
        textileProps.setProperty("buttonColorLight", "255 255 255");
        textileProps.setProperty("buttonColorDark", "230 230 230");

        textileProps.setProperty("windowBorderColor", "27 42 111");
        textileProps.setProperty("windowTitleBackgroundColor", "40 54 114");
        textileProps.setProperty("windowTitleColorLight", "80 112 162");
        textileProps.setProperty("windowTitleColorDark", "44 59 126");

        textileProps.setProperty("menuForegroundColor", "215 221 234");
        textileProps.setProperty("menuBackgroundColor", "64 64 64");
        textileProps.setProperty("menuColorLight", "96 96 96");
        textileProps.setProperty("menuColorDark", "48 48 48");
        textileProps.setProperty("menuSelectionForegroundColor", "255 255 255");
        textileProps.setProperty("menuSelectionBackgroundColor", "48 48 48");
        
        textileProps.setProperty("toolbarBackgroundColor", "64 78 102");
        textileProps.setProperty("toolbarColorLight", "76 91 120");
        textileProps.setProperty("toolbarColorDark", "64 78 102");
        
        textileProps.setProperty("desktopColor", "152 168 201");
        textileProps.setProperty("tooltipBackgroundColor", "255 255 220");

        snowProps.setProperty("textureSet", "Snow");
        snowProps.setProperty("darkTexture", "off");
        snowProps.setProperty("backgroundColor", "240 240 240");
        snowProps.setProperty("backgroundColorLight", "225 234 247");
        snowProps.setProperty("backgroundColorDark", "188 206 237");
        snowProps.setProperty("alterBackgroundColor", "180 180 180");

        snowProps.setProperty("selectionBackgroundColorLight", "225 234 247");
        snowProps.setProperty("selectionBackgroundColorDark", "188 206 237");

        snowProps.setProperty("frameColor", "164 164 164");
        snowProps.setProperty("gridColor", "196 196 196");

        snowProps.setProperty("disabledForegroundColor", "96 96 96");
        snowProps.setProperty("disabledBackgroundColor", "240 240 240");

        snowProps.setProperty("rolloverColor", "225 234 247");
        snowProps.setProperty("rolloverColorLight", "245 248 252");
        snowProps.setProperty("rolloverColorDark", "225 234 247");

        snowProps.setProperty("controlBackgroundColor", "248 248 248");
        snowProps.setProperty("controlShadowColor", "160 160 160");
        snowProps.setProperty("controlDarkShadowColor", "180 180 180");
        snowProps.setProperty("controlColorLight", "240 240 240");
        snowProps.setProperty("controlColorDark", " 210 210 210");

        snowProps.setProperty("buttonColorLight", "255 255 255");
        snowProps.setProperty("buttonColorDark", "230 230 230");

        snowProps.setProperty("windowTitleForegroundColor", "48 64 96");
        snowProps.setProperty("windowTitleBackgroundColor", "200 215 240");
        snowProps.setProperty("windowTitleColorLight", "250 250 250");
        snowProps.setProperty("windowTitleColorDark", "230 230 230");
        snowProps.setProperty("windowBorderColor", "160 160 160");
        snowProps.setProperty("windowIconColor", "48 64 96");
        snowProps.setProperty("windowIconShadowColor", "255 255 255");
        snowProps.setProperty("windowIconRolloverColor", "200 0 0");

        snowProps.setProperty("windowInactiveTitleForegroundColor", "48 64 96");
        snowProps.setProperty("windowInactiveTitleBackgroundColor", "240 240 240");
        snowProps.setProperty("windowInactiveTitleColorLight", "250 250 250");
        snowProps.setProperty("windowInactiveTitleColorDark", "230 230 230");
        snowProps.setProperty("windowInactiveBorderColor", "180 180 180");

        snowProps.setProperty("menuForegroundColor", "0 0 0");
        snowProps.setProperty("menuBackgroundColor", "250 250 250");
        snowProps.setProperty("menuColorLight", "250 250 250");
        snowProps.setProperty("menuColorDark", "230 230 230");
        snowProps.setProperty("menuSelectionForegroundColor", "0 0 0");
        snowProps.setProperty("menuSelectionBackgroundColor", "188 206 237");
        
        snowProps.setProperty("toolbarBackgroundColor", "64 64 64");
        snowProps.setProperty("toolbarColorLight", "250 250 250");
        snowProps.setProperty("toolbarColorDark", "230 230 230");
        
        snowProps.setProperty("tabSelectionForegroundColor", "0 0 0");
        snowProps.setProperty("desktopColor", "240 240 240");
        snowProps.setProperty("tooltipBackgroundColor", "242 246 254");

        String key = null;
        String value = null;
        Iterator iter = smallFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = smallFontProps.getProperty(key);
            rockSmallFontProps.setProperty(key, value);
            textileSmallFontProps.setProperty(key, value);
            snowSmallFontProps.setProperty(key, value);
            //...
        }

        iter = mediumFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = mediumFontProps.getProperty(key);
            rockMediumFontProps.setProperty(key, value);
            textileMediumFontProps.setProperty(key, value);
            snowMediumFontProps.setProperty(key, value);
            //...
        }

        iter = largeFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = largeFontProps.getProperty(key);
            rockLargeFontProps.setProperty(key, value);
            textileLargeFontProps.setProperty(key, value);
            snowLargeFontProps.setProperty(key, value);
            //..
        }

        iter = rockProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = rockProps.getProperty(key);
            rockSmallFontProps.setProperty(key, value);
            rockMediumFontProps.setProperty(key, value);
            rockLargeFontProps.setProperty(key, value);
        }
        iter = textileProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = textileProps.getProperty(key);
            textileSmallFontProps.setProperty(key, value);
            textileMediumFontProps.setProperty(key, value);
            textileLargeFontProps.setProperty(key, value);
        }
        iter = snowProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            value = snowProps.getProperty(key);
            snowSmallFontProps.setProperty(key, value);
            snowMediumFontProps.setProperty(key, value);
            snowLargeFontProps.setProperty(key, value);
        }

        //...

        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Medium-Font");
        themesList.add("Large-Font");

        themesList.add("Rock");
        themesList.add("Rock-Small-Font");
        themesList.add("Rock-Medium-Font");
        themesList.add("Rock-Large-Font");

        themesList.add("Textile");
        themesList.add("Textile-Small-Font");
        themesList.add("Textile-Medium-Font");
        themesList.add("Textile-Large-Font");

        themesList.add("Snow");
        themesList.add("Snow-Small-Font");
        themesList.add("Snow-Medium-Font");
        themesList.add("Snow-Large-Font");

        themesMap.put("Default", defaultProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Medium-Font", mediumFontProps);
        themesMap.put("Large-Font", largeFontProps);

        themesMap.put("Rock", rockProps);
        themesMap.put("Rock-Small-Font", rockSmallFontProps);
        themesMap.put("Rock-Medium-Font", rockMediumFontProps);
        themesMap.put("Rock-Large-Font", rockLargeFontProps);

        themesMap.put("Textile", textileProps);
        themesMap.put("Textile-Small-Font", textileSmallFontProps);
        themesMap.put("Textile-Medium-Font", textileMediumFontProps);
        themesMap.put("Textile-Large-Font", textileLargeFontProps);

        themesMap.put("Snow", snowProps);
        themesMap.put("Snow-Small-Font", snowSmallFontProps);
        themesMap.put("Snow-Medium-Font", snowMediumFontProps);
        themesMap.put("Snow-Large-Font", snowLargeFontProps);
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
            myTheme = new TextureDefaultTheme();
        }
        if ((myTheme != null) && (themesProps != null)) {
            TextureIcons.setUp();
            myTheme.setUpColor();
            myTheme.setProperties(themesProps);
            myTheme.setUpColorArrs();
            AbstractLookAndFeel.setTheme(myTheme);
            // Setup textures
            TextureUtils.setUpTextures();
        }
    }

    public static void setCurrentTheme(Properties themesProps) {
        setTheme(themesProps);
    }

    public String getName() {
        return "Texture";
    }

    public String getID() {
        return "Texture";
    }

    public String getDescription() {
        return "The Texture Look and Feel";
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public AbstractBorderFactory getBorderFactory() {
        return TextureBorderFactory.getInstance();
    }

    public AbstractIconFactory getIconFactory() {
        return TextureIconFactory.getInstance();
    }

    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new TextureDefaultTheme();
        }
        setTheme(myTheme);
        // Setup textures
        TextureUtils.setUpTextures();
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        table.put("SplitPane.dividerSize", new Integer(8));
        table.put("TabbedPane.tabAreaInsets", new InsetsUIResource(5, 5, 6, 5));
        table.put("ScrollBar.incrementButtonGap", new Integer(-1));
        table.put("ScrollBar.decrementButtonGap", new Integer(-1));
    }
    
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {
            // BaseLookAndFeel classes
            "TextFieldUI", BaseTextFieldUI.class.getName(),
            "TextAreaUI", BaseTextAreaUI.class.getName(),
            "EditorPaneUI", BaseEditorPaneUI.class.getName(),
            "PasswordFieldUI", BasePasswordFieldUI.class.getName(),
            "ToolTipUI", BaseToolTipUI.class.getName(),
            "TreeUI", BaseTreeUI.class.getName(),
            "TableUI", BaseTableUI.class.getName(),
            "TableHeaderUI", BaseTableHeaderUI.class.getName(),
            "ProgressBarUI", BaseProgressBarUI.class.getName(),
            "FileChooserUI", BaseFileChooserUI.class.getName(),
            "PopupMenuUI", BasePopupMenuUI.class.getName(),
            "DesktopPaneUI", BaseDesktopPaneUI.class.getName(),

            // TextureLookAndFeel classes
            "LabelUI", TextureLabelUI.class.getName(),
            "PanelUI", TexturePanelUI.class.getName(),
            "RadioButtonUI", TextureRadioButtonUI.class.getName(),
            "CheckBoxUI", TextureCheckBoxUI.class.getName(),
            "ButtonUI", TextureButtonUI.class.getName(),
            "ToggleButtonUI", TextureToggleButtonUI.class.getName(),
            "ComboBoxUI", TextureComboBoxUI.class.getName(),
            "MenuBarUI", TextureMenuBarUI.class.getName(),
            "MenuUI", TextureMenuUI.class.getName(),
            "MenuItemUI", TextureMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", TextureCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", TextureRadioButtonMenuItemUI.class.getName(),
            "PopupMenuSeparatorUI", TexturePopupMenuSeparatorUI.class.getName(),
            "SeparatorUI", TextureSeparatorUI.class.getName(),
            "TabbedPaneUI", TextureTabbedPaneUI.class.getName(),
            "SliderUI", TextureSliderUI.class.getName(),
            "SplitPaneUI", TextureSplitPaneUI.class.getName(),
            "ScrollPaneUI", TextureScrollPaneUI.class.getName(),
            "ScrollBarUI", TextureScrollBarUI.class.getName(),
            "ToolBarUI", TextureToolBarUI.class.getName(),
            "InternalFrameUI", TextureInternalFrameUI.class.getName(),
            "RootPaneUI", TextureRootPaneUI.class.getName(),};
        table.putDefaults(uiDefaults);
        if (JTattooUtilities.getJavaVersion() >= 1.5) {
            table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
            table.put("SpinnerUI", BaseSpinnerUI.class.getName());
        }
    }

}