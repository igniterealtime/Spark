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

import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public interface AbstractIconFactory {

    public Icon getOptionPaneErrorIcon();

    public Icon getOptionPaneWarningIcon();

    public Icon getOptionPaneInformationIcon();

    public Icon getOptionPaneQuestionIcon();

    public Icon getFileChooserDetailViewIcon();

    public Icon getFileChooserHomeFolderIcon();

    public Icon getFileChooserListViewIcon();

    public Icon getFileChooserNewFolderIcon();

    public Icon getFileChooserUpFolderIcon();

    public Icon getMenuIcon();

    public Icon getIconIcon();

    public Icon getMaxIcon();

    public Icon getMinIcon();

    public Icon getCloseIcon();

    public Icon getPaletteCloseIcon();

    public Icon getRadioButtonIcon();

    public Icon getCheckBoxIcon();

    public Icon getComboBoxIcon();

    public Icon getTreeComputerIcon();

    public Icon getTreeFloppyDriveIcon();

    public Icon getTreeHardDriveIcon();

    public Icon getTreeFolderIcon();

    public Icon getTreeLeafIcon();

    public Icon getTreeCollapsedIcon();

    public Icon getTreeExpandedIcon();

    public Icon getMenuArrowIcon();

    public Icon getMenuCheckBoxIcon();

    public Icon getMenuRadioButtonIcon();

    public Icon getUpArrowIcon();

    public Icon getDownArrowIcon();

    public Icon getLeftArrowIcon();

    public Icon getRightArrowIcon();

    public Icon getSplitterUpArrowIcon();

    public Icon getSplitterDownArrowIcon();

    public Icon getSplitterLeftArrowIcon();

    public Icon getSplitterRightArrowIcon();

    public Icon getSplitterHorBumpIcon();

    public Icon getSplitterVerBumpIcon();

    public Icon getThumbHorIcon();

    public Icon getThumbVerIcon();

    public Icon getThumbHorIconRollover();

    public Icon getThumbVerIconRollover();
}
