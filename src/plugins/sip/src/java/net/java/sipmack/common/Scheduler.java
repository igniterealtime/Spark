/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */


package net.java.sipmack.common;

import net.java.sipmack.common.scheduler.Timer;
import net.java.sipmack.common.scheduler.TimerTask;

import java.util.Date;

public class Scheduler {

    private static Scheduler singleton = null;

    Timer timer = new Timer(false);

    /**
     * Returns the currently valid instance of the scheduler.
     *
     * @return the currently valid instance of the scheduler.
     */
    public static Scheduler getInstance() {
        if (singleton == null)
            singleton = new Scheduler();

        return singleton;
    }

    private Scheduler() {
    }

    /**
     * @author thiagoc
     */
    private class DelegatorTask extends TimerTask {
        private TimerTask task = null;

        public DelegatorTask(TimerTask task) {
            this.task = task;
        }

        public void run() {
            task.run();
        }
    }

    /**
     * ReSchedules the specified task to execute after the specified delay.
     *
     * @param task the task to reschedule
     * @param date the time/date this task is scheduled for
     */
    public void reschedule(TimerTask task, Date date) {
        timer.reschedule(task, date);
    }

    /**
     * ReSchedules the specified task to execute after the specified delay.
     *
     * @param task  the task to reschedule
     * @param delay delay in milliseconds before task is to be executed.
     */
    public void reschedule(TimerTask task, long delay) {
        timer.reschedule(task, delay);
    }

    /**
     * Schedules the specified task for execution at the specified time.
     *
     * @param task task to be scheduled.
     * @param time time at which task is to be executed.
     */
    public void schedule(TimerTask task, Date time) {
        timer.schedule(task, time);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>,
     * beginning at the specified time.
     *
     * @param task      task to be scheduled.
     * @param firstTime First time at which task is to be executed.
     * @param period    time in milliseconds between successive task executions.
     */
    public void schedule(TimerTask task, Date firstTime, long period) {
        timer.schedule(task, firstTime, period);
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param task  task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     */
    public void schedule(TimerTask task, long delay) {
        timer.schedule(task, delay);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>,
     * beginning after the specified delay.
     *
     * @param task   task to be scheduled.
     * @param delay  delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     */
    public void schedule(TimerTask task, long delay, long period) {
        timer.schedule(task, delay, period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>,
     * beginning at the specified time.
     *
     * @param task      task to be scheduled.
     * @param firstTime First time at which task is to be executed.
     * @param period    time in milliseconds between successive task executions.
     */
    public void scheduleAtFixedRate(TimerTask task, Date firstTime,
                                    long period) {
        timer.schedule(task, firstTime, period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>,
     * beginning after the specified delay.
     *
     * @param task
     *            task to be scheduled.
     * @param delay
     *            delay in milliseconds before task is to be executed.
     * @param period
     *            time in milliseconds between successive task executions.
     */
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        timer.schedule(task, delay, period);
	}
}
