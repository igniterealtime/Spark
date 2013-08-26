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
 
package com.jtattoo.plaf.acryl;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

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
