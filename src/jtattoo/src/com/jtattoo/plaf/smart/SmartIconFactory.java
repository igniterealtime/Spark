/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class SmartIconFactory implements AbstractIconFactory {

    private static SmartIconFactory instance = null;

    private SmartIconFactory() {
    }

    public static synchronized SmartIconFactory getInstance() {
        if (instance == null) {
            instance = new SmartIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return SmartIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return SmartIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return SmartIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return SmartIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return SmartIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return SmartIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return SmartIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return SmartIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return SmartIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return SmartIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return SmartIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return SmartIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return SmartIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return SmartIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return SmartIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return SmartIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return SmartIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return SmartIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return SmartIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return SmartIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return SmartIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return SmartIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return SmartIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return SmartIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return SmartIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return SmartIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return SmartIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return SmartIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return SmartIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return SmartIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return SmartIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return SmartIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return SmartIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return SmartIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return SmartIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return SmartIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return SmartIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return SmartIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return SmartIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return SmartIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return SmartIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return SmartIcons.getThumbVerIconRollover();
    }
}
