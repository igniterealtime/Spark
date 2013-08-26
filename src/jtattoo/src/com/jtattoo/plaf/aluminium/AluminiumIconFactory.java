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
 
package com.jtattoo.plaf.aluminium;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class AluminiumIconFactory implements AbstractIconFactory {

    private static AluminiumIconFactory instance = null;

    private AluminiumIconFactory() {
    }

    public static synchronized AluminiumIconFactory getInstance() {
        if (instance == null) {
            instance = new AluminiumIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return AluminiumIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return AluminiumIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return AluminiumIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return AluminiumIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return AluminiumIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return AluminiumIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return AluminiumIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return AluminiumIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return AluminiumIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return AluminiumIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return AluminiumIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return AluminiumIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return AluminiumIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return AluminiumIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return AluminiumIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return AluminiumIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return AluminiumIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return AluminiumIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return AluminiumIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return AluminiumIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return AluminiumIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return AluminiumIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return AluminiumIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return AluminiumIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return AluminiumIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return AluminiumIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return AluminiumIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return AluminiumIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return AluminiumIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return AluminiumIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return AluminiumIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return AluminiumIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return AluminiumIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return AluminiumIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return AluminiumIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return AluminiumIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return AluminiumIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return AluminiumIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return AluminiumIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return AluminiumIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return AluminiumIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return AluminiumIcons.getThumbVerIconRollover();
    }
}
