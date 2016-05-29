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
 
package com.jtattoo.plaf.aero;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class AeroIconFactory implements AbstractIconFactory {

    private static AeroIconFactory instance = null;

    private AeroIconFactory() {
    }

    public static synchronized AeroIconFactory getInstance() {
        if (instance == null) {
            instance = new AeroIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return AeroIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return AeroIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return AeroIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return AeroIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return AeroIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return AeroIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return AeroIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return AeroIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return AeroIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return AeroIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return AeroIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return AeroIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return AeroIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return AeroIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return AeroIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return AeroIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return AeroIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return AeroIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return AeroIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return AeroIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return AeroIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return AeroIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return AeroIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return AeroIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return AeroIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return AeroIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return AeroIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return AeroIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return AeroIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return AeroIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return AeroIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return AeroIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return AeroIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return AeroIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return AeroIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return AeroIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return AeroIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return AeroIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return AeroIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return AeroIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return AeroIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return AeroIcons.getThumbVerIconRollover();
    }
}
