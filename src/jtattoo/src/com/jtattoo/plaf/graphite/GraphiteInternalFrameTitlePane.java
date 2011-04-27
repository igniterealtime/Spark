/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.graphite;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class GraphiteInternalFrameTitlePane extends BaseInternalFrameTitlePane {
    
    public GraphiteInternalFrameTitlePane(JInternalFrame f) { 
        super(f); 
    }
    
    protected int getHorSpacing() {
        return 0;
    }
    
    protected int getVerSpacing() {
        return 0;
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getWindowBorderColor(), 10));
        } else {
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getWindowInactiveBorderColor(), 10));
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        Graphics2D g2D = (Graphics2D)g;
        Color fc = AbstractLookAndFeel.getWindowTitleForegroundColor();
        if (fc.equals(Color.white)) {
            Color bc = AbstractLookAndFeel.getWindowTitleColorDark();
            g2D.setColor(bc);
            JTattooUtilities.drawString(frame, g, title, x-1, y-1);
            g2D.setColor(ColorHelper.darker(bc, 30));
            JTattooUtilities.drawString(frame, g, title, x+1, y+1);
        }
        g.setColor(fc);
        JTattooUtilities.drawString(frame, g, title, x, y);
    }

}
