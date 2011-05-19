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
package org.jivesoftware.spark.roar;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;

public class RoarPanel {
    private static int _width = 300;
    private static int _height = 80;


    private static JWindow createWindow(Icon icon, String head, String body,
	    int posx, int posy, Color backgroundcolor, Color headerColor, Color messageColor) {

	final JWindow window = new JWindow();
	JPanel windowpanel = new JPanel(new GridBagLayout());
	windowpanel.setBackground(backgroundcolor);

	AWTUtilities.setWindowShape(window, new RoundRectangle2D.Float(0, 0,
		_width, _height, 20, 20));
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
	window.setSize(_width, _height);
	window.setLocation(posx - (_width+5), posy+5);
	window.setAlwaysOnTop(true);

	return window;
    }
    
    private static void fadein(JWindow window)
    {
	AWTUtilities.setWindowOpacity(window, 0.9f);
	window.setVisible(true);
    }
    
    public static void popupWindow(final RoarMessageListener owner, Icon icon, String head, String text, int x, int y, int duration, Color backgroundcolor, Color headercolor, Color textcolor) {
	
	final JWindow window = createWindow(icon, head, text, x, y,backgroundcolor, headercolor, textcolor);
	fadein(window);
	

	TimerTask closeTimer = new TimerTask() {
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
    }

}
