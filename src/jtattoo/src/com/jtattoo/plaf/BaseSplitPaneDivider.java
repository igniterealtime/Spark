/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

/**
 * @author Michael Hagen
 */
public class BaseSplitPaneDivider extends BasicSplitPaneDivider {

    protected boolean centerOneTouchButtons = true;

    public BaseSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            if (UIManager.get("SplitPane.centerOneTouchButtons") != null) {
                centerOneTouchButtons = UIManager.getBoolean("SplitPane.centerOneTouchButtons");
            }
        }
        setLayout(new MyDividerLayout());
    }

    public Border getBorder() {
        return null;
    }

    public void paint(Graphics g) {
        int width = getSize().width;
        int height = getSize().height;
        int dx = 0;
        int dy = 0;
        if ((width % 2) == 1) {
            dx = 1;
        }
        if ((height % 2) == 1) {
            dy = 1;
        }
        Color color = AbstractLookAndFeel.getBackgroundColor();
        Color cHi = ColorHelper.brighter(color, 25);
        Color cLo = ColorHelper.darker(color, 5);
        Color colors[] = ColorHelper.createColorArr(cHi, cLo, 10);

        if (UIManager.getLookAndFeel() instanceof AbstractLookAndFeel) {
            AbstractLookAndFeel lf = (AbstractLookAndFeel) UIManager.getLookAndFeel();
            if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                JTattooUtilities.fillVerGradient(g, colors, 0, 0, width, height);
                Icon horBumps = lf.getIconFactory().getSplitterHorBumpIcon();
                if ((horBumps != null) && (width > horBumps.getIconWidth())) {
                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
                    g2D.setComposite(alpha);

                    if (splitPane.isOneTouchExpandable() && centerOneTouchButtons) {
                        int centerY = height / 2;
                        int x = (width - horBumps.getIconWidth()) / 2 + dx;
                        int y = centerY - horBumps.getIconHeight() - 40;
                        horBumps.paintIcon(this, g, x, y);
                        y = centerY + 40;
                        horBumps.paintIcon(this, g, x, y);
                    } else {
                        int x = (width - horBumps.getIconWidth()) / 2 + dx;
                        int y = (height - horBumps.getIconHeight()) / 2;
                        horBumps.paintIcon(this, g, x, y);
                    }

                    g2D.setComposite(composite);
                }
            } else {
                JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
                Icon verBumps = lf.getIconFactory().getSplitterVerBumpIcon();
                if ((verBumps != null) && (height > verBumps.getIconHeight())) {
                    Graphics2D g2D = (Graphics2D) g;
                    Composite composite = g2D.getComposite();
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
                    g2D.setComposite(alpha);

                    if (splitPane.isOneTouchExpandable() && centerOneTouchButtons) {
                        int centerX = width / 2;
                        int x = centerX - verBumps.getIconWidth() - 40;
                        int y = (height - verBumps.getIconHeight()) / 2 + dy;
                        verBumps.paintIcon(this, g, x, y);
                        x = centerX + 40;
                        verBumps.paintIcon(this, g, x, y);
                    } else {
                        int x = (width - verBumps.getIconWidth()) / 2;
                        int y = (height - verBumps.getIconHeight()) / 2 + dy;
                        verBumps.paintIcon(this, g, x, y);
                    }

                    g2D.setComposite(composite);
                }
            }
        }
        paintComponents(g);
    }

    protected JButton createLeftOneTouchButton() {
        JButton b = new JButton() {

            public void paint(Graphics g) {
                Color color = getBackground();
                int w = getSize().width;
                int h = getSize().height;
                if (getModel().isPressed() && getModel().isArmed()) {
                    g.setColor(ColorHelper.darker(color, 40));
                    g.fillRect(0, 0, w, h);
                } else if (getModel().isRollover()) {
                    g.setColor(AbstractLookAndFeel.getTheme().getRolloverColor());
                    g.fillRect(0, 0, w, h);
                }
                AbstractLookAndFeel lf = (AbstractLookAndFeel) UIManager.getLookAndFeel();
                Icon icon = null;
                if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    icon = lf.getIconFactory().getSplitterLeftArrowIcon();
                } else {
                    icon = lf.getIconFactory().getSplitterUpArrowIcon();
                }
                int x = (w - icon.getIconWidth()) / 2;
                int y = (h - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g, x, y);
                if (getModel().isArmed()) {
                    if (getModel().isPressed()) {
                        JTattooUtilities.draw3DBorder(g, ColorHelper.darker(color, 30), ColorHelper.brighter(color, 80), 0, 0, w, h);
                    } else {
                        JTattooUtilities.draw3DBorder(g, ColorHelper.brighter(color, 80), ColorHelper.darker(color, 30), 0, 0, w, h);
                    }
                }
            }

            public boolean isFocusTraversable() {
                return false;
            }
        };
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setRolloverEnabled(true);
        return b;
    }

    protected JButton createRightOneTouchButton() {
        JButton b = new JButton() {

            public void paint(Graphics g) {
                Color color = getBackground();
                int w = getSize().width;
                int h = getSize().height;
                if (getModel().isPressed() && getModel().isArmed()) {
                    g.setColor(ColorHelper.darker(color, 40));
                    g.fillRect(0, 0, w, h);
                } else if (getModel().isRollover()) {
                    g.setColor(AbstractLookAndFeel.getTheme().getRolloverColor());
                    g.fillRect(0, 0, w, h);
                }
                AbstractLookAndFeel lf = (AbstractLookAndFeel) UIManager.getLookAndFeel();
                Icon icon = null;
                if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    icon = lf.getIconFactory().getSplitterRightArrowIcon();
                } else {
                    icon = lf.getIconFactory().getSplitterDownArrowIcon();
                }
                int x = (w - icon.getIconWidth()) / 2;
                int y = (h - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g, x, y);
                if (getModel().isArmed()) {
                    if (getModel().isPressed()) {
                        JTattooUtilities.draw3DBorder(g, ColorHelper.darker(color, 30), ColorHelper.brighter(color, 80), 0, 0, w, h);
                    } else {
                        JTattooUtilities.draw3DBorder(g, ColorHelper.brighter(color, 80), ColorHelper.darker(color, 30), 0, 0, w, h);
                    }
                }
            }

            public boolean isFocusTraversable() {
                return false;
            }
        };
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setRolloverEnabled(true);
        return b;
    }

    /**
     * Used to layout a <code>BasicSplitPaneDivider</code>.
     * Layout for the divider
     * involves appropriately moving the left/right buttons around.
     * <p>
     */
    protected class MyDividerLayout implements LayoutManager {

        public void layoutContainer(Container c) {
            if (leftButton != null && rightButton != null && c == BaseSplitPaneDivider.this) {
                if (splitPane.isOneTouchExpandable()) {
                    Insets insets = getInsets();
                    int blockSize = 11;
                    int xOffs = 0;
                    int yOffs = 0;
                    if (centerOneTouchButtons) {
                        blockSize = 13;
                        xOffs = ((getWidth() - (2 * blockSize)) / 2) - blockSize;
                        yOffs = ((getHeight() - (2 * blockSize)) / 2) - blockSize;
                    }

                    if (orientation == JSplitPane.VERTICAL_SPLIT) {
                        int extraX = (insets != null) ? insets.left : 0;
                        if (insets != null) {
                            blockSize -= (insets.top + insets.bottom);
                            blockSize = Math.max(blockSize, 0);
                        }
                        int y = (c.getSize().height - blockSize) / 2;
                        leftButton.setBounds(xOffs + extraX, y, blockSize * 2, blockSize);
                        rightButton.setBounds(xOffs + extraX + blockSize * 2 + 1, y, blockSize * 2, blockSize);
                    } else {
                        int extraY = (insets != null) ? insets.top : 0;
                        if (insets != null) {
                            blockSize -= (insets.left + insets.right);
                            blockSize = Math.max(blockSize, 0);
                        }
                        int x = (c.getSize().width - blockSize) / 2;
                        leftButton.setBounds(x, yOffs + extraY, blockSize, blockSize * 2);
                        rightButton.setBounds(x, yOffs + extraY + blockSize * 2 + 1, blockSize, blockSize * 2);
                    }
                } else {
                    leftButton.setBounds(-5, -5, 1, 1);
                    rightButton.setBounds(-5, -5, 1, 1);
                }
            }
        }

        public Dimension minimumLayoutSize(Container c) {
            // NOTE: This isn't really used, refer to
            // BasicSplitPaneDivider.getPreferredSize for the reason.
            // I leave it in hopes of having this used at some point.
            if (c != BaseSplitPaneDivider.this || splitPane == null) {
                return new Dimension(0, 0);
            }
            Dimension buttonMinSize = null;

            if (splitPane.isOneTouchExpandable() && leftButton != null) {
                buttonMinSize = leftButton.getMinimumSize();
            }

            Insets insets = getInsets();
            int width = getDividerSize();
            int height = width;

            if (orientation == JSplitPane.VERTICAL_SPLIT) {
                if (buttonMinSize != null) {
                    int size = buttonMinSize.height;
                    if (insets != null) {
                        size += insets.top + insets.bottom;
                    }
                    height = Math.max(height, size);
                }
                width = 1;
            } else {
                if (buttonMinSize != null) {
                    int size = buttonMinSize.width;
                    if (insets != null) {
                        size += insets.left + insets.right;
                    }
                    width = Math.max(width, size);
                }
                height = 1;
            }
            return new Dimension(width, height);
        }

        public Dimension preferredLayoutSize(Container c) {
            return minimumLayoutSize(c);
        }

        public void removeLayoutComponent(Component c) {
        }

        public void addLayoutComponent(String string, Component c) {
        }

    } // End of class MyDividerLayout
}
