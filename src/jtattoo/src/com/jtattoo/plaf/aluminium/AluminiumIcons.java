/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AluminiumIcons extends BaseIcons {

    private static Icon iconIcon = null;
    private static Icon maxIcon = null;
    private static Icon minIcon = null;
    private static Icon closeIcon = null;
    private static Icon thumbHorIcon = null;
    private static Icon thumbHorIconRollover = null;
    private static Icon thumbVerIcon = null;
    private static Icon thumbVerIconRollover = null;
    private static Icon splitterUpArrowIcon = null;
    private static Icon splitterDownArrowIcon = null;
    private static Icon splitterLeftArrowIcon = null;
    private static Icon splitterRightArrowIcon = null;

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            iconIcon = new BaseIcons.IconSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(0, 0, 1, 0));
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            minIcon = new BaseIcons.MinSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(0, 0, 1, 0));
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            maxIcon = new BaseIcons.MaxSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(0, 0, 1, 0));
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
            Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
            Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
            closeIcon = new BaseIcons.CloseSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(0, 0, 1, 0));
        }
        return closeIcon;
    }

    public static Icon getThumbHorIcon() {
        if (thumbHorIcon == null) {
            thumbHorIcon = new LazyImageIcon("aluminium/icons/thumb_hor.gif");
        }
        return thumbHorIcon;
    }

    public static Icon getThumbHorIconRollover() {
        if (thumbHorIconRollover == null) {
            thumbHorIconRollover = new LazyImageIcon("aluminium/icons/thumb_hor_rollover.gif");
        }
        return thumbHorIconRollover;
    }

    public static Icon getThumbVerIcon() {
        if (thumbVerIcon == null) {
            thumbVerIcon = new LazyImageIcon("aluminium/icons/thumb_ver.gif");
        }
        return thumbVerIcon;
    }

    public static Icon getThumbVerIconRollover() {
        if (thumbVerIconRollover == null) {
            thumbVerIconRollover = new LazyImageIcon("aluminium/icons/thumb_ver_rollover.gif");
        }
        return thumbVerIconRollover;
    }

    public static Icon getSplitterUpArrowIcon() {
        if (splitterUpArrowIcon == null) {
            splitterUpArrowIcon = new LazyImageIcon("aluminium/icons/SplitterUpArrow.gif");
        }
        return splitterUpArrowIcon;
    }

    public static Icon getSplitterDownArrowIcon() {
        if (splitterDownArrowIcon == null) {
            splitterDownArrowIcon = new LazyImageIcon("aluminium/icons/SplitterDownArrow.gif");
        }
        return splitterDownArrowIcon;
    }

    public static Icon getSplitterLeftArrowIcon() {
        if (splitterLeftArrowIcon == null) {
            splitterLeftArrowIcon = new LazyImageIcon("aluminium/icons/SplitterLeftArrow.gif");
        }
        return splitterLeftArrowIcon;
    }

    public static Icon getSplitterRightArrowIcon() {
        if (splitterRightArrowIcon == null) {
            splitterRightArrowIcon = new LazyImageIcon("aluminium/icons/SplitterRightArrow.gif");
        }
        return splitterRightArrowIcon;
    }


}
