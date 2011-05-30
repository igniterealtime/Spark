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
package org.jivesoftware.sparkplugin.ui.components;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 * Component to attach itself to a Frame.
 *
 * @author Derek DeMoro
 */
public class SnapWindow extends JWindow implements ComponentListener, WindowListener, FocusListener, MouseMotionListener {

	private static final long serialVersionUID = -3546188378308911117L;

	private final JFrame parentFrame;

    private int preferredWidth = 300;

    private boolean isActive = false;

    public SnapWindow(final JFrame parentFrame) {
        this.parentFrame = parentFrame;

        parentFrame.addComponentListener(this);
        parentFrame.addMouseMotionListener(this);
        parentFrame.addWindowListener(this);

        adjustWindow();

        parentFrame.addFocusListener(this);

        setFocusableWindowState(false);
    }


    public void focusGained(FocusEvent e) {

    }

    public void focusLost(FocusEvent e) {
    }

    public void componentResized(ComponentEvent componentEvent) {
        adjustWindow();
    }

    public void componentMoved(ComponentEvent componentEvent) {
        adjustWindow();
    }

    public void componentShown(ComponentEvent componentEvent) {
        this.setVisible(true);
    }

    public void componentHidden(ComponentEvent componentEvent) {
        this.setVisible(false);
    }

    public void setPreferredWidth(int width) {
        this.preferredWidth = width;
    }

    private void adjustWindow() {
        if (!parentFrame.isVisible()) {
            return;
        }

        Point mainWindowLocation = parentFrame.getLocationOnScreen();

        int x = (int)mainWindowLocation.getX() + parentFrame.getWidth();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = getWidth();
        if (width == 0) {
            width = preferredWidth;
        }
        if ((int)screenSize.getWidth() - width < x) {
            x = (int)mainWindowLocation.getX() - width;
        }


        setSize(preferredWidth, (int)parentFrame.getHeight());
        setLocation(x, (int)mainWindowLocation.getY());
    }

    public void close() {
        parentFrame.removeComponentListener(this);
        parentFrame.removeWindowListener(this);
        parentFrame.removeMouseMotionListener(this);
        dispose();
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {

    }

    public void windowIconified(WindowEvent windowEvent) {
        setVisible(false);
    }

    public void windowDeiconified(WindowEvent windowEvent) {
        setVisible(true);
    }

    public void windowActivated(WindowEvent windowEvent) {
        if (!isActive) {
            setVisible(true);
            toFront();
            isActive = true;
            adjustWindow();
        }
    }

    public void windowDeactivated(WindowEvent windowEvent) {
        setVisible(false);
        isActive = false;
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        adjustWindow();
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        adjustWindow();
    }
}
