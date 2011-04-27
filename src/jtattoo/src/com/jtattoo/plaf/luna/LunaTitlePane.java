/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class LunaTitlePane extends BaseTitlePane {

    public LunaTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    protected int getHorSpacing() {
        return 2;
    }

    protected int getVerSpacing() {
        return 5;
    }

    public void createButtons() {
        iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY, iconifyIcon, 1.0f);
        maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
        } else {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 40));
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintComponent(Graphics g) {
        if (getFrame() != null) {
            setState(DecorationHelper.getExtendedState(getFrame()));
        }

        paintBackground(g);

        boolean leftToRight = isLeftToRight();
        boolean isSelected = (window == null) ? true : JTattooUtilities.isWindowActive(window);

        Color foreground = AbstractLookAndFeel.getWindowInactiveTitleForegroundColor();
        if (isSelected) {
            foreground = AbstractLookAndFeel.getWindowTitleForegroundColor();
        }

        int width = getWidth();
        int height = getHeight();
        int titleWidth = width - buttonsWidth - 4;
        int xOffset = leftToRight ? 2 : width - 2;
        if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
            int mw = menuBar.getWidth() + 2;
            xOffset += leftToRight ? mw : -mw;
            titleWidth -= height;
        }

        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        String frameTitle = JTattooUtilities.getClippedText(getTitle(), fm, titleWidth);
        if (frameTitle != null) {
            int titleLength = fm.stringWidth(frameTitle);
            int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() - 1;
            if (!leftToRight) {
                xOffset -= titleLength;
            }

            if (isSelected) {
                g.setColor(LunaLookAndFeel.getTheme().getWindowBorderColor());
                JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset - 1, yOffset - 1);
                JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset + 1, yOffset + 1);
            }

            g.setColor(foreground);
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset, yOffset);
            paintText(g, xOffset, yOffset, frameTitle);
        }
    }
}
