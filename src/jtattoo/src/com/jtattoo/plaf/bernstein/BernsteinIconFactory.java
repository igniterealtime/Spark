/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import javax.swing.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class BernsteinIconFactory implements AbstractIconFactory {

    private static BernsteinIconFactory instance = null;

    private BernsteinIconFactory() {
    }

    public static synchronized BernsteinIconFactory getInstance() {
        if (instance == null) {
            instance = new BernsteinIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return BernsteinIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return BernsteinIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return BernsteinIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return BernsteinIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return BernsteinIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return BernsteinIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return BernsteinIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return BernsteinIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return BernsteinIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return BernsteinIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return BernsteinIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return BernsteinIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return BernsteinIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return BernsteinIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return BernsteinIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return BernsteinIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return BernsteinIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return BernsteinIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return BernsteinIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return BernsteinIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return BernsteinIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return BernsteinIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return BernsteinIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return BernsteinIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return BernsteinIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return BernsteinIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return BernsteinIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return BernsteinIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return BernsteinIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return BernsteinIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return BernsteinIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return BernsteinIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return BernsteinIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return BernsteinIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return BernsteinIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return BernsteinIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return BernsteinIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return BernsteinIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return BernsteinIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return BernsteinIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return BernsteinIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return BernsteinIcons.getThumbVerIconRollover();
    }
}
