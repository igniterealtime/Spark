/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AeroTabbedPaneUI extends BaseTabbedPaneUI {
    private Color sepColors[] = null;
    
    public static ComponentUI createUI(JComponent c) { 
        return new AeroTabbedPaneUI(); 
    }
    
    protected void installComponents() {
        simpleButtonBorder = true;
        super.installComponents();
    }
    
    protected Color[] getContentBorderColors(int tabPlacement) {
        if (sepColors == null) {
            sepColors = new Color[5];
            sepColors[0] = ColorHelper.brighter(AeroLookAndFeel.getControlColorDark(), 40);
            sepColors[1] = ColorHelper.brighter(AeroLookAndFeel.getControlColorLight(), 40);
            sepColors[2] = ColorHelper.brighter(AeroLookAndFeel.getControlColorLight(), 60);
            sepColors[3] = ColorHelper.brighter(AeroLookAndFeel.getControlColorLight(), 20);
            sepColors[4] = ColorHelper.brighter(AeroLookAndFeel.getControlColorDark(), 30);
        }
        return sepColors;
    }
    
    protected Font getTabFont(boolean isSelected) {
        if (isSelected)
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        else
            return super.getTabFont(isSelected);
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(AbstractLookAndFeel.getTabAreaBackgroundColor());
        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
        if (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.LEFT) {
            g.fillRect(x, y, tabAreaWidth, tabAreaHeight);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            g.fillRect(x, h - tabAreaHeight + 1, w, tabAreaHeight);
        } else {
            g.fillRect(w - tabAreaWidth + 1, y, tabAreaWidth, h);
        }
        super.paintContentBorder(g, tabPlacement, selectedIndex, x, y, w, h);
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        g.setFont(font);
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            // html
            Graphics2D g2D = (Graphics2D)g;
            Object savedRenderingHint = null;
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
            }
            v.paint(g, textRect);
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
            }
        } else {
            // plain text
            int mnemIndex = -1;
            if (JTattooUtilities.getJavaVersion() >= 1.4) 
                mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                if (isSelected ) {
                    Color titleColor = AbstractLookAndFeel.getWindowTitleForegroundColor();
                    if (ColorHelper.getGrayValue(titleColor) > 164)
                        g.setColor(Color.black);
                    else
                        g.setColor(Color.white);
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y + 1 + metrics.getAscent());
                    g.setColor(titleColor);
                } else {
                    g.setColor(tabPane.getForegroundAt(tabIndex));
                }
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());

            } else { // tab disabled
                g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x - 1, textRect.y + metrics.getAscent() - 1);
            }
        }
    }
    
}