/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public McWinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected LayoutManager createLayout() {
        return new MyTitlePaneLayout();
    }

    protected void createButtons() {
        iconButton = new BaseTitleButton(iconifyAction, ICONIFY, iconIcon, 1.0f);
        maxButton = new BaseTitleButton(maximizeAction, MAXIMIZE, maxIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
        setButtonIcons();
    }

    public void paintBorder(Graphics g) {
        if (JTattooUtilities.isActive(this)) {
            g.setColor(McWinLookAndFeel.getWindowBorderColor());
        } else {
            g.setColor(McWinLookAndFeel.getWindowInactiveBorderColor());
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isActive()) {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowTitleColorLight(), 40));
            JTattooUtilities.drawString(frame, g, title, x, y + 1);
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        } else {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowInactiveTitleColorLight(), 40));
            JTattooUtilities.drawString(frame, g, title, x, y + 1);
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        }
    }

//------------------------------------------------------------------------------
// inner classes
//------------------------------------------------------------------------------
    class MyTitlePaneLayout extends TitlePaneLayout {

        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            return minimumLayoutSize(c);
        }

        public Dimension minimumLayoutSize(Container c) {
            int width = 30;
            if (frame.isClosable()) {
                width += 21;
            }
            if (frame.isMaximizable()) {
                width += 16 + (frame.isClosable() ? 10 : 4);
            }
            if (frame.isIconifiable()) {
                width += 16 + (frame.isMaximizable() ? 2 : (frame.isClosable() ? 10 : 4));
            }
            FontMetrics fm = getFontMetrics(getFont());
            String frameTitle = frame.getTitle();
            int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
            int title_length = frameTitle != null ? frameTitle.length() : 0;

            if (title_length > 2) {
                int subtitle_w = fm.stringWidth(frame.getTitle().substring(0, 2) + "...");
                width += (title_w < subtitle_w) ? title_w : subtitle_w;
            } else {
                width += title_w;
            }

            int height = paletteTitleHeight;
            if (!isPalette) {
                int fontHeight = fm.getHeight() + 7;
                Icon icon = frame.getFrameIcon();
                int iconHeight = 0;
                if (icon != null) {
                    iconHeight = Math.min(icon.getIconHeight(), 18);
                }
                iconHeight += 5;
                height = Math.max(fontHeight, iconHeight);
            }
            return new Dimension(width, height);
        }

        public void layoutContainer(Container c) {
            boolean leftToRight = JTattooUtilities.isLeftToRight(frame);

            int w = getWidth();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int spacing = 4;
            int buttonHeight = 26;
            if (h <= 23) {
                spacing = 2;
                buttonHeight = 18;
            } else if (h <= 26) {
                spacing = 3;
                buttonHeight = 22;
            } else {
                spacing = 4;
                buttonHeight = 26;
            }
            int buttonWidth = buttonHeight;

            int x = leftToRight ? w - spacing : 0;
            int y = Math.max(0, ((h - buttonHeight) / 2) - 1);

            if (frame.isClosable()) {
                x += leftToRight ? -buttonWidth : spacing;
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            if (frame.isMaximizable() && !isPalette) {
                x += leftToRight ? -spacing - buttonWidth : spacing;
                maxButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            if (frame.isIconifiable() && !isPalette) {
                x += leftToRight ? -spacing - buttonWidth : spacing;
                iconButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            buttonsWidth = leftToRight ? w - x : x;
        }
    } // end class MyTitlePaneLayout

}
