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

package net.java.sipmack.common.scheduler;

import java.util.Date;

public class Timer {
    /**
     * The timer task queue. This data structure is shared with the timer
     * thread. The timer produces tasks, via its various schedule calls, and the
     * timer thread consumes, executing timer tasks as appropriate, and removing
     * them from the queue when they're obsolete.
     */
    private TaskQueue queue = new TaskQueue();

    /**
     * The timer thread.
     */
    private TimerThread thread = new TimerThread(queue);

    /**
     * ReSchedules the specified task to perform after "delay" milliseconds
     *
     * @param task the task to reschedule
     * @param date the time/date this task is scheduled for
     */
    public void reschedule(TimerTask task, java.util.Date date) {
        queue.reschedule(task, date.getTime());
    }

    /**
     * ReSchedules the specified task to perform after "delay" milliseconds
     *
     * @param task  the task to reschedule
     * @param delay delay in milliseconds before task is to be executed.
     */
    public void reschedule(TimerTask task, long delay) {
        queue.reschedule(task, delay);
    }

    /**
     * This object causes the timer's task execution thread to exit gracefully
     * when there are no live references to the Timer object and no tasks in the
     * timer queue. It is used in preference to a finalizer on Timer as such a
     * finalizer would be susceptible to a subclass's finalizer forgetting to
     * call it.
     */
    //TODO REMOVE
    @SuppressWarnings("unused")
    private Object threadReaper = new Object() {
        protected void finalize() throws Throwable {
            synchronized (queue) {
                thread.newTasksMayBeScheduled = false;
                queue.notify(); // In case queue is empty.
            }
        }
    };

    /**
     * Creates a new timer. The associated thread does <i>not</i> run as a
     * daemon.
     *
     * @see Thread
     * @see #cancel()
     */
    public Timer() {
        thread.start();
    }

    /**
     * Creates a new timer whose associated thread may be specified to run as a
     * daemon. A deamon thread is called for if the timer will be used to
     * schedule repeating "maintenance activities", which must be performed as
     * long as the application is running, but should not prolong the lifetime
     * of the application.
     *
     * @param isDaemon true if the associated thread should run as a daemon.
     * @see Thread
     * @see #cancel()
     */
    public Timer(boolean isDaemon) {
        thread.setDaemon(isDaemon);
        thread.start();
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param task  task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *                                  <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, or timer was
     *                                  cancelled.
     */
    public void schedule(TimerTask task, long delay) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        sched(task, System.currentTimeMillis() + delay, 0);
    }

    /**
     * Schedules the specified task for execution at the specified time. If the
     * time is in the past, the task is scheduled for immediate execution.
     *
     * @param task task to be scheduled.
     * @param time time at which task is to be executed.
     * @throws IllegalArgumentException if <tt>time.getTime()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, timer was
     *                                  cancelled, or timer thread terminated.
     */
    public void schedule(TimerTask task, Date time) {
        sched(task, time.getTime(), 0);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>,
     * beginning after the specified delay. Subsequent executions take place at
     * approximately regular intervals separated by the specified period.
     * <p/>
     * <p/>
     * In fixed-delay execution, each execution is scheduled relative to the
     * actual execution time of the previous execution. If an execution is
     * delayed for any reason (such as garbage collection or other background
     * activity), subsequent executions will be delayed as well. In the long
     * run, the frequency of execution will generally be slightly lower than the
     * reciprocal of the specified period (assuming the system clock underlying
     * <tt>Object.wait(long)</tt> is accurate).
     * <p/>
     * <p/>
     * Fixed-delay execution is appropriate for recurring activities that
     * require "smoothness." In other words, it is appropriate for activities
     * where it is more important to keep the frequency accurate in the short
     * run than in the long run. This includes most animation tasks, such as
     * blinking a cursor at regular intervals. It also includes tasks wherein
     * regular activity is performed in response to human input, such as
     * automatically repeating a character as long as a key is held down.
     *
     * @param task   task to be scheduled.
     * @param delay  delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *                                  <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, timer was
     *                                  cancelled, or timer thread terminated.
     */
    public void schedule(TimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, System.currentTimeMillis() + delay, -period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>,
     * beginning at the specified time. Subsequent executions take place at
     * approximately regular intervals, separated by the specified period.
     * <p/>
     * <p/>
     * In fixed-delay execution, each execution is scheduled relative to the
     * actual execution time of the previous execution. If an execution is
     * delayed for any reason (such as garbage collection or other background
     * activity), subsequent executions will be delayed as well. In the long
     * run, the frequency of execution will generally be slightly lower than the
     * reciprocal of the specified period (assuming the system clock underlying
     * <tt>Object.wait(long)</tt> is accurate).
     * <p/>
     * <p/>
     * Fixed-delay execution is appropriate for recurring activities that
     * require "smoothness." In other words, it is appropriate for activities
     * where it is more important to keep the frequency accurate in the short
     * run than in the long run. This includes most animation tasks, such as
     * blinking a cursor at regular intervals. It also includes tasks wherein
     * regular activity is performed in response to human input, such as
     * automatically repeating a character as long as a key is held down.
     *
     * @param task      task to be scheduled.
     * @param firstTime First time at which task is to be executed.
     * @param period    time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if <tt>time.getTime()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, timer was
     *                                  cancelled, or timer thread terminated.
     */
    public void schedule(TimerTask task, Date firstTime, long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, firstTime.getTime(), -period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>,
     * beginning after the specified delay. Subsequent executions take place at
     * approximately regular intervals, separated by the specified period.
     * <p/>
     * <p/>
     * In fixed-rate execution, each execution is scheduled relative to the
     * scheduled execution time of the initial execution. If an execution is
     * delayed for any reason (such as garbage collection or other background
     * activity), two or more executions will occur in rapid succession to
     * "catch up." In the long run, the frequency of execution will be exactly
     * the reciprocal of the specified period (assuming the system clock
     * underlying <tt>Object.wait(long)</tt> is accurate).
     * <p/>
     * <p/>
     * Fixed-rate execution is appropriate for recurring activities that are
     * sensitive to <i>absolute</i> time, such as ringing a chime every hour on
     * the hour, or running scheduled maintenance every day at a particular
     * time. It is also appropriate for for recurring activities where the total
     * time to perform a fixed number of executions is important, such as a
     * countdown timer that ticks once every second for ten seconds. Finally,
     * fixed-rate execution is appropriate for scheduling multiple repeating
     * timer tasks that must remain synchronized with respect to one another.
     *
     * @param task   task to be scheduled.
     * @param delay  delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *                                  <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, timer was
     *                                  cancelled, or timer thread terminated.
     */
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, System.currentTimeMillis() + delay, period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>,
     * beginning at the specified time. Subsequent executions take place at
     * approximately regular intervals, separated by the specified period.
     * <p/>
     * <p/>
     * In fixed-rate execution, each execution is scheduled relative to the
     * scheduled execution time of the initial execution. If an execution is
     * delayed for any reason (such as garbage collection or other background
     * activity), two or more executions will occur in rapid succession to
     * "catch up." In the long run, the frequency of execution will be exactly
     * the reciprocal of the specified period (assuming the system clock
     * underlying <tt>Object.wait(long)</tt> is accurate).
     * <p/>
     * <p/>
     * Fixed-rate execution is appropriate for recurring activities that are
     * sensitive to <i>absolute</i> time, such as ringing a chime every hour on
     * the hour, or running scheduled maintenance every day at a particular
     * time. It is also appropriate for for recurring activities where the total
     * time to perform a fixed number of executions is important, such as a
     * countdown timer that ticks once every second for ten seconds. Finally,
     * fixed-rate execution is appropriate for scheduling multiple repeating
     * timer tasks that must remain synchronized with respect to one another.
     *
     * @param task      task to be scheduled.
     * @param firstTime First time at which task is to be executed.
     * @param period    time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if <tt>time.getTime()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, timer was
     *                                  cancelled, or timer thread terminated.
     */
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, firstTime.getTime(), period);
    }

    /**
     * Schedule the specifed timer task for execution at the specified time with
     * the specified period, in milliseconds. If period is positive, the task is
     * scheduled for repeated execution; if period is zero, the task is
     * scheduled for one-time execution. Time is specified in Date.getTime()
     * format. This method checks timer state, task state, and initial execution
     * time, but not period.
     *
     * @throws IllegalArgumentException if <tt>time()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or cancelled, timer was
     *                                  cancelled, or timer thread terminated.
     */
    private void sched(TimerTask task, long time, long period) {
        if (time < 0)
            throw new IllegalArgumentException("Illegal execution time.");

        synchronized (queue) {
            if (!thread.newTasksMayBeScheduled)
                throw new IllegalStateException("Timer already cancelled.");

            synchronized (task.lock) {
                if (task.state != TimerTask.VIRGIN)
                    throw new IllegalStateException(
                            "Task already scheduled or cancelled");
                task.nextExecutionTime = time;
                task.period = period;
                task.state = TimerTask.SCHEDULED;
            }

            queue.add(task);
            if (queue.getMin() == task)
                queue.notify();
        }
    }

    /**
     * Terminates this timer, discarding any currently scheduled tasks. Does not
     * interfere with a currently executing task (if it exists). Once a timer
     * has been terminated, its execution thread terminates gracefully, and no
     * more tasks may be scheduled on it.
     * <p/>
     * <p/>
     * Note that calling this method from within the run method of a timer task
     * that was invoked by this timer absolutely guarantees that the ongoing
     * task execution is the last task execution that will ever be performed by
     * this timer.
     * <p/>
     * <p/>
     * This method may be called repeatedly; the second and subsequent calls
     * have no effect.
     */
    public void cancel() {
        synchronized (queue) {
            thread.newTasksMayBeScheduled = false;
            queue.clear();
            queue.notify(); // In case queue was already empty.
        }
    }
}

/**
 * This "helper class" implements the timer's task execution thread, which waits
 * for tasks on the timer queue, executions them when they fire, reschedules
 * repeating tasks, and removes cancelled tasks and spent non-repeating tasks
 * from the queue.
 */
class TimerThread extends Thread {
    /**
     * This flag is set to false by the reaper to inform us that there are no
     * more live references to our Timer object. Once this flag is true and
     * there are no more tasks in our queue, there is no work left for us to do,
     * so we terminate gracefully. Note that this field is protected by queue's
     * monitor!
     */
    boolean newTasksMayBeScheduled = true;

    /**
     * Our Timer's queue. We store this reference in preference to a reference
     * to the Timer so the reference graph remains acyclic. Otherwise, the Timer
     * would never be garbage-collected and this thread would never go away.
     */
    private TaskQueue queue;

    TimerThread(TaskQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            mainLoop();
        }
        finally {
            // Somone killed this Thread, behave as if Timer cancelled
            synchronized (queue) {
                newTasksMayBeScheduled = false;
                queue.clear(); // Eliminate obsolete references
            }
        }
    }

    /**
     * The main timer loop. (See class comment.)
     */
    private void mainLoop() {
        while (true) {
            try {
                TimerTask task;
                boolean taskFired;
                synchronized (queue) {
                    // Wait for queue to become non-empty
                    while (queue.isEmpty() && newTasksMayBeScheduled) queue.wait();
                    if (queue.isEmpty())
                        break; // Queue is empty and will forever remain; die

                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
                    task = queue.getMin();
                    synchronized (task.lock) {
                        if (task.state == TimerTask.CANCELLED) {
                            queue.removeMin();
                            continue; // No action required, poll queue again
                        }
                        currentTime = System.currentTimeMillis();
                        executionTime = task.nextExecutionTime;
                        if (taskFired = (executionTime <= currentTime)) {
                            if (task.period == 0) { // Non-repeating, remove
                                queue.removeMin();
                                task.state = TimerTask.EXECUTED;
                            }
                            else { // Repeating task, reschedule
                                queue
                                        .rescheduleMin(task.period < 0 ? currentTime
                                                - task.period
                                                : executionTime + task.period);
                            }
                        }
                    }
                    if (!taskFired) // Task hasn't yet fired; wait
                        queue.wait(executionTime - currentTime);
                }
                if (taskFired) // Task fired; run it, holding no locks
                    task.run();
            }
            catch (InterruptedException e) {
            }
        }
    }
}

/**
 * This class represents a timer task queue: a priority queue of TimerTasks,
 * ordered on nextExecutionTime. Each Timer object has one of these, which it
 * shares with its TimerThread. Internally this class uses a heap, which offers
 * log(n) performance for the add, removeMin and rescheduleMin operations, and
 * constant time performance for the the getMin operation.
 */
class TaskQueue {
    /**
     * Priority queue represented as a balanced binary heap: the two children of
     * queue[n] are queue[2*n] and queue[2*n+1]. The priority queue is ordered
     * on the nextExecutionTime field: The TimerTask with the lowest
     * nextExecutionTime is in queue[1] (assuming the queue is nonempty). For
     * each node n in the heap, and each descendant of n, d, n.nextExecutionTime <=
     * d.nextExecutionTime.
     *
     * @uml.property name="queue"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private TimerTask[] queue = new TimerTask[128];

    /**
     * The number of tasks in the priority queue. (The tasks are stored in
     * queue[1] up to queue[size]).
     */
    private int size = 0;

    /**
     * Adds a new task to the priority queue.
     */
    void add(TimerTask task) {
        // Grow backing store if necessary
        if (++size == queue.length) {
            TimerTask[] newQueue = new TimerTask[2 * queue.length];
            System.arraycopy(queue, 0, newQueue, 0, size);
            queue = newQueue;
        }

        queue[size] = task;
        fixUp(size);
    }

    /**
     * Return the "head task" of the priority queue. (The head task is an task
     * with the lowest nextExecutionTime.)
     */
    TimerTask getMin() {
        return queue[1];
    }

    /**
     * Remove the head task from the priority queue.
     */
    void removeMin() {
        queue[1] = queue[size];
        queue[size--] = null; // Drop extra reference to prevent memory leak
        fixDown(1);
    }

    /**
     * Sets the nextExecutionTime associated with the head task to the specified
     * value, and adjusts priority queue accordingly.
     */
    void rescheduleMin(long newTime) {
        queue[1].nextExecutionTime = newTime;
        fixDown(1);
    }

    /**
     * Returns true if the priority queue contains no elements.
     */
    boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all elements from the priority queue.
     */
    void clear() {
        // Null out task references to prevent memory leak
        for (int i = 1; i <= size; i++)
            queue[i] = null;

        size = 0;
    }

    /**
     * Establishes the heap invariant (described above) assuming the heap
     * satisfies the invariant except possibly for the leaf-node indexed by k
     * (which may have a nextExecutionTime less than its parent's).
     * <p/>
     * This method functions by "promoting" queue[k] up the hierarchy (by
     * swapping it with its parent) repeatedly until queue[k]'s
     * nextExecutionTime is greater than or equal to that of its parent.
     */
    private void fixUp(int k) {
        while (k > 1) {
            int j = k >> 1;
            if (queue[j].nextExecutionTime <= queue[k].nextExecutionTime)
                break;
            TimerTask tmp = queue[j];
            queue[j] = queue[k];
            queue[k] = tmp;
            k = j;
        }
    }

    /**
     * Establishes the heap invariant (described above) in the subtree rooted at
     * k, which is assumed to satisfy the heap invariant except possibly for
     * node k itself (which may have a nextExecutionTime greater than its
     * children's).
     * <p/>
     * This method functions by "demoting" queue[k] down the hierarchy (by
     * swapping it with its smaller child) repeatedly until queue[k]'s
     * nextExecutionTime is less than or equal to those of its children.
     */
    private void fixDown(int k) {
        int j;
        while ((j = k << 1) <= size) {
            if (j < size
                    && queue[j].nextExecutionTime > queue[j + 1].nextExecutionTime)
                j++; // j indexes smallest kid
            if (queue[k].nextExecutionTime <= queue[j].nextExecutionTime)
                break;
            TimerTask tmp = queue[j];
            queue[j] = queue[k];
            queue[k] = tmp;
            k = j;
        }
    }

    int findTask(TimerTask task) {
        for (int i = 1; i <= size; i++)
            if (queue[i] == task)
                return i;
        return -1;
    }

    void reschedule(TimerTask task, long time) {
        int i = findTask(task);
        if (i == -1)
            return;
        long oldTime = queue[i].nextExecutionTime;
        queue[i].nextExecutionTime = time;
        if (oldTime < time)
			fixDown(i);
		else
			fixUp(i);
	}

}
