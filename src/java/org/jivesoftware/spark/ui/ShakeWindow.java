/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.spark.SparkManager;

import javax.swing.Timer;
import javax.swing.JFrame;

import java.awt.Point;
import java.awt.Window;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShakeWindow {

    public static final int SHAKE_DISTANCE = 10;
    public static final double SHAKE_CYCLE = 50;
    public static final int SHAKE_DURATION = 1000;
    public static final int SHAKE_UPDATE = 5;

    private Window window;
    private Point naturalLocation;
    private long startTime;
    private Timer shakeTimer;
    private final double TWO_PI = Math.PI * 2.0;
    private boolean added = false;

    public ShakeWindow(Window d) {
        window = d;
    }

    public void startShake() {
        if(window instanceof JFrame){
            JFrame f = (JFrame)window;
            f.setState(Frame.NORMAL);
            f.setVisible(true);
        }
        SparkManager.getNativeManager().flashWindow(window);

        naturalLocation = window.getLocation();
        startTime = System.currentTimeMillis();
        shakeTimer =
                new Timer(SHAKE_UPDATE,
                        new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                // calculate elapsed time
                                long elapsed = System.currentTimeMillis() - startTime;
                                // use sin to calculate an x-offset
                                double waveOffset = (elapsed % SHAKE_CYCLE) /
                                        SHAKE_CYCLE;
                                double angle = waveOffset * TWO_PI;

                                // offset the x-location by an amount
                                // proportional to the sine, up to
                                // shake_distance
                                int shakenX = (int)((Math.sin(angle) *
                                        SHAKE_DISTANCE) +
                                        naturalLocation.x);

                                int shakenY;
                                if (added) {
                                    shakenY = naturalLocation.y - 10;
                                    added = false;
                                }
                                else {
                                    shakenY = naturalLocation.y + 10;
                                    added = true;
                                }

                                window.setLocation(shakenX, shakenY);
                                window.repaint();

                                // should we stop timer?
                                if (elapsed >= SHAKE_DURATION) stopShake();
                            }
                        }
                );
        shakeTimer.start();
    }

    public void stopShake() {
        shakeTimer.stop();
        window.setLocation(naturalLocation);
        window.repaint();

        SparkManager.getNativeManager().stopFlashing(window);
    }


}
