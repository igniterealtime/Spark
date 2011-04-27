/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class PulsarIconFactory implements AbstractIconFactory
{
   private static PulsarIconFactory instance = null;
   
   private PulsarIconFactory()
   { }
   
   public static synchronized PulsarIconFactory getInstance()
   {
      if (instance == null)
         instance = new PulsarIconFactory();
      return instance;
   }
   
   public Icon getOptionPaneErrorIcon()
   { return PulsarIcons.getOptionPaneErrorIcon(); }
   
   public Icon getOptionPaneWarningIcon()
   { return PulsarIcons.getOptionPaneWarningIcon(); }
   
   public Icon getOptionPaneInformationIcon()
   { return PulsarIcons.getOptionPaneInformationIcon(); }
   
   public Icon getOptionPaneQuestionIcon()
   { return PulsarIcons.getOptionPaneQuestionIcon(); }
   
   public Icon getFileChooserDetailViewIcon()
   { return PulsarIcons.getFileChooserDetailViewIcon(); }
      
   public Icon getFileChooserHomeFolderIcon()
   { return PulsarIcons.getFileChooserHomeFolderIcon(); }
   
   public Icon getFileChooserListViewIcon()
   { return PulsarIcons.getFileChooserListViewIcon(); }
   
   public Icon getFileChooserNewFolderIcon()
   { return PulsarIcons.getFileChooserNewFolderIcon(); }
   
   public Icon getFileChooserUpFolderIcon()
   { return PulsarIcons.getFileChooserUpFolderIcon(); }
   
   public Icon getMenuIcon()
   { return PulsarIcons.getMenuIcon(); }
   
   public Icon getIconIcon()
   { return PulsarIcons.getIconIcon(); }
   
   public Icon getMaxIcon()
   { return PulsarIcons.getMaxIcon(); }
   
   public Icon getMinIcon()
   { return PulsarIcons.getMinIcon(); }
   
   public Icon getCloseIcon()
   { return PulsarIcons.getCloseIcon(); }
   
   public Icon getPaletteCloseIcon()
   { return PulsarIcons.getPaletteCloseIcon(); }
   
   public Icon getRadioButtonIcon()
   { return PulsarIcons.getRadioButtonIcon(); }
      
   public Icon getCheckBoxIcon()
   { return PulsarIcons.getCheckBoxIcon(); }
     
   public Icon getComboBoxIcon()
   { return PulsarIcons.getComboBoxIcon(); }
     
   public Icon getTreeComputerIcon()
   { return PulsarIcons.getTreeComputerIcon(); }
     
   public Icon getTreeFloppyDriveIcon()
   { return PulsarIcons.getTreeFloppyDriveIcon(); }
     
   public Icon getTreeHardDriveIcon()
   { return PulsarIcons.getTreeHardDriveIcon(); }
     
   public Icon getTreeFolderIcon()
   { return PulsarIcons.getTreeFolderIcon(); }
     
   public Icon getTreeLeafIcon()
   { return PulsarIcons.getTreeLeafIcon(); }
     
   public Icon getTreeCollapsedIcon()
   { return PulsarIcons.getTreeControlIcon(true); }
     
   public Icon getTreeExpandedIcon()
   { return PulsarIcons.getTreeControlIcon(false); }
     
   public Icon getMenuArrowIcon()
   { return PulsarIcons.getMenuArrowIcon(); }
     
   public Icon getMenuCheckBoxIcon()
   { return PulsarIcons.getMenuCheckBoxIcon(); }
     
   public Icon getMenuRadioButtonIcon()
   { return PulsarIcons.getMenuRadioButtonIcon(); }
     
   public Icon getUpArrowIcon()
   { return PulsarIcons.getUpArrowIcon(); }
     
   public Icon getDownArrowIcon()
   { return PulsarIcons.getDownArrowIcon(); }
     
   public Icon getLeftArrowIcon()
   { return PulsarIcons.getLeftArrowIcon(); }
     
   public Icon getRightArrowIcon()
   { return PulsarIcons.getRightArrowIcon(); }
   
   public Icon getSplitterDownArrowIcon()
   { return PulsarIcons.getSplitterDownArrowIcon(); }
   
   public Icon getSplitterHorBumpIcon()
   { return PulsarIcons.getSplitterHorBumpIcon(); }
   
   public Icon getSplitterLeftArrowIcon()
   { return PulsarIcons.getSplitterLeftArrowIcon(); }
   
   public Icon getSplitterRightArrowIcon()
   { return PulsarIcons.getSplitterRightArrowIcon(); }
   
   public Icon getSplitterUpArrowIcon()
   { return PulsarIcons.getSplitterUpArrowIcon(); }
   
   public Icon getSplitterVerBumpIcon()
   { return PulsarIcons.getSplitterVerBumpIcon(); }

   public Icon getThumbHorIcon()
   { return PulsarIcons.getThumbHorIcon(); }

   public Icon getThumbVerIcon()
   { return PulsarIcons.getThumbVerIcon(); }

   public Icon getThumbHorIconRollover()
   { return PulsarIcons.getThumbHorIconRollover(); }

   public Icon getThumbVerIconRollover()
   { return PulsarIcons.getThumbVerIconRollover(); }
}
