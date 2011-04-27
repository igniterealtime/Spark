/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class PulsarInternalFrameUI extends BaseInternalFrameUI {

    public PulsarInternalFrameUI(JInternalFrame b) { 
        super(b); 
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new PulsarInternalFrameUI((JInternalFrame)c); 
    }
    
    protected JComponent createNorthPane(JInternalFrame w)  {
        titlePane = new PulsarInternalFrameTitlePane(w);
        return titlePane;
    }
    
}

