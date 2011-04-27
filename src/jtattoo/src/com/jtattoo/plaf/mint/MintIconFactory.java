/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class MintIconFactory implements AbstractIconFactory {

    private static MintIconFactory instance = null;

    private MintIconFactory() {
    }

    public static synchronized MintIconFactory getInstance() {
        if (instance == null) {
            instance = new MintIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return MintIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return MintIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return MintIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return MintIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return MintIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return MintIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return MintIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return MintIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return MintIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return MintIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return MintIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return MintIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return MintIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return MintIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return MintIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return MintIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return MintIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return MintIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return MintIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return MintIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return MintIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return MintIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return MintIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return MintIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return MintIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return MintIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return MintIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return MintIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return MintIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return MintIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return MintIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return MintIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return MintIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return MintIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return MintIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return MintIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return MintIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return MintIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return MintIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return MintIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return MintIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return MintIcons.getThumbVerIconRollover();
    }
}
