package org.jivesoftware.sparkimpl.plugin.idle;

/**
 * Interface for classes fetching the computer idle time on a specific OS.
 * @author Laurent Cohen
 */
interface IdleTimeDetector {
    /**
     * Get the total idle time of the system.
     * @return the idle time in milliseconds.
     */
    long getIdleTimeMillis();
}
