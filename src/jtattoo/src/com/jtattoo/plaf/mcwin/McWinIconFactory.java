/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import javax.swing.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinIconFactory implements AbstractIconFactory {

    private static McWinIconFactory instance = null;

    private McWinIconFactory() {
    }

    public static synchronized McWinIconFactory getInstance() {
        if (instance == null) {
            instance = new McWinIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return McWinIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return McWinIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return McWinIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return McWinIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return McWinIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return McWinIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return McWinIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return McWinIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return McWinIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return McWinIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return McWinIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return McWinIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return McWinIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return McWinIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return McWinIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return McWinIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return McWinIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return McWinIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return McWinIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return McWinIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return McWinIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return McWinIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return McWinIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return McWinIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return McWinIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return McWinIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return McWinIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return McWinIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return McWinIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return McWinIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return McWinIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return McWinIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return McWinIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return McWinIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return McWinIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return McWinIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return McWinIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return McWinIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return McWinIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return McWinIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return McWinIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return McWinIcons.getThumbVerIconRollover();
    }
}
