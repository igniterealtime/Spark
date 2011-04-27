/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AeroToolBarUI extends AbstractToolBarUI
{
   public static ComponentUI createUI(JComponent c) { 
       return new AeroToolBarUI(); 
   }
   
   public Border getRolloverBorder() { 
       return AeroBorders.getRolloverToolButtonBorder(); 
   }
   
   public Border getNonRolloverBorder() { 
       return AeroBorders.getToolButtonBorder(); 
   }
   
   public boolean isButtonOpaque() { 
       return false; 
   }
   
}

