/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import javax.swing.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class FastIconFactory implements AbstractIconFactory {

    private static FastIconFactory instance = null;

    private FastIconFactory() {
    }

    public static synchronized FastIconFactory getInstance() {
        if (instance == null) {
            instance = new FastIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return FastIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return FastIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return FastIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return FastIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return FastIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return FastIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return FastIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return FastIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return FastIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return FastIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return FastIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return FastIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return FastIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return FastIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return FastIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return FastIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return FastIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return FastIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return FastIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return FastIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return FastIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return FastIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return FastIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return FastIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return FastIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return FastIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return FastIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return FastIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return FastIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return FastIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return FastIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return FastIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return FastIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return FastIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return FastIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return FastIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return FastIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return FastIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return FastIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return FastIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return FastIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return FastIcons.getThumbVerIconRollover();
    }
}
