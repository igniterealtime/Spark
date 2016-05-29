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
 
package com.jtattoo.plaf.hifi;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;

/**
 * @author Michael Hagen
 */
public class HiFiTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiTabbedPaneUI();
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        Color SEP_COLORS[] = {
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 40),
            ColorHelper.brighter(AbstractLookAndFeel.getBackgroundColor(), 20),
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 20),
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 40),
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 60),
        };
        return SEP_COLORS;
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Color backColor = tabPane.getBackgroundAt(tabIndex);
        if (!(backColor instanceof UIResource)) {
            super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
            return;
        }
        g.setFont(font);
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            // html
            Graphics2D g2D = (Graphics2D) g;
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
            if (JTattooUtilities.getJavaVersion() >= 1.4) {
                mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
            }

            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            Color fc = tabPane.getForegroundAt(tabIndex);
            if (isSelected) {
                fc = AbstractLookAndFeel.getTheme().getTabSelectionForegroundColor();
            }
            if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
                fc = AbstractLookAndFeel.getTheme().getDisabledForegroundColor();
            }
            if (ColorHelper.getGrayValue(fc) > 128) {
                g2D.setColor(Color.black);
            } else {
                g2D.setColor(Color.white);
            }
            JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y + 1 + metrics.getAscent());
            g2D.setComposite(composite);
            g2D.setColor(fc);
            JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
        }
    }

}