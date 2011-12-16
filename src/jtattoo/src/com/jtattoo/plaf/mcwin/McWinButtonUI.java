/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.jtattoo.plaf.*;

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
        if (McWinLookAndFeel.getTheme().doDrawSquareButtons()
                || (((width < 64) || (height < 16)) && ((b.getText() == null) || b.getText().length() == 0))) {
            Color[] backColors = null;
            if (b.getBackground() instanceof ColorUIResource) {
                if (!model.isEnabled()) {
                    backColors = McWinLookAndFeel.getTheme().getDisabledColors();
                } else if (model.isPressed() && model.isArmed()) {
                    backColors = new Color[] { McWinLookAndFeel.getTheme().getBackgroundColor() };
                } else if (model.isRollover()) {
                    backColors = McWinLookAndFeel.getTheme().getRolloverColors();
                } else if (b.equals(b.getRootPane().getDefaultButton())) {
                    if (JTattooUtilities.isFrameActive(b)) {
                        if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                            backColors = McWinLookAndFeel.getTheme().getFocusColors();
                        } else {
                            if (McWinLookAndFeel.getTheme().isBrightMode()) {
                                backColors = new Color[McWinLookAndFeel.getTheme().getSelectedColors().length];
                                for (int i = 0; i < backColors.length; i++) {
                                    backColors[i] = ColorHelper.brighter(McWinLookAndFeel.getTheme().getSelectedColors()[i], 30);
                                }
                            } else {
                                backColors = McWinLookAndFeel.getTheme().getSelectedColors();
                            }
                        }
                    } else {
                        backColors = McWinLookAndFeel.getTheme().getButtonColors();
                    }
                } else {
                    if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                        backColors = McWinLookAndFeel.getTheme().getFocusColors();
                    } else {
                        backColors = McWinLookAndFeel.getTheme().getButtonColors();
                    }
                }
            } else {
                backColors = ColorHelper.createColorArr(ColorHelper.brighter(b.getBackground(), 20), ColorHelper.darker(b.getBackground(), 20), 20);
            }
            JTattooUtilities.fillHorGradient(g, backColors, 0, 0, width - 1, height - 1);
            Color frameColor = backColors[backColors.length / 2];
            g2D.setColor(ColorHelper.darker(frameColor, 25));
            g2D.drawRect(0, 0, width - 1, height - 1);
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2D.setComposite(alpha);
            g2D.setColor(Color.white);
            g2D.drawRect(1, 1, width - 3, height - 3);
        } else if (model.isPressed() && model.isArmed()) {
            int d = height - 2;
            Color color = McWinLookAndFeel.getTheme().getBackgroundColor();
            g2D.setColor(color);
            g2D.fillRoundRect(1, 1, width - 1, height - 1, d, d);
            g2D.setColor(ColorHelper.darker(color, 40));
            g2D.drawRoundRect(0, 0, width - 1, height - 1, d, d);
        } else {
            int d = height - 2;
            Color[] backColors = null;
            if (b.getBackground() instanceof ColorUIResource) {
                if (!model.isEnabled()) {
                    backColors = McWinLookAndFeel.getTheme().getDisabledColors();
                } else if (model.isRollover()) {
                    backColors = McWinLookAndFeel.getTheme().getRolloverColors();
                } else if (b.equals(b.getRootPane().getDefaultButton())) {
                    if (JTattooUtilities.isFrameActive(b)) {
                        backColors = McWinLookAndFeel.getTheme().getSelectedColors();
                    } else {
                        backColors = McWinLookAndFeel.getTheme().getButtonColors();
                    }
                } else {
                    backColors = McWinLookAndFeel.getTheme().getButtonColors();
                }
            } else {
                backColors = ColorHelper.createColorArr(ColorHelper.brighter(b.getBackground(), 20), ColorHelper.darker(b.getBackground(), 20), 20);
            }
            Color frameColor = backColors[backColors.length / 2];

            Shape savedClip = g.getClip();
            Area clipArea = new Area(savedClip);
            Area rectArea = new Area(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, d, d));
            rectArea.intersect(clipArea);
            g2D.setClip(rectArea);
            JTattooUtilities.fillHorGradient(g, backColors, 0, 0, width - 1, height - 1);
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
        if (McWinLookAndFeel.getTheme().doDrawSquareButtons()
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


