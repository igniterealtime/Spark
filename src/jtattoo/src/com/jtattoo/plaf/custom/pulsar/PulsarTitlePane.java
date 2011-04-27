/*
 * Copyright 2010 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author  Michael Hagen
 */
public class PulsarTitlePane extends BaseTitlePane {
    private static Color ALTERNATE_COLORS[] = new Color[] {
            new Color(44, 183, 236),
            new Color(50, 188, 241),
            new Color(56, 192, 245),
            new Color(62, 196, 251),
            new Color(66, 198, 253),
            new Color(67, 200, 255),
            new Color(70, 201, 255),
            new Color(72, 203, 255),
            new Color(68, 195, 254),
            new Color(68, 195, 254),
            new Color(59, 175, 242),
            new Color(57, 174, 241),
            new Color(57, 174, 241),
            new Color(56, 172, 239),
            new Color(52, 168, 236),
            new Color(47, 164, 231),
            new Color(41, 157, 224),
            new Color(41, 157, 224),
            new Color(33, 150, 218),
            new Color(24, 141, 210),
            new Color(17, 138, 205),
            new Color( 0, 124, 189),
        };

    private Image emptyImage = null;
    private Image titleImage = null;
    private PropertyChangeListener rootPaneListener = null;

    public PulsarTitlePane(JRootPane root, BaseRootPaneUI ui) { 
        super(root, ui);
        emptyImage = Toolkit.getDefaultToolkit().createImage(new byte[] { (byte)0 } );
    }

    protected void installListeners() {
        super.installListeners();
        if (window != null) {
            getRootPane().addPropertyChangeListener(createRootPaneListener());
        }
    }

    protected void uninstallListeners() {
        if (window != null) {
            getRootPane().removePropertyChangeListener(rootPaneListener);
        }
        super.uninstallListeners();
    }

    protected PropertyChangeListener createRootPaneListener() {
        rootPaneListener = new RootPaneListener();
        return rootPaneListener;
    }

    public LayoutManager createLayout() {
        return new MyTitlePaneLayout();
    }

    protected Image getFrameIconImage() {
        if (titleImage == null) {
            return super.getFrameIconImage();
        } else {
            return emptyImage;
        }
    }

    protected int getHorSpacing() {
        return 2;
    }
    
    protected int getVerSpacing() {
        return 0;
    }
    
    public void paintBorder(Graphics g) {
    }

    public void paintBackground(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        if (isActive()) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, w, h - 2);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, w, h - 2);
        }

        Shape savedClip = g.getClip();
        int dw = getHeight() * (getButtonCount() + 1);
        int ex = w - dw;
        int ey = -h;
        int ew = 80;
        int eh = 2 * h;
        Area clipArea = new Area(savedClip);
        Area ellipseArea = new Area(new Ellipse2D.Double(ex, ey, ew, eh));
        clipArea.intersect(ellipseArea);
        g2D.setClip(clipArea);
        JTattooUtilities.fillHorGradient(g, ALTERNATE_COLORS, Math.max(0, w - dw - ew), 0, w, h - 2);
        g2D.setClip(savedClip);
        JTattooUtilities.fillHorGradient(g, ALTERNATE_COLORS, Math.max(0, w - dw + (ew / 2)), 0, w, h - 2);

        Color fc1 = ColorHelper.darker(AbstractLookAndFeel.getWindowTitleColorDark(), 10);
        Color fc2 = ColorHelper.darker(AbstractLookAndFeel.getWindowTitleColorDark(), 30);
        g.setColor(fc1);
        g.drawLine(0, h - 2, w, h - 2);
        g.setColor(fc2);
        g.drawLine(0, h - 1, w, h - 1);

        fc1 = ColorHelper.darker(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 10);
        fc2 = ColorHelper.darker(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 30);
        Color fc3 = ColorHelper.brighter(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 30);
        g.setColor(fc1);
        g.drawLine(w - dw + (ew / 3) , h - 2, w, h - 2);
        g.setColor(fc2);
        g.drawLine(w - dw + (ew / 2), h - 1, w, h - 1);

        Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Composite savedComposite = g2D.getComposite();
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(fc3);
        g.drawArc(ex + 2, ey - 1, ew + 2, eh, 180, 90);
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.setColor(fc1);
        g.drawArc(ex + 1, ey, ew + 1, eh, 180, 90);
        g2D.setComposite(savedComposite);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        Graphics2D g2D = (Graphics2D)g;
        if (titleImage != null) {
            g2D.drawImage(titleImage, 0, getHeight() - titleImage.getHeight(null), null);
            x = titleImage.getWidth(null);
            y += 2;
        }
        Color fc = AbstractLookAndFeel.getWindowTitleForegroundColor();
        Color bc = AbstractLookAndFeel.getWindowTitleBackgroundColor();
        if (!isActive()) {
            fc = AbstractLookAndFeel.getWindowInactiveTitleForegroundColor();
            bc = AbstractLookAndFeel.getWindowInactiveTitleBackgroundColor();
        }
        if (fc.equals(Color.white)) {
            g2D.setColor(bc);
            JTattooUtilities.drawString(rootPane, g, title, x - 1, y-1);
            g2D.setColor(ColorHelper.darker(bc, 30));
            JTattooUtilities.drawString(rootPane, g, title, x + 1, y+1);
        }
        g.setColor(fc);
        JTattooUtilities.drawString(rootPane, g, title, x, y);
    }

    private int getButtonCount() {
        int buttonCount = 1;
        if (iconifyButton.getParent() != null) {
            buttonCount++;
        }
        if (maxButton.getParent() != null) {
            buttonCount++;
        }
        return buttonCount;
    }
//-----------------------------------------------------------------------------------------------
    protected class RootPaneListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();

            // Frame.state isn't currently bound.
            if ("titleImage".equals(name)) {
                titleImage = (Image)pce.getNewValue();
                revalidate();
                repaint();
            }
        }
    }

//-----------------------------------------------------------------------------------------------
    protected class MyTitlePaneLayout implements LayoutManager {

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
            int buttonHeight = h - getVerSpacing() - 2;
            int buttonWidth = buttonHeight;

            int x = leftToRight ? spacing : w - buttonWidth - spacing;
            int y = Math.max(0, ((h - buttonHeight) / 2) - 1);

            if (menuBar != null) {
                if (leftToRight) {
                    menuBar.setBounds(2, y, getHeight() - 2, getHeight() - 2);
                } else {
                    menuBar.setBounds(getWidth() - getHeight() - 2, y, getHeight() - 2, getHeight() - 2);
                }
            }

            x = leftToRight ? w - spacing  - 4 : 0;
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
        }
    }

}
