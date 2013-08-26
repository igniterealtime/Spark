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
 
package com.jtattoo.plaf.graphite;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class GraphiteIconFactory implements AbstractIconFactory
{
   private static GraphiteIconFactory instance = null;

   private GraphiteIconFactory()
   { }

   public static synchronized GraphiteIconFactory getInstance()
   {
      if (instance == null)
         instance = new GraphiteIconFactory();
      return instance;
   }

   public Icon getOptionPaneErrorIcon()
   { return GraphiteIcons.getOptionPaneErrorIcon(); }

   public Icon getOptionPaneWarningIcon()
   { return GraphiteIcons.getOptionPaneWarningIcon(); }

   public Icon getOptionPaneInformationIcon()
   { return GraphiteIcons.getOptionPaneInformationIcon(); }

   public Icon getOptionPaneQuestionIcon()
   { return GraphiteIcons.getOptionPaneQuestionIcon(); }

   public Icon getFileChooserDetailViewIcon()
   { return GraphiteIcons.getFileChooserDetailViewIcon(); }

   public Icon getFileChooserHomeFolderIcon()
   { return GraphiteIcons.getFileChooserHomeFolderIcon(); }

   public Icon getFileChooserListViewIcon()
   { return GraphiteIcons.getFileChooserListViewIcon(); }

   public Icon getFileChooserNewFolderIcon()
   { return GraphiteIcons.getFileChooserNewFolderIcon(); }

   public Icon getFileChooserUpFolderIcon()
   { return GraphiteIcons.getFileChooserUpFolderIcon(); }

   public Icon getMenuIcon()
   { return GraphiteIcons.getMenuIcon(); }

   public Icon getIconIcon()
   { return GraphiteIcons.getIconIcon(); }

   public Icon getMaxIcon()
   { return GraphiteIcons.getMaxIcon(); }

   public Icon getMinIcon()
   { return GraphiteIcons.getMinIcon(); }

   public Icon getCloseIcon()
   { return GraphiteIcons.getCloseIcon(); }

   public Icon getPaletteCloseIcon()
   { return GraphiteIcons.getPaletteCloseIcon(); }

   public Icon getRadioButtonIcon()
   { return GraphiteIcons.getRadioButtonIcon(); }

   public Icon getCheckBoxIcon()
   { return GraphiteIcons.getCheckBoxIcon(); }

   public Icon getComboBoxIcon()
   { return GraphiteIcons.getComboBoxIcon(); }

   public Icon getTreeComputerIcon()
   { return GraphiteIcons.getTreeComputerIcon(); }

   public Icon getTreeFloppyDriveIcon()
   { return GraphiteIcons.getTreeFloppyDriveIcon(); }

   public Icon getTreeHardDriveIcon()
   { return GraphiteIcons.getTreeHardDriveIcon(); }

   public Icon getTreeFolderIcon()
   { return GraphiteIcons.getTreeFolderIcon(); }

   public Icon getTreeLeafIcon()
   { return GraphiteIcons.getTreeLeafIcon(); }

   public Icon getTreeCollapsedIcon()
   { return GraphiteIcons.getTreeControlIcon(true); }

   public Icon getTreeExpandedIcon()
   { return GraphiteIcons.getTreeControlIcon(false); }

   public Icon getMenuArrowIcon()
   { return GraphiteIcons.getMenuArrowIcon(); }

   public Icon getMenuCheckBoxIcon()
   { return GraphiteIcons.getMenuCheckBoxIcon(); }

   public Icon getMenuRadioButtonIcon()
   { return GraphiteIcons.getMenuRadioButtonIcon(); }

   public Icon getUpArrowIcon()
   { return GraphiteIcons.getUpArrowIcon(); }

   public Icon getDownArrowIcon()
   { return GraphiteIcons.getDownArrowIcon(); }

   public Icon getLeftArrowIcon()
   { return GraphiteIcons.getLeftArrowIcon(); }

   public Icon getRightArrowIcon()
   { return GraphiteIcons.getRightArrowIcon(); }

   public Icon getSplitterDownArrowIcon()
   { return GraphiteIcons.getSplitterDownArrowIcon(); }

   public Icon getSplitterHorBumpIcon()
   { return GraphiteIcons.getSplitterHorBumpIcon(); }

   public Icon getSplitterLeftArrowIcon()
   { return GraphiteIcons.getSplitterLeftArrowIcon(); }

   public Icon getSplitterRightArrowIcon()
   { return GraphiteIcons.getSplitterRightArrowIcon(); }

   public Icon getSplitterUpArrowIcon()
   { return GraphiteIcons.getSplitterUpArrowIcon(); }

   public Icon getSplitterVerBumpIcon()
   { return GraphiteIcons.getSplitterVerBumpIcon(); }

   public Icon getThumbHorIcon()
   { return GraphiteIcons.getThumbHorIcon(); }

   public Icon getThumbVerIcon()
   { return GraphiteIcons.getThumbVerIcon(); }

   public Icon getThumbHorIconRollover()
   { return GraphiteIcons.getThumbHorIconRollover(); }

   public Icon getThumbVerIconRollover()
   { return GraphiteIcons.getThumbVerIconRollover(); }
}
