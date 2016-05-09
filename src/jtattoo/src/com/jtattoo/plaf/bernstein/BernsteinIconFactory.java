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
 
package com.jtattoo.plaf.bernstein;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

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
