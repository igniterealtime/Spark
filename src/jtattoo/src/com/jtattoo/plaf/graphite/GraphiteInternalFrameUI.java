/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.graphite;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class GraphiteInternalFrameUI extends BaseInternalFrameUI {

    public GraphiteInternalFrameUI(JInternalFrame b) { 
        super(b); 
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new GraphiteInternalFrameUI((JInternalFrame)c); 
    }
    
    protected JComponent createNorthPane(JInternalFrame w)  {
        titlePane = new GraphiteInternalFrameTitlePane(w);
        return titlePane;
    }
    
}

