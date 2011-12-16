/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

/**
 * @author Michael Hagen
 */
public class BaseBorders {

    private static Border focusFrameBorder = null;
    private static Border textFieldBorder = null;
    private static Border spinnerBorder = null;
    private static Border comboBoxBorder = null;
    private static Border progressBarBorder = null;
    private static Border tableHeaderBorder = null;
    private static Border popupMenuBorder = null;
    private static Border menuItemBorder = null;
    private static Border toolBarBorder = null;
    private static Border toolButtonBorder = null;
    private static Border paletteBorder = null;
    private static Border scrollPaneBorder = null;
    private static Border tableScrollPaneBorder = null;
    private static Border tabbedPaneBorder = null;
    private static Border desktopIconBorder = null;

    public static void initDefaults() {
        textFieldBorder = null;
        spinnerBorder = null;
        comboBoxBorder = null;
        progressBarBorder = null;
        tableHeaderBorder = null;
        popupMenuBorder = null;
        menuItemBorder = null;
        toolBarBorder = null;
        toolButtonBorder = null;
        paletteBorder = null;
        scrollPaneBorder = null;
        tableScrollPaneBorder = null;
        tabbedPaneBorder = null;
        desktopIconBorder = null;
    }

    //------------------------------------------------------------------------------------
    // Lazy access methods
    //------------------------------------------------------------------------------------
    public static Border getFocusFrameBorder() {
        if (focusFrameBorder == null) {
            focusFrameBorder = new FocusFrameBorder();
        }
        return focusFrameBorder;
    }

    //------------------------------------------------------------------------------------
    // Lazy access methods
    //------------------------------------------------------------------------------------
    public static Border getTextBorder() {
        if (textFieldBorder == null) {
            textFieldBorder = new TextFieldBorder();
        }
        return textFieldBorder;
    }

    public static Border getSpinnerBorder() {
        if (spinnerBorder == null) {
            spinnerBorder = new SpinnerBorder();
        }
        return spinnerBorder;
    }

    public static Border getTextFieldBorder() {
        return getTextBorder();
    }

    public static Border getComboBoxBorder() {
        if (comboBoxBorder == null) {
            comboBoxBorder = new ComboBoxBorder();
        }
        return comboBoxBorder;
    }

    public static Border getProgressBarBorder() {
        if (progressBarBorder == null) {
            progressBarBorder = BorderFactory.createLineBorder(ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 30));
        }
        return progressBarBorder;
    }

    public static Border getTableHeaderBorder() {
        if (tableHeaderBorder == null) {
            tableHeaderBorder = new TableHeaderBorder();
        }
        return tableHeaderBorder;
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

    public static Border getMenuItemBorder() {
        if (menuItemBorder == null) {
            menuItemBorder = new MenuItemBorder();
        }
        return menuItemBorder;
    }

    public static Border getToolBarBorder() {
        if (toolBarBorder == null) {
            toolBarBorder = new ToolBarBorder();
        }
        return toolBarBorder;
    }

    public static Border getToolButtonBorder() {
        if (toolButtonBorder == null) {
            toolButtonBorder = new ToolButtonBorder();
        }
        return toolButtonBorder;
    }

    public static Border getMenuBarBorder() {
        return BorderFactory.createEmptyBorder(1, 1, 1, 1);
    }

    public static Border getPaletteBorder() {
        if (paletteBorder == null) {
            paletteBorder = new PaletteBorder();
        }
        return paletteBorder;
    }

    public static Border getScrollPaneBorder() {
        if (scrollPaneBorder == null) {
            scrollPaneBorder = new ScrollPaneBorder(false);
        }
        return scrollPaneBorder;
    }

    public static Border getTableScrollPaneBorder() {
        if (tableScrollPaneBorder == null) {
            tableScrollPaneBorder = new ScrollPaneBorder(true);
        }
        return tableScrollPaneBorder;
    }

    public static Border getTabbedPaneBorder() {
        if (tabbedPaneBorder == null) {
            tabbedPaneBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        }
        return tabbedPaneBorder;
    }

    public static Border getDesktopIconBorder() {
        if (desktopIconBorder == null) {
            desktopIconBorder = new BorderUIResource.CompoundBorderUIResource(
                    new LineBorder(AbstractLookAndFeel.getWindowBorderColor(), 1),
                    new MatteBorder(2, 2, 1, 2, AbstractLookAndFeel.getWindowBorderColor()));
        }
        return desktopIconBorder;
    }

    //------------------------------------------------------------------------------------
    // Implementation of border classes
    //------------------------------------------------------------------------------------
    public static class FocusFrameBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color hiColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFocusFrameColor(), 60);
            Color loColor = AbstractLookAndFeel.getTheme().getFocusFrameColor();
            g.setColor(loColor);
            g.drawRect(x, y, width - 1, height - 1);
            g.setColor(hiColor);
            g.drawRect(x + 1, y + 1, width - 3, height - 3);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class FocusFrameBorder

    public static class TextFieldBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, width - 1, height - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class TextFieldBorder

    public static class SpinnerBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, width - 1, height - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class SpinnerBorder

    public static class ComboBoxBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, width - 1, height - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class ComboBoxBorder

    public static class TableHeaderBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 0);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color cHi = AbstractLookAndFeel.getControlHighlight();
            Color cLo = AbstractLookAndFeel.getControlShadow();
            JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class TableHeaderBorder

    public static class ScrollPaneBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);
        private static final Insets tableInsets = new Insets(1, 1, 1, 1);
        
        private boolean tableBorder = false;

        public ScrollPaneBorder(boolean tableBorder) {
            this.tableBorder = tableBorder;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
            g.drawRect(x, y, w - 1, h - 1);
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getBackgroundColor(), 50));
            g.drawRect(x + 1, y + 1, w - 3, h - 3);
        }

        public Insets getBorderInsets(Component c) {
            if (tableBorder) {
                return new Insets(tableInsets.top, tableInsets.left, tableInsets.bottom, tableInsets.right);
            } else {
                return new Insets(insets.top, insets.left, insets.bottom, insets.right);
            }
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets ins = getBorderInsets(c);
            borderInsets.left = ins.left;
            borderInsets.top = ins.top;
            borderInsets.right = ins.right;
            borderInsets.bottom = ins.bottom;
            return borderInsets;
        }

    } // class ScrollPaneBorder

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
            Color logoColor = AbstractLookAndFeel.getTheme().getMenuSelectionBackgroundColor();
            imageGraphics.setColor(logoColor);
            imageGraphics.fillRect(0, 0, w, h);

            imageGraphics.setFont(logoFont);
            FontMetrics fm = imageGraphics.getFontMetrics();
            AffineTransform at = new AffineTransform();
            at.setToRotation(Math.PI + (Math.PI / 2));
            imageGraphics.setTransform(at);
            int xs = -h + 4;
            int ys = fm.getAscent() + 2;

            imageGraphics.setColor(ColorHelper.darker(logoColor, 20));
            imageGraphics.drawString(JTattooUtilities.getClippedText(AbstractLookAndFeel.getTheme().getLogoString(), fm, h - 16), xs - 1, ys + 1);

            imageGraphics.setColor(Color.white);
            imageGraphics.drawString(JTattooUtilities.getClippedText(AbstractLookAndFeel.getTheme().getLogoString(), fm, h - 16), xs, ys);

            Rectangle2D r2D = new Rectangle2D.Double(0, 0, w, h);
            TexturePaint texturePaint = new TexturePaint(image, r2D);
            g2D.setPaint(texturePaint);
            g2D.fillRect(0, 0, w, h);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            int dx = getBorderInsets(c).left;
            Color logoColor = AbstractLookAndFeel.getMenuSelectionBackgroundColor();
            Color menuColor = AbstractLookAndFeel.getMenuBackgroundColor();
            Color borderColorLo = AbstractLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 50);
            g.setColor(logoColor);
            g.fillRect(x, y, dx - 1, h - 1);
            if (hasLogo()) {
                paintLogo((Graphics2D) g, dx, h);
            }
            g.setColor(borderColorHi);
            g.drawLine(x + 1, y + 1, x + dx, y + 1);
            g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
            g.setColor(ColorHelper.brighter(menuColor, 50.0));
            g.drawLine(x + dx, y + 1, x + w - 2, y + 1);
            g.setColor(borderColorLo);
            g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - 1);
            g.drawRect(x, y, w - 1, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            if (hasLogo()) {
                return new Insets(logoInsets.top, logoInsets.left, logoInsets.bottom, logoInsets.right);
            } else {
                return new Insets(insets.top, insets.left, insets.bottom, insets.right);
            }
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets ins = getBorderInsets(c);
            borderInsets.left = ins.left;
            borderInsets.top = ins.top;
            borderInsets.right = ins.right;
            borderInsets.bottom = ins.bottom;
            return borderInsets;
        }

    } // class PopupMenuBorder

    public static class PopupMenuShadowBorder extends PopupMenuBorder {

        private static final int shadowSize = 3;

        public PopupMenuShadowBorder() {
            logoInsets = new Insets(2, 18, shadowSize + 1, shadowSize + 1);
            insets = new Insets(2, 1, shadowSize + 1, shadowSize + 1);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            int dx = getBorderInsets(c).left;
            Color logoColor = AbstractLookAndFeel.getTheme().getMenuSelectionBackgroundColor();
            Color borderColorLo = AbstractLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 50);
            g.setColor(logoColor);
            g.fillRect(x, y, dx - 1, h - 1 - shadowSize);
            if (hasLogo()) {
                paintLogo(g2D, dx, h - shadowSize);
            }
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, AbstractLookAndFeel.getTheme().getMenuAlpha());
            g2D.setComposite(alpha);
            g.setColor(borderColorHi);
            g.drawLine(x + 1, y + 1, x + dx, y + 1);
            g.drawLine(x + 1, y + 1, x + 1, y + h - shadowSize - 1);
            g.setColor(Color.white);
            g.drawLine(x + dx, y + 1, x + w - 2, y + 1);
            g.setColor(borderColorLo);
            g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - shadowSize - 1);
            g.drawRect(x, y, w - shadowSize - 1, h - shadowSize - 1);

            // paint the shadow
            g2D.setColor(Color.black);
            float alphaValue = 0.6f;
            for (int i = 0; i < shadowSize; i++) {
                alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
                g2D.setComposite(alpha);
                g.drawLine(x + w - shadowSize + i, y + shadowSize, x + w - shadowSize + i, y + h - shadowSize - 1 + i);
                g.drawLine(x + shadowSize, y + h - shadowSize + i, x + w - shadowSize + i, y + h - shadowSize + i);
                alphaValue -= (alphaValue / 2);
            }

            g2D.setComposite(composite);
        }
    } // class PopupMenuShadowBorder

    public static class MenuItemBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JMenuItem b = (JMenuItem) c;
            ButtonModel model = b.getModel();
            Color borderColorLo = AbstractLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 50);
            if (c.getParent() instanceof JMenuBar) {
                if (model.isArmed() || model.isSelected()) {
                    g.setColor(borderColorLo);
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y, x, y + h - 1);
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                    g.setColor(borderColorHi);
                    g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
                    g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
                }
            } else {
                if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                    g.setColor(borderColorLo);
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                    g.setColor(borderColorHi);
                    g.drawLine(x, y + 1, x + w - 2, y + 1);
                }
            }
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class MenuItemBorder

    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {

        private static final Color shadow = new Color(160, 160, 160);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (((JToolBar) c).isFloatable()) {
                Graphics2D g2D = (Graphics2D) g;
                Composite composite = g2D.getComposite();
                AlphaComposite alpha = alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                g2D.setComposite(alpha);
                if (((JToolBar) c).getOrientation() == HORIZONTAL) {
                    if (!JTattooUtilities.isLeftToRight(c)) {
                        x += w - 15;
                    }
                    g.setColor(Color.white);
                    g.drawLine(x + 3, y + 4, x + 3, h - 5);
                    g.drawLine(x + 6, y + 3, x + 6, h - 4);
                    g.drawLine(x + 9, y + 4, x + 9, h - 5);
                    g.setColor(shadow);
                    g.drawLine(x + 4, y + 4, x + 4, h - 5);
                    g.drawLine(x + 7, y + 3, x + 7, h - 4);
                    g.drawLine(x + 10, y + 4, x + 10, h - 5);
                } else {
                    // vertical
                    g.setColor(Color.white);
                    g.drawLine(x + 3, y + 3, w - 4, y + 3);
                    g.drawLine(x + 3, y + 6, w - 4, y + 6);
                    g.drawLine(x + 3, y + 9, w - 4, y + 9);
                    g.setColor(shadow);
                    g.drawLine(x + 3, y + 4, w - 4, y + 4);
                    g.drawLine(x + 3, y + 7, w - 4, y + 7);
                    g.drawLine(x + 3, y + 10, w - 4, y + 10);
                }
                g2D.setComposite(composite);
            }
        }

        public Insets getBorderInsets(Component c) {
            Insets insets = new Insets(2, 2, 2, 2);
            if (((JToolBar) c).isFloatable()) {
                if (((JToolBar) c).getOrientation() == HORIZONTAL) {
                    if (JTattooUtilities.isLeftToRight(c)) {
                        insets.left = 15;
                    } else {
                        insets.right = 15;
                    }
                } else {
                    insets.top = 15;
                }
            }
            Insets margin = ((JToolBar) c).getMargin();
            if (margin != null) {
                insets.left += margin.left;
                insets.top += margin.top;
                insets.right += margin.right;
                insets.bottom += margin.bottom;
            }
            return insets;
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets insets = getBorderInsets(c);
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class ToolBarBorder

    public static class ToolButtonBorder implements Border, UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            Color frameColor = AbstractLookAndFeel.getToolbarBackgroundColor();
            Color frameHiColor = ColorHelper.brighter(frameColor, 10);
            Color frameLoColor = ColorHelper.darker(frameColor, 30);
            JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor, x, y, w, h);
            if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                JTattooUtilities.draw3DBorder(g, frameLoColor, frameHiColor, x, y, w, h);
            } else {
                JTattooUtilities.draw3DBorder(g, frameLoColor, frameHiColor, x, y, w, h);
                JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor, x + 1, y + 1, w - 2, h - 2);
            }
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class ToolButtonBorder

    public static class PaletteBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (JTattooUtilities.isFrameActive((JComponent) c)) {
                g.setColor(AbstractLookAndFeel.getWindowBorderColor());
            } else {
                g.setColor(AbstractLookAndFeel.getWindowInactiveBorderColor());
            }
            g.drawRect(x, y, w - 1, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class PaletteBorder

    public static class BaseInternalFrameBorder extends AbstractBorder implements UIResource {

        protected final int dw = 5;
        protected final int trackWidth = 22;
        protected final Insets insets = new Insets(dw, dw, dw, dw);
        protected final Insets paletteInsets = new Insets(3, 3, 3, 3);

        public BaseInternalFrameBorder() {
            insets.top = dw;
        }

        public boolean isResizable(Component c) {
            boolean resizable = true;
            if (c instanceof JDialog) {
                JDialog dialog = (JDialog) c;
                resizable = dialog.isResizable();
            } else if (c instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) c;
                resizable = frame.isResizable();
            } else if (c instanceof JRootPane) {
                JRootPane jp = (JRootPane) c;
                if (jp.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) c.getParent();
                    resizable = frame.isResizable();
                } else if (jp.getParent() instanceof JDialog) {
                    JDialog dialog = (JDialog) c.getParent();
                    resizable = dialog.isResizable();
                }
            }
            return resizable;
        }

        public boolean isActive(Component c) {
            boolean active = true;
            if (c instanceof JDialog) {
                JDialog dlg = (JDialog) c;
                if (dlg.getParent() instanceof JComponent) {
                    return JTattooUtilities.isActive((JComponent) (dlg.getParent()));
                }
            } else if (c instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) c;
                active = frame.isSelected();
                if (active) {
                    return JTattooUtilities.isActive(frame);
                }
            } else if (c instanceof JRootPane) {
                JRootPane jp = (JRootPane) c;
                if (jp.getTopLevelAncestor() instanceof Window) {
                    Window window = (Window) jp.getTopLevelAncestor();
                    return JTattooUtilities.isWindowActive(window);
                }
            }
            return active;
        }

        public int getTitleHeight(Component c) {
            int th = 21;
            int fh = getBorderInsets(c).top + getBorderInsets(c).bottom;
            if (c instanceof JDialog) {
                JDialog dialog = (JDialog) c;
                th = dialog.getSize().height - dialog.getContentPane().getSize().height - fh - 1;
                if (dialog.getJMenuBar() != null) {
                    th -= dialog.getJMenuBar().getSize().height;
                }
            } else if (c instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) c;
                th = frame.getSize().height - frame.getRootPane().getSize().height - fh - 1;
                if (frame.getJMenuBar() != null) {
                    th -= frame.getJMenuBar().getSize().height;
                }
            } else if (c instanceof JRootPane) {
                JRootPane jp = (JRootPane) c;
                if (jp.getParent() instanceof JFrame) {
                    JFrame frame = (JFrame) c.getParent();
                    th = frame.getSize().height - frame.getContentPane().getSize().height - fh - 1;
                    if (frame.getJMenuBar() != null) {
                        th -= frame.getJMenuBar().getSize().height;
                    }
                } else if (jp.getParent() instanceof JDialog) {
                    JDialog dialog = (JDialog) c.getParent();
                    th = dialog.getSize().height - dialog.getContentPane().getSize().height - fh - 1;
                    if (dialog.getJMenuBar() != null) {
                        th -= dialog.getJMenuBar().getSize().height;
                    }
                }
            }
            return th;
        }

        public Insets getBorderInsets(Component c) {
            if (isResizable(c)) {
                return new Insets(insets.top, insets.left, insets.bottom, insets.right);
            } else {
                return new Insets(paletteInsets.top, paletteInsets.left, paletteInsets.bottom, paletteInsets.right);
            }
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            Insets ins = getBorderInsets(c);
            borderInsets.left = ins.left;
            borderInsets.top = ins.top;
            borderInsets.right = ins.right;
            borderInsets.bottom = ins.bottom;
            return borderInsets;
        }

    } // class BaseInternalFrameBorder

    public static class Down3DBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(1, 1, 1, 1);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color frameColor = AbstractLookAndFeel.getTheme().getBackgroundColor();
            JTattooUtilities.draw3DBorder(g, ColorHelper.darker(frameColor, 20), ColorHelper.brighter(frameColor, 80), x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        public Insets getBorderInsets(Component c, Insets borderInsets) {
            borderInsets.left = insets.left;
            borderInsets.top = insets.top;
            borderInsets.right = insets.right;
            borderInsets.bottom = insets.bottom;
            return borderInsets;
        }

    } // class Down3DBorder
    
} // class BaseBorders
