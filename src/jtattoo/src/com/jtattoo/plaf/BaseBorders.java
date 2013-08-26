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

package com.jtattoo.plaf;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class BaseBorders {

    protected static Border buttonBorder = null;
    protected static Border focusFrameBorder = null;
    protected static Border textFieldBorder = null;
    protected static Border spinnerBorder = null;
    protected static Border comboBoxBorder = null;
    protected static Border progressBarBorder = null;
    protected static Border tableHeaderBorder = null;
    protected static Border popupMenuBorder = null;
    protected static Border menuItemBorder = null;
    protected static Border toolBarBorder = null;
    protected static Border toolButtonBorder = null;
    protected static Border rolloverToolButtonBorder = null;
    protected static Border internalFrameBorder = null;
    protected static Border paletteBorder = null;
    protected static Border scrollPaneBorder = null;
    protected static Border tableScrollPaneBorder = null;
    protected static Border tabbedPaneBorder = null;
    protected static Border desktopIconBorder = null;

    public static void initDefaults() {
        buttonBorder = null;
        textFieldBorder = null;
        spinnerBorder = null;
        comboBoxBorder = null;
        progressBarBorder = null;
        tableHeaderBorder = null;
        popupMenuBorder = null;
        menuItemBorder = null;
        toolBarBorder = null;
        toolButtonBorder = null;
        rolloverToolButtonBorder = null;
        paletteBorder = null;
        internalFrameBorder = null;
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
                popupMenuBorder = new BasePopupMenuBorder();
            } else {
                popupMenuBorder = new BasePopupMenuShadowBorder();
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
            Graphics2D g2D = (Graphics2D)g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
            g2D.setComposite(alpha);
            Color cHi = AbstractLookAndFeel.getTheme().getControlHighlightColor();
            Color cLo = AbstractLookAndFeel.getTheme().getControlShadowColor();
            JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
            g2D.setComposite(savedComposite);
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

    public static class BasePopupMenuBorder extends AbstractBorder implements UIResource {

        protected static Font logoFont;
        protected static Insets leftLogoInsets;
        protected static Insets rightLogoInsets;
        protected static Insets insets;
        protected static int shadowSize;

        public BasePopupMenuBorder() {
            logoFont = new Font("Dialog", Font.BOLD, 12);
            leftLogoInsets = new Insets(2, 18, 1, 1);
            rightLogoInsets = new Insets(2, 2, 1, 18);
            insets = new Insets(2, 1, 1, 1);
            shadowSize = 0;
        }
        
        public boolean isMenuBarPopup(Component c) {
            boolean menuBarPopup = false;
            if (c instanceof JPopupMenu) {
                JPopupMenu pm = (JPopupMenu) c;
                if (pm.getInvoker() != null) {
                    menuBarPopup = (pm.getInvoker().getParent() instanceof JMenuBar);
                }
            }
            return menuBarPopup;
        }

        public boolean hasLogo(Component c) {
            return ((AbstractLookAndFeel.getTheme().getLogoString() != null) && (AbstractLookAndFeel.getTheme().getLogoString().length() > 0));
        }

        public Color getLogoColorHi() {
            return Color.white;
        }
        
        public Color getLogoColorLo() {
            return ColorHelper.darker(AbstractLookAndFeel.getTheme().getMenuSelectionBackgroundColor(), 20);
        }
        
        public void paintLogo(Component c, Graphics g, int x, int y, int w, int h) {
            if (hasLogo(c)) {
                Graphics2D g2D = (Graphics2D)g;
                
                Font savedFont = g2D.getFont();
                g.setFont(logoFont);
                
                FontMetrics fm = g2D.getFontMetrics();
                String logo = JTattooUtilities.getClippedText(AbstractLookAndFeel.getTheme().getLogoString(), fm, h - 16);

                AffineTransform savedTransform = g2D.getTransform();
                
                Color fc = getLogoColorHi();
                Color bc = getLogoColorLo();
                
                if (JTattooUtilities.isLeftToRight(c)) {
                    g2D.translate(fm.getAscent() + 1, h - shadowSize - 4);
                    g2D.rotate(Math.toRadians(-90));
                    g2D.setColor(bc);
                    JTattooUtilities.drawString((JComponent)c, g, logo, 0, 1);
                    g2D.setColor(fc);
                    JTattooUtilities.drawString((JComponent)c, g, logo, 1, 0);
                } else {
                    g2D.translate(w - shadowSize - 4, h - shadowSize - 4);
                    g2D.rotate(Math.toRadians(-90));
                    g2D.setColor(bc);
                    JTattooUtilities.drawString((JComponent)c, g, logo, 0, 1);
                    g2D.setColor(fc);
                    JTattooUtilities.drawString((JComponent)c, g, logo, 1, 0);
                }
                
                g2D.setTransform(savedTransform);
                g2D.setFont(savedFont);
            }
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color logoColor = AbstractLookAndFeel.getMenuSelectionBackgroundColor();
            Color borderColorLo = AbstractLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 40);
            g.setColor(logoColor);
            if (JTattooUtilities.isLeftToRight(c)) {
                int dx = getBorderInsets(c).left;
                g.fillRect(x, y, dx - 1, h - 1);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + dx, y + 1, x + w - 2, y + 1);
                g.setColor(borderColorHi);
                g.drawLine(x + 1, y, x + 1, y + h - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x + dx - 1, y, x + w, y);
                    // left
                    g.drawLine(x, y, x, y + h - 1);
                    // bottom
                    g.drawLine(x, y + h - 1, x + w, y + h - 1);
                    // right
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                } else {
                    g.drawRect(x, y, w - 1, h - 1);
                }
                // - logo separator
                g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - 1);
            } else {
                int dx = getBorderInsets(c).right;
                g.fillRect(x + w - dx, y, dx, h - 1);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + 1, y + 1, x + w - dx - 1, y + 1);
                g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x, y, x + w - dx, y);
                    // left
                    g.drawLine(x, y, x, y + h - 1);
                    // bottom
                    g.drawLine(x, y + h - 1, x + w, y + h - 1);
                    // right
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                } else {
                    g.drawRect(x, y, w - 1, h - 1);
                }
                // - logo separator
                g.drawLine(x + w - dx, y + 1, x + w - dx, y + h - 1);
            }
        }

        public Insets getBorderInsets(Component c) {
            if (hasLogo(c)) {
                if (JTattooUtilities.isLeftToRight(c)) {
                    return new Insets(leftLogoInsets.top, leftLogoInsets.left, leftLogoInsets.bottom + shadowSize, leftLogoInsets.right + shadowSize);
                } else {
                    return new Insets(rightLogoInsets.top, rightLogoInsets.left, rightLogoInsets.bottom + shadowSize, rightLogoInsets.right + shadowSize);
                }
            } else {
                return new Insets(insets.top, insets.left, insets.bottom + shadowSize, insets.right + shadowSize);
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

    public static class BasePopupMenuShadowBorder extends BasePopupMenuBorder {

        public BasePopupMenuShadowBorder() {
            shadowSize = 4;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, AbstractLookAndFeel.getTheme().getMenuAlpha());
            g2D.setComposite(alpha);
            Color logoColor = AbstractLookAndFeel.getTheme().getMenuSelectionBackgroundColor();
            Color borderColorLo = AbstractLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 40);
            g.setColor(logoColor);
            if (JTattooUtilities.isLeftToRight(c)) {
                int dx = getBorderInsets(c).left;
                g.fillRect(x, y, dx - 1, h - 1 - shadowSize);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + dx, y + 1, x + w - shadowSize - 2, y + 1);
                g.setColor(borderColorHi);
                g.drawLine(x + 1, y, x + 1, y + h - shadowSize - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x + dx - 1, y, x + w - shadowSize - 1, y);
                    // left
                    g.drawLine(x, y, x, y + h - shadowSize - 1);
                    // bottom
                    g.drawLine(x, y + h - shadowSize - 1, x + w - shadowSize - 1, y + h - shadowSize - 1);
                    // right
                    g.drawLine(x + w - shadowSize - 1, y + 1, x + w - shadowSize - 1, y + h - shadowSize - 1);
                } else {
                    g.drawRect(x, y, w - shadowSize - 1, h - shadowSize - 1);
                }
                // - logo separator
                g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - shadowSize - 1);
            } else {
                int dx = getBorderInsets(c).right - shadowSize;
                g.fillRect(x + w - dx - shadowSize, y, dx - 1, h - 1 - shadowSize);
                paintLogo(c, g, x, y, w, h);
                // - highlight 
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getMenuBackgroundColor(), 40));
                g.drawLine(x + 1, y + 1, x + w - dx - shadowSize - 1, y + 1);
                g.drawLine(x + 1, y + 1, x + 1, y + h - shadowSize - 2);
                // - outer frame
                g.setColor(borderColorLo);
                if (isMenuBarPopup(c)) {
                    // top
                    g.drawLine(x, y, x + w - dx - shadowSize, y);
                    // left
                    g.drawLine(x, y, x, y + h - shadowSize - 1);
                    // bottom
                    g.drawLine(x, y + h - shadowSize - 1, x + w - shadowSize - 1, y + h - shadowSize - 1);
                    // right
                    g.drawLine(x + w - shadowSize - 1, y, x + w - shadowSize - 1, y + h - shadowSize - 1);
                } else {
                    g.drawRect(x, y, w - shadowSize - 1, h - shadowSize - 1);
                }
                // - logo separator
                g.drawLine(x + w - dx - shadowSize, y + 1, x + w - dx - shadowSize, y + h - shadowSize - 1);
            }

            // paint the shadow
            g2D.setColor(AbstractLookAndFeel.getTheme().getShadowColor());
            float alphaValue = 0.4f;
            for (int i = 0; i < shadowSize; i++) {
                alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
                g2D.setComposite(alpha);
                g.drawLine(x + w - shadowSize + i, y + shadowSize, x + w - shadowSize + i, y + h - shadowSize - 1 + i);
                g.drawLine(x + shadowSize, y + h - shadowSize + i, x + w - shadowSize + i, y + h - shadowSize + i);
                alphaValue -= (alphaValue / 2);
            }

            g2D.setComposite(savedComposite);
        }
        
    } // class PopupMenuShadowBorder

    public static class MenuItemBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JMenuItem b = (JMenuItem) c;
            ButtonModel model = b.getModel();
            Color borderColorLo = AbstractLookAndFeel.getFrameColor();
            Color borderColorHi = ColorHelper.brighter(AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 40);
            if (c.getParent() instanceof JMenuBar) {
                if (model.isArmed() || model.isSelected()) {
                    g.setColor(borderColorLo);
                    g.drawLine(x, y, x + w - 1, y);
                    g.drawLine(x, y, x, y + h - 1);
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                    g.setColor(borderColorHi);
                    g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
                    g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
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
                Composite savedComposite = g2D.getComposite();
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
                g2D.setComposite(savedComposite);
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
