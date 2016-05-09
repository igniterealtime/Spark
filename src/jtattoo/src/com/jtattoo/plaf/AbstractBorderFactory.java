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

import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public interface AbstractBorderFactory {

    public Border getFocusFrameBorder();

    public Border getButtonBorder();

    public Border getToggleButtonBorder();

    public Border getTextBorder();

    public Border getSpinnerBorder();

    public Border getTextFieldBorder();

    public Border getComboBoxBorder();

    public Border getTableHeaderBorder();

    public Border getTableScrollPaneBorder();

    public Border getScrollPaneBorder();

    public Border getTabbedPaneBorder();

    public Border getMenuBarBorder();

    public Border getMenuItemBorder();

    public Border getPopupMenuBorder();

    public Border getInternalFrameBorder();

    public Border getPaletteBorder();

    public Border getToolBarBorder();

    public Border getDesktopIconBorder();

    public Border getProgressBarBorder();
}

