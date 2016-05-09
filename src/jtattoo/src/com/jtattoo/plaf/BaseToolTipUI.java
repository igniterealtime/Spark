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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 * @author Michael Hagen, Daniel Raedel
 */
public class BaseToolTipUI extends MetalToolTipUI {

    private boolean fancyLayout = false;
    private ComponentListener popupWindowListener = null;

    public static ComponentUI createUI(JComponent c) {
        return new BaseToolTipUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        int borderSize = AbstractLookAndFeel.getTheme().getTooltipBorderSize();
        int shadowSize = AbstractLookAndFeel.getTheme().getTooltipShadowSize();
        fancyLayout = DecorationHelper.isTranslucentWindowSupported() && ToolTipManager.sharedInstance().isLightWeightPopupEnabled();
        if (fancyLayout) {
            c.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize + shadowSize, borderSize + shadowSize, borderSize + shadowSize));
            c.setOpaque(false);
            Container parent = c.getParent();
            if (parent instanceof JPanel) {
                ((JPanel) c.getParent()).setOpaque(false);
            }
        } else {
            c.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
        }
    }

    protected void installListeners(JComponent c) {
        super.installListeners(c);
        
        // We must set the popup window to opaque because it is cached and reused within the PopupFactory
        popupWindowListener = new ComponentAdapter() {

            public void componentHidden(ComponentEvent e) {
                Window window = (Window)e.getComponent();
                DecorationHelper.setTranslucentWindow(window, false);
                window.removeComponentListener(popupWindowListener);
            }
        };
    }

    public void paint(Graphics g, JComponent c) {
        Graphics2D g2D = (Graphics2D) g;
        Composite savedComposit = g2D.getComposite();
        Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int borderSize = AbstractLookAndFeel.getTheme().getTooltipBorderSize();
        int shadowSize = AbstractLookAndFeel.getTheme().getTooltipShadowSize();

        int w = c.getWidth();
        int h = c.getHeight();
        Color backColor = AbstractLookAndFeel.getTheme().getTooltipBackgroundColor();
        
        if (fancyLayout && shadowSize > 0) {
            Container parent = c.getParent();
            while (parent != null) {
                if ((parent.getClass().getName().indexOf("HeavyWeightWindow") > 0) && (parent instanceof Window)) {
                    // Make the popup transparent
                    Window window = (Window)parent;
                    // Add a component listener to revert this operation if popup is closed
                    window.addComponentListener(popupWindowListener);
                    DecorationHelper.setTranslucentWindow(window, true);
                    break;
                }
                parent = parent.getParent();
            }
            // draw the shadow
            g2D.setColor(AbstractLookAndFeel.getTheme().getShadowColor());
            float[] composites = {0.01f, 0.02f, 0.04f, 0.06f, 0.08f, 0.12f};
            int shadowOffset = AbstractLookAndFeel.getTheme().isTooltipCastShadow() ? shadowSize : 0;
            for (int i = 0; i < shadowSize; i++) {
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, composites[i >= composites.length ? composites.length - 1 : i]));
                g2D.fillRoundRect(i + shadowOffset, borderSize + i, w - (2 * i) - shadowOffset, h - borderSize - (2 * i), 12 - i, 12 - i);

            }
            g2D.setComposite(savedComposit);

            // Draw background with borders
            if (ColorHelper.getGrayValue(backColor) < 128) {
                g2D.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getBackgroundColor(), 20));
            } else {
                g2D.setColor(Color.white);

            }
            //g2D.fillRoundRect(shadowSize, 0, w - (2 * shadowSize) - 1, h - shadowSize - 1, 6, 6);
            g2D.fillRoundRect(shadowSize, 0, w - (2 * shadowSize) - 1, h - shadowSize - 1, shadowSize, shadowSize);
            g2D.setColor(ColorHelper.darker(backColor, 40));
            //g2D.drawRoundRect(shadowSize, 0, w - (2 * shadowSize) - 1, h - shadowSize - 1, 6, 6);
            g2D.drawRoundRect(shadowSize, 0, w - (2 * shadowSize) - 1, h - shadowSize - 1, shadowSize, shadowSize);
            g2D.setColor(ColorHelper.darker(backColor, 10));
            g2D.drawRect(borderSize + shadowSize - 1, borderSize - 1, w - (2 * borderSize) - (2 * shadowSize) + 1, h - (2 * borderSize) - shadowSize + 1);

            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
            // Draw the text. This must be done within an offscreen image because of a bug
            // in the jdk, wich causes ugly antialiased font rendering when background is
            // transparent and popup is heavy weight.
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D big = bi.createGraphics();
            big.setClip(0, 0, w, h);
            Paint savedPaint = big.getPaint();
            Color cHi;
            Color cLo;
            if (ColorHelper.getGrayValue(backColor) < 128) {
                cHi = ColorHelper.brighter(backColor, 10);
                cLo = ColorHelper.darker(backColor, 20);
            } else {
                cHi = ColorHelper.brighter(backColor, 40);
                cLo = ColorHelper.darker(backColor, 5);
            }
            big.setPaint(new GradientPaint(0, borderSize, cHi, 0, h - (2 * borderSize) - shadowSize, cLo));
            big.fillRect(borderSize + shadowSize, borderSize, w - (2 * borderSize) - (2 * shadowSize), h - (2 * borderSize) - shadowSize);

            big.setPaint(savedPaint);

            if (c instanceof JToolTip) {
                JToolTip tip = (JToolTip) c;
                if (tip.getComponent() != null && tip.getComponent().isEnabled()) {
                    c.setForeground(AbstractLookAndFeel.getTheme().getTooltipForegroundColor());
                } else {
                    c.setForeground(AbstractLookAndFeel.getTheme().getDisabledForegroundColor());
                }
            }
            super.paint(big, c);
            g2D.setClip(borderSize + shadowSize, borderSize, w - (2 * borderSize) - (2 * shadowSize), h - (2 * borderSize) - shadowSize);
            g2D.drawImage(bi, 0, 0, null);

        } else {
            // Draw background with borders
            if (ColorHelper.getGrayValue(backColor) < 128) {
                g2D.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getBackgroundColor(), 20));
            } else {
                g2D.setColor(Color.white);
            }
            g2D.fillRect(0, 0, w, h);
            g2D.setColor(ColorHelper.darker(backColor, 40));
            g2D.drawRect(0, 0, w - 1, h - 1);
            g2D.setColor(ColorHelper.darker(backColor, 10));
            g2D.drawRect(borderSize - 1, borderSize - 1, w - (2 * borderSize - 1), h - (2 * borderSize - 1));
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);

            Paint savedPaint = g2D.getPaint();
            Color cHi;
            Color cLo;
            if (ColorHelper.getGrayValue(backColor) < 128) {
                cHi = ColorHelper.brighter(backColor, 10);
                cLo = ColorHelper.darker(backColor, 20);
            } else {
                cHi = ColorHelper.brighter(backColor, 40);
                cLo = ColorHelper.darker(backColor, 5);
            }
            g2D.setPaint(new GradientPaint(0, borderSize, cHi, 0, h - (2 * borderSize), cLo));
            g2D.fillRect(borderSize, borderSize, w - (2 * borderSize), h - (2 * borderSize));
            g2D.setPaint(savedPaint);

            super.paint(g, c);
        }
    }
}
