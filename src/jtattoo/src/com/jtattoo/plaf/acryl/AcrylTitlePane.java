/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.acryl;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class AcrylTitlePane extends BaseTitlePane {

    public AcrylTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    public LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    protected int getHorSpacing() {
        return 1;
    }

    protected int getVerSpacing() {
        return 3;
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(AcrylLookAndFeel.getWindowBorderColor());
        } else {
            g.setColor(AcrylLookAndFeel.getWindowInactiveBorderColor());
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

            g.setColor(ColorHelper.darker(AcrylLookAndFeel.getWindowTitleColorDark(), 30));
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset - 1, yOffset - 1);
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset - 1, yOffset + 1);
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset + 1, yOffset - 1);
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset + 1, yOffset + 1);
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset + 1, yOffset + 1);

            g.setColor(foreground);
            JTattooUtilities.drawString(rootPane, g, frameTitle, xOffset, yOffset);
            paintText(g, xOffset, yOffset, frameTitle);
        }
    }

//-----------------------------------------------------------------------------------------------    
    protected class TitlePaneLayout implements LayoutManager {

        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            int height = computeHeight();
            return new Dimension(height, height);
        }

        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        }

        protected int computeHeight() {
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
            return fm.getHeight() + 6;
        }

        public void layoutContainer(Container c) {
            boolean leftToRight = isLeftToRight();

            int spacing = getHorSpacing();
            int w = getWidth();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int buttonHeight = h - getVerSpacing();
            int buttonWidth = buttonHeight + 10;

            int x = leftToRight ? spacing : w - buttonWidth - spacing;
            int y = Math.max(0, ((h - buttonHeight) / 2) - 1);
            int cpx = 0;
            int cpy = 0;
            int cpw = getWidth();
            int cph = getHeight();

            if (menuBar != null) {
                int mw = menuBar.getPreferredSize().width;
                int mh = menuBar.getPreferredSize().height;
                if (leftToRight) {
                    cpx = 4 + mw;
                    menuBar.setBounds(2, (h - mh) / 2, mw, mh);
                } else {
                    menuBar.setBounds(getWidth() - mw, (h - mh) / 2, mw, mh);
                }
                cpw -= 4 + mw;
            }

            x = leftToRight ? w - spacing : 0;
            if (closeButton != null) {
                x += leftToRight ? -buttonWidth : spacing;
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            if ((maxButton != null) && (maxButton.getParent() != null)) {
                if (DecorationHelper.isFrameStateSupported(Toolkit.getDefaultToolkit(), BaseRootPaneUI.MAXIMIZED_BOTH)) {
                    x += leftToRight ? -spacing - buttonWidth : spacing;
                    maxButton.setBounds(x, y, buttonWidth, buttonHeight);
                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }

            if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
                x += leftToRight ? -spacing - buttonWidth : spacing;
                iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }
            buttonsWidth = leftToRight ? w - x : x;
            if (customTitlePanel != null) {
                if (!leftToRight) {
                    cpx += buttonsWidth;
                }
                cpw -= buttonsWidth;
                Graphics g = getGraphics();
                if (g != null) {
                    FontMetrics fm = g.getFontMetrics();
                    int tw = SwingUtilities.computeStringWidth(fm, JTattooUtilities.getClippedText(getTitle(), fm, cpw));
                    if (leftToRight) {
                        cpx += tw;
                    }
                    cpw -= tw;
                }
                customTitlePanel.setBounds(cpx, cpy, cpw, cph);
            }
        }
    }
}
