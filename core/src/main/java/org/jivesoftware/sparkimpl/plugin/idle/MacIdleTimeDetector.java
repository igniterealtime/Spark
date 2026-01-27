package org.jivesoftware.sparkimpl.plugin.idle;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Utility method to retrieve the idle time on Mac OS X 10.4+
 * @author kwindszus
 * @author Laurent Cohen
 */
class MacIdleTimeDetector implements IdleTimeDetector {
    /**
     * Wraps the interactions with the native library.
     */
    public interface ApplicationServices extends Library {
        /**
         * Wrapper for the native library.
         */
        ApplicationServices INSTANCE = Native.load("ApplicationServices", ApplicationServices.class);
        /**
         * The type for any mouse or keyboard input event.
         */
        int KCG_ANY_INPUT_EVENT_TYPE = ~0;
        /**
         * User-only state.
         */
        int KCG_EVENT_SOURCE_STATE_PRIVATE = -1;
        /**
         * System-only state.
         */
        int KCG_EVENT_SOURCE_STATE_HID_SYSTEM_STATE = 1;
        /**
         * User and system state.
         */
        int KCG_EVENT_SOURCE_STATE_COMBINED_SESSION_STATE = 0;

        /**
         * Returns the elapsed time since the last event for a Quartz event source.
         *
         * @param sourceStateId the source state to access.
         * @param eventType     the event type to access. To get the elapsed time since the previous input event: keyboard, mouse, or tablet, specify KCG_ANY_INPUT_EVENT_TYPE.
         * @return the elapsed seconds since the last input event.
         * @see "http://developer.apple.com/mac/library/documentation/Carbon/Reference/QuartzEventServicesRef/Reference/reference.html#//apple_ref/c/func/CGEventSourceSecondsSinceLastEventType"
         */
        double CGEventSourceSecondsSinceLastEventType(int sourceStateId, int eventType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdleTimeMillis() {
        final double idleTimeSeconds = ApplicationServices.INSTANCE.CGEventSourceSecondsSinceLastEventType(
            ApplicationServices.KCG_EVENT_SOURCE_STATE_COMBINED_SESSION_STATE, ApplicationServices.KCG_ANY_INPUT_EVENT_TYPE);
        return (long) (idleTimeSeconds * 1000);
    }
}
