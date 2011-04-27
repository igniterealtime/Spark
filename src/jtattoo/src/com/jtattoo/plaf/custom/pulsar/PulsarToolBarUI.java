/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class PulsarToolBarUI extends AbstractToolBarUI
{
   public static ComponentUI createUI(JComponent c) { 
       return new PulsarToolBarUI(); 
   }
   
   public Border getRolloverBorder() { 
       return PulsarBorders.getRolloverToolButtonBorder();
   }
   
   public Border getNonRolloverBorder() { 
       return PulsarBorders.getToolButtonBorder();
   }
   
   public boolean isButtonOpaque() { 
       return false; 
   }
   
}

