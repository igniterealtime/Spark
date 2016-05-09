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
 
package com.jtattoo.plaf.fast;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

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
