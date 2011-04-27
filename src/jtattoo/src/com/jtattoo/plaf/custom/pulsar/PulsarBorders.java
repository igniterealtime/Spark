/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Michael Hagen
 */
public class PulsarBorders extends BaseBorders {
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

    private static Border buttonBorder;
    private static Border toggleButtonBorder;
    private static Border rolloverToolButtonBorder;
    private static Border menuItemBorder = null;
    private static Border popupMenuBorder = null;
    private static Border internalFrameBorder;
    
//------------------------------------------------------------------------------------
// Lazy access methods
//------------------------------------------------------------------------------------
    public static Border getButtonBorder() {
        if (buttonBorder == null)
            buttonBorder = new ButtonBorder();
        return buttonBorder;
    }
    
    public static Border getToggleButtonBorder() {
        if (toggleButtonBorder == null)
            toggleButtonBorder = new ToggleButtonBorder();
        return toggleButtonBorder;
    }
    
    public static Border getRolloverToolButtonBorder() {
        if (rolloverToolButtonBorder == null)
            rolloverToolButtonBorder = new RolloverToolButtonBorder();
        return rolloverToolButtonBorder;
    }
    
    public static Border getMenuItemBorder() {
        if (menuItemBorder == null) {
            menuItemBorder = new MenuItemBorder();
        }
        return menuItemBorder;
    }

    public static Border getPopupMenuBorder() {
        if (popupMenuBorder == null) {
            if (AbstractLookAndFeel.getTheme().isMenuOpaque()) {
                popupMenuBorder = new PopupMenuBorder();
            } else {
                popupMenuBorder = new PopupMenuShadowBorder();
            }
        }
        return popupMenuBorder;
    }

    public static Border getInternalFrameBorder() {
        if (internalFrameBorder == null)
            internalFrameBorder = new InternalFrameBorder();
        return internalFrameBorder;
    }
    
//------------------------------------------------------------------------------------
// Inner classes
//------------------------------------------------------------------------------------
    public static class ButtonBorder implements Border, UIResource {

        private static final Insets borderInsets = new Insets(3, 6, 3, 6);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Color frameColor = AbstractLookAndFeel.getTheme().getFrameColor();
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2D.setColor(Color.white);
            g2D.drawRoundRect(x, y, w - 1, h - 1, 6, 6);

            g2D.setColor(frameColor);
            g2D.drawRoundRect(x, y, w - 1, h - 2, 6, 6);

            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
        }

        public Insets getBorderInsets(Component c) {
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ButtonBorder
    
    public static class ToggleButtonBorder implements Border, UIResource {

        private static final Insets borderInsets = new Insets(3, 6, 4, 6);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Color frameColor = AbstractLookAndFeel.getTheme().getFrameColor();
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2D.setColor(Color.white);
            g2D.drawRoundRect(x, y, w - 1, h - 1, 4, 4);

            g2D.setColor(frameColor);
            g2D.drawRoundRect(x, y, w - 1, h - 2, 4, 4);

            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
        }

        public Insets getBorderInsets(Component c) {
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ToggleButtonBorder

    public static class RolloverToolButtonBorder implements Border, UIResource {
        private static final Insets insets = new Insets(1, 1, 1, 1);
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton)c;
            ButtonModel model = button.getModel();
            Color loColor = ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 10);
            if (model.isEnabled()) {
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    Graphics2D g2D = (Graphics2D)g;
                    Composite composite = g2D.getComposite();
                    g.setColor(loColor);
                    g.drawRect(x, y, w - 1, h - 1);
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.black);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(composite);
                }
                else if (model.isRollover()) {
                    Graphics2D g2D = (Graphics2D)g;
                    Composite composite = g2D.getComposite();
                    g.setColor(loColor);
                    g.drawRect(x, y, w - 1, h - 1);
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
                    g2D.setComposite(alpha);
                    g.setColor(Color.white);
                    g.fillRect(x + 1, y + 1, w - 2, h - 2);
                    g2D.setComposite(composite);
                }
            }
        }
        
        public Insets getBorderInsets(Component c)
        { return insets; }
        
        public boolean isBorderOpaque()
        { return true; }
        
    } // class RolloverToolButtonBorder

    public static class MenuItemBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JMenuItem b = (JMenuItem) c;
            ButtonModel model = b.getModel();
            Color borderColor = ColorHelper.darker(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 20);
            g.setColor(borderColor);
            if (c.getParent() instanceof JMenuBar) {
                if (model.isArmed() || model.isSelected()) {
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y + 1, x, y + h - 1);
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                }
            } else {
                if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                }
            }
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }
    } // class MenuItemBorder

    public static class PopupMenuBorder extends AbstractBorder implements UIResource {
        protected static final Font logoFont = new Font("Dialog", Font.BOLD, 12);
        protected Insets logoInsets = new Insets(2, 18, 1, 1);
        protected Insets insets = new Insets(2, 1, 1, 1);

        public boolean hasLogo() {
            return ((AbstractLookAndFeel.getTheme().getLogoString() != null) && (AbstractLookAndFeel.getTheme().getLogoString().length() > 0));
        }

        public void paintLogo(Graphics2D g2D, int w, int h) {
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D imageGraphics = image.createGraphics();
            Color logoColorHi = AbstractLookAndFeel.getTheme().getMenuSelectionBackgroundColorDark();
            Color logoColorLo = AbstractLookAndFeel.getTheme().getMenuSelectionBackgroundColor();
            Color colors[] = ColorHelper.createColorArr(logoColorHi, logoColorLo, 32);
            JTattooUtilities.fillHorGradient(imageGraphics, colors, 0, 0, w, h);

            imageGraphics.setFont(logoFont);
            FontMetrics fm = imageGraphics.getFontMetrics();
            AffineTransform at = new AffineTransform();
            at.setToRotation(Math.PI + (Math.PI / 2));
            imageGraphics.setTransform(at);
            int xs = -h + 4;
            int ys = fm.getAscent() + 2;

            imageGraphics.setColor(ColorHelper.darker(logoColorLo, 20));
            imageGraphics.drawString(JTattooUtilities.getClippedText(AbstractLookAndFeel.getTheme().getLogoString(), fm, h - 16), xs - 1, ys + 1);

            imageGraphics.setColor(Color.white);
            imageGraphics.drawString(JTattooUtilities.getClippedText(AbstractLookAndFeel.getTheme().getLogoString(), fm, h - 16), xs, ys);

            Rectangle2D r2D = new Rectangle2D.Double(0, 0, w, h);
            TexturePaint texturePaint = new TexturePaint(image, r2D);
            g2D.setPaint(texturePaint);
            g2D.fillRect(0, 0, w, h);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            int dx = getBorderInsets(c).left - 1;
            Color logoColor = AbstractLookAndFeel.getMenuSelectionBackgroundColor();
            Color menuColor = AbstractLookAndFeel.getMenuBackgroundColor();
            Color borderColor = ColorHelper.darker(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 20);
            g.setColor(logoColor);
            g.fillRect(x, y, dx, h);
            if (hasLogo()) {
                paintLogo((Graphics2D) g, dx, h);
            }
            g.setColor(borderColor);
            boolean menuBarPopup = false;
            JPopupMenu pm = (JPopupMenu)c;
            if (pm.getInvoker() != null) {
                menuBarPopup = (pm.getInvoker().getParent() instanceof JMenuBar);
            }
            if (menuBarPopup)
                g.drawLine(x + dx, y, x + w, y);
            else
                g.drawLine(x, y, x + w, y);
            g.drawLine(x, y, x, y + h);
            g.drawLine(x + w - 1, y, x + w - 1, y + h);
            g.drawLine(x, y + h - 1, x + w, y + h - 1);
            g.drawLine(x + dx, y, x + dx, y + h);
        }

        public Insets getBorderInsets(Component c) {
            if (hasLogo()) {
                return logoInsets;
            } else {
                return insets;
            }
        }
    } // class PopupMenuBorder

    public static class InternalFrameBorder extends BaseInternalFrameBorder {

        private static Insets borderInsets = new Insets(3, 3, 3, 3);

        public InternalFrameBorder() {
        }

        public Insets getBorderInsets(Component c) {
            if (isResizable(c)) {
                return borderInsets;
            } else {
                return paletteInsets;
            }
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            int th = getTitleHeight(c);
            int dw = getBorderInsets(c).top;
            Color titleColor = AbstractLookAndFeel.getWindowTitleColorLight();
            Color borderColor = AbstractLookAndFeel.getWindowTitleColorDark();
            Color frameColor = AbstractLookAndFeel.getWindowBorderColor();
            if (!isActive(c)) {
                titleColor = AbstractLookAndFeel.getWindowInactiveTitleColorLight();
                borderColor = AbstractLookAndFeel.getWindowInactiveTitleColorDark();
                frameColor = AbstractLookAndFeel.getWindowInactiveBorderColor();
            }

            g.setColor(borderColor);
            g.fillRect(x, y + 1, w, dw - 1);
            g.fillRect(x + 1, y + h - dw, w - 2, dw - 1);
            g.fillRect(x + 1, y + dw + th - 1, dw - 1, y + h - dw);
            g.fillRect(x + w - dw, y + dw + th - 1, dw - 1, y + h - dw);

            if (isActive(c)) {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), x + 1, y + dw, dw - 1, y + dw + th - 2);
                JTattooUtilities.fillHorGradient(g, ALTERNATE_COLORS, x + w - dw, y + dw, dw - 1, y + dw + th - 2);
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), x + 1, y + dw, dw - 1, y + dw + th - 2);
                JTattooUtilities.fillHorGradient(g, ALTERNATE_COLORS, x + w - dw, y + dw, dw - 1, y + dw + th - 2);
            }
            g.setColor(frameColor);
            g.drawRect(x, y, w - 1, h - 1);

            g.setColor(ALTERNATE_COLORS[0]);
            int tw = (th + 1) * (getButtonCount(c) + 1);
            g.fillRect(w - tw - dw, y + 1, tw + dw - 1, dw);
            g.setColor(ColorHelper.brighter(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 20));
            g.drawLine(w - tw - dw + 1, y + 1, w - tw - dw + 1, y + dw);
        }

        private int getButtonCount(Component c) {
            int buttonCount = 1;
            if (c instanceof JRootPane) {
                JRootPane jp = (JRootPane) c;
                if (jp.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) c.getParent();
                    if (frame.isResizable()) {
                        buttonCount = 3;
                    } else {
                        buttonCount = 2;
                    }
                }
            } else if (c instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) c;
                if (frame.isIconifiable()) {
                    buttonCount++;
                }
                if (frame.isMaximizable()) {
                    buttonCount++;
                }
            }
            return buttonCount;
        }
    } // class InternalFrameBorder
        
} // class PulsarBorders

