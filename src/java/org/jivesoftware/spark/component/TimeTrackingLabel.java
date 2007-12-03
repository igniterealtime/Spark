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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * UI to show the time in a chat room.
 */
public class TimeTrackingLabel extends JLabel {
    private Date startTime;
    private JComponent parentComponent;

    private final String HOURS = "h";
    private final String MINUTES = "min";
    private final String SECONDS = "sec";

    private final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private final long MS_IN_AN_HOUR = 1000 * 60 * 60;
    private final long MS_IN_A_MINUTE = 1000 * 60;
    private final long MS_IN_A_SECOND = 1000;
    final Timer timer;

    /**
     * Construct a new Label using the start time.
     *
     * @param startingTime the start time.
     * @param parent       the parent component.
     */
    public TimeTrackingLabel(Date startingTime, JComponent parent) {
        startTime = startingTime;
        parentComponent = parent;

        // Set default
        setText("0 sec");

        ActionListener updateTime = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Date currentTime = new Date();
                long diff = currentTime.getTime() - startTime.getTime();
                //long numDays = diff / MS_IN_A_DAY;
                diff = diff % MS_IN_A_DAY;
                long numHours = diff / MS_IN_AN_HOUR;
                diff = diff % MS_IN_AN_HOUR;
                long numMinutes = diff / MS_IN_A_MINUTE;
                diff = diff % MS_IN_A_MINUTE;
                long numSeconds = diff / MS_IN_A_SECOND;
//                diff = diff % MS_IN_A_SECOND;
                //long numMilliseconds = diff;

                StringBuffer buf = new StringBuffer();
                if (numHours > 0) {
                    buf.append(numHours).append(" ").append(HOURS).append(", ");
                }

                if (numMinutes > 0) {
                    buf.append(numMinutes).append(" ").append(MINUTES).append(", ");
                }

                buf.append(numSeconds).append(" ").append(SECONDS);

                String result = buf.toString();
                setText(result);
                parentComponent.invalidate();
                parentComponent.repaint();
            }
        };

        timer = new Timer(1000, updateTime);

        timer.start();
    }

    public void resetTime() {
        startTime = new Date();
    }

    public void startTimer(){
        timer.start();
    }

    /**
     * Returns the total length of the session in milliseconds.
     *
     * @return long - milliseconds.
     */
    public long getTotalTime() {
        return startTime.getTime() - new Date().getTime();
    }

    /**
     * Stop the clock.
     */
    public void stopTimer() {
        timer.stop();
    }

    public String toString() {
        return getText();
    }


}