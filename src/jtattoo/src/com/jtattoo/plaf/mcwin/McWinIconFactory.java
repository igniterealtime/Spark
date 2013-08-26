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
 
package com.jtattoo.plaf.mcwin;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

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
