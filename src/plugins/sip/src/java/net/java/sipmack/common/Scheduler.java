/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package net.java.sipmack.common;

import net.java.sipmack.common.scheduler.Timer;
import net.java.sipmack.common.scheduler.TimerTask;

import java.util.Date;

/**
 * <p/>
 * Title: SIP Communicator
 * </p>
 * <p/>
 * Description: A SIP UA
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p/>
 * Company: Network Research Team, Louis Pasteur University
 * </p>
 *
 * @author Emil Ivov
 * @version 1.0
 */

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
