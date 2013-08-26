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
import java.awt.*;
import java.awt.geom.*;
import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;

/**
 * @author Michael Hagen
 */
public class TextureTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new TextureTabbedPaneUI();
    }

    protected void installComponents() {
        simpleButtonBorder = true;
        super.installComponents();
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        Color c = AbstractLookAndFeel.getTheme().getSelectionBackgroundColorDark();
        return new Color[] { getLoBorderColor(0), c, c, c, ColorHelper.darker(c, 10) };
    }

    protected Color getLoBorderColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex() 
                && tabPane.getBackgroundAt(tabIndex) instanceof ColorUIResource
                && AbstractLookAndFeel.getTheme().isDarkTexture()) {
            return ColorHelper.darker(super.getLoBorderColor(tabIndex), 20);
        }
        return AbstractLookAndFeel.getFrameColor();
    }

    protected Font getTabFont(boolean isSelected) {
        if (isSelected) {
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        } else {
            return super.getTabFont(isSelected);
        }
    }

    protected int getTexture() {
        return TextureUtils.BACKGROUND_TEXTURE_TYPE;
    }

    protected int getSelectedTexture() {
        return TextureUtils.SELECTED_TEXTURE_TYPE;
    }

    protected int getUnSelectedTexture(int tabIndex) {
        if (tabIndex == rolloverIndex && tabPane.isEnabledAt(tabIndex)) {
            return TextureUtils.ROLLOVER_TEXTURE_TYPE;
        }
        return TextureUtils.ALTER_BACKGROUND_TEXTURE_TYPE;
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        int textureType = TextureUtils.getTextureType(tabPane);
        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
        // paint the background
        if (tabPane.isOpaque()) {
            int xt = tabPlacement == RIGHT ? w - tabAreaWidth : 0;
            int yt = tabPlacement == BOTTOM ? h - tabAreaHeight : 0;
            int wt = tabPlacement == TOP || tabPlacement == BOTTOM ? w : tabAreaWidth;
            int ht = tabPlacement == LEFT || tabPlacement == RIGHT ? h : tabAreaHeight;
            g.setColor(tabAreaBackground);
            g.fillRect(xt, yt, wt, ht);
        }
        if (isContentOpaque()) {
            int xt = tabPlacement == LEFT ? tabAreaWidth : 0;
            int yt = tabPlacement == TOP ? tabAreaHeight : 0;
            int wt = tabPlacement == LEFT || tabPlacement == RIGHT ? w - tabAreaWidth : w;
            int ht = tabPlacement == TOP || tabPlacement == BOTTOM ? h - tabAreaHeight : h;
            TextureUtils.fillComponent(g, tabPane, xt, yt, wt, ht, textureType);
        }
        
        int sepHeight = tabAreaInsets.bottom;
        if (sepHeight > 0) {
            Insets bi = new Insets(0, 0, 0, 0);
            if (tabPane.getBorder() != null) {
                bi = tabPane.getBorder().getBorderInsets(tabPane);
            }
            switch (tabPlacement) {
                case TOP: {
                    TextureUtils.fillComponent(g, tabPane, x, y + tabAreaHeight - sepHeight + bi.top, w, sepHeight, getSelectedTexture());
                    if (textureType == TextureUtils.MENUBAR_TEXTURE_TYPE) {
                        Graphics2D g2D = (Graphics2D) g;
                        Composite saveComposite = g2D.getComposite();
                        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                        g2D.setComposite(alpha);
                        g2D.setColor(Color.black);
                        g2D.drawLine(x, y, w, y);
                        g2D.drawLine(w, y, w, y + tabAreaHeight - sepHeight);
                        alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
                        g2D.setComposite(alpha);
                        g2D.setColor(Color.white);
                        g2D.drawLine(x, y + tabAreaHeight - sepHeight, w, y + tabAreaHeight - sepHeight);
                        alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
                        g2D.setComposite(alpha);
                        g2D.drawLine(x, y + 1, x, y + tabAreaHeight - sepHeight - 1);
                        g2D.setComposite(saveComposite);
                    }
                    break;
                }
                case LEFT: {
                    TextureUtils.fillComponent(g, tabPane, x + tabAreaWidth - sepHeight + bi.left, y, sepHeight, h, getSelectedTexture());
                    break;
                }
                case BOTTOM: {
                    TextureUtils.fillComponent(g, tabPane, x, y + h - tabAreaHeight - bi.bottom, w, sepHeight, getSelectedTexture());
                    break;
                }
                case RIGHT: {
                    TextureUtils.fillComponent(g, tabPane, x + w - tabAreaWidth - bi.right, y, sepHeight, h, getSelectedTexture());
                    break;
                }
            }
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Color backColor = tabPane.getBackgroundAt(tabIndex);
        if (!(backColor instanceof UIResource) || !AbstractLookAndFeel.getTheme().isDarkTexture()) {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            return;
        }
        if (isTabOpaque() || isSelected) {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            Shape savedClip = g.getClip();
            Area orgClipArea = new Area(savedClip);
            int d = 2 * GAP;
            switch (tabPlacement) {
                case TOP:
                default:
                    if (isSelected) {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y, w , h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y, w, h + 1));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        TextureUtils.fillRect(g, tabPane, x, y, w, h + 4, getSelectedTexture());
                        g2D.setClip(savedClip);
                    } else {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y, w, h));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        TextureUtils.fillRect(g, tabPane, x, y, w, h + 4, getUnSelectedTexture(tabIndex));
                        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                        g2D.setComposite(alpha);
                        Color colors[] = AbstractLookAndFeel.getTheme().getButtonColors();
                        JTattooUtilities.fillHorGradient(g, colors, x, y, w, h + 4);
                        g2D.setComposite(savedComposite);
                        g2D.setClip(savedClip);
                    }
                    break;
                case LEFT:
                    if (isSelected) {
                        TextureUtils.fillComponent(g, tabPane, x + 1, y + 1, w, h - 1, getSelectedTexture());
                    } else {
                        TextureUtils.fillComponent(g, tabPane, x + 1, y + 1, w - 1, h - 1, getUnSelectedTexture(tabIndex));
                    }
                    break;
                case BOTTOM:
                    if (isSelected) {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y - 4, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y - 1, w, h + 1));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        TextureUtils.fillRect(g, tabPane, x, y - 4, w, h + 4, getSelectedTexture());
                        g2D.setClip(savedClip);
                    } else {
                        Area clipArea = new Area(new RoundRectangle2D.Double(x, y - 4, w, h + 4, d, d));
                        Area rectArea = new Area(new Rectangle2D.Double(x, y, w, h));
                        clipArea.intersect(rectArea);
                        clipArea.intersect(orgClipArea);
                        g2D.setClip(clipArea);
                        TextureUtils.fillRect(g, tabPane, x, y - 4, w, h + 4, getUnSelectedTexture(tabIndex));
                        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                        g2D.setComposite(alpha);
                        Color colors[] = AbstractLookAndFeel.getTheme().getButtonColors();
                        JTattooUtilities.fillHorGradient(g, colors, x, y - 4, w, h + 4);
                        g2D.setComposite(savedComposite);
                        g2D.setClip(savedClip);
                    }
                    break;
                case RIGHT:
                    if (isSelected) {
                        TextureUtils.fillComponent(g, tabPane, x, y + 1, w, h - 1, getSelectedTexture());
                    } else {
                        TextureUtils.fillComponent(g, tabPane, x, y + 1, w, h - 1, getUnSelectedTexture(tabIndex));
                    }
                    break;
            }
        }
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Color backColor = tabPane.getBackgroundAt(tabIndex);
        if (!(backColor instanceof UIResource) || !AbstractLookAndFeel.getTheme().isDarkTexture()) {
            super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
            return;
        }
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
            if (JTattooUtilities.getJavaVersion() >= 1.4) {
                mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
            }

            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                if (isSelected) {
                    Color titleColor = AbstractLookAndFeel.getTabSelectionForegroundColor();
                    if (ColorHelper.getGrayValue(titleColor) > 164) {
                        g.setColor(Color.black);
                    } else {
                        g.setColor(Color.white);
                    }
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y + 1 + metrics.getAscent());
                    g.setColor(titleColor);
                    Graphics2D g2D = (Graphics2D)g;
                    Shape savedClip = g2D.getClip();

                    Area clipArea = new Area(new Rectangle2D.Double(textRect.x, textRect.y, textRect.width, textRect.height / 2 + 1));
                    clipArea.intersect(new Area(savedClip));
                    g2D.setClip(clipArea);
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());

                    clipArea = new Area(new Rectangle2D.Double(textRect.x, textRect.y + (textRect.height / 2) + 1, textRect.width, textRect.height));
                    clipArea.intersect(new Area(savedClip));
                    g2D.setClip(clipArea);
                    g2D.setColor(ColorHelper.darker(titleColor, 20));
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                    g2D.setClip(savedClip);
                } else {
                    g.setColor(tabPane.getForegroundAt(tabIndex));
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                }

            } else { // tab disabled
                Graphics2D g2D = (Graphics2D) g;
                Composite savedComposite = g2D.getComposite();
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
                g2D.setComposite(alpha);
                g.setColor(Color.white);
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent() + 1);
                g2D.setComposite(savedComposite);
                g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
            }
        }
    }

}