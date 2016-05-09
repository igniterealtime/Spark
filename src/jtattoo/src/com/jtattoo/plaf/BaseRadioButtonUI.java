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
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;

/**
 * @author Michael Hagen
 */
public class BaseRadioButtonUI extends BasicRadioButtonUI {

    private static BaseRadioButtonUI radioButtonUI = null;
    /* These Dimensions/Rectangles are allocated once for all
     * RadioButtonUI.paint() calls.  Re-using rectangles
     * rather than allocating them in each paint call substantially
     * reduced the time it took paint to run.  Obviously, this
     * method can't be re-entered.
     */
    protected static Dimension size = new Dimension();
    protected static Rectangle viewRect = new Rectangle();
    protected static Rectangle iconRect = new Rectangle();
    protected static Rectangle textRect = new Rectangle();

    public static ComponentUI createUI(JComponent c) {
        if (radioButtonUI == null) {
            radioButtonUI = new BaseRadioButtonUI();
        }
        return radioButtonUI;
    }

    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setRolloverEnabled(true);
        icon = UIManager.getIcon("RadioButton.icon");
    }

    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();

        Insets i = c.getInsets();
        size = b.getSize(size);
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = size.width - (i.right + viewRect.x);
        viewRect.height = size.height - (i.bottom + viewRect.y);
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        Icon altIcon = b.getIcon();
        int iconTextGap = getDefaultTextIconGap(b);
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            iconTextGap = b.getIconTextGap();
        }
        String text = SwingUtilities.layoutCompoundLabel(
                c,
                fm,
                b.getText(),
                altIcon != null ? altIcon : getDefaultIcon(),
                b.getVerticalAlignment(),
                b.getHorizontalAlignment(),
                b.getVerticalTextPosition(),
                b.getHorizontalTextPosition(),
                viewRect,
                iconRect,
                textRect,
                iconTextGap);

        // fill background
        if (c.isOpaque()) {
            paintBackground(g, c);
        }

        paintIcon(g, c, iconRect);

        if (text != null) {
            paintText(g, c, text, textRect);
        }

        if (b.hasFocus() && b.isFocusPainted() && (textRect.width > 0) && (textRect.height > 0)) {
            paintFocus(g, textRect, size);
        }
    }

    protected void paintBackground(Graphics g, JComponent c) {
        g.setColor(c.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        Icon ico = null;
        if (!model.isEnabled()) {
            if (b.isSelected()) {
                ico = b.getDisabledSelectedIcon();
            } else {
                ico = b.getDisabledIcon();
            }
        } else {
            if (model.isPressed()) {
                ico = b.getPressedIcon();
            } else {
                if (model.isRollover()) {
                    if (b.isSelected()) {
                        ico = b.getRolloverSelectedIcon();
                    } else {
                        ico = b.getRolloverIcon();
                    }
                } else {
                    if (b.isSelected()) {
                        ico = b.getSelectedIcon();
                    } else {
                        ico = b.getIcon();
                    }
                }
            }
        }

        if (ico != null) {
            ico.paintIcon(c, g, iconRect.x, iconRect.y - 1);
        } else {
            if (b.getIcon() != null) {
                b.getIcon().paintIcon(c, g, iconRect.x, iconRect.y - 1);
            } else {
                getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y - 1);
            }
        }
    }

    protected void paintText(Graphics g, JComponent c, String text, Rectangle textRect) {
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
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
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            Font f = c.getFont();
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            int mnemIndex = -1;
            if (JTattooUtilities.getJavaVersion() >= 1.4) {
                mnemIndex = b.getDisplayedMnemonicIndex();
            } else {
                mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(b.getText(), model.getMnemonic());
            }
            if (model.isEnabled()) {
                g.setColor(b.getForeground());
                JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
            } else {
                g.setColor(Color.white);
                JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x + 1, textRect.y + 1 + fm.getAscent());
                g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
                JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
            }
        }
    }

    protected void paintFocus(Graphics g, Rectangle t, Dimension d) {
        g.setColor(AbstractLookAndFeel.getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, t.x, t.y - 1, t.width + 1, t.height + 1);
    }
}
