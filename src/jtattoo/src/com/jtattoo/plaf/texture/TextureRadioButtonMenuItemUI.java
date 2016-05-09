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
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class TextureRadioButtonMenuItemUI extends BaseRadioButtonMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new TextureRadioButtonMenuItemUI();
    }

    protected void paintBackground(Graphics g, JComponent c, int x, int y, int w, int h) {
//        if (!AbstractLookAndFeel.getTheme().isDarkTexture()) {
//            super.paintBackground(g, c, x, y, w, h);
//            return;
//        }
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();
        if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
            TextureUtils.fillComponent(g, c, TextureUtils.ROLLOVER_TEXTURE_TYPE);
        } else {
            TextureUtils.fillComponent(g, c, TextureUtils.MENUBAR_TEXTURE_TYPE);
        }
    }

    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        if (!AbstractLookAndFeel.getTheme().isDarkTexture()) {
            super.paintText(g, menuItem, textRect, text);
            return;
        }
	ButtonModel model = menuItem.getModel();
        FontMetrics fm = menuItem.getFontMetrics(menuItem.getFont());
	int mnemIndex = menuItem.getDisplayedMnemonicIndex();
        if (!menuItem.isArmed()) {
            g.setColor(Color.black);
            JTattooUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent() - 1);
        }
	if (!model.isEnabled()) {
	    // *** paint the text disabled
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getDisabledForegroundColor(), 40));
	} else {
	    // *** paint the text normally
            if (menuItem.isArmed()) {
                g.setColor(AbstractLookAndFeel.getMenuSelectionForegroundColor());
            } else {
                g.setColor(AbstractLookAndFeel.getMenuForegroundColor());
            }
	}
        JTattooUtilities.drawStringUnderlineCharAt(menuItem, g,text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
    }

}
