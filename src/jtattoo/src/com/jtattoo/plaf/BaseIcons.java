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
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class BaseIcons {
    
    public static final LazyImageIcon PEARL_RED_24x24 = new LazyImageIcon("icons/pearl_red_24x24.png");
    public static final LazyImageIcon PEARL_YELLOW_24x24 = new LazyImageIcon("icons/pearl_yellow_24x24.png");
    public static final LazyImageIcon PEARL_GREEN_24x24 = new LazyImageIcon("icons/pearl_green_24x24.png");
    public static final LazyImageIcon PEARL_GREY_24x24 = new LazyImageIcon("icons/pearl_grey_24x24.png");
    public static final LazyImageIcon PEARL_RED_28x28 = new LazyImageIcon("icons/pearl_red_28x28.png");
    public static final LazyImageIcon PEARL_YELLOW_28x28 = new LazyImageIcon("icons/pearl_yellow_28x28.png");
    public static final LazyImageIcon PEARL_GREEN_28x28 = new LazyImageIcon("icons/pearl_green_28x28.png");
    public static final LazyImageIcon PEARL_GREY_28x28 = new LazyImageIcon("icons/pearl_grey_28x28.png");
    public static final LazyImageIcon PEARL_RED_32x32 = new LazyImageIcon("icons/pearl_red_32x32.png");
    public static final LazyImageIcon PEARL_YELLOW_32x32 = new LazyImageIcon("icons/pearl_yellow_32x32.png");
    public static final LazyImageIcon PEARL_GREEN_32x32 = new LazyImageIcon("icons/pearl_green_32x32.png");
    public static final LazyImageIcon PEARL_GREY_32x32 = new LazyImageIcon("icons/pearl_grey_32x32.png");
    public static final LazyImageIcon ICONIZER_10x10 = new LazyImageIcon("icons/iconizer_10x10.png");
    public static final LazyImageIcon ICONIZER_12x12 = new LazyImageIcon("icons/iconizer_12x12.png");
    public static final LazyImageIcon MINIMIZER_10x10 = new LazyImageIcon("icons/minimizer_10x10.png");
    public static final LazyImageIcon MINIMIZER_12x12 = new LazyImageIcon("icons/minimizer_12x12.png");
    public static final LazyImageIcon MAXIMIZER_10x10 = new LazyImageIcon("icons/maximizer_10x10.png");
    public static final LazyImageIcon MAXIMIZER_12x12 = new LazyImageIcon("icons/maximizer_12x12.png");
    public static final LazyImageIcon CLOSER_10x10 = new LazyImageIcon("icons/closer_10x10.png");
    public static final LazyImageIcon CLOSER_12x12 = new LazyImageIcon("icons/closer_12x12.png");
    public static final LazyImageIcon EMPTY_8x8 = new LazyImageIcon("icons/empty_8x8.png");

    protected static Icon comboBoxIcon = null;
    protected static Icon checkBoxIcon = null;
    protected static Icon menuCheckBoxIcon = null;
    protected static Icon radioButtonIcon = null;
    protected static Icon menuRadioButtonIcon = null;
    protected static Icon optionPaneErrorIcon = null;
    protected static Icon optionPaneWarningIcon = null;
    protected static Icon optionPaneInformationIcon = null;
    protected static Icon optionPaneQuestionIcon = null;
    protected static Icon fileChooserDetailViewIcon = null;
    protected static Icon fileChooserHomeFolderIcon = null;
    protected static Icon fileChooserListViewIcon = null;
    protected static Icon fileChooserNewFolderIcon = null;
    protected static Icon fileChooserUpFolderIcon = null;
    protected static Icon treeComputerIcon = null;
    protected static Icon treeFloppyDriveIcon = null;
    protected static Icon treeHardDriveIcon = null;
    protected static Icon treeFolderIcon = null;
    protected static Icon treeLeafIcon = null;
    protected static Icon treeOpenIcon = null;
    protected static Icon treeClosedIcon = null;
    protected static Icon paletteCloseIcon = null;
    protected static Icon menuIcon = null;
    
    protected static Icon iconIcon = null;
    protected static Icon maxIcon = null;
    protected static Icon minIcon = null;
    protected static Icon closeIcon = null;
    
    protected static Icon upArrowIcon = null;
    protected static Icon upArrowInverseIcon = null;
    protected static Icon downArrowIcon = null;
    protected static Icon downArrowInverseIcon = null;
    protected static Icon leftArrowIcon = null;
    protected static Icon leftArrowInverseIcon = null;
    protected static Icon rightArrowIcon = null;
    protected static Icon rightArrowInverseIcon = null;
    protected static Icon menuArrowIcon = null;
    protected static Icon splitterUpArrowIcon = null;
    protected static Icon splitterDownArrowIcon = null;
    protected static Icon splitterLeftArrowIcon = null;
    protected static Icon splitterRightArrowIcon = null;
    protected static Icon splitterHorBumpIcon = null;
    protected static Icon splitterVerBumpIcon = null;
    protected static Icon thumbHorIcon = null;
    protected static Icon thumbVerIcon = null;
    protected static Icon thumbHorIconRollover = null;
    protected static Icon thumbVerIconRollover = null;

    public static void initDefaults() {
        comboBoxIcon = null;
        checkBoxIcon = null;
        menuCheckBoxIcon = null;
        radioButtonIcon = null;
        menuRadioButtonIcon = null;
        optionPaneErrorIcon = null;
        optionPaneWarningIcon = null;
        optionPaneInformationIcon = null;
        optionPaneQuestionIcon = null;
        fileChooserDetailViewIcon = null;
        fileChooserHomeFolderIcon = null;
        fileChooserListViewIcon = null;
        fileChooserNewFolderIcon = null;
        fileChooserUpFolderIcon = null;
        treeComputerIcon = null;
        treeFloppyDriveIcon = null;
        treeHardDriveIcon = null;
        treeFolderIcon = null;
        treeLeafIcon = null;
        treeOpenIcon = null;
        treeClosedIcon = null;
        paletteCloseIcon = null;
        menuIcon = null;
        iconIcon = null;
        maxIcon = null;
        minIcon = null;
        closeIcon = null;
        upArrowIcon = null;
        upArrowInverseIcon = null;
        downArrowIcon = null;
        downArrowInverseIcon = null;
        leftArrowIcon = null;
        leftArrowInverseIcon = null;
        rightArrowIcon = null;
        rightArrowInverseIcon = null;
        menuArrowIcon = null;
        splitterUpArrowIcon = null;
        splitterDownArrowIcon = null;
        splitterLeftArrowIcon = null;
        splitterRightArrowIcon = null;
        splitterHorBumpIcon = null;
        splitterVerBumpIcon = null;
        thumbHorIcon = null;
        thumbVerIcon = null;
        thumbHorIconRollover = null;
        thumbVerIconRollover = null;
    }

    public static Icon getRadioButtonIcon() {
        if (radioButtonIcon == null) {
            radioButtonIcon = new RadioButtonIcon();
        }
        return radioButtonIcon;
    }

    public static Icon getCheckBoxIcon() {
        if (checkBoxIcon == null) {
            checkBoxIcon = new CheckBoxIcon();
        }
        return checkBoxIcon;
    }

    // OptionPane
    public static Icon getOptionPaneErrorIcon() {
        if (optionPaneErrorIcon == null) {
            optionPaneErrorIcon = new LazyImageIcon("icons/OptionPaneError.png");
        }
        return optionPaneErrorIcon;
    }

    public static Icon getOptionPaneWarningIcon() {
        if (optionPaneWarningIcon == null) {
            optionPaneWarningIcon = new LazyImageIcon("icons/OptionPaneWarning.png");
        }
        return optionPaneWarningIcon;
    }

    public static Icon getOptionPaneInformationIcon() {
        if (optionPaneInformationIcon == null) {
            optionPaneInformationIcon = new LazyImageIcon("icons/OptionPaneInformation.png");
        }
        return optionPaneInformationIcon;
    }

    public static Icon getOptionPaneQuestionIcon() {
        if (optionPaneQuestionIcon == null) {
            optionPaneQuestionIcon = new LazyImageIcon("icons/OptionPaneQuestion.png");
        }
        return optionPaneQuestionIcon;
    }

    // FileChooser
    public static Icon getFileChooserDetailViewIcon() {
        if (fileChooserDetailViewIcon == null) {
            fileChooserDetailViewIcon = new LazyImageIcon("icons/DetailsView.gif");
        }
        return fileChooserDetailViewIcon;
    }

    public static Icon getFileChooserHomeFolderIcon() {
        if (fileChooserHomeFolderIcon == null) {
            fileChooserHomeFolderIcon = new LazyImageIcon("icons/Home.gif");
        }
        return fileChooserHomeFolderIcon;
    }

    public static Icon getFileChooserListViewIcon() {
        if (fileChooserListViewIcon == null) {
            fileChooserListViewIcon = new LazyImageIcon("icons/ListView.gif");
        }
        return fileChooserListViewIcon;
    }

    public static Icon getFileChooserNewFolderIcon() {
        if (fileChooserNewFolderIcon == null) {
            fileChooserNewFolderIcon = new LazyImageIcon("icons/NewFolder.gif");
        }
        return fileChooserNewFolderIcon;
    }

    public static Icon getFileChooserUpFolderIcon() {
        if (fileChooserUpFolderIcon == null) {
            fileChooserUpFolderIcon = new LazyImageIcon("icons/UpFolder.gif");
        }
        return fileChooserUpFolderIcon;
    }

    // Tree
    public static Icon getTreeComputerIcon() {
        if (treeComputerIcon == null) {
            treeComputerIcon = new LazyImageIcon("icons/Computer.gif");
        }
        return treeComputerIcon;
    }

    public static Icon getTreeFloppyDriveIcon() {
        if (treeFloppyDriveIcon == null) {
            treeFloppyDriveIcon = new LazyImageIcon("icons/FloppyDrive.gif");
        }
        return treeFloppyDriveIcon;
    }

    public static Icon getTreeHardDriveIcon() {
        if (treeHardDriveIcon == null) {
            treeHardDriveIcon = new LazyImageIcon("icons/HardDrive.gif");
        }
        return treeHardDriveIcon;
    }

    public static Icon getTreeFolderIcon() {
        if (treeFolderIcon == null) {
            treeFolderIcon = new LazyImageIcon("icons/TreeClosed.gif");
        }
        return treeFolderIcon;
    }

    public static Icon getTreeLeafIcon() {
        if (treeLeafIcon == null) {
            treeLeafIcon = new LazyImageIcon("icons/TreeLeaf.gif");
        }
        return treeLeafIcon;
    }

    public static Icon getTreeControlIcon(boolean isCollapsed) {
        if (isCollapsed) {
            if (treeClosedIcon == null) {
                treeClosedIcon = new LazyImageIcon("icons/TreeClosedButton.gif");
            }
            return treeClosedIcon;
        } else {
            if (treeOpenIcon == null) {
                treeOpenIcon = new LazyImageIcon("icons/TreeOpenButton.gif");
            }
            return treeOpenIcon;
        }
    }

    // TitlePane icons
    public static Icon getMenuIcon() {
        if (menuIcon == null) {
            menuIcon = new LazyImageIcon("icons/JavaCup.gif");
        }
        return menuIcon;
    }

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                iconIcon = new MacIconIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                iconIcon = new IconSymbol(iconColor, null, iconRolloverColor);
            }
        }
        return iconIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                maxIcon = new MacMaxIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                maxIcon = new MaxSymbol(iconColor, null, iconRolloverColor);
            }
        }
        return maxIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                minIcon = new MacMinIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                minIcon = new MinSymbol(iconColor, null, iconRolloverColor);
            }
        }
        return minIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                closeIcon = new MacCloseIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                closeIcon = new CloseSymbol(iconColor, null, iconRolloverColor);
            }
        }
        return closeIcon;
    }

    public static Icon getPaletteCloseIcon() {
        if (paletteCloseIcon == null) {
            paletteCloseIcon = new CloseSymbol(Color.black, null, Color.red);
        }
        return paletteCloseIcon;
    }

    // MenuIcons
    public static Icon getMenuArrowIcon() {
        if (menuArrowIcon == null) {
            menuArrowIcon = new LazyMenuArrowImageIcon("icons/MenuRightArrow.gif", "icons/MenuLeftArrow.gif");
        }
        return menuArrowIcon;
    }

    public static Icon getMenuCheckBoxIcon() {
        if (menuCheckBoxIcon == null) {
            menuCheckBoxIcon = new CheckBoxIcon();
        }
        return menuCheckBoxIcon;
    }

    public static Icon getMenuRadioButtonIcon() {
        if (menuRadioButtonIcon == null) {
            menuRadioButtonIcon = new RadioButtonIcon();
        }
        return menuRadioButtonIcon;
    }

    // ArrowIcons
    public static Icon getUpArrowIcon() {
        if (upArrowIcon == null) {
            upArrowIcon = new LazyImageIcon("icons/UpArrow.gif");
        }
        return upArrowIcon;
    }

    public static Icon getUpArrowInverseIcon() {
        if (upArrowInverseIcon == null) {
            upArrowInverseIcon = new LazyImageIcon("icons/UpArrowInverse.gif");
        }
        return upArrowInverseIcon;
    }
    
    public static Icon getDownArrowIcon() {
        if (downArrowIcon == null) {
            downArrowIcon = new LazyImageIcon("icons/DownArrow.gif");
        }
        return downArrowIcon;
    }

    public static Icon getDownArrowInverseIcon() {
        if (downArrowInverseIcon == null) {
            downArrowInverseIcon = new LazyImageIcon("icons/DownArrowInverse.gif");
        }
        return downArrowInverseIcon;
    }
    
    public static Icon getLeftArrowIcon() {
        if (leftArrowIcon == null) {
            leftArrowIcon = new LazyImageIcon("icons/LeftArrow.gif");
        }
        return leftArrowIcon;
    }

    public static Icon getLeftArrowInverseIcon() {
        if (leftArrowInverseIcon == null) {
            leftArrowInverseIcon = new LazyImageIcon("icons/LeftArrowInverse.gif");
        }
        return leftArrowInverseIcon;
    }
    
    public static Icon getRightArrowIcon() {
        if (rightArrowIcon == null) {
            rightArrowIcon = new LazyImageIcon("icons/RightArrow.gif");
        }
        return rightArrowIcon;
    }

    public static Icon getRightArrowInverseIcon() {
        if (rightArrowInverseIcon == null) {
            rightArrowInverseIcon = new LazyImageIcon("icons/RightArrowInverse.gif");
        }
        return rightArrowInverseIcon;
    }
    
    public static Icon getSplitterUpArrowIcon() {
        if (splitterUpArrowIcon == null) {
            splitterUpArrowIcon = new LazyImageIcon("icons/SplitterUpArrow.gif");
        }
        return splitterUpArrowIcon;
    }

    public static Icon getSplitterDownArrowIcon() {
        if (splitterDownArrowIcon == null) {
            splitterDownArrowIcon = new LazyImageIcon("icons/SplitterDownArrow.gif");
        }
        return splitterDownArrowIcon;
    }

    public static Icon getSplitterLeftArrowIcon() {
        if (splitterLeftArrowIcon == null) {
            splitterLeftArrowIcon = new LazyImageIcon("icons/SplitterLeftArrow.gif");
        }
        return splitterLeftArrowIcon;
    }

    public static Icon getSplitterRightArrowIcon() {
        if (splitterRightArrowIcon == null) {
            splitterRightArrowIcon = new LazyImageIcon("icons/SplitterRightArrow.gif");
        }
        return splitterRightArrowIcon;
    }

    public static Icon getSplitterHorBumpIcon() {
        if (splitterHorBumpIcon == null) {
            splitterHorBumpIcon = new LazyImageIcon("icons/SplitterHorBumps.gif");
        }
        return splitterHorBumpIcon;
    }

    public static Icon getSplitterVerBumpIcon() {
        if (splitterVerBumpIcon == null) {
            splitterVerBumpIcon = new LazyImageIcon("icons/SplitterVerBumps.gif");
        }
        return splitterVerBumpIcon;
    }

    public static Icon getComboBoxIcon() {
        return getDownArrowIcon();
    }

    public static Icon getComboBoxInverseIcon() {
        return getDownArrowInverseIcon();
    }

    public static Icon getThumbHorIcon() {
        if (thumbHorIcon == null) {
            thumbHorIcon = new LazyImageIcon("icons/thumb_hor.gif");
        }
        return thumbHorIcon;
    }

    public static Icon getThumbVerIcon() {
        if (thumbVerIcon == null) {
            thumbVerIcon = new LazyImageIcon("icons/thumb_ver.gif");
        }
        return thumbVerIcon;
    }

    public static Icon getThumbHorIconRollover() {
        if (thumbHorIconRollover == null) {
            thumbHorIconRollover = new LazyImageIcon("icons/thumb_hor_rollover.gif");
        }
        return thumbHorIconRollover;
    }

    public static Icon getThumbVerIconRollover() {
        if (thumbVerIconRollover == null) {
            thumbVerIconRollover = new LazyImageIcon("icons/thumb_ver_rollover.gif");
        }
        return thumbVerIconRollover;
    }

//-----------------------------------------------------------------------------------------------------------
    private static class CheckBoxIcon implements Icon {

        private static Icon checkIcon = new LazyImageIcon("icons/CheckSymbol.gif");
        private static Icon checkIconDisabled = new LazyImageIcon("icons/CheckSymbolDisabled.gif");
        private static Icon checkPressedIcon = new LazyImageIcon("icons/CheckPressedSymbol.gif");
        private static final int WIDTH = 15;
        private static final int HEIGHT = 15;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 3;
            }
            
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (c instanceof JCheckBoxMenuItem) {
                g.setColor(Color.white);
                g.fillRect(x, y, WIDTH, HEIGHT);
                if (b.isEnabled()) {
                    g.setColor(AbstractLookAndFeel.getFrameColor());
                } else {
                    g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 40));
                }
                g.drawRect(x, y, WIDTH, HEIGHT);
            } else {
                if (b.isEnabled()) {
                    if (b.isRolloverEnabled() && model.isRollover()) {
                        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x, y, WIDTH, HEIGHT);
                    } else {
                        if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getFocusColors(), x, y, WIDTH, HEIGHT);
                        } else {
                            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getCheckBoxColors(), x, y, WIDTH, HEIGHT);
                        }
                        if (!model.isPressed()) {
                            g.setColor(Color.white);
                            g.drawLine(x + 1, y + 1, x + 1, y + HEIGHT - 2);
                            g.drawLine(x + WIDTH - 1, y + 1, x + WIDTH - 1, y + HEIGHT - 2);
                        }
                    }
                    if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                        Color hiColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFocusFrameColor(), 30);
                        Color loColor = ColorHelper.darker(AbstractLookAndFeel.getTheme().getFocusFrameColor(), 20);
                        g.setColor(hiColor);
                        g.drawRect(x - 1, y - 1, WIDTH + 2, HEIGHT + 2);
                        g.setColor(loColor);
                        g.drawRect(x, y, WIDTH, HEIGHT);
                    } else {
                        g.setColor(AbstractLookAndFeel.getFrameColor());
                        g.drawRect(x, y, WIDTH, HEIGHT);
                    }
                } else {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDisabledColors(), x, y, WIDTH, HEIGHT);
                    g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 40));
                    g.drawRect(x, y, WIDTH, HEIGHT);
                }
            }
            int xi = x + ((WIDTH - checkIcon.getIconWidth()) / 2) + 1;
            int yi = y + ((HEIGHT - checkIcon.getIconHeight()) / 2) + 1;
            if (model.isPressed() && model.isArmed()) {
                checkPressedIcon.paintIcon(c, g, xi, yi);
            } else if (model.isSelected()) {
                if (b.isEnabled()) {
                    checkIcon.paintIcon(c, g, xi, yi);
                } else {
                    checkIconDisabled.paintIcon(c, g, xi, yi);
                }
            }
        }

        public int getIconWidth() {
            return WIDTH + 4;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

//-----------------------------------------------------------------------------------------------------------
    private static class RadioButtonIcon implements Icon {

        private static final int WIDTH = 14;
        private static final int HEIGHT = 14;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!JTattooUtilities.isLeftToRight(c)) {
                x += 3;
            }
            Graphics2D g2D = (Graphics2D) g;
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            Shape savedClip = g.getClip();
            Area clipArea = new Area(new Ellipse2D.Double(x, y, WIDTH + 1, HEIGHT + 1));
            clipArea.intersect(new Area(savedClip));
            g2D.setClip(clipArea);
            if (c instanceof JRadioButtonMenuItem) {
                g.setColor(Color.white);
                g.fillRect(x, y, WIDTH, HEIGHT);
            } else {
                if (b.isEnabled()) {
                    if (b.isRolloverEnabled() && model.isRollover()) {
                        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x, y, WIDTH, HEIGHT);
                    } else {
                        if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getFocusColors(), x, y, WIDTH, HEIGHT);
                        } else {
                            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getCheckBoxColors(), x, y, WIDTH, HEIGHT);
                        }
                    }
                } else {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDisabledColors(), x, y, WIDTH, HEIGHT);
                }
            }
            g2D.setClip(savedClip);
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!model.isRollover()) {
                Composite savedComposite = g2D.getComposite();
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
                g2D.setComposite(alpha);
                g2D.setColor(Color.white);
                g2D.drawOval(x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
                g2D.setComposite(savedComposite);
            }
            if (b.isEnabled()) {
                if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
                    Color hiColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFocusFrameColor(), 30);
                    Color loColor = ColorHelper.darker(AbstractLookAndFeel.getTheme().getFocusFrameColor(), 20);
                    g.setColor(hiColor);
                    g.drawOval(x - 1, y - 1, WIDTH + 2, HEIGHT + 2);
                    g.setColor(loColor);
                    g2D.drawOval(x, y, WIDTH, HEIGHT);
                } else {
                    g.setColor(AbstractLookAndFeel.getFrameColor());
                    g2D.drawOval(x, y, WIDTH, HEIGHT);
                }
            } else {
                g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 40));
                g2D.drawOval(x, y, WIDTH, HEIGHT);
            }

            if (model.isSelected()) {
                if (b.isEnabled()) {
                    g.setColor(Color.black);
                } else {
                    g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 40));
                }
                g2D.fillOval(x + 4, y + 4, WIDTH - 7, HEIGHT - 7);
            }
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
        }

        public int getIconWidth() {
            return WIDTH + 4;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

//-----------------------------------------------------------------------------------------------------------
    public static class MacCloseIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon closerIcon = null;
            Icon pearlIcon = null;
            if (w <= 18) {
                closerIcon = CLOSER_10x10;
                pearlIcon = PEARL_RED_24x24;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_24x24;
                }
            } else if (w <= 22) {
                closerIcon = CLOSER_12x12;
                pearlIcon = PEARL_RED_28x28;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_28x28;
                }
            } else {
                closerIcon = CLOSER_12x12;
                pearlIcon = PEARL_RED_32x32;
                if (!JTattooUtilities.isActive(btn)) {
                    pearlIcon = PEARL_GREY_32x32;
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x += (pearlIcon.getIconWidth() - closerIcon.getIconWidth()) / 2;
                y += (pearlIcon.getIconHeight() - closerIcon.getIconHeight()) / 2;
                closerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }
    
    public static class MacIconIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon iconizerIcon = null;
            Icon pearlIcon = null;
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                if (w <= 18) {
                    iconizerIcon = ICONIZER_10x10;
                    pearlIcon = PEARL_YELLOW_24x24;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_24x24;
                    }
                } else if (w <= 22) {
                    iconizerIcon = ICONIZER_12x12;
                    pearlIcon = PEARL_YELLOW_28x28;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_28x28;
                    }
                } else {
                    iconizerIcon = ICONIZER_12x12;
                    pearlIcon = PEARL_YELLOW_32x32;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_32x32;
                    }
                }
                
            } else {
                if (w <= 18) {
                    iconizerIcon = ICONIZER_10x10;
                    pearlIcon = PEARL_GREEN_24x24;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_24x24;
                    }
                } else if (w <= 22) {
                    iconizerIcon = ICONIZER_12x12;
                    pearlIcon = PEARL_GREEN_28x28;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_28x28;
                    }
                } else {
                    iconizerIcon = ICONIZER_12x12;
                    pearlIcon = PEARL_GREEN_32x32;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_32x32;
                    }
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x += (pearlIcon.getIconWidth() - iconizerIcon.getIconWidth()) / 2;
                y += (pearlIcon.getIconHeight() - iconizerIcon.getIconHeight()) / 2;
                iconizerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }

    public static class MacMaxIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon maximizerIcon = null;
            Icon pearlIcon = null;
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                if (w <= 18) {
                    maximizerIcon = MAXIMIZER_10x10;
                    pearlIcon = PEARL_GREEN_24x24;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_24x24;
                    }
                } else if (w <= 22) {
                    maximizerIcon = MAXIMIZER_12x12;
                    pearlIcon = PEARL_GREEN_28x28;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_28x28;
                    }
                } else {
                    maximizerIcon = MAXIMIZER_12x12;
                    pearlIcon = PEARL_GREEN_32x32;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_32x32;
                    }
                }
            } else {
                if (w <= 18) {
                    maximizerIcon = MAXIMIZER_10x10;
                    pearlIcon = PEARL_YELLOW_24x24;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_24x24;
                    }
                } else if (w <= 22) {
                    maximizerIcon = MAXIMIZER_12x12;
                    pearlIcon = PEARL_YELLOW_28x28;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_28x28;
                    }
                } else {
                    maximizerIcon = MAXIMIZER_12x12;
                    pearlIcon = PEARL_YELLOW_32x32;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_32x32;
                    }
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x += (pearlIcon.getIconWidth() - maximizerIcon.getIconWidth()) / 2;
                y += (pearlIcon.getIconHeight() - maximizerIcon.getIconHeight()) / 2;
                maximizerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }
    
    public static class MacMinIcon implements Icon, UIResource {

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton btn = (AbstractButton) c;
            ButtonModel model = btn.getModel();
            int w = c.getWidth();
            int h = c.getHeight();
            Icon minimizerIcon = null;
            Icon pearlIcon = null;
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                if (w <= 18) {
                    minimizerIcon = MINIMIZER_10x10;
                    pearlIcon = PEARL_GREEN_24x24;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_24x24;
                    }
                } else if (w <= 22) {
                    minimizerIcon = MINIMIZER_12x12;
                    pearlIcon = PEARL_GREEN_28x28;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_28x28;
                    }
                } else {
                    minimizerIcon = MINIMIZER_12x12;
                    pearlIcon = PEARL_GREEN_32x32;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_32x32;
                    }
                }
            } else {
                if (w <= 18) {
                    minimizerIcon = MINIMIZER_10x10;
                    pearlIcon = PEARL_YELLOW_24x24;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_24x24;
                    }
                } else if (w <= 22) {
                    minimizerIcon = MINIMIZER_12x12;
                    pearlIcon = PEARL_YELLOW_28x28;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_28x28;
                    }
                } else {
                    minimizerIcon = MINIMIZER_12x12;
                    pearlIcon = PEARL_YELLOW_32x32;
                    if (!JTattooUtilities.isActive(btn)) {
                        pearlIcon = PEARL_GREY_32x32;
                    }
                }
            }
            x = (w - pearlIcon.getIconWidth()) / 2;
            y = (h - pearlIcon.getIconHeight()) / 2;
            pearlIcon.paintIcon(c, g, x, y);
            if (model.isRollover()) {
                x += (pearlIcon.getIconWidth() - minimizerIcon.getIconWidth()) / 2;
                y += (pearlIcon.getIconHeight() - minimizerIcon.getIconHeight()) / 2;
                minimizerIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconHeight() {
            return 24;
        }

        public int getIconWidth() {
            return 24;
        }
    }
    
//-----------------------------------------------------------------------------------------------------------
    public static class IconSymbol implements Icon {

        private Color foregroundColor = null;
        private Color shadowColor = null;
        private Color inactiveForegroundColor = null;
        private Color inactiveShadowColor = null;
        private Color rolloverColor = null;
        private Insets insets = new Insets(0, 0, 0, 0);

        public IconSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
        }

        public IconSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
            this.insets = insets;
        }

        public IconSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Color inactiveForegroundColor, Color inactiveShadowColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = inactiveForegroundColor;
            this.inactiveShadowColor = inactiveShadowColor;
            this.insets = insets;
        }

        public int getIconHeight() {
            return 16;
        }

        public int getIconWidth() {
            return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.translate(insets.left, insets.top);
            int w = c.getWidth() - insets.left - insets.right;
            int h = c.getHeight() - insets.top - insets.bottom;
            boolean active = JTattooUtilities.isActive((JComponent) c);
            Color color = foregroundColor;
            if (!active) {
                color = inactiveForegroundColor;
            }
            if (c instanceof AbstractButton) {
                if (((AbstractButton) c).getModel().isRollover() && (rolloverColor != null)) {
                    color = rolloverColor;
                }
            }
            int lw = (w / 12) + 1;
            int dx = (w / 5) + 2;
            int dy = dx;

            Stroke savedStroke = g2D.getStroke();
            g2D.setStroke(new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            if (shadowColor != null) {
                if (!active) {
                    g2D.setColor(inactiveShadowColor);
                } else {
                    g2D.setColor(shadowColor);
                }
                g2D.drawLine(dx + 1, h - dy, w - dx + 1, h - dy);
            }
            g2D.setColor(color);
            g2D.drawLine(dx, h - dy - 1, w - dx, h - dy - 1);
            g2D.setStroke(savedStroke);
            g2D.translate(-insets.left, -insets.top);
        }
    }

//-----------------------------------------------------------------------------------------------------------
    public static class MaxSymbol implements Icon {

        private Color foregroundColor = null;
        private Color shadowColor = null;
        private Color rolloverColor = null;
        private Color inactiveForegroundColor = null;
        private Color inactiveShadowColor = null;
        private Insets insets = new Insets(0, 0, 0, 0);

        public MaxSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
        }

        public MaxSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
            this.insets = insets;
        }

        public MaxSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Color inactiveForegroundColor, Color inactiveShadowColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = inactiveForegroundColor;
            this.inactiveShadowColor = inactiveShadowColor;
            this.insets = insets;
        }

        public int getIconHeight() {
            return 16;
        }

        public int getIconWidth() {
            return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.translate(insets.left, insets.top);
            int w = c.getWidth() - insets.left - insets.right;
            int h = c.getHeight() - insets.top - insets.bottom;
            boolean active = JTattooUtilities.isActive((JComponent) c);
            Color color = foregroundColor;
            if (!active) {
                color = inactiveForegroundColor;
            }
            if (c instanceof AbstractButton) {
                if (((AbstractButton) c).getModel().isRollover() && (rolloverColor != null)) {
                    color = rolloverColor;
                }
            }
            int lw = (w / 12);
            int dx = (w / 5) + 1;
            int dy = (h / 5) + 2;

            Stroke savedStroke = g2D.getStroke();
            g2D.setStroke(new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            if (shadowColor != null) {
                if (!active) {
                    g2D.setColor(inactiveShadowColor);
                } else {
                    g2D.setColor(shadowColor);
                }
                g2D.drawRect(dx + 1, dy + 1, w - (2 * dx), h - (2 * dy));
                g2D.drawLine(dx + 1, dy + lw + 1, w - dx, dy + lw + 1);
            }
            g2D.setColor(color);
            g2D.drawRect(dx, dy, w - (2 * dx), h - (2 * dy));
            g2D.drawLine(dx + 1, dy + lw, w - dx, dy + lw);

            g2D.setStroke(savedStroke);
            g2D.translate(-insets.left, -insets.top);
        }
    }

//-----------------------------------------------------------------------------------------------------------
    public static class MinSymbol implements Icon {

        private Color foregroundColor = null;
        private Color shadowColor = null;
        private Color rolloverColor = null;
        private Color inactiveForegroundColor = null;
        private Color inactiveShadowColor = null;
        private Insets insets = new Insets(0, 0, 0, 0);

        public MinSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
        }

        public MinSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
            this.insets = insets;
        }

        public MinSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Color inactiveForegroundColor, Color inactiveShadowColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = inactiveForegroundColor;
            this.inactiveShadowColor = inactiveShadowColor;
            this.insets = insets;
        }

        public int getIconHeight() {
            return 16;
        }

        public int getIconWidth() {
            return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.translate(insets.left, insets.top);
            int w = c.getWidth() - insets.left - insets.right;
            int h = c.getHeight() - insets.top - insets.bottom;

            int lw = (h > 22) ? 2 : 1;
            int delta = w / 4;

            w = Math.min(w, h) - 6;
            h = w;

            int x1 = 3;
            int y1 = 3;
            int w1 = w - delta;
            int h1 = h - delta;

            int x2 = delta + 2;
            int y2 = delta + 2;
            int w2 = w - delta;
            int h2 = h - delta;

            boolean active = JTattooUtilities.isActive((JComponent) c);
            Color ic = foregroundColor;
            Color sc = shadowColor;
            if (!active) {
                ic = inactiveForegroundColor;
                if (sc != null) {
                    sc = inactiveShadowColor;
                }
            }
            if (c instanceof AbstractButton) {
                if (((AbstractButton) c).getModel().isRollover() && (rolloverColor != null)) {
                    ic = rolloverColor;
                }
            }

            Shape savedClip = g2D.getClip();
            Stroke savedStroke = g2D.getStroke();
            g2D.setStroke(new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            Area clipArea = new Area(savedClip);
            clipArea.subtract(new Area(new Rectangle2D.Double(x2, y2, w2, h2)));
            g2D.setClip(clipArea);
            paintRect(g2D, x1, y1, w1, h1, lw, ic, sc);
            g2D.setClip(savedClip);
            paintRect(g2D, x2, y2, w2, h2, lw, ic, sc);

            g2D.setStroke(savedStroke);
            g2D.translate(-insets.left, -insets.top);
        }

        private void paintRect(Graphics2D g2D, int x, int y, int w, int h, int lw, Color iconColor, Color shadowColor) {
            if (shadowColor != null) {
                g2D.setColor(shadowColor);
                g2D.drawRect(x + 1, y + 1, w, h);
                g2D.drawLine(x + 1, y + lw + 1, x + w + 1, y + lw + 1);
            }
            g2D.setColor(iconColor);
            g2D.drawRect(x, y, w, h);
            g2D.drawLine(x, y + lw, x + w, y + lw);

        }
    }

//-----------------------------------------------------------------------------------------------------------
    public static class CloseSymbol implements Icon {

        private Color foregroundColor = null;
        private Color shadowColor = null;
        private Color rolloverColor = null;
        private Color inactiveForegroundColor = null;
        private Color inactiveShadowColor = null;
        private Insets insets = new Insets(0, 0, 0, 0);

        public CloseSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
        }

        public CloseSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.insets = insets;
            this.inactiveForegroundColor = foregroundColor;
            this.inactiveShadowColor = shadowColor;
        }

        public CloseSymbol(Color foregroundColor, Color shadowColor, Color rolloverColor, Color inactiveForegroundColor, Color inactiveShadowColor, Insets insets) {
            this.foregroundColor = foregroundColor;
            this.shadowColor = shadowColor;
            this.rolloverColor = rolloverColor;
            this.inactiveForegroundColor = inactiveForegroundColor;
            this.inactiveShadowColor = inactiveShadowColor;
            this.insets = insets;
        }

        public int getIconHeight() {
            return 16;
        }

        public int getIconWidth() {
            return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.translate(insets.left, insets.top);
            int w = c.getWidth() - insets.left - insets.right;
            int h = c.getHeight() - insets.top - insets.bottom;
            boolean active = JTattooUtilities.isActive((JComponent) c);
            Color color = foregroundColor;
            if (!active) {
                color = inactiveForegroundColor;
            }
            if (c instanceof AbstractButton) {
                if (((AbstractButton) c).getModel().isRollover() && (rolloverColor != null)) {
                    color = rolloverColor;
                }
            }
            int lw = (w / 12) + 1;
            int dx = (w / 5) + 2;
            int dy = dx;

            Stroke savedStroke = g2D.getStroke();
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2D.setStroke(new BasicStroke(lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
            if (shadowColor != null) {
                if (!active) {
                    g2D.setColor(inactiveShadowColor);
                } else {
                    g2D.setColor(shadowColor);
                }
                g2D.drawLine(dx + 1, dy + 1, w - dx + 1, h - dy + 1);
                g2D.drawLine(w - dx + 1, dy + 1, dx + 1, h - dy + 1);
            }
            g2D.setColor(color);
            g2D.drawLine(dx, dy, w - dx, h - dy);
            g2D.drawLine(w - dx, dy, dx, h - dy);

            g2D.setStroke(savedStroke);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
            g2D.translate(-insets.left, -insets.top);
        }
    }
}
