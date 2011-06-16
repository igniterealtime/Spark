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
package org.jivesoftware.spark.ui;

import org.jivesoftware.spark.SparkManager;

import javax.swing.Timer;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
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
    private Timer moveTimer;
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
    
    /**
     * punishes the User by moving the Chatwindow around for 10 seconds
     */
    public void startRandomMovement(final int seconds)
    {
	if(window instanceof JFrame){
            JFrame f = (JFrame)window;
            f.setState(Frame.NORMAL);
            f.setVisible(true);
        }
        SparkManager.getNativeManager().flashWindow(window);
        
        final long startTime = System.currentTimeMillis()/1000L;
        
	moveTimer = new Timer(5, new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		double x = Math.random()*10000 % d.getWidth();
		double y = Math.random()*10000 % d.getHeight();
		int xx = Math.round(Math.round(x));
		int yy = Math.round(Math.round(y));
		window.setLocation(xx,yy);
		window.repaint();
		
		long now = System.currentTimeMillis()/1000L;
		long diff = now-startTime;
		System.out.println(diff);
		if(diff > seconds)
		{
		    moveTimer.stop();
		}

	        }
	    });
	
	moveTimer.start();
	
    }

}
