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
 
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class TextureIconFactory implements AbstractIconFactory {

    private static TextureIconFactory instance = null;

    private TextureIconFactory() {
    }

    public static synchronized TextureIconFactory getInstance() {
        if (instance == null) {
            instance = new TextureIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return TextureIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return TextureIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return TextureIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return TextureIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return TextureIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return TextureIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return TextureIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return TextureIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return TextureIcons.getFileChooserUpFolderIcon();
    }

    public Icon getMenuIcon() {
        return TextureIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return TextureIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return TextureIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return TextureIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return TextureIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return TextureIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return TextureIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return TextureIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return TextureIcons.getComboBoxIcon();
    }

    public Icon getTreeComputerIcon() {
        return TextureIcons.getTreeComputerIcon();
    }

    public Icon getTreeFloppyDriveIcon() {
        return TextureIcons.getTreeFloppyDriveIcon();
    }

    public Icon getTreeHardDriveIcon() {
        return TextureIcons.getTreeHardDriveIcon();
    }

    public Icon getTreeFolderIcon() {
        return TextureIcons.getTreeFolderIcon();
    }

    public Icon getTreeLeafIcon() {
        return TextureIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return TextureIcons.getTreeControlIcon(true);
    }

    public Icon getTreeExpandedIcon() {
        return TextureIcons.getTreeControlIcon(false);
    }

    public Icon getMenuArrowIcon() {
        return TextureIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return TextureIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return TextureIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return TextureIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return TextureIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return TextureIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return TextureIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return TextureIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return TextureIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return TextureIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return TextureIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return TextureIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return TextureIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return TextureIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return TextureIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return TextureIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return TextureIcons.getThumbVerIconRollover();
    }
}
