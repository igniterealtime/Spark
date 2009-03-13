/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.log.Log;

/**
 * The <code>LinkLabel</code> class is a JLabel subclass
 * to mimic an html link. When clicked, it launches the specified url
 * in the default browser.
 *
 * @author Derek DeMoro
 */
final public class LinkLabel extends JLabel implements MouseListener {

	private static final long serialVersionUID = 454820993140807217L;
	// cursors used in url-link related displays and default display
    private Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private Cursor LINK_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    private Color rolloverTextColor;
    private Color foregroundTextColor;
    private String labelURL;

    private boolean invokeBrowser;

    /**
     * @param text            the text for the label.
     * @param url             the url to launch when this label is clicked on.
     * @param foregroundColor the text color for the label.
     * @param rolloverColor   the text color to be used when the mouse is over the label.
     */
    public LinkLabel(String text,
                     String url,
                     Color foregroundColor,
                     Color rolloverColor) {
        super(text);

        rolloverTextColor = rolloverColor;
        foregroundTextColor = foregroundColor;
        labelURL = url;

        this.addMouseListener(this);

        this.setForeground(foregroundTextColor);
        this.setVerticalTextPosition(JLabel.TOP);
    }

    public void setInvokeBrowser(boolean invoke) {
        invokeBrowser = invoke;
    }

    /**
     * MouseListener implementation.
     *
     * @param me the MouseEvent.
     */
    public void mouseClicked(MouseEvent me) {
        if (invokeBrowser) {
            try {
                BrowserLauncher.openURL(labelURL);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
        this.setForeground(rolloverTextColor);
        this.setCursor(LINK_CURSOR);
    }

    public void mouseExited(MouseEvent me) {
        this.setForeground(foregroundTextColor);
        this.setCursor(DEFAULT_CURSOR);
    }

}
