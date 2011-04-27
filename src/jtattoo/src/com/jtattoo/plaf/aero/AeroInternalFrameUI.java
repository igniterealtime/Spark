/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AeroInternalFrameUI extends BaseInternalFrameUI {

    public AeroInternalFrameUI(JInternalFrame b) { 
        super(b); 
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new AeroInternalFrameUI((JInternalFrame)c); 
    }
    
    protected JComponent createNorthPane(JInternalFrame w)  {
        titlePane = new AeroInternalFrameTitlePane(w);
        return titlePane;
    }
    
}

