/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public McWinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected void createButtons() {
        iconButton = new BaseTitleButton(iconifyAction, ICONIFY, iconIcon, 0.6f);
        maxButton = new BaseTitleButton(maximizeAction, MAXIMIZE, maxIcon, 0.6f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 0.6f);
        setButtonIcons();
    }

    public void paintBorder(Graphics g) {
        if (JTattooUtilities.isActive(this)) {
            g.setColor(McWinLookAndFeel.getWindowBorderColor());
        } else {
            g.setColor(McWinLookAndFeel.getWindowInactiveBorderColor());
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isActive()) {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowTitleColorLight(), 50));
            JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        } else {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowInactiveTitleColorLight(), 50));
            JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        }
    }
}
