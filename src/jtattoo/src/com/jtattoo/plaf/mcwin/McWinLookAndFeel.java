/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.util.*;
import javax.swing.*;
import com.jtattoo.plaf.*;
import javax.swing.plaf.InsetsUIResource;

/**
 * @author Michael Hagen
 */
public class McWinLookAndFeel extends AbstractLookAndFeel {

    private static McWinDefaultTheme myTheme = null;

    private static final ArrayList themesList = new ArrayList();
    private static final HashMap themesMap = new HashMap();
    private static final Properties defaultProps = new Properties();
    private static final Properties smallFontProps = new Properties();
    private static final Properties largeFontProps = new Properties();
    private static final Properties giantFontProps = new Properties();
    private static final Properties modernProps = new Properties();
    private static final Properties modernSmallFontProps = new Properties();
    private static final Properties modernLargeFontProps = new Properties();
    private static final Properties modernGiantFontProps = new Properties();
    private static final Properties pinkProps = new Properties();
    private static final Properties pinkSmallFontProps = new Properties();
    private static final Properties pinkLargeFontProps = new Properties();
    private static final Properties pinkGiantFontProps = new Properties();

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

        modernProps.setProperty("brightMode", "on");
        modernProps.setProperty("menuOpaque", "on");
        modernProps.setProperty("backgroundPattern", "off");
        modernProps.setProperty("drawSquareButtons", "on");
        modernProps.setProperty("windowTitleForegroundColor", "54 68 84");
        modernProps.setProperty("windowTitleColorLight", "201 210 220");
        modernProps.setProperty("windowTitleColorDark", "170 185 202");
        modernProps.setProperty("windowBorderColor", "150 168 188");
        modernProps.setProperty("windowInactiveTitleForegroundColor", "67 84 103");
        modernProps.setProperty("windowInactiveTitleColorLight", "218 224 231");
        modernProps.setProperty("windowInactiveTitleColorDark", "194 205 216");
        modernProps.setProperty("windowInactiveBorderColor", "172 186 202");
        modernProps.setProperty("backgroundColor", "220 226 233");
        modernProps.setProperty("selectionBackgroundColor", "200 215 240");
        modernProps.setProperty("rolloverColor", "208 208 145");
        modernProps.setProperty("rolloverColorLight", "248 248 184");
        modernProps.setProperty("rolloverColorDark", "220 220 172");
        modernProps.setProperty("controlColorLight", "194 204 216");
        modernProps.setProperty("controlColorDark", "160 177 197");
        modernProps.setProperty("menuBackgroundColor", "242 244 247");
        modernProps.setProperty("menuColorLight", "242 244 247");
        modernProps.setProperty("menuColorDark", "220 226 233");
        modernProps.setProperty("menuSelectionBackgroundColor", "220 220 172");
        modernProps.setProperty("toolbarBackgroundColor", "220 226 233");

        pinkProps.setProperty("backgroundColorLight", "248 244 248");
        pinkProps.setProperty("backgroundColorDark", "255 255 255");
        pinkProps.setProperty("focusCellColor", "160 120 160");
        pinkProps.setProperty("selectionBackgroundColor", "248 202 248");
        pinkProps.setProperty("rolloverColor", "240 145 240");
        pinkProps.setProperty("controlColorLight", "255 220 255");
        pinkProps.setProperty("controlColorDark", "248 140 248");
        pinkProps.setProperty("rolloverColorLight", "240 180 240");
        pinkProps.setProperty("rolloverColorDark", "232 120 232");
        pinkProps.setProperty("windowTitleForegroundColor", "0 0 0");
        pinkProps.setProperty("windowTitleBackgroundColor", "248 180 248");
        pinkProps.setProperty("windowTitleColorLight", "248 180 248");
        pinkProps.setProperty("windowTitleColorDark", "200 120 200");
        pinkProps.setProperty("windowBorderColor", "200 120 200");
        pinkProps.setProperty("menuSelectionForegroundColor", "0 0 0");
        pinkProps.setProperty("menuSelectionBackgroundColor", "248 202 248");
        pinkProps.setProperty("desktopColor", "255 255 255");

        String key = null;
        String value = null;
        Iterator iter = smallFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = smallFontProps.getProperty(key);
            modernSmallFontProps.setProperty(key, value);
            pinkSmallFontProps.setProperty(key, value);
        }
        iter = largeFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = largeFontProps.getProperty(key);
            modernLargeFontProps.setProperty(key, value);
            pinkLargeFontProps.setProperty(key, value);
        }
        iter = giantFontProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = giantFontProps.getProperty(key);
            modernGiantFontProps.setProperty(key, value);
            pinkGiantFontProps.setProperty(key, value);
        }

        iter = modernProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = modernProps.getProperty(key);
            modernSmallFontProps.setProperty(key, value);
            modernLargeFontProps.setProperty(key, value);
            modernGiantFontProps.setProperty(key, value);
        }
        iter = pinkProps.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            value = pinkProps.getProperty(key);
            pinkSmallFontProps.setProperty(key, value);
            pinkLargeFontProps.setProperty(key, value);
            pinkGiantFontProps.setProperty(key, value);
        }

        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Large-Font");
        themesList.add("Giant-Font");

        themesList.add("Modern");
        themesList.add("Modern-Small-Font");
        themesList.add("Modern-Large-Font");
        themesList.add("Modern-Giant-Font");

        themesList.add("Pink");
        themesList.add("Pink-Small-Font");
        themesList.add("Pink-Large-Font");
        themesList.add("Pink-Giant-Font");

        themesMap.put("Default", defaultProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Large-Font", largeFontProps);
        themesMap.put("Giant-Font", giantFontProps);

        themesMap.put("Modern", modernProps);
        themesMap.put("Modern-Small-Font", modernSmallFontProps);
        themesMap.put("Modern-Large-Font", modernLargeFontProps);
        themesMap.put("Modern-Giant-Font", modernGiantFontProps);

        themesMap.put("Pink", pinkProps);
        themesMap.put("Pink-Small-Font", pinkSmallFontProps);
        themesMap.put("Pink-Large-Font", pinkLargeFontProps);
        themesMap.put("Pink-Giant-Font", pinkGiantFontProps);
    }

    public static java.util.List getThemes() {
        return themesList;
    }

    public static Properties getThemeProperties(String name) {
        return ((Properties) themesMap.get(name));
    }

    public static void setTheme(String name) {
        if (myTheme != null) {
            myTheme.setInternalName(name);
        }
        setTheme((Properties) themesMap.get(name));
    }

    public static void setTheme(String name, String licenseKey, String logoString) {
        Properties props = (Properties) themesMap.get(name);
        props.put("licenseKey", licenseKey);
        props.put("logoString", logoString);
        if (myTheme != null) {
            myTheme.setInternalName(name);
        }
        setTheme(props);
    }

    public static void setTheme(Properties themesProps) {
        if (myTheme == null) {
            myTheme = new McWinDefaultTheme();
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
        return "McWin";
    }

    public String getID() {
        return "McWin";
    }

    public String getDescription() {
        return "The McWin Look and Feel";
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public AbstractBorderFactory getBorderFactory() {
        return McWinBorderFactory.getInstance();
    }

    public AbstractIconFactory getIconFactory() {
        return McWinIconFactory.getInstance();
    }

    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new McWinDefaultTheme();
        }
        setTheme(myTheme);
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {
            // BaseLookAndFeel classes
            "LabelUI", BaseLabelUI.class.getName(),
            "SeparatorUI", BaseSeparatorUI.class.getName(),
            "TextFieldUI", BaseTextFieldUI.class.getName(),
            "TextAreaUI", BaseTextAreaUI.class.getName(),
            "EditorPaneUI", BaseEditorPaneUI.class.getName(),
            "PasswordFieldUI", BasePasswordFieldUI.class.getName(),
            "ToolTipUI", BaseToolTipUI.class.getName(),
            "TreeUI", BaseTreeUI.class.getName(),
            "TableUI", BaseTableUI.class.getName(),
            "TableHeaderUI", BaseTableHeaderUI.class.getName(),
            "ScrollBarUI", BaseScrollBarUI.class.getName(),
            "ProgressBarUI", BaseProgressBarUI.class.getName(),
            "FileChooserUI", BaseFileChooserUI.class.getName(),
            "MenuUI", BaseMenuUI.class.getName(),
            "PopupMenuUI", BasePopupMenuUI.class.getName(),
            "MenuItemUI", BaseMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", BaseCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", BaseRadioButtonMenuItemUI.class.getName(),
            "PopupMenuSeparatorUI", BaseSeparatorUI.class.getName(),
            // McWinLookAndFeel classes
            "CheckBoxUI", McWinCheckBoxUI.class.getName(),
            "RadioButtonUI", McWinRadioButtonUI.class.getName(),
            "ButtonUI", McWinButtonUI.class.getName(),
            "ToggleButtonUI", McWinToggleButtonUI.class.getName(),
            "ComboBoxUI", McWinComboBoxUI.class.getName(),
            "SliderUI", McWinSliderUI.class.getName(),
            "PanelUI", McWinPanelUI.class.getName(),
            "ScrollPaneUI", McWinScrollPaneUI.class.getName(),
            "TabbedPaneUI", McWinTabbedPaneUI.class.getName(),
            "ToolBarUI", McWinToolBarUI.class.getName(),
            "MenuBarUI", McWinMenuBarUI.class.getName(),
            "SplitPaneUI", McWinSplitPaneUI.class.getName(),
            "InternalFrameUI", McWinInternalFrameUI.class.getName(),
            "RootPaneUI", McWinRootPaneUI.class.getName(),};
        table.putDefaults(uiDefaults);
        if (JTattooUtilities.getJavaVersion() >= 1.5) {
            table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
            table.put("SpinnerUI", BaseSpinnerUI.class.getName());
        }
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        table.put("SplitPane.centerOneTouchButtons", Boolean.FALSE);
        table.put("TabbedPane.tabAreaInsets", new InsetsUIResource(5, 5, 6, 5));
    }
}
