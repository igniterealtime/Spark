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
 
package com.jtattoo.plaf.mcwin;

import com.jtattoo.plaf.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * @author Michael Hagen
 */
public class McWinButtonUI extends BaseButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new McWinButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (b.getParent() instanceof JToolBar) {
            b.setContentAreaFilled(true);
        }
        if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
            return;
        }

        int width = b.getWidth();
        int height = b.getHeight();

        if (!(b.isBorderPainted() && (b.getBorder() instanceof UIResource))
                || (b.getParent() instanceof JToolBar)) {
            super.paintBackground(g, b);
            if ((b.getParent() instanceof JToolBar)) {
                g.setColor(Color.lightGray);
                g.drawRect(0, 0, width - 2, height - 1);
                g.setColor(AbstractLookAndFeel.getTheme().getToolbarBackgroundColor());
                g.drawLine(width - 1, 0, width - 1, height - 1);
            }
            return;
        }

        ButtonModel model = b.getModel();
        Graphics2D g2D = (Graphics2D) g;
        Composite composite = g2D.getComposite();
        Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color colors[] = AbstractLookAndFeel.getTheme().getButtonColors();
        if (b.isEnabled()) {
            Color background = b.getBackground();
            if (background instanceof ColorUIResource) {
                if (model.isPressed() && model.isArmed()) {
                    colors = AbstractLookAndFeel.getTheme().getPressedColors();
                } else if (b.isRolloverEnabled() && model.isRollover()) {
                    colors = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else {
                    if (b.getRootPane() != null && b.equals(b.getRootPane().getDefaultButton())) {
                        if (JTattooUtilities.isFrameActive(b)) {
                            if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                                colors = AbstractLookAndFeel.getTheme().getFocusColors();
                            } else {
                                if (AbstractLookAndFeel.getTheme().isBrightMode()) {
                                    colors = new Color[AbstractLookAndFeel.getTheme().getSelectedColors().length];
                                    for (int i = 0; i < colors.length; i++) {
                                        colors[i] = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getSelectedColors()[i], 30);
                                    }
                                } else {
                                    colors = AbstractLookAndFeel.getTheme().getSelectedColors();
                                }
                            }
                        }
                    } else {
                        if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                            colors = AbstractLookAndFeel.getTheme().getFocusColors();
                        }
                    }
                }
            } else { // backgound != ColorUIResource
                if (model.isPressed() && model.isArmed()) {
                    colors = ColorHelper.createColorArr(ColorHelper.darker(background, 30), ColorHelper.darker(background, 10), 20);
                } else {
                    if (b.isRolloverEnabled() && model.isRollover()) {
                        colors = ColorHelper.createColorArr(ColorHelper.brighter(background, 50), ColorHelper.brighter(background, 10), 20);
                    } else {
                        colors = ColorHelper.createColorArr(ColorHelper.brighter(background, 30), ColorHelper.darker(background, 10), 20);
                    }
                }
            }
        } else { // disabled
            colors = AbstractLookAndFeel.getTheme().getDisabledColors();
        }
        
        if (AbstractLookAndFeel.getTheme().doDrawSquareButtons()
                || (((width < 64) || (height < 16)) && ((b.getText() == null) || b.getText().length() == 0))) {
            JTattooUtilities.fillHorGradient(g, colors, 0, 0, width - 1, height - 1);
            Color frameColor = colors[colors.length / 2];
            g2D.setColor(ColorHelper.darker(frameColor, 25));
            g2D.drawRect(0, 0, width - 1, height - 1);
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2D.setComposite(alpha);
            g2D.setColor(Color.white);
            g2D.drawRect(1, 1, width - 3, height - 3);
        } else {
            int d = height - 2;
            Color frameColor = colors[colors.length / 2];

            Shape savedClip = g.getClip();
            Area clipArea = new Area(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, d, d));
            clipArea.intersect(new Area(savedClip));
            g2D.setClip(clipArea);
            JTattooUtilities.fillHorGradient(g, colors, 0, 0, width - 1, height - 1);
            g2D.setClip(savedClip);

            g2D.setColor(ColorHelper.darker(frameColor, 25));
            g2D.drawRoundRect(0, 0, width - 1, height - 1, d, d);

            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            g2D.setColor(Color.white);
            g2D.drawRoundRect(1, 1, width - 3, height - 3, d - 2, d - 2);

        }
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
        g2D.setComposite(composite);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        Graphics2D g2D = (Graphics2D) g;
        int width = b.getWidth();
        int height = b.getHeight();
        if (AbstractLookAndFeel.getTheme().doDrawSquareButtons()
                || !b.isContentAreaFilled()
                || ((width < 64) || (height < 16)) && ((b.getText() == null) || b.getText().length() == 0)) {
            g.setColor(AbstractLookAndFeel.getFocusColor());
            BasicGraphicsUtils.drawDashedRect(g, 4, 3, width - 8, height - 6);
        } else {
            Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(AbstractLookAndFeel.getFocusColor());
            int d = b.getHeight() - 4;
            g2D.drawRoundRect(2, 2, b.getWidth() - 5, b.getHeight() - 5, d, d);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
        }
    }

}


