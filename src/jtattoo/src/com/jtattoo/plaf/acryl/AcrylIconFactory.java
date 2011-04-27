/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.acryl;

import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AcrylIconFactory implements AbstractIconFactory {

    private static AcrylIconFactory instance = null;

    private AcrylIconFactory() {
    }

    public static synchronized AcrylIconFactory getInstance() {
        if (instance == null) {
            instance = new AcrylIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return AcrylIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return AcrylIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return AcrylIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return AcrylIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return AcrylIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return AcrylIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return AcrylIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return AcrylIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return AcrylIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return AcrylIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return AcrylIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return AcrylIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return AcrylIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return AcrylIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return AcrylIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return AcrylIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return AcrylIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return AcrylIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return AcrylIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return AcrylIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return AcrylIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return AcrylIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return AcrylIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return AcrylIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return AcrylIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return AcrylIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return AcrylIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return AcrylIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return AcrylIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return AcrylIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return AcrylIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return AcrylIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return AcrylIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return AcrylIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return AcrylIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return AcrylIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return AcrylIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return AcrylIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return AcrylIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return AcrylIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return AcrylIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return AcrylIcons.getThumbVerIconRollover();
    }
}
