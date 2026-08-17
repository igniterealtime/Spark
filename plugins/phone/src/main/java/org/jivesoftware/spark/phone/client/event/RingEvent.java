package org.jivesoftware.spark.phone.client.event;

/**
 * Dispatched when one of the user's devices is ringing.
 */
public class RingEvent {

    private final String callID;
    private final String device;
    private final String callerID;
    private final String callerIDName;

    public RingEvent(String callID, String device, String callerID, String callerIDName) {
        this.callID = callID;
        this.device = device;
        this.callerID = callerID;
        this.callerIDName = callerIDName;
    }

    public String getCallID() {
        return callID;
    }

    public String getDevice() {
        return device;
    }

    public String getCallerID() {
        return callerID;
    }

    public String getCallerIDName() {
        return callerIDName;
    }
}
