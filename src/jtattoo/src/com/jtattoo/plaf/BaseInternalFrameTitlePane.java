/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

/**
 * This class is a modified copy of the javax.swing.plaf.metal.MetalInternalFrameTitlePane
 *
 * Class that manages a JLF title bar
 * <p>
 *
 * @author Steve Wilson
 * @author Brian Beck
 * @author Michael Hagen
 */
public class BaseInternalFrameTitlePane extends BasicInternalFrameTitlePane implements ActionListener {

    public static final String PAINT_ACTIVE = "paintActive";
    public static final String ICONIFY = "Iconify";
    public static final String MAXIMIZE = "Maximize";
    public static final String CLOSE = "Close";
    protected boolean isPalette = false;
    protected Icon paletteCloseIcon;
    protected int paletteTitleHeight;
    protected int buttonsWidth = 0;

    public BaseInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected void installDefaults() {
        super.installDefaults();
        setFont(UIManager.getFont("InternalFrame.font"));
        paletteTitleHeight = UIManager.getInt("InternalFrame.paletteTitleHeight");
        paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
        iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
        minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
        maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
        closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
    }

    protected void createButtons() {
        iconButton = new BaseTitleButton(iconifyAction, ICONIFY, iconIcon, 1.0f);
        maxButton = new BaseTitleButton(maximizeAction, MAXIMIZE, maxIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
        setButtonIcons();
    }

    protected void enableActions() {
        super.enableActions();
        maximizeAction.setEnabled(frame.isMaximizable());
    }

    protected void assembleSystemMenu() {
    }

    protected void addSystemMenuItems(JMenu systemMenu) {
    }

    protected void addSubComponents() {
        add(iconButton);
        add(maxButton);
        add(closeButton);
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new BasePropertyChangeHandler();
    }

    protected LayoutManager createLayout() {
        return new BaseTitlePaneLayout();
    }

    protected int getHorSpacing() {
        return 3;
    }

    protected int getVerSpacing() {
        return 4;
    }

    public void activateFrame() {
    }

    public void deactivateFrame() {
    }

    public boolean isActive() {
        return JTattooUtilities.isActive(this);
    }

    public boolean isPalette() {
        return isPalette;
    }

    public void setPalette(boolean b) {
        isPalette = b;
        if (isPalette) {
            closeButton.setIcon(paletteCloseIcon);
            if (frame.isMaximizable()) {
                remove(maxButton);
            }
            if (frame.isIconifiable()) {
                remove(iconButton);
            }
        } else {
            closeButton.setIcon(closeIcon);
            if (frame.isMaximizable()) {
                add(maxButton);
            }
            if (frame.isIconifiable()) {
                add(iconButton);
            }
        }
        revalidate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        AbstractButton button = (AbstractButton) e.getSource();
        button.getModel().setRollover(false);
    }

    public void paintPalette(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if (JTattooUtilities.isFrameActive(this)) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, width, height);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, width, height);
        }
    }

    public void paintBackground(Graphics g) {
        if (isActive()) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(), getHeight());
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, getWidth(), getHeight());
        }
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
        }
        JTattooUtilities.drawString(frame, g, title, x, y);
    }

    public void paintBorder(Graphics g) {
        Color borderColor = AbstractLookAndFeel.getWindowInactiveBorderColor();
        if (isActive() || isPalette) {
            borderColor = AbstractLookAndFeel.getWindowBorderColor();
        }
        JTattooUtilities.draw3DBorder(g, ColorHelper.brighter(borderColor, 20), ColorHelper.darker(borderColor, 10), 0, 0, getWidth(), getHeight());
    }

    public void paintComponent(Graphics g) {
        if (isPalette) {
            paintPalette(g);
            return;
        }

        paintBackground(g);

        boolean leftToRight = JTattooUtilities.isLeftToRight(frame);

        int width = getWidth();
        int height = getHeight();
        int xOffset = leftToRight ? 5 : width - 5;
        int titleWidth = width - buttonsWidth - 10;

        Icon icon = frame.getFrameIcon();
        if (icon != null) {
            if (!leftToRight) {
                xOffset -= icon.getIconWidth();
            }
            int iconY = ((height / 2) - (icon.getIconHeight() / 2));
            icon.paintIcon(frame, g, xOffset, iconY);
            xOffset += leftToRight ? icon.getIconWidth() + 5 : -5;
            titleWidth -= icon.getIconWidth() + 5;
        }

        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        String frameTitle = JTattooUtilities.getClippedText(frame.getTitle(), fm, titleWidth);
        int titleLength = fm.stringWidth(frameTitle);
        int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();
        if (!leftToRight) {
            xOffset -= titleLength;
        }
        paintText(g, xOffset, yOffset, frameTitle);
        paintBorder(g);
    }

    class BasePropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler {

        public void propertyChange(PropertyChangeEvent evt) {
            String prop = (String) evt.getPropertyName();
            if (prop.equals(JInternalFrame.IS_SELECTED_PROPERTY)) {
                Boolean b = (Boolean) evt.getNewValue();
                iconButton.putClientProperty(PAINT_ACTIVE, b);
                closeButton.putClientProperty(PAINT_ACTIVE, b);
                maxButton.putClientProperty(PAINT_ACTIVE, b);
                if (b.booleanValue()) {
                    activateFrame();
                } else {
                    deactivateFrame();
                }
                repaint();
            }
            super.propertyChange(evt);
        }
    }

//------------------------------------------------------------------------------
// inner classes
//------------------------------------------------------------------------------
    class BaseTitlePaneLayout extends TitlePaneLayout {

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

            int spacing = getHorSpacing();
            int w = getWidth();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int buttonHeight = h - getVerSpacing();
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
    } // end class BaseTitlePaneLayout
}