/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.roar.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.jivesoftware.spark.roar.displaytype.RoarDisplayType;

import com.sun.awt.AWTUtilities;

public class RoarPanel {
    public static int WIDTH = 300;
    public static int HEIGHT = 80;


    /**
     * Creates the WindowGui
     * @param icon
     * @param head
     * @param body
     * @param posx
     * @param posy
     * @param backgroundcolor
     * @param headerColor
     * @param messageColor
     * @return
     */
    private static JWindow createWindow(Icon icon, String head, String body,
	    int posx, int posy, Color backgroundcolor, Color headerColor, Color messageColor) {

	final JWindow window = new JWindow();
	JPanel windowpanel = new JPanel(new GridBagLayout());
	windowpanel.setBackground(backgroundcolor);

	AWTUtilities.setWindowShape(window, new RoundRectangle2D.Float(0, 0,
		WIDTH, HEIGHT, 20, 20));
	AWTUtilities.setWindowOpaque(window, true);


	JLabel text = new JLabel("<HTML>" + body + "</HTML>");
	text.setForeground(messageColor);

	JLabel header = new JLabel(head);
	header.setForeground(headerColor);
	header.setFont(new Font(header.getFont().getName(), Font.BOLD, header
		.getFont().getSize() + 2));

	windowpanel.add(new JLabel(icon), new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	windowpanel.add(header, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
	windowpanel.add(text, new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

	window.add(windowpanel);
	window.setSize(WIDTH, HEIGHT);
	window.setLocation(posx - (WIDTH+5), posy+5);
	window.setAlwaysOnTop(true);

	return window;
    }
    
    /**
     * Fades the window and sets it visible
     * @param window
     */
    private static void fadein(JWindow window)
    {
	AWTUtilities.setWindowOpacity(window, 0.3f);
	AWTUtilities.setWindowOpacity(window, 0.5f);
	AWTUtilities.setWindowOpacity(window, 0.9f);
	window.setVisible(true);
    }
    
    /**
     * Creates a popupwindow with specified items, displays it for given time,
     * and notifies its owner on closure
     * 
     * @param owner
     *            , the owner of this Panel
     * @param icon
     *            , the icon to display
     * @param head
     *            , the header
     * @param text
     *            , the message body
     * @param x
     *            , the x locaiton on screen
     * @param y
     *            , the y location on screen
     * @param duration
     *            , the display duration
     * @param backgroundcolor
     *            , the background color
     * @param headercolor
     *            , the headertext color
     * @param textcolor
     *            , the messagebody color
     */
    public static void popupWindow(final RoarDisplayType owner, Icon icon, String head, String text, int x, int y, int duration, Color backgroundcolor, Color headercolor, Color textcolor, final Action action) {
	
	final JWindow window = createWindow(icon, head, text, x, y,backgroundcolor, headercolor, textcolor);
	fadein(window);
	
	
	final TimerTask closeTimer = new TimerTask() {
	    @Override
	    public void run() {
		if (window != null) {
		    owner.closingRoarPanel(window.getX(),window.getY());
		    window.dispose();   
		}
	    }
	};

	Timer t = new Timer();
	t.schedule(closeTimer, duration);
	
	window.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		action.actionPerformed(null);
		closeTimer.run();
	    }
	});
    }

}
