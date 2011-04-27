/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import javax.swing.*;
import javax.swing.plaf.*;
import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class PulsarRootPaneUI extends BaseRootPaneUI
{
   public static ComponentUI createUI(JComponent c) { 
       return new PulsarRootPaneUI(); 
   }
   
   public BaseTitlePane createTitlePane(JRootPane root) { 
       return new PulsarTitlePane(root, this);
   }   
}
